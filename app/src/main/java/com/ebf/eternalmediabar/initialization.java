package com.ebf.eternalmediabar;



import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Initialization {

    public Boolean loadData(EternalMediaBar eternalMediaBar) {
        if (eternalMediaBar.savedData.categories.size() < 1) {
            try {
                eternalMediaBar.savedData = eternalMediaBar.savedData.returnSettings(eternalMediaBar);
            } catch (Exception e) {
                //the save data loader has compensation for any variables being missing, so we don't need to compensate for file not found.
                int[] organize = new int[]{0, 1, 1};
                eternalMediaBar.savedData.categories.add(new CategoryClass(organize, "Social", "1", "1", new ArrayList<>(Arrays.asList("Communication", "Social", "Sports", "Education"))));
                eternalMediaBar.savedData.categories.add(new CategoryClass(organize, "Media", "2", "2",  new ArrayList<>(Arrays.asList("Music", "Video", "Entertainment", "Books", "Comics", "Photo"))));
                eternalMediaBar.savedData.categories.add(new CategoryClass(organize, "Games", "3", "3", Collections.singletonList("Games")));
                eternalMediaBar.savedData.categories.add(new CategoryClass(organize, "Web", "4", "4", new ArrayList<>(Arrays.asList("Weather", "News", "Shopping", "Lifestyle", "Transportation", "Travel", "Web"))));
                eternalMediaBar.savedData.categories.add(new CategoryClass(organize, "Utility", "5", "5", new ArrayList<>(Arrays.asList("Business", "Finance", "Health", "Medical", "Productivity"))));
                eternalMediaBar.savedData.categories.add(new CategoryClass(organize, "Settings", "6", "6", new ArrayList<>(Arrays.asList("Live Wallpaper", "Personalization", "Tools", "Widgets", "Libraries", "Android Wear"))));
                eternalMediaBar.savedData.categories.add(new CategoryClass(organize, "New Apps", "7", "7", Collections.singletonList("Unorganized")));
                //we should initialize the other variables as well.
                eternalMediaBar.savedData.theme = "Internal";
                eternalMediaBar.savedData.mirrorMode = false;
                eternalMediaBar.savedData.cleanCacheOnStart = false;
                eternalMediaBar.savedData.gamingMode = false;
                eternalMediaBar.savedData.loadAppBG = true;
                eternalMediaBar.savedData.doubleTap = false;
                eternalMediaBar.savedData.fontCol = 0xffffffff;
                eternalMediaBar.savedData.menuCol = 0xcc000000;
                eternalMediaBar.savedData.iconCol = 0xffffffff;
                eternalMediaBar.savedData.dimCol = 0x66000000;
                eternalMediaBar.savedData.hiddenApps = Collections.emptyList();
            }
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
        for (int i = 0; i < eternalMediaBar.savedData.categories.size();) {
            for (int ii=0; ii < eternalMediaBar.savedData.categories.get(i).appList.size();) {
                //try to check if the launch intent is valid, if it's not, or the check fails, remove the app's entry.
                try {
                    //this will fail if the app is not valid, and then be handled by the catch.
                    if (manager.queryIntentActivities(manager.getLaunchIntentForPackage(eternalMediaBar.savedData.categories.get(i).appList.get(ii).URI), PackageManager.MATCH_DEFAULT_ONLY).size() < 1) {
                        eternalMediaBar.savedData.categories.get(i).appList.remove(ii);
                        ii--;
                    }
                    //if the app was valid, iterate through the available activities to find the app's entry position, and remove it..
                    else {
                        for (ResolveInfo activity : availableActivities ) {
                            if (activity.activityInfo.packageName.equals(eternalMediaBar.savedData.categories.get(i).appList.get(ii).URI)) {
                                availableActivities.remove(activity);
                                //now set the index of iii to break the loop, since we already found what we were looking for.
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    if (eternalMediaBar.savedData.categories.get(i).appList.get(ii).URI.equals(".options")) {
                        sysApps[0] = true;
                    } else if (!eternalMediaBar.savedData.categories.get(i).appList.get(ii).isPersistent) {
                        eternalMediaBar.savedData.categories.get(i).appList.remove(ii);
                        ii--;
                    }
                }
                ii++;
            }
            i++;
        }

        //now check the list of bools and add any missing system apps.
        if (!sysApps[0]) {
            AppDetail eternalSettings = new AppDetail("Eternal Media Bar - Settings", ".options", true);
            for (int i = 0; i < eternalMediaBar.savedData.categories.size(); ) {
                if (eternalMediaBar.savedData.categories.get(i).categoryTags.contains("Tools")) {
                    eternalMediaBar.savedData.categories.get(i).appList.add(eternalSettings);
                    break;
                }
                i++;
            }
        }
        //now add any remaining apps to the newly installed list
        if (availableActivities.size() > 0) {
            for (ResolveInfo ri : availableActivities) {
                AppDetail appRI = new AppDetail(ri.loadLabel(manager), ri.activityInfo.packageName, false, null);
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
        return null;
    }
}