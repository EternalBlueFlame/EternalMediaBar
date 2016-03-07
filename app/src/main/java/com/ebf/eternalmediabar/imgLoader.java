package com.ebf.eternalmediabar;

import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;

public class imgLoader extends AsyncTask<imgLoader, Integer, Drawable>{

    public EternalMediaBar eternalMediaBar;
    public String ico;
    public PackageManager manager;
    public Boolean isInternal;


    public imgLoader(EternalMediaBar eternalMediaBar, String ico, PackageManager manager, Boolean isInternal){
        this.eternalMediaBar = eternalMediaBar;
        this.ico = ico;
        this.manager = manager;
        this.isInternal = isInternal;
    }


    @Override
    protected Drawable doInBackground(imgLoader... params) {
        //check if the icon is supposed to be internal or not
        if(isInternal) {
            //run a switch to load an icon dependant on it's value.
            switch (ico) {
                case ".colHeader": {
                    return new ColorDrawable(eternalMediaBar.savedData.fontCol);
                }
                case ".radioCheck":{
                    return eternalMediaBar.svgLoad(R.drawable.ic_radio_button_checked_white_24dp);
                }
                case ".radioUnCheck":{
                    return eternalMediaBar.svgLoad(R.drawable.ic_radio_button_unchecked_white_24dp);
                }
                case "0": {
                    return ContextCompat.getDrawable(eternalMediaBar, R.drawable.blank);
                }
                case "1": {
                    return ContextCompat.getDrawable(eternalMediaBar, R.drawable.social_144px);
                }
                case "2": {
                    return ContextCompat.getDrawable(eternalMediaBar, R.drawable.media_144px);
                }
                case "3": {
                    return eternalMediaBar.svgLoad(R.drawable.games_144px);
                }
                case "4": {
                    return eternalMediaBar.svgLoad(R.drawable.web_144px);
                }
                case "5": {
                    return eternalMediaBar.svgLoad(R.drawable.extras_144px);
                }
                case "6": {
                    return eternalMediaBar.svgLoad(R.drawable.settings_144px);
                }
                case "7": {
                    return eternalMediaBar.svgLoad(R.drawable.new_install_144px);
                }
                default: {
                    //use this for loading the application's icon
                    try {
                        return manager.getApplicationIcon(ico);
                    } catch (Exception e) {
                        if (ico.equals(".options")) {
                            return eternalMediaBar.svgLoad(R.drawable.sub_settings_144px);
                        } else {
                            return eternalMediaBar.svgLoad(R.drawable.error_144px);
                        }
                    }
                }
            }
        }
        else{
            //run it on a switch
            switch (ico){
                //case 0 for no icon.
                case "0":{
                    return ContextCompat.getDrawable(eternalMediaBar, R.drawable.blank);
                }
                //each case tries to load an icon from an app, if it fails, it falls back to internal icon.
                case "1":{
                    try{return manager.getApplicationIcon("com.android.contacts");}
                    //if it fails, fallback to the built-in Social icon.
                    catch (Exception e){return ContextCompat.getDrawable(eternalMediaBar, R.drawable.social_144px);}
                }
                case "2":{
                    //this icon is composed of two different icons, so we make them as a list of drawables.
                    Drawable[] layers = new Drawable[2];
                    //load the base icon
                    try {layers[0] = manager.getApplicationIcon("com.google.android.videos");}
                    catch (Exception e) {}
                    //load the other icon,
                    try {
                        layers[1] = new ScaleDrawable(manager.getApplicationIcon("com.google.android.music"), Gravity.CENTER, 1f, 1f);
                        //now change the scale of it by changing the level
                        layers[1].setLevel(7000);
                    }
                    catch (Exception e) {}
                    //if the process didn't fail, load the list of icons and draw them as a Layered Drawable.
                    if (layers != new Drawable[2]) {
                        try {
                            return new LayerDrawable(layers);
                        } catch (Exception e) {
                            return eternalMediaBar.svgLoad(R.drawable.media_144px);
                        }
                    }
                    else {
                        return eternalMediaBar.svgLoad(R.drawable.media_144px);
                    }
                }
                case "3": {
                    try {
                        return manager.getApplicationIcon("com.google.android.play.games");
                    } catch (Exception e) {
                        return eternalMediaBar.svgLoad(R.drawable.games_144px);
                    }
                }
                case "4":{
                    try {
                        return manager.getApplicationIcon("com.android.chrome");
                    } catch (Exception e) {
                        return eternalMediaBar.svgLoad(R.drawable.web_144px);
                    }
                }
                case "5": {
                    try {
                        return manager.getApplicationIcon("com.google.android.apps.docs");
                    } catch (Exception e) {
                        eternalMediaBar.svgLoad(R.drawable.extras_144px);
                    }
                }
                case "6":{
                    try {
                        return manager.getApplicationIcon("com.android.settings");
                    } catch (Exception e) {
                        return eternalMediaBar.svgLoad(R.drawable.settings_144px);
                    }
                }
                case "7": {
                    return eternalMediaBar.svgLoad(R.drawable.new_install_144px);
                }
                default:{
                    return ContextCompat.getDrawable(eternalMediaBar, R.drawable.blank);
                }
            }
        }
    }
}

