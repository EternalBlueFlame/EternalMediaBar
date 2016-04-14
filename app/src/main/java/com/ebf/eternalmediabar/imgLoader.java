package com.ebf.eternalmediabar;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;


public class imgLoader extends AsyncTask<imgLoader, Integer, Bitmap>{

    public String ico;


    public imgLoader(String ico){
        this.ico = ico;
    }


    @Override
    protected Bitmap doInBackground(imgLoader... params) {
        //run a switch to load an icon dependant on it's value.
        switch (ico) {
            case ".colHeaderFont": {
                return Bitmap.createBitmap(new int[]{EternalMediaBar.activity.savedData.fontCol},1,1, Bitmap.Config.ARGB_8888);
            }
            case ".radioCheck":{
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    default:{return getBitmap(true, "", R.drawable.ic_radio_button_checked_white_24dp);}
                }
            }
            case ".radioUnCheck":{
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    default:{return getBitmap(true, "", R.drawable.ic_radio_button_unchecked_white_24dp);}
                }
            }
            case "0": {
                return Bitmap.createBitmap(new int[]{-1},1,1, Bitmap.Config.ALPHA_8);
            }
            case "1": {
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    case "Google":{return getBitmap(false, "com.android.contacts", R.drawable.social_144px);}
                    default:{return getBitmap(true, "", R.drawable.social_144px);}
                }
            }
            case "2": {
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    case "Google":{return getBitmap(false, "com.google.android.videos", R.drawable.media_144px);}
                    default:{return getBitmap(true, "", R.drawable.media_144px);}
                }
            }
            case "3": {
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    case "Google":{return getBitmap(false, "com.google.android.play.games", R.drawable.games_144px);}
                    default:{return getBitmap(true, "", R.drawable.games_144px);}
                }
            }
            case "4": {
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    case "Google":{return getBitmap(false, "com.android.chrome", R.drawable.web_144px);}
                    default:{return getBitmap(true, "", R.drawable.web_144px);}
                }
            }
            case "5": {
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    case "Google":{return getBitmap(false, "com.google.android.apps.docs", R.drawable.extras_144px);}
                    default:{return getBitmap(true, "", R.drawable.extras_144px);}
                }
            }
            case "6": {
                switch (EternalMediaBar.activity.savedData.theme){
                    //case "material":
                    //no google case for this
                    default:{return getBitmap(true, "", R.drawable.new_install_144px);}
                }
            }
            case".options": {
                switch (EternalMediaBar.activity.savedData.theme) {
                    //case "material":
                    //no google case for this
                    default: {
                        return getBitmap(true, "", R.drawable.sub_settings_144px);
                    }
                }
            }
            default: {
                //try to load app icon, if it fails, get the error icon
                try {
                    return ((BitmapDrawable)EternalMediaBar.activity.manager.getApplicationIcon(ico)).getBitmap();
                } catch (Exception e) {
                    switch (EternalMediaBar.activity.savedData.theme){
                        //case "material":
                        //no google case
                        default:{return getBitmap(true, "", R.drawable.error_144px);}
                    }
                }
            }
        }
    }

    public Bitmap getBitmap(boolean internal, String launchIntent, int internalImage){
        if(!internal) {
            try {
                return ((BitmapDrawable) EternalMediaBar.activity.manager.getApplicationIcon(launchIntent)).getBitmap();
            } catch (Exception e) {
                //get the drawable and recolor it
                Drawable ico = EternalMediaBar.activity.getResources().getDrawableForDensity(internalImage, EternalMediaBar.activity.getResources().getDisplayMetrics().densityDpi);
                ico.setColorFilter(EternalMediaBar.activity.savedData.iconCol, PorterDuff.Mode.SRC_IN);
                //now push it to a bitmap and return the value
                return ((BitmapDrawable) ico).getBitmap();
            }
        }
        else{
            //get the drawable and recolor it
            Drawable ico = EternalMediaBar.activity.getResources().getDrawableForDensity(internalImage, EternalMediaBar.activity.getResources().getDisplayMetrics().densityDpi);
            ico.setColorFilter(EternalMediaBar.activity.savedData.iconCol, PorterDuff.Mode.SRC_IN);
            //now push it to a bitmap and return the value
            return ((BitmapDrawable) ico).getBitmap();
        }
    }
}

