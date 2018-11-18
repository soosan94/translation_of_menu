package com.example.owner.real_final.database;

import java.io.Serializable;

public class TravelData implements Serializable{
    String key;
    String budget;
    String expense;
    String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    String nation;
    String maxDay;
    String minDay;

    public TravelData(){}

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getNation() {return nation;}
    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getMinDay() {
        return minDay;
    }
    public void setMinDay(String minDay) {
        this.minDay = minDay;
    }

    public String getMaxDay() {
        return maxDay;
    }
    public void setMaxDay(String maxDay) {
        this.maxDay = maxDay;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }


    public TravelData(String key, String nation, String minDay, String maxDay,String budget,String expense,String unit){
        this.key=key;
        this.nation = nation;
        this.maxDay = maxDay;
        this.minDay=minDay;
        this.budget = budget;
        this.expense = expense;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return " "+nation+"\n" +
                " "+minDay +"\n" +
                " "+maxDay;
    }

   /* public Barang(String nm, String mrk, String hrg){
        nation = nm;
        minDay = mrk;
        maxDay = hrg;
    }*/
}
