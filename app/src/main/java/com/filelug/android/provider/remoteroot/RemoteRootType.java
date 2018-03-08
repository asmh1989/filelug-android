package com.filelug.android.provider.remoteroot;

/**
 * Possible values for the {@code type} column of the {@code remote_root} table.
 */
public enum RemoteRootType {
    /**
     * 
     */
    USER_HOME,

    /**
     *
     */
    LOCAL_DISK,

    /**
     *
     */
    DVD_PLAYER,

    /**
     *
     */
    NETWORK_DISK,

    /**
     *
     */
    EXTERNAL_DISK,

    /**
     *
     */
    TIME_MACHINE,

    /**
     *
     */
    DIRECTORY,

    /**
     * 
     */
    WINDOWS_SHORTCUT_DIRECTORY,

    /**
     * 
     */
    UNIX_SYMBOLIC_LINK_DIRECTORY,

    /**
     *
     */
    MAC_ALIAS_DIRECTORY

}