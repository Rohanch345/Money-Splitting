package com.example.splitexpense.ui.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.Expense;
import com.example.splitexpense.viewmodel.ExpenseViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExpensesFragment extends Fragment {

    private RecyclerView expensesRecyclerView;
    private FloatingActionButton addExpenseFab;
    private FloatingActionButton addPaymentFab;
    
    private ExpenseViewModel expenseViewModel;
    private ExpenseAdapter expenseAdapter;
    
    private List<Expense> expenses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        
        // Initialize ViewModel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        
        // Initialize UI components
        expensesRecyclerView = view.findViewById(R.id.expenses_recycler_view);
        addExpenseFab = view.findViewById(R.id.add_expense_fab);
        addPaymentFab = view.findViewById(R.id.add_payment_fab);
        
        // Set up RecyclerView
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseAdapter = new ExpenseAdapter(expenses, new ExpenseAdapter.OnExpenseActionListener() {
            @Override
            public void onViewExpenseDetails(long expenseId) {
                // Navigate to expense details activity
                Intent intent = new Intent(getActivity(), ExpenseDetailsActivity.class);
                intent.putExtra("expense_id", expenseId);
                startActivity(intent);
            }
        });
        expensesRecyclerView.setAdapter(expenseAdapter);
        
        // Set up FAB click listeners
        addExpenseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to create expense activity
                Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
                startActivity(intent);
            }
        });
        
        addPaymentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to record payment activity
                Intent intent = new Intent(getActivity(), RecordPaymentActivity.class);
                startActivity(intent);
            }
        });
        
        // Observe expense action results
        expenseViewModel.getExpenseActionResult().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Load expenses
        loadExpenses();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Refresh expenses when returning to fragment
        loadExpenses();
    }
    
    private void loadExpenses() {
        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenseList) {
                if (expenseList != null) {
                    expenses.clear();
                    expenses.addAll(expenseList);
                    expenseAdapter.notifyDataSetChanged();
                    
                    // Update UI based on whether there are expenses
                    if (expenses.isEmpty()) {
                        // Show empty state
                        view.findViewById(R.id.no_expenses_text).setVisibility(View.VISIBLE);
                        expensesRecyclerView.setVisibility(View.GONE);
                    } else {
                        // Show expenses
                        view.findViewById(R.id.no_expenses_text).setVisibility(View.GONE);
                        expensesRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
