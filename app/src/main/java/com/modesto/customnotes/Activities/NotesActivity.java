package com.modesto.customnotes.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.modesto.customnotes.Adapters.NotesAdapters;
import com.modesto.customnotes.Models.Notes;
import com.modesto.customnotes.Providers.AuthProvider;
import com.modesto.customnotes.Providers.NotesProvider;
import com.modesto.customnotes.R;

import java.util.Date;

public class NotesActivity extends AppCompatActivity {

    ImageView mImageViewAddNotes;
    ImageView mImageViewBackN;

    String idorganizer;
    AuthProvider mAuth;
    NotesProvider mNotesProvider;
    NotesAdapters mNotesAdapter;
    RecyclerView mRecyclerViewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        idorganizer= getIntent().getStringExtra("id_organizer");
        mAuth= new AuthProvider();
        mNotesProvider = new NotesProvider();
        mRecyclerViewNote= findViewById(R.id.recicleViewNote);

        //Este linear layout manager lo que va hacer es que nos muestre las tarjetas una debajo de otra como si fuera
        //un linear layout Vertical
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        mRecyclerViewNote.setLayoutManager(linearLayoutManager);

        mImageViewAddNotes = findViewById(R.id.ImageViewAddNotes);
        mImageViewBackN = findViewById(R.id.ImageViewBackN);
        mImageViewAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreteNotes();
            }
        });
        mImageViewBackN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this, OrganizerActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getallNotesbyOrganizerbyUsers();
    }

    @Override
    public void onBackPressed() {

    }

    private void getallNotesbyOrganizerbyUsers() {
        Query query = mNotesProvider.getallNotesbyOrganizerbyUsers(mAuth.getUid(),idorganizer);

        //Creamos el FirestoreRecycleOptions que sea del modelo Post e inicializandolo, dentro mandamos la consulta
        //y el modelo que en este caso seria el modelo Post
        FirestoreRecyclerOptions<Notes> options = new FirestoreRecyclerOptions.Builder<Notes>()
                .setQuery(query,Notes.class)
                .build();

        //Instanciamos el postAdapters mandamos de parametro el options y el contexto de la clase
        mNotesAdapter = new NotesAdapters(options,this);

        //Pasamos el adapter de parametro en el recycleview para mostrarlo
        mRecyclerViewNote.setAdapter(mNotesAdapter);

        //Esto empezara a escuchar los cambios provenientes desde la base de datos
        mNotesAdapter.startListening();
    }

    private void CreteNotes(){
        //CREAMOS EL ALER DIALOG QUE NOS SERVIRA COO LA VENTANA EMERGENTE PARA AÑADIR UN COMENTARIO EN EL POST
        AlertDialog.Builder alert = new AlertDialog.Builder(NotesActivity.this);
        alert.setTitle("¡Nota!");
        alert.setMessage("Ingresa los datos de la nota");
        alert.setIcon(R.drawable.ic_inicio);
        alert.setCancelable(false);

        //ESTE SERA EL CAMPO DE TEXTO QUE NOS PERITA INGRESAR EL COMENTARIO
        final EditText edtxTitulo = new EditText(NotesActivity.this);

        //Creamos el texto descriptivo del editext
        edtxTitulo.setHint("Titulo");

        //ESTE PARAMS VA HACER EL ENCARGADO DE DARLE LOS MARGENES NECESARIOS A LA PROPIEDAD EDITEXT PARA QUE NO QUEDE TAN PEGADO A LAS
        //ESQUINAS
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        //AQUI SE LE OTORGAN LOS MARGENES REQUERIDOS
        params.setMargins(36,0,36,36);

        //SE ASIGNA EL PARAMS AL EDITTEXT QUE CONTIENE LOS MARGENES
        edtxTitulo.setLayoutParams(params);

        //PARA QUE ESOS MARGENES SE RESPETEN TENEMOS QUE CREAR UN RELATIVE LAYOUT
        RelativeLayout container = new RelativeLayout(NotesActivity.this);

        //Y TAMBIEN TENEMOS QUE CREAR UN RLATIVELAYOUT.LAYOUTPARAMS
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        //ESTE RELATIVE LAYOUT CONTENDRA EL RELATIVELAYOUT.PARAMS Y EL EDITTEXT CON LOS MARGENES ESTABLECIDOS Y ASI
        //MANDAREMOS DE PARAMETRO EL RELATIVE LAYOUT AL ALERT DIALOG Y QUE ASI SE RESPETE LOS PARAMETROS ANTES DADOS
        container.setLayoutParams(relativeParams);
        container.addView(edtxTitulo);

        //LE AÑADIMOS LA VISTA QUE ACABAMOS DE CREAR AL ALERT DIALOG QUE EN ESTE CASO SERIA EL EDITTEXT
        alert.setView(container);

        //QUE ES EL BOTON QUE SE VA A EJECUTAR CUANDO LA ACCION SEA OK O EN ESTE CASO CREAR COMENTARIO
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Este string sera el encargado de capturar el valor que el usuario ingreso en el campo de texto
                String titulo = edtxTitulo.getText().toString();
                //VALIDAMOS QUE EL VALOR QUE INGRESE EL USUARIO NO DEBE ESTAR VACIO
                if(!titulo.isEmpty()) {
                    insertNote(titulo);
                }else{
                    Toast.makeText(NotesActivity.this, "Favor de ingresar el nombre y descripcion de la nota", Toast.LENGTH_LONG).show();
                }
            }
        });

        //ESTE ES EL BOTON QUE SE VA A EJECUTAR CUANDO LA ACCION SE CANCELE LO DEJAMOS VACIO YA QUE CUANDO EL USUAARIO LE DE CANCELAR
        //SE VA HA CERRAR EL ALERDIALOG
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //Ejecutamos este metodo para que se muestre
        alert.show();

    }

    private void insertNote(String title) {
        Notes notes= new Notes();
        notes.setId_Organizador(idorganizer);
        notes.setId_Users(mAuth.getUid());
        notes.setTitle(title);
        notes.setInformacion("");
        notes.setTimestamp(new Date().getTime());

        mNotesProvider.createNotes(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NotesActivity.this, "La nota se a guardado con exito", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(NotesActivity.this, "No se pudo crear la notar", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




}