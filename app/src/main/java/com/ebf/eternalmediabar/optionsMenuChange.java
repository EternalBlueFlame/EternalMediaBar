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
        sLayout.setBackgroundColor(EternalMediaBar.activity.savedData.menuCol);
        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist)).removeAllViews();
        //animate the menu opening
        TranslateAnimation anim;
        if (!EternalMediaBar.activity.savedData.mirrorMode) {
            //reset the position
            sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels);
            anim = new TranslateAnimation(0, -(144 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        else{
            //reset the position
            sLayout.setX(-144 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f);
            anim = new TranslateAnimation(0, (144 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
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
                    sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels - (144 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f));
                } else {
                    sLayout.setX(0);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //if the program can be launched open the app options menu
        if (isLaunchable) {loadAppOptionsMenu(launchIntent, appName);}
        //otherwise load the normal options menu
        else{loadMainOptionsItems();}
        EternalMediaBar.activity.optionVitem=1;


        LinearLayout lLayout = (LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist);
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f)));
        lLayout.addView(spacer);
        //close settings menu, we put this here since it's on the menu without exception.
        lLayout.addView(new listItemLayout().optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
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
        if (!EternalMediaBar.activity.savedData.mirrorMode) {anim = new TranslateAnimation(0, (144 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);}
        else{anim = new TranslateAnimation(0, -(144 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f), 0, 0);}
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
                    sLayout.setX(-144 * EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f);
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
        lLayout.addView(new listItemLayout().optionsListItemView("Copy to...", 2, 0, launchIntent, appName));
        lLayout.addView(new listItemLayout().optionsListItemView("Move to...", 3, 0, launchIntent, appName));
        //if the app is in other lists, add an option remove item from this list.
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
            //later this will be modified to support hiding the icon when it's only in one list.
        }
        // !!! ENABLE AFTER FIXED !!! //Auto Categorize
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


        lLayout.addView(new listItemLayout().optionsListItemView("Application Settings", 7, 0, launchIntent, appName));
        lLayout.addView(new listItemLayout().optionsListItemView("Reorganize This Category", 11, 0, launchIntent, appName));

    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public void loadMainOptionsItems(){
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.addView(new listItemLayout().optionsListItemView("Choose Theme", 8, 0, ".", "."));
        lLayout.addView(new listItemLayout().optionsListItemView("Theme Colors", 15,0,".","."));
        lLayout.addView(new listItemLayout().optionsListItemView("Mirror Layout", 9, 0, ".", "."));
        if(EternalMediaBar.activity.savedData.doubleTap){
            lLayout.addView(new listItemLayout().optionsListItemView("Disable Double Tap", 13, 0, ".", "."));
        } else {
            lLayout.addView(new listItemLayout().optionsListItemView("Enable Double Tap", 13, 0, ".", "."));
        }
        lLayout.addView(new listItemLayout().optionsListItemView("Homepage",16,0,"http://github.com/EternalBlueFlame/EternalMediaBar","."));

    }


    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public void createCopyList(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem=0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(new listItemLayout().optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));
        //add the options for copying the menu item, skip the one for the current menu
        for (int i=0; i < EternalMediaBar.activity.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.activity.hItem) {
                lLayout.addView(new listItemLayout().optionsListItemView("Copy to " + EternalMediaBar.activity.savedData.categories.get(i).categoryName, 4, i, ".", "3"));
            }
            i++;
        }
        goBackItems(lLayout, launchIntent, appName);
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void createMoveList(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        lLayout.addView(new listItemLayout().optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));
        //add the options for moving the menu item, skip the one for the current menu
        for (int i=0; i < EternalMediaBar.activity.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.activity.hItem) {
                lLayout.addView(new listItemLayout().optionsListItemView("Move to " + EternalMediaBar.activity.savedData.categories.get(i).categoryName, 5, i, ".", ""));
            }
            i++;
        }
        goBackItems(lLayout, launchIntent, appName);
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for theme change//////
    //////////////////////////////////////////////////
    public void themeChange(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        //add the items for changing the theme
        lLayout.addView(new listItemLayout().optionsListItemView("Lunar\nDefault", 14, 0, radioCheck(EternalMediaBar.activity.savedData.theme.equals("Internal")), "Internal"));
        lLayout.addView(new listItemLayout().optionsListItemView("Lunar\nInverse", 14, 0, radioCheck(EternalMediaBar.activity.savedData.theme.equals("LunarInverse")), "LunarInverse"));
        lLayout.addView(new listItemLayout().optionsListItemView("Google", 14, 0, radioCheck(EternalMediaBar.activity.savedData.theme.equals("Google")), "Google"));
        lLayout.addView(new listItemLayout().optionsListItemView("Material", 14, 0, radioCheck(EternalMediaBar.activity.savedData.theme.equals("Material")), "Material"));

        goBackItems(lLayout, launchIntent, appName);
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for theme change//////
    //////////////////////////////////////////////////
    public void themeColorChange(String launchIntent, String appName){
        EternalMediaBar.activity.optionVitem = 0;
        LinearLayout lLayout = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist);
        lLayout.removeAllViews();
        //add the items for changing the theme colors
        lLayout.addView(new listItemLayout().optionsListItemView("Change Font Color", 10, 0, ".", "Font"));
        lLayout.addView(new listItemLayout().optionsListItemView("Change Icon Color", 10, 0, ".", "Icon"));
        lLayout.addView(new listItemLayout().optionsListItemView("Change Menu Color", 10, 0, ".", "Menu"));
        lLayout.addView(new listItemLayout().optionsListItemView("Change Menu Color", 10, 0, ".", "App Backgrounds"));

        goBackItems(lLayout, launchIntent, appName);
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

        goBackItems(lLayout, launchIntent, appName);
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

    private void goBackItems(LinearLayout lLayout, String launchIntent, String appName){
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f)));
        lLayout.addView(spacer);
        //add go back buttons
        lLayout.addView(new listItemLayout().optionsListItemView("Go Back", 1, 1, launchIntent, appName));
        lLayout.addView(new listItemLayout().optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Functionality///////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    /////////////////Change Boolean///////////////////
    //////////////////////////////////////////////////

    public Boolean toggleBool(Boolean bool){
        if (bool){return false;}
        else{return true;}
    }


    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public void copyItem(int secondaryIndex){
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
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(launchIntent)));
        menuClose();
        return intent;
    }


    //////////////////////////////////////////////////
    ////////////////Change The Theme//////////////////
    //////////////////////////////////////////////////
    public void setIconTheme(String theme){
        EternalMediaBar.activity.savedData.theme = theme;
        menuClose();

    }


    //////////////////////////////////////////////////
    //////////////////Organize List///////////////////
    //////////////////////////////////////////////////
    public void organizeList(int secondaryIndex){
        //we want to define the organization method when we load this menu.
        //toggle for if the organize is used every time the list is loaded.
        if (secondaryIndex ==1){EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[1] =1;}
        else if (secondaryIndex ==-1){EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[1] =0;}

        switch(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).organizeMode[2]){
            //no organization
            case 0:case 3:{break;}
            //alphabetical
            case 1:{
                Collections.sort(EternalMediaBar.activity.savedData.categories.get(EternalMediaBar.activity.hItem).appList, new Comparator<appDetail>() {
                    @Override
                    public int compare(appDetail lhs, appDetail rhs) {
                        return lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
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
                break;
            }
            //Most used
            case 4:{break;}
        }
        if (EternalMediaBar.activity.optionsMenu) {
            menuClose();
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
                case "App Backgrounds":{EternalMediaBar.activity.savedData.dimCol = secondaryIndex;break;}
            }
            menuClose();
            return;
        }
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
        //set the currentCol int to define the value of what the color was when we started, also use it so we can quicklyset the initial positions of the values.
        int currentCol = 0xffffffff;
        switch (colorName){
            case "Font":{currentCol=EternalMediaBar.activity.savedData.fontCol;break;}
            case "Icon":{currentCol=EternalMediaBar.activity.savedData.iconCol;break;}
            case "Menu":{currentCol=EternalMediaBar.activity.savedData.menuCol;break;}
            case "App Backgrounds":{currentCol=EternalMediaBar.activity.savedData.dimCol;break;}
        }
        seekerRed.setProgress(Color.red(currentCol));
        seekerGreen.setProgress(Color.green(currentCol));
        seekerBlue.setProgress(Color.blue(currentCol));
        seekerAlpha.setProgress(Color.alpha(currentCol) - 25);
        hexText.setText("#" + Integer.toHexString(currentCol));
        ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(currentCol));




        //change the listener for the Red seek bar
        seekerRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,progress,0,0,colorName);}
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it again for green
        seekerGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,0,progress,0,colorName);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for blue
        seekerBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,0,0,progress,colorName);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for Alpha
        seekerAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(progress +25, 0,0,0,colorName);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        hexText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    //we attempt to parse the given hex color, we don't have to push it directly to the variable here because changing the sliver values does that for us anyway.
                    int col = Color.parseColor(((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).getText().toString());
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.redSeek)).setProgress(Color.red(col));
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.greenSeek)).setProgress(Color.green(col));
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.blueSeek)).setProgress(Color.blue(col));
                    ((SeekBar) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.alphaSeek)).setProgress(Color.alpha(col));
                } catch (Exception e) {}
                return false;
            }
        });


        lLayout.addView(child);
        lLayout.addView(new listItemLayout().optionsListItemView("Save and close", 0, 0, ".", "."));
        //if the user decides to cancel, this is where we use the value of the color before changes that we stored earlier.
        lLayout.addView(new listItemLayout().optionsListItemView("Close without saving", 10, currentCol, ".", colorName));
    }

    public void scrollSetColor(int a, int r, int g, int b, String type){
        switch (type){
            case "Font":{
                if (a==0){a=Color.alpha(EternalMediaBar.activity.savedData.fontCol);}
                if (r==0){r=Color.red(EternalMediaBar.activity.savedData.fontCol);}
                if (g==0){g=Color.green(EternalMediaBar.activity.savedData.fontCol);}
                if (b==0){b=Color.blue(EternalMediaBar.activity.savedData.fontCol);}
                EternalMediaBar.activity.savedData.fontCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.fontCol));
                //changing the hex box will automatically change the scroll bars accordingly.
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.fontCol));
            }
            case "Icon":{
                if (a==0){a=Color.alpha(EternalMediaBar.activity.savedData.iconCol);}
                if (r==0){r=Color.red(EternalMediaBar.activity.savedData.iconCol);}
                if (g==0){g=Color.green(EternalMediaBar.activity.savedData.iconCol);}
                if (b==0){b=Color.blue(EternalMediaBar.activity.savedData.iconCol);}
                EternalMediaBar.activity.savedData.iconCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.iconCol));
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.iconCol));
            }
            case "Menu":{
                if (a==0){a=Color.alpha(EternalMediaBar.activity.savedData.menuCol);}
                if (r==0){r=Color.red(EternalMediaBar.activity.savedData.menuCol);}
                if (g==0){g=Color.green(EternalMediaBar.activity.savedData.menuCol);}
                if (b==0){b=Color.blue(EternalMediaBar.activity.savedData.menuCol);}
                EternalMediaBar.activity.savedData.menuCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.menuCol));
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.menuCol));
            }
            case "App Backgrounds":{
                if (a==0){a=Color.alpha(EternalMediaBar.activity.savedData.dimCol);}
                if (r==0){r=Color.red(EternalMediaBar.activity.savedData.dimCol);}
                if (g==0){g=Color.green(EternalMediaBar.activity.savedData.dimCol);}
                if (b==0){b=Color.blue(EternalMediaBar.activity.savedData.dimCol);}
                EternalMediaBar.activity.savedData.dimCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.activity.savedData.dimCol));
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.activity.savedData.dimCol));
            }
        }
    }

}
