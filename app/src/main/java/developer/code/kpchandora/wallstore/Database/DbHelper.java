package developer.code.kpchandora.wallstore.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static developer.code.kpchandora.wallstore.Database.CategoryContract.*;

/**
 * Created by kpchandora on 1/2/18.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "categories.db";
    private static final int DB_VERSION = 5;

    private static final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + CategoryContract.CATEGORY_TABLE + "( " +
                    CategoryContract.CATEGORY_ID + " TEXT PRIMARY KEY, " +
                    CategoryContract.CATEGORY_TITLE + " TEXT NOT NULL, " +
                    CategoryContract.CATEGORY_IMAGE_URL + " TEXT NOT NULL," +
                    CategoryContract.CATEGORY_FLAG + " INTEGER DEFAULT 0);";

    private static final String UPDATE_CATEGORY_TABLE =
            "DROP TABLE IF EXISTS " + CategoryContract.CATEGORY_TABLE;

    private static final String CREATE_FRONT_TABLE = "CREATE TABLE " + CategoryContract.FRONT_TABLE + "( " +
            CategoryContract.FRONT_ID + " TEXT PRIMARY KEY, " +
            CategoryContract.FRONT_TITLE + " TEXT NOT NULL, " +
            CategoryContract.FRONT_IMAGE_URL + " TEXT NOT NULL);";

    private static final String UPDATE_FRONT_TABLE =
            "DROP TABLE IF EXISTS " + CategoryContract.FRONT_TABLE;

    private static final String CREATE_DOWNLOAD_TABLE = "CREATE TABLE " + CategoryContract.DOWNLOAD_TABLE + " ( " +
            CategoryContract.DOWNLOAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CategoryContract.DOWNLOAD_URI + " TEXT NOT NULL);";

    private static final String UPDATE_DOWNLOAD_TABLE =
            "DROP TABLE IF EXISTS " + CategoryContract.DOWNLOAD_TABLE;


    private static final String CREATE_TABLE_NOTIFICATION =
            "CREATE TABLE " + TABLE_NOTIFICATION + " ( " +
                    NOTIFICATION_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTIFICATION_TITLE_COLUMN + " TEXT, " +
                    NOTIFICATION_CONTENT_COLUMN + " TEXT, " +
                    IMAGE_URL_COLUMN + " TEXT, " +
                    NOTIFICATION_TIME_STAMP + " LONG, " +
                    NOTIFICATION_EXP_TIME + " LONG, " +
                    NOTIFICATION_TOTAL_TIME + " LONG, " +
                    NOTIFICATION_FLAG + " INTEGER DEFAULT 0, " +
                    NOTIFICATION_COUNT + " INTEGER DEFAULT 0);";

    private static final String DELETE_TABLE_NOTIFICATION =
            "DROP TABLE IF EXISTS " + TABLE_NOTIFICATION;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_FRONT_TABLE);
        db.execSQL(CREATE_DOWNLOAD_TABLE);
        db.execSQL(CREATE_TABLE_NOTIFICATION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL(UPDATE_CATEGORY_TABLE);
        db.execSQL(UPDATE_FRONT_TABLE);
        db.execSQL(UPDATE_DOWNLOAD_TABLE);
        db.execSQL(DELETE_TABLE_NOTIFICATION);

        onCreate(db);
    }
}
