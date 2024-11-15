import java.util.concurrent.TimeUnit;

class GroundRobot extends RobotControl {
  private int nameLength;
  
  private String macType;
  
  private String macAddress;
  
  private String name;
  
  private String connectionHandle = "";
  
  private int ultrasonicDistanceInCMFiltered = 0;
  
  private float accelXFiltered = 0.0F;
  
  private float accelYFiltered = 0.0F;
  
  private float accelZFiltered = 0.0F;
  
  private float gyroXRaw = 0.0F;
  
  private float gyroYRaw = 0.0F;
  
  private float gyroZRaw = 0.0F;
  
  private float tempCelsius = 0.0F;
  
  private int ultrasonicDistanceInCMRaw = 0;
  
  private int ultrasonicFilterCoeff = 0;
  
  private int[] vTargets = new int[2];
  
  private float accelXRaw = 0.0F;
  
  private float accelYRaw = 0.0F;
  
  private float accelZRaw = 0.0F;
  
  private int leftEncCount = 0;
  
  private int rightEncCount = 0;
  
  private float magnetometerXRaw = 0.0F;
  
  private float magnetometerYRaw = 0.0F;
  
  private float magnetometerZRaw = 0.0F;
  
  private float headingDegrees = 0.0F;
  
  private float magnetometerXFiltered = 0.0F;
  
  private float magnetometerYFiltered = 0.0F;
  
  private float magnetometerZFiltered = 0.0F;
  
  private static boolean isWaiting = false;
  
  private String lastWaitSensor;
  
  private int lastWaitValue = -1;
  
  private int prefFlag = 0;
  
  public GroundRobot(int nameLength, String macType, String macAddress, String name) {
    this.nameLength = nameLength;
    this.macType = macType;
    this.macAddress = macAddress;
    this.name = name;
    this.connectionHandle = "";
  }
  
  public GroundRobot(int nameLength, String macType, String macAddress, String name, String connectionHandle) {
    this.nameLength = nameLength;
    this.macType = macType;
    this.macAddress = macAddress;
    this.name = name;
    this.connectionHandle = connectionHandle;
  }
  
  public void setIsWaiting(boolean set) {
    isWaiting = set;
  }
  
  public boolean getIsWaiting() {
    return isWaiting;
  }
  
  public int getNameLength() {
    return this.nameLength;
  }
  
  public String getMacType() {
    return this.macType;
  }
  
