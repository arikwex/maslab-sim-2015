#include <SoftwareSerial.h>


class Ult
{
  private:
  int trigPin, echoPin;
  
  public:
    Ult(int tPin, int ePin)
    {
      trigPin = tPin; // Pin to trigger the ultrasound sensor
      echoPin = ePin; // Pin to read the distance
      pinMode(trigPin, OUTPUT);
      pinMode(echoPin, INPUT);
    }
    unsigned long trigger()
    {
      unsigned long duration;
      // The PING))) is triggered by a HIGH pulse of 2 or more microseconds.
      // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
      digitalWrite(trigPin, LOW);
      delayMicroseconds(2);
      digitalWrite(trigPin, HIGH);
      delayMicroseconds(5);
      digitalWrite(trigPin, LOW); 

      // pulse whose duration is the time (in microseconds) from the sending
      // of the ping to the reception of its echo off of an object.
      duration = pulseIn(echoPin, HIGH, 6000);
      return duration;
    }

};

Ult** ults;
// Dynamic array of all the ultrasound sensors
int numUlts = 1;
int ultPins[][2] = {{22,23}};
// Handles ultrasonic sensor initialization
void ultInit()
{
  int tPin, ePin;
  Ult* tempUlt;

  // Free up any allocated memory from before
  for (int i = 0 ; i < numUlts; i++)
  {
    free(ults[i]);
  }
  free(ults);


  // Reallocate the array
  ults = (Ult**) malloc(sizeof(Ult*) * numUlts);
  for (int i = 0; i < numUlts; i++)
  {
    // Create the Ult object and store
    // it in the array
    tPin = ultPins[i][0];
    ePin = ultPins[i][1];
    tempUlt = new Ult(tPin, ePin);
    ults[i] = tempUlt;
  }
  
}
int INIT = 1;
// Special function run when the arduino is first connected to power
void setup()
{
  Serial.begin(9600);
  Serial.flush();
}

void loop()
{
  if(INIT==1){
    ultInit();
    INIT = 0;
  }
  String output;
  for (int i = 0; i < numUlts; i++)
  {
    
    unsigned long duration = ults[i]->trigger();
    output += duration;
    output += "\n";
  }
  output += "J";
  output += "\n";
  Serial.print(output);

}


