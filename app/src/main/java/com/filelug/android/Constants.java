package com.filelug.android;

public interface Constants {

	public static final boolean DEBUG = true;

	public static final String PROTOCOL_HTTP = "http";
	public static final String PROTOCOL_HTTPS = "https";

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	/* Production Mode */
	public static final String PROTOCOL = PROTOCOL_HTTPS;
	public static final String AA_SERVER = "repo.filelug.com";
	public static final String LUG_SERVER = "%1$s.filelug.com";
	/* Develop Mode */
//	public static final String PROTOCOL = PROTOCOL_HTTP;
////	public static final String AA_SERVER = "192.168.1.107:8080";
//	public static final String AA_SERVER = "192.168.11.4:8080";
////	public static final String AA_SERVER = "192.168.43.148:8080";
//	public static final String LUG_SERVER = AA_SERVER;

	public static final int HTTP_STATUS_CODE_CONTINUE = 100;
	public static final int HTTP_STATUS_CODE_SWITCHING_PROTOCOLS = 101;
	public static final int HTTP_STATUS_CODE_PROCESSING = 102;
	public static final int HTTP_STATUS_CODE_OK = 200;
	public static final int HTTP_STATUS_CODE_CREATED = 201;
	public static final int HTTP_STATUS_CODE_ACCEPTED = 202;
	public static final int HTTP_STATUS_CODE_NON_AUTHORITATIVE_INFORMATION = 203;
	public static final int HTTP_STATUS_CODE_NO_CONTENT = 204;
	public static final int HTTP_STATUS_CODE_RESET_CONTENT = 205;
	public static final int HTTP_STATUS_CODE_PARTIAL_CONTENT = 206;
	public static final int HTTP_STATUS_CODE_MULTI_STATUS = 207;
	public static final int HTTP_STATUS_CODE_MULTIPLE_CHOICES = 300;
	public static final int HTTP_STATUS_CODE_MOVED_PERMANENTLY = 301;
	public static final int HTTP_STATUS_CODE_MOVED_TEMPORARILY = 302;
	public static final int HTTP_STATUS_CODE_SEE_OTHER = 303;
	public static final int HTTP_STATUS_CODE_NOT_MODIFIED = 304;
	public static final int HTTP_STATUS_CODE_USE_PROXY = 305;
	public static final int HTTP_STATUS_CODE_TEMPORARY_REDIRECT = 307;
	public static final int HTTP_STATUS_CODE_BAD_REQUEST = 400;
	public static final int HTTP_STATUS_CODE_UNAUTHORIZED = 401;
	public static final int HTTP_STATUS_CODE_PAYMENT_REQUIRED = 402;
	public static final int HTTP_STATUS_CODE_FORBIDDEN = 403;
	public static final int HTTP_STATUS_CODE_NOT_FOUND = 404;
	public static final int HTTP_STATUS_CODE_METHOD_NOT_ALLOWED = 405;
	public static final int HTTP_STATUS_CODE_NOT_ACCEPTABLE = 406;
	public static final int HTTP_STATUS_CODE_PROXY_AUTHENTICATION_REQUIRED = 407;
	public static final int HTTP_STATUS_CODE_REQUEST_TIMEOUT = 408;
	public static final int HTTP_STATUS_CODE_CONFLICT = 409;
	public static final int HTTP_STATUS_CODE_GONE = 410;
	public static final int HTTP_STATUS_CODE_LENGTH_REQUIRED = 411;
	public static final int HTTP_STATUS_CODE_PRECONDITION_FAILED = 412;
	public static final int HTTP_STATUS_CODE_REQUEST_TOO_LONG = 413;
	public static final int HTTP_STATUS_CODE_REQUEST_URI_TOO_LONG = 414;
	public static final int HTTP_STATUS_CODE_UNSUPPORTED_MEDIA_TYPE = 415;
	public static final int HTTP_STATUS_CODE_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
	public static final int HTTP_STATUS_CODE_EXPECTATION_FAILED = 417;
	public static final int HTTP_STATUS_CODE_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
	public static final int HTTP_STATUS_CODE_METHOD_FAILURE = 420;
	public static final int HTTP_STATUS_CODE_UNPROCESSABLE_ENTITY = 422;
	public static final int HTTP_STATUS_CODE_LOCKED = 423;
	public static final int HTTP_STATUS_CODE_FAILED_DEPENDENCY = 424;
	public static final int HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
	public static final int HTTP_STATUS_CODE_NOT_IMPLEMENTED = 501;
	public static final int HTTP_STATUS_CODE_BAD_GATEWAY = 502;
	public static final int HTTP_STATUS_CODE_SERVICE_UNAVAILABLE = 503;
	public static final int HTTP_STATUS_CODE_GATEWAY_TIMEOUT = 504;
	public static final int HTTP_STATUS_CODE_HTTP_VERSION_NOT_SUPPORTED = 505;
	public static final int HTTP_STATUS_CODE_INSUFFICIENT_STORAGE = 507;

