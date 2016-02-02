package com.ebf.eternalmediabar;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


//LAST KNOWN GOOD 1/24


public class EternalMediaBar extends Activity {

    public PackageManager manager;
    private List<AppDetail> oldApps = new ArrayList<>();
    public List<AppDetail> hli = new ArrayList<>();
    public settingsClass savedData = new settingsClass();

    public int hitem = 0;
    private boolean init = false;
    public boolean optionsMenu = false;
    public int vitem = 0;
    public int optionVitem =1;
    public boolean[] warningtoggle;
    private boolean foundApp = false;

    private optionsMenuChange changeOptionsMenu = new optionsMenuChange();



    //override the on create method to run the starting scripts
        //@Override will override the built in function with your own. this is mostly for being able to call functions without having to specifically call it.
        @Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //set the current layout value
            setContentView(R.layout.activity_eternal_media_bar);

            //run once
            if (!init) {
                if (savedData.vLists.size()<=1) {
                    try {
                        //try load prefrences
                        //Load the value as a reference to the file instead of a cloned instance of it, just because it's easier, actual efficiency is yet to be determined.
                        FileInputStream fileStream = openFileInput("lists.dat");
                        ObjectInputStream objStream = new ObjectInputStream(fileStream);
                        savedData = (settingsClass) objStream.readObject();
                        //close the stream to save RAM.
                        objStream.close();
                        fileStream.close();
                        //for some odd reason saveData.oldApps cant be accessed directly in most cases, so we'll push it to another variable to edit and change.
                        oldApps = savedData.oldApps;
                    }
                    catch (Exception e) {
                        //output to debug log just in case something went fully wrong
                        e.printStackTrace();
                        //catch with below by initializing vLists properly
                        savedData.vLists.add(new ArrayList<AppDetail>());
                        savedData.vLists.add(new ArrayList<AppDetail>());
                        savedData.vLists.add(new ArrayList<AppDetail>());
                        savedData.vLists.add(new ArrayList<AppDetail>());
                        savedData.vLists.add(new ArrayList<AppDetail>());
                        savedData.vLists.add(new ArrayList<AppDetail>());
                        savedData.vLists.add(new ArrayList<AppDetail>());
                        //we should initialize the other variables as well.
                        savedData.useGoogleIcons = false;
                        savedData.mirrorMode = false;
                        savedData.cleanCacheOnStart = false;
                        savedData.gamingMode = false;
                        savedData.useManufacturerIcons = false;
                        savedData.loadAppBG = true;
                        savedData.fontCol = Color.WHITE;
                        savedData.menuCol = Color.WHITE;
                        savedData.iconCol = Color.WHITE;
                        savedData.hiddenApps = new ArrayList<AppDetail>();
                    }
                }
                //load in the apps
                loadApps();

                //setup the warning variable
                warningtoggle= new boolean[1];
                warningtoggle[0] = false;

                //make sure this doesn't happen again
                init = true;

                //Lastly, activate the list move function to load the list view and attempt to highlight what menu we are on.
                listmove(0, true);
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        if (init){
            //load in the apps
            loadApps();
            //make sure vitem isn't out of bounds
            if (vitem >= savedData.vLists.get(hitem).size()){
                vitem = savedData.vLists.get(hitem).size();
            }

            //make sure that if the new apps list disappears, we aren't on it.
            if (hitem == (savedData.vLists.size()-1) && savedData.vLists.get(savedData.vLists.size()-1).size()==0){
                listmove(0, true);
            }
            //otherwise just load normally
            else{
                loadListView();
            }

        }
    }

