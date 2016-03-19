package com.gorrilaport.mysteryshoptools;

/**
 * Created by Ricardo on 3/13/2016.
 */
public class NotePadDbSchema {
    public static final class NotesTable{
        public static final String NAME = "notes";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String TEXTINPUT = "textinput";
        }
    }
}
