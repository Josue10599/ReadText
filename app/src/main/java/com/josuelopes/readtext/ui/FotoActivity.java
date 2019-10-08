package com.josuelopes.readtext.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.josuelopes.readtext.R;
import com.josuelopes.readtext.firebase.FirebaseLeituraImagem;

import java.util.Objects;

public class FotoActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_IMAGE_CAMERA = 1;

    private FirebaseLeituraImagem leituraImagem;
    private TextInputEditText campoTextoExtraido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foto_main);
        if (savedInstanceState == null) verificarPermissao();
        leituraImagem = new FirebaseLeituraImagem();
        campoTextoExtraido = findViewById(R.id.foto_texto_extraido_textInputEditText);
        configuraFloatingActionButton();
    }

    private void configuraFloatingActionButton() {
        FloatingActionButton floatingActionButton = findViewById(R.id.foto_abrir_camera_floatingActionButton);
        floatingActionButton.setOnClickListener((view) -> verificarPermissao());
    }

    private void abrirCameraDoCelular() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, REQUEST_CODE_IMAGE_CAMERA);
    }

    private void verificarPermissao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        } else abrirCameraDoCelular();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                abrirCameraDoCelular();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ImageView viewImagem = findViewById(R.id.foto_imagem_retirada_imageView);
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                viewImagem.setImageBitmap(imageBitmap);
                leituraImagem.lerTexto(imageBitmap,
                        firebaseVisionText -> campoTextoExtraido.setText(firebaseVisionText.getText()),
                        e -> campoTextoExtraido.setText(getString(R.string.erro_leitura_imagem)));
            } else imagemErro(viewImagem);
        } else imagemErro(viewImagem);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void imagemErro(ImageView viewImagem) {
        viewImagem.setImageDrawable(getDrawable(R.drawable.ic_error_outline_black_24dp));
    }
}
