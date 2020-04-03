package com.vuducminh.nicefood.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vuducminh.nicefood.model.CommentModel;
import com.vuducminh.nicefood.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.MyViewHolder>{

    private Context context;
    private List<CommentModel> commentModelList;

    public MyCommentAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layour_comment_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Long timeStamp = Long.valueOf(commentModelList.get(position).getCommentTimeStamp().get("timeStamp").toString());
        holder.tv_comment_date.setText(DateUtils.getRelativeTimeSpanString(timeStamp));
        holder.tv_comment.setText(commentModelList.get(position).getComment());
        holder.tv_comment_name.setText(commentModelList.get(position).getName());
        holder.ratingBar.setRating(commentModelList.get(position).getRatingValue());
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Unbinder unbinder;

        @BindView(R.id.tv_comment_date)
        TextView tv_comment_date;
        @BindView(R.id.tv_comment)
        TextView tv_comment;
        @BindView(R.id.tv_comment_name)
        TextView tv_comment_name;
        @BindView(R.id.rating_bar)
        RatingBar ratingBar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
