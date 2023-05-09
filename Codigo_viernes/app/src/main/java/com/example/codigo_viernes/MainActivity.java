package com.example.codigo_viernes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public  void Estudiantes(View view) {
        Intent intestudiantes = new Intent(this, EstudianteActivity.class);
        startActivity(intestudiantes);
    }

    public  void Materias(View view) {
        Intent intmaterias = new Intent(this,MateriasActivity.class);
        startActivity(intmaterias);
    }

    public  void Matriculas(View view) {
        Intent intmatriculas = new Intent(this,MatriculasActivity.class);
        startActivity(intmatriculas);
    }


}