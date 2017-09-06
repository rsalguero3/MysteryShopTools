package com.gorrilaport.mysteryshoptools.util;

public class Constants {
    public final static String SERIALIZED_CATEGORY = "serialized_category";
    public static final String REALM_DATABASE = "mysteryshoptools.realm";
    public static final String SQLITE_DATABASE = "mysteryshoptools.sqlite";
    public static final String FIRST_RUN = "first_run";
    public static final String PREFERRED_EDITOR = "preferred_editor";
    public final static int LINED_EDITOR = 1;
    public final static int PLAIN_EDITOR = 2;
    public static final String NOTE_ID = "NOTE_ID";
    public static final String DEFAULT_CATEGORY = "General";
    public static final String APP_FOLDER = "MysteryShopTools";
    public static final String BACKUP_FOLDER = "MysteryShopTools/Backups";
    public static final String REALM_EXPORT_FILE_NAME = "realm_export";
    public static final String REALM_IMPORT_FILE_NAME = "realm_import";
    public static final String REALM_EXPORT_FILE_PATH = "realm_export_path";
    public static final String IS_DUAL_SCREEN = "is_dual_screen";
    public static final String SORT_PREFERENCE = "sort_PREFERENCE";
    public static final String IMAGE_PATH = "IMAGE_PATH";


    public final static int NOTE = 1;
    public final static int CATEGORY = 2;
    public static final int CAMERA = 3;
    public final static int SETTINGS = 4;
    public final static int TIMER = 5;
    public static final int COMPANIES = 6;
    public static final int ABOUT = 7;


    public final static int SORT_TITLE = 1;
    public final static int SORT_CATEGORY = 2;
    public static final int SORT_COLOR = 3;
    public final static int SORT_DATE = 4;

    public static final String NOTES_TABLE = "note";
    public static final String CATEGORY_TABLE = "category";
    public static final String IMAGE_TABLE = "image";

    //Async Query Ids
    public static final int INSERT_NOTE = 1;
    public final static int DELETE_NOTE = 2;
    public static final int INSERT_CATEGORY = 3;
    public final static int DELETE_CATEGORY = 4;
    public final static int QUERY_GET_ALL_CATEGORY = 5;

    public static final String COLUMN_ID = "_id";
    public final static String COLUMN_NAME = "name";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_MODIFIED_TIME = "modified_time";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_CREATED_TIME = "created_time";
    public static final String COLUMNS_CATEGORY_ID = "category_id";
    public static final String COLUMNS_TODO_LIST_ID = "todo_list_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_SORTED_NOTES = "sorted_note";
    public static final String COLUMN_SORTED_TODOS = "sorted_todo";
    public static final String COLUMN_BACKUP_PROVIDER = "backup_provider";
    public static final String COLUMN_BACKUP_FILE_SIZE = "backup_file_size";
    public final static String PREFERENCE_FILE = "preference_file";


    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_NOTE_ID = "note_id";



    public final static String SORT_DATE_CREATED = "dateCreated";
    public final static String SORT_DATE_MODIFIED = "dateModified";

    public final static String NOTE_TYPE_TEXT = "text";
    public final static String NOTE_TYPE_IMAGE = "image";
    public final static String NOTE_TYPE_AUDIO = "audio";
    public final static String NOTE_TYPE_REMINDER = "reminder";

    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_AUDIO = "audio/amr";
    public final static String MIME_TYPE_SKETCH = "image/png";


    public final static String MIME_TYPE_IMAGE_EXT = ".jpg";
    public final static String MIME_TYPE_AUDIO_EXT = ".amr";
    public final static String MIME_TYPE_SKETCH_EXT = ".png";

    public static final String ATTACHMENTS_FOLDER = "Attachments";
    public static final String SERIALIZED_NOTE = "serialized_note";

    public static final String COLUMN_NEXT_REMINDER = "next_reminder";
    public static final String COLUMN_LOCAL_AUDIO_PATH = "local_audio_path";
    public static final String COLUMN_LOCAL_IMAGE_PATH = "local_image_path";
    public static final String COLUMN_LOCAL_SKETCH_PATH = "local_sketch_image_path";
    public static final String COLUMNS_NOTE_TYPE = "note_type";


    public static final String[] COLUMNS_NOTE = {

            Constants.COLUMN_ID,
            Constants.COLUMN_TITLE,
            Constants.COLUMN_CONTENT,
            Constants.COLUMN_COLOR,
            Constants.COLUMN_CATEGORY_NAME,
            Constants.COLUMNS_CATEGORY_ID,
            Constants.COLUMN_CREATED_TIME,
            Constants.COLUMN_MODIFIED_TIME

    };

    public static final String[] COLUMNS_CATEGORY = {

            Constants.COLUMN_ID,
            Constants.COLUMN_NAME,
            Constants.COLUMN_CREATED_TIME,
            Constants.COLUMN_MODIFIED_TIME

    };
}