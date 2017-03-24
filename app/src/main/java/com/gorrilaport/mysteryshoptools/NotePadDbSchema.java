package com.gorrilaport.mysteryshoptools;

/**
 * Created by Ricardo on 3/13/2016.
 */
public class NotePadDbSchema {
    public static final class NotesTable{
        public static final String NAME = "notes";

        public static final class Cols{
            public static final String NOTE_ID = "_id";
            public static final String TITLE = "title";
            public static final String TEXT = "text";
            public static final String LIST_PARENT = "list_parent";
            public static final String IS_CHECKED = "is_checked";
            public static final String TIME_CREATED = "time_created";
            public static final String COLOR = "color";
            public static final String TYPE = "type";
        }

        public static final String [] COLUMNS = {
                Cols.NOTE_ID,
                Cols.TITLE,
                Cols.TEXT,
                Cols.LIST_PARENT,
                Cols.IS_CHECKED,
                Cols.TIME_CREATED,
                Cols.COLOR,
                Cols.TYPE
        };
    }
}
