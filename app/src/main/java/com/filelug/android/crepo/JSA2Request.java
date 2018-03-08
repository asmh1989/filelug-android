package com.filelug.android.crepo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.filelug.android.util.AccountUtils;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vincent Chang on 2015/8/25.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class JSA2Request extends JsonArrayRequest {

	private final Listener<JSONArray> mListener;
	private HashMap<String, String> mParams = new HashMap<String, String>();
	private HashMap<String, String> mHeaders = new HashMap<String, String>();
	private boolean mUpdateAccessTime = true;

	public JSA2Request(int method, String url, JSONArray jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener) {
		this(method, url, jsonRequest, listener, errorListener, false);
	}

	public JSA2Request(int method, String url, JSONArray jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener, boolean isUpdateTime) {
		super(method, url, jsonRequest, listener, errorListener);
		mListener = listener;
		mUpdateAccessTime = isUpdateTime;
		addDefaultHeaders();
	}

	private void addDefaultHeaders() {
//		mHeaders.put("Accept", "application/json");
//		mHeaders.put("Accept-Charset", "utf-8");
//		mHeaders.put("Accept-Encoding", "gzip, deflate");
//		mHeaders.put("Content-Type", "application/json");
	}

	public JSA2Request addHeader(String name, String value) {
		mHeaders.put(name, value);
		return this;
	}

	public JSA2Request addParameter(String name, String value) {
		mParams.put(name, value);
		return this;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders;
	}

	@Override
	protected void deliverResponse(JSONArray response) {
		if (mListener != null) {
			mListener.onResponse(response);
			if ( mUpdateAccessTime ) {
				AccountUtils.setActiveAccountAccessTime();
			}
		}
	}

}
