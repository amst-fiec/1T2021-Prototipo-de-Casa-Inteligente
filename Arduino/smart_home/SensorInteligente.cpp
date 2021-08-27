
/** @file     SensorInteligente.cpp
    @date     8 de julio de 2020
    @authors  Rosa Pincay, Javier Arce
    @Modificado Ronny Benitez, Natalia Mawyin
    @brief    Librería para detección de pesos usando velostat y para realizar mediciones de batería.
              Incluye el envío de datos encriptados a Sigfox Backend.
*/

#include "SensorInteligente.h"
#include "Arduino.h"


/**
   @brief   Crea un objeto de tipo SensorInteligente, se utiliza para realizar mediciones de batería.
            Es necesario declarar el constructor de la función para poder utilizar sus métodos.
   @param   pinA1: pin analógico al que se conecta la batería.
*/
SensorInteligente::SensorInteligente(int pinA1)
{
  _pinA1 = pinA1;
}


/**
   @brief   Crea un objeto de tipo SensorInteligente, se utiliza para medir los valores de la batería y el velostat.
            Es necesario declarar el constructor de la función para poder utilizar sus métodos.
   @param   pinA0: pin analógico al que se conecta el velostat.
   @param   pinA1: pin analógico al que se conecta la fuente de alimentación.
*/
SensorInteligente::SensorInteligente(int pinA0, int pinA1)
{
  _pinA0 = pinA0;
  _pinA1 = pinA1;
}


/**
   @brief   Inicializa las variables públicas y privadas que son utilizadas en la librería.
*/
void SensorInteligente::inicializar()
{
  _limiteMaximoBateria = 0;
  _tiempoAnterior = 0.0;
  nivelBateriaMayor = 100;
  voltajeBateria = 0.0;
  porcentajeBateria = 0;
  bateria = 0.0;
}


/**
   @brief   Función que realiza la lectura del pin analógico A1 y convierte ese valor a un voltaje.
   @return  voltajeBateria: valor de tipo float en el rango de [0, 5].
*/
float SensorInteligente::leerVoltajeBateria()
{
  bateria = analogRead(_pinA1);
  delay(2); // permite que se estabilice el convertidor analógico-digital (ADC).
  voltajeBateria = (((float) bateria) * 5.0) / 1023.0;
  return voltajeBateria;
}


/**
   @brief   Función que se debe utilizar de forma obligatoria si se requiere obtener el porcentaje de batería.
            Es necesario que se implemente un divisor de voltaje.
            Establece el voltaje máximo de entrada que entrega la batería en el rango de [0, 1023].
            Debe utilizarse en la función setup() luego de inicializar las variables de la librería.
   @param   rBajo: valor de la resistencia en Ohmios (Ω) que se encuentra conectada al negativo.
   @param   rArriba: valor de la resistencia en Ohmios Ohmios (Ω) que está conectada al positivo.
   @param   VIn: voltaje de la batería, puede indicar un voltaje nominal o realizar mediciones con un multímetro.
*/
void SensorInteligente::calibrarBateria(float rBajo, float rArriba, float vIn) {
  float vMax = (rBajo / (rBajo + rArriba)) * vIn;
  _limiteMaximoBateria = (int)(vMax * (1023 / 5));
  Serial.print("Calibrado! - El valor analogico maximo de bateria es = ");
  Serial.println(_limiteMaximoBateria);
}


/**
   @brief   Función que se encarga de mapear el nivel de la batería en el rango de [0, 100]
   @return  porcentajeBateria: valor de tipo int que indica el nivel actual de carga de la batería.
*/
int SensorInteligente::leerPorcentajeBateria() {
  bateria = analogRead(_pinA1);
  delay(5); // permite que se estabilice el convertidor analógico-digital (ADC).
  porcentajeBateria = map((int) bateria, 0, _limiteMaximoBateria, 0, 100);
  SensorInteligente::compararNivelBateria(porcentajeBateria);
  return porcentajeBateria;
}

/**
   @brief   Obtiene el nivel de bateria más bajo que ha sido censado.
            Si existe una caída del nivel de la batería mayor al 5% el porcentaje no cambiará.
   @param   porcentajeBatería: valor que será leído continuamente en el programa.
   @return  nivelBateriaMayor: devuelve el porcentaje de batería mayor que ha sido leído.
*/
int SensorInteligente::compararNivelBateria(int porcentajeBateria)
{
  if ((porcentajeBateria <= nivelBateriaMayor)) {
    nivelBateriaMayor = porcentajeBateria;
  }
  if ((porcentajeBateria > nivelBateriaMayor - 5)) {
    nivelBateriaMayor = porcentajeBateria;
  }
  return nivelBateriaMayor;

  /*if ((porcentajeBateria <= nivelBateriaMayor)) {
    nivelBateriaMayor = porcentajeBateria;
    return nivelBateriaMayor;
    }*/
}


/**
   @brief   Función que determina el envío de la batería dado cierto intervalo de tiempo.
            La batería solo se envía cuando el nivel es mayor a 30%.
   @param   intervalo: tiempo de espera para realizar el envío del valor de la batería.
   @param   porcentaje:
*/
void SensorInteligente::enviarBateria(long intervalo, int porcentaje)
{
  int bateria = SensorInteligente::compararNivelBateria(porcentaje);
  _tiempoAnterior = millis();
  if (millis() - _tiempoAnterior > intervalo) {
    if (bateria > 30) {
      Serial.print("Bateria: ");
      Serial.print(bateria);
      Serial.print(" - Porcentaje bateria: ");
      Serial.println(porcentaje);
    } else {
      Serial.println(" -> Bateria baja");
    }
  }
}
