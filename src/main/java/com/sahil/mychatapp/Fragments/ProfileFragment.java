package com.sahil.mychatapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sahil.mychatapp.Model.User;
import com.sahil.mychatapp.R;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private TextView prof_username;
    private ImageView  prof_image;


    private DatabaseReference ref;
    private  FirebaseUser firebaseUser;

    //for image storage
    StorageReference storageReference;
    private static  final int IMAGE_REQUEST = 1;
    private Uri image_uri;
    private StorageTask uploadTask;
    private ImageView upload_btn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container ,false);

        prof_username = view.findViewById(R.id.profile_username);
        prof_image = view.findViewById(R.id.profile_image);
        upload_btn = view.findViewById(R.id.upload_btn);
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        ref = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        // storage reference for profile image
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(getActivity()== null){
                    return;
                }
                User user = snapshot.getValue(User.class);

                prof_username.setText(user.getUserName());

                if(user.getImageURL().equals("default")){
//                    prof_image.setImageResource(R.mipmap.ic_launcher_round);
                    Glide.with(getContext())
                            .load(R.drawable.default_dp)
                            .apply(RequestOptions.circleCropTransform())
                            .into(prof_image);
                }else{
                    Glide.with(Objects.requireNonNull(getContext()))
                            .load(user.getImageURL())
                            .apply(RequestOptions.circleCropTransform())
                            .into(prof_image);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        return view;
    }

    private void chooseImage() {
        Log.d("DDDD", " 0");

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                IMAGE_REQUEST);
    }
    private String getFileExtention(Uri uri){
        Log.d("DDDD", " 0.1");

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void UploadMyImage(){
        Log.d("DDDD", " 1");
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading..");
        progressDialog.show();

        if(image_uri!=null){
            Log.d("DDDD", " 2");

            final StorageReference fileref = storageReference.child(System.currentTimeMillis()+ "." +
                    getFileExtention(image_uri));

            uploadTask = fileref.putFile(image_uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            } ).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                     if(task.isSuccessful()){
                         Log.d("DDDD", " 3");

                         Uri dUri = task.getResult();
                        String mUri = dUri.toString();

                        ref = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("ImageURL", mUri);
                        ref.updateChildren(map);
                        progressDialog.dismiss();
                     } else{
                         Log.d("DDDD", " 4");

                         Toast.makeText(getContext(), "Failed!!", Toast.LENGTH_SHORT).show();
                     }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("DDDD", " 5");

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            });
        } else {
            Log.d("DDDD", " 6");

            Toast.makeText(getContext(), "No Image selected..", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DDDD", " 11");

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
        && data !=null && data.getData()!=null){

            image_uri = data.getData();
            Log.d("DDDD", " 12");
            Log.d("DDDD", " 12 " +image_uri );

            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "In Progress", Toast.LENGTH_SHORT).show();

            } else{
                UploadMyImage();
            }
        }
    }
}