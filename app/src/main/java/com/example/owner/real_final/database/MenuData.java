package com.example.owner.real_final.database;

import java.io.Serializable;

public class MenuData {

    String mKey; ///mKey 추가? 삭제?
    String mName;
    String mPrice;
    String mAmount;

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public MenuData(){}

    public MenuData(String mKey, String mName,String mPrice,String mAmount){
        this.mKey = mKey;
        this.mName = mName;
        this.mPrice = mPrice;
        this.mAmount = mAmount;
    }
}
