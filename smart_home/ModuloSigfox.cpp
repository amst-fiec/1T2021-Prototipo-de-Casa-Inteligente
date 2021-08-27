/** @file     ModuloSigfox.cpp
    @date     
    @authors  Ronny Benitez, Natalia Mawyin
    @brief    Librería integrando el modulo sigfox para la detección de temperatura 
*             y aceleración, realizar mediciones de batería  
*/

#include "Modulos.h"
#include "Arduino.h"

//Función de inicialización y configuración de sigfox
void ModuloSigfox::InicializarSigfox(){
  int flag = -1;
  while(flag == -1){
    //Bandera que determina si existe o no la cobertura
    flag = TesteoCobertura();
  }
  Isigfox->testComms();
  Isigfox->getZone();
  GetDeviceID();
  /*
   * while (flagInit == -1) {
    Serial.println(""); // Make a clean restart
    delay(1000);
    flagInit = Isigfox->initSigfox();
    Isigfox->testComms();
    //Isigfox->setPublicKey(); // set public key for usage with SNEK
  }
  tSensors->initSensors();
  Serial.println(""); // Make a clean start
   */
 
}
void ModuloSigfox::InicializarSensor(){
   tSensors->initSensors(); // Inicializa los sensores del Thinxtra Module
  }

int ModuloSigfox::TesteoCobertura(){
  flagInit = Isigfox->initSigfox(); //inicializa el sigfox
  return flagInit;
}

  //Función para obtener el ID del dispositivo
void ModuloSigfox::GetDeviceID() {
  recvMsg *RecvMsg;
  const char msg[] = "AT$I=10";

  RecvMsg = (recvMsg *)malloc(sizeof(recvMsg));
  Isigfox->sendMessage(msg, 7, RecvMsg);

  Serial.print("Device ID: ");
  for (int i = 0; i < RecvMsg->len; i++) {
    Serial.print(RecvMsg->inData[i]);
  }
  Serial.println("");
  free(RecvMsg);
}

//Agregando valores al buffer de datos
void ModuloSigfox::Buffer_PloadA(float var_float, float var2_float, int var_int) {
  const uint8_t payloadSize = 7;
  byte *float_byte = (byte *)&var_float; 
  byte *float2_byte = (byte *)&var2_float;
  byte *int_byte = (byte *)&var_int; 
  uint8_t buf_str[payloadSize];
  buf_str[0] = float_byte[0];
  buf_str[1] = float_byte[1];
  buf_str[2] = float_byte[2];
  buf_str[3] = float_byte[3];
  buf_str[4] = float2_byte[0];
  buf_str[5] = float2_byte[1];
  buf_str[6] = float2_byte[2];
  buf_str[7] = float2_byte[3];
  buf_str[8] = int_byte[0];
  buf_str[9] = int_byte[1];
  //Función para enviar los datos
  Send_Pload(buf_str, payloadSize);
  // Wait 1s
  delay(1000);
}

void ModuloSigfox::Buffer_Pload(float var_float, float var2_float) {
  const uint16_t payloadSize = 10;
  byte *float_byte = (byte *)&var_float; 
  byte *float2_byte = (byte *)&var2_float;
  uint8_t buf_str[payloadSize];
  buf_str[0] = float_byte[0];
  buf_str[1] = float_byte[1];
  buf_str[2] = float_byte[2];
  buf_str[3] = float_byte[3];
  buf_str[4] = float2_byte[0];
  buf_str[5] = float2_byte[1];
  buf_str[6] = float2_byte[2];
  buf_str[7] = float2_byte[3];
  //Función para enviar los datos
  Send_Pload(buf_str, payloadSize);
  // Wait 1s
  delay(1000);
}

//Agregando valores al buffer de datos, pero con diferentes tipos de datos, sobrecarga de la función
void ModuloSigfox::Buffer_Pload(float var_float, float var2_float, int var_int,int var2_int) {
  const uint8_t payloadSize = 12;
  byte *float_byte = (byte *)&var_float; 
  byte *float2_byte = (byte *)&var2_float;
  byte *int_byte = (byte *)&var_int;
  byte *int2_byte = (byte *)&var2_int;  
  uint8_t buf_str[payloadSize];
  buf_str[0] = float_byte[0];
  buf_str[1] = float_byte[1];
  buf_str[2] = float_byte[2];
  buf_str[3] = float_byte[3];
  buf_str[4] = float2_byte[0];
  buf_str[5] = float2_byte[1];
  buf_str[6] = float2_byte[2];
  buf_str[7] = float2_byte[3];
  buf_str[8] = int_byte[0];
  buf_str[9] = int_byte[1];
  buf_str[10] = int2_byte[0];
  buf_str[11] = int2_byte[1];
  Send_Pload(buf_str, payloadSize);
  // Wait 1s
  delay(1000);
}

