#include "mbed.h"

AnalogIn shield(PC_1);
Serial pc(USBTX, USBRX);
Serial bluetooth(D10, D2); //Tx, Rx
PwmOut cal(D9);
Ticker ticker;

char myCharPointer[6];
int i;

float buffer[5] = { };
int ind;
bool firstValues;

const float fEnvio = 0.025;			//envio 40 Hz
const float fMuestreo = 0.00625;	//muestreo 160 hz

float medianFilter(float value){

	float v = 0;
	buffer[ind] = value;

	if(ind >= 4){
		ind = 0;

		if(firstValues){
			firstValues = false;
		}
	}

	if(!firstValues){
		for(int j = 0; j < 5; j ++){
			v += buffer[j];
		}

		v = v / 5;
	}

	ind ++;
	return v;
}

void send(){
	float val = shield.read();
	val = medianFilter(val);
	float f = (fEnvio / fMuestreo) - 1;

	if(i >= f){
		sprintf(myCharPointer, "{%1.2f}", val);
		pc.printf(myCharPointer);
		bluetooth.printf(myCharPointer);
		memset(myCharPointer, 0, sizeof myCharPointer);
		i = 0;
	}

	i ++;
}

int main() {
	pc.baud(115200);
	bluetooth.baud(115200);
	cal.period(0.1f);
	cal.write(0.50f);

	ind = 0;
	firstValues = true;
	i = 0;

	ticker.attach(&send, fMuestreo);

	while(1){}
}
