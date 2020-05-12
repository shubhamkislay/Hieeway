package com.shubhamkislay.jetpacklogin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import com.shubhamkislay.jetpacklogin.Helper.BitmapHelper;
import com.shubhamkislay.jetpacklogin.Interface.AddTextFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.BrushFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.EditImageFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.EmojiFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.FiltersListFragmentListener;
import com.shubhamkislay.jetpacklogin.Utils.AddTextFragment;
import com.shubhamkislay.jetpacklogin.Utils.BitmapUtils;
import com.shubhamkislay.jetpacklogin.Utils.EmojiFragment;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class PhotoEditToolsActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener, BrushFragmentListener, EmojiFragmentListener, AddTextFragmentListener {



    public static final String pictureName = "flash.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;
    public static final int PERMISSION_INSERT_IMAGE = 1001;
    public static final int CAMERA_REQUEST = 1003;


    private LruCache<String, Bitmap> mMemoryCache;




    //  ImageView img_preview;
    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;

    DatabaseReference databaseReference, receiverReference;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;

    String userChattingWithId;
    String usernameChattingWith;
    String userphotoUrl;
    String currentUsername;
    String currentUserPhoto;
    String otherUserPublicKeyID;
    String currentUserPublicKeyID;


    String currentPhotoPath;
    /*TabLayout tabLayout;
    ViewPager viewPager;*/


    CoordinatorLayout coordinatorLayout;
    String bmp;

    CardView btn_filters_list, btn_edit, btn_brush, btn_emoji, btn_add_text, btn_add_image, btn_crop;

    Button click_photo,browse_image,save_photo, action_send_pic, action_cancel_photo;

    RelativeLayout action_send_pic_layout;


    Bitmap originalBitmap,filteredBitmap,finalBitmap;
    EmojiFragment emojiFragment ;


    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;
    BrushFragment brushFragment;

    DatabaseReference senderChatCreateRef;
    DatabaseReference receiverChatCreateRef;

    RelativeLayout relativeLayout;

    int device_height;
    int device_width;









    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;


    Uri image_selected_uri;


    //Load native image filters library

    static{
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit_tools);


        // Get max available VM memory, exceeding this amount will throw an
// OutOfMemory exception. Stored in kilobytes as LruCache takes an
// int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

// Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        final Intent intent = getIntent();

        userChattingWithId = intent.getStringExtra("userChattingWithId");
        usernameChattingWith = intent.getStringExtra("usernameChattingWith");
        userphotoUrl = intent.getStringExtra("userphotoUrl");
        currentUserPhoto = intent.getStringExtra("currentUserPhoto");
        currentUsername = intent.getStringExtra("currentUsername");
        otherUserPublicKeyID = intent.getStringExtra("otherUserPublicKeyID");
        currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");

        senderChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userChattingWithId);

        receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userChattingWithId).child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        action_send_pic_layout = findViewById(R.id.action_send_pic_layout);

        action_send_pic = findViewById(R.id.action_send_pic);

        action_cancel_photo = findViewById(R.id.action_cancel_photo);


        action_cancel_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PhotoEditToolsActivity.this,CameraActivity.class);

                intent.putExtra("userChattingWithId",userChattingWithId);
                startActivity(intent);
                finish();
            }
        });


        /*Intent intent = getIntent();

        bmp = intent.getStringExtra("path");*/

        // bmp = getBitmapFromMemCache(bitmapCacheName);




        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pickaboo");
