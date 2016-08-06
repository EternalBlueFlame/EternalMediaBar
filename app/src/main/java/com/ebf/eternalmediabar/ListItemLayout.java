package com.ebf.eternalmediabar;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ebf.eternalVariables.AsyncImageView;


public class ListItemLayout {



    ////////////////////////////////////////////////////////////
    ////////////////////// App List Item ///////////////////////
    ////////////////////////////////////////////////////////////

    //this function is generally the same for each version on this script, so the meanings will only be commented on when it's actually different.
    public static View appListItemView (CharSequence text, final int index, final Boolean isLaunchable, final String launchIntent, final String appName, boolean isSearch){

        final int position = ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.apps_display)).getChildCount();
        //create EternalMediaBar.dpi.scaledDensity as a variable ahead of time so we don't have to calculate this over and over. This is because we scale things by EternalMediaBar.dpi.scaledDensity rather than pixels.
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(54 * EternalMediaBar.dpi.scaledDensity));
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView(ImgLoader.ProcessInput(launchIntent) , new LinearLayout.LayoutParams(Math.round(34 * EternalMediaBar.dpi.scaledDensity), Math.round(34 * EternalMediaBar.dpi.scaledDensity)),
                10 * EternalMediaBar.dpi.scaledDensity, 10 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now add the progress view to the display, then process the image view and add it to the display.
        layout.addView(image.icon);

        //now add the text similar to the image
        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
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
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose();
                    EternalMediaBar.optionsMenu = false;
                } else {
                    //otherwise act normally.
                    //if double tap is enabled, be sure the item is selected before it can be opened by clicking it.
                    if (EternalMediaBar.vItem != position && EternalMediaBar.savedData.doubleTap) {
                        EternalMediaBar.activity.listMove(position, false);
                    } else {
                        //if double tap is off, or this is the position for the app, or both, open it.
                        //if its the options button, open the options menu
                        switch (launchIntent){
                            case ".options":{
                                EternalMediaBar.activity.listMove(index, false);
                                OptionsMenuChange.menuOpen(false, launchIntent, appName);
                                break;
                            }
                            case ".webSearch":{
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, appName);
                                EternalMediaBar.activity.startActivity(intent);
                                break;
                            }
                            case ".storeSearch":{
                                Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + appName +"&c=apps"));
                                EternalMediaBar.activity.startActivity(storeIntent);
                                break;
                            }
                            default:{
                                //if it's not the options menu then try to open the app
                                EternalMediaBar.activity.startActivity(EternalMediaBar.manager.getLaunchIntentForPackage(launchIntent));
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
                    OptionsMenuChange.menuOpen(isLaunchable, launchIntent, appName);
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

        String[] icons = launchIntent.split(":");
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView(icons[1].trim(), new LinearLayout.LayoutParams(Math.round(50 * EternalMediaBar.dpi.scaledDensity), Math.round(50 * EternalMediaBar.dpi.scaledDensity)),
                4 * EternalMediaBar.dpi.scaledDensity, 16 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        //now process the image view and add it to the display.
        new ImgLoader().execute(image);
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
                    OptionsMenuChange.menuOpen(false, ".", ".category");
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
        String[] icons = launchIntent.split(":");
        //create the icon base using the async image loader
        AsyncImageView image = new AsyncImageView(icons[1].trim(), new LinearLayout.LayoutParams(Math.round(28 * EternalMediaBar.dpi.scaledDensity), Math.round(28 * EternalMediaBar.dpi.scaledDensity)),
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

    public static View optionsListItemView (CharSequence text, final int index, final int secondaryIndex, final String launchIntent, final String appName){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        //in the options menu, besides the header, only radio buttons have icons, so check if it's a radio button before worrying about adding an icon
        if (launchIntent.equals(".radioUnCheck") || launchIntent.equals(".radioCheck")) {
            layout.setMinimumHeight(Math.round(70 * EternalMediaBar.dpi.scaledDensity));

            //create the icon base using the async image loader
            AsyncImageView image = new AsyncImageView(launchIntent, new LinearLayout.LayoutParams(Math.round(24 * EternalMediaBar.dpi.scaledDensity), Math.round(24 * EternalMediaBar.dpi.scaledDensity)),
                    10 * EternalMediaBar.dpi.scaledDensity, 16 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
            //now process the image view and add it to the display.
            new ImgLoader().execute(image);
            layout.addView(image.icon);

            //the text also has to be repositioned with a radio item, so we have to define that stuff here as well.
            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
            appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
            appLabel.setX(26 * EternalMediaBar.dpi.scaledDensity);
            appLabel.setY((2 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setId(R.id.list_item_text);
            appLabel.setWidth(Math.round(90 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            appLabel.setGravity(Gravity.CENTER);
            layout.addView(appLabel);
        }
        //if it is the header this also has a different layout and an image. so we have to define that accordingly, similar to the above.
        else if(appName.equals(".optionsHeader")){
            layout.setMinimumHeight(Math.round(85 * EternalMediaBar.dpi.scaledDensity));

            //create the icon base using the async image loader
            AsyncImageView image = new AsyncImageView(launchIntent, new LinearLayout.LayoutParams(Math.round(48 * EternalMediaBar.dpi.scaledDensity), Math.round(48 * EternalMediaBar.dpi.scaledDensity)),
                    6 * EternalMediaBar.dpi.scaledDensity, 36 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
            //now process the image view and add it to the display.
            new ImgLoader().execute(image);
            layout.addView(image.icon);

            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
            appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
            appLabel.setX(2 * EternalMediaBar.dpi.scaledDensity);
            appLabel.setY((48 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setId(R.id.list_item_text);
            appLabel.setWidth(Math.round(115 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            appLabel.setGravity(Gravity.CENTER);

            layout.addView(appLabel);
        }
        else {
            //if it's not either of the above, we won't need to deal with the image, just the text.
            layout.setMinimumHeight(Math.round(70 * EternalMediaBar.dpi.scaledDensity));
            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            appLabel.setTextColor(EternalMediaBar.savedData.fontCol);
            appLabel.setAlpha(Color.alpha(EternalMediaBar.savedData.fontCol));
            appLabel.setX(12 * EternalMediaBar.dpi.scaledDensity);
            appLabel.setY((24 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setId(R.id.list_item_text);
            appLabel.setWidth(Math.round(115 * EternalMediaBar.dpi.scaledDensity));
            appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            layout.addView(appLabel);
        }

        //if it's not the header then setup the click cunctionality redirects
        if(!appName.equals(".searchHeader")) {
            //this is where it gets tricky, options menu items, have a LOT of redirects to other functions, based on what they do. This is defined on creation of the menu item with the index variable.
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (index) {
                        case -1: {/*/ Null Case /*/}
                        //menu open and close
                        case 0: {
                            OptionsMenuChange.menuClose();break;}
                        case 1: {
                            OptionsMenuChange.menuOpen(secondaryIndex==1, launchIntent, appName);break;}
                        //copy, hide and move menus
                        case 2: {
                            OptionsMenuChange.createCopyList(launchIntent, appName);break;}
                        case 3: {
                            OptionsMenuChange.createMoveList(launchIntent, appName);break;}
                        case 4: {
                            OptionsMenuChange.copyItem(secondaryIndex);break;}
                        case 5: {
                            OptionsMenuChange.moveItem(secondaryIndex);break;}
                        case 6: {
                            OptionsMenuChange.hideApp();break;}
                        //open app settings
                        case 7: {EternalMediaBar.activity.startActivity(OptionsMenuChange.openAppSettings(launchIntent));break;}
                        //open a URL
                        case 16: {EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(launchIntent)));break;}
                        //toggles
                        case 9: {EternalMediaBar.savedData.mirrorMode = OptionsMenuChange.toggleBool(EternalMediaBar.savedData.mirrorMode); EternalMediaBar.activity.loadListView();break;}
                        case 13: {EternalMediaBar.savedData.doubleTap = OptionsMenuChange.toggleBool(EternalMediaBar.savedData.doubleTap);break;}
                        //cases for changing theme
                        case 8: {
                            OptionsMenuChange.themeChange(launchIntent, appName);break;}
                        case 14:{
                            OptionsMenuChange.setIconTheme(appName);break;}
                        //cases for changing colors
                        case 10: {
                            OptionsMenuChange.colorSelect(appName, secondaryIndex);break;}
                        case 15: {
                            OptionsMenuChange.themeColorChange(launchIntent, appName);break;}
                        //list organize
                        case 11: {
                            OptionsMenuChange.listOrganizeSelect(secondaryIndex, launchIntent, appName);break;}
                        case 12: {
                            OptionsMenuChange.organizeList(secondaryIndex);break;}
                        case 17:{EternalMediaBar.selectedApps.add(appName);}
                        case 18:{}//TODO copy move list then for selected apps, move each app, in reverse order.
                    }
                }
            });
        }


        return layout.getRootView();
    }
}
