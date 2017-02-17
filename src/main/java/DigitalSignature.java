import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

class DigitalSignature {

    private static final BigInteger p = new BigInteger("AF5228967057FE1CB84B92511BE89A47", 16);
    private static final BigInteger q = new BigInteger("57A9144B382BFF0E5C25C9288DF44D23", 16);
    private static final BigInteger a = new BigInteger("9E93A4096E5416CED0242228014B67B5", 16);

    private String inputFileName;
    private BigInteger x, y;

    DigitalSignature(String inputFileName) {
        this.inputFileName = inputFileName;
        Random random = new Random();
        do {
            x = new BigInteger(128, random);
        } while (x.compareTo(p) == 1);
        y = a.modPow(x, p);
    }

    void run() {
        try {


            PrintWriter writer = new PrintWriter("data/result.txt");
            writer.write(inputFileName + '\n');
            writer.write("H = " + '\n');
            writer.write("Y = " + y.toString(16).toUpperCase() +'\n');
            writer.write("K = " + '\n');
            writer.write("S = " + '\n');
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
