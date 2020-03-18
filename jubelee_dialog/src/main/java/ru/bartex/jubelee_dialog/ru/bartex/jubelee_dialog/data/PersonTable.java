package ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data;

import android.provider.BaseColumns;

/**
 * Created by Андрей on 11.08.2018.
 */
public class PersonTable {


    private PersonTable(){
        //пустой конструктор
    };

        public final static String TABLE_NAME = "persons";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_DAY = "day";
        public final static String COLUMN_MONTH = "month";
        public final static String COLUMN_YEAR = "year";
        public final static String COLUMN_DR = "dr";
        public final static String COLUMN_PAST_DAYS = "past_days";
        public final static String COLUMN_CHOOSE = "choose";

}
