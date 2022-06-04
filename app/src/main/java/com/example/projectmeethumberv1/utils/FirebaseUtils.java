package com.example.projectmeethumberv1.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.projectmeethumberv1.TopicBoardActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseUtils {
    public void downloadFile(Context context, String fileName, String fileExtention, String destinationDir, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDir, fileName + fileExtention);
        downloadManager.enqueue(request);
        getDownloadDialog(context, fileName).show();
    }

    public Dialog getDownloadDialog(Context context, String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Download is complete")
                .setMessage(fileName + " was downloaded.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadGroupFiles(DataSnapshot mainSnapshot, ArrayList<String> fileNames, HashMap<String, Object> files, String groupName) {
        fileNames.clear();
        files.clear();
        mainSnapshot.getChildren().forEach(groupSnapshot -> extractFilesFromGroup(groupSnapshot, groupName, fileNames, files));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void extractFilesFromGroup(DataSnapshot groupSnapshot, String groupName, ArrayList<String> fileNames, HashMap<String, Object> files) {
        if (groupSnapshot.getKey().equalsIgnoreCase(groupName)) {
            groupSnapshot.getChildren().forEach(fileSnapshot -> addGroupFilesToList(fileSnapshot, fileNames, files));
        }
    }

    private void addGroupFilesToList(DataSnapshot fileSnapshot, ArrayList<String> fileNames, HashMap<String, Object> files) {
        fileNames.add(fileSnapshot.getKey());
        files.put(fileSnapshot.getKey(), fileSnapshot.getValue());
    }

}
