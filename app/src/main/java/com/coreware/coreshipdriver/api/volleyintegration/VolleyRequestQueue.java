package com.coreware.coreshipdriver.api.volleyintegration;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyRequestQueue {
    public static final String LOG_TAG = VolleyRequestQueue.class.getName();

    private static VolleyRequestQueue mQueueInstance;
    private RequestQueue mRequestQueue;

    private Map<String, List<Request>> mRequestsByUrl = new HashMap<>();

    private VolleyRequestQueue(Context context) {
        mRequestQueue = getRequestQueue(context);
    }

    public static synchronized VolleyRequestQueue getInstance(Context context) {
        if (mQueueInstance == null) {
            mQueueInstance = new VolleyRequestQueue(context);
        }
        return mQueueInstance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Context context, Request<T> request) {
        List<Request> requests = mRequestsByUrl.get(request.getUrl());
        if (requests == null) {
            requests = new ArrayList<>();
        }
        requests.add(request);
        mRequestsByUrl.put(request.getUrl(), requests);
        getRequestQueue(context).add(request);
    }

    public void cancelRequestsByUrl(String url) {
        if (mRequestsByUrl.containsKey(url)) {
            List<Request> requestList = mRequestsByUrl.get(url);
            for (Request request : requestList) {
                request.cancel();
            }
            mRequestsByUrl.put(url, new ArrayList<>());
        }
    }

    public void removeRequestFromQueue(Request request) {
        if (mRequestsByUrl.containsKey(request.getUrl())) {
            mRequestsByUrl.get(request.getUrl()).remove(request);
        }
    }

}
