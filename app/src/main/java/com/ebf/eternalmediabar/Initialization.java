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

    public static void loadData(EternalMediaBar eternalMediaBar) {
        if (EternalMediaBar.savedData.categories.size() < 1) {

            EternalMediaBar.savedData = SettingsClass.returnSettings(eternalMediaBar);
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
        for (int i = 0; i < EternalMediaBar.savedData.categories.size();) {
            for (int ii=0; ii < EternalMediaBar.savedData.categories.get(i).appList.size();) {
                //try to check if the launch intent is valid, if it's not, or the check fails, remove the app's entry.
                try {
                    //this will fail if the app is not valid, and then be handled by the catch.
                    if (manager.queryIntentActivities(manager.getLaunchIntentForPackage(EternalMediaBar.savedData.categories.get(i).appList.get(ii).URI), PackageManager.MATCH_DEFAULT_ONLY).size() < 1) {
                        EternalMediaBar.savedData.categories.get(i).appList.remove(ii);
                        ii--;
                    }
                    //if the app was valid, iterate through the available activities to find the app's entry position, and remove it..
                    else {
                        for (ResolveInfo activity : availableActivities ) {
                            if (activity.activityInfo.packageName.equals(EternalMediaBar.savedData.categories.get(i).appList.get(ii).URI)) {
                                availableActivities.remove(activity);
                                //now set the index of iii to break the loop, since we already found what we were looking for.
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    if (EternalMediaBar.savedData.categories.get(i).appList.get(ii).URI.equals(".options")) {
                        sysApps[0] = true;
                    } else if (!EternalMediaBar.savedData.categories.get(i).appList.get(ii).isPersistent) {
                        EternalMediaBar.savedData.categories.get(i).appList.remove(ii);
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
            for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Tools")) {
                    EternalMediaBar.savedData.categories.get(i).appList.add(eternalSettings);
                    break;
                }
                i++;
            }
        }
        //now add any remaining apps to the newly installed list
        if (availableActivities.size() > 0) {
            for (ResolveInfo ri : availableActivities) {
                AppDetail appRI = new AppDetail(ri.loadLabel(manager), ri.activityInfo.packageName, false);
                for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                    if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Unorganized")) {
                        EternalMediaBar.savedData.categories.get(i).appList.add(appRI);
                        break;
                    }
                    i++;
                }
            }
            availableActivities.clear();
        }
    }
}
