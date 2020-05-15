import java.util.*;

public class State{
    public String name;

    /** Connection between current state to neighbour nodes by a certain alphabet
     */
    public HashMap<String, Set<State>> edges=new HashMap<>();
    /** States that are used for creating a new DFA node.
     */
    public Set<State> parentStates;
    public State(){}
    public State(String name){
        this.name = name;
    }
    private static String epsilon= "λ";
    /** Adds a neighbour node by an alphabet to current state
     * @param targetState target neighbour node
     * @param  alphabet the alphabet that connects current state to the neighbour.
     */
    public void addTargetState(State targetState, String alphabet){
        if(edges.containsKey(alphabet)){
            if(!edges.containsValue(targetState))
                edges.get(alphabet).add(targetState);
        }else{
            Set<State> alphabetStates = new HashSet<>();
            alphabetStates.add(targetState);
            edges.put(alphabet,alphabetStates);
        }
    }
    /** Adds a neighbour node by an alphabet to current state
     * @param targetStates the list of target neighbours nodes
     * @param  alphabet the alphabet that connects current state to the neighbours.
     */
    public void addTargetState(Set<State>targetStates, String alphabet) {
        if(edges.containsKey(alphabet)){
            for(State s:targetStates){
                if(!edges.containsValue(s))
                    edges.get(alphabet).add(s);
            }
        }else{
            edges.put(alphabet, new HashSet<>());
            for(State s:targetStates){
                edges.get(alphabet).add(s);
            }
        }
    }
    /** Iterates over current state's neighbour's that are connected to current state by Epsilon
     * till there's no more node connected by epsilon.
     * @param targetStates target neighbour node
     */
    public void processEpsilonTargets(Set<State>targetStates){
        for(State targetState:targetStates){
            if(targetState.edges.containsKey(epsilon)){
                targetState.processEpsilonTargets(targetState.edges.get(epsilon));
            }
            for(String alphabet:targetState.edges.keySet()){
                addTargetState(targetState.edges.get(alphabet),alphabet);
            }
        }
    }
    /** Iterates over current state's neighbour's that are connected to current state by alphabet
     * and checks neighbours connections to other neighbours by Epsilon
     * @param alphabet the alphabet that connects current state to the neighbour
     */
    public void processNonEpsilonTargets(String alphabet){
        Set<State>targetStates = edges.get(alphabet);
        Iterator<State> it = targetStates.iterator();
        while (it.hasNext()){
            State targetState = it.next();
            if(targetState.edges.containsKey(epsilon)){
                addTargetState(targetState.edges.get(epsilon),alphabet);
            }
        }
    }
}
