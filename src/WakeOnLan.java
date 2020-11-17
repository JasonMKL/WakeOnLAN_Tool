import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Reference: https://blog.csdn.net/OneT1me/article/details/84503161
 */
public class WakeOnLan{
    public static void main(String[] args) throws IOException {
        String targetDeviceMACAddress = shouAllMACAddressRecord();
        String magicPackageHead = "FFFFFFFFFFFF";
        String magicPackageContent = null;

        for (int i=0; i<16;  i++){
            if(i==0){
                magicPackageContent = magicPackageHead;
            }
            magicPackageContent += targetDeviceMACAddress;
        }
        sendMagicPackage(magicPackageContent);
    }

    private static String shouAllMACAddressRecord() throws IOException {
        File recordFile = new File("deviceRecord.txt");
        if (!recordFile.exists()) {
            System.out.println("deviceRecord.txt do not exists.");
        }
        FileInputStream fileInputStream = new FileInputStream(recordFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

        int choiceNumber = 0;
        String line;
        ArrayList<HashMap<String, String>> deviceName =  new ArrayList<>();
        while((line = reader.readLine()) != null){
            String[] tokens = line.split("\\|");
            HashMap<String, String> device = new HashMap<>();
            device.put(tokens[0],tokens[1]);
            deviceName.add(device);
            System.out.println(choiceNumber + " : " + tokens[0] + " : " + tokens[1]);
            choiceNumber++;
        }

        System.out.print("Input the number : ");
        Scanner scanner = new Scanner(System.in);
        choiceNumber = scanner.nextInt();
        HashMap<String,String> result = deviceName.get(choiceNumber);
        Object[] returnInfo = result.keySet().toArray();

        return result.get(returnInfo[0]);
    }

    private static void sendMagicPackage(String packageContent){
        byte[] data = hexStringToBytes(packageContent);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Input the broadcast address : ");
        String broadcastAddress = scanner.nextLine();

        try {
            DatagramSocket datagramSocket=new DatagramSocket();
            DatagramPacket datagramPacket=new DatagramPacket(
                    data,0,
                    data.length,
                    InetAddress.getByName(broadcastAddress),
                    34567
            );
            datagramSocket.send(datagramPacket);
            datagramSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            int position = i * 2;
            result[i] = (byte) (charToByte(hexChars[position]) << 4 | charToByte(hexChars[position + 1]));
        }
        return result;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
