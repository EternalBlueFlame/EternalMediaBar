package com.ebf.eternalmediabar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.View;

import com.ebf.eternalVariables.AsyncImageView;

public class ImgLoader extends AsyncTask<AsyncImageView, Void, Bitmap>{

    private AsyncImageView v;

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
            // If this item hasn't been recycled already, hide the
            // progress and set and show the image
            v.icon.setVisibility(View.VISIBLE);
            v.icon.setImageBitmap(result);
    }

    @Override
    protected Bitmap doInBackground(AsyncImageView... params) {
        v=params[0];
        return ProcessInput(v.internalCommand, v.URI);
    }


    public static Bitmap ProcessInput(String internalCommand, String uri){
        //run a switch to load an icon dependant on it's value.
        switch (uri) {
            case ".colHeaderFont": {
                return Bitmap.createBitmap(new int[]{EternalMediaBar.savedData.fontCol},1,1, Bitmap.Config.ARGB_8888);
            }
            case ".colHeaderIcon": {
                return Bitmap.createBitmap(new int[]{EternalMediaBar.savedData.iconCol},1,1, Bitmap.Config.ARGB_8888);
            }
            case ".colHeaderMenu": {
                return Bitmap.createBitmap(new int[]{EternalMediaBar.savedData.menuCol},1,1, Bitmap.Config.ARGB_8888);
            }
            case ".radioCheck":{
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_radio_button_checked_white_24dp);}
                    //case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_radio_check);}
                    default:{return getBitmap(true, "", R.drawable.ic_radio_button_checked_white_24dp);}
                }
            }
            case ".radioUnCheck":{
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_radio_button_unchecked_white_24dp);}
                    //case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_radio_uncheck);}
                    default:{return getBitmap(true, "", R.drawable.ic_radio_button_unchecked_white_24dp);}
                }
            }
            case ".webSearch":{
                return getBitmap(false, "com.google.android.googlequicksearchbox", R.drawable.lunar_web);
            }
            case ".storeSearch":{
                return getBitmap(false, "com.android.vending", R.drawable.lunar_web);
            }
            case "0": {
                return Bitmap.createBitmap(new int[]{-1},1,1, Bitmap.Config.ALPHA_8);
            }
            case "icon.social": {
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_people_white_48dp);}
                    case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_social);}
                    case "Google":{return getBitmap(false, "com.android.contacts", R.drawable.lunar_social);}
                    default:{return getBitmap(true, "", R.drawable.lunar_social);}
                }
            }
            case "icon.media":case ".musicSearch": {
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_play_circle_filled_white_48dp);}
                    case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_media);}
                    case "Google":{return getBitmap(false, "com.google.android.videos", R.drawable.lunar_media);}
                    default:{return getBitmap(true, "", R.drawable.lunar_media);}
                }
            }
            case "icon.games": {
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_videogame_asset_white_48dp);}
                    case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_games);}
                    case "Google":{return getBitmap(false, "com.google.android.play.games", R.drawable.lunar_games);}
                    default:{return getBitmap(true, "", R.drawable.lunar_games);}
                }
            }
            case "icon.web": {
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_web_white_48dp);}
                    case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_web);}
                    case "Google":{return getBitmap(false, "com.android.chrome", R.drawable.lunar_web);}
                    default:{return getBitmap(true, "", R.drawable.lunar_web);}
                }
            }
            case "icon.utility": {
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_assignment_white_48dp);}
                    case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_utility);}
                    case "Google":{return getBitmap(false, "com.google.android.apps.docs", R.drawable.lunar_utility);}
                    default:{return getBitmap(true, "", R.drawable.lunar_utility);}
                }
            }
            case "icon.settings": {
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_settings_white_48dp);}
                    case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_settings);}
                    case "Google":{return getBitmap(false, "com.android.settings", R.drawable.lunar_settings);}
                    default:{return getBitmap(true, "", R.drawable.lunar_settings);}
                }
            }
            case "icon.new": {
                switch (EternalMediaBar.savedData.theme){
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_fiber_new_white_48dp);}
                    case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_new_apps);}
                    //no google case for this
                    default:{return getBitmap(true, "", R.drawable.lunar_new_apps);}
                }
            }
            case".options": {
                switch (EternalMediaBar.savedData.theme) {
                    case "Material":{return getBitmap(true, "", R.drawable.material_ic_settings_applications_white_48dp);}
                    //case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_emb);}
                    //no google case for this
                    default: {
                        return getBitmap(true, "", R.drawable.sub_settings_144px);
                    }
                }
            }
            case ".audio":{
                MediaMetadataRetriever mData=new MediaMetadataRetriever();
                try{
                    android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(internalCommand);

                    byte [] data = mmr.getEmbeddedPicture();
                    return BitmapFactory.decodeByteArray(data, 0, data.length);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return Bitmap.createBitmap(new int[]{EternalMediaBar.savedData.iconCol},1,1, Bitmap.Config.ARGB_8888);
                }
            }
            default: {
                //try to load app icon, if it fails, get the error icon
                try {
                    int scale = Math.round(EternalMediaBar.dpi.scaledDensity * 48);
                    return Bitmap.createScaledBitmap(((BitmapDrawable)EternalMediaBar.manager.getApplicationIcon(uri)).getBitmap(), scale,scale, true);
                } catch (Exception e) {
                    switch (EternalMediaBar.savedData.theme){
                        case "Material":{return getBitmap(true, "", R.drawable.material_ic_error_white_48dp);}
                        //case "LunarInverse":{return getBitmap(true, "", R.drawable.lunar_inverse_error);}
                        //no google case
                        default:{return getBitmap(true, "", R.drawable.material_ic_error_white_48dp);}
                    }
                }
            }
        }
    }

    public static Bitmap getBitmap(boolean internal, String launchIntent, int internalImage){
        if(!internal) {
            try {
                return ((BitmapDrawable) EternalMediaBar.manager.getApplicationIcon(launchIntent)).getBitmap();
            } catch (Exception e) {
                //get the icon as a bitmap drawable
                BitmapDrawable ico = (BitmapDrawable)EternalMediaBar.activity.getResources().getDrawable(internalImage);
                //set the color filter
                ico.setColorFilter(EternalMediaBar.savedData.iconCol, PorterDuff.Mode.MULTIPLY);
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
            ico.setColorFilter(EternalMediaBar.savedData.iconCol, PorterDuff.Mode.MULTIPLY);
            //create the bitmap to modify
            Bitmap bit = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);
            //set the bounds of the icon, for some unknown reason this is necessary
            ico.setBounds(0, 0, 96, 96);
            //draw the icon to the bitmap via canvas
            Canvas canvas = new Canvas(bit);
            ico.draw(canvas);
            //invalidate the now useless drawable and canvas before returning the image, so that the data can be cleaned by GC
            ico=null;
            canvas = null;
            return bit;
        }
    }
}

