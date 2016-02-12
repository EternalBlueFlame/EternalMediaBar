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


    public void writeXML(settingsClass saveData){
        //writing the XML, it is saved as a readable XML file, so we have to write it as if we are making one REALLY long string then save it to a file.
        String xml = "";
        //first add the vLists
        for (int i=0; i< saveData.vLists.size();){
            xml = xml+"<vList>";
            for (int ii=0; ii<saveData.vLists.get(i).size();){
                //Similar to HTML we will use the same syntax to declare the variables, this makes it easy to parse later on.
                //we will add the whitespace as well, just in case for some odd reason we actually need to be able to read the save file for debugging purposes.
                xml = xml+"\n     <AppData>";
                xml = xml+"\n          <label>"+saveData.vLists.get(i).get(ii).label.toString()+"</label>";
                xml = xml+"\n          <name>"+saveData.vLists.get(i).get(ii).name+"</name>";
                xml = xml+"\n          <icon>"+saveData.vLists.get(i).get(ii).icon.toString()+"</icon>";
                xml = xml+"\n          <persistent>"+saveData.vLists.get(i).get(ii).isPersistent+"</persistent>";
                xml = xml+"\n     </AppData>";
                ii++;
            }
            xml = xml+"\n</vList>\n\n";
            i++;
        }

    }


}