	public static final String PATH_CREPO = "crepo";
	public static final String AA_SERVER_REPOSITORY_URI = PROTOCOL + "://" + AA_SERVER + "/" + PATH_CREPO;
	public static final String LUG_SERVER_REPOSITORY_URI = PROTOCOL + "://" + LUG_SERVER + "/" + PATH_CREPO;
	public static final String MODULE_USER = "/user";
	public static final String MODULE_SYSTEM = "/system";
	public static final String MODULE_COMPUTER = "/computer";
	public static final String MODULE_DIRECTORY = "/directory";

	public static final String EXCHANGE_ACCESS_TOKEN_WITH_AUTH_CODE_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/tokenac";
	public static final String LOGIN_WITH_AUTHORIZATION_CODE_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/loginac";
	public static final String LOGIN_WITH_SESSION_ID_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/loginse";
	public static final String CREATE_OR_UPDATE_USER_PROFILE_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/uprofile";
	public static final String CONNECT_TO_COMPUTER_URI = AA_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/connect-to-computer";
	public static final String CHANGE_NICKNAME_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/nickname";
	public static final String SEND_CHANGE_USER_EMAIL_SECURITY_CODE_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/change-email-code";
	public static final String CHANGE_EMAIL_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/change-email";
	public static final String CHECK_USER_DELETABLE_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/check-deletable";
	public static final String DELETE_USER_2_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/delete2";
	public static final String CREATE_OR_UPDATE_DEVICE_TOKEN_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/device-token";
	public static final String REQUEST_CONNECTION_URI = AA_SERVER_REPOSITORY_URI + MODULE_USER + "/reconnect";
	public static final String PING_DESKTOP_URI = AA_SERVER_REPOSITORY_URI + MODULE_SYSTEM + "/dping";
	public static final String FIND_AVAILABLE_COMPUTERS_3_URI = AA_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/available3";
	public static final String CHANGE_USER_COMPUTER_PROFILES_URI = AA_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/ucprofiles";
	public static final String CREATE_COMPUTER_WITH_QR_CODE_URI = AA_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/create-with-qrcode";
	public static final String FIND_ALL_FILE_DOWNLOADED_URI = AA_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/dhis";
	public static final String FIND_ALL_FILE_UPLOADED_URI = AA_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/uhis";

//	public static final String LUG_FIND_ALL_ROOT_DIRECTORIES_URI = LUG_SERVER_REPOSITORY_URI + "/rootDirectories";
	public static final String LUG_LIST_ROOTS_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/roots";
	public static final String LUG_LIST_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/list";
//	public static final String LUG_FIND_BY_PATH_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/find";
	public static final String LUG_DOWNLOAD_FILE_3_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/ddownload3";
	public static final String LUG_CREATE_FILE_DOWNLOAD_SUMMARY_2_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/ddownload-sum2";
	public static final String LUG_UPLOAD_FILE_4_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/dupload4";
	public static final String LUG_CONFIRM_UPLOAD_FILE_2_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/dcupload2";
	public static final String LUG_CONFIRM_DOWNLOAD_FILE_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/dcdownload";
	public static final String LUG_CREATE_FILE_UPLOAD_SUMMARY_2_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/dupload-sum2";
	public static final String LUG_REPLACE_FILE_UPLOAD_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/replace-upload";
	public static final String LUG_FIND_FILE_UPLOADED_BY_TRANSFER_KEY_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/find-dupload";
	public static final String LUG_CANCEL_DOWNLOAD_FILE_FROM_DEVICE_URI = LUG_SERVER_REPOSITORY_URI + MODULE_DIRECTORY + "/ddcancel";
	public static final String LUG_DELETE_COMPUTER_FROM_DEVICE_URI = LUG_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/delete2";
	public static final String AA_DELETE_COMPUTER_FROM_DEVICE_URI = AA_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/delete2";
	public static final String LUG_CHANGE_COMPUTER_NAME_URI = LUG_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/name";
	public static final String AA_CHANGE_COMPUTER_NAME_URI = AA_SERVER_REPOSITORY_URI + MODULE_COMPUTER + "/name";

