package com.ebf.eternalmediabar;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;
import com.ebf.eternalVariables.Widget;

import java.util.ArrayList;
import java.util.List;


public class EternalMediaBar extends Activity {

    public static PackageManager manager;
    public static SettingsClass savedData = new SettingsClass();

    public static int hItem = 0;
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

    public static AppWidgetManager mAppWidgetManager;
    public static AppWidgetHost mAppWidgetHost;

    public static DisplayMetrics dpi = new DisplayMetrics();

    public static List<String> selectedApps = new ArrayList<>();

    //we have to instance the event receiver so we can get rid of it when the app is not open.
    EternalUtil.intentReceiver mainReciever = new EternalUtil.intentReceiver();


    /**
     * <h2>App Initialization</h2>
     *
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        getWindowManager().getDefaultDisplay().getMetrics(dpi);
        manager = getPackageManager();
        activity = this;
        Initialization.loadData(this);
        if (vItem > savedData.categories.get(hItem).appList.size()){
            vItem = 0;
        }
        loadListView();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        filter.addAction("android.hardware.usb.action.USB_STATE");
        filter.addAction("android.intent.action.HDMI_PLUGGED");
        registerReceiver(mainReciever, filter);
        getPerms();
    }



    /**
     * <h2> Android 6+ permissions check</h2>
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void getPerms() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }
    }

    /**
     * <h2> on close</h2>
     * unregister the recievers and hosts before trimming the memory to save RAM and CPU when in background.
     */
    @Override
    protected void onPause() {
        unregisterReceiver(mainReciever);
        mAppWidgetHost.stopListening();
        onTrimMemory(TRIM_MEMORY_COMPLETE);
        super.onPause();
    }

    ///@Override
    //protected void onStop() {
    //    super.onStop();
    //}


