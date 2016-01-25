package com.ebf.eternalmediabar;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class settingsClass implements Serializable {
    private static final long serialVersionUID = 0L;
    Color iconCol;
    Color menuCol;
    Color fontCol;
    boolean cleanCacheOnStart;
    boolean loadAppBG;
    boolean gamingMode;
    boolean useGoogleIcons;
    boolean useManufacturerIcons;
    boolean mirrorMode;
    List<AppDetail> oldApps;
    List<AppDetail> hiddenApps = new ArrayList<AppDetail>();
    List<List<AppDetail>> vLists = new ArrayList<List<AppDetail>>();
}
