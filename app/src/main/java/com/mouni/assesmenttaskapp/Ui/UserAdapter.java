package com.mouni.assesmenttaskapp.Ui;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

        if (user.getLocalImagePath() != null && !user.getLocalImagePath().isEmpty()) {
            // Load local image
            holder.avatarImageView.setImageURI(Uri.parse(user.getLocalImagePath()));
        } else {
            Glide.with(context)
                    .load(user.getAvatar())
                    .into(holder.avatarImageView);
        }


        // Set click listener for image upload
        holder.uploadIcon.setOnClickListener(v -> {
            // Launch ImageUploadActivity for this user
            Intent intent = new Intent(context, ImageUploadActivity.class);
            intent.putExtra("user_id", user.getId());
            ((Activity) context).startActivityForResult(intent, MainActivity.REQUEST_IMAGE_UPLOAD);
        });

    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateUserImage(int userId, String localImagePath) {
        for (User user : userList) {
            if (user.getId() == userId) {
                user.setLocalImagePath(localImagePath);
                break;
            }
        }
        notifyDataSetChanged();
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

