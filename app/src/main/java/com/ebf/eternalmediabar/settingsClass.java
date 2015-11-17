package com.ebf.eternalmediabar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 11/5/2015.
 */
public class settingsClass implements Serializable {
    private static final long serialVersionUID = 0L;
    int iconcol;
    int menucol;
    int fontcol;
    boolean cleancacheonstart;
    boolean loadappbkg;
    boolean gamingmode;
    List<AppDetail> oldapps;
    private List<AppDetail> hiddenapps = new ArrayList<AppDetail>();
    List<List<AppDetail>> vlists = new ArrayList<>();
}
