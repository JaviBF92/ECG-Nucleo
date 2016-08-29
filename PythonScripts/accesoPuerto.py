import serial, re

nucleo = serial.Serial('/dev/ttyACM0', 115200, timeout = 1)
buff = ""
while True:
	buff += nucleo.read(10)
	values =  re.split('({....})', buff)
	if len(values) != 1:
		for i in values[1:-1:2]:
			print(i[1:-1] + "\n")
	buff = values[-1]

