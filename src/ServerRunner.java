import javax.swing.*;

/**
 * class to run the server
 * @author Austin Letsch
 */
public class ServerRunner {

    /**
     * runner for the server
     * @param args arguments for the server
     */
    public static void main(String[] args){
        Server server = new Server();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.runServer();
    }
}
