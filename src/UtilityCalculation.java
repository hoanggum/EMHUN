import java.util.*;

public class UtilityCalculation {

    public static int calculateTransactionUtility(Transaction transaction) {
        int totalUtility = 0;
        for (int utility : transaction.utilities) {
            totalUtility += utility;
        }
        return totalUtility;
    }

    public static void calculateAndPrintAllTransactionUtilities(List<Transaction> transactions) {
        for (int i = 0; i < transactions.size(); i++) {
            int tu = calculateTransactionUtility(transactions.get(i));
            System.out.println("Transaction " + (i + 1) + " TU: " + tu);
        }
    }

    public static void calculateRLUForAllItems(List<Transaction> transactions, Set<Integer> rho, Set<Integer> delta, UtilityArray utilityArray) {
        Set<Integer> combinedSet = new HashSet<>(rho);
        combinedSet.addAll(delta);

        for (Integer item : combinedSet) {
            int totalRLU = 0;
            System.out.println("\nCalculating RLU for item: " + item);

            for (Transaction transaction : transactions) {
                if (transaction.items.contains(item)) {
                    System.out.println("  Found item " + item + " in transaction: " + transaction.items);

                    int rlu = calculateRemainingRemainingUtility(transaction, item);  // Gọi hàm tính RLU
                    totalRLU += rlu;

                    System.out.println("  RLU for this transaction: " + rlu + " (cumulative RLU: " + totalRLU + ")");
                }
            }

            utilityArray.setRLU(item, totalRLU);
            System.out.println("Calculated total RLU for item " + item + ": " + totalRLU);
        }
    }

    public static int calculateRemainingRemainingUtility(Transaction transaction, int currentItem) {
        boolean foundCurrentItem = false;
        int rru = 0;
        System.out.print("    Remaining items after " + currentItem + ": ");

        for (int i = 0; i < transaction.items.size(); i++) {
            int item = transaction.items.get(i);
            int utility = transaction.utilities.get(i);

            if (foundCurrentItem && utility > 0) {
                rru += utility;
                System.out.print(item + "(" + utility + ") ");
            }

            if (item == currentItem) {
                foundCurrentItem = true;
                if (utility > 0) {
                    rru += utility;
                    System.out.println("    Adding utility of currentItem " + currentItem + ": " + utility);
                }
            }
        }
        System.out.println();
        return rru;
    }



    public static void calculateRTWUForAllItems(List<Transaction> transactions, Set<Integer> rho, Set<Integer> delta,Set<Integer> eta, UtilityArray utilityArray) {
        Set<Integer> combinedSet = new HashSet<>(rho);
        combinedSet.addAll(delta);
        combinedSet.addAll(eta);
        for (Integer item : combinedSet) {
            int totalRTWU = 0;
            System.out.println("\nCalculating RTWU for item: " + item);

            for (Transaction transaction : transactions) {
                if (transaction.items.contains(item)) {
                    System.out.println("  Found item " + item + " in transaction: " + transaction.items);

                    System.out.print("RTU for this transaction: ");
                    int rtwu = calculateRTUForTransaction(transaction);
                    totalRTWU += rtwu;
                    System.out.println(" = "+ rtwu + " (cumulative RTWU: " + totalRTWU + ")");
                }
            }

            utilityArray.setRTWU(item, totalRTWU);
            System.out.println("Calculated total RTWU for item " + item + ": " + totalRTWU);
        }
    }


    public static int calculateRTUForTransaction(Transaction transaction) {
        int rtwu = 0;
        StringBuilder sb = new StringBuilder();
        for (int utility : transaction.utilities) {
            if (utility > 0) {
                if (sb.length() > 0) {
                    sb.append(" + ");
                }
                sb.append(utility);
                rtwu += utility;
            }
        }
        System.out.print(sb.toString());
        return rtwu;
    }






}
