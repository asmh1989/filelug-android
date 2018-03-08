package com.filelug.android.crepo;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.filelug.android.MainApplication;
import com.filelug.android.util.AccountUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JSORequest extends JsonObjectRequest {

	private final Listener<JSONObject> mListener;
	private HashMap<String, String> mParams = new HashMap<String, String>();
	private HashMap<String, String> mHeaders = new HashMap<String, String>();
	private boolean mUpdateAccessTime = true;

	public JSORequest(int method, String url, JSONObject jsonRequest, Listener<JSONObject> listener, ErrorListener errorListener) {
		this(method, url, jsonRequest, listener, errorListener, true);
	}

	public JSORequest(int method, String url, JSONObject jsonRequest, Listener<JSONObject> listener, ErrorListener errorListener, boolean isUpdateTime) {
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

	public JSORequest addHeader(String name, String value) {
		mHeaders.put(name, value);
		return this;
	}

	public JSORequest addParameter(String name, String value) {
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
	protected void deliverResponse(JSONObject response) {
		if (mListener != null) {
			mListener.onResponse(response);
			if ( mUpdateAccessTime ) {
				Context context = MainApplication.getInstance().getApplicationContext();
				AccountUtils.setActiveAccountAccessTime();
			}
		}
	}

}
