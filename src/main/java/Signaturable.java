import java.math.BigInteger;

public interface Signaturable {

    BigInteger[] sign(BigInteger H, BigInteger U, BigInteger Z);
//    boolean isCorrectSign()

}
