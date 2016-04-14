package com.ebf.eternalmediabar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;

import com.ebf.eternalVariables.appDetail;

import java.util.Collections;
import java.util.Comparator;


//This class is intended to manage a large number of functions related to menu interactions,
public class optionsMenuChange {

    //////////////////////////////////////////////////
    /////////////////Open the menu////////////////////
    //////////////////////////////////////////////////
    public void menuOpen(final boolean isLaunchable, final String launchIntent, final String appName){
        //set the variables for the menu
        EternalMediaBar.activity.optionsMenu = true;
        EternalMediaBar.activity.optionVitem = 1;
        //load the layout and make sure nothing is in it.
        ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist)).removeAllViews();
        //animate the menu opening
        TranslateAnimation anim;
        if (!EternalMediaBar.activity.savedData.mirrorMode) {
            //reset the position
            sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels);
            anim = new TranslateAnimation(0, -(145 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        else{
            //reset the position
            sLayout.setX(-145 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f);
            anim = new TranslateAnimation(0, (145 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
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
                ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
                // clear animation to prevent flicker
                sLayout.clearAnimation();
                //manually set position of menu
                if (!EternalMediaBar.activity.savedData.mirrorMode) {
                    sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels - (145 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f));
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
            loadAppOptionsMenu(launchIntent, appName);
            EternalMediaBar.activity.optionVitem=1;
        }
        //otherwise load the normal options menu
        else{
            loadMainOptionsItems();
            EternalMediaBar.activity.optionVitem=1;
        }


        //close settings menu, we put this here since it's on the menu without exception.
        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist)).addView(new listItemLayout().optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    /////////////////Close the menu///////////////////
    //////////////////////////////////////////////////
    public void menuClose() {
        //load the layouts
        ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
        //empty the one that has content
        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist)).removeAllViews();
        //set the variables in the main activity
        EternalMediaBar.activity.optionsMenu = false;
        EternalMediaBar.activity.optionVitem=1;
        //animate menu closing
        TranslateAnimation anim;
        if (!EternalMediaBar.activity.savedData.mirrorMode) {
            anim = new TranslateAnimation(0, (145 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        else{
            anim = new TranslateAnimation(0, -(145 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
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
                ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
                // clear animation to prevent flicker
                sLayout.clearAnimation();
                //manually set position of menu off screen
                if (!EternalMediaBar.activity.savedData.mirrorMode) {
                    sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels);
                } else {
                    sLayout.setX(-145 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        //save any changes and reload the view
        EternalMediaBar.activity.savedData.writeXML(EternalMediaBar.activity);
        EternalMediaBar.activity.loadListView();
    }


    //////////////////////////////////////////////////
    /////Load the settings menu for a selected app////
    //////////////////////////////////////////////////
    public void loadAppOptionsMenu(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem = 1;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        //add the app that's selected so the user knows for sure what they are messing with.
        lLayout.addView(new listItemLayout().optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));
        //copy the item to another category
        lLayout.addView(new listItemLayout().optionsListItemView("Copy to...", 2, 0, launchIntent, appName));
        //move the item to another category
        lLayout.addView(new listItemLayout().optionsListItemView("Move to...", 3, 0, launchIntent, appName));
        //first option is to remove an item from the list.
        //later this will be modified to support hiding the icon when it's only in one menu
        int i=0;
        for (int ii=0; ii< EternalMediaBar.activity.savedData.categories.size();){
            for (int iii=0; iii< EternalMediaBar.activity.savedData.categories.get(ii).appList.size();){
                if (EternalMediaBar.activity.savedData.categories.get(ii).appList.get(iii).name.equals(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList.get(EternalMediaBar.activity.vItem).name)){
                    i++;
                }
                iii++;
            }
            ii++;
        }
        if (i>1) {
            lLayout.addView(new listItemLayout().optionsListItemView("Remove From This List", 6, 0, launchIntent, "4"));
        }
        else{
            //hide the icon
        }
        // !!! ENABLE AFTER FIXED !!! //Auto Organize
                        /*/Automatically get the category for this item from google play
                        ConnectivityManager cm = (ConnectivityManager) EternalMediaBar.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                            //if there is Wifi, go ahead and try.
                            if (cm.getActiveNetworkInfo() != null &&cm.getActiveNetworkInfo() == cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)) {
                                //child = createMenuEntry(R.layout.options_item, "Auto Move", null, 9, i - 1, false, launchIntent, appName);
                                //lLayout.addView(child);
                            }
                            //if there is no wifi but there is still internet
                            else{
                                //child = createMenuEntry(R.layout.options_item, "Auto Move", svgLoad(R.drawable.blank), 10, i - 1, false, launchIntent, appName);
                               //lLayout.addView(child);
                            }/*/

        //open the app's settings
        lLayout.addView(new listItemLayout().optionsListItemView("Application Settings", 7, 0, launchIntent, appName));

        //open the organization menu
        lLayout.addView(new listItemLayout().optionsListItemView("Reorganize This Category", 11, 0, launchIntent, appName));

    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public void loadMainOptionsItems(){
        //add the item for changing whether or not to use Google icons.
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.addView(new listItemLayout().optionsListItemView("Choose Theme", 8, 0, ".", "."));
        if (EternalMediaBar.activity.savedData.dimLists){
            lLayout.addView(new listItemLayout().optionsListItemView("Don't Dim List Backgrounds", 13, 0, ".", "."));
        }
        else{
            lLayout.addView(new listItemLayout().optionsListItemView("Dim List Backgrounds", 13, 0, ".", "."));
        }
        //add the item for mirroring the UI
        lLayout.addView(new listItemLayout().optionsListItemView("Mirror Layout", 9, 0, ".", "."));

        //add the item for changing the font color
        lLayout.addView(new listItemLayout().optionsListItemView("Change Font Color", 10, 0, ".", "Font"));
        lLayout.addView(new listItemLayout().optionsListItemView("Change Icon Color", 10, 0, ".", "Icon"));
        //lLayout.addView(new listItemLayout().optionsListItemView("Change Menu Color", 10, 0, ".", "Menu"));
    }


    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public void createCopyList(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem=0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(new listItemLayout().optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));

        for (int i=0; i < EternalMediaBar.activity.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.activity.hItem) {
                lLayout.addView(new listItemLayout().optionsListItemView("Copy to " + EternalMediaBar.activity.hli.get(i).label, 4, i, ".", "3"));
            }
            i++;
        }
        //return to first settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Go Back", 1, 1, launchIntent, appName));
        //close settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void createMoveList(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(new listItemLayout().optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));

        for (int i=0; i < EternalMediaBar.activity.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.activity.hItem) {
                lLayout.addView(new listItemLayout().optionsListItemView("Move to " + EternalMediaBar.activity.hli.get(i).label, 5, i, ".", ""));
            }
            i++;
        }
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f)));
        lLayout.addView(spacer);
        //return to first settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Go Back", 1, 1, launchIntent, appName));
        //close settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for theme change//////
    //////////////////////////////////////////////////
    public void themeChange(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(new listItemLayout().optionsListItemView("Internal", 14, 0, radioCheck(EternalMediaBar.activity.savedData.theme.equals("Internal")), "Internal"));
        lLayout.addView(new listItemLayout().optionsListItemView("Google", 14, 0, radioCheck(EternalMediaBar.activity.savedData.theme.equals("Google")), "Google"));
        lLayout.addView(new listItemLayout().optionsListItemView("Material", 14, 0, radioCheck(EternalMediaBar.activity.savedData.theme.equals("Material")), "Material"));

        //return to first settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Go Back", 1, 1, launchIntent, appName));
        //close settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public void listOrganizeSelect(int secondaryIndex, String launchIntent, String appName){

        //we have to reload this menu every time we change it because we're using a radio button system, so we might as well make use of that by changing the variable from its own function.
        if(secondaryIndex!=0){
            EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[2] = secondaryIndex;
        }
        //add the items for changing the organization method
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(new listItemLayout().optionsListItemView( "Alphabetically", 11, 1, radioCheck(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[2]==1), appName));
        lLayout.addView(new listItemLayout().optionsListItemView("Reverse Alphabetically", 11, 2, radioCheck(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[2]==2), appName));
        lLayout.addView(new listItemLayout().optionsListItemView("No Organization", 11, 3, radioCheck(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[2]==3), appName));
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f)));
        lLayout.addView(spacer);
        //check whether or not the organization is always applied, and make the apply options accordingly.
        if (EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[1] ==0) {
            lLayout.addView(new listItemLayout().optionsListItemView("Apply once", 12, -1, ".radioCheck", appName));
            lLayout.addView(new listItemLayout().optionsListItemView("Always apply", 12, 1, ".radioUnCheck", appName));
        }
        else{
            lLayout.addView(new listItemLayout().optionsListItemView("Apply once", 12, -1, ".radioUnCheck", appName));
            lLayout.addView(new listItemLayout().optionsListItemView("Always apply", 12, 1, ".radioCheck", appName));

        }

        //return to first settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Go Back", 1, 1, launchIntent, appName));
        //close settings menu
        lLayout.addView(new listItemLayout().optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    ///////////////Inner Functionality////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    private String radioCheck(Boolean choose){
        //decide based on the bool
        if (choose) {
            return ".radioCheck";
        } else {
            return ".radioUnCheck";
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
    public void copyItem(int secondaryIndex){
        //create a copy of the selected item in another list
        EternalMediaBar.activity.savedData.categories.get(secondaryIndex).appList.add(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList.get(EternalMediaBar.activity.vItem));
        menuClose();
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void moveItem(int secondaryIndex){
        EternalMediaBar.activity.savedData.categories.get(secondaryIndex).appList.add(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList.get(EternalMediaBar.activity.vItem));
        EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList.remove(EternalMediaBar.activity.vItem);
        menuClose();
        //make sure that if the new apps list disappears, we aren't on it.
        if (EternalMediaBar.activity.hItem == (EternalMediaBar.activity.savedData.categories.size()-1) && EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.savedData.categories.size()-1).appList.size()==0){
            EternalMediaBar.activity.listMove(0, true);
        }
        else{
            EternalMediaBar.activity.loadListView();
        }
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void hideApp(){
        EternalMediaBar.activity.savedData.hiddenApps.add(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList.get(EternalMediaBar.activity.vItem));
        EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList.remove(EternalMediaBar.activity.vItem);
        menuClose();
    }


    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public Intent openAppSettings(String launchIntent){

        //open application settings
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(launchIntent)));
        menuClose();
        return intent;
    }


    //////////////////////////////////////////////////
    ////////////////////Mirror UI/////////////////////
    //////////////////////////////////////////////////
    public void mirrorUI(){
        if (EternalMediaBar.activity.savedData.mirrorMode){
            EternalMediaBar.activity.savedData.mirrorMode=false;
        }
        else{
            EternalMediaBar.activity.savedData.mirrorMode = true;
        }
        menuClose();
    }


    //////////////////////////////////////////////////
    ////////////////Change The Theme//////////////////
    //////////////////////////////////////////////////
    public void setIconTheme(String theme){
        EternalMediaBar.activity.savedData.theme = theme;
        menuClose();

    }

    //////////////////////////////////////////////////
    ////////////////Change The Theme//////////////////
    //////////////////////////////////////////////////
    public void organizeByGoogle(){


    }

    //////////////////////////////////////////////////
    ////////////Enable/Disable Dim Lists//////////////
    //////////////////////////////////////////////////
    public void toggleDimLists(){
        if (EternalMediaBar.activity.savedData.dimLists){
            EternalMediaBar.activity.savedData.dimLists=false;
        }
        else{
            EternalMediaBar.activity.savedData.dimLists = true;
        }
        menuClose();
    }

    //////////////////////////////////////////////////
    //////////////////Organize List///////////////////
    //////////////////////////////////////////////////
    public void organizeList(int secondaryIndex){
        //we want to define the organization method when we load this menu.
        if (secondaryIndex ==1){
            EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[1] =1;
        }
        else if (secondaryIndex ==-1){
            EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[1] =0;
        }

        switch(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[2]){
            //no organization
            case 0:case 3:{
                break;
            }
            //alphabetical
            case 1:{
                Collections.sort(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList, new Comparator<appDetail>() {
                    @Override
                    public int compare(appDetail lhs, appDetail rhs) {
                        return lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                if (EternalMediaBar.activity.optionsMenu) {
                    menuClose();
                }
                break;
            }
            //reverse alphabetical
            case 2:{
                Collections.sort(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList, new Comparator<appDetail>() {
                    @Override
                    public int compare(appDetail lhs, appDetail rhs) {
                        return -lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                if (EternalMediaBar.activity.optionsMenu) {
                    menuClose();
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
    public void colorSelect(final String colorName, int secondaryIndex){
        //Instead of making a new case, it's easier to compensate for the cancel button by modifying this call
        if (secondaryIndex!=0){
            switch (colorName){
                case "Font":{EternalMediaBar.activity.savedData.fontCol = secondaryIndex;break;}
                case "Icon":{EternalMediaBar.activity.savedData.iconCol = secondaryIndex;break;}
                case "Menu":{EternalMediaBar.activity.savedData.menuCol = secondaryIndex;break;}
            }
            menuClose();
            return;
        }
        int col = 0;
        LinearLayout lLayout = (LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        //load the header that contains the current color
        lLayout.addView(new listItemLayout().optionsListItemView("Choose " + colorName + " Color", -1, 0, ".colHeader" + colorName, ".optionsHeader"));
        //create the inflater for the seeker bars
        View child = EternalMediaBar.activity.getLayoutInflater().inflate(R.layout.color_select, null);
        //get the red seeker bar, then set it's progress
        SeekBar seekerRed = (SeekBar) child.findViewById(R.id.redSeek);
        SeekBar seekerGreen = (SeekBar) child.findViewById(R.id.greenSeek);
        SeekBar seekerBlue = (SeekBar) child.findViewById(R.id.blueSeek);
        SeekBar seekerAlpha = (SeekBar) child.findViewById(R.id.alphaSeek);
        EditText hexText = (EditText) child.findViewById(R.id.hexText);
        switch (colorName){
            case "Font":{
                seekerRed.setProgress(Color.red(EternalMediaBar.activity.savedData.fontCol));
                seekerGreen.setProgress(Color.green(EternalMediaBar.activity.savedData.fontCol));
                seekerBlue.setProgress(Color.blue(EternalMediaBar.activity.savedData.fontCol));
                seekerAlpha.setProgress(Color.alpha(EternalMediaBar.activity.savedData.fontCol) - 25);
                hexText.setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.fontCol));
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.fontCol));
                col=EternalMediaBar.activity.savedData.fontCol;break;
            }
            case "Icon":{
                seekerRed.setProgress(Color.red(EternalMediaBar.activity.savedData.iconCol));
                seekerGreen.setProgress(Color.green(EternalMediaBar.activity.savedData.iconCol));
                seekerBlue.setProgress(Color.blue(EternalMediaBar.activity.savedData.iconCol));
                seekerAlpha.setProgress(Color.alpha(EternalMediaBar.activity.savedData.iconCol) - 25);
                hexText.setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.iconCol));
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.iconCol));
                col=EternalMediaBar.activity.savedData.iconCol;break;
            }
            case "Menu":{
                seekerRed.setProgress(Color.red(EternalMediaBar.activity.savedData.menuCol));
                seekerGreen.setProgress(Color.green(EternalMediaBar.activity.savedData.menuCol));
                seekerBlue.setProgress(Color.blue(EternalMediaBar.activity.savedData.menuCol));
                seekerAlpha.setProgress(Color.alpha(EternalMediaBar.activity.savedData.menuCol) - 25);
                hexText.setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.menuCol));
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.menuCol));
                col=EternalMediaBar.activity.savedData.menuCol;break;
            }
        }



        //lastly change the listener for the Red seek bar
        seekerRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //when the bar is moved, change the value of the font color, then update the image accordingly.
                switch (colorName) {
                    case "Font": {
                        EternalMediaBar.activity.savedData.fontCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.fontCol), progress, Color.green(EternalMediaBar.activity.savedData.fontCol), Color.blue(EternalMediaBar.activity.savedData.fontCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.fontCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.fontCol));break;
                    }
                    case "Icon": {
                        EternalMediaBar.activity.savedData.iconCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.iconCol), progress, Color.green(EternalMediaBar.activity.savedData.iconCol), Color.blue(EternalMediaBar.activity.savedData.iconCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.iconCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.iconCol));break;
                    }
                    case "Menu": {
                        EternalMediaBar.activity.savedData.menuCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.menuCol), progress, Color.green(EternalMediaBar.activity.savedData.menuCol), Color.blue(EternalMediaBar.activity.savedData.menuCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.menuCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.menuCol));break;
                    }
                }
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
                        EternalMediaBar.activity.savedData.fontCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.fontCol), Color.red(EternalMediaBar.activity.savedData.fontCol), progress, Color.blue(EternalMediaBar.activity.savedData.fontCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.fontCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.fontCol));break;
                    }
                    case "Icon": {
                        EternalMediaBar.activity.savedData.iconCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.iconCol), Color.red(EternalMediaBar.activity.savedData.iconCol), progress, Color.blue(EternalMediaBar.activity.savedData.iconCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.iconCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.iconCol));break;
                    }
                    case "Menu": {
                        EternalMediaBar.activity.savedData.menuCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.menuCol), Color.red(EternalMediaBar.activity.savedData.menuCol), progress, Color.blue(EternalMediaBar.activity.savedData.menuCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.menuCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.menuCol));break;
                    }
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
                        EternalMediaBar.activity.savedData.fontCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.fontCol), Color.red(EternalMediaBar.activity.savedData.fontCol), Color.green(EternalMediaBar.activity.savedData.fontCol), progress);
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.fontCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.fontCol));break;
                    }
                    case "Icon": {
                        EternalMediaBar.activity.savedData.iconCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.iconCol), Color.red(EternalMediaBar.activity.savedData.iconCol), Color.green(EternalMediaBar.activity.savedData.iconCol), progress);
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.iconCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.iconCol));break;
                    }
                    case "Menu": {
                        EternalMediaBar.activity.savedData.menuCol = Color.argb(Color.alpha(EternalMediaBar.activity.savedData.menuCol), Color.red(EternalMediaBar.activity.savedData.menuCol), Color.green(EternalMediaBar.activity.savedData.menuCol), progress);
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.menuCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.menuCol));break;
                    }
                }
            }

            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for Alpha
        seekerAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the bar is moved, change the value of the font color, then update the image accordingly.
                switch (colorName) {
                    case "Font":{
                        EternalMediaBar.activity.savedData.fontCol = Color.argb(progress+25, Color.red(EternalMediaBar.activity.savedData.fontCol), Color.green(EternalMediaBar.activity.savedData.fontCol), Color.blue(EternalMediaBar.activity.savedData.fontCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.fontCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.fontCol));break;
                    }
                    case "Icon":{
                        EternalMediaBar.activity.savedData.iconCol = Color.argb(progress+25, Color.red(EternalMediaBar.activity.savedData.iconCol), Color.green(EternalMediaBar.activity.savedData.iconCol), Color.blue(EternalMediaBar.activity.savedData.iconCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.iconCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.iconCol));break;
                    }
                    case "Menu":{
                        EternalMediaBar.activity.savedData.menuCol = Color.argb(progress+25, Color.red(EternalMediaBar.activity.savedData.menuCol), Color.green(EternalMediaBar.activity.savedData.menuCol), Color.blue(EternalMediaBar.activity.savedData.menuCol));
                        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.menuCol));
                        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.menuCol));break;
                    }
                }
            }
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        hexText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    int col = Color.parseColor(((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).getText().toString());
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.redSeek)).setProgress(Color.red(col));
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.greenSeek)).setProgress(Color.green(col));
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.blueSeek)).setProgress(Color.blue(col));
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.alphaSeek)).setProgress(Color.alpha(col));
                } catch (Exception e) {
                }
                return false;
            }
        });


                //and finally add the view
                lLayout.addView(child);
        //add the item for save and quit
        lLayout.addView(new listItemLayout().optionsListItemView("Save and close", 0, 0, ".", "."));

        //add the item for cancel changes
        lLayout.addView(new listItemLayout().optionsListItemView("Close without saving", 10, col, ".", colorName));
    }

}
