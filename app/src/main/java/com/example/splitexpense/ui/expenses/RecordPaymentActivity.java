package com.example.splitexpense.ui.expenses;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.User;
import com.example.splitexpense.viewmodel.PaymentViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecordPaymentActivity extends AppCompatActivity {

    private Spinner paymentToSpinner;
    private TextInputEditText amountInput;
    private TextInputEditText descriptionInput;
    private Button dateButton;
    private Button recordPaymentButton;
    
    private PaymentViewModel paymentViewModel;
    private Calendar selectedDate = Calendar.getInstance();
    private List<User> friends = new ArrayList<>();
    private List<String> friendUsernames = new ArrayList<>();
    private String selectedFriendUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_payment);
        
        // Initialize ViewModel
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        
        // Initialize UI components
        paymentToSpinner = findViewById(R.id.payment_to_spinner);
        amountInput = findViewById(R.id.payment_amount_input);
        descriptionInput = findViewById(R.id.payment_description_input);
        dateButton = findViewById(R.id.payment_date_button);
        recordPaymentButton = findViewById(R.id.record_payment_button);
        
        // Set up date button
        updateDateButtonText();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        
        // Load friends for payment
        loadFriendsForPayment();
        
        // Set up record payment button
        recordPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordPayment();
            }
        });
        
        // Observe payment action results
        paymentViewModel.getPaymentActionResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    Toast.makeText(RecordPaymentActivity.this, result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Payment recorded successfully")) {
                        finish();
                    }
                }
            }
        });
    }
    
    private void loadFriendsForPayment() {
        // This would typically come from the ExpenseViewModel's getFriendsForExpenseSplitting method
        // For simplicity, we're reusing that method here
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModelProvider.get(com.example.splitexpense.viewmodel.ExpenseViewModel.class)
            .getFriendsForExpenseSplitting().observe(this, new Observer<List<User>>() {
                @Override
                public void onChanged(List<User> friendsList) {
                    if (friendsList != null) {
                        friends.clear();
                        friendUsernames.clear();
                        
                        friends.addAll(friendsList);
                        
                        // Extract usernames for spinner
                        for (User friend : friends) {
                            friendUsernames.add(friend.getUsername());
                        }
                        
                        // Set up spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                RecordPaymentActivity.this,
                                android.R.layout.simple_spinner_item,
                                friendUsernames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        paymentToSpinner.setAdapter(adapter);
                        
                        // Set up spinner selection listener
                        paymentToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedFriendUsername = friendUsernames.get(position);
                            }
                            
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selectedFriendUsername = null;
                            }
                        });
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
    
    private void recordPayment() {
        String amountStr = amountInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        long date = selectedDate.getTimeInMillis();
        
        // Validate input
        if (selectedFriendUsername == null || selectedFriendUsername.isEmpty()) {
            Toast.makeText(this, "Please select a friend to pay", Toast.LENGTH_SHORT).show();
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
        
        // Record payment
        paymentViewModel.recordPayment(selectedFriendUsername, amount, description, date);
    }
}
