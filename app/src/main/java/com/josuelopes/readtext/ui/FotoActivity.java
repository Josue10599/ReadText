package com.josuelopes.readtext.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.josuelopes.readtext.R;
import com.josuelopes.readtext.firebase.FirebaseLeituraImagem;

import java.util.Objects;
import java.util.UUID;

public class FotoActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_IMAGE_CAMERA = 1;

    private FirebaseLeituraImagem leituraImagem;
    private TextInputEditText campoTextoExtraido;
    private FloatingActionButton compartilhar;
    private ImageView viewImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foto_main);
        if (savedInstanceState == null) verificarPermissao();
        leituraImagem = new FirebaseLeituraImagem();
        configuraCampoTextoExtraido();
        configuraAbrirCameraFloatingActionButton();
        configuraCompartilharTextoFloatingActionButton();
        viewImagem = findViewById(R.id.foto_imagem_retirada_imageView);
        Glide.with(this).load(R.drawable.ic_error_outline_black_24dp)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(viewImagem);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                abrirCameraDoCelular();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, UUID.randomUUID().toString(), "Imagem Read Text");
                Glide.with(this).load(imageBitmap)
                        .placeholder(R.drawable.ic_error_outline_black_24dp)
                        .error(R.drawable.ic_error_outline_black_24dp)
                        .into(viewImagem);
                leituraImagem.lerTexto(imageBitmap, firebaseVisionText -> {
                            campoTextoExtraido.setText(firebaseVisionText.getText());
                            apresentaBotaoCompartilhar(firebaseVisionText.getText());
                        },
                        e -> campoTextoExtraido.setText(getString(R.string.erro_leitura_imagem)));
            } else imagemErro(viewImagem);
        } else imagemErro(viewImagem);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void apresentaBotaoCompartilhar(String texto) {
        if (texto.isEmpty()) compartilhar.hide();
        else compartilhar.show();
    }

    private void configuraCampoTextoExtraido() {
        campoTextoExtraido = findViewById(R.id.foto_texto_extraido_textInputEditText);
    }

    private void configuraAbrirCameraFloatingActionButton() {
        FloatingActionButton abrirCamera = findViewById(R.id.foto_abrir_camera_floatingActionButton);
        abrirCamera.setOnClickListener((view) -> verificarPermissao());
    }

    private void configuraCompartilharTextoFloatingActionButton() {
        compartilhar = findViewById(R.id.foto_abrir_compartilhar_floatingActionButton);
        compartilhar.hide();
        compartilhar.setOnClickListener((view -> compartilharTextoExtraido()));
    }

    private void compartilharTextoExtraido() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Objects.requireNonNull(campoTextoExtraido.getText()).toString());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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

    private void imagemErro(ImageView viewImagem) {
        viewImagem.setImageDrawable(getDrawable(R.drawable.ic_error_outline_black_24dp));
        viewImagem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }
}
