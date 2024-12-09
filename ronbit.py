from serial import *
import time
import binascii

class ronbit:
    def __init__(self):
        # define a start variables
        self.name = ""
        self.mac_type = ""
        self.mac_address = ""
        self.connected = False

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
        discovered_bots = check_bots(self.port_name, timeout)
        if not (name in discovered_bots.keys()):
            print("Error: ronbit not found :(")
            return
        mac_info = discovered_bots[name]

        self.name = name
        self.mac_type = mac_info[0]
        self.mac_address = mac_info[1]

        start_time = time.time()
        raw_data = ""

        try:
            serial_port = Serial(self.port_name)
            serial_port.write(bytearray.fromhex('03' + "07" + self.mac_type + self.mac_address))
            while (time.time() - start_time <= timeout):
                if serial_port.in_waiting > 0:
                    data = serial_port.read(1)
                    raw_data += data.hex()

            if raw_data == "0309636f6e6e65637465640206ee80a927fd84":
                raise IOError            

            serial_port.close()
        except:
            print("Error: failed to connect to robot")
            return
        
        print(raw_data)



        self.connected = True

    def disconnect(self):
        bytesStr = "03"
        packet = "04" + self.mac_type + bytesStr + "010700"
        serial_port = Serial(self.port_name)
        serial_port.write(bytearray.fromhex(packet))
        serial_port.close()


def check_bots(port_name, timeout = 2):
    start_time = time.time()
    start_scan = bytearray.fromhex('01')
    stop_scan = bytearray.fromhex('02')
    raw_data = ""
    try:
        serial_port = Serial(port_name)
        serial_port.write(start_scan)
        while (time.time() - start_time <= timeout):
            if serial_port.in_waiting > 0:
                data = serial_port.read(1)
                raw_data += data.hex()

        serial_port.write(stop_scan)
        serial_port.close()
    except:
        print("Unfortunately, th")
        return

    # hex_message = hex_please(raw_data)

    if (len(raw_data) == 0):
        print("not good friend...")
        return {}
    else:
        return recordBots(raw_data)

def hex_string_to_ascii(hex_string):
    bytes_obj = binascii.unhexlify(hex_string)
    return bytes_obj.decode('ascii', errors='ignore')

def recordBots(data):
    bots = {}
    name_length = 0
    name = ""
    while len(data):
        #trim first 2 chars because useless
        data = data[2:]
        
        name_length = int((data[:2]), 16) - 7
        data = data[2:]
        
        mac_type = data[:2]
        data = data[2:]
        
        mac_address = data[:12]
        data = data[12:]
        
        name = hex_string_to_ascii(data[:(name_length * 2)])
        data = data[(name_length * 2):]
        
        bots[name] = [mac_type, mac_address]
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
