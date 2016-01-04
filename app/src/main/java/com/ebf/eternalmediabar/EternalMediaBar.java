package com.ebf.eternalmediabar;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


//LAST KNOWN GOOD 12/12


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
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                        saveddata.vLists.add(new ArrayList<AppDetail>());
                    }
                }
                //load in the apps
                loadApps();
                //render everything
                loadListView(saveddata.vLists.get(hitem));

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
            loadListView(saveddata.vLists.get(hitem));

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
                    loadListView(saveddata.vLists.get(hitem));
                }
                return true;
            }
            //case event for left
            case KeyEvent.KEYCODE_A: case KeyEvent.KEYCODE_DPAD_LEFT: case KeyEvent.KEYCODE_8: case KeyEvent.KEYCODE_NUMPAD_8: {

                if (hitem > 0) {
                    hitem--;
                    vitem=0;
                    loadListView(saveddata.vLists.get(hitem));
                }
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
                    //onOptions(1, false, saveddata.vLists.get(hitem).get(vitem).name, (String) saveddata.vLists.get(hitem).get(vitem).label);
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

        //empty hli first to be sure we dont accidentally make duplicate entries
        hli.clear();
        //setup the horizontal bar, theres a pre-defined setting to ease the ability for custom options later down the road.most importantly it simplifies the code.
        hli.add(createAppDetail(1, "Settings", svgLoad(R.drawable.settings_144px)));
        hli.add(createAppDetail(1, "Extra", svgLoad(R.drawable.extras_144px)));
        hli.add(createAppDetail(1, "Photo", svgLoad(R.drawable.photo_144px)));
        hli.add(createAppDetail(1, "Media", svgLoad(R.drawable.media_144px)));
        hli.add(createAppDetail(1, "Games", svgLoad(R.drawable.games_144px)));
        hli.add(createAppDetail(1, "Web", svgLoad(R.drawable.web_144px)));
        hli.add(createAppDetail(1, "News & Weather", svgLoad(R.drawable.news_weather_144px)));
        hli.add(createAppDetail(1, "Store", svgLoad(R.drawable.shop_144px)));
        if (saveddata.vLists.get(saveddata.vLists.size()-1).size() >0) {
            hli.add(createAppDetail(1, "New Apps", svgLoad(R.drawable.new_install_144px)));
        }
        else{
            if (hitem>hli.size()){
                hitem=hli.size();
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






	private void onEnter(final int index, final int secondaryIndex, final boolean islaunchable, final String launchIntent, final String appname){
		if (islaunchable) {
			EternalMediaBar.this.startActivity(manager.getLaunchIntentForPackage(launchIntent));
		} else {
			if (launchIntent.equals("")) {
				hitem = (index);
				loadListView(saveddata.vLists.get(hitem));
			} else {
                //initialize the variables for the list ahead of time
                final LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
                Llayout.removeAllViews();
                int optii = 0;
                //choose which list to make dependant on the values given for the call.
				switch (index) {
					case 0: {
                        //do nothing/close and save settings
                        optionsmenu = false;
                        optionVitem=1;
                        //animate menu closing
                        TranslateAnimation anim = new TranslateAnimation(0,(144 * getResources().getDisplayMetrics().density + 0.5f), 0, 0);
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
                                //manually set position of menu off screen
                                Llayout.setX(getResources().getDisplayMetrics().widthPixels);
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
                        View child = createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, "");
                        Llayout.addView(child);
                        optii++;

                        for (; optii <= saveddata.vLists.size(); ) {
                            if (optii - 1 != hitem) {
                                child = createMenuEntry(R.layout.options_item, "Move to " + hli.get(optii - 1).label, null, 3, optii - 1, false, ".", "3");
                                Llayout.addView(child);
                            }
                            optii++;
                        }
                        //return to first settings menu
                        child = createMenuEntry(R.layout.options_item, "Go Back", null, 8, 0, false, launchIntent, appname);
                        Llayout.addView(child);
                        //close settings menu
                        child = createMenuEntry(R.layout.options_item, "Exit Options", null, 0, 0, false, launchIntent, appname);
                        Llayout.addView(child);
                        optionVitem = 1;
                        break;
                    }
                    case 2: {
                        //move item list
                        View child = createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, "");
                        Llayout.addView(child);
                        optii++;

                        for (; optii <= saveddata.vLists.size(); ) {
                            if (optii - 1 != hitem) {
                                child = createMenuEntry(R.layout.options_item, "Move to " + hli.get(optii - 1).label, null, 4, optii - 1, false, ".", "");
                                Llayout.addView(child);
                            }
                            optii++;
                        }
                        //Automatically get the category for this item from google play
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (cm.getActiveNetworkInfo() != null) {
                            //if there is Wifi, go ahead and try.
                            if (cm.getActiveNetworkInfo() == cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)) {
                                child = createMenuEntry(R.layout.options_item, "Auto Move", null, 9, optii - 1, false, launchIntent, appname);
                                Llayout.addView(child);
                            }
                            //if there is no wifi but there is still internet
                            else{
                                child = createMenuEntry(R.layout.options_item, "Auto Move", null, 10, optii - 1, false, launchIntent, appname);
                                Llayout.addView(child);
                            }
                        }
                        //return to first settings menu
                        child = createMenuEntry(R.layout.options_item, "Go Back", null, 8, 0, false, launchIntent, appname);
                        Llayout.addView(child);
                        //close settings menu
                        child = createMenuEntry(R.layout.options_item, "Exit Options", null, 0, 0, false, launchIntent, appname);
                        Llayout.addView(child);
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
                        loadListView(saveddata.vLists.get(hitem));
                        break;
                    }
					case 5: {
                        //remove/hide item(vitem);
                            saveddata.vLists.get(hitem).remove(vitem);
                        //resize the layout and save,
                        onEnter(0,0,false,"","");
                        break;
                    }
					case 6: {
                        //open application settings
                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + Uri.parse(launchIntent)));
                        startActivity(intent);
                    }
                    case 7: {
                        //do nothing
                    }
                    case 8: {
                        //go back to main options menu
                        onOptions(index, true, launchIntent, appname);
                    }

                    case 9:{
                        //search for if the app is locally defined first
                        //if (launchintent == com.google.googleplay || launchintent == com.aptoide.aptoide ||...){
                        //Move it to store; foundapp = true;}
                        //else if....{play music, play videos, play games, gallery, photos, }
                        //else{//use below market search

                        //search google play for the app and reorganize this app into the appropriate category

                        //first we make a search request
                        Market.AppsRequest appsRequest = Market.AppsRequest.newBuilder()
                                .setQuery(launchIntent)
                                .setStartIndex(0).setEntriesCount(1)
                                .setWithExtendedInfo(false)
                                .build();

                        //now we search the store for the app
                        MarketSession.Callback<Market.AppsResponse> callback = new MarketSession.Callback<Market.AppsResponse>() {

                            @Override
                            public void onResult(Market.ResponseContext context, Market.AppsResponse response) {

                                //using the results, we organize the app as necessary
                                String appCategory = response.getApp(0).getExtendedInfo().getCategory();
                                //let's put the results to logcat just so we can be sure what the category results are actually supposed to be.
                                Log.d("EternalMediaBar", "Searched GooglePlay for: " + appCategory);


                                switch (appCategory) {

                                    //WARNING: the case values may not be accurate, need the debug log output to find true values

                                    case "TOOLS": {
                                        //Move to settings
                                        saveddata.vLists.get(0).add(saveddata.vLists.get(hitem).get(vitem));
                                        saveddata.vLists.get(hitem).remove(vitem);
                                        foundApp = true;
                                    }
                                    case "GAME":{
                                        //Move to games
                                        saveddata.vLists.get(4).add(saveddata.vLists.get(hitem).get(vitem));
                                        saveddata.vLists.get(hitem).remove(vitem);
                                        foundApp = true;
                                    }
                                    case "NEWS_AND_MAGAZINES": case "WEATHER":{
                                        //Move to news & weather
                                        saveddata.vLists.get(6).add(saveddata.vLists.get(hitem).get(vitem));
                                        saveddata.vLists.get(hitem).remove(vitem);
                                        foundApp = true;
                                    }
                                    case "COMMUNICATION":{
                                        //Move to web
                                        saveddata.vLists.get(5).add(saveddata.vLists.get(hitem).get(vitem));
                                        saveddata.vLists.get(hitem).remove(vitem);
                                        foundApp = true;
                                    }
                                    case "MEDIA_AND_VIDEO":case "MUSIC_AND_AUDIO":{
                                        //Move to media
                                        saveddata.vLists.get(3).add(saveddata.vLists.get(hitem).get(vitem));
                                        saveddata.vLists.get(hitem).remove(vitem);
                                        foundApp = true;
                                    }
                                    case "PHOTOGRAPHY":{
                                        //Move to photo
                                        saveddata.vLists.get(2).add(saveddata.vLists.get(hitem).get(vitem));
                                        saveddata.vLists.get(hitem).remove(vitem);
                                        foundApp = true;
                                    }
                                }

                            }
                        };
                        //now append the results to a session
                        MarketSession session = new MarketSession();
                        session.append(appsRequest, callback);
                        session.flush();


                        //do the below after all the organization attempts

                        //if the app wasint found, display a warning error
                        if (!foundApp){
                            //go to the menu that will display the error warning
                            onEnter(10,1,false,launchIntent,appname);
                        }
                        else{
                            //reload the listview to see the changes.
                            loadListView(saveddata.vLists.get(hitem));
                            //set foundApp to false so we can do this all again at some point
                            foundApp = false;
                            onEnter(0,0,false,".",".");
                        }
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
                        }
                    }
				}
			}
		}
	}

	
	private void onOptions( final int index, final boolean islaunchable, final String launchIntent, final String appname){
        //first check to be sure its something that should be opening the menu
		if (islaunchable) {
            //set the variables for the menu
			optionsmenu = true;
			optionVitem = 1;
			vitem = (index);
            //load the layout and make sure nothing is in it.
			loadListView(saveddata.vLists.get(hitem));
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
            View child = createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, "");
            Llayout.addView(child);


            //add all the extra options

            //copy the item to another category
            child = createMenuEntry(R.layout.options_item, "Copy to...", null, 1, 0, false, launchIntent, appname);
            Llayout.addView(child);

            //move the item to another category
            child = createMenuEntry(R.layout.options_item, "Move to...", null, 2, 0, false, launchIntent, appname);
            Llayout.addView(child);

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
                child = createMenuEntry(R.layout.options_item, "Remove From This List", null, 5, 0, false, launchIntent, "4");
                Llayout.addView(child);
            }

            //open the app's settings
            child = createMenuEntry(R.layout.options_item, "Application Settings", null, 6, 0, false, launchIntent, appname);
            Llayout.addView(child);

            //close settings menu
            child = createMenuEntry(R.layout.options_item, "Exit Options", null, 0, 0, false, launchIntent, appname);
            Llayout.addView(child);

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
            app.icon=ContextCompat.getDrawable(this, R.drawable.error_144px);
        }
        return app;
    }

    //call function for drawing menu entries
    public View createMenuEntry(int inflater, CharSequence text, @Nullable Drawable icon, final int index, final int secondaryIndex, final Boolean isLaunchable, final String launchIntent, final String appname){
        //initialize the views we know will be there
        View child = getLayoutInflater().inflate(inflater, null);
        TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
        appLabel.setText(text);
        child.findViewById(R.id.item_app_label_glow).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textglow));
        //if the launch intent exists try and add an icon from it
        if (launchIntent.length()>1) {
            //if it's an options menu item the image view will fail and skip this
            final ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
            //run the image grabber in a secondary thread to help performance.
            runRun.execute(new Runnable() {
                @Override
                public void run() {
                    //attempt to add the icon from the launchIntent
                    //null icon or a new blank one will be blank, invalid icons will show up as exclamations
                    try {
                        appIcon.setImageDrawable(manager.getApplicationIcon(launchIntent));
                    } catch (Exception e) {
                        appIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.error_144px));
                    }
                }
            });

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
                onEnter(index, secondaryIndex, isLaunchable, launchIntent, appname);
            }
        });

        btn.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onOptions(index, isLaunchable, launchIntent, appname);
                return true;
            }
        });

        //return the view value
        return child;
    }

    AsyncTask task = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }
    };

}

class runRun extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}
