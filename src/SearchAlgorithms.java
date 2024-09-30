import java.util.*;

public class SearchAlgorithms {

    private UtilityArray utilityArray;
    private Set<Integer> beta;
    private List<Integer> itemList;
    private List<Integer> filteredPrimary;
    private List<Integer> filteredSecondary;

    private List<HighUtilityItemset> highUtilityItemsets = new ArrayList<>();

    public SearchAlgorithms(UtilityArray utilityArray) {
        this.utilityArray = utilityArray;
    }

    public void search(List<Integer> eta, Set<Integer> X, List<Transaction> transactions, List<Integer> primary, List<Integer> secondary, int minU) {
        if (primary.isEmpty()) {
            return;
        }
        utilityArray = new UtilityArray(transactions.size()+1);

        for (Integer item : primary) {

            beta = new HashSet<>(X);
            System.out.println("beta"+ beta);
            beta.add(item);
            itemList = new ArrayList<>(beta);

            int utilityBeta = calculateUtility(transactions, beta);
            System.out.println("Utility of " + beta + ": " + utilityBeta);

            List<Transaction> projectedDB = projectDatabase(transactions, itemList);
            printProjectedDatabase(projectedDB, item);

            if (utilityBeta >= minU) {
                System.out.println("U("+item+") = "+ utilityBeta+ " >= "+ minU +"  HUI Found: " + beta);
                highUtilityItemsets.add(new HighUtilityItemset(beta, utilityBeta));
            }
            else{
                System.out.println( utilityBeta + " < " + minU + " so "+ item +" is not a HUI. ");
            }


            if (utilityBeta > minU) {
                searchN(eta, beta, transactions, minU);
            }

            filteredPrimary = new ArrayList<>();
            filteredSecondary = new ArrayList<>();
            UtilityCalculation.calculateRSUForAllItem(transactions,itemList, secondary, utilityArray);
            System.out.println("\n------------------------------------");

            UtilityCalculation.calculateRLUForAllItem(transactions,itemList, secondary, utilityArray);
            for (Integer secItem : secondary) {
                int rsu = utilityArray.getRSU(secItem);
                int rlu = utilityArray.getRLU(secItem);

                if (rsu >= minU) {
                    filteredPrimary.add(secItem);
                }
                if (rlu >= minU) {
                    filteredSecondary.add(secItem);
                }
            }
            System.out.println("Primary"+ itemList + " = " +filteredPrimary);
            System.out.println("Secondary"+ itemList + " = " +filteredSecondary);
            processSecondary(filteredSecondary, itemList, transactions, minU);
            search(eta, beta, projectedDB, filteredPrimary, filteredSecondary, minU);
        }
    }
    private void processSecondary(List<Integer> secondary, List<Integer> beta, List<Transaction> transactions, int minU) {
        for (int i = 0; i < secondary.size(); i++) {
            Integer secItem = secondary.get(i);
            Set<Integer> betaNew = new HashSet<>(beta);
            betaNew.add(secItem);

            int utilityBetaNew = calculateUtility(transactions, betaNew);
            System.out.println("Utility of combination " + betaNew + ": " + utilityBetaNew);

            if (utilityBetaNew >= minU) {
                System.out.println("U(" + secItem + ") = " + utilityBetaNew + " >= " + minU + "  HUI Found: " + betaNew);
                highUtilityItemsets.add(new HighUtilityItemset(betaNew, utilityBetaNew));
            } else {
                System.out.println(utilityBetaNew + " < " + minU + " so " + secItem + " is not a HUI.");
            }

            for (int j = i + 1; j < secondary.size(); j++) {
                Integer nextSecItem = secondary.get(j);
                Set<Integer> betaExtended = new HashSet<>(betaNew);
                betaExtended.add(nextSecItem);

                int utilityBetaExtended = calculateUtility(transactions, betaExtended);
                System.out.println("Utility of extended combination " + betaExtended + ": " + utilityBetaExtended);

                if (utilityBetaExtended >= minU) {
                    System.out.println("U(" + nextSecItem + ") = " + utilityBetaExtended + " >= " + minU + "  HUI Found: " + betaExtended);
                    highUtilityItemsets.add(new HighUtilityItemset(betaExtended, utilityBetaExtended));
                } else {
                    System.out.println(utilityBetaExtended + " < " + minU + " so " + nextSecItem + " is not a HUI.");
                }
            }
        }
    }
    public void searchN(List<Integer> eta, Set<Integer> beta, List<Transaction> transactions, int minU) {
        if (eta.isEmpty()) {
            return;
        }

        for (Integer item : eta) {
            Set<Integer> betaNew = new HashSet<>(beta);
            betaNew.add(item);

            List<Integer> itemList = new ArrayList<>(betaNew);
            List<Transaction> projectedDB = projectDatabase(transactions, itemList);
            printProjectedDatabase(projectedDB, item);

            int utilityBetaNew = calculateUtility(transactions, betaNew);
            System.out.println("Utility of (negative) " + betaNew + ": " + utilityBetaNew);

            if (utilityBetaNew >= minU) {
                System.out.println("U("+item+") = "+ utilityBetaNew+ " >= "+ minU +"  HUI Found: " + betaNew);
                highUtilityItemsets.add(new HighUtilityItemset(betaNew, utilityBetaNew));
            }
            else {
                System.out.println(utilityBetaNew + " <" + minU + " so " + betaNew + " is not a HUI. ");
            }

            filteredPrimary = new ArrayList<>();
            UtilityCalculation.calculateRSUForAllItem(transactions,itemList, eta, utilityArray);
            for (Integer secItem : eta) {
                int rsu = utilityArray.getRSU(secItem);
                System.out.println("RSU = " + rsu);
                if (rsu >= minU) {
                    filteredPrimary.add(secItem);
                }
            }


            List<Integer> remainingEta = new ArrayList<>(eta);
            remainingEta.remove(item);

            searchN(remainingEta, betaNew, transactions, minU);

        }
        System.out.println("\n---------------------------------");

    }

