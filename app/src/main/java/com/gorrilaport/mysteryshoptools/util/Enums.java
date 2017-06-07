package com.gorrilaport.mysteryshoptools.util;

public class Enums {
    public enum SortMethodEnum{
        Title,
        Category,
        Color,
        DateModified
    }

    public enum SortMethodTodoList{
        Title,
        Category,
        NumberTask,
        DateModified
    }


    public enum SortMethodTask{
        Title,
        Status,
        Priority,
        DueDate,
        DateModified
    }

    public enum PreferredEditor{
        LinedEditor,
        PlainEditor
    }

    public enum Priority{
        Low,
        Normal,
        Important,
        High
    }

    public enum ASYNC_QUERIES{
        TodoListInsert,
        TaskInsert
    }
}
