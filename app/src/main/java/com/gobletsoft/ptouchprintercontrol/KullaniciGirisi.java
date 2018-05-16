package com.gobletsoft.ptouchprintercontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class KullaniciGirisi extends AppCompatActivity {

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_kullanici_girisi);

        final EditText etEmail = findViewById(R.id.editTextEmail);
        final EditText etPassword = findViewById(R.id.editTextPassword);
        Button btnSignin = findViewById(R.id.buttonSignin);
        Button btnForgotPassword = findViewById(R.id.buttonForgotPassword);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //kullanıcı bilgilerini db ile karşılaştır ve cevap yolla.

                if (etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Email ve Şifre Boş Bırakılamaz.", Toast.LENGTH_LONG).show();
                }
                else{

                    startActivity(new Intent(KullaniciGirisi.this, Activity_StartMenu.class));
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // şifre değiştirt - değişim maili yolla -, db ile email karşılaştır ve değiştir.
            }
        });
    }
}
