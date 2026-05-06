import java.math.BigInteger;

/**
 * Lớp đại diện cho một chữ ký Schnorr
 * Chữ ký bao gồm hai thành phần: s và e
 * 
 * s: phần tử bí mật của chữ ký
 * e: kết quả hàm băm H(M || r)
 */
public class SchorrSignature {
    private final BigInteger s; // s component của chữ ký
    private final BigInteger e; // e component của chữ ký (hàm băm)

    public SchorrSignature(BigInteger s, BigInteger e) {
        this.s = s;
        this.e = e;
    }

    public BigInteger getS() {
        return s;
    }

    public BigInteger getE() {
        return e;
    }

    @Override
    public String toString() {
        // Hiển thị dạng hex rút gọn
        String sHex = s.toString(16);
        String eHex = e.toString(16);

        int maxLen = 64;
        if (sHex.length() > maxLen) {
            sHex = sHex.substring(0, maxLen) + "...";
        }
        if (eHex.length() > maxLen) {
            eHex = eHex.substring(0, maxLen) + "...";
        }

        return "SchorrSignature{\n" +
                "  s=" + sHex + "\n" +
                "  e=" + eHex + "\n" +
                "}";
    }
}
