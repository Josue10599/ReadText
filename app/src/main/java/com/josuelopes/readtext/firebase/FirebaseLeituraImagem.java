package com.josuelopes.readtext.firebase;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class FirebaseLeituraImagem {

    public void lerTexto(Bitmap bitmap, OnSuccessListener<FirebaseVisionText> sucesso, OnFailureListener falha) {
        reconhecerTextoNaImagem(bitmap, sucesso, falha);
    }

    private void reconhecerTextoNaImagem(Bitmap bitmap, OnSuccessListener<FirebaseVisionText> sucesso, OnFailureListener falha) {
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVision.getInstance().getOnDeviceTextRecognizer()
                .processImage(visionImage)
                .addOnSuccessListener(sucesso)
                .addOnFailureListener(falha);
    }

}
