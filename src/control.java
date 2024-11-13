import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class RobotControl {
  static String rawData = "";
  
  static String hexMessage = "";
  
  static int[] readBytes;
  
  static DecimalFormat df = new DecimalFormat("00");
  
  static SerialPort comPort = null;
  
  static InputStream in = null;
  
  protected static OutputStream commands = null;
  
  private static ArrayList<GroundRobot> discoveredBots = null;
  
  static ArrayList<GroundRobot> connectedBots = new ArrayList<>();
  
  private static byte[] alive = parseHexBinary("00");
  
  private static byte[] startScan = parseHexBinary("01");
  
  private static byte[] stopScan = parseHexBinary("02");
  
  protected static boolean canReadFlag = true;
  
  protected static MessageCodes msgCodes = new MessageCodes();
  
  protected static int connectedRobots = 0;
  
  private static byte[] parseHexBinary(String s) {
    int len = s.length();
    if (len % 2 != 0)
      throw new IllegalArgumentException("hexBinary needs to be even-length: " + s); 
    byte[] out = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int h = hexToBin(s.charAt(i));
      int l = hexToBin(s.charAt(i + 1));
      if (h == -1 || l == -1)
        throw new IllegalArgumentException("contains illegal character for hexBinary: " + s); 
      out[i / 2] = (byte)(h * 16 + l);
    } 
    return out;
  }
  
  private static int hexToBin(char ch) {
    if ('0' <= ch && ch <= '9')
      return ch - 48; 
    if ('A' <= ch && ch <= 'F')
      return ch - 65 + 10; 
    if ('a' <= ch && ch <= 'f')
      return ch - 97 + 10; 
    return -1;
  }
  
  public void checkPorts(String usb_address) {
    SerialPort[] comArr = SerialPort.getCommPorts();
    for (int i = 0; i < comArr.length; i++) {
      if (usb_address.equals(comArr[i].getSystemPortName()))
        comPort = comArr[i]; 
    } 
  }
  
  public void waitTime(long waitTimeSpan) {
    try {
      Thread.sleep(waitTimeSpan);
    } catch (Exception E) {
      E.printStackTrace();
    } 
  }
  
  public void setup(String usb_address) {
    checkPorts(usb_address);
    comPort.openPort();
    comPort.setBaudRate(9600);
    comPort.setComPortTimeouts(1, 100, 0);
    if (!setStreams(comPort)) {
      System.exit(0);
    } else {
      System.out.println("Command Port Input and OutPut Streams successfully set.\n");
    } 
    try {
      in.skip(in.available());
    } catch (IOException e) {
      e.printStackTrace();
    } 
    if (!dongleAlive()) {
      System.out.println("Dongle Issues");
      System.exit(0);
    } 
  }
  
  public ArrayList<GroundRobot> scan(long timeoutTime) {
    rawData = "";
    hexMessage = "";
    ArrayList<GroundRobot> findBots = new ArrayList<>();
    try {
      commands.write(startScan);
      long time = System.currentTimeMillis();
      do {
        if (comPort.bytesAvailable() <= 0)
          continue; 
        rawData = String.valueOf(rawData) + (char)in.read();
      } while (System.currentTimeMillis() - time <= timeoutTime);
      commands.write(stopScan);
    } catch (Exception E) {
      E.printStackTrace();
    } 
    hexMessage = hexPlease(rawData);
    rawData = "";
    if (hexMessage.length() == 0) {
      hexMessage = "";
      System.out.println("ERROR: No Robots Found");
      return findBots;
    } 
    discoveredBots = recordBots(hexMessage);
    return discoveredBots;
  }
  
  public void listen() {
    (new Thread(new ReadInput())).start();
  }
  
  public GroundRobot connect(String botName) {
    for (GroundRobot bot : discoveredBots) {
      rawData = "";
      hexMessage = "";
      try {
        in.skip(in.available());
      } catch (IOException e) {
        e.printStackTrace();
      } 
      if (bot.getName().equals(botName)) {
        int TIMEOUT_LIM = 3200;
        int connectCount = 0;
        int CONNECT_LIM = 5;
        System.out.print("Connecting...");
        for (int i = 0; i < CONNECT_LIM; i++) {
          try {
            msgCodes.getClass();
            commands.write(parseHexBinary(String.valueOf("03") + "07" + bot.getMacType() + bot.getMacAddress()));
          } catch (IOException e1) {
            e1.printStackTrace();
          } 
          long time = System.currentTimeMillis();
          do {
            if (comPort.bytesAvailable() <= 0)
              continue; 
            try {
              rawData = String.valueOf(rawData) + (char)in.read();
            } catch (IOException e1) {
              e1.printStackTrace();
            } 
          } while (System.currentTimeMillis() - time <= TIMEOUT_LIM);
          hexMessage = hexPlease(rawData);
          if (hexMessage.length() == 0) {
            connectCount++;
            if (connectCount == CONNECT_LIM) {
              System.out.println("Connection Failed.\n");
              try {
                msgCodes.getClass();
                commands.write(parseHexBinary(String.valueOf("05") + "0100"));
              } catch (IOException e1) {
                e1.printStackTrace();
              } 
              System.exit(0);
              return null;
            } 
            System.out.print(connectCount + 1);
            System.out.print("...");
          } else {
            connectedBots.add(bot);
            connectedRobots++;
            System.out.println("Robot [" + bot.getName() + "] Successfully Connected.\n");
            try {
              Thread.sleep(600L);
            } catch (Exception E) {
              E.printStackTrace();
            } 
            return bot;
          } 
        } 
      } 
    } 
    System.out.println("Named Robot Not Found.\n");
    System.exit(0);
    return null;
  }
  
  public void disconnect(GroundRobot robot) {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "07" + "00";
    passThrough(robot.getMacType(), packet);
  }
  
  private static ArrayList<GroundRobot> recordBots(String data) {
    ArrayList<GroundRobot> record = new ArrayList<>();
    GroundRobot temp = null;
    boolean recordBot = true;
    int atChar = 0;
    int maxChar = data.length();
    String atByte = "";
    int atIntByte = 0;
    int nameLength = 0;
    String macType = "";
    String macAddress = "";
    String name = "";
    while (atChar < maxChar) {
      if (recordBot && data.charAt(atChar) == '0' && data.charAt(atChar + 1) == '0') {
        recordBot = false;
        atChar += 2;
        continue;
      } 
      if (nameLength == 0) {
        atByte = data.substring(atChar, atChar + 2);
        atIntByte = Integer.decode("0x" + atByte).intValue();
        nameLength = atIntByte - 7;
        atByte = "";
        atIntByte = 0;
        atChar += 2;
        continue;
      } 
      if (macType.equals("")) {
        macType = data.substring(atChar, atChar + 2);
        atChar += 2;
        continue;
      } 
      if (macAddress.equals("")) {
        macAddress = data.substring(atChar, atChar + 12);
        atChar += 12;
        continue;
      } 
      if (name.equals("")) {
        name = data.substring(atChar, atChar + nameLength * 2);
        name = hexStringToASCII(name);
        atChar += nameLength * 2;
        temp = new GroundRobot(nameLength, macType, macAddress, name);
        if (!botMacAddressExists(record, temp))
          record.add(temp); 
        temp = null;
        atByte = "";
        atIntByte = 0;
        nameLength = 0;
        macType = "";
        macAddress = "";
        name = "";
        recordBot = true;
      } 
    } 
    return record;
  }
  
  private static String hexStringToASCII(String hexString) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < hexString.length() - 1; i += 2) {
      String output = hexString.substring(i, i + 2);
      int decimal = Integer.parseInt(output, 16);
      sb.append((char)decimal);
    } 
    return sb.toString();
  }
  
  protected String asciiToHexString(String asciiString) {
    String hexString = "";
    for (int i = 0; i < asciiString.length(); i++) {
      int asciiByte = asciiString.charAt(i);
      hexString = String.valueOf(hexString) + intToHexString(asciiByte);
    } 
    return hexString;
  }
  
  private static boolean botMacAddressExists(ArrayList<GroundRobot> arr, GroundRobot botToTest) {
    for (GroundRobot bot : arr) {
      if (bot.getMacAddress().equals(botToTest.getMacAddress()))
        return true; 
    } 
    return false;
  }
  
  private static boolean setStreams(SerialPort com) {
    for (int i = 0; i < 5; i++) {
      in = com.getInputStream();
      commands = com.getOutputStream();
    } 
    if (in == null && commands == null) {
      System.out.println("ERROR: Failure setting InputStream & OutputStream | Please restart");
      return false;
    } 
    if (commands == null) {
      System.out.println("ERROR: Failure setting OutputStream | Please restart");
      return false;
    } 
    if (in == null) {
      System.out.println("ERROR: Failure setting InputStream | Please restart");
      return false;
    } 
    return true;
  }
  
  private static boolean dongleAlive() {
    try {
      commands.write(alive);
      long time = System.currentTimeMillis();
      do {
        if (comPort.bytesAvailable() <= 0)
          continue; 
        rawData = String.valueOf(rawData) + (char)in.read();
      } while (System.currentTimeMillis() - time <= 500L);
    } catch (IOException e) {
      e.printStackTrace();
    } 
    hexMessage = hexPlease(rawData);
    if (hexMessage.length() == 0) {
      rawData = "";
      hexMessage = "";
      return false;
    } 
    rawData = "";
    hexMessage = "";
    return true;
  }
  
  static String hexPlease(String arg) {
    StringBuilder output = new StringBuilder();
    char[] ch = arg.toCharArray();
    byte b;
    int i;
    char[] arrayOfChar1;
    for (i = (arrayOfChar1 = ch).length, b = 0; b < i; ) {
      char c = arrayOfChar1[b];
      int j = c;
      j = (j >= 65280) ? (j - 65280) : j;
      String append = Integer.toHexString(j).toUpperCase();
      if (append.length() == 1)
        append = "0" + append; 
      output.append(append);
      b++;
    } 
    String completeHex = output.toString();
    if (completeHex.length() == 0)
      return completeHex; 
    if (!completeHex.startsWith("00"))
      completeHex = "00" + completeHex; 
    if (completeHex.length() % 2 != 0)
      completeHex = "0" + completeHex; 
    return completeHex;
  }
  
  public String intToHexString(int preHex) {
    if (preHex < 16)
      return "0" + Integer.toHexString(preHex); 
    return Integer.toHexString(preHex);
  }
  
  protected void passThrough(String handle, String packet) {
    int numBytes = packet.length() / 2;
    String numBytesStr = intToHexString(numBytes);
    msgCodes.getClass();
    packet = String.valueOf("04") + handle + numBytesStr + packet;
    try {
      byte[] passBytes = parseHexBinary(packet);
      commands.write(passBytes);
    } catch (Exception E) {
      E.printStackTrace();
    } 
  }
}
