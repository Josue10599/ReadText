package com.josuelopes.readtext.firebase;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.josuelopes.readtext.R;

public class FirebaseLeituraImagem {

    private final Context context;
    private FirebaseVisionImage mVisionImage;

    public FirebaseLeituraImagem(Bitmap bitmap, Context context) {
        mVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        this.context = context;
    }

    public String lerTexto() {
        return reconhecerTextoNaImagem();
    }

    private String reconhecerTextoNaImagem() {
        final String[] textoExtraido = {""};
        FirebaseVisionTextRecognizer reconhecerTexto = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        reconhecerTexto.processImage(mVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String text = firebaseVisionText.getText();
                textoExtraido[0] = text;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textoExtraido[0] = context.getString(R.string.erro_leitura_imagem);
            }
        });
        return textoExtraido[0];
    }

}
