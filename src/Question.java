import javax.swing.*;
import java.awt.*;

/**
 * An abstract class which contains all common fields and methods of different question types
 * @author India Berry
 */
public abstract class Question extends JPanel {
    /**
     * {String} contains the question itself
     */
    private String questionDescription;

    /**
     * {double} contains score for question object
     */
    private double score;

    /**
     * {String} contains user instructions for the question (how to answer)
     */
    private String questionPrompt;

    /**
     * {JPanel} will contain question prompt and question description
     */
    private final JPanel questionInitial = new JPanel();

    /**
     * {JPanel} panel which contains all GUI components
     */
    private final JPanel mainPanel = new JPanel();

    /**
     * {JPanel} will contain components for the user to manipulate in order to select or type their answer for the question
     */
    private final JPanel questionOptions = new JPanel();

    /**
     * {JPanel} will contain media
     */
    private final JPanel media = new JPanel();

    /**
     * stores the file name if there is media attached to the question
     */
    private String fileName  = "";


    /**
     * Constructor for Questionnaire
     * @param questionDescription {String} question to be answered
     * @param questionPrompt {String} User instructions for how to answer the question
     */
    public Question(String questionDescription, String questionPrompt){
        setQuestionDescription(questionDescription);
        setQuestionPrompt(questionPrompt);

        questionInitial.setLayout(new BorderLayout());
        questionOptions.setLayout(new FlowLayout());
        media.setLayout(new FlowLayout());

        JTextField questionName = new JTextField(getQuestionDescription());
        questionName.setText(questionDescription);
        questionName.setEditable(false);

        JTextField questionInstructions = new JTextField(getQuestionPrompt());
        questionInstructions.setText(questionPrompt);
        questionInstructions.setEditable(false);

        questionInitial.add(questionInstructions, BorderLayout.NORTH);
        questionInitial.add(questionName, BorderLayout.CENTER);
        mainPanel.add(questionInitial, BorderLayout.NORTH);
        mainPanel.add(questionOptions,BorderLayout.SOUTH);
        mainPanel.add(media, BorderLayout.CENTER);

        mainPanel.setVisible(true);
    }


    /**
     * Setter for question description
     * @param questionDescription {String}
     */
    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }


    /**
     * Getter for question description
     * @return {String} questionDescription
     */
    public String getQuestionDescription(){
        return questionDescription;
    }

    /**
     * Setter for question prompt
     * @param questionPrompt {String}
     */
    public void setQuestionPrompt(String questionPrompt){
        this.questionPrompt = questionPrompt;
    }



    /**
     * Getter for question prompt
     * @return {String} questionPrompt
     */
    public String getQuestionPrompt(){
        return questionPrompt;
    }

    /**
     * Getter for mainPanel
     * @return {JPanel}
     */
    public JPanel getMainPanel(){ return mainPanel; }

    /**
     * Add a component to the questionOptions JPanel
     * @param component {JComponent}
     */
    public void addToQuestionOptions(JComponent component){
        questionOptions.add(component);
    }

    /**
     * Add a file (image) to the media panel
     * @param filePath {String}
     */
    public void addToMedia(String filePath) {
        Icon image = new ImageIcon(getClass().getResource( filePath));
        JLabel ImageLabel = new JLabel(image);
        ImageLabel.setMaximumSize(media.getSize()); // meant to scale image, may not work
        ImageLabel.setOpaque(true);
        media.add(ImageLabel);
        mainPanel.repaint();
        mainPanel.revalidate();
        fileName = filePath;
    }

    /**
     * Setter for score
     * @param score {double}
     */
    public void setScore(double score){
        this.score = score;
    }

    /**
     * getter for fileName
     * @return {String}
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Getter for score
     * @return {double}
     */
    public double getScore(){
        return score;
    }

    /**
     * Abstract method calculateScore determines the score on the correctness of the user input or actions
     */
    public abstract void calculateScore();

    /**
     * Abstract method toString(), implemented by simpleQuestion, MultipleChoiceQuestion, and FillBlankQuestion.
     * @return {String}
     */
    @Override
    public abstract String toString();
}
