package com.ebf.eternalmediabar;

import android.content.pm.PackageManager;
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
    protected Bitmap doInBackground(imgLoader... params) {
        //check if the icon is supposed to be internal or not
        if(isInternal) {
            //run a switch to load an icon dependant on it's value.
            switch (ico) {
                case ".colHeaderFont": {
                    return Bitmap.createBitmap(new int[]{eternalMediaBar.savedData.fontCol},1,1, Bitmap.Config.ARGB_8888);
                }
                case ".radioCheck":{
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.ic_radio_button_checked_white_24dp);
                }
                case ".radioUnCheck":{
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.ic_radio_button_unchecked_white_24dp);
                }
                case "0": {
                    return Bitmap.createBitmap(new int[]{-1},1,1, Bitmap.Config.ALPHA_8);
                }
                case "1": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.social_144px);
                }
                case "2": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.media_144px);
                }
                case "3": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.games_144px);
                }
                case "4": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.web_144px);
                }
                case "5": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.extras_144px);
                }
                case "6": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.settings_144px);
                }
                case "7": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.new_install_144px);
                }
                default: {
                    //use this for loading the application's icon
                    try {
                        return ((BitmapDrawable)manager.getApplicationIcon(ico)).getBitmap();
                    } catch (Exception e) {
                        if (ico.equals(".options")) {
                            return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.sub_settings_144px);
                        } else {
                            return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.error_144px);
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
                    return Bitmap.createBitmap(new int[]{-1}, 1, 1, Bitmap.Config.ARGB_8888);
                }
                //each case tries to load an icon from an app, if it fails, it falls back to internal icon.
                case "1":{
                    try{return ((BitmapDrawable)manager.getApplicationIcon("com.android.contacts")).getBitmap();}
                    //if it fails, fallback to the built-in Social icon.
                    catch (Exception e){BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.social_144px);}
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
                            //because we are trying to combine multiple images to a single image, we have to create a bitmap, then draw the images to it as if it's a canvas.
                            Bitmap bit = (Bitmap.createBitmap((int) (48 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f),(int) (48 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f), Bitmap.Config.ARGB_8888));
                            LayerDrawable layersDraw = new LayerDrawable(layers);
                            layersDraw.setBounds(0,0, (int) (48 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f),(int) (48 * eternalMediaBar.getResources().getDisplayMetrics().density + 0.5f));
                            layersDraw.draw(new Canvas(bit));


                            return bit;
                        } catch (Exception e) {
                            return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.media_144px);
                        }
                    }
                    else {
                        return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.media_144px);
                    }
                }
                case "3": {
                    try {
                        return ((BitmapDrawable)manager.getApplicationIcon("com.google.android.play.games")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.games_144px);
                    }
                }
                case "4":{
                    try {
                        return ((BitmapDrawable)manager.getApplicationIcon("com.android.chrome")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.web_144px);
                    }
                }
                case "5": {
                    try {
                        return ((BitmapDrawable)manager.getApplicationIcon("com.google.android.apps.docs")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.extras_144px);
                    }
                }
                case "6":{
                    try {
                        return ((BitmapDrawable)manager.getApplicationIcon("com.android.settings")).getBitmap();
                    } catch (Exception e) {
                        return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.settings_144px);
                    }
                }
                case "7": {
                    return BitmapFactory.decodeResource(eternalMediaBar.getResources(),R.drawable.new_install_144px);
                }
                default:{
                    return Bitmap.createBitmap(new int[]{-1}, 1, 1, Bitmap.Config.ALPHA_8);
                }
            }
        }
    }
}

