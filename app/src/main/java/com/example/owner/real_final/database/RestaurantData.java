
package com.example.owner.real_final.database;

import java.io.Serializable;


public class RestaurantData implements Serializable{
    String rKey;
    String rName;
    String rNation;
    String rBudget;
    String rDate;


    public String getrKey() {
        return rKey;
    }

    public void setrKey(String rKey) {
        this.rKey = rKey;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getrNation() {
        return rNation;
    }

    public void setrNation(String rNation) {
        this.rNation = rNation;
    }

    public String getrBudget() {
        return rBudget;
    }

    public void setrBudget(String rBudget) {
        this.rBudget = rBudget;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }


    public RestaurantData() {
    }

    public RestaurantData(String rKey,String rName, String rDate, String rNation, String rBudget) {
        this.rKey = rKey;
        this.rName = rName;
        this.rNation = rNation;
        this.rBudget = rBudget;
        this.rDate = rDate;
    }
}