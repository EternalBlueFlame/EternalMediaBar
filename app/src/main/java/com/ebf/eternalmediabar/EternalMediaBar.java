package com.ebf.eternalmediabar;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebf.eternalVariables.appDetail;
import com.ebf.eternalVariables.webSearchResults;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EternalMediaBar extends Activity {

    public PackageManager manager;
    public List<appDetail> hli = new ArrayList<>();
    public settingsClass savedData = new settingsClass();

    public int hItem = 0;
    public boolean init = false;
    public boolean optionsMenu = false;
    public int vItem = 0;
    public int optionVitem =1;
    public boolean[] warningToggle;

    private optionsMenuChange changeOptionsMenu = new optionsMenuChange();
    intentReceiver mainReciever = new intentReceiver();


    //////////////////////////////////////////////////
    ////////////When the app first starts/////////////
    ////////////or comes back from being//////////////
    ////////////   in the background   ///////////////
    //////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        if (init){
            //load in the apps
            new initialization().loadData(this);

            //make sure vItem isn't out of bounds
            if (vItem >= savedData.categories.get(hItem).appList.size()){
                vItem = savedData.categories.get(hItem).appList.size();
            }

            //make sure that if the new apps list disappears, we aren't on it.
            if (hItem == (savedData.categories.size()-1) && savedData.categories.get(savedData.categories.size()-1).appList.size()==0){
                listMove(0, true);
            }
            //otherwise just load normally
            else{
                loadListView();
            }
        }
        else{
            setContentView(R.layout.activity_eternal_media_bar);
            new initialization().loadData(this);
            loadListView();
        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mainReciever, filter);

    }


    //////////////////////////////////////////////////
    ///////////Intent receiver for search/////////////
    //////////////////////////////////////////////////
    private void searchIntent(String query, boolean isWeb) {
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

            //because we don't want to use any data here, be sure wifi is being used before searching the web.
            if(isWeb && query.length()>2 && ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                //search it online first
                try {
                    JSONArray arrayObject = new JSONObject(new webSearchResults().execute(query).get()).getJSONObject("responseData").getJSONArray("results");

                    if(!arrayObject.isNull(0)) {
                        //get the icon for the web tag
                    for (int i = 0; i < savedData.categories.size(); ) {
                        if (savedData.categories.get(i).categoryTags.contains("Web")) {
                            searchView.addView(createMenuEntry(R.layout.search_category, "On The Internet",-1,0,false, savedData.categories.get(i).categoryIcon + " : " + savedData.categories.get(i).categoryGoogleIcon,"hItem"));
                            break;
                        }
                        i++;
                    }
                        //display the search results
                    for (int i=0;!arrayObject.isNull(i);) {
                        searchView.addView(searchItem(arrayObject.getJSONObject(i).getString("titleNoFormatting").replace("&#39;","'"), arrayObject.getJSONObject(i).getString("url"), arrayObject.getJSONObject(i).getString("content").replace("<b>", "").replace("</b>", "").replace("&#39;","'")));
                            i++;
                        }
                    }
                }
                catch (Exception e){e.printStackTrace();}
            }

            //make sure the search is lower case, because searches are caps sensitive
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
                            searchView.addView(createMenuEntry(R.layout.search_category, hli.get(i).label,-1,0,false, savedData.categories.get(i).categoryIcon + " : " + savedData.categories.get(i).categoryGoogleIcon,"hItem"));
                            categoryListed=true;
                        }
                        //display the actual search result
                        searchView.addView(createMenuEntry(R.layout.list_item, savedData.categories.get(i).appList.get(ii).label, -1, 0, true, savedData.categories.get(i).appList.get(ii).name, (String) savedData.categories.get(i).appList.get(ii).label));
                    }
                    ii++;
                }
                i++;
            }
        }
    }





    //////////////////////////////////////////////////
    ///////this function is for requesting any////////
    ////////needed permissions in android 6+//////////
    //////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.M)
    public void getPerms(){
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
                    //get the layout
                    LinearLayout vLayout = (LinearLayout)findViewById(R.id.apps_display);
                    //get the item in the layout and activate its button function
                    vLayout.getChildAt(vItem).performClick();
                }
                else{
                    //get the layout
                    LinearLayout lLayout = (LinearLayout)findViewById(R.id.optionslist);
                    //get the item in the layout and activate its button function
                    lLayout.getChildAt(optionVitem).performClick();
                }
				return true;
			}
            //event for when E/Y/Triangle is pressed
			case KeyEvent.KEYCODE_BUTTON_4: case KeyEvent.KEYCODE_E: case KeyEvent.KEYCODE_TAB: case KeyEvent.KEYCODE_0: case KeyEvent.KEYCODE_NUMPAD_0: {
                if (!optionsMenu) {
                    //get the layout
                    LinearLayout vLayout = (LinearLayout)findViewById(R.id.apps_display);
                    //get the item in the layout and activate its button function
                    vLayout.getChildAt(vItem).performLongClick();
                }
                else{
                    changeOptionsMenu.menuClose(EternalMediaBar.this);
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
                    ((TextView) layout.getChildAt(vItem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                    //scale the icon back to normal
                    ImageView appIcon = (ImageView) layout.getChildAt(vItem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1f);
                    appIcon.setScaleY(1f);
                }
                catch(Exception e){}
                //change vItem
                vItem = move;
                try {
                    //change the font face
                    ((TextView) layout.getChildAt(vItem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                    //scale the icon larger
                    ImageView appIcon = (ImageView) layout.getChildAt(vItem).findViewById(R.id.item_app_icon);
                    appIcon.setScaleX(1.25f);
                    appIcon.setScaleY(1.25f);

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
                        ((TextView) vLayout.getChildAt(optionVitem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                        //change OptionsVItem
                        optionVitem = move;
                        ((TextView) vLayout.getChildAt(optionVitem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
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

        if (savedData.mirrorMode){
            setContentView(R.layout.activity_eternal_media_bar_mirror);
        }
        else{
            setContentView(R.layout.activity_eternal_media_bar);
        }

        ((SearchView) findViewById(R.id.searchView)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchIntent(query, true);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchIntent(newText, false);
                return false;
            }
        });


        //////////////////////
        //Draw the Categories
        //////////////////////
        hli.clear();
        //setup the horizontal bar, there's a pre-defined setting to ease the ability for custom options later down the road.most importantly it simplifies the code.


        for (int i = 0; i < savedData.categories.size(); ) {
            appDetail hMenuItem = new appDetail();
            hMenuItem.name = "hItem";
            hMenuItem.isPersistent = true;
            hMenuItem.label = savedData.categories.get(i).categoryName;
            hMenuItem.icon = new imgLoader(this, savedData.categories.get(i).categoryIcon, manager, savedData.useGoogleIcons).doInBackground();
            hli.add(hMenuItem);
            i++;
        }


        manager = getPackageManager();
        LinearLayout layout = (LinearLayout)findViewById(R.id.categories);
        //empty the list
        layout.removeAllViews();
        //if we have it set to dim the background, dim it
        if(savedData.dimLists) {
            //we have to use the depreciated method to retain android 4.0 support, we don't have any need for the theme extension anyway.
            layout.setBackgroundColor(getResources().getColor(R.color.dimColor));
        }
        //loop to add all entries of hli to the list
        for (int ii=0; (ii)<hli.size();) {
            if(!savedData.categories.get(ii).categoryName.equals("New Apps")) {
                layout.addView(createMenuEntry(R.layout.category_item, hli.get(ii).label, ii, 0, false, savedData.categories.get(ii).categoryIcon + " : " + savedData.categories.get(ii).categoryGoogleIcon, "hItem"));
            }
            else if (savedData.categories.get(ii).appList.size() >0){
                layout.addView(createMenuEntry(R.layout.category_item, hli.get(ii).label, ii, 0, false, savedData.categories.get(ii).categoryIcon + " : " + savedData.categories.get(ii).categoryGoogleIcon, "hItem"));
            }
        ii++;
        }
        //change the display for the appropriate icon
        //change the font type
        try {
            ((TextView) layout.getChildAt(hItem).findViewById(R.id.item_app_label)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            //modify the icon to go be larger, and go under the text so it appears even bigger and doesn't scale out of the view.
            ImageView appIcon = (ImageView) layout.getChildAt(hItem).findViewById(R.id.item_app_icon);
            appIcon.setScaleX(1.25f);
            appIcon.setScaleY(1.25f);
            appIcon.setY(3 * getResources().getDisplayMetrics().density + 0.5f);
            //scroll to the new entry
            layout.scrollTo(0, (int) layout.getChildAt(hItem).getY());
        }
        catch(Exception e){}
        listMove(0, false);


        //copy category method but with the vList
        LinearLayout vLayout = (LinearLayout)findViewById(R.id.apps_display);
        for(int i=0; i<vLayout.getChildCount();){
            vLayout.getChildAt(i).invalidate();
            i++;
        }
        vLayout.removeAllViews();

        //set the list organization method
        if(savedData.categories.get(hItem).appList.size() >1) {
            changeOptionsMenu.organizeList(this, 0);
        }
        //if we have it set to dim the background, dim it
        if(savedData.dimLists) {
            //we have to use the depreciated method to retain android 4.0 support, we don't have any need for the theme extension anyway.
            vLayout.setBackgroundColor(getResources().getColor(R.color.dimColor));
        }


        for (int ii=0; ii< savedData.categories.get(hItem).appList.size();) {
            vLayout.addView(createMenuEntry(R.layout.list_item, savedData.categories.get(hItem).appList.get(ii).label, ii, 0, true, savedData.categories.get(hItem).appList.get(ii).name, (String) savedData.categories.get(hItem).appList.get(ii).label));
            ii++;
        }

        //make sure the vList item is selected
        listMove(0, false);
    }

    //////////////////////////////////////////////////
    /////////Function for creating list items/////////
    //////////////////////////////////////////////////
    public View createMenuEntry(int inflater, CharSequence text, final int index, final int secondaryIndex, final Boolean isLaunchable, final String launchIntent, final String appName){
        //initialize the views we know will be there
        View child = getLayoutInflater().inflate(inflater, null);
        TextView appLabel = (TextView) child.findViewById(R.id.item_app_label);
        appLabel.setText(text);
        appLabel.setTextColor(savedData.fontCol);
        appLabel.setAlpha(Color.alpha(savedData.fontCol));
        //if the launch intent exists try and add an icon from it
        if (launchIntent.length()>1 && inflater!=R.layout.options_item) {
            //if it's an options menu item the image view will fail and skip this
            //attempt to add the icon from the launchIntent
            if (appName.equals("hItem")){
                String[] icons = launchIntent.split(":");
                ((ImageView) child.findViewById(R.id.item_app_icon)).setImageBitmap(new imgLoader(this, icons[1].trim(), manager, savedData.useGoogleIcons).doInBackground());
            }
            else {
                ((ImageView) child.findViewById(R.id.item_app_icon)).setImageBitmap(new imgLoader(this, launchIntent, manager, false).doInBackground());
            }
        }

        //setup the onclick listener and button
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appName.equals("hItem")){
                    if (optionsMenu) {
                        changeOptionsMenu.menuClose(EternalMediaBar.this);
                        optionsMenu = false;
                        listMove(index, true);
                    }
                    else{
                        listMove(index, true);
                    }
                }
                else if (launchIntent.equals(".options")){
                    if (optionsMenu){
                        changeOptionsMenu.menuClose(EternalMediaBar.this);
                        optionsMenu = false;
                    }
                    else {

                        listMove(index, false);
                        //load the layout and make sure nothing is in it.
                        changeOptionsMenu.menuOpen(EternalMediaBar.this, false, launchIntent, appName);
                    }
                }
                else {
                    if (isLaunchable) {
                        if (secondaryIndex==1){
                            changeOptionsMenu.menuOpen(EternalMediaBar.this, true, launchIntent, appName);
                        }
                        else {
                            EternalMediaBar.this.startActivity(manager.getLaunchIntentForPackage(launchIntent));
                        }
                    }
                    else {
                        //choose which list to make dependant on the values given for the call.
                        switch (index) {
                            case -1:{/*/ Null Case /*/}
                            //menu open and close
                            case 0:{changeOptionsMenu.menuClose(EternalMediaBar.this); break;}
                            case 1:{changeOptionsMenu.menuOpen(EternalMediaBar.this, false, launchIntent, appName);break;}
                            //copy, hide and move menus
                            case 2:{changeOptionsMenu.createCopyList(EternalMediaBar.this, launchIntent, appName);break;}
                            case 3:{changeOptionsMenu.createMoveList(EternalMediaBar.this, launchIntent, appName);break;}
                            case 4:{changeOptionsMenu.copyItem(EternalMediaBar.this, secondaryIndex);break;}
                            case 5:{changeOptionsMenu.moveItem(EternalMediaBar.this, secondaryIndex);break;}
                            case 6:{changeOptionsMenu.hideApp(EternalMediaBar.this);break;}
                            //open app settings
                            case 7:{startActivity(changeOptionsMenu.openAppSettings(EternalMediaBar.this, launchIntent));break;}
                            //toggles
                            case 8:{changeOptionsMenu.toggleGoogleIcons(EternalMediaBar.this);break;}
                            case 9:{changeOptionsMenu.mirrorUI(EternalMediaBar.this);break;}
                            case 13:{changeOptionsMenu.toggleDimLists(EternalMediaBar.this);break;}
                            //list organize
                            case 11:{changeOptionsMenu.listOrganizeSelect(EternalMediaBar.this,  secondaryIndex, launchIntent, appName);break;}
                            case 12:{changeOptionsMenu.organizeList(EternalMediaBar.this, secondaryIndex);break;}
                            //cases for changing colors
                            case 10:{changeOptionsMenu.colorSelect(EternalMediaBar.this, appName, secondaryIndex);break;}
                        }
                    }
                }
            }
        });

        child.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
            if (optionsMenu){
                changeOptionsMenu.menuClose(EternalMediaBar.this);
            }
            else{
                listMove(index, appName.equals("hItem"));
            }
            changeOptionsMenu.menuOpen(EternalMediaBar.this, isLaunchable, launchIntent, appName);
            return true;
            }
        });

        //return the view value
        return child;
    }

    public View searchItem(String title, final String url, String description){
        View child = getLayoutInflater().inflate(R.layout.web_search_item, null);

        TextView webLabel = (TextView) child.findViewById(R.id.item_title);
        webLabel.setText(title);
        webLabel.setTextColor(savedData.fontCol);
        webLabel.setAlpha(Color.alpha(savedData.fontCol));
        webLabel.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);

        TextView webURL = (TextView) child.findViewById(R.id.item_url);
        webURL.setText(url);
        webURL.setTextColor(savedData.fontCol);
        webURL.setAlpha(Color.alpha(savedData.fontCol));
        webURL.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        TextView webDescription = (TextView) child.findViewById(R.id.item_description);
        webDescription.setText(description);
        webDescription.setTextColor(savedData.fontCol);
        webDescription.setAlpha(Color.alpha(savedData.fontCol));
        webDescription.setPaintFlags(Paint.ANTI_ALIAS_FLAG);

        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("https://", "http://"))));
            }
        });

        return child;
    }

}
