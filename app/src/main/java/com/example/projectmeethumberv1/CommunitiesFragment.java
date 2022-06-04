package com.example.projectmeethumberv1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class CommunitiesFragment extends Fragment {

    private static final String COMMUNITY_NAME = "name";
    private static final String GROUPS_ENTRY_NAME = "groups";
    private static final String GROUP_NOT_FOUND_MESSAGE = "Selected group not found";

    private FirebaseListAdapter<Group> adapter, newAdapter;
    private ListView listOfGroups;
    private DatabaseReference databaseReference;
    private ArrayList<HashMap<String, Object>> communities;
    private ArrayList<String> communityNames;
    private Button btnAddCommunity;
//    private ArrayAdapter<String> adapter;

    public CommunitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communities, parent, false);

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
        listOfGroups = view.findViewById(R.id.list_of_groups_communities);
        listOfGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TopicBoardActivity.class);
//                long id = adapter.getItem(i).getId();
                Group selectedGroupFromList = (Group) (listOfGroups.getItemAtPosition(i));
                intent.putExtra("group", selectedGroupFromList);
                startActivity(intent);
//                Log.d("myTag", "This is my message");
            }
        });
        displayAllGroups();

        btnAddCommunity = view.findViewById(R.id.btnAddCommunity);

        btnAddCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AddCommunityFragment addCommunityFragment = new AddCommunityFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerView, addCommunityFragment); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });
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