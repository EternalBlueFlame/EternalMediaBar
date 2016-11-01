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

    /**
     * define the layout sizes here so we don't have to process each more than once for the life of the app.
     * NOTE: it may be a decent idea to define the offsets statically like this as well, that way the values don't have to be calculated for every item. I am unsure if it will effect the RAM use however, needs further testing.
     */
    private static final ViewGroup.LayoutParams layout24 = new LinearLayout.LayoutParams(Math.round(24 * EternalMediaBar.dpi.scaledDensity), Math.round(24 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout48 = new LinearLayout.LayoutParams(Math.round(48 * EternalMediaBar.dpi.scaledDensity), Math.round(48 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout34 = new LinearLayout.LayoutParams(Math.round(34 * EternalMediaBar.dpi.scaledDensity), Math.round(34 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout50 = new LinearLayout.LayoutParams(Math.round(50 * EternalMediaBar.dpi.scaledDensity), Math.round(50 * EternalMediaBar.dpi.scaledDensity));
    private static final ViewGroup.LayoutParams layout28 = new LinearLayout.LayoutParams(Math.round(28 * EternalMediaBar.dpi.scaledDensity), Math.round(28 * EternalMediaBar.dpi.scaledDensity));


    /**
     * <h2>Normal List Item View</h2>
     *
     * this function generates a view that contains the layout, image view, text, and the onClick functionality.
     * usually used for apps on the main list and in the search bar.
     *
     * The image is defined through an AsyncImageView via:
     * @see ImgLoader#ProcessInput(String, String)
     *
     * @param menuItem the AppDetail being displayed
     * @param index the index of the menu, used with:
     *              @see EternalMediaBar#listMove(int)
     * @param isSearch if this item is in the searchbar's list.
     * @return the View to draw.
     */
    public static View appListItemView (final AppDetail menuItem, final int index, boolean isSearch){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(54 * EternalMediaBar.dpi.scaledDensity));
        final AsyncImageView image = new AsyncImageView(menuItem.internalCommand, menuItem.URI , layout34, 10 * EternalMediaBar.dpi.scaledDensity, 10 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);

        layout.addView(image.icon);
        layout.addView(image.selectedIcon);

        if (!isSearch) {
            layout.addView(GenLabel(menuItem.label, 2, Gravity.START, 54, 9, 120));
        } else{
            TextView text = GenLabel(menuItem.label, 2, Gravity.START, 54, 9, 120);
            text.setWidth(Math.round(EternalMediaBar.dpi.widthPixels * 0.65f));
            layout.addView(text);
        }

        /**
         * define the on click manager,
         * if you are copying or moving an app then add or remove this from the list dependant on whether or not this is selected.
         * if the options menu is open but you are not copying or moving, just close options menu.
         * otherwise, if double tap is on, move to this item, and if you are already on this item or double tap is off, parse the URI to figure out how to run it.
         */
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EternalMediaBar.copyingOrMoving) {
                    if (EternalMediaBar.selectedApps.contains(menuItem.URI)) {
                        EternalMediaBar.selectedApps.remove(menuItem.URI);
                        image.selectedIcon.setVisibility(View.INVISIBLE);
                    } else {
                        EternalMediaBar.selectedApps.add(menuItem.URI);
                        image.selectedIcon.setVisibility(View.VISIBLE);
                    }
                } else if (!EternalMediaBar.optionsMenu) {
                    if (EternalMediaBar.savedData.doubleTap && EternalMediaBar.vItem != index) {
                        EternalMediaBar.listMove(index);
                    } else {
                        switch (menuItem.URI) {
                            case ".options": {
                                EternalMediaBar.listMove(index);
                                OptionsMenuChange.menuOpen(new AppDetail("Eternal Media Bar - Settings", ".options"), R.id.SETTINGS);
                                break;
                            }
                            case ".finance": {
                                EternalMediaBar.listMove(index);
                                EternalMediaBar.activity.startActivity(new Intent(EternalMediaBar.activity, EternalFinance.class));
                                break;
                            }
                            case ".audio": {
                                Intent musicIntent = new Intent();
                                musicIntent.setAction(android.content.Intent.ACTION_VIEW);
                                musicIntent.setDataAndType(Uri.fromFile(new File(menuItem.internalCommand)), "audio/*");
                                EternalMediaBar.activity.startActivity(musicIntent);
                                break;
                            }
                            default: {
                                EternalMediaBar.activity.startActivity(EternalMediaBar.manager.getLaunchIntentForPackage(menuItem.URI));
                                break;
                            }
                        }
                    }
                } else {
                    OptionsMenuChange.menuClose(false);
                    EternalMediaBar.optionsMenu = false;
                }
            }
        });

        /**
         * define the long click functionality.
         * this just closes options menu if it's open, or opens the options menu for this entry.
         */
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose(false);
                    EternalMediaBar.optionsMenu = false;
                } else {
                    EternalMediaBar.listMove(index);
                    OptionsMenuChange.menuOpen(menuItem, R.id.APP);
                }
                return true;
            }
        });

        //finally return the root view, and all it's children as a single view.
        return layout.getRootView();
    }


    /**
     * <h2>Search Provider Item View</h2>
     *
     * this is used to draw the search providers in the search bar.
     * this is very similar to:
     * @see ListItemLayout#appListItemView(AppDetail, int, boolean)
     * @param menuItem the AppDetail being displayed
     * @return the View to draw.
     */
    public static View searchView (final AppDetail menuItem){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(58 * EternalMediaBar.dpi.scaledDensity));
        AsyncImageView image = new AsyncImageView(menuItem.internalCommand, menuItem.URI, layout34, 8 * EternalMediaBar.dpi.scaledDensity, 20 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        layout.addView(image.icon);
        layout.addView(image.selectedIcon);

        layout.addView(GenLabel(menuItem.label, 1, Gravity.CENTER, 0, 34, 70));

        /**
         * unlike the normal menu item we don't compensate for the double tap function, because these items have no index.
         * besides that it's the same.
         */
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EternalMediaBar.copyingOrMoving && !EternalMediaBar.optionsMenu) {
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
                            EternalUtil.searchIntent(":audio:" + menuItem.internalCommand);
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

        return layout.getRootView();
    }


    /**
     * <h2>Widget View</h2>
     * similar to the other views, however because widgets define their own onClickListener normally, we only have to define the onLongClick listener for editing it.
     * @param widget the widget to display/edit
     *               @see Widget
     * @return the view to draw.
     */
    public static View loadWidget(final Widget widget){
        AppWidgetProviderInfo appWidgetInfo = EternalMediaBar.mAppWidgetManager.getAppWidgetInfo(widget.ID);
        AppWidgetHostView hostView = EternalMediaBar.mAppWidgetHost.createView(EternalMediaBar.activity, widget.ID, appWidgetInfo);
        hostView.setAppWidget(widget.ID, appWidgetInfo);
        hostView.setX(widget.X);
        hostView.setY(widget.Y);
        hostView.setMinimumWidth(widget.width);
        hostView.setMinimumHeight(widget.height);
        for (View childView : hostView.getTouchables()) {
            childView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (EternalMediaBar.optionsMenu) {
                        OptionsMenuChange.menuClose(false);
                        EternalMediaBar.optionsMenu = false;
                    } else {
                        EternalMediaBar.editingWidget = widget;
                        OptionsMenuChange.menuOpen(new AppDetail(), R.id.WIDGET);
                    }
                    return false;
                }
            });
        }
        return hostView;
    }


    /**
     * <h2> Category Item View</h2>
     *
     * basically the same as the normal Item View
     * @see ListItemLayout#appListItemView(AppDetail, int, boolean)
     *
     * @param category the category to display
     * @param index the position in the list that this item displays
     * @return the view to draw.
     */

    public static View categoryListItemView (final CategoryClass category, final int index){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setMinimumHeight(Math.round(80 * EternalMediaBar.dpi.scaledDensity));
        AsyncImageView image = new AsyncImageView("",category.categoryIcon,layout50 ,
                4 * EternalMediaBar.dpi.scaledDensity, 19 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        layout.addView(image.icon);

        layout.addView(GenLabel(category.categoryName, 1, Gravity.CENTER,0, 45, 84));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EternalMediaBar.optionsMenu) {
                    OptionsMenuChange.menuClose(false);
                    EternalMediaBar.optionsMenu = false;
                } else {
                    EternalMediaBar.categoryListMove(index);
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
                    OptionsMenuChange.menuOpen(new AppDetail(category.categoryName, index + ""), R.id.CATEGORY);
                }
                return true;
            }
        });

        return layout.getRootView();
    }


    /**
     * <h2> search category display</h2>
     *
     * basically the same as the normal Item View except we don't use any listeners because we dont want user interaction with this, its only cosmetic.
     * @see ListItemLayout#appListItemView(AppDetail, int, boolean)
     *
     * @param category the category to display
     * @return the view to draw
     */
    public static View searchCategoryItemView (CategoryClass category){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        layout.setBackgroundColor(0xff333333);
        layout.setMinimumHeight(Math.round(20 * EternalMediaBar.dpi.scaledDensity));
        AsyncImageView image = new AsyncImageView("",category.categoryIcon, layout28,
                4 * EternalMediaBar.dpi.scaledDensity, 4 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
        layout.addView(image.icon);
        layout.addView(GenLabel(category.categoryName, 1, Gravity.START,34, 6, 115));

        return layout.getRootView();
    }


    /**
     * <h2> Options List Item View</h2>
     *
     * this view is a lot more dynamic than others, the same methods as normal are used but they change a lot depending on circumstance.
     * @see ListItemLayout#appListItemView(AppDetail, int, boolean)
     *
     * @param text the text to display
     * @param index defines the command to run onClick.
     * @param secondaryIndex defines an int that may be used for the onClick
     * @param menuItem the AppDetail to interact with, in some cases it may be null, or used to interact with the onClick.
     * @return the View to display.
     */
    public static View optionsListItemView (CharSequence text, final int index, final int secondaryIndex, final AppDetail menuItem){

        RelativeLayout layout = new RelativeLayout(EternalMediaBar.activity);
        /**
         * Define the image view and text. We use different views and sizes dependant on the internal command from the AppDetail provided.
         */
        final AsyncImageView image;
        switch (menuItem.internalCommand){
            case ".radioUnCheck":case ".radioCheck": {
                layout.setMinimumHeight(Math.round(70 * EternalMediaBar.dpi.scaledDensity));
                image = new AsyncImageView("",menuItem.internalCommand, layout24,
                        10 * EternalMediaBar.dpi.scaledDensity, 16 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
                layout.addView(image.icon);
                layout.addView(GenLabel(text, 2, Gravity.CENTER, 26, 2, 90));
                break;
            }
            case ".optionsHeader": {
                layout.setMinimumHeight(Math.round(95 * EternalMediaBar.dpi.scaledDensity));
                image = new AsyncImageView("",menuItem.URI, layout48,
                        6 * EternalMediaBar.dpi.scaledDensity, 36 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
                layout.addView(image.icon);
                layout.addView(GenLabel(text, 2, Gravity.CENTER, 2, 48, 115));
                break;
            }
            default:{
                if (index == R.id.ACTION_UNHIDE){
                    image = new AsyncImageView(menuItem.internalCommand, menuItem.URI ,layout34,
                            10 * EternalMediaBar.dpi.scaledDensity, 10 * EternalMediaBar.dpi.scaledDensity, R.id.list_item_icon, true);
                    layout.addView(image.icon);
                    layout.addView(image.selectedIcon);
                } else {
                    layout.setMinimumHeight(Math.round(70 * EternalMediaBar.dpi.scaledDensity));
                    layout.addView(GenLabel(text, 2, Gravity.START, 12, 24, 115));
                }
                break;
            }

        }

        /**
         * this is a very complex onClick listener that will decide what to do based on the ID in the index.
         * The name of the ID will hint towards the use to make it easier to skim.
         * but for the most part it's best to check the class of the function being run in the case.
         * usually it is from either:
         * @see EternalUtil
         * @see OptionsMenuChange
         */
        if(index!=R.id.NULL) {
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (index) {
                        case R.id.CLOSE: {
                            OptionsMenuChange.menuClose(false);break;}
                        case R.id.CLOSE_AND_SAVE: {
                            OptionsMenuChange.menuClose(true);break;}
                        case R.id.APP: case R.id.SETTINGS: {
                            OptionsMenuChange.menuOpen(menuItem, index);break;}

                        case R.id.COPY_LIST: case R.id.MOVE_LIST:{
                            OptionsMenuChange.createCopyList(menuItem, index);
                            Toast.makeText(EternalMediaBar.activity, "Select the apps you want to move\nThen select where to move them.", Toast.LENGTH_LONG).show();
                            break;
                        }
                        case R.id.HIDE_LIST:{
                            OptionsMenuChange.unhideList(menuItem);break;
                        }
                        case R.id.ACTION_COPY: case R.id.ACTION_MOVE: case R.id.ACTION_HIDE: case R.id.ACTION_UNHIDE: {
                            EternalUtil.relocateItem(secondaryIndex, index);break;
                        }
                        case R.id.ACTION_REMOVE:{
                            EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(EternalMediaBar.vItem);
                            OptionsMenuChange.menuClose(true); break;
                        }

                        case R.id.ACTION_APP_SYSTEM_SETTINGS:{
                            EternalUtil.openAppSettings(menuItem);break;
                        }
                        case R.id.ACTION_URL:{
                            EternalMediaBar.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.URI)));break;
                        }

                        case R.id.THEME_SELECT: {
                            OptionsMenuChange.themeChange(menuItem);break;
                        }
                        case R.id.ACTION_THEME_CHANGE:{
                            EternalMediaBar.savedData.theme = menuItem.URI;
                            OptionsMenuChange.themeChange(new AppDetail("Eternal Media Bar - Settings", ".options"));
                            break;
                        }
                        case R.id.COLOR_APP_BG: case R.id.COLOR_OPTIONS: case R.id.COLOR_FONT: case R.id.COLOR_ICON:{
                            OptionsMenuChange.colorSelect(index);break;
                        }
                        case R.id.COLOR_ACTION_CANCEL:{
                            OptionsMenuChange.cancelColorSelect(secondaryIndex, menuItem); break;
                        }
                        case R.id.COLOR_MENU: {
                            OptionsMenuChange.themeColorChange(menuItem);break;
                        }
                        case R.id.ORGANIZE_MENU: {
                            OptionsMenuChange.listOrganizeSelect(menuItem);break;
                        }
                        case R.id.ACTION_ORGANIZE: {
                            EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode = secondaryIndex;
                            OptionsMenuChange.listOrganizeSelect(menuItem);
                            Toast.makeText(EternalMediaBar.activity, "Changes will take effect\nwhen you exit the menu", Toast.LENGTH_SHORT).show();
                            break;
                        }

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


    /**
     * <h2>Make a label</h2>
     * this will create a TextView label to draw to screen.
     * @param text the text to display
     * @param lines the max number of lines
     * @param gravity the alignment.
     *                @see Gravity
     * @param x the horizontal position
     * @param y the vertical position
     * @param width the width of the view.
     * @return the view to draw.
     */
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
