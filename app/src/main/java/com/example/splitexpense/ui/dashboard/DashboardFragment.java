package com.example.splitexpense.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;

public class DashboardFragment extends Fragment {

    private TextView totalBalanceAmount;
    private TextView youOweAmount;
    private TextView owesYouAmount;
    private RecyclerView recentActivityRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        // Initialize UI components
        totalBalanceAmount = view.findViewById(R.id.total_balance_amount);
        youOweAmount = view.findViewById(R.id.you_owe_amount);
        owesYouAmount = view.findViewById(R.id.owes_you_amount);
        recentActivityRecyclerView = view.findViewById(R.id.recent_activity_recycler_view);
        
        // Set up RecyclerView
        recentActivityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // TODO: Set up adapter for recent activity when database is implemented
        
        // TODO: Load balance data from database
        // For now, just display placeholder values
        
        return view;
    }
}
