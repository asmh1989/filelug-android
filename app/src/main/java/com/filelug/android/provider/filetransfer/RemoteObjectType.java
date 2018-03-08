package com.filelug.android.provider.filetransfer;

/**
 * Possible values for the {@code type} column of the {@code file_transfer} table.
 */
public enum RemoteObjectType {
    /**
     * 
     */
    FILE,

    /**
     * 
     */
    WINDOWS_SHORTCUT_FILE,

    /**
     * 
     */
    UNIX_SYMBOLIC_LINK_FILE,

    /**
     * 
     */
    MAC_ALIAS_FILE

}