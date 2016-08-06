package com.ebf.eternalmediabar;


import android.content.Context;
import android.util.Log;

import com.ebf.eternalVariables.AppDetail;
import com.ebf.eternalVariables.CategoryClass;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;


public class SettingsClass implements Serializable {
    private static final long serialVersionUID = 0L;
    int iconCol;
    int menuCol;
    int fontCol;
    boolean cleanCacheOnStart;
    boolean loadAppBG;
    boolean gamingMode;
    boolean doubleTap;
    String theme;
    boolean mirrorMode;
    int dimCol;
    List<AppDetail> hiddenApps = new ArrayList<>();
    List<CategoryClass> categories = new ArrayList<>();

    //////////////////////////////////////////////////
    //////Create the string for saving to a file//////
    //////////////////////////////////////////////////
    //we can't manage files from this class due to permissions, but we can handle processing the variables
    public void writeXML(EternalMediaBar eternalMediaBar){
        try {
            FileOutputStream fileStream = eternalMediaBar.openFileOutput("data.xml", Context.MODE_PRIVATE);
            StringBuilder sB = new StringBuilder();
            sB.append("<xmlRoot>\n" + "<iconCol>");
            sB.append(EternalMediaBar.savedData.iconCol);
            sB.append("</iconCol>\n<menuCol>");
            sB.append(EternalMediaBar.savedData.menuCol);
            sB.append("</menuCol>\n<fontCol>");
            sB.append(EternalMediaBar.savedData.fontCol);
            sB.append("</fontCol>\n<cleanCacheOnStart>");
            sB.append(EternalMediaBar.savedData.cleanCacheOnStart);
            sB.append("</cleanCacheOnStart>\n<loadAppBG>");
            sB.append(EternalMediaBar.savedData.loadAppBG);
            sB.append("</loadAppBG>\n<gamingMode>");
            sB.append(EternalMediaBar.savedData.gamingMode);
            sB.append("</gamingMode>\n<doubleTap>");
            sB.append(EternalMediaBar.savedData.doubleTap);
            sB.append("</doubleTap>\n<theme>");
            sB.append(EternalMediaBar.savedData.theme);
            sB.append("</theme>\n<mirrorMode>");
            sB.append(EternalMediaBar.savedData.mirrorMode);
            sB.append("</mirrorMode>\n<dimLists>");
            sB.append(EternalMediaBar.savedData.dimCol);
            sB.append("</dimLists>\n\n<vLists>\n");
            sB.append(appSavesToXML(EternalMediaBar.savedData));
            sB.append(hiddenAppsToString(EternalMediaBar.savedData));
            sB.append("\n</xmlRoot>");
            fileStream.write(sB.toString().getBytes());
            //write a string to the stream
            //close the stream to save some RAM.
            fileStream.flush();
            fileStream.close();
        }
        catch (Exception e){e.printStackTrace();}
    }

    /*/
        //save to external storage
        //try{
        //    FileWriter data = new FileWriter(Environment.getExternalStorageDirectory().getPath() +"/data6.xml");
        //    data.write(String);
        //    data.flush();
        //    data.close();
        }
        catch(Exception e){
            //first fail, ask for write permissions so it won't fail the next time
            //getPerms();
            //and print the stack just in case.
            e.printStackTrace();
        }/*/


    public String appSavesToXML(SettingsClass saveData) {

        StringBuilder bytes= new StringBuilder();

        for (CategoryClass category : saveData.categories) {
            //include this list of names as part of the save file. Also use a string to define icon.
            bytes.append("\n     <vList>\n          <listName>");
            bytes.append(category.categoryName);
            bytes.append("</listName>\n          <listIcon>");
            bytes.append(category.categoryIcon);
            bytes.append("</listIcon>\n          <listGoogleIcon>");
            bytes.append(category.categoryGoogleIcon);
            bytes.append("</listGoogleIcon>\n");

            try{
                bytes.append("          <categoryTags>");
                for (int ii=0;ii<category.categoryTags.size();){
                    if (category.categoryTags.get(ii).matches(".*[a-zA-Z]+.*")) {
                        bytes.append("\n               <tag>");
                        bytes.append(category.categoryTags.get(ii));
                        bytes.append("</tag>");
                    }
                    ii++;
                }
                bytes.append("\n          </categoryTags>\n");
            }
            catch (Exception e){}
            //due to the way the variable is managed, this may fail, so we need to compensate for that.
            try {
                //while we do this we can also define the organize mode to save some time and effort.
                bytes.append("          <organizeMode>\n               <sub>");
                bytes.append(category.organizeMode[0]);
                bytes.append("</sub>\n               <repeat>");
                bytes.append(category.organizeMode[1]);
                bytes.append("</repeat>\n               <main>");
                bytes.append(category.organizeMode[2]);
                bytes.append("</main>\n          </organizeMode>\n\n");
            } catch (Exception e) {}
            //now load the actual apps in the list
            for (AppDetail app : category.appList){
                //Similar to HTML we will use the same syntax to declare the variables, this makes it easy to parse later on.
                //we will add the whitespace as well, just in case for some odd reason we actually need to be able to read the save file for debugging purposes.
                bytes.append("\n          <AppData>\n               <label>");
                bytes.append(app.label.toString().replace("&", "andabcd"));
                bytes.append("</label>\n               <URI>");
                bytes.append(app.URI);
                bytes.append("</URI>\n               <persistent>");
                bytes.append(app.isPersistent);
                bytes.append("</persistent>\n          </AppData>");
            }
            bytes.append("\n     </vList>\n");
        }
        bytes.append("</vLists>\n");

        return bytes.toString();

    }

