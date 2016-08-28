#include "mbed.h"
#include <list>

AnalogIn shield(PC_1);
Serial pc(USBTX, USBRX);
Serial bluetooth(D10, D2); //Tx, Rx
PwmOut cal(D9);
Ticker ticker;

char myCharPointer[6];

void send(){
	float val = shield.read();

	sprintf(myCharPointer, "{%1.2f}", val);
	pc.printf(myCharPointer);
	bluetooth.printf(myCharPointer);
	memset(myCharPointer, 0, sizeof myCharPointer);

}

int main() {
	pc.baud(115200);
	bluetooth.baud(115200);
	cal.period(0.1f);
	cal.write(0.50f);

	ticker.attach(&send, 0.025);

	while(1){}
}

