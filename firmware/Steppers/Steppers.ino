
int const numSteppers = 3;
int const stepPins[numSteppers] = {2,4,6};
int const dirPins[numSteppers] = {3,5,7};
float const speed = 0.1;

char serialRead()
{
  char in;
  // Loop until input is not -1 (which means no input was available)
  while ((in = Serial.read()) == -1) {}
  return in;
}

void setup() { 
  Serial.begin(9600);
  Serial.flush();
  for(int i = 0; i < numSteppers; i++){
    pinMode(dirPins[i], OUTPUT); 
    pinMode(stepPins[i], OUTPUT);
  } 
} 

void loop(){ 
  
  if (Serial.available() >0){
    int steps[numSteppers];
    for(int i = 0; i < numSteppers; i++){
      char stepsByte1 = serialRead();
      char stepsByte0 = serialRead();
      steps[i] = (int)(256*stepsByte1 + stepsByte0);
    } 
    moveDelta(0, 3600, 3600, speed);
    delay(200);
  }
 
}


void moveDelta(int steps1, int steps2, int steps3, float s){
   int steps[numSteppers] = {abs(steps1), abs(steps2), abs(steps3)};
   
   
   for (int i = 0; i < numSteppers; i++){
     
     int dir = (steps[i] < 0)? HIGH:LOW;
     digitalWrite(dirPins[i], dir);
   }
   
   float usDelay = (1/s) * 70;
   
   while(steps[0] > 0 || steps[1] > 0 || steps[1] > 0){
     
     for(int i = 0; i < numSteppers; i++){
       
       if (steps[i] > 0){
         digitalWrite(stepPins[i], HIGH);
       }   
     }
     
     delayMicroseconds(usDelay);
     
     for(int i = 0; i < numSteppers; i++){
       
       if (steps[i] > 0){
         digitalWrite(stepPins[i], LOW);
         steps[i] --;
       }     
     }
     
     delayMicroseconds(usDelay);
   }
}
