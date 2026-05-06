import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Triển khai thuật toán Chữ ký số Schnorr
 * 
 * Thuật toán Schnorr là một sơ đồ chữ ký số dựa trên Bài toán Logarit Rời Rạc
 * (DLP)
 * Có 3 giai đoạn chính:
 * 1. Key Generation: Phát sinh cặp khóa (khóa bí mật, khóa công khai)
 * 2. Signing: Tạo chữ ký cho một thông điệp
 * 3. Verification: Kiểm tra tính hợp lệ của chữ ký
 */
public class SchorrSignatureAlgorithm {
    private SchorrKeyPair keyPair;
    private final SecureRandom random;

    public SchorrSignatureAlgorithm() {
        this.random = new SecureRandom();
    }

    /**
     * GIAI ĐOẠN 1: Phát sinh khóa (Key Generation)
     * 
     * Quá trình:
     * 1. Nhận các tham số hệ thống (p, q, g)
     * 2. Chọn khóa bí mật x ngẫu nhiên từ [1, q-1]
     * 3. Tính khóa công khai y = g^x mod p
     * 
     * @return SchorrKeyPair chứa cặp khóa công khai/bí mật
     */
    public SchorrKeyPair generateKeys() {
        // Phát sinh các tham số hệ thống
        SchorrSignatureParams params = SchorrSignatureParams.generateSystemParameters();

        // Bước 1: Chọn khóa bí mật x ngẫu nhiên từ [1, q-1]
        BigInteger q = params.getQ();
        BigInteger x = new BigInteger(q.bitLength(), random);
        while (x.compareTo(BigInteger.ONE) < 0 || x.compareTo(q) >= 0) {
            x = new BigInteger(q.bitLength(), random);
        }

        // Bước 2: Tính khóa công khai y = g^x mod p
        BigInteger g = params.getG();
        BigInteger p = params.getP();
        BigInteger y = g.modPow(x, p);

        // Lưu cặp khóa
        this.keyPair = new SchorrKeyPair(x, y, params);
        return keyPair;
    }

    /**
     * GIAI ĐOẠN 2: Tạo chữ ký (Signing)
     * 
     * Quá trình ký với thông điệp M:
     * 1. Chọn số ngẫu nhiên k từ [1, q-1]
     * 2. Tính r = g^k mod p
     * 3. Tính e = H(M || r) sử dụng SHA-256
     * 4. Tính s = k + e*x mod q
     * 5. Trả về chữ ký (s, e)
     * 
     * @param message Thông điệp cần ký
     * @return SchorrSignature chứa cặp (s, e)
     * @throws Exception nếu lỗi xảy ra trong quá trình ký
     */
    public SchorrSignature sign(String message) throws Exception {
        if (keyPair == null) {
            throw new IllegalStateException("Chưa phát sinh khóa. Vui lòng gọi generateKeys() trước.");
        }

        SchorrSignatureParams params = keyPair.getParams();
        BigInteger x = keyPair.getPrivateKey(); // Khóa bí mật
        BigInteger p = params.getP();
        BigInteger q = params.getQ();
        BigInteger g = params.getG();

        // Bước 1: Chọn số ngẫu nhiên k từ [1, q-1]
        BigInteger k = new BigInteger(q.bitLength(), random);
        while (k.compareTo(BigInteger.ONE) < 0 || k.compareTo(q) >= 0) {
            k = new BigInteger(q.bitLength(), random);
        }

        // Bước 2: Tính r = g^k mod p
        BigInteger r = g.modPow(k, p);

        // Bước 3: Tính e = H(M || r) sử dụng SHA-256
        // Nối thông điệp M với giá trị r
        String messageWithR = message + r.toString(16);
        BigInteger e = hashSHA256(messageWithR, q);

        // Bước 4: Tính s = k + e*x mod q
        // s = (k + e*x) mod q
        BigInteger s = k.add(e.multiply(x)).mod(q);

        // Bước 5: Trả về chữ ký (s, e)
        return new SchorrSignature(s, e);
    }

    /**
     * GIAI ĐOẠN 3: Kiểm tra chữ ký (Verification)
     * 
     * Quá trình xác minh chữ ký (s, e) cho thông điệp M:
     * 1. Tính r' = g^s * y^(-e) mod p
     * Hoặc tương đương: r' = g^s * y^(q-1-e) mod p
     * 2. Tính e' = H(M || r')
     * 3. Kiểm tra nếu e' == e thì chữ ký hợp lệ
     * 
     * Lý thuyết: Nếu chữ ký hợp lệ thì:
     * g^s * y^(-e) = g^(k + e*x) * (g^x)^(-e) = g^k = r
     * 
     * @param message   Thông điệp gốc
     * @param signature Chữ ký cần xác minh
     * @return true nếu chữ ký hợp lệ, false nếu không
     * @throws Exception nếu lỗi xảy ra
     */
    public boolean verify(String message, SchorrSignature signature) throws Exception {
        if (keyPair == null) {
            throw new IllegalStateException("Chưa phát sinh khóa. Vui lòng gọi generateKeys() trước.");
        }

        SchorrSignatureParams params = keyPair.getParams();
        BigInteger y = keyPair.getPublicKey(); // Khóa công khai
        BigInteger p = params.getP();
        BigInteger q = params.getQ();
        BigInteger g = params.getG();
        BigInteger s = signature.getS();
        BigInteger e = signature.getE();

        // Bước 1: Tính r' = g^s * y^(-e) mod p
        // y^(-e) = y^(q-e) vì y^q = 1 (theo định lý Fermat)
        // Do đó: y^(-e) = y^(q-e)

        // Tính g^s mod p
        BigInteger gs = g.modPow(s, p);

        // Tính y^(-e) mod p, tức là y^(q - e) mod p
        // Điều này hoạt động vì y^q ≡ 1 (mod p) theo định lý Fermat
        BigInteger negE = e.negate().mod(q); // -e mod q
        BigInteger yInvE = y.modPow(negE, p);

        // Tính r' = g^s * y^(-e) mod p
        BigInteger rPrime = gs.multiply(yInvE).mod(p);

        // Bước 2: Tính e' = H(M || r')
        String messageWithRPrime = message + rPrime.toString(16);
        BigInteger ePrime = hashSHA256(messageWithRPrime, q);

        // Bước 3: Kiểm tra e' == e
        return ePrime.equals(e);
    }

    /**
     * Hàm băm SHA-256 để tính hash của thông điệp
     * Kết quả được chuyển đổi thành BigInteger modulo q
     * 
     * @param input Chuỗi cần băm
     * @param q     Modulo để giảm kích thước hash
     * @return BigInteger đại diện cho hash
     * @throws NoSuchAlgorithmException nếu SHA-256 không khả dụng
     */
    private BigInteger hashSHA256(String input, BigInteger q) throws NoSuchAlgorithmException {
        // Tạo đối tượng MessageDigest cho SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Tính hash của input
        byte[] hashBytes = digest.digest(input.getBytes());

        // Chuyển byte[] thành BigInteger
        BigInteger hashValue = new BigInteger(1, hashBytes);

        // Lấy modulo q để đảm bảo e nằm trong [0, q-1]
        return hashValue.mod(q);
    }

    // Getters
    public SchorrKeyPair getKeyPair() {
        return keyPair;
    }

    public boolean hasKeyPair() {
        return keyPair != null;
    }
}
