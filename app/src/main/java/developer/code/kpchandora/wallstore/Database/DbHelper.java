package developer.code.kpchandora.wallstore.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kpchandora on 1/2/18.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "categories.db";
    private static final int DB_VERSION = 3;

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

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_FRONT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL(UPDATE_CATEGORY_TABLE);
        db.execSQL(UPDATE_FRONT_TABLE);

        onCreate(db);
    }
}