	// Account Constants
	public static final String ACCOUNT_TYPE_FILELUG = "com.filelug";
	public final static String AUTH_REQUIRED_FEATURES = "authRequiredFeatures";
	public final static String AUTH_OPTIONS = "authOptions";
	public final static String AUTH_ACTION_TYPE = "authActionType";
	public final static String AUTH_ACTION_ADD_ACCOUNT = "addAcount";
	public final static String AUTH_ACTION_LOGIN_WHEN_403 = "loginWhen403";
	public final static String AUTH_ACTION_VERIFY_PHONE_NUMBER = "verifyPhoneNumber";
	public static final String AUTH_TOKEN_TYPE = "authTokenType";
	public static final String AUTH_TOKEN_TYPE_GENERAL_SERVICE = "com.filelug.token.general";
	public static final String AUTH_TOKEN_TYPE_GENERAL_SERVICE_LABEL = "Access to Filelug general service";
	public static final String OLD_USER_ACCOUNT_PATTERN = "%2$s (%1$s)";
	public static final String USER_ACCOUNT_PATTERN = "+%1$s %2$s";

	// Repository Constants
	public static final String ENCRYPTED_USER_COMPUTER_ID_PREFIX = "@";
	public static final String ENCRYPTED_USER_COMPUTER_ID_SUFFIX = "#";
	public static final String COMPUTER_DELIMITERS = "|";

