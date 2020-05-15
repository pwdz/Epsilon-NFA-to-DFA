import Utils.FileHandler;

import java.util.*;

public class NFAtoDFA {
    private List<String> alphabets;
    private List<State> states;
    private int initialState;
    private Set<Integer> finalState;
    private String epsilon = "λ";
    private List<State> nfaStates;
    private Set<Integer> nfaFinalStats;
    private int trappedStateIndex = -1;
    private List<State> reachableStates;
    private String sourceFile;
    private String desFile;

    public NFAtoDFA(String sourceFilePath, String destinationFilePath) {
        sourceFile = sourceFilePath;
        desFile = destinationFilePath;
    }

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

    public void parseAlphabet(String line) {
        alphabets = new ArrayList<>();
        alphabets.add(epsilon);
        for (String s : line.trim().split(" "))
            alphabets.add(s);
    }

    public void parseStates(String line) {
        int statesCount = line.trim().split(" ").length;
        states = new ArrayList<>();
        for (String stateName : line.trim().split(" ")) {
            State currState = new State();
            currState.name = stateName;
            states.add(currState);
        }
    }

    public void setInitialState(String line) {
        for (State currState : states)
            if (currState.name.equals(line)) {
                initialState = states.indexOf(currState);
                break;
            }
    }

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

    public void convertEpsilonNFAtoNFA() {
        for (State currState : states) {
            if (currState.edges.containsKey(epsilon))
                currState.processEpsilonTargets(currState.edges.get(epsilon));
            for (String alphabets : currState.edges.keySet()) {
                currState.processNonEpsilonTargets(alphabets);
            }
        }

        removeEpsilons();
    }

    private void removeEpsilons() {
        for (State currState : states)
            if (currState.edges.containsKey(epsilon))
                currState.edges.remove(epsilon);
        alphabets.remove(epsilon);
    }

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

    public void convertToDFA() {
        nfaStates = new ArrayList<>();
        nfaFinalStats = new HashSet<>();

        reachableStates = new ArrayList<>();
        State nfaInitState = new State(states.get(initialState).name);
        reachableStates.add(nfaInitState);

        processCurrStateEdges();
    }

    private void processCurrStateEdges() {
        if (reachableStates.size() == 0)
            return;
        else {
            if (reachableStates.get(0).parentStates == null) {
                reachableStates.get(0).parentStates = new HashSet<>();
                reachableStates.get(0).parentStates.add(states.get(indexFinder(reachableStates.get(0).name)));
            }
            processCurrStateParents(reachableStates.get(0));
            processCurrStateEdges();
        }

    }

    private int indexFinder(String name) {
        for (State state : states) {
            if (state.name.equals(name))
                return states.indexOf(state);
        }
        return -1;
    }

    private void processCurrStateParents(State currState) {
        for (String alphabet : alphabets) {
            HashSet<State> tempStates = new HashSet<>();
            for (State state : currState.parentStates) {
                if (finalState.contains(states.indexOf(state)) && !state.name.equals("N")) {
                    nfaFinalStats.add(nfaStates.size());
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
                    addNewDFAState();
                currState.addTargetState(nfaStates.get(trappedStateIndex), alphabet);
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
        nfaStates.add(currState);
        reachableStates.remove(0);
    }

    private boolean doesStateExist(String name) {
        for (State s : nfaStates) {
            if (s.name.equals(name))
                return true;
        }
        if (reachableStates != null)
            for (State s : reachableStates)
                if (s.name.equals(name))
                    return true;
        return false;
    }

    private State getNFAByName(String name) {
        for (State s : nfaStates) {
            if (s.name.equals(name))
                return s;
        }
        for (State s : reachableStates)
            if (s.name.equals(name))
                return s;
        return null;
    }

    private void addNewDFAState() {
        State newState = new State("N");
        for (String a : alphabets)
            newState.addTargetState(newState, a);
        nfaStates.add(newState);
        trappedStateIndex = nfaStates.size() - 1;
    }

    public void printNFATrans() {
        for (State currState : states) {
            for (String alphabet : currState.edges.keySet()) {
                for (State s : currState.edges.get(alphabet))
                    System.out.println(currState.name + " " + alphabet + " " + s.name);
            }
        }
    }

    public void printResultInFile() {
        List<String> lines = new ArrayList<>();
        String line = "";

        for (String a : alphabets)
            line += a + " ";
        lines.add(line);

        line = "";
        for (State s : nfaStates)
            line += s.name + " ";
        lines.add(line);

        lines.add(nfaStates.get(initialState).name);

        line="";
        for(int i:nfaFinalStats){
            if(!nfaStates.get(i).name.equals("N"))
            line+=nfaStates.get(i).name+" ";
        }
        lines.add(line);

        for (State currState : nfaStates) {
            for (String alphabet : alphabets) {
                State temp = currState.edges.get(alphabet).iterator().next();
                lines.add(currState.name + " " + alphabet + " " + temp.name);
            }
        }
        FileHandler.writeLines(desFile, lines);
    }
}
