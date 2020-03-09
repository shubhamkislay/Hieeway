package com.shubhamkislay.jetpacklogin.Model;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchChatsAsyncTaskModel {

    private Query Query;
    private ValueEventListener valueEventListener;

    public  SearchChatsAsyncTaskModel(){

    }

    public SearchChatsAsyncTaskModel(com.google.firebase.database.Query query, ValueEventListener valueEventListener) {
        Query = query;
        this.valueEventListener = valueEventListener;
    }

    public com.google.firebase.database.Query getQuery() {
        return Query;
    }

    public void setQuery(com.google.firebase.database.Query query) {
        Query = query;
    }

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }
}
