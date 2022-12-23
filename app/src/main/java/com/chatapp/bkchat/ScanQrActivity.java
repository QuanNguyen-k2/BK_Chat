package com.chatapp.bkchat;

import static android.graphics.Color.WHITE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.CaptureActivity;


public class ScanQrActivity extends AppCompatActivity {

    private ImageView qrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        Button scanBtn = findViewById(R.id.buttonScan);
        Toolbar mToolbar = findViewById(R.id.toolbar_setting);
        qrCode = findViewById(R.id.qrCode);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");

        createQrCode();

        scanBtn.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setCaptureActivity(CaptureAct.class);
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            intentIntegrator.setPrompt("Scanning QR Code");
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.initiateScan();
        });

    }

    private void createQrCode() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            BitMatrix bitMatrix = writer.encode(uid, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : WHITE);
                }
            }
            qrCode.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String uid = intentResult.getContents();
                sendToProfileByUid(uid);
//                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
//                rootRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.hasChild(uid)) {
//                            sendToProfileByUid(uid);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendToProfileByUid(String uid) {
        Intent profileIntent = new Intent(ScanQrActivity.this, ProfileActivity.class);
        profileIntent.putExtra("visit_user_id", uid);
        startActivity(profileIntent);
        finish();
    }


    public static class CaptureAct extends CaptureActivity {

    }
}