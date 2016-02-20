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
            xml = xml+"\n          <listName>" + ((TextView) eternalMediaBar.hli.get(i).findViewById(R.id.item_app_label)).getText() + "</listName>";
            xml = xml+"\n          <listIcon>" + ((TextView) eternalMediaBar.hli.get(i).findViewById(R.id.item_app_label)).getText() + "</listIcon>\n\n";
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
        for (int i=0; i<saveData.oldApps.size();){
            xml = xml+"\n     <AppData>";
            xml = xml+"\n          <label>"+saveData.oldApps.get(i).label.toString().replace("&", "andabcd")+"</label>";
            xml = xml+"\n          <name>"+saveData.oldApps.get(i).name+"</name>";
            //getting the icon is probably unnecessary, this will need to be researched more //xml = xml+"\n          <icon>"+saveData.oldApps.get(i).icon.toString()+"</icon>";
            xml = xml+"\n          <persistent>"+saveData.oldApps.get(i).isPersistent+"</persistent>";
            xml = xml+"\n     </AppData>";
            i++;
        }
        xml = xml+"\n</hiddenApps>\n\n";

        xml = xml+"\n</xmlRoot>\n\n";


        return xml;
    }










    public String returnSettings(String xml) {

        try {
            settingsClass savedData= new settingsClass();
            //build a document file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

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
                        //appDetail app = new appDetail;//app.label=appElement.getElementsByTagName("label").item(0).getTextContent();//etc...
                        //settingsClassOutput.vLists.get(vListNodes).add(new appDetail);
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

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
