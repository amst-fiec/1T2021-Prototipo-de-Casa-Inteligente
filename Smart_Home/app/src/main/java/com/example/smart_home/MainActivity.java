package com.example.smart_home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnIniciarS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnIniciarS = (Button) findViewById(R.id.btnIniciarS);
    }

    public void IniciarSesion(){
        Intent intent = new Intent(this, InicioSesion.class);
        startActivity(intent);
    }
}