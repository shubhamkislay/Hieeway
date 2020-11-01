package com.hieeway.hieeway;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hieeway.hieeway.Helper.GroupMemberListHelper;
import com.hieeway.hieeway.Interface.ChangePictureListener;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.GroupMember;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChangeGroupDetailsActivity extends AppCompatActivity implements ChangePictureListener {

    private static final int PERMISSION_PICK_IMAGE = 1000;
    TextView page_title, add_group_icon;
    ProgressBar progress_bar_one;
    Button create_group_btn;
    EditText group_name;
    CircleImageView group_icon;
    private String groupName;
    private String groupID;
    private String groupIcon;
    private String profilepic = "default";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private DatabaseReference groupRef;
    private List<Friend> groupMemberList;
    private String userID;
    private static final int UPDATED_RESULT = 10;
    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group_details);
        resultIntent = new Intent();

        groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");

        Intent intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        groupID = intent.getStringExtra("groupID");
        groupIcon = intent.getStringExtra("icon");

        page_title = findViewById(R.id.page_title);
        add_group_icon = findViewById(R.id.add_group_icon);
        progress_bar_one = findViewById(R.id.progress_bar_one);
        create_group_btn = findViewById(R.id.create_group_btn);
        group_name = findViewById(R.id.group_name);
        group_icon = findViewById(R.id.group_icon);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        groupMemberList = GroupMemberListHelper.getInstance().getGroupMemberList();


        group_name.setText(groupName);

        if (!groupIcon.equals("default"))
            Glide.with(this)
                    .load(groupIcon)
                    .into(group_icon);

        try {
            page_title.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));
            add_group_icon.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));
            create_group_btn.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        } catch (Exception e) {
            //
        }


        create_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDetails();
            }
        });

        group_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        add_group_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


    }

    private void uploadImage() {
        PictureChangeDialog pictureChangeDialog = new PictureChangeDialog(ChangeGroupDetailsActivity.this, false, profilepic, ChangeGroupDetailsActivity.this, groupID);
        pictureChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pictureChangeDialog.setCancelable(true);
        pictureChangeDialog.show();
    }

    private void checkDetails() {
        if (group_name.getText().toString().length() > 2 && group_name.getText().toString().length() < 31) {

            /*Intent intent = new Intent(ChangeGroupDetailsActivity.this, CreateGroupActivity.class);
            intent.putExtra("icon", profilepic);
            intent.putExtra("groupID", groupID);
            intent.putExtra("groupName", group_name.getText().toString());
            intent.putExtra("requestType","create");
            startActivity(intent);
            finish();*/

            groupName = group_name.getText().toString();


            updateGroupDetail();

        } else {
            group_name.setError("Group name should be 3-30 characters");
        }
    }

    private void updateGroupDetail() {


        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupID);

        HashMap<String, Object> groupHash = new HashMap<>();
        groupHash.put("groupName", groupName);

        groupRef.updateChildren(groupHash);


        DatabaseReference myGroup = FirebaseDatabase.getInstance().getReference("MyGroup")
                .child(userID)
                .child(groupID);

        myGroup.updateChildren(groupHash);


        for (Friend friend : groupMemberList) {


            DatabaseReference mGroup = FirebaseDatabase.getInstance().getReference("MyGroup")
                    .child(friend.getFriendId())
                    .child(groupID);

            HashMap<String, Object> ghashMap = new HashMap<>();
            ghashMap.put("groupName", groupName);


            mGroup.updateChildren(ghashMap);

        }

/*        //Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateGroupActivity.this, GroupChatActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("groupID", groupID);
        intent.putExtra("groupName", groupName);
        intent.putExtra("icon", icon);
        startActivity(intent);*/

        resultIntent.putExtra("groupName", groupName);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void changePicture(Boolean active) {
        imageSelect();
    }

    @Override
    public void removedPicture(Boolean active) {

        profilepic = "default";

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupID);

        HashMap<String, Object> groupHash = new HashMap<>();
        groupHash.put("icon", profilepic);

        groupRef.updateChildren(groupHash);


        for (Friend friend : groupMemberList) {


            DatabaseReference mGroup = FirebaseDatabase.getInstance().getReference("MyGroup")
                    .child(friend.getFriendId())
                    .child(groupID);

            HashMap<String, Object> ghashMap = new HashMap<>();
            ghashMap.put("icon", profilepic);

            mGroup.updateChildren(ghashMap);
        }

