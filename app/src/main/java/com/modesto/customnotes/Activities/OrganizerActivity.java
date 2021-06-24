package com.modesto.customnotes.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.modesto.customnotes.Adapters.OrganizerAdapter;
import com.modesto.customnotes.Models.Organizer;
import com.modesto.customnotes.Providers.AuthProvider;
import com.modesto.customnotes.Providers.OrganizerProvider;
import com.modesto.customnotes.Providers.UsersProvider;
import com.modesto.customnotes.R;

import java.util.Date;

public class OrganizerActivity extends AppCompatActivity {

    FloatingActionButton mFab;
    RecyclerView mRecyclerViewHome;
    AuthProvider mAuthProvider;
    OrganizerProvider mOrganizerProvider;
    OrganizerAdapter mOrganizaterAdapter;
    TextView mTextViewUserName;
    UsersProvider mUsersProvider;
    Button mButtonSalir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        mRecyclerViewHome= findViewById(R.id.recicleViewHome);
        mFab= findViewById(R.id.fab);
        mTextViewUserName = findViewById(R.id.TextViewUserName);
        mButtonSalir = findViewById(R.id.ButtonSalir);

        mAuthProvider= new AuthProvider();
        mOrganizerProvider = new OrganizerProvider();
        mUsersProvider= new UsersProvider();

        //Este linear layout manager lo que va hacer es que nos muestre las tarjetas una debajo de otra como si fuera
        //un linear layout Vertical
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        mRecyclerViewHome.setLayoutManager(linearLayoutManager);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearOrganizer();
            }
        });
        mButtonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        CargarUserName();
    }

    private void CargarUserName() {
        mUsersProvider.getUserName(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.contains("username")){

                    String username= documentSnapshot.getString("username").toString();

                    if(!username.isEmpty()){
                        mTextViewUserName.setText("Bienvenido\n"+username);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllOrganizerbyUser();
    }

    @Override
    public void onBackPressed() {

    }

    private void getAllOrganizerbyUser() {
        Query query = mOrganizerProvider.getAllOrganizerbyUser(mAuthProvider.getUid());

        //Creamos el FirestoreRecycleOptions que sea del modelo Post e inicializandolo, dentro mandamos la consulta
        //y el modelo que en este caso seria el modelo Post
        FirestoreRecyclerOptions<Organizer> options = new FirestoreRecyclerOptions.Builder<Organizer>()
                .setQuery(query,Organizer.class)
                .build();

        //Instanciamos el postAdapters mandamos de parametro el options y el contexto de la clase
        mOrganizaterAdapter = new OrganizerAdapter(options,this);

        //Pasamos el adapter de parametro en el recycleview para mostrarlo
        mRecyclerViewHome.setAdapter(mOrganizaterAdapter);

        //Esto empezara a escuchar los cambios provenientes desde la base de datos
        mOrganizaterAdapter.startListening();
    }

    private void CrearOrganizer() {
        //CREAMOS EL ALER DIALOG QUE NOS SERVIRA COO LA VENTANA EMERGENTE PARA AÑADIR UN COMENTARIO EN EL POST
        AlertDialog.Builder alert = new AlertDialog.Builder(OrganizerActivity.this);
        alert.setTitle("¡Organizador!");
        alert.setMessage("Ingresa El nombre del organizador");
        alert.setIcon(R.drawable.ic_inicio);
        alert.setCancelable(false);

        //ESTE SERA EL CAMPO DE TEXTO QUE NOS PERITA INGRESAR EL COMENTARIO
        final EditText editText = new EditText(OrganizerActivity.this);

        //Creamos el texto descriptivo del editext
        editText.setHint("Contenido");

        //ESTE PARAMS VA HACER EL ENCARGADO DE DARLE LOS MARGENES NECESARIOS A LA PROPIEDAD EDITEXT PARA QUE NO QUEDE TAN PEGADO A LAS
        //ESQUINAS
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        //AQUI SE LE OTORGAN LOS MARGENES REQUERIDOS
        params.setMargins(36,0,36,36);

        //SE ASIGNA EL PARAMS AL EDITTEXT QUE CONTIENE LOS MARGENES
        editText.setLayoutParams(params);

        //PARA QUE ESOS MARGENES SE RESPETEN TENEMOS QUE CREAR UN RELATIVE LAYOUT
        RelativeLayout container = new RelativeLayout(OrganizerActivity.this);

        //Y TAMBIEN TENEMOS QUE CREAR UN RLATIVELAYOUT.LAYOUTPARAMS
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        //ESTE RELATIVE LAYOUT CONTENDRA EL RELATIVELAYOUT.PARAMS Y EL EDITTEXT CON LOS MARGENES ESTABLECIDOS Y ASI
        //MANDAREMOS DE PARAMETRO EL RELATIVE LAYOUT AL ALERT DIALOG Y QUE ASI SE RESPETE LOS PARAMETROS ANTES DADOS
        container.setLayoutParams(relativeParams);
        container.addView(editText);

        //LE AÑADIMOS LA VISTA QUE ACABAMOS DE CREAR AL ALERT DIALOG QUE EN ESTE CASO SERIA EL EDITTEXT
        alert.setView(container);

        //QUE ES EL BOTON QUE SE VA A EJECUTAR CUANDO LA ACCION SEA OK O EN ESTE CASO CREAR COMENTARIO
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Este string sera el encargado de capturar el valor que el usuario ingreso en el campo de texto
                String value = editText.getText().toString();
                //VALIDAMOS QUE EL VALOR QUE INGRESE EL USUARIO NO DEBE ESTAR VACIO
                if(!value.isEmpty()) {
                    createOrganizer(value);
                }else{
                    Toast.makeText(OrganizerActivity.this, "Favor de ingresar el nombre del organizador", Toast.LENGTH_SHORT).show();
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

    private void createOrganizer(String value) {
        Organizer organizer= new Organizer();
        organizer.setId_Users(mAuthProvider.getUid());
        organizer.setName_Category(value);
        organizer.setTimestamp(new Date().getTime());
        mOrganizerProvider.save(organizer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(OrganizerActivity.this, "El Organizador se agrego correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(OrganizerActivity.this, "No se pudo crear el organizador", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}