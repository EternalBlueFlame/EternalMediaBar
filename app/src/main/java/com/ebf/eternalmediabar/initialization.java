package com.ebf.eternalmediabar;


import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
                        ee.printStackTrace();
                        eternalMediaBar.savedData = eternalMediaBar.savedData.returnSettings("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<xmlRoot>\n</xmlRoot>");
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
