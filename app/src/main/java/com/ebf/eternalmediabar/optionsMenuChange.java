package com.ebf.eternalmediabar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;

import java.util.Collections;
import java.util.Comparator;


//This class is intended to manage a large number of functions related to menu interactions,
public class optionsMenuChange {

    //////////////////////////////////////////////////
    /////////////////Open the menu////////////////////
    //////////////////////////////////////////////////
    public void menuOpen(final EternalMediaBar eternalMediaBar, final boolean isLaunchable, final String launchIntent, final String appName){
        //set the variables for the menu
        eternalMediaBar.optionsMenu = true;
        eternalMediaBar.optionVitem = 1;
        //load the layout and make sure nothing is in it.
        ScrollView sLayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
        ((LinearLayout)eternalMediaBar.findViewById(R.id.optionslist)).removeAllViews();
        //animate the menu opening
        TranslateAnimation anim;
        if (!eternalMediaBar.savedData.mirrorMode) {
            //reset the position
            sLayout.setX(eternalMediaBar.getResources().getDisplayMetrics().widthPixels);
            anim = new TranslateAnimation(0, -(145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        else{
            //reset the position
            sLayout.setX(-145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f);
            anim = new TranslateAnimation(0, (145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        anim.setDuration(200);
        anim.setInterpolator(new LinearInterpolator());
        anim.setFillEnabled(false);
        sLayout.setAnimation(anim);
        //now move the menu itself
        sLayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScrollView sLayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
                // clear animation to prevent flicker
                sLayout.clearAnimation();
                //manually set position of menu
                if (!eternalMediaBar.savedData.mirrorMode) {
                    sLayout.setX(eternalMediaBar.getResources().getDisplayMetrics().widthPixels - (145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f));
                } else {
                    sLayout.setX(0);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        //if the program can be launched open the app options menu
        if (isLaunchable) {
            loadAppOptionsMenu(eternalMediaBar, launchIntent, appName);
            eternalMediaBar.optionVitem=1;
        }
        //otherwise load the normal options menu
        else{
            loadMainOptionsItems(eternalMediaBar);
            eternalMediaBar.optionVitem=1;
        }


        //close settings menu, we put this here since it's on the menu without exception.
        ((LinearLayout)eternalMediaBar.findViewById(R.id.optionslist)).addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Exit Options", 0, 0, false, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    /////////////////Close the menu///////////////////
    //////////////////////////////////////////////////
    public void menuClose(final EternalMediaBar eternalMediaBar) {
        //load the layouts
        ScrollView sLayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
        //empty the one that has content
        ((LinearLayout)eternalMediaBar.findViewById(R.id.optionslist)).removeAllViews();
        //set the variables in the main activity
        eternalMediaBar.optionsMenu = false;
        eternalMediaBar.optionVitem=1;
        //animate menu closing
        TranslateAnimation anim;
        if (!eternalMediaBar.savedData.mirrorMode) {
            anim = new TranslateAnimation(0, (145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        else{
            anim = new TranslateAnimation(0, -(145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        anim.setDuration(200);
        anim.setInterpolator(new LinearInterpolator());
        anim.setFillEnabled(false);
        sLayout.setAnimation(anim);
        //now move the menu itself
        sLayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                ScrollView sLayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
                // clear animation to prevent flicker
                sLayout.clearAnimation();
                //manually set position of menu off screen
                if (!eternalMediaBar.savedData.mirrorMode) {
                    sLayout.setX(eternalMediaBar.getResources().getDisplayMetrics().widthPixels);
                }
                else{
                    sLayout.setX(-145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        //save any changes and reload the view
        eternalMediaBar.savedData.writeXML(eternalMediaBar.savedData, eternalMediaBar);
        eternalMediaBar.loadListView();
    }


    //////////////////////////////////////////////////
    /////Load the settings menu for a selected app////
    //////////////////////////////////////////////////
    public void loadAppOptionsMenu(EternalMediaBar eternalMediaBar, String launchIntent, String appName){
        eternalMediaBar.optionVitem = 1;
        LinearLayout lLayout = (LinearLayout)eternalMediaBar.findViewById(R.id.optionslist);

        //add the app that's selected so the user knows for sure what they are messing with.
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, appName, -1, 0, false, launchIntent, ""));


        //add all the extra options

        //copy the item to another category
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Copy to...", 2, 0, false, launchIntent, appName));

        //move the item to another category
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Move to...", 3, 0, false, launchIntent, appName));

        //first option is to remove an item from the list.
        //in RC2 this will be modified to support hiding the icon even when it's only in one menu
        int i=0;
        for (int ii=0; ii< eternalMediaBar.savedData.categories.size();){
            for (int iii=0; iii< eternalMediaBar.savedData.categories.get(ii).appList.size();){
                if (eternalMediaBar.savedData.categories.get(ii).appList.get(iii).name.equals(eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList.get(eternalMediaBar.vItem).name)){
                    i++;
                }
                iii++;
            }
            ii++;
        }
        if (i>1) {
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Remove From This List", 6, 0, false, launchIntent, "4"));
        }

        //open the app's settings
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Application Settings", 7, 0, false, launchIntent, appName));

        //open the organization menu
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Reorganize This Category", 11, 0, false, launchIntent, appName));

    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public void loadMainOptionsItems(EternalMediaBar eternalMediaBar){
        //add the item for changing whether or not to use Google icons.
        eternalMediaBar.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)eternalMediaBar.findViewById(R.id.optionslist);
        if (eternalMediaBar.savedData.useGoogleIcons){
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Don't use Google Icons", 8, 0, false, ".", "."));
        }
        else{
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Use Google Icons", 8, 0, false, ".", "."));
        }
        if (eternalMediaBar.savedData.dimLists){
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Don't Dim List Backgrounds", 13, 0, false, ".", "."));
        }
        else{
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Dim List Backgrounds", 13, 0, false, ".", "."));
        }
        //add the item for mirroring the UI
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Mirror Layout", 9, 0, false, ".", "."));

        //add the item for changing the font color
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Change Font Color", 10, 0, false, ".", "Font"));
        //lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Change Icon Color", 10, 0, false, ".", "Icon"));
        //lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Change Menu Color", 10, 0, false, ".", "Menu"));
    }


    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public void createCopyList(EternalMediaBar eternalMediaBar, String launchIntent, String appName){
        eternalMediaBar.optionVitem=0;
        LinearLayout lLayout = (LinearLayout)eternalMediaBar.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, appName, -1, 0, false, launchIntent, ""));

        for (int i=0; i < eternalMediaBar.savedData.categories.size()-1; ) {
            if (i != eternalMediaBar.hItem) {
                lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Copy to " + eternalMediaBar.hli.get(i).label, 4, i, false, ".", "3"));
            }
            i++;
        }
        //return to first settings menu
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Go Back", 1, 1, true, launchIntent, appName));
        //close settings menu
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Exit Options", 0, 0, false, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void createMoveList(EternalMediaBar eternalMediaBar, String launchIntent, String appName){
        eternalMediaBar.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)eternalMediaBar.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, appName, -1, 0, false, launchIntent, ""));

        for (int i=0; i < eternalMediaBar.savedData.categories.size()-1; ) {
            if (i != eternalMediaBar.hItem) {
                lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Move to " + eternalMediaBar.hli.get(i).label, 5, i, false, ".", ""));
            }
            i++;
        }
        // !!! ENABLE AFTER FIXED !!!
                        /*/Automatically get the category for this item from google play
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (cm.getActiveNetworkInfo() != null) {
                            //if there is Wifi, go ahead and try.
                            if (cm.getActiveNetworkInfo() == cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)) {
                                //child = createMenuEntry(R.layout.options_item, "Auto Move", null, 9, i - 1, false, launchIntent, appName);
                                //lLayout.addView(child);
                            }
                            //if there is no wifi but there is still internet
                            else{
                                //child = createMenuEntry(R.layout.options_item, "Auto Move", svgLoad(R.drawable.blank), 10, i - 1, false, launchIntent, appName);
                               //lLayout.addView(child);
                            }
                        }/*/
        //return to first settings menu
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Go Back", 1, 1, true, launchIntent, appName));
        //close settings menu
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Exit Options", 0, 0, false, launchIntent, appName));
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public void listOrganizeSelect(EternalMediaBar eternalMediaBar, int secondaryIndex, String launchIntent, String appName){

        //we have to reload this menu every time we change it because we're using a radio button system, so we might as well make use of that by changing the variable from its own function.
        if(secondaryIndex!=0){
            eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[2] = secondaryIndex;
        }
        //add the item for changing the organization method
        eternalMediaBar.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)eternalMediaBar.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        if (eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[2]==1){
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Alphabetically", 11, 1, false, ".radioCheck", appName));
        }
        else{
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Alphabetically", 11, 1, false, ".radioUnCheck", appName));
        }
        if (eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[2]==2){
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Reverse Alphabetically", 11, 2, false, ".radioCheck", appName));
        }
        else{
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Reverse Alphabetically", 11, 2, false, ".radioUnCheck", appName));
        }
        if (eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[2]==3){
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "No Organization", 11, 3, false, ".radioCheck", appName));
        }
        else{
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "No Organization", 11, 3, false, ".radioUnCheck", appName));
        }

        lLayout.addView(View.inflate(eternalMediaBar.getApplicationContext(), R.layout.list_item, null));

        if (eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[1] ==0) {
            //add the item for only applying this once
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Apply just this once", 12, -1, false, ".radioCheck", appName));
            //add the item for always applying this
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Always use this method for this category", 12, 1, false, ".radioUnCheck", appName));
        }
        else{
            //add the item for only applying this once
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Apply just this once", 12, -1, false, ".radioUnCheck", appName));
            //add the item for always applying this
            lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.list_item, "Always use this method for this category", 12, 1, false, ".radioCheck", appName));

        }
    }



    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Functionality///////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////


    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public void copyItem(EternalMediaBar eternalMediaBar, int secondaryIndex){
        //create a copy of the selected item in another list
        eternalMediaBar.savedData.categories.get(secondaryIndex).appList.add(eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList.get(eternalMediaBar.vItem));
        menuClose(eternalMediaBar);
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void moveItem(EternalMediaBar eternalMediaBar, int secondaryIndex){
        eternalMediaBar.savedData.categories.get(secondaryIndex).appList.add(eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList.get(eternalMediaBar.vItem));
        eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList.remove(eternalMediaBar.vItem);
        menuClose(eternalMediaBar);
        //make sure that if the new apps list disappears, we aren't on it.
        if (eternalMediaBar.hItem == (eternalMediaBar.savedData.categories.size()-1) && eternalMediaBar.savedData.categories.get(eternalMediaBar.savedData.categories.size()-1).appList.size()==0){
            eternalMediaBar.listMove(0, true);
        }
        else{
            eternalMediaBar.loadListView();
        }
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void hideApp(EternalMediaBar eternalMediaBar){
        eternalMediaBar.savedData.hiddenApps.add(eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList.get(eternalMediaBar.vItem));
        eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList.remove(eternalMediaBar.vItem);
        menuClose(eternalMediaBar);
    }


    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public Intent openAppSettings(EternalMediaBar eternalMediaBar, String launchIntent){

        //open application settings
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(launchIntent)));
        menuClose(eternalMediaBar);
        return intent;
    }


    //////////////////////////////////////////////////
    ////////////////////Mirror UI/////////////////////
    //////////////////////////////////////////////////
    public void mirrorUI(EternalMediaBar eternalMediaBar){
        if (eternalMediaBar.savedData.mirrorMode){
            eternalMediaBar.savedData.mirrorMode=false;
        }
        else{
            eternalMediaBar.savedData.mirrorMode = true;
        }
        menuClose(eternalMediaBar);
    }


    //////////////////////////////////////////////////
    ///////////Enable/Disable Google Icons////////////
    //////////////////////////////////////////////////
    public void toggleGoogleIcons(EternalMediaBar eternalMediaBar){
        if (eternalMediaBar.savedData.useGoogleIcons){
            eternalMediaBar.savedData.useGoogleIcons=false;
        }
        else{
            eternalMediaBar.savedData.useGoogleIcons = true;
        }
        menuClose(eternalMediaBar);
    }


    //////////////////////////////////////////////////
    ////////////Enable/Disable Dim Lists//////////////
    //////////////////////////////////////////////////
    public void toggleDimLists(EternalMediaBar eternalMediaBar){
        if (eternalMediaBar.savedData.dimLists){
            eternalMediaBar.savedData.dimLists=false;
        }
        else{
            eternalMediaBar.savedData.dimLists = true;
        }
        menuClose(eternalMediaBar);
    }

    //////////////////////////////////////////////////
    //////////////////Organize List///////////////////
    //////////////////////////////////////////////////
    public void organizeList(EternalMediaBar eternalMediaBar, int secondaryIndex){
        //we want to define the organization method when we load this menu.
        if (secondaryIndex ==1){
            eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[1] =1;
        }
        else if (secondaryIndex ==-1){
            eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[1] =0;
        }

        switch(eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).organizeMode[2]){
            //no organization
            case 0:case 3:{
                break;
            }
            //alphabetical
            case 1:{
                Collections.sort(eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList, new Comparator<appDetail>() {
                    @Override
                    public int compare(appDetail lhs, appDetail rhs) {
                        return lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                if (eternalMediaBar.optionsMenu) {
                    menuClose(eternalMediaBar);
                }
                break;
            }
            //reverse alphabetical
            case 2:{
                Collections.sort(eternalMediaBar.savedData.categories.get(eternalMediaBar.hItem).appList, new Comparator<appDetail>() {
                    @Override
                    public int compare(appDetail lhs, appDetail rhs) {
                        return -lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                if (eternalMediaBar.optionsMenu) {
                    menuClose(eternalMediaBar);
                }
                break;
            }
            //Most used
            case 4:{

            }
        }
    }







    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Color Selector//////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    public void colorSelect(final EternalMediaBar eternalMediaBar, final String colorName, int secondaryIndex){
        //Instead of making a new case, it's easier to compensate for the cancel button by modifying this call
        if (secondaryIndex!=0){
            switch (colorName){
                case "Font":{eternalMediaBar.savedData.fontCol = secondaryIndex;}
                case "Icon":{eternalMediaBar.savedData.iconCol = secondaryIndex;}
                case "Menu":{eternalMediaBar.savedData.menuCol = secondaryIndex;}
            }
            menuClose(eternalMediaBar);
            return;
        }
        LinearLayout lLayout = (LinearLayout) eternalMediaBar.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        //load the header that contains the current color
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, "Choose "+colorName+" Color", -1, 0, false, ".colHeader", "."));
        //create the inflater for the seeker bars
        View child = eternalMediaBar.getLayoutInflater().inflate(R.layout.color_select, null);
        //get the red seeker bar, then set it's progress
        SeekBar seekerRed = (SeekBar) child.findViewById(R.id.redSeek);
        SeekBar seekerGreen = (SeekBar) child.findViewById(R.id.greenSeek);
        SeekBar seekerBlue = (SeekBar) child.findViewById(R.id.blueSeek);
        SeekBar seekerAlpha = (SeekBar) child.findViewById(R.id.alphaSeek);
        EditText hexText = (EditText) child.findViewById(R.id.hexText);
        switch (colorName){
            case "Font":{
                seekerRed.setProgress(Color.red(eternalMediaBar.savedData.fontCol));
                seekerGreen.setProgress(Color.green(eternalMediaBar.savedData.fontCol));
                seekerBlue.setProgress(Color.blue(eternalMediaBar.savedData.fontCol));
                seekerAlpha.setProgress(Color.alpha(eternalMediaBar.savedData.fontCol) - 25);
                hexText.setText("#"+Integer.toHexString(eternalMediaBar.savedData.fontCol));}
            case "Icon":{
                seekerRed.setProgress(Color.red(eternalMediaBar.savedData.iconCol));
                seekerGreen.setProgress(Color.green(eternalMediaBar.savedData.iconCol));
                seekerBlue.setProgress(Color.blue(eternalMediaBar.savedData.iconCol));
                seekerAlpha.setProgress(Color.alpha(eternalMediaBar.savedData.iconCol) - 25);
                hexText.setText("#"+Integer.toHexString(eternalMediaBar.savedData.iconCol));}
            case "Menu":{
                seekerRed.setProgress(Color.red(eternalMediaBar.savedData.menuCol));
                seekerGreen.setProgress(Color.green(eternalMediaBar.savedData.menuCol));
                seekerBlue.setProgress(Color.blue(eternalMediaBar.savedData.menuCol));
                seekerAlpha.setProgress(Color.alpha(eternalMediaBar.savedData.menuCol) - 25);
                hexText.setText("#"+Integer.toHexString(eternalMediaBar.savedData.menuCol));}
        }



        //lastly change the listener for the Red seek bar
        seekerRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //when the bar is moved, change the value of the font color, then update the image accordingly.
                switch (colorName) {
                    case "Font":{
                        eternalMediaBar.savedData.fontCol = Color.argb(Color.alpha(eternalMediaBar.savedData.fontCol), progress, Color.green(eternalMediaBar.savedData.fontCol), Color.blue(eternalMediaBar.savedData.fontCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.fontCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.fontCol));}
                    case "Icon":{
                        eternalMediaBar.savedData.iconCol = Color.argb(Color.alpha(eternalMediaBar.savedData.iconCol), progress, Color.green(eternalMediaBar.savedData.iconCol), Color.blue(eternalMediaBar.savedData.iconCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.iconCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.iconCol));}
                    case "Menu":{
                        eternalMediaBar.savedData.menuCol = Color.argb(Color.alpha(eternalMediaBar.savedData.menuCol), progress, Color.green(eternalMediaBar.savedData.menuCol), Color.blue(eternalMediaBar.savedData.menuCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.menuCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.menuCol));}
                };
            }
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it again for green
        seekerGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the bar is moved, change the value of the font color, then update the image accordingly.
                switch (colorName) {
                    case "Font": {
                        eternalMediaBar.savedData.fontCol = Color.argb(Color.alpha(eternalMediaBar.savedData.fontCol), Color.red(eternalMediaBar.savedData.fontCol), progress, Color.blue(eternalMediaBar.savedData.fontCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.fontCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.fontCol));}
                    case "Icon":{
                        eternalMediaBar.savedData.iconCol = Color.argb(Color.alpha(eternalMediaBar.savedData.iconCol), Color.red(eternalMediaBar.savedData.iconCol), progress, Color.blue(eternalMediaBar.savedData.iconCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.iconCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.iconCol));}
                    case "Menu":{
                        eternalMediaBar.savedData.menuCol = Color.argb(Color.alpha(eternalMediaBar.savedData.menuCol), Color.red(eternalMediaBar.savedData.menuCol), progress, Color.blue(eternalMediaBar.savedData.menuCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.menuCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.menuCol));}
                }
            }
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for blue
        seekerBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the bar is moved, change the value of the font color, then update the image accordingly.
                switch (colorName) {
                    case "Font": {
                        eternalMediaBar.savedData.fontCol = Color.argb(Color.alpha(eternalMediaBar.savedData.fontCol), Color.red(eternalMediaBar.savedData.fontCol), Color.green(eternalMediaBar.savedData.fontCol), progress);
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.fontCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.fontCol));
                    }
                    case "Icon": {
                        eternalMediaBar.savedData.iconCol = Color.argb(Color.alpha(eternalMediaBar.savedData.iconCol), Color.red(eternalMediaBar.savedData.iconCol), Color.green(eternalMediaBar.savedData.iconCol), progress);
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.iconCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.iconCol));
                    }
                    case "Menu": {
                        eternalMediaBar.savedData.menuCol = Color.argb(Color.alpha(eternalMediaBar.savedData.menuCol), Color.red(eternalMediaBar.savedData.menuCol), Color.green(eternalMediaBar.savedData.menuCol), progress);
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.menuCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.menuCol));
                    }
                }
            }

            //these are useless, but we need them to exist
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Now we do it one more time for Alpha
        seekerAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the bar is moved, change the value of the font color, then update the image accordingly.
                switch (colorName) {
                    case "Font":{
                        eternalMediaBar.savedData.fontCol = Color.argb(progress+25, Color.red(eternalMediaBar.savedData.fontCol), Color.green(eternalMediaBar.savedData.fontCol), Color.blue(eternalMediaBar.savedData.fontCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.fontCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.fontCol));}
                    case "Icon":{
                        eternalMediaBar.savedData.iconCol = Color.argb(progress+25, Color.red(eternalMediaBar.savedData.iconCol), Color.green(eternalMediaBar.savedData.iconCol), Color.blue(eternalMediaBar.savedData.iconCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.iconCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.iconCol));}
                    case "Menu":{
                        eternalMediaBar.savedData.menuCol = Color.argb(progress+25, Color.red(eternalMediaBar.savedData.menuCol), Color.green(eternalMediaBar.savedData.menuCol), Color.blue(eternalMediaBar.savedData.menuCol));
                        ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.menuCol));
                        ((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(eternalMediaBar.savedData.menuCol));}
                }
            }
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        hexText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {
                    int col = Color.parseColor(((EditText) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).getText().toString());
                    ((SeekBar) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.redSeek)).setProgress(Color.red(col));
                    ((SeekBar) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.greenSeek)).setProgress(Color.green(col));
                    ((SeekBar) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.blueSeek)).setProgress(Color.blue(col));
                    ((SeekBar) (eternalMediaBar.findViewById(R.id.optionslist)).findViewById(R.id.alphaSeek)).setProgress(Color.alpha(col));
                }
                catch (Exception e){}
            }
        });



        //and finally add the view
        lLayout.addView(child);
        //add the item for save and quit
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Save and close", 0, 0, false, ".", "."));

        //add the item for cancel changes
        lLayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Close without saving", 10, eternalMediaBar.savedData.fontCol, false, ".", colorName));
    }

}
