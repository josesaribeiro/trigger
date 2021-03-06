package com.example.trigger;

import com.example.trigger.DoorState;
import com.example.trigger.DoorState.StateCode;

import java.security.cert.Certificate;


public class HttpsDoorSetup implements Setup {
    static final String type = "HttpsDoorSetup";
    int id;
    String name;
    public String open_query;
    public String close_query;
    public String status_query;
    public String ssids;
    public Certificate certificate;
    public Boolean ignore_hostname_mismatch;

    public HttpsDoorSetup(int id, String name) {
        this.id = id;
        this.name = name;
        this.open_query = "";
        this.close_query = "";
        this.status_query = "";
        this.ssids = "";
        this.certificate = null;
        this.ignore_hostname_mismatch = false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getSSIDs() {
        return ssids;
    }

    private static String stripUrls(String... urls) {
        // remove path
        String prefix = "https://";
        for (String url : urls) {
            if (url.startsWith(prefix)) {
                int i = url.indexOf('/', prefix.length());
                if (i > 0) {
                    url = url.substring(0, i);
                }
            }
            return url;
        }

        return "";
    }

    @Override
    public String getRegisterUrl() {
        // extract from known urls
        return stripUrls(open_query, close_query, status_query);
    }

    @Override
    public DoorState parseReply(DoorReply reply) {
        // strip HTML from response
        String msg = android.text.Html.fromHtml(reply.message).toString().trim();

        switch (reply.code) {
            case LOCAL_ERROR:
            case REMOTE_ERROR:
                return new DoorState(StateCode.UNKNOWN, msg);
            case SUCCESS:
                if (msg.equals("UNLOCKED")) {
                    // door unlocked
                    return new DoorState(StateCode.OPEN, msg);
                } else if (msg.equals("LOCKED")) {
                    // door locked
                    return new DoorState(StateCode.CLOSED, msg);
                } else {
                    return new DoorState(StateCode.UNKNOWN, msg);
                }
            default:
                // should not happen
                return new DoorState(StateCode.UNKNOWN, msg);
        }
    }
}
