
import java.util.*;

/**
 * classe Transition
 *
 */
public class Transition<S>
{
    private S source;
    private S target;
    private Letter label;

    /**
     * Constructeur d'objets de classe Transition
     */
    public Transition(S source, Letter a, S target)
    {
        this.source = source;
        this.target = target;
        this.label = a;
    }

    // accesseurs des attributs de la classe
    public S getSource()
    { return this.source;}

    public S getTarget()
    { return this.target;}

    public Letter getLabel()
    {return this.label;}

}