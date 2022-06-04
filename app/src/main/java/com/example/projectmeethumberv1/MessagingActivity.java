package com.example.projectmeethumberv1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

public class MessagingActivity extends AppCompatActivity {

    private RelativeLayout activity_messaging;
    private FirebaseListAdapter<Message> adapter;
    private FloatingActionButton sendBtn;
    private ListView listOfMessages;
    private  Long groupId = 0l;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        groupId = getIntent().getLongExtra(Constants.groupIdParam, 0);
        groupName = getIntent().getStringExtra("groupName");
        getSupportActionBar().setTitle(groupName);
        activity_messaging = findViewById(R.id.activity_messaging);
        listOfMessages = findViewById(R.id.list_of_messages);
        sendBtn = findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(view -> {
            EditText textField = findViewById(R.id.messageField);
            if(textField.getText().toString().isEmpty())
                return;

            FirebaseDatabase.getInstance().getReference("message").push()
                    .setValue(new Message (FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    textField.getText().toString().trim(), new Date().getTime(), groupId) );
            displayAllMessages();
            textField.setText("");
        });

        displayAllMessages();

    }

    private void displayAllMessages() {
        Query query = FirebaseDatabase.getInstance().getReference("message").orderByChild("groupId").equalTo(groupId);
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.list_item)
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)   //Added this
                .build();
        // Get references to the views of message.xml

        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View v, Message model, int position) {
                // Get references to the views of message.xml
                TextView mess_text, mess_user, mess_time;
                mess_user = (TextView) v.findViewById(R.id.message_user);
                mess_text = (TextView) v.findViewById(R.id.message_text);
                mess_time = (TextView) v.findViewById(R.id.message_time);
                mess_user.setText(model.getUserName());
                mess_time.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getMessageTime()));
                mess_text.setText(model.getTextMessage());

//                if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(model.getUserName())){
//                    Drawable bd = getResources().getDrawable(R.drawable.current_user_background);
//                    v.setBackgroundDrawable(bd);
//                }
            }
        };
        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listOfMessages.setStackFromBottom(true);
        listOfMessages.setAdapter(adapter);
//        listOfMessages.smoothScrollToPosition(adapter.getCount() -1);
    }
}