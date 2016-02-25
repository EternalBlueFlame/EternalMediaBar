package com.ebf.eternalmediabar;

import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


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


    //////////////////////////////////////////////////
    //////Create the string for saving to a file//////
    //////////////////////////////////////////////////
    //we can't manage files from this class due to permissions, but we can handle processing the variables
    public String writeXML(settingsClass saveData, EternalMediaBar eternalMediaBar){

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n";
        xml = xml+"<xmlRoot>";
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
            xml = xml+ "          <sub>"+saveData.organizeMode[i][0]+"</sub>\n";
            xml = xml+ "          <repeat>"+saveData.organizeMode[i][1]+"</repeat>\n";
            xml = xml+ "          <main>"+saveData.organizeMode[i][2]+"</main>\n";
            xml = xml+ "     </modeSet>\n";
            i++;
        }
        xml = xml+ "</organizeMode>\n\n";


        //create a loop for the vLists, and the lists in them, and each appData in them
        xml = xml+"<vLists>";
        for (int i=0; i< saveData.vLists.size();){
            xml = xml+"\n     <vList>";
            //in 2.5 include this list of names as part of the save file. Also use a string to define icon.
            xml = xml+"\n          <listName>" + eternalMediaBar.hli.get(i).label + "</listName>";
            //we will define it as a number string for now, with an index of 1, later we can use the numbers to define internal icons and launch intent/image file strings to define custom icons.
            xml = xml+"\n          <listIcon>" + (i+1) + "</listIcon>\n\n";
            //The rest of the information needed for the hList is the same for every entry, so it can easily be created manually on loading a save file.
            //now load the actual apps in the list
            for (int ii=0; ii<saveData.vLists.get(i).size();){
                //Similar to HTML we will use the same syntax to declare the variables, this makes it easy to parse later on.
                //we will add the whitespace as well, just in case for some odd reason we actually need to be able to read the save file for debugging purposes.
                xml = xml+"\n          <AppData>";
                xml = xml+"\n               <label>"+saveData.vLists.get(i).get(ii).label.toString().replace("&", "andabcd")+"</label>";
                xml = xml+"\n               <name>"+saveData.vLists.get(i).get(ii).name+"</name>";
                //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
                xml = xml+"\n               <persistent>"+saveData.vLists.get(i).get(ii).isPersistent+"</persistent>";
                xml = xml+"\n          </AppData>";
                ii++;
            }
            xml = xml+"\n     </vList>\n\n";
            i++;
        }
        xml = xml+"</vLists>\n\n";

        //same to how we did it for each vList, we do again for the old apps.
        xml = xml+"<oldApps>";
        for (int i=0; i<saveData.oldApps.size();){
            xml = xml+"\n     <AppData>";
            xml = xml+"\n          <label>"+saveData.oldApps.get(i).label.toString().replace("&", "andabcd")+"</label>";
            xml = xml+"\n          <name>"+saveData.oldApps.get(i).name+"</name>";
            //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
            xml = xml+"\n          <persistent>"+saveData.oldApps.get(i).isPersistent+"</persistent>";
            xml = xml+"\n     </AppData>\n";
            i++;
        }
        xml = xml+"</oldApps>\n\n";

        //and one more list of appData for the hidden apps
        xml = xml+"<hiddenApps>\n";
        for (int i=0; i<saveData.hiddenApps.size();){
            xml = xml+"\n     <AppData>";
            xml = xml+"\n          <label>"+saveData.hiddenApps.get(i).label.toString().replace("&", "andabcd")+"</label>";
            xml = xml+"\n          <name>"+saveData.hiddenApps.get(i).name+"</name>";
            //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
            xml = xml+"\n          <persistent>"+saveData.hiddenApps.get(i).isPersistent+"</persistent>";
            xml = xml+"\n     </AppData>";
            i++;
        }
        xml = xml+"\n</hiddenApps>\n\n";

        xml = xml+"\n</xmlRoot>\n\n";


        return xml;
    }




    //////////////////////////////////////////////////
    //////////Create Boolean from a String////////////
    //////////////////////////////////////////////////
    private Boolean boolFromString(String s){
        if (s.equals("false")) {
            return false;
        } else {
            return true;
        }
    }

    //////////////////////////////////////////////////
    /////////////////Load a save file/////////////////
    //////////////////////////////////////////////////
    // this function is so huge I have to seperate it as if it's multiple functions or we'll all be lost.
    public String returnSettings(String xml) {
        settingsClass savedData= new settingsClass();

        //try to make the HTML document from XML. This should have no reason to fail, but we have to compensate for just in case it does or the compiler gets mad..
        try {
            //build a document file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            //try to load the individual elements, it is possible one or more could be missing, so we have to compensate for that.
            //make a node list to re-use.
            NodeList SingularNode;
            //try to get the iconCol
            try {
                //get the variable for the icon color
                SingularNode = doc.getElementsByTagName("iconCol");
                savedData.iconCol = Integer.parseInt(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e) {
                //if it fails to load, set the value to -1
                savedData.iconCol = -1;
            }
            //we repeat this for every variable
            try {
                //get the menuCol
                SingularNode = doc.getElementsByTagName("menuCol");
                savedData.menuCol = Integer.parseInt(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e){
                savedData.menuCol = -1;
            }
            try {
                //get the fontCol
                SingularNode = doc.getElementsByTagName("fontCol");
                savedData.fontCol = Integer.parseInt(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e){
                savedData.fontCol = -1;
            }
            try {
                //get the bool for cleanCacheOnStart
                SingularNode = doc.getElementsByTagName("cleanCacheOnStart");
                savedData.cleanCacheOnStart = boolFromString(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e){
                savedData.cleanCacheOnStart = false;
            }
            try {
                //get the bool for loadAppBG
                SingularNode = doc.getElementsByTagName("loadAppBG");
                savedData.loadAppBG = boolFromString(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e){
                savedData.loadAppBG = false;
            }
            try{
                //get the bool for gamingMode
                SingularNode = doc.getElementsByTagName("gamingMode");
                savedData.gamingMode = boolFromString(((Element) SingularNode.item(0)).getTextContent());
            }
            catch(Exception e){
                savedData.gamingMode = false;
            }
            try {
                //get the bool for useGoogleIcons
                SingularNode = doc.getElementsByTagName("useGoogleIcons");
                savedData.useGoogleIcons = boolFromString(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e){
                savedData.useGoogleIcons = false;
            }
            try {
                //get the bool for useManufacturerIcons
                SingularNode = doc.getElementsByTagName("useManufacturerIcons");
                savedData.useManufacturerIcons = boolFromString(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e){
                savedData.useManufacturerIcons = false;
            }
            try {
                //get the bool for mirrorMode
                SingularNode = doc.getElementsByTagName("mirrorMode");
                savedData.mirrorMode = boolFromString(((Element) SingularNode.item(0)).getTextContent());
            }
            catch (Exception e){
                savedData.mirrorMode = false;
            }


            //////////////////////////////////////////////////
            //////////////////Load a vLists///////////////////
            //////////////////////////////////////////////////
            try {
                // grab the vLists tag
                NodeList categoryList = doc.getElementsByTagName("vLists");
                Element categoryNode = (Element) categoryList.item(0);
                //debug log that we got it
                System.out.println("vLists");

                //iterate through  VList tags
                NodeList vListList = categoryNode.getElementsByTagName("vList");
                for (int vListNodes = 0; vListNodes < vListList.getLength(); vListNodes++) {
                    Element appElements = (Element) vListList.item(vListNodes);
                    savedData.vLists.add(new ArrayList<AppDetail>());

                    // iterate through AppData tags
                    NodeList appsList = appElements.getElementsByTagName("AppData");
                    for (int currentApp = 0; currentApp < appsList.getLength(); currentApp++) {
                        try {
                            Element appElement = (Element) appsList.item(currentApp);
                            AppDetail tempApp = new AppDetail();
                            if (appElement.getElementsByTagName("persistent").item(0).getTextContent().equals("false")) {
                                tempApp.isPersistent = false;
                            } else {
                                tempApp.isPersistent = true;
                            }
                            tempApp.label = appElement.getElementsByTagName("label").item(0).getTextContent().replace("andabcd", "&");
                            tempApp.name = appElement.getElementsByTagName("name").item(0).getTextContent();
                            savedData.vLists.get(savedData.vLists.size() - 1).add(tempApp);
                            Log.d("EternalMediaBar", "added " + tempApp.label + " ; " + tempApp.name + " to " + (savedData.vLists.size() - 1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e){
                savedData.vLists.add(new ArrayList<AppDetail>());
                savedData.vLists.add(new ArrayList<AppDetail>());
                savedData.vLists.add(new ArrayList<AppDetail>());
                savedData.vLists.add(new ArrayList<AppDetail>());
                savedData.vLists.add(new ArrayList<AppDetail>());
                savedData.vLists.add(new ArrayList<AppDetail>());
                savedData.vLists.add(new ArrayList<AppDetail>());
                //create the horizontal list
                //...
            }

            //////////////////////////////////////////////////
            /////////////////Load a save file/////////////////
            //////////////////////////////////////////////////
            try{
                //enter the oldApps list
                NodeList oldAppsList = doc.getElementsByTagName("oldApps");
                Element appElements = (Element) oldAppsList.item(0);

                // iterate through AppData tags
                NodeList appsList = appElements.getElementsByTagName("AppData");
                for (int currentApp = 0; currentApp < appsList.getLength(); currentApp++) {
                    try {
                        Element appElement = (Element) appsList.item(currentApp);
                        AppDetail tempApp = new AppDetail();
                        if (appElement.getElementsByTagName("persistent").item(0).getTextContent().equals("false")) {
                            tempApp.isPersistent = false;
                        }
                        else{
                            tempApp.isPersistent=true;
                        }
                        tempApp.label = appElement.getElementsByTagName("label").item(0).getTextContent().replace("andabcd", "&");
                        tempApp.name = appElement.getElementsByTagName("name").item(0).getTextContent();
                        savedData.vLists.get(savedData.vLists.size()-1).add(tempApp);
                        Log.d("EternalMediaBar", "added " + tempApp.label +" ; " + tempApp.name + " to " + (savedData.vLists.size()-1) );
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }


            /////////////////////////
            //get hidden apps and organize mode

            }
            catch (Exception e){
                e.printStackTrace();
            }



        return "";
    }

}
