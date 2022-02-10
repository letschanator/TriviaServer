import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * class to run a client or player for the trivia game
 * @author Austin Letsch
 */
public class Client extends JFrame {

    /**
     * the output stream to the server
     */
    private ObjectOutputStream output;

    /**
     * the input stream from the server
     */
    private ObjectInputStream input;

    /**
     * the ip address of the server
     */
    private String server;

    /**
     * the client socket
     */
    private Socket client;

    /**
     * the display area for information from the server
     */
    private JTextArea displayArea;

    /**
     * the current question
     */
    private Question question;

    /**
     * the top panel, starts as a way to input names and start the game and changes with each question
     */
    private JPanel topPanel;

    /**
     * the bottom panel consisting of the display area
     */
    private JPanel textAreaPanel;

    /**
     * label explaining the users name or where to input it
     */
    private JLabel nameLabel;

    /**
     * the text field to enter the users name
     */
    private JTextField nameInput;

    /**
     * submit button for their name
     */
    private JButton inputNameButton;

    /**
     * button to start the game
     */
    private JButton startButton;

    /**
     * the name of the player
     */
    private String name;

    /**
     * the submit button for every question
     */
    private JButton submitButton;

    /**
     * boolean for weather the game has started yet
     */
    private boolean gameStarted = false;



    /**
     * constrictor for the client
     * @param host the ip adress of the server
     */
    public Client(String host){
        super("Super Cool Trivia Game!!!!");
        server = host;

        topPanel = new JPanel(); // top panel to begin with, a button to start the game and a field to input a name
        topPanel.setLayout(new FlowLayout());
        nameLabel = new JLabel("Enter your name:  ");
        topPanel.add(nameLabel);
        nameInput = new JTextField(20);
        topPanel.add(nameInput);
        inputNameButton = new JButton("Submit name");
        topPanel.add(inputNameButton);
        startButton = new JButton("Start game");
        topPanel.add(startButton);
        add(topPanel,BorderLayout.CENTER);

        textAreaPanel = new JPanel(); // the bottom panel that is always there, including the submit button and the display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        textAreaPanel.add(displayArea,BorderLayout.EAST);
        submitButton = new JButton("Submit");
        textAreaPanel.add(submitButton,BorderLayout.WEST);
        add(textAreaPanel, BorderLayout.SOUTH);

        setFocusable(true);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(gameStarted){ // if the button is pressed after the game starts, sends the score to the server
                    question.calculateScore();
                    sendData(question.getScore());
                }
            }
        });



        setSize(500,300);
        setVisible(true);

        inputNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                name = nameInput.getText(); // sends name to server
                sendData("NAME:" + name);
                nameLabel.setText("Your name is: " + name );
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gameStarted = true; // starts the game
                sendData("START");
            }
        });
    }

    /**
     * run the client
     */
    public void runClient(){
        try{
            connectToServer();
            getStreams();
            processConnection();
        }catch (EOFException e){
            displayMessage("\nClient terminated connection");
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            closeConnection();
        }
    }

    /**
     * tries to connect to the server
     * @throws IOException an IO exception
     */
    private void connectToServer() throws IOException{
        displayMessage("attempting connection\n");
        client = new Socket(InetAddress.getByName(server), 23717);
        displayMessage("Connected to: " + client.getInetAddress().getHostName());
    }

    /**
     * gets the IO streams
     * @throws IOException an IO exception
     */
    private void getStreams() throws  IOException{
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();
        input = new ObjectInputStream(client.getInputStream());
        displayMessage("got IO streams\n");
    }

    /**
     * possess inputs from the server
     * @throws IOException
     */
    private void processConnection() throws IOException{
        do{
            try{
                Object received = input.readObject();
                if(received instanceof String){ // will either receive a string or one of the types of questions from the server
                    String message = (String) received;
                    if(message.equals("Game Started\n")){
                        gameStarted = true;
                    }if(message.equals("amelia.jpeg")||message.equals("1200px-Antarctica_(orthographic_projection).svg.png")){
                        question.addToMedia(message);
                        topPanel.removeAll();
                        topPanel.add(question.getMainPanel());
                        this.repaint();
                        this.revalidate();
                        this.pack();
                    }else {
                        displayMessage(message);
                    }
                } else if (received instanceof MultipleChoiceQuestion) {
                    topPanel.removeAll();
                    question = (MultipleChoiceQuestion) received;
                    Question currQuestion = new MultipleChoiceQuestion((MultipleChoiceQuestion) question);
                    question = currQuestion; // need to create a new version of the question each time to make the buttons work rightr
                    topPanel.add(currQuestion.getMainPanel());
                    this.repaint();
                    this.revalidate();
                    this.pack();
                } else if (received instanceof ShortAnswerQuestion) {
                    topPanel.removeAll();
                    question = (Question) received;
                    Question currQuestion = new ShortAnswerQuestion((ShortAnswerQuestion) question);
                    question = currQuestion;
                    topPanel.add(question.getMainPanel());
                    this.repaint();
                    this.revalidate();
                    this.pack();
                } else if (received instanceof FillBlankQuestion) {
                    topPanel.removeAll();
                    question = (Question) received;
                    Question currQuestion = new FillBlankQuestion((FillBlankQuestion) question);
                    question = currQuestion;
                    topPanel.add(question.getMainPanel());
                    this.repaint();
                    this.revalidate();
                    this.pack();
                }

            }
            catch (ClassNotFoundException classNotFoundException){
                displayMessage("\nUnknown object type received");
            }

        } while (true);
    }


    /**
     * closes connection the the server
     */
    private void closeConnection(){

        displayMessage("\nClosing Connection");

        try{
            output.close(); //closes all the input and output connections
            input.close();
            client.close();
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    /**
     * sends an object to the server
     * @param send the object to send
     */
    private void sendData(Object send){

        try{
            output.writeObject(send);
            output.flush();
        }catch (IOException e){
            displayArea.append("\n error writing object");
        }
    }

    /**
     * displays a message to the display area
     * @param message the message to display
     */
    private void displayMessage(final String message){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        displayArea.append(message);
                    }
                }
        );
    }
}
