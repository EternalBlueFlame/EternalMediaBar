package com.ebf.eternalmediabar;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.SequenceInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


//LAST KNOWN GOOD 11/5 @ 1:32am


public class EternalMediaBar extends Activity {

    //list and hlist are never used but for some odd reason need to be present for it to render.
    private ListView list;
    private LinearLayout hlist;
    private PackageManager manager;
    private List<AppDetail> oldapps = new ArrayList<AppDetail>();
    private List<AppDetail> hli = new ArrayList<AppDetail>();
    private settingsClass saveddata = new settingsClass();

    public int hitem = 0;
    private boolean init = false;
    private boolean optionsmenu = false;
    public int vitem = 0;
    public int optionVitem =1;



    //override the on create method to run the starting scripts
        //@Override will override the built in function with your own. this is mostly for being able to call functions without having to specifically call it.
        @Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //set the current layout value
            setContentView(R.layout.activity_eternal_media_bar);

            //run once
            if (!init) {
                if (saveddata.vlists.size()<=1) {
                    try {
                        //try load prefrences
                        //for some off reason only one file can be loaded EVER, so all variables that ever need to be accessed have to be in the same serialieable class.
                        FileInputStream fileStream = openFileInput("lists.dat");
                        ObjectInputStream objStream = new ObjectInputStream(fileStream);
                        saveddata = (settingsClass) objStream.readObject();
                        //close the stream to save RAM.
                        objStream.close();
                        fileStream.close();
                        //for some odd reason savedata.oldapps cant be accessed directly in most cases, so we'll push it to another variable to edit and change.
                        oldapps = saveddata.oldapps;
                    } catch (Exception e) {
                        //output to debug log just in case something went fully wrong
                        e.printStackTrace();
                        //catch with below by initializing vlists properly
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                        saveddata.vlists.add(new ArrayList<AppDetail>());
                    }
                }
                //load in the apps
                loadApps();
                //render everything
                loadListView(saveddata.vlists.get(hitem));
                //make sure this doesnt happen again
                init = true;
            }

            //Async process for use later, this will help with various things, but no use for it just yet.
            //AsyncTask task = new AsyncTask() {
            //    @Override
            //    protected Object doInBackground(Object[] params) {
            //        return null;
            //        }
            //    };

