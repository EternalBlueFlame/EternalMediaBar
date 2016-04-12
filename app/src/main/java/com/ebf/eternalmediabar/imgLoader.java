package com.ebf.eternalmediabar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.AsyncTask;
import android.view.Gravity;


public class imgLoader extends AsyncTask<imgLoader, Integer, Bitmap>{

    public String ico;
    public Boolean isInternal;


    public imgLoader(String ico, Boolean isInternal){
        this.ico = ico;
        this.isInternal = isInternal;
    }


    @Override
    protected Bitmap doInBackground(imgLoader... params) {
        //check if the icon is supposed to be internal or not
        if(!isInternal) {
            //run a switch to load an icon dependant on it's value.
            switch (ico) {
                case ".colHeaderFont": {
                    return Bitmap.createBitmap(new int[]{EternalMediaBar.activity.savedData.fontCol},1,1, Bitmap.Config.ARGB_8888);
                }
                case ".radioCheck":{
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.ic_radio_button_checked_white_24dp);
                }
                case ".radioUnCheck":{
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.ic_radio_button_unchecked_white_24dp);
                }
                case "0": {
                    return Bitmap.createBitmap(new int[]{-1},1,1, Bitmap.Config.ALPHA_8);
                }
                case "1": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.social_144px);
                }
                case "2": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.media_144px);
                }
                case "3": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.games_144px);
                }
                case "4": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.web_144px);
                }
                case "5": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.extras_144px);
                }
                case "6": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.settings_144px);
                }
                case "7": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.new_install_144px);
                }
                default: {
                    //use this for loading the application's icon
                    try {
                        return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon(ico)).getBitmap();
                    } catch (Exception e) {
                        if (ico.equals(".options")) {
                            return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.sub_settings_144px);
                        } else {
                            return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.error_144px);
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
                    return Bitmap.createBitmap(new int[]{0x00000000}, 1, 1, Bitmap.Config.ARGB_8888);
                }
                //each case tries to load an icon from an app, if it fails, it falls back to internal icon.
                case "1":{
                    try{return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon("com.android.contacts")).getBitmap();}
                    //if it fails, fallback to the built-in Social icon.
                    catch (Exception e){return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.social_144px);}
                }
                case "2":{
                    //try to return the google play movies icon.if that's not available, try to get the music icon. if all else fails, get the internal one.
                    try{
                        return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon("com.google.android.videos")).getBitmap();
                    }catch (Exception e){
                        try{
                            return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon("com.google.android.music")).getBitmap();
                        }catch (Exception ee){
                            return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.media_144px);
                        }
                    }
                }
                case "3": {
                    try {
                        return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon("com.google.android.play.games")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.games_144px);
                    }
                }
                case "4":{
                    try {
                        return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon("com.android.chrome")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.web_144px);
                    }
                }
                case "5": {
                    try {
                        return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon("com.google.android.apps.docs")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.extras_144px);
                    }
                }
                case "6":{
                    try {
                        return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon("com.android.settings")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.settings_144px);
                    }
                }
                case "7": {
                    return BitmapFactory.decodeResource(EternalMediaBar.activity.getResources(),R.drawable.new_install_144px);
                }
                default:{
                    return Bitmap.createBitmap(new int[]{-1}, 1, 1, Bitmap.Config.ALPHA_8);
                }
            }
        }
    }
}

