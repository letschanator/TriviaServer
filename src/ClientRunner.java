import javax.swing.*;

/**
 * class for running a client
 * @author Austin Letsch
 */
public class ClientRunner {

    /**
     * runner for the client
     * @param args the arguments for the client, first one should be the server IP address
     */
    public static void main(String[] args){
        Client client;

        if(args.length == 0){
            client = new Client("127.0.0.1");
        }else {
            client = new Client(args[0]);
        }
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.runClient();
    }
}
