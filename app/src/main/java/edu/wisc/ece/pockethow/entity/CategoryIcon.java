package edu.wisc.ece.pockethow.entity;

import android.net.Uri;

import java.util.List;

public class CategoryIcon {
    public Integer Icon;
    public String Label;
    private boolean isChecked;
    private Uri uri;
    private Long downloadId;
    private String wikihowAPIname;

    public CategoryIcon(Integer icon, String label, String url, String wikihowAPIname) {
        this.Icon = icon;
        this.Label = label;
        this.isChecked = false;
        this.uri = Uri.parse(url);
        this.downloadId = null;
        this.wikihowAPIname = wikihowAPIname;
    }

    public CategoryIcon(Integer icon, String label, String url) {
        this.Icon = icon;
        this.Label = label;
        this.isChecked = false;
        this.uri = Uri.parse(url);
        this.downloadId = null;
    }

    public CategoryIcon(Integer icon, String label) {
        this.Icon = icon;
        this.Label = label;
        this.isChecked = false;
        this.uri = null;
    }


    public boolean isChecked() {
        return this.isChecked;
    }

    public void toggleChecked() {
        isChecked = !isChecked;
    }

    public void toggleReset()
    {
        isChecked = false;
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getDatabaseName() {
        List<String> pathSegs = this.uri.getPathSegments();
        return pathSegs.get(pathSegs.size() - 1);
    }

    public void addDownloadId(Long input)
    {
        this.downloadId = input;
    }

    public Long getDownloadId()
    {
        return this.downloadId;
    }

    public void removeDownloadId()
    {
        this.downloadId = null;
    }

}
