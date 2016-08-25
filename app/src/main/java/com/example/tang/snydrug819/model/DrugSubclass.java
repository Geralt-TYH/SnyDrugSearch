package com.example.tang.snydrug819.model;

/**
 * Created by Tang on 2016/8/19.
 */
public class DrugSubclass {
    private int id;
    private String drugsubclassName;
    private String drugsubclassCode;
    private int drugclassId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrugsubclassName() {
        return drugsubclassName;
    }

    public void setDrugsubclassName(String drugsubclassName) {
        this.drugsubclassName = drugsubclassName;
    }

    public String getDrugsubclassCode() {
        return drugsubclassCode;
    }

    public void setDrugsubclassCode(String drugsubclassCode) {
        this.drugsubclassCode = drugsubclassCode;
    }

    public int getDrugclassId() {
        return drugclassId;
    }

    public void setDrugclassId(int drugclassId) {
        this.drugclassId = drugclassId;
    }
}
