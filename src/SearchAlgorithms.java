import java.util.*;

public class SearchAlgorithms {

    private UtilityArray utilityArray;
    private Set<Integer> beta;
    private List<Integer> itemList;
    private List<Integer> filteredPrimary;
    private List<Integer> filteredSecondary;

    public SearchAlgorithms(UtilityArray utilityArray) {
        this.utilityArray = utilityArray;
    }

    public void search(List<Integer> eta, Set<Integer> X, List<Transaction> transactions, List<Integer> primary, List<Integer> secondary, int minU) {
        // Neo: Nếu primary rỗng hoặc không còn item nào để mở rộng, thoát khỏi đệ quy
        if (primary.isEmpty()) {
            return;
        }
        utilityArray = new UtilityArray(transactions.size()+1);

        for (Integer item : primary) {

            // Tạo tập hợp item mới với item hiện tại
            beta = new HashSet<>(X);
            beta.add(item);
            itemList = new ArrayList<>(beta);

            System.out.println("Utility of eta : " + eta );
            // Tính utility của beta
            int utilityBeta = calculateUtility(transactions, beta);
            System.out.println("Utility of " + beta + ": " + utilityBeta);

            if (utilityBeta >= minU) {
                System.out.println("U("+item+") = "+ utilityBeta+ " >= "+ minU +"  HUI Found: " + beta);
            }
            else{
                System.out.println( utilityBeta +"is not a HUI. ");
            }
            List<Transaction> projectedDB = projectDatabase(transactions, itemList);
            printProjectedDatabase(projectedDB, item);
            if (utilityBeta > minU) {
                searchN(eta, beta, transactions, minU);
            }

             // In cơ sở dữ liệu sau khi chiếu

            // Quét cơ sở dữ liệu để tính toán RSU cho các item trong danh sách Secondary
            filteredPrimary = new ArrayList<>();
            filteredSecondary = new ArrayList<>();
            UtilityCalculation.calculateRSUForAllItems(projectedDB, secondary, utilityArray);
            for (Integer secItem : secondary) {
                int rsu = utilityArray.getRSU(secItem); // Lấy RSU từ utilityArray
                System.out.println("RSU =" + rsu);
                if (rsu >= minU) {
                    filteredPrimary.add(secItem);
                } else {
                    filteredSecondary.add(secItem);
                }
            }
//            // Đệ quy tìm kiếm với tập item mới
            search(eta, beta, projectedDB, filteredPrimary, filteredSecondary, minU);
        }
    }

    public void searchN(List<Integer> eta, Set<Integer> beta, List<Transaction> transactions, int minU) {
        // Neo: Nếu danh sách eta rỗng, thoát khỏi đệ quy
        if (eta.isEmpty()) {
            return;
        }

        // Duyệt qua tất cả các item trong eta (negative items)
        for (Integer item : eta) {
            // Tạo tập hợp item mới với item hiện tại
            Set<Integer> betaNew = new HashSet<>(beta);
            betaNew.add(item);

            // Tính utility của beta
            int utilityBetaNew = calculateUtility(transactions, betaNew);
            System.out.println("Utility of (negative) " + betaNew + ": " + utilityBetaNew);

            if (utilityBetaNew >= minU) {
                System.out.println("U("+item+") = "+ utilityBetaNew+ " >= "+ minU +"  HUI Found: " + betaNew);

            }
            else{
                System.out.println(utilityBetaNew+ " <" +minU +" so "+ betaNew + " is not a HUI. ");
            }
            List<Integer> itemList = new ArrayList<>(betaNew);
            List<Transaction> projectedDB = projectDatabase(transactions, itemList);
            printProjectedDatabase(projectedDB, item);


            List<Integer> remainingEta = new ArrayList<>(eta);
            remainingEta.remove(item);

            // Gọi đệ quy với danh sách remainingEta
            searchN(remainingEta, betaNew, transactions, minU);

        }
    }

    // Hàm chiếu cơ sở dữ liệu theo item
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

    // Hàm tính utility cho giao dịch dựa trên các utility đã chiếu
    private int calculateTransactionUtility(List<Integer> utilities) {
        return utilities.stream().mapToInt(Integer::intValue).sum();
    }

    // Hàm in cơ sở dữ liệu đã chiếu
    private void printProjectedDatabase(List<Transaction> projectedDB, Integer item) {
        System.out.println("\nProjected Database after item " + item + ":");
        for (Transaction transaction : projectedDB) {
            System.out.println("Items: " + transaction.getItems() + ", Utilities: " + transaction.getUtilities() + ", Transaction Utility: " + transaction.getTransUtility());
        }
        System.out.println("----------------------------------");
    }



    // Hàm tính utility cho tập hợp item
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
            }
        }
        return totalUtility;
    }

    // Hàm tính RSU cho item
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

    // Hàm tính remaining utility cho các item còn lại trong giao dịch
    private int calculateRemainingUtility(Transaction transaction, Integer item) {
        int index = transaction.getItems().indexOf(item);
        return transaction.getUtilities().subList(index + 1, transaction.getUtilities().size()).stream()
                .mapToInt(Integer::intValue).sum();
    }
}
