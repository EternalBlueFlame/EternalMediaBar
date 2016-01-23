package com.ebf.eternalmediabar;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


//LAST KNOWN GOOD 1/14


public class EternalMediaBar extends Activity {

    private PackageManager manager;
    private List<AppDetail> oldApps = new ArrayList<>();
    private List<AppDetail> hli = new ArrayList<>();
    private settingsClass saveddata = new settingsClass();

    public int hitem = 0;
    private boolean init = false;
    private boolean optionsmenu = false;
    public int vitem = 0;
    public int optionVitem =1;
    public boolean[] warningtoggle;
    private boolean foundApp = false;



    //override the on create method to run the starting scripts
        //@Override will override the built in function with your own. this is mostly for being able to call functions without having to specifically call it.
        @Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //set the current layout value
            setContentView(R.layout.activity_eternal_media_bar);

            //run once
            if (!init) {
                if (saveddata.vLists.size()<=1) {
                    try {
                        //try load prefrences
                        //for some off reason only one file can be loaded EVER, so all variables that ever need to be accessed have to be in the same serialieable class.
                        FileInputStream fileStream = openFileInput("lists.dat");
                        ObjectInputStream objStream = new ObjectInputStream(fileStream);
                        saveddata = (settingsClass) objStream.readObject();
                        //close the stream to save RAM.
                        objStream.close();
                        fileStream.close();
                        //for some odd reason savedata.oldApps cant be accessed directly in most cases, so we'll push it to another variable to edit and change.
                        oldApps = saveddata.oldApps;
                    } catch (Exception e) {
                        //output to debug log just in case something went fully wrong
                        e.printStackTrace();
                        //catch with below by initializing vLists properly
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                    }
                }
                //load in the apps
                loadApps();
                //render everything
                loadListView();

                //setup the warning variable
                warningtoggle= new boolean[1];
                warningtoggle[0] = false;

                //make sure this doesnt happen again
                init = true;
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        if (init){
            //load in the apps
            loadApps();
            //make sure vitem isn't out of bounds
            if (vitem >= saveddata.vLists.get(hitem).size()){
                vitem = saveddata.vLists.get(hitem).size();
            }
            //render everything
            loadListView();

        }
    }

