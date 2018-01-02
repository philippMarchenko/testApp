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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.devfill.testapp.model.DataRealm;
import com.devfill.testapp.ui.MainActivity;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String LOG_TAG = "MainFragment";

    public static final int TASK_UPDATE_DATA = 0;   //константа для передачи события в сервис

    public static final int EVENT_DATA_UPDATED = 1; //константы для определения события из сервиса
    public static final int EVENT_DATA_EMPTY = 2;
    public static final int EVENT_DATA_ERROR = 3;

    public final static String UPDATED_DATA_ACTION = "updated_data_action";

    private static BroadcastReceiver br;            //приемник данных и событий с сервиса

    private TableLayout tableLayout;                //наша табличка

    private DBHelper dbHelper;                      //работаем С БД
    private SQLiteDatabase db;
    private Realm mRealm;

    private NestedScrollView nestedScrollView;      //обьект для прослушки скролинга
    private SwipeRefreshLayout swipeRefreshLayout;  //свайп для обновления данных
    private ProgressBar progressBar;
    private ProgressDialog  progressDialog;

    private int start = 0;                          //переменные для подгрузки данных в таблицу
    private int end = 50;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        Log.i(LOG_TAG, "onCreateView ");

        dbHelper = new DBHelper(getContext());

        try{
            db = dbHelper.getWritableDatabase();
            mRealm = Realm.getInstance(getContext());
        }
        catch(Exception e){
            Log.i(LOG_TAG, "Не удалось получить екземпляр БД " + e.getMessage());
        }

        nestedScrollView =  rootView.findViewById(R.id.neestedscroll);
        progressBar =  rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        swipeRefreshLayout =  rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);

        tableLayout =  rootView.findViewById(R.id.tableLayout);
        tableLayout.setColumnShrinkable(0,true);                    //разрешим перенос на новую строку 0 столбцу

        switch (ServiceRoutes.type_db){                             //в зависимости от типа БД отобразим данные с базы в таблицу
                case MainActivity.IDM_SQ_LITE:
                    if(isTableExists("routes",true)){               //есть ли такая таблица в БД
                        showTableSQLite();
                    }
                    break;
                case MainActivity.IDM_REALM:
                    showTableFromRealm();
                    break;
                default:
                    break;
           }

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.i(LOG_TAG, "BOTTOM SCROLL");
                    progressBar.setVisibility(View.VISIBLE);        //докретиди до конца списка,покажем прогресбар
                    start = end;                                    //пересохраним переменные для для подгрузки следующих 50 елементов
                    end = end + 50;

                    switch (ServiceRoutes.type_db){                 //отобразим на экране в зависимости от типа БД
                        case MainActivity.IDM_SQ_LITE:
                            showTableSQLite();
                            break;
                        case MainActivity.IDM_REALM:
                            showTableFromRealm();
                            break;
                        default:
                            break;
                    }

                    initTableListener();                                 //инициализация слушателя нажатия на строку в таблице
                }
            }
        });

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.i(LOG_TAG, "onReceive ");

                int event = intent.getIntExtra("event",255);            //принимаем событие из сервиса

                switch (event){
                    case MainFragment.EVENT_DATA_UPDATED:               //данные обновились
                        tableLayout.removeAllViews();                   //очистим таблицу
                        start = 0;                                      //проинициализируем переменные
                        end = 50;

                        switch (ServiceRoutes.type_db){                 //отобразим на экране в зависимости от типа БД
                            case MainActivity.IDM_SQ_LITE:
                                showTableSQLite();
                                break;
                            case MainActivity.IDM_REALM:
                                showTableFromRealm();
                                break;
                            default:
                                break;
                        }

                        initTableListener();                            //инициализация слушателя нажатия на строку в таблице
                        progressDialog.dismiss();                       //скроем progressDialog
                        break;
                    case MainFragment.EVENT_DATA_EMPTY:                 //пришел пустой список с сервера
                        showAllertDialogListEmpty();                    //покажем сообщение
                        progressDialog.dismiss();                       //скроем progressDialog
                        break;
                    case MainFragment.EVENT_DATA_ERROR:                        //ошибка загрузки с сервера
                        showAllertDialogError(intent.getStringExtra("error")); //покажем сообщение
                        progressDialog.dismiss();                              //скроем progressDialog
                        break;
                }
            }
        };


        initTableListener();                                        //инициализация слушателя нажатия на строку в таблице

        initProgressDialog();                                       //инициализация progressDialog

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
                    bundle.putInt("id",tableLayout.indexOfChild(v));        //передадим во второй фрагмент ID выбраного маршрута
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);

                    getFragmentManager().beginTransaction()                 //покажем детальный фрагмент
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
                requestToServer();                                          //если нажали повторить пошлем сообщение сервису что надо повторить запрос
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

    private void showTableSQLite(){

        Cursor c = db.query("routes", null, null, null, null, null, null);

        progressBar.setVisibility(View.INVISIBLE);

        int from_dateColIndex = c.getColumnIndex("from_date");
        int from_timeColIndex = c.getColumnIndex("from_time");
        int infoColIndex = c.getColumnIndex("info");
        int to_timeColIndex = c.getColumnIndex("to_time");
        int priceColIndex = c.getColumnIndex("price");

      for(int i = start ; i < end; i++){      //перебираем наши маршруты

         if(c.moveToPosition(i)) {            //курсор ставим на нужное место

             TableRow tableRow = new TableRow(getContext());
             tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

             TextView textView = new TextView(getContext());                //ложим TextView в строку
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

             tableLayout.addView(tableRow);                 //строку ложим в таблицу
         }
      }

        Log.i(LOG_TAG, "row added count  " + start );

    }

    private void showTableFromRealm(){

        progressBar.setVisibility(View.INVISIBLE);

        try {
            RealmResults<DataRealm> realmCities= mRealm.where(DataRealm.class).findAllAsync();
            realmCities.load();

            Log.i(LOG_TAG, "realmCities size " + realmCities.size());

            for(int i = start ; i < end; i++){                  //перебираем наши маршруты

                DataRealm dataRealm = realmCities.get(i);       //достаем нужный елемент

                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView textView = new TextView(getContext());
                textView.setText(dataRealm.getInfo());
                tableRow.addView(textView,0);

                textView = new TextView(getContext());
                textView.setText(" " + dataRealm.getFrom_date() + " ");
                tableRow.addView(textView,1);                           //ложим TextView в строку

                textView = new TextView(getContext());
                textView.setText(" " + dataRealm.getFrom_time() + " ");
                tableRow.addView(textView,2);

                textView = new TextView(getContext());
                textView.setText(" " + dataRealm.getTo_time() + " ");
                tableRow.addView(textView,3);

                textView = new TextView(getContext());
                textView.setText(" " + Integer.toString(dataRealm.getPrice()) + " ");
                tableRow.addView(textView,4);

                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                tableRow.setBackgroundResource(outValue.resourceId);
                tableRow.setClickable(true);

                tableLayout.addView(tableRow);                      //строку ложим в таблицу
            }
        }
        catch (Exception e){

        }





    }

    public boolean isTableExists(String tableName, boolean openDb) {

      try{
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
      }
      catch(Exception e){

      }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume ");

        MainActivity.state = MainActivity.STATE_MAIN;           //сохраним состояние в MainActivity для востановления после поворота


        try{
            if(ServiceRoutes.brRegistered == false) {
            getActivity().registerReceiver(br, new IntentFilter(UPDATED_DATA_ACTION));  //зарегистрируем приемник данных и событий с сервиса
            ServiceRoutes.brRegistered = true;
           }
        }
        catch (Exception e){
            Log.i(LOG_TAG, "Error register reciver " + e.getMessage());
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       if( mRealm != null){
           mRealm.close();                               //закроем соеденение с Realm
       }

        if (ServiceRoutes.brRegistered == true) {
            try {
                getActivity().unregisterReceiver(br);    //закроем соеденение с brRegistered
                ServiceRoutes.brRegistered = false;
            } catch (IllegalArgumentException e) {
                Log.d(LOG_TAG, "Error unregisterReceiver" + e.getMessage());
            }
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

        if(netType == null){                        //если нет подключения к инету покажем сообщение
            Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            progressDialog.show();
            Intent intent = new Intent(getActivity(), ServiceRoutes.class);
            intent.putExtra("task", TASK_UPDATE_DATA);
            getActivity().startService(intent);     //запросим данные с сервера еще раз через сервис
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
    }  //метод определяющий тип сети

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);

        requestToServer();
    }
}
