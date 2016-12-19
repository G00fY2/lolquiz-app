package com.g00fy2.lolquiz.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.riotapi.staticdata.ChampionDto;
import com.g00fy2.lolquiz.riotapi.staticdata.ChampionListDto;
import com.g00fy2.lolquiz.sqlite.QueryArgsGenerator.QueryArgs;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.g00fy2.lolquiz.sqlite.MySQLiteHelper.COLUMN_ID;
import static com.g00fy2.lolquiz.sqlite.MySQLiteHelper.COLUMN_IMAGEURL;
import static com.g00fy2.lolquiz.sqlite.MySQLiteHelper.COLUMN_NAME;
import static com.g00fy2.lolquiz.sqlite.MySQLiteHelper.TABLE_CHAMPIONS;

public class ChampionsDataSource {
    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;
    private ArrayList<QueryArgs> queryArgs;

    public ChampionsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void openWriteable() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void openReadable() throws SQLException {
        db = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Integer insertChampionsTransaction(ChampionListDto championList) throws SQLException {
        int count = 0;
        db.execSQL("delete from " + TABLE_CHAMPIONS); //TODO: Check if data already exist
        db.beginTransaction();
        if (championList.data != null && !championList.data.isEmpty()) {
            try {
                for (Map.Entry<String, ChampionDto> entry : championList.data.entrySet()) {
                    ContentValues values = new ContentValues();
                    values.put(MySQLiteHelper.COLUMN_ID, entry.getKey());
                    values.put(MySQLiteHelper.COLUMN_NAME, entry.getValue().name);
                    values.put(MySQLiteHelper.QUIZCOLUMN_ARMOR, entry.getValue().stats.armor);
                    values.put(MySQLiteHelper.QUIZCOLUMN_ARMORPERLEVEL, entry.getValue().stats.armorperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_ATTACKDAMAGE, entry.getValue().stats.attackdamage);
                    values.put(MySQLiteHelper.QUIZCOLUMN_ATTACKDAMAGEPERLEVEL, entry.getValue().stats.attackdamageperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_ATTACKRANGE, entry.getValue().stats.attackrange);
                    values.put(MySQLiteHelper.QUIZCOLUMN_ATTACKSPEEDOFFSET, entry.getValue().stats.attackspeedoffset);
                    values.put(MySQLiteHelper.QUIZCOLUMN_ATTACKSPEEDPERLEVEL, entry.getValue().stats.attackspeedperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_CRIT, entry.getValue().stats.crit);
                    values.put(MySQLiteHelper.QUIZCOLUMN_CRITPERLEVEL, entry.getValue().stats.critperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_HP, entry.getValue().stats.hp);
                    values.put(MySQLiteHelper.QUIZCOLUMN_HPPERLEVEL, entry.getValue().stats.hpperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_HPREGEN, entry.getValue().stats.hpregenperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_HPREGENPERLEVEL, entry.getValue().stats.hpregenperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_MOVESPEED, entry.getValue().stats.movespeed);
                    values.put(MySQLiteHelper.QUIZCOLUMN_MP, entry.getValue().stats.mp);
                    values.put(MySQLiteHelper.QUIZCOLUMN_MPPERLEVEL, entry.getValue().stats.mpperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_MPREGEN, entry.getValue().stats.mpregen);
                    values.put(MySQLiteHelper.QUIZCOLUMN_MPREGENPERLEVEL, entry.getValue().stats.mpregenperlevel);
                    values.put(MySQLiteHelper.QUIZCOLUMN_SPELLBLOCK, entry.getValue().stats.spellblock);
                    values.put(MySQLiteHelper.QUIZCOLUMN_SPELLBLOCKPERLEVEL, entry.getValue().stats.spellblockperlevel);
                    db.insertOrThrow(TABLE_CHAMPIONS, null, values);
                    count++;
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                throw e;
            } finally {
                db.endTransaction();
            }
        }
        return count;
    }

    public Integer updateChampionImgTransaction(ChampionListDto championList) throws SQLException {
        int count = 0;

        db.beginTransaction();
        if (championList.data != null && !championList.data.isEmpty()) {
            String apiUrl = "https://ddragon.leagueoflegends.com/cdn/" + championList.version + "/img/champion/";

            try {
                for (Map.Entry<String, ChampionDto> entry : championList.data.entrySet()) {
                    ContentValues values = new ContentValues();
                    String imgUrl = apiUrl + entry.getValue().image.full;
                    values.put(MySQLiteHelper.COLUMN_IMAGEURL, imgUrl);
                    String whereClause = MySQLiteHelper.COLUMN_ID + "=" + Integer.toString(entry.getValue().id);
                    db.update(TABLE_CHAMPIONS, values, whereClause, null);
                    count++;
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                throw e;
            } finally {
                db.endTransaction();
            }
        }
        return count;
    }

    public QuestionAnswerSet getRandomQASet() throws ApiException {
        if (this.queryArgs == null) {
            this.queryArgs = new QueryArgsGenerator().getQueryArgs();
        }
        // TODO: Non Mana Champions
        QuestionAnswerSet qaSet;
        if (queryArgs.size() > 0) {
            qaSet = new QuestionAnswerSet();
            QueryArgs args = queryArgs.get(new Random().nextInt(queryArgs.size()));
            qaSet.setCategory(args.column);
            qaSet.setMin(args.min);

            // build random sort subquery to not get same GROUP_CONCAT everytime
            // order by primary key otherwise
            StringBuilder subquery = new StringBuilder();
            subquery.append("(")
                    .append("SELECT ")
                    .append(args.column).append(",")
                    .append(COLUMN_NAME).append(",")
                    .append(COLUMN_ID).append(",")
                    .append(COLUMN_IMAGEURL)
                    .append(" FROM ")
                    .append(TABLE_CHAMPIONS)
                    .append(" ORDER BY ")
                    .append("RANDOM()")
                    .append(")");

            Cursor cursor = db.query(false,                         // distinct
                    subquery.toString(),                            // from
                    new String[]{args.column,                       // select
                            "GROUP_CONCAT(" + COLUMN_NAME + ",', ') AS RESULT_NAME",
                            "GROUP_CONCAT(" + COLUMN_ID + ") AS RESULT_ID",
                            COLUMN_IMAGEURL},
                    null,                                           // where
                    null,                                           // whereArgs
                    args.column,                                    // groupBy
                    null,                                           // having
                    args.column + (args.min ? " ASC" : " DESC"),    // orderBy
                    "1");                                           // limit

            int cursorCount = cursor.getCount();

            // only fill qaSet if cursor has data
            if (cursorCount > 0) {
                cursor.moveToFirst();
                qaSet.setQuestionValue(cursor.getDouble(cursor.getColumnIndex(args.column)));

                qaSet.addAnswer(cursor.getString(1),    // RESULT_NAME
                        cursor.getDouble(0),            // args.column
                        cursor.getString(3)             // IMAGEURL
                );

                String answerID = cursor.getString(cursor.getColumnIndex("RESULT_ID"));
                //get 3 Random false answers
                Cursor cursorFlaseAns = db.query(false,                 // distinct
                        subquery.toString(),                            // from
                        new String[]{args.column,                       // select
                                "GROUP_CONCAT(" + COLUMN_NAME + ",', ') AS RESULT_NAME",
                                "GROUP_CONCAT(" + COLUMN_ID + ") AS RESULT_ID",
                                COLUMN_IMAGEURL},
                        COLUMN_ID + " NOT IN (" + answerID + ") ",      // where
                        null,                                           // whereArgs
                        args.column,                                    // groupBy
                        null,                                           // having
                        null,                                           // orderBy (already random from subqery)
                        "3");                                           // limit

                int falseAnsCount = cursorFlaseAns.getCount();

                if (falseAnsCount == 3) {
                    cursorFlaseAns.moveToFirst();
                    for (int i = 0; i < 3; i++) {
                        qaSet.addAnswer(cursorFlaseAns.getString(1),    // RESULT_NAME
                                cursorFlaseAns.getDouble(0),            // args.column
                                cursorFlaseAns.getString(3)             // IMAGEURL
                        );
                        cursorFlaseAns.moveToNext();
                    }
                }
                cursorFlaseAns.close();
            }
            cursor.close();

            // remove this object from queryArgs to not use same question again
            queryArgs.remove(args);

            // if empty cursor or qaSet doesn't have 4 answers call method again to get new random answer
            if (cursorCount == 0 || qaSet.getCounter() < 4) {
                qaSet = getRandomQASet();
            }

        } else {
            throw new ApiException("Question list is empty.");
        }

        return qaSet;
    }
}
