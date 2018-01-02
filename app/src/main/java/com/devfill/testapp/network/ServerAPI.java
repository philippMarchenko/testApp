package com.devfill.testapp.network;


import com.devfill.testapp.model.RouteModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ServerAPI {

    String BASE_URL = "http://projects.gmoby.org/";


    @GET("/web/index.php/api/trips")
    Call<RouteModel> getRoutes(@Query(value = "text") String text); //GET запрос на сервер по данные маршрутов

}