package com.example.splitexpense.ui.friends;

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
import androidx.viewpager2.widget.ViewPager2;

import com.example.splitexpense.R;
import com.example.splitexpense.data.entity.Friendship;
import com.example.splitexpense.data.entity.User;
import com.example.splitexpense.viewmodel.FriendViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton addFriendFab;
    
    private FriendViewModel friendViewModel;
    private FriendsViewPagerAdapter viewPagerAdapter;
    
    private RecyclerView friendsRecyclerView;
    private RecyclerView requestsRecyclerView;
    private FriendAdapter friendAdapter;
    private FriendRequestAdapter requestAdapter;
    
    private List<User> friends = new ArrayList<>();
    private List<Friendship> friendRequests = new ArrayList<>();
    private List<User> requestUsers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        
        // Initialize ViewModel
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        
        // Initialize UI components
        tabLayout = view.findViewById(R.id.friends_tab_layout);
        viewPager = view.findViewById(R.id.friends_view_pager);
        addFriendFab = view.findViewById(R.id.add_friend_fab);
        
        // Set up ViewPager with adapter
        viewPagerAdapter = new FriendsViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        
        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Friends");
                    break;
                case 1:
                    tab.setText("Requests");
                    break;
            }
        }).attach();
        
        // Set up FAB click listener
        addFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to add friend activity
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });
        
        // Observe friend action results
        friendViewModel.getFriendActionResult().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        
        // Load friends and friend requests
        loadFriends();
        loadFriendRequests();
    }
    
    private void loadFriends() {
        friendViewModel.getAcceptedFriendships().observe(getViewLifecycleOwner(), new Observer<List<Friendship>>() {
            @Override
            public void onChanged(List<Friendship> friendships) {
                if (friendships != null) {
                    for (Friendship friendship : friendships) {
                        // Get friend user details
                        String friendUsername = friendship.getFriendUsername();
                        friendViewModel.getUserByUsername(friendUsername).observe(getViewLifecycleOwner(), new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                if (user != null && !friends.contains(user)) {
                                    friends.add(user);
                                    if (friendAdapter != null) {
                                        friendAdapter.updateFriends(friends);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    private void loadFriendRequests() {
        friendViewModel.getPendingFriendRequests().observe(getViewLifecycleOwner(), new Observer<List<Friendship>>() {
            @Override
            public void onChanged(List<Friendship> requests) {
                if (requests != null) {
                    friendRequests = requests;
                    for (Friendship request : requests) {
                        // Get requester user details
                        String requesterUsername = request.getUserUsername();
                        friendViewModel.getUserByUsername(requesterUsername).observe(getViewLifecycleOwner(), new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                if (user != null && !requestUsers.contains(user)) {
                                    requestUsers.add(user);
                                    if (requestAdapter != null) {
                                        requestAdapter.updateRequests(friendRequests, requestUsers);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    // Method to initialize the friends list tab
    public View createFriendsTab(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_friends_tab, container, false);
        
        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        friendAdapter = new FriendAdapter(friends, new FriendAdapter.OnFriendActionListener() {
            @Override
            public void onRemoveFriend(Friendship friendship) {
                friendViewModel.removeFriendship(friendship);
            }
        });
        
        friendsRecyclerView.setAdapter(friendAdapter);
        
        return view;
    }
    
    // Method to initialize the friend requests tab
    public View createRequestsTab(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_requests_tab, container, false);
        
        requestsRecyclerView = view.findViewById(R.id.requests_recycler_view);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        requestAdapter = new FriendRequestAdapter(friendRequests, requestUsers, new FriendRequestAdapter.OnRequestActionListener() {
            @Override
            public void onAcceptRequest(long friendshipId) {
                friendViewModel.acceptFriendRequest(friendshipId);
            }
            
            @Override
            public void onRejectRequest(long friendshipId) {
                friendViewModel.rejectFriendRequest(friendshipId);
            }
        });
        
        requestsRecyclerView.setAdapter(requestAdapter);
        
        return view;
    }
    
    // ViewPager adapter for the friends and requests tabs
    private class FriendsViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        
        public FriendsViewPagerAdapter(Fragment fragment) {
            super(fragment);
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FriendsTabFragment();
                case 1:
                    return new RequestsTabFragment();
                default:
                    return new FriendsTabFragment();
            }
        }
        
        @Override
        public int getItemCount() {
            return 2;
        }
    }
    
    // Fragment for the Friends tab
    public class FriendsTabFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return createFriendsTab(inflater, container);
        }
    }
    
    // Fragment for the Requests tab
    public class RequestsTabFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return createRequestsTab(inflater, container);
        }
    }
}