//Agregando valores al buffer de datos, pero con diferentes tipos de datos. Sobrecarga de funciones
void ModuloSigfox::Buffer_PloadB(int var_int,int var2_int, int var3_int) {
  const uint8_t payloadSize = 7;
  byte *int_byte = (byte *)&var_int; 
  byte *int2_byte = (byte *)&var2_int;
  byte *int3_byte = (byte *)&var3_int;  
  uint8_t buf_str[payloadSize];
  buf_str[0] = int_byte[0];
  buf_str[1] = int_byte[1];
  buf_str[2] = int2_byte[0];
  buf_str[3] = int2_byte[1];
  buf_str[4] = int3_byte[0];
  buf_str[5] = int3_byte[1];
  Send_Pload(buf_str, payloadSize);
  // Wait 1s
  delay(1000);
}

void ModuloSigfox::Buffer_Pload(int var_int,int var2_int, int var3_int, int var4_int) {
  const uint8_t payloadSize = 12;
  byte *int_byte = (byte *)&var_int; 
  byte *int2_byte = (byte *)&var2_int;
  byte *int3_byte = (byte *)&var3_int;  
  byte *int4_byte = (byte *)&var4_int; 
  uint8_t buf_str[payloadSize];
  buf_str[0] = int_byte[0];
  buf_str[1] = int_byte[1];
  buf_str[2] = int2_byte[0];
  buf_str[3] = int2_byte[1];
  buf_str[4] = int3_byte[0];
  buf_str[5] = int3_byte[1];
  buf_str[6] = int3_byte[0];
  buf_str[7] = int3_byte[1];
  Send_Pload(buf_str, payloadSize);
  // Wait 1s
  delay(1000);
}

//Agregando valores al buffer de datos, pero con diferentes tipos de datos. Sobrecarga de funciones
void ModuloSigfox::Buffer_Pload(int var_int,int var2_int) {
  const uint8_t payloadSize = 4;
  byte *int_byte = (byte *)&var_int; 
  byte *int2_byte = (byte *)&var2_int;
  uint8_t buf_str[payloadSize];
  buf_str[0] = int_byte[0];
  buf_str[1] = int_byte[1];
  buf_str[2] = int2_byte[0];
  buf_str[3] = int2_byte[1];
  Send_Pload(buf_str, payloadSize);
  // Wait 1s
  delay(1000);
}

void ModuloSigfox::Buffer_Pload(int var_int) {
  const uint8_t payloadSize = 4;
  byte *int_byte = (byte *)&var_int; 
  uint8_t buf_str[payloadSize];
  buf_str[0] = int_byte[0];
  buf_str[1] = int_byte[1];
  Send_Pload(buf_str, payloadSize);
  // Wait 1s
  delay(1000);
}

// Función SendPayload => Envía mensajes a la red de Sigfox​
void ModuloSigfox::Send_Pload(uint8_t *sendData, int len) {
  recvMsg *RecvMsg; RecvMsg = (recvMsg *)malloc(sizeof(recvMsg));
  // Función SendPayload dentro de la libreria=> Envía mensajes a la red de Sigfox​
  Isigfox->sendPayload(sendData, len, 0, RecvMsg);
  for (int i = 0; i < RecvMsg->len; i++) {
    Serial.print(RecvMsg->inData[i]);
  }
  Serial.println("");
  free(RecvMsg);
}

//Para casos de usar el sensor acelerometro y temperatura
// Obtiene la temperatura
float ModuloSigfox::ObtenerTemperatura() {
  temperatura = round(tSensors->getTemp() * 10) / 10.0;
  return temperatura;
}

// Retorna la aceleración en el eje X ​
float ModuloSigfox::getAxeX() {
  acceleration_xyz *xyz_g; 
  xyz_g = (acceleration_xyz *)malloc(sizeof(acceleration_xyz));
  tSensors->getAccXYZ(xyz_g);
  axeX = (float)xyz_g->x_g;
  free(xyz_g);
  return axeX;
}


// Retorna la aceleración en el eje Y
float ModuloSigfox::getAxeY() {
  acceleration_xyz *xyz_g; 
  xyz_g = (acceleration_xyz *)malloc(sizeof(acceleration_xyz));
  tSensors->getAccXYZ(xyz_g);
  axeX = (float)xyz_g->y_g;
  free(xyz_g);
  return axeY;
}


// Retorna la aceleración en el eje Z ​
float ModuloSigfox::getAxeZ() {
  acceleration_xyz *xyz_g; 
  xyz_g = (acceleration_xyz *)malloc(sizeof(acceleration_xyz));
  tSensors->getAccXYZ(xyz_g);
  axeX = (float)xyz_g->z_g;
  free(xyz_g);
  return axeZ;
}
