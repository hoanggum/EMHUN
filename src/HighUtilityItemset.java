import java.util.List;
import java.util.Set;

class HighUtilityItemset {
    private Set<Integer> itemset;
    private int utility;

    public HighUtilityItemset(Set<Integer> itemset, int utility) {
        this.itemset = itemset;
        this.utility = utility;
    }

    public Set<Integer> getItemset() {
        return itemset;
    }

    public int getUtility() {
        return utility;
    }

    @Override
    public String toString() {
        return "Itemset: " + itemset + ", Utility: " + utility;
    }
}
