import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
        DigitalSignature digitalSignature = new DigitalSignature("data/data.txt");
        digitalSignature.run();

        System.out.println("**********************");
        digitalSignature.F(new BigInteger("15412343", 16), new BigInteger("234234", 16));
        System.out.println("**********************");


    }

}
