package com.example.smart_home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu_Consultas extends AppCompatActivity {

    Button btn_consultar_estado, btn_analizar_temperatura;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_consultas);

        btn_consultar_estado = (Button) findViewById(R.id.btn_consultar_estado);
        btn_analizar_temperatura = (Button) findViewById(R.id.btn_analizar_temperatura);
    }

    //Función que pasa al Activity de visualización del estado del hogar
    public void Consultar_Estado(View v){
        Intent intent = new Intent(this, Estado_Hogar.class);
        startActivity(intent);

    }

    //Función que pasa al activity de visualizaión y análisis de la temperatura
    public void show_Analizar_Tmperatura(View v){
        Intent intent = new Intent(this, Temperatura.class);
        startActivity(intent);


    }
    public void cerrarSesion(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

}