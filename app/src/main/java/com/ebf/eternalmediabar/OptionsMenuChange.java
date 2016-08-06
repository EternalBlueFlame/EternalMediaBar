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

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;

import java.util.Collections;
import java.util.Comparator;


//This class is intended to manage a large number of functions related to menu interactions,
public class OptionsMenuChange {

    //////////////////////////////////////////////////
    /////////////////Open the menu////////////////////
    //////////////////////////////////////////////////
    public static void menuOpen(final boolean isLaunchable, final String launchIntent, final String appName){
        //set the variables for the menu
        EternalMediaBar.optionsMenu = true;
        EternalMediaBar.optionVitem = 1;
        //load the layout and make sure nothing is in it.
        ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
        sLayout.setBackgroundColor(EternalMediaBar.savedData.menuCol);
        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist)).removeAllViews();
        //animate the menu opening
        TranslateAnimation anim;
        if (!EternalMediaBar.savedData.mirrorMode) {
            //reset the position
            sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels);
            anim = new TranslateAnimation(0, -(144 * EternalMediaBar.dpi.scaledDensity), 0, 0);
        }
        else{
            //reset the position
            sLayout.setX(-144 * EternalMediaBar.dpi.scaledDensity);
            anim = new TranslateAnimation(0, (144 * EternalMediaBar.dpi.scaledDensity), 0, 0);
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
                if (!EternalMediaBar.savedData.mirrorMode) {
                    sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels - (144 * EternalMediaBar.dpi.scaledDensity));
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
        else if (appName.equals(".category")){loadCategoryOptionsItems();}
        //otherwise load the normal options menu
        else{loadMainOptionsItems();}
        EternalMediaBar.optionVitem=1;

        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //close settings menu, we put this here since it's on the menu without exception.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    /////////////////Close the menu///////////////////
    //////////////////////////////////////////////////
    public static void menuClose() {
        //load the layouts
        ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
        //empty the one that has content
        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.optionslist)).removeAllViews();
        //set the variables in the main activity
        EternalMediaBar.optionsMenu = false;
        EternalMediaBar.optionVitem=1;
        //animate menu closing
        TranslateAnimation anim;
        if (!EternalMediaBar.savedData.mirrorMode) {anim = new TranslateAnimation(0, (144 * EternalMediaBar.dpi.scaledDensity), 0, 0);}
        else{anim = new TranslateAnimation(0, -(144 * EternalMediaBar.dpi.scaledDensity), 0, 0);}
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
                if (!EternalMediaBar.savedData.mirrorMode) {
                    sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels);
                } else {
                    sLayout.setX(-144 * EternalMediaBar.dpi.scaledDensity);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        //save any changes and reload the view
        EternalMediaBar.savedData.writeXML(EternalMediaBar.activity);
        EternalMediaBar.activity.loadListView();
    }


    //////////////////////////////////////////////////
    /////Load the settings menu for a selected app////
    //////////////////////////////////////////////////
    public static void loadAppOptionsMenu(String launchIntent, String appName){
        EternalMediaBar.optionVitem = 1;

        //add the app that's selected so the user knows for sure what they are messing with.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Copy to...", 2, 0, launchIntent, appName));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Move to...", 3, 0, launchIntent, appName));

        //if the app is in other lists, add an option remove item from this list.
        int i=0;
        for (CategoryClass category : EternalMediaBar.savedData.categories){
            for (AppDetail app: category.appList){
                if (app.URI.equals(launchIntent)){
                    i++;
                }
                if (i>1){
                    break;
                }
            }
            if (i>1){
                break;
            }
        }
        if (i>1) {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Remove this item", 6, 0, launchIntent, "4"));
        }
        else{
            //later this will be modified to support hiding the icon when it's only in one list.
        }
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Application Settings", 7, 0, launchIntent, appName));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Reorganize This Category", 11, 0, launchIntent, appName));

    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public static void loadMainOptionsItems(){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose Theme", 8, 0, ".", "."));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Theme Colors", 15, 0, ".", "."));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Mirror Layout", 9, 0, ".", "."));
        if(EternalMediaBar.savedData.doubleTap){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Disable Double Tap", 13, 0, ".", "."));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Enable Double Tap", 13, 0, ".", "."));
        }
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Homepage", 16, 0, "http://github.com/EternalBlueFlame/EternalMediaBar", "."));

    }

    //////////////////////////////////////////////////
    //////Load the settings menu for categories///////
    //////////////////////////////////////////////////
    public static void loadCategoryOptionsItems(){
        EternalMediaBar.optionVitem = 0;
        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).categoryName, 17, 0, EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).categoryIcon, ".optionsHeader"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Move multiple apps", 17, 0, ".", "."));
        goBackItems(".", ".");

    }

    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public static void createMultipleMoveList(){
        EternalMediaBar.optionVitem = 0;
        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).categoryName, 17, 0, EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).categoryIcon, ".optionsHeader"));
        goBackItems(".", ".category");


        for (int i=0; i< EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.size();){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).label, 18, 0, EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).URI, i+""));
            i++;
        }
        goBackItems(".", ".category");
    }
    public static void moveMultipleItems(int secondaryIndex){

    }

    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public static void createCopyList(String launchIntent, String appName){
        EternalMediaBar.optionVitem=0;

        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));
        //add the options for copying the menu item, skip the one for the current menu
        for (int i=0; i < EternalMediaBar.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.hItem) {
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Copy to " + EternalMediaBar.savedData.categories.get(i).categoryName, 4, i, ".", "3"));
            }
            i++;
        }
        goBackItems(launchIntent, appName);
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public static void createMoveList(String launchIntent, String appName){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(appName, -1, 0, launchIntent, ".optionsHeader"));
        //add the options for moving the menu item, skip the one for the current menu
        for (int i=0; i < EternalMediaBar.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.hItem) {
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Move to " + EternalMediaBar.savedData.categories.get(i).categoryName, 5, i, ".", ""));
            }
            i++;
        }
        goBackItems(launchIntent, appName);
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for theme change//////
    //////////////////////////////////////////////////
    public static void themeChange(String launchIntent, String appName){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        //add the items for changing the theme
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Lunar\nDefault", 14, 0, radioCheck(EternalMediaBar.savedData.theme.equals("Internal")), "Internal"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Lunar\nInverse", 14, 0, radioCheck(EternalMediaBar.savedData.theme.equals("LunarInverse")), "LunarInverse"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Google", 14, 0, radioCheck(EternalMediaBar.savedData.theme.equals("Google")), "Google"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Material", 14, 0, radioCheck(EternalMediaBar.savedData.theme.equals("Material")), "Material"));

        goBackItems(launchIntent, appName);
    }

    //////////////////////////////////////////////////
    ///Load the settings menu for theme color change//
    //////////////////////////////////////////////////
    public static void themeColorChange(String launchIntent, String appName){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        //add the items for changing the theme colors
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Font Color", 10, 0, ".", "Font"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Icon Color", 10, 0, ".", "Icon"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Menu Color", 10, 0, ".", "Menu"));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change List Color", 10, 0, ".", "App Backgrounds"));

        goBackItems(launchIntent, appName);
    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public static void listOrganizeSelect(int secondaryIndex, String launchIntent, String appName){

        //we have to reload this menu every time we change it because we're using a radio button system, so we might as well make use of that by changing the variable from its own function.
        if(secondaryIndex!=0){
            EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[2] = secondaryIndex;
        }
        //add the items for changing the organization method
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Alphabetically", 11, 1, radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[2] == 1), appName));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Reverse Alphabetically", 11, 2, radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[2] == 2), appName));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("No Organization", 11, 3, radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[2] == 3), appName));
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //check whether or not the organization is always applied, and make the apply options accordingly.
        if (EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[1] ==0) {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Apply once", 12, -1, ".radioCheck", appName));
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Always apply", 12, 1, ".radioUnCheck", appName));
        }
        else{
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Apply once", 12, -1, ".radioUnCheck", appName));
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Always apply", 12, 1, ".radioCheck", appName));
        }

        goBackItems(launchIntent, appName);
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    ///////////////Inner Functionality////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    private static String radioCheck(Boolean choose){
        //decide based on the bool
        if (choose) {
            return ".radioCheck";
        } else {
            return ".radioUnCheck";
        }
    }

    private static void goBackItems(String launchIntent, String appName){
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //add go back buttons
        if (launchIntent.equals(".category")){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Go Back", 1, 0, launchIntent, appName));
        } else if (!launchIntent.equals(".settings") && !launchIntent.equals(".")) {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Go Back", 1, 1, launchIntent, appName));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Go Back", 1, 0, launchIntent, appName));
        }
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Exit Options", 0, 0, launchIntent, appName));
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Functionality///////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    /////////////////Change Boolean///////////////////
    //////////////////////////////////////////////////

    public static Boolean toggleBool(Boolean bool){
        menuClose();
        return !bool;
    }


    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public static void copyItem(int secondaryIndex){
        EternalMediaBar.savedData.categories.get(secondaryIndex).appList.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(EternalMediaBar.vItem));
        menuClose();
    }


    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public static void moveItem(int secondaryIndex) {
        EternalMediaBar.savedData.categories.get(secondaryIndex).appList.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(EternalMediaBar.vItem));
        EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(EternalMediaBar.vItem);
        menuClose();
        //make sure that if the new apps list disappears, we aren't on it.
        if (EternalMediaBar.hItem == (EternalMediaBar.savedData.categories.size()-1) && EternalMediaBar.savedData.categories.get(EternalMediaBar.savedData.categories.size()-1).appList.size()==0){
            EternalMediaBar.activity.listMove(0, true);
        }
        else{
            EternalMediaBar.activity.loadListView();
        }
    }


    //////////////////////////////////////////////////
    //////////////Hide an app menu item///////////////
    //////////////////////////////////////////////////
    public static void hideApp() {
        EternalMediaBar.savedData.hiddenApps.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(EternalMediaBar.vItem));
        EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(EternalMediaBar.vItem);
        menuClose();
    }


    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public static Intent openAppSettings(String launchIntent){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(launchIntent)));
        menuClose();
        return intent;
    }


    //////////////////////////////////////////////////
    ////////////////Change The Theme//////////////////
    //////////////////////////////////////////////////
    public static void setIconTheme(String theme){
        EternalMediaBar.savedData.theme = theme;
        menuClose();
    }


    //////////////////////////////////////////////////
    //////////////////Organize List///////////////////
    //////////////////////////////////////////////////
    public static void organizeList(int secondaryIndex){
        //we want to define the organization method when we load this menu.
        //toggle for if the organize is used every time the list is loaded.
        if (secondaryIndex ==1){EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[1] =1;}
        else if (secondaryIndex ==-1){EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[1] =0;}

        switch(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode[2]){
            //no organization
            case 0:case 3:{break;}
            //alphabetical
            case 1:{
                Collections.sort(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //reverse alphabetical
            case 2:{
                Collections.sort(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return -lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //Most used
            case 4:{break;}
        }
        if (EternalMediaBar.optionsMenu) {
            menuClose();
        }
    }







    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Color Selector//////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    public static void colorSelect(final String colorName, int secondaryIndex){
        //Instead of making a new case, it's easier to compensate for the cancel button by modifying this call
        if (secondaryIndex!=0){
            switch (colorName){
                case "Font":{EternalMediaBar.savedData.fontCol = secondaryIndex;break;}
                case "Icon":{EternalMediaBar.savedData.iconCol = secondaryIndex;break;}
                case "Menu":{EternalMediaBar.savedData.menuCol = secondaryIndex;break;}
                case "App Backgrounds":{EternalMediaBar.savedData.dimCol = secondaryIndex;break;}
            }
            menuClose();
            return;
        }
        EternalMediaBar.optionsLayout.removeAllViews();
        //load the header that contains the current color
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose " + colorName + " Color", -1, 0, ".colHeader" + colorName, ".optionsHeader"));
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
            case "Font":{currentCol=EternalMediaBar.savedData.fontCol;break;}
            case "Icon":{currentCol=EternalMediaBar.savedData.iconCol;break;}
            case "Menu":{currentCol=EternalMediaBar.savedData.menuCol;break;}
            case "App Backgrounds":{currentCol=EternalMediaBar.savedData.dimCol;break;}
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


        EternalMediaBar.optionsLayout.addView(child);
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Save and close", 0, 0, ".", "."));
        //if the user decides to cancel, this is where we use the value of the color before changes that we stored earlier.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Close without saving", 10, currentCol, ".", colorName));
    }

    public static void scrollSetColor(int a, int r, int g, int b, String type){
        switch (type){
            case "Font":{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.fontCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.fontCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.fontCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.fontCol);}
                EternalMediaBar.savedData.fontCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.savedData.fontCol));
                //changing the hex box will automatically change the scroll bars accordingly.
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.savedData.fontCol));
                break;
            }
            case "Icon":{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.iconCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.iconCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.iconCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.iconCol);}
                EternalMediaBar.savedData.iconCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.savedData.iconCol));
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.savedData.iconCol));
                break;
            }
            case "Menu":{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.menuCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.menuCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.menuCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.menuCol);}
                EternalMediaBar.savedData.menuCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.savedData.menuCol));
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.savedData.menuCol));
                break;
            }
            case "App Backgrounds":{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.dimCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.dimCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.dimCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.dimCol);}
                EternalMediaBar.savedData.dimCol = Color.argb(a, r, g, b);
                ((ImageView) ((LinearLayout) EternalMediaBar.activity.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.savedData.dimCol));
                ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.savedData.dimCol));
                break;
            }
        }
    }

}
