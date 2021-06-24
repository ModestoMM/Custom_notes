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
import com.modesto.customnotes.Models.Users;
import com.modesto.customnotes.Providers.AuthProvider;
import com.modesto.customnotes.Providers.UsersProvider;
import com.modesto.customnotes.R;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;
    TextView mTextViewLogin;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mTextInputUsername = findViewById(R.id.textInputUserName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
        mTextViewLogin = findViewById(R.id.TextViewLogin);
        mButtonRegister = findViewById(R.id.btnRegister);

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento....")
                .setCancelable(false) .build();

        mAuthProvider= new AuthProvider();
        mUsersProvider = new UsersProvider();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUsersSesion() != null){
            Intent intent= new Intent(RegisterActivity.this,MainActivity.class);
            //Con esto limpiamos el historial que haya hasta el momento
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void register() {
        String username = mTextInputUsername.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String confirmPassword = mTextInputConfirmPassword.getText().toString();



        //Vamos a validar que si el nombre de usuario no esta vacio con el metodo isEmpty
        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() ){
            //Lo utilizamos el metodo si el email es valido
            if(isEmailValid(email)){
                //Validams si el confirmar password y el password son iguales
                if(password.equals(confirmPassword)){
                    //Validamos que el password sea mayor a 6 recordar que las contraseñas en firebase necesitan cuando minimo 6 caracteres
                    if(password.length() >= 6){

                        crearUser(username,email,password);

                    }else{
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();}

                }else{Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();}

            }else{Toast.makeText(this, "Insertaste todos los campos pero el correo no es valido", Toast.LENGTH_LONG).show();}

        }else{Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();}
    }

    private void crearUser(final String username, final String email, String password) {
        mDialog.show();
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            //Este metodo se terminara de ejecutar cuando se termine de registrar el usuario
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Con este if verificamos si la tarea fue exitosa significa que se creo exitosamente el usuario en firebaseAuthentication
                if(task.isSuccessful()){



                    //Sacamos el id del usuario
                    String id= mAuthProvider.getUid();

                    //Creamos una instancia del modelos Users para pasarle los parametros para utilizar el metodo create de la clase
                    //UsersProvider asi lograr instanciar esos valores en la base de datos al mandar el objeto de la clase Users
                    Users users= new Users();
                    users.setId(id);
                    users.setEmail(email);
                    users.setUsername(username);
                    //Esto devolvera un long que es el tiempo
                    users.setTimestamp(new Date().getTime());

                    //Vamos almacenar el usuario en la base de datos cramos una coleccion donde en el documento pondremos el id
                    //que se crea en el autentication firebas y de ahi con el metodo set nos alacenara una informacion
                    //Verificaremos si se pudo guardar el usuario en labase de datos con el metodo addOnCompleteListener
                    mUsersProvider.create(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mDialog.dismiss();
                                Intent intent= new Intent(RegisterActivity.this,MainActivity.class);
                                //Con esto limpiamos el historial de pantalla en las que ha navegado el usuario
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }else{
                                mDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Este metodo lo verificaremos para saber si el Email tiene un formato valido
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}