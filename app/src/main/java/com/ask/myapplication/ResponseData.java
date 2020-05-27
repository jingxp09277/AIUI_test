package com.ask.myapplication;

import com.google.gson.JsonObject;

import java.util.Arrays;

/**
 * Created by LWB on 2020/5/26
 * parse AIUI websocket JSON
 */
public class ResponseData {
    private String action;
    private Data data;
    private String sid;
    private int code;
    private String desc;

    public String getAction() {
        return action;
    }

    public Data getData() {
        return data;
    }

    public String getSid() {
        return sid;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static class Data {
        String sub;
        String auth_id;
        public Text text;
        int result_id;
        public boolean is_last;
        boolean is_finish;
        JsonArags json_args;
    }

    public static class JsonArags {
        private String language;
        private String accent;

        public String getLanguage() {
            return language;
        }

        public String getAccent() {
            return accent;
        }
    }

    public static class Text {
        int bg;
        int ed;
        boolean ls;
        String pgs;
        String text;
        int sn;
        int[] rg;
        boolean deleted;
        Ws[] ws;
        JsonObject vad;

        public Text getText(){
            StringBuilder sb = new StringBuilder();
            for(Ws ws :this.ws){
                sb.append(ws.cw[0].w);
            }
            text = sb.toString();
            return this;
        }

        @Override
        public String toString (){
            return "Text{" +
                    "bg=" + bg +
                    ", ed=" + ed +
                    ", ls=" + ls +
                    ", sn=" + sn +
                    ", text='" + text + '\'' +
                    ", pgs=" + pgs +
                    ", rg=" + Arrays.toString(rg) +
                    ", deleted=" + deleted +
                    ", vad=" + (vad == null ? "null" : vad.getAsJsonArray("ws").toString()) +
                    '}';
        }
    }

    public static class Ws {
        Cw[] cw;
        int bg;
        int ed;
    }

    public static class Cw {
        int sc;
        String w;
    }
}

