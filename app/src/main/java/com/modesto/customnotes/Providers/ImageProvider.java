package com.modesto.customnotes.Providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.modesto.customnotes.Utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider(){
        //Con esto hacemos referencia a nuestro modulo de firebaseStorage
        mStorage= FirebaseStorage.getInstance().getReference();
    }

    //En este metodo estableceremos la logica para añmacenar las imagenes
    public UploadTask save(Context context, File file){
        //En este metodo haremos la compresion de la imagen recibimos el contexto, el archivo, el ancho de la image y el alto
        byte [] imageByte = CompressorBitmapImage.getImage(context,file.getPath(), 500, 500);

        //Aqui estableceremos con que nombre se guardara mi imagen en este caso lo guardamos por fecha, mandamos la
        //referencia de la Firebase en lugar del mStorage ya que de esta manera evitamos que se cree una carpeta a la hora
        //de subir las 2 imagenes
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(new Date() + ".jpg");

        //Le damos la referencia de la imagen que se guarda al igualar el mStorage a storage que se utiliza para guardar
        //la imagen en la StorageFirebase
        mStorage= storageReference;

        //Recive de parametro el mapa de bits que contiene el imageByte
        UploadTask Task = storageReference.putBytes(imageByte);
        return Task;
    }

    //Returnamos la variable getStorage que sera la instancia para conectarse con StorageFirebase
    public StorageReference getStorage(){
        return mStorage;
    }



}
