package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.hieeway.hieeway.Adapters.ContactsAdapter;
import com.hieeway.hieeway.Interface.SyncServiceListener;
import com.hieeway.hieeway.Model.ContactUser;
import com.hieeway.hieeway.Model.Iso2Phone;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.hieeway.hieeway.MyApplication.CONTACT_SERVICE_RUNNUNG;

public class ContactsActivity extends AppCompatActivity implements SyncServiceListener {

    HashMap<String, Object> phoneBookNameHashMap;
    Activity activity;
    Boolean synced = false;
    Button sync_btn;
    Boolean isSyncing = false;
    private RecyclerView contacts_recyclerView;
    private TextView contacts_title, contacts_counter;
    private int count = 0;
    private ContactsAdapter contactAdapter;
    private List<User> userList;
    private List<ContactUser> contactsList;
    private String phonenumber = "default";
    private ConstraintLayout add_phone_number_layout;
    Cursor phone;
    ContactsListViewModel contactsListViewModel;
    private Button add_number_btn;
    private static final int FETCH_NUMBER = 2;
    private RelativeLayout parent_layout, sync_msg_layout;
    private TextView features_list, features_title, phone_title, features_list2, sync_txt;
    private SyncService myService;
    private boolean bound = false;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        activity = ContactsActivity.this;

        parent_layout = findViewById(R.id.parent_layout);
        contacts_recyclerView = findViewById(R.id.contacts_recyclerView);
        contacts_title = findViewById(R.id.contacts_title);
        contacts_counter = findViewById(R.id.contacts_counter);
        sync_btn = findViewById(R.id.sync_btn);
        userList = new ArrayList<>();
        contactsList = new ArrayList<>();


        contacts_recyclerView.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));


        contacts_recyclerView.setHasFixedSize(true);
        phoneBookNameHashMap = new HashMap<>();


        contacts_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        add_phone_number_layout = findViewById(R.id.add_phone_number_layout);
        add_number_btn = findViewById(R.id.add_number_btn);
        features_list = findViewById(R.id.feautures_list1);
        features_list2 = findViewById(R.id.feautures_list2);
        features_title = findViewById(R.id.feature_title);
        phone_title = findViewById(R.id.phone_title);
        sync_msg_layout = findViewById(R.id.sync_msg_layout);
        sync_txt = findViewById(R.id.sync_txt);

        phone_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        features_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        features_list.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        add_number_btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        features_list2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        sync_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        add_number_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ContactsActivity.this, PhoneAuthenticationActivity.class), FETCH_NUMBER);
            }
        });


        Intent intent = getIntent();
        phonenumber = intent.getStringExtra("phonenumber");


        if (!phonenumber.equals("default")) {

            add_phone_number_layout.setVisibility(View.GONE);
            checkSynced();

        }

      /*  if(isServiceRunning("SyncService"))
            sync_msg_layout.setVisibility(View.VISIBLE);*/

        if (CONTACT_SERVICE_RUNNUNG) {
            sync_msg_layout.setVisibility(View.VISIBLE);
            checkIfServiceIsStillRuning();
        }


        contactsListViewModel = ViewModelProviders.of(this).get(ContactsListViewModel.class);
        contactsListViewModel.getContacts().observe(this, new Observer<List<ContactUser>>() {
            @Override
            public void onChanged(List<ContactUser> contactUsers) {
                // Toast.makeText(ContactsActivity.this,"REfreshed"+contactUsers.size(),Toast.LENGTH_SHORT).show();

                mapToUserList(contactUsers);
            }
        });

      /*  sharedViewModel = ViewModelProviders.of(ContactsActivity.this).get(SharedViewModel.class);


        sharedViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                phonenumber = user.getPhonenumber();
                if(phonenumber.equals("default"))
                    add_phone_number_layout.setVisibility(View.VISIBLE);
                else
                    add_phone_number_layout.setVisibility(View.GONE);
            }
        });*/


        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSyncing)
                    // getContactList();
                    if (!CONTACT_SERVICE_RUNNUNG)
                        checkPermissionContact();


                    /*if(!isServiceRunning("SyncService")) {
                        sync_msg_layout.setVisibility(View.VISIBLE);
                        checkPermissionContact();
                    }*/
                    else
                        Toast.makeText(ContactsActivity.this, "Service is running", Toast.LENGTH_SHORT).show();
                //startSyncService();

            }
        });


    }

    private void checkIfServiceIsStillRuning() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!CONTACT_SERVICE_RUNNUNG) {
                    //populateUserList();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sync_msg_layout.setVisibility(View.GONE);
                        }
                    }, 5000);

                } else
                    checkIfServiceIsStillRuning();
            }
        }, 3000);
    }


    public void startSyncService() {
        CONTACT_SERVICE_RUNNUNG = true;

        Intent serviceInent = new Intent(this, SyncService.class);
        serviceInent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        serviceInent.putExtra("phonenumber", phonenumber);


        sync_msg_layout.setVisibility(View.VISIBLE);
        //bindService(serviceInent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(serviceInent);
        checkIfServiceIsStillRuning();
    }