	// Repository Parameter Constants
	public static final String PARAM_COUNTRY_ID = "country-id";
	public static final String PARAM_COUNTRY_CODE = "country-code";
	public static final String PARAM_PHONE = "phone";
	public static final String PARAM_PHONE_WITH_COUNTRY = "phone-with-country";
	public static final String PARAM_NICKNAME = "nickname";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_EMAIL_IS_VERIFIED = "email-is-verified";
	public static final String PARAM_VERIFICATION = "verification";
	public static final String PARAM_LOCALE = "locale";
	public static final String PARAM_ACCOUNT = "account";
	public static final String PARAM_CODE = "code";
	public static final String PARAM_COMPUTER_ID = "computer-id";
	public static final String PARAM_COMPUTER_ADMIN_ID = "computer-admin-id";
	public static final String PARAM_COMPUTER_NAME = "computer-name";
	public static final String PARAM_COMPUTER_GROUP = "computer-group";
	public static final String PARAM_SHOW_HIDDEN = "showHidden";
	public static final String PARAM_DEVICE_VERSION = "device.version";
	public static final String PARAM_DEVICE_BUILD = "device.build";
	public static final String PARAM_SESSION_ID = "sessionId";
	public static final String PARAM_OLD_SESSION_ID = "oldSessionId";
	public static final String PARAM_NEW_SESSION_ID = "newSessionId";
	public static final String PARAM_SOCKET_CONNECTED = "socket-connected";
	public static final String PARAM_LUG_SERVER_ID = "lug-server-id";
	public static final String PARAM_UPLOAD_DIRECTORY = "upload-directory";
	public static final String PARAM_UPLOAD_SUB_DIRECTORY_TYPE = "upload-subdirectory-type";
	public static final String PARAM_UPLOAD_SUB_DIRECTORY_VALUE = "upload-subdirectory-value";
	public static final String PARAM_UPLOAD_DESCRIPTION_TYPE = "upload-description-type";
	public static final String PARAM_UPLOAD_DESCRIPTION_VALUE = "upload-description-value";
	public static final String PARAM_UPLOAD_NOTIFICATION_TYPE = "upload-notification-type";
	public static final String PARAM_DOWNLOAD_DIRECTORY = "download-directory";
	public static final String PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE = "download-subdirectory-type";
	public static final String PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE = "download-subdirectory-value";
	public static final String PARAM_DOWNLOAD_DESCRIPTION_TYPE = "download-description-type";
	public static final String PARAM_DOWNLOAD_DESCRIPTION_VALUE = "download-description-value";
	public static final String PARAM_DOWNLOAD_NOTIFICATION_TYPE = "download-notification-type";
	public static final String PARAM_USER_COMPUTER_ID = "user-computer-id";
	public static final String PARAM_USER_ID = "user-id";
	public static final String PARAM_NEW_NICKNAME = "new-nickname";
	public static final String PARAM_NEW_EMAIL = "new-email";
	public static final String PARAM_DESKTOP_LOCALE = "desktop.locale";
	public static final String PARAM_DESKTOP_VERSION = "desktop.version";
	public static final String PARAM_FILE_ENCODING = "file.encoding";
	public static final String PARAM_FILE_SEPARATOR = "file.separator";
	public static final String PARAM_PATH_SEPARATOR = "path.separator";
	public static final String PARAM_LINE_SEPARATOR = "line.separator";
	public static final String PARAM_OS_NAME = "os.name";
	public static final String PARAM_OS_VERSION = "os.version";
	public static final String PARAM_OS_ARCH = "os.arch";
	public static final String PARAM_ID = "id";
	public static final String PARAM_LABEL = "label";
	public static final String PARAM_PATH = "path";
	public static final String PARAM_DISK = "disk";
	public static final String PARAM_REAL_PATH = "realPath";
	public static final String PARAM_TYPE = "type";
	public static final String PARAM_FILE_SIZE = "fileSize";
	public static final String PARAM_END_TIMESTAMP = "endTimestamp";
	public static final String PARAM_FILE_NAME = "filename";
	public static final String PARAM_SYMLINK = "symlink";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_PARENT = "parent";
	public static final String PARAM_READABLE = "readable";
	public static final String PARAM_WRITABLE = "writable";
	public static final String PARAM_HIDDEN = "hidden";
	public static final String PARAM_LAST_MODIFIED = "lastModified";
	public static final String PARAM_CONTENT_TYPE = "contentType";
	public static final String PARAM_SIZE_IN_BYTES = "sizeInBytes";
	public static final String PARAM_REAL_PARENT = "realParent";
	public static final String PARAM_REAL_NAME = "realName";
	public static final String PARAM_CALCULATE_SIZE = "calculateSize";
	public static final String PARAM_TRANSFER_KEY = "transferKey";
	public static final String PARAM_T = "t";
	public static final String PARAM_UP_KEY = "upkey";
	public static final String PARAM_UP_DIR = "updir";
	public static final String PARAM_UP_NAME = "upname";
	public static final String PARAM_UP_SIZE = "upsize";
	public static final String PARAM_FILE_LAST_MODIFIED = "File-Last-Modified";
	public static final String PARAM_FILE_RANGE = "File-Range";
	public static final String PARAM_UPLOADED_BUT_UNCOMFIRMED = "uploaded_but_uncomfirmed";
	public static final String PARAM_STATUS = "status";
	public static final String PARAM_DOWNLOAD_SIZE_LIMIT = "download-size-limit";
	public static final String PARAM_UPLOAD_SIZE_LIMIT = "upload-size-limit";
	public static final String PARAM_SESSIONS = "sessions";
	public static final String PARAM_DEVICE_TOKEN = "device-token";
	public static final String PARAM_NOTIFICATION_TYPE = "notification-type";
	public static final String PARAM_DEVICE_TYPE = "device-type";
	public static final String PARAM_DEVICE_VERSION_2 = "device-version";
	public static final String PARAM_FILELUG_VERSION = "filelug-version";
	public static final String PARAM_FILELUG_BUILD = "filelug-build";
	public static final String PARAM_UPLOAD_GROUP_ID = "upload-group-id";
	public static final String PARAM_UPLOAD_KEYS = "upload-keys";
	public static final String PARAM_UPLOAD_DIR = "upload-dir";
	public static final String PARAM_DOWNLOAD_GROUP_ID = "download-group-id";
	public static final String PARAM_DOWNLOAD_KEY_PATHS = "download-key-paths";
	public static final String PARAM_DOWNLOAD_DIR = "download-dir";
	public static final String PARAM_SUB_DIR_TYPE = "subdirectory-type";
	public static final String PARAM_SUB_DIR_VALUE = "subdirectory-value";
	public static final String PARAM_DESCRIPTION_TYPE = "description-type";
	public static final String PARAM_DESCRIPTION_VALUE = "description-value";
	public static final String PARAM_TRANSFER_STATUS = "transfer-status";
	public static final String PARAM_TRANSFER_KEY_2 = "transfer-key";
	public static final String PARAM_TITLE = "title";
	public static final String PARAM_BODY = "body";
	public static final String PARAM_FL_TYPE = "fl-type";
	public static final String PARAM_OLD_TRANSFER_KEY = "old-transferKey";
	public static final String PARAM_NEW_TRANSFER_KEY = "new-transferKey";
	public static final String PARAM_TRANSFERRED_SIZE = "transferredSize";
	public static final String PARAM_FILE_LAST_MODIFIED_DATE = "fileLastModifiedDate";
	public static final String PARAM_QR_CODE = "qr-code";
	public static final String PARAM_NEED_CREATE_OR_UPDATE_USER_PROFILE = "need-create-or-update-user-profile";
	public static final String PARAM_NEW_COMPUTER_GROUP = "new-computer-group";
	public static final String PARAM_NEW_COMPUTER_NAME = "new-computer-name";

