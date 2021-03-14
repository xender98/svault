package com.example.registerloginsp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHelper extends FingerprintManager.AuthenticationCallback {
    private Context ct;
    public static final String MyPREFERENCES = "Mypassword" ;
    Intent myData;

    public FingerprintHelper(Context context,Intent myData) {

        this.ct = context;
        this.myData=myData;
    }

    public void startAuth(FingerprintManager fm, FingerprintManager.CryptoObject co){
        CancellationSignal cancel=new CancellationSignal();
        fm.authenticate(co,cancel,0,this,null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        this.update("There was an auth error.  "+errString,false);

    }


    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        this.update("Auth Failed. ",false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        this.update("Error."+helpString,false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        this.update("Access to The Svult Granted",true);
    }

    private void update(String s, boolean b) {
        Toast.makeText(this.ct,s,Toast.LENGTH_SHORT).show();

        if(b){
            SharedPreferences sharedPreferences=this.ct.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("locktype","1");

            editor.putBoolean("passwordset",true);
            editor.commit();
            Toast.makeText(this.ct,"Lock type 1 set",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(this.ct,Display.class);
            intent.putExtra(Intent.EXTRA_INTENT,myData);
            ct.startActivity(intent);


        }
    }

}
