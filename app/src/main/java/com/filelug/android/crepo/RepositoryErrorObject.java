package com.filelug.android.crepo;

public class RepositoryErrorObject {

	private int code;
	private String message;

	public RepositoryErrorObject() {
		this(-1, null);
	}

	public RepositoryErrorObject(String message) {
		this(-1, message);
	}

	public RepositoryErrorObject(int errorCode, String message) {
		this.code = errorCode;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return this.code + "-" + this.message;
	}

}
