package com.ebf.eternalVariables;

import java.util.ArrayList;
import java.util.List;

public class CategoryClass {

    public int organizeMode = 1;
    public boolean organizeGroupDevs =false;
    public boolean organizeAlways = true;
    public List<AppDetail> appList = new ArrayList<>();
    public String categoryName = "";
    public String categoryIcon = "";
    public List<String> categoryTags = new ArrayList<>();

    //basic constructors for the class, allows us to set the default values without needing to fully instance the class
    public CategoryClass(int organizeMode, boolean organizeAlways, boolean organizeGroupDevs, String categoryName, String categoryIcon, List<String> categoryTags){
        this.organizeMode = organizeMode;
        this.organizeAlways = organizeAlways;
        this.organizeGroupDevs = organizeGroupDevs;
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        this.categoryTags = categoryTags;
    }
    public CategoryClass(){}
}
