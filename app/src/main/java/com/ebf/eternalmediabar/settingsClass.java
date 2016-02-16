package com.ebf.eternalmediabar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class settingsClass implements Serializable {
    private static final long serialVersionUID = 0L;
    int iconCol;
    int menuCol;
    int fontCol;
    boolean cleanCacheOnStart;
    boolean loadAppBG;
    boolean gamingMode;
    boolean useGoogleIcons;
    boolean useManufacturerIcons;
    boolean mirrorMode;
    int[][] organizeMode;
    List<AppDetail> oldApps;
    List<AppDetail> hiddenApps = new ArrayList<AppDetail>();
    List<List<AppDetail>> vLists = new ArrayList<List<AppDetail>>();


    //we can't manage files from this class due to permissions, but we can handle processing the variables
    public String writeXML(settingsClass saveData){

        String xml = "";

        //lets list all the variables that the system needs to use
        xml = xml+"<iconCol>"+saveData.iconCol+"</iconCol>\n";
        xml = xml+"<menuCol>"+saveData.menuCol+"</menuCol>\n";
        xml = xml+"<fontCol>"+saveData.fontCol+"</fontCol>\n";
        xml = xml+"<cleanCacheOnStart>"+saveData.cleanCacheOnStart+"</cleanCacheOnStart>\n";
        xml = xml+"<loadAppBG>"+saveData.loadAppBG+"</loadAppBG>\n";
        xml = xml+"<gamingMode>"+saveData.gamingMode+"</gamingMode>\n";
        xml = xml+"<useGoogleIcons>"+saveData.useGoogleIcons+"</useGoogleIcons>\n";
        xml = xml+"<useManufacturerIcons>"+saveData.useManufacturerIcons+"</useManufacturerIcons>\n";
        xml = xml+"<mirrorMode>"+saveData.mirrorMode+"</mirrorMode>\n\n";

        //do a loop for all the items in the organize mode
        xml = xml+ "<organizeMode>\n";
        for (int i=0; i<7;){
            xml = xml+ "     <modeSet>\n";
            xml = xml+ "          <1>"+saveData.organizeMode[i][0]+"</1>\n";
            xml = xml+ "          <2>"+saveData.organizeMode[i][1]+"</2>\n";
            xml = xml+ "          <3>"+saveData.organizeMode[i][2]+"</3>\n";
            xml = xml+ "     </modeSet>\n";
            i++;
        }
        xml = xml+ "</organizeMode>\n\n";


        //create a loop for the vLists, and the lists in them, and each appData in them
        for (int i=0; i< saveData.vLists.size();){
            xml = xml+"<vList>";
            for (int ii=0; ii<saveData.vLists.get(i).size();){
                //Similar to HTML we will use the same syntax to declare the variables, this makes it easy to parse later on.
                //we will add the whitespace as well, just in case for some odd reason we actually need to be able to read the save file for debugging purposes.
                xml = xml+"\n     <AppData>";
                xml = xml+"\n          <label>"+saveData.vLists.get(i).get(ii).label.toString()+"</label>";
                xml = xml+"\n          <name>"+saveData.vLists.get(i).get(ii).name+"</name>";
                //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
                xml = xml+"\n          <persistent>"+saveData.vLists.get(i).get(ii).isPersistent+"</persistent>";
                xml = xml+"\n     </AppData>";
                ii++;
            }
            xml = xml+"</vList>\n\n";
            i++;
        }

        //same to how we did it for each vList, we do again for the old apps.
        xml = xml+"<oldApps>";
        for (int i=0; i<saveData.oldApps.size();){
            xml = xml+"\n     <AppData>";
            xml = xml+"\n          <label>"+saveData.oldApps.get(i).label.toString()+"</label>";
            xml = xml+"\n          <name>"+saveData.oldApps.get(i).name+"</name>";
            //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
            xml = xml+"\n          <persistent>"+saveData.oldApps.get(i).isPersistent+"</persistent>";
            xml = xml+"\n     </AppData>\n";
            i++;
        }
        xml = xml+"</oldApps>\n\n";

        //and one more list of appData for the hidden apps
        xml = xml+"<hiddenApps>\n";
        for (int i=0; i<saveData.oldApps.size();){
            xml = xml+"\n     <AppData>";
            xml = xml+"\n          <label>"+saveData.oldApps.get(i).label.toString()+"</label>";
            xml = xml+"\n          <name>"+saveData.oldApps.get(i).name+"</name>";
            //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
            xml = xml+"\n          <persistent>"+saveData.oldApps.get(i).isPersistent+"</persistent>";
            xml = xml+"\n     </AppData>";
            i++;
        }
        xml = xml+"\n</hiddenApps>\n\n";


        return xml;
    }


}
