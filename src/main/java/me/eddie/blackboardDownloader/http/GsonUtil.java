package me.eddie.blackboardDownloader.http;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class GsonUtil {
    private static Gson gson = null;
    private static JsonParser json = null;

    public static Gson getGson(){
        if(gson == null){
            gson = new Gson();
        }
        return gson;
    }

    public static JsonParser getParser(){
        if(json == null){
            json = new JsonParser();
        }
        return json;
    }
}

