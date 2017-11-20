package edu.wisc.ece.pockethow.frontend;

/**
 * Created by Cameron on 11/6/2017.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.entity.CategoryIcon;

public class GridviewAdapter extends BaseAdapter
{
    private ArrayList<CategoryIcon> listCategories;
    private Activity activity;

    public GridviewAdapter(Activity activity,ArrayList<CategoryIcon> listCategories) {
        super();
        this.listCategories = listCategories;
        this.activity = activity;
    }

    public boolean toggleChecked(int position){
        this.listCategories.get(position).toggleChecked();
        return this.listCategories.get(position).isChecked();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listCategories.size();
    }

    @Override
    public CategoryIcon getItem(int position) {
        // TODO Auto-generated method stub
        return listCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder
    {
        public ImageView imgViewIcon;
        public TextView txtViewLabel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.gridview_row, null);

            view.txtViewLabel = (TextView) convertView.findViewById(R.id.textView1);
            view.imgViewIcon = (ImageView) convertView.findViewById(R.id.imageView1);

            convertView.setTag(view);

        }
        else
        {
            view = (ViewHolder) convertView.getTag();

        }

        view.txtViewLabel.setText(listCategories.get(position).Label);
        view.imgViewIcon.setImageResource(listCategories.get(position).Icon);
        //view.imgViewIcon.setBackgroundResource(this.listCategories.get(position).isChecked() ? R.color.colorPrimary : R.color.white);
        //view.txtViewLabel.setBackgroundResource(this.listCategories.get(position).isChecked() ? R.color.colorPrimary : R.color.white);
        return convertView;
    }
}