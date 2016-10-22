package com.ebf.eternalmediabar;

import android.content.Intent;
import android.net.Uri;

import com.ebf.eternalVariables.AppDetail;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EternalUtil {


    //////////////////////////////////////////////////
    //////////////////Organize List///////////////////
    //////////////////////////////////////////////////
    public static void organizeList(List<AppDetail> list, int mode){
        switch(mode){
            //no organization
            case 0:{break;}
            //Alphabetical
            case 1:{
                Collections.sort(list, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //Reverse alphabetical
            case 2:{
                Collections.sort(list, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return -lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //Newest
            case 3:{break;}
            //Most used
            case 4:{break;}
        }
    }


    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public static void openAppSettings(AppDetail menuItem){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(menuItem.URI)));
        OptionsMenuChange.menuClose();
        EternalMediaBar.activity.startActivity(intent);
    }

}
