package com.example.splitexpense.ui.expenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.Expense;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;
    private OnExpenseActionListener listener;
    
    public interface OnExpenseActionListener {
        void onViewExpenseDetails(long expenseId);
    }
    
    public ExpenseAdapter(List<Expense> expenses, OnExpenseActionListener listener) {
        this.expenses = expenses;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        
        holder.expenseTitle.setText(expense.getTitle());
        holder.expenseAmount.setText(String.format("$%.2f", expense.getAmount()));
        
        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(expense.getDate()));
        holder.expenseDate.setText(formattedDate);
        
        holder.expenseCategory.setText(expense.getCategory());
        holder.expenseCreator.setText("Created by: " + expense.getCreatedBy());
        
        // Set split info (placeholder - actual count would come from database)
        holder.expenseSplitInfo.setText("Split with friends");
        
        // Set up details button
        holder.viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewExpenseDetails(expense.getId());
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return expenses.size();
    }
    
    public void updateExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }
    
    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView expenseTitle;
        private TextView expenseAmount;
        private TextView expenseDate;
        private TextView expenseCategory;
        private TextView expenseCreator;
        private TextView expenseSplitInfo;
        private Button viewDetailsButton;
        
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseTitle = itemView.findViewById(R.id.expense_title);
            expenseAmount = itemView.findViewById(R.id.expense_amount);
            expenseDate = itemView.findViewById(R.id.expense_date);
            expenseCategory = itemView.findViewById(R.id.expense_category);
            expenseCreator = itemView.findViewById(R.id.expense_creator);
            expenseSplitInfo = itemView.findViewById(R.id.expense_split_info);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }
    }
}
