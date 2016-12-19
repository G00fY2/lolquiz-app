package com.g00fy2.lolquiz.sqlite;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by thoma on 19.11.2016.
 */

public class QueryArgsGenerator {
    private ArrayList<QueryArgs> queryArgs = new ArrayList<>();

    public QueryArgsGenerator() {
        Field[] fields = MySQLiteHelper.class.getFields();

        for (Field f : fields) {
            try {
                if (f.getType().equals(String.class) && f.getName().startsWith("QUIZCOLUMN")) {
                    String columnName = (String) f.get(null);

                    QueryArgs newMinArgs = new QueryArgs();
                    newMinArgs.column = (columnName);
                    newMinArgs.min = true;
                    queryArgs.add(newMinArgs);

                    QueryArgs newMaxArgs = new QueryArgs();
                    newMaxArgs.column = (columnName);
                    newMaxArgs.min = false;
                    queryArgs.add(newMaxArgs);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<QueryArgs> getQueryArgs() {
        return queryArgs;
    }

    public class QueryArgs {
        public String column = "";
        public boolean min;
        public boolean used;
    }
}
