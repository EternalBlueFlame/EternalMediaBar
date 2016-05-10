package com.ebf.eternalmediabar;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class EternalMediaBar extends Activity {

    public PackageManager manager;
    public settingsClass savedData = new settingsClass();

    public int hItem = 0;
    public boolean init = false;
    public boolean optionsMenu = false;
    public int vItem = 0;
    public int optionVitem =1;
    public boolean[] warningToggle;

    public static EternalMediaBar activity;

    //we'll define options menu change ahead of time so we don't have to instance it over and over.
    private optionsMenuChange changeOptionsMenu = new optionsMenuChange();
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
        //be sure the activity variable references this script so we can have global access to it.
        activity = this;
        //be sure to load the save data, and/or update any changes that may have happened while the app was out of focus.
        new initialization().loadData(this);
        //if this hasin't been initialized yet
        if (init){
            //do all the initialization
            new initialization().loadData(this);

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
    }


    //////////////////////////////////////////////////
    ///////////Intent receiver for search/////////////
    //////////////////////////////////////////////////
    private void searchIntent(String query) {
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
            searchView.addView(new listItemLayout().appListItemView("Search " +query + "On Google", -1, 0, true, ".webSearch", query));

            //handle local device searching, first because results are caps sensitive, put the query (and later the potential results) to lowercase.
            query=query.toLowerCase();
            //iterate vLists
            for (int i = 0; i < savedData.categories.size(); ) {
                //set the bool for if there is a header on the category then iterate through the apps in the category
                Boolean categoryListed =false;
                for (int ii = 0; ii < savedData.categories.get(i).appList.size(); ) {
                    //make sure the labels are lowercase, and if it finds something
                    if (savedData.categories.get(i).appList.get(ii).label.toString().toLowerCase().contains(query)) {
                        //check if this category has a header, if not make one and note that there is one.
                        if(!categoryListed){
                            searchView.addView(new listItemLayout().searchCategoryItemView(savedData.categories.get(i).categoryName, savedData.categories.get(i).categoryIcon + " : " + savedData.categories.get(i).categoryGoogleIcon));
                            categoryListed=true;
                        }
                        //display the actual search result
                        searchView.addView(new listItemLayout().appListItemView(savedData.categories.get(i).appList.get(ii).label, -1, 0, true, savedData.categories.get(i).appList.get(ii).name, (String) savedData.categories.get(i).appList.get(ii).label));
                    }
                    ii++;
                }
                i++;
            }
        }
        //if the search query is less than 3 characters, just invalidate the search results view to be sure it gets cleared.
        else{searchView.invalidate();}
    }





    //////////////////////////////////////////////////
    ///////this function is for requesting any////////
    ////////needed permissions in android 6+//////////
    //////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.M)
    public void getPerms() {
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
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
                    changeOptionsMenu.menuClose();
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

        if (savedData.mirrorMode){setContentView(R.layout.activity_eternal_media_bar_mirror);}
        else{setContentView(R.layout.activity_eternal_media_bar);}

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
                layout.addView(new listItemLayout().categoryListItemView(savedData.categories.get(ii).categoryName, ii, savedData.categories.get(ii).categoryIcon + " : " + savedData.categories.get(ii).categoryGoogleIcon));
            }
            else if (savedData.categories.get(ii).appList.size() >0){
                layout.addView(new listItemLayout().categoryListItemView(savedData.categories.get(ii).categoryName, ii, savedData.categories.get(ii).categoryIcon + " : " + savedData.categories.get(ii).categoryGoogleIcon));
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
        if(savedData.categories.get(hItem).appList.size() >1) {
            changeOptionsMenu.organizeList(0);
        }
        //dim the list background to the dim color
        vLayout.setBackgroundColor(savedData.dimCol);


        for (int ii=0; ii< savedData.categories.get(hItem).appList.size();) {
            vLayout.addView(new listItemLayout().appListItemView(savedData.categories.get(hItem).appList.get(ii).label, ii, 0, true, savedData.categories.get(hItem).appList.get(ii).name, (String) savedData.categories.get(hItem).appList.get(ii).label));
            ii++;
        }

        //make sure the vList item is selected
        listMove(0, false);
    }

}
