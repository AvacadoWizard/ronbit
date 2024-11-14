from serial import *
import time
import binascii

class ronbit:
    def __init__(self):
        # define a start variables
        self.name = ""
        self.name_length = ""
        self.mac_type = ""
        self.mac_address = ""

        # define port variable for connecting to serial device
        self.port_name = ""

    def set_port(self, port_name):
        try:
            self.port_name = port_name
            port = Serial(port_name)
            print("Succesfully set port!")
            port.close()
        except:
            print(f"Error: can not connect to port: {port_name}")

    def connect(self, name, timeout=2):
        discovered_bots = scan(self.port_name, timeout)
        if (name in discovered_bots.keys()):
            pass
        else:
            print("Error: ronbit not found :(")
            return
        




def scan(port_name, timeout = 2):
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
        return recordBots(raw_data)


def hex_string_to_ascii(hex_string):
    bytes_obj = binascii.unhexlify(hex_string)
    return bytes_obj.decode('ascii', errors='ignore')

def recordBots(data):
    bots = {}
    recordBot = True
    atChar = 0
    maxChar = len(data)
    atByte = ""
    atIntByte = 0
    name_length = 0
    macType = ""
    macAddress = ""
    name = ""
    print(data)
    while (atChar < maxChar):
        if (recordBot and data[atChar] == "0" and data[atChar + 1] == "0"):
            recordBot = False
            atChar += 2
            continue
        if (name_length == 0):
            atByte = data[atChar:atChar+2]
            atIntByte = int((atByte), 16)
            name_length = atIntByte - 7
            atByte = ""
            atIntByte = 0
            atChar += 2
            continue
        if (macType == ""):
            macType = data[atChar:atChar + 2]
            atChar += 2
            continue
        if macAddress == "":
            macAddress = data[atChar:atChar+12]
            atChar+=12
            continue
        if name == "":
            name = data[atChar:(atChar + name_length * 2)]
            name = hex_string_to_ascii(name)
            atChar += name_length * 2
            temp = [macType, macAddress]
            if (not (name in bots.keys())):
                bots[name] = temp
            temp = None
            atByte = ""
            atIntByte = 0
            name_length = 0
            macType = ""
            
            macAddress = ""
            name = ""
            recordBot = True
    return bots

def print_ports(): # prints a list of port
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
    print(result)

