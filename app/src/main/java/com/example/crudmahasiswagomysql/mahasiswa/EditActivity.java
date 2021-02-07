package com.example.crudmahasiswagomysql.mahasiswa;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.example.crudmahasiswagomysql.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public class EditActivity extends AppCompatActivity {

    private static String BASE_URL = "http://192.168.43.237:83/mima/v1/";
    private TextView IDMhs;
    private EditText Nim, Nama, Jurusan, No_Hp;
    private ImageView imageView;
    private Button btnUbah;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Ubah mahasiswa");

        IDMhs = findViewById(R.id.tv_IdMhs);
        Nim = findViewById(R.id.et_nim);
        Nama = findViewById(R.id.et_nama);
        Jurusan = findViewById(R.id.et_jurusan);
        No_Hp = findViewById(R.id.et_hp);
        imageView = findViewById(R.id.iv_photo);
        btnUbah = findViewById(R.id.btn_edit);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String nim = intent.getStringExtra("nim");
        String nama = intent.getStringExtra("nama");
        String jurusan = intent.getStringExtra("jurusan");
        String hp = intent.getStringExtra("hp");
        String photo = intent.getStringExtra("photo");
        IDMhs.setText(id);
        Nim.setText(nim);
        Nama.setText(nama);
        Jurusan.setText(jurusan);
        No_Hp.setText(hp);
        Picasso.get().load(photo).into(imageView);

        progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setMessage("Uploading Image. Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(EditActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .start(EditActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                if (permissionDeniedResponse.isPermanentlyDenied()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                    builder.setTitle("Permission Required")
                                            .setMessage("Permission to access your device storage is required to pick profile image. Please go to settings to enable permission to access storage")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent1 = new Intent();
                                                    intent1.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent1.setData(Uri.fromParts("package", getPackageName(), null));
                                                    startActivityForResult(intent1, 51);
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                btnUbah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File imageFile = new File(resultUri.getPath());
                        String id = IDMhs.getText().toString();
                        String nim = Nim.getText().toString();
                        String nama = Nama.getText().toString();
                        String jurusan = Jurusan.getText().toString();
                        String no_hp = No_Hp.getText().toString();
                        progressDialog.show();
                        AndroidNetworking.upload(BASE_URL+"mahasiswa-edit/")
                                .addMultipartFile("image", imageFile)
                                .addMultipartParameter("id", id)
                                .addMultipartParameter("nim", nim)
                                .addMultipartParameter("nama", nama)
                                .addMultipartParameter("jurusan", jurusan)
                                .addMultipartParameter("no_hp", no_hp)
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        float progress = (float) bytesUploaded / totalBytes * 100;
                                        progressDialog.setProgress((int)progress);
                                    }
                                })
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            progressDialog.dismiss();
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean status = jsonObject.getBoolean("status");
                                            String message = jsonObject.getString("message");
                                            if (status == false) {
                                                Toast.makeText(EditActivity.this, "Unable to upload photo: " + message, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(EditActivity.this, message, Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(EditActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(EditActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        anError.printStackTrace();
                                        Toast.makeText(EditActivity.this, "Error Uploading Image", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(EditActivity.this, "Crop Error : " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
