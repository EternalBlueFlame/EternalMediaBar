package com.ebf.eternalVariables;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ebf.eternalmediabar.EternalMediaBar;
import com.ebf.eternalmediabar.R;

public class AsyncImageView {
    public String ico;
    public ImageView icon = new ImageView(EternalMediaBar.activity);
    public ProgressBar progress = new ProgressBar(EternalMediaBar.activity, null, android.R.attr.progressBarStyleSmall);

    public AsyncImageView(String ico, ViewGroup.LayoutParams params, float y, float x, int id, boolean viewBounds){
        this.ico = ico;

        icon.setLayoutParams(params);
        icon.setX(x);
        icon.setY(y);
        icon.setId(id);
        icon.setAdjustViewBounds(viewBounds);
        progress.setLayoutParams(params);
        progress.setX(x);
        progress.setY(y);
        progress.setId(R.id.list_item_progress);
    }
}
