package com.ebf.eternalmediabar;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.AsyncImageView;

import java.io.File;


public class ListItemLayout {



    ////////////////////////////////////////////////////////////
    ////////////////////// App List Item ///////////////////////
    ////////////////////////////////////////////////////////////

    //this function is generally the same for each version on this script, so the meanings will only be commented on when it's actually different.
    public static View appListItemView (final AppDetail menuItem, final int index, boolean isSearch){

        final int position = ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.apps_display)).getChildCount();
        //create EternalMediaBar.dpi.scaledDensity as a variable ahead of time so we don't have to calculate this over and over. This is because we scale things by EternalMediaBar.dpi.scaledDensity rather than pixels.
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(54 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        final AsyncImageView image = new AsyncImageView(menuItem.internalCommand, menuItem.URI , new LinearLayout.LayoutParams(Math.round(34 * EternalMediaBar.dpi.scaledDensity), Math.round(34 * EternalMediaBar.dpi.scaledDensity)),
                10 * EternalMediaBar.dpi.scaledDensity, 10 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now add the progress view to the display, then process the image view and add it to the display.
        new ImgLoader().execute(image);
        layout.addView(image.icon);
        layout.addView(image.selectedIcon);

        //now add the text similar to the image
        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(menuItem.label);
        appLabel.setLines(2);
        //because of how dynamic text has to be, we define the text first, and everything else second.
        appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
        appLabel.setX(54 * EternalMediaBar.dpi.scaledDensity);
        appLabel.setY((9 * EternalMediaBar.dpi.scaledDensity));
        appLabel.setId(R.id.list_item_text);
        if (!isSearch) {
            appLabel.setWidth(Math.round(120 * EternalMediaBar.dpi.scaledDensity));
        } else{
            appLabel.setWidth(Math.round(EternalMediaBar.dpi.widthPixels * 0.65f));
        }
        //set the font size then add the text to the root view
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(appLabel);

        //setup the onclick listener and button
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the options menu is closed, then close the menu
                if (EternalMediaBar.copyingOrMoving) {
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
                    if (EternalMediaBar.vItem != position && EternalMediaBar.savedData.doubleTap) {
                        EternalMediaBar.activity.listMove(position, false);
                    } else {
                        //if double tap is off, or this is the position for the app, or both, open it.
                        //if its the options button, open the options menu
                        switch (menuItem.URI){
                            case ".options":{
                                EternalMediaBar.activity.listMove(index, false);
                                //use a blank value for the AppDetail to be absolutley sure we don't break anything.
                                OptionsMenuChange.menuOpen(new AppDetail("Eternal Media Bar - Settings", ".options", true), false);
                                break;
                            }
                            case ".webSearch":{
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, menuItem.label);
                                EternalMediaBar.activity.startActivity(intent);
                                break;
                            }
                            case ".storeSearch":{
                                Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + menuItem.label +"&c=apps"));
                                EternalMediaBar.activity.startActivity(storeIntent);
                                break;
                            }
                            case ".musicSearch":{
                                EternalMediaBar.activity.searchIntent(":audio:" + menuItem.internalCommand);
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
                    OptionsMenuChange.menuClose();
                    EternalMediaBar.optionsMenu = false;
                } else {
                    EternalMediaBar.activity.listMove(index, false);
                    OptionsMenuChange.menuOpen(menuItem, true);
                }
                return true;
            }
        });

        //finally return the root view, and all it's children as a single view.
        return layout.getRootView();
    }



    ////////////////////////////////////////////////////////////
    //////////////////// Category List Item ////////////////////
    ////////////////////////////////////////////////////////////

    public static View categoryListItemView (CharSequence text, final int index, final String launchIntent){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(75 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView(ImgLoader.ProcessInput("",launchIntent), new LinearLayout.LayoutParams(Math.round(50 * EternalMediaBar.dpi.scaledDensity), Math.round(50 * EternalMediaBar.dpi.scaledDensity)),
                4 * EternalMediaBar.dpi.scaledDensity, 16 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now process the image view and add it to the display.
        layout.addView(image.icon);

        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setSingleLine(true);
        appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
        appLabel.setY((50 * EternalMediaBar.dpi.scaledDensity));
        appLabel.setId(R.id.list_item_text);
        appLabel.setGravity(Gravity.CENTER);
        appLabel.setWidth(Math.round(80 * EternalMediaBar.dpi.scaledDensity));
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(appLabel);

        //on click this just changes the category, unless the options menu is open, then it coses options.
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose();
                    EternalMediaBar.optionsMenu = false;
                    EternalMediaBar.activity.listMove(index, true);
                } else {
                    EternalMediaBar.activity.listMove(index, true);
                }
            }
        });

        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose();
                    EternalMediaBar.optionsMenu = false;
                } else {
                    OptionsMenuChange.menuOpen(new AppDetail("","",".category",""), false);
                }
                return true;
            }
        });

        return layout.getRootView();
    }




    ////////////////////////////////////////////////////////////
    ///////////////////// Search List Item /////////////////////
    ////////////////////////////////////////////////////////////


    public static View searchCategoryItemView (CharSequence text, final String launchIntent){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        //in the search category the background is tinted a different color, and the icon size is smaller, there's also no action when it's clicked. Beyond that, it's more of the same
        layout.setBackgroundColor(0xff333333);
        layout.setMinimumHeight(Math.round(28 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView("",launchIntent, new LinearLayout.LayoutParams(Math.round(28 * EternalMediaBar.dpi.scaledDensity), Math.round(28 * EternalMediaBar.dpi.scaledDensity)),
                4 * EternalMediaBar.dpi.scaledDensity, 4 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now process the image view and add it to the display.
        new ImgLoader().execute(image);
        layout.addView(image.icon);

        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(2);
        appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
        appLabel.setX(34 * EternalMediaBar.dpi.scaledDensity);
        appLabel.setY((6 * EternalMediaBar.dpi.scaledDensity));
        appLabel.setId(R.id.list_item_text);
        appLabel.setWidth(Math.round(115 * EternalMediaBar.dpi.scaledDensity));
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(appLabel);

        return layout.getRootView();
    }

    ////////////////////////////////////////////////////////////
    //////////////////// Options List Item /////////////////////
    ////////////////////////////////////////////////////////////

    public static View optionsListItemView (CharSequence text, final int index, final int secondaryIndex, final AppDetail menuItem){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(70 * EternalMediaBar.dpi.scaledDensity));
        TextView appLabel = new TextView(EternalMediaBar.activity);
        //in the options menu, besides the header, only radio buttons have icons, so check if it's a radio button before worrying about adding an icon
        if (menuItem.internalCommand.equals(".radioUnCheck") || menuItem.internalCommand.equals(".radioCheck")) {
            //create the icon base using the async image loader
            AsyncImageView image = new AsyncImageView("",menuItem.internalCommand, new LinearLayout.LayoutParams(Math.round(24 * EternalMediaBar.dpi.scaledDensity), Math.round(24 * EternalMediaBar.dpi.scaledDensity)),
                    10 * EternalMediaBar.dpi.scaledDensity, 16 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
            //now process the image view and add it to the display.
            new ImgLoader().execute(image);
            layout.addView(image.icon);

            appLabel.setX(26 * EternalMediaBar.dpi.scaledDensity);
            appLabel.setY((2 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setWidth(Math.round(90 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setGravity(Gravity.CENTER);
        }
        //if it is the header this also has a different layout and an image. so we have to define that accordingly, similar to the above.
        else if(menuItem.internalCommand.equals(".optionsHeader")){
            layout.setMinimumHeight(Math.round(95 * EternalMediaBar.dpi.scaledDensity));

            //create the icon base using the async image loader
            AsyncImageView image = new AsyncImageView("",menuItem.URI, new LinearLayout.LayoutParams(Math.round(48 * EternalMediaBar.dpi.scaledDensity), Math.round(48 * EternalMediaBar.dpi.scaledDensity)),
                    6 * EternalMediaBar.dpi.scaledDensity, 36 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
            //now process the image view and add it to the display.
            new ImgLoader().execute(image);
            layout.addView(image.icon);

            appLabel.setX(2 * EternalMediaBar.dpi.scaledDensity);
            appLabel.setY((48 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setWidth(Math.round(115 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setGravity(Gravity.CENTER);
        }
        else {
            //if it's not either of the above, we won't need to deal with the image, just the text.
            appLabel.setX(12 * EternalMediaBar.dpi.scaledDensity);
            appLabel.setY((24 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setWidth(Math.round(115 * EternalMediaBar.dpi.scaledDensity));
        }
        //this part is generic to all three menus, so we write it after the non-generic parts are set.
        appLabel.setText(text);
        appLabel.setLines(2);
        appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
        appLabel.setId(R.id.list_item_text);
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(appLabel);

        //if it's not the header then setup the click cunctionality redirects
        if(index!=-1) {
            //this is where it gets tricky, options menu items, have a LOT of redirects to other functions, based on what they do. This is defined on creation of the menu item with the index variable.
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (index) {
                        //menu open and close
                        case 0: {
                            OptionsMenuChange.menuClose();break;}
                        //menu open for if it's an app's settings, and if it's just the options menu
                        case 1: {
                            OptionsMenuChange.menuOpen(menuItem, true);break;}
                        case 2: {
                            OptionsMenuChange.menuOpen(menuItem, false);break;}
                        //we leave case 3 open in case it is necessary for creating a new option menu to manage categories
                        //we leave case 4 open in case it is necessary for managing media on the device.
                        //we leave case 5 open in case it is necessary for managing constacts.
                        //cases 6 through 9 are for unforseen necessary options menus.

                        //case for making the list of categories for moving or copying.
                        case 10:{
                            OptionsMenuChange.createCopyList(menuItem, false);break;
                        }
                        case 11:{
                            OptionsMenuChange.createCopyList(menuItem, true);break;
                        }
                        case 12:{
                            break;//create hide apps menu designed similar to move/copy for ability to multi-select
                        }
                        //cases for copying, moving, and hiding apps.
                        case 13:{
                            OptionsMenuChange.relocateItem(secondaryIndex, false, false);break;
                        }
                        case 14:{
                            OptionsMenuChange.relocateItem(secondaryIndex, true, false);break;
                        }
                        case 15:{
                            OptionsMenuChange.relocateItem(secondaryIndex, false, true);break;
                        }
                        case 16:{
                            EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(EternalMediaBar.vItem);
                            OptionsMenuChange.menuClose(); break;
                        }
                        //case for opening app's system settings
                        case 17:{
                            EternalMediaBar.activity.startActivity(OptionsMenuChange.openAppSettings(menuItem));break;
                        }
                        //case for opening a URL
                        case 18:{
                            EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.URI)));break;
                        }
                        //cases for theme changing
                        case 19: {
                            OptionsMenuChange.themeChange(menuItem);break;
                        }
                        case 20:{//reload the list, we have to change the menu item back to the default options menu item
                            EternalMediaBar.savedData.theme = menuItem.URI;
                            OptionsMenuChange.themeChange(new AppDetail("Eternal Media Bar - Settings", ".options", true));
                            break;
                        }
                        //cases for changing colors
                        case 21: {
                            OptionsMenuChange.colorSelect(menuItem);break;
                        }
                        case 22:{
                            OptionsMenuChange.cancelColorSelect(secondaryIndex, menuItem); break;
                        }
                        case 23: {
                            OptionsMenuChange.themeColorChange(menuItem);break;
                        }
                        //list organize
                        case 24: {
                            OptionsMenuChange.listOrganizeSelect(menuItem);break;
                        }
                        case 25: {
                            EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode = secondaryIndex;
                            OptionsMenuChange.listOrganizeSelect(menuItem.setCommand(""));
                            Toast.makeText(EternalMediaBar.activity, "Changes will take effect\nwhen you exit the menu", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 26:{
                            if (secondaryIndex==-1){
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeAlways = false;
                                EternalUtil.organizeList();
                                OptionsMenuChange.listOrganizeSelect(menuItem.setCommand(""));
                                Toast.makeText(EternalMediaBar.activity, "Changes will take effect\nwhen you exit the menu", Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeAlways = true;
                                EternalUtil.organizeList();
                                OptionsMenuChange.listOrganizeSelect(menuItem.setCommand(""));
                                Toast.makeText(EternalMediaBar.activity, "Changes will take effect\nwhen you exit the menu", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        //cases for toggles
                        case 30: {
                            EternalMediaBar.savedData.mirrorMode = ! EternalMediaBar.savedData.mirrorMode;
                            OptionsMenuChange.menuClose();break;
                        }
                        case 31: {
                            EternalMediaBar.savedData.doubleTap = ! EternalMediaBar.savedData.doubleTap;
                            OptionsMenuChange.menuClose();break;
                        }
                        //null case
                        default:{break;}
                    }
                }
            });
        }


        return layout.getRootView();
    }
}
