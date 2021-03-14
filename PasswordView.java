package com.example.registerloginsp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordView extends AppCompatActivity {
    private EditText password;
    private EditText password1;
    public static final int PASSWORD_LENGTH = 8;
    public static final String MyPREFERENCES = "Mypassword" ;
    private static final String TAG = "PasswordView";
    SharedPreferences sharedPreferences;
    Button save;

    private Intent dataHave;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isPasswordSet=sharedPreferences.getBoolean("passwordset",false);
        dataHave = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);;

        if(!isPasswordSet) {
            setContentView(R.layout.passwordview);
            save = findViewById(R.id.done1);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    password = findViewById(R.id.editTextTextPassword);
                    password1 = findViewById(R.id.editTextTextPassword2);
                    if (!(password.getText().toString()).equals(password1.getText().toString())) {
                        Toast.makeText(PasswordView.this, "Both Password is not match please re-enter password", Toast.LENGTH_LONG).show();
                    } else {
                        boolean strong = checkpassword(password.getText().toString());
                        if (!strong) {
                            Toast.makeText(PasswordView.this, "password Must satisfy below rule", Toast.LENGTH_LONG).show();

                        } else {
                            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("password", password.getText().toString());
                            editor.putString("locktype", "2");

                            editor.putBoolean("passwordset", true);
                            editor.commit();

                            Toast.makeText(PasswordView.this, "password Save", Toast.LENGTH_LONG).show();
                            Toast.makeText(PasswordView.this, "Lock type 2 set", Toast.LENGTH_LONG).show();

                            Intent intent=new Intent(PasswordView.this,MainActivity.class);
                            intent.putExtra(Intent.EXTRA_INTENT,dataHave);

                            startActivity(intent);

                        }
                    }
                }

            });
        }
        else{
            setContentView(R.layout.passwordlogin);

            save=findViewById(R.id.login);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pass=sharedPreferences.getString("password","");
                    password = findViewById(R.id.passwordlogin);

                    String userpass=password.getText().toString();
                    if(pass.equals(userpass)){
                        Intent intent=new Intent(PasswordView.this,Display.class);
                        intent.putExtra(Intent.EXTRA_INTENT,dataHave);

                        startActivity(intent);
                        PasswordView.this.finish();


                    }
                    else{
                        Toast.makeText(PasswordView.this, "Wrong password ", Toast.LENGTH_LONG).show();

                    }


                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isPasswordSet = sharedPreferences.getBoolean("passwordset", false);
        if (!isPasswordSet) {
            super.onBackPressed();
        }
        else{
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    private boolean checkpassword(String password) {

        if (password.length() < PASSWORD_LENGTH) return false;
        boolean haslower=false;
        boolean hasupper=false;
        boolean hasdigit=false;
        boolean hasspecial=false;
        String special=" !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~";

        for (int i = 0; i < password.length(); i++) {

            char ch = password.charAt(i);

            if (is_Numeric(ch)) hasdigit=true;
            else if (is_lower(ch))haslower=true ;

            else if (is_upper(ch))hasupper=true ;

            else if(special.contains(ch+""))
                hasspecial=true ;
            else
                return false;

        }
        Log.d(TAG, "checkpassword: ");


        Log.d(TAG, "checkpassword: "+hasdigit);

        Log.d(TAG, "checkpassword: "+haslower);

        Log.d(TAG, "checkpassword: "+hasupper);

        Log.d(TAG, "checkpassword: "+hasspecial);


        if(haslower&&hasdigit&&hasspecial&&hasupper)
            return true;
        else
            return false;

    }
    private static boolean is_upper(char ch) {

        return (ch >= 'A' && ch <= 'Z');
    }
    private static boolean is_lower(char ch) {

        return (ch >= 'a' && ch <= 'z');
    }


    private static boolean is_Numeric(char ch) {

        return (ch >= '0' && ch <= '9');
    }

}
