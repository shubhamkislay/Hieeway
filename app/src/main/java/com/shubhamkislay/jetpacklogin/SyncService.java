package com.shubhamkislay.jetpacklogin;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shubhamkislay.jetpacklogin.Adapters.ContactsAdapter;
import com.shubhamkislay.jetpacklogin.Interface.SyncServiceListener;
import com.shubhamkislay.jetpacklogin.Model.Iso2Phone;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_3_ID;
import static com.shubhamkislay.jetpacklogin.MyApplication.CONTACT_SERVICE_RUNNUNG;

public class SyncService extends Service {

    private final IBinder binder = new LocalBinder();
    HashMap<String, Object> phoneBookNameHashMap;
    Boolean synced = false;
    Boolean isSyncing = false;
    Cursor phone;
    SyncServiceListener syncServiceListener;
    String phonenumber = "default";
    private List<User> userList;
    // Registered callbacks

    @Override
    public void onCreate() {
        super.onCreate();

        userList = new ArrayList<>();
        phoneBookNameHashMap = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String userID = intent.getStringExtra("userID");
        phonenumber = intent.getStringExtra("phonenumber");


        Intent notificationIntent = new Intent(this, ContactsActivity.class);

        notificationIntent.putExtra("phonenumber", phonenumber);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setContentTitle("Syncing your contacts")
                .setSmallIcon(R.drawable.ic_action_contacts_phonebook)
                // .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);
        getContactList();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return binder;
    }

    public void setSyncServiceListener(SyncServiceListener syncServiceListener) {
        this.syncServiceListener = syncServiceListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getUserDetails(final String number, final String name, final Boolean continueProgress) {

        DatabaseReference findUserRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = findUserRef.orderByChild("phonenumber").equalTo(number);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);


                        //if(user.getPhonenumber().equals(number))
                        if (userList.contains(user)) {


                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userID", user.getUserid());
                            hashMap.put("name", name);

                            FirebaseDatabase.getInstance().getReference("Contacts")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(user.getUserid()).updateChildren(hashMap);
                        }

                        if (!userList.contains(user) && !user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                            userList.add(user);
                            phoneBookNameHashMap.put(user.getUserid(), name);
                           /* count = count + 1;
                            contacts_counter.setText("" + count);*/

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userID", user.getUserid());
                            hashMap.put("name", name);

                            FirebaseDatabase.getInstance().getReference("Contacts")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(user.getUserid()).updateChildren(hashMap);


                            isSyncing = false;


                        }

                        if (!continueProgress) {
                            // syncServiceListener.afterExecution();
                            CONTACT_SERVICE_RUNNUNG = false;
                            stopSelf();
                        }
                    }
                    if (!continueProgress) {
                       /* contactAdapter = new ContactsAdapter(ContactsActivity.this, userList, activity, phoneBookNameHashMap);
                        contacts_recyclerView.setAdapter(contactAdapter);*/
                        // syncServiceListener.afterExecution();
                        CONTACT_SERVICE_RUNNUNG = false;
                        stopSelf();
                    }
                } else {
                    if (!continueProgress) {

                        //syncServiceListener.afterExecution();
                        CONTACT_SERVICE_RUNNUNG = false;
                        stopSelf();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getContactList() {

        userList.clear();


        isSyncing = true;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);
        } else {
            phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        }

        //sync_msg_layout.setVisibility(View.VISIBLE);
        //  sync_btn.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (phone.moveToNext()) {
                    String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    number = number.replace("(", "");
                    number = number.replace(")", "");

                    if (!String.valueOf(number.charAt(0)).equals("+")) {
                        if (String.valueOf(number.charAt(0)).equals("0"))
                            number = number.substring(1);
                        number = getCountryIso() + number;

                    }

                    Log.v("Contact", "name: " + name + " number: " + number);
                    getUserDetails(number, name, phone.moveToNext());

                                /*if(!phone.moveToNext()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sync_msg_layout.setVisibility(View.GONE);
                                            sync_btn.setVisibility(View.VISIBLE);
                                        }
                                    },5000);*/

                    // }

                }
            }
        }).start();

    }

    private String getCountryIso() {

        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null)

            iso = telephonyManager.getNetworkCountryIso().toString();

        return Iso2Phone.getPhone(iso);


    }

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        SyncService getService() {
            // Return this instance of MyService so clients can call public methods
            return SyncService.this;
        }
    }
}