package com.ebf.eternalmediabar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
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
            case ".colHeaderIcon": {
                return Bitmap.createBitmap(new int[]{EternalMediaBar.activity.savedData.iconCol},1,1, Bitmap.Config.ARGB_8888);
            }
            case ".radioCheck":{
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_radio_button_checked_white_24dp);}
                    default:{return getBitmap(true, "", R.drawable.ic_radio_button_checked_white_24dp);}
                }
            }
            case ".radioUnCheck":{
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_radio_button_unchecked_white_24dp);}
                    default:{return getBitmap(true, "", R.drawable.ic_radio_button_unchecked_white_24dp);}
                }
            }
            case "0": {
                return Bitmap.createBitmap(new int[]{-1},1,1, Bitmap.Config.ALPHA_8);
            }
            case "1": {
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_people_white_48dp);}
                    case "Google":{return getBitmap(false, "com.android.contacts", R.drawable.lunar_social);}
                    default:{return getBitmap(true, "", R.drawable.lunar_social);}
                }
            }
            case "2": {
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_play_circle_filled_white_48dp);}
                    case "Google":{return getBitmap(false, "com.google.android.videos", R.drawable.lunar_media);}
                    default:{return getBitmap(true, "", R.drawable.lunar_media);}
                }
            }
            case "3": {
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_videogame_asset_white_48dp);}
                    case "Google":{return getBitmap(false, "com.google.android.play.games", R.drawable.lunar_games);}
                    default:{return getBitmap(true, "", R.drawable.lunar_games);}
                }
            }
            case "4": {
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_web_white_48dp);}
                    case "Google":{return getBitmap(false, "com.android.chrome", R.drawable.lunar_web);}
                    default:{return getBitmap(true, "", R.drawable.lunar_web);}
                }
            }
            case "5": {
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_assignment_white_48dp);}
                    case "Google":{return getBitmap(false, "com.google.android.apps.docs", R.drawable.extras_144px);}
                    default:{return getBitmap(true, "", R.drawable.extras_144px);}
                }
            }
            case "6": {
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_settings_white_48dp);}
                    case "Google":{return getBitmap(false, "com.android.settings", R.drawable.settings_144px);}
                    default:{return getBitmap(true, "", R.drawable.settings_144px);}
                }
            }
            case "7": {
                switch (EternalMediaBar.activity.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_fiber_new_white_48dp);}
                    //no google case for this
                    default:{return getBitmap(true, "", R.drawable.lunar_new_apps);}
                }
            }
            case".options": {
                switch (EternalMediaBar.activity.savedData.theme) {
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_settings_applications_white_48dp);}
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
                        case "Material":{return getBitmap(true, "", R.drawable.material_ic_error_white_48dp);}
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
                //get the icon as a bitmap drawable
                BitmapDrawable ico = (BitmapDrawable)EternalMediaBar.activity.getResources().getDrawable(internalImage);
                //set the color filter
                ico.setColorFilter(EternalMediaBar.activity.savedData.iconCol, PorterDuff.Mode.MULTIPLY);
                //create the bitmap to modify
                Bitmap bit = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);
                //set the bounds of the icon, for some unknown reason this is necessary
                ico.setBounds(0, 0, 96, 96);
                //draw the icon to the bitmap via canvas
                Canvas canvas = new Canvas(bit);
                ico.draw(canvas);
                //invalidate the now useless drawable and canvas before returning the image
                ico=null;
                canvas = null;
                return bit;
            }
        }
        else{
            //get the icon as a bitmap drawable
            BitmapDrawable ico = (BitmapDrawable)EternalMediaBar.activity.getResources().getDrawable(internalImage);
            //set the color filter
            ico.setColorFilter(EternalMediaBar.activity.savedData.iconCol, PorterDuff.Mode.MULTIPLY);
            //create the bitmap to modify
            Bitmap bit = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);
            //set the bounds of the icon, for some unknown reason this is necessary
            ico.setBounds(0, 0, 96, 96);
            //draw the icon to the bitmap via canvas
            Canvas canvas = new Canvas(bit);
            ico.draw(canvas);
            //invalidate the now useless drawable and canvas before returning the image
            ico=null;
            canvas = null;
            return bit;
        }
    }
}

