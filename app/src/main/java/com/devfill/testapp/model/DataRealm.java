package com.devfill.testapp.model;

import io.realm.RealmObject;

public class DataRealm extends RealmObject{

    private int id_route;
    private String from_city_name;
    private int from_city_highlight;
    private int from_city_id;
    private String from_date;
    private String from_time;
    private String from_info;
    private String to_city_name;
    private int to_city_highlight;
    private  int to_city_id;
    private  String info;
    private String to_date;
    private  String to_time;
    private String to_info;
    private  int price;
    private int bus_id;
    private int reservation_coun;


    public DataRealm(){

    }

    public int getId_route() {
        return id_route;
    }

    public void setId_route(int id_route) {
        this.id_route = id_route;
    }

    public String getFrom_city_name() {
        return from_city_name;
    }

    public void setFrom_city_name(String from_city_name) {
        this.from_city_name = from_city_name;
    }

    public int getFrom_city_highlight() {
        return from_city_highlight;
    }

    public void setFrom_city_highlight(int from_city_highlight) {
        this.from_city_highlight = from_city_highlight;
    }

    public int getFrom_city_id() {
        return from_city_id;
    }

    public void setFrom_city_id(int from_city_id) {
        this.from_city_id = from_city_id;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getFrom_time() {
        return from_time;
    }

    public void setFrom_time(String from_time) {
        this.from_time = from_time;
    }

    public String getFrom_info() {
        return from_info;
    }

    public void setFrom_info(String from_info) {
        this.from_info = from_info;
    }

    public String getTo_city_name() {
        return to_city_name;
    }

    public void setTo_city_name(String to_city_name) {
        this.to_city_name = to_city_name;
    }

    public int getTo_city_highlight() {
        return to_city_highlight;
    }

    public void setTo_city_highlight(int to_city_highlight) {
        this.to_city_highlight = to_city_highlight;
    }

    public int getTo_city_id() {
        return to_city_id;
    }

    public void setTo_city_id(int to_city_id) {
        this.to_city_id = to_city_id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getTo_time() {
        return to_time;
    }

    public void setTo_time(String to_time) {
        this.to_time = to_time;
    }

    public String getTo_info() {
        return to_info;
    }

    public void setTo_info(String to_info) {
        this.to_info = to_info;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBus_id() {
        return bus_id;
    }

    public void setBus_id(int bus_id) {
        this.bus_id = bus_id;
    }

    public int getReservation_coun() {
        return reservation_coun;
    }

    public void setReservation_coun(int reservation_coun) {
        this.reservation_coun = reservation_coun;
    }
}