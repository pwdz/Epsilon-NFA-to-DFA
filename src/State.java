import java.util.*;

public class State{
    public String name;
    public HashMap<String, Set<State>> edges=new HashMap<>();
    public Set<State> parentStates;
    public State(){}
    public State(String name){
        this.name = name;
    }
    private static String epsilon= "λ";
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
