package com.example.splitexpense.ui.expenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.ExpenseSplit;
import com.example.splitexpense.data.entity.User;

import java.util.List;

public class ExpenseSplitAdapter extends RecyclerView.Adapter<ExpenseSplitAdapter.ExpenseSplitViewHolder> {

    private List<ExpenseSplit> expenseSplits;
    private List<User> users;
    
    public ExpenseSplitAdapter(List<ExpenseSplit> expenseSplits, List<User> users) {
        this.expenseSplits = expenseSplits;
        this.users = users;
    }
    
    @NonNull
    @Override
    public ExpenseSplitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_split, parent, false);
        return new ExpenseSplitViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ExpenseSplitViewHolder holder, int position) {
        ExpenseSplit split = expenseSplits.get(position);
        
        // Find user for this split
        User user = null;
        for (User u : users) {
            if (u.getUsername().equals(split.getUsername())) {
                user = u;
                break;
            }
        }
        
        if (user != null) {
            holder.usernameText.setText(user.getUsername());
            holder.nameText.setText(user.getFullName());
        } else {
            holder.usernameText.setText(split.getUsername());
            holder.nameText.setText("");
        }
        
        holder.amountText.setText(String.format("$%.2f", split.getAmount()));
        holder.statusText.setText(split.isPaid() ? "Paid" : "Unpaid");
        
        // Set status text color based on payment status
        if (split.isPaid()) {
            holder.statusText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorSuccess));
        } else {
            holder.statusText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWarning));
        }
    }
    
    @Override
    public int getItemCount() {
        return expenseSplits.size();
    }
    
    static class ExpenseSplitViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameText;
        private TextView nameText;
        private TextView amountText;
        private TextView statusText;
        
        public ExpenseSplitViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.split_username);
            nameText = itemView.findViewById(R.id.split_name);
            amountText = itemView.findViewById(R.id.split_amount);
            statusText = itemView.findViewById(R.id.split_status);
        }
    }
}
