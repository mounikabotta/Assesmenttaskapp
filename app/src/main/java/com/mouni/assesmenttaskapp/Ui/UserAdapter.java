package com.mouni.assesmenttaskapp.Ui;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mouni.assesmenttaskapp.Data.User;
import com.mouni.assesmenttaskapp.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;
    private OnImageClickListener onImageClickListener;

    public UserAdapter(Context context, List<User> userList, OnImageClickListener onImageClickListener) {
        this.context = context;
        this.userList = userList;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        Log.d("User","First Name:" +user.getFirstName());
        holder.emailTextView.setText(user.getEmail());

        // Load avatar or local image using Glide
        if (user.getLocalImagePath() != null) {
            Glide.with(context)
                    .load(user.getLocalImagePath())
                    .placeholder(R.drawable.placeholder) // A placeholder image
                    .into(holder.avatarImageView);
        } else {
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.placeholder) // A placeholder image
                    .into(holder.avatarImageView);
        }

        // Set click listener for image upload
        holder.uploadIcon.setOnClickListener(v -> {
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void setUserList(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView;
        ImageView avatarImageView, uploadIcon;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            emailTextView = itemView.findViewById(R.id.text_view_email);
            avatarImageView = itemView.findViewById(R.id.image_view_avatar);
            uploadIcon = itemView.findViewById(R.id.image_view_upload_icon);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(User user);
    }
}

