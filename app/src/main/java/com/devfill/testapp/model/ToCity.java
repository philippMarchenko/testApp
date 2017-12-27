package com.devfill.testapp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ToCity {
    @SerializedName("highlight")
    @Expose
    private Integer highlight;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public Integer getHighlight() {
        return highlight;
    }

    public void setHighlight(Integer highlight) {
        this.highlight = highlight;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
