package com.ebf.eternalmediabar;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebf.eternalVariables.AppDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EternalMediaBar extends Activity {

    public static PackageManager manager;
    public static SettingsClass savedData = new SettingsClass();

    public static int hItem = 0;
    public static boolean init = false;
    public static boolean optionsMenu = false;
    public static boolean copyingOrMoving;
    public static int vItem = 0;
    public static int optionVitem =1;
    public static boolean[] warningToggle;
    //static instance of the activity

    public static EternalMediaBar activity;

    public static LinearLayout optionsLayout;

    public static DisplayMetrics dpi = new DisplayMetrics();

    public static final List<String> innerURI = Arrays.asList(".options", ".music");

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
        activity = this;
        //be sure to load the save data, and/or update any changes that may have happened while the app was out of focus.
        Initialization.loadData(this);
        //if this hasin't been initialized yet
        if (init){
            //make sure vItem isn't out of bounds
            if (vItem >= savedData.categories.get(hItem).appList.size()){
                vItem=0;
            }
        }
        //now load the list view normally
        loadListView();
        //now deal with the event receiver.
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
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
                final String search = query.replace(":audio:","");
                System.out.println("searching for music: " + search );
                final List<AppDetail> songs = recrusiveSearch(search);

                if (songs.size() >0){
                    System.out.println("there are songs");
                    for (AppDetail song : songs){
                        System.out.println("found song" + song.internalCommand);
                        searchView.addView(ListItemLayout.appListItemView(song, -1, true));
                        //songs.remove(song);
                    }
                }

            } else {
                searchView.addView(ListItemLayout.appListItemView(new AppDetail("Search \"" + query + "\" on the web", "", ".webSearch", query), -1, true));
                searchView.addView(ListItemLayout.appListItemView(new AppDetail("Search \"" + query + "\" on the Apps Store ",",",".storeSearch", query), -1, true));
                searchView.addView(ListItemLayout.appListItemView(new AppDetail("Search \"" + query + "\" in your Music ","",".musicSearch", query), -1, true));

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
                                searchView.addView(ListItemLayout.searchCategoryItemView(savedData.categories.get(i).categoryName, savedData.categories.get(i).categoryIcon));
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
        final List<AppDetail> output = new ArrayList<AppDetail>();

        ContentResolver cr = this.getContentResolver();

        Cursor cur = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, MediaStore.Audio.Media.TITLE + " ASC");
        int count = 0;

        if(cur != null) {
            count = cur.getCount();
            if(count > 0) {
                while(cur.moveToNext()) {
                    if ( cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)).toLowerCase().contains(search.toLowerCase()) ){
                        System.out.println("found song" + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                        output.add(new AppDetail(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                                cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),".audio",
                                cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))
                        ));
                    }
                }
            }
        }
        cur.close();
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
            //if the headset is plugged in, show a toast and move to the media list
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
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
        else if(!isCategory && optionsMenu){
            LinearLayout layout = (LinearLayout) findViewById(R.id.optionslist);
            if (move >= 0 && move < layout.getChildCount()) {
                try {
                    move -= vItem;
                    move += optionVitem;
                    LinearLayout vLayout = (LinearLayout) findViewById(R.id.optionslist);
                    //if you are trying to move within the actual list size then do so.
                    if (move >= 0 || move < vLayout.getChildCount()) {
                        //set the font face.
                        ((TextView) vLayout.getChildAt(optionVitem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                        //change OptionsVItem
                        optionVitem = move;
                        ((TextView) vLayout.getChildAt(optionVitem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                        //scroll to the new entry
                        vLayout.scrollTo(0, (int) vLayout.getChildAt(optionVitem).getX());
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
        optionsLayout = (LinearLayout)findViewById(R.id.optionslist);

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


        //////////////////////
        //Draw the Categories
        //////////////////////

        manager = getPackageManager();
        LinearLayout layout = (LinearLayout)findViewById(R.id.categories);
        //empty the list
        layout.removeAllViews();
        //dim the color to the dimCol
        layout.setBackgroundColor(savedData.dimCol);
        //loop to add all entries of hli to the list
        for (int ii=0; (ii)<savedData.categories.size();) {
            if(!savedData.categories.get(ii).categoryTags.contains("Unorganized")) {
                layout.addView(ListItemLayout.categoryListItemView(savedData.categories.get(ii).categoryName, ii, savedData.categories.get(ii).categoryIcon));
            }
            else if (savedData.categories.get(ii).appList.size() >0){
                layout.addView(ListItemLayout.categoryListItemView(savedData.categories.get(ii).categoryName, ii, savedData.categories.get(ii).categoryIcon));
            }
        ii++;
        }
        listMove(0, false);

        //now define the apps list
        LinearLayout vLayout = (LinearLayout)findViewById(R.id.apps_display);
        for(int i=0; i<vLayout.getChildCount();){
            vLayout.getChildAt(i).invalidate();
            i++;
        }
        vLayout.removeAllViews();

        //set the list organization method
        if(savedData.categories.get(hItem).appList.size() >1 && EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeAlways) {
            EternalUtil.organizeList();
        }
        //dim the list background to the dim color
        vLayout.setBackgroundColor(savedData.dimCol);


        for (int ii=0; ii< savedData.categories.get(hItem).appList.size();) {
            vLayout.addView(ListItemLayout.appListItemView(savedData.categories.get(hItem).appList.get(ii), ii, false));
            ii++;
        }

        //make sure the vList item is selected
        listMove(0, false);
    }

}
