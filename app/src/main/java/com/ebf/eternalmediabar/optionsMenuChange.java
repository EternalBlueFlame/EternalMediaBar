package com.ebf.eternalmediabar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;


//This class is intended to manage a large number of functions related to menu interactions,
public class optionsMenuChange {

    //////////////////////////////////////////////////
    /////////////////Open the menu////////////////////
    //////////////////////////////////////////////////
    public void menuOpen(final EternalMediaBar eternalMediaBar, final int index, final boolean islaunchable, final String launchIntent, final String appname, LinearLayout Llayout){
        //set the variables for the menu
        eternalMediaBar.optionsMenu = true;
        eternalMediaBar.optionVitem = 1;
        //load the layout and make sure nothing is in it.
        ScrollView Slayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
        Llayout.removeAllViews();
        //animate the menu opening
        TranslateAnimation anim;
        if (!eternalMediaBar.savedData.mirrorMode) {
            //reset the position
            Slayout.setX(eternalMediaBar.getResources().getDisplayMetrics().widthPixels);
            anim = new TranslateAnimation(0, -(145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
        else{
            //reset the position
            Slayout.setX(-145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f);
            anim = new TranslateAnimation(0, (145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        }
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
                ScrollView Slayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
                // clear animation to prevent flicker
                Slayout.clearAnimation();
                //manually set position of menu
                if (!eternalMediaBar.savedData.mirrorMode) {
                    Slayout.setX(eternalMediaBar.getResources().getDisplayMetrics().widthPixels - (145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f));
                } else {
                    Slayout.setX(0);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        if (islaunchable) {
            loadAppOptionsMenu(eternalMediaBar, index, launchIntent, appname, Llayout);
            eternalMediaBar.optionVitem=1;
        }
        else{
            loadMainOptionsItems(eternalMediaBar, Llayout);
            eternalMediaBar.optionVitem=1;
        }


        //close settings menu, we put this here since it's on the menu without exception.
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Exit Options", eternalMediaBar.svgLoad(R.drawable.blank), 0, 0, false, launchIntent, appname));
    }

    //////////////////////////////////////////////////
    /////////////////Close the menu///////////////////
    //////////////////////////////////////////////////
    public void menuClose(final EternalMediaBar eternalMediaBar, LinearLayout Llayout) {
        //load the layouts
        ScrollView Slayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
        //empty the one that has content
        Llayout.removeAllViews();
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
        Slayout.setAnimation(anim);
        //now move the menu itself
        Slayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                ScrollView Slayout = (ScrollView) eternalMediaBar.findViewById(R.id.options_displayscroll);
                // clear animation to prevent flicker
                Slayout.clearAnimation();
                //manually set position of menu off screen
                if (!eternalMediaBar.savedData.mirrorMode) {
                    Slayout.setX(eternalMediaBar.getResources().getDisplayMetrics().widthPixels);
                }
                else{
                    Slayout.setX(-145 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        //save any changes
        eternalMediaBar.savefiles();
    }

    //////////////////////////////////////////////////
    /////Load the settings menu for a selected app////
    //////////////////////////////////////////////////
    public void loadAppOptionsMenu(EternalMediaBar eternalMediaBar, int index, String launchIntent, String appname, LinearLayout Llayout){
        eternalMediaBar.optionVitem = 1;

        //add the app that's selected so the user knows for sure what they are messing with.
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, ""));


        //add all the extra options

        //copy the item to another category
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Copy to...", eternalMediaBar.svgLoad(R.drawable.blank), 1, 0, false, launchIntent, appname));

        //move the item to another category
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Move to...", eternalMediaBar.svgLoad(R.drawable.blank), 2, 0, false, launchIntent, appname));

        //first option is to remove an item from the list.
        //in RC2 this will be modified to support hiding the icon even when it's only in one menu
        int i=0;
        for (int ii=0; ii< eternalMediaBar.savedData.vLists.size();){
            for (int iii=0; iii< eternalMediaBar.savedData.vLists.get(ii).size();){
                if (eternalMediaBar.savedData.vLists.get(ii).get(iii).name.equals(eternalMediaBar.savedData.vLists.get(eternalMediaBar.hitem).get(eternalMediaBar.vitem).name)){
                    i++;
                }
                iii++;
            }
            ii++;
        }
        if (i>1) {
            Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Remove From This List", eternalMediaBar.svgLoad(R.drawable.blank), 5, 0, false, launchIntent, "4"));
        }

        //open the app's settings
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Application Settings", eternalMediaBar.svgLoad(R.drawable.blank), 6, 0, false, launchIntent, appname));

    }

    //////////////////////////////////////////////////
    /////Load the settings menu for customization/////
    //////////////////////////////////////////////////
    public void loadMainOptionsItems(EternalMediaBar eternalMediaBar, LinearLayout Llayout){
        //add the item for changing whether or not to use Google icons.
        eternalMediaBar.optionVitem = 0;
        if (eternalMediaBar.savedData.useGoogleIcons){
            Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Don't use Google Icons", eternalMediaBar.svgLoad(R.drawable.blank), 11, 0, false, ".", "."));
        }
        else{
            Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Use Google Icons", eternalMediaBar.svgLoad(R.drawable.blank), 12, 0, false, ".", "."));
        }

        //add the item for mirroring the UI
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Mirror Layout", eternalMediaBar.svgLoad(R.drawable.blank), 13, 0, false, ".", "."));

        //add the item for changing the font color
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Change Font Color", eternalMediaBar.svgLoad(R.drawable.blank), 14, 0, false, ".", "."));
    }

    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public void createCopyList(EternalMediaBar eternalMediaBar, LinearLayout Llayout, String launchIntent, String appname){
        eternalMediaBar.optionVitem=0;
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, ""));

        for (int i=0; i < eternalMediaBar.savedData.vLists.size()-1; ) {
            if (i != eternalMediaBar.hitem) {
                Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Copy to " + eternalMediaBar.hli.get(i).label, eternalMediaBar.svgLoad(R.drawable.blank), 3, i, false, ".", "3"));
            }
            i++;
        }
        //return to first settings menu
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Go Back", eternalMediaBar.svgLoad(R.drawable.blank), 8, 0, false, launchIntent, appname));
        //close settings menu
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Exit Options", eternalMediaBar.svgLoad(R.drawable.blank), 0, 0, false, launchIntent, appname));
    }

    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void createMoveList(EternalMediaBar eternalMediaBar, LinearLayout Llayout, String launchIntent, String appname){
        eternalMediaBar.optionVitem = 0;
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, appname, null, 7, 0, false, launchIntent, ""));

        for (int i=0; i < eternalMediaBar.savedData.vLists.size()-1; ) {
            if (i != eternalMediaBar.hitem) {
                Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Move to " + eternalMediaBar.hli.get(i).label, eternalMediaBar.svgLoad(R.drawable.blank), 4, i, false, ".", ""));
            }
            i++;
        }
        // !!! ENABLE AFTER FIXED !!!
                        /*/Automatically get the category for this item from google play
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
                        }/*/
        //return to first settings menu
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Go Back", eternalMediaBar.svgLoad(R.drawable.blank), 8, 0, false, launchIntent, appname));
        //close settings menu
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Exit Options", eternalMediaBar.svgLoad(R.drawable.blank), 0, 0, false, launchIntent, appname));
    }

    //////////////////////////////////////////////////
    ////////////////Color select menu/////////////////
    //////////////////////////////////////////////////
    public void colorSelect(final EternalMediaBar eternalMediaBar, LinearLayout Llayout, int secondaryIndex){
        //Instead of making a new case, it's easier to compensate for the cancel button by modifying this call
        if (secondaryIndex!=0){
            eternalMediaBar.savedData.fontCol = secondaryIndex;
            menuClose(eternalMediaBar, Llayout);
            return;
        }
        Llayout = (LinearLayout) eternalMediaBar.findViewById(R.id.optionslist);
        Llayout.removeAllViews();
        //load the header that contains the current color
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_header, "Choose Font Color", new ColorDrawable(eternalMediaBar.savedData.fontCol), 0, 0, false, ".", "."));
        //create the inflater for the seeker bars
        View child = eternalMediaBar.getLayoutInflater().inflate(R.layout.color_select, null);
        //get the red seeker bar, then set it's progress
        SeekBar seekerRed = (SeekBar) child.findViewById(R.id.redSeek);
        seekerRed.setProgress(Color.red(eternalMediaBar.savedData.fontCol));
        //lastly change the listener
        seekerRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the bar is moved, change the value of the font color, then update the image accordingly.
                eternalMediaBar.savedData.fontCol = Color.argb(255, progress, Color.green(eternalMediaBar.savedData.fontCol), Color.blue(eternalMediaBar.savedData.fontCol));
                ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.fontCol));
            }

            //these are useless, but we need them to exist
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //Now we do it again for green
        SeekBar seekerGreen = (SeekBar) child.findViewById(R.id.greenSeek);
        seekerGreen.setProgress(Color.green(eternalMediaBar.savedData.fontCol));
        //lastly change the listener
        seekerGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the bar is moved, change the value of the font color, then update the image accordingly.
                eternalMediaBar.savedData.fontCol = Color.argb(255, Color.red(eternalMediaBar.savedData.fontCol), progress, Color.blue(eternalMediaBar.savedData.fontCol));
                ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.fontCol));
            }

            //these are useless, but we need them to exist
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //Now we do it one more time for blue
        SeekBar seekerBlue = (SeekBar) child.findViewById(R.id.blueSeek);
        seekerBlue.setProgress(Color.blue(eternalMediaBar.savedData.fontCol));
        //lastly change the listener
        seekerBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the bar is moved, change the value of the font color, then update the image accordingly.
                eternalMediaBar.savedData.fontCol = Color.argb(255,  Color.red(eternalMediaBar.savedData.fontCol), Color.green(eternalMediaBar.savedData.fontCol), progress);
                ((ImageView) ((LinearLayout) eternalMediaBar.findViewById(R.id.optionslist)).getChildAt(0).findViewById(R.id.item_app_icon)).setImageDrawable(new ColorDrawable(eternalMediaBar.savedData.fontCol));
            }
            //these are useless, but we need them to exist
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        //and finally add the view
        Llayout.addView(child);
        //add the item for save and quit
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Save and close", eternalMediaBar.svgLoad(R.drawable.blank), 0, 0, false, ".", "."));

        //add the item for cancel changes
        Llayout.addView(eternalMediaBar.createMenuEntry(R.layout.options_item, "Close without saving", eternalMediaBar.svgLoad(R.drawable.blank), 14, eternalMediaBar.savedData.fontCol, false, ".", "."));
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////Functionality///////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////


    //////////////////////////////////////////////////
    //////////////Copy an app menu item///////////////
    //////////////////////////////////////////////////
    public void copyItem(EternalMediaBar eternalMediaBar, int secondaryIndex, LinearLayout Llayout){
        eternalMediaBar.savedData.vLists.get(secondaryIndex).add(eternalMediaBar.savedData.vLists.get(eternalMediaBar.hitem).get(eternalMediaBar.vitem));
        menuClose(eternalMediaBar, Llayout);
    }

    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void moveItem(EternalMediaBar eternalMediaBar, int secondaryIndex, LinearLayout Llayout){
        eternalMediaBar.savedData.vLists.get(secondaryIndex).add(eternalMediaBar.savedData.vLists.get(eternalMediaBar.hitem).get(eternalMediaBar.vitem));
        eternalMediaBar.savedData.vLists.get(eternalMediaBar.hitem).remove(eternalMediaBar.vitem);
        menuClose(eternalMediaBar, Llayout);
        //make sure that if the new apps list disappears, we aren't on it.
        if (eternalMediaBar.hitem == (eternalMediaBar.savedData.vLists.size()-1) && eternalMediaBar.savedData.vLists.get(eternalMediaBar.savedData.vLists.size()-1).size()==0){
            eternalMediaBar.listmove(0, true);
        }
        else{
            eternalMediaBar.loadListView();
        }
    }

    /////////////////NOT IMPLEMENTED//////////////////
    //////////////////////////////////////////////////
    //////////////Move an app menu item///////////////
    //////////////////////////////////////////////////
    public void hideApp(EternalMediaBar eternalMediaBar, LinearLayout Llayout){
        menuClose(eternalMediaBar, Llayout);
        eternalMediaBar.savedData.hiddenApps.add(eternalMediaBar.savedData.vLists.get(eternalMediaBar.hitem).get(eternalMediaBar.vitem));
        eternalMediaBar.savedData.vLists.get(eternalMediaBar.hitem).remove(eternalMediaBar.vitem);
        eternalMediaBar.loadListView();
    }

    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public Intent openAppSettings(EternalMediaBar eternalMediaBar, LinearLayout Llayout, String launchIntent){

        //open application settings
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(launchIntent)));
        menuClose(eternalMediaBar, Llayout);
        return intent;
    }

    //////////////////////////////////////////////////
    ////////////////////Mirror UI/////////////////////
    //////////////////////////////////////////////////
    public void mirrorUI(EternalMediaBar eternalMediaBar, LinearLayout Llayout){
        if (eternalMediaBar.savedData.mirrorMode){
            eternalMediaBar.savedData.mirrorMode=false;
        }
        else{
            eternalMediaBar.savedData.mirrorMode=true;
        }
        menuClose(eternalMediaBar, Llayout);
    }

    //////////////////////////////////////////////////
    ///////////Enable/Disable Google Icons////////////
    //////////////////////////////////////////////////
    public void toggleGoogleIcons(EternalMediaBar eternalMediaBar, LinearLayout Llayout){
        if (eternalMediaBar.savedData.useGoogleIcons){
            eternalMediaBar.savedData.useGoogleIcons=false;
        }
        else{
            eternalMediaBar.savedData.useGoogleIcons=true;
        }
        menuClose(eternalMediaBar, Llayout);
    }
}
