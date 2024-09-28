import java.util.List;

public class Transaction {
    List<Integer> items;
    int transactionUtility;
    List<Integer> utilities;
    public Transaction(List<Integer> items, List<Integer> utilities,int transactionUtility) {
        this.items = items;
        this.utilities = utilities;
        this.transactionUtility = transactionUtility;
    }
    public List<Integer> getItems() {
        return items;
    }

    public List<Integer> getUtilities() {
        return utilities;
    }
    public int getTransUtility() {
        return transactionUtility;
    }
    @Override
    public String toString() {
        return "Items: " + items + ", Profit: " + utilities + ", Transaction Utility: " + transactionUtility;
    }
}
