package com.sar.user.smart_city;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ImageSelect extends AppCompatActivity implements DeletePostImageListener, AddImageListener {
    private static final int FILE_REQUEST_CODE = 20;
    private AppCompatImageView ivBack;
    private AppCompatTextView tvPost;
    private RecyclerView imageRV;
    private Context mContext;
    private PreferenceManager mPreferenceManager;
    private List<UploadImageData> mLocalImageList;
    private LearnDragAdapter mLearnDragAdapter;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private Bitmap bitmap;
    private String mobileNumber;
    private MaterialButton saveBtn;
    private String imageFileName;
    private static String text;
    private AppCompatImageView uploadImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_image);
        initViews();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(imageRV);
        Intent intent=getIntent();
        text=intent.getStringExtra("text");
        mContext = this;
        mLocalImageList = new ArrayList<>();
        handleImageRecyclerView();
    }
    /**
     * @return boolean
     * @param: MotionEvent ev
     * @name: dispatchTouchEvent
     * @desc: dispatch keyboard on touch
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        uploadImage = findViewById(R.id.uploadImage);
        imageRV = findViewById(R.id.imageRV);
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ImageSelect.this,FinalAct.class);
                intent.putExtra("text",text);
                startActivity(intent);
            }
        });
        handleActions();
        handleAttachActions();
    }
    /**
     * @name: handleImageRecyclerView
     * @desc: handling Recycler view
     */
    private void handleImageRecyclerView() {
        mLearnDragAdapter = new LearnDragAdapter(mContext, mLocalImageList, this, this);
        imageRV.setLayoutManager(new GridLayoutManager(this, 3));
        imageRV.setHasFixedSize(true);
        imageRV.setAdapter(mLearnDragAdapter);
    }
    /**
     * @name: handleActions
     * @desc: handling various actions
     */
    private void handleActions() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSelect.this.finish();
            }
        });
    }
    /**
     * @name: handleAttachActions
     * @desc: select files from gallery
     */
    private void handleAttachActions() {
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(ImageSelect.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/jpeg");
                                    try {
                                        startActivityForResult(intent, 22);
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
//                                    Intent intent = new Intent(mContext, FilePickerActivity.class);
//                                    intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
//                                            .setCheckPermission(true)
//                                            .setSelectedMediaFiles(mediaFiles)
//                                            .setShowFiles(true)
//                                            .setShowImages(true)
//                                            .setShowAudios(false)
//                                            .setShowVideos(false)
//                                            .setIgnoreNoMedia(false)
//                                            .enableVideoCapture(false)
//                                            .enableImageCapture(true)
//                                            .setIgnoreHiddenFile(false)
//                                            .setMaxSelection(10)
//                                            .build());
//                                    startActivityForResult(intent, FILE_REQUEST_CODE);
//                                } else {
//                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permissiondenied), Toast.LENGTH_SHORT).show();
//                                }
                            }
                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                /*Intentionally left blank*/
                            }
                        }).onSameThread().check();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 22:
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream is = getContentResolver().openInputStream(data.getData());
//                        uploadImage(getBytes(is));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    @Override
    public void deleteImage(MediaFile file, int position) {
        mLocalImageList.remove(position);
        mLearnDragAdapter.notifyDataSetChanged();
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.DOWN | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPostion = viewHolder.getAdapterPosition();
            int toPostion = target.getAdapterPosition();
            Collections.swap(mLocalImageList, fromPostion, toPostion);
            imageRV.getAdapter().notifyItemMoved(fromPostion, toPostion);
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }
    };
    @Override
    public void addImage() {
        Intent intent = new Intent(mContext, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setSelectedMediaFiles(mediaFiles)
                .setShowFiles(true)
                .setShowImages(true)
                .setIgnoreNoMedia(false)
                .enableImageCapture(true)
                .setIgnoreHiddenFile(false)
                .setMaxSelection(1)
                .build());
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }
    @Override
    public void addContentView() {
        uploadImage.setVisibility(View.VISIBLE);
        imageRV.setVisibility(View.GONE);
    }
    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();
        int buffSize = 1024;
        byte[] buff = new byte[buffSize];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }
        return byteBuff.toByteArray();
    }
//    private void uploadImage(byte[] imageBytes) {
//        final String URL = "https://smart-city-reviews-cv-server.herokuapp.com/api/";
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        ImageUpload retrofitInterface = retrofit.create(ImageUpload.class);
//        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
//        Call<ResponseData> responseDataCall= (Call<ResponseData>) retrofitInterface.uploadImage(body);
//        responseDataCall.enqueue(new Callback<ResponseData>() {
//            @Override
//            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
//                Log.d("kak",response.body().getImage().toString());
//            }
//            @Override
//            public void onFailure(Call<ResponseData> call, Throwable t) {
//                Log.d("kak",t.getMessage().toString());
//            }
//        });
//    }
}