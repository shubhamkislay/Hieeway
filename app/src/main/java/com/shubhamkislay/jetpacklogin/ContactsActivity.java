package com.shubhamkislay.jetpacklogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Adapters.ContactsAdapter;
import com.shubhamkislay.jetpacklogin.Adapters.PeopleAdapter;
import com.shubhamkislay.jetpacklogin.Model.ContactUser;
import com.shubhamkislay.jetpacklogin.Model.Iso2Phone;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

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
    private RelativeLayout parent_layout;
    private Button add_number_btn;
    private static final int FETCH_NUMBER = 2;
    private TextView features_list, features_title, phone_title, features_list2;


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

        phone_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        features_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        features_list.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        add_number_btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        features_list2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

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

        } else {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (!user.getPhonenumber().equals("default"))
                                    add_phone_number_layout.setVisibility(View.GONE);
                                checkSynced();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }







        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSyncing)
                    getContactList();
            }
        });


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
                                    getContactList();

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
                                    populateUserList();
                                }
                            } catch (Exception e) {
                                getContactList();

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
        for (final ContactUser contactUser : contactsList) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(contactUser.getUserID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                count += 1;
                                contacts_counter.setText("" + count);
                                userList.add(user);
                                phoneBookNameHashMap.put(user.getUserid(), contactUser.getName());

                                contactAdapter = new ContactsAdapter(ContactsActivity.this, userList, activity, phoneBookNameHashMap);
                                contacts_recyclerView.setAdapter(contactAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }

    private void getContactList() {
        isSyncing = true;
        Cursor phone;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);
        } else {
            phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        }

        while (phone.moveToNext()) {
            String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            number = number.replace(" ", "");
            number = number.replace("-", "");
            number = number.replace("(", "");
            number = number.replace(")", "");

            if (!String.valueOf(number.charAt(0)).equals("+"))
                number = getCountryIso() + number;


            getUserDetails(number, name);

        }
        //  isSyncing = false;
        // Toast.makeText(ContactsActivity.this,"No of accounts: "+userList.size(),Toast.LENGTH_SHORT).show();


    }

    private void getUserDetails(final String number, final String name) {

        DatabaseReference findUserRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = findUserRef.orderByChild("phonenumber").equalTo(number);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);


                        //if(user.getPhonenumber().equals(number))
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
                                    .push().updateChildren(hashMap);

                            isSyncing = false;


                        }
                    }
                    contactAdapter = new ContactsAdapter(ContactsActivity.this, userList, activity, phoneBookNameHashMap);
                    contacts_recyclerView.setAdapter(contactAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
                userList.clear();
                phonenumber = data.getData().toString();
                add_phone_number_layout.setVisibility(View.GONE);
                checkSynced();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
