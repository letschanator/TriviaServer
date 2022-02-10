import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contains methods and fields for short answer type questions
 * Extends Question class
 */
public class ShortAnswerQuestion extends Question{

    /**
     * {JTextField} will contain user input
     */
    private final JTextField shortAnswer;

    /**
     * {String} input from the user
     */
    private String userAnswer;

    /**
     * {String} the answer to the question
     */
    private String correctAnswer;

    /**
     * SimpleQuestion constructor, calls super constructor with the question prompt specific to short answer questions
     * @param questionDescription {String} question being asked
     */
    public ShortAnswerQuestion(String questionDescription, String correctAnswer){
        super(questionDescription, "Please answer the following question.");
        setCorrectAnswer(correctAnswer.replaceAll("\\s", "")); // take out spaces

        shortAnswer = new JTextField(30); // initialize JTextField

        ActionListener shortListener = new ActionListener(){ // initialize a new action listener
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setUserAnswer(shortAnswer.getText().replaceAll("\\s", "")); // set and take out spaces
            }
        };

        shortAnswer.addActionListener(shortListener); // add the action listener to the textfield
        addToQuestionOptions(shortAnswer); // add to JPanel
    }

    /**
     * Copy constructor for ShortAnswerQuestion
     * @param q
     */
    public ShortAnswerQuestion(ShortAnswerQuestion q){
        this(q.getQuestionDescription(),q.getCorrectAnswer());
    }

    /**
     * Setter for correctAnswer
     * @param correctAnswer {String} correct answer to question
     */
    public void setCorrectAnswer(String correctAnswer){this.correctAnswer = correctAnswer;}

    /**
     * Getter for correctAnswer
     * @return {String}
     */
    public String getCorrectAnswer(){return correctAnswer;}

    /**
     * Setter for user answer
     * @param userAnswer {String}
     */
    public void setUserAnswer(String userAnswer){this.userAnswer = userAnswer;}

    /**
     * Getter for user answer
     * @return {String}
     */
    public String getUserAnswer(){return userAnswer;}

    /**
     * If user's input matches the correct answer, the score for the question is 1
     * Implements super (Question) abstract method
     */
    @Override
    public void calculateScore(){
        try {
            if (getUserAnswer().equalsIgnoreCase(getCorrectAnswer())) {
                setScore(1);
            }
        }
        catch(NullPointerException exception){
            addToQuestionOptions(new JTextField("Correct answer may have not been initialized"));
        }
    }

    /**
     * Implementation of super toString method
     * @return {String}
     */
    @Override
    public String toString() {
        return String.format("%s\n%s\n", getQuestionPrompt(), getQuestionDescription());
    }
}
