package com.ebf.eternalmediabar;

import com.ebf.eternalVariables.AppDetail;

import java.util.Collections;
import java.util.Comparator;

public class EternalUtil {


    //////////////////////////////////////////////////
    //////////////////Organize List///////////////////
    //////////////////////////////////////////////////
    public static void organizeList(){
        switch(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).organizeMode){
            //no organization
            case 0:{break;}
            //Alphabetical
            case 1:{
                Collections.sort(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //Reverse alphabetical
            case 2:{
                Collections.sort(EternalMediaBar.savedData.categories.get(EternalMediaBar.hItem).appList, new Comparator<AppDetail>() {
                    @Override
                    public int compare(AppDetail lhs, AppDetail rhs) {
                        return -lhs.label.toString().compareTo(rhs.label.toString());
                    }
                });
                break;
            }
            //Newest
            case 3:{break;}
            //Most used
            case 4:{break;}
        }
    }
}
