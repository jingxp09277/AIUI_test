package com.ask.myapplication.mywebsocketclient;

import android.os.Environment;
import android.util.Log;

import com.ask.myapplication.Decoder;
import com.ask.myapplication.ResponseData;
import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSONUtil;

/**
 * 此类是一开始给到的java websocket demo,使用 commons-codec-1.6.jar和Java-WebSocket-1.3.7.jar
 * Created by LWB on 2020/5/27
 */
public class AIUIGO1 {
    static String TAG = AIUIGO1.class.getSimpleName();

    private static final String BASE_URL = "ws://wsapi.xfyun.cn/v1/aiui";
    private static final String ORIGIN = "http://wsapi.xfyun.cn";
    private static final String APPID = "5ecb87b2";
    private static final String APIKEY = "0e633770d549cf2aa02d4b362346aa4b";
    //    private static final String FILE_PATH = "D://test/hello.wav";

    private static final Gson json = new Gson();
    private Decoder decoder = new Decoder();

    private static String FILE_PATH;
    private static final int CHUNCKED_SIZE = 1280;


    public static void init() throws UnsupportedEncodingException, URISyntaxException, InterruptedException {
        File mAudioDirFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        String mPath = mAudioDirFile.getPath();
        String filename = mPath + "/20200525_183206.pcm";
        Log.d(TAG, "init: filename " + filename);
        FILE_PATH = filename;

        String url1 = getHandShakeParams();
        URI url = new URI(BASE_URL + url1);
        Log.d(TAG, "init: " + BASE_URL + url1);
        DraftWithOrigin draft = new DraftWithOrigin(ORIGIN);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MyWebSocketClient client = new MyWebSocketClient(url, draft, countDownLatch);
        client.connect();
        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            System.out.println("连接中");
            Thread.sleep(1000);
        }
        /*// 发送音频
        byte[] bytes = new byte[CHUNCKED_SIZE];
        try (RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "r")) {
            int len = -1;
            while ((len = raf.read(bytes)) != -1) {
                if (len < CHUNCKED_SIZE) {
                    bytes = Arrays.copyOfRange(bytes, 0, len);
                }
                send(client, bytes);
                Thread.sleep(40);
            }
            send(client, "--end--".getBytes());
            System.out.println("发送结束标识完成");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        System.out.println("等待连接关闭");
        countDownLatch.await();
    }

    public static String getHandShakeParams() throws UnsupportedEncodingException {
        /*String param = "{\"result_level\":\"complete\",\"auth_id\":\"894c985bf8b1111c6728db79d3479aef\"," +
                "\"data_type\":\"audio\",\"aue\":\"raw\",\"scene\":\"main_box\",\"sample_rate\":\"16000\"," +
//                "\"from\":\"cn\",\"to\":\"en\","+
                "\"attachparams\":{" +
//                "\"pers_param\":{" +
                "\"iat-params\":{" +
                "\"language\":\"en_us\",\"accent\":\"mandarin\""+
                ",\"ptt\":\"1\"" +
                "}" +
                "}" +
                "}";*/
//        zh-cn：mandarin，lmz，cantonese//en-us
        String param = "{\"language\":\"zh-cn\",\"accent\":\"mandarin\",\"ptt\":\"1\",\"result_level\":\"complete\",\"auth_id\":\"894c985bf8b1111c6728db79d3479aef\",\"data_type\":\"audio\",\"aue\":\"raw\",\"scene\":\"main_box\",\"sample_rate\":\"16000\"}";
        String paramBase64 = new String(Base64.encodeBase64(param.getBytes(StandardCharsets.UTF_8)));

        String curtime = System.currentTimeMillis() / 1000L + "";
        String signtype = "sha256";
        String originStr = APIKEY + curtime + paramBase64;
        String checksum = getSHA256Str(originStr);

        return "?appid=" + APPID + "&checksum=" + checksum + "&curtime=" + curtime + "&param=" + paramBase64 + "&signtype=" + signtype;
    }

    public static void send(WebSocketClient client, byte[] bytes) {
        if (client.isClosed()) {
            throw new RuntimeException("client connect closed!");
        }
        client.send(bytes);
    }

    private static class MyWebSocketClient extends WebSocketClient {

        private CountDownLatch countDownLatch;
        private Decoder decoder = new Decoder();

        public MyWebSocketClient(URI serverUri, Draft protocolDraft, CountDownLatch countDownLatch) {
            super(serverUri, protocolDraft);
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            System.out.println("打开连接, code:" + handshake.getHttpStatusMessage());
        }

        @Override
        public void onMessage(String msg) {
            /*System.out.println("msg: " + msg);
            if (JSONUtil.isJson(msg)) {
                Log.d(TAG, "onMessage: msg is json");
            }*/
            Log.d(TAG, "onMessage: " + msg);
            String resptext = "";
            ResponseData resp = null;

//            Log.d(TAG, "onMessage: dataaaa"+JSONUtil.parseObj(msg).getStr("data").isEmpty());
            if (!JSONUtil.parseObj(msg).getStr("data").isEmpty()) {
                resp = json.fromJson(msg, ResponseData.class);
            }
            ;

            if (resp != null) {
                if (resp.getCode() != 0) {
                    Log.e(TAG, "code=>" + resp.getCode() + " error=>" + resp.getDesc() + " sid=" + resp.getSid());
                    //其中 sid 字段主要用于追查问题，如果出现问题，可以提供 sid 给讯飞技术人员帮助确认问题
                    return;
                }

                if (resp.getAction().equals("result")) {
                    if (resp.getData() != null) {
                        ResponseData.Text te = resp.getData().text.getText();
                        Log.d(TAG, "onMessage: text is " + te.toString());
                        try {
                            decoder.decode(te);
                            resptext = decoder.toString();
                            Log.d(TAG, "onMessage:middle result:" + decoder.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (resp.getData().is_last) {
                            Log.d(TAG, "onMessage: is last is true");
                        }
                    }
                }
            }

        }

        @Override
        public void onError(Exception e) {
            System.out.println("连接发生错误：" + e.getMessage() + ", " + new Date());
            e.printStackTrace();
        }

        @Override
        public void onClose(int arg0, String arg1, boolean arg2) {
            System.out.println("链接已关闭" + "," + new Date());
            countDownLatch.countDown();
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            System.out.println("服务端返回：" + new String(bytes.array(), StandardCharsets.UTF_8));
        }
    }

    /***
     * 利用Apache的工具类实现SHA-256加密
     *
     * @param str
     *            加密后的报文
     * @return
     */
    public static String getSHA256Str(String str) {
        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
//            encdeStr = SecureUtil.sha256(String.valueOf(hash));
//            encdeStr = Hex.encodeHexString(hash);
            encdeStr = HexUtil.encodeHexStr(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encdeStr;
    }
}
