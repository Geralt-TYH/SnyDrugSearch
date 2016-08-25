package com.example.tang.snydrug819.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.tang.snydrug819.db.drugDB;
import com.example.tang.snydrug819.model.Drug;
import com.example.tang.snydrug819.model.DrugClass;
import com.example.tang.snydrug819.model.DrugSubclass;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by Tang on 2016/8/19.
 */
public class Utility {
    public synchronized static boolean handleDrugclassResponse(drugDB drugDB, String response){
        if(!TextUtils.isEmpty(response)){
            String[] allDrugclass=response.split(",");
            if(allDrugclass!=null&&allDrugclass.length>0){
                for (String p:allDrugclass){
                    String[] array=p.split("\\|");
                    DrugClass drugClass=new DrugClass();
                    drugClass.setDrugclassCode(array[0]);
                    drugClass.setDrugclassName(array[1]);
                    drugDB.saveDrugclass(drugClass);
                }
                return true;
            }
        }
        return false;
    }
    public synchronized static boolean handleDrugsubclassResponse(drugDB drugDB, String response, int class_id){
        if(!TextUtils.isEmpty(response)){
            String[] allDrugsubclass=response.split(",");
            if(allDrugsubclass!=null&&allDrugsubclass.length>0){
                for (String p:allDrugsubclass){
                    String[] array=p.split("\\|");
                    DrugSubclass drugSubclass=new DrugSubclass();
                    drugSubclass.setDrugsubclassCode(array[0]);
                    drugSubclass.setDrugsubclassName(array[1]);
                    drugSubclass.setDrugclassId(class_id);
                    drugDB.saveDrugsubclass(drugSubclass);
                }
                return true;
            }
        }
        return false;
    }
    public synchronized static boolean handleDrugResponse(drugDB drugDB, String response, int subclass_id){
        if(!TextUtils.isEmpty(response)){
            String[] allDrug=response.split(",");
            if(allDrug!=null&&allDrug.length>0){
                for (String p:allDrug){
                    String[] array=p.split("\\|");
                    Drug drug=new Drug();
                    drug.setDrugCode(array[0]);
                    drug.setDrugName(array[1]);
                    drug.setDrugsubclassId(subclass_id);
                    drugDB.saveDrug(drug);
                }
                return true;
            }
        }
        return false;
    }
    public static void handleDrugEffectResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject druginfo=jsonObject.getJSONObject("druginfo");
            String drugname=druginfo.getString("drugname");
            String drugid=druginfo.getString("drugid");
            String drugclass=druginfo.getString("drugclass");
            String drugsubclass=druginfo.getString("drugsubclass");
            String flavor=druginfo.getString("flavor");
            String effect=druginfo.getString("effect");
            saveDruginfo(context,drugname,drugid,drugclass,drugsubclass,flavor,effect);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private static void saveDruginfo(Context context, String drugname, String drugid, String drugclass, String drugsubclass, String flavor, String effect) {
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("selectDRUG",true);
        editor.putString("drugname",drugname);
        editor.putString("drugid",drugid);
        editor.putString("drugclass",drugclass);
        editor.putString("drugsubclass",drugsubclass);
        editor.putString("flavor",flavor);
        editor.putString("effect",effect);
        editor.commit();

    }
}