    /**
     * <h2>Input handler</h2>
     *
     * Interpret keys pressed for menu navigation.
     *
     * @param keyCode key code defined by the OS
     * @param event key press event defined by the OS.
     * @return true if a known/used key was pressed
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("Key Pressed: ", "" + keyCode);
        switch (keyCode) {
            //case event for down
            case KeyEvent.KEYCODE_S: case KeyEvent.KEYCODE_DPAD_DOWN: case KeyEvent.KEYCODE_4: case KeyEvent.KEYCODE_NUMPAD_4: {listMove(vItem + 1);return true;}
            //case event for up
            case KeyEvent.KEYCODE_W: case KeyEvent.KEYCODE_DPAD_UP: case KeyEvent.KEYCODE_2: case KeyEvent.KEYCODE_NUMPAD_2:{listMove(vItem - 1);return true;}
            //case event for right
            case KeyEvent.KEYCODE_D: case KeyEvent.KEYCODE_DPAD_RIGHT: case KeyEvent.KEYCODE_6: case KeyEvent.KEYCODE_NUMPAD_6:{categoryListMove(hItem + 1);return true;}
            //case event for left
            case KeyEvent.KEYCODE_A: case KeyEvent.KEYCODE_DPAD_LEFT: case KeyEvent.KEYCODE_8: case KeyEvent.KEYCODE_NUMPAD_8: {categoryListMove(hItem - 1);return true;}
            //event for when enter/x/a is pressed
			case KeyEvent.KEYCODE_ENTER: case KeyEvent.KEYCODE_NUMPAD_ENTER: case KeyEvent.KEYCODE_1: case KeyEvent.KEYCODE_5: case KeyEvent.KEYCODE_NUMPAD_5: case KeyEvent.KEYCODE_BUTTON_1: {
                if (!optionsMenu) {
                    //get the item in the layout and activate its button function
                    ((LinearLayout)findViewById(R.id.apps_display)).getChildAt(vItem).performClick();
                } else{
                    ((LinearLayout)findViewById(R.id.optionslist)).getChildAt(optionVitem).performClick();
                }
				return true;
			}
            //event for when E/Y/Triangle is pressed
			case KeyEvent.KEYCODE_BUTTON_4: case KeyEvent.KEYCODE_E: case KeyEvent.KEYCODE_TAB: case KeyEvent.KEYCODE_0: case KeyEvent.KEYCODE_NUMPAD_0: {
                if (!optionsMenu) {
                    //get the item in the layout and activate its button function
                    ((LinearLayout)findViewById(R.id.apps_display)).getChildAt(vItem).performLongClick();
                } else{
                    OptionsMenuChange.menuClose(false);
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

    /**
     * <h2> Select a new menu item</h2>
     *
     * this is separated into two functions, one for verticle and one for horizontal.
     * @see EternalMediaBar#categoryListMove(int)
     *
     * @param move position to move to
     */
    public static void listMove(int move){
        if (move >=0) {
            if (!optionsMenu) {
                if (move < appsLayout.getChildCount()) {
                    ImageView appIcon;
                    if (vItem>0) {
                        //change the old font face
                        ((TextView) appsLayout.getChildAt(vItem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                        //scale the icon back to normal
                        appIcon = (ImageView) appsLayout.getChildAt(vItem).findViewById(R.id.list_item_icon);
                        appIcon.setScaleX(1f);
                        appIcon.setScaleY(1f);
                    }
                    //change vItem then basically do inverse of above
                    vItem = move;
                    ((TextView) appsLayout.getChildAt(vItem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                    appIcon = (ImageView) appsLayout.getChildAt(vItem).findViewById(R.id.list_item_icon);
                    appIcon.setScaleX(1.5f);
                    appIcon.setScaleY(1.5f);

                    //scroll to the new entry
                    EternalMediaBar.activity.findViewById(R.id.apps_displayscroll).scrollTo(0, (int) (appsLayout.getChildAt(vItem).getY() - (EternalMediaBar.activity.findViewById(R.id.apps_displayscroll).getHeight() * 0.4F)));
                }
            } else if (move < optionsLayout.getChildCount()) {
                //move is defined by vitem plus a modifier, but we need it for the options menu, so we have to subtract the vitem value then add the current optionVitem value in it's place.
                move -= vItem;
                move += optionVitem;
                //from here, it's gonna basically be the same as the above, except we don't zoom the icon.
                ((TextView) optionsLayout.getChildAt(optionVitem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                optionVitem = move;
                ((TextView) optionsLayout.getChildAt(optionVitem).findViewById(R.id.list_item_text)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
                EternalMediaBar.activity.findViewById(R.id.options_displayscroll).scrollTo(0, (int) (optionsLayout.getChildAt(optionVitem).getY() - (EternalMediaBar.activity.findViewById(R.id.options_displayscroll).getHeight() * 0.4F)));
            }
        }
    }

    public static void categoryListMove(int move){
        if (move >= 0 && move < categoriesLayout.getChildCount()) {
            /**
             * This one is significantly simplified from the other versions because by changing categories we reload everything, so actually, the changes are handled in
             * @see EternalMediaBar#loadListView()
             */
            hItem = move;
            EternalMediaBar.activity.loadListView();
            EternalMediaBar.activity.findViewById(R.id.categoriesscroll).scrollTo((int) (categoriesLayout.getChildAt(optionVitem).getX() - (EternalMediaBar.activity.findViewById(R.id.categoriesscroll).getWidth() * 0.4F)), 0);
        }
    }




    //////////////////////////////////////////////////
    ///////Function to draw all the information///////
    //////////////////////////////////////////////////
    public void loadListView(){

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
                EternalUtil.searchIntent(newText);
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
        count =0;
        //now define the apps list

        for (AppDetail app : savedData.categories.get(hItem).appList) {
            appsLayout.addView(ListItemLayout.appListItemView(app, count, false));
            count++;
        }
        //make sure the vList item is selected
        listMove(0);

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        if (savedData.widgets.size()>0){
            for (Widget widget : savedData.widgets) {
                ((RelativeLayout)EternalMediaBar.activity.findViewById(R.id.mainlayout)).addView(ListItemLayout.loadWidget(widget));
            }
            mAppWidgetHost.startListening();
        }

        Runtime.getRuntime().gc();
    }


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
    public void selectWidget() {
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //This avoids a bug in the com.android.settings.AppWidgetPickActivity by adding empty extras to the intent.
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, new ArrayList<AppWidgetProviderInfo>());
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, new ArrayList<Bundle>());

        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }

}
