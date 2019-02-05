import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CaptionServer {

    private int port;
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private ObjectInputStream objectInputStream = null;
    private BufferedImage bufferedImage = null;
    private Integer count = 0;
    private String path = "/home/hafeez/Desktop/Major Project/ImagesRecieved/";
    private String imageType = "jpg";
    private String caption;

    private CaptionServer(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
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
                bufferedImage = (BufferedImage) this.objectInputStream.readObject();
                ImageIO.write(bufferedImage, "jpg", new File(path + count.toString() + "." + imageType));
                caption = getCaption();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCaption() {
        return "This is a sample caption!";
    }

}
