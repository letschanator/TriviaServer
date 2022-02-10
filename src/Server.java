import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * class to run the server
 * @author Austin Letsch
 */
public class Server extends JFrame {

    /**
     * Text area to append any information to
     */
    private JTextArea outputArea;

    /**
     * checks if any player has started the game yet
     */
    private boolean gameStarted = false;

    /**
     * the arrayList of all of the players currently playing
     */
    private ArrayList<Player> players;

    /**
     * the server socket
     */
    private ServerSocket server;

    /**
     * executor service for the multithreaded server
     */
    private ExecutorService runGame;


    /**
     * the number of questions that have been sent to the users so far
     */
    private int numberOfQuestionsSent = 0;


    /**
     * the question data set
     */
    private DataSet questions;

    /**
     * constructor for the server
     */
    public Server(){
        super("Trivia Server");

        runGame = Executors.newCachedThreadPool();

        players = new ArrayList<>();

        try {
            server = new ServerSocket(23717, 100);
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        outputArea = new JTextArea();
        add(outputArea, BorderLayout.CENTER);
        displayMessage("Server awaiting connections\n");

        setSize(300,300);
        setVisible(true);


        questions = new DataSet();
    }

    /**
     * run the server
     */
    public void runServer(){
        int playerNum = 1;
        while (!gameStarted){ // waits for any number of players to join the game until one of the players starts the game
            try{
                Player player = new Player(server.accept(),playerNum);
                players.add(player);
                runGame.execute(players.get(playerNum-1));
                playerNum++;
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }



        for(Player player:players){ // after game has started makes all players unsuspended
            player.setSuspended(false);
        }
    }

    /**
     * appends a message to the display area
     * @param messageToDisplay  the message to display
     */
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        outputArea.append(messageToDisplay); // add message
                    }
                }
        );
    }


    /**
     * call once the game starts
     */
    public void startGame(){
        gameStarted = true;
    }

    /**
     * sends a question to all players
     */
    public void sendQuestion(Question question){
        for (Player player:players){
            player.setAnswered(false);
            if(question.getFileName().equals("")){
                player.sendData(question);
            }else{
                player.sendData(question);
                player.sendData(question.getFileName());
            }
        }
        numberOfQuestionsSent++;
    }

    /**
     * wait for all players to answer the current question
     */
    public void waitOnPlayers(){
        boolean done = true;
        for(Player player:players){
            if(!player.getAnswered()){
                done = false;
            }
        }
        if(done){
            if(numberOfQuestionsSent < 5){ // sends another question if 5 haven't already been sent
                questions.getNewRandom();
                sendQuestion(questions.getRandomQuestion());
            }else{
                gameStarted = false;
                endGame();
            }

        }
    }

    /**
     * sends data to all players in players
     * @param send the object to send
     */
    public void sendDataToAll(Object send){
        for(Player player:players){
            player.sendData(send);
        }
    }

    /**
     * removes a player from the list that has disconnected
     * @param index index in the array list of the player
     */
    public void removeFromPlayerList(int index){
        players.remove(index);
        for(int i = index; i < players.size(); i++){ // sets back all of the player numbers to correctly represent the index in in the list
            players.get(i).setPlayerNumber(players.get(i).getPlayerNumber()-1);
        }
    }

    /**
     * ends the game by sorting the player list and sends a score board to each player
     */
    public void endGame(){
        Collections.sort(players); // sorts based on score
        String scoreBoard = "The top players are:\n";
        for(int i = 0; i < players.size(); i++){
            int num = i + 1;
            if(players.get(i).getPlayerName().equals("")) {
                scoreBoard = scoreBoard + num + ". player " + players.get(i).getPlayerNumber() + " had a score of " + players.get(i).getScore() + "\n";
            }else{
                scoreBoard = scoreBoard + num + ". " + players.get(i).getPlayerName() + " had a score of " + players.get(i).getScore() + "\n";
            }
        }
        sendDataToAll(scoreBoard);
    }


    /**
     * the player implementation, implements runnable for the Executor service
     * implements Comparable for easy sort based on score at the end
     */
    private class Player implements Runnable,Comparable<Player>{

        /**
         * the connection socket
         */
        private Socket connection;

        /**
         * the input stream from this player
         */
        private ObjectInputStream input;

        /**
         * the output stream to this player
         */
        private ObjectOutputStream output;

        /**
         * the player name if the player has provided one
         */
        private String playerName;

        /**
         * the unique player number for this player
         * corresponds the the index in players +1
         */
        private int playerNumber;

        /**
         * weather this player is suspended
         */
        private boolean suspended = true;

        /**
         * the score of the player
         */
        private Double score = 0.0;

        /**
         * weather the player has answered this question
         */
        private boolean answered = false;

        /**
         * constructor for a player
         * @param socket the socket to connect to
         * @param number the player number
         */
        public Player(Socket socket, int number){
            this.playerNumber = number;
            connection = socket;
            this.playerName = "";

            try{
               output = new ObjectOutputStream(connection.getOutputStream());
               output.flush();
               input = new ObjectInputStream(connection.getInputStream());
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }


        /**
         * run the server side elements for this player
         */
        @Override
        public void run() {
            try {
                displayMessage(playerNumber + " connected\n");
                sendDataToAll(playerNumber + " player(s) connected, waiting for a player to start game\n");

                while (true) { // checks if the player sends a name or starts the game
                    try {
                        Object received = input.readObject();
                        if (received instanceof String) { // only strings that matter are the clients name and if they want to start
                            String message = (String) received;
                            if (message.substring(0, 5).equals("NAME:")) { //clients sends player name as NAME:<name>
                                playerName = message.substring(5);
                            }
                            if (received.equals("START")) { //signal to start game
                                sendDataToAll("Game Started\n");
                                startGame();
                                questions.getNewRandom();
                                sendQuestion(questions.getRandomQuestion());
                            }
                        } else { // otherwise waits for a score to be sent
                            if (received instanceof Double && !answered && gameStarted) { //if the object sent is a double, and they have not answered the question yet and the game has started
                                score += (Double) received; // after game has started, clients should only send score updates
                                answered = true;
                                sendData("You got " + (Double) received + " points on that question\n");
                                waitOnPlayers();
                            }
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }



        /**
         * send an object to this player
         * @param send the object to send
         */
        private void sendData(Object send){
            try{
                output.writeObject(send);
                output.flush();
            }catch (IOException e){
                displayMessage("Error writing object");
            }
        }

        /**
         * closes the connection to this player
         */
        public  void closeConnection() {
            try {
                input.close();
                output.close();
                connection.close();
                removeFromPlayerList(playerNumber - 1); // this player is no longer connected, so have to remove it from the list
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * sets the state of suspended
         * @param suspended
         */
        public void setSuspended(boolean suspended) {
            this.suspended = suspended;
        }

        /**
         * getter for player name
         * @return the players name
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * getter for the score of this player
         * @return the score of this player
         */
        public Double getScore() {
            return score;
        }

        /**
         * getter for weather this player has answered the current question
         * @return true if player has answered the question false otherwise
         */
        public boolean getAnswered(){
            return answered;
        }

        /**
         * setter for the player number of this player
         * @param playerNumber the new player number
         */
        public void setPlayerNumber(int playerNumber) {
            this.playerNumber = playerNumber;
        }

        /**
         * getter for the player number of this player
         * @return the player number
         */
        public int getPlayerNumber() {
            return playerNumber;
        }

        /**
         * compares two players based on their score
         * @param player the other player to compare to
         * @return 0 if their equal, 1 if the other player is less, -1 otherwise
         */
        @Override
        public int compareTo(Player player) {
            if (score == player.getScore()){
                return 0;
            }else if(score > player.getScore()){
                return -1;
            }else
                return 1;
        }

        /**
         * setter for weather the player has answered the current question or not
         * @param answered weather the player has answered the question
         */
        public void setAnswered(boolean answered) {
            this.answered = answered;
        }
    }
}
