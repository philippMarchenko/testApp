package com.devfill.testapp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.devfill.testapp.model.Data;
import com.devfill.testapp.model.RouteModel;
import com.devfill.testapp.network.ServerAPI;

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

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreateService");


        dbHelper = new DBHelper(getBaseContext());
        db = dbHelper.getWritableDatabase();



        try{
            createTableSim(db);
        }
        catch(Exception e){

        }

        initRetrofit();
        getRoutes();


    }

    void getRoutes(){

        String netType = getNetworkType(getBaseContext());
        if(netType == null){
            Toast.makeText(getBaseContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getRoutes(request_url).enqueue(new Callback<RouteModel>() {
                    @Override
                    public void onResponse(Call<RouteModel> call, Response<RouteModel> response) {

                        RouteModel routeModel = response.body();
                        List<Data> dataList =  routeModel.getData();

                        saveDataInDB(dataList);

                        Log.i(LOG_TAG, "size " + routeModel.getData().size() );

                    }

                    @Override
                    public void onFailure(Call<RouteModel> call, Throwable t) {

                        Toast.makeText(getBaseContext(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getListNews " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getListNews " + e.getMessage());
            }
        }
    }


    void saveDataInDB (List<Data> list){

        ContentValues cv = new ContentValues();
       // Cursor c = db.query("routes", null, null, null, null, null, null);

        for(int i = 0 ; i < 10; i ++){

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

    void createTableSim(SQLiteDatabase db) {

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
     //   db.close();

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