  public String getMacAddress() {
    return this.macAddress;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setConnectionHandle(String ch) {
    this.connectionHandle = ch;
  }
  
  public String getConnectionHandle() {
    return this.connectionHandle;
  }
  
  public void setName(String nameStr) {
    if (nameStr.length() > 8)
      nameStr.substring(0, 7); 
    String nameStrHex = asciiToHexString(nameStr);
    int nameLen = nameStrHex.length() / 2 + 1;
    String nameLenStrHex = intToHexString(nameLen);
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("03") + "02" + nameLenStrHex + nameStrHex + "00";
    passThrough(this.macType, packet);
    this.name = nameStr;
  }
  
  public boolean equals(Object o) {
    boolean isEqual = false;
    String mac1 = "";
    String mac2 = "";
    if (o instanceof GroundRobot) {
      GroundRobot c = (GroundRobot)o;
      mac1 = getMacAddress();
      mac2 = c.getMacAddress();
      isEqual = mac1.equals(mac2);
    } 
    return isEqual;
  }
  
  public void setUltrasonicFilterCoeff(int value) {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("03") + "03" + "01" + intToHexString(value);
    passThrough(this.macType, packet);
  }
  
  public int getUltrasonicFilterCoeff() {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("09") + "03" + "0100";
    passThrough(this.macType, packet);
    this.prefFlag = 0;
    for (long stop = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(500L); stop > System.nanoTime(); ) {
      try {
        Thread.sleep(20L);
      } catch (Exception E) {
        E.printStackTrace();
      } 
      if (this.prefFlag == 1)
        return this.ultrasonicFilterCoeff; 
    } 
    System.out.println("Data Timeout Error");
    return 0;
  }
  
  public void setVTargets(int valueL, int valueR) {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("03") + "09" + "04";
    int b0 = (valueL & 0xFF00) >> 8;
    int b1 = valueL & 0xFF;
    packet = String.valueOf(packet) + intToHexString(b0);
    packet = String.valueOf(packet) + intToHexString(b1);
    b0 = (valueR & 0xFF00) >> 8;
    b1 = valueR & 0xFF;
    packet = String.valueOf(packet) + intToHexString(b0);
    packet = String.valueOf(packet) + intToHexString(b1);
    passThrough(this.macType, packet);
  }
  
  public int[] getVTargets() {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("09") + "09" + "0100";
    passThrough(this.macType, packet);
    this.prefFlag = 0;
    for (long stop = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(500L); stop > System.nanoTime(); ) {
      try {
        Thread.sleep(20L);
      } catch (Exception E) {
        E.printStackTrace();
      } 
      if (this.prefFlag == 1)
        return this.vTargets; 
    } 
    System.out.println("Data Timeout Error");
    int[] fail_data = new int[2];
    return fail_data;
  }
  
  public void setPrefFlag(int state) {
    this.prefFlag = state;
  }
  
  public int getPrefFlag() {
    return this.prefFlag;
  }
  
  public void writeVTargets(int valueL, int valueR) {
    this.vTargets[0] = valueL;
    this.vTargets[1] = valueR;
  }
  
  public void writeUltrasonicFilterCoeff(int value) {
    this.ultrasonicFilterCoeff = value;
  }
  
  public void activateMotors() {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "00" + "00";
    passThrough(this.macType, packet);
  }
  
  public void deactivateMotors() {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "01" + "00";
    passThrough(this.macType, packet);
  }
  
  public void setupWait(String sensor) {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "0C" + "05" + sensor + "00000000";
    passThrough(this.macType, packet);
    this.lastWaitSensor = sensor;
    this.lastWaitValue = 0;
  }
  
  public void setupWait(String sensor, int value) {
    int b0 = (value & 0xFF000000) >> 24;
    int b1 = (value & 0xFF0000) >> 16;
    int b2 = (value & 0xFF00) >> 8;
    int b3 = value & 0xFF;
    String b0Str = intToHexString(b0);
    String b1Str = intToHexString(b1);
    String b2Str = intToHexString(b2);
    String b3Str = intToHexString(b3);
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "0C" + "05" + sensor + b0Str + b1Str + b2Str + b3Str;
    passThrough(this.macType, packet);
    this.lastWaitSensor = sensor;
    this.lastWaitValue = value;
  }
  
  public void syncLights() {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "04" + "00";
    passThrough(this.macType, packet);
  }
  
  public void setLights(int red, int green, int blue) {
    for (int i = 0; i < 8; i++)
      setLight(i, red, green, blue); 
  }
  
  public void setLight(int index, int red, int green, int blue) {
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "03" + "04" + intToHexString(index) + intToHexString(red) + intToHexString(green) + intToHexString(blue);
    passThrough(this.macType, packet);
  }
  
  public void enableSensor(String sensorName, int sensorState) {
    msgCodes.getClass();
    String packet = String.valueOf("02") + sensorName + "01" + intToHexString(sensorState);
    passThrough(this.macType, packet);
  }
  
  public void playNote(String note, int duration, boolean wait) {
    int durationByteL = duration >> 8;
    int durationByteH = duration & 0xFF;
    String durationByteLStr = intToHexString(durationByteL);
    String durationByteHStr = intToHexString(durationByteH);
    String waitStr = "00";
    if (wait) {
      waitStr = "01";
    } else {
      waitStr = "00";
    } 
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "10" + "04" + note + durationByteLStr + durationByteHStr + waitStr;
    passThrough(this.macType, packet);
    if (wait) {
      isWaiting = true;
      while (isWaiting)
        waitTime(10L); 
    } 
  }
  
  public void playSong(String song, boolean wait) {
    String waitStr = "00";
    if (wait) {
      waitStr = "01";
    } else {
      waitStr = "00";
    } 
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "11" + "02" + song + waitStr;
    passThrough(this.macType, packet);
    if (wait) {
      isWaiting = true;
      while (isWaiting)
        waitTime(10L); 
    } 
  }
  
  public void move(String lmd, String rmd, float lms, float rms, boolean wait, boolean encoders) {
    if (wait && this.lastWaitValue == -1) {
      System.out.println("Missing Wait Error");
    } else if (wait && this.lastWaitValue != -1 && this.lastWaitSensor.equals("05") && (lmd.equals(rmd) || rms != lms)) {
      System.out.println("Rotation Wait Error");
    } else if (wait && this.lastWaitValue != -1 && this.lastWaitSensor.equals("04") && (!lmd.equals(rmd) || rms != lms)) {
      System.out.println("Distance Wait Error");
    } 
    int lmsInt = Math.min(100, Math.round(lms * 100.0F));
    int rmsInt = Math.min(100, Math.round(rms * 100.0F));
    String waitStr = "00";
    if (wait) {
      waitStr = "01";
    } else {
      waitStr = "00";
    } 
    String encodersStr = "00";
    if (encoders) {
      encodersStr = "00";
    } else {
      encodersStr = "01";
    } 
    msgCodes.getClass();
    msgCodes.getClass();
    String packet = String.valueOf("01") + "02" + "05" + lmd + rmd + intToHexString(lmsInt) + intToHexString(rmsInt) + waitStr + encodersStr;
    passThrough(this.macType, packet);
    if (wait) {
      isWaiting = true;
      while (isWaiting)
        waitTime(10L); 
    } 
  }
  
  public void setUSDistanceCMFiltered(int cm) {
    this.ultrasonicDistanceInCMFiltered = cm;
  }
  
  public int getUSDistanceCMFiltered() {
    return this.ultrasonicDistanceInCMFiltered;
  }
  
  public void setAccelXFiltered(float accelX) {
    this.accelXFiltered = accelX;
  }
  
  public float getAccelXFiltered() {
    return this.accelXFiltered;
  }
  
  public void setAccelYFiltered(float accelY) {
    this.accelYFiltered = accelY;
  }
  
  public float getAccelYFiltered() {
    return this.accelYFiltered;
  }
  
  public void setAccelZFiltered(float accelZ) {
    this.accelZFiltered = accelZ;
  }
  
  public float getAccelZFiltered() {
    return this.accelZFiltered;
  }
  
  public void setGyroXRaw(float gyroX) {
    this.gyroXRaw = gyroX;
  }
  
  public float getGyroXRaw() {
    return this.gyroXRaw;
  }
  
  public void setGyroYRaw(float gyroY) {
    this.gyroYRaw = gyroY;
  }
  
  public float getGyroYRaw() {
    return this.gyroYRaw;
  }
  
  public void setGyroZRaw(float gyroZ) {
    this.gyroZRaw = gyroZ;
  }
  
  public float getGyroZRaw() {
    return this.gyroZRaw;
  }
  
  public void setTempCelsius(float tempC) {
    this.tempCelsius = tempC;
  }
  
  public float getTempCelsius() {
    return this.tempCelsius;
  }
  
  public void setUSDistanceCMRaw(int cm) {
    this.ultrasonicDistanceInCMRaw = cm;
  }
  
  public int getUSDistanceCMRaw() {
    return this.ultrasonicDistanceInCMRaw;
  }
  
  public void setAccelXRaw(float accelX) {
    this.accelXRaw = accelX;
  }
  
  public float getAccelXRaw() {
    return this.accelXRaw;
  }
  
  public void setAccelYRaw(float accelY) {
    this.accelYRaw = accelY;
  }
  
  public float getAccelYRaw() {
    return this.accelYRaw;
  }
  
  public void setAccelZRaw(float accelZ) {
    this.accelZRaw = accelZ;
  }
  
  public float getAccelZRaw() {
    return this.accelZRaw;
  }
  
  public void setLeftEncCount(int lEnc) {
    this.leftEncCount = lEnc;
  }
  
  public int getLeftEncCount() {
    return this.leftEncCount;
  }
  
  public void setRightEncCount(int rEnc) {
    this.rightEncCount = rEnc;
  }
  
  public int getRightEncCount() {
    return this.rightEncCount;
  }
  
  public void setMagnetometerXRaw(float magnetometerX) {
    this.magnetometerXRaw = magnetometerX;
  }
  
  public float getMagnetometerXRaw() {
    return this.magnetometerXRaw;
  }
  
  public void setMagnetometerYRaw(float magnetometerY) {
    this.magnetometerYRaw = magnetometerY;
  }
  
  public float getMagnetometerYRaw() {
    return this.magnetometerYRaw;
  }
  
  public void setMagnetometerZRaw(float magnetometerZ) {
    this.magnetometerZRaw = magnetometerZ;
  }
  
  public float getMagnetometerZRaw() {
    return this.magnetometerZRaw;
  }
  
  public void setHeadingDegrees(float deg) {
    this.headingDegrees = deg;
  }
  
  public float getHeadingDegrees() {
    return this.headingDegrees;
  }
  
  public void setMagnetometerXFiltered(float magXFiltered) {
    this.magnetometerXFiltered = magXFiltered;
  }
  
  public float getMagnetometerXFiltered() {
    return this.magnetometerXFiltered;
  }
  
  public void setMagnetometerYFiltered(float magYFiltered) {
    this.magnetometerYFiltered = magYFiltered;
  }
  
  public float getMagnetometerYFiltered() {
    return this.magnetometerYFiltered;
  }
  
  public void setMagnetometerZFiltered(float magZFiltered) {
    this.magnetometerZFiltered = magZFiltered;
  }
  
  public float getMagnetometerZFiltered() {
    return this.magnetometerZFiltered;
  }
  
  public String toString() {
    return "Name[" + this.name + "] MacType[" + this.macType + "] MacAddress[" + this.macAddress + "] Connection Handle[" + this.connectionHandle + "]";
  }
}
