package com.ebf.eternalmediabar;


import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
    boolean dimLists;
    List<appDetail> hiddenApps = new ArrayList<>();
    List<categoryClass> categories = new ArrayList<>();


    //////////////////////////////////////////////////
    //////Create the string for saving to a file//////
    //////////////////////////////////////////////////
    //we can't manage files from this class due to permissions, but we can handle processing the variables
    public void writeXML(settingsClass saveData, EternalMediaBar eternalMediaBar){
        try {
            FileOutputStream fileStream = eternalMediaBar.openFileOutput("data.xml", Context.MODE_PRIVATE);
            fileStream.write((
                            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<xmlRoot>\n<iconCol>" +
                                    saveData.iconCol + "</iconCol>\n<menuCol>" +
                                    saveData.menuCol + "</menuCol>\n<fontCol>" +
                                    saveData.fontCol + "</fontCol>\n<cleanCacheOnStart>" +
                                    saveData.cleanCacheOnStart + "</cleanCacheOnStart>\n<loadAppBG>" +
                                    saveData.loadAppBG + "</loadAppBG>\n<gamingMode>" +
                                    saveData.gamingMode + "</gamingMode>\n<useGoogleIcons>" +
                                    saveData.useGoogleIcons + "</useGoogleIcons>\n<useManufacturerIcons>" +
                                    saveData.useManufacturerIcons + "</useManufacturerIcons>\n<mirrorMode>" +
                                    saveData.mirrorMode + "</mirrorMode>\n<dimLists>" +
                                    saveData.dimLists + "</dimLists>\n\n<vLists>\n" +
                                    appSavesToXML(saveData, eternalMediaBar) +
                                    hiddenAppsToString(saveData, eternalMediaBar)+
                                    "\n</xmlRoot>"
                    ).getBytes());
            //write a string to the stream
            //close the stream to save some RAM.
            fileStream.flush();
            fileStream.close();
        }
        catch (Exception e){e.printStackTrace();}
    }



    private String appSavesToXML(settingsClass saveData, EternalMediaBar eternalMediaBar) {

        StringBuilder bytes= new StringBuilder();

        for (int i=0; i< saveData.categories.size();) {
            //include this list of names as part of the save file. Also use a string to define icon.
            bytes.append("\n     <vList>\n          <listName>" +
                    saveData.categories.get(i).categoryName + "</listName>\n          <listIcon>" +
                    saveData.categories.get(i).categoryIcon + "</listIcon>\n          <listGoogleIcon>" +
                    saveData.categories.get(i).categoryGoogleIcon + "</listGoogleIcon>\n");

            try{
                bytes.append("          <categoryTags>");
                for (int ii=0;ii<saveData.categories.get(i).categoryTags.size();){
                    bytes.append("\n               <tag>" + saveData.categories.get(i).categoryTags.get(ii) + "</tag>");
                    ii++;
                }
                bytes.append("\n          </categoryTags>\n");
            }
            catch (Exception e){}
            //due to the way the variable is managed, this may fail, so we need to compensate for that.
            try {
                //while we do this we can also define the organize mode to save some time and effort.
                bytes.append("          <organizeMode>\n               <sub>" +
                        saveData.categories.get(i).organizeMode[0] + "</sub>\n               <repeat>" +
                        saveData.categories.get(i).organizeMode[1] + "</repeat>\n               <main>" +
                        saveData.categories.get(i).organizeMode[2] + "</main>\n          </organizeMode>\n\n");
            } catch (Exception e) {}
            //now load the actual apps in the list
            for (int ii=0; ii<saveData.categories.get(i).appList.size();){
                //Similar to HTML we will use the same syntax to declare the variables, this makes it easy to parse later on.
                //we will add the whitespace as well, just in case for some odd reason we actually need to be able to read the save file for debugging purposes.
                bytes.append("\n          <AppData>\n               <label>"+
                        saveData.categories.get(i).appList.get(ii).label.toString().replace("&", "andabcd")+"</label>\n               <name>"+
                        saveData.categories.get(i).appList.get(ii).name+"</name>\n               <persistent>"+
                        saveData.categories.get(i).appList.get(ii).isPersistent+"</persistent>\n          </AppData>");
                ii++;
            }
            bytes.append("\n     </vList>\n");
            i++;
        }
        bytes.append("</vLists>\n");

        return bytes.toString();

    }

    private String hiddenAppsToString(settingsClass saveData, EternalMediaBar eternalMediaBar){

        //the same way we did it for the vLists, we'll do it again for the hidden apps
        StringBuilder bytes= new StringBuilder("\n<hiddenApps>\n");
        for (int i=0; i<saveData.hiddenApps.size();){
            bytes.append("\n     <AppData>\n          <label>"+
                    saveData.hiddenApps.get(i).label.toString().replace("&", "andabcd")+"</label>\n          <name>"+
                    saveData.hiddenApps.get(i).name+"</name>\n          <persistent>"+
                    saveData.hiddenApps.get(i).isPersistent+"</persistent>\n     </AppData>");
            i++;
        }
        bytes.append("\n</hiddenApps>\n");
        return bytes.toString();
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
    // this function is so huge I have to separate it as if it's multiple functions or we'll all be lost.
    public settingsClass returnSettings(String xml) {
        settingsClass savedData= new settingsClass();
        //catch with below by initializing vLists properly
        //we should initialize the other variables as well.
        savedData.hiddenApps = new ArrayList<>();
        savedData.categories = new ArrayList<>();

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
                savedData.iconCol = Integer.parseInt(SingularNode.item(0).getTextContent());
            }
            catch (Exception e) {
                //if it fails to load, set the value to -1
                savedData.iconCol = -1;
            }
            //we repeat this for every variable
            try {
                //get the menuCol
                SingularNode = doc.getElementsByTagName("menuCol");
                savedData.menuCol = Integer.parseInt(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.menuCol = -1;
            }
            try {
                //get the fontCol
                SingularNode = doc.getElementsByTagName("fontCol");
                savedData.fontCol = Integer.parseInt(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.fontCol = -1;
            }
            try {
                //get the bool for cleanCacheOnStart
                SingularNode = doc.getElementsByTagName("cleanCacheOnStart");
                savedData.cleanCacheOnStart = boolFromString(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.cleanCacheOnStart = false;
            }
            try {
                //get the bool for loadAppBG
                SingularNode = doc.getElementsByTagName("loadAppBG");
                savedData.loadAppBG = boolFromString(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.loadAppBG = false;
            }
            try{
                //get the bool for gamingMode
                SingularNode = doc.getElementsByTagName("gamingMode");
                savedData.gamingMode = boolFromString(SingularNode.item(0).getTextContent());
            }
            catch(Exception e){
                savedData.gamingMode = false;
            }
            try {
                //get the bool for useGoogleIcons
                SingularNode = doc.getElementsByTagName("useGoogleIcons");
                savedData.useGoogleIcons = boolFromString(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.useGoogleIcons = false;
            }
            try {
                //get the bool for useManufacturerIcons
                SingularNode = doc.getElementsByTagName("useManufacturerIcons");
                savedData.useManufacturerIcons = boolFromString(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.useManufacturerIcons = false;
            }
            try {
                //get the bool for mirrorMode
                SingularNode = doc.getElementsByTagName("mirrorMode");
                savedData.mirrorMode = boolFromString(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.mirrorMode = false;
            }
            try {
                //get the bool for mirrorMode
                SingularNode = doc.getElementsByTagName("dimLists");
                savedData.dimLists = boolFromString(SingularNode.item(0).getTextContent());
            }
            catch (Exception e){
                savedData.dimLists = true;
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
                for (int categoriesInXML = 0; categoriesInXML < vListList.getLength();) {
                    categoryClass newCategory = new categoryClass();
                    Element appElements = (Element) vListList.item(categoriesInXML);
                    try {
                        newCategory.categoryName = appElements.getElementsByTagName("listName").item(0).getTextContent();
                        newCategory.categoryIcon = appElements.getElementsByTagName("listIcon").item(0).getTextContent();
                        newCategory.categoryGoogleIcon = appElements.getElementsByTagName("listGoogleIcon").item(0).getTextContent();
                    }
                    catch(Exception e){
                        switch (categoriesInXML){
                            case 0:{newCategory.categoryName = "Social"; break;}
                            case 1:{newCategory.categoryName = "Media"; break;}
                            case 2:{newCategory.categoryName = "Games"; break;}
                            case 3:{newCategory.categoryName = "Web"; break;}
                            case 4:{newCategory.categoryName = "Utility"; break;}
                            case 5:{newCategory.categoryName = "Settings"; break;}
                            case 6:{newCategory.categoryName = "New Apps"; break;}
                        }
                        newCategory.categoryIcon = vListList.toString();
                        newCategory.categoryGoogleIcon = vListList.toString();
                    }
                    //Get the category tags
                    try{
                        // iterate through AppData tags
                        NodeList tagList = appElements.getElementsByTagName("categoryTags").item(0).getChildNodes();
                        for (int currentTag = 0; currentTag < tagList.getLength();) {
                            newCategory.categoryTags.add(tagList.item(currentTag).getTextContent());
                            currentTag++;
                        }
                    }
                    catch (Exception e){
                        Log.d("EternalMediaBar","Failed to get tags");
                        switch (categoriesInXML){
                            case 0:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Communication", "Social", "Sports", "Education"));break;}
                            case 1:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Music", "Video", "Entertainment", "Books", "Comics", "Photo"));break;}
                            case 2:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Games"));break;}
                            case 3:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Weather", "News", "Shopping", "Lifestyle", "Transportation", "Travel", "Web"));break;}
                            case 4:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Business", "Finance", "Health", "Medical", "Productivity"));break;}
                            case 5:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Live Wallpaper", "Personalization", "Tools", "Widgets", "Libraries", "Android Wear"));break;}
                            case 6:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Unorganized"));break;}
                        }
                    }
                    //try and grab the variables for the list name, list icon, list google icon and list organization mode.
                    try {
                        newCategory.organizeMode = new int[]{Integer.parseInt(appElements.getElementsByTagName("sub").item(0).getTextContent()), Integer.parseInt(appElements.getElementsByTagName("repeat").item(0).getTextContent()), Integer.parseInt(appElements.getElementsByTagName("main").item(0).getTextContent())};
                    }
                    catch (Exception e){newCategory.organizeMode = new int[]{0,1,1};}
                    // iterate through AppData tags
                    NodeList appsList = appElements.getElementsByTagName("AppData");
                    for (int currentApp = 0; currentApp < appsList.getLength();) {
                        try {
                            Element appElement = (Element) appsList.item(currentApp);
                            appDetail tempApp = new appDetail();
                            if (appElement.getElementsByTagName("persistent").item(0).getTextContent().equals("false")) {
                                tempApp.isPersistent = false;
                            } else {
                                tempApp.isPersistent = true;
                            }
                            tempApp.label = appElement.getElementsByTagName("label").item(0).getTextContent().replace("andabcd", "&");
                            tempApp.name = appElement.getElementsByTagName("name").item(0).getTextContent();
                            newCategory.appList.add(tempApp);
                        } catch (Exception e) {}
                        currentApp++;
                    }
                    savedData.categories.add(newCategory);
                    categoriesInXML++;
                }
            }
            catch (Exception e){e.printStackTrace();}

            //////////////////////////////////////////////////
            ///////////////Load the hidden apps///////////////
            //////////////////////////////////////////////////
            try{
                //enter the hiddenApps list
                NodeList oldAppsList = doc.getElementsByTagName("hiddenApps");
                Element appElements = (Element) oldAppsList.item(0);

                // iterate through AppData tags
                NodeList appsList = appElements.getElementsByTagName("AppData");
                for (int currentApp = 0; currentApp < appsList.getLength();) {
                    try {
                        Element appElement = (Element) appsList.item(currentApp);
                        appDetail tempApp = new appDetail();
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
                    currentApp++;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            }
        catch (Exception e){
            e.printStackTrace();
            savedData.categories.add(new categoryClass());
            savedData.categories.add(new categoryClass());
            savedData.categories.add(new categoryClass());
            savedData.categories.add(new categoryClass());
            savedData.categories.add(new categoryClass());
            savedData.categories.add(new categoryClass());
            savedData.categories.add(new categoryClass());
            //we should initialize the other variables as well.
            savedData.useGoogleIcons = false;savedData.mirrorMode = false;
            savedData.cleanCacheOnStart = false;
            savedData.gamingMode = false;
            savedData.useManufacturerIcons = false;
            savedData.loadAppBG = true;
            savedData.fontCol = -1;savedData.menuCol = -1;
            savedData.iconCol = -1;savedData.dimLists= true;
            savedData.hiddenApps = new ArrayList<>();
            int[] tempInt = new int[]{0, 1, 1};
            for(int i=0;i<savedData.categories.size();){
                savedData.categories.get(i).organizeMode = tempInt;
                switch (i){
                    case 0:{
                        savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Communication", "Social", "Sports", "Education"));
                        savedData.categories.get(i).categoryName = "Social";break;
                    }
                    case 1:{
                        savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Music", "Video", "Entertainment", "Books", "Comics", "Photo"));
                        savedData.categories.get(i).categoryName = "Media";break;
                    }
                    case 2:{
                        savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Games"));
                        savedData.categories.get(i).categoryName = "Games";break;
                    }
                    case 3:{
                        savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Weather", "News", "Shopping", "Lifestyle", "Transportation", "Travel", "Web"));
                        savedData.categories.get(i).categoryName = "Web";break;
                    }
                    case 4:{
                        savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Business", "Finance", "Health", "Medical", "Productivity"));
                        savedData.categories.get(i).categoryName = "Utility";break;
                    }
                    case 5:{savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Live Wallpaper", "Personalization", "Tools", "Widgets", "Libraries", "Android Wear"));
                        savedData.categories.get(i).categoryName = "Settings";break;
                    }
                    case 6:{
                        savedData.categories.get(i).categoryTags = new ArrayList<>(Arrays.asList("Unorganized"));savedData.categories.get(i).categoryName = "New Apps";break;
                    }
                }
                savedData.categories.get(i).categoryIcon = ""+(i+1);
                savedData.categories.get(i).categoryGoogleIcon = ""+(i+1);
                i++;
            }
        }

        return savedData;
    }

}
