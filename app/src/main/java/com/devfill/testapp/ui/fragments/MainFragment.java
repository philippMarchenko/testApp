package com.devfill.testapp.ui.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.testapp.DBHelper;
import com.devfill.testapp.R;
import com.devfill.testapp.ServiceRoutes;

public class MainFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String LOG_TAG = "MainFragment";

    public static final int TASK_UPDATE_DATA = 0;   //константа для передачи события в сервис

    public static final int EVENT_DATA_UPDATED = 1; //константы для определения события из сервиса
    public static final int EVENT_DATA_EMPTY = 2;
    public static final int EVENT_DATA_ERROR = 3;

    public final static String UPDATED_DATA_ACTION = "updated_data_action";

    private static BroadcastReceiver br;

    private TableLayout tableLayout;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private ProgressDialog  progressDialog;
    private int start = 0;
    private int end = 50;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();

        nestedScrollView =  rootView.findViewById(R.id.neestedscroll);
        progressBar =  rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        swipeRefreshLayout =  rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);

        tableLayout =  rootView.findViewById(R.id.tableLayout);
        tableLayout.setColumnShrinkable(0,true);

        if(isTableExists("routes",true)){
          showTable();
        }

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.i(LOG_TAG, "BOTTOM SCROLL");
                    progressBar.setVisibility(View.VISIBLE);
                    start = end;
                    end = end + 50;
                    showTable();
                    initTableListener();

                }
            }
        });


        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.i(LOG_TAG, "onReceive ");

                int event = intent.getIntExtra("event",255);

                switch (event){
                    case MainFragment.EVENT_DATA_UPDATED:
                        tableLayout.removeAllViews();
                        start = 0;
                        end = 50;
                        showTable();
                        initTableListener();
                        progressDialog.dismiss();
                        break;
                    case MainFragment.EVENT_DATA_EMPTY:
                        showAllertDialogListEmpty();
                        progressDialog.dismiss();
                        break;
                    case MainFragment.EVENT_DATA_ERROR:
                        showAllertDialogError(intent.getStringExtra("error"));
                        progressDialog.dismiss();
                        break;
                }
            }
        };


        initTableListener();

        initProgressDialog();

        return rootView;
    }

    private void initTableListener(){

        for(int i = 0; i < tableLayout.getChildCount(); i++) {
            View view = tableLayout.getChildAt(i);
            final TableRow row = (TableRow) view;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(LOG_TAG, "onClick getId " + tableLayout.indexOfChild(v));

                    Bundle bundle = new Bundle();
                    bundle.putInt("id",tableLayout.indexOfChild(v));
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.container,detailFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

        }

    }

    private void showAllertDialogListEmpty(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Сообщение");
        builder.setMessage("Список маршрутов пуст!");
        builder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showAllertDialogError(String error){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ошибка!");
        builder.setMessage("Неверные параматры запроса! \n"+ error);
        builder.setPositiveButton("Повторить!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestToServer();
            }
        });
        builder.setNegativeButton("Отмена!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showTable(){

        Cursor c = db.query("routes", null, null, null, null, null, null);

        progressBar.setVisibility(View.INVISIBLE);

        int from_dateColIndex = c.getColumnIndex("from_date");
        int from_timeColIndex = c.getColumnIndex("from_time");
        int infoColIndex = c.getColumnIndex("info");
        int to_timeColIndex = c.getColumnIndex("to_time");
        int priceColIndex = c.getColumnIndex("price");

      for(int i = start ; i < end; i++){

         if(c.moveToPosition(i)) {

             TableRow tableRow = new TableRow(getContext());
             tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

             TextView textView = new TextView(getContext());
             textView.setText(" " + c.getString(infoColIndex) + " ");
             tableRow.addView(textView,0);

             textView = new TextView(getContext());
             textView.setText(" " + c.getString(from_dateColIndex) + " ");
             tableRow.addView(textView,1);

             textView = new TextView(getContext());
             textView.setText(" " + c.getString(from_timeColIndex) + " ");
             tableRow.addView(textView,2);

             textView = new TextView(getContext());
             textView.setText(" " + c.getString(to_timeColIndex) + " ");
             tableRow.addView(textView,3);

             textView = new TextView(getContext());
             textView.setText(" " + Integer.toString(c.getInt(priceColIndex)) + " ");
             tableRow.addView(textView,4);

             TypedValue outValue = new TypedValue();
             getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
             tableRow.setBackgroundResource(outValue.resourceId);
             tableRow.setClickable(true);

             tableLayout.addView(tableRow);
         }
      }

        Log.i(LOG_TAG, "row added count  " + start );

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

    @Override
    public void onResume() {
        super.onResume();


        try{
            if(ServiceRoutes.brRegistered == false) {
            getActivity().registerReceiver(br, new IntentFilter(UPDATED_DATA_ACTION));
            ServiceRoutes.brRegistered = true;
           }
        }
        catch (Exception e){
            Log.i(LOG_TAG, "Error register reciver " + e.getMessage());
        }


    }

    private void initProgressDialog(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Обновляем список маршрутов..."); // Setting Message
        progressDialog.setTitle("Пожалуйста, подождите!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
    }

    private void requestToServer(){
        String netType = getNetworkType(getContext());

        if(netType == null){
            Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            progressDialog.show();
            Intent intent = new Intent(getActivity(), ServiceRoutes.class);
            intent.putExtra("task", TASK_UPDATE_DATA);
            getActivity().startService(intent);
        }
    }

    private String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);

        requestToServer();
    }
}
