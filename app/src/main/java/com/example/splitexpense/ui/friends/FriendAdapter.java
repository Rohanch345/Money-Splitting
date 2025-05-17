package com.example.splitexpense.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.Friendship;
import com.example.splitexpense.data.entity.User;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<User> friends;
    private OnFriendActionListener listener;

    public interface OnFriendActionListener {
        void onRemoveFriend(Friendship friendship);
    }

    public FriendAdapter(List<User> friends, OnFriendActionListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User currentFriend = friends.get(position);
        holder.usernameText.setText(currentFriend.getUsername());
        holder.emailText.setText(currentFriend.getEmail());
        
        holder.removeFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a temporary friendship object for removal
                // The actual friendship will be retrieved from the database in the ViewModel
                Friendship friendship = new Friendship(
                        "currentUser", // This will be replaced in the ViewModel
                        currentFriend.getUsername(),
                        "ACCEPTED");
                listener.onRemoveFriend(friendship);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void updateFriends(List<User> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameText;
        private TextView emailText;
        private Button removeFriendButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.friend_username);
            emailText = itemView.findViewById(R.id.friend_email);
            removeFriendButton = itemView.findViewById(R.id.remove_friend_button);
        }
    }
}
