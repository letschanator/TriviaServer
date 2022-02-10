import com.sun.source.tree.WhileLoopTree;

import java.util.ArrayList;
import java.util.Random;
import java.util.WeakHashMap;

public class DataSet {

    /**
     * the array list of questions
     */
    public ArrayList<Question> questionList = new ArrayList<Question>(15);

    /**
     * the current random index to pull a question from the list
     */
    private int index;

    /**
     * contains an array list of the indexes already used
     */
    private ArrayList<Integer> indexesUsed;

    /**
     * constructor for a data set
     * initialises a list of 14 questions
     */
    public DataSet(){
        questionList.add( new ShortAnswerQuestion("Who was the first president of the U.S.?", "George Washington"));
        questionList.add( new ShortAnswerQuestion("What does 'He' stand for in the periodic table?", "Helium"));
        questionList.add(new ShortAnswerQuestion("What is the southern most continent?", "Antarctica"));
        questionList.add( new MultipleChoiceQuestion("How long is an Olympic swimming pool?", new String[]{"25 meters", "50 meters", "75 meters", "100 meters"}, "50 meters"));
        questionList.add( new MultipleChoiceQuestion("How many branches of the government are there?", new String[]{"3", "5", "10"}, "3"));
        questionList.add( new MultipleChoiceQuestion("What is cynophobia?", new String[]{"Fear of bananas", "Fear of the color cyan", "Fear of dogs", "Fear of yogurt"}, "Fear of dogs"));
        questionList.add( new FillBlankQuestion("Amelia _____ was first first solo woman to fly solo across the _____ Ocean.", 2, new String[] {"Earhart", "Atlantic"}));
        questionList.add( new FillBlankQuestion("The common name for dried plums is _____", 1, new String[] {"prunes"}));
        questionList.add( new ShortAnswerQuestion("What was the first soft drink in space?", "Coke"));
        questionList.add( new ShortAnswerQuestion("What is the only non-perishable food?", "honey"));
        questionList.add( new MultipleChoiceQuestion("Which country invented ice cream?", new String[] {"Switzerland", "Italy", "Spain", "China"}, "China"));
        questionList.add( new MultipleChoiceQuestion("What was the first toy to be advertised on television?", new String[] {"Magic 8 Ball", "Mr. Potato Head", "Slinky", "Matchbox cars"}, "Mr. Potato Head"));
        questionList.add( new FillBlankQuestion("A baby _____ is termed a 'kid', hint: it's an animal.", 1, new String[] {"goat"}));
        questionList.add( new FillBlankQuestion("The best class ever is: _____", 1, new String[]{"SWD"}));

        questionList.get(6).addToMedia("amelia.jpeg");
        questionList.get(2).addToMedia("1200px-Antarctica_(orthographic_projection).svg.png");

        indexesUsed =  new ArrayList<>();
    }

    /**
     * gets the question at the index generated in get new random
     * @return the question at index {index}
     */
    public Question getRandomQuestion(){
        return questionList.get(index);
    }

    /**
     * makes the index into a new random number in the array list
     */
    public void getNewRandom() {
        Random rand = new Random();
        index = rand.nextInt(14);
        boolean used = true;
        while (used) {
            used = false;
            for (int i : indexesUsed) {
                if (i == index) {
                    used = true;
                }
            }
            if (used) {
                index = rand.nextInt(14);
            }
        }
        indexesUsed.add(index);
    }

}
