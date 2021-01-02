package com.sar.user.smart_city;

class ResponseData {
    private String image;
    private String Status;
    private Integer class_confidence;
    private String class_text;
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public ResponseData(String image) {
        this.image=image;
    }
    public String getStatus() {
        return Status;
    }
    public Integer getClass_confidence() {
        return class_confidence;
    }
    public void setClass_confidence(int class_confidence) {
        this.class_confidence = class_confidence;
    }
    public String getClass_text() {
        return class_text;
    }
    public void setClass_text(String class_text) {
        this.class_text = class_text;
    }
    public void setStatus(String status) {
        Status = status;
    }
}