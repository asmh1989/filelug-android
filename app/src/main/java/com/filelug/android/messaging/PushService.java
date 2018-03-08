package com.filelug.android.messaging;

/**
 * Created by Vincent Chang on 2015/10/24.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public interface PushService {

    public static final int PUSH_SERVICE_TYPE_NONE = 0;
    public static final int PUSH_SERVICE_TYPE_SNS_MOBILE_SERVICE = 1;
    public static final int PUSH_SERVICE_TYPE_GCM = 2;
    public static final int PUSH_SERVICE_TYPE_BAIDU = 3;

    public static final String PUSH_SERVICE_TYPE_NONE_VALUE = "NONE";
    public static final String PUSH_SERVICE_TYPE_SNS_MOBILE_SERVICE_VALUE = "SNSMobileService";
    public static final String PUSH_SERVICE_TYPE_GCM_VALUE = "GCM";
    public static final String PUSH_SERVICE_TYPE_BAIDU_VALUE = "BAIDU";

    public static final String NOTIFICATION_TYPE_UPLOAD_FILE = "upload-file";
    public static final String NOTIFICATION_TYPE_UPLOAD_FILES = "all-files-uploaded-successfully";

}
