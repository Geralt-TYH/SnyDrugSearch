package com.example.tang.snydrug819.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tang.snydrug819.model.Drug;
import com.example.tang.snydrug819.model.DrugClass;
import com.example.tang.snydrug819.model.DrugSubclass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tang on 2016/8/19.
 */
public class drugDB {
    /*
    * 数据库名
    * */
    public static final String DB_NAME="snydrug";
    /*
    * 数据库版本
    * */
    public static final int VERSION=1;
    private  static com.example.tang.snydrug819.db.drugDB drugDB;
    private SQLiteDatabase db;
    private drugDB(Context context){
        drugOpenHelper dbHelper=new drugOpenHelper(context, DB_NAME, null,VERSION);
        db=dbHelper.getWritableDatabase();
    }
    public synchronized static com.example.tang.snydrug819.db.drugDB getInstance(Context context){
        if(drugDB ==null){
            drugDB =new drugDB(context);
        }
        return drugDB;
    }
    public void saveDrugclass(DrugClass drugClass){
        if(drugClass!=null){
            ContentValues values=new ContentValues();
            values.put("class_name",drugClass.getDrugclassName());
            values.put("class_code",drugClass.getDrugclassCode());
            db.insert("DRUG_CLASSES",null,values);
        }
    }
    /*
    * 从数据库读取药的总类信息*/
    public List<DrugClass> loadDrugclass(){
        List<DrugClass> list=new ArrayList<DrugClass>();
        Cursor cursor=db.query("DRUG_CLASSES",null, null, null,null,null ,null);
        if(cursor.moveToFirst()){
            do {
                DrugClass drugClass=new DrugClass();
                drugClass.setId(cursor.getInt(cursor.getColumnIndex("id")));
                drugClass.setDrugclassName(cursor.getString(cursor.getColumnIndex("class_name")));
                drugClass.setDrugclassCode(cursor.getString(cursor.getColumnIndex("class_code")));
                list.add(drugClass);
            }while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
    /*
    将药的小类信息存储到数据库
    */
    public void saveDrugsubclass(DrugSubclass drugSubclass){
        if(drugSubclass!=null){
            ContentValues values=new ContentValues();
            values.put("subclass_name",drugSubclass.getDrugsubclassName());
            values.put("subclass_code",drugSubclass.getDrugsubclassCode());
            values.put("class_id",drugSubclass.getDrugclassId());
            db.insert("DRUG_SUBCLASS",null,values);
        }
    }
    /*读取药的小类信息*/
    public List<DrugSubclass> loadDrugsubclass(int class_id){
        List<DrugSubclass> list=new ArrayList<DrugSubclass>();
        Cursor cursor=db.query("DRUG_SUBCLASS",null,"class_id = ?",new String[]{String.valueOf(class_id)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                DrugSubclass drugSubclass=new DrugSubclass();
                drugSubclass.setId(cursor.getInt(cursor.getColumnIndex("id")));
                drugSubclass.setDrugclassId(cursor.getInt(cursor.getColumnIndex("class_id")));
                drugSubclass.setDrugsubclassName(cursor.getString(cursor.getColumnIndex("subclass_name")));
                drugSubclass.setDrugsubclassCode(cursor.getString(cursor.getColumnIndex("subclass_code")));
                list.add(drugSubclass);
            }while(cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
    /*将药信息存入数据库*/
    public void saveDrug(Drug drug){
        if(drug!=null){
            ContentValues values=new ContentValues();
            values.put("drug_name",drug.getDrugName());
            values.put("drug_code",drug.getDrugCode());
            values.put("subclass_id",drug.getDrugsubclassId());
            db.insert("DRUG",null,values);
        }
    }
    /*从数据库读取某药的信息*/
    public List<Drug> loadDrug(int subclass_id){
        List<Drug> list=new ArrayList<Drug>();
        Cursor cursor=db.query("DRUG",null,"subclass_id=?",new String[]{String.valueOf(subclass_id)},null,null,null);
        if(cursor.moveToFirst()){
            do {
                Drug drug=new Drug();
                drug.setId(cursor.getInt(cursor.getColumnIndex("id")));
                drug.setDrugCode(cursor.getString(cursor.getColumnIndex("drug_code")));
                drug.setDrugName(cursor.getString(cursor.getColumnIndex("drug_name")));
                drug.setDrugsubclassId(cursor.getInt(cursor.getColumnIndex("subclass_id")));
                list.add(drug);
            }while(cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }


}
