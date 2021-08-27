package com.example.smart_home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.awt.font.TextAttribute;

public class Temperatura extends AppCompatActivity {
    TextView tv_t, tv_h, estaT, estaH;

    DatabaseReference db_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatura);
        tv_t = findViewById(R.id.tv_temperatura);
        tv_h = findViewById(R.id.tv_humedad);
        estaT = findViewById(R.id.estadoT);
        estaH = findViewById(R.id.estadoH);

        instanciarBase();
        leer_tempertura();
    }

    public void instanciarBase(){
        db_reference = FirebaseDatabase.getInstance().getReference();
    }
    public void leer_tempertura(){
        db_reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String temp = String.valueOf(dataSnapshot.child("lecturas").child("temp").getValue());
                    String humed = String.valueOf(dataSnapshot.child("lecturas").child("humed").getValue());
                    if(!temp.isEmpty() && !humed.isEmpty()){
                        det_confort(Double.parseDouble(temp),Double.parseDouble(humed));
                    }
                } catch (NullPointerException n){
                    //toaster("Error al obtener los valores");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException().toString()); //El error puede no ser un String
            }
        });
    }

    //Función para determinar parametros de confort
    public void det_confort(Double t, Double h){

        if (t!=0.0){//Evaluar que el valor no sea 0, caso contrario notificar
            String estat_t ="n/a";
            tv_t.setText(String.valueOf(t));
            if(t >= 27 ){
                estat_t = "Peligro - Sofocante";
            } else if(t >=25 && t < 27){
                estat_t = "Caliente";
            }else if(t >=24 && t < 25){
                estat_t = "Confortable";
            }else if(t >=22 && t < 24){
                estat_t = "Frio";
            }else if(t < 22 ){
                estat_t = "Peligro - Muy frio";
            }
            estaT.setText(estat_t);
        }else {
            //toaster("Valores en 0, revise el dispositivo");
        }
        if (h!=0.0){        //Evaluar que el valor no sea 0, caso contrario notificar
            tv_h.setText(String.valueOf(h));
            String estat_h = "n/a";
            if(h >= 50 ){
                estat_h = "Peligro - Electricidad Estatica";
            } else if(h >= 33 && h <40){
                estat_h = "Confortable";
            }
            else{estat_h = "Muy bajo";}
            estaH.setText(estat_h);
        }else {
            //toaster("Valores en 0, revise el dispositivo");
        }
    }

    public void toaster(String msg){
        //Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    public void actualizar(){

    }



}