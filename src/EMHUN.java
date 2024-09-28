import java.util.*;
import java.io.*;
public class EMHUN {
    private List<Transaction> transactions;
    private int minU;
    Set<Integer> rho = new HashSet<>();
    Set<Integer> delta = new HashSet<>();
    Set<Integer> eta = new HashSet<>();

    Set<Integer> X = new HashSet<>();
    public EMHUN(List<Transaction> transactions, int minU) {
        this.transactions = transactions;
        this.minU = minU;
    }
    public void EMHUN() {
        UtilityArray utilityArray = new UtilityArray(transactions.size());
        System.out.println("\nCalculating Transaction Utility for each transaction:");
        UtilityCalculation.calculateAndPrintAllTransactionUtilities(transactions);


        this.classifyItems(transactions);
        System.out.println("\nAfter classify. We have: ");
        this.printClassification();

        System.out.println("\nCalculating RTWU for all items in (ρ ∪ δ):");
        UtilityCalculation.calculateRTWUForAllItems(transactions, this.rho, this.delta,this.eta,utilityArray);

//        System.out.println("\nCalculating RLU for all items in ρ ∪ δ:");
//        UtilityCalculation.calculateRLUForAllItems(transactions, this.rho, this.delta, utilityArray);

        Set<Integer> combinedSet = new HashSet<>(this.rho);
        combinedSet.addAll(this.delta);

        Set<Integer> secondaryItems = getSecondaryItems(combinedSet, utilityArray, this.minU);

        System.out.println("\nFinal set of Secondary items: " + secondaryItems);

        System.out.println("\nSorted order of items based on Definition 7: ");
        List<Integer> sortedSecondary = sortItems(secondaryItems, utilityArray);
        System.out.println("Sorted Secondary(X): " + sortedSecondary);

        List<Integer> sortedEta = sortItems(this.eta, utilityArray);
        System.out.println("Sorted η: " + sortedEta);

        filterTransactions(transactions, secondaryItems, eta);
        System.out.println("\nTransactions after filtering:");
        printTransactions(transactions);

        sortItemsInTransactions(transactions, utilityArray);
        System.out.println("\nTransactions after sorting item:");
        printTransactions(transactions);
        System.out.println("\nTransactions after sorting strans:");
        sortTransactions();
        printTransactions(transactions);

    }
    public void classifyItems(List<Transaction> database) {
        Map<Integer, Boolean> hasPositive = new HashMap<>();
        Map<Integer, Boolean> hasNegative = new HashMap<>();

        for (Transaction transaction : database) {
            for (int i = 0; i < transaction.items.size(); i++) {
                int item = transaction.items.get(i);
                int utility = transaction.utilities.get(i);

                if (utility > 0) {
                    hasPositive.put(item, true);
                } else if (utility < 0) {
                    hasNegative.put(item, true);
                }
            }
        }

        Set<Integer> allItems = new HashSet<>(hasPositive.keySet());
        allItems.addAll(hasNegative.keySet());

        for (Integer item : allItems) {
            boolean positive = hasPositive.getOrDefault(item, false);
            boolean negative = hasNegative.getOrDefault(item, false);

            if (positive && !negative) {
                rho.add(item);
            } else if (positive && negative) {
                delta.add(item);
            } else if (negative && !positive) {
                eta.add(item);
            }
        }

    }
    public void printClassification() {
        System.out.println("Items with positive utility only (ρ): " + rho);
        System.out.println("Items with both positive and negative utility (δ): " + delta);
        System.out.println("Items with negative utility only (η): " + eta);
    }
    public static Set<Integer> getSecondaryItems(Set<Integer> combinedSet, UtilityArray utilityArray, int minU) {
        Set<Integer> secondary = new HashSet<>();

        for (Integer item : combinedSet) {
            int rlu = utilityArray.getRTWU(item);

            if (rlu >= minU) {
                secondary.add(item);
            }
        }

        System.out.println("Secondary(X) items: " + secondary);
        return secondary;
    }

