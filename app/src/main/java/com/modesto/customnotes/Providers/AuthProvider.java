package com.modesto.customnotes.Providers;

import android.content.Context;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//Esta clase es la que contendra la logica del usuario en ell FirebaseAuth para asi tener mejor administrada nuestra logica
public class AuthProvider {

    private FirebaseAuth mAuth;

    public AuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }

    //Esn este metodo creraremos un usuario en al activity register
    public Task<AuthResult> register(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email,password);
    }

    //Este metodo lo utilizaremos para hacer la logica del login de autenticar con el correo y password
    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    //Este metodo lo utilizaremos para sacar el id del usuario que haya iniciado seson ya
    public String getUid() {
        //Validamos si el usuario ya tiene la sesion iniciada
        if (mAuth.getCurrentUser() != null) {
            //El getCurrentUsers nos va a permitir optener la sesion actual del usuario ya que este logueado
            //y el getUid nos debolvera el id que tenemos creado en firebase autentication
            return mAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    //Este metodo lo utilizaremos para verificar si el usuario ya tiene una sesion activada y asi hacer que entre a la sesion
    //directamente y que ya no se vuelva a logear otra vez
    public FirebaseUser getUsersSesion() {
        //Validamos si el usuario ya tiene la sesion iniciada
        if (mAuth.getCurrentUser() != null) {
            //El getCurrentUsers nos va a permitir optener la sesion actual del usuario ya que este logueado
            return mAuth.getCurrentUser();
        } else {
            return null;
        }
    }

    //Este metodo lo usaremos para optener el email
    public String getEmail() {
        //Validamos si el usuario ya tiene la sesion iniciada
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }

    //Metodo para cerrar sesion
    public void logout(Context context){
        if(mAuth != null){
            mAuth.signOut();
        }
    }


}
