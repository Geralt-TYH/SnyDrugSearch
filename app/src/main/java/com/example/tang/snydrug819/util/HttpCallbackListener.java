package com.example.tang.snydrug819.util;

/**
 * Created by Tang on 2016/8/19.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
