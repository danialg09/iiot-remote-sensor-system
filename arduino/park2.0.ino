#include <LiquidCrystal.h>

int trig = 7;
int echo = 6;
int sure;
int uzaklik;


void setup() {
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);

  Serial.begin(9600);

}

void loop() {
  digitalWrite(trig,LOW);
  delayMicroseconds(5);
  digitalWrite(trig,HIGH);
  delayMicroseconds(10);
  digitalWrite(trig,LOW);

  sure = pulseIn(echo, HIGH, 11600);
  uzaklik = sure*0.0345/2;

  delay(50);

  Serial.println(uzaklik);
}