    private List<Transaction> projectDatabase(List<Transaction> transactions,  List<Integer> items) {
        List<Transaction> projectedDB = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getItems().containsAll(items)) {
                List<Integer> projectedItems = new ArrayList<>();
                List<Integer> projectedUtilities = new ArrayList<>();
                int lastItemIndex = -1;
                for (Integer item : items) {
                    int itemIndex = transaction.getItems().indexOf(item);
                    if (itemIndex > lastItemIndex) {
                        lastItemIndex = itemIndex;
                    }
                }
                // Chỉ giữ lại các item và utility sau khi xuất hiện của item hiện tại
                for (int i = lastItemIndex + 1; i < transaction.getItems().size(); i++) {
                    projectedItems.add(transaction.getItems().get(i));
                    projectedUtilities.add(transaction.getUtilities().get(i));
                }

                // Nếu có item sau item cuối cùng trong danh sách items, thêm transaction đã chiếu vào danh sách kết quả
                if (!projectedItems.isEmpty()) {
                    projectedDB.add(new Transaction(projectedItems, projectedUtilities, calculateTransactionUtility(projectedUtilities)));
                }
            }
        }
        return projectedDB;
    }

    private int calculateTransactionUtility(List<Integer> utilities) {
        return utilities.stream().mapToInt(Integer::intValue).sum();
    }

    private void printProjectedDatabase(List<Transaction> projectedDB, Integer item) {
        System.out.println("\nProjected Database after item " + item + ":");
        for (Transaction transaction : projectedDB) {
            System.out.println("Items: " + transaction.getItems() + ", Utilities: " + transaction.getUtilities() + ", Transaction Utility: " + transaction.getTransUtility());
        }
        System.out.println("----------------------------------");
    }



    private int calculateUtility(List<Transaction> transactions, Set<Integer> itemset) {
        int totalUtility = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getItems().containsAll(itemset)) {
                int itemsetUtility = 0;
                for (Integer item : itemset) {
                    int index = transaction.getItems().indexOf(item);
                    if (index != -1) {
                        itemsetUtility += transaction.getUtilities().get(index);
                    }
                }
                totalUtility += itemsetUtility;
                System.out.println("Utility of " + itemset + " in transaction: " + itemsetUtility);
            } else {
                System.out.println("Transaction does not contain all items in itemset: " + itemset);
            }
        }
        return totalUtility;
    }

    private int calculateRSU(List<Transaction> transactions, Set<Integer> itemset, Integer item) {
        int totalRSU = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getItems().containsAll(itemset)) {
                int remainingUtility = calculateRemainingUtility(transaction, item);
                totalRSU += remainingUtility;
            }
        }
        return totalRSU;
    }

    private int calculateRemainingUtility(Transaction transaction, Integer item) {
        int index = transaction.getItems().indexOf(item);
        return transaction.getUtilities().subList(index + 1, transaction.getUtilities().size()).stream()
                .mapToInt(Integer::intValue).sum();
    }

    public List<HighUtilityItemset> getHighUtilityItemsets() {
        return highUtilityItemsets;
    }
}
