import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import serial, re

frec = 25
buff = ""

nucleo = serial.Serial('/dev/ttyACM0', 115200, timeout = 1)
refresh = 5
def data_gen(t=0):
    #cnt = 0
    while True:
        #cnt += 1
        t += frec * 0.001
        yield t, lectura()

def lectura():
    global buff
    buff += nucleo.read(6)
    values =  re.split('({....})', buff)
    if len(values) != 1:
        buff = values[2]
        return float(values[1][1:-1])
    else:
        return 0



def init():
    ax.set_ylim(-0.1, 1.1)
    ax.set_xlim(0, refresh)
    del xdata[:]
    del ydata[:]
    line.set_data(xdata, ydata)
    return line,

fig, ax = plt.subplots()
line, = ax.plot([], [], lw=2)
ax.grid()
xdata, ydata = [], []


def run(data):
    # update the data
    t, y = data
    xdata.append(t)
    ydata.append(y)
    xmin, xmax = ax.get_xlim()

    if t >= xmax:
        del xdata[:]
        del ydata[:]
        ax.set_xlim(xmax, xmax + refresh)
        ax.figure.canvas.draw()
    line.set_data(xdata, ydata)

    return line,

ani = animation.FuncAnimation(fig, run, data_gen, blit=False, interval=frec,
                              repeat=False, init_func=init)
plt.show()
