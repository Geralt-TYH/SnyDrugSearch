package com.example.tang.snydrug819.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tang.snydrug819.R;
import com.example.tang.snydrug819.util.HttpCallbackListener;
import com.example.tang.snydrug819.util.HttpUtil;
import com.example.tang.snydrug819.util.Utility;

import org.w3c.dom.Text;

/**
 * Created by Tang on 2016/8/25.
 */
public class DrugActivity extends Activity {
    private LinearLayout drugInfoLayout;
    /*显示药名*/
    private TextView drugnameText;
    private TextView drugflavor;
    private TextView drugeffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drug__layout);
        //初始化控件
        drugInfoLayout= (LinearLayout) findViewById(R.id.drug_info_layout);
        drugnameText= (TextView) findViewById(R.id.drug_name);
        drugflavor= (TextView) findViewById(R.id.drug_flavor);
        drugeffect= (TextView) findViewById(R.id.drug_effect);
        String drugCode=getIntent().getStringExtra("drugCode");
        if(!TextUtils.isEmpty(drugCode)){
            //有中药小类代号就去查询归经合功效
            drugflavor.setText("同步中...");
            drugInfoLayout.setVisibility(View.INVISIBLE);
            drugnameText.setVisibility(View.INVISIBLE);
            queryDrugInfoCode(drugCode);
        }else{
            //没有中药小类代号就直接显示本地资料
            showDrug();
        }

    }



    private void queryDrugInfoCode(String drugCode) {
        String address="http://10.50.119.77/snydrug/data/list/drug"+drugCode+".html";//TODO
        queryFromServer(address,"drugCode");
    }

    private void queryDrugInfo(String drugInfoCode){
        String address="http://10.50.119.77/snydrug/data/druginfo/"+drugInfoCode+".html";//TODO
        queryFromServer(address,"drugInfoCode");

    }
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("drugCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length==2){
                            String drugInfoCode=array[1];
                            queryDrugInfo(drugInfoCode);
                        }
                    }
                }else if("drugInfoCode".equals(type)){
                    Utility.handleDrugEffectResponse(DrugActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDrug();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drugflavor.setText("同步失败");
                    }
                });

            }
        });


    }

    private void showDrug() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        drugnameText.setText(prefs.getString("drugname",""));
        drugflavor.setText("归经:\n"+prefs.getString("flavor",""));
        drugeffect.setText("功效:\n"+prefs.getString("effect",""));
        drugInfoLayout.setVisibility(View.VISIBLE);
        drugnameText.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,chooseDrug.class);
        intent.putExtra("from_drug_activity",true);
        startActivity(intent);
        finish();
    }
}
