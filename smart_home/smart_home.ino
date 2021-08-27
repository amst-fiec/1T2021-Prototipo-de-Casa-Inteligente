#include <MQUnifiedsensor.h>
#include  <Adafruit_LiquidCrystal.h>
#include <math.h>​
#include "Arduino.h"
#include "Modulos.h"
#include "SensorInteligente.h"
#include "DHT.h"

//Lectura de gas
/************************Hardware Related Macros************************************/
#define         Board                   ("Arduino UNO")
#define         Pin                     (A3)  //Analog input 3 of your arduino
/***********************Software Related Macros************************************/
#define         Type                    ("MQ-2") //MQ2
#define         Voltage_Resolution      (5)
#define         ADC_Bit_Resolution      (10) // For arduino UNO/MEGA/NANO
#define         RatioMQ2CleanAir        (9.83) //RS / R0 = 9.83 ppm 
/*****************************Globals***********************************************/
MQUnifiedsensor MQ2(Board, Voltage_Resolution, ADC_Bit_Resolution, Pin, Type);

ModuloSigfox fsigfox = ModuloSigfox(); //Instancia de clase FuncionesSigfox
SensorInteligente bateria = SensorInteligente(1);
float voltBat;

Adafruit_LiquidCrystal lcd(12, 11, 5, 4, 3, 2); //(RS, RW, E, D4,D5, D6, D7)
DHT dht(7, DHT11);

int control = 0;
int cont = 0;

typedef union{
    int16_t number;
    uint8_t bytes[2];
} INT16_t;

void setup() {
  
  //Funciones bateria
  bateria.inicializar();
  bateria.calibrarBateria(10000, 33000, 9);
  
  //INICIALIZACION MQ2 SENSOR DE GAS
  iniciarGas();
  //INICIALIZACION DE TEMPERATURA HUMEDAD
  dht.begin();
  
  lcd.begin(16, 2);            // Inicia el LCD 16x02 (columnas, filas)
  lcd.print("Hola Mundo");   
 
  //Funciones de Sigfox
  fsigfox.InicializarSigfox();
  Serial.begin(9600); //Init serial port
  delay(1000);
  
}

void loop() {  
  lcd.clear();
  lcd.setCursor(0,0);
  //----------FUNCIONES DE BATERIA-----------
  int porcBat = (uint16_t)bateria.leerPorcentajeBateria();
  Serial.print("\nBateria: ");
  Serial.print(porcBat);
  Serial.println("%");
  
  lcd.print(porcBat);
  delay(500);
  //#########################################
  lcd.setCursor(0,1);
  //----------LECTURA DE GAS----------
  MQ2.update(); // Update data, the arduino will be read the voltage on the analog pin
  float gasAux = MQ2.readSensor();
  float gas = round(gasAux); // Sensor will read PPM concentration using the model and a and b values setted before or in the setup
  //gas = (uint16_t)gasAux;
  Serial.print("Gas: ");
  Serial.print(gas);
  Serial.println(" PPM");

  lcd.print(gas);
  delay(500);
  //###################################

  //----------LECTURA DE SONIDO----------
  float soundAux;
  soundAux = getSound();
  float sound = round(soundAux);
  //sound = (uint16_t)soundAux;
  delay(500);
  //###################################

  //-----PRIMER ENVIO DE DATOS->->->->->->->->->->->->->
  delay(1000);
  //#####################################################
  
  //----------LECTURA DE HUMEDAD Y TEMPERATURA-----------------
  int t = (uint16_t) dht.readTemperature();
  int h = (uint16_t) dht.readHumidity();
  // Revision de lecturas
  if (isnan(h) || isnan(t)) {
    Serial.println("Falla al leer el sensor DHT11!");
    return;
  }
  Serial.print("Humedad: ");
  Serial.print(h);
  Serial.print("    Temperatura: ");
  Serial.println(t);
  delay(500);

  //############################################################

  //-----SEGUNDO ENVIO DE DATOS->->->->->->->->->->->->->
  fsigfox.Buffer_PloadB(t,h, porcBat);
  delay(500);
  
  //#####################################################
  
  delay(1000);
}

void iniciarGas(){
  //Selección del método para calcular la concentracion en PPM
  MQ2.setRegressionMethod(1); //_PPM =  a*ratio^b
  MQ2.setA(574.25); MQ2.setB(-2.222); // Configurate the ecuation values to get LPG concentration
  MQ2.init(); 
  float calcR0 = 0;
  for(int i = 1; i<=10; i ++)
  {
    MQ2.update(); // Update data, the arduino will be read the voltage on the analog pin
    calcR0 += MQ2.calibrate(RatioMQ2CleanAir);
    Serial.print(".");
  }
  MQ2.setR0(calcR0/10);
  if(isinf(calcR0)) {Serial.println("Warning: Conection issue founded, R0 is infite (Open circuit detected) please check your wiring and supply"); while(1);}
  if(calcR0 == 0){Serial.println("Warning: Conection issue founded, R0 is zero (Analog pin with short circuit to ground) please check your wiring and supply");}
}

float getSound(){
  int adc_ky = analogRead(A3); //Lemos la salida analógica del KY
  float v_ky = adc_ky * (5.0 / 1023.0); //Convertimos la lectura en un valor de voltaje
  float decibel = 20*log((v_ky/5.0));
  Serial.print("Sound ADC:");
  Serial.print(adc_ky);
  Serial.print("    voltaje: ");
  Serial.print(v_ky);
  Serial.print("    nivel: ");
  Serial.println(decibel);
  return v_ky;
}
