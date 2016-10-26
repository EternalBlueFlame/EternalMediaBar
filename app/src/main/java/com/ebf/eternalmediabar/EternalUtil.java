package com.ebf.eternalmediabar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EternalUtil {


    //////////////////////////////////////////////////
    //////////////////Organize List///////////////////
    //////////////////////////////////////////////////
    public static void organizeList(List<AppDetail> list, int mode){
        switch(mode){
            //no organization
            case 0:{break;}
            //Alphabetical
            case 1:{
                Collections.sort(list, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //Reverse alphabetical
            case 2:{
                Collections.sort(list, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return -lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //Newest
            case 3:{Collections.sort(list, new Comparator<AppDetail>() {
                @Override
                public int compare(AppDetail lhs, AppDetail rhs) {
                    try {
                        if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime > EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return 1;
                        } else if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime < EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } catch (PackageManager.NameNotFoundException e){
                        return 0;
                    }
                }
            });
            break;}
            //Oldest
            case 4:{Collections.sort(list, new Comparator<AppDetail>() {
                @Override
                public int compare(AppDetail lhs, AppDetail rhs) {
                    try {
                        if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime < EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return 1;
                        } else if (EternalMediaBar.manager.getPackageInfo(lhs.URI, 0).firstInstallTime > EternalMediaBar.manager.getPackageInfo(rhs.URI, 0).firstInstallTime) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } catch (PackageManager.NameNotFoundException e){
                        return 0;
                    }
                }
            });
                break;}
            //most used
            case 5:{
                break;
            }
        }
    }


    //////////////////////////////////////////////////
    ////////////Open application Settings/////////////
    //////////////////////////////////////////////////
    public static void openAppSettings(AppDetail menuItem){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + Uri.parse(menuItem.URI)));
        OptionsMenuChange.menuClose(false);
        EternalMediaBar.activity.startActivity(intent);
    }


    //////////////////////////////////////////////////
    /////////Copy/move/hide an app menu item//////////
    //////////////////////////////////////////////////
    public static void relocateItem(int category, int action){

        if (action == R.id.ACTION_UNHIDE){
            for (CategoryClass categoryClass : EternalMediaBar.savedData.categories) {
                if (categoryClass.categoryTags.contains("Unorganized")) {
                    categoryClass.appList.add(EternalMediaBar.savedData.hiddenApps.get(category));
                    EternalMediaBar.savedData.hiddenApps.remove(category);
                    categoryClass.hasBeenOrganized = false;
                    break;
                }
            }
        } else {

            Toast.makeText(EternalMediaBar.activity, "Select the apps you want to move\nThen select where to move them.", Toast.LENGTH_LONG).show();
            for (String uri : EternalMediaBar.selectedApps) {
                for (int i = 0; i < EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.size(); ) {
                    if (EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i).URI.equals(uri)) {
                        switch (action) {
                            case R.id.ACTION_HIDE: {
                                EternalMediaBar.savedData.hiddenApps.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(i);
                                break;
                            }
                            case R.id.ACTION_COPY: {
                                EternalMediaBar.savedData.categories.get(category).appList.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                                EternalMediaBar.savedData.categories.get(category).hasBeenOrganized = false;
                                break;
                            }
                            case R.id.ACTION_MOVE: {
                                EternalMediaBar.savedData.categories.get(category).appList.add(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.get(i));
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList.remove(i);
                                EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).hasBeenOrganized = false;
                                break;
                            }
                        }
                        break;
                    }
                    i++;
                }
            }
        }
        OptionsMenuChange.menuClose(true);
    }

}
