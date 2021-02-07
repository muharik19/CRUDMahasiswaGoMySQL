package com.example.crudmahasiswagomysql.mahasiswa;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public class CreateActivity extends AppCompatActivity {

    private static String BASE_URL = "http://192.168.43.237:83/mima/v1/";
    private EditText Nim, Nama, Hp, Jurusan;
    private ImageView imageView;
    private Button btnSimpan;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New mahasiswa");

        Nim = findViewById(R.id.et_nim);
        Nama = findViewById(R.id.et_nama);
        Jurusan = findViewById(R.id.et_jurusan);
        Hp = findViewById(R.id.et_hp);
        imageView = findViewById(R.id.iv_photo);
        btnSimpan = findViewById(R.id.btn_tambah);

        progressDialog = new ProgressDialog(CreateActivity.this);
        progressDialog.setMessage("Uploading Photo. Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(CreateActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .start(CreateActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                if (permissionDeniedResponse.isPermanentlyDenied()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                                    builder.setTitle("Permission Required")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                    startActivityForResult(intent, 51);
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
                btnSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File imageFile = new File(resultUri.getPath());
                        String nim = Nim.getText().toString();
                        String nama = Nama.getText().toString();
                        String jurusan = Jurusan.getText().toString();
                        String hp = Hp.getText().toString();
                        progressDialog.show();
                        AndroidNetworking.upload(BASE_URL+"mahasiswa-add/")
                                .addMultipartFile("image", imageFile)
                                .addMultipartParameter("nim", nim)
                                .addMultipartParameter("nama", nama)
                                .addMultipartParameter("jurusan", jurusan)
                                .addMultipartParameter("no_hp", hp)
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
                                        Log.i("mytag", response);
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean status = jsonObject.getBoolean("status");
                                            String message = jsonObject.getString("message");
                                            if (status == false) {
                                                Toast.makeText(CreateActivity.this, "Unable to upload photo: " + message, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CreateActivity.this, message, Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(CreateActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(CreateActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        anError.printStackTrace();
                                        Toast.makeText(CreateActivity.this, "Error Uploading Image", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(CreateActivity.this, "Crop Error : " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
