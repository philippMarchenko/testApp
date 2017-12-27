package com.devfill.testapp.ui.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.devfill.testapp.DBHelper;
import com.devfill.testapp.R;

public class MainFragment extends android.support.v4.app.Fragment{

    private static final String LOG_TAG = "MainFragment";

    private TableLayout tableLayout;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public String list_table[] = new String[5];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();


        tableLayout =  rootView.findViewById(R.id.tableLayout);
        tableLayout.setColumnShrinkable(0,true);

        if(isTableExists("routes",true)){
          showTable();
        }

        return rootView;
    }

    private void showTable(){

        Cursor c = db.query("routes", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int id_routeColIndex = c.getColumnIndex("id_route");
            int from_city_nameColIndex = c.getColumnIndex("from_city_name");
            int from_city_highlightColIndex = c.getColumnIndex("from_city_highlight");
            int from_city_idColIndex = c.getColumnIndex("from_city_id");
            int from_dateColIndex = c.getColumnIndex("from_date");
            int from_timeColIndex = c.getColumnIndex("from_time");
            int from_infoColIndex = c.getColumnIndex("from_info");
            int to_city_nameColIndex = c.getColumnIndex("to_city_name");
            int to_city_highlightColIndex = c.getColumnIndex("to_city_highlight");
            int to_city_idColIndex = c.getColumnIndex("to_city_id");
            int infoColIndex = c.getColumnIndex("info");
            int to_dateColIndex = c.getColumnIndex("to_date");
            int to_timeColIndex = c.getColumnIndex("to_time");
            int to_infoColIndex = c.getColumnIndex("to_info");
            int priceColIndex = c.getColumnIndex("price");
            int bus_idColIndex = c.getColumnIndex("bus_id");
            int reservation_countColIndex = c.getColumnIndex("reservation_count");

            do {

                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));



                TextView textView = new TextView(getContext());
                textView.setText(c.getString(infoColIndex));
                tableRow.addView(textView,0);

                textView = new TextView(getContext());
                textView.setText(c.getString(from_dateColIndex));
                tableRow.addView(textView,1);

                textView = new TextView(getContext());
                textView.setText(c.getString(from_timeColIndex));
                tableRow.addView(textView,2);

                textView = new TextView(getContext());
                textView.setText(c.getString(to_timeColIndex));
                tableRow.addView(textView,3);

                textView = new TextView(getContext());
                textView.setText(Integer.toString(c.getInt(priceColIndex)));
                tableRow.addView(textView,4);

               /* tableLayout.addView(tableRow);

                tableRow = new TableRow(getContext());
                View  v = new View(getContext());
                v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                v.setBackgroundColor(Color.rgb(51, 51, 51));
                tableRow.addView(v);*/

                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                tableRow.setBackgroundResource(outValue.resourceId);
                tableRow.setClickable(true);

                tableLayout.addView(tableRow);

                }
                while (c.moveToNext());
            }



       /* for(int i = 0 ; i < 5; i ++){

            TableRow tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(getContext());
            textView.setText(Integer.toString(i));
            tableRow.addView(textView,0);

            textView = new TextView(getContext());
            textView.setText(Integer.toString(i));
            tableRow.addView(textView,1);

            textView = new TextView(getContext());
            textView.setText(Integer.toString(i));
            tableRow.addView(textView,2);

            textView = new TextView(getContext());
            textView.setText(Integer.toString(i));
            tableRow.addView(textView,3);

            textView = new TextView(getContext());
            textView.setText(Integer.toString(i));
            tableRow.addView(textView,4);

            tableLayout.addView(tableRow);
        }
*/
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        if(openDb) {
            if(db == null || !db.isOpen()) {
                db = dbHelper.getReadableDatabase();
            }

            if(!db.isReadOnly()) {
                db.close();
                db = dbHelper.getReadableDatabase();
            }
        }

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}