/*        //Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateGroupActivity.this, GroupChatActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("groupID", groupID);
        intent.putExtra("groupName", groupName);
        intent.putExtra("icon", icon);
        startActivity(intent);*/

        Glide.with(this).load(getDrawable(R.drawable.groups_image)).into(group_icon);

        resultIntent.putExtra("groupIcon", profilepic);


    }

    public void imageSelect() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PERMISSION_PICK_IMAGE);
    }

    private void uploadImage(Uri imageUri) {

        progress_bar_one.setVisibility(View.VISIBLE);
        group_icon.setImageDrawable(getDrawable(R.drawable.dark_drawable));

        /*final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this );

        progressDialog.setMessage("Uploading photo");
        progressDialog.show();*/


        //registerUsernameEntryFragment.setProgressVisibility(true);

        if (imageUri != null) {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }


                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        String mUri = downloadUri.toString();


                        groupRef = groupRef.child(groupID);


                        profilepic = mUri;
                        progress_bar_one.setVisibility(View.GONE);

                        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups")
                                .child(groupID);

                        HashMap<String, Object> groupHash = new HashMap<>();
                        groupHash.put("icon", mUri);

                        groupRef.updateChildren(groupHash);


                        for (Friend friend : groupMemberList) {


                            DatabaseReference mGroup = FirebaseDatabase.getInstance().getReference("MyGroup")
                                    .child(friend.getFriendId())
                                    .child(groupID);

                            HashMap<String, Object> ghashMap = new HashMap<>();
                            ghashMap.put("icon", mUri);

                            mGroup.updateChildren(ghashMap);
                        }

/*        //Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateGroupActivity.this, GroupChatActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("groupID", groupID);
        intent.putExtra("groupName", groupName);
        intent.putExtra("icon", icon);
        startActivity(intent);*/

                        resultIntent.putExtra("groupIcon", mUri);


                        //progressDialog.dismiss();


                        try {
                            Glide.with(ChangeGroupDetailsActivity.this).load(mUri).into(group_icon);
                        } catch (Exception e) {
                            //
                        }
                            
                        

                            

                        /*registerUsernameEntryFragment.setProgressVisibility(false);
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(PHOTO_URL, mUri);
                        editor.apply();

                        registerUsernameEntryFragment.setUploadedImage(mUri);*/

                    } else {
                        Toast.makeText(ChangeGroupDetailsActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                        // registerUsernameEntryFragment.setProgressVisibility(false);
                        progress_bar_one.setVisibility(View.GONE);

                    }

                }
            });


        } else {
            Toast.makeText(ChangeGroupDetailsActivity.this, "No Image selected", Toast.LENGTH_SHORT).show();
            progress_bar_one.setVisibility(View.GONE);

        }


    }

    private String getExtension(Uri uri) {


        ContentResolver contentResolver = ChangeGroupDetailsActivity.this.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            if (requestCode == PERMISSION_PICK_IMAGE) {
                //  Bitmap bitmap = BitmapUtils.getBitmapFromGallery(getContext(), data.getData(), 800, 800);


                // image_selected_uri = data.getData();

                startCrop(data.getData());


                /*//clear bitmap memory

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();


                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                //   img_preview.setImageBitmap(originalBitmap);
                photoEditorView.getSource().setImageBitmap(originalBitmap);*/

                //bitmap.recycle();

                //Render selected img thumbnail

                /*filtersListFragment.displayThumbnail(originalBitmap);*/

                /*filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);*/
            } else if (requestCode == UCrop.REQUEST_CROP) {
                //  Toast.makeText(this, "Image cropped",Toast.LENGTH_SHORT).show();
                handleCropResult(data);

            } else if (requestCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }


        }
    }

    private void startCrop(Uri myUri) {


        Uri uri = myUri;


        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();


        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        UCrop.Options up = new UCrop.Options();
        up.setLogoColor(getResources().getColor(R.color.colorBlack));
        up.setStatusBarColor(getResources().getColor(R.color.colorBlack));
        up.setToolbarColor(getResources().getColor(R.color.colorBlack));
        up.setLogoColor(getResources().getColor(R.color.colorPrimaryDark));
        up.setActiveWidgetColor(getResources().getColor(R.color.colorPrimaryDark));
        up.setCompressionQuality(50);
        /*up.setRootViewBackgroundColor(getResources().getColor(R.color.colorBlack));
        up.setToolbarWidgetColor(getResources().getColor(R.color.colorBlack));
        up.setDimmedLayerColor(getResources().getColor(R.color.colorPrimaryDark));*/

        uCrop.withAspectRatio(18, 18);
        uCrop.withOptions(up);

        /*UCropActivity uCropActivity = new UCropActivity();

        uCropActivity.setTheme(R.style.AppTheme);*/


        /**
         * Use the code below for a square image selection i.e. for profile pictures
         * uCrop.withAspectRatio(1.0f, 1.0f);**/

        uCrop.start(ChangeGroupDetailsActivity.this);

    }

    private void handleCropError(Intent data) {

        final Throwable cropError = UCrop.getError(data);

        if (cropError != null) {
            // Toast.makeText(this, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }


    }

    private void handleCropResult(Intent data) {

        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            //registerUsernameEntryFragment.setImageUri(resultUri);
            //Toast.makeText(this, "Image cropped",Toast.LENGTH_SHORT).show();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(ChangeGroupDetailsActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
            } else {

                uploadImage(resultUri);
            }

        } else {
            Toast.makeText(this, "Cannot retrieve crop image", Toast.LENGTH_SHORT).show();
        }

    }
}