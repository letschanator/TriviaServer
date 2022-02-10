import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class for fill in the blank questions, extends class Questionnaire
 * @author India Berry
 */
public class FillBlankQuestion extends Question{

    /**
     * {int} contains the number of blanks within the question description
     */
    private int blanksNum;

    /**
     * {double} contains the score of the question
     */
    private double score=0;

    /**
     * {String[]} contains all of the correct answers for the question
     */
    private String[] correctChoices;

    /**
     * {String[]} contains all of the user's answers for the question
     */
    private String[] userAnswers;

    /**
     * {String[]} contains the question split for the blanks
     */
    private String[] splitQuestionDescription;

    /**
     * {JTextField[]} contains the text fields for each of the blanks
     */
    private JTextField[] blanks;

    /**
     * {JTextField[]} contains the text fields for each of the blanks in the question
     */
    private JTextField[] splitQuestion;

    /**
     * FillBlankQuestion constructor, calls super constructor with question prompt specific to fill in the blank questions, sets number of blanks
     * @param questionDescription {String}
     * @param blanksNum {int}
     */
    public FillBlankQuestion(String questionDescription, int blanksNum, String[] correctChoices){
        super(questionDescription, "Please fill in the blanks in the following question.");
        setBlanksNum(blanksNum);
        setSplitQuestionDescription();
        setCorrectChoices(correctChoices);

        blanks = new JTextField[blanksNum];
        splitQuestion = new JTextField[getSplitQuestionDescription().length];

        for(int i=0; i<blanksNum; i++){
            blanks[i] = new JTextField(20);
        }
        for(int i=0; i<getSplitQuestionDescription().length; i++){
            splitQuestion[i] = new JTextField(getSplitQuestionDescription()[i]);
            splitQuestion[i].setEditable(false);
        }

        ActionListener[] blanksListener = new ActionListener[getBlanksNum()];

        for(int i=0; i<getBlanksNum(); i++) {
            blanksListener[i] = new ActionListener(){

                /**
                 * Specifies actions to take when a button is pressed
                 * @param actionEvent {ActionEvent}
                 */
                @Override
                public void actionPerformed (ActionEvent actionEvent){
                    String[] userAnswers = new String[getBlanksNum()];
                    for(int i=0; i<getBlanksNum(); i++){
                        if(actionEvent.getSource().equals(blanks[i])){
                            userAnswers[i] = blanks[i].getText();
                        }
                    }
                    setUserAnswers(userAnswers);
                }
            } ;
        }


        for(int i=0; i<getSplitQuestionDescription().length; i++){
            for(int j=0; j<getBlanksNum(); j++){
                addToQuestionOptions(splitQuestion[i]);
                blanks[j].addActionListener(blanksListener[j]);
                addToQuestionOptions(blanks[j]);
            }
        }
    }

    /**
     * Copy constructor for FillBlankQuestion
     * @param q {FillBlankQuestion}
     */
    public FillBlankQuestion(FillBlankQuestion q){
        this(q.getQuestionDescription(),q.getBlanksNum(),q.getCorrectChoices());
    }

    /**
     * Setter for blanksNum (number of blanks in question description)
     * @param blanksNum {int}
     */
    public void setBlanksNum(int blanksNum) {
        this.blanksNum = blanksNum;
    }

    /**
     * Getter for blanksNum (number of blanks in question description)
     * @return {int} blanksNum
     */
    public int getBlanksNum(){
        return blanksNum;
    }

    /**
     * setter for the correct choices array
     * @param correctChoices {String[]}
     */
    public void setCorrectChoices(String[] correctChoices){
        this.correctChoices = correctChoices.clone();
    }

    /**
     * getter for the correct choices array
     * @return {String[]}
     */
    public String[] getCorrectChoices(){
        return correctChoices;
    }

    /**
     * sets the split question based on where the blanks are
     */
    public void setSplitQuestionDescription(){
        splitQuestionDescription = getQuestionDescription().split("_____"); // 5 underscores
    }

    /**
     * getter for the split question description
     * @return {String[]}
     */
    public String[] getSplitQuestionDescription(){
        return splitQuestionDescription;
    }

    /**
     * setter for the user answers
     * @param userAnswers {String[]}
     */
    public void setUserAnswers(String[] userAnswers){this.userAnswers = userAnswers;}

    /**
     * Getter for the user answers
     * @return {String[]}
     */
    public String[] getUserAnswers(){return userAnswers;}


    /**
     * calculates the score based on how many blanks were correct/the number of blanks
     */
    @Override
    public void calculateScore(){
        double blanksNum = (double)(getBlanksNum());
        try {
            for (int i=0; i < getBlanksNum(); i++){
                if(blanks[i].getText().equalsIgnoreCase(getCorrectChoices()[i])){
                    score += 1/blanksNum;
                }
            }
            setScore(score);
        }
        catch(NullPointerException exception){
            addToQuestionOptions(new JTextField("Choices may have not be initialized"));
        }
    }

    /**
     * Implementation of super class toString method
     * @return {String}
     */
    @Override
    public String toString() {
        // add question prompt and question description to string
        String fillBlankString = String.format("%s\n%s\n", getQuestionPrompt(), getQuestionDescription());

        // add blanks to be filled in to string
        for(int i=1; i<=getBlanksNum(); i++){
            fillBlankString += String.format("\n%d%s", i, ". ____");
        }

        return fillBlankString;
    }
}

