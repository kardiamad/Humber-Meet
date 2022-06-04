package com.example.projectmeethumberv1;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.projectmeethumberv1.utils.FirebaseUtils;
import com.example.projectmeethumberv1.utils.UriUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TopicBoardActivity extends AppCompatActivity {

    private static final String GROUP_ATTRIBUTE_NAME = "group";
    private static final String FILES_ENTRY_NAME = "files";
    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String PROGRESS_DIALOG_TITLE = "Uploading file...";
    private static final String PERMISSION_ABSENCE_MESSAGE = "Please provide permission";
    private static final String SUCCESS_MESSAGE = "File successfully uploaded";
    private static final String FAILED_MESSAGE = "File failed to uploaded: ";
    private static final String FILE_TYPE_PATTERN = "*/*";
    private static final Integer REQUEST_CODE = 86;

    private TextView textView;
    private Button selectToUploadButton;
    private Button uploadButton;
    private TextView uploadNotification;
    private Uri fileUri;
    private ProgressDialog progressDialog;
    private DatabaseReference fileDatabaseReference;
    private DatabaseReference filesFolderDatabaseReference;
    private Group communityAttributes;
    private ListView listView;
    private ArrayList<String> fileNames;
    private ArrayList<String> fileNamesCopyForSearch;
    private HashMap<String, Object> files;
    private ArrayList<String> foundFiles;
    private ArrayAdapter<String> adapter;
    private FirebaseUtils firebaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_board);

        textView = findViewById(R.id.groupTitle);
        communityAttributes = (Group)getIntent().getExtras().get("group");
        textView.setText(communityAttributes.getName() + "");
        selectToUploadButton = findViewById(R.id.button_select_upload_file);
        uploadButton = findViewById(R.id.button_upload_file);
        uploadNotification = findViewById(R.id.uploaded_file_name);

        listView = findViewById(R.id.listFiles);
        fileNames = new ArrayList<>();
        files = new HashMap<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_communities, fileNames);
        listView.setAdapter(adapter);

        filesFolderDatabaseReference = FirebaseDatabase.getInstance().getReference().child(FILES_ENTRY_NAME);
        filesFolderDatabaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot mainSnapshot) {
                firebaseUtils.loadGroupFiles(mainSnapshot, fileNames, files, textView.getText().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fileDatabaseReference = filesFolderDatabaseReference
                .child(textView.getText().toString());

        selectToUploadButton.setOnClickListener(view -> {
            if (isValidSelfPermission()) {
                selectFile();
            } else {
                ActivityCompat.requestPermissions(TopicBoardActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            }
        });

        uploadButton.setOnClickListener(view -> {
            if (fileUri != null) {
                uploadFile(fileUri);
            }
        });
        firebaseUtils = new FirebaseUtils();
    }

    public void onClick(View v) {
        download(((TextView) v).getText().toString());
    }

    private void download(String fileName) {
        StorageReference downloadedFileReference = FirebaseStorage.getInstance()
                .getReference()
                .child(FILES_ENTRY_NAME)
                .child(communityAttributes.getName())
                .child(fileName + ".pdf");

        downloadedFileReference.getDownloadUrl().addOnSuccessListener(uri ->
                firebaseUtils.downloadFile(TopicBoardActivity.this, fileName, ".pdf", DIRECTORY_DOWNLOADS, uri.toString())
        ).addOnFailureListener(e ->
                Toast.makeText(TopicBoardActivity.this, FAILED_MESSAGE + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private boolean isValidSelfPermission() {
        return ContextCompat.checkSelfPermission(TopicBoardActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (hasPermission(requestCode, grantResults)) {
            selectFile();
        } else {
            Toast.makeText(TopicBoardActivity.this, PERMISSION_ABSENCE_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermission(int requestCode, int[] grantResults) {
        return requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType(FILE_TYPE_PATTERN);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chooser"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isFileChosen(requestCode, resultCode, data)) {
            fileUri = data.getData();
            uploadNotification.setText(UriUtils.getFileName(fileUri, TopicBoardActivity.this) + " is selected");
        }
    }

    private boolean isFileChosen(int requestCode, int resultCode, Intent data) {
        return requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null;
    }

    private void uploadFile(Uri fileUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(PROGRESS_DIALOG_TITLE);
        progressDialog.setProgress(0);
        progressDialog.show();

        StorageReference uploadedFilesReference = FirebaseStorage.getInstance()
                .getReference()
                .child(FILES_ENTRY_NAME)
                .child(communityAttributes.getName())
                .child(UriUtils.getFileName(fileUri, TopicBoardActivity.this));

        uploadedFilesReference.putFile(fileUri)
                .addOnSuccessListener(getOnSuccessListener(uploadedFilesReference))
                .addOnFailureListener(getOnFailureListener())
                .addOnProgressListener(getOnProgressListener());
        uploadNotification.setText("No file is selected");
    }

    private OnSuccessListener<UploadTask.TaskSnapshot> getOnSuccessListener(StorageReference uploadedFilesReference) {
        return taskSnapshot -> uploadedFilesReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    files.put(UriUtils.extractFirstFilenameToken(uri, TopicBoardActivity.this), String.valueOf(uri));
                    fileDatabaseReference.setValue(files);
                    Toast.makeText(TopicBoardActivity.this, SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                });
    }

    private OnFailureListener getOnFailureListener() {
        return exception -> Toast.makeText(TopicBoardActivity.this, FAILED_MESSAGE + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    private OnProgressListener<UploadTask.TaskSnapshot> getOnProgressListener() {
        return snapshot -> {
            int progress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setProgress(progress);
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comminity_top_menu, menu);
        MenuItem item = menu.findItem(R.id.action_document_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextSubmit(String search) {
                searchDocument(search);
                // To avoid calling onQueryTextSubmit twice
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            foundFiles.clear();
            adapter.clear();
            adapter.addAll(fileNamesCopyForSearch);
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void searchDocument(String search) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Searching...");
        progressDialog.show();
        foundFiles = files.keySet().stream()
                .filter(o -> o.equalsIgnoreCase(search))
                .collect(Collectors.toCollection(ArrayList::new));
        progressDialog.cancel();
        fileNamesCopyForSearch = new ArrayList<>(fileNames);
        adapter.clear();
        adapter.addAll(foundFiles);
    }
}
