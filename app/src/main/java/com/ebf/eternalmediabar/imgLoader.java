package com.ebf.eternalmediabar;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by JakeD on 2/25/2016.
 */
public class imgLoader {


    public Drawable load(EternalMediaBar eternalMediaBar,String ico, PackageManager manager){

        //run it on a switch
        switch (ico){
            //case 0 for no icon.
            case "0":{
                return ContextCompat.getDrawable(eternalMediaBar, R.drawable.blank);
            }
            //case 1 for the Social icon
            case "1":{
                //if we aren't using google icons, use the built-in one
                if (!eternalMediaBar.savedData.useGoogleIcons){
                    return ContextCompat.getDrawable(eternalMediaBar, R.drawable.social_144px);
                }
                //otherwise try to load the icon from the Contacts app
                else{
                    try{
                        return manager.getApplicationIcon("com.android.contacts");
                    }
                    //if it fails, fallback to the built-in Social icon.
                    catch (Exception e){
                        return ContextCompat.getDrawable(eternalMediaBar, R.drawable.social_144px);
                    }
                }
            }
            default:{
                return ContextCompat.getDrawable(eternalMediaBar, R.drawable.blank);
            }
        }

    }
}
