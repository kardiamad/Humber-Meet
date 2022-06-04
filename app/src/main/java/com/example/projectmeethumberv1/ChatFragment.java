package com.example.projectmeethumberv1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;



public class ChatFragment extends Fragment {

    private FirebaseListAdapter<Group> adapter, newAdapter;
    private ListView listOfGroups;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, parent, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chat_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_chat_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextSubmit(String search) {
                return false;
            }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    displayAllGroups();
                    return false;
                }
                searchCommunity(newText);
                return false;
            }
        });


        searchView.setOnCloseListener(() -> {
            displayAllGroups();
            return false;
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void searchCommunity(String search) {
        Query query = FirebaseDatabase.getInstance().getReference("groups").orderByChild("name").equalTo(search);
        FirebaseListOptions<Group> options = new FirebaseListOptions.Builder<Group>()
                .setLayout(R.layout.group_item)
                .setQuery(query, Group.class)
                .setLifecycleOwner(this)
                .build();

        newAdapter = new FirebaseListAdapter<Group>(options) {
            @Override
            protected void populateView(View v, Group model, int position) {
                TextView gr_name;
                gr_name = (TextView) v.findViewById(R.id.group_name);
                gr_name.setText(model.getName());
            }
        };
        listOfGroups.setAdapter(newAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listOfGroups = view.findViewById(R.id.list_of_groups_chat);
        listOfGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MessagingActivity.class);
                long id = adapter.getItem(i).getId();
                String groupName = adapter.getItem(i).getName();
                intent.putExtra(Constants.groupIdParam, id);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
//                Log.d("myTag", "This is my message");
            }
        });
        displayAllGroups();
    }


    private void displayAllGroups() {
        Query query = FirebaseDatabase.getInstance().getReference("groups");
        FirebaseListOptions<Group> options = new FirebaseListOptions.Builder<Group>()
                .setLayout(R.layout.group_item)
                .setQuery(query, Group.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseListAdapter<Group>(options) {
            @Override
            protected void populateView(View v, Group model, int position) {
                TextView gr_name;
                gr_name = (TextView) v.findViewById(R.id.group_name);
                gr_name.setText(model.getName());
            }
        };
        listOfGroups.setAdapter(adapter);
    }
}