*/

        // filtersListFragment = new FiltersListFragment();
        photoEditorView = findViewById(R.id.image_preview);

        photoEditor = new PhotoEditor.Builder(PhotoEditToolsActivity.this,photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "emojione-android.ttf"))
                .build();


        click_photo = findViewById(R.id.action_camera);

        browse_image = findViewById(R.id.action_open);

        save_photo = findViewById(R.id.action_save);


        relativeLayout = findViewById(R.id.activity_view_layout);


        action_send_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {

                        BitmapHelper.getInstance().setBitmap(saveBitmap);

                        imageUri = getImageUri(PhotoEditToolsActivity.this,saveBitmap);


                        // createChatListItem(usernameChattingWith,userphotoUrl,currentUsername,currentUserPhoto);
                        Intent intent1 = new Intent(PhotoEditToolsActivity.this, SendMediaService.class);

                        intent1.putExtra("userChattingWithId", userChattingWithId);
                        intent1.putExtra("imageUri", imageUri.toString());
                        intent1.putExtra("usernameChattingWith", usernameChattingWith);
                        intent1.putExtra("userphotoUrl", userphotoUrl);
                        intent1.putExtra("currentUsername", currentUsername);
                        intent1.putExtra("currentUserPhoto", currentUserPhoto);
                        intent1.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                        intent1.putExtra("otherUserPublicKeyID", otherUserPublicKeyID);
                        intent1.putExtra("type", "photo");


                        startService(intent1);

                        //  uploadImage();

                        //startActivity(new Intent(PhotoEditToolsActivity.this,PhotoTreatmentActivity.class));

                        finish();


                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

            }
        });



       /* byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/





        click_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // openCamera();
                finish();
            }
        });


        browse_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFromGallery();

            }
        });

        save_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToGalary();

            }
        });


        //
        //  img_preview = findViewById(R.id.image_preview);
        /*tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);*/


        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_height = displayMetrics.heightPixels;
        device_width = displayMetrics.widthPixels;
