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
import com.modesto.customnotes.Activities.NotesActivity;
import com.modesto.customnotes.Models.Organizer;
import com.modesto.customnotes.Providers.NotesProvider;
import com.modesto.customnotes.Providers.OrganizerProvider;
import com.modesto.customnotes.R;
import com.modesto.customnotes.Utils.RelativeTime;

public class OrganizerAdapter extends FirestoreRecyclerAdapter<Organizer,OrganizerAdapter.ViewHolder> {

    Context context;
    OrganizerProvider mOrganizerPorvider;
    NotesProvider mNotesProviders;

    public OrganizerAdapter(@NonNull FirestoreRecyclerOptions<Organizer> options,Context context ) {
        super(options);
        this.context= context;
        mOrganizerPorvider = new OrganizerProvider();
        mNotesProviders = new NotesProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final Organizer organizer) {

        DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(position);

        final String OrganizerId = documentSnapshot.getId();

        String relativeTime = RelativeTime.getTimeAgo(organizer.getTimestamp(),context);
        holder.textViewRelativeTime.setText("Se creo "+relativeTime);
        holder.textViewTitle.setText(organizer.getName_Category());

        /*ESTE EVENTO SE LLEVA ACABO CUANDO DEJAS SOSTENIDO EL CLICK VERE QUE HACER CON EL MAS ADELANTE EN DETALLES DE ESTETICA
        holder.ViewHolder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });*/

        holder.ViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, NotesActivity.class);
                intent.putExtra("id_organizer",OrganizerId);
                context.startActivity(intent);
            }
        });

        holder.imageViewDeleteOrganizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowConfirmDelete(OrganizerId);
            }
        });

    }

    private void ShowConfirmDelete(final String organizerId) {
        //Creamos la alerta con el alertDialog.Builder le agregamos el icono ic_dialog_alog es un icono de alerta
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Eliminar Organizador")
                .setMessage("¿Estas seguro de realizar esta accion?")
                //AGREGAMOS EL BOTON POSTIVO
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOrganizer(organizerId);
                    }
                })
                //AGREGAMOS EL BPTPN NEGATIVO
                .setNegativeButton("NO", null)
                .show();
    }

    private void deleteOrganizer(String organizerId) {

        mOrganizerPorvider.DeleteOrganizer(organizerId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "El Organizador ha sido eliminado con exito", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "El Organizador no se pudo eliminar correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardvieworganizer,parent,false);
    return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewRelativeTime;
        ImageView imageViewDeleteOrganizer;
        View ViewHolder;
        public ViewHolder(@NonNull View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitleMyOrganizer);
            textViewRelativeTime = view.findViewById(R.id.textViewRelativeTimeMyOrganizater);
            imageViewDeleteOrganizer = view.findViewById(R.id.imageViewDeleteMyOrganizer);
            ViewHolder =view;
        }
    }
}
