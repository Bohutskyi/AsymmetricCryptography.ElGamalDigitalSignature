import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

class DigitalSignature implements IterationFunction, Encryptionable {

    private static final BigInteger p = new BigInteger("AF5228967057FE1CB84B92511BE89A47", 16);
    private static final BigInteger q = new BigInteger("57A9144B382BFF0E5C25C9288DF44D23", 16);
    private static final BigInteger a = new BigInteger("9E93A4096E5416CED0242228014B67B5", 16);
    private static final BigInteger CONST = new BigInteger("FFFFFFFF", 16);
    private static final int[] table = {
            0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
            0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
            0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
            0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
            0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
            0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
            0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
            0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
            0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
            0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
            0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
            0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
            0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
            0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
            0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
            0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16};

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

    public BigInteger iterationFunction(BigInteger M, BigInteger H) {
        return encrypt(M.xor(H), H).xor(M).xor(H);
    }

    public BigInteger encrypt(BigInteger M, BigInteger key) {
        StringBuilder s = new StringBuilder(key.toString(2));
        while (s.length() < 64) {
            s.insert(0 ,0);
        }
        BigInteger[] k = new BigInteger[4];
        k[0] = new BigInteger(s.substring(0, 32), 2);
        k[1] = new BigInteger(s.substring(32, 64), 2);
        k[2] = CONST.subtract(k[0]);
        k[3] = CONST.subtract(k[1]);

        BigInteger L0 = new BigInteger(M.toString(2).substring(0, 32), 2);
        BigInteger R0 = new BigInteger(M.toString(2).substring(32, 64), 2);
        BigInteger L;

        for (int i = 0; i < 4; ++i) {
            L = new BigInteger(L0.toString());
            L0 = F(k[i], R0).xor(L);
            R0 = L;
        }

        s = new StringBuilder(R0.toString(2));
        s.append(L0.toString(2));
        return new BigInteger(s.toString(), 2);
    }

    private BigInteger F(BigInteger K, BigInteger R) {
        BigInteger X = K.xor(R);
        StringBuilder temp = new StringBuilder(X.toString(2));
        while (temp.length() < 32) {
            temp.insert(0 ,0);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; ++i) {
            result.append(Integer.toHexString(table[Integer.parseInt(temp.substring(i * 8, (i + 1) * 8), 2)]));
        }
        BigInteger t = new BigInteger(result.toString(), 16);
        result = new StringBuilder(t.toString(2));
        while (result.length() < 32) {
            result.insert(0 ,0);
        }
        return new BigInteger(result.substring(13, result.length()) + result.substring(0, 13), 2);

//        int s = Integer.parseInt(result.toString(), 16);
        /*
        System.out.println(s.substring(13, s.length()) + s.substring(0, 13));*/
//        s = Integer.rotateLeft(s, 13);
//        return new BigInteger(Integer.toString(s, 16), 16);
    }

    private BigInteger hash(String M) {
        StringBuilder temp = new StringBuilder(M);
        temp.append(1);
        while (temp.length() % 64 != 0) {
            temp.append(0);
        }

        BigInteger H = BigInteger.ZERO;
        int size = temp.length() / 64;
        for (int i = 0; i < size; ++i) {
            H = iterationFunction(new BigInteger(temp.substring(i * 64, 64 * (i + 1)), 2), H);
        }
        return H;
    }

    void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            BigInteger M = new BigInteger(reader.readLine(), 16);
            reader.close();
            M = hash(M.toString(2));
            System.out.println(M.toString(16));

//            System.out.println("**********************");
//            F(new BigInteger("15412343", 16), new BigInteger("234234", 16));
//            System.out.println("**********************");



            PrintWriter writer = new PrintWriter("data/result.txt");
            writer.write(inputFileName + '\n');
            writer.write("H = " +'\n');
            writer.write("Y = " + y.toString(16) +'\n');
            writer.write("K = " + '\n');
            writer.write("S = " + '\n');
            writer.close();
        } catch (Exception e) {
            System.out.println("Exception");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
