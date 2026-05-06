import java.awt.GraphicsEnvironment;

import javax.swing.*;

/**
 * =========================================================================
 * SCHNORR DIGITAL SIGNATURE ALGORITHM - MAIN ENTRY POINT
 * =========================================================================
 * 
 * Ứng dụng: Mô phỏng thuật toán Chữ ký số Schnorr
 * Trường: HAUI (Hanoi University of Industry)
 * Môn học: An toàn Bảo mật Thông tin
 * 
 * Tác giả: Sinh viên Lớp [Tên lớp]
 * Ngày: 2026
 * 
 * =========================================================================
 * CHỨC NĂNG:
 * =========================================================================
 * 
 * GIAI ĐOẠN 1: Phát sinh khóa (Key Generation)
 * - Tạo cặp khóa công khai/bí mật
 * - Sử dụng số nguyên tố lớn (256+ bit)
 * 
 * GIAI ĐOẠN 2: Tạo chữ ký (Signing)
 * - Ký thông điệp sử dụng khóa bí mật
 * - Kết quả: Chữ ký (s, e)
 * 
 * GIAI ĐOẠN 3: Xác minh chữ ký (Verification)
 * - Kiểm tra tính hợp lệ của chữ ký
 * - Hiển thị kết quả: Hợp lệ (Xanh) / Không hợp lệ (Đỏ)
 * 
 * =========================================================================
 * CÔNG NGHỆ SỬ DỤNG:
 * =========================================================================
 * 
 * - Ngôn ngữ: Java
 * - GUI Framework: Java Swing
 * - Toán học: java.math.BigInteger
 * - Hash: SHA-256 (java.security.MessageDigest)
 * - Random: java.security.SecureRandom
 * - Mục tiêu: Giáo dục, Demo, Học tập
 * 
 * =========================================================================
 * KIẾN TRÚC:
 * =========================================================================
 * 
 * SchorrSignatureParams.java → Tham số hệ thống (p, q, g)
 * SchorrKeyPair.java → Cặp khóa (khóa bí mật, khóa công khai)
 * SchorrSignature.java → Chữ ký (s, e)
 * SchorrSignatureAlgorithm.java → Triển khai thuật toán 3 giai đoạn
 * SchorrGUI.java → Giao diện Swing
 * SchorrMain.java → Entry point chính (FILE NÀY)
 * 
 * =========================================================================
 */
public class SchorrMain {

    // Phiên bản ứng dụng
    private static final String APP_VERSION = "1.0";
    private static final String APP_NAME = "Schnorr Digital Signature Simulator";

    /**
     * Điểm vào chính của ứng dụng
     * Kiểm tra các điều kiện tiên quyết rồi khởi chạy GUI
     * 
     * @param args Tham số dòng lệnh (không sử dụng)
     */
    public static void main(String[] args) {
        try {
            // ===== BƯỚC 1: In thông báo chào mừng =====
            printWelcomeBanner();

            // ===== BƯỚC 2: Kiểm tra yêu cầu hệ thống =====
            checkSystemRequirements();

            // ===== BƯỚC 3: Cấu hình Look & Feel =====
            configureUI();

            // ===== BƯỚC 4: Khởi chạy GUI trên Event Dispatch Thread =====
            launchGUI();

        } catch (Exception e) {
            // Xử lý lỗi
            handleFatalError(e);
        }
    }

    /**
     * In thông báo chào mừng
     */
    private static void printWelcomeBanner() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                ║");
        System.out.println("║              🔐 SCHNORR DIGITAL SIGNATURE SIMULATOR 🔐          ║");
        System.out.println("║                          v" + APP_VERSION + "                                  ║");
        System.out.println("║                                                                ║");
        System.out.println("║  HAUI - An toàn Bảo mật Thông tin                             ║");
        System.out.println("║  Hanoi University of Industry                                  ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("📋 Ứng dụng triển khai 3 giai đoạn của Schnorr Digital Signature:");
        System.out.println("   1️⃣  Phát sinh khóa (Key Generation)");
        System.out.println("   2️⃣  Tạo chữ ký (Signing)");
        System.out.println("   3️⃣  Xác minh chữ ký (Verification)");
        System.out.println();
        System.out.println("⏳ Khởi chạy giao diện... Vui lòng chờ!");
        System.out.println();
    }

