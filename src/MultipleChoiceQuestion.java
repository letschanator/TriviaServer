import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contains all fields and methods needed for multiple choice questions
 */
public class MultipleChoiceQuestion extends Question{
    /**
     * {String[]} array of multiple choice options
     */
    private String[] choices;

    /**
     * {JButton[]} Buttons for multiple choice options
     */
    private JButton[] choiceButtons;

    /**
     * {String} contains user's button selection
     */
    private String choice;

    /**
     * {String} contains the answer to the question
     */
    private String correctChoice;

    /**
     * MultipleChoiceQuestion constructor, calls super constructor with question prompt specific to multiple choice questions and sets the choices[] field
     * @param questionDescription {String} question being asked
     * @param choices {String[]} multiple choice answer options
     */
    public MultipleChoiceQuestion(String questionDescription, String[] choices, String correctChoice){
        super(questionDescription, "Please select one from the following choices.");
        setChoices(choices);
        setCorrectChoice(correctChoice);
    }

    /**
     * Copy constructor for MultipleChoiceQuestion
     * @param q {MultipleChoiceQuestion}
     */
    public MultipleChoiceQuestion(MultipleChoiceQuestion q){
        super(q.getQuestionDescription(), q.getQuestionPrompt());
        this.setChoices(q.getChoices());
        this.setCorrectChoice(q.getCorrectChoice());
    }

    /**
     * Setter for choices array
     * @param choices {String[]}
     */
    public void setChoices(String[] choices){
        this.choices = choices.clone(); // copy the String array
        choiceButtons = new JButton[choices.length]; // initializes the array of buttons to be the same length as the question options
        ActionListener buttonListener = new ActionListener() { // create action listener for buttons

            /**
             * Specifies actions to take when a button is pressed
             * @param actionEvent {ActionEvent}
             */
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    // get button pressed
                    String action = actionEvent.getActionCommand();
                    for (int i = 0; i < getChoices().length; i++) {
                        if (action.equals(getChoices()[i])) {
                            setChoice(getChoices()[i]); // set user entered choice
                        }
                    }
                }
                catch(NullPointerException exception){
                    addToQuestionOptions(new JTextField("Choices may have not be initialized"));
                }
            }
        };
        for (int i=0; i< choices.length; i++){
            choiceButtons[i] = new JButton(choices[i]); // initialize each button
            choiceButtons[i].addActionListener(buttonListener); // add action listener to each button
            addToQuestionOptions(choiceButtons[i]); // add choices buttons to GUI
        }
    }

    /**
     * Getter for choices array
     * @return {String[]} choices
     */
    public String[] getChoices(){
        return choices;
    }

    /**
     * Setter for Choice
     * @param choice {String}
     */
    public void setChoice(String choice){
        this.choice = choice;
    }

    /**
     * Getter for Choice
     * @return {String}
     */
    public String getChoice(){
        return choice;
    }

    /**
     * Setter for correctChoice
     * @param correctChoice {String}
     */
    public void setCorrectChoice(String correctChoice){
        this.correctChoice = correctChoice;
    }

    /**
     * Getter for correctChoice
     * @return {String}
     */
    public String getCorrectChoice(){
        return correctChoice;
    }

    /**
     * Implements super abstract method and sets the score equal to 1 if the user is correct
     */
    @Override
    public void calculateScore(){
        try {

            if (getChoice().equals(getCorrectChoice())) {
                setScore(1);
            } else {
                setScore(0);
            }
        }catch (NullPointerException e){

        }
    }

    /**
     * Implementation of super class toString method
     * @return {String}
     */
    @Override
    public String toString() {
        // Question prompt and question description added to String
        String multipleChoiceString = String.format("%s\n%s\n", getQuestionPrompt(), getQuestionDescription());

        // multiple choice options added to string
        for(int i=0; i < getChoices().length; i++){
            multipleChoiceString += String.format("\n%s%s", "[ ] ", getChoices()[i]);
        }

        return String.format("%s\n", multipleChoiceString);
    }
}


