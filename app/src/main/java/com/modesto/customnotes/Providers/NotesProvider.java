package com.modesto.customnotes.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.modesto.customnotes.Models.Notes;

import java.util.HashMap;
import java.util.Map;

public class NotesProvider {

    private CollectionReference mCollections;

    public NotesProvider() {
        mCollections= FirebaseFirestore.getInstance().collection("Notes");
    }

    public Task<Void> createNotes(Notes notes){

        DocumentReference documentReference = mCollections.document();

        String id= documentReference.getId();

        notes.setId(id);

        return documentReference.set(notes);
    }

    public Task<Void> DeleteNotes(String idNote){
        return mCollections.document(idNote).delete();
    }

    public Query getallNotesbyOrganizerbyUsers(String idUsers, String idOrganizador){
        return mCollections.whereEqualTo("id_Users",idUsers).whereEqualTo("id_Organizador",idOrganizador).orderBy("timestamp",Query.Direction.DESCENDING);
    }

    public Task<Void> updateNotesInformacion (String idNotes,String informacion){
        Map<String,Object> map= new HashMap<>();
        map.put("informacion",informacion);
        return mCollections.document(idNotes).update(map);
    }

    public Task<DocumentSnapshot> getInformacionNotes(String idNote){
        return mCollections.document(idNote).get();
    }

}
