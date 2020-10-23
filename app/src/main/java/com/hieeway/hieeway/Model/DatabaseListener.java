package com.hieeway.hieeway.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DatabaseListener {

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    public DatabaseListener() {
    }

    public DatabaseListener(DatabaseReference databaseReference, ValueEventListener valueEventListener) {
        this.databaseReference = databaseReference;
        this.valueEventListener = valueEventListener;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }
}