*/






        btn_filters_list = findViewById(R.id.btn_filters_list);
        btn_edit = findViewById(R.id.btn_edit);
        btn_brush = findViewById(R.id.btn_brush);
        btn_emoji = findViewById(R.id.btn_emoji);
        btn_add_text = findViewById(R.id.btn_add_text);
        btn_add_image = findViewById(R.id.btn_add_image);
        btn_crop = findViewById(R.id.btn_crop);



        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(filtersListFragment != null)
                {
                    filtersListFragment.show(getSupportFragmentManager(),filtersListFragment.getTag());
                }
                else
                {
                    FiltersListFragment filtersListFragment = FiltersListFragment.getInstance(null);
                    filtersListFragment.setListener(PhotoEditToolsActivity.this);
                    filtersListFragment.show(getSupportFragmentManager(),filtersListFragment.getTag());
                }


            }
        });



        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListener(PhotoEditToolsActivity.this);
                editImageFragment.show(getSupportFragmentManager(),editImageFragment.getTag());
            }
        });



        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enable brush mode

                photoEditor.setBrushDrawingMode(true);

                brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(PhotoEditToolsActivity.this);
                brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());

            }
        });

        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListener(PhotoEditToolsActivity.this);
                emojiFragment.show(getSupportFragmentManager(),emojiFragment.getTag());

            }
        });


        btn_add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTextFragment addTextFragment = AddTextFragment.getInstance();
                addTextFragment.setListener(PhotoEditToolsActivity.this);
                addTextFragment.show(getSupportFragmentManager(),addTextFragment.getTag());
            }
        });


        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageToPicture();
            }
        });


        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image_selected_uri = getImageUri(PhotoEditToolsActivity.this,finalBitmap);

                startCrop(image_selected_uri);
            }
        });





        coordinatorLayout = findViewById(R.id.coordinator);


        loadImage();


        //  setupViewPager(/*viewPager*/);


        // tabLayout.setupWithViewPager(viewPager);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        // String pathAuidio = MediaStore.Audio.Media.CONTENT_TYPE(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getExtension(Uri uri)
    {


        ContentResolver contentResolver = PhotoEditToolsActivity.this.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    private void uploadImage()
    {

        /*final ProgressDialog progressDialog = new ProgressDialog(PhotoEditToolsActivity.this );

        progressDialog.setMessage("Uploading");
        progressDialog.show();*/
        if(imageUri!=null)
        {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }




                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if(task.isSuccessful())
                    {

                        Uri downloadUri = task.getResult();

                        String mUri = downloadUri.toString();


                        databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                                .child(FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid())
                                .child(userChattingWithId);

                        receiverReference = FirebaseDatabase.getInstance().getReference("Messages")
                                .child(userChattingWithId)
                                .child(FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid());

                        final String mKey = databaseReference.push().getKey();


                        final HashMap<String,  Object> sendMessageHash = new HashMap<>();
                        sendMessageHash.put("senderId",FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid());
                        sendMessageHash.put("receiverId",userChattingWithId);
                        sendMessageHash.put("messageId",mKey);
                        sendMessageHash.put("messageText","");
                        sendMessageHash.put("sentStatus","sending");
                        sendMessageHash.put("seen", "notseen");
                        sendMessageHash.put("photourl",mUri);
                        sendMessageHash.put("audiourl", "none");
                        sendMessageHash.put("videourl", "none");
                        sendMessageHash.put("gotReplyID","none");
                        sendMessageHash.put("replyTag", false);
                        sendMessageHash.put("replyID","none");
                        sendMessageHash.put("senderReplyMessage","none");
                        sendMessageHash.put("ifMessageTwo",false);
                        sendMessageHash.put("messageTextTwo","");
                        sendMessageHash.put("ifMessageThree",false);
                        sendMessageHash.put("messageTextThree","");
                        sendMessageHash.put("showReplyMsg",false);
                        sendMessageHash.put("replyMsg"," ");
                        sendMessageHash.put("showGotReplyMsg",false);
                        sendMessageHash.put("gotReplyMsg"," ");
                        databaseReference.child(mKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sentStatus", "sent");
                                hashMap.put("messageId", mKey);

                                databaseReference.child(mKey).updateChildren(hashMap);
                            }
                        });
                        receiverReference.child(mKey).updateChildren(sendMessageHash);

                        createChatListItem(usernameChattingWith,userphotoUrl,currentUsername,currentUserPhoto);
                       // progressDialog.dismiss();


                    }
                    else
                    {
                        Toast.makeText(PhotoEditToolsActivity.this,"Uploading failed" ,Toast.LENGTH_SHORT).show();
                      //  progressDialog.dismiss();
                    }

                }
            });


        }
        else
        {
            Toast.makeText(PhotoEditToolsActivity.this,"No Image selected",Toast.LENGTH_SHORT ).show();
        }


    }
    public void createChatListItem(final String usernameUserChattingWith,final String userChattingWith_photo,final String currentUserName,final String currentUserPhoto)
    {

        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();


        final HashMap<String, Object> timeStampHash = new HashMap<>();
        timeStampHash.put("timeStamp", ts);
        timeStampHash.put("id", userChattingWithId);
        timeStampHash.put("username", usernameUserChattingWith);
        timeStampHash.put("photo", userChattingWith_photo);
        timeStampHash.put("seen", "notseen");
        timeStampHash.put("chatPending", false);
        senderChatCreateRef.updateChildren(timeStampHash);

        HashMap<String,Object> timeStampHashReceiver = new HashMap<>();

        timeStampHashReceiver.put("timeStamp", ts);
        timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        timeStampHashReceiver.put("username", currentUserName);
        timeStampHashReceiver.put("photo", currentUserPhoto);
        timeStampHashReceiver.put("seen", "notseen");
        timeStampHashReceiver.put("chatPending",true);
        receiverChatCreateRef.updateChildren(timeStampHashReceiver);

    }

    private void startCrop(Uri uri) {





        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();


        UCrop uCrop = UCrop.of(uri,Uri.fromFile( new File(getCacheDir(),destinationFileName)));





        /**
         * Use the code below for a square image selection i.e. for profile pictures
         * uCrop.withAspectRatio(1.0f, 1.0f);**/

        uCrop.start(PhotoEditToolsActivity.this);


    }

    private void addImageToPicture() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                        {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_INSERT_IMAGE);
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        Toast.makeText(PhotoEditToolsActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    public void loadImage() {


        originalBitmap = BitmapHelper.getInstance().getBitmap();

        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);


        // img_preview.setImageBitmap(originalBitmap);
        photoEditorView.getSource().setImageBitmap(originalBitmap);

    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void setupViewPager(/*ViewPager viewPager*/) {

        //    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);


        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

/*
        adapter.addFragment(filtersListFragment,"Filters");
        adapter.addFragment(editImageFragment, "EditImageFragment");


        viewPager.setAdapter(adapter);*/


    }

    @Override
    public void onBrightnessChanged(int brightness) {

        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        //  img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));


    }

    @Override
    public void onSaturationChanged(float saturation) {

        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        // img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onContrastChanged(float contrast) {


        contrastFinal = contrast;

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        // img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {


        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();


        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        finalBitmap = myFilter.processFilter(bitmap);

    }

    @Override
    public void onFilterSelected(Filter filter) {

        //  resetControl();

        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        //  img_preview.setImageBitmap(filter.processFilter(filteredBitmap));
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));

        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);


    }

    private void resetControl() {
        if(editImageFragment != null)
            editImageFragment.resetControls();

        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_main, menu);

         return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         int id = item.getItemId();

         if(id == R.id.action_open)
         {
             openFromGallery();
             return true;
         }
        else if(id == R.id.action_save)
        {
            saveImageToGalary();
            return true;
        }

        else if(id== R.id.action_camera)
        {

            openCamera();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
*/

    private void openCamera() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                        {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE,"New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
                            values.put(MediaStore.Images.Media.ORIENTATION, "Orientation");
                            image_selected_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_selected_uri);
                            startActivityForResult(cameraIntent,CAMERA_REQUEST);

                        }

                        else
                        {

                            Toast.makeText(PhotoEditToolsActivity.this,"Permission Denied", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void saveImageToGalary() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                        {


                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {


                                    photoEditorView.getSource().setImageBitmap(saveBitmap);



                                    final String path = BitmapUtils.insertImage(getContentResolver()
                                            ,saveBitmap
                                            , System.currentTimeMillis()+"_profile.jpg"
                                            ,null,0);


                                    if(!TextUtils.isEmpty(path))
                                    {
                                        Snackbar snackbar = Snackbar.make(relativeLayout,"Image Saved to gallery",Snackbar.LENGTH_LONG)
                                                .setAction("OPEN", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        openImage(path);

                                                    }
                                                });
                                        snackbar.show();
                                    }

                                    else
                                    {
                                        Snackbar snackbar = Snackbar.make(relativeLayout,"Unable to save Image!",Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }

                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });

                            /*final String path = BitmapUtils.insertImage(getContentResolver()
                                    ,finalBitmap
                                    , System.currentTimeMillis()+"_profile.jpg"
                            ,null);


                            if(!TextUtils.isEmpty(path))
                            {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout,"Image Saved to gallery",Snackbar.LENGTH_LONG)
                                        .setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                openImage(path);

                                            }
                                        });
                                snackbar.show();
                            }

                            else
                            {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout,"Unable to save Image!",Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }*/

                        }

                        else
                        {

                            Toast.makeText(PhotoEditToolsActivity.this,"Permission Denied", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void openImage(String path) {


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);

    }

    private void openFromGallery() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted())
                        {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");

                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        }
                        else
                        {
                            Toast.makeText(PhotoEditToolsActivity.this,"Permission Denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode==PERMISSION_PICK_IMAGE) {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);




                image_selected_uri = data.getData();


                //clear bitmap memory

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();


                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                //   img_preview.setImageBitmap(originalBitmap);
                photoEditorView.getSource().setImageBitmap(originalBitmap);

                bitmap.recycle();

                //Render selected img thumbnail

                /*filtersListFragment.displayThumbnail(originalBitmap);*/

                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);
            }

            else if(requestCode==PERMISSION_INSERT_IMAGE) {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 200, 200);

                photoEditor.addImage(bitmap);
            }

            else if(requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }

            else if(requestCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }

            else if(requestCode == CAMERA_REQUEST )
            {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, image_selected_uri,800,800);

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();



                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                //   img_preview.setImageBitmap(originalBitmap);
                photoEditorView.getSource().setImageBitmap(bitmap);

                //  bitmap.recycle();

                //Render selected img thumbnail

