package com.modesto.customnotes.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.modesto.customnotes.Activities.EditNoteActivity;
import com.modesto.customnotes.Models.Notes;

import com.modesto.customnotes.Providers.NotesProvider;
import com.modesto.customnotes.R;
import com.modesto.customnotes.Utils.RelativeTime;

public class NotesAdapters extends FirestoreRecyclerAdapter<Notes,NotesAdapters.ViewHolder> {

    Context context;
    NotesProvider mNotesProviders;
    public NotesAdapters(@NonNull FirestoreRecyclerOptions<Notes> options, Context context) {
        super(options);
        this.context= context;
        mNotesProviders = new NotesProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final NotesAdapters.ViewHolder holder, int position, @NonNull final Notes notes) {

        DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(position);

        final String NotesId = documentSnapshot.getId();

        holder.textViewTitleMyNotes.setText(notes.getTitle());
        String relativeTime = RelativeTime.getTimeAgo(notes.getTimestamp(),context);
        holder.textViewRelativeTimeMyNotes.setText(relativeTime);

        holder.imageViewNoteDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowConfirmDelete(NotesId);
            }
        });

        holder.ViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, EditNoteActivity.class);
                intent.putExtra("id_notes",NotesId);
                intent.putExtra("id_organizer",notes.getId_Organizador());
                intent.putExtra("title", notes.getTitle());
                context.startActivity(intent);
            }
        });

    }

    private void ShowConfirmDelete(final String notesId) {
        //Creamos la alerta con el alertDialog.Builder le agregamos el icono ic_dialog_alog es un icono de alerta
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Eliminar Nota")
                .setMessage("¿Estas seguro de realizar esta accion?")
                //AGREGAMOS EL BOTON POSTIVO
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNotes(notesId);
                    }
                })
                //AGREGAMOS EL BPTPN NEGATIVO
                .setNegativeButton("NO", null)
                .show();
    }

    private void deleteNotes(String notesId) {

        mNotesProviders.DeleteNotes(notesId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "La nota ha sido eliminado con exito", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "La nota no se pudo eliminar correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @NonNull
    @Override
    public NotesAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewnotes,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitleMyNotes;
        TextView textViewRelativeTimeMyNotes;
        ImageView imageViewNoteDelete;
        View ViewHolder;

        public ViewHolder(@NonNull View view) {
            super(view);
            textViewTitleMyNotes = view.findViewById(R.id.textViewTitleMyNotes);
            textViewRelativeTimeMyNotes = view.findViewById(R.id.textViewRelativeTimeMyNotes);
            imageViewNoteDelete = view.findViewById(R.id.imageViewDeleteMyNote);
            ViewHolder =view;

        }

    }
}
