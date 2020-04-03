package com.vuducminh.nicefood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vuducminh.nicefood.callback.IRecyclerClickListener;
import com.vuducminh.nicefood.eventbus.PopluarCategoryClick;
import com.vuducminh.nicefood.model.PopluarCategoryModel;
import com.vuducminh.nicefood.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyPopularCategoriesAdapter extends RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder>{

    Context context;
    List<PopluarCategoryModel> popluarCategoryModelList;

    public MyPopularCategoriesAdapter(Context context, List<PopluarCategoryModel> popluarCategoryModelList) {
        this.context = context;
        this.popluarCategoryModelList = popluarCategoryModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lauout_popular_categories_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(popluarCategoryModelList.get(position).getImage())
                .into(holder.category_image);
        holder.tv_category_name.setText(popluarCategoryModelList.get(position).getName());

        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                EventBus.getDefault().postSticky(new PopluarCategoryClick(popluarCategoryModelList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return popluarCategoryModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;

        @BindView(R.id.tv_category_name)
        TextView tv_category_name;
        @BindView(R.id.category_image)
        ImageView category_image;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v,getAdapterPosition());
        }
    }
}