/*    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            SyncService.LocalBinder binder = (SyncService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setSyncServiceListener(ContactsActivity.this);
            myService.getContactList();// register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };*/

    public void stopSyncService() {
        Intent serviceInent = new Intent(this, SyncService.class);

        stopService(serviceInent);
    }

    private void checkSynced() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            try {
                                if (!user.getSynced()) {

                                    //getContactList();
                                    if (!CONTACT_SERVICE_RUNNUNG)
                                        checkPermissionContact();

                                    HashMap<String, Object> syncHash = new HashMap<>();
                                    syncHash.put("synced", true);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .updateChildren(syncHash);

                                } else {
                                    synced = true;
                                    // Toast.makeText(ContactsActivity.this, "Synced", Toast.LENGTH_SHORT).show();

                                    /*Snackbar snackbar = Snackbar
                                            .make(parent_layout, "Synced", Snackbar.LENGTH_SHORT);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimaryDark));
                                    snackbar.show();*/

                                    //  populateUserList();
                                }
                            } catch (Exception e) {

                                // getContactList();
                                if (!CONTACT_SERVICE_RUNNUNG)
                                    checkPermissionContact();

                                HashMap<String, Object> syncHash = new HashMap<>();
                                syncHash.put("synced", true);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .updateChildren(syncHash);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void populateUserList() {
        FirebaseDatabase.getInstance().getReference("Contacts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ContactUser contactUser = snapshot.getValue(ContactUser.class);
                                contactsList.add(contactUser);

                            }
                            mapToUserList(contactsList);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void mapToUserList(List<ContactUser> contactsList) {

        userList.clear();
        count = 0;

        for (final ContactUser contactUser : contactsList) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(contactUser.getUserID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (!userList.contains(user)) {
                                    count += 1;
                                    contacts_counter.setText("" + count);
                                    userList.add(user);
                                    phoneBookNameHashMap.put(user.getUserid(), contactUser.getName());
                                }


                            }
                            contactAdapter = new ContactsAdapter(ContactsActivity.this, userList, activity, phoneBookNameHashMap);
                            contacts_recyclerView.setAdapter(contactAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }

    /*private void getContactList() {


        Dexter.withActivity(ContactsActivity.this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            count = 0;
                            contacts_counter.setText("" + count);
                            userList.clear();
                            contactAdapter = new ContactsAdapter(ContactsActivity.this, userList, activity, phoneBookNameHashMap);
                            contacts_recyclerView.setAdapter(contactAdapter);

                            isSyncing = true;
                            count = 0;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);
                            } else {
                                phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                            }

                            sync_msg_layout.setVisibility(View.VISIBLE);
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

                                        if (!String.valueOf(number.charAt(0)).equals("+"))
                                            number = getCountryIso() + number;


                                        Log.v("Name", "" + name);
                                        getUserDetails(number, name, phone.moveToNext());

                                *//*if(!phone.moveToNext()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sync_msg_layout.setVisibility(View.GONE);
                                            sync_btn.setVisibility(View.VISIBLE);
                                        }
                                    },5000);*//*

                                        // }

                                    }
                                }
                            }).start();

                        } else {
                            Toast.makeText(ContactsActivity.this, "Permxission not given!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();



        //  isSyncing = false;
        // Toast.makeText(ContactsActivity.this,"No of accounts: "+userList.size(),Toast.LENGTH_SHORT).show();


    }

*/
    private void checkPermissionContact() {
        Dexter.withActivity(ContactsActivity.this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            sync_msg_layout.setVisibility(View.VISIBLE);


                            startSyncService();


                        } else {
                            Toast.makeText(ContactsActivity.this, "Permxission not given!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    @Override
    public void afterExecution() {

        sync_msg_layout.setVisibility(View.GONE);

    }

    /* private void getUserDetails(final String number, final String name, final Boolean continueProgress) {

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
                             count = count + 1;
                             contacts_counter.setText("" + count);

                             HashMap<String, Object> hashMap = new HashMap<>();
                             hashMap.put("userID", user.getUserid());
                             hashMap.put("name", name);

                             FirebaseDatabase.getInstance().getReference("Contacts")
                                     .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                     .child(user.getUserid()).updateChildren(hashMap);





                         }

                         if (!continueProgress) {
                             new Handler().postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     sync_msg_layout.setVisibility(View.GONE);
                                     // sync_btn.setVisibility(View.VISIBLE);
                                 }
                             }, 5000);
                         }
                     }
                     if (!continueProgress) {

                         isSyncing = false;
                         contactAdapter = new ContactsAdapter(ContactsActivity.this, userList, activity, phoneBookNameHashMap);
                         contacts_recyclerView.setAdapter(contactAdapter);
                     }
                 } else {
                     if (!continueProgress) {
                         contactAdapter = new ContactsAdapter(ContactsActivity.this, userList, activity, phoneBookNameHashMap);
                         contacts_recyclerView.setAdapter(contactAdapter);
                         isSyncing = false;
                         new Handler().postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 sync_msg_layout.setVisibility(View.GONE);
                                // sync_btn.setVisibility(View.VISIBLE);
                             }
                         }, 5000);
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }
 */
    private String getCountryIso() {

        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null)

            iso = telephonyManager.getNetworkCountryIso().toString();

        return Iso2Phone.getPhone(iso);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == FETCH_NUMBER) {
            if (resultCode == RESULT_OK) {
                count = 0;
                userList.clear();
                phonenumber = data.getData().toString();
                add_phone_number_layout.setVisibility(View.GONE);
                checkSynced();
            }
        }


    }

    private boolean isServiceRunning(String serviceName) {
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i
                    .next();

            if (runningServiceInfo.service.getClassName().equals(serviceName)) {
                serviceRunning = true;

                if (runningServiceInfo.foreground) {
                    //service run in foreground
                }
            }
        }
        return serviceRunning;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if(isSyncing)
            startSyncService();*/
    }
}
