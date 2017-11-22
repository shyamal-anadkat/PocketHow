package edu.wisc.ece.pockethow.entity;

import android.net.Uri;

import java.net.URL;

/**
 * Created by Cameron on 11/8/2017.
 */

public class CategoryIcon {
    public Integer Icon;
    public String Label;
    private boolean isChecked;
    private Uri uri;

    public CategoryIcon(Integer icon, String label, String url){
        this.Icon = icon;
        this.Label = label;
        this.isChecked = false;
        this.uri = Uri.parse(url);
    }

    public CategoryIcon(Integer icon, String label){
        this.Icon = icon;
        this.Label = label;
        this.isChecked = false;
        this.uri = null;
    }

    public boolean isChecked(){
        return this.isChecked;
    }

    public void toggleChecked(){
        isChecked = !isChecked;
    }

    public Uri getUri(){
        return this.uri;
    }

}
