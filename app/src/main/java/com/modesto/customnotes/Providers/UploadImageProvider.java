package com.modesto.customnotes.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.modesto.customnotes.Models.ImageR;

public class UploadImageProvider {

    private CollectionReference mCollection;

    public UploadImageProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("ImageR");
    }

    public Task<Void> saveImage(ImageR imageR){

        DocumentReference documentReference = mCollection.document();

        String id= documentReference.getId();

        imageR.setId(id);

        return documentReference.set(imageR);
    }

    public Query getAllImagesbyNotes(String idnotes){
        return mCollection.whereEqualTo("id_Note",idnotes).orderBy("timestamp",Query.Direction.DESCENDING);
    }

    public Task<DocumentSnapshot> getImage (String id_imageR){
        return mCollection.document(id_imageR).get();
    }

}
