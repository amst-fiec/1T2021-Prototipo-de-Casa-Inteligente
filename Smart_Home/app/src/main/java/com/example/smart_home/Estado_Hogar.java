package com.example.smart_home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Estado_Hogar extends AppCompatActivity {

    DatabaseReference db_reference;
    String temperatura, humedad, gas_val, bateria, sonido;
    String estat_temp, estat_humed, estat_gas, estat_bat;

    TextView tv_temp, tv_hume, tv_sound, tv_gas, tv_dht, tv_bat;
    ValueEventListener listener;

    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_hogar);
        createNotificationChannel();
        //Instancia de la base
        instanciarBase();
        leer_tempertura();
        leer_gas_sound();
        leer_bateria();

        //leer_sensores();
    }
    /*
    @Override
    protected void onStop() {
        super.onStop();
        db_reference.removeEventListener(listener);
    }*/
    public void instanciarBase(){
        db_reference = FirebaseDatabase.getInstance().getReference();
    }
    public void leer_tempertura(){
        //El error puede no ser un String
        listener = db_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String temp = String.valueOf(dataSnapshot.child("lecturas").child("temp").getValue());
                    temperatura = temp;
                    String humed = String.valueOf(dataSnapshot.child("lecturas").child("humed").getValue());
                    humedad = humed;
                    if (!temp.isEmpty() && !humed.isEmpty()) {
                        det_confort(Double.parseDouble(temp), Double.parseDouble(humed));
                    }
                } catch (NullPointerException n) {
                    toaster("Error al obtener los valores");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException().toString()); //El error puede no ser un String
            }
        });
    }
    public void leer_gas_sound(){
        db_reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String gas = String.valueOf(dataSnapshot.child("lecturas").child("gas").getValue());
                    gas_val = gas;
                    String sound = String.valueOf(dataSnapshot.child("lecturas").child("sound").getValue());
                    sonido = sound;
                    if(!gas.isEmpty() && !sound.isEmpty()){
                        mostrarLecturasGasSound(Double.parseDouble(gas),Double.parseDouble(sound));
                        eval_Tuberia(Double.parseDouble(sound));
                    }
                } catch (NullPointerException n){
                    toaster("Error al obtener los valores");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException().toString()); //El error puede no ser un String
            }
        });
    }
    public void leer_bateria(){

        db_reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String bat = String.valueOf(dataSnapshot.child("lecturas").child("bat").getValue());
                    if(!bat.isEmpty()){
                        tv_bat = findViewById(R.id.tv_bat);
                        tv_bat.setText(bat);
                    }
                } catch (NullPointerException n){
                    toaster("Error al obtener los valores");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException().toString()); //El error puede no ser un String
            }
        });
    }

    //HU2 - Act4
    //Función para determinar parametros de confort
    public void det_confort(Double t, Double h){
        String dht;
        tv_dht = findViewById(R.id.tv_dht11);
        if (t!=0.0){//Evaluar que el valor no sea 0, caso contrario notificar
            tv_temp = findViewById(R.id.tv_temp);
            String estat_t ="n/a";
            if(t > 27 ){
                estat_t = "Peligro - Sofocante";
            } else if(t >25 && t < 27){
                estat_t = "Caliente";
            }else if(t >24 && t < 25){
                estat_t = "Confortable";
            }else if(t >22 && t < 24){
                estat_t = "Frio";
            }else if(t < 22 ){
                estat_t = "Peligro - Muy frio";
            }
            tv_temp.setText(estat_t);
        }else {
            toaster("Valores en 0, revise el dispositivo");
        }
        if (h!=0.0){        //Evaluar que el valor no sea 0, caso contrario notificar
            tv_hume = findViewById(R.id.tv_hume);
            String estat_h = "n/a";
            if(h >= 50 ){
                estat_h = "Peligro - Electricidad Estatica";
            } else if(h >= 33 && h <40){
                estat_h = "Confortable";
            }
            tv_hume.setText(estat_h);
        }else {
            toaster("Valores en 0, revise el dispositivo");
        }

    }

    //Funcion para mostrar parametros de los sensores de gas y sonido
    public void mostrarLecturasGasSound(Double s, Double g){
        if (s!=0.0){//Evaluar que el valor no sea 0, caso contrario notificar
            String aux_sound = String.valueOf(s);
            tv_sound = findViewById(R.id.tv_sound);
            tv_sound.setText(aux_sound);
        }else {
            toaster("Valores en 0, revise el dispositivo");
        }
        if (g!=0.0){//Evaluar que el valor no sea 0, caso contrario notificar
            eval_Gas(g);
            /*String aux_gas = String.valueOf(g);
            tv_gas = findViewById(R.id.tv_gas);
            tv_gas.setText(aux_gas);
             */

        }else {
            toaster("Valores en 0, revise el dispositivo");
        }
    }

    //HU2 - Act5
    //Función para determinar fuga de gas
    public void eval_Gas(Double g){
        String titulo = "Gas detectado";
        String alerta = "Se ha detectado un nivel anormal de gas";
        int n_id = 2;
        if (g!=0.0){//Evaluar que el valor no sea 0, caso contrario notificar
            tv_gas = findViewById(R.id.tv_gas);
            String estat_gas_level ="n/a";

            if(g > 159 ){
                estat_gas_level = "Gas detectado";
                toaster("Peligro de intoxicacion");
                notificarFallo(titulo, alerta, n_id);
            } else if(g >90 && g < 159){
                estat_gas_level = "Gas detectado";
                toaster("Peligro de incendio");
                notificarFallo(titulo, alerta, n_id);
            }else if(g >50 && g < 90){
                estat_gas_level = "Nivel de gas al límite";
                toaster("Peligro de incendio");
                notificarFallo(titulo, alerta, n_id);
            }else if(g < 50){
                estat_gas_level = "Nivel normal";
            }
            //Agregar text view para mostrar nivel de gas
            tv_gas.setText(estat_gas_level);


        }else {
            toaster("Valores en 0, revise el dispositivo");
        }
    }

    //HU2 - Act6
    //Función para determinar avería en el desague
    public void eval_Tuberia(Double s){
            if(s!=0.0){
                if(s < 400) {
                    String titulo = "Obstruccion";
                    String alerta = "Se ha detectado una tuberia tapada";
                    int n_id = 1;
                    notificarFallo(titulo, alerta, n_id);
                }
            }else {
                toaster("Valores en 0, revise el dispositivo");
            }
    }

    //Funcion que crea notificaciones
    public void notificarFallo(String titulo, String mensaje, int n){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
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
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificacion";
            String description = "Valores peligrosos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Permisos de CONEXION INTERNET - LOCALIZACIÓN
    public boolean verConexioInternet() {
        try {
            ConnectivityManager con = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert con != null;
            NetworkInfo networkInfo = con.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                toaster("Verifique su conexion de Internet");
                return false;
            }
        } catch (NullPointerException n) {
            return false;
        }
    }

    public void toaster(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public String getEstat_temp() {
        return estat_temp;
    }

    public void setEstat_temp(String estat_temp) {
        this.estat_temp = estat_temp;
    }

    public String getEstat_humed() {
        return estat_humed;
    }

    public void setEstat_humed(String estat_humed) {
        this.estat_humed = estat_humed;
    }
}