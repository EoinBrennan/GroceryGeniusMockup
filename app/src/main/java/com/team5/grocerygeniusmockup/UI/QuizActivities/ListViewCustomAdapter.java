package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    ArrayList<Integer> choices = new ArrayList<Integer>();

    public ListViewCustomAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        for (int j = 0; j < data.size(); j++) {
            choices.add(3);
        }
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

            final int listposition = position;

            holder = new SectionHolder();
            holder.mySpinner = (Spinner)row.findViewById(R.id.spinner);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            if (!(position < choices.size())) {
                for (int i = choices.size(); i == position; i++) {
                    choices.add(3);
                }
            }
            holder.mySpinner.setSelection(choices.get(position));
            holder.mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int item, long id) {
                    choices.set(listposition, item);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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

    public int getFrequency(int position) {
        return choices.get(position);
    }

    static class SectionHolder
    {
        Spinner mySpinner;
        TextView txtTitle;

    }
}