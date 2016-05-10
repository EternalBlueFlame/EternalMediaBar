package com.ebf.eternalmediabar;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class listItemLayout {



    ////////////////////////////////////////////////////////////
    ////////////////////// App List Item ///////////////////////
    ////////////////////////////////////////////////////////////

    //this function is generally the same for each version on this script, so the meanings will only be commented on when it's actually different.
    public View appListItemView (CharSequence text, final int index, final int secondaryIndex, final Boolean isLaunchable, final String launchIntent, final String appName){

        final int position = ((LinearLayout)EternalMediaBar.activity.findViewById(R.id.apps_display)).getChildCount();
        //create dpi as a variable ahead of time so we dont have to calculate this over and over. This is because we scale things by DPI rather than pixels.
        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(54 * dpi));
        //create the icon base
        ImageView image = new ImageView(EternalMediaBar.activity);
        //setup the image values like size and position.
        image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(34 * dpi), Math.round(34 * dpi)));
        image.setY(10 * dpi);
        image.setX(6 * dpi);
        image.setId(R.id.list_item_icon);
        image.setAdjustViewBounds(true);
        //now add the actual image and add it to the root view
        image.setImageBitmap(new imgLoader(launchIntent).doInBackground());
        layout.addView(image);

        //now add the text similar to the image
        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(2);
        //because of how dynamic text has to be, we define the text first, and everything else second.
        appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        appLabel.setX(46 * dpi);
        appLabel.setY((9 * dpi));
        appLabel.setId(R.id.list_item_text);
        appLabel.setWidth(Math.round(120 * dpi));
        //set the font size then add the text to the root view
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(appLabel);

        //setup the onclick listener and button
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the options menu is closed, then close the menu
                if (EternalMediaBar.activity.optionsMenu) {
                    new optionsMenuChange().menuClose();
                    EternalMediaBar.activity.optionsMenu = false;
                } else {
                    //otherwise act normally.
                    //if double tap is enabled, be sure the item is selected before it can be opened by clicking it.
                    if (EternalMediaBar.activity.vItem != position && EternalMediaBar.activity.savedData.doubleTap) {
                        EternalMediaBar.activity.listMove(position, false);
                    } else {
                        //if double tap is off, or this is the position for the app, or both, open it.
                        //if its the options button, open the options menu
                        if (launchIntent.equals(".options")) {
                            EternalMediaBar.activity.listMove(index, false);
                            new optionsMenuChange().menuOpen(false, launchIntent, appName);
                        }
                        else if(launchIntent.equals(".webSearch")){
                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                            intent.putExtra(SearchManager.QUERY, appName);
                            EternalMediaBar.activity.startActivity(intent);
                        }
                        else {
                            //if it's not the options menu then try to open the app
                            EternalMediaBar.activity.startActivity(EternalMediaBar.activity.manager.getLaunchIntentForPackage(launchIntent));
                        }
                    }
                }
            }
        });

        //define the function for long click, this closes the options menu if it's open, or opens the settings menu for an app.
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EternalMediaBar.activity.optionsMenu) {
                    new optionsMenuChange().menuClose();
                    EternalMediaBar.activity.optionsMenu = false;
                } else {
                    EternalMediaBar.activity.listMove(index, false);
                }
                new optionsMenuChange().menuOpen(isLaunchable, launchIntent, appName);
                return true;
            }
        });

        //finally return the root view, and all it's children as a single view.
        return layout.getRootView();
    }



    ////////////////////////////////////////////////////////////
    //////////////////// Category List Item ////////////////////
    ////////////////////////////////////////////////////////////

    public View categoryListItemView (CharSequence text, final int index, final String launchIntent){

        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(62 * dpi));
        ImageView image = new ImageView(EternalMediaBar.activity);
        image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(50 * dpi), Math.round(50 * dpi)));
        image.setY(4 * dpi);
        image.setX(16 * dpi);
        image.setId(R.id.list_item_icon);
        image.setAdjustViewBounds(true);
        String[] icons = launchIntent.split(":");
        image.setImageBitmap(new imgLoader(icons[1].trim()).doInBackground());
        layout.addView(image);

        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(2);
        appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        appLabel.setY((35 * dpi));
        appLabel.setId(R.id.list_item_text);
        appLabel.setGravity(Gravity.CENTER);
        appLabel.setWidth(Math.round(80 * dpi));
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(appLabel);

        //on click this just changes the category, unless the options menu is open, then it coses options.
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EternalMediaBar.activity.optionsMenu) {
                    new optionsMenuChange().menuClose();
                    EternalMediaBar.activity.optionsMenu = false;
                    EternalMediaBar.activity.listMove(index, true);
                } else {
                    EternalMediaBar.activity.listMove(index, true);
                }
            }
        });

        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EternalMediaBar.activity.optionsMenu) {
                    new optionsMenuChange().menuClose();
                    EternalMediaBar.activity.optionsMenu = false;
                }
                return true;
            }
        });

        return layout.getRootView();
    }




    ////////////////////////////////////////////////////////////
    ///////////////////// Search List Item /////////////////////
    ////////////////////////////////////////////////////////////


    public View searchCategoryItemView (CharSequence text, final String launchIntent){

        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        //in the search category the background is tinted a different color, and the icon size is smaller, there's also no action when it's clicked. Beyond that, it's more of the same
        layout.setBackgroundColor(0xff333333);
        layout.setMinimumHeight(Math.round(28 * dpi));
        ImageView image = new ImageView(EternalMediaBar.activity);
        image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(24 * dpi), Math.round(24 * dpi)));
        image.setY(4 * dpi);
        image.setX(4 * dpi);
        image.setId(R.id.list_item_icon);
        image.setAdjustViewBounds(true);
        String[] icons = launchIntent.split(":");
        image.setImageBitmap(new imgLoader(icons[1].trim()).doInBackground());
        layout.addView(image);

        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(2);
        appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        appLabel.setX(30 * dpi);
        appLabel.setY((6 * dpi));
        appLabel.setId(R.id.list_item_text);
        appLabel.setWidth(Math.round(115 * dpi));
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(appLabel);

        return layout.getRootView();
    }

    ////////////////////////////////////////////////////////////
    //////////////////// Options List Item /////////////////////
    ////////////////////////////////////////////////////////////

    public View optionsListItemView (CharSequence text, final int index, final int secondaryIndex, final String launchIntent, final String appName){

        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        //in the options menu, besides the header, only radio buttons have icons, so check if it's a radio button before worrying about adding an icon
        if (launchIntent.equals(".radioUnCheck") || launchIntent.equals(".radioCheck")) {
            layout.setMinimumHeight(Math.round(60 * dpi));
            ImageView image = new ImageView(EternalMediaBar.activity);
            image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(24 * dpi), Math.round(24 * dpi)));
            image.setY(10 * dpi);
            image.setX(16 * dpi);
            image.setId(R.id.list_item_icon);
            image.setAdjustViewBounds(true);
            image.setImageBitmap(new imgLoader(launchIntent).doInBackground());
            layout.addView(image);

            //the text also has to be repositioned with a radio item, so we have to define that stuff here as well.
            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
            appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
            appLabel.setX(26 * dpi);
            appLabel.setY((2 * dpi));
            appLabel.setId(R.id.list_item_text);
            appLabel.setWidth(Math.round(90 * dpi));
            appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            appLabel.setGravity(Gravity.CENTER);
            layout.addView(appLabel);
        }
        //if it is the header this also has a different layout and an image. so we have to define that accordingly, similar to the above.
        else if(appName.equals(".optionsHeader")){
            layout.setMinimumHeight(Math.round(85 * dpi));
            ImageView image = new ImageView(EternalMediaBar.activity);
            image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(48 * dpi), Math.round(48 * dpi)));
            image.setY(6 * dpi);
            image.setX(36 * dpi);
            image.setId(R.id.list_item_icon);
            image.setAdjustViewBounds(true);
            image.setImageBitmap(new imgLoader(launchIntent).doInBackground());
            layout.addView(image);

            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
            appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
            appLabel.setX(2 * dpi);
            appLabel.setY((48 * dpi));
            appLabel.setId(R.id.list_item_text);
            appLabel.setWidth(Math.round(115 * dpi));
            appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            appLabel.setGravity(Gravity.CENTER);

            layout.addView(appLabel);
        }
        else {
            //if it's not either of the above, we won't need to deal with the image, just the text.
            layout.setMinimumHeight(Math.round(60 * dpi));
            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
            appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
            appLabel.setX(12 * dpi);
            appLabel.setY((24 * dpi));
            appLabel.setId(R.id.list_item_text);
            appLabel.setWidth(Math.round(115 * dpi));
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
                        case 0: {new optionsMenuChange().menuClose();break;}
                        case 1: {new optionsMenuChange().menuOpen(false, launchIntent, appName);break;}
                        //copy, hide and move menus
                        case 2: {new optionsMenuChange().createCopyList(launchIntent, appName);break;}
                        case 3: {new optionsMenuChange().createMoveList(launchIntent, appName);break;}
                        case 4: {new optionsMenuChange().copyItem(secondaryIndex);break;}
                        case 5: {new optionsMenuChange().moveItem(secondaryIndex);break;}
                        case 6: {new optionsMenuChange().hideApp();break;}
                        //open app settings
                        case 7: {EternalMediaBar.activity.startActivity( new optionsMenuChange().openAppSettings(launchIntent));break;}
                        //open a URL
                        case 16: {EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(launchIntent)));break;}
                        //toggles
                        case 9: {EternalMediaBar.activity.savedData.mirrorMode = new optionsMenuChange().toggleBool(EternalMediaBar.activity.savedData.mirrorMode);break;}
                        case 13: {EternalMediaBar.activity.savedData.doubleTap = new optionsMenuChange().toggleBool(EternalMediaBar.activity.savedData.doubleTap);break;}
                        //cases for changing theme
                        case 8: {new optionsMenuChange().themeChange(launchIntent, appName);break;}
                        case 14:{new optionsMenuChange().setIconTheme(appName);break;}
                        //cases for changing colors
                        case 10: {new optionsMenuChange().colorSelect(appName, secondaryIndex);break;}
                        case 15: {new optionsMenuChange().themeColorChange(launchIntent, appName);break;}
                        //list organize
                        case 11: {new optionsMenuChange().listOrganizeSelect(secondaryIndex, launchIntent, appName);break;}
                        case 12: {new optionsMenuChange().organizeList(secondaryIndex);break;}
                    }
                }
            });
        }


        return layout.getRootView();
    }
}
