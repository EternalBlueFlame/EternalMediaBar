package com.ebf.eternalmediabar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
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
    public static void menuOpen(AppDetail item, int type){

        //just in case the user happened to hit "go back" on the copy or move page.
        if (EternalMediaBar.copyingOrMoving){
            EternalMediaBar.copyingOrMoving = false;
            //clear selected apps
            for (String uri : EternalMediaBar.selectedApps){
                for (int i=0; i<EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.size();){
                    if (uri.equals(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).URI)) {
                        EternalMediaBar.appsLayout.getChildAt(i).findViewById(R.id.list_item_checkbox).setVisibility(View.INVISIBLE);
                        break;
                    }
                    i++;
                }
            }
            EternalMediaBar.selectedApps.clear();
        }

        //run the animation
        ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
        sLayout.setBackgroundColor(EternalMediaBar.savedData.menuCol);
        EternalMediaBar.optionsLayout.removeAllViews();
        //animate the menu opening
        TranslateAnimation anim;
        if (!EternalMediaBar.savedData.mirrorMode) {
            //reset the position
            sLayout.setX(EternalMediaBar.activity.getResources().getDisplayMetrics().widthPixels);
            anim = new TranslateAnimation(0, -(144 * EternalMediaBar.dpi.scaledDensity), 0, 0);
        } else{
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


        switch(type){
            case R.id.APP:{
                loadAppOptionsMenu(item);
                break;
            }
            case R.id.CATEGORY:{

                break;
            }
            case R.id.WIDGET:{
                modifyWidget();
                break;
            }
            case R.id.SETTINGS:{
                loadMainOptionsItems();
                break;
            }
        }

        EternalMediaBar.optionsMenu = true;
        EternalMediaBar.optionVitem =1;

        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //close settings menu, we put this here since it's on the menu without exception.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Exit Options", R.id.CLOSE, 0, item));

    }


    //////////////////////////////////////////////////
    /////////////////Close the menu///////////////////
    //////////////////////////////////////////////////
    public static void menuClose(boolean reload) {
        //load the layouts
        ScrollView sLayout = (ScrollView) EternalMediaBar.activity.findViewById(R.id.options_displayscroll);
        //empty the one that has content
        EternalMediaBar.copyingOrMoving = false;
        EternalMediaBar.optionsLayout.removeAllViews();
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
                    EternalMediaBar.appsLayout.getChildAt(EternalMediaBar.vItem).findViewById(R.id.list_item_checkbox).setVisibility(View.INVISIBLE);
                    break;
                }
                i++;
            }
        }
        EternalMediaBar.selectedApps.clear();

        if (reload) {
            //save any changes and reload the view
            EternalMediaBar.savedData.writeXML(EternalMediaBar.activity);
            EternalMediaBar.activity.loadListView();
        }
    }


    //////////////////////////////////////////////////
    /////Load the settings menu for a selected app////
    //////////////////////////////////////////////////
    public static void loadAppOptionsMenu(AppDetail menuItem){
        EternalMediaBar.optionVitem = 1;

        //add the app that's selected so the user knows for sure what they are messing with.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(menuItem.label, R.id.NULL, 0, new AppDetail(menuItem.label,menuItem.URI, ".optionsHeader")));

        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Copy to...", R.id.COPY_LIST, 0, menuItem));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Move to...", R.id.MOVE_LIST, 1, menuItem));

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
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Remove this item", R.id.ACTION_REMOVE, 0, menuItem));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Hide this item", R.id.ACTION_HIDE, 0, menuItem));
            //later this will be modified to support hiding the icon when it's only in one list.
        }
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Application Settings", R.id.ACTION_APP_SYSTEM_SETTINGS, 0, menuItem));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Reorganize This Category", R.id.ORGANIZE_MENU, 0, menuItem));

    }


    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public static void loadMainOptionsItems(){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose Theme", R.id.THEME_SELECT, 0, new AppDetail()));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Theme Colors", R.id.COLOR_MENU, 0, new AppDetail()));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Mirror Layout", R.id.ACTION_MIRROR, 0, new AppDetail()));
        if(EternalMediaBar.savedData.doubleTap){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Disable Double Tap", R.id.ACTION_DOUBLE_TAP, 0, new AppDetail()));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Enable Double Tap", R.id.ACTION_DOUBLE_TAP, 0, new AppDetail()));
        }
        if (EternalMediaBar.savedData.hiddenApps.size() >0){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Un-Hide Apps", R.id.UNHIDE_LIST, 0, new AppDetail()));
        }
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Add A Widget", R.id.ACTION_ADD_WIDGET, 0, new AppDetail()));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Homepage", R.id.ACTION_URL, 0, new AppDetail("", "http://github.com/EternalBlueFlame/EternalMediaBar")));

    }


    //////////////////////////////////////////////////
    ///////////Copy/move an app menu item/////////////
    //////////////////////////////////////////////////
    public static void createCopyList(AppDetail menuItem, int action){
        EternalMediaBar.optionVitem=0;
        EternalMediaBar.copyingOrMoving = true;

        EternalMediaBar.appsLayout.getChildAt(EternalMediaBar.vItem).findViewById(R.id.list_item_checkbox).setVisibility(View.VISIBLE);
        EternalMediaBar.selectedApps.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(EternalMediaBar.vItem).URI);

        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(menuItem.label, R.id.NULL, 0, new AppDetail(menuItem.label,menuItem.URI, ".optionsHeader")));
        //add the options for copying the menu item, skip the one for the current menu
        for (int i=0; i < EternalMediaBar.savedData.categories.size()-1; ) {
            if (i != EternalMediaBar.hItem) {
                if (action == R.id.MOVE_LIST){
                        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Move to " + EternalMediaBar.savedData.categories.get(i).categoryName, R.id.ACTION_MOVE, i, menuItem));
                } else{
                    EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Copy to " + EternalMediaBar.savedData.categories.get(i).categoryName, R.id.ACTION_COPY, i, menuItem));
                }
            }
            i++;
        }
        goBackItems(menuItem, R.id.APP, false);
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for theme change//////
    //////////////////////////////////////////////////
    public static void themeChange(AppDetail menuItem){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        //add the items for changing the theme
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Lunar\nDefault", R.id.ACTION_THEME_CHANGE, 0, new AppDetail("","Internal", radioCheck(EternalMediaBar.savedData.theme.equals("Internal")))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Lunar\nInverse", R.id.ACTION_THEME_CHANGE, 0, new AppDetail("", "LunarInverse", radioCheck(EternalMediaBar.savedData.theme.equals("LunarInverse")))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Google", R.id.ACTION_THEME_CHANGE, 0, new AppDetail("", "Google", radioCheck(EternalMediaBar.savedData.theme.equals("Google")))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Material", R.id.ACTION_THEME_CHANGE, 0, new AppDetail("", "Material", radioCheck(EternalMediaBar.savedData.theme.equals("Material")))));

        goBackItems(menuItem,R.id.SETTINGS, false);
    }

    //////////////////////////////////////////////////
    ///Load the settings menu for theme color change//
    //////////////////////////////////////////////////
    public static void themeColorChange(AppDetail menuItem){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        //add the items for changing the theme colors
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Font Color", R.id.COLOR_FONT, 0, new AppDetail("", "Font", "")));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Icon Color", R.id.COLOR_ICON, 0, new AppDetail("", "Icon", "")));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change Menu Color", R.id.COLOR_OPTIONS, 0, new AppDetail("", "Menu", "")));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Change List Color", R.id.COLOR_APP_BG, 0, new AppDetail("", "App Backgrounds", "")));

        goBackItems(menuItem, R.id.SETTINGS, true);
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public static void listOrganizeSelect(AppDetail menuItem){
        //add the items for changing the organization method
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Alphabetically", R.id.ACTION_ORGANIZE, 1, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 1))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Reverse Alphabetically", R.id.ACTION_ORGANIZE, 2, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 2))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("No Organization", R.id.ACTION_ORGANIZE, 0, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 0))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Newest", R.id.ACTION_ORGANIZE, 3, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 3))));
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Oldest", R.id.ACTION_ORGANIZE, 4, menuItem.setCommand(radioCheck(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode == 4))));

        goBackItems(menuItem, R.id.APP, false);
    }


    //////////////////////////////////////////////////
    ///////////////Modify Widget Menu/////////////////
    //////////////////////////////////////////////////
    public static void modifyWidget(){
        EternalMediaBar.optionVitem = 0;

        EternalMediaBar.optionsLayout.removeAllViews();

        final View child = EternalMediaBar.activity.getLayoutInflater().inflate(R.layout.widget_edit, null);
        ((EditText)child.findViewById(R.id.number_left)).setText(EternalMediaBar.editingWidget.X);
        ((EditText)child.findViewById(R.id.number_top)).setText(EternalMediaBar.editingWidget.Y);
        ((EditText)child.findViewById(R.id.number_height)).setText(EternalMediaBar.editingWidget.height);
        ((EditText)child.findViewById(R.id.number_width)).setText(EternalMediaBar.editingWidget.width);


        child.findViewById(R.id.left_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) child.findViewById(R.id.number_left)).setText(EternalMediaBar.editingWidget.X++);
            }
        });
        child.findViewById(R.id.left_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) child.findViewById(R.id.number_left)).setText(EternalMediaBar.editingWidget.X--);
            }
        });
        child.findViewById(R.id.top_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) child.findViewById(R.id.number_top)).setText(EternalMediaBar.editingWidget.Y++);
            }
        });
        child.findViewById(R.id.top_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)child.findViewById(R.id.number_top)).setText(EternalMediaBar.editingWidget.Y--);
            }
        });
        child.findViewById(R.id.width_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) child.findViewById(R.id.number_width)).setText(EternalMediaBar.editingWidget.width++);
            }
        });
        child.findViewById(R.id.width_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)child.findViewById(R.id.number_width)).setText(EternalMediaBar.editingWidget.width--);
            }
        });
        child.findViewById(R.id.height_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) child.findViewById(R.id.number_height)).setText(EternalMediaBar.editingWidget.height++);
            }
        });
        child.findViewById(R.id.height_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)child.findViewById(R.id.number_height)).setText(EternalMediaBar.editingWidget.height--);
            }
        });

        ((EditText)child.findViewById(R.id.number_left)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    //we attempt to parse the given int
                    EternalMediaBar.editingWidget.X = Integer.parseInt(v.getText().toString());
                } catch (Exception e) {
                }
                return false;
            }
        });
        //TODO repeat this for the rest of the occurrences

        EternalMediaBar.optionsLayout.addView(child);
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

    private static void goBackItems(AppDetail menuItem, int type, boolean saveOnExit){
        //add an empty space
        Space spacer = new Space(EternalMediaBar.activity);
        spacer.setMinimumHeight(Math.round(50 * (EternalMediaBar.dpi.scaledDensity)));
        EternalMediaBar.optionsLayout.addView(spacer);
        //add go back buttons

        switch (type){
            case R.id.APP: case R.id.SETTINGS:{
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Go Back", type, 0, menuItem));
                break;
            }
        }
        if (saveOnExit){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Exit Options", R.id.CLOSE_AND_SAVE, 0, menuItem));
        } else {
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Exit Options", R.id.CLOSE, 0, menuItem));
        }
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Functionality///////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////


    public static void unhideList(AppDetail menuItem){

        EternalMediaBar.optionsLayout.removeAllViews();

        for (int i =0; i<EternalMediaBar.savedData.hiddenApps.size();){
            EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView(EternalMediaBar.savedData.hiddenApps.get(i).label,R.id.ACTION_UNHIDE, i, new AppDetail()));
            i++;
        }

        goBackItems(menuItem, R.id.SETTINGS, false);
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Color Selector//////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    public static void colorSelect(final int type){
        EternalMediaBar.optionsLayout.removeAllViews();
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
        switch (type){
            case R.id.COLOR_FONT:{currentCol=EternalMediaBar.savedData.fontCol;
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose Font Color", R.id.NULL, 0, new AppDetail(".colHeaderFont","", ".optionsHeader")));break;}
            case R.id.COLOR_ICON:{currentCol=EternalMediaBar.savedData.iconCol;
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose Icon Color", R.id.NULL, 0, new AppDetail(".colHeaderIcon","", ".optionsHeader")));break;}
            case R.id.COLOR_OPTIONS:{currentCol=EternalMediaBar.savedData.menuCol;
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose Menu Color", R.id.NULL, 0, new AppDetail(".colHeaderMenu","", ".optionsHeader")));break;}
            case R.id.COLOR_APP_BG:{currentCol=EternalMediaBar.savedData.dimCol;
                EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Choose App Background Color", R.id.NULL, 0, new AppDetail(".colHeaderApp" + type,"", ".optionsHeader")));break;}
        }
        seekerRed.setProgress(Color.red(currentCol));
        seekerGreen.setProgress(Color.green(currentCol));
        seekerBlue.setProgress(Color.blue(currentCol));
        seekerAlpha.setProgress(Color.alpha(currentCol) - 25);
        hexText.setText("#" + Integer.toHexString(currentCol));
        ((ImageView) EternalMediaBar.optionsLayout.getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(currentCol));




        //change the listener for the Red seek bar
        seekerRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,progress,0,0,type);}
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it again for green
        seekerGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,0,progress,0,type);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for blue
        seekerBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(0,0,0,progress,type);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Now we do it one more time for Alpha
        seekerAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {scrollSetColor(progress +25, 0,0,0,type);}
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
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Save and close", R.id.CLOSE_AND_SAVE, 0, new AppDetail()));
        //if the user decides to cancel, this is where we use the value of the color before changes that we stored earlier.
        EternalMediaBar.optionsLayout.addView(ListItemLayout.optionsListItemView("Close without saving", R.id.COLOR_ACTION_CANCEL, currentCol, new AppDetail()));
    }



    public static void cancelColorSelect(int oldColor, AppDetail menuItem){
        //Instead of making a new case, it's easier to compensate for the cancel button by modifying this call
        switch (menuItem.internalCommand){
            case "Font":{EternalMediaBar.savedData.fontCol = oldColor;break;}
            case "Icon":{EternalMediaBar.savedData.iconCol = oldColor;break;}
            case "Menu":{EternalMediaBar.savedData.menuCol = oldColor;break;}
            case "App Backgrounds":{EternalMediaBar.savedData.dimCol = oldColor;break;}
        }
        menuClose(false);
    }




    public static void scrollSetColor(int a, int r, int g, int b, int type){
        switch (type){
            case R.id.COLOR_FONT:{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.fontCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.fontCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.fontCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.fontCol);}
                break;
            }
            case R.id.COLOR_ICON:{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.iconCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.iconCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.iconCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.iconCol);}
                break;
            }
            case R.id.COLOR_OPTIONS:{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.menuCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.menuCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.menuCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.menuCol);}
                break;
            }
            case R.id.COLOR_APP_BG:{
                if (a==0){a=Color.alpha(EternalMediaBar.savedData.dimCol);}
                if (r==0){r=Color.red(EternalMediaBar.savedData.dimCol);}
                if (g==0){g=Color.green(EternalMediaBar.savedData.dimCol);}
                if (b==0){b=Color.blue(EternalMediaBar.savedData.dimCol);}
                break;
            }
        }
        EternalMediaBar.savedData.dimCol = Color.argb(a, r, g, b);
        ((ImageView) EternalMediaBar.optionsLayout.getChildAt(0).findViewById(R.id.list_item_icon)).setImageDrawable(new ColorDrawable(EternalMediaBar.savedData.fontCol));
        //changing the hex box will automatically change the scroll bars accordingly.
        ((EditText) (EternalMediaBar.activity.findViewById(R.id.optionslist)).findViewById(R.id.hexText)).setText("#" + Integer.toHexString(EternalMediaBar.savedData.fontCol));
    }

}
