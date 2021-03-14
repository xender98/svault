package com.example.registerloginsp;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FingurePrintView extends AppCompatActivity {
    private static final String TAG = "FingurePrintView";

    private FingerprintManager fm;
    private KeyguardManager km;
    Intent myData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingureprintview);
        Log.d(TAG, "onCreate: ");
        myData=getIntent().getParcelableExtra(Intent.EXTRA_INTENT);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            fm = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (!fm.isHardwareDetected()) {
                Toast.makeText(this, "FingurePrint Scanner Not Detected", Toast.LENGTH_SHORT).show();
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "FingurePrint Permissions Not Granted", Toast.LENGTH_SHORT).show();
            } else if (!km.isKeyguardSecure()) {

                Toast.makeText(this, "Add lock to your phone", Toast.LENGTH_SHORT).show();
            } else if (!fm.hasEnrolledFingerprints()) {

                Toast.makeText(this, "Please Add atleast One FingurePrint TO use Svault", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sucesses", Toast.LENGTH_SHORT).show();
                FingerprintHelper myHelper = new FingerprintHelper(this,myData);
                myHelper.startAuth(fm, null);



            }
        }

    }

    @Override
    public void onBackPressed() {
        return ;
    }
}
