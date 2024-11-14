import binascii

def hex_string_to_ascii(hex_string):
    bytes_obj = binascii.unhexlify(hex_string)
    return bytes_obj.decode('ascii', errors='ignore')

data = "000f00ee80a927fd846c722038303a6565000f00e480a927fd846c722038303a6534000f00e480a927fd846c722038303a6534000f00ee80a927fd846c722038303a6565000f00e480a927fd846c722038303a6534000f00ee80a927fd846c722038303a6565000f00e480a927fd846c722038303a6534000f00e480a927fd846c722038303a6534000f00ee80a927fd846c722038303a6565000f00e480a927fd846c722038303a6534"

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
            print(atChar)
    return bots

def improved_record_bots(data):
    bots = {}
    for ()

print(recordBots(data))