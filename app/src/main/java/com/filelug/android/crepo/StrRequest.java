package com.filelug.android.crepo;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.filelug.android.util.AccountUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class StrRequest extends JsonRequest<String> {

	private final Listener<String> mListener;
	private HashMap<String, String> mParams = new HashMap<String, String>();
	private HashMap<String, String> mHeaders = new HashMap<String, String>();
	private boolean mUpdateAccessTime = true;

	public StrRequest(int method, String url, JSONObject jsonRequest, Listener<String> listener, ErrorListener errorListener) {
		this(method, url, jsonRequest, listener, errorListener, true);
	}

	public StrRequest(int method, String url, JSONObject jsonRequest, Listener<String> listener, ErrorListener errorListener, boolean isUpdateTime) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
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

	public StrRequest addHeader(String name, String value) {
		mHeaders.put(name, value);
		return this;
	}

	public StrRequest addParameter(String name, String value) {
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
	protected void deliverResponse(String response) {
		if (mListener != null) {
			mListener.onResponse(response);
			if ( mUpdateAccessTime ) {
				AccountUtils.setActiveAccountAccessTime();
			}
		}
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			String str = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
			return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

}
