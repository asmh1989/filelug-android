package com.filelug.android.docsprovider.cursor;

import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.DocumentsContract;

/**
 * Created by Vincent Chang on 2016/6/8.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class BaseCursor extends MatrixCursor {

    private Bundle mExtras;

    public BaseCursor(String[] columnNames) {
        super(columnNames);
    }

    @Override
    public Bundle getExtras() {
        if (mExtras == null) {
            return super.getExtras();
        }
        return mExtras;
    }

    public void putString(String key, String value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putString(key, value);
    }

    public void putBoolean(String key, boolean value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBoolean(key, value);
    }

    public void setIsLoading(boolean isLoading) {
        putBoolean(DocumentsContract.EXTRA_LOADING, isLoading);
    }

    public void setErrorInformation(String errorMessage) {
        putString(DocumentsContract.EXTRA_ERROR, errorMessage);
    }

    public void setPromptMessage(String promptMessage) {
        putString(DocumentsContract.EXTRA_PROMPT, promptMessage);
    }

}
