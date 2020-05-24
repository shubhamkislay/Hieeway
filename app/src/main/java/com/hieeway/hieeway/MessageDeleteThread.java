package com.hieeway.hieeway;

import android.os.Handler;
import android.os.Looper;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.hieeway.hieeway.Model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MessageDeleteThread extends Thread {

    private String userChattingWith;
    public Handler handler;
   // private
    private List<ChatMessage> deleteQueueList;
    private List<ChatMessage> updateList = new ArrayList<>();
    private DatabaseReference databaseReference;
    public Looper looper;




    public void setDeleteQueueList(List<ChatMessage> deleteQueueList)
    {

        this.deleteQueueList = deleteQueueList;

    }

    public List<ChatMessage> getDeleteQueueList()
    {
        return deleteQueueList;
    }





    @Override
    public void run() {
        Log.i(TAG, "run: Started!");


   //     updateList = getDeleteQueueList();


        Looper.prepare();


        looper = Looper.myLooper();

       handler = new Handler();


//        Log.i(TAG, "DeleteQueueList size: "+updateList.size());


        Looper.loop();

        Log.i(TAG, "run: Completed!");
    }
}
