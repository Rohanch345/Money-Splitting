package com.example.splitexpense.ui.friends;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.User;
import com.example.splitexpense.viewmodel.FriendViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {

    private TextInputEditText searchUsernameInput;
    private Button searchButton;
    private TextView searchResultsLabel;
    private RecyclerView searchResultsRecyclerView;
    private TextView noResultsText;
    
    private FriendViewModel friendViewModel;
    private UserSearchAdapter userSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        
        // Initialize ViewModel
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        
        // Initialize UI components
        searchUsernameInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_button);
        searchResultsLabel = findViewById(R.id.search_results_label);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);
        noResultsText = findViewById(R.id.no_results_text);
        
        // Set up RecyclerView
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userSearchAdapter = new UserSearchAdapter(new ArrayList<>(), new UserSearchAdapter.OnUserActionListener() {
            @Override
            public void onSendFriendRequest(String username) {
                friendViewModel.sendFriendRequest(username);
            }
        });
        searchResultsRecyclerView.setAdapter(userSearchAdapter);
        
        // Set up search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchUsernameInput.getText().toString().trim();
                if (!searchQuery.isEmpty()) {
                    performSearch(searchQuery);
                } else {
                    Toast.makeText(AddFriendActivity.this, "Please enter a username to search", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Observe friend action results
        friendViewModel.getFriendActionResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    Toast.makeText(AddFriendActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void performSearch(String searchQuery) {
        friendViewModel.searchUsers(searchQuery).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    searchResultsLabel.setVisibility(View.VISIBLE);
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                    noResultsText.setVisibility(View.GONE);
                    userSearchAdapter.updateUsers(users);
                } else {
                    searchResultsLabel.setVisibility(View.VISIBLE);
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    noResultsText.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
