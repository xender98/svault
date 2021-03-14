package com.example.registerloginsp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static int READ_EXTERNAL_STORAGE_PERMISSION_RESULT=1;
    RadioGroup rg;
    Button bt;
    private Intent dataHave;
    private Intent Ex;
    public static final String MyPREFERENCES = "Mypassword" ;
    SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean firstTime=sharedPreferences.getBoolean("firstTime",true);
        if(firstTime){
            Log.d(TAG, "onClick: 5");
            Intent spalecIntent=new Intent(this,SplashActivity.class);
            startActivity(spalecIntent);
        }
        else {
            boolean isSetPassword = sharedPreferences.getBoolean("passwordset", false);
            Log.d(TAG, "onCreate: " + isSetPassword);
            dataHave = getIntent();
            Ex = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);

            if (!isSetPassword) {
                setContentView(R.layout.activity_main);
                checkPermission();

                rg = findViewById(R.id.radiogroup);
                bt = findViewById(R.id.next);
                Log.d(TAG, "onCreate: 1");
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d(TAG, "onClick: 2");
                        int id = rg.getCheckedRadioButtonId();
                        RadioButton rb = findViewById(id);
                        if (findViewById(R.id.fingureprint).equals(rb)) {
                            openNewActivity1();
                        } else if (findViewById(R.id.password).equals(rb)) {
                            openNewActivity2();
                        } else if (findViewById(R.id.pin).equals(rb)) {
                            openNewActivity3();
                        } else {
                            Toast.makeText(MainActivity.this, "Please select one options", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                String type = sharedPreferences.getString("locktype", "");
                switch (type) {
                    case "1":
                        openNewActivity1();
                        break;
                    case "2":
                        openNewActivity2();
                        break;
                    case "3":
                        openNewActivity3();
                        break;
                    default:
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void openNewActivity3() {
        Log.d(TAG, "onClick: 5");
        Intent pin=new Intent(this,PinView.class);
        pin.putExtra(Intent.EXTRA_INTENT,dataHave);
        if(Ex!=null)
            pin.putExtra(Intent.EXTRA_INTENT,Ex);

        startActivity(pin);
    }

    private void openNewActivity2() {
        Log.d(TAG, "onClick: 4");
        Intent password=new Intent(this,PasswordView.class);
        password.putExtra(Intent.EXTRA_INTENT,dataHave);
        if(Ex!=null)
            password.putExtra(Intent.EXTRA_INTENT,Ex);
        startActivity(password);
    }

    private void openNewActivity1() {
        Log.d(TAG, "onClick: 3");
        Intent fingure=new Intent(this,FingurePrintView.class);
        fingure.putExtra(Intent.EXTRA_INTENT,dataHave);
        if(Ex!=null)
            fingure.putExtra(Intent.EXTRA_INTENT,Ex);
        startActivity(fingure);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this,"App needs this permission",Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
        }
    }
}