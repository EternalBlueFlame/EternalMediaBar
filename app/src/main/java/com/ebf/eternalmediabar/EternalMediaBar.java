package com.ebf.eternalmediabar;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


//LAST KNOWN GOOD 12/9


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

    @Override
    protected void onResume() {
        super.onResume();
        if (init){
            //load in the apps
            loadApps();
            //make sure vitem isn't out of bounds
            if (vitem >= saveddata.vlists.get(hitem).size()){
                vitem = saveddata.vlists.get(hitem).size();
            }
            //render everything
            loadListView(saveddata.vlists.get(hitem));

        }
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
            case KeyEvent.KEYCODE_S: case KeyEvent.KEYCODE_DPAD_DOWN: case KeyEvent.KEYCODE_4: case KeyEvent.KEYCODE_NUMPAD_4: {
                listmove(vitem+1);
                return true;
            }
            //case event for up
            case KeyEvent.KEYCODE_W: case KeyEvent.KEYCODE_DPAD_UP: case KeyEvent.KEYCODE_2: case KeyEvent.KEYCODE_NUMPAD_2:{
                listmove(vitem-1);
                return true;
            }
            //case event for right
            case KeyEvent.KEYCODE_D: case KeyEvent.KEYCODE_DPAD_RIGHT: case KeyEvent.KEYCODE_6: case KeyEvent.KEYCODE_NUMPAD_6:{

                if ((hitem+1) < hli.size()) {
                    hitem++;
                    vitem=0;
                    loadListView(saveddata.vlists.get(hitem));
                }
                return true;
            }
            //case event for left
            case KeyEvent.KEYCODE_A: case KeyEvent.KEYCODE_DPAD_LEFT: case KeyEvent.KEYCODE_8: case KeyEvent.KEYCODE_NUMPAD_8: {

                if (hitem > 0) {
                    hitem--;
                    vitem=0;
                    loadListView(saveddata.vlists.get(hitem));
                }
                return true;
            }
            //event for when enter/x/a is pressed
			case KeyEvent.KEYCODE_ENTER: case KeyEvent.KEYCODE_NUMPAD_ENTER: case KeyEvent.KEYCODE_DPAD_CENTER: case KeyEvent.KEYCODE_1: case KeyEvent.KEYCODE_5: case KeyEvent.KEYCODE_NUMPAD_5: case KeyEvent.KEYCODE_BUTTON_1: {
                if (!optionsmenu) {
                    //onEnter(0, 0, true, saveddata.vlists.get(hitem).get(vitem).name, saveddata.vlists.get(hitem).get(vitem).label);
                    //get the layout
                    LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
                    //get the item in the layout and activate its button function
                    Vlayout.getChildAt(vitem).findViewById(R.id.item_app_button).performClick();
                }
                else{
                    //get the layout
                    LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
                    //get the item in the layout and activate its button function
                    Llayout.getChildAt(optionVitem).findViewById(R.id.item_app_button).performClick();
                }
				return true;
			}
            //event for when E/Y/Triangle is pressed
			case KeyEvent.KEYCODE_BUTTON_4: case KeyEvent.KEYCODE_E: case KeyEvent.KEYCODE_TAB: case KeyEvent.KEYCODE_0: case KeyEvent.KEYCODE_NUMPAD_0: {
                if (!optionsmenu) {
                    //onOptions(1, false, saveddata.vlists.get(hitem).get(vitem).name, (String) saveddata.vlists.get(hitem).get(vitem).label);
                    //get the layout
                    LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
                    //get the item in the layout and activate its button function
                    Vlayout.getChildAt(vitem).findViewById(R.id.item_app_button).performLongClick();
                }
                else{
                    onEnter(0,0,false,".",".");
                }
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
                //if you are trying to move too far down set proceed to false
                if (vitem >move){if(vitem==0){proceed=false;}}
                //if you are trying to move too far up set proceed to false
                else if ((vitem+2) >Vlayout.getChildCount()){proceed=false;}

                if (proceed) {
                    //change the old glow
                    TextView appLabelGlow = (TextView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText("");
                    //change vitem
                    vitem = move;
                    //change the new glow
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
                //if you are trying to move too far down set proceed to false
                if (optionVitem >move){if(optionVitem==1){proceed=false;}}
                //if you are trying to move too far up set proceed to false
                else if ((optionVitem+2) > Vlayout.getChildCount()){proceed=false;}

                if (proceed) {
                    //change the old glow
                    TextView appLabelGlow = (TextView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText("");
                    //change Optionsvitem
                    optionVitem = move;
                    //change the new glow
                    appLabelGlow = (TextView) Vlayout.getChildAt(move).findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText(((TextView) Vlayout.getChildAt(move).findViewById(R.id.item_app_label)).getText());
                    //scroll to the new entry
                    Vlayout.scrollTo((int) Vlayout.getChildAt(optionVitem).getX(), (int) Vlayout.getChildAt(optionVitem).getY());
                }
            }
        }

    //load the installed apps and sort them into their proper places on the lists.
    private void loadApps(){
        manager = getPackageManager();
        List<String> newapps = new ArrayList<String>();
        //get the apps from the intent activity list of resolve info in the host OS.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        AppDetail app = new AppDetail();
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent , 0);
        //copy only the necessary info from each app into a copy of the AppDetail Class
        for(ResolveInfo ri : availableActivities) {
            AppDetail appri = new AppDetail();
            appri.label = ri.loadLabel(manager);
            appri.name = ri.activityInfo.packageName;
            appri.ismenu = 0;
            appri.icon = null;
            //add the app to the list of all new apps to compair against oldapps later.
            newapps.add(ri.activityInfo.packageName);
            //check if the app has previously been found
            boolean fail = false;
            //check each entry in oldapps
            for (int i=0; i<oldapps.size();){
                //in each entry check to see if the app launch intent is the same
                if (oldapps.get(i).name.equals(appri.name)){
                    //if one entry is the same set fail to true and break the search
                    fail=true;
                }
                if (fail){break;}
                else{i++;}
            }
            //if fail is false, add the app to the newly installed list where the user can organize it, and the old apps list.where we can keep track of it easier.
            if (!fail) {
                saveddata.vlists.get(8).add(appri);
                oldapps.add(appri);
            }
        }

        //empty hli first to be sure we dont accidentally make duplicate entries
        hli.clear();
        //setup the horizontal bar, theres a pre-defined setting to ease the ability for custom options later down the road.most importantly it simplifies the code.
        hli.add(createAppDetail(1, "Settings", svgLoad(R.drawable.settings_144px)));
        hli.add(createAppDetail(1, "Extra", svgLoad(R.drawable.extras_144px)));
        hli.add(createAppDetail(1, "Photo", svgLoad(R.drawable.photo_144px)));
        hli.add(createAppDetail(1, "Music", svgLoad(R.drawable.music_144px)));
        hli.add(createAppDetail(1, "Video", svgLoad(R.drawable.video_144px)));
        hli.add(createAppDetail(1, "Games", svgLoad(R.drawable.games_144px)));
        hli.add(createAppDetail(1, "Web", svgLoad(R.drawable.web_144px)));
        hli.add(createAppDetail(1, "Store", svgLoad(R.drawable.shop_144px)));
        hli.add(createAppDetail(1, "New Apps", svgLoad(R.drawable.new_install_144px)));




        //now check if there are any apps in the old list that are no longer installed, and be sure to remove them from any list they may be on
            for (int i = 0; i < oldapps.size(); ) {
                if (!newapps.contains(oldapps.get(i).name)){
                    //create an instance of the app
                    AppDetail toremove = oldapps.get(i);
                    //search all lists for it and remove each entry.
                    for (int ii=0; ii< saveddata.vlists.size();){
                        if (saveddata.vlists.get(ii).contains(toremove)){
                            saveddata.vlists.get(ii).remove(toremove);
                        }
                        ii++;
                    }
                    oldapps.remove(toremove);
                }
                i++;
            }

        savefiles();
    }

    //intended to later return a drawable from an SVG when support is better
    //currently returns a normal drawable from PNG image.
    Drawable svgLoad(int imagetoload){
        ImageView imageView = new ImageView(this);

        imageView.setImageDrawable(getResources().getDrawable(imagetoload));

        //more SVG stuff that has to wait.
        //imageView.setImageDrawable(svg.createPictureDrawable());
        return imageView.getDrawable();
    }

    //draws the list of apps and categories to screen
    public void loadListView(final List<AppDetail> appslist){
        manager = getPackageManager();

        LinearLayout layout = (LinearLayout)findViewById(R.id.categories);
        layout.removeAllViews();
        for (int ii=0; (ii-1)<hli.size();) {

            //sometimes layout items are null, when null it will fail to add the rest of the contents and just add an empty space instead
            View child = getLayoutInflater().inflate(R.layout.category_item, null);
            try {
                child = createMenuEntry(R.layout.category_item, hli.get(ii - 1).label, hli.get(ii - 1).icon, ii - 1, 0, false, "", "");
                if (ii == hitem + 1) {
                    TextView appLabelGlow = (TextView) child.findViewById(R.id.item_app_label_glow);
                    appLabelGlow.setText(hli.get(ii - 1).label);
                }
            }
            catch(Exception e){}
            layout.addView(child);
            ii++;
        }



        //copy category method but with a verticle list
        LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
        Vlayout.removeAllViews();
        for (int ii=0; ii<appslist.size();) {
            View child = createMenuEntry(R.layout.list_item, appslist.get(ii).label, null, ii, 0, true, appslist.get(ii).name, (String) appslist.get(ii).label);
            if (ii==vitem) {
                TextView appLabelGlow = (TextView) child.findViewById(R.id.item_app_label_glow);
                appLabelGlow.setText(appslist.get(ii).label);
            }
            Vlayout.addView(child);
            ii++;
        }
    }






	private void onEnter(final int index, final int secondaryIndex, final boolean islaunchable, final String launchintent, final String appname){
		if (islaunchable) {
			EternalMediaBar.this.startActivity(manager.getLaunchIntentForPackage(launchintent));
		} else {
			if (launchintent.equals("")) {
				hitem = (index);
				loadListView(saveddata.vlists.get(hitem));
			} else {
				switch (index) {
					case 0: {
                        //do nothing/close and save settings
                        optionsmenu = false;
                        optionVitem=1;
                        final LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
                        //remove all items in menu
                        Llayout.removeAllViews();
                        //animate menu closing
                        TranslateAnimation anim = new TranslateAnimation(0,(144 * getResources().getDisplayMetrics().density + 0.5f), 0, 0);
                        anim.setDuration(200);
                        anim.setInterpolator(new LinearInterpolator());
                        anim.setFillEnabled(false);
                        Llayout.startAnimation(anim);
                        //now move the menu itself
                        Llayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // clear animation to prevent flicker
                                Llayout.clearAnimation();
                                //manually set position of menu off screen
                                Llayout.setX(getResources().getDisplayMetrics().widthPixels);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        //save just to be sure.
                        savefiles();
							break;
						}
					case 1: {
                        //Copy Item List
                        LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
                        Llayout.removeAllViews();
                        int optii = 0;
                        //View child = getLayoutInflater().inflate(R.layout.options_header, null);
                        View child = createMenuEntry(R.layout.options_header, appname, null, 5, 0, false, launchintent, "");
                        //TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
                        //ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
                        //try {
                        //    appIcon.setImageDrawable(getPackageManager().getApplicationIcon(launchintent));
                        //} catch (Exception e) {
                        //    appIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_144px));
                        //}
                        //appLabel.setText(appname);
                        //Button appbutton = (Button) child.findViewById(R.id.item_app_button);
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                        Llayout.addView(child);
                        optii++;

                        for (; optii <= 8; ) {
                            if (optii - 1 != hitem)
                                child = getLayoutInflater().inflate(R.layout.options_item, null);
                            TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
                            Button appbutton = (Button) child.findViewById(R.id.item_app_button);

                            appLabel.setText("Copy to " + hli.get(optii - 1).label);
                            listenupdown(appbutton, 2, optii -1, false, ".", "2");
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
                            saveddata.vlists.get(secondaryIndex).add(saveddata.vlists.get(hitem).get(vitem));
                            onEnter(0,0,false,".",".");
                            break;
                        }
                        //move item
                        if (appname == "3") {
                            saveddata.vlists.get(secondaryIndex).add(saveddata.vlists.get(hitem).get(vitem));
                            saveddata.vlists.get(hitem).remove(vitem);
                            onEnter(0,0,false,".",".");
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
                        //resize the layout and save, later this should probably be an animation or something.
                        onEnter(0,0,false,"","");
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
                            listenupdown(appbutton, 2, optii -1, false, ".", "3");
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
                    case 5: {
                        //do nothing
                    }
				}
			}
		}
	}

	
	private void onOptions( final int index, final boolean islaunchable, final String launchintent, final String appname){
        //first check to be sure its something that should be opening the menu
		if (islaunchable) {
            //set the variables for the menu
			optionsmenu = true;
			optionVitem = 1;
			vitem = (index);
            //load the layout and make sure nothing is in it.
			loadListView(saveddata.vlists.get(hitem));
			final LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
			Llayout.removeAllViews();
            //reset the position
            Llayout.setX(getResources().getDisplayMetrics().widthPixels);
            //animate the menu opening
            TranslateAnimation anim = new TranslateAnimation(0,-(144 * getResources().getDisplayMetrics().density + 0.5f), 0, 0);
            anim.setDuration(200);
            anim.setInterpolator(new LinearInterpolator());
            anim.setFillEnabled(false);
            Llayout.startAnimation(anim);
            //now move the menu itself
            Llayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    // clear animation to prevent flicker
                    Llayout.clearAnimation();
                    //manually set position of menu
                    Llayout.setX(getResources().getDisplayMetrics().widthPixels-(144 * getResources().getDisplayMetrics().density + 0.5f));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });


            //add the app thats selected so the user knows for sure what they are messing with.
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


            //add all the extra options
            //first option is to remove or hide an item, this option is ironically hidden until work starts on RC2
            /*
             child = getLayoutInflater().inflate(R.layout.options_item, null);
             appLabel = (TextView) child.findViewById(R.id.item_app_label);
             appLabel.setText("Remove/Hide");
             Button appbutton = (Button) child.findViewById(R.id.item_app_button);
             TextView appLabelGlow = (TextView) child.findViewById(R.id.item_app_label_glow);
             appLabelGlow.setText("Remove/Hide");
             //if its the selected, make its click function start the app
             listenupdown(appbutton, 2, 0, false, launchintent, appname);
             child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
             Llayout.addView(child);
             */

            //copy the item to another category
            child = getLayoutInflater().inflate(R.layout.options_item, null);
            appLabel = (TextView) child.findViewById(R.id.item_app_label);
            appLabel.setText("Copy to...");
            child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
            Button appbutton = (Button) child.findViewById(R.id.item_app_button);
            //if its the selected, make its click function start the app
            listenupdown(appbutton, 1, 0, false, launchintent, appname);
            Llayout.addView(child);

            //move the item to another category
            child = getLayoutInflater().inflate(R.layout.options_item, null);
            appLabel = (TextView) child.findViewById(R.id.item_app_label);
            appLabel.setText("Move to...");
            child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
            appbutton = (Button) child.findViewById(R.id.item_app_button);
            //if its the selected, make its click function start the app
            listenupdown(appbutton, 3, 0, false, launchintent, appname);
            Llayout.addView(child);

            //open the app's settings
            child = getLayoutInflater().inflate(R.layout.options_item, null);
            appLabel = (TextView) child.findViewById(R.id.item_app_label);
            appLabel.setText("Application Settings");
            child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
            appbutton = (Button) child.findViewById(R.id.item_app_button);
            //if its the selected, make its click function start the app
            listenupdown(appbutton, 4, 0, false, launchintent, appname);
            Llayout.addView(child);

        }
    }

    //call function for asigning button functionality on A/X/Enter/left mouse pressed
    private void listenupdown(Button btn, final int index, final int secondaryIndex, final boolean islaunchable, final String launchintent, final String appname){

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
				onEnter(index, secondaryIndex, islaunchable, launchintent, appname);
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





    //call function for creating app detail entries, usually for menus
    public AppDetail createAppDetail (int ismenu, String name, @Nullable Drawable icon){
        AppDetail app = new AppDetail();
        app.ismenu = ismenu;
        app.label = (CharSequence) name;
        if (icon!=null) {
            app.icon = icon;
        }
        else{
            app.icon=getResources().getDrawable(R.drawable.error_144px);
        }
        return app;
    }

    //call function for drawing menu entries
    public View createMenuEntry(int inflater, CharSequence text, @Nullable Drawable icon, int index, int secondaryIndex, Boolean isLaunchable, String launchintent, String appname){
        View child = getLayoutInflater().inflate(inflater, null);
        TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
        appLabel.setText(text);
        child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
        ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
        if (launchintent!="") {
            try {
                //null icon or a new blank one will be blank, invalid icons will show up as exclimations
                appIcon.setImageDrawable(manager.getApplicationIcon(launchintent));
            } catch (Exception e) {
                appIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_144px));
            }
        }
        else{
            try {
                //null icon or a new blank one will be blank, invalid icons will show up as exclimations
                appIcon.setImageDrawable(icon);
            } catch (Exception e) {
                appIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_144px));
            }
        }

        Button appbutton = (Button) child.findViewById(R.id.item_app_button);
        //if the launch intent isn't null, and its clicked/tapped, make its click function start the app
            listenupdown(appbutton, index, secondaryIndex, isLaunchable, launchintent, appname);
        return child;
    }
}
