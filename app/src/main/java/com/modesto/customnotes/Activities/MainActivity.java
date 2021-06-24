package com.modesto.customnotes.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.modesto.customnotes.Providers.AuthProvider;
import com.modesto.customnotes.Providers.UsersProvider;
import com.modesto.customnotes.R;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button mButtonLogin;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    AuthProvider mAuthProvider;
    AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonLogin = findViewById(R.id.btnLogin);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);

        mAuthProvider = new AuthProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento....")
                .setCancelable(false) .build();

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        mDialog.show();

        if(!email.isEmpty() && !password.isEmpty()){
            //Este metodo es el que valia dentro del firebase autentication si el correo y la contraseña ya estan registrados y asi
            //hacer el login
            mAuthProvider.login(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if(task.isSuccessful()){
                        Intent intent= new Intent(MainActivity.this,OrganizerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "El email o la contraseña que ingresaste no son correctas", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            mDialog.dismiss();
            Toast.makeText(this, "Favor de ingresar el Email y contraseña", Toast.LENGTH_LONG).show();
        }
    }

    }
