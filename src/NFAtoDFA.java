import Utils.FileHandler;

import java.util.*;

/** Converts an Epsilon-NFA to DFA.
 * @author Mohammad ebrahim Adibzadeh
 */
public class NFAtoDFA {
    /** Represents the NFA’s alphabet.
     */
    private List<String> alphabets;
    /** Represents the NFA’s states.
     */
    private List<State> states;
    /** Represents the NFA’s initialState's index.
     */
    private int initialState;
    /** Represents the NFA’s finalStates indexes.
     */
    private Set<Integer> finalState;
    private String epsilon = "λ";
    
    /** Represents the DFA’s states.
     */
    private List<State> dfaStates;
    /** Represents the DFA’s finalStates indexes.
     */
    private Set<Integer> dfaFinalStats;
    /** Represents the trappedState's index for DFA.
     */
    private int trappedStateIndex = -1;
    /** Represents the reachable states for converting NFA to DFA.
     */
    private List<State> reachableStates;
    /** Represents the path for source file of NFA.
     */
    private String sourceFile;
    /** Represents the path for result output file.
     */
    private String desFile;

    public NFAtoDFA(String sourceFilePath, String destinationFilePath) {
        sourceFile = sourceFilePath;
        desFile = destinationFilePath;
    }
    /** parses the source files data.
     */
    public void parseInput() {
        List<String> lines = FileHandler.readLines(sourceFile);
        parseAlphabet(lines.get(0));
        parseStates(lines.get(1));
        setInitialState(lines.get(2));
        setFinalState(lines.get(3));
        for (int i = 4; i < lines.size(); i++) {
            parseTransition(lines.get(i));
        }
    }
    /** parses the alphabet.
     */
    public void parseAlphabet(String line) {
        alphabets = new ArrayList<>();
        alphabets.add(epsilon);
        for (String s : line.trim().split(" "))
            alphabets.add(s);
    }
    /** parses the states.
     */
    public void parseStates(String line) {
        int statesCount = line.trim().split(" ").length;
        states = new ArrayList<>();
        for (String stateName : line.trim().split(" ")) {
            State currState = new State();
            currState.name = stateName;
            states.add(currState);
        }
    }
    /** parses the initial state.
     */
    public void setInitialState(String line) {
        for (State currState : states)
            if (currState.name.equals(line)) {
                initialState = states.indexOf(currState);
                break;
            }
    }
    /** parses the final states.
     */
    public void setFinalState(String line) {
        finalState = new HashSet<>();
        List<String> fStates = Arrays.asList(line.trim().split(" "));
        for (State myState : states) {
            for (int i = 0; i < fStates.size(); i++) {
                if (myState.name.equals(fStates.get(i))) {
                    finalState.add(states.indexOf(myState));
                }
            }
        }
    }
    /** parses the transitions.
     */
    public void parseTransition(String line) {
        String[] split = line.trim().split(" ");
        int target = -1, source = -1;
        for (State currState : states) {
            if (currState.name.equals(split[0]))
                source = states.indexOf(currState);
            if (currState.name.equals(split[2]))
                target = states.indexOf(currState);
            if (target >= 0 && source >= 0)
                break;
        }
        states.get(source).addTargetState(states.get(target), split[1]);
    }
    /** Iterates the connected nodes by epsilon and turns the Epsilon-NFA to a NFA.
     * Updates final states.
     * Removes epsilons at last.
     */
    public void convertEpsilonNFAtoNFA() {
        for (State currState : states) {
            if (currState.edges.containsKey(epsilon))
                currState.processEpsilonTargets(currState.edges.get(epsilon));
            for (String alphabets : currState.edges.keySet()) {
                currState.processNonEpsilonTargets(alphabets);
            }
        }
        setFinalStates();
        removeEpsilons();
    }
    /** Removes every node's epsilon
     */
    private void removeEpsilons() {
        for (State currState : states)
            if (currState.edges.containsKey(epsilon))
                currState.edges.remove(epsilon);
        alphabets.remove(epsilon);
    }
    /** Updates final states of NFA after converting Epsilon-NFA to NFA.
     */
    private void setFinalStates() {
        for (State currState : states) {
            for (int fState : finalState) {
                if (currState.edges.containsKey(epsilon)) {
                    if (currState.edges.get(epsilon).contains(states.get(fState)))
                        finalState.add(states.indexOf(currState));
                } else
                    break;
            }
        }
    }
    /** Converts NFA to DFA
     */
    public void convertToDFA() {
        dfaStates = new ArrayList<>();
        dfaFinalStats = new HashSet<>();

        reachableStates = new ArrayList<>();
        State nfaInitState = new State(states.get(initialState).name);
        reachableStates.add(nfaInitState);

        processNFA();
    }
    /** Iterates over NFA initial node and DFA's newly made nodes till there's no more new nodes.
     */
    private void processNFA() {
        if (reachableStates.size() == 0)
            return;
        else {
            if (reachableStates.get(0).parentStates == null) {
                reachableStates.get(0).parentStates = new HashSet<>();
                reachableStates.get(0).parentStates.add(states.get(indexFinder(reachableStates.get(0).name)));
            }
            processCurrState(reachableStates.get(0));
            processNFA();
        }

    }
    /** Gets the index of the given name of a state in List states.
     * @param name A String containing a state's name.
     * @return index of the given name in stats list.
     */
    private int indexFinder(String name) {
        for (State state : states) {
            if (state.name.equals(name))
                return states.indexOf(state);
        }
        return -1;
    }
    /** Connects the currState to it's neighbour nodes.
     * Creates a trapped state if needed.
     * @param currState the state which needs to be connected to it's neighbours.
     */
    private void processCurrState(State currState) {
        for (String alphabet : alphabets) {
            HashSet<State> tempStates = new HashSet<>();
            for (State state : currState.parentStates) {
                if (finalState.contains(states.indexOf(state)) && !state.name.equals("N")) {
                    dfaFinalStats.add(dfaStates.size());
                }
                if (state.edges.containsKey(alphabet)) {
                    for (State s : state.edges.get(alphabet)) {
                        tempStates.add(s);
                    }
                }
            }
            String name = "";
            List<State> temp = new ArrayList<>(tempStates);
            temp.sort((s2, s1) -> s1.name.compareTo(s2.name));
            tempStates = new HashSet<>(temp);

            for (State s : tempStates) {
                name += s.name;
            }
            if (name.equals("")) {
                if (trappedStateIndex == -1)
                    createTrappedDFAState();
                currState.addTargetState(dfaStates.get(trappedStateIndex), alphabet);
            } else {
                State targetState;
                if (doesStateExist(name)) {
                    targetState = getNFAByName(name);
                } else {
                    targetState = new State(name);
                    targetState.parentStates = tempStates;
                    reachableStates.add(targetState);
                }
                currState.addTargetState(targetState, alphabet);
            }
        }
        dfaStates.add(currState);
        reachableStates.remove(0);
    }
    /** Checks by name if a state already exist's in dfaStates or reachableStates by the currState.
     * @param name the name that needs to be checked.
     * @return result of search.
     */
    private boolean doesStateExist(String name) {
        for (State s : dfaStates) {
            if (s.name.equals(name))
                return true;
        }
        if (reachableStates != null)
            for (State s : reachableStates)
                if (s.name.equals(name))
                    return true;
        return false;
    }
    /** Gets by name if a state already exist's in dfaStates or reachableStates by the currState.
     * @param name the name that the equivalent states needs to be returned.
     * @return result of search.
     */
    private State getNFAByName(String name) {
        for (State s : dfaStates) {
            if (s.name.equals(name))
                return s;
        }
        for (State s : reachableStates)
            if (s.name.equals(name))
                return s;
        return null;
    }
    /** Creates a trapped-state for DFA.
     */
    private void createTrappedDFAState() {
        State newState = new State("N");
        for (String a : alphabets)
            newState.addTargetState(newState, a);
        dfaStates.add(newState);
        trappedStateIndex = dfaStates.size() - 1;
    }

    public void printNFATrans() {
        for (State currState : states) {
            for (String alphabet : currState.edges.keySet()) {
                for (State s : currState.edges.get(alphabet))
                    System.out.println(currState.name + " " + alphabet + " " + s.name);
            }
        }
    }
    /** prints the DFA result in the given output path
     */
    public void printResultInFile() {
        List<String> lines = new ArrayList<>();
        String line = "";

        for (String a : alphabets)
            line += a + " ";
        lines.add(line);

        line = "";
        for (State s : dfaStates)
            line += s.name + " ";
        lines.add(line);

        lines.add(dfaStates.get(initialState).name);

        line="";
        for(int i:dfaFinalStats){
            if(!dfaStates.get(i).name.equals("N"))
            line+=dfaStates.get(i).name+" ";
        }
        lines.add(line);

        for (State currState : dfaStates) {
            for (String alphabet : alphabets) {
                State temp = currState.edges.get(alphabet).iterator().next();
                lines.add(currState.name + " " + alphabet + " " + temp.name);
            }
        }
        FileHandler.writeLines(desFile, lines);
    }
}
