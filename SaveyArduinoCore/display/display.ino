/* WOWOWOWOOW :P */
#include <Usb.h>
#include <AndroidAccessory.h>


AndroidAccessory acc("Megadevs",
"ATSS",
"Anti Tefth Social System",
"1.0",
"http://www.megadevs.com",
"0000000012345678");


/* VARS */
boolean on = LOW;
boolean off = HIGH;
boolean colorRed = on; //off = green
boolean c = off;
boolean p = off;
int n=2;


int n1=0;
int n2=0;
int n3=0;

int dummy = 25;
int prog=-1;
int val = 0;  
//unsigned int display0[] = {1,4,3,2,5,10,9,8,7,6};

unsigned int display2[] = {
  49,47,45,43,41, 40,42,44,46,48};
unsigned int display1[] = {
  39,37,35,33,31, 30,32,34,36,38};
unsigned int display0[] = {
  5,4,3,2,1, 6,7,8,9,10};

//unsigned int progress[] = {14,15,16,17,18,19,20,21};
//unsigned int progress[] = {22,23,24,25,26,27,28,29};
unsigned int progress[] = {29,27,25,28,26,24,22};


boolean num0[] = {
  c,on,on,on,c,on,on,p,on,off};
boolean num1[] = {
  c,off,off,on,c,on,off,p,off,off};
boolean num2[] = {
  c,on,on,off,c,on,on,p,off,on};
boolean num3[] = {
  c,off,on,on,c,on,on,p,off,on};
boolean num4[] = {
  c,off,off,on,c,on,off,p,on,on};
boolean num5[] = {
  c,off,on,on,c,off,on,p,on,on};
boolean num6[] = {
  c,on,on,on,c,off,on,p,on,on};
boolean num7[] = {
  c,off,off,on,c,on,on,p,off,off};
boolean num8[] = {
  c,on,on,on,c,on,on,p,on,on};
boolean num9[] = {
  c,off,on,on,c,on,on,p,on,on};


void displayNumber(unsigned int* disp, int number, boolean point){
  boolean pins[10];
  for(int i=0; i<10 ;i++){
    if(number == 0){
      pins[i] = num0[i];
    }
    else if(number == 1){
      pins[i] = num1[i];
    }
    else if(number == 2){
      pins[i] = num2[i];
    }
    else if(number == 3){
      pins[i] = num3[i];
    }
    else if(number == 4){
      pins[i] = num4[i];
    }
    else if(number == 5){
      pins[i] = num5[i];
    }
    else if(number == 6){
      pins[i] = num6[i];
    }
    else if(number == 7){
      pins[i] = num7[i];
    }
    else if(number == 8){
      pins[i] = num8[i];
    }
    else if(number == 9){
      pins[i] = num9[i];
    }    
  }

  if(point){
    digitalWrite(disp[7],on);
  }
  else{
    digitalWrite(disp[7],off); 
  }


  for(int i=0; i<10 ;i++){
    if(i!=0 && i!=4 && i!=7) {
      digitalWrite(disp[i],pins[i]);
    }
  }
}

void setup() {                
  Serial.begin(9600);
  Serial.print("\r\nStart");

  acc.powerOn();

  //display
  for(int i=0;i<10;i++){
    pinMode(display0[i], OUTPUT);
    pinMode(display1[i], OUTPUT);
    pinMode(display2[i], OUTPUT);
  }


  //progressbar
  
  for(int i=0;i<7;i++){
    pinMode(progress[i], OUTPUT);
    digitalWrite(progress[i],LOW);
  } 
  



}

void loop() {

  byte err;
  byte idle;
  static byte count = 0;
  byte msg[4];

  //n1=1;
  if (acc.isConnected()) {
    //n1=9;
    int len = acc.read(msg, sizeof(msg), 1);

    if (len > 0) {
      n1=msg[0]   == dummy ? n1   : msg[0];
      n2=msg[1]   == dummy ? n2   : msg[1];
      n3=msg[2]   == dummy ? n3   : msg[2];      

      /*
       Serial.print ("msg[0]: ");
       Serial.print (msg[0]);
       Serial.print (" msg[1]: ");
       Serial.print (msg[1]);       
       Serial.print (" msg[2]: ");
       Serial.print (msg[2]);
       Serial.print (" msg[3]: ");
       Serial.print (msg[3]);
       Serial.print ("\n");
       */
      
      prog=msg[3] == dummy ? prog : msg[3];
      /*if(msg[3]!=dummy){
        Serial.print("Progress :");
        Serial.print(prog);
        Serial.print("\n");
      }
      */
      
    }
  }  



  if(colorRed){
    digitalWrite(display0[0] , on);
    digitalWrite(display0[4] , off);
    digitalWrite(display1[0] , on);
    digitalWrite(display1[4] , off);
    digitalWrite(display2[0] , on);
    digitalWrite(display2[4] , off);
  } 
  else{
    digitalWrite(display0[0] , off);
    digitalWrite(display0[4] , on);
    digitalWrite(display1[0] , off);
    digitalWrite(display1[4] , on);
    digitalWrite(display2[0] , off);
    digitalWrite(display2[4] , on);
  }

  displayNumber(display0, n1 , true);
  displayNumber(display1, n2 , false); 
  displayNumber(display2, n3 , false);

  //progress
  
  for(int i=0;i<7;i++){
    if(i <= prog)
      digitalWrite(progress[i] , HIGH);
    else
      digitalWrite(progress[i] , LOW);
  }
  

  n++;
  if(n==10)n=0;

  delay(200);              // wait for a second
}


