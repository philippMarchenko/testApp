package com.devfill.testapp.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.devfill.testapp.R;
import com.devfill.testapp.ServiceRoutes;
import com.devfill.testapp.ui.fragments.DetailFragment;
import com.devfill.testapp.ui.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    public static final int IDM_SQ_LITE = 201;          //константы для меню выбора БД
    public static final int IDM_REALM = 202;


    MainFragment mainFragment  = new MainFragment();    //создали обьект фрагмента со списком маршрутов

    private static final String LOG_TAG = "MainActivity";

    public static final int STATE_MAIN = 0;             //константы для определения типа действующего фрагмента(для востановления его после поворота)
    public static final int STATE_DETAIL = 1;
    public static int state = STATE_MAIN;
    public static int current_id;                       //для хранения состояния

    private FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "state " + state);

        if(!isMyServiceRunning(ServiceRoutes.class)) {              //если сервис не запущен
            Intent intent = new Intent(this, ServiceRoutes.class);  //запустим
            startService(intent);
        }

        ft = this.getSupportFragmentManager().beginTransaction();
        if(state == STATE_MAIN){
            ft.replace(R.id.container, mainFragment);       //востановление фрагмента после поворота
        }
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //создаем меню и его пункты
        SubMenu subMenuChooseTypePoint = menu.addSubMenu("Тип базы данных");
        subMenuChooseTypePoint.add(Menu.NONE, IDM_SQ_LITE, Menu.NONE, "Выбрать SQLite");
        subMenuChooseTypePoint.add(Menu.NONE, IDM_REALM, Menu.NONE, "Выбрать Realm");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences mySharedPreferences = getBaseContext().getSharedPreferences(ServiceRoutes.TYPE_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        switch (item.getItemId()) {                     //в зависимости от выбраного типа БД, результат сохраним в SharedPreferences
            case IDM_SQ_LITE:
                editor.putInt("db_type", IDM_SQ_LITE);
                editor.apply();
                return true;
            case IDM_REALM:
                editor.putInt("db_type", IDM_REALM);
                editor.apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy" );
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("state", state);                //сохраним состояние фрагмента и ID маршрута
        outState.putInt("id", current_id);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = savedInstanceState.getInt("state");
        current_id = savedInstanceState.getInt("id");
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }

}