    private int getTypeOrder(int item) {
        if (rho.contains(item)) return 1;
        if (delta.contains(item)) return 2;
        if (eta.contains(item)) return 3;
        return Integer.MAX_VALUE;
    }
    public List<Integer> sortItems(Set<Integer> items, UtilityArray utilityArray) {
        List<Integer> sortedItems = new ArrayList<>(items);

        sortedItems.sort((item1, item2) -> {
            int typeOrder1 = getTypeOrder(item1);
            int typeOrder2 = getTypeOrder(item2);

            if (typeOrder1 != typeOrder2) {
                return typeOrder1 - typeOrder2;
            } else {
                return utilityArray.getRTWU(item1) - utilityArray.getRTWU(item2);
            }
        });

        return sortedItems;
    }



    public void filterTransactions(List<Transaction> transactions, Set<Integer> secondaryItems, Set<Integer> eta) {
        Set<Integer> combinedSet = new HashSet<>(secondaryItems);
        combinedSet.addAll(eta);

        for (Transaction transaction : transactions) {
            List<Integer> filteredItems = new ArrayList<>();
            List<Integer> filteredUtilities = new ArrayList<>();

            for (int i = 0; i < transaction.items.size(); i++) {
                int item = transaction.items.get(i);
                int utility = transaction.utilities.get(i);

                if (combinedSet.contains(item)) {
                    filteredItems.add(item);
                    filteredUtilities.add(utility);
                }
            }
            transaction.items = filteredItems;
            transaction.utilities = filteredUtilities;
        }
    }

    public void sortItemsInTransactions(List<Transaction> transactions, UtilityArray utilityArray) {
        for (Transaction transaction : transactions) {
            Set<Integer> itemsSet = new HashSet<>(transaction.items);

            Set<Integer> secondaryItems = new HashSet<>();
            Set<Integer> etaItems = new HashSet<>();

            for (int item : itemsSet) {
                if (rho.contains(item) || delta.contains(item)) {
                    secondaryItems.add(item);
                } else if (eta.contains(item)) {
                    etaItems.add(item);
                }
            }

            List<Integer> sortedSecondary = sortItems(secondaryItems, utilityArray);
            List<Integer> sortedEta = sortItems(etaItems, utilityArray);

            List<Integer> combinedSortedItems = new ArrayList<>(sortedSecondary);
            combinedSortedItems.addAll(sortedEta);

            Map<Integer, Integer> itemUtilityMap = new HashMap<>();
            for (int i = 0; i < transaction.items.size(); i++) {
                itemUtilityMap.put(transaction.items.get(i), transaction.utilities.get(i));
            }

            List<Integer> newItems = new ArrayList<>();
            List<Integer> newUtilities = new ArrayList<>();
            for (int item : combinedSortedItems) {
                newItems.add(item);
                newUtilities.add(itemUtilityMap.get(item));
            }

            transaction.items = newItems;
            transaction.utilities = newUtilities;
        }
    }


    public void printTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            System.out.print("\nItems: " + transaction.items + " : ");
            System.out.print("Utilities: " + transaction.utilities);
            System.out.println("\n----------");
        }
    }
    public void sortTransactions() {
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction tx, Transaction ty) {
                return compareTransactions(tx, ty);
            }
        });
    }

    private int compareTransactions(Transaction tx, Transaction ty) {
        int j = tx.items.size() - 1;
        int k = ty.items.size() - 1;
        while (j >= 0 && k >= 0) {
            int itemTx = tx.items.get(j);
            int itemTy = ty.items.get(k);

            if (itemTx == itemTy) {
                j--;
                k--;
            } else {
                return itemTx < itemTy ? -1 : 1;
            }
        }

        if (j < 0 && k >= 0) return 1;

        if (k < 0 && j >= 0) return -1;

        return 0;
    }





}
