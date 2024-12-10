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

        # define port variable for connecting with serial library
        self.port_name = ""

    def set_port(self, port_name):
        try:
            self.port_name = port_name
            port = Serial(port_name)
            print("Succesfully set port!")
            port.close()
        except:
            print(f"Error: can not connect to port: {port_name}")

    def passthrough(self, packet, error=""):
        num_bytes = int(len(packet)/2)
        num_bytes_str = int_to_hex_byte(num_bytes)
        packet = "04" + self.mac_type + num_bytes_str + packet
        try:
            serial_port = Serial(self.port_name)
            serial_port.write(bytearray.fromhex(packet))
            serial_port.close
        except:
            if (not len(error)>0):
                print("Error: problem writing to serial")
            else:
                print(error)

    def connect(self, name, timeout=2):
        discovered_bots = check_bots(self.port_name, timeout)
        if not (name in discovered_bots.keys()):
            print("Error: robot not found in connected bots")
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

            if raw_data == "0309636f6e6e65637465640206ee80a927fd84": # whats so wrong about this
                raise IOError            

            serial_port.close()
        except:
            print("Error: failed to connect to robot")
            return
        self.connected = True

    def disconnect(self): # disconnects from the actively connected robot
        if self.connected:
            print("Error: no bot connected to begin with")
            return
        packet = "010700"
        self.passthrough(packet, "Error: cannot disconnect from bot")

    def activate_motors(self):
        packet = "010000"
        self.passthrough(packet, "Error: can not activate motors")
    
    def deactivate_motors(self):
        packet = "010100"
        self.passthrough(packet, "Error: can not deactivate motors")

    def set_lights(self, r, g, b, i=8):
        packet = "010304"
        if (i==8):
            for i in range(8):
                packet = packet + int_to_hex_byte(i) + int_to_hex_byte(r) + int_to_hex_byte(g) + int_to_hex_byte(b)
                self.passthrough(packet, "Error: can not set lights")
        else:
            packet = packet + int_to_hex_byte(i) + int_to_hex_byte(r) + int_to_hex_byte(g) + int_to_hex_byte(b)
            self.passthrough(packet, "Error: can not set light")
        

def check_bots(port_name, timeout = 2): # searches all of the bots available
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
        print("Error: unable to scan for bots")
        return {}

    if (len(raw_data) == 0):
        print("Error: nothing received from scan")
        return {}
    else:
        return recordBots(raw_data)
    
def int_to_hex_byte(pre_hex): # converts an integer to a proper hex byte
    if (pre_hex < 16):
        return "0" + pre_hex
    return hex(pre_hex)[2:]

def hex_string_to_ascii(hex_string): # converts hex to string
    bytes_obj = binascii.unhexlify(hex_string)
    return bytes_obj.decode('ascii', errors='ignore')

def recordBots(data): # returns a dictionary of bots and info based on raw data
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

def print_ports(): # prints a list of usable ports
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
