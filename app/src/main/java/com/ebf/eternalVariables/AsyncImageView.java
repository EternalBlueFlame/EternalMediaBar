package com.ebf.eternalVariables;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ebf.eternalmediabar.EternalMediaBar;
import com.ebf.eternalmediabar.R;

public class AsyncImageView {
    public String internalCommand;
    public String URI;
    public ImageView icon = new ImageView(EternalMediaBar.activity);
    public ImageView selectedIcon = new ImageView(EternalMediaBar.activity);

    public AsyncImageView(String internalCommand, String uri, ViewGroup.LayoutParams params, float y, float x, int id, boolean viewBounds){
        this.internalCommand = internalCommand;
        this.URI = uri;

        icon.setLayoutParams(params);
        icon.setX(x);
        icon.setY(y);
        icon.setId(id);
        icon.setAdjustViewBounds(viewBounds);

        selectedIcon.setLayoutParams(params);
        selectedIcon.setX(x);
        selectedIcon.setY(y);
        selectedIcon.setId(R.id.list_item_checkbox);
        selectedIcon.setAdjustViewBounds(viewBounds);
        selectedIcon.setImageResource(android.R.drawable.checkbox_on_background);
        selectedIcon.setVisibility(View.INVISIBLE);
    }

    public AsyncImageView(Bitmap ico, ViewGroup.LayoutParams params, float y, float x, int id, boolean viewBounds){
        icon.setLayoutParams(params);
        icon.setX(x);
        icon.setY(y);
        icon.setId(id);
        icon.setAdjustViewBounds(viewBounds);
        icon.setImageBitmap(ico);

        selectedIcon.setLayoutParams(params);
        selectedIcon.setX(x);
        selectedIcon.setY(y);
        selectedIcon.setId(R.id.list_item_checkbox);
        selectedIcon.setAdjustViewBounds(viewBounds);
        selectedIcon.setImageResource(android.R.drawable.checkbox_on_background);
        selectedIcon.setVisibility(View.INVISIBLE);

    }
}
