package com.vuducminh.nicefood.ui.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vuducminh.nicefood.model.CommentModel;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentModel>> mutableLiveDataCommentList;

    public CommentViewModel() {
        mutableLiveDataCommentList = new MutableLiveData<>();
    }

    public MutableLiveData<List<CommentModel>> getMutableLiveDataCommentList() {
        return mutableLiveDataCommentList;
    }

    public void setCommentList(List<CommentModel> commentList) {
        mutableLiveDataCommentList.setValue(commentList);
    }
}
