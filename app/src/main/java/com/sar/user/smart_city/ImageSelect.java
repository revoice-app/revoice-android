package com.sar.user.smart_city;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ImageSelect extends AppCompatActivity implements DeletePostImageListener, AddImageListener {

    private static final int FILE_REQUEST_CODE = 20;

    private AppCompatImageView ivBack;
    private AppCompatTextView tvPost;
    private RecyclerView imageRV;
    private static ObjectMapper serializationObjMapper;

    private Context mContext;
    private PreferenceManager mPreferenceManager;
    private List<UploadImageData> mLocalImageList;
    private LearnDragAdapter mLearnDragAdapter;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private Bitmap bitmap;
    private String mobileNumber;
    private MaterialButton saveBtn;
    private String imageFileName;
    private static ProgressDialog progress;
    private AppCompatImageView uploadImage;
    private static String text = "";
    private static String textSent = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_image);
        initViews();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(imageRV);
        text=getIntent().getStringExtra("text");
        textSent=getIntent().getStringExtra("textSent");

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

        saveBtn.setOnClickListener(view -> {
            try {
                if(mLocalImageList.size()>0) {
                    showLoader(this);
                    uploadImages(convertToString(bitmap));
                }
                else
                {
                    Intent intent=new Intent(ImageSelect.this,FinalAct.class);
                    intent.putExtra("text",text);
                    byte[] b=new byte[]{};
                    intent.putExtra("image",b);
                    intent.putExtra("class","None detected: Sorry we could not detect any hampering. Kindly try with a different image.");
                    intent.putExtra("textSent",textSent);
                    startActivity(intent);
                }

                } catch (IOException e) {
                e.printStackTrace();
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
        imageRV.setLayoutManager(new GridLayoutManager(this, 1));

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
                                            .setMaxSelection(1)
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
                    saveBtn.setText("Save & Next");
                    Uri path = files.get(0).getUri();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
//                        uploadImages(convertToString(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


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
                        // uploadImage(convertToString(bitmap));
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



    private void uploadImages(String imageBytes) throws IOException {
        final String UL = "https://smart-city-reviews-cv-server.herokuapp.com/api/test";

        RequestQueue queue = Volley.newRequestQueue(this);

//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(UL, null, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                VolleyLog.wtf(response.toString());
//            }
//        }, new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.wtf(error.getMessage(), "utf-8");
//            }
//        }){
//
//            @Override
//            protected Map<String,String> getParams(){
//            Map<String,String> params = new HashMap<String, String>();
//            params.put("image","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");
//            return params;
//        }};
//        queue.add(jsonObjectRequest);

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("image", imageBytes);
        Log.d("kaka4",imageBytes);

        JsonObjectRequest req = new JsonObjectRequest(UL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.d("kaka", response.toString());
                            hideLoader(ImageSelect.this);
                            Intent intent=new Intent(ImageSelect.this,FinalAct.class);
                            intent.putExtra("text",text);
                            Log.d("pop",textSent);
                            intent.putExtra("textSent",textSent);

                            Gson gson = new Gson();
                            JsonParser parser = new JsonParser();
                            // response will be the json String

                            ResponseData emp = gson.fromJson(response.toString(), ResponseData.class);
                            String image = emp.getImage();
                            Bitmap de=null;
                            if(image.length()>0) {
                                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                                intent.putExtra("image",decodedString);
                            }
                            if(emp.getClass_text().equals(""))
                            {
                                emp.setClass_text("None detected: Sorry we could not detect any hampering. Kindly try with a different image.");
                            }
                            else
                            {
                                emp.setClass_text("We have notified the PWD Department as we detected "+ emp.getClass_text() +" in your captured image.");
                            }
                            intent.putExtra("class",emp.getClass_text()+System.lineSeparator());

                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ImageSelect.this, "Please try again", Toast.LENGTH_SHORT).show();
                            hideLoader(ImageSelect.this);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(ImageSelect.this,"Server Error", Toast.LENGTH_SHORT).show();
                hideLoader(ImageSelect.this);

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// add the request object to the queue to be executed
//        ApplicationController.getInstance().addToRequestQueue(req);
        queue.add(req);
    }

    public static String getFileToByte(String filePath) {
        Bitmap bmp = null;
        ByteArrayOutputStream bos = null;
        byte[] bt = null;
        String encodeString = null;
        try {
            bmp = BitmapFactory.decodeFile(filePath);
            bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bt = bos.toByteArray();
            encodeString = Base64.encodeToString(bt, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeString;
    }

    private String convertToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return  imageString;
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        this.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] imgByte = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    private static ObjectMapper getSerializationObjectMapper() {
        if (null == serializationObjMapper) {
            serializationObjMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return serializationObjMapper;
    }

    /**
     * Method to hide loader
     *
     * @param context the context
     */
    public static void showLoader(Context context) {
        hideLoader(context);
        if (null != context && !(((Activity) context).isFinishing())) {
            //show dialog
            progress = ProgressDialog.show(context, null, null, false, false);
            progress.setContentView(R.layout.common_loader);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.show();
        }
    }
    public static void hideLoader(Context context) {
        if (null != progress && progress.isShowing() && !(((Activity) context).isFinishing())) {
            progress.hide();
            progress.dismiss();
        }
        progress = null;
    }

}