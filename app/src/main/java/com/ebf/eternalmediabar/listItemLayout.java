package com.ebf.eternalmediabar;

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

    //the design of the web search is too vastly different to cover in this, so we will manage that elsewhere
    public View appListItemView (CharSequence text, final int index, final int secondaryIndex, final Boolean isLaunchable, final String launchIntent, final String appName){

        //create dpi as a variable ahead of time so we dont have to calculate this over and over for every item in the layout,
        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(60 * dpi));
        //create the icon if we can, be sure its instantiated ahead of time so we can clean it out
        ImageView image = new ImageView(EternalMediaBar.activity);
        if (launchIntent.length() > 1 && (isLaunchable || launchIntent.equals(".options"))) {
            //create the image and set its values
            //there ar two different sizes for icons, one for the search header, and another for everything else
            image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(48 * dpi), Math.round(48 * dpi)));
            image.setY(6 * dpi);
            image.setX(6 * dpi);
            image.setId(R.id.list_item_icon);
            image.setAdjustViewBounds(true);
            //now add the actual image
            image.setImageBitmap(new imgLoader(launchIntent).doInBackground());
            layout.addView(image);
        }
        //now add the text
        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(2);
        //because of how dynamic text has to be, we define the text first, and everything else second.
        appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        appLabel.setX(62 * dpi);
        appLabel.setY((12 * dpi));
        appLabel.setId(R.id.list_item_text);
        appLabel.setWidth(Math.round(115 * dpi));
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        layout.addView(appLabel);

        //set the on click listener, but if it's the search header, there sin't one for that
            //setup the onclick listener and button
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (launchIntent.equals(".options")) {
                        if (EternalMediaBar.activity.optionsMenu) {
                            new optionsMenuChange().menuClose();
                            EternalMediaBar.activity.optionsMenu = false;
                        } else {

                            EternalMediaBar.activity.listMove(index, false);
                            //load the layout and make sure nothing is in it.
                            new optionsMenuChange().menuOpen(false, launchIntent, appName);
                        }
                    } else {
                        if (isLaunchable) {
                            if (secondaryIndex == 1) {
                                new optionsMenuChange().menuOpen(true, launchIntent, appName);
                            } else {
                                EternalMediaBar.activity.startActivity(EternalMediaBar.activity.manager.getLaunchIntentForPackage(launchIntent));
                            }
                        }
                    }
                }
            });

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


        return layout.getRootView();
    }



    ////////////////////////////////////////////////////////////
    //////////////////// Category List Item ////////////////////
    ////////////////////////////////////////////////////////////

    //the design of the web search is too vastly different to cover in this, so we will manage that elsewhere
    public View categoryListItemView (CharSequence text, final int index, final String launchIntent){

        //create dpi as a variable ahead of time so we don't have to calculate this over and over for every item in the layout,
        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(62 * dpi));
        //create the icon
        ImageView image = new ImageView(EternalMediaBar.activity);
        //there ar two different sizes for icons, one for the search header, and another for everything else
        image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(50 * dpi), Math.round(50 * dpi)));
        image.setY(4 * dpi);
        image.setX(16 * dpi);
        image.setId(R.id.list_item_icon);
        image.setAdjustViewBounds(true);
        //now add the actual image
        String[] icons = launchIntent.split(":");
        image.setImageBitmap(new imgLoader(icons[1].trim()).doInBackground());
        layout.addView(image);

        //now add the text
        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(2);
        //because of how dynamic text has to be, we define the text first, and everything else second.
        appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        appLabel.setY((35 * dpi));
        appLabel.setId(R.id.list_item_text);
        appLabel.setGravity(Gravity.CENTER);
        appLabel.setWidth(Math.round(80 * dpi));
        appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        layout.addView(appLabel);

        //set the on click listener, but if it's the search header, there sin't one for that
        //setup the onclick listener and button
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

    //the design of the web search is too vastly different to cover in this, so we will manage that elsewhere
    public View searchCategoryItemView (CharSequence text, final String launchIntent){

        //create dpi as a variable ahead of time so we dont have to calculate this over and over for every item in the layout,
        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setBackgroundColor(0xff333333);
        layout.setMinimumHeight(Math.round(28 * dpi));
        //create the icon, be sure it's not made on options menus or the web search
        //create the image and set its values
        ImageView image = new ImageView(EternalMediaBar.activity);
        //there ar two different sizes for icons, one for the search header, and another for everything else
        image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(24 * dpi), Math.round(24 * dpi)));
        image.setY(4 * dpi);
        image.setX(4 * dpi);
        image.setId(R.id.list_item_icon);
        image.setAdjustViewBounds(true);
        //now add the actual image
        String[] icons = launchIntent.split(":");
        image.setImageBitmap(new imgLoader(icons[1].trim()).doInBackground());
        layout.addView(image);

        //now add the text
        TextView appLabel = new TextView(EternalMediaBar.activity);
        appLabel.setText(text);
        appLabel.setLines(2);
        //because of how dynamic text has to be, we define the text first, and everything else second.
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

    //the design of the web search is too vastly different to cover in this, so we will manage that elsewhere
    public View optionsListItemView (CharSequence text, final int index, final int secondaryIndex, final String launchIntent, final String appName){

        //create dpi as a variable ahead of time so we don't have to calculate this over and over for every item in the layout,
        float dpi =EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        //create the icon, be sure it's not made on options menus or the web search
        if (launchIntent.equals(".radioUnCheck") || launchIntent.equals(".radioCheck")) {
            layout.setMinimumHeight(Math.round(60 * dpi));
            //create the image and set its values
            ImageView image = new ImageView(EternalMediaBar.activity);
            //there ar two different sizes for icons, one for the search header, and another for everything else
            image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(24 * dpi), Math.round(24 * dpi)));
            image.setY(10 * dpi);
            image.setX(16 * dpi);
            image.setId(R.id.list_item_icon);
            image.setAdjustViewBounds(true);
            //now add the actual image
            image.setImageBitmap(new imgLoader(launchIntent).doInBackground());

            layout.addView(image);

            //now add the text
            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            //because of how dynamic text has to be, we define the text first, and everything else second.
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
        else if(appName.equals(".optionsHeader")){
            layout.setMinimumHeight(Math.round(85 * dpi));
            //create the image and set its values
            ImageView image = new ImageView(EternalMediaBar.activity);
            //there ar two different sizes for icons, one for the search header, and another for everything else
            image.setLayoutParams(new LinearLayout.LayoutParams(Math.round(48 * dpi), Math.round(48 * dpi)));
            image.setY(6 * dpi);
            image.setX(36 * dpi);
            image.setId(R.id.list_item_icon);
            image.setAdjustViewBounds(true);
            //now add the actual image
            image.setImageBitmap(new imgLoader(launchIntent).doInBackground());

            layout.addView(image);

            //now add the text
            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            //because of how dynamic text has to be, we define the text first, and everything else second.
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
            layout.setMinimumHeight(Math.round(60 * dpi));
            //now add the text
            TextView appLabel = new TextView(EternalMediaBar.activity);
            appLabel.setText(text);
            appLabel.setLines(2);
            //because of how dynamic text has to be, we define the text first, and everything else second.
            appLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
            appLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
            appLabel.setX(12 * dpi);
            appLabel.setY((24 * dpi));
            appLabel.setId(R.id.list_item_text);
            appLabel.setWidth(Math.round(115 * dpi));
            appLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            layout.addView(appLabel);
        }

        //set the on click listener, but if it's the search header, there sin't one for that
        if(!appName.equals(".searchHeader")) {
            //setup the onclick listener and button
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //choose which list to make dependant on the values given for the call.
                    switch (index) {
                        case -1: {/*/ Null Case /*/}
                        //menu open and close
                        case 0: {
                            new optionsMenuChange().menuClose();
                            break;
                        }
                        case 1: {
                            new optionsMenuChange().menuOpen(false, launchIntent, appName);
                            break;
                        }
                        //copy, hide and move menus
                        case 2: {
                            new optionsMenuChange().createCopyList(launchIntent, appName);
                            break;
                        }
                        case 3: {
                            new optionsMenuChange().createMoveList(launchIntent, appName);
                            break;
                        }
                        case 4: {
                            new optionsMenuChange().copyItem(secondaryIndex);
                            break;
                        }
                        case 5: {
                            new optionsMenuChange().moveItem(secondaryIndex);
                            break;
                        }
                        case 6: {
                            new optionsMenuChange().hideApp();
                            break;
                        }
                        //open app settings
                        case 7: {
                            EternalMediaBar.activity.startActivity( new optionsMenuChange().openAppSettings(launchIntent));
                            break;
                        }
                        //toggles
                        case 8: {
                            new optionsMenuChange().themeChange(launchIntent, appName);
                            break;
                        }
                        case 9: {
                            new optionsMenuChange().mirrorUI();
                            break;
                        }
                        case 13: {
                            new optionsMenuChange().toggleDimLists();
                            break;
                        }
                        case 14:{
                            new optionsMenuChange().setIconTheme(appName);
                            break;
                        }
                        //cases for changing colors
                        case 10: {
                            new optionsMenuChange().colorSelect(appName, secondaryIndex);
                            break;
                        }
                        //list organize
                        case 11: {
                            new optionsMenuChange().listOrganizeSelect(secondaryIndex, launchIntent, appName);
                            break;
                        }
                        case 12: {
                            new optionsMenuChange().organizeList(secondaryIndex);
                            break;
                        }
                    }
                }
            });
        }


        return layout.getRootView();
    }


    ////////////////////////////////////////////////////////////
    ////////////////// Web Search List Item ////////////////////
    ////////////////////////////////////////////////////////////


    public View webSearchItem(String title, final String url, String description) {

        //create dpi as a variable ahead of time so we don't have to calculate this over and over for every item in the layout,
        float dpi = EternalMediaBar.activity.getResources().getDisplayMetrics().density + 0.5f;
        //make the core layout
        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);

        layout.setMinimumHeight(Math.round(180 * dpi));

        //now add the text
        TextView webLabel = new TextView(EternalMediaBar.activity);
        webLabel.setText(title);
        //because of how dynamic text has to be, we define the text first, and everything else second.
        webLabel.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        webLabel.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        webLabel.setX(2 * dpi);
        webLabel.setY((2 * dpi));
        webLabel.setLines(2);
        webLabel.setWidth(Math.round(EternalMediaBar.activity.findViewById(R.id.searchView).getWidth() - (2 * dpi)));
        webLabel.setTextSize(8 * dpi);
        webLabel.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        webLabel.setBackgroundColor(0xff333333);
        layout.addView(webLabel);

        //now add the text
        TextView webURL = new TextView(EternalMediaBar.activity);
        webURL.setText(url);
        //because of how dynamic text has to be, we define the text first, and everything else second.
        webURL.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        webURL.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        webURL.setX(2 * dpi);
        webURL.setY(40 * dpi);
        webURL.setLines(2);
        webURL.setWidth(Math.round(EternalMediaBar.activity.findViewById(R.id.searchView).getWidth() - (2 * dpi)));
        webURL.setTextSize(7*dpi);
        webURL.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        layout.addView(webURL);

        //now add the text
        TextView webDescription = new TextView(EternalMediaBar.activity);
        webDescription.setText(description);
        //because of how dynamic text has to be, we define the text first, and everything else second.
        webDescription.setTextColor(EternalMediaBar.activity.savedData.fontCol);
        webDescription.setAlpha(Color.alpha(EternalMediaBar.activity.savedData.fontCol));
        webDescription.setX(2 * dpi);
        webDescription.setY(78 * dpi);
        webDescription.setWidth(Math.round(EternalMediaBar.activity.findViewById(R.id.searchView).getWidth() - (2 * dpi)));
        webDescription.setTextSize(8 * dpi);
        webDescription.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        layout.addView(webDescription);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("https://", "http://"))));
            }
        });

        return layout.getRootView();
    }

}
