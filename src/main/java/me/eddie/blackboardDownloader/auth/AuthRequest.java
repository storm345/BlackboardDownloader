package me.eddie.blackboardDownloader.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AuthRequest {
    private String response_type = "code";
    private String redirect_uri;
    private String client_id;
    private String scope = "read";
    private String state;

    public AuthRequest(String redirect_uri, String client_id, String state) {
        this.redirect_uri = redirect_uri;
        this.client_id = client_id;
        this.state = state;
    }

    public String toURL(String baseURL){
        try {
            return baseURL+"?"
                    +"response_type="+ URLEncoder.encode(response_type, "UTF-8")
                    +"&redirect_uri="+ URLEncoder.encode(redirect_uri, "UTF-8")
                    +"&client_id="+ URLEncoder.encode(client_id, "UTF-8")
                    +"&scope="+ URLEncoder.encode(scope, "UTF-8")
                    +"&state="+ URLEncoder.encode(state, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