    public void savefiles(){
        try{
            // apply the instanced value back to the savedata version so we can save it.
            saveddata.oldApps = oldApps;
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
                listmove(vitem+1, false);
                return true;
            }
            //case event for up
            case KeyEvent.KEYCODE_W: case KeyEvent.KEYCODE_DPAD_UP: case KeyEvent.KEYCODE_2: case KeyEvent.KEYCODE_NUMPAD_2:{
                listmove(vitem-1, false);
                return true;
            }
            //case event for right
            case KeyEvent.KEYCODE_D: case KeyEvent.KEYCODE_DPAD_RIGHT: case KeyEvent.KEYCODE_6: case KeyEvent.KEYCODE_NUMPAD_6:{
                listmove(hitem+1, true);
                return true;
            }
            //case event for left
            case KeyEvent.KEYCODE_A: case KeyEvent.KEYCODE_DPAD_LEFT: case KeyEvent.KEYCODE_8: case KeyEvent.KEYCODE_NUMPAD_8: {
                listmove(hitem-1, true);
                return true;
            }
            //event for when enter/x/a is pressed
			case KeyEvent.KEYCODE_ENTER: case KeyEvent.KEYCODE_NUMPAD_ENTER: case KeyEvent.KEYCODE_DPAD_CENTER: case KeyEvent.KEYCODE_1: case KeyEvent.KEYCODE_5: case KeyEvent.KEYCODE_NUMPAD_5: case KeyEvent.KEYCODE_BUTTON_1: {
                if (!optionsmenu) {
                    //get the layout
                    LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
                    //get the item in the layout and activate its button function
                    Vlayout.getChildAt(vitem).findViewById(R.id.item_app_button).performClick();
                }
                else{
                    //get the layout
                    LinearLayout Llayout = (LinearLayout)findViewById(R.id.optionslist);
                    //get the item in the layout and activate its button function
                    Llayout.getChildAt(optionVitem).findViewById(R.id.item_app_button).performClick();
                }
				return true;
			}
            //event for when E/Y/Triangle is pressed
			case KeyEvent.KEYCODE_BUTTON_4: case KeyEvent.KEYCODE_E: case KeyEvent.KEYCODE_TAB: case KeyEvent.KEYCODE_0: case KeyEvent.KEYCODE_NUMPAD_0: {
                if (!optionsmenu) {
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


    // function to move when a key or button is pressed, it's much lighter than the usual loadlist function.
    void listmove(int move, boolean isCategory){
        //function to move the highlight selection based on which menu you are on.
        if (!isCategory) {
            //if you are not on the options menu
            if (!optionsmenu) {
                LinearLayout Vlayout = (LinearLayout) findViewById(R.id.apps_display);
                boolean proceed = true;
                //if you are trying to move too far down set proceed to false
                if (vitem > move) {
                    if (vitem == 0) {
                        proceed = false;
                    }
                }
                //if you are trying to move too far up set proceed to false
                else if ((vitem + 2) > Vlayout.getChildCount()) {
                    proceed = false;
                }

                if (proceed) {
                    //change the old shadow
                    TextView appLabel = (TextView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_label);
                    appLabel.setShadowLayer(0f, 0f, 0f, Color.argb(150, 0, 0, 0));
                    appLabel.setLines(2);
                    appLabel.setTypeface(null, Typeface.NORMAL);
                    ImageView appIcon = (ImageView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1f);
                    appIcon.setScaleY(1f);
                    //change vitem
                    vitem = move;
                    //change the new shadow
                    appLabel = (TextView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_label);
                    appLabel.setShadowLayer(25f, 1f, 1f, Color.argb(255, 0, 0, 0));
                    appLabel.setLines(2);
                    appLabel.setTypeface(null, Typeface.BOLD_ITALIC);
                    appIcon = (ImageView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1.25f);
                    appIcon.setScaleY(1.25f);

                    //scroll to the new entry
                    Vlayout.scrollTo((int) Vlayout.getChildAt(vitem).getX(), 0);

                }
            }
            //if you are on the options menu
            else {
                move -= vitem;
                move += optionVitem;
                LinearLayout Vlayout = (LinearLayout) findViewById(R.id.optionslist);
                boolean proceed = true;
                //if you are trying to move too far down set proceed to false
                if (optionVitem > move) {
                    if (optionVitem == 1) {
                        proceed = false;
                    }
                }
                //if you are trying to move too far up set proceed to false
                else if ((optionVitem + 2) > Vlayout.getChildCount()) {
                    proceed = false;
                }

                if (proceed) {
                    //change the old shadow
                    TextView appLabel = (TextView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_label);
                    appLabel.setShadowLayer(0f, 0f, 0f, Color.argb(150, 0, 0, 0));
                    appLabel.setLines(2);
                    appLabel.setTypeface(null, Typeface.NORMAL);
                    ImageView appIcon = (ImageView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1f);
                    appIcon.setScaleY(1f);
                    //change Optionsvitem
                    optionVitem = move;
                    //change the new shadow
                    appLabel = (TextView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_label);
                    appLabel.setShadowLayer(25f, 1f, 1f, Color.argb(255, 0, 0, 0));
                    appLabel.setLines(2);
                    appLabel.setTypeface(null, Typeface.BOLD_ITALIC);
                    appIcon = (ImageView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1.25f);
                    appIcon.setScaleY(1.25f);
                    //scroll to the new entry
                    Vlayout.scrollTo((int) Vlayout.getChildAt(optionVitem).getX(), 0);
                }
            }
        }
        else{

            LinearLayout Hlayout = (LinearLayout) findViewById(R.id.categories);
            boolean proceed = true;
            //if you are trying to move too far down set proceed to false
            if (hitem-1 > move) {
                if (hitem == 1) {
                    proceed = false;
                }
            }
            //if you are trying to move too far up set proceed to false
            else if ((hitem + 3) > Hlayout.getChildCount()) {
                proceed = false;
            }

            if (proceed) {
                //change hitem
                hitem = move;
                //reload the list
                loadListView();
                //change the new shadow, because we reload the list first, we don't have to manually reset the previous entry.
                TextView appLabel = (TextView) Hlayout.getChildAt(move + 1).findViewById(R.id.item_app_label);
                appLabel.setShadowLayer(25f, 1f, 1f, Color.argb(255, 0, 0, 0));
                appLabel.setLines(2);
                appLabel.setTypeface(null, Typeface.BOLD_ITALIC);
                ImageView appIcon = (ImageView) Hlayout.getChildAt(hitem+1).findViewById(R.id.item_app_icon);
                appIcon.setScaleX(1.25f);
                appIcon.setScaleY(1.25f);
                //scroll to the new entry
                Hlayout.scrollTo(0, (int) Hlayout.getChildAt(move).getY());
            }
        }
    }

    //load the installed apps and sort them into their proper places on the lists.
    private void loadApps(){
        manager = getPackageManager();
        List<String> newapps = new ArrayList<>();
        //get the apps from the intent activity list of resolve info in the host OS.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent , 0);
        //copy only the necessary info from each app into a copy of the AppDetail Class
        for(ResolveInfo ri : availableActivities) {
            AppDetail appri = new AppDetail();
            appri.label = ri.loadLabel(manager);
            appri.name = ri.activityInfo.packageName;
            appri.isMenu = 0;
            appri.icon = null;
            //add the app to the list of all new apps to compair against oldApps later.
            newapps.add(ri.activityInfo.packageName);
            //check if the app has previously been found
            boolean fail = false;
            //check each entry in oldApps
            for (int i=0; i<oldApps.size();){
                //in each entry check to see if the app launch intent is the same
                if (oldApps.get(i).name.equals(appri.name)){
                    //if one entry is the same set fail to true and break the search
                    fail=true;
                }
                if (fail){break;}
                else{i++;}
            }
            //if fail is false, add the app to the newly installed list where the user can organize it, and the old apps list.where we can keep track of it easier.
            if (!fail) {
                saveddata.vLists.get(saveddata.vLists.size()-1).add(appri);
                oldApps.add(appri);
            }
        }



        //now check if there are any apps in the old list that are no longer installed, and be sure to remove them from any list they may be on
            for (int i = 0; i < oldApps.size(); ) {
                if (!newapps.contains(oldApps.get(i).name)){
                    //create an instance of the app
                    AppDetail toremove = oldApps.get(i);
                    //search all lists for it and remove each entry.
                    for (int ii=0; ii< saveddata.vLists.size();){
                        if (saveddata.vLists.get(ii).contains(toremove)){
                            saveddata.vLists.get(ii).remove(toremove);
                        }
                        ii++;
                    }
                    oldApps.remove(toremove);
                }
                i++;
            }

        savefiles();
    }

    //intended to later return a drawable from an SVG when support is better
    //currently returns a normal drawable from PNG image.
    Drawable svgLoad(int imagetoload){
        ImageView imageView = new ImageView(this);

        imageView.setImageDrawable(ContextCompat.getDrawable(this, imagetoload));

        //more SVG stuff that has to wait.
        //imageView.setImageDrawable(svg.createPictureDrawable());
        return imageView.getDrawable();
    }

    //draws the list of apps and categories to screen
    public void loadListView(){
        manager = getPackageManager();

        //empty hli first to be sure we dont accidentally make duplicate entries
        hli.clear();
        //setup the horizontal bar, theres a pre-defined setting to ease the ability for custom options later down the road.most importantly it simplifies the code.
        //check if we are using google icons, if not use built-in icons.
        if (!saveddata.useGoogleIcons) {
            hli.add(createAppDetail(1, "Social", svgLoad(R.drawable.social_144px)));
            hli.add(createAppDetail(1, "Media", svgLoad(R.drawable.media_144px)));
            hli.add(createAppDetail(1, "Games", svgLoad(R.drawable.games_144px)));
            hli.add(createAppDetail(1, "Web", svgLoad(R.drawable.web_144px)));
            hli.add(createAppDetail(1, "Utility", svgLoad(R.drawable.extras_144px)));
            hli.add(createAppDetail(1, "Settings", svgLoad(R.drawable.settings_144px)));
        }
        else{
            try{hli.add(createAppDetail(1, "Social", manager.getApplicationIcon("com.android.contacts")));}
            catch (Exception e){hli.add(createAppDetail(1, "Social", svgLoad(R.drawable.social_144px)));}
            //For media we actually try and combine some icons, so this is more complicated
            Drawable[] layers = new Drawable[2];
            try{layers[0] = manager.getApplicationIcon("com.google.android.videos");}
            catch (Exception e){}
            try{layers[1] = manager.getApplicationIcon("com.google.android.music");}
            catch (Exception e){}
            if (layers != new Drawable[2]){
                hli.add(createAppDetail(1, "Media", new LayerDrawable(layers)));
            }
            else{
                hli.add(createAppDetail(1, "Media", svgLoad(R.drawable.media_144px)));
            }
            try{hli.add(createAppDetail(1, "Games", manager.getApplicationIcon("com.google.android.play.games")));}
            catch (Exception e){hli.add(createAppDetail(1, "Games", svgLoad(R.drawable.games_144px)));}
            try{hli.add(createAppDetail(1, "Web", manager.getApplicationIcon("com.android.chrome")));}
            catch (Exception e){hli.add(createAppDetail(1, "Web", svgLoad(R.drawable.web_144px)));}
            try{hli.add(createAppDetail(1, "Utility", manager.getApplicationIcon("com.google.android.apps.docs")));}
            catch (Exception e){hli.add(createAppDetail(1, "Utility", svgLoad(R.drawable.extras_144px)));}
            try{hli.add(createAppDetail(1, "Settings", manager.getApplicationIcon("com.android.settings")));}
            catch (Exception e){hli.add(createAppDetail(1, "Settings", svgLoad(R.drawable.settings_144px)));}
        }
        //now draw the new apps icon if there are any new apps.
        if (saveddata.vLists.get(saveddata.vLists.size()-1).size() >0) {
            hli.add(createAppDetail(1, "New Apps", svgLoad(R.drawable.new_install_144px)));
        }

        LinearLayout layout = (LinearLayout)findViewById(R.id.categories);
        //empty the list
        layout.removeAllViews();
        //add a blank view to the front
        layout.addView(createMenuEntry(R.layout.category_item, "", svgLoad(R.drawable.blank), -1, 0, false, "", ""));
        //loop to add all entries of hli to the list
        for (int ii=0; (ii)<hli.size();) {
                layout.addView(createMenuEntry(R.layout.category_item, hli.get(ii ).label, hli.get(ii).icon, ii, 0, false, "", ""));
        ii++;
        }
        //add an empty view to the end of the list
        layout.addView(createMenuEntry(R.layout.category_item, "", svgLoad(R.drawable.blank), -1, 0, false, "", ""));



        //copy category method but with a verticle list
        LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
        Vlayout.removeAllViews();

        //Create entries for EMB specific apps
        if (hitem == 5){
            Vlayout.addView(createMenuEntry(R.layout.list_item, "Eternal Media Bar - Settings", svgLoad(R.drawable.sub_settings_144px), 11, 0, false, ".", ".opt"));
        }


        for (int ii=0; ii<saveddata.vLists.get(hitem).size();) {
            Vlayout.addView(createMenuEntry(R.layout.list_item, saveddata.vLists.get(hitem).get(ii).label, null, ii, 0, true, saveddata.vLists.get(hitem).get(ii).name, (String) saveddata.vLists.get(hitem).get(ii).label));
            ii++;
        }
    }






	private void onEnter(final int index, final int secondaryIndex, final boolean islaunchable, final String launchIntent, final String appname){
		if (islaunchable) {
			EternalMediaBar.this.startActivity(manager.getLaunchIntentForPackage(launchIntent));
		}
        else {
			if (launchIntent.equals("")) {
                if (index!=-1){
                    listmove(index, true);
                }
			}
            else {
                //initialize the variables for the list ahead of time
                ScrollView Slayout = (ScrollView) findViewById(R.id.options_displayscroll);
                LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
                Llayout.removeAllViews();
                int optii = 0;
                //choose which list to make dependant on the values given for the call.
				switch (index) {
					case 0: {
                        //do nothing/close and save settings
                        optionsmenu = false;
                        optionVitem=1;
                        //animate menu closing
                        TranslateAnimation anim = new TranslateAnimation(0,(146 * getResources().getDisplayMetrics().density + 0.5f), 0, 0);
                        anim.setDuration(200);
                        anim.setInterpolator(new LinearInterpolator());
                        anim.setFillEnabled(false);
                        Slayout.setAnimation(anim);
                        //now move the menu itself
                        Slayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                ScrollView Slayout = (ScrollView) findViewById(R.id.options_displayscroll);
                                // clear animation to prevent flicker
                                Slayout.clearAnimation();
                                //manually set position of menu off screen
                                Slayout.setX(getResources().getDisplayMetrics().widthPixels);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        //save just to be sure.
                        savefiles();
							break;
						}
					case 1: {
                        //Copy Item List
                        Llayout.addView(createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, ""));

                        for (; optii < saveddata.vLists.size()-1; ) {
                            if (optii != hitem) {
                                Llayout.addView(createMenuEntry(R.layout.options_item, "Copy to " + hli.get(optii).label, svgLoad(R.drawable.blank), 3, optii, false, ".", "3"));
                            }
                            optii++;
                        }
                        //return to first settings menu
                        Llayout.addView(createMenuEntry(R.layout.options_item, "Go Back",svgLoad(R.drawable.blank), 8, 0, false, launchIntent, appname));
                        //close settings menu
                        Llayout.addView(createMenuEntry(R.layout.options_item, "Exit Options", svgLoad(R.drawable.blank), 0, 0, false, launchIntent, appname));
                        optionVitem = 1;
                        break;
                    }
                    case 2: {
                        //move item list
                        Llayout.addView(createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, ""));

                        for (; optii < saveddata.vLists.size()-1; ) {
                            if (optii != hitem) {
                                Llayout.addView(createMenuEntry(R.layout.options_item, "Move to " + hli.get(optii).label, svgLoad(R.drawable.blank), 4, optii, false, ".", ""));
                            }
                            optii++;
                        }
                        // !!! ENABLE AFTER FIXED !!!
                        //Automatically get the category for this item from google play
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (cm.getActiveNetworkInfo() != null) {
                            //if there is Wifi, go ahead and try.
                            if (cm.getActiveNetworkInfo() == cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)) {
                                //child = createMenuEntry(R.layout.options_item, "Auto Move", null, 9, optii - 1, false, launchIntent, appname);
                                //Llayout.addView(child);
                            }
                            //if there is no wifi but there is still internet
                            else{
                                //child = createMenuEntry(R.layout.options_item, "Auto Move", svgLoad(R.drawable.blank), 10, optii - 1, false, launchIntent, appname);
                               //Llayout.addView(child);
                            }
                        }
                        //return to first settings menu
                        Llayout.addView(createMenuEntry(R.layout.options_item, "Go Back", svgLoad(R.drawable.blank), 8, 0, false, launchIntent, appname));
                        //close settings menu
                        Llayout.addView(createMenuEntry(R.layout.options_item, "Exit Options", svgLoad(R.drawable.blank), 0, 0, false, launchIntent, appname));
                        optionVitem = 1;
                        break;
                    }
                    case 3:{
                        //copy item
                        saveddata.vLists.get(secondaryIndex).add(saveddata.vLists.get(hitem).get(vitem));
                        onEnter(0, 0, false, ".", ".");
                        break;
                    }
                    case 4:{
                        //move item
                        saveddata.vLists.get(secondaryIndex).add(saveddata.vLists.get(hitem).get(vitem));
                        saveddata.vLists.get(hitem).remove(vitem);
                        onEnter(0,0,false,".",".");
                        loadListView();
                        break;
                    }
					case 5: {
                        //resize the layout and save, we have to hide the menu first because the layouts reload when an item is removed.
                        onEnter(0,0,false,".",".");
                        //remove/hide item(vitem);
                        saveddata.vLists.get(hitem).remove(vitem);
                        loadListView();
                        break;
                    }
					case 6: {
                        //open application settings
                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + Uri.parse(launchIntent)));
                        onEnter(0, 0, false, ".", ".");
                        startActivity(intent);
                        break;
                    }
                    case 7: {
                        //do nothing
                    }
                    case 8: {
                        //go back to main options menu
                        onOptions(index, true, launchIntent, appname);
                        break;
                    }

                    case 9:{

                    }
                    case 10:{
                        switch (secondaryIndex){
                            case 0:{
                                //Warn that this action will use #MB of the user's mobile data before searching google play

                                //okay option
                                //nevermind option
                                //okay and don't remind me again option; Will set warningtoggle[1] to true;
                            }
                            case 1:{
                                //warning that the app wasint found during search
                                //for now since the app is still in early beta of RC1, let's just send it to the logcat
                                Log.d("EternalMediaBar", "Couldn't organize app: " + launchIntent);
                                onEnter(0,0,false,".",".");
                            }
                            break;
                        }
                    }
                    case 11: {
                        saveddata.useGoogleIcons = false;
                        loadListView();
                        onEnter(0, 0, false, ".", ".");
                        break;
                    }
                    case 12:{
                        saveddata.useGoogleIcons = true;
                        loadListView();
                        onEnter(0,0,false,".",".");
                        break;
                    }
				}
			}
		}
	}

	
	private void onOptions( final int index, final boolean islaunchable, final String launchIntent, final String appname){
        //first check to be sure its something that should be opening the menu
            //first, move the item highlight
            listmove(index, false);
            //set the variables for the menu
			optionsmenu = true;
			optionVitem = 1;
            //load the layout and make sure nothing is in it.
			//loadListView();
            ScrollView Slayout = (ScrollView) findViewById(R.id.options_displayscroll);
            LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
			Llayout.removeAllViews();
            //reset the position
            Slayout.setX(getResources().getDisplayMetrics().widthPixels);
            //animate the menu opening
            TranslateAnimation anim = new TranslateAnimation(0,-(146 * getResources().getDisplayMetrics().density + 0.5f), 0, 0);
            anim.setDuration(200);
            anim.setInterpolator(new LinearInterpolator());
            anim.setFillEnabled(false);
            Slayout.setAnimation(anim);
            //now move the menu itself
            Slayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ScrollView Slayout = (ScrollView) findViewById(R.id.options_displayscroll);
                    // clear animation to prevent flicker
                    Slayout.clearAnimation();
                    //manually set position of menu
                    Slayout.setX(getResources().getDisplayMetrics().widthPixels - (146 * getResources().getDisplayMetrics().density + 0.5f));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

        if (islaunchable) {
            //add the app thats selected so the user knows for sure what they are messing with.
            Llayout.addView(createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, ""));


            //add all the extra options

            //copy the item to another category
            Llayout.addView(createMenuEntry(R.layout.options_item, "Copy to...", svgLoad(R.drawable.blank), 1, 0, false, launchIntent, appname));

            //move the item to another category
            Llayout.addView(createMenuEntry(R.layout.options_item, "Move to...", svgLoad(R.drawable.blank), 2, 0, false, launchIntent, appname));

            //first option is to remove an item from the list.
            //in RC2 this will be modified to support hiding the icon even when it's only in one menu
            int i=0;
            for (int ii=0; ii<saveddata.vLists.size();){
                for (int iii=0; iii<saveddata.vLists.get(ii).size();){
                    if (saveddata.vLists.get(ii).get(iii).name.equals(saveddata.vLists.get(hitem).get(vitem).name)){
                        i++;
                    }
                    iii++;
                }
                ii++;
            }
            if (i>1) {
                Llayout.addView(createMenuEntry(R.layout.options_item, "Remove From This List", svgLoad(R.drawable.blank), 5, 0, false, launchIntent, "4"));
            }

            //open the app's settings
            Llayout.addView(createMenuEntry(R.layout.options_item, "Application Settings", svgLoad(R.drawable.blank), 6, 0, false, launchIntent, appname));

            //close settings menu
            Llayout.addView(createMenuEntry(R.layout.options_item, "Exit Options", svgLoad(R.drawable.blank), 0, 0, false, launchIntent, appname));

        }
        else{
            if (saveddata.useGoogleIcons){
                Llayout.addView(createMenuEntry(R.layout.options_item, "Don't use Google Icons", svgLoad(R.drawable.blank), 11, 0, false, ".", "."));
            }
            else if (!saveddata.useGoogleIcons){
                Llayout.addView(createMenuEntry(R.layout.options_item, "Use Google Icons", svgLoad(R.drawable.blank), 12, 0, false, ".", "."));
            }
        }
    }

    //call function for creating app detail entries, usually for menus
    public AppDetail createAppDetail (int ismenu, String name, @Nullable Drawable icon){
        AppDetail app = new AppDetail();
        app.isMenu = ismenu;
        app.label = name;
        if (icon!=null) {
            app.icon = icon;
        }
        else{
            svgLoad(R.drawable.error_144px);
        }
        return app;
    }

    //call function for drawing menu entries
    public View createMenuEntry(int inflater, CharSequence text, @Nullable Drawable icon, final int index, final int secondaryIndex, final Boolean isLaunchable, final String launchIntent, final String appname){
        //initialize the views we know will be there
        View child = getLayoutInflater().inflate(inflater, null);
        TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
        appLabel.setText(text);
        //if the launch intent exists try and add an icon from it
        if (launchIntent.length()>1) {
            //if it's an options menu item the image view will fail and skip this
            ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
            //attempt to add the icon from the launchIntent
            //null icon or a new blank one will be blank, invalid icons will show up as exclamations
            try {
                appIcon.setImageDrawable(manager.getApplicationIcon(launchIntent));
            } catch (Exception e) {
                if (icon == null) {
                    svgLoad(R.drawable.error_144px);
                }
            }

        }
        else{
            try {
                //try to load pre-designated icon, only for bundeled icons.
                ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(icon);
            } catch (Exception e) {}
        }

        //setup the onclick listener and button
        Button btn = (Button) child.findViewById(R.id.item_app_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appname.equals(".opt")){
                    onOptions(index, isLaunchable, launchIntent, appname);
                }
                else {
                    onEnter(index, secondaryIndex, isLaunchable, launchIntent, appname);
                }
            }
        });

        btn.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (optionsmenu){
                    onEnter(0,0,false,".",".");
                }
                onOptions(index, isLaunchable, launchIntent, appname);
                return true;
            }
        });

        //return the view value
        return child;
    }

}

class runRun extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}
