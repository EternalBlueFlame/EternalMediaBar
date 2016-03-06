package com.ebf.eternalmediabar;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    List<int[]> organizeMode;
    List<AppDetail> hiddenApps = new ArrayList<AppDetail>();
    List<List<AppDetail>> vLists = new ArrayList<List<AppDetail>>();
    List<String> categoryNames = new ArrayList<>();
    List<String> categoryIcons = new ArrayList<>();
    List<String> categoryGoogleIcons = new ArrayList<>();


    //////////////////////////////////////////////////
    //////Create the string for saving to a file//////
    //////////////////////////////////////////////////
    //we can't manage files from this class due to permissions, but we can handle processing the variables
    public String writeXML(settingsClass saveData, EternalMediaBar eternalMediaBar){

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n";
        xml = xml+"<xmlRoot>\n";
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

        //create a loop for the vLists, and the lists in them, and each appData in them
        xml = xml+"<vLists>\n";
        for (int i=0; i< saveData.vLists.size();){
            xml = xml+"\n     <vList>";
            //this will fail if the New Apps category is empty
            try {
                //in 2.5 include this list of names as part of the save file. Also use a string to define icon.
                xml = xml + "\n          <listName>" + eternalMediaBar.hli.get(i).label + "</listName>";
                //we will define it as a number string for now, with an index of 1, later we can use the numbers to define internal icons and launch intent/image file strings to define custom icons.
                xml = xml + "\n          <listIcon>" + (i + 1) + "</listIcon>";
                xml = xml + "\n          <listGoogleIcon>" + (i + 1) + "</listGoogleIcon>\n";
                //The rest of the information needed for the hList is the same for every entry, so it can easily be created manually on loading a save file.
            }
            catch (Exception e){}
            //in revision 2, due to the way the variable is managed, this may fail, so we need to compensate for that.
            try {
                //while we do this we can also define the organize mode to save some time and effort.
                xml = xml + "          <organizeMode>\n";
                xml = xml + "               <sub>" + saveData.organizeMode.get(i)[0] + "</sub>\n";
                xml = xml + "               <repeat>" + saveData.organizeMode.get(i)[1] + "</repeat>\n";
                xml = xml + "               <main>" + saveData.organizeMode.get(i)[2] + "</main>\n";
                xml = xml + "          </organizeMode>\n\n";
            } catch (Exception e) {
                xml = xml + "          <organizeMode>\n";
                xml = xml + "               <sub>" + 0 + "</sub>\n";
                xml = xml + "               <repeat>" + 1 + "</repeat>\n";
                xml = xml + "               <main>" + 1 + "</main>\n";
                xml = xml + "          </organizeMode>\n";
            }
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
            xml = xml+"\n     </vList>\n";
            i++;
        }
        xml = xml+"</vLists>\n";

        //the same way we did it for the vLists, we'll do it again for the hidden apps
        xml = xml+"\n<hiddenApps>\n";
        for (int i=0; i<saveData.hiddenApps.size();){
            xml = xml+"\n     <AppData>";
            xml = xml+"\n          <label>"+saveData.hiddenApps.get(i).label.toString().replace("&", "andabcd")+"</label>";
            xml = xml+"\n          <name>"+saveData.hiddenApps.get(i).name+"</name>";
            //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
            xml = xml+"\n          <persistent>"+saveData.hiddenApps.get(i).isPersistent+"</persistent>";
            xml = xml+"\n     </AppData>";
            i++;
        }
        xml = xml+"\n</hiddenApps>\n";

        xml = xml+"\n</xmlRoot>";


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
    public settingsClass returnSettings(String xml) {
        settingsClass savedData= new settingsClass();
        //catch with below by initializing vLists properly
        //we should initialize the other variables as well.
        savedData.useGoogleIcons = false;
        savedData.mirrorMode = false;
        savedData.cleanCacheOnStart = false;
        savedData.gamingMode = false;
        savedData.useManufacturerIcons = false;
        savedData.loadAppBG = true;
        savedData.fontCol = -1;
        savedData.menuCol = -1;
        savedData.iconCol = -1;
        savedData.hiddenApps = new ArrayList<>();
        savedData.organizeMode = new ArrayList<>();

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

                //iterate through  VList tags
                NodeList vListList = categoryNode.getElementsByTagName("vList");
                for (int vListNodes = 0; vListNodes < vListList.getLength(); vListNodes++) {
                    Element appElements = (Element) vListList.item(vListNodes);
                    savedData.vLists.add(new ArrayList<AppDetail>());
                    try {
                        savedData.categoryNames.add(appElements.getElementsByTagName("listName").item(0).getTextContent());
                        savedData.categoryIcons.add(appElements.getElementsByTagName("listIcon").item(0).getTextContent());
                        savedData.categoryGoogleIcons.add(appElements.getElementsByTagName("listGoogleIcon").item(0).getTextContent());
                    }
                    catch(Exception e){
                        System.out.print("failed to get menu" + vListNodes);
                        switch (vListNodes){
                            case 0:{
                                savedData.categoryNames.add("Social");
                                savedData.categoryIcons.add("1");
                                savedData.categoryGoogleIcons.add("1");
                            }
                            case 1:{
                                savedData.categoryNames.add("Media");
                                savedData.categoryIcons.add("2");
                                savedData.categoryGoogleIcons.add("2");
                            }
                            case 2:{
                                savedData.categoryNames.add("Games");
                                savedData.categoryIcons.add("3");
                                savedData.categoryGoogleIcons.add("3");
                            }
                            case 3:{
                                savedData.categoryNames.add("Web");
                                savedData.categoryIcons.add("4");
                                savedData.categoryGoogleIcons.add("4");
                            }
                            case 4:{
                                savedData.categoryNames.add("Utility");
                                savedData.categoryIcons.add("5");
                                savedData.categoryGoogleIcons.add("5");
                            }
                            case 5:{
                                savedData.categoryNames.add("Settings");
                                savedData.categoryIcons.add("6");
                                savedData.categoryGoogleIcons.add("6");
                            }
                            case 6:{
                                savedData.categoryNames.add("New Apps");
                                savedData.categoryIcons.add("7");
                                savedData.categoryGoogleIcons.add("7");
                            }
                        }
                    }
                    //try and grab the variables for the list name, list icon, list google icon and list organization mode.
                    try {
                        savedData.organizeMode.add(new int[]{Integer.parseInt(appElements.getElementsByTagName("sub").item(0).getTextContent()), Integer.parseInt(appElements.getElementsByTagName("repeat").item(0).getTextContent()), Integer.parseInt(appElements.getElementsByTagName("main").item(0).getTextContent())});
                    }
                    catch (Exception e){
                        savedData.organizeMode.add(new int[]{0,1,1});
                    }
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
            ///////////////Load the hidden apps///////////////
            //////////////////////////////////////////////////
            try{
                //enter the hiddenApps list
                NodeList oldAppsList = doc.getElementsByTagName("hiddenApps");
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
                        savedData.hiddenApps.add(tempApp);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            }
            catch (Exception e){
                e.printStackTrace();
            }

        return savedData;
    }

}
