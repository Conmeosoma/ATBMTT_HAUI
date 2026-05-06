import java.math.BigInteger;

/**
 * Lớp đại diện cho cặp khóa công khai/bí mật của Schnorr
 */
public class SchorrKeyPair {
    private final BigInteger privateKey; // Khóa bí mật (x)
    private final BigInteger publicKey; // Khóa công khai (y = g^x mod p)
    private final SchorrSignatureParams params; // Tham số hệ thống

    public SchorrKeyPair(BigInteger privateKey, BigInteger publicKey, SchorrSignatureParams params) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.params = params;
    }

    // Getters
    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public SchorrSignatureParams getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "SchorrKeyPair{" +
                "privateKey=" + privateKey.toString(16).substring(0, Math.min(32, privateKey.toString(16).length()))
                + "..." +
                ", publicKey=" + publicKey.toString(16).substring(0, Math.min(32, publicKey.toString(16).length()))
                + "..." +
                '}';
    }
}