	public static final String EXT_PARAM_REGISTERED = "registered";
	public static final String EXT_PARAM_FILELUG_ACCOUNT = "filelug-account";
	public static final String EXT_PARAM_ACCESS_TIME = "access-time";
	public static final String EXT_PARAM_LOGGED_IN = "logged-in";
	public static final String EXT_PARAM_OLD_COMPUTER_ID = "old-computer-id";
	public static final String EXT_PARAM_OLD_COMPUTER_NAME = "old-computer-name";
	public static final String EXT_PARAM_NEW_COMPUTER_ID = "new-computer-id";
	public static final String EXT_PARAM_NEW_COMPUTER_NAME = "new-computer-name";
	public static final String EXT_PARAM_OLD_ACCOUNT = "old-account";
	public static final String EXT_PARAM_NEW_ACCOUNT = "new-account";
	public static final String EXT_PARAM_SELECTED_UPLOAD_FILES = "selected-upload-files";
	public static final String EXT_PARAM_SELECTED_DOWNLOAD_FILES = "selected-download-files";
	public static final String EXT_PARAM_HISTORY_TYPE = "history-type";
	public static final String EXT_PARAM_CURRENT_FOLDER = "current-folder";
	public static final String EXT_PARAM_TRANSFER_TYPE = "transfer-type";
	public static final String EXT_PARAM_SYSTEM_CHANGE_DEVICE_TOKEN = "system-change-device-token";
	public static final String EXT_PARAM_CHANGED_PREFERENCES = "changed-preferences";
	public static final String EXT_PARAM_NOTIFICATION_CALLBACK_TYPE = "notification-callback-type";
	public static final String EXT_PARAM_ROW_ID = "row-id";
	public static final String EXT_PARAM_NOTIFICATION_ID = "notification-id";
	public static final String EXT_PARAM_ERROR_CODE = "error-code";
	public static final String EXT_PARAM_ERROR_MESSAGE = "error-message";
	public static final String EXT_PARAM_REQUEST_FROM_ACTIVITY = "request-from-activity";
	public static final String EXT_PARAM_ORIGIN_EXTRA = "origin-extra";
	public static final String EXT_PARAM_INTENT_ACTION = "intent-action";
	public static final String EXT_PARAM_ACCEPT_TYPE = "accept-type";
	public static final String EXT_PARAM_OPENABLE = "openable";
	public static final String EXT_PARAM_ALLOW_MULTIPLE = "allow-multiple";
	public static final String EXT_PARAM_LOCAL_ONLY = "local-only";
	public static final String EXT_PARAM_OFF_RESULT_URIS = "off-result-uris";
	public static final String EXT_PARAM_OFF_RESULT_MIME_TYPES = "off-result-mime-types";
	public static final String EXT_PARAM_OFF_RESULT_ERRORS = "off-result-errors";
	public static final String EXT_PARAM_FILE_IN_CACHE = "file-in-cache";
	public static final String EXT_PARAM_REMOTE_FILE_OBJECT = "remote-file-object";
	public static final String EXT_PARAM_IS_SAVE_AS_DEFAULT = "is-save-as-default";
	public static final String EXT_PARAM_NUMBER_OF_DOWNLOAD_FILES = "number-of-download-files";
	public static final String EXT_PARAM_FORCE_RELOGIN = "force-login";
	public static final String EXT_PARAM_ORIGIN_EMAIL = "origin-email";
	public static final String EXT_PARAM_ORIGIN_EMAIL_VERIFY_STATUS = "origin-email-verify-status";

