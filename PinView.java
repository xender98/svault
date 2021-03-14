package com.example.registerloginsp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PinView extends AppCompatActivity {
    Button save1;
    EditText pin,pin1;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "Mypassword" ;
    private Intent dataHave;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isPasswordSet=sharedPreferences.getBoolean("passwordset",false);
        dataHave=getIntent().getParcelableExtra(Intent.EXTRA_INTENT);
        if(!isPasswordSet) {
            setContentView(R.layout.pinview);
            save1 = findViewById(R.id.pinsave);
            save1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pin = findViewById(R.id.getpin1);
                    pin1 = findViewById(R.id.getpin2);
                    if (!(pin.getText().toString()).equals(pin1.getText().toString())) {
                        Toast.makeText(PinView.this, "Both Pin is not match please re-enter pin", Toast.LENGTH_LONG).show();
                    } else {
                        String spin = pin.getText().toString();
                        if (spin.length() < 6) {
                            Toast.makeText(PinView.this, "Pin at least contain six digits please re-enter pin", Toast.LENGTH_LONG).show();
                        } else {
                            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("pin", pin.getText().toString());
                            editor.putString("locktype", "3");

                            editor.putBoolean("passwordset", true);
                            editor.commit();
                            Toast.makeText(PinView.this, "Lock type 3 set", Toast.LENGTH_LONG).show();
                            Toast.makeText(PinView.this, "Pin Save", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(PinView.this,MainActivity.class);
                            intent.putExtra(Intent.EXTRA_INTENT,dataHave);

                            startActivity(intent);

                        }
                    }
                }
            });
        }
        else{
            setContentView(R.layout.pinlogin);
            save1=findViewById(R.id.pinloginbt);
            save1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pass=sharedPreferences.getString("pin","");
                    pin = findViewById(R.id.loginpin);

                    String userpin=pin.getText().toString();
                    if(pass.equals(userpin)){
                        Intent intent=new Intent(PinView.this,Display.class);
                        intent.putExtra(Intent.EXTRA_INTENT,dataHave);

                        startActivity(intent);


                    }
                    else{
                        Toast.makeText(PinView.this, "Wrong pin ", Toast.LENGTH_LONG).show();

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
            return;
        }
    }
}
