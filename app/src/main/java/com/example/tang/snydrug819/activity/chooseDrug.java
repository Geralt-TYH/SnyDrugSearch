package com.example.tang.snydrug819.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tang.snydrug819.R;

import com.example.tang.snydrug819.db.drugDB;
import com.example.tang.snydrug819.model.Drug;
import com.example.tang.snydrug819.model.DrugClass;
import com.example.tang.snydrug819.model.DrugSubclass;
import com.example.tang.snydrug819.util.HttpCallbackListener;
import com.example.tang.snydrug819.util.HttpUtil;
import com.example.tang.snydrug819.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tang on 2016/8/19.
 */
public class chooseDrug extends Activity {
    public static final int LEVEL_CLASSES=0;
    public static final int LEVEL_SUBCLASS=1;
    public static final int LEVEL_EFFECT=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private drugDB drugdb;
    private boolean isFromDrugActivity;
    private List<String> dataList=new ArrayList<String>();
    /*
    * 大类列表*/
    private List<DrugClass> classList;
    /*小类列表*/
    private List<DrugSubclass> subclassList;
    /*功效列表*/
    private List<Drug> drugList;
    /*选中的大类,小类，功效*/
    private DrugClass selectCLASS;
    private DrugSubclass selectSUBCLASS;
    private Drug selectDRUG;
    /*当前选中的级别*/
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromDrugActivity=getIntent().getBooleanExtra("from_drug_activity",false);
        SharedPreferences prefes= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefes.getBoolean("selectDRUG",false)&&!isFromDrugActivity){
            Intent intent=new Intent(this,DrugActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_drug);
        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        drugdb = drugDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_CLASSES){
                    selectCLASS=classList.get(position);
                    querySUBCLASS();
                }else if (currentLevel==LEVEL_SUBCLASS){
                    selectSUBCLASS=subclassList.get(position);
                    queryEFFECT();
                }else if (currentLevel==LEVEL_EFFECT){
                    String drugCode=drugList.get(position).getDrugCode();
                    Intent intent=new Intent(chooseDrug.this,DrugActivity.class);
                    intent.putExtra("drugCode",drugCode);
                    startActivity(intent);
                    finish();
                }
            }
        });

        queryCLASS();//加载中药大类信息

    }
    /*
    * 查询所有的总类，优先从数据库中查询，如果没有查询到再去服务器上查询
    * */
    private void queryCLASS() {

        classList= drugdb.loadDrugclass();
        if(classList.size()>0){
            dataList.clear();
            for (DrugClass c:classList){
                dataList.add(c.getDrugclassName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("总类");
            currentLevel=LEVEL_CLASSES;

        }else{
            queryFromServer(null,"CLASSES");
        }
    }
    /*
    * 查询所有子类，优先到数据库中查询，如果没有再到服务器查询*/
    private void querySUBCLASS() {
        subclassList= drugdb.loadDrugsubclass(selectCLASS.getId());
        if(subclassList.size()>0){
            dataList.clear();
            for (DrugSubclass s:subclassList){
                dataList.add(s.getDrugsubclassName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectCLASS.getDrugclassName());
            currentLevel=LEVEL_SUBCLASS;
        }else
        {
            queryFromServer(selectCLASS.getDrugclassCode(),"SUBCLASS");
        }

    }
    /*查询功效
    * */
    private void queryEFFECT() {
        drugList= drugdb.loadDrug(selectSUBCLASS.getId());
        if(drugList.size()>0){
            dataList.clear();
            for (Drug e:drugList){
                dataList.add(e.getDrugName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectSUBCLASS.getDrugsubclassName());
            currentLevel=LEVEL_EFFECT;
        }else{
            queryFromServer(selectSUBCLASS.getDrugsubclassCode(),"EFFECT");
        }
    }
    /*
    * 根据传入的子类代号和类型从服务器上查询总类子类功效信息*/
    private void queryFromServer(final String code, final String type) {
        String address;

        if(!TextUtils.isEmpty(code)){
            address="http://10.50.119.77/snydrug/data/list/drug"+code+".html";//TODO
        }else{
            address="http://10.50.119.77/snydrug/data/list/drug.html";//TODO
        }
        showProgreessDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("CLASSES".equals(type)){
                    result= Utility.handleDrugclassResponse(drugdb, response);
                }
                else if("SUBCLASS".equals(type)){
                    result=Utility.handleDrugsubclassResponse(drugdb,response,selectCLASS.getId());
                }
                else if("EFFECT".equals(type)){
                    result=Utility.handleDrugResponse(drugdb,response,selectSUBCLASS.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("CLASSES".equals(type)){
                                queryCLASS();
                            }
                            if("SUBCLASS".equals(type)){
                                querySUBCLASS();
                            }
                            if("EFFECT".equals(type)){
                                queryEFFECT();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(chooseDrug.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
    /*
    显示进度对话框
    * */
    private void showProgreessDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /*
    * 关闭进度条
    * */
    private void closeProgressDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_EFFECT){
            querySUBCLASS();
        }else if(currentLevel==LEVEL_SUBCLASS){
            queryCLASS();
        }else{
            finish();
        }
    }
}
