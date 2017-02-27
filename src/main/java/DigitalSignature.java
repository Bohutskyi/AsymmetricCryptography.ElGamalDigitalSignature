import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Random;

public class DigitalSignature implements Encryptionable, IterationFunction, Signaturable {

    private static final String INSERT = "------------------------------";
    private static final BigInteger p = new BigInteger("00AF5228967057FE1CB84B92511BE89A47", 16);
    private static final BigInteger q = new BigInteger("0057A9144B382BFF0E5C25C9288DF44D23", 16);
    private static final BigInteger a = new BigInteger("009E93A4096E5416CED0242228014B67B5", 16);

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

    private final String inputFileName;
    private final BigInteger x, y;

    DigitalSignature(String inputFileName) {
        this.inputFileName = inputFileName;
        BigInteger temp;
        do {
            temp = new BigInteger(128, new Random());
        } while (p.compareTo(temp) == -1);
        this.x = temp;
        this.y = a.modPow(x, p);
    }

    final void run() {
        try {
            BigInteger H = getHash();
            BigInteger U = new BigInteger(128, new Random());
            BigInteger Z = a.modPow(U, p);
            BigInteger[] s = sign(H, U, Z);
            BigInteger S = a.modPow(s[1], p);

//            System.out.println("H: " + H.toString(16));
//            System.out.println("X: " + x.toString(16));
//            System.out.println("Y: " + y.toString(16));
//            System.out.println("U: " + U.toString(16));
//            System.out.println("Z: " + Z.toString(16));
//            System.out.println("K: " + s[0].toString(16));
//            System.out.println("G: " + s[1].toString(16));
//            System.out.println("S: " + S.toString(16));

            FileWriter writer = new FileWriter(inputFileName + ".sig");
            writer.write(INSERT + "\n");
            writer.write(inputFileName + "\n");
            writer.write("H = " + H.toString(16).toUpperCase() + "\n");
            writer.write("Y = " + y.toString(16).toUpperCase() + "\n");
            writer.write("K = " + s[0].toString(16).toUpperCase() + "\n");
            writer.write("S = " + S.toString(16).toUpperCase() + "\n");
            writer.write(INSERT);
            writer.close();
            writer = new FileWriter(inputFileName + ".sig.add");
            writer.write(INSERT + "\n");
            writer.write(inputFileName + "\n");
            writer.write("U = " + U.toString(16) + "\n");
            writer.write("Z = " + Z.toString(16) + "\n");
            writer.write("G = " + s[1].toString(16) + "\n");
            writer.write(INSERT);
            writer.close();
            System.out.println("Success");
            System.out.println("Sign saved to " + inputFileName + ".sig");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private BigInteger getHash() {
        BigInteger H = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            String buffer = reader.readLine();
            reader.close();

            int count = 0;
            StringBuilder temp = new StringBuilder();
            BigInteger H0 = BigInteger.ZERO;
            if (buffer == null) {
                buffer = "";
            }
            for (int i = 0, n = buffer.length(); i < n; ++i) {
                temp.append(Integer.toHexString((int) buffer.charAt(i)));
                count++;
                if (count == 8) {
                    count = 0;
                    H0 = iterationFunction(new BigInteger(temp.toString(), 16), H0);
                    temp = new StringBuilder();
                }
            }

            temp.append(80);
            while (temp.length() < 16) {
                temp.append("00");
            }
            H0 = iterationFunction(new BigInteger(temp.toString(), 16), H0);
//            System.out.println("FINAL: " + H0.toString(16));

            temp = new StringBuilder(H0.toString(16));
            while (temp.length() < 16) {
                temp.insert(0, 0);
            }
            StringBuilder result = new StringBuilder();
            for (int i = 7; i >= 0; --i) {
                result.append(temp.substring(i * 2, 2 * (i + 1)));
            }
            result.insert(0, "00FFFFFFFFFFFF00");
            H = new BigInteger(result.toString(), 16);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return H;
    }

    static boolean isCorrect(String inputFileName, String signedFileName) {
        BigInteger hash = new DigitalSignature(inputFileName).getHash();
        BigInteger H, Y, K, S, t;
        t = null;
        S = null;
        try {
//            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(signedFileName), "windows-1251"));
            String buffer;
            H = Y = K = S = null;
            while ((buffer = reader.readLine()) != null) {
                String[] temp = buffer.split(" ");
                if (temp[0].equalsIgnoreCase("s")) {
                    S = new BigInteger(temp[2], 16);
                } else if (temp[0].equalsIgnoreCase("k")) {
                    K = new BigInteger(temp[2], 16);
                } else if (temp[0].equalsIgnoreCase("y")) {
                    Y = new BigInteger(temp[2], 16);
                }
                else if (temp[0].equalsIgnoreCase("h")) {
                    H = new BigInteger(temp[2], 16);
                }
            }
            reader.close();
            if (hash.compareTo(H) != 0) {
                return false;
            }
//            H = new DigitalSignature(hashFileName).getHash();
//            System.out.println(hash.toString(16));
            t = Y.modPow(K, p);
            t = t.multiply(S);
            t = t.mod(p);
            t = t.add(K.multiply(H));
            t = a.modPow(t, p);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (t.compareTo(S) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public final BigInteger iterationFunction(BigInteger input, BigInteger H) {
        return encrypt(input.xor(H), H).xor(input).xor(H);
    }


    public final BigInteger encrypt(BigInteger input, BigInteger key) {
        StringBuilder temp;
        BigInteger[] K = new BigInteger[4];

        temp = new StringBuilder(key.toString(16));
        while (temp.length() < 16) {
            temp.insert(0, 0);
        }
        StringBuilder t1 = new StringBuilder(), t2 = new StringBuilder();
        for (int i = 3; i>= 0; --i ) {
            t1.append(temp.substring(i*2, 2*i + 2));
            t2.append(temp.substring(i*2 + 8, 2*i + 10));
        }

        K[0] = new BigInteger(t1.toString(), 16);
        K[1] = new BigInteger(t2.toString(), 16);
        K[2] = CONST.subtract(K[1]);
        K[3] = CONST.subtract(K[0]);

        temp = new StringBuilder(input.toString(16));
        while (temp.length() < 16) {
            temp.insert(0, 0);
        }
        t1 = new StringBuilder();
        t2 = new StringBuilder();
        for (int i = 3; i >= 0; --i) {
            t1.append(temp.substring(i*2, 2*i + 2));
            t2.append(temp.substring(i*2 + 8, 2*i + 10));
        }

        BigInteger L0 = new BigInteger(t1.toString(), 16);
        BigInteger R0 = new BigInteger(t2.toString(), 16);
        BigInteger R;

        for (int i = 0; i < 4; ++i) {
            R = new BigInteger(L0.toString(16), 16);
            L0 = F(K[i], R0).xor(R);
            R0 = R;
        }


        temp = new StringBuilder(R0.toString(16));
        while (temp.length() < 8) {
            temp.insert(0, 0);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 3; i>= 0; --i) {
            result.append(temp.substring(i * 2, (i + 1) * 2));
        }
        temp = new StringBuilder(L0.toString(16));
        while (temp.length() < 8) {
            temp.insert(0, 0);
        }
        for (int i = 3; i>= 0; --i) {
            result.append(temp.substring(i * 2, (i + 1) * 2));
        }

        temp = new StringBuilder(input.toString(16));
        while (temp.length() < 16) {
            temp.insert(0, 0);
        }

        return new BigInteger(result.toString(), 16);
    }

    final BigInteger F(BigInteger K, BigInteger R) {
        BigInteger X = K.xor(R);
        StringBuilder result = new StringBuilder();
        StringBuilder temp = new StringBuilder(X.toString(16));
        while (temp.length() < 8) {
            temp.insert(0, 0);
        }
        for (int i = 3; i >= 0; --i) {
            StringBuilder s = new StringBuilder();
            s.append(Integer.toHexString(table[Integer.parseInt(temp.substring(i * 2, (i + 1) * 2), 16)]));
            while (s.length() < 2) {
                s.insert(0,0);
            }
            result.insert(0, s.toString());
        }
        BigInteger t = new BigInteger(result.toString(), 16);
        result = new StringBuilder(t.toString(2));
        while (result.length() % 32 != 0) {
            result.insert(0, 0);
        }

        t = new BigInteger(result.substring(13, result.length()) + result.substring(0, 13), 2);

        return t;
    }

    public final BigInteger[] sign(BigInteger H, BigInteger U, BigInteger Z) {
        BigInteger[] result = new BigInteger[2];
        BigInteger temp = (x.add(H)).modInverse(q);
        result[0] = (U.subtract(Z)).multiply(temp).mod(q);
        result[1] = ((x.multiply(Z)).add(U.multiply(H))).multiply(temp).mod(q);
        return result;
    }

}
