package com.modesto.customnotes.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.modesto.customnotes.Models.Users;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//Aqui lo que haremos es establecer la logica para almacenar optener actualizar o eliminar datos de firestone
public class UsersProvider {

    private CollectionReference mCollection;


    public UsersProvider(){
        mCollection= FirebaseFirestore.getInstance().collection("Users");
    }

    //Este metodo nos permitira optener la informacion de un solo usuario en la base de datos
    public Task<DocumentSnapshot> getUsers(String idUsers){
        //Hcemos una consulta a la coleccion de Users despues verificaremso si existe dentro de la coleccion un usuario con el id
        //que se acabo de registrar con al cuenta de google, despues llamaremos al metodo get que nos permitira obtener informacion
        //Una sola vez de la base de datos
       return mCollection.document(idUsers).get();
    }

    //Este metodo nos permitira optener la informacion de un solo usuario en la base de datos en tiempo real
    public DocumentReference getUsersRealTime(String idUsers){
        //Hcemos una consulta a la coleccion de Users despues verificaremso si existe dentro de la coleccion un usuario con el id
        //que se acabo de register en el modulo de Autentication
       return mCollection.document(idUsers);
    }

    //En este metodo almacenaremos la informacion del usuario, esto necesitaria como estamos trabajando con el modelo de usuarios ese modelo
    public Task<Void> create (Users users){
       return mCollection.document(users.getId()).set(users);
    }

    //En este metodo almacenaremos la informacion del usuario, esto necesitaria como estamos trabajando con el modelo de usuarios ese modelo
    public Task<Void> update (Users users){
        Map<String,Object> map= new HashMap<>();
        map.put("username",users.getUsername());
        map.put("timestamp",new Date().getTime());
        return mCollection.document(users.getId()).update(map);
    }

    public Task<DocumentSnapshot> getUserName(String idUser){
        return mCollection.document(idUser).get();
    }
}
