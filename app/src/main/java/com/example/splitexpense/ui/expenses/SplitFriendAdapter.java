package com.example.splitexpense.ui.expenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class SplitFriendAdapter extends RecyclerView.Adapter<SplitFriendAdapter.SplitFriendViewHolder> {

    public static final int SPLIT_EQUALLY = 0;
    public static final int SPLIT_UNEQUALLY = 1;
    
    private List<User> friends;
    private OnSplitAmountChangedListener listener;
    private int splitMode = SPLIT_EQUALLY;
    private double equalAmount = 0.0;
    
    public interface OnSplitAmountChangedListener {
        void onSplitAmountChanged(String username, boolean isSelected, double amount);
    }
    
    public SplitFriendAdapter(List<User> friends, OnSplitAmountChangedListener listener) {
        this.friends = friends;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public SplitFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_split_friend, parent, false);
        return new SplitFriendViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SplitFriendViewHolder holder, int position) {
        User friend = friends.get(position);
        holder.friendName.setText(friend.getUsername());
        
        // Set up checkbox listener
        holder.friendCheckbox.setOnCheckedChangeListener(null); // Remove previous listener
        holder.friendCheckbox.setChecked(false);
        
        holder.friendCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (splitMode == SPLIT_UNEQUALLY) {
                        holder.friendAmountLayout.setVisibility(View.VISIBLE);
                        double amount = 0.0;
                        try {
                            String amountStr = holder.friendAmountInput.getText().toString();
                            if (!amountStr.isEmpty()) {
                                amount = Double.parseDouble(amountStr);
                            }
                        } catch (NumberFormatException e) {
                            // Use default 0.0
                        }
                        listener.onSplitAmountChanged(friend.getUsername(), true, amount);
                    } else {
                        // Equal split
                        listener.onSplitAmountChanged(friend.getUsername(), true, equalAmount);
                    }
                } else {
                    holder.friendAmountLayout.setVisibility(View.GONE);
                    listener.onSplitAmountChanged(friend.getUsername(), false, 0.0);
                }
            }
        });
        
        // Set up amount input
        holder.friendAmountInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && holder.friendCheckbox.isChecked()) {
                    try {
                        double amount = Double.parseDouble(holder.friendAmountInput.getText().toString());
                        listener.onSplitAmountChanged(friend.getUsername(), true, amount);
                    } catch (NumberFormatException e) {
                        holder.friendAmountInput.setError("Invalid amount");
                    }
                }
            }
        });
        
        // Update UI based on split mode
        if (splitMode == SPLIT_EQUALLY) {
            holder.friendAmountLayout.setVisibility(View.GONE);
            if (equalAmount > 0) {
                holder.friendAmountInput.setText(String.format("%.2f", equalAmount));
            }
        } else {
            holder.friendAmountLayout.setVisibility(holder.friendCheckbox.isChecked() ? View.VISIBLE : View.GONE);
        }
    }
    
    @Override
    public int getItemCount() {
        return friends.size();
    }
    
    public void setSplitMode(int splitMode) {
        this.splitMode = splitMode;
        notifyDataSetChanged();
    }
    
    public void updateEqualAmount(double amount) {
        this.equalAmount = amount;
        notifyDataSetChanged();
    }
    
    static class SplitFriendViewHolder extends RecyclerView.ViewHolder {
        private CheckBox friendCheckbox;
        private TextView friendName;
        private TextInputLayout friendAmountLayout;
        private TextInputEditText friendAmountInput;
        
        public SplitFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendCheckbox = itemView.findViewById(R.id.friend_checkbox);
            friendName = itemView.findViewById(R.id.friend_name);
            friendAmountLayout = itemView.findViewById(R.id.friend_amount_layout);
            friendAmountInput = itemView.findViewById(R.id.friend_amount_input);
        }
    }
}
