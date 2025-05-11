package com.example.splitexpense.ui.expenses;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.User;
import com.example.splitexpense.viewmodel.ExpenseViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateExpenseActivity extends AppCompatActivity {

    private TextInputEditText titleInput;
    private TextInputEditText amountInput;
    private TextInputEditText descriptionInput;
    private Spinner categorySpinner;
    private Button dateButton;
    private RecyclerView splitFriendsRecyclerView;
    private RadioGroup splitMethodRadioGroup;
    private RadioButton splitEquallyRadio;
    private RadioButton splitUnequallyRadio;
    private Button createExpenseButton;
    
    private ExpenseViewModel expenseViewModel;
    private SplitFriendAdapter splitFriendAdapter;
    
    private Calendar selectedDate = Calendar.getInstance();
    private List<User> friends = new ArrayList<>();
    private Map<String, Double> splitAmounts = new HashMap<>();
    
    private static final String[] EXPENSE_CATEGORIES = {
            "Food", "Transportation", "Housing", "Entertainment", "Utilities", "Shopping", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);
        
        // Initialize ViewModel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        
        // Initialize UI components
        titleInput = findViewById(R.id.expense_title_input);
        amountInput = findViewById(R.id.expense_amount_input);
        descriptionInput = findViewById(R.id.expense_description_input);
        categorySpinner = findViewById(R.id.expense_category_spinner);
        dateButton = findViewById(R.id.expense_date_button);
        splitFriendsRecyclerView = findViewById(R.id.split_friends_recycler_view);
        splitMethodRadioGroup = findViewById(R.id.split_method_radio_group);
        splitEquallyRadio = findViewById(R.id.split_equally_radio);
        splitUnequallyRadio = findViewById(R.id.split_unequally_radio);
        createExpenseButton = findViewById(R.id.create_expense_button);
        
        // Set up category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, EXPENSE_CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        
        // Set up date button
        updateDateButtonText();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        
        // Set up split method radio group
        splitMethodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateSplitMethodUI();
            }
        });
        
        // Set up RecyclerView for friends
        splitFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        splitFriendAdapter = new SplitFriendAdapter(friends, new SplitFriendAdapter.OnSplitAmountChangedListener() {
            @Override
            public void onSplitAmountChanged(String username, boolean isSelected, double amount) {
                if (isSelected) {
                    splitAmounts.put(username, amount);
                } else {
                    splitAmounts.remove(username);
                }
            }
        });
        splitFriendsRecyclerView.setAdapter(splitFriendAdapter);
        
        // Load friends for splitting
        loadFriendsForSplitting();
        
        // Set up create expense button
        createExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExpense();
            }
        });
        
        // Observe expense action results
        expenseViewModel.getExpenseActionResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    Toast.makeText(CreateExpenseActivity.this, result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Expense created successfully")) {
                        finish();
                    }
                }
            }
        });
    }
    
    private void loadFriendsForSplitting() {
        expenseViewModel.getFriendsForExpenseSplitting().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> friendsList) {
                if (friendsList != null) {
                    friends.clear();
                    friends.addAll(friendsList);
                    splitFriendAdapter.notifyDataSetChanged();
                    updateSplitMethodUI();
                }
            }
        });
    }
    
    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        dateButton.setText(dateFormat.format(selectedDate.getTime()));
    }
    
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateButtonText();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void updateSplitMethodUI() {
        boolean isEqualSplit = splitEquallyRadio.isChecked();
        splitFriendAdapter.setSplitMode(isEqualSplit ? SplitFriendAdapter.SPLIT_EQUALLY : SplitFriendAdapter.SPLIT_UNEQUALLY);
        
        // If equal split, calculate equal amounts
        if (isEqualSplit && !friends.isEmpty()) {
            try {
                double totalAmount = Double.parseDouble(amountInput.getText().toString());
                int selectedFriendsCount = 0;
                
                // Count selected friends
                for (Map.Entry<String, Double> entry : splitAmounts.entrySet()) {
                    if (entry.getValue() > 0) {
                        selectedFriendsCount++;
                    }
                }
                
                // Add 1 for the current user
                selectedFriendsCount++;
                
                if (selectedFriendsCount > 0) {
                    double equalAmount = totalAmount / selectedFriendsCount;
                    
                    // Update split amounts
                    for (String username : splitAmounts.keySet()) {
                        splitAmounts.put(username, equalAmount);
                    }
                    
                    splitFriendAdapter.updateEqualAmount(equalAmount);
                }
            } catch (NumberFormatException e) {
                // Handle invalid amount
            }
        }
    }
    
    private void createExpense() {
        String title = titleInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        long date = selectedDate.getTimeInMillis();
        
        // Validate input
        if (title.isEmpty()) {
            titleInput.setError("Title cannot be empty");
            return;
        }
        
        if (amountStr.isEmpty()) {
            amountInput.setError("Amount cannot be empty");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                amountInput.setError("Amount must be greater than zero");
                return;
            }
        } catch (NumberFormatException e) {
            amountInput.setError("Invalid amount");
            return;
        }
        
        // Validate splits
        if (splitAmounts.isEmpty()) {
            Toast.makeText(this, "Please select at least one friend to split with", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create expense
        expenseViewModel.createExpense(title, description, amount, category, date, splitAmounts);
    }
}
