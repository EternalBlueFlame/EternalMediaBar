package com.ebf.eternalmediabar;



import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;

import java.util.List;


public class Initialization {

    /**
     * <h2> App Initialization</h2>
     * this is used to call the function for loading the settings class if necessary, and to double check if any installed apps are missing from the list or if any apps in the list are no longer installed.
     *
     */
    public static void loadData() {
        if (EternalMediaBar.savedData.categories.size() < 1) {
            EternalMediaBar.savedData = SettingsClass.returnSettings();
        }

        /**
         * get the list of installed apps,and do a loop for all the apps in the launcher.
         * check that the apps in the launcher are still valid, if they are not, then remove them.
         * if the app is invalid, check if it's a part of the launcher, if it's not, remove it.
         */
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = EternalMediaBar.manager.queryIntentActivities(intent, 0);
        Boolean[] sysApps = new Boolean[]{false,false};

        for (CategoryClass category : EternalMediaBar.savedData.categories) {
            for (int i=0; i< category.appList.size();) {
                switch (category.appList.get(i).URI){
                    case ".options":{
                        sysApps[0] = true;
                        break;
                    }
                    case ".finance":{
                        sysApps[1] = true;
                        break;
                    }
                    default:{
                        if (EternalMediaBar.manager.queryIntentActivities(EternalMediaBar.manager.getLaunchIntentForPackage(category.appList.get(i).URI), PackageManager.MATCH_DEFAULT_ONLY).size() >0){
                            for (ResolveInfo activity : availableActivities ) {
                                if (activity.activityInfo.packageName.equals(category.appList.get(i).URI)) {
                                    availableActivities.remove(activity);
                                    break;
                                }
                            }
                        } else {
                            category.appList.remove(category.appList.get(i));
                            i--;
                        }
                        break;
                    }
                }
                i++;
            }
        }

        //for every app that is part of this launcher that is missing, do a loop check to find the proper category to place the item and add it back.
        if (!sysApps[0]) {
            for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Tools")) {
                    EternalMediaBar.savedData.categories.get(i).appList.add(new AppDetail("Eternal Media Bar - Settings", ".options"));
                    break;
                }
                i++;
            }
        }
        if (!sysApps[1]) {
            for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Business")) {
                    EternalMediaBar.savedData.categories.get(i).appList.add(new AppDetail("Eternal Finance", ".finance"));
                    break;
                }
                i++;
            }
        }
        /**
         * lastly, for every app in the available activities that is left, add it to the unorganized category.
         * similar to how it was done for adding back apps from this launcher.
         */
        if (availableActivities.size() > 0) {
            for (ResolveInfo ri : availableActivities) {
                for (int i = 0; i < EternalMediaBar.savedData.categories.size(); ) {
                    if (EternalMediaBar.savedData.categories.get(i).categoryTags.contains("Unorganized")) {
                        EternalMediaBar.savedData.categories.get(i).appList.add(new AppDetail(ri.loadLabel(EternalMediaBar.manager), ri.activityInfo.packageName));
                        break;
                    }
                    i++;
                }
            }
            availableActivities.clear();
        }
    }
}
