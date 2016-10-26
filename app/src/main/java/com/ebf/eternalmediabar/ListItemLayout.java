package com.ebf.eternalmediabar;

import android.app.SearchManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.AsyncImageView;
import com.ebf.eternalVariables.CategoryClass;
import com.ebf.eternalVariables.Widget;
import com.ebf.eternalfinance.EternalFinance;

import java.io.File;


public class ListItemLayout {

    private static final ViewGroup.LayoutParams layout24 = new LinearLayout.LayoutParams(Math.round(24 * EternalMediaBar.dpi.scaledDensity), Math.round(24 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout48 = new LinearLayout.LayoutParams(Math.round(48 * EternalMediaBar.dpi.scaledDensity), Math.round(48 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout34 = new LinearLayout.LayoutParams(Math.round(34 * EternalMediaBar.dpi.scaledDensity), Math.round(34 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout50 = new LinearLayout.LayoutParams(Math.round(50 * EternalMediaBar.dpi.scaledDensity), Math.round(50 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout28 = new LinearLayout.LayoutParams(Math.round(28 * EternalMediaBar.dpi.scaledDensity), Math.round(28 * EternalMediaBar.dpi.scaledDensity));


    ////////////////////////////////////////////////////////////
    ////////////////////// App List Item ///////////////////////
    ////////////////////////////////////////////////////////////

    //this function is generally the same for each version on this script, so the meanings will only be commented on when it's actually different.
    public static View appListItemView (final AppDetail menuItem, final int index, boolean isSearch){

        //create EternalMediaBar.dpi.scaledDensity as a variable ahead of time so we don't have to calculate this over and over. This is because we scale things by EternalMediaBar.dpi.scaledDensity rather than pixels.
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(54 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        final AsyncImageView image = new AsyncImageView(menuItem.internalCommand, menuItem.URI , layout34, 10 * EternalMediaBar.dpi.scaledDensity, 10 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now add the progress view to the display, then process the image view and add it to the display.

        layout.addView(image.icon);
        layout.addView(image.selectedIcon);

        if (!isSearch) {
            layout.addView(GenLabel(menuItem.label, 2, Gravity.START, 54, 9, 120));
        } else{
            TextView text = GenLabel(menuItem.label, 2, Gravity.START, 54, 9, 120);
            text.setWidth(Math.round(EternalMediaBar.dpi.widthPixels * 0.65f));
            layout.addView(text);
        }

        //setup the onclick listener and button
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the options menu is closed, then close the menu
                if (EternalMediaBar.copyingOrMoving) {
                    //now add the progress view to the display, then process the image view and add it to the display.
                    if (EternalMediaBar.selectedApps.contains(menuItem.URI)){
                        EternalMediaBar.selectedApps.remove(menuItem.URI);
                        image.selectedIcon.setVisibility(View.INVISIBLE);
                    } else {
                        EternalMediaBar.selectedApps.add(menuItem.URI);
                        image.selectedIcon.setVisibility(View.VISIBLE);
                    }
                } else if (!EternalMediaBar.optionsMenu) {
                    //otherwise act normally.
                    //if double tap is enabled, be sure the item is selected before it can be opened by clicking it.
                    if (EternalMediaBar.savedData.doubleTap && EternalMediaBar.vItem != index) {
                        EternalMediaBar.activity.listMove(index, false);
                    } else {
                        //if double tap is off, or this is the position for the app, or both, open it.
                        //if its the options button, open the options menu
                        switch (menuItem.URI){
                            case ".options":{
                                EternalMediaBar.activity.listMove(index, false);
                                //use a blank value for the AppDetail to be absolutely sure we don't break anything.
                                OptionsMenuChange.menuOpen(new AppDetail("Eternal Media Bar - Settings", ".options"), R.id.SETTINGS);
                                break;
                            }
                            case ".finance":{
                                EternalMediaBar.activity.startActivity(new Intent(EternalMediaBar.activity, EternalFinance.class));
                                break;
                            }
                            case ".audio":{
                                Intent musicIntent = new Intent();
                                musicIntent.setAction(android.content.Intent.ACTION_VIEW);
                                musicIntent.setDataAndType(Uri.fromFile(new File(menuItem.internalCommand)), "audio/*");
                                EternalMediaBar.activity.startActivity(musicIntent);
                                break;
                            }
                            default:{
                                //if it's not the options menu then try to open the app
                                EternalMediaBar.activity.startActivity(EternalMediaBar.manager.getLaunchIntentForPackage(menuItem.URI));
                                break;
                            }
                        }
                    }
                }
            }
        });

        //define the function for long click, this closes the options menu if it's open, or opens the settings menu for an app.
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose(false);
                    EternalMediaBar.optionsMenu = false;
                } else {
                    EternalMediaBar.activity.listMove(index, false);
                    OptionsMenuChange.menuOpen(menuItem, R.id.APP);
                }
                return true;
            }
        });

        //finally return the root view, and all it's children as a single view.
        return layout.getRootView();
    }



    ////////////////////////////////////////////////////////////
    ////////////////////// App List Item ///////////////////////
    ////////////////////////////////////////////////////////////

    //this function is generally the same for each version on this script, so the meanings will only be commented on when it's actually different.
    public static View searchView (final AppDetail menuItem, final int index){

        //create EternalMediaBar.dpi.scaledDensity as a variable ahead of time so we don't have to calculate this over and over. This is because we scale things by EternalMediaBar.dpi.scaledDensity rather than pixels.
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(58 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView(menuItem.internalCommand, menuItem.URI, layout34, 8 * EternalMediaBar.dpi.scaledDensity, 20 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now add the progress view to the display, then process the image view and add it to the display.
        layout.addView(image.icon);
        layout.addView(image.selectedIcon);

        //now add the text similar to the image
        layout.addView(GenLabel(menuItem.label, 1, Gravity.CENTER, 0, 34, 70));

        //setup the onclick listener and button
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the options menu is closed, then close the menu
                if (!EternalMediaBar.copyingOrMoving && !EternalMediaBar.optionsMenu) {
                    //otherwise act normally.
                    switch (menuItem.URI) {
                        case ".webSearch": {
                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                            intent.putExtra(SearchManager.QUERY, menuItem.internalCommand);
                            EternalMediaBar.activity.startActivity(intent);
                            break;
                        }
                        case ".storeSearch": {
                            EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + menuItem.internalCommand + "&c=apps")));
                            break;
                        }
                        case ".musicSearch": {
                            EternalMediaBar.activity.searchIntent(":audio:" + menuItem.internalCommand);
                            break;
                        }
                        case ".ytSearch": {
                            try {
                                Intent intent = new Intent(Intent.ACTION_SEARCH);
                                intent.setPackage("com.google.android.youtube");
                                intent.putExtra("query", menuItem.internalCommand);
                                EternalMediaBar.activity.startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/results?search_query=" + menuItem.internalCommand)));
                            }
                            break;
                        }
                        case ".mapSearch": {
                            try {
                                Intent intent = new Intent(Intent.ACTION_SEARCH);
                                intent.setPackage("com.google.android.apps.maps");
                                intent.putExtra("query", menuItem.internalCommand);
                                EternalMediaBar.activity.startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/maps/search/" + menuItem.internalCommand)));
                            }
                            break;
                        }
                    }
                }
            }
        });
        //finally return the root view, and all it's children as a single view.
        return layout.getRootView();
    }


    ////////////////////////////////////////////////////////////
    /////////////////////// Widget Item ////////////////////////
    ////////////////////////////////////////////////////////////
    public static View loadWidget(final Widget widget){
        AppWidgetProviderInfo appWidgetInfo = EternalMediaBar.mAppWidgetManager.getAppWidgetInfo(widget.ID);
        AppWidgetHostView hostView = EternalMediaBar.mAppWidgetHost.createView(EternalMediaBar.activity, widget.ID, appWidgetInfo);
        hostView.setAppWidget(widget.ID, appWidgetInfo);
        hostView.setX(widget.X);
        hostView.setY(widget.Y);
        hostView.setMinimumWidth(widget.width);
        hostView.setMinimumHeight(widget.height);
        hostView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose(false);
                    EternalMediaBar.optionsMenu = false;
                } else {
                    OptionsMenuChange.menuOpen(new AppDetail(), R.id.WIDGET);
                    EternalMediaBar.editingWidget = widget;
                }
                return false;
            }
        });
        return hostView;
    }


    ////////////////////////////////////////////////////////////
    //////////////////// Category List Item ////////////////////
    ////////////////////////////////////////////////////////////

    public static View categoryListItemView (final CategoryClass category, final int index){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(80 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView(ImgLoader.ProcessInput("",category.categoryIcon),layout50 ,
                4 * EternalMediaBar.dpi.scaledDensity, 19 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now process the image view and add it to the display.
        layout.addView(image.icon);

        layout.addView(GenLabel(category.categoryName, 1, Gravity.CENTER,0, 45, 84));

        //on click this just changes the category, unless the options menu is open, then it coses options.
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose(false);
                    EternalMediaBar.optionsMenu = false;
                } else {
                    EternalMediaBar.activity.listMove(index, true);
                }
            }
        });

        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose(false);
                    EternalMediaBar.optionsMenu = false;
                } else {
                    //instead of making an entirely different version of menuOpen for category, we'll just convert the unique data to an AppDetail for us to parse later
                    OptionsMenuChange.menuOpen(new AppDetail(category.categoryName, index + ""), R.id.CATEGORY);
                }
                return true;
            }
        });

        return layout.getRootView();
    }




    ////////////////////////////////////////////////////////////
    ///////////////////// Search List Item /////////////////////
    ////////////////////////////////////////////////////////////


    public static View searchCategoryItemView (CategoryClass category){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        //in the search category the background is tinted a different color, and the icon size is smaller, there's also no action when it's clicked. Beyond that, it's more of the same
        layout.setBackgroundColor(0xff333333);
        layout.setMinimumHeight(Math.round(20 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView("",category.categoryIcon, layout28,
                4 * EternalMediaBar.dpi.scaledDensity, 4 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now process the image view and add it to the display.
        layout.addView(image.icon);
        //add the text
        layout.addView(GenLabel(category.categoryName, 1, Gravity.START,34, 6, 115));

        return layout.getRootView();
    }

    ////////////////////////////////////////////////////////////
    //////////////////// Options List Item /////////////////////
    ////////////////////////////////////////////////////////////

    public static View optionsListItemView (CharSequence text, final int index, final int secondaryIndex, final AppDetail menuItem){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        //in the options menu, besides the header, only radio buttons have icons, so check if it's a radio button before worrying about adding an icon
        final AsyncImageView image;
        switch (menuItem.internalCommand){
            case ".radioUnCheck":case ".radioCheck": {
                layout.setMinimumHeight(Math.round(70 * EternalMediaBar.dpi.scaledDensity));
                //create the icon base using the async image loader
                image = new AsyncImageView("",menuItem.internalCommand, layout24,
                        10 * EternalMediaBar.dpi.scaledDensity, 16 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
                //now process the image view and add it to the display.
                layout.addView(image.icon);

                layout.addView(GenLabel(text, 2, Gravity.CENTER, 26, 2, 90));
                break;
            }
            case ".optionsHeader": {
                layout.setMinimumHeight(Math.round(95 * EternalMediaBar.dpi.scaledDensity));

                //create the icon base using the async image loader
                image = new AsyncImageView("",menuItem.URI, layout48,
                        6 * EternalMediaBar.dpi.scaledDensity, 36 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
                //now process the image view and add it to the display.
                layout.addView(image.icon);
                layout.addView(GenLabel(text, 2, Gravity.CENTER, 2, 48, 115));
                break;
            }
            default:{
                if (index == R.id.ACTION_UNHIDE){
                    //create the icon base using the async image loader
                    image = new AsyncImageView(menuItem.internalCommand, menuItem.URI ,layout34,
                            10 * EternalMediaBar.dpi.scaledDensity, 10 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
                    //now add the progress view to the display, then process the image view and add it to the display.
                    layout.addView(image.icon);
                    layout.addView(image.selectedIcon);
                } else {
                    //if it's not either of the above, we won't need to deal with the image, just the text.
                    layout.setMinimumHeight(Math.round(70 * EternalMediaBar.dpi.scaledDensity));
                    layout.addView(GenLabel(text, 2, Gravity.START, 12, 24, 115));
                }
                break;
            }

        }

        //this part is generic to all three menus, so we write it after the non-generic parts are set.


        //if it's not the header then setup the click cunctionality redirects
        if(index!=R.id.NULL) {
            //this is where it gets tricky, options menu items, have a LOT of redirects to other functions, based on what they do. This is defined on creation of the menu item with the index variable.
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (index) {
                        //menu close
                        case R.id.CLOSE: {
                            OptionsMenuChange.menuClose(false);break;}
                        case R.id.CLOSE_AND_SAVE: {
                            OptionsMenuChange.menuClose(true);break;}
                        //menu open for if it's an app's settings, and if it's just the options menu
                        case R.id.APP: case R.id.SETTINGS: {
                            OptionsMenuChange.menuOpen(menuItem, index);break;}

                        //case for making the list of categories for moving or copying.
                        case R.id.COPY_LIST: case R.id.MOVE_LIST:{
                            OptionsMenuChange.createCopyList(menuItem, index);break;
                        }
                        case R.id.HIDE_LIST:{
                            OptionsMenuChange.unhideList(menuItem);break;
                        }
                        //cases for copying, moving, and hiding apps.
                        case R.id.ACTION_COPY: case R.id.ACTION_MOVE: case R.id.ACTION_HIDE: case R.id.ACTION_UNHIDE: {
                            EternalUtil.relocateItem(secondaryIndex, index);break;
                        }
                        case R.id.ACTION_REMOVE:{
                            EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(EternalMediaBar.vItem);
                            OptionsMenuChange.menuClose(true); break;
                        }
                        //case for opening app's system settings
                        case R.id.ACTION_APP_SYSTEM_SETTINGS:{
                            EternalUtil.openAppSettings(menuItem);break;
                        }
                        //case for opening a URL
                        case R.id.ACTION_URL:{
                            EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.URI)));break;
                        }
                        //cases for theme changing
                        case R.id.THEME_SELECT: {
                            OptionsMenuChange.themeChange(menuItem);break;
                        }
                        case R.id.ACTION_THEME_CHANGE:{//reload the list, we have to change the menu item back to the default options menu item
                            EternalMediaBar.savedData.theme = menuItem.URI;
                            OptionsMenuChange.themeChange(new AppDetail("Eternal Media Bar - Settings", ".options"));
                            break;
                        }
                        //cases for changing colors
                        case R.id.COLOR_APP_BG: case R.id.COLOR_OPTIONS: case R.id.COLOR_FONT: case R.id.COLOR_ICON:{
                            OptionsMenuChange.colorSelect(index);break;
                        }
                        case R.id.COLOR_ACTION_CANCEL:{
                            OptionsMenuChange.cancelColorSelect(secondaryIndex, menuItem); break;
                        }
                        case R.id.COLOR_MENU: {
                            OptionsMenuChange.themeColorChange(menuItem);break;
                        }
                        //list organize
                        case R.id.ORGANIZE_MENU: {
                            OptionsMenuChange.listOrganizeSelect(menuItem);break;
                        }
                        case R.id.ACTION_ORGANIZE: {
                            EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode = secondaryIndex;
                            OptionsMenuChange.listOrganizeSelect(menuItem);
                            Toast.makeText(EternalMediaBar.activity, "Changes will take effect\nwhen you exit the menu", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        //cases for toggles
                        case R.id.ACTION_MIRROR: {
                            EternalMediaBar.savedData.mirrorMode = ! EternalMediaBar.savedData.mirrorMode;
                            OptionsMenuChange.menuClose(true);break;
                        }
                        case R.id.ACTION_DOUBLE_TAP: {
                            EternalMediaBar.savedData.doubleTap = ! EternalMediaBar.savedData.doubleTap;
                            OptionsMenuChange.menuClose(true);break;
                        }
                        case R.id.ACTION_ADD_WIDGET: {
                            EternalMediaBar.activity.selectWidget();
                        }
                    }
                }
            });
        }


        return layout.getRootView();
    }


    private static TextView GenLabel(CharSequence text, int lines, int gravity, int x, int y, int width){
        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(lines);
        appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
        appLabel.setId(R.id.list_item_text);
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        appLabel.setX(x * EternalMediaBar.dpi.scaledDensity);
        appLabel.setY(y * EternalMediaBar.dpi.scaledDensity);
        appLabel.setWidth(Math.round(width * EternalMediaBar.dpi.scaledDensity));
        appLabel.setGravity(gravity);

        return appLabel;

    }
}
