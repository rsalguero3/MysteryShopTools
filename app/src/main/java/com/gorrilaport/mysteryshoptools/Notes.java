package com.gorrilaport.mysteryshoptools;

import android.text.format.DateFormat;

import java.util.UUID;

/**
 * Created by Ricardo on 3/13/2016.
 */
public class Notes {
    private UUID mUUID;
    private String mTitle;
    private String mTextInput;
    private DateFormat mDate;

    public Notes (){
        mUUID = UUID.randomUUID();
        mDate = getDate();
    }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTextInput(String textInput){
        this.mTextInput = textInput;
    }

    public String getTextInput(){
        return mTextInput;
    }

    public DateFormat getDate(){
        return mDate;
    }

}
