package com.vuducminh.nicefood.callback;

import com.vuducminh.nicefood.model.CommentModel;

import java.util.List;

public interface ICommentCallbackListener {
    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);
}
