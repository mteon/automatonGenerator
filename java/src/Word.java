import java.util.*;

/** classe Word .*/
public class Word{
    private ArrayList<Letter> contain;

    public Word(ArrayList<Letter> contain) {
        this.contain = contain;
    }

    public ArrayList<Letter> getContain(){
        return this.contain;
    }
    Iterator<Letter> iterator(){
        return contain.iterator();
    }
}