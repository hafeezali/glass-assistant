import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class CaptionServer {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private InputStream inputStream = null;
    private byte[] imageBytes;
    private Integer count = 0;
    private String path = "/home/hafeez/Desktop/Major Project/ImagesReceived/";
    private String imageType = "jpg";
    private String caption;
    private OutputStream out;
    private int byteCount;
    private boolean fileCreated;
    private Process process;
    private BufferedReader bufferedReader;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String isSendingImage;
    private String objects;

    private CaptionServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CaptionServer captionServer = new CaptionServer(5000);
        captionServer.listen();
        System.out.println(captionServer.getCaption());
    }

    private void listen() {
        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Connection Established...");
                dataInputStream = new DataInputStream(socket.getInputStream());
                isSendingImage = dataInputStream.readUTF();
                if (isSendingImage.equals("TRUE")) {
                    inputStream = socket.getInputStream();
                    imageBytes = new byte[16 * 1024];
                    fileCreated = false;
                    while ((byteCount = inputStream.read(imageBytes)) > 0) {
                        if (!fileCreated) {
                            System.out.println("Receiving image: " + count.toString());
                            out = new FileOutputStream(path + count.toString() + "." + imageType);
                            fileCreated = true;
                        }
                        out.write(imageBytes, 0, byteCount);
                    }
                    if (fileCreated) {
                        out.close();
                        System.out.println("Saved image: " + count.toString());
                        socket.close();
                        caption = getCaption();
                        if (caption == null) {
                            caption = "I am not really sure, but I think it's a blur. Please stay still and try again.";
                        }
                        System.out.println(caption);
                        socket = serverSocket.accept();
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.writeUTF(caption);
                        dataOutputStream.flush();
                        dataInputStream.close();
                        dataOutputStream.close();
                        socket.close();
                        objects = getObjects();
                        count++;
                    }
                } else {
                    if (objects == null) {
                        objects = "Sorry, no objects have been identified. Please try again";
                    }
                    dataInputStream.close();
                    socket.close();
                    socket = serverSocket.accept();
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(objects);
                    dataOutputStream.flush();
                    dataInputStream.close();
                    dataOutputStream.close();
                    socket.close();
                    System.out.println(objects);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getObjects() {
        try {
            process = Runtime.getRuntime().exec("python3 ./../MicrosoftAzure/analyze_image.py " + "./../../ImagesReceived/" + count.toString() + "." + imageType + " detect");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            objects = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }

    private String getCaption() {
        try {
            process = Runtime.getRuntime().exec("python3 ./../MicrosoftAzure/analyze_image.py " + "./../../ImagesReceived/" + count.toString() + "." + imageType + " describe");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            caption = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return caption;
    }

}
