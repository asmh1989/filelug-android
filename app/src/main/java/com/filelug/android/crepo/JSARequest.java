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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class JSARequest extends JsonRequest<JSONArray> {

	private final Listener<JSONArray> mListener;
	private HashMap<String, String> mParams = new HashMap<String, String>();
	private HashMap<String, String> mHeaders = new HashMap<String, String>();
	private boolean mUpdateAccessTime;

	public JSARequest(int method, String url, JSONObject jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener) {
		this(method, url, jsonRequest, listener, errorListener, true);
	}

	public JSARequest(int method, String url, JSONObject jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener, boolean isUpdateTime) {
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

	public JSARequest addHeader(String name, String value) {
		mHeaders.put(name, value);
		return this;
	}

	public JSARequest addParameter(String name, String value) {
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

	// Copy from com.android.volley.toolbox.JsonArrayRequest.parseNetworkResponse()
	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
			return Response.success(new JSONArray(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

}
