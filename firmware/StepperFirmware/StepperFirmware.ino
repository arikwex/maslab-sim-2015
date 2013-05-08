String end = String("E");
String split = String("S");
String start = String("F");

String p_out = String("P");
String p_in = String("p");

String temp;
String n;
long steps[3];
int const numSteppers = 3;
int const stepPins[numSteppers] = {2,4,6};
int const dirPins[numSteppers] = {3,5,7};
int const limitPins[numSteppers] = {8,9,10};
float const speed = 0.1;
int limit[3] = {0,0,0};
int counter;

int const pneumaticPin = 53;
bool pneumatic = false;

void getCommand(){
  while(true){
      temp = String((char)serialRead());
       
      if (temp.equals(split)){
        steps[counter] = n.toInt();
        n = "";
        counter += 1;
      }
      else if (temp.equals(start)){
        counter =0;
        n = "";
      }
      else if (temp.equals(end)){
        break;
      } else if (temp.equals(p_out)) {
        pneumatic = true;
        break;
      } else if (temp.equals(p_in)) {
        pneumatic = false;
        break;
      }
      else{
        n.concat(temp);
      }
    }
}

// reads from serial port with blocking
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
    pinMode(limitPins[i], INPUT);
  } 

  pinMode(pneumaticPin, OUTPUT);
  digitalWrite(pneumaticPin, LOW);
}

void loop() {
  if(Serial.available()>0){    
    getCommand(); 
    moveDelta(steps[0],steps[1],steps[2],speed);
    setPneumatic(pneumatic);

    for (int i = 0; i<numSteppers; i++)
        steps[i] = 0;

    Serial.print("w");
    Serial.flush();
  } 
}

void setPneumatic(bool position) {
  if (position)
    digitalWrite(pneumaticPin, HIGH);
  else
    digitalWrite(pneumaticPin, LOW);
}
  
void moveDelta(long steps1, long steps2, long steps3, float s){
   long numSteps1[numSteppers] = {steps1, steps2, steps3};
   long numSteps[numSteppers];
   
   for (int i = 0; i < numSteppers; i++){
     
     int dir = (numSteps1[i] < 0)? HIGH:LOW;
     numSteps[i] = abs(numSteps1[i]);
     digitalWrite(dirPins[i], dir);
   }
   
   float usDelay = (1/s) * 70;
   
   while(numSteps[0] > 0 || numSteps[1] > 0 || numSteps[2] > 0){
     
     for (int i = 0; i<numSteppers; i++){
       limit[i] = digitalRead(limitPins[i]);
       if (limit[i] && (numSteps1[i] >0)){
         numSteps[i]=0;
         digitalWrite(stepPins[i], LOW);
         
       }
     }
   
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
