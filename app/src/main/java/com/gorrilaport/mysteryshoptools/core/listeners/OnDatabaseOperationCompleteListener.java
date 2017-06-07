package com.gorrilaport.mysteryshoptools.core.listeners;

public interface OnDatabaseOperationCompleteListener {
    void onSaveOperationFailed(String error);
    void onSaveOperationSucceeded(long id);
    void onDeleteOperationCompleted(String message);
    void onDeleteOperationFailed(String error);
    void onUpdateOperationCompleted(String message);
    void onUpdateOperationFailed(String error);
}
