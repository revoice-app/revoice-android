package com.sar.user.smart_city;

class ModelReview {


        private String image;
        private String text;
        private String textClass;

    public String getImage() {
        return image;
    }

    public ModelReview(String text, String image, String textClass) {
        this.image = image;
        this.text = text;
        this.textClass = textClass;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextClass() {
        return textClass;
    }

    public void setTextClass(String textClass) {
        this.textClass = textClass;
    }
}
