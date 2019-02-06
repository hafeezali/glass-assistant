import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CaptionServer {

    private int port;
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private InputStream inputStream = null;
    private byte[] imageBytes;
    private ByteArrayInputStream byteArrayInputStream;
    private BufferedImage bufferedImage = null;
    private Integer count = 0;
    private String path = "/home/hafeez/Desktop/Major Project/ImagesRecieved/";
    private String imageType = "jpg";
    private String caption;

    private CaptionServer(int port) {
        this.port = port;
        try {
            System.out.println("Hello");
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            System.out.println("Connection Established");
            inputStream = socket.getInputStream();
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
                System.out.println("Waiting for image");
                inputStream.read(imageBytes);
                byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                bufferedImage = ImageIO.read(byteArrayInputStream);
                ImageIO.write(bufferedImage, "jpg", new File(path + count.toString() + "." + imageType));
                caption = getCaption();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCaption() {
        return "This is a sample caption!";
    }

}
