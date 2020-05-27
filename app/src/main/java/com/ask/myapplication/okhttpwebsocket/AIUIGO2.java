package com.ask.myapplication.okhttpwebsocket;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static com.ask.myapplication.mywebsocketclient.AIUIGO1.getHandShakeParams;

/**
 * Created by LWB on 2020/5/27
 */
public class AIUIGO2 extends WebSocketListener {
    static String TAG = AIUIGO2.class.getSimpleName();

    private static final String BASE_URL = "ws://wsapi.xfyun.cn/v1/aiui";
    private static AIUIGO2 mAIUIGo;

    //单例模式
    public static AIUIGO2 getInstance() {
        if (mAIUIGo == null) {
            mAIUIGo = new AIUIGO2();
        }
        return mAIUIGo;
    }

    private WebSocket webSocket;


    public void onAIUIGO() throws UnsupportedEncodingException {
        String url = null;
        url = BASE_URL + getHandShakeParams();
        Log.d(TAG, "onAIUIGO: url "+url);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url1 = url.toString().replace("http://", "ws://").replace("https://", "wss://"); //真正地址
        url1 = "ws://wsapi.xfyun.cn/v1/aiui?appid=5ecb87b2&checksum=3c4c8ebd65acc377622d2478a5b61912f4b2f9979df95cb4259ccfcab914c4f2&curtime=1590572684&param=eyJsYW5ndWFnZSI6InpoLWNuIiwiYWNjZW50IjoibWFuZGFyaW4iLCJwdHQiOiIxIiwicmVzdWx0X2xldmVsIjoiY29tcGxldGUiLCJhdXRoX2lkIjoiODk0Yzk4NWJmOGIxMTExYzY3MjhkYjc5ZDM0NzlhZWYiLCJkYXRhX3R5cGUiOiJhdWRpbyIsImF1ZSI6InJhdyIsInNjZW5lIjoibWFpbl9ib3giLCJzYW1wbGVfcmF0ZSI6IjE2MDAwIn0=&signtype=sha256";
        Request request = new Request.Builder().url(url1).build();
        webSocket = client.newWebSocket(request, mAIUIGo);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
        Log.d(TAG, "onClosed: code " + code + " ,reason " + reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        Log.d(TAG, "onFailure: code " + t + " ,reason " + response);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        Log.d(TAG, "onMessage: text " + text);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        Log.d(TAG, "onOpen: response " + response);
    }
}
