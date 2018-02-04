package developer.code.kpchandora.wallstore.Database;

import android.provider.BaseColumns;

/**
 * Created by kpchandora on 1/2/18.
 */

public final class CategoryContract {

    public static final String CATEGORY_TABLE = "category_table";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_TITLE = "category_title";
    public static final String CATEGORY_FLAG = "category_flag";
    public static final String CATEGORY_IMAGE_URL = "category_url";

    public static final String FRONT_TABLE = "front_table";
    public static final String FRONT_ID = "front_id";
    public static final String FRONT_TITLE = "front_title";
    public static final String FRONT_IMAGE_URL = "front_url";

    public static final String DOWNLOAD_TABLE = "download_table";
    public static final String DOWNLOAD_ID = "download_id";
    public static final String DOWNLOAD_URI = "download_uri";

    public static final String TABLE_NOTIFICATION = "store_notification";
    public static final String NOTIFICATION_ID_COLUMN = BaseColumns._ID;
    public static final String NOTIFICATION_TITLE_COLUMN = "notification_title";
    public static final String NOTIFICATION_CONTENT_COLUMN = "notification_content";
    public static final String IMAGE_URL_COLUMN = "notification_url";
    public static final String NOTIFICATION_TIME_STAMP = "notification_time";
    public static final String NOTIFICATION_EXP_TIME = "notification_exp_time";
    public static final String NOTIFICATION_TOTAL_TIME = "notification_total_time";
    public static final String NOTIFICATION_FLAG = "notification_flag";
    public static final String NOTIFICATION_COUNT = "notification_count";

}
