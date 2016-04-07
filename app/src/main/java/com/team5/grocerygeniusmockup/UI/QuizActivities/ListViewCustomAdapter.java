package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Section;
import com.team5.grocerygeniusmockup.R;

import java.util.ArrayList;

/**
 * Created by User on 07/04/2016.
 */
public class ListViewCustomAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    ArrayList<String> data= null;

    public ListViewCustomAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    //this gets called for every item in the listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SectionHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SectionHolder();
            holder.mySpinner = (Spinner)row.findViewById(R.id.spinner);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (SectionHolder)row.getTag();
        }

        String section = data.get(position);
        holder.txtTitle.setText(section);

        return row;
    }

    static class SectionHolder
    {
        Spinner mySpinner;
        TextView txtTitle;

    }
}