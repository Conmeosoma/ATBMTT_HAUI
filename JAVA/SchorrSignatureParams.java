import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Lớp chứa các tham số cơ bản cho thuật toán Schnorr Digital Signature
 * Định nghĩa các hằng số và phương thức tạo tham số của hệ thống
 */
public class SchorrSignatureParams {
    // Các tham số cơ bản của hệ thống Schnorr
    private final BigInteger p; // Số nguyên tố lớn p
    private final BigInteger q; // Ước số nguyên tố q của p-1
    private final BigInteger g; // Phần tử sinh của nhóm con cấp q

    public SchorrSignatureParams(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }

    /**
     * Tạo các tham số hệ thống tự động
     * Sử dụng các số nguyên tố tạm thời cho mục đích demo
     */
    public static SchorrSignatureParams generateSystemParameters() {
        SecureRandom random = new SecureRandom();

        // Tạo số nguyên tố q có 256 bit (kích thước bảo mật vừa phải)
        BigInteger q = BigInteger.probablePrime(256, random);

        // Tạo số nguyên tố p sao cho (p-1) % q == 0
        // p = 2*q + 1 (Sophie Germain prime)
        BigInteger p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);

        // Nếu p không phải số nguyên tố, tiếp tục tìm kiếm
        while (!p.isProbablePrime(40)) {
            q = BigInteger.probablePrime(256, random);
            p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);
        }

        // Tìm phần tử sinh g của nhóm con cấp q
        BigInteger g;
        do {
            // Chọn ngẫu nhiên h từ [2, p-2]
            BigInteger h = new BigInteger(p.bitLength(), random);
            while (h.compareTo(BigInteger.TWO) < 0 || h.compareTo(p.subtract(BigInteger.TWO)) > 0) {
                h = new BigInteger(p.bitLength(), random);
            }
            // g = h^((p-1)/q) mod p
            BigInteger exponent = p.subtract(BigInteger.ONE).divide(q);
            g = h.modPow(exponent, p);
        } while (g.equals(BigInteger.ONE)); // Nếu g = 1, thử lại

        return new SchorrSignatureParams(p, q, g);
    }

    // Getters
    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getG() {
        return g;
    }
}
