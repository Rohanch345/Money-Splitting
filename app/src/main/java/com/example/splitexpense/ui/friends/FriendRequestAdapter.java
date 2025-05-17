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

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.RequestViewHolder> {

    private List<Friendship> friendRequests;
    private List<User> requestUsers;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAcceptRequest(long friendshipId);
        void onRejectRequest(long friendshipId);
    }

    public FriendRequestAdapter(List<Friendship> friendRequests, List<User> requestUsers, OnRequestActionListener listener) {
        this.friendRequests = friendRequests;
        this.requestUsers = requestUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new RequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Friendship currentRequest = friendRequests.get(position);
        User requestUser = null;
        
        // Find the corresponding user for this request
        for (User user : requestUsers) {
            if (user.getUsername().equals(currentRequest.getUserUsername())) {
                requestUser = user;
                break;
            }
        }
        
        if (requestUser != null) {
            holder.usernameText.setText(requestUser.getUsername());
            holder.emailText.setText(requestUser.getEmail());
        } else {
            holder.usernameText.setText(currentRequest.getUserUsername());
            holder.emailText.setText("Email not available");
        }
        
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAcceptRequest(currentRequest.getId());
            }
        });
        
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRejectRequest(currentRequest.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public void updateRequests(List<Friendship> friendRequests, List<User> requestUsers) {
        this.friendRequests = friendRequests;
        this.requestUsers = requestUsers;
        notifyDataSetChanged();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameText;
        private TextView emailText;
        private Button acceptButton;
        private Button rejectButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.request_username);
            emailText = itemView.findViewById(R.id.request_email);
            acceptButton = itemView.findViewById(R.id.accept_request_button);
            rejectButton = itemView.findViewById(R.id.reject_request_button);
        }
    }
}
