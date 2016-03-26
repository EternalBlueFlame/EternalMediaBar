package com.ebf.eternalmediabar;


import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class initialization  extends AsyncTask<EternalMediaBar, Integer, Void> {


    @Override
    protected Void doInBackground(EternalMediaBar... eternalMediaBar) {
        //run once
        if (!eternalMediaBar[0].init) {
            if (eternalMediaBar[0].savedData.categories.size() <= 1) {
                try {
                    //try load preferences
                    FileInputStream fs = eternalMediaBar[0].openFileInput("data.xml");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    fs.close();
                    eternalMediaBar[0].savedData = eternalMediaBar[0].savedData.returnSettings(sb.toString());


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
                        eternalMediaBar[0].savedData = eternalMediaBar[0].savedData.returnSettings(sb.toString());
                    } catch (Exception ee) {
                        //the save data loader has compensation for any variables being missing, so we don't need to compensate for file not found.
                        eternalMediaBar[0].savedData.categories.add(new categoryClass());
                        eternalMediaBar[0].savedData.categories.add(new categoryClass());
                        eternalMediaBar[0].savedData.categories.add(new categoryClass());
                        eternalMediaBar[0].savedData.categories.add(new categoryClass());
                        eternalMediaBar[0].savedData.categories.add(new categoryClass());
                        eternalMediaBar[0].savedData.categories.add(new categoryClass());
                        eternalMediaBar[0].savedData.categories.add(new categoryClass());
                        //we should initialize the other variables as well.
                        eternalMediaBar[0].savedData.useGoogleIcons = false;
                        eternalMediaBar[0].savedData.mirrorMode = false;
                        eternalMediaBar[0].savedData.cleanCacheOnStart = false;
                        eternalMediaBar[0].savedData.gamingMode = false;
                        eternalMediaBar[0].savedData.useManufacturerIcons = false;
                        eternalMediaBar[0].savedData.loadAppBG = true;
                        eternalMediaBar[0].savedData.fontCol = -1;
                        eternalMediaBar[0].savedData.menuCol = -1;
                        eternalMediaBar[0].savedData.iconCol = -1;
                        eternalMediaBar[0].savedData.dimLists= true;
                        eternalMediaBar[0].savedData.hiddenApps = new ArrayList<>();
                        int[] tempInt = new int[]{0, 1, 1};
                        for(int i=0;i<eternalMediaBar[0].savedData.categories.size();){
                            eternalMediaBar[0].savedData.categories.get(i).organizeMode = tempInt;
                            switch (i){
                                case 0:{
                                    eternalMediaBar[0].savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Communication", "Social", "Sports", "Education"));
                                    eternalMediaBar[0].savedData.categories.get(i).categoryName = "Social";
                                }
                                case 1:{
                                    eternalMediaBar[0].savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Music", "Video", "Entertainment", "Books", "Comics", "Photo"));
                                    eternalMediaBar[0].savedData.categories.get(i).categoryName = "Media";
                                }
                                case 2:{
                                    eternalMediaBar[0].savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Games"));
                                    eternalMediaBar[0].savedData.categories.get(i).categoryName = "Games";
                                }
                                case 3:{
                                    eternalMediaBar[0].savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Weather", "News", "Shopping", "Lifestyle", "Transportation", "Travel", "Web"));
                                    eternalMediaBar[0].savedData.categories.get(i).categoryName = "Web";
                                }
                                case 4:{
                                    eternalMediaBar[0].savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Business", "Finance", "Health", "Medical", "Productivity"));
                                    eternalMediaBar[0].savedData.categories.get(i).categoryName = "Utility";
                                }
                                case 5:{
                                    eternalMediaBar[0].savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Live Wallpaper", "Personalization", "Tools", "Widgets", "Libraries", "Android Wear"));
                                    eternalMediaBar[0].savedData.categories.get(i).categoryName = "Settings";
                                }
                                case 6:{
                                    eternalMediaBar[0].savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Unorganized"));
                                    eternalMediaBar[0].savedData.categories.get(i).categoryName = "New Apps";
                                }
                            }

                            eternalMediaBar[0].savedData.categories.get(i).categoryIcon = ""+i;
                            eternalMediaBar[0].savedData.categories.get(i).categoryGoogleIcon = ""+i;
                        }
                    }
                }
            }
            //we dont use this, but due to glitches in earlier revisions, there may be things in here, when it should be empty.
            eternalMediaBar[0].savedData.hiddenApps.clear();
            //load in the apps
            eternalMediaBar[0].loadApps();
            //setup the warning variable
            eternalMediaBar[0].warningToggle = new boolean[1];
            eternalMediaBar[0].warningToggle[0] = false;

            //make sure this doesn't happen again
            eternalMediaBar[0].init = true;
        }
        return null;
    }
}