//                filtersListFragment.displayThumbnail(originalBitmap);

                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);
            }

        }
    }

    private void handleCropError(Intent data) {

        final Throwable cropError = UCrop.getError(data);

        if(cropError!=null)
        {
            Toast.makeText(PhotoEditToolsActivity.this, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(PhotoEditToolsActivity.this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }


    }

    private void handleCropResult(Intent data) {

        final Uri resultUri = UCrop.getOutput(data);
        if(resultUri != null)
        {
            photoEditorView.getSource().setImageURI(resultUri);


            Bitmap bitmap = ((BitmapDrawable) photoEditorView.getSource().getDrawable()).getBitmap();

            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);

            filteredBitmap = originalBitmap;

            finalBitmap = originalBitmap;

            // filtersListFragment.displayThumbnail(originalBitmap);

            filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
            filtersListFragment.setListener(this);

        }
        else
        {
            Toast.makeText(this, "Cannot retrieve crop image",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) { photoEditor.setOpacity(opacity); }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
        brushFragment.dismiss();
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {

        if(isEraser)
            photoEditor.brushEraser();
        else
            photoEditor.setBrushDrawingMode(true);

    }

    @Override
    public void onEmojiSelected(String emoji) {

        photoEditor.addEmoji(emoji);
        emojiFragment.dismiss();

    }

    @Override
    public void onAddTextButtonClick(String text, int color) {

        photoEditor.addText(text, color);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PhotoEditToolsActivity.this,CameraActivity.class));
        finish();
    }
}

