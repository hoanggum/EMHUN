import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Transaction> readTransactionsFromFile(String fileName) {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length != 3) {
                    System.out.println("Dòng không hợp lệ: " + line);
                    continue;
                }
                String transactionId = parts[0];
                String[] itemsStr = parts[0].trim().split(" ");
                List<Integer> items = new ArrayList<>();
                for (String item : itemsStr) {
                    items.add(Integer.parseInt(item));
                }

                int transUtility = Integer.parseInt(parts[1].trim());
                String[] utilitiesStr = parts[2].trim().split(" ");
                List<Integer> utilities = new ArrayList<>();
                for (String utility : utilitiesStr) {
                    utilities.add(Integer.parseInt(utility));
                }
                Transaction transaction = new Transaction(items, utilities,transUtility);
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }
    public static void main(String[] args) {
        String fileName = "table3.txt";
        List<Transaction> transactions = readTransactionsFromFile(fileName);
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
        int minUtility = 25;
        EMHUN emhun = new EMHUN(transactions, minUtility);

        emhun.EMHUN();

    }
}