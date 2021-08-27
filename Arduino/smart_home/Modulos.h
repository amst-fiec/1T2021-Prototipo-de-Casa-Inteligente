/** @file     Modulos.h
    @date    
    @authors  Ronny Benitez, Natalia
    @brief    Librerías para el envio de datos a la nube o Sigfox
*/

#ifndef MODULOS_H
#define MODULOS_H
#include "Arduino.h"
#include <Isigfox.h> //Uso de la  librería sigfox
#include <WISOL.h>
#include <Tsensors.h> //Uso de sensores de Sigfox
#include <math.h>​

class ModuloSigfox
{
//Se inicializan  las funciones  a utilizar
  public:
    Isigfox *Isigfox = new WISOL();
    Tsensors *tSensors = new Tsensors(); //Uso de sensores
    // Funciones
    void InicializarSigfox();
    void InicializarSensor();
    int TesteoCobertura();
    void GetDeviceID();
    void Buffer_PloadA(float var_float, float var2_float, int var_int);
    void Buffer_Pload(float var_float, float var2_float);
    void Buffer_Pload(float var_float, float var2_float, int var_int, int var2_int);
    void Buffer_Pload(float var_float, float var2_float, float var3_float);
    void Buffer_PloadB(int var_int,int var2_int, int var3_int);
    void Buffer_Pload(int var_int,int var2_int, int var3_int, int var4_int);
    void Buffer_Pload(int var_int,int var2_int);
    void Buffer_Pload(int var_int);
    void Send_Pload(uint8_t *sendData, int len);
    float ObtenerTemperatura();
    float getAxeX();
    float getAxeY();
    float getAxeZ();
    // Variables
    float temperatura;
    float axeX;
    float axeY;
    float axeZ;
    int flagInit;
};
#endif /* MODULOS_H */
