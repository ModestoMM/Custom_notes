package com.modesto.customnotes.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.modesto.customnotes.Models.Organizer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrganizerProvider {

    private CollectionReference mCollection;

    public OrganizerProvider() { mCollection= FirebaseFirestore.getInstance().collection("Organizer"); }

    //Guardamos el organizador
    public Task<Void>save(Organizer organizer){

        DocumentReference documentReference = mCollection.document();

        String id= documentReference.getId();

        organizer.setId(id);

        return documentReference.set(organizer);
    }

    public Query getAllOrganizerbyUser(String idUsers){
        return mCollection.whereEqualTo("id_Users",idUsers).orderBy("timestamp",Query.Direction.ASCENDING);
    }

    public Task<Void> DeleteOrganizer(String idOrganizer){
    return mCollection.document(idOrganizer).delete();
    }

    public Task<Void> updateOrganizer (String idOrganizer,String nameCategory){
        Map<String,Object> map= new HashMap<>();
         map.put("name_Category",nameCategory);
        return mCollection.document(idOrganizer).update(map);
    }



}
