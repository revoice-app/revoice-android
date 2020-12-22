package com.sar.user.smart_city;

import com.jaiselrahman.filepicker.model.MediaFile;

public class UploadImageData {

    private String mImageUrl;
    private MediaFile mediaFile;

    public UploadImageData(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public UploadImageData(MediaFile mediaFile, String mImageUrl) {
        this.mediaFile = mediaFile;
        this.mImageUrl = mImageUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }
}
