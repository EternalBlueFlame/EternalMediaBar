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



//This class is intended to manage a large number of functions related to menu interactions,
public class OptionsMenuChange {

    //////////////////////////////////////////////////
    /////////////////Open the menu////////////////////
    //////////////////////////////////////////////////
    public static void menuOpen(final AppDetail menuItem, boolean isApp){
        //just in case the user happened to hit "go back" on the copy or move page.
        if (EternalMediaBar.copyingOrMoving){
            EternalMediaBar.copyingOrMoving = false;
            //clear selected apps
            for (String uri : EternalMediaBar.selectedApps){
                for (int i=0; i<EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.size();){
                    if (uri.equals(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).URI)) {
                        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.apps_display)).getChildAt(i).findViewById(R.id.list_item_checkbox).setVisibility(View.INVISIBLE);
                        break;
                    }
                    i++;
                }
            }
            EternalMediaBar.selectedApps.clear();
        }

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
        if (isApp) {loadAppOptionsMenu(menuItem);}
        //otherwise load the normal options menu
        else{loadMainOptionsItems();}
        EternalMediaBar.optionVitem=1;

        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //close settings menu, we put this here since it's on the menu without exception.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Exit Options", 0, 0, menuItem));
    }


    //////////////////////////////////////////////////
    /////////////////Close the menu///////////////////
    //////////////////////////////////////////////////
    public static void menuClose() {
        //load the layouts
        ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
        //empty the one that has content
        EternalMediaBar.copyingOrMoving = false;
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
        //clear selected apps
        for (String uri : EternalMediaBar.selectedApps){
            for (int i=0; i<EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.size();){
                if (uri.equals(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).URI)) {
                    ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.apps_display)).getChildAt(EternalMediaBar.vItem).findViewById(R.id.list_item_checkbox).setVisibility(View.INVISIBLE);
                    break;
                }
                i++;
            }
        }
        EternalMediaBar.selectedApps.clear();


        //save any changes and reload the view
        EternalMediaBar.savedData.writeXML(EternalMediaBar.activity);
        EternalMediaBar.activity.loadListView();
    }


    //////////////////////////////////////////////////
    /////Load the settings menu for a selected app////
    //////////////////////////////////////////////////
    public static void loadAppOptionsMenu(AppDetail menuItem){
        EternalMediaBar.optionVitem = 1;

        //add the app that's selected so the user knows for sure what they are messing with.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(menuItem.label, -1, 0, new AppDetail(menuItem.label,"",menuItem.URI, ".optionsHeader")));

        if (menuItem.internalCommand.equals(".search") || menuItem.URI.equals(".webSearch") || menuItem.URI.equals(".storeSearch") || menuItem.URI.equals(".musicSearch")) {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Application Settings", 17, 0, menuItem));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Copy to...", 10, 0, menuItem));
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Move to...", 11, 1, menuItem));

            //if the app is in other lists, add an option remove item from this list.
            int i = 0;
            for (CategoryClass category : EternalMediaBar.savedData.categories) {
                for (AppDetail app : category.appList) {
                    if (app.URI.equals(menuItem.URI)) {
                        i++;
                    }
                    if (i > 1) {
                        break;
                    }
                }
                if (i > 1) {
                    break;
                }
            }
            if (i > 1) {
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Remove this item", 16, 0, menuItem));
            } else {
                //later this will be modified to support hiding the icon when it's only in one list.
            }
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Application Settings", 17, 0, menuItem));
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Reorganize This Category", 24, 0, menuItem));
        }

    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public static void loadMainOptionsItems(){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose Theme", 19, 0, new AppDetail()));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Theme Colors", 23, 0, new AppDetail()));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Mirror Layout", 30, 0, new AppDetail()));
        if(EternalMediaBar.savedData.doubleTap){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Disable Double Tap", 31, 0, new AppDetail()));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Enable Double Tap", 31, 0, new AppDetail()));
        }
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Homepage", 18, 0, new AppDetail("", "http://github.com/EternalBlueFlame/EternalMediaBar", false)));

    }


    //////////////////////////////////////////////////
    ///////////Copy/move an app menu item/////////////
    //////////////////////////////////////////////////
    public static void createCopyList(AppDetail menuItem, boolean isMove){
        EternalMediaBar.optionVitem=0;
        EternalMediaBar.copyingOrMoving = true;

        ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.apps_display)).getChildAt(EternalMediaBar.vItem).findViewById(R.id.list_item_checkbox).setVisibility(View.VISIBLE);
        EternalMediaBar.selectedApps.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(EternalMediaBar.vItem).URI);

        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(menuItem.label, -1, 0, new AppDetail(menuItem.label,"",menuItem.URI, ".optionsHeader")));
        //add the options for copying the menu item, skip the one for the current menu
        for (int i=0; i < EternalMediaBar.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.hItem) {
                if (isMove){
                        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Move to " + EternalMediaBar.savedData.categories.get(i).categoryName, 14, i, menuItem));
                    } else{
                        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Copy to " + EternalMediaBar.savedData.categories.get(i).categoryName, 13, i, menuItem));
                    }
                }
            i++;
        }
        goBackItems(menuItem, true);
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for theme change//////
    //////////////////////////////////////////////////
    public static void themeChange(AppDetail menuItem){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        //add the items for changing the theme
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Lunar\nDefault", 20, 0, new AppDetail("", "","Internal", radioCheck(EternalMediaBar.savedData.theme.equals("Internal")))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Lunar\nInverse", 20, 0, new AppDetail("", "", "LunarInverse", radioCheck(EternalMediaBar.savedData.theme.equals("LunarInverse")))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Google", 20, 0, new AppDetail("", "", "Google", radioCheck(EternalMediaBar.savedData.theme.equals("Google")))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Material", 20, 0, new AppDetail("", "", "Material", radioCheck(EternalMediaBar.savedData.theme.equals("Material")))));

        goBackItems(menuItem,false);
    }

    //////////////////////////////////////////////////
    ///Load the settings menu for theme color change//
    //////////////////////////////////////////////////
    public static void themeColorChange(AppDetail menuItem){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        //add the items for changing the theme colors
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Font Color", 21, 0, new AppDetail("", "", "Font", "")));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Icon Color", 21, 0, new AppDetail("", "", "Icon", "")));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Menu Color", 21, 0, new AppDetail("", "", "Menu", "")));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change List Color", 21, 0, new AppDetail("", "", "App Backgrounds", "")));

        goBackItems(menuItem, false);
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public static void listOrganizeSelect(AppDetail menuItem){
        //add the items for changing the organization method
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Alphabetically", 25, 1, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 1))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Reverse Alphabetically", 25, 2, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 2))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("No Organization", 25, 3, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 3))));
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //check whether or not the organization is always applied, and make the apply options accordingly.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Apply once", 26, -1, menuItem.setCommand(radioCheck(!EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeAlways))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Always apply", 26, 1, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeAlways))));

        goBackItems(menuItem, true);
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

    private static void goBackItems(AppDetail menuItem, boolean isApp){
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //add go back buttons
        if (isApp){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Go Back", 1, 0, menuItem));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Go Back", 2, 0, menuItem));
        }
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Exit Options", 0, 0, menuItem));
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Functionality///////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////


    //////////////////////////////////////////////////
    /////////Copy/move/hide an app menu item//////////
    //////////////////////////////////////////////////
    public static void relocateItem(int category, boolean isMove, boolean isHide){

        for (String uri : EternalMediaBar.selectedApps){
            for (int i =0; i<EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.size();){
                if (EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).URI.equals(uri)){
                    if (!isHide) {
                        //copy and move
                        EternalMediaBar.savedData.categories.get(category).appList.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                    } else {
                        //hide
                        EternalMediaBar.savedData.hiddenApps.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                    }
                    if (isMove || isHide){
                        //remove the old item
                        EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(i);
                    }
                    break;
                }
                i++;
            }
        }
        menuClose();

        if (isMove){
            EternalMediaBar.activity.loadListView();
        }

    }

    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public static Intent openAppSettings(AppDetail menuItem){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(menuItem.URI)));
        menuClose();
        return intent;
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Color Selector//////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    public static void colorSelect(final AppDetail menuItem){
        EternalMediaBar.optionsLayout.removeAllViews();
        //load the header that contains the current color
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose " + menuItem.label + " Color", -1, 0, new AppDetail(".colHeader" + menuItem.URI,"","", ".optionsHeader")));
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
        switch (menuItem.URI){
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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,progress,0,0,menuItem.URI);}
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it again for green
        seekerGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,0,progress,0,menuItem.URI);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for blue
        seekerBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,0,0,progress,menuItem.URI);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for Alpha
        seekerAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(progress +25, 0,0,0,menuItem.URI);}
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
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Save and close", 0, 0, menuItem));
        //if the user decides to cancel, this is where we use the value of the color before changes that we stored earlier.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Close without saving", 22, currentCol, menuItem));
    }



    public static void cancelColorSelect(int oldColor, AppDetail menuItem){
        //Instead of making a new case, it's easier to compensate for the cancel button by modifying this call
        switch (menuItem.internalCommand){
            case "Font":{EternalMediaBar.savedData.fontCol = oldColor;break;}
            case "Icon":{EternalMediaBar.savedData.iconCol = oldColor;break;}
            case "Menu":{EternalMediaBar.savedData.menuCol = oldColor;break;}
            case "App Backgrounds":{EternalMediaBar.savedData.dimCol = oldColor;break;}
        }
        menuClose();
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
