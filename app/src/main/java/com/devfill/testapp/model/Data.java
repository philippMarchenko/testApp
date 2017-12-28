package com.devfill.testapp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Data {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_city")
    @Expose
    private FromCity fromCity;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("from_time")
    @Expose
    private String fromTime;
    @SerializedName("from_info")
    @Expose
    private String fromInfo;
    @SerializedName("to_city")
    @Expose
    private ToCity toCity;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("to_time")
    @Expose
    private String toTime;
    @SerializedName("to_info")
    @Expose
    private String toInfo;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("bus_id")
    @Expose
    private Integer busId;
    @SerializedName("reservation_count")
    @Expose
    private Integer reservationCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FromCity getFromCity() {
        return fromCity;
    }

    public void setFromCity(FromCity fromCity) {
        this.fromCity = fromCity;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getFromInfo() {
        return fromInfo;
    }

    public void setFromInfo(String fromInfo) {
        this.fromInfo = fromInfo;
    }

    public ToCity getToCity() {
        return toCity;
    }

    public void setToCity(ToCity toCity) {
        this.toCity = toCity;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getToInfo() {
        return toInfo;
    }

    public void setToInfo(String toInfo) {
        this.toInfo = toInfo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(Integer reservationCount) {
        this.reservationCount = reservationCount;
    }

}
