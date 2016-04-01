package com.ebf.eternalmediabar;



import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class initialization  extends AsyncTaskLoader<Boolean> {

    EternalMediaBar eternalMediaBar;

    public initialization(Context context, EternalMediaBar eternalMediaBar) {
        super(context);
        this.eternalMediaBar = eternalMediaBar;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Boolean loadInBackground() {
        //run once
        if (!eternalMediaBar.init) {
            if (eternalMediaBar.savedData.categories.size() <= 1) {
                try {
                    eternalMediaBar.savedData = eternalMediaBar.savedData.returnSettings(eternalMediaBar);


                } catch (Exception e) {
                        //the save data loader has compensation for any variables being missing, so we don't need to compensate for file not found.
                        eternalMediaBar.savedData.categories.add(new categoryClass());
                        eternalMediaBar.savedData.categories.add(new categoryClass());
                        eternalMediaBar.savedData.categories.add(new categoryClass());
                        eternalMediaBar.savedData.categories.add(new categoryClass());
                        eternalMediaBar.savedData.categories.add(new categoryClass());
                        eternalMediaBar.savedData.categories.add(new categoryClass());
                        eternalMediaBar.savedData.categories.add(new categoryClass());
                        //we should initialize the other variables as well.
                        eternalMediaBar.savedData.useGoogleIcons = false;
                        eternalMediaBar.savedData.mirrorMode = false;
                        eternalMediaBar.savedData.cleanCacheOnStart = false;
                        eternalMediaBar.savedData.gamingMode = false;
                        eternalMediaBar.savedData.useManufacturerIcons = false;
                        eternalMediaBar.savedData.loadAppBG = true;
                        eternalMediaBar.savedData.fontCol = -1;
                        eternalMediaBar.savedData.menuCol = -1;
                        eternalMediaBar.savedData.iconCol = -1;
                        eternalMediaBar.savedData.dimLists = true;
                        eternalMediaBar.savedData.hiddenApps = new ArrayList<>();
                        int[] tempInt = new int[]{0, 1, 1};
                        for (int i = 0; i < eternalMediaBar.savedData.categories.size(); ) {
                            eternalMediaBar.savedData.categories.get(i).organizeMode = tempInt;
                            switch (i) {
                                case 0: {
                                    eternalMediaBar.savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Communication", "Social", "Sports", "Education"));
                                    eternalMediaBar.savedData.categories.get(i).categoryName = "Social";
                                    break;
                                }
                                case 1: {
                                    eternalMediaBar.savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Music", "Video", "Entertainment", "Books", "Comics", "Photo"));
                                    eternalMediaBar.savedData.categories.get(i).categoryName = "Media";
                                    break;
                                }
                                case 2: {
                                    eternalMediaBar.savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Games"));
                                    eternalMediaBar.savedData.categories.get(i).categoryName = "Games";
                                    break;
                                }
                                case 3: {
                                    eternalMediaBar.savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Weather", "News", "Shopping", "Lifestyle", "Transportation", "Travel", "Web"));
                                    eternalMediaBar.savedData.categories.get(i).categoryName = "Web";
                                    break;
                                }
                                case 4: {
                                    eternalMediaBar.savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Business", "Finance", "Health", "Medical", "Productivity"));
                                    eternalMediaBar.savedData.categories.get(i).categoryName = "Utility";
                                    break;
                                }
                                case 5: {
                                    eternalMediaBar.savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Live Wallpaper", "Personalization", "Tools", "Widgets", "Libraries", "Android Wear"));
                                    eternalMediaBar.savedData.categories.get(i).categoryName = "Settings";
                                    break;
                                }
                                case 6: {
                                    eternalMediaBar.savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Unorganized"));
                                    eternalMediaBar.savedData.categories.get(i).categoryName = "New Apps";
                                    break;
                                }
                            }
                            eternalMediaBar.savedData.categories.get(i).categoryIcon = "" + (i + 1);
                            eternalMediaBar.savedData.categories.get(i).categoryGoogleIcon = "" + (i + 1);
                            i++;
                        }
                }
            }
            //we dont use this, but due to glitches in earlier revisions, there may be things in here, when it should be empty.
            eternalMediaBar.savedData.hiddenApps.clear();
            //setup the warning variable
            eternalMediaBar.warningToggle = new boolean[1];
            eternalMediaBar.warningToggle[0] = false;

            //make sure this doesn't happen again
            eternalMediaBar.init = true;
        }


        //////////////////////////////////////////////////
        ///////Figure out what is installed, or not///////
        //////////////////////////////////////////////////
        PackageManager manager = eternalMediaBar.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);
        //create a list of bools for checking what system apps are present.
        Boolean[] sysApps = new Boolean[]{false};

        //try to remove any apps that have invalid launch intents, unless it's marked as persistent.
        for (int i = 0; i < eternalMediaBar.savedData.categories.size(); ) {
            for (int ii = 0; ii < eternalMediaBar.savedData.categories.get(i).appList.size(); ) {
                //try to check if the launch intent is valid, if it's not, or the check fails, remove the app's entry.
                try {
                    if (manager.queryIntentActivities(manager.getLaunchIntentForPackage(eternalMediaBar.savedData.categories.get(i).appList.get(ii).name), PackageManager.MATCH_DEFAULT_ONLY).size() < 1) {
                        eternalMediaBar.savedData.categories.get(i).appList.remove(ii);
                        ii--;
                    }
                    //if the app was valid, iterate through the available activities to find the app's entry position, and remove it..
                    else {
                        for (int iii = 0; iii < availableActivities.size(); ) {
                            if (availableActivities.get(iii).activityInfo.packageName.equals(eternalMediaBar.savedData.categories.get(i).appList.get(ii).name)) {
                                availableActivities.remove(iii);
                                //now set the index of iii to break the loop, since we already found what we were looking for.
                                iii = availableActivities.size();
                            }
                            iii++;
                        }
                    }
                } catch (Exception e) {
                    if (eternalMediaBar.savedData.categories.get(i).appList.get(ii).name.equals(".options")) {
                        sysApps[0] = true;
                    } else if (!eternalMediaBar.savedData.categories.get(i).appList.get(ii).isPersistent) {
                        eternalMediaBar.savedData.categories.get(i).appList.remove(ii);
                    }
                }
                ii++;
            }
            i++;
        }

        //now check the list of bools and add any missing system apps.
        if (!sysApps[0]) {
            appDetail eternalSettings = new appDetail();
            eternalSettings.isPersistent = true;
            eternalSettings.label = "Eternal Media Bar - Settings";
            eternalSettings.name = ".options";
            for (int i = 0; i < eternalMediaBar.savedData.categories.size(); ) {
                if (eternalMediaBar.savedData.categories.get(i).categoryTags.contains("Tools")) {
                    eternalMediaBar.savedData.categories.get(i).appList.add(eternalSettings);
                    break;
                }
                i++;
            }
        }

        if (availableActivities.size() > 0) {
            for (ResolveInfo ri : availableActivities) {
                appDetail appRI = new appDetail();
                appRI.label = ri.loadLabel(manager);
                appRI.name = ri.activityInfo.packageName;
                appRI.isPersistent = false;
                appRI.icon = null;
                for (int i = 0; i < eternalMediaBar.savedData.categories.size(); ) {
                    if (eternalMediaBar.savedData.categories.get(i).categoryTags.contains("Unorganized")) {
                        eternalMediaBar.savedData.categories.get(i).appList.add(appRI);
                        break;
                    }
                    i++;
                }
            }
            availableActivities.clear();
        }
        eternalMediaBar.savedData.writeXML(eternalMediaBar.savedData, eternalMediaBar);
        return null;
    }
}