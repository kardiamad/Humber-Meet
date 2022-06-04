package com.example.projectmeethumberv1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.type.LatLng;

import java.util.Date;
import java.util.Random;


public class AddCommunityFragment extends Fragment {

    Button btnSubmit, btnCancelAdd;
    Fragment thisFragment;
    EditText txtGroupName;

    public AddCommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnCancelAdd = view.findViewById(R.id.btnCancelAdd);
        thisFragment = this;

        btnCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getFragmentManager().beginTransaction().remove(thisFragment).commit();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                txtGroupName = view.findViewById(R.id.txtGroupName);
                Random r = new Random();
                Long id = Long.parseLong(String.valueOf(r.nextInt(100 - 1) + 1)) ;
                Group newGroup = new Group(id, txtGroupName.getText().toString());
                FirebaseDatabase.getInstance().getReference("groups").push()
                        .setValue(newGroup);

                getFragmentManager().beginTransaction().remove(thisFragment).commit();
            }
        });
    }
}