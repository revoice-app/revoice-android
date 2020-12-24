package com.sar.user.smart_city;

class ModelReview {


        private String image;
        private String text;
        private String textClass;
        private String location;
        private String date;
        private String textSent;

    public String getTextSent() {
        return textSent;
    }

    public void setTextSent(String textSent) {
        this.textSent = textSent;
    }



    public String getImage() {
        return image;
    }


    public ModelReview(String text,String image, String textClass, String location, String date, String textSent) {
        this.image = image;
        this.text = text;
        this.textClass = textClass;
        this.location = location;
        this.date = date;
        this.textSent = textSent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ModelReview(String text, String image, String textClass, String location, String date) {
        this.image = image;
        this.text = text;
        this.textClass = textClass;
        this.location = location;
        this.date = date;
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
