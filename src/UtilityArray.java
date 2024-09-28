import java.util.HashMap;
import java.util.Map;

public class UtilityArray {
    private int[] rtwus;
    private int[] rlus;
    private int[] rsus;

    private int size;

    public UtilityArray(int size) {
        this.size = size;
        this.rtwus = new int[size];
        this.rlus = new int[size];
        this.rsus = new int[size];
    }

    public void setRTWU(int index, int value) {
        if (index >= 0 && index < size) {
            rtwus[index] = value;
        }
    }

    public int getRTWU(int index) {
        if (index >= 0 && index < size) {
            return rtwus[index];
        }
        return 0;  // Giá trị mặc định
    }

    public void setRLU(int index, int value) {
        if (index >= 0 && index < size) {
            rlus[index] = value;
        }
    }

    public int getRLU(int index) {
        if (index >= 0 && index < size) {
            return rlus[index];
        }
        return 0;
    }

    public void setRSU(int index, int value) {
        if (index >= 0 && index < size) {
            rsus[index] = value;
        }
    }

    public int getRSU(int index) {
        if (index >= 0 && index < size) {
            return rsus[index];
        }
        return 0;
    }

    public void printUtilityArray() {
        System.out.println("RTWU Array:");
        for (int i = 0; i < size; i++) {
            System.out.print(rtwus[i] + " ");
        }
        System.out.println();

        System.out.println("RLU Array:");
        for (int i = 0; i < size; i++) {
            System.out.print(rlus[i] + " ");
        }
        System.out.println();

        System.out.println("RSU Array:");
        for (int i = 0; i < size; i++) {
            System.out.print(rsus[i] + " ");
        }
        System.out.println();
    }
}
