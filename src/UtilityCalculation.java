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

    public static void calculateRLUForAllItemsRhoAnDenta(List<Transaction> transactions, Set<Integer> rho, Set<Integer> delta, UtilityArray utilityArray) {
        Set<Integer> combinedSet = new HashSet<>(rho);
        combinedSet.addAll(delta);

        for (Integer item : combinedSet) {
            int totalRLU = 0;
            System.out.println("\nCalculating RLU for item: " + item);

            for (Transaction transaction : transactions) {
                if (transaction.items.contains(item)) {
                    System.out.println("  Found item " + item + " in transaction: " + transaction.items);

                    int rlu = calculateRemainingResiduaUtility(transaction, item);  // Gọi hàm tính RLU
                    totalRLU += rlu;

                    System.out.println("  RLU for this transaction: " + rlu + " (cumulative RLU: " + totalRLU + ")");
                }
            }

            utilityArray.setRLU(item, totalRLU);
            System.out.println("Calculated total RLU for item " + item + ": " + totalRLU);
        }
    }
    public static void calculateRLUForAllItems(List<Transaction> transactions, List<Integer> secondary, UtilityArray utilityArray) {
        for (Integer item : secondary) {
            int totalRLU = 0;
            System.out.println("\nCalculating RLU for item: " + item);

            for (Transaction transaction : transactions) {
                if (transaction.getItems().contains(item)) {
                    int index = transaction.getItems().indexOf(item);
                    int itemUtility = transaction.getUtilities().get(index);

                    int remainingUtility = calculateRemainingUtility(transaction, index + 1); // Start from the next index
                    totalRLU += itemUtility + remainingUtility;

                    System.out.println("  Found item " + item + " in transaction" + transaction.items + " with utility: " + itemUtility + ", Remaining Residual Utility: " + remainingUtility);
                }
            }

            utilityArray.setRLU(item, totalRLU);
            System.out.println("Calculated total RLU for item " + item + ": " + totalRLU);
        }
    }
    public static int calculateRemainingResiduaUtility(Transaction transaction, int currentItem) {
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


    public static void calculateRSUForAllItems(List<Transaction> transactions, List<Integer> secondary, UtilityArray utilityArray) {
        for (Integer item : secondary) {
            int totalRSU = 0;
            System.out.println("\nCalculating RSU for item: " + item);

            for (Transaction transaction : transactions) {
                if (transaction.getItems().contains(item)) {
                    int index = transaction.getItems().indexOf(item);
                    int itemUtility = transaction.getUtilities().get(index);

                    int remainingUtility = calculateRemainingUtility(transaction, index + 1);

                    totalRSU += itemUtility + remainingUtility;

                    System.out.println("  Found item " + item + " in transaction"+ transaction.items + "with utility: " + itemUtility + ", Remaining Residual Utility: " + remainingUtility);
                }
            }

            utilityArray.setRSU(item, totalRSU);
            System.out.println("Calculated total RSU for item " + item + ": " + totalRSU);
        }
    }

    private static int calculateRemainingUtility(Transaction transaction, int startIndex) {
        int remainingUtility = 0;
        List<Integer> items = transaction.getItems();
        List<Integer> utilities = transaction.getUtilities();

        for (int i = startIndex; i < items.size(); i++) {
            if (utilities.get(i) > 0) {
                remainingUtility += utilities.get(i);
            }
        }

        return remainingUtility;
    }

    public static void calculateRSUForAllItem(List<Transaction> transactions, List<Integer> X, List<Integer> secondary, UtilityArray utilityArray) {
        for (Integer item : secondary) {
            int totalRSU = 0;
            System.out.println("\nCalculating RSU for item: " + item);

            for (Transaction transaction : transactions) {
                if (transaction.getItems().containsAll(X) && transaction.getItems().contains(item)) {
                    int utilityX = calculateUtilityForSet(transaction, X);

                    int indexZ = transaction.getItems().indexOf(item);
                    int utilityZ = transaction.getUtilities().get(indexZ);

                    int rru = calculateRemainingUtility(transaction, indexZ + 1);

                    totalRSU += utilityX + utilityZ + rru;

                    System.out.println("  Found set X " + X + " and item " + item + " in transaction " + transaction.getItems() +
                            " with utility of X: " + utilityX + ", utility of z: " + utilityZ +
                            ", Remaining Residual Utility (RRU): " + rru + ", Calculated RSU: " + (utilityX + utilityZ + rru));
                }
            }

            utilityArray.setRSU(item, totalRSU);
            System.out.println("Calculated total RSU for item " + item + ": " + totalRSU);
        }
    }
    public static void calculateRLUForAllItem(List<Transaction> transactions, List<Integer> X, List<Integer> secondary, UtilityArray utilityArray) {
        for (Integer item : secondary) {
            int totalRLU = 0;
            System.out.println("\nCalculating RLU for item: " + item);

            for (Transaction transaction : transactions) {
                if (transaction.getItems().containsAll(X) && transaction.getItems().contains(item)) {
                    int utilityX = calculateUtilityForSet(transaction, X);
                    int maxIndexX = findLocationMaxIndexForSet(transaction, X);
                    int index = transaction.getItems().indexOf(maxIndexX);

                    int remainingUtility = calculateRemainingUtility(transaction, maxIndexX + 1);



                    totalRLU += utilityX + remainingUtility;

                    System.out.println("  Found item " + item + " in transaction " + transaction.getItems() +
                            " with utility of X: " + utilityX + ", Remaining Residual Utility (RRU): " + remainingUtility+", Calculated RSU: " + (utilityX + remainingUtility));
                }
            }

            utilityArray.setRLU(item, totalRLU);
            System.out.println("Calculated total RLU for item " + item + ": " + totalRLU);
        }
    }
    private static int calculateUtilityForSet(Transaction transaction,  List<Integer> X) {
        int totalUtility = 0;
        for (Integer item : X) {
            if (transaction.getItems().contains(item)) {
                int index = transaction.getItems().indexOf(item);
                totalUtility += transaction.getUtilities().get(index);
            }
        }

        return totalUtility;
    }
    private static int findLocationMaxIndexForSet(Transaction transaction, List<Integer> X) {
        int maxIndex = -1;
        for (Integer item : X) {
            int index = transaction.getItems().indexOf(item);
            if (index > maxIndex) {
                maxIndex = index;
            }
        }
        return maxIndex;
    }

}
