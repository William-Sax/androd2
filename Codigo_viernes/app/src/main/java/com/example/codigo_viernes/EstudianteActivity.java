package com.example.codigo_viernes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EstudianteActivity extends AppCompatActivity {

    EditText jetcarnet, jetnombre,jetcarrera, jetsemestre;
    CheckBox jcbactivo;
    String carnet, nombre,carrera,semestre, idestudiante;
    // Nuevo es instanciar el Firebases en db
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante);
        //Ocultar barra de titulo por defecto
        getSupportActionBar().hide();
        //Asocciar objetos Java con objetos Xml
        jetcarnet=findViewById(R.id.etcarnet);
        jetnombre=findViewById(R.id.etnombre);
        jetcarrera=findViewById(R.id.etcarrera);
        jetsemestre=findViewById(R.id.etsemestre);
        jcbactivo=findViewById(R.id.cbactivo);
        idestudiante = "";
    }

    public void Adicionar(View view){
        carnet=jetcarnet.getText().toString();
        nombre=jetnombre.getText().toString();
        carrera=jetcarrera.getText().toString();
        semestre=jetsemestre.getText().toString();
        if(carnet.isEmpty() || nombre.isEmpty() || carrera.isEmpty() || semestre.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }else{
            // Create a new student with a first and last name
            Map<String, Object> estudiante = new HashMap<>();
            estudiante.put("Carnet", carnet);
            estudiante.put("Nombre", nombre);
            estudiante.put("Carrera", carrera);
            estudiante.put("Semestre", semestre);
            estudiante.put("Activo", "Si");

            // Add a new document with a generated ID
            db.collection("Estudiantes")
                    .add(estudiante)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(EstudianteActivity.this, "Documento adicionado", Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Log.w(TAG, "Error adding document", e);
                            Toast.makeText(EstudianteActivity.this, "Error adicionando documento", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }//fin Metodo Adicionar

    private void Consultar_Documento(){
        carnet=jetcarnet.getText().toString();
        if(carnet.isEmpty()){
            Toast.makeText(this, "Digite numero de Carnet, requerido para realizar consulta", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }
        else{
            db.collection("Estudiantes")
                    .whereEqualTo("Carnet",carnet)
                    // get se va ir a buscar lo que especificacmos en el where
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                   // Log.d(TAG, document.getId() + " => " + document.getData());
                                    // variable gblobal para guardar la clave que se ve encriptada en la pagina firebase
                                    idestudiante = document.getId(); // la llave
                                    //se esta mandando a la variable java
                                    // el registro que encontro, que es un documento y extraiga un string
                                    // de Nombre,Direccion, Semestre
                                    jetnombre.setText(document.getString("Nombre"));
                                    jetcarrera.setText(document.getString("Carrera"));
                                    jetsemestre.setText(document.getString("Semestre"));
                                    if(document.getString("Activo").equals("Si"))
                                    {
                                        jcbactivo.setChecked(true);
                                    }
                                    else
                                        jcbactivo.setChecked(false);
                                }
                            } else {
                                Toast.makeText(EstudianteActivity.this, "Error consulta", Toast.LENGTH_SHORT).show();
                                //Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    // Clase 21/04/23

    // por ser noSQL recordar que hay que traer toda la informacion aunque no la necesitemos porque sino terina eliminado
    // la informacion que no se traiga

    public void Modificar(View view){
        //ingresa la informacion del texto en la valriable java
        carnet=jetcarnet.getText().toString();
        nombre=jetnombre.getText().toString();
        carrera=jetcarrera.getText().toString();
        semestre=jetsemestre.getText().toString();

        //Validar que esten todos los datos
        if (carnet.isEmpty() || nombre.isEmpty() || carrera.isEmpty() || semestre.isEmpty()){
            Toast.makeText(this,"Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }
        else

        {   Map<String, Object> estudiante = new HashMap<>();
            estudiante.put("Carnet", carnet);
            estudiante.put("Nombre", nombre);
            estudiante.put("Carrera", carrera);
            estudiante.put("Semestre", semestre);
            estudiante.put("Activo", "Si");
            //Modify document
            db.collection("Estudiantes").document(idestudiante)
                    .set(estudiante)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EstudianteActivity.this,"Documento Actualizado...",Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EstudianteActivity.this,"Error actualizando estudiante...",Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    } // fin modificar

    public void Consultar(View view){
        Consultar_Documento();
    }


    public void Eliminar(View view){
        if (!idestudiante.equals(""))
        {
            db.collection("Estudiantes").document(idestudiante)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Limpiar_campos();
                            Toast.makeText(EstudianteActivity.this,"Estudiante eliminado correctamente...",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EstudianteActivity.this,"Error eliminando estudiante...",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(EstudianteActivity.this,"Debe primero consultar para eliminar",Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();

        }
    } // fin eliminar


    public void Anular(View view){
        if (!idestudiante.equals("")){

            //ingresa la informacion del texto en la valriable java
            carnet=jetcarnet.getText().toString();
            nombre=jetnombre.getText().toString();
            carrera=jetcarrera.getText().toString();
            semestre=jetsemestre.getText().toString();

            //Validar que esten todos los datos
            if (carnet.isEmpty() || nombre.isEmpty() || carrera.isEmpty() || semestre.isEmpty()){
                Toast.makeText(this,"Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
                jetcarnet.requestFocus();
            }
            else {
                Map<String, Object> estudiante = new HashMap<>();
                estudiante.put("Carnet", carnet);
                estudiante.put("Nombre", nombre);
                estudiante.put("Carrera", carrera);
                estudiante.put("Semestre", semestre);
                estudiante.put("Activo", "No"); // modificar condicionar verificando si esta checkeado o no
                //Modify document
                db.collection("Estudiantes").document(idestudiante)
                        .set(estudiante)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EstudianteActivity.this, "Documento Actualizado...", Toast.LENGTH_SHORT).show();
                                Limpiar_campos();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EstudianteActivity.this, "Error actualizando estudiante...", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else{
            Toast.makeText(EstudianteActivity.this,"Debe primero consultar para eliminar",Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }
    }


    private void Limpiar_campos(){
        jetcarnet.setText("");
        jetsemestre.setText("");
        jetcarrera.setText("");
        jetnombre.setText("");
        jcbactivo.setChecked(false);
        jetcarnet.setText("");
        jetcarnet.requestFocus();

    }

    public  void Regresar(View view) {
        Intent intregresar = new Intent(this,MainActivity.class);
        startActivity(intregresar);
    }


}