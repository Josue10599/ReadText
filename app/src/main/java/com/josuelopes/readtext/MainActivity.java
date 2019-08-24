package com.josuelopes.readtext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_IMAGE_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarPermissao();
    }

    private void abrirCameraDoCelular() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, REQUEST_CODE_IMAGE_CAMERA);
    }

    private void verificarPermissao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        } else {
            abrirCameraDoCelular();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                abrirCameraDoCelular();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }
}
