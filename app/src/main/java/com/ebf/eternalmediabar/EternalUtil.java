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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EternalUtil {


    /**
     * <h2>Organize the app lists</h2>
     * use one of the custom defined collections.sort methods to organize the list of apps.
     * @param list the list to organize
     * @param mode the mode to organize the list with
     */
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


    /**
     * sinple call to open the Android System settings menu for the selected app.
     * @param menuItem the app to open the menu for.
     */
    public static void openAppSettings(AppDetail menuItem){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(menuItem.URI)));
        OptionsMenuChange.menuClose(false);
        EternalMediaBar.activity.startActivity(intent);
    }


    /**
     * <h2>Relocate apps</h2>
     * used for moving apps from one menu to another.
     * @param category
     * @param action
     */
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


    /**
     * c<h2> search intent reciever</h2>
     * custom intent reciever for search functionality.
     * If the search contains ":audio: search through the user's music via Android MediaStore.
     * Otherwise display the options to search through one of the providers, before displaying the actual list of results.
     * @param query
     */
    public static void searchIntent(String query) {
        LinearLayout searchView = (LinearLayout)EternalMediaBar.activity.findViewById(R.id.search_view);
        searchView.removeAllViews();

        if (query.length()>1) {
            //search music
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
                //normal search
            } else {
                /**
                 * add the searchbar for the providers such as google, youtube, maps, etc.
                 * What these actually do is defined in:
                 * @see ListItemLayout#searchView(AppDetail)
                 */
                HorizontalScrollView providerScroller = new HorizontalScrollView(EternalMediaBar.activity);
                LinearLayout providerList = new LinearLayout(EternalMediaBar.activity);
                providerList.setOrientation(LinearLayout.HORIZONTAL);
                providerList.setMinimumHeight(Math.round(EternalMediaBar.dpi.scaledDensity * 58));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Web", ".webSearch", query)));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Store", ".storeSearch", query)));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Music",".musicSearch", query)));
                providerList.addView(ListItemLayout.searchView(new AppDetail("YouTube",".ytSearch", query)));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Maps",".mapSearch", query)));
                providerScroller.addView(providerList);
                searchView.addView(providerScroller);

                /**
                 * Display the search results for the users apps.
                 * For every category that we iterate through, show a header for it before displaying the apps in it.
                 */
                query = query.toLowerCase();
                for (CategoryClass category : EternalMediaBar.savedData.categories) {
                    Boolean categoryListed = false;
                    for (AppDetail app : category.appList) {
                        if (app.label.toString().toLowerCase().contains(query)) {
                            if (!categoryListed) {
                                searchView.addView(ListItemLayout.searchCategoryItemView(category));
                                categoryListed = true;
                            }
                            searchView.addView(ListItemLayout.appListItemView(app.setCommand(".search"), -1, true));
                        }
                    }
                }
            }
        }
        //if the search query is less than 1 character, just remove all views from search and run GC.
        else{
            searchView.removeAllViews();
            Runtime.getRuntime().gc();
        }
    }


    /**
     * <h2>Hardware Event Receiver</h2>
     * receive certain hardware events and act accordingly.
     * events to receive are defined in:
     * @see EternalMediaBar#onResume()
     */
    public static class intentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case Intent.ACTION_HEADSET_PLUG: {
                    if (intent.getIntExtra("state", -1) == 1) {
                        Toast.makeText(EternalMediaBar.activity, "Headset plugged in", Toast.LENGTH_SHORT).show();
                        //we have to iterate through the tags to find the list with the desired tag
                        for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                            if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Music")) {
                                EternalMediaBar.categoryListMove(i);
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
                        for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                            if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Video")) {
                                EternalMediaBar.categoryListMove(i);
                                break;
                            }
                            i++;
                        }
                    }
                    break;
                }

                case "android.hardware.usb.action.USB_STATE": {
                    System.out.println("USB: " + intent.getType() + " : " + intent.getDataString() + " : "+intent.getIntExtra("state", -1)  + " : " + intent.getExtras().toString() + " : " + intent.getFlags()
                    );//TODO need to detect what exactly got plugged in.
                    if (intent.getExtras().getBoolean("connected")) {
                        Toast.makeText(EternalMediaBar.activity, "USB plugged in", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                            if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Games")) {
                                EternalMediaBar.categoryListMove(i);
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
