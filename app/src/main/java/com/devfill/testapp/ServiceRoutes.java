package com.devfill.testapp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.devfill.testapp.model.Data;
import com.devfill.testapp.model.RouteModel;
import com.devfill.testapp.network.ServerAPI;
import com.devfill.testapp.ui.fragments.MainFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceRoutes extends Service {

    final String LOG_TAG = "serviceTag";
    final String request_url = "from_date=2016-01-01&to_date=2018-03-01";

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private Retrofit retrofit;
    private ServerAPI serverAPI;

    public static boolean brRegistered = false;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreateService");

        dbHelper = new DBHelper(getBaseContext());

        try{
            createTableSim(db);
        }
        catch(Exception e){
        }

        initRetrofit();

        if(!isTableExists("routes",true)){
            getRoutes();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        int task = intent.getIntExtra("task",255);

        if(task == MainFragment.TASK_UPDATE_DATA){
            Log.d(LOG_TAG, " TASK_UPDATE_DATA ");
            getRoutes();
        }

        return START_STICKY;
    }

    private void getRoutes(){

            try {

                serverAPI.getRoutes(request_url).enqueue(new Callback<RouteModel>() {
                    @Override
                    public void onResponse(Call<RouteModel> call, Response<RouteModel> response) {

                        RouteModel routeModel = response.body();
                        final List<Data> dataList =  routeModel.getData();

                        if(dataList.size() > 0){
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    saveDataInDB(dataList);
                                }
                            });
                            t.start();
                        }
                        else{
                          sendEvent(getBaseContext(),MainFragment.EVENT_DATA_EMPTY,"");
                        }
                        Log.i(LOG_TAG, "size " + routeModel.getData().size() );
                    }
                    @Override
                    public void onFailure(Call<RouteModel> call, Throwable t) {
                        sendEvent(getBaseContext(),MainFragment.EVENT_DATA_ERROR,t.getMessage());
                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getListNews " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getListNews " + e.getMessage());
            }
    }

    private void saveDataInDB (List<Data> list){

        db = dbHelper.getWritableDatabase();

        cleanTable("routes");

        ContentValues cv = new ContentValues();

        for(int i = 0 ; i < list.size(); i ++){

            cv.put("id_route", list.get(i).getId());
            cv.put("from_city_name", list.get(i).getFromCity().getName());
            cv.put("from_city_highlight", list.get(i).getFromCity().getHighlight());
            cv.put("from_city_id", list.get(i).getFromCity().getId());
            cv.put("from_date", list.get(i).getFromDate());
            cv.put("from_time", list.get(i).getFromTime());
            cv.put("from_info", list.get(i).getFromInfo());
            cv.put("to_city_name", list.get(i).getToCity().getName());
            cv.put("to_city_highlight", list.get(i).getToCity().getHighlight());
            cv.put("to_city_id", list.get(i).getToCity().getId());
            cv.put("info", list.get(i).getInfo());
            cv.put("to_date", list.get(i).getToDate());
            cv.put("to_time", list.get(i).getToTime());
            cv.put("to_info", list.get(i).getToInfo());
            cv.put("price", list.get(i).getPrice());
            cv.put("bus_id", list.get(i).getBusId());
            cv.put("reservation_count", list.get(i).getReservationCount());

            long rowID = db.insert("" + "routes" + "", null, cv);
            Log.d(LOG_TAG, "row inserted, ID = " + rowID);

            cv.clear();

            if(i == 200){
                sendEvent(getBaseContext(),MainFragment.EVENT_DATA_UPDATED,"");
            }

        }

    }

    private void cleanTable(String table) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(LOG_TAG, "--- Clear table " + table);
        int clearCount = db.delete(table, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + table + "'");
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
    }

    private static void sendEvent (Context context,int event,String error) {

        final Intent intent = new Intent(MainFragment.UPDATED_DATA_ACTION);
        intent.putExtra("event",event);
        intent.putExtra("error",error);

        try {
            context.sendBroadcast(intent);            //послали интент фрагменту
        } catch (Error e) {
            e.printStackTrace();
        }

    }

    private void initRetrofit (){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        serverAPI = retrofit.create(ServerAPI.class);
    }

    private void createTableSim(SQLiteDatabase db) {

        db.execSQL("create table routes " + " ("
                + "id integer primary key autoincrement,"
                + "id_route integer,"
                + "from_city_name text,"
                + "from_city_highlight integer,"
                + "from_city_id integer,"
                + "from_date text,"
                + "from_time text,"
                + "from_info text,"
                + "to_city_name text,"
                + "to_city_highlight integer,"
                + "to_city_id integer,"
                + "info text,"
                + "to_date text,"
                + "to_time text,"
                + "to_info text,"
                + "price integer,"
                + "bus_id integer,"
                + "reservation_count integer" + ");");


        Log.d(LOG_TAG, "--- onCreate database ---");

    }

    private boolean isTableExists(String tableName, boolean openDb) {
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
