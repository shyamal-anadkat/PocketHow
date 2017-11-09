package edu.wisc.ece.pockethow.entity;

/**
 * Created by Cameron on 11/8/2017.
 */

public class CategoryIcon {
    public Integer Icon;
    public String Label;
    private boolean isChecked;

    public CategoryIcon(Integer icon, String label){
        this.Icon = icon;
        this.Label = label;
        this.isChecked = false;
    }

    public boolean isChecked(){
        return this.isChecked;
    }

    public void toggleChecked(){
        isChecked = !isChecked;
    }

}
