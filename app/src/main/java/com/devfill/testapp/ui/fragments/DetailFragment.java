package com.devfill.testapp.ui.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.devfill.testapp.DBHelper;
import com.devfill.testapp.R;
import com.devfill.testapp.ServiceRoutes;
import com.devfill.testapp.model.DataRealm;
import com.devfill.testapp.ui.MainActivity;

import io.realm.Realm;
import io.realm.RealmResults;


public class DetailFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = "DetailFragment";

    private int id = 0;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private Realm mRealm;

    TextView id_route;
    TextView from_city_highlight;
    TextView from_city_id;
    TextView from_city_name;
    TextView from_date;
    TextView from_time;
    TextView from_info;
    TextView to_city_highlight;
    TextView to_city_id;
    TextView to_city_name;
    TextView to_date;
    TextView to_time;
    TextView to_info;
    TextView info;
    TextView price;
    TextView bus_id;
    TextView reservation_count;

    ViewGroup mContainer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.datail_fragment, container, false);
        mContainer = container;
        id = getArguments().getInt("id");
        MainActivity.current_id = id;
        MainActivity.state = MainActivity.STATE_DETAIL;

        initViews(rootView);

        dbHelper = new DBHelper(getContext());
        mRealm = Realm.getInstance(getContext());


        switch (ServiceRoutes.type_db){
            case MainActivity.IDM_SQ_LITE:
                try{
                    db = dbHelper.getWritableDatabase();
                    showDataRoute(id);
                }
                catch (Exception e){
                }
                break;
            case MainActivity.IDM_REALM:
                showDataRealm(id);
                break;
            default:
                break;
        }

       return rootView;
    }

    private void initViews(View view){

        id_route = view.findViewById(R.id.id_route);
        from_city_highlight = view.findViewById(R.id.from_city_highlight);
        from_city_id = view.findViewById(R.id.from_city_id);
        from_city_name = view.findViewById(R.id.from_city_name);
        from_date = view.findViewById(R.id.from_date);
        from_time = view.findViewById(R.id.from_time);
        from_info = view.findViewById(R.id.from_info);
        to_city_highlight = view.findViewById(R.id.to_city_highlight);
        to_city_id = view.findViewById(R.id.to_city_id);
        to_city_name = view.findViewById(R.id.to_city_name);
        to_date = view.findViewById(R.id.to_date);
        to_time = view.findViewById(R.id.to_time);
        to_info = view.findViewById(R.id.to_info);
        info = view.findViewById(R.id.info);
        price = view.findViewById(R.id.price);
        bus_id = view.findViewById(R.id.bus_id);
        reservation_count = view.findViewById(R.id.reservation_count);
    }

    private void showDataRoute(int id) {

        Cursor c = db.query("routes", null, null, null, null, null, null);

        if (c.moveToPosition(id)) {

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

            Log.i(LOG_TAG, "ID маршрута " + c.getInt(id_routeColIndex) );



            id_route.setText("ID маршрута " + c.getInt(id_routeColIndex));
            from_city_highlight.setText("Highlight: " + c.getInt(from_city_highlightColIndex));
            from_city_id.setText("ID города: " + c.getInt(from_city_idColIndex));
            from_city_name.setText("Имя города: " + c.getString(from_city_nameColIndex));
            from_date.setText("Дата отправления: " + c.getString(from_dateColIndex));
            from_time.setText("Время отправления: " + c.getString(from_timeColIndex));
            from_info.setText("Информация: " + c.getString(from_infoColIndex));
            to_city_highlight.setText("Highlight: " + c.getInt(to_city_highlightColIndex));
            to_city_id.setText("ID города: " + c.getInt(to_city_idColIndex));
            to_city_name.setText("Имя города: " + c.getString(to_city_nameColIndex));
            to_date.setText("Дата прибытия: " +  c.getString(to_dateColIndex));
            to_time.setText("Время прибытия: " +  c.getString(to_timeColIndex));
            to_info.setText("Информация: " +  c.getString(to_infoColIndex));
            info.setText("Общая информация: " +  c.getString(infoColIndex));
            price.setText("Цена: " +  c.getInt(priceColIndex));
            bus_id.setText("Номер маршрута: " +  c.getInt(bus_idColIndex));
            reservation_count.setText("Зарезервировано мест: " +  c.getInt(reservation_countColIndex));


        }
    }

    private void showDataRealm(int id){

        try {
            RealmResults<DataRealm> realmCities= mRealm.where(DataRealm.class).findAllAsync();
            realmCities.load();

            Log.i(LOG_TAG, "realmCities size " + realmCities.size());

            DataRealm dataRealm = realmCities.get(id);

            id_route.setText("ID маршрута " + dataRealm.getId_route());
            from_city_highlight.setText("Highlight: " + dataRealm.getFrom_city_highlight());
            from_city_id.setText("ID города: " + dataRealm.getFrom_city_id());
            from_city_name.setText("Имя города: " + dataRealm.getFrom_city_name());
            from_date.setText("Дата отправления: " + dataRealm.getFrom_date());
            from_time.setText("Время отправления: " + dataRealm.getFrom_time());
            from_info.setText("Информация: " + dataRealm.getFrom_info());
            to_city_highlight.setText("Highlight: " + dataRealm.getTo_city_highlight());
            to_city_id.setText("ID города: " + dataRealm.getTo_city_id());
            to_city_name.setText("Имя города: " + dataRealm.getTo_city_name());
            to_date.setText("Дата прибытия: " +  dataRealm.getTo_date());
            to_time.setText("Время прибытия: " +  dataRealm.getTo_time());
            to_info.setText("Информация: " +  dataRealm.getTo_info());
            info.setText("Общая информация: " +  dataRealm.getInfo());
            price.setText("Цена: " +  dataRealm.getPrice());
            bus_id.setText("Номер маршрута: " +  dataRealm.getBus_id());
            reservation_count.setText("Зарезервировано мест: " +  dataRealm.getReservation_coun());

        }
        catch (Exception e){
        }
    }

}
