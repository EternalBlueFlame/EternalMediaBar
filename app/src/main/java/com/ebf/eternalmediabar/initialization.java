package com.ebf.eternalmediabar;


import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class initialization  extends AsyncTask<initialization, Integer, Void> {
    public EternalMediaBar eternalMediaBar;

    public initialization(EternalMediaBar eternalMediaBar){
        this.eternalMediaBar = eternalMediaBar;
    }


    @Override
    protected Void doInBackground(initialization... params) {
        //run once
        if (!eternalMediaBar.init) {
            if (eternalMediaBar.savedData.vLists.size() <= 1) {
                try {
                    //try load preferences
                    FileInputStream fs = eternalMediaBar.openFileInput("data.xml");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    fs.close();
                    eternalMediaBar.savedData = eternalMediaBar.savedData.returnSettings(sb.toString());


                } catch (Exception e) {
                    try {
                        FileInputStream fs = new FileInputStream(Environment.getExternalStorageDirectory() + "/data.xml");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        reader.close();
                        fs.close();
                        eternalMediaBar.savedData = eternalMediaBar.savedData.returnSettings(sb.toString());
                    } catch (Exception ee) {
                        //the save data loader has compensation for any variables being missing, so we don't need to compensate for file not found.
                        eternalMediaBar.savedData.vLists.add(new ArrayList<appDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<appDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<appDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<appDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<appDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<appDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<appDetail>());
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
                        eternalMediaBar.savedData.dimLists= true;
                        eternalMediaBar.savedData.hiddenApps = new ArrayList<>();
                        int[] tempInt = new int[]{0, 1, 1};
                        eternalMediaBar.savedData.organizeMode = new ArrayList<>();
                        eternalMediaBar.savedData.organizeMode.add(tempInt);
                        eternalMediaBar.savedData.organizeMode.add(tempInt);
                        eternalMediaBar.savedData.organizeMode.add(tempInt);
                        eternalMediaBar.savedData.organizeMode.add(tempInt);
                        eternalMediaBar.savedData.organizeMode.add(tempInt);
                        eternalMediaBar.savedData.organizeMode.add(tempInt);
                        eternalMediaBar.savedData.organizeMode.add(tempInt);
                        eternalMediaBar.savedData.categoryTags = new ArrayList<>();
                        eternalMediaBar.savedData.categoryTags.add("Communication : Social : Sports : Education");
                        eternalMediaBar.savedData.categoryTags.add("Music : Video : Entertainment : Books : Comics : Photo");
                        eternalMediaBar.savedData.categoryTags.add("Games");
                        eternalMediaBar.savedData.categoryTags.add("Weather : News : Shopping : Lifestyle : Transportation : Travel");
                        eternalMediaBar.savedData.categoryTags.add("Business : Finance : Health : Medical : Productivity");
                        eternalMediaBar.savedData.categoryTags.add("Live Wallpaper : Personalization : Tools : Widgets : Libraries : Android Wear");
                        eternalMediaBar.savedData.categoryTags.add("Unorganized");

                        eternalMediaBar.savedData.categoryNames = new ArrayList<>();
                        eternalMediaBar.savedData.categoryNames.add("Social");
                        eternalMediaBar.savedData.categoryNames.add("Media");
                        eternalMediaBar.savedData.categoryNames.add("Games");
                        eternalMediaBar.savedData.categoryNames.add("Web");
                        eternalMediaBar.savedData.categoryNames.add("Utility");
                        eternalMediaBar.savedData.categoryNames.add("Settings");
                        eternalMediaBar.savedData.categoryNames.add("New Apps");

                        eternalMediaBar.savedData.categoryGoogleIcons = new ArrayList<>();
                        eternalMediaBar.savedData.categoryGoogleIcons.add("1");
                        eternalMediaBar.savedData.categoryGoogleIcons.add("2");
                        eternalMediaBar.savedData.categoryGoogleIcons.add("3");
                        eternalMediaBar.savedData.categoryGoogleIcons.add("4");
                        eternalMediaBar.savedData.categoryGoogleIcons.add("5");
                        eternalMediaBar.savedData.categoryGoogleIcons.add("6");
                        eternalMediaBar.savedData.categoryGoogleIcons.add("7");

                        eternalMediaBar.savedData.categoryIcons = new ArrayList<>();
                        eternalMediaBar.savedData.categoryIcons.add("1");
                        eternalMediaBar.savedData.categoryIcons.add("2");
                        eternalMediaBar.savedData.categoryIcons.add("3");
                        eternalMediaBar.savedData.categoryIcons.add("4");
                        eternalMediaBar.savedData.categoryIcons.add("5");
                        eternalMediaBar.savedData.categoryIcons.add("6");
                        eternalMediaBar.savedData.categoryIcons.add("7");

                    }
                }
            }
            //we dont use this, but due to glitches in earlier revisions, there may be things in here, when it should be empty.
            eternalMediaBar.savedData.hiddenApps.clear();
            //load in the apps
            eternalMediaBar.loadApps();
            //setup the warning variable
            eternalMediaBar.warningToggle = new boolean[1];
            eternalMediaBar.warningToggle[0] = false;

            //make sure this doesn't happen again
            eternalMediaBar.init = true;

            eternalMediaBar.loadListView();
        }
        return null;
    }
}
