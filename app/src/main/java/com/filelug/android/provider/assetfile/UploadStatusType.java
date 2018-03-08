package com.filelug.android.provider.assetfile;

/**
 * Possible values for the {@code status} column of the {@code asset_file} table.
 */
public enum UploadStatusType {
    /**
     * 
     */
    wait,

    /**
     * 
     */
    processing,

    /**
     * 
     */
    success,

    /**
     * 
     */
    failure,

    /**
     * 
     */
    canceling,

    /**
     * 
     */
    not_found,

    /**
     *
     */
    device_uploaded_but_unconfirmed

}