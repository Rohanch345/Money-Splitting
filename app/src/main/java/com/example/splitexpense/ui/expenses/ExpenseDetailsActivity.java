package com.example.splitexpense.ui.expenses;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.Expense;
import com.example.splitexpense.data.entity.ExpenseSplit;
import com.example.splitexpense.data.entity.User;
import com.example.splitexpense.viewmodel.ExpenseViewModel;
import com.example.splitexpense.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseDetailsActivity extends AppCompatActivity {

    private TextView expenseTitleText;
    private TextView expenseAmountText;
    private TextView expenseDateText;
    private TextView expenseCategoryText;
    private TextView expenseDescriptionText;
    private TextView expenseCreatorText;
    private RecyclerView splitDetailsRecyclerView;
    
    private ExpenseViewModel expenseViewModel;
    private UserViewModel userViewModel;
    private ExpenseSplitAdapter splitAdapter;
    
    private long expenseId;
    private List<ExpenseSplit> expenseSplits = new ArrayList<>();
    private List<User> splitUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);
        
        // Get expense ID from intent
        expenseId = getIntent().getLongExtra("expense_id", -1);
        if (expenseId == -1) {
            Toast.makeText(this, "Invalid expense ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize ViewModels
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // Initialize UI components
        expenseTitleText = findViewById(R.id.expense_detail_title);
        expenseAmountText = findViewById(R.id.expense_detail_amount);
        expenseDateText = findViewById(R.id.expense_detail_date);
        expenseCategoryText = findViewById(R.id.expense_detail_category);
        expenseDescriptionText = findViewById(R.id.expense_detail_description);
        expenseCreatorText = findViewById(R.id.expense_detail_creator);
        splitDetailsRecyclerView = findViewById(R.id.split_details_recycler_view);
        
        // Set up RecyclerView
        splitDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        splitAdapter = new ExpenseSplitAdapter(expenseSplits, splitUsers);
        splitDetailsRecyclerView.setAdapter(splitAdapter);
        
        // Load expense details
        loadExpenseDetails();
        
        // Load expense splits
        loadExpenseSplits();
    }
    
    private void loadExpenseDetails() {
        expenseViewModel.getExpenseById(expenseId).observe(this, new Observer<Expense>() {
            @Override
            public void onChanged(Expense expense) {
                if (expense != null) {
                    // Set expense details
                    expenseTitleText.setText(expense.getTitle());
                    expenseAmountText.setText(String.format("$%.2f", expense.getAmount()));
                    
                    // Format date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(new Date(expense.getDate()));
                    expenseDateText.setText(formattedDate);
                    
                    expenseCategoryText.setText(expense.getCategory());
                    expenseDescriptionText.setText(expense.getDescription());
                    expenseCreatorText.setText("Created by: " + expense.getCreatedBy());
                }
            }
        });
    }
    
    private void loadExpenseSplits() {
        expenseViewModel.getExpenseSplits(expenseId).observe(this, new Observer<List<ExpenseSplit>>() {
            @Override
            public void onChanged(List<ExpenseSplit> splits) {
                if (splits != null) {
                    expenseSplits.clear();
                    expenseSplits.addAll(splits);
                    
                    // Load user details for each split
                    for (ExpenseSplit split : splits) {
                        String username = split.getUsername();
                        userViewModel.getUserByUsername(username).observe(ExpenseDetailsActivity.this, new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                if (user != null && !splitUsers.contains(user)) {
                                    splitUsers.add(user);
                                    splitAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
