package com.filelug.android.docsprovider;

import android.text.TextUtils;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class DocumentIdUtils {

    private static final String NODE_ACCOUNT_NOT_SET = "notSet";
    private static final String NODE_ACCOUNT_PREFIX = "a:";
    private static final String NODE_COMPUTER_PREFIX = "c:";
    private static final String NODE_ROOT_DIRECTORY_PREFIX = "r:";
    private static final String NODE_FILE_PREFIX = "f:";
    public static final String NODE_SEPARATOR = ";";

    public static boolean isNotSet(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return false;
        String[] idParts = documentId.split(NODE_SEPARATOR);
        if ( idParts.length != 1 ) return false;
        return documentId.equals(NODE_ACCOUNT_NOT_SET);
    }

    public static String getAccountNotSetDocumentId() {
        return NODE_ACCOUNT_NOT_SET;
    }

    public static boolean isAccountRoot(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return false;
        String[] idParts = documentId.split(NODE_SEPARATOR);
        if ( idParts.length != 1 ) return false;
        return documentId.startsWith(NODE_ACCOUNT_PREFIX);
    }

    public static String getAccountDocumentId(String accountName) {
        return NODE_ACCOUNT_PREFIX + accountName;
    }

    public static String getAccountName(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return null;
        return documentId.substring(NODE_ACCOUNT_PREFIX.length(), documentId.length());
    }

    public static boolean isComputer(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return false;
        String[] idParts = documentId.split(NODE_SEPARATOR);
        if ( idParts.length != 2 ) return false;
        return idParts[0].startsWith(NODE_ACCOUNT_PREFIX) && idParts[1].startsWith(NODE_COMPUTER_PREFIX);
    }

    public static String getComputerDocumentId(String accountName, int computerId) {
        return NODE_ACCOUNT_PREFIX + accountName + NODE_SEPARATOR + NODE_COMPUTER_PREFIX + computerId;
    }

    public static String[] getComputerIdAndParents(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return null;
        String[] ids = documentId.split(NODE_SEPARATOR);
        if ( ids.length != 2 ) return null;
        String accountName = ids[0].substring(NODE_ACCOUNT_PREFIX.length(), ids[0].length());
        String computerIdStr = ids[1].substring(NODE_COMPUTER_PREFIX.length(), ids[1].length());
        return new String[] { accountName, computerIdStr };
    }

    public static boolean isRootDirectory(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return false;
        String[] idParts = documentId.split(NODE_SEPARATOR);
        if ( idParts.length != 3 ) return false;
        return idParts[0].startsWith(NODE_ACCOUNT_PREFIX) && idParts[1].startsWith(NODE_COMPUTER_PREFIX) && idParts[2].startsWith(NODE_ROOT_DIRECTORY_PREFIX);
    }

    public static String getRootDirectoryDocumentId(String accountName, int computerId, long rootDirectoryRowId) {
        return NODE_ACCOUNT_PREFIX + accountName + NODE_SEPARATOR + NODE_COMPUTER_PREFIX + computerId + NODE_SEPARATOR + NODE_ROOT_DIRECTORY_PREFIX + rootDirectoryRowId;
    }

    public static String[] getRootDirectoryRowIdAndParents(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return null;
        String[] ids = documentId.split(NODE_SEPARATOR);
        if ( ids.length != 3 ) return null;
        String accountName = ids[0].substring(NODE_ACCOUNT_PREFIX.length(), ids[0].length());
        String computerIdStr = ids[1].substring(NODE_COMPUTER_PREFIX.length(), ids[1].length());
        String rootDirectoryRowIdStr = ids[2].substring(NODE_ROOT_DIRECTORY_PREFIX.length(), ids[2].length());
        return new String[] { accountName, computerIdStr, rootDirectoryRowIdStr };
    }

    public static boolean isFile(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return false;
        String[] idParts = documentId.split(NODE_SEPARATOR);
        if ( idParts.length != 3 ) return false;
        return idParts[0].startsWith(NODE_ACCOUNT_PREFIX) && idParts[1].startsWith(NODE_COMPUTER_PREFIX) && idParts[2].startsWith(NODE_FILE_PREFIX);
    }

    public static String getFileDocumentId(String accountName, int computerId, long fileRowId) {
        return NODE_ACCOUNT_PREFIX + accountName + NODE_SEPARATOR + NODE_COMPUTER_PREFIX + computerId + NODE_SEPARATOR + NODE_FILE_PREFIX + fileRowId;
    }

    public static String[] getFileRowIdAndParents(String documentId) {
        if ( TextUtils.isEmpty(documentId) ) return null;
        String[] ids = documentId.split(NODE_SEPARATOR);
        if ( ids.length != 3 ) return null;
        String accountName = ids[0].substring(NODE_ACCOUNT_PREFIX.length(), ids[0].length());
        String computerIdStr = ids[1].substring(NODE_COMPUTER_PREFIX.length(), ids[1].length());
        String fileRowIdStr = ids[2].substring(NODE_FILE_PREFIX.length(), ids[2].length());
        return new String[] { accountName, computerIdStr, fileRowIdStr };
    }

}