            //task.execute();

        }



    public void savefiles(){
        try{
            // apply the instanced value back to the savedata version so we can save it.
            saveddata.oldapps = oldapps;
            //create a file output stream with an object, to save a variable to a file, then close the stream.
            FileOutputStream fileStream = openFileOutput("lists.dat", Context.MODE_PRIVATE);
            ObjectOutputStream fileOutput = new ObjectOutputStream(fileStream);
            fileOutput.writeObject(saveddata);
            //close the stream to save RAM
            fileOutput.close();
            fileStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
            //can't get read/write permissions, or something unforseen has gone horribly wrong
        }
    }




    //when a key is pressed this function will be called, this includes built-in and USB controllers, software, and hardware keyboards.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //case event for down
            case KeyEvent.KEYCODE_S: {
                listmove(vitem+1);
                return true;
            }
            //case event for up
            case KeyEvent.KEYCODE_W: {
                listmove(vitem-1);
                return true;
            }
            //case event for right
            case KeyEvent.KEYCODE_D: {

                if ((hitem+1) < hli.size()) {
                    hitem++;
                    vitem=0;
                    loadListView(saveddata.vlists.get(hitem));
                }
                return true;
            }
            //case event for left
            case KeyEvent.KEYCODE_A: {

                if (hitem > 0) {
                    hitem--;
                    vitem=0;
                    loadListView(saveddata.vlists.get(hitem));
                }
                return true;
            }
			case KeyEvent.KEYCODE_ENTER:{
				onEnter(0, true, saveddata.vlists.get(hitem).get(vitem).name, "");
				return true;
			}
			case KeyEvent.KEYCODE_1:{
				onEnter(0, true, saveddata.vlists.get(hitem).get(vitem).name, "");
				return true;
			}
			case KeyEvent.KEYCODE_4:{
				onOptions(1, false, saveddata.vlists.get(hitem).get(vitem).name, (String) saveddata.vlists.get(hitem).get(vitem).label);
				return true;
			}
			case KeyEvent.KEYCODE_E:{
				onOptions(1, false, saveddata.vlists.get(hitem).get(vitem).name, (String) saveddata.vlists.get(hitem).get(vitem).label);
				return true;
			}

            //case event for unused keys
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    // NOT YET IMPLEMENTED FUNCTION
    void listmove(int move){
        //function to move the highlight selection based on which menu you are on.
        //if you are not on the options menu
            if (!optionsmenu){
                LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
                boolean proceed = true;
                if (vitem >move){if(vitem==0){proceed=false;}}
                else if ((vitem+2) >Vlayout.getChildCount()){proceed=false;}
                if (proceed) {
                    TextView appLabelGlow = (TextView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText("");
                    vitem = move;
                    appLabelGlow = (TextView) Vlayout.getChildAt(move).findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText(((TextView) Vlayout.getChildAt(move).findViewById(R.id.item_app_label)).getText());
                }
            }
            //if you are on the options menu
        else{
                move-=vitem;
                move+=optionVitem;
                LinearLayout Vlayout = (LinearLayout)findViewById(R.id.optionslist);
                boolean proceed = true;
                if (optionVitem >move){if(optionVitem==1){proceed=false;}}
                else if ((optionVitem+2) > Vlayout.getChildCount()){proceed=false;}
                if (proceed) {
                    TextView appLabelGlow = (TextView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText("");
                    optionVitem = move;
                    appLabelGlow = (TextView) Vlayout.getChildAt(move).findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText(((TextView) Vlayout.getChildAt(move).findViewById(R.id.item_app_label)).getText());
                }
            }
        }

    //load the installed apps and sort them into their proper places on the lists.
    private void loadApps(){
        manager = getPackageManager();
        //get the apps from the intent activity list of resolve info in the host OS.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        AppDetail app = new AppDetail();
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent , 0);
        //copy only the necessary info from each app into a copy of the AppDetail Class
        for(ResolveInfo ri : availableActivities){
            AppDetail appri = new AppDetail();
            appri.label = ri.loadLabel(manager);
            appri.name = ri.activityInfo.packageName;
            appri.ismenu = 0;
            //add the setup App Detail class to the currently installed apps, and if it is not in the previously saved oldapps variable, add it.
            //this can be used to check if apps are new. but this is a rather fat way of doing it....
            if (!oldapps.contains(appri)){
                oldapps.add(appri);
                if (!saveddata.vlists.get(8).contains(appri)){
                    saveddata.vlists.get(8).add(appri);
                }
            }
        }


        //setup the horizontal bar, theres a pre-defined setting to ease the ability for custom options later down the road.most importantly it simplifies the code.
        hli.add(hmenuloader("Settings", svgLoad(R.drawable.settings_144px )));
        hli.add(hmenuloader("Extra", svgLoad(R.drawable.extras_144px)));
        hli.add(hmenuloader("Photo", svgLoad(R.drawable.photo_144px)));
        hli.add(hmenuloader("Music", svgLoad(R.drawable.music_144px)));
        hli.add(hmenuloader("Video", svgLoad(R.drawable.video_144px)));
        hli.add(hmenuloader("Games", svgLoad(R.drawable.games_144px)));
        hli.add(hmenuloader("Web", svgLoad(R.drawable.web_144px)));
        hli.add(hmenuloader("Store", svgLoad(R.drawable.shop_144px)));
        hli.add(hmenuloader("New Apps", svgLoad(R.drawable.new_install_144px)));




        //now check if there are any apps in the old list that are no longer installed, and be sure to remove them from any list they may be on
            for (int iii = 0; iii < oldapps.size(); ) {
                if (!saveddata.vlists.get(8).contains(oldapps.get(iii))) {
                    for (int iv = 0; iv < saveddata.vlists.size(); ) {
                        if (saveddata.vlists.get(iv).contains(oldapps.get(iii))) {
                            saveddata.vlists.get(iv).remove(oldapps.get(iii));
                            iv++;
                        }
                    }
                    oldapps.remove(iii);
                }
                iii++;
            }

        savefiles();
    }

    //return a drawable from an SVG
    Drawable svgLoad(int imagetoload){
        ImageView imageView = new ImageView(this);
        // Parse the SVG from the variable - will have to wait till android SVG is better implemented.
        //SVG svg = SVGParser.getSVGFromResource(getResources(), svgtoload);

        imageView.setImageDrawable(getResources().getDrawable(imagetoload));

        //more SVG stuff that has to wait.
        //imageView.setImageDrawable(svg.createPictureDrawable());
        return imageView.getDrawable();
    }

    //draws the list of apps and categories to screen
    public void loadListView(final List<AppDetail> appslist){
        manager = getPackageManager();
        //scale = findViewById(R.id.mainlayout).getResources().getDisplayMetrics().density +0.5f;

        LinearLayout layout = (LinearLayout)findViewById(R.id.categories);
        layout.removeAllViews();
        for (int ii=0; (ii-1)<hli.size();) {
            View child = getLayoutInflater().inflate(R.layout.category_item, null);
            //sometimes layout items are null, when null it will fail to add the rest of the contents and just add an empty space instead
            try {
                ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(hli.get(ii - 1).icon);
                TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
                appLabel.setText(hli.get(ii - 1).label);
                if (ii==hitem +1) {
                    TextView appLabelGlow = (TextView) child.findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText(hli.get(ii -1).label);
                }
                child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(this, R.anim.textglow));
                Button appbutton = (Button) child.findViewById(R.id.item_app_button);
                //if its the selected, make its click function start the app
                listenupdown(appbutton, ii -1, false, "", "");
            }
            catch(Exception e){}

            layout.addView(child);
            ii++;
        }



        //EXPERIMENTAL, attempt to copy category method but with a verticle list
        LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
        Vlayout.removeAllViews();
        for (int ii=0; ii<appslist.size();) {
            //FOR LATER the else and > can be changed to a pre-set integer by the settungs, so the user can have more than one icon at the top, good for lower DPI devices like bluestacks
            View child = getLayoutInflater().inflate(R.layout.list_item, null);
            ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
            try {
                appIcon.setImageDrawable(manager.getApplicationIcon(appslist.get(ii).name));
            }
            catch(Exception e){appIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_144px));}
            TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
            appLabel.setText(appslist.get(ii).label);

            if (ii==vitem) {
                TextView appLabelGlow = (TextView) child.findViewById(R.id.item_app_label_glow);
                appLabelGlow.setText(appslist.get(ii).label);
            }

            Button appbutton = (Button) child.findViewById(R.id.item_app_button);
            //if its the selected, make its click function start the app
            listenupdown(appbutton, ii, true, appslist.get(ii).name, (String)appslist.get(ii).label);
            //after all is said and done add the item whether its blank or not
            Vlayout.addView(child);
            ii++;
        }
        try{
            Vlayout.getChildAt(vitem).findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(this, R.anim.textglow));
        }
        catch (Exception e){}

    }






	private void onEnter(final int index, final boolean islaunchable, final String launchintent, final String appname){
		if (islaunchable) {
			EternalMediaBar.this.startActivity(manager.getLaunchIntentForPackage(launchintent));
		} else {
			if (launchintent == "") {
				hitem = (index);
				loadListView(saveddata.vlists.get(hitem));
			} else {
				switch (index) {
					case 0: {
							//do nothing
							optionsmenu = false;
							break;
						}
					case 1: {
							//Copy Item List
							LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
							Llayout.removeAllViews();
							int optii = 0;
							View child = getLayoutInflater().inflate(R.layout.options_header, null);
							TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
							ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
							try {
								appIcon.setImageDrawable(getPackageManager().getApplicationIcon(launchintent));
							} catch (Exception e) {
								appIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_144px));
							}
							appLabel.setText(appname);
							Button appbutton = (Button) child.findViewById(R.id.item_app_button);
							Llayout.addView(child);
							optii++;

							for (; optii <= 8; ) {
								if (optii - 1 != hitem)
									child = getLayoutInflater().inflate(R.layout.options_item, null);
								appLabel = (TextView) child.findViewById(R.id.item_app_label);
								appbutton = (Button) child.findViewById(R.id.item_app_button);

								appLabel.setText("Copy to " + hli.get(optii - 1).label);
								listenupdown(appbutton, 2, false, Integer.toString(optii - 1), "2");
								child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
								Llayout.addView(child);
								optii++;
							}
							optionVitem = 1;
							TextView appLabelGlow = (TextView) Llayout.getChildAt(1).findViewById(R.id.item_app_label_glow);
							appLabelGlow.setText(((TextView) Llayout.getChildAt(1).findViewById(R.id.item_app_label)).getText());
							break;
						}
					case 2: {
							LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
							Llayout.removeAllViews();
							//copy item
							if (appname == "2") {
								saveddata.vlists.get(Integer.parseInt(launchintent)).add(saveddata.vlists.get(hitem).get(vitem));
								savefiles();
								break;
							}
							//move item
							if (appname == "3") {
								saveddata.vlists.get(Integer.parseInt(launchintent)).add(saveddata.vlists.get(hitem).get(vitem));
								saveddata.vlists.get(hitem).remove(vitem);
								savefiles();
                                loadListView(saveddata.vlists.get(hitem));
								break;
							}

							//remove/hide item
							int ii = 0;
							for (int i = 0; i <= saveddata.vlists.size(); ) {
								if (saveddata.vlists.get(i).contains(saveddata.vlists.get(hitem).get(vitem))) {
									ii++;
								}
								i++;
							}
							if (appname == "1" && ii == 1) {
								//hiddenapps.add(saveddata.vlists.get(hitem).get(vitem));
								saveddata.vlists.get(hitem).remove(vitem);
							} else if (appname == "1") {
								saveddata.vlists.get(hitem).remove(vitem);
							}
							optionsmenu = false;
							optionVitem = 1;
							savefiles();
							break;
						}
					case 3: {
							//move item list
							LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
							Llayout.removeAllViews();
							int optii = 0;
							View child = getLayoutInflater().inflate(R.layout.options_header, null);
							TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
							ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
							try {
								appIcon.setImageDrawable(getPackageManager().getApplicationIcon(launchintent));
							} catch (Exception e) {
								appIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_144px));
							}
							appLabel.setText(appname);
							Button appbutton = (Button) child.findViewById(R.id.item_app_button);
							Llayout.addView(child);
							optii++;

							for (; optii <= 8; ) {
								if (optii - 1 != hitem)
									child = getLayoutInflater().inflate(R.layout.options_item, null);
								appLabel = (TextView) child.findViewById(R.id.item_app_label);
								appbutton = (Button) child.findViewById(R.id.item_app_button);

								appLabel.setText("Move to " + hli.get(optii - 1).label);
								listenupdown(appbutton, 2, false, Integer.toString(optii - 1), "3");
								child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
								Llayout.addView(child);
								optii++;
							}
							optionVitem = 1;
							TextView appLabelGlow = (TextView) Llayout.getChildAt(1).findViewById(R.id.item_app_label_glow);
							appLabelGlow.setText(((TextView) Llayout.getChildAt(1).findViewById(R.id.item_app_label)).getText());
							Llayout.getChildAt(1).findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
							break;
						}
					case 4: {
							//open application settings
							Intent intent = new Intent();
							intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							intent.setData(Uri.parse("package:" + Uri.parse(launchintent)));
							startActivity(intent);
                        }
				}
			}
		}
	}

	
	private void onOptions( final int index, final boolean islaunchable, final String launchintent, final String appname){
		if (islaunchable) {
			optionsmenu = true;
			optionVitem = 1;
			vitem = (index);
			loadListView(saveddata.vlists.get(hitem));
			LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
			Llayout.removeAllViews();

			View child = getLayoutInflater().inflate(R.layout.options_header, null);
			TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
			ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
			try {
				appIcon.setImageDrawable(getPackageManager().getApplicationIcon(launchintent));
			} catch (Exception e) {
				appIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_144px));
			}
			appLabel.setText(appname);
			Llayout.addView(child);


			/*
			 child = getLayoutInflater().inflate(R.layout.options_item, null);
			 appLabel = (TextView) child.findViewById(R.id.item_app_label);
			 appLabel.setText("Remove/Hide");
			 Button appbutton = (Button) child.findViewById(R.id.item_app_button);
			 TextView appLabelGlow = (TextView) child.findViewById(R.id.item_app_label_glow);
			 appLabelGlow.setText("Remove/Hide");
			 //if its the selected, make its click function start the app
			 listenupdown(appbutton, 2, false, launchintent, appname);
			 child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
			 Llayout.addView(child);
			 */

			child = getLayoutInflater().inflate(R.layout.options_item, null);
			appLabel = (TextView) child.findViewById(R.id.item_app_label);
			appLabel.setText("Copy to...");
			child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
			Button appbutton = (Button) child.findViewById(R.id.item_app_button);
			//if its the selected, make its click function start the app
			listenupdown(appbutton, 1, false, launchintent, appname);
			Llayout.addView(child);

			child = getLayoutInflater().inflate(R.layout.options_item, null);
			appLabel = (TextView) child.findViewById(R.id.item_app_label);
			appLabel.setText("Move to...");
			child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
			appbutton = (Button) child.findViewById(R.id.item_app_button);
			//if its the selected, make its click function start the app
			listenupdown(appbutton, 3, false, launchintent, appname);
			Llayout.addView(child);

			child = getLayoutInflater().inflate(R.layout.options_item, null);
			appLabel = (TextView) child.findViewById(R.id.item_app_label);
			appLabel.setText("Application Settings");
			child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
			appbutton = (Button) child.findViewById(R.id.item_app_button);
			//if its the selected, make its click function start the app
			listenupdown(appbutton, 4, false, launchintent, appname);
			Llayout.addView(child);


			//animateopt();

		}
		}

    //call function for asigning button functionality on A/X/Enter/left mouse pressed
    private void listenupdown(Button btn, final int index, final boolean islaunchable, final String launchintent, final String appname){

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
				onEnter(index, islaunchable, launchintent, appname);       
        }
		});

        btn.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onOptions(index, islaunchable, launchintent, appname);
				return true;
            }
        });
    }





    void animateopt(){
        try{
            LinearLayout Llayout = (LinearLayout)findViewById(R.id.optionslist);
            Llayout.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(this, R.anim.expand));
        }
        catch (Exception e){}
    }




    //call function for asigning categories to the horizontal menu
    public AppDetail hmenuloader (String name, Drawable icon){
        AppDetail app = new AppDetail();
        app.ismenu = 1;
        app.label = (CharSequence) name;
        app.icon = icon;
        return app;
    }
}
