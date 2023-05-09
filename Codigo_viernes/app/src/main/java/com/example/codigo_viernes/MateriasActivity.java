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

public class MateriasActivity extends AppCompatActivity {

    EditText jetcodigo, jetmateria,jetprofesor, jetcreditos;
    CheckBox jcbactivo;
    String codigo, materia, profesor, creditos, idmateria, collection = "Materias" ;

    Byte sw, activo;

    // Nuevo es instanciar el Firebases en db
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materias);

        //Ocultar barra de titulo por defecto
        getSupportActionBar().hide();
        //Asocciar objetos Java con objetos Xml
        jetcodigo=findViewById(R.id.etcodigo);
        jetmateria=findViewById(R.id.etmateria);
        jetprofesor=findViewById(R.id.etprofesor);
        jetcreditos=findViewById(R.id.etcreditos);
        jcbactivo=findViewById(R.id.cbactivo);
        idmateria = "";
    }

    private void Consultar_Documento(){
        codigo=jetcodigo.getText().toString();
        if(codigo.isEmpty()){
            Toast.makeText(this, "Digite numero de Codigo, requerido para realizar consulta", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else{
            db.collection(collection)
                    .whereEqualTo("Codigo",codigo)
                    // get se va ir a buscar lo que especificamos en el where
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // variable gblobal para guardar la clave que se ve encriptada en la pagina firebase
                                    idmateria = document.getId(); // la llave
                                    //se esta mandando a la variable java
                                    // el registro que encontro, que es un documento y extraiga un string
                                    // de Nombre,Direccion, Semestre

                                    jetmateria.setText(document.getString("Materia"));
                                    jetprofesor.setText(document.getString("Profesor"));
                                    jetcreditos.setText(document.getString("Creditos"));
                                    if(document.getString("Activo").equals("Si"))
                                    {
                                        jcbactivo.setChecked(true);

                                        jetcodigo.setEnabled(false);
                                        jetmateria.setEnabled(true);
                                        jetprofesor.setEnabled(true);
                                        jetcreditos.setEnabled(true);

                                        activo = 1;
                                    }
                                    else
                                    {jcbactivo.setChecked(false);
                                        activo = 0;}
                                    }

                                materia=jetmateria.getText().toString();
                                if(materia.equals("")){
                                    sw = 0;
                                    jetcodigo.setEnabled(false);
                                    jetmateria.setEnabled(true);
                                    jetprofesor.setEnabled(true);
                                    jetcreditos.setEnabled(true);
                                    Toast.makeText(MateriasActivity.this,"No se encuentra codigo digitado",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    sw = 1;
                                    Toast.makeText(MateriasActivity.this,"Si se encuentra codigo digitado",Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(MateriasActivity.this, "Error consulta", Toast.LENGTH_SHORT).show();
                                //Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    public void Modificar(View view){
        //ingresa la informacion del texto en la valriable java
        codigo=jetcodigo.getText().toString();
        materia=jetmateria.getText().toString();
        profesor=jetprofesor.getText().toString();
        creditos=jetcreditos.getText().toString();

        //Validar que esten todos los datos
        if (codigo.isEmpty() || materia.isEmpty() || profesor.isEmpty() || creditos.isEmpty()){
            Toast.makeText(this,"Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else {
            Map<String, Object> estudiante = new HashMap<>();
            estudiante.put("Codigo", codigo);
            estudiante.put("Materia", materia);
            estudiante.put("Profesor", profesor);
            estudiante.put("Creditos", creditos);
            estudiante.put("Activo", "Si");

            if(sw == 0){
            db.collection(collection).add(estudiante).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MateriasActivity.this, "Documento Guardado", Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Log.w(TAG, "Error adding document", e);
                            Toast.makeText(MateriasActivity.this, "Error Guardando Documento", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
            else {
                db.collection(collection).document(idmateria)
                        .set(estudiante)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MateriasActivity.this, "Documento Editado", Toast.LENGTH_SHORT).show();
                                Limpiar_campos();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MateriasActivity.this, "Error Editando Documento", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    } // fin modificar

    public void Consultar(View view){
        Consultar_Documento();

    }

    public void Eliminar(View view){
        if (!idmateria.equals(""))
        {
            db.collection(collection).document(idmateria)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Limpiar_campos();
                            Toast.makeText(MateriasActivity.this,"Materia eliminado correctamente...",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MateriasActivity.this,"Error eliminando materia...",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(MateriasActivity.this,"Debe primero consultar para eliminar",Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();

        }
    } // fin eliminar

    public void Anular(View view){

            //ingresa la informacion del texto en la valriable java
            codigo=jetcodigo.getText().toString();
            materia=jetmateria.getText().toString();
            profesor=jetprofesor.getText().toString();
            creditos=jetcreditos.getText().toString();

            if (!materia.equals("")){

            //Validar que esten todos los datos
            if (codigo.isEmpty() || materia.isEmpty() || profesor.isEmpty() || creditos.isEmpty()){
                Toast.makeText(this,"Realizar Primero Consulta", Toast.LENGTH_SHORT).show();
                jetcodigo.requestFocus();
            }
            else {
                Map<String, Object> estudiante = new HashMap<>();
                estudiante.put("Codigo", codigo);
                estudiante.put("Materia", materia);
                estudiante.put("Profesor", profesor);
                estudiante.put("Creditos", creditos);
                if(activo == 1)
                    estudiante.put("Activo", "No");
                else
                    estudiante.put("Activo", "Si");

                //Modify document
                db.collection(collection).document(idmateria)
                        .set(estudiante)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(activo == 1)
                                {Toast.makeText(MateriasActivity.this, "Documento Desactivado", Toast.LENGTH_SHORT).show();
                                Limpiar_campos();}
                                else
                                {Toast.makeText(MateriasActivity.this, "Documento Activado", Toast.LENGTH_SHORT).show();
                                    Limpiar_campos();}
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MateriasActivity.this, "Error Activacion/Desactivacion", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else{
            Toast.makeText(MateriasActivity.this,"Debe primero consultar para Anular",Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
    }

    public void Cancelar(View view)
    {
        Limpiar_campos();
        jetcodigo.setEnabled(true);
    }


    private void Limpiar_campos(){

        jetcodigo.setText("");
        jetmateria.setText("");
        jetprofesor.setText("");
        jetcreditos.setText("");
        jcbactivo.setChecked(false);
        jetcodigo.requestFocus();

        jetcodigo.setEnabled(true);
        jetmateria.setEnabled(false);
        jetprofesor.setEnabled(false);
        jetcreditos.setEnabled(false);

    }// limpiar

    public  void Regresar(View view) {
        Intent intregresar = new Intent(this,MainActivity.class);
        startActivity(intregresar);
    }

    public  void Verificacion(View view) {
        //Toast.makeText(MateriasActivity.this, "Primero realizar la consulta", Toast.LENGTH_SHORT).show();
    }



}// fin codigo

