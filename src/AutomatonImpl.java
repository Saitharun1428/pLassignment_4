import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class AutomatonImpl implements Automaton {

    class StateLabelPair {
        int state;
        char label;
        public StateLabelPair(int state_, char label_) { state = state_; label = label_; }

        @Override
        public int hashCode() {
            return Objects.hash((Integer) state, (Character) label);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StateLabelPair o1 = (StateLabelPair) o;
            return (state == o1.state) && (label == o1.label);
        }
    }

    HashSet<Integer> start_states;
    HashSet<Integer> accept_states;
    HashSet<Integer> current_states;
    HashMap<StateLabelPair, HashSet<Integer>> transitions;

    public AutomatonImpl() {
        start_states = new HashSet<Integer>();
        accept_states = new HashSet<Integer>();
        // Initialize current_states here to avoid NullPointerException in reset()
        current_states = new HashSet<Integer>(); 
        transitions = new HashMap<StateLabelPair, HashSet<Integer>>();
    }

    @Override
    public void addState(int s, boolean is_start, boolean is_accept) {
        if (is_start) {
            start_states.add(s);
        }
        if (is_accept) {
            accept_states.add(s);
        }
    }

    @Override
    public void addTransition(int s_initial, char label, int s_final) {
        StateLabelPair key = new StateLabelPair(s_initial, label);
        
        // Find the set of destination states for this key
        HashSet<Integer> dest_states = transitions.get(key);

        if (dest_states == null) {
            // If no transitions exist for this key, create a new set
            dest_states = new HashSet<Integer>();
            transitions.put(key, dest_states);
        }
        
        // Add the final state to the set
        dest_states.add(s_final);
    }

    @Override
    public void reset() {
        // Reset current states to be a copy of the start states
        current_states = new HashSet<>(start_states);
    }

    @Override
    public void apply(char input) {
        HashSet<Integer> next_states = new HashSet<>();

        // Find all possible next states from all current states
        for (int s : current_states) {
            StateLabelPair key = new StateLabelPair(s, input);
            
            if (transitions.containsKey(key)) {
                // If transitions exist, add all destination states
                next_states.addAll(transitions.get(key));
            }
        }
        
        // Update the current states
        current_states = next_states;
    }

    @Override
    public boolean accepts() {
        // Check if any current state is an accept state
        for (int s : current_states) {
            if (accept_states.contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasTransitions(char label) {
        // Check if any current state has an outgoing transition for the label
        for (int s : current_states) {
            StateLabelPair key = new StateLabelPair(s, label);
            if (transitions.containsKey(key)) {
                return true;
            }
        }
        return false;
    }
}