package com.ebf.eternalmediabar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;

import java.util.ArrayList;
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
            case 3:{Collections.sort(list, new Comparator<AppDetail>() {
                @Override
                public int compare(AppDetail lhs, AppDetail rhs) {
                    try {
                        if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime > EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return 1;
                        } else if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime < EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } catch (PackageManager.NameNotFoundException e){
                        return 0;
                    }
                }
            });
            break;}
            //Oldest
            case 4:{Collections.sort(list, new Comparator<AppDetail>() {
                @Override
                public int compare(AppDetail lhs, AppDetail rhs) {
                    try {
                        if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime < EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return 1;
                        } else if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime > EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } catch (PackageManager.NameNotFoundException e){
                        return 0;
                    }
                }
            });
                break;}
            //most used
            case 5:{
                break;
            }
        }
    }


    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public static void openAppSettings(AppDetail menuItem){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(menuItem.URI)));
        OptionsMenuChange.menuClose(false);
        EternalMediaBar.activity.startActivity(intent);
    }


    //////////////////////////////////////////////////
    /////////Copy/move/hide an app menu item//////////
    //////////////////////////////////////////////////
    public static void relocateItem(int category, int action){

        if (action == R.id.ACTION_UNHIDE){
            for (CategoryClass categoryClass : EternalMediaBar.savedData.categories) {
                if (categoryClass.categoryTags.contains("Unorganized")) {
                    categoryClass.appList.add(EternalMediaBar.savedData.hiddenApps.get(category));
                    EternalMediaBar.savedData.hiddenApps.remove(category);
                    categoryClass.hasBeenOrganized = false;
                    break;
                }
            }
        } else {

            for (String uri : EternalMediaBar.selectedApps) {
                for (int i = 0; i < EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.size(); ) {
                    if (EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).URI.equals(uri)) {
                        switch (action) {
                            case R.id.ACTION_HIDE: {
                                EternalMediaBar.savedData.hiddenApps.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(i);
                                break;
                            }
                            case R.id.ACTION_COPY: {
                                EternalMediaBar.savedData.categories.get(category).appList.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                                EternalMediaBar.savedData.categories.get(category).hasBeenOrganized = false;
                                break;
                            }
                            case R.id.ACTION_MOVE: {
                                EternalMediaBar.savedData.categories.get(category).appList.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(i);
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).hasBeenOrganized = false;
                                break;
                            }
                        }
                        break;
                    }
                    i++;
                }
            }
        }
        OptionsMenuChange.menuClose(true);
    }


    //////////////////////////////////////////////////
    ///////////Intent receiver for search/////////////
    //////////////////////////////////////////////////
    public static void searchIntent(String query) {
        //get the results view and be sure it's clear.
        LinearLayout searchView = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.search_view);
        if(searchView.getChildCount()>0){
            for(int i=0;i<searchView.getChildCount();){
                searchView.getChildAt(i).invalidate();
                i++;
            }
            searchView.removeAllViews();
        }
        //first, be sure there's actually something to search
        if (query.length()>0) {
            if (query.contains(":audio:")){
                Cursor cur = EternalMediaBar.activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, MediaStore.Audio.Media.TITLE + " ASC");

                if(cur != null && cur.getCount() > 0) {
                    while(cur.moveToNext()) {
                        if (cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)).toLowerCase().contains(query.replace(":audio:","").toLowerCase())
                                & cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)).toLowerCase().contains(query.replace(":audio:","").toLowerCase()) ){

                            searchView.addView(ListItemLayout.appListItemView(new AppDetail(
                                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)) + " - " +cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)) + "\n" +cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                                    ,".audio",
                                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))
                            ), -1, true));
                        }
                    }
                    cur.close();
                }

            } else {
                HorizontalScrollView providerScroller = new HorizontalScrollView(EternalMediaBar.activity);
                LinearLayout providerList = new LinearLayout(EternalMediaBar.activity);
                providerList.setOrientation(LinearLayout.HORIZONTAL);
                providerList.setMinimumHeight(Math.round(EternalMediaBar.dpi.scaledDensity * 58));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Web", ".webSearch", query), -1));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Store", ".storeSearch", query), -1));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Music",".musicSearch", query), -1));
                providerList.addView(ListItemLayout.searchView(new AppDetail("YouTube",".ytSearch", query), -1));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Maps",".mapSearch", query), -1));
                providerScroller.addView(providerList);
                searchView.addView(providerScroller);

                //handle local device searching, first because results are caps sensitive, put the query (and later the potential results) to lowercase.
                query = query.toLowerCase();
                //iterate vLists
                for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                    //set the bool for if there is a header on the category then iterate through the apps in the category
                    Boolean categoryListed = false;
                    for (int ii = 0; ii < EternalMediaBar.savedData.categories.get(i).appList.size(); ) {
                        //make sure the labels are lowercase, and if it finds something
                        if (EternalMediaBar.savedData.categories.get(i).appList.get(ii).label.toString().toLowerCase().contains(query)) {
                            //check if this category has a header, if not make one and note that there is one.
                            if (!categoryListed) {
                                searchView.addView(ListItemLayout.searchCategoryItemView(EternalMediaBar.savedData.categories.get(i)));
                                categoryListed = true;
                            }
                            //display the actual search result
                            searchView.addView(ListItemLayout.appListItemView(EternalMediaBar.savedData.categories.get(i).appList.get(ii).setCommand(".search"), -1, true));
                        }
                        ii++;
                    }
                    i++;
                }
            }
        }
        //if the search query is less than 3 characters, just invalidate the search results view to be sure it gets cleared.
        else{searchView.invalidate();}
    }



    //////////////////////////////////////////////////
    ///////////Intent receiver for events/////////////
    //////////////////////////////////////////////////
    public static class intentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                //if the headset is plugged in, show a toast and move to the media list
                case Intent.ACTION_HEADSET_PLUG: {
                    if (intent.getIntExtra("state", -1) == 1) {
                        Toast.makeText(EternalMediaBar.activity, "Headset plugged in", Toast.LENGTH_SHORT).show();
                        //we have to iterate through the tags to find the list with the desired tag
                        for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                            if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Music")) {
                                EternalMediaBar.activity.listMove(i, true);
                                break;
                            }
                            i++;
                        }
                    }
                    break;
                }

                case "android.intent.action.HDMI_PLUGGED":{
                    if (intent.getExtras().toString().contains("true")) {
                        Toast.makeText(EternalMediaBar.activity, "HDMI plugged in", Toast.LENGTH_SHORT).show();
                        //we have to iterate through the tags to find the list with the desired tag
                        for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                            if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Video")) {
                                EternalMediaBar.activity.listMove(i, true);
                                break;
                            }
                            i++;
                        }
                    }
                    break;
                }

                case "android.hardware.usb.action.USB_STATE": {
                    System.out.println("HDMI: " + intent.getType() + " : " + intent.getDataString() + " : "+intent.getIntExtra("state", -1)  + " : " + intent.getExtras().toString() + " : " + intent.getFlags()
                    );//TODO need to detect what exactly got plugged in.
                    if (intent.getExtras().getBoolean("connected")) {
                        Toast.makeText(EternalMediaBar.activity, "USB plugged in", Toast.LENGTH_SHORT).show();
                        //we have to iterate through the tags to find the list with the desired tag
                        for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                            if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Games")) {
                                EternalMediaBar.activity.listMove(i, true);
                                break;
                            }
                            i++;
                        }
                    }
                    break;
                }




            }
        }
    }



}