    public void savefiles(){
        try{
            // apply the instanced value back to the savedata version so we can save it.
            savedData.oldApps = oldApps;
            //create a file output stream with an object, to save a variable to a file, then close the stream.
            FileOutputStream fileStream = openFileOutput("lists.dat", Context.MODE_PRIVATE);
            ObjectOutputStream fileOutput = new ObjectOutputStream(fileStream);
            fileOutput.writeObject(savedData);
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
                if (!optionsMenu) {
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
                if (!optionsMenu) {
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
            if (!optionsMenu) {
                LinearLayout Vlayout = (LinearLayout) findViewById(R.id.apps_display);
                boolean proceed = true;
                //if you are trying to move too far down set proceed to false
                if (move < 0) {
                    proceed = false;
                }
                //if you are trying to move too far up set proceed to false
                else if (move > Vlayout.getChildCount()-1) {
                    proceed = false;
                }

                if (proceed) {
                    //change the old shadow, assuming it exists.
                    TextView appLabel;
                    ImageView appIcon;
                    try {
                        appLabel = (TextView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_label);
                        appLabel.setShadowLayer(0f, 0f, 0f, Color.argb(150, 0, 0, 0));
                        //change the font type and lines
                        appLabel.setLines(2);
                        appLabel.setTypeface(null, Typeface.NORMAL);
                        //scale the icon back to normal
                        appIcon = (ImageView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_icon);
                        appIcon.setScaleX(1f);
                        appIcon.setScaleY(1f);
                    }
                    catch(Exception e){}
                    //change vitem
                    vitem = move;
                    //change the new shadow
                    appLabel = (TextView) Vlayout.getChildAt(vitem).findViewById(R.id.item_app_label);
                    appLabel.setShadowLayer(25f, 1f, 1f, Color.argb(255, 0, 0, 0));
                    //change the font type and lines
                    appLabel.setLines(2);
                    appLabel.setTypeface(null, Typeface.BOLD_ITALIC);
                    //scale the icon larger
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
                if (move < 0) {
                    proceed = false;
                }
                //if you are trying to move too far up set proceed to false
                else if (move > Vlayout.getChildCount()-1) {
                    proceed = false;
                }

                if (proceed) {
                    //change the old shadow
                    TextView appLabel = (TextView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_label);
                    appLabel.setShadowLayer(0f, 0f, 0f, Color.argb(150, 0, 0, 0));
                    //change the font type and lines
                    appLabel.setLines(2);
                    appLabel.setTypeface(null, Typeface.NORMAL);
                    //scale the icon back to normal
                    ImageView appIcon = (ImageView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1f);
                    appIcon.setScaleY(1f);
                    //change Optionsvitem
                    optionVitem = move;
                    //change the new shadow
                    appLabel = (TextView) Vlayout.getChildAt(optionVitem).findViewById(R.id.item_app_label);
                    appLabel.setShadowLayer(25f, 1f, 1f, Color.argb(255, 0, 0, 0));
                    //change the font type and lines
                    appLabel.setLines(2);
                    appLabel.setTypeface(null, Typeface.BOLD_ITALIC);
                    //scale the icon to be larger
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
            if (move < 0) {
                proceed = false;
            }
            //if you are trying to move too far up set proceed to false
            else if ((hitem + 1) > Hlayout.getChildCount()) {
                proceed = false;
            }

            if (proceed) {
                //change hitem
                hitem = move;
                //reload the list
                loadListView();
                //change the new shadow, because we reload the list first, we don't have to manually reset the previous entry.
                TextView appLabel = (TextView) Hlayout.getChildAt(hitem).findViewById(R.id.item_app_label);
                appLabel.setShadowLayer(25f, 1f, 1f, Color.argb(255, 0, 0, 0));
                appLabel.setLines(2);
                //change the font type
                appLabel.setTypeface(null, Typeface.BOLD_ITALIC);
                //modify the icon to go be larger, and go under the text so it appears even bigger and doesn't scale out of the view.
                ImageView appIcon = (ImageView) Hlayout.getChildAt(hitem).findViewById(R.id.item_app_icon);
                appIcon.setScaleX(1.25f);
                appIcon.setScaleY(1.25f);
                appIcon.setY(3 * getResources().getDisplayMetrics().density + 0.5f);
                //scroll to the new entry
                Hlayout.scrollTo(0, (int) Hlayout.getChildAt(hitem).getY());
                listmove(0, false);
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
            //add the app to the list of all new apps to compare against oldApps later.
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
                savedData.vLists.get(savedData.vLists.size()-1).add(appri);
                oldApps.add(appri);
            }
        }



        //now check if there are any apps in the old list that are no longer installed, and be sure to remove them from any list they may be on
            for (int i = 0; i < oldApps.size(); ) {
                if (!newapps.contains(oldApps.get(i).name)){
                    //create an instance of the app
                    AppDetail toremove = oldApps.get(i);
                    //search all lists for it and remove each entry.
                    for (int ii=0; ii< savedData.vLists.size();){
                        if (savedData.vLists.get(ii).contains(toremove)){
                            savedData.vLists.get(ii).remove(toremove);
                        }
                        ii++;
                    }
                    oldApps.remove(toremove);
                }
                i++;
            }

        savefiles();
    }


    //returns a normal drawable from PNG image.
    Drawable svgLoad(int imagetoload){
        //imageView.setImageDrawable(svg.createPictureDrawable());
        return ContextCompat.getDrawable(this, imagetoload);
    }

    //draws the list of apps and categories to screen
    public void loadListView(){

        if (savedData.mirrorMode){
            setContentView(R.layout.activity_eternal_media_bar_mirror);
        }
        else{
            setContentView(R.layout.activity_eternal_media_bar);
        }

        manager = getPackageManager();

        //empty hli first to be sure we dont accidentally make duplicate entries
        hli.clear();
        //setup the horizontal bar, theres a pre-defined setting to ease the ability for custom options later down the road.most importantly it simplifies the code.
        //check if we are using google icons, if not use built-in icons.
        if (!savedData.useGoogleIcons) {
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
            try{layers[1] = new ScaleDrawable(manager.getApplicationIcon("com.google.android.music"),Gravity.CENTER,1f,1f);
                    layers[1].setLevel(7000);}
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
        if (savedData.vLists.get(savedData.vLists.size()-1).size() >0) {
            hli.add(createAppDetail(1, "New Apps", svgLoad(R.drawable.new_install_144px)));
        }

        LinearLayout layout = (LinearLayout)findViewById(R.id.categories);
        //empty the list
        layout.removeAllViews();
        //loop to add all entries of hli to the list
        for (int ii=0; (ii)<hli.size();) {
                layout.addView(createMenuEntry(R.layout.category_item, hli.get(ii ).label, hli.get(ii).icon, ii, 0, false, "", ""));
        ii++;
        }


        //copy category method but with a verticle list
        LinearLayout Vlayout = (LinearLayout)findViewById(R.id.apps_display);
        for(int i=0; i<Vlayout.getChildCount();){
            Vlayout.getChildAt(i).invalidate();
            i++;
        }
        Vlayout.removeAllViews();
        //Create entries for EMB specific apps
        if (hitem == 5){
            Vlayout.addView(createMenuEntry(R.layout.list_item, "Eternal Media Bar - Settings", svgLoad(R.drawable.sub_settings_144px), 11, 0, false, ".", ".opt"));
        }


        for (int ii=0; ii< savedData.vLists.get(hitem).size();) {
            Vlayout.addView(createMenuEntry(R.layout.list_item, savedData.vLists.get(hitem).get(ii).label, null, ii, 0, true, savedData.vLists.get(hitem).get(ii).name, (String) savedData.vLists.get(hitem).get(ii).label));
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
                LinearLayout Llayout = (LinearLayout) findViewById(R.id.optionslist);
                Llayout.removeAllViews();
                //choose which list to make dependant on the values given for the call.
				switch (index) {
					case 0: {
                        changeOptionsMenu.menuClose(this, Llayout);
                        break;
                    }
					case 1: {
                        changeOptionsMenu.createCopyList(this, Llayout, launchIntent, appname);
                        break;
                    }
                    case 2: {
                        changeOptionsMenu.createMoveList(this, Llayout, launchIntent, appname);
                        break;
                    }
                    case 3:{
                        changeOptionsMenu.copyItem(this, secondaryIndex, Llayout);
                        break;
                    }
                    case 4:{
                        changeOptionsMenu.moveItem(this, secondaryIndex, Llayout);
                        break;
                    }
					case 5: {
                        changeOptionsMenu.hideApp(this, Llayout);
                        break;
                    }
					case 6: {
                        startActivity(changeOptionsMenu.openAppSettings(this, Llayout, launchIntent));
                        break;
                    }
                    case 8: {
                        changeOptionsMenu.menuOpen(this, index, islaunchable, launchIntent, appname, Llayout);
                        break;
                    }
                    case 12:{
                        changeOptionsMenu.toggleGoogleIcons(this, Llayout);
                        break;
                    }
                    case 13:{
                        changeOptionsMenu.mirrorUI(this, Llayout);
                        break;
                    }
                    case 14:{
                        changeOptionsMenu.colorSelect(this, Llayout, secondaryIndex);
                        break;
                    }

				}
			}
		}
	}


	public void onOptions(final int index, final boolean islaunchable, final String launchIntent, final String appname){
            //first, move the item highlight
            listmove(index, false);
            //load the layout and make sure nothing is in it.
        changeOptionsMenu.menuOpen(this, index, islaunchable, launchIntent, appname, (LinearLayout) findViewById(R.id.optionslist));
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
                    if (optionsMenu){
                        onEnter(0, 0, false, ".", ".");
                    }
                    else {
                        onOptions(index, isLaunchable, launchIntent, appname);
                    }
                }
                else {
                    onEnter(index, secondaryIndex, isLaunchable, launchIntent, appname);
                }
            }
        });

        btn.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (optionsMenu){
                    onEnter(0, 0, false, ".", ".");
                }
                onOptions(index, isLaunchable, launchIntent, appname);
                return true;
            }
        });

        //return the view value
        return child;
    }

}

//class runRun extends AsyncTask<Void, Void, Void> {

//    @Override
//    protected Void doInBackground(Void... params) {return null;}
//}
