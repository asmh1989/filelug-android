package com.filelug.android.provider.remotehierarchicalmodel;

/**
 * Possible values for the {@code type} column of the {@code remote_hierarchical_model} table.
 */
public enum RemoteObjectType {
    /**
     * 
     */
    FILE,

    /**
     * 
     */
    DIRECTORY,

    /**
     * 
     */
    WINDOWS_SHORTCUT_FILE,

    /**
     * 
     */
    WINDOWS_SHORTCUT_DIRECTORY,

    /**
     * 
     */
    UNIX_SYMBOLIC_LINK_FILE,

    /**
     * 
     */
    UNIX_SYMBOLIC_LINK_DIRECTORY,

    /**
     * 
     */
    MAC_ALIAS_FILE,

    /**
     * 
     */
    MAC_ALIAS_DIRECTORY

}