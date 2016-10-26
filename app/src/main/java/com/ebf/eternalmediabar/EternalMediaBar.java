package com.ebf.eternalmediabar;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;
import com.ebf.eternalVariables.Widget;

import java.util.ArrayList;
import java.util.List;


public class EternalMediaBar extends Activity {

    public static PackageManager manager;
    public static SettingsClass savedData = new SettingsClass();

    public static int hItem = 0;
    public static boolean init = false;
    public static boolean optionsMenu = false;
    public static boolean copyingOrMoving = false;
    public static Widget editingWidget;
    public static int vItem = 0;
    public static int optionVitem =1;

    //static instance of the activity
    public static EternalMediaBar activity;

    public static LinearLayout optionsLayout;
    public static LinearLayout appsLayout;
    public static LinearLayout categoriesLayout;

    public static DisplayMetrics dpi = new DisplayMetrics();

    public static List<String> selectedApps = new ArrayList<>();

    //we have to instance the event receiver so we can get rid of it when the app is not open.
    intentReceiver mainReciever = new intentReceiver();


    //////////////////////////////////////////////////
    ////////////When the app first starts/////////////
    ////////////or comes back from being//////////////
    ////////////   in the background   ///////////////
    //////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        manager = getPackageManager();
        activity = this;
        //be sure to load the save data, and/or update any changes that may have happened while the app was out of focus.
        Initialization.loadData(this);
        //if this has been initialized, make sure vItem isn't out of bounds
        if (init && vItem >= savedData.categories.get(hItem).appList.size()-1){
            vItem = 0;
        }
        //now load the list view normally
        loadListView();
        //now deal with the event receiver.
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction("android.hardware.usb.action.USB_STATE");
        filter.addAction("android.intent.action.HDMI_PLUGGED");
        registerReceiver(mainReciever, filter);
        getPerms();
    }


    //////////////////////////////////////////////////
    ///////////Intent receiver for search/////////////
    //////////////////////////////////////////////////
    public void searchIntent(String query) {
        //get the results view and be sure it's clear.
        LinearLayout searchView = (LinearLayout)findViewById(R.id.search_view);
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
                List<AppDetail> songs = recrusiveSearch(query.replace(":audio:",""));

                if (songs.size() >0){
                    for (AppDetail song : songs){
                        searchView.addView(ListItemLayout.appListItemView(song, -1, true));
                    }
                }

            } else {
                LinearLayout providerList = new LinearLayout(this);
                providerList.setOrientation(LinearLayout.HORIZONTAL);
                providerList.setMinimumHeight(Math.round(dpi.scaledDensity * 58));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Web", ".webSearch", query), -1));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Store", ".storeSearch", query), -1));
                providerList.addView(ListItemLayout.searchView(new AppDetail("Music",".musicSearch", query), -1));
                searchView.addView(providerList);

                //handle local device searching, first because results are caps sensitive, put the query (and later the potential results) to lowercase.
                query = query.toLowerCase();
                //iterate vLists
                for (int i = 0; i < savedData.categories.size(); ) {
                    //set the bool for if there is a header on the category then iterate through the apps in the category
                    Boolean categoryListed = false;
                    for (int ii = 0; ii < savedData.categories.get(i).appList.size(); ) {
                        //make sure the labels are lowercase, and if it finds something
                        if (savedData.categories.get(i).appList.get(ii).label.toString().toLowerCase().contains(query)) {
                            //check if this category has a header, if not make one and note that there is one.
                            if (!categoryListed) {
                                searchView.addView(ListItemLayout.searchCategoryItemView(savedData.categories.get(i)));
                                categoryListed = true;
                            }
                            //display the actual search result
                            searchView.addView(ListItemLayout.appListItemView(savedData.categories.get(i).appList.get(ii).setCommand(".search"), -1, true));
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


    private List<AppDetail> recrusiveSearch(final String search){
        List<AppDetail> output = new ArrayList<AppDetail>();

        Cursor cur = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, MediaStore.Audio.Media.TITLE + " ASC");

        if(cur != null &&cur.getCount() > 0) {
            while(cur.moveToNext()) {
                if (cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)).toLowerCase().contains(search.toLowerCase())
                                & cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)).toLowerCase().contains(search.toLowerCase()) ){
                    output.add(new AppDetail(
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)) + " - " +cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)) + "\n" +cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ,".audio",
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))
                    ));
                }
            }
            cur.close();
        }
        return output;


    }


    //////////////////////////////////////////////////
    ///////this function is for requesting any////////
    ////////needed permissions in android 6+//////////
    //////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.M)
    public void getPerms() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }
    }

    //////////////////////////////////////////////////
    //unregister IntentReceiver on minimize or close//
    //////////////////////////////////////////////////
    @Override
    protected void onPause() {
        unregisterReceiver(mainReciever);
        onTrimMemory(TRIM_MEMORY_COMPLETE);
        super.onPause();
    }

    //////////////////////////////////////////////////
    ///////////Intent receiver for events/////////////
    //////////////////////////////////////////////////
    private class intentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                //if the headset is plugged in, show a toast and move to the media list
                case Intent.ACTION_HEADSET_PLUG: {
                    if (intent.getIntExtra("state", -1) == 1) {
                        Toast.makeText(EternalMediaBar.this, "Headset plugged in", Toast.LENGTH_SHORT).show();
                        //we have to iterate through the tags to find the list with the desired tag
                        for (int i = 0; i < savedData.categories.size(); ) {
                            if (savedData.categories.get(i).categoryTags.contains("Music")) {
                                listMove(i, true);
                                break;
                            }
                            i++;
                        }
                    }
                    break;
                }

                case "android.intent.action.HDMI_PLUGGED":{
                    Toast.makeText(EternalMediaBar.this, "HDMI plugged in", Toast.LENGTH_SHORT).show();
                    //we have to iterate through the tags to find the list with the desired tag
                    for (int i = 0; i < savedData.categories.size(); ) {
                        if (savedData.categories.get(i).categoryTags.contains("Video")) {
                            listMove(i, true);
                            break;
                        }
                        i++;
                    }
                    break;
                }

                case "android.hardware.usb.action.USB_STATE": {
                    if (intent.getExtras().getBoolean("connected")) {
                        Toast.makeText(EternalMediaBar.this, "USB plugged in", Toast.LENGTH_SHORT).show();
                        //we have to iterate through the tags to find the list with the desired tag
                        for (int i = 0; i < savedData.categories.size(); ) {
                            if (savedData.categories.get(i).categoryTags.contains("Games")) {
                                listMove(i, true);
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




    //////////////////////////////////////////////////
    //////////When a button or key is pressed/////////
    //////////////////////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("Key Pressed: ", "" + keyCode);
        switch (keyCode) {
            //case event for down
            case KeyEvent.KEYCODE_S: case KeyEvent.KEYCODE_DPAD_DOWN: case KeyEvent.KEYCODE_4: case KeyEvent.KEYCODE_NUMPAD_4: {listMove(vItem + 1, false);return true;}
            //case event for up
            case KeyEvent.KEYCODE_W: case KeyEvent.KEYCODE_DPAD_UP: case KeyEvent.KEYCODE_2: case KeyEvent.KEYCODE_NUMPAD_2:{listMove(vItem - 1, false);return true;}
            //case event for right
            case KeyEvent.KEYCODE_D: case KeyEvent.KEYCODE_DPAD_RIGHT: case KeyEvent.KEYCODE_6: case KeyEvent.KEYCODE_NUMPAD_6:{listMove(hItem + 1, true);return true;}
            //case event for left
            case KeyEvent.KEYCODE_A: case KeyEvent.KEYCODE_DPAD_LEFT: case KeyEvent.KEYCODE_8: case KeyEvent.KEYCODE_NUMPAD_8: {listMove(hItem - 1, true);return true;}
            //event for when enter/x/a is pressed
			case KeyEvent.KEYCODE_ENTER: case KeyEvent.KEYCODE_NUMPAD_ENTER: case KeyEvent.KEYCODE_1: case KeyEvent.KEYCODE_5: case KeyEvent.KEYCODE_NUMPAD_5: case KeyEvent.KEYCODE_BUTTON_1: {
                if (!optionsMenu) {
                    //get the item in the layout and activate its button function
                    ((LinearLayout)findViewById(R.id.apps_display)).getChildAt(vItem).performClick();
                }
                else{
                    ((LinearLayout)findViewById(R.id.optionslist)).getChildAt(optionVitem).performClick();
                }
				return true;
			}
            //event for when E/Y/Triangle is pressed
			case KeyEvent.KEYCODE_BUTTON_4: case KeyEvent.KEYCODE_E: case KeyEvent.KEYCODE_TAB: case KeyEvent.KEYCODE_0: case KeyEvent.KEYCODE_NUMPAD_0: {
                if (!optionsMenu) {
                    //get the item in the layout and activate its button function
                    ((LinearLayout)findViewById(R.id.apps_display)).getChildAt(vItem).performLongClick();
                }
                else{
                    OptionsMenuChange.menuClose();
                }
				return true;
			}

            //case event for unused keys
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    //////////////////////////////////////////////////
    /////////////change selected item/////////////////
    //////////////////////////////////////////////////
    void listMove(int move, boolean isCategory){
        if (!isCategory && !optionsMenu){
            LinearLayout layout = (LinearLayout) findViewById(R.id.apps_display);
            if (move >= 0 && move < layout.getChildCount()) {
                //change the old item, if it exists
                try {
                    //change the old font face
                    ((TextView) layout.getChildAt(vItem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                    //scale the icon back to normal
                    ImageView appIcon = (ImageView) layout.getChildAt(vItem).findViewById(R.id.list_item_icon);
                    appIcon.setScaleX(1f);
                    appIcon.setScaleY(1f);
                }
                catch(Exception e){}
                //change vItem
                vItem = move;
                try {
                    //change the font face
                    ((TextView) layout.getChildAt(vItem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                    //scale the icon larger
                    ImageView appIcon = (ImageView) layout.getChildAt(vItem).findViewById(R.id.list_item_icon);
                    appIcon.setScaleX(1.5f);
                    appIcon.setScaleY(1.5f);

                    //scroll to the new entry
                    layout.scrollTo(0, (int) layout.getChildAt(vItem).getX());
                }
                catch (Exception e){}
            }
        }
        else if(!isCategory){
            LinearLayout layout = (LinearLayout) findViewById(R.id.optionslist);
            if (move >= 0 && move < layout.getChildCount()) {
                try {
                    move -= vItem;
                    move += optionVitem;
                    appsLayout = (LinearLayout) findViewById(R.id.optionslist);
                    //if you are trying to move within the actual list size then do so.
                    if (move >= 0 || move < appsLayout.getChildCount()) {
                        //set the font face.
                        ((TextView) appsLayout.getChildAt(optionVitem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                        //change OptionsVItem
                        optionVitem = move;
                        ((TextView) appsLayout.getChildAt(optionVitem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                        //scroll to the new entry
                        appsLayout.scrollTo(0, (int) appsLayout.getChildAt(optionVitem).getX());
                    }
                }
                catch (Exception e){}
            }
        }
        else{
            LinearLayout layout = (LinearLayout) findViewById(R.id.categories);
            if (move >= 0 && move < layout.getChildCount()) {
                //change hItem
                hItem = move;
                //reload the list
                loadListView();
            }
        }
    }



    //////////////////////////////////////////////////
    ///////Function to draw all the information///////
    //////////////////////////////////////////////////
    public void loadListView(){

        getWindowManager().getDefaultDisplay().getMetrics(dpi);
        if (savedData.mirrorMode){setContentView(R.layout.activity_eternal_media_bar_mirror);}
        else{setContentView(R.layout.activity_eternal_media_bar);}
        optionVitem = 0;

        ((SearchView) findViewById(R.id.searchView)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchIntent(newText);
                return false;
            }
        });
        //redefine the layouts and remove their views, and reorganize the list we are about to load, before running GC.
        optionsLayout = (LinearLayout)findViewById(R.id.optionslist);
        optionsLayout = (LinearLayout)findViewById(R.id.optionslist);
        categoriesLayout = (LinearLayout)findViewById(R.id.categories);
        appsLayout = (LinearLayout)findViewById(R.id.apps_display);
        appsLayout.setBackgroundColor(savedData.dimCol);
        categoriesLayout.setBackgroundColor(savedData.dimCol);
        categoriesLayout.removeAllViews();
        appsLayout.removeAllViews();
        savedData.categories.get(hItem).Organize();

        Runtime.getRuntime().gc();
        //////////////////////
        //Draw the Categories
        //////////////////////

        //dim the color to the dimCol
        int count =0;
        //loop to add all entries of hli to the list
        for (CategoryClass category :savedData.categories) {
            if(!category.categoryTags.contains("Unorganized")) {
                categoriesLayout.addView(ListItemLayout.categoryListItemView(category, count));
            } else if (category.appList.size() >0){
                categoriesLayout.addView(ListItemLayout.categoryListItemView(category, count));
            }
            count++;
        }
        listMove(0, false);
        count =0;
        //now define the apps list

        for (AppDetail app : savedData.categories.get(hItem).appList) {
            appsLayout.addView(ListItemLayout.appListItemView(app, count, false));
            count++;
        }
        //make sure the vList item is selected
        listMove(0, false);

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        if (savedData.widgets.size()>0){
            for (Widget widget : savedData.widgets) {
                ((RelativeLayout)EternalMediaBar.activity.findViewById(R.id.mainlayout)).addView(ListItemLayout.loadWidget(widget));
            }
            mAppWidgetHost.startListening();
        } else {
                //selectWidget();
        }

        Runtime.getRuntime().gc();
    }

    public static AppWidgetManager mAppWidgetManager;
    public static AppWidgetHost mAppWidgetHost;

    /**
     * If the user has selected an widget, the result will be in the 'data' when
     * this function is called.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            /**
             * Checks if the widget needs any configuration. If it needs, launches the
             * configuration activity.
             */
            if (requestCode == R.id.REQUEST_PICK_APPWIDGET) {
                int appWidgetId = data.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                if (mAppWidgetManager.getAppWidgetInfo(appWidgetId).configure != null) {
                    Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                    intent.setComponent(mAppWidgetManager.getAppWidgetInfo(appWidgetId).configure);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
                } else {
                    createWidget(appWidgetId);
                }

                //Creates the widget and adds to our view layout.
            } else if (requestCode == R.id.REQUEST_CREATE_APPWIDGET) {
                createWidget(data.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1));
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            if (data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) != -1) {
                mAppWidgetHost.deleteAppWidgetId(data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1));
            }
        }
    }


    /**
     * Creates the widget and adds to our view layout.
     */
    public void createWidget(int appWidgetId) {
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        savedData.widgets.add(new Widget(appWidgetId, appWidgetInfo.minWidth, appWidgetInfo.minHeight));
    }

    /**
     * Launches the menu to select the widget. The selected widget will be on
     * the result of the activity.
     */
    void selectWidget() {
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        /**
         * This avoids a bug in the com.android.settings.AppWidgetPickActivity,
         * This just adds empty extras to the intent, avoiding the bug.
         */
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, new ArrayList<AppWidgetProviderInfo>());
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, new ArrayList<Bundle>());

        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }


    @Override
    protected void onStop() {
        mAppWidgetHost.stopListening();
        super.onStop();
    }

}
