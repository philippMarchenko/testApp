package com.devfill.testapp.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.devfill.testapp.R;
import com.devfill.testapp.ServiceRoutes;
import com.devfill.testapp.ui.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {


    MainFragment mainFragment  = new MainFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(!isMyServiceRunning(ServiceRoutes.class)) {
            Intent intent = new Intent(this, ServiceRoutes.class);
            startService(intent);
        }


        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mainFragment);
        ft.commit();
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
}
