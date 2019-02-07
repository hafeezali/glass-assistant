import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    }

    private void listen() {
        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Connection Established...");
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
                    caption = getCaption();
                    System.out.println("Saved image: " + count.toString());
                    count++;
                    // TODO: Need to send caption back to client
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCaption() {
        return "This is a sample caption!";
    }

}