	public static final String LOCAL_BROADCAST_REGISTRATION_COMPLETE = "local-broadcast-registration-complete";
	public static final String LOCAL_BROADCAST_DESKTOP_CONNECTION_STATUS = "local-broadcast-desktop-connection-status";
	public static final String LOCAL_BROADCAST_CACHE_SHARED_STATUS = "local-broadcast-cache-shared-status";
//	public static final String LOCAL_BROADCAST_ACTION_CONNECTION_STATUS_CHANGED = "local-broadcast-action-connection-status-changed";
//	public static final String LOCAL_BROADCAST_ACTION_NEED_TO_REQUEST_CONNECT = "local-broadcast-action-need-to-request-connect";

	public static final String ENV_PARAM_SD_CARD_STORAGE = "/storage/ext_sd";
	public static final String ENV_PARAM_USB_STORAGE = "/storage/usb";

	public static final String ENV_PARAM_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
	public static final String ENV_PARAM_SECONDARY_STORAGE = "SECONDARY_STORAGE";
	public static final String ENV_PARAM_EXTERNAL_ADD_STORAGE = "EXTERNAL_ADD_STORAGE";
	public static final String ENV_PARAM_EXTERNAL_ADD_USB_STORAGE = "EXTERNAL_ADD_USB_STORAGE";
	public static final String ENV_PARAM_SECOND_VOLUME_STORAGE = "SECOND_VOLUME_STORAGE";
	public static final String ENV_PARAM_THIRD_VOLUME_STORAGE = "THIRD_VOLUME_STORAGE";
	public static final String ENV_PARAM_USB_OTG_STORAGE = "USBOTG_STORAGE";

	// Activity Request Code
	public static final int REQUEST_SETTINGS = 1;
	public static final int REQUEST_INITIAL = 2;
	public static final int REQUEST_CHANGE_NICKNAME = 3;
	public static final int REQUEST_CHANGE_EMAIL = 4;
	public static final int REQUEST_VERIFY_EMAIL_SECURITY_CODE = 5;
	public static final int REQUEST_CHANGE_COMPUTER = 6;
	public static final int REQUEST_DELETE_COMPUTER = 7;
	public static final int REQUEST_CHANGE_COMPUTER_NAME = 8;
	public static final int REQUEST_UPLOAD_FILES = 9;
	public static final int REQUEST_DOWNLOAD_FILES = 10;
	public static final int REQUEST_GETTING_STARTED = 11;
	public static final int REQUEST_CHANGE_ACCOUNT = 12;
	public static final int REQUEST_ADD_NEW_COMPUTER = 13;
	public static final int REQUEST_PERMISSION_CAMERA = 14;
	public static final int REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_ADD_ACCOUNT = 15;
	public static final int REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_DELETE_ACCOUNT = 16;
	public static final int REQUEST_PERMISSION_ACCOUNTS_AND_STORAGE = 17;
	public static final int REQUEST_ACCOUNT_KIT_LOGIN = 18;
	public static final int REQUEST_ACCOUNT_KIT_VERIFY_PHONE_NUMBER = 19;
	public static final int REQUEST_MANAGE_CURRENT_ACCOUNT = 20;
	public static final int REQUEST_MANAGE_CURRENT_COMPUTER = 21;

	// Http request header
	public static final String REQ_HEADER_ACCEPT = "Accept";
	public static final String REQ_HEADER_ACCEPT_CHARSET = "Accept-Charset";
	public static final String REQ_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String REQ_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String REQ_HEADER_AUTHORIZATION = "Authorization";

	// Http request header value
	public static final String APPLICATION_JSON = "application/json";
	public static final String GZIP_DEFLATE = "gzip, deflate";
	public static final String UTF8 = "utf-8";

	public final String INVALID_FILE_NAME_CHARACTERS = "\\\\/:*?\"<>|";

