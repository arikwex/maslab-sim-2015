String end = String("E");
String split = String("S");
String start = String("F");
String temp;
String n;
int steps[3];
int tsteps[3] = {600,700,-900};
int const numSteppers = 3;
int const stepPins[numSteppers] = {2,4,6};
int const dirPins[numSteppers] = {3,5,7};
float const speed = 0.1;
int counter;

void getCommand(){
  
  //temp = String(serialRead());
  
  while(Serial.available() > 0){
      temp = String((char)serialRead());
       
      if (temp.equals(split)){
        steps[counter] = n.toInt();
        Serial.print(steps[counter]==tsteps[counter]);
        n = "";
        counter += 1;
      }
      else if (temp.equals(start)){
        counter =0;
        n = "";
      }
      else if (temp.equals(end)){
        break;
      }
      else{
        n.concat(temp);
      }
    }
}

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

void loop() {
  
  
  if(Serial.available()>0){
    
    getCommand();
    //moveDelta(steps[0],steps[1],steps[2],speed);
    } 
  }
  
void moveDelta(int steps1, int steps2, int steps3, float s){
   int numSteps[numSteppers] = {steps1, steps2, steps3};
   
   for (int i = 0; i < numSteppers; i++){
     
     int dir = (numSteps[i] < 0)? HIGH:LOW;
     numSteps[i] = abs(numSteps[i]);
     digitalWrite(dirPins[i], dir);
   }
   
   float usDelay = (1/s) * 70;
   
   while(numSteps[0] > 0 || numSteps[1] > 0 || numSteps[1] > 0){
     
     for(int i = 0; i < numSteppers; i++){
       
       if (numSteps[i] > 0){
         digitalWrite(stepPins[i], HIGH);
       }   
     }
     
     delayMicroseconds(usDelay);
     
     for(int i = 0; i < numSteppers; i++){
       
       if (numSteps[i] > 0){
         digitalWrite(stepPins[i], LOW);
         numSteps[i] --;
       }     
     }
     
     delayMicroseconds(usDelay);
   }
}
  


