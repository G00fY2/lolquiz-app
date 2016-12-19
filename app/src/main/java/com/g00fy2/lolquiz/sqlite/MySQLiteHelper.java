package com.g00fy2.lolquiz.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // TODO: Non Mana Champions
    public static final String TABLE_CHAMPIONS = "champions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String QUIZCOLUMN_ARMOR = "armor";
    public static final String QUIZCOLUMN_ARMORPERLEVEL = "armorperlevel";
    public static final String QUIZCOLUMN_ATTACKDAMAGE = "attackdamage";
    public static final String QUIZCOLUMN_ATTACKDAMAGEPERLEVEL = "attackdamageperlevel";
    public static final String QUIZCOLUMN_ATTACKRANGE = "attackrange";
    public static final String QUIZCOLUMN_ATTACKSPEEDOFFSET = "attackspeedoffset";
    public static final String QUIZCOLUMN_ATTACKSPEEDPERLEVEL = "attackspeedperlevel";
    public static final String QUIZCOLUMN_CRIT = "crit";
    public static final String QUIZCOLUMN_CRITPERLEVEL = "critperlevel";
    public static final String QUIZCOLUMN_HP = "hp";
    public static final String QUIZCOLUMN_HPPERLEVEL = "hpperlevel";
    public static final String QUIZCOLUMN_HPREGEN = "hpregen";
    public static final String QUIZCOLUMN_HPREGENPERLEVEL = "hpregenperlevel";
    public static final String QUIZCOLUMN_MOVESPEED = "movespeed";
    public static final String QUIZCOLUMN_MP = "mp";
    public static final String QUIZCOLUMN_MPPERLEVEL = "mpperlevel";
    public static final String QUIZCOLUMN_MPREGEN = "mpregen";
    public static final String QUIZCOLUMN_MPREGENPERLEVEL = "mpregenperlevel";
    public static final String QUIZCOLUMN_SPELLBLOCK = "spellblock";
    public static final String QUIZCOLUMN_SPELLBLOCKPERLEVEL = "spellblockperlevel";
    public static final String COLUMN_IMAGEURL = "imageurl";
    
    

    private static final String DATABASE_NAME = "champions.db";
    private static final int DATABASE_VERSION = 1;

    // SQL Statement um Datenbank zu erstellen
    private static final String DATABASE_CREATE =
            "create table " + TABLE_CHAMPIONS
                    + "("
                    + COLUMN_ID + " integer primary key, "
                    + COLUMN_NAME + " text not null, "
                    + QUIZCOLUMN_ARMOR + " real default 0.0, "
                    + QUIZCOLUMN_ARMORPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_ATTACKDAMAGE + " real default 0.0, "
                    + QUIZCOLUMN_ATTACKDAMAGEPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_ATTACKRANGE + " real default 0.0, "
                    + QUIZCOLUMN_ATTACKSPEEDOFFSET + " real default 0.0, "
                    + QUIZCOLUMN_ATTACKSPEEDPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_CRIT + " real default 0.0, "
                    + QUIZCOLUMN_CRITPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_HP + " real default 0.0, "
                    + QUIZCOLUMN_HPPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_HPREGEN + " real default 0.0, "
                    + QUIZCOLUMN_HPREGENPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_MOVESPEED + " real default 0.0, "
                    + QUIZCOLUMN_MP + " real default 0.0, "
                    + QUIZCOLUMN_MPPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_MPREGEN + " real default 0.0, "
                    + QUIZCOLUMN_MPREGENPERLEVEL + " real default 0.0, "
                    + QUIZCOLUMN_SPELLBLOCK + " real default 0.0, "
                    + QUIZCOLUMN_SPELLBLOCKPERLEVEL + " real default 0.0, "
                    + COLUMN_IMAGEURL + " text default '' "
                    + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAMPIONS);
        onCreate(db);
    }
}
