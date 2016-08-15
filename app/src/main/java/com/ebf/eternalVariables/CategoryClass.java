package com.ebf.eternalVariables;

import java.util.ArrayList;
import java.util.List;

public class CategoryClass {

    public int[] organizeMode = new int[]{0,1,1};
    public List<AppDetail> appList = new ArrayList<>();
    public String categoryName = "";
    public String categoryIcon = "";
    public List<String> categoryTags = new ArrayList<>();

    //basic constructors for the class, allows us to set the default values without needing to fully instance the class
    public CategoryClass(int[] organizeMode, String categoryName, String categoryIcon, List<String> categoryTags){
        this.organizeMode = organizeMode;
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        this.categoryTags = categoryTags;
    }
    public CategoryClass(){}
}
