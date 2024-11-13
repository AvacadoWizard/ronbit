from serial import *
import time

def check_ports(): # skibidi skibidi hawk tuah hawk
    ports = []
    for i in range(256):
        ports.append("COM" + str(i)) 
    result = []

    for port_name in ports:
        try:
            serial_port = Serial(port_name)
            serial_port.close()
            result.append(port_name)
        except:
            pass
    return result

def recordBots(data):
    bots = []
    recordBot = True
    atChar = 0
    maxChar = len(data)
    atByte = ""
    atIntByte = 0
    nameLength = 0
    macType = ""
    macAddress = ""
    name = ""

    while (atChar < maxChar):
        if (recordBot and data[atChar] == "0" and data[atChar + 1] == "0"):
            recordBot = False
            atChar += 2
            continue


def scan(port_name, timeout):
    start_time = time.time()
    start_scan = bytearray.fromhex('01')
    stop_scan = bytearray.fromhex('02')
    bots = []
    raw_data = ""
    try:
        serial_port = Serial(port_name)
        serial_port.write(start_scan)
        while (time.time() - start_time <= timeout):
            if serial_port.in_waiting > 0:
                data = serial_port.read(1)
                raw_data += data.hex()

        serial_port.write(stop_scan)
    except:
        print("poop doopy fart")

    # hex_message = hex_please(raw_data)

    if (len(raw_data) == 0):
        print("not good friend...")
    else:


        return raw_data

print(scan("COM5", 2))
