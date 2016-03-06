package com.ebf.eternalmediabar;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class EternalMediaBar extends Activity {

    public PackageManager manager;
    public List<AppDetail> hli = new ArrayList<>();
    public settingsClass savedData = new settingsClass();

    public int hItem = 0;
    private boolean init = false;
    public boolean optionsMenu = false;
    public int vItem = 0;
    public int optionVitem =1;
    public boolean[] warningToggle;

    private optionsMenuChange changeOptionsMenu = new optionsMenuChange();



    //////////////////////////////////////////////////
    ////////////When the app first starts/////////////
    //////////////////////////////////////////////////
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the current layout value
        setContentView(R.layout.activity_eternal_media_bar);

        //run once
        if (!init) {
            if (savedData.vLists.size()<=1) {
                try {
                    //try load preferences
                    FileInputStream fs = openFileInput("data.xml");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    fs.close();
                    savedData = savedData.returnSettings(sb.toString());



                }
                catch (Exception e) {
                    e.printStackTrace();
                    try{
                        FileInputStream fs = new FileInputStream(Environment.getExternalStorageDirectory() + "/data.xml");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        reader.close();
                        fs.close();
                        savedData = savedData.returnSettings(sb.toString());
                    }
                    catch (Exception ee) {
                        //output to debug log just in case something went fully wrong
                        ee.printStackTrace();
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
                        savedData.hiddenApps = new ArrayList<>();
                        int[] tempInt = new int[]{0, 1, 1};
                        savedData.organizeMode = new ArrayList<>();
                        savedData.organizeMode.add(tempInt);
                        savedData.organizeMode.add(tempInt);
                        savedData.organizeMode.add(tempInt);
                        savedData.organizeMode.add(tempInt);
                        savedData.organizeMode.add(tempInt);
                        savedData.organizeMode.add(tempInt);
                        savedData.organizeMode.add(tempInt);
                    }
                }
            }
            //we dont use this, but due to glitches in earlier revisions, there may be things in here, when it should be empty.
            savedData.hiddenApps.clear();
            //load in the apps
            loadApps();
            //setup the warning variable
            warningToggle = new boolean[1];
            warningToggle[0] = false;

            //make sure this doesn't happen again
            init = true;

            //Lastly, activate the list move function to load the list view and attempt to highlight what menu we are on.
            listMove(0, true);
        }
    }

    //////////////////////////////////////////////////
    ///////this function is for requesting any////////
    ////////needed permissions in android 6+//////////
    //////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.M)
            public void getPerms(){
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
    }

    //////////////////////////////////////////////////
    ///////When the app comes back from being/////////
    ///////       in the background          /////////
    //////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        if (init){
            //load in the apps
            loadApps();
            //make sure vItem isn't out of bounds
            if (vItem >= savedData.vLists.get(hItem).size()){
                vItem = savedData.vLists.get(hItem).size();
            }

            //make sure that if the new apps list disappears, we aren't on it.
            if (hItem == (savedData.vLists.size()-1) && savedData.vLists.get(savedData.vLists.size()-1).size()==0){
                listMove(0, true);
            }
            //otherwise just load normally
            else{
                loadListView();
            }

        }
    }

    //////////////////////////////////////////////////
    ///////////Save a settingsClass to file///////////
    //////////////////////////////////////////////////
    public void saveFiles(){

        //save using the new save file format
        try{
            //create a file Output Stream, this lets us write to the internal memory
            FileOutputStream fileStream = openFileOutput("data.xml", Context.MODE_PRIVATE);
            fileStream.write(savedData.writeXML(savedData, this).getBytes());
            //write a string to the stream
            //close the stream to save some RAM.
            fileStream.close();

        //System.out.print(savedData.writeXML(savedData, this));
        /*try{
            FileWriter data = new FileWriter(Environment.getExternalStorageDirectory().getPath() +"/data.xml");
            data.write(savedData.writeXML(savedData, this));
            data.flush();
            data.close();*/
        }
        catch(Exception e){
            //first fail, ask for write permissions so it won't fail the next time
            //getPerms();
            //and print the stack just in case.
            e.printStackTrace();
        }
    }


    //////////////////////////////////////////////////
    //////////When a button or key is pressed/////////
    //////////////////////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("Key Pressed: ", "" + keyCode);
        switch (keyCode) {
            //case event for down
            case KeyEvent.KEYCODE_S: case KeyEvent.KEYCODE_DPAD_DOWN: case KeyEvent.KEYCODE_4: case KeyEvent.KEYCODE_NUMPAD_4: {
                listMove(vItem + 1, false);
                return true;
            }
            //case event for up
            case KeyEvent.KEYCODE_W: case KeyEvent.KEYCODE_DPAD_UP: case KeyEvent.KEYCODE_2: case KeyEvent.KEYCODE_NUMPAD_2:{
                listMove(vItem - 1, false);
                return true;
            }
            //case event for right
            case KeyEvent.KEYCODE_D: case KeyEvent.KEYCODE_DPAD_RIGHT: case KeyEvent.KEYCODE_6: case KeyEvent.KEYCODE_NUMPAD_6:{
                listMove(hItem + 1, true);
                return true;
            }
            //case event for left
            case KeyEvent.KEYCODE_A: case KeyEvent.KEYCODE_DPAD_LEFT: case KeyEvent.KEYCODE_8: case KeyEvent.KEYCODE_NUMPAD_8: {
                listMove(hItem - 1, true);
                return true;
            }
            //event for when enter/x/a is pressed
			case KeyEvent.KEYCODE_ENTER: case KeyEvent.KEYCODE_NUMPAD_ENTER: case KeyEvent.KEYCODE_DPAD_CENTER: case KeyEvent.KEYCODE_1: case KeyEvent.KEYCODE_5: case KeyEvent.KEYCODE_NUMPAD_5: case KeyEvent.KEYCODE_BUTTON_1: {
                if (!optionsMenu) {
                    //get the layout
                    LinearLayout vLayout = (LinearLayout)findViewById(R.id.apps_display);
                    //get the item in the layout and activate its button function
                    vLayout.getChildAt(vItem).findViewById(R.id.item_app_button).performClick();
                }
                else{
                    //get the layout
                    LinearLayout lLayout = (LinearLayout)findViewById(R.id.optionslist);
                    //get the item in the layout and activate its button function
                    lLayout.getChildAt(optionVitem).findViewById(R.id.item_app_button).performClick();
                }
				return true;
			}
            //event for when E/Y/Triangle is pressed
			case KeyEvent.KEYCODE_BUTTON_4: case KeyEvent.KEYCODE_E: case KeyEvent.KEYCODE_TAB: case KeyEvent.KEYCODE_0: case KeyEvent.KEYCODE_NUMPAD_0: {
                if (!optionsMenu) {
                    //get the layout
                    LinearLayout vLayout = (LinearLayout)findViewById(R.id.apps_display);
                    //get the item in the layout and activate its button function
                    vLayout.getChildAt(vItem).findViewById(R.id.item_app_button).performLongClick();
                }
                else{
                    changeOptionsMenu.menuClose(EternalMediaBar.this, (LinearLayout) findViewById(R.id.optionslist));
                }
				return true;
			}

            //case event for unused keys
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    //////////////////////////////////////////////////
    /////////////change selected item/////////////////
    //////////////////////////////////////////////////
    void listMove(int move, boolean isCategory){
        //function to move the highlight selection based on which menu you are on.
        if (!isCategory) {
            //if you are not on the options menu
            if (!optionsMenu) {
                LinearLayout vLayout = (LinearLayout) findViewById(R.id.apps_display);
                boolean proceed = true;
                //if you are trying to move too far down set proceed to false
                if (move < 0) {
                    proceed = false;
                }
                //if you are trying to move too far up set proceed to false
                else if (move > vLayout.getChildCount()-1) {
                    proceed = false;
                }

                if (proceed) {
                    //change the old item, if it exists
                    try {
                        //change the old font face
                        ((TextView) vLayout.getChildAt(vItem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                        //scale the icon back to normal
                        ImageView appIcon = (ImageView) vLayout.getChildAt(vItem).findViewById(R.id.item_app_icon);
                        appIcon.setScaleX(1f);
                        appIcon.setScaleY(1f);
                    }
                    catch(Exception e){}
                    //change vItem
                    vItem = move;
                    //change the font face
                    ((TextView) vLayout.getChildAt(vItem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                    //scale the icon larger
                    ImageView appIcon = (ImageView) vLayout.getChildAt(vItem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1.25f);
                    appIcon.setScaleY(1.25f);

                    //scroll to the new entry
                    vLayout.scrollTo(0, (int) vLayout.getChildAt(vItem).getX());

                }
            }
            //if you are on the options menu
            else {
                move -= vItem;
                move += optionVitem;
                LinearLayout vLayout = (LinearLayout) findViewById(R.id.optionslist);
                boolean proceed = true;
                //if you are trying to move too far down set proceed to false
                if (move < 0) {
                    proceed = false;
                }
                //if you are trying to move too far up set proceed to false
                else if (move > vLayout.getChildCount()-1) {
                    proceed = false;
                }

                if (proceed) {
                    //set the font face.
                    ((TextView) vLayout.getChildAt(optionVitem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                    //change OptionsVItem
                    optionVitem = move;
                    ((TextView) vLayout.getChildAt(optionVitem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                    //scroll to the new entry
                    vLayout.scrollTo(0, (int) vLayout.getChildAt(optionVitem).getX());
                }
            }
        }
        else{
            LinearLayout hLayout = (LinearLayout) findViewById(R.id.categories);
            boolean proceed = true;
            //if you are trying to move too far down set proceed to false
            if (move < 0) {
                proceed = false;
            }
            //if you are trying to move too far up set proceed to false
            else if (move >= hLayout.getChildCount()) {
                proceed = false;
            }

            if (proceed) {
                //change hItem
                hItem = move;
                //reload the list
                loadListView();
                try {
                    hLayout.scrollTo((int) hLayout.getChildAt(hItem).getY(), 0);
                }
                catch (Exception e){}
            }
        }
    }


    //////////////////////////////////////////////////
    ///////Figure out what is installed, or not///////
    //////////////////////////////////////////////////
    private void loadApps(){
        manager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent , 0);

        savedData.vLists.get(savedData.vLists.size()-1).clear();

        //create a list of bools for checking what system apps are present.
        Boolean[] sysapps = new Boolean[]{false};
        //try to remove any apps that have invalid launch intents, unless it's marked as persistent.
        for (int i=0; i<savedData.vLists.size();){
            for (int ii=0; ii<savedData.vLists.get(i).size();){
                //iterate through the lists, then check if it's persistent.
                if (!savedData.vLists.get(i).get(ii).isPersistent) {
                    //try to check if the launch intent is valid, if it's not, or the check fails, remove the app's entry.
                    try {
                        if (manager.queryIntentActivities(manager.getLaunchIntentForPackage(savedData.vLists.get(i).get(ii).name), PackageManager.MATCH_DEFAULT_ONLY).size() < 1) {
                            savedData.vLists.get(i).remove(ii);
                        }
                        //if the app was valid, iterate through the available activities to find the app's entry position, and remove it..
                        else{
                            for (int iii=0; iii<availableActivities.size();){
                                if(availableActivities.get(iii).activityInfo.packageName.equals(savedData.vLists.get(i).get(ii).name)){
                                    availableActivities.remove(iii);
                                    //now set the index of iii to break the loop, since we already found what we were looking for.
                                    iii=availableActivities.size();
                                }
                                iii++;
                            }
                        }
                    } catch (Exception e) {
                        savedData.vLists.get(i).remove(ii);
                    }
                }
                //do a check for if the system apps are present and modify the bool as necessary.
                else if(savedData.vLists.get(i).get(ii).name.equals(".options")){
                    sysapps[0] = true;
                }
                ii++;
            }
            i++;
        }

        //now check the list of bools and add any missing system apps.
        if (!sysapps[0]){
            AppDetail eternalSettings = new AppDetail();
            eternalSettings.isPersistent = true;
            eternalSettings.label = "Eternal Media Bar - Settings";
            eternalSettings.name = ".options";
            savedData.vLists.get(5).add(eternalSettings);
        }

        if (availableActivities.size()>0) {
            for (ResolveInfo ri : availableActivities) {
                AppDetail appRI = new AppDetail();
                appRI.label = ri.loadLabel(manager);
                appRI.name = ri.activityInfo.packageName;
                appRI.isPersistent = false;
                appRI.icon = null;
                savedData.vLists.get(savedData.vLists.size()-1).add(appRI);
            }
            availableActivities.clear();
        }
    }



    //////////////////////////////////////////////////
    ///////////Return a drawable from a png///////////
    //////////////////////////////////////////////////
    Drawable svgLoad(int imageToLoad){
        //imageView.setImageDrawable(svg.createPictureDrawable());
        return ContextCompat.getDrawable(this, imageToLoad);
    }




    //////////////////////////////////////////////////
    ///////Function to draw all the information///////
    //////////////////////////////////////////////////
    public void loadListView(){

        if (savedData.mirrorMode){
            setContentView(R.layout.activity_eternal_media_bar_mirror);
        }
        else{
            setContentView(R.layout.activity_eternal_media_bar);
        }

        //////////////////////
        //Draw the Categories
        //////////////////////
        hli.clear();
        //setup the horizontal bar, there's a pre-defined setting to ease the ability for custom options later down the road.most importantly it simplifies the code.

        for(int i=0; i<savedData.vLists.size();){
            AppDetail hMenuItem = new AppDetail();
            hMenuItem.name = "hItem";
            hMenuItem.isPersistent = true;
            hMenuItem.label = savedData.categoryNames.get(i);
            hMenuItem.icon = new imgLoader(this, savedData.categoryIcons.get(i), manager, savedData.useGoogleIcons).doInBackground();
            hli.add(hMenuItem);
            i++;
        }



        manager = getPackageManager();
        LinearLayout layout = (LinearLayout)findViewById(R.id.categories);
        //empty the list
        layout.removeAllViews();
        //loop to add all entries of hli to the list
        for (int ii=0; (ii)<hli.size();) {
            if(savedData.vLists.get(ii).size()>0) {
                layout.addView(createMenuEntry(R.layout.category_item, hli.get(ii).label, hli.get(ii).icon, ii, 0, false, "hItem", "hItem"));
            }
        ii++;
        }
        //change the display for the appropriate icon
        //change the font type
        try {
            ((TextView) layout.getChildAt(hItem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            //modify the icon to go be larger, and go under the text so it appears even bigger and doesn't scale out of the view.
            ImageView appIcon = (ImageView) layout.getChildAt(hItem).findViewById(R.id.item_app_icon);
            appIcon.setScaleX(1.25f);
            appIcon.setScaleY(1.25f);
            appIcon.setY(3 * getResources().getDisplayMetrics().density + 0.5f);
            //scroll to the new entry
            layout.scrollTo(0, (int) layout.getChildAt(hItem).getY());
        }
        catch(Exception e){}
        listMove(0, false);


        //copy category method but with the vList
        LinearLayout vLayout = (LinearLayout)findViewById(R.id.apps_display);
        for(int i=0; i<vLayout.getChildCount();){
            vLayout.getChildAt(i).invalidate();
            i++;
        }
        vLayout.removeAllViews();


        if (savedData.organizeMode.get(hItem)[1] == 1 && savedData.vLists.get(hItem).size() >1) {
            changeOptionsMenu.organizeList(this, null, 0);
        }


        for (int ii=0; ii< savedData.vLists.get(hItem).size();) {
            vLayout.addView(createMenuEntry(R.layout.list_item, savedData.vLists.get(hItem).get(ii).label, null, ii, 0, true, savedData.vLists.get(hItem).get(ii).name, (String) savedData.vLists.get(hItem).get(ii).label));
            ii++;
        }

        //make sure the vList item is selected
        listMove(0, false);
    }

    //////////////////////////////////////////////////
    /////////Function for creating list items/////////
    //////////////////////////////////////////////////
    public View createMenuEntry(int inflater, CharSequence text, @Nullable Drawable icon, final int index, final int secondaryIndex, final Boolean isLaunchable, final String launchIntent, final String appName){
        //initialize the views we know will be there
        View child = getLayoutInflater().inflate(inflater, null);
        TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
        appLabel.setText(text);
        appLabel.setTextColor(savedData.fontCol);
        //if the launch intent exists try and add an icon from it
        if (launchIntent.length()>1 && icon ==null) {
            //if it's an options menu item the image view will fail and skip this
            ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);
            //attempt to add the icon from the launchIntent
            //null icon or a new blank one will be blank, invalid icons will show up as exclamations
            try {
                appIcon.setImageDrawable(manager.getApplicationIcon(launchIntent));
            } catch (Exception e) {
                if (launchIntent.equals(".options")){
                    appIcon.setImageDrawable(svgLoad(R.drawable.sub_settings_144px));
                }
                else {
                    appIcon.setImageDrawable(svgLoad(R.drawable.error_144px));
                }
            }
        }
        else{
            try {
                //try to load pre-designated icon, only for bundled icons.
                ImageView appIcon = (ImageView) child.findViewById(R.id.item_app_icon);

                //we'll have to offload this somewhere else for figuring out icons.
                appIcon.setImageDrawable(icon);
            } catch (Exception e) {}
        }

        //setup the onclick listener and button
        Button btn = (Button) child.findViewById(R.id.item_app_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appName.equals("hItem")){
                    listMove(index, true);
                }
                else if (launchIntent.equals(".options")){
                    if (optionsMenu){
                        changeOptionsMenu.menuClose(EternalMediaBar.this, (LinearLayout) findViewById(R.id.optionslist));
                    }
                    else {

                        listMove(index, false);
                        //load the layout and make sure nothing is in it.
                        changeOptionsMenu.menuOpen(EternalMediaBar.this, false, launchIntent, appName, (LinearLayout) findViewById(R.id.optionslist));
                    }
                }
                else {
                    if (isLaunchable) {
                        if (secondaryIndex==1){
                            changeOptionsMenu.menuOpen(EternalMediaBar.this, true, launchIntent, appName, (LinearLayout) findViewById(R.id.optionslist));
                        }
                        else {
                            EternalMediaBar.this.startActivity(manager.getLaunchIntentForPackage(launchIntent));
                        }
                    }
                    else {
                        //initialize the variables for the list ahead of time
                        LinearLayout lLayout = (LinearLayout) findViewById(R.id.optionslist);
                        lLayout.removeAllViews();
                        //choose which list to make dependant on the values given for the call.
                        switch (index) {
                            case -1:{/*/ Null Case /*/}
                            case 0:{changeOptionsMenu.menuClose(EternalMediaBar.this, lLayout); break;}
                            case 1:{changeOptionsMenu.menuOpen(EternalMediaBar.this, false, launchIntent, appName, lLayout);break;}
                            case 2:{changeOptionsMenu.createCopyList(EternalMediaBar.this, lLayout, launchIntent, appName);break;}
                            case 3:{changeOptionsMenu.createMoveList(EternalMediaBar.this, lLayout, launchIntent, appName);break;}
                            case 4:{changeOptionsMenu.copyItem(EternalMediaBar.this, secondaryIndex, lLayout);break;}
                            case 5:{changeOptionsMenu.moveItem(EternalMediaBar.this, secondaryIndex, lLayout);break;}
                            case 6:{changeOptionsMenu.hideApp(EternalMediaBar.this, lLayout);break;}
                            case 7:{startActivity(changeOptionsMenu.openAppSettings(EternalMediaBar.this, lLayout, launchIntent));break;}
                            case 8:{changeOptionsMenu.toggleGoogleIcons(EternalMediaBar.this, lLayout);break;}
                            case 9:{changeOptionsMenu.mirrorUI(EternalMediaBar.this, lLayout);break;}
                            case 10:{changeOptionsMenu.colorSelect(EternalMediaBar.this, lLayout, secondaryIndex);break;}
                            case 11:{changeOptionsMenu.listOrganizeSelect(EternalMediaBar.this, lLayout, secondaryIndex, launchIntent, appName);break;}
                            case 12:{changeOptionsMenu.organizeList(EternalMediaBar.this, lLayout, secondaryIndex);break;}

                        }
                    }
                }
            }
        });

        btn.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
            if (optionsMenu){
                changeOptionsMenu.menuClose(EternalMediaBar.this, (LinearLayout) findViewById(R.id.optionslist));
            }
            else if (appName.equals("hItem")){
                listMove(index, true);
            }
            else {
                listMove(index, false);
            }
            changeOptionsMenu.menuOpen(EternalMediaBar.this, isLaunchable, launchIntent, appName, (LinearLayout) findViewById(R.id.optionslist));
            return true;
            }
        });

        //return the view value
        return child;
    }

}
