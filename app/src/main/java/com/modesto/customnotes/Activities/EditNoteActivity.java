package com.modesto.customnotes.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.UploadTask;
import com.modesto.customnotes.Adapters.ImageAdapters;
import com.modesto.customnotes.Adapters.NotesAdapters;
import com.modesto.customnotes.Models.ImageR;
import com.modesto.customnotes.Models.Notes;
import com.modesto.customnotes.Providers.AuthProvider;
import com.modesto.customnotes.Providers.ImageProvider;
import com.modesto.customnotes.Providers.NotesProvider;
import com.modesto.customnotes.Providers.UploadImageProvider;
import com.modesto.customnotes.R;
import com.modesto.customnotes.Utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class EditNoteActivity extends AppCompatActivity {

    String idnotes;
    String idorganizer;
    String title;
    AuthProvider mAuth;
    TextInputEditText mTextInputContentNote;
    Button mButtonSave;
    ImageView mImageViewBack;
    ImageView mImageViewPhoto;
    ImageView mImageViewZoom;
    RecyclerView mRecicleViewImage;
    TextView mTextViewTittle;

    NotesProvider mNotesProvider;
    ImageProvider mImageProvider;
    UploadImageProvider muploadImageProvider;

    AlertDialog mDialog;

    private final int GALERY_REQUEST_CODE= 1;
    private final int PHOTO_REQUEST_CODE= 2;
    AlertDialog.Builder mBuildersSelector;
    CharSequence options[];

    //Estas variables las ocupareemos para utilizar la camara en nestra aplicacion LA PHOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    //Es el archivvo que vamos a estar almacenando en firebase storage
    File mImageFile;

    ImageAdapters mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mTextInputContentNote = findViewById(R.id.textInputContenidoNotes);
        mTextViewTittle = findViewById(R.id.TextViewtittleNote);
        mImageViewBack = findViewById(R.id.ImageViewBack);
        mImageViewPhoto = findViewById(R.id.ImageViewPhoto);
        mImageViewZoom = findViewById(R.id.ImageViewZoom);
        mButtonSave = findViewById(R.id.ButtonSaveContentNote);
        mRecicleViewImage = findViewById(R.id.recicleViewImage);

        idnotes= getIntent().getStringExtra("id_notes");
        title= getIntent().getStringExtra("title");
        idorganizer= getIntent().getStringExtra("id_organizer");

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento....")
                .setCancelable(false) .build();

        mAuth= new AuthProvider();
        mNotesProvider = new NotesProvider();
        mImageProvider = new ImageProvider();
        muploadImageProvider = new UploadImageProvider();


        //UInicializamos el alertDialog.Builder
        mBuildersSelector = new AlertDialog.Builder(this);
        mBuildersSelector.setTitle("Selecciona una opcion");
        options = new CharSequence[]{"Imagen de galeria","Tomar foto"};


        mTextViewTittle.setText(title);
        ContentNote();

        mRecicleViewImage.setLayoutManager(new GridLayoutManager(EditNoteActivity.this,3));

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarContentNote();
            }
        });

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(EditNoteActivity.this, NotesActivity.class);
                    intent.putExtra("id_organizer",idorganizer);
                    startActivity(intent);
                    finish();

            }
        });

        mImageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage();
            }
        });

        mImageViewZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditNoteActivity.this,ZoomActivity.class);
                intent.putExtra("contentNote",mTextInputContentNote.getText().toString());
                intent.putExtra("id_notes",idnotes);
                intent.putExtra("title",title);
                intent.putExtra("id_organizer",idorganizer);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllImagesbyNotes();
    }

    private void getAllImagesbyNotes() {
        Query query = muploadImageProvider.getAllImagesbyNotes(idnotes);

        //Creamos el FirestoreRecycleOptions que sea del modelo Post e inicializandolo, dentro mandamos la consulta
        //y el modelo que en este caso seria el modelo Post
        FirestoreRecyclerOptions<ImageR> options = new FirestoreRecyclerOptions.Builder<ImageR>()
                .setQuery(query,ImageR.class)
                .build();

        //Instanciamos el postAdapters mandamos de parametro el options y el contexto de la clase
        mImageAdapter = new ImageAdapters(options,this, title, idorganizer);

        //Pasamos el adapter de parametro en el recycleview para mostrarlo
        mRecicleViewImage.setAdapter(mImageAdapter);

        //Esto empezara a escuchar los cambios provenientes desde la base de datos
        mImageAdapter.startListening();
    }

    private void selectOptionImage() {

        mBuildersSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    opengalery(GALERY_REQUEST_CODE);
                }else if(i == 1){
                    takePhoto(PHOTO_REQUEST_CODE);
                }
            }
        });
        mBuildersSelector.show();
    }

    private void takePhoto(int photo_request_code) {
        //Lo principal de la logica para utilizar la camara es usar un Intent el cual recive de parametro
        //Media Store
        Intent takePictureInten = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Aqui vamos a encontrar la logica para tomar la foto
        if(takePictureInten.resolveActivity(getPackageManager())!= null){
            File photoFile = null;
            //Esta exception es porque trabajaremos con archivos
            try{
                photoFile = createPhotofile(photo_request_code);
            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo "+e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if(photoFile != null){

                //Esto recibe el contexto que seria la clase post en este caso y tambien recibe algo llamado Autority y
                //en este caso el autority seria el nombre de nuestro paquete y al final el archivo
                Uri photoUri = FileProvider.getUriForFile(EditNoteActivity.this,"com.modesto.customnotes",photoFile);
                takePictureInten.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                //Recordar que el metodo recibe un request code que seria el canal y tambien que se debe sobre escribir el
                //metodo onActivityForResult que espera la accion del usuario en este cas seria tomar la photo
                startActivityForResult(takePictureInten,photo_request_code);
            }
        }
    }

    //Este metodo lo necesitamos para crear el archivo de la imagen
    private File createPhotofile(int requestcode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //Lo primero que recibe es el nombre con el que se va a guardar el archivo, segundo la extencion del archivo
        //Y por ultimo el Storge dir que si observamos apunta al directorio de Pictures
        File photoFile = File.createTempFile(
                new Date() +"_photo",
                ".jpg",
                storageDir
        );
        //Validamos cual es el request code si el del 1 o 2 para saber donde mostrar la imagen
        if(requestcode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:"+photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void opengalery(int galery_request_code) {
        //Con este intent podemos abrir la galerya filtando solo por image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");

        //Recibe el inten y el requeste code que e request code es solo un numero entero que identifica cual va hacer la
        //accion que va a ejecutar el usuario, el metodo activityForResult necesita obligatoriamente sobreescribir el
        //metodo onActivityResult
        startActivityForResult(galleryIntent,galery_request_code);
    }

    //En este metodo vamos a estar esperando la accion que el usuario realice en la aplicacion que en este caso seria
    //la accion de ir a la galeria y seleccionar una foto dependiendo de que GALERY_REQUEST_CODE sea
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Esto se hace si la accion que esta reealizando el usuario es igual a la de seleccionar la imagen de una galeria
        //y tambien verificamos si el resultcode es igual a result_ok ya que esto significa que todooo fue bien a la hora de
        //seleccionar la imagen desde la galeria
        if(requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK){
            //La exception es porque trabajaremos con archivos
            try{
                //esto lo hacemos para evitar inconvenientes
                mPhotoFile = null;

                //Esto nos va a transformar la Uri en este archivo mImageFile es para ello que usamos la clase FileUtils
                mImageFile = FileUtils.from(this, data.getData());

                clickImage();

            }catch (Exception e){
                Log.d("ERROR","Se produjo un error "+e.getMessage());
                Toast.makeText(this, "Se produjo un error "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        //SELECCION DE FOTOGRAFIA
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){

            mImageFile = null;

            //Pasamos el archivo en el photofile para despues hacer las validaciones necesarias al mandarlo a la base de datos
            mPhotoFile= new File(mAbsolutePhotoPath);

            clickImage();

        }
    
    }

    private void clickImage() {

        if(mImageFile != null){
            saveImage(mImageFile);
        }
        else if(mPhotoFile != null){
            saveImage(mPhotoFile);
        }

    }

    private void saveImage(File mImageFile) {
        mDialog.show();
        mImageProvider.save(EditNoteActivity.this,mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage) {
                if(taskImage.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Aqui ya almacenamos u obtenemos la url de la imagen y ahora la almasenaremos en la database
                             String url = uri.toString();

                            ImageR imageR = new ImageR();
                            imageR.setId_Note(idnotes);
                            imageR.setTimestamp(new Date().getTime());
                            imageR.setUri(url);

                            muploadImageProvider.saveImage(imageR).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {
                                    mDialog.dismiss();
                                    if(taskSave.isSuccessful()){
                                        Toast.makeText(EditNoteActivity.this, "La imagen ha sido guardada con exito", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(EditNoteActivity.this, "La imagen no se ha guardado", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    });
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(EditNoteActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ContentNote() {
        mNotesProvider.getInformacionNotes(idnotes).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                if(documentSnapshot.contains("informacion")){

                    String informacion = documentSnapshot.getString("informacion");
                    if(!informacion.isEmpty() && !informacion.equals(null)){
                        mTextInputContentNote.setText(informacion);
                    }
                }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    private void GuardarContentNote() {
        String contenido = mTextInputContentNote.getText().toString();
        mNotesProvider.updateNotesInformacion(idnotes,contenido).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditNoteActivity.this, "La informacion se guardo de manera correcta", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditNoteActivity.this, "No se ha podido guardar de forma correcta la informacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}