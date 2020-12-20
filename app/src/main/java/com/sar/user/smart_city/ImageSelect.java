package com.sar.user.smart_city;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private AppCompatImageView uploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_image);
        initViews();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(imageRV);

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
        tvPost = findViewById(R.id.tv_post);
        uploadImage = findViewById(R.id.uploadImage);
        imageRV = findViewById(R.id.imageRV);
        saveBtn = findViewById(R.id.saveBtn);

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
                                    Intent intent = new Intent(mContext, FilePickerActivity.class);
                                    intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                                            .setCheckPermission(true)
                                            .setSelectedMediaFiles(mediaFiles)
                                            .setShowFiles(true)
                                            .setShowImages(true)
                                            .setShowAudios(false)
                                            .setShowVideos(false)
                                            .setIgnoreNoMedia(false)
                                            .enableVideoCapture(false)
                                            .enableImageCapture(true)
                                            .setIgnoreHiddenFile(false)
                                            .setMaxSelection(10)
                                            .build());
                                    startActivityForResult(intent, FILE_REQUEST_CODE);
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permissiondenied), Toast.LENGTH_SHORT).show();
                                }
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
            case FILE_REQUEST_CODE:
                List<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);

                if (null != files) {

                    if (files.size() > 0) {
                        uploadImage.setVisibility(View.GONE);
                        imageRV.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < files.size(); i++) {
                        final MediaFile mediaFile = files.get(i);
                        final UploadImageData imageData = new UploadImageData(mediaFile);
                        mLocalImageList.add(imageData);
                        handleImageRecyclerView();
                        imageRV.scrollToPosition(mLocalImageList.size() - 1);
                    }

                } else {
                    Toast.makeText(ImageSelect.this, "Image not selected", Toast.LENGTH_SHORT).show();
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
                .setMaxSelection(10)
                .build());
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @Override
    public void addContentView() {
        uploadImage.setVisibility(View.VISIBLE);
        imageRV.setVisibility(View.GONE);
    }
}

