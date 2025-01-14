package com.example.blooddonor.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonor.R;
import com.example.blooddonor.domain.models.UserDTO;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private List<UserDTO> users;
    private final UserAdapter.OnLocationClickListener listener;

    public UserAdapter(List<UserDTO> users, UserAdapter.OnLocationClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        UserDTO user = users.get(position);
        holder.nameTextView.setText(user.getFullName());
        holder.typeTextView.setText(user.getRoleName());

        holder.itemView.setOnClickListener(v -> listener.onLocationClick(user));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(user.getId()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnLocationClickListener {
        void onLocationClick(UserDTO user);
        void onEditClick(UserDTO user);
        void onDeleteClick(int userId);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, typeTextView;
        ImageView btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.user_name);
            typeTextView = itemView.findViewById(R.id.user_type);
            btnEdit = itemView.findViewById(R.id.btn_edit_user);
            btnDelete = itemView.findViewById(R.id.btn_delete_user);
        }
    }
}
