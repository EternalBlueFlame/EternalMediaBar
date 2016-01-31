package com.ebf.eternalmediabar;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

//This class is intended to manage a large number of functions related to menu interactions,
public class onItemSelected  {

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
    public void loadAppOptionsMenu(EternalMediaBar eternalMediaBar, final int index, final String launchIntent, final String appname, LinearLayout Llayout){

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
    public void createCopyList(EternalMediaBar eternalMediaBar, LinearLayout Llayout, final String launchIntent, final String appname){
        eternalMediaBar.optionVitem=1;
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


}