	public static final int NOTIFICATION_CALLBACK_TYPE_UPLOAD_PING_ERROR = 1;
	public static final int NOTIFICATION_CALLBACK_TYPE_UPLOAD_COMPLETED = 2;
	public static final int NOTIFICATION_CALLBACK_TYPE_UPLOAD_TRANSFER_ERROR = 3;
	public static final int NOTIFICATION_CALLBACK_TYPE_UPLOAD_RESPONSE_ERROR = 4;
	public static final int NOTIFICATION_CALLBACK_TYPE_UPLOAD_CANCELED = 5;
	public static final int NOTIFICATION_CALLBACK_TYPE_GCM_UPLOAD_FILES = 6;
	public static final int NOTIFICATION_CALLBACK_TYPE_GCM_UPLOAD_FILE = 7;
	public static final int NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE = 8;
	public static final int NOTIFICATION_CALLBACK_TYPE_STOP_UPLOAD_FILE = 9;
	public static final int NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_PING_ERROR = 101;
	public static final int NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_COMPLETED = 102;
	public static final int NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_SUCCESS = 103;
	public static final int NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_TRANSFER_ERROR = 104;
	public static final int NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_RESPONSE_ERROR = 105;
	public static final int NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_CANCELED = 106;
	public static final int NOTIFICATION_CALLBACK_TYPE_GCM_DOWNLOAD_FILES = 107;
	public static final int NOTIFICATION_CALLBACK_TYPE_GCM_DOWNLOAD_FILE = 108;
	public static final int NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE = 109;
	public static final int NOTIFICATION_CALLBACK_TYPE_OPEN_DOWNLOADED_FILE = 110;
	public static final int NOTIFICATION_CALLBACK_TYPE_STOP_DOWNLOAD_FILE = 111;

	public static final int TRANSFER_TYPE_DOWNLOAD = 0;
	public static final int TRANSFER_TYPE_UPLOAD = 1;
	public static final int TRANSFER_TYPE_OPEN_FROM = 2;

	public static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_USER_AGENT = "User-Agent";
	public static final String HTTP_HEADER_CONNECTION = "Connection";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
	public static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HTTP_HEADER_IF_RANGE = "If-Range";
	public static final String HTTP_HEADER_RANGE = "Range";
	public static final String HTTP_HEADER_LAST_MODIFIED = "Last-Modified";
	public static final String HTTP_HEADER_CONTENT_RANGE = "Content-Range";
	public static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HTTP_HEADER_ACCEPT_RANGES = "Accept-Ranges";

	public static final String DOWNLOAD_FILENAME_SUFFIX = ".fldownload";

	// 登入、連線電腦
	public static final int MESSAGE_ACCOUNT_NOT_SET = 1;
	public static final int MESSAGE_LOGIN_FAILED = 2;
	public static final int MESSAGE_LOGGED_IN_AND_SOCKET_CONNECTED = 3;
	public static final int MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_CONNECTED = 4;
	public static final int MESSAGE_LOGGED_IN_BUT_CONNECTION_FAILED = 5;
	public static final int MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_SET = 6;
	public static final int MESSAGE_FIND_AVAILABLE_COMPUTERS_GET_AUTH_TOKEN_ERROR = 7;
	public static final int MESSAGE_LOGGED_IN_BUT_COMPUTER_LIST_NOT_FOUND = 8;
	// 變更連線電腦
	public static final int MESSAGE_CHANGE_COMPUTER_GET_AUTH_TOKEN_ERROR = 9;
	public static final int MESSAGE_COMPUTER_CHANGED_AND_SOCKET_CONNECTED = 10;
	public static final int MESSAGE_COMPUTER_CHANGED_BUT_NOT_CONNECTED = 11;
	public static final int MESSAGE_COMPUTER_CHANGE_ERROR = 12;
	// 變更？？名稱
	public static final int MESSAGE_COMPUTER_NAME_CHANGED = 13;
	public static final int MESSAGE_EMAIL_CHANGED = 14;
	public static final int MESSAGE_NICKNAME_CHANGED = 15;
	// 刪除？？
	public static final int MESSAGE_ACCOUNT_DELETED = 16;
	public static final int MESSAGE_COMPUTER_DELETED = 17;
	// 其他
	public static final int MESSAGE_SELECT_ITEM_CHANGED = 18;
	public static final int MESSAGE_ACCOUNT_HAS_BEEN_DELETED = 19;
	public static final int MESSAGE_FILELUG_SERVICE_DESTROY = 20;
	public static final int MESSAGE_PING_DESKTOP_SUCCESS = 21;
	public static final int MESSAGE_PING_DESKTOP_ERROR = 22;
	public static final int MESSAGE_REQUEST_CONNECTION_SUCCESS = 23;
	public static final int MESSAGE_REQUEST_CONNECTION_ERROR = 24;
	public static final int MESSAGE_REQUEST_CONNECTION_GET_AUTH_TOKEN_ERROR = 25;
	public static final int MESSAGE_RESPONSE_403_SESSION_ID_NOT_EXIST = 26;
	public static final int MESSAGE_RESPONSE_501_COMPUTER_NOT_EXIST = 27;

}
