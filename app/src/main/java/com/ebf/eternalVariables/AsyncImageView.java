package com.ebf.eternalVariables;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ebf.eternalmediabar.EternalMediaBar;

public class AsyncImageView {
    public String ico;
    public ImageView icon = new ImageView(EternalMediaBar.activity);

    public AsyncImageView(String ico, ViewGroup.LayoutParams params, float y, float x, int id, boolean viewBounds){
        this.ico = ico;

        icon.setLayoutParams(params);
        icon.setX(x);
        icon.setY(y);
        icon.setId(id);
        icon.setAdjustViewBounds(viewBounds);
    }

    public AsyncImageView(Bitmap ico, ViewGroup.LayoutParams params, float y, float x, int id, boolean viewBounds){
        icon.setLayoutParams(params);
        icon.setX(x);
        icon.setY(y);
        icon.setId(id);
        icon.setAdjustViewBounds(viewBounds);
        icon.setImageBitmap(ico);
    }
}
