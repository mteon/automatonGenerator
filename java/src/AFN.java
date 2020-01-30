import java.util.HashSet;
import java.util.Iterator;

public class AFN<S> {
    private HashSet<Letter> alphabet;
    private States<S> setOfStates;
    private States<S> setOfInitialStates;
    private States<S> setOfFinalStates;
    private Transitions<S> transitionRelation;

    public AFN(HashSet<Letter> alphabet, States<S> setOfStates, States<S> setOfInitialStates,
               States<S> setOfFinalStates, Transitions<S> transitionRelation) {
        this.alphabet = alphabet;
        this.setOfStates = setOfStates;
        this.setOfInitialStates = setOfInitialStates;
        this.setOfFinalStates = setOfFinalStates;
        this.transitionRelation = transitionRelation;
    }

    public HashSet<Letter> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(HashSet<Letter> alphabet) {
        this.alphabet = alphabet;
    }

    public States<S> getSetOfStates() {
        return setOfStates;
    }

    public void setSetOfStates(States<S> setOfStates) {
        this.setOfStates = setOfStates;
    }

    public States<S> getSetOfInitialStates() {
        return setOfInitialStates;
    }

    public void setSetOfInitialStates(States<S> setOfInitialStates) {
        this.setOfInitialStates = setOfInitialStates;
    }

    public States<S> getSetOfFinalStates() {
        return setOfFinalStates;
    }

    public void setSetOfFinalStates(States<S> setOfFinalStates) {
        this.setOfFinalStates = setOfFinalStates;
    }

    public Transitions<S> getTransitionRelation() {
        return transitionRelation;
    }

    public void setTransitionRelation(Transitions<S> transitionRelation) {
        this.transitionRelation = transitionRelation;
    }

    public boolean isFinalState (S state) {
        Iterator<S> states = setOfFinalStates.getSetofStates().iterator();
        while (states.hasNext()) {
            if (states.next().equals(state)) return true;
        }
        return false;
    }

    public boolean containsFinalState (States<S> states) {
        Iterator<S> finalStates = states.iterator();
        while (finalStates.hasNext()) {
            if (isFinalState(finalStates.next())) return true;
        }
        return false;
    }

    public boolean Recognize(Word w) {
        States<S> initialStates = this.setOfInitialStates;
        Iterator<Letter> iterator = w.iterator();
        while (iterator.hasNext()) {
            Letter l = iterator.next();
            initialStates = this.transitionRelation.successors(initialStates, l);
        }
        for (S state: initialStates.getSetofStates()) {
            if (setOfFinalStates.getSetofStates().contains(state)) return true;
        }
        return false;
    }

    public boolean EmptyLanguage() {
        if (this.setOfFinalStates.getSetofStates().isEmpty()) return true;
        if (this.setOfInitialStates.getSetofStates().isEmpty()) return true;
        States<S> statesFromInitialToFinal = getSetOfInitialStates();
        while(!containsFinalState(statesFromInitialToFinal)) {
            for (Letter l: getAlphabet()) {
                if (getTransitionRelation().successors(statesFromInitialToFinal, l).getSetofStates().isEmpty())  {
                    return true;
                }
                statesFromInitialToFinal.addAllStates(getTransitionRelation().successors(statesFromInitialToFinal, l));
            }
        }
        return false;
    }

    public boolean isDeterministic () {
        for (S s: getSetOfStates().getSetofStates()) {
            for (Letter l : getAlphabet()) {
                if(getTransitionRelation().successor(s, l).getSetofStates().size() > 1) return false;
            }
        }
        return true;
    }

    public boolean isComplete () {
        for (S s: getSetOfStates().getSetofStates()) {
            for (Letter l : getAlphabet()) {
                if(getTransitionRelation().successor(s, l).getSetofStates().isEmpty()) return false;
            }
        }
        return true;
    }

    public void Complete () {
        if (!isComplete()) {
            S trashbin = (S) new State("trashbin");
            this.setOfStates.addState(trashbin);
            Iterator<S> states = getSetOfStates().iterator();
            while (states.hasNext()) {
                S s = states.next();
                for (Letter l: getAlphabet()) {
                    if (getTransitionRelation().successor(s, l).getSetofStates().isEmpty()) {
                        getTransitionRelation().addTransition(new Transition<>(s, l, trashbin));
                    }
                }
            }
        }
    }

    public States<S> Reachable (){
        States<S> statesReachable = new States<>();
        if (this.getSetOfInitialStates().getSetofStates().isEmpty()) return statesReachable;
        States<S> currentStates = this.getSetOfInitialStates();
        statesReachable.addAllStates(currentStates);

        Iterator<Letter> alphabet = getAlphabet().iterator();
        while(alphabet.hasNext()) {
            Letter currentLetter = alphabet.next();
            States<S> nextCurrentStates = getTransitionRelation().successors(currentStates, currentLetter);
            if (currentStates.equals(nextCurrentStates)) break;
            else {
                for (S s: nextCurrentStates.getSetofStates()) {
                    statesReachable.addState(s);
                }
                currentStates = nextCurrentStates;
            }
        }
        return statesReachable;
    }

    public States<S> Coreachable () {
        return this.Mirror().Reachable();
    }

    public void Trim() {
        States<S> reachableStates = this.Reachable();
        States<S> coreachableStates = this.Coreachable();
        States<S> statesToRemove = new States<>();

        Iterator<S> iterator = getSetOfStates().iterator();
        while(iterator.hasNext()) {
            S currentState = iterator.next();
            if (!reachableStates.contains(currentState) && !coreachableStates.contains(currentState)) {
                statesToRemove.add(currentState);
            }
        }
        while(statesToRemove.getSetofStates().size()!=0) {
            Iterator<S> iteratorRemove = statesToRemove.iterator();
            while (iteratorRemove.hasNext()) {
                S currentState = iterator.next();
                if (!reachableStates.contains(currentState) && !coreachableStates.contains(currentState)) {
                    this.setOfStates.remove(currentState);
                    this.transitionRelation.remove(currentState);
                    statesToRemove.remove(currentState);
                    break;
                }
            }
        }
    }

    public AFN<S> Mirror() {
        Transitions<S> transitions = new Transitions<>();
        for (Transition<S> t: this.getTransitionRelation().getSetofTransitions()) {
            transitions.addTransition(new Transition<>(t.getTarget(), t.getLabel(), t.getSource()));
        }
        return new AFN<>(this.alphabet, this.setOfStates, this.setOfFinalStates, this.setOfInitialStates, transitions);
    }
}
