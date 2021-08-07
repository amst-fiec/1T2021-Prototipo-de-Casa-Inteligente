package com.example.smart_home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnIniciarS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIniciarS = (Button) findViewById(R.id.btnIniciarS);
    }
    public void IniciarSesion(View v){
        Intent intent = new Intent(this, Inicio_Sesion.class);
        startActivity(intent);
    }

}