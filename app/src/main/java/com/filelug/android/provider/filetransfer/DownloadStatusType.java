package com.filelug.android.provider.filetransfer;

/**
 * Possible values for the {@code status} column of the {@code file_transfer} table.
 */
public enum DownloadStatusType {
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
    desktop_uploaded_but_unconfirmed

}