    public String hiddenAppsToString(SettingsClass saveData){

        //the same way we did it for the vLists, we'll do it again for the hidden apps
        StringBuilder bytes= new StringBuilder("\n<hiddenApps>\n");
        for (int i=0; i<saveData.hiddenApps.size();){
            bytes.append("\n     <AppData>\n          <label>");
            bytes.append(saveData.hiddenApps.get(i).label.toString().replace("&", "andabcd"));
            bytes.append("</label>\n          <URI>");
            bytes.append(saveData.hiddenApps.get(i).URI);
            bytes.append("</URI>\n          <persistent>");
            bytes.append(saveData.hiddenApps.get(i).isPersistent);
            bytes.append("</persistent>\n     </AppData>");
            bytes.append(saveData.hiddenApps.get(i).isPersistent);
            bytes.append("</persistent>\n     </AppData>");
            i++;
        }
        bytes.append("\n</hiddenApps>\n");
        return bytes.toString();
    }



    //////////////////////////////////////////////////
    //////////Create Boolean from a String////////////
    //////////////////////////////////////////////////
    private static Boolean boolFromString(String s){
            return !(s.equals("false"));
    }

    //////////////////////////////////////////////////
    /////////////////Load a save file/////////////////
    //////////////////////////////////////////////////
    // this function is so huge I have to separate it as if it's multiple functions or we'll all be lost.
    public static SettingsClass returnSettings(EternalMediaBar eternalMediaBar) {
        SettingsClass savedData= new SettingsClass();
        //catch with below by initializing vLists properly
        //we should initialize the other variables as well.
        savedData.hiddenApps = new ArrayList<>();
        savedData.categories = new ArrayList<>();

        //try to make the HTML document from XML. This should have no reason to fail, but we have to compensate for just in case it does or the compiler gets mad..
        try {

            //try load preferences
            FileInputStream fs = eternalMediaBar.openFileInput("data.xml");
            //build a document file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document doc = dbFactory.newDocumentBuilder().parse(fs);
            fs.close();
            //try to load the individual elements, it is possible one or more could be missing, so we have to compensate for that.
            //get the variable for the icon color
            try {savedData.iconCol = Integer.parseInt(doc.getElementsByTagName("iconCol").item(0).getTextContent());}
            catch (Exception e) {savedData.iconCol = 0xffffffff;}
            //get the menuCol
            try {
                savedData.menuCol = Integer.parseInt(doc.getElementsByTagName("menuCol").item(0).getTextContent());
                if (savedData.menuCol==-1){savedData.menuCol=0xcc000000;}
            }
            catch (Exception e){savedData.menuCol = 0xffffff;}
            //get the fontCol
            try {savedData.fontCol = Integer.parseInt(doc.getElementsByTagName("fontCol").item(0).getTextContent());}
            catch (Exception e){savedData.fontCol = 0xffffff;}
            //get the bool for cleanCacheOnStart
            try {savedData.cleanCacheOnStart = boolFromString(doc.getElementsByTagName("cleanCacheOnStart").item(0).getTextContent());}
            catch (Exception e){savedData.cleanCacheOnStart = false;}
            //get the bool for loadAppBG
            try {savedData.loadAppBG = boolFromString(doc.getElementsByTagName("loadAppBG").item(0).getTextContent());}
            catch (Exception e){savedData.loadAppBG = false;}
            //get the bool for gamingMode
            try{savedData.gamingMode = boolFromString(doc.getElementsByTagName("gamingMode").item(0).getTextContent());}
            catch(Exception e){savedData.gamingMode = false;}
            //get the bool for useGoogleIcons
            try {savedData.theme = doc.getElementsByTagName("theme").item(0).getTextContent();}
            catch (Exception e){savedData.theme = "Internal";}
            //get the bool for mirrorMode
            try {savedData.mirrorMode = boolFromString(doc.getElementsByTagName("mirrorMode").item(0).getTextContent());}
            catch (Exception e){savedData.mirrorMode = false;}
            //get the bool for mirrorMode
            try {savedData.dimCol = Integer.parseInt(doc.getElementsByTagName("dimCol").item(0).getTextContent());}
            catch (Exception e){savedData.dimCol = 0x66000000;}
            //get the bool for doubleTap
            try {savedData.doubleTap = boolFromString(doc.getElementsByTagName("doubleTap").item(0).getTextContent());}
            catch (Exception e){savedData.doubleTap = false;}


            //////////////////////////////////////////////////
            //////////////////Load a vLists///////////////////
            //////////////////////////////////////////////////
            try {
                //iterate through  VList tags
                NodeList vListList = ((Element) doc.getElementsByTagName("vLists").item(0)).getElementsByTagName("vList");
                for (int categoriesInXML = 0; categoriesInXML < vListList.getLength();) {
                    CategoryClass newCategory = new CategoryClass();
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
                            if (tagList.item(currentTag).getTextContent().matches(".*[a-zA-Z]+.*")) {
                                newCategory.categoryTags.add(tagList.item(currentTag).getTextContent());
                            }
                            currentTag++;
                        }
                    }
                    catch (Exception e){
                        Log.d("EternalMediaBar","Failed to get tags");
                        switch (categoriesInXML){
                            case 0:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Communication", "Social", "Sports", "Education"));break;}
                            case 1:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Music", "Video", "Entertainment", "Books", "Comics", "Photo"));break;}
                            case 2:{newCategory.categoryTags = Collections.singletonList("Games");break;}
                            case 3:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Weather", "News", "Shopping", "Lifestyle", "Transportation", "Travel", "Web"));break;}
                            case 4:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Business", "Finance", "Health", "Medical", "Productivity"));break;}
                            case 5:{newCategory.categoryTags = new ArrayList<>(Arrays.asList("Live Wallpaper", "Personalization", "Tools", "Widgets", "Libraries", "Android Wear"));break;}
                            case 6:{newCategory.categoryTags = Collections.singletonList("Unorganized");break;}
                        }
                    }
                    //try and grab the variables for the list URI, list icon, list google icon and list organization mode.
                    try {
                        newCategory.organizeMode = new int[]{Integer.parseInt(appElements.getElementsByTagName("sub").item(0).getTextContent()), Integer.parseInt(appElements.getElementsByTagName("repeat").item(0).getTextContent()), Integer.parseInt(appElements.getElementsByTagName("main").item(0).getTextContent())};
                    }
                    catch (Exception e){newCategory.organizeMode = new int[]{0,1,1};}
                    // iterate through AppData tags
                    NodeList appsList = appElements.getElementsByTagName("AppData");
                    for (int currentApp = 0; currentApp < appsList.getLength();) {
                        try {
                            Element appElement = (Element) appsList.item(currentApp);
                            newCategory.appList.add(new AppDetail(
                                    appElement.getElementsByTagName("label").item(0).getTextContent().replace("andabcd", "&"),
                                    appElement.getElementsByTagName("URI").item(0).getTextContent(),
                                    boolFromString(appElement.getElementsByTagName("persistent").item(0).getTextContent())
                            ));
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
                // iterate through AppData tags
                NodeList appsList = ((Element) doc.getElementsByTagName("hiddenApps").item(0)).getElementsByTagName("AppData");
                for (int currentApp = 0; currentApp < appsList.getLength();) {
                    try {
                        Element appElement = (Element) appsList.item(currentApp);
                        savedData.hiddenApps.add(new AppDetail(appElement.getElementsByTagName("label").item(0).getTextContent().replace("andabcd", "&"),
                                appElement.getElementsByTagName("URI").item(0).getTextContent(),
                                boolFromString(appElement.getElementsByTagName("persistent").item(0).getTextContent())
                                ));
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
            //e.printStackTrace();

            //the save data loader has compensation for any variables being missing, so we don't need to compensate for file not found.
            int[] organize = new int[]{0, 1, 1};
            savedData.categories.add(new CategoryClass(organize, "Social", "1", "1", new ArrayList<>(Arrays.asList("Communication", "Social", "Sports", "Education"))));
            savedData.categories.add(new CategoryClass(organize, "Media", "2", "2",  new ArrayList<>(Arrays.asList("Music", "Video", "Entertainment", "Books", "Comics", "Photo"))));
            savedData.categories.add(new CategoryClass(organize, "Games", "3", "3", Collections.singletonList("Games")));
            savedData.categories.add(new CategoryClass(organize, "Web", "4", "4", new ArrayList<>(Arrays.asList("Weather", "News", "Shopping", "Lifestyle", "Transportation", "Travel", "Web"))));
            savedData.categories.add(new CategoryClass(organize, "Utility", "5", "5", new ArrayList<>(Arrays.asList("Business", "Finance", "Health", "Medical", "Productivity"))));
            savedData.categories.add(new CategoryClass(organize, "Settings", "6", "6", new ArrayList<>(Arrays.asList("Live Wallpaper", "Personalization", "Tools", "Widgets", "Libraries", "Android Wear"))));
            savedData.categories.add(new CategoryClass(organize, "New Apps", "7", "7", Collections.singletonList("Unorganized")));
            //we should initialize the other variables as well.
            savedData.theme = "Internal";
            savedData.mirrorMode = false;
            savedData.cleanCacheOnStart = false;
            savedData.gamingMode = false;
            savedData.loadAppBG = true;
            savedData.doubleTap = false;
            savedData.fontCol = 0xffffffff;
            savedData.menuCol = 0xcc000000;
            savedData.iconCol = 0xffffffff;
            savedData.dimCol = 0x66000000;
            savedData.hiddenApps = Collections.emptyList();
        }

        return savedData;
    }

}
