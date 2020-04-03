package com.vuducminh.nicefood.model;

import java.util.Map;

public class CommentModel {
    private float ratingValue;
    private String comment,name,uid;
    private Map<String,Object> commentTimeStamp;

    public CommentModel(float ratingValue, String comment, String name, String uid, Map<String, Object> commentTimeStamp) {
        this.ratingValue = ratingValue;
        this.comment = comment;
        this.name = name;
        this.uid = uid;
        this.commentTimeStamp = commentTimeStamp;
    }

    public CommentModel() {
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> getCommentTimeStamp() {
        return commentTimeStamp;
    }

    public void setCommentTimeStamp(Map<String, Object> commentTimeStamp) {
        this.commentTimeStamp = commentTimeStamp;
    }
}