    /**
     * Kiểm tra yêu cầu hệ thống
     */
    private static void checkSystemRequirements() {
        System.out.println("✓ Kiểm tra yêu cầu hệ thống:");

        // ===== 1. Kiểm tra phiên bản Java =====
        String javaVersion = System.getProperty("java.version");
        System.out.println("  ✓ Java Version: " + javaVersion);

        // Kiểm tra Java 8 hoặc mới hơn
        try {
            String versionStr = javaVersion.split("\\.")[0];
            if (versionStr.equals("1")) {
                // Java 8 hoặc 7: format là 1.x.x_xx
                versionStr = javaVersion.split("\\.")[1];
            }
            int majorVersion = Integer.parseInt(versionStr);

            if (majorVersion < 8) {
                throw new RuntimeException("Java 8 hoặc mới hơn được yêu cầu!");
            }
        } catch (NumberFormatException e) {
            System.out.println("  ⚠ Cảnh báo: Không thể xác định phiên bản Java chính xác");
        }

        // ===== 2. Kiểm tra bộ nhớ khả dụng =====
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024); // Convert to MB
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        System.out.println("  ✓ Bộ nhớ: " + maxMemory + " MB (Khả dụng: " + freeMemory + " MB)");

        if (maxMemory < 256) {
            System.out.println("  ⚠ Cảnh báo: Bộ nhớ có thể không đủ (< 256 MB)");
            System.out.println("    Gợi ý: java -Xmx512m SchorrMain");
        }

        // ===== 3. Kiểm tra hệ điều hành =====
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        System.out.println("  ✓ Hệ điều hành: " + osName + " " + osVersion);

        // ===== 4. Kiểm tra giao diện đồ họa =====
        if (GraphicsEnvironment.isHeadless()) {
            throw new RuntimeException("Lỗi: Không tìm thấy giao diện đồ họa (GUI)! Hệ thống chạy ở chế độ headless.");
        }
        System.out.println("  ✓ Giao diện đồ họa: Có sẵn");

        // ===== 5. Kiểm tra các lớp cần thiết =====
        try {
            Class.forName("SchorrSignatureParams");
            Class.forName("SchorrKeyPair");
            Class.forName("SchorrSignature");
            Class.forName("SchorrSignatureAlgorithm");
            Class.forName("SchorrGUI");
            System.out.println("  ✓ Tất cả các lớp cần thiết: OK");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Lỗi: Không tìm thấy lớp: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * Cấu hình Look & Feel của Swing
     */
    private static void configureUI() {
        System.out.println("⚙️  Cấu hình giao diện...");

        try {
            // Cố gắng sử dụng Look & Feel của hệ thống
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("  ✓ Look & Feel: " + UIManager.getLookAndFeel().getName());
        } catch (Exception e) {
            // Nếu không thành công, sử dụng mặc định
            System.out.println("  ⚠ Không thể tải Look & Feel của hệ thống, sử dụng mặc định");
        }

        System.out.println();
    }

    /**
     * Khởi chạy giao diện GUI trên Event Dispatch Thread
     * Điều này đảm bảo tính an toàn luồng (thread safety)
     */
    private static void launchGUI() {
        System.out.println("🚀 Khởi chạy giao diện...");
        System.out.println();

        // Khởi chạy GUI trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Tạo cửa sổ chính
                SchorrGUI gui = new SchorrGUI();
                System.out.println("[GUI] Giao diện đã được khởi chạy thành công!");
            } catch (Exception e) {
                System.err.println("[LỖI] Không thể khởi chạy giao diện:");
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    /**
     * Xử lý lỗi fatal
     */
    private static void handleFatalError(Exception e) {
        System.err.println();
        System.err.println("╔════════════════════════════════════════════════════════════════╗");
        System.err.println("║                                                                ║");
        System.err.println("║                   ❌ LỖI FATAL ❌                              ║");
        System.err.println("║                                                                ║");
        System.err.println("╚════════════════════════════════════════════════════════════════╝");
        System.err.println();
        System.err.println("Thông báo lỗi: " + e.getMessage());
        System.err.println();
        System.err.println("Chi tiết:");
        e.printStackTrace();
        System.err.println();
        System.err.println("Gợi ý:");
        System.err.println("  1. Đảm bảo tất cả 5 file Java đã được biên dịch thành công");
        System.err.println("  2. Kiểm tra phiên bản Java (cần Java 8 hoặc mới hơn)");
        System.err.println("  3. Đảm bảo bạn đang ở thư mục chính xác");
        System.err.println("  4. Xóa tất cả file .class và biên dịch lại");
        System.err.println();
        System.err.println("Lệnh biên dịch:");
        System.err.println("  javac SchorrSignatureParams.java SchorrKeyPair.java \\");
        System.err.println("         SchorrSignature.java SchorrSignatureAlgorithm.java \\");
        System.err.println("         SchorrGUI.java SchorrMain.java");
        System.err.println();
        System.err.println("Lệnh chạy:");
        System.err.println("  java SchorrMain");
        System.err.println();

        System.exit(1);
    }
}
