package com.ebf.eternalmediabar;


import android.graphics.Color;
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
                    e.printStackTrace();
                    try {
                        FileInputStream fs = new FileInputStream(Environment.getExternalStorageDirectory() + "/data.xml");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        reader.close();
                        fs.close();
                        eternalMediaBar.savedData = eternalMediaBar.savedData.returnSettings(sb.toString());
                    } catch (Exception ee) {
                        //output to debug log just in case something went fully wrong
                        ee.printStackTrace();
                        //catch with below by initializing vLists properly
                        eternalMediaBar.savedData.vLists.add(new ArrayList<AppDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<AppDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<AppDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<AppDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<AppDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<AppDetail>());
                        eternalMediaBar.savedData.vLists.add(new ArrayList<AppDetail>());
                        //we should initialize the other variables as well.
                        eternalMediaBar.savedData.useGoogleIcons = false;
                        eternalMediaBar.savedData.mirrorMode = false;
                        eternalMediaBar.savedData.cleanCacheOnStart = false;
                        eternalMediaBar.savedData.gamingMode = false;
                        eternalMediaBar.savedData.useManufacturerIcons = false;
                        eternalMediaBar.savedData.loadAppBG = true;
                        eternalMediaBar.savedData.fontCol = Color.WHITE;
                        eternalMediaBar.savedData.menuCol = Color.WHITE;
                        eternalMediaBar.savedData.iconCol = Color.WHITE;
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
