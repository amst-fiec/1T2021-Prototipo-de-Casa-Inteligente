package com.example.smart_home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Estado_Hogar extends AppCompatActivity {

    DatabaseReference db_reference;

    TextView tv_temp, tv_hume, tv_sound, tv_dht11, tv_gas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_hogar);
        db_reference = FirebaseDatabase.getInstance().getReference().child("Lecturas");
        iniciarBaseDeDatos();
        leer_sensores();
    }

    //Funcion para hacer la lectura a la base
    public void leer_sensores(){
        db_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mostrarLecturasGasSound(snapshot);
                    det_confort(snapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

    public void iniciarBaseDeDatos(){
        db_reference = FirebaseDatabase.getInstance().getReference().child("Lecturas");
    }
    //Funcion para mostrar parametros de los sensores de gas y sonido
    public void mostrarLecturasGasSound(DataSnapshot snapshot){

        String aux_sound, aux_gas;

        aux_sound = String.valueOf(snapshot.child("Lecturas").child("KY38").child("level_s").getValue());
        aux_gas = String.valueOf(snapshot.child("Lecturas").child("MQ2").child("level_g").getValue());

        tv_sound = findViewById(R.id.tv_sound);
        tv_gas = findViewById(R.id.tv_gas);

        tv_sound.setText(aux_sound);
        tv_gas.setText(aux_gas);
    }

    //HU2 - Act4
    //Función para determinar parametros de confort
    public void det_confort(DataSnapshot snapshot){

        float aux_temp, aux_humed;
        aux_temp = (float) snapshot.child("Lecturas").child("DHT11").child("temp").getValue();
        aux_humed = (float) snapshot.child("Lecturas").child("DHT11").child("humed").getValue();

        tv_temp = findViewById(R.id.tv_temp);
        tv_hume = findViewById(R.id.tv_hume);
        String estat_t ="n/a", estat_h = "n/a";

        if(aux_temp > 27 ){
            estat_t = "Peligro - Sofocante";
        } else if(aux_temp < 27 && aux_temp >25){
            estat_t = "Caliente";
        }else if(aux_temp < 25 && aux_temp >24){
            estat_t = "Confortable";
        }else if(aux_temp < 24 && aux_temp >22){
            estat_t = "Frio";
        }else if(aux_temp < 22 ){
            estat_t = "Peligro - Muy frio";
        }

        if(aux_humed >= 50 ){
            estat_h = "Peligro - Electricidad Estatica";
        } else if(aux_humed >= 33 && aux_humed <40){
            estat_h = "Confortable";
        }

        tv_temp.setText(estat_t);
        tv_hume.setText(estat_h);
    }

    //HU2 - Act5
    //Función para determinar fuga de gas
    public void eval_Gas(){

    }

    //HU2 - Act6
    //Función para determinar avería en el desague
    public void eval_Tuberia(DataSnapshot snapshot){
        float aux_sound;
        aux_sound = (float) snapshot.child("Lecturas").child("KY38").child("level_s").getValue();
            if(aux_sound < 400){
                String titulo = "Obstruccion";
                String alerta = "Se ha detectado una tuberia tapada";
                int n_id = 1;
                notificarFallo(titulo, alerta, n_id);
            }
    }

    //Funcion que crea notificaciones
    public void notificarFallo(String titulo, String mensaje, int n){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.alert_toilet)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Revisar..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(n, builder.build());
    }



}