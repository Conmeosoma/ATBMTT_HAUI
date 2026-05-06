import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.DSAParameterSpec;

/**
 * ============================================================
 * CHỮ KÝ SỐ SCHNORR – Mô phỏng bằng Java Swing
 * ============================================================
 *
 * Thuật toán Schnorr gồm 3 giai đoạn:
 *
 * [KEY GEN] Chọn p, q nguyên tố (q | p−1), g sinh viên nhóm con bậc q.
 * Khóa bí mật x ∈ [1, q−1], khóa công khai y = g^x mod p.
 *
 * [SIGN] Chọn ngẫu nhiên k ∈ [1, q−1]
 * r = g^k mod p
 * e = H(M ‖ r) (SHA-256, rút gọn mod q)
 * s = (k − x·e) mod q
 * Chữ ký = (s, e)
 *
 * [VERIFY] rv = g^s · y^e mod p
 * ev = H(M ‖ rv)
 * Hợp lệ ⟺ ev == e
 */
public class SchnorrSignatureApp {

    // =========================================================
    // 1. CORE ENGINE – Toàn bộ toán học Schnorr ở đây
    // =========================================================
    static class SchnorrEngine {

        // --- Tham số công khai hệ thống ---
        private BigInteger p; // Số nguyên tố lớn (1024-bit)
        private BigInteger q; // Số nguyên tố bậc (160-bit), q | (p−1)
        private BigInteger g; // Phần tử sinh nhóm con bậc q trong Z*_p

        // --- Cặp khóa ---
        private BigInteger privateKey; // x (bí mật)
        private BigInteger publicKey; // y = g^x mod p (công khai)

        // --- Chữ ký hiện tại ---
        private BigInteger sigS; // Thành phần s
        private BigInteger sigE; // Thành phần e (hash)

        private final SecureRandom rng = new SecureRandom();

        // ---------------------------------------------------------
        // PHÁT SINH KHÓA (Key Generation)
        // ---------------------------------------------------------
        public void generateKeys() throws Exception {
            /*
             * Dùng Java DSA param generator để có bộ (p, q, g) chuẩn 1024/160-bit
             * đảm bảo: q nguyên tố, q | (p−1), ord(g) = q.
             */
            AlgorithmParameterGenerator apg = AlgorithmParameterGenerator.getInstance("DSA");
            apg.init(1024);
            AlgorithmParameters ap = apg.generateParameters();
            DSAParameterSpec spec = ap.getParameterSpec(DSAParameterSpec.class);

            p = spec.getP();
            q = spec.getQ();
            g = spec.getG();

            // Chọn ngẫu nhiên x ∈ (1, q) → khóa bí mật
            do {
                privateKey = new BigInteger(q.bitLength(), rng);
            } while (privateKey.compareTo(BigInteger.ONE) <= 0
                    || privateKey.compareTo(q) >= 0);

            // Tính y = g^x mod p → khóa công khai
            publicKey = g.modPow(privateKey, p);

            // Xóa chữ ký cũ khi sinh khóa mới
            sigS = null;
            sigE = null;
        }

        // ---------------------------------------------------------
        // TẠO CHỮ KÝ (Signing)
        // ---------------------------------------------------------
        public void sign(String message) throws Exception {
            if (privateKey == null)
                throw new IllegalStateException("Chưa phát sinh khóa!");

            BigInteger k, r;

            // Bước 1: Chọn nonce bí mật k ∈ (1, q)
            do {
                k = new BigInteger(q.bitLength(), rng);
            } while (k.compareTo(BigInteger.ONE) <= 0
                    || k.compareTo(q) >= 0);

            // Bước 2: Tính cam kết r = g^k mod p
            r = g.modPow(k, p);

            // Bước 3: Băm thông điệp cùng cam kết e = H(M ‖ r) mod q
            sigE = schnorrHash(message, r);

            // Bước 4: Tính thành phần chứng minh s = (k − x·e) mod q
            sigS = k.subtract(privateKey.multiply(sigE)).mod(q);
        }

        // ---------------------------------------------------------
        // KIỂM TRA CHỮ KÝ (Verification)
        // ---------------------------------------------------------
        public boolean verify(String message) throws Exception {
            if (publicKey == null)
                throw new IllegalStateException("Chưa có khóa công khai!");
            if (sigS == null || sigE == null)
                throw new IllegalStateException("Chưa có chữ ký!");

            // Bước 1: Phục hồi cam kết
            // g^s · y^e ≡ g^(k−xe) · g^(xe) ≡ g^k ≡ r (mod p)
            BigInteger gs = g.modPow(sigS, p);
            BigInteger ye = publicKey.modPow(sigE, p);
            BigInteger rv = gs.multiply(ye).mod(p);

            // Bước 2: Băm lại để so sánh
            BigInteger ev = schnorrHash(message, rv);

            // Bước 3: Chữ ký hợp lệ khi và chỉ khi ev == e
            return ev.equals(sigE);
        }

        // ---------------------------------------------------------
        // HÀM BĂM SCHNORR: H(M ‖ r) dùng SHA-256, thu gọn mod q
        // ---------------------------------------------------------
        private BigInteger schnorrHash(String message, BigInteger r) throws Exception {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(message.getBytes(StandardCharsets.UTF_8));
            sha256.update(r.toByteArray());
            byte[] digest = sha256.digest();
            // Diễn giải hash như số nguyên dương, rút gọn về [0, q)
            return new BigInteger(1, digest).mod(q);
        }

        // --- Getters ---
        public BigInteger getP() {
            return p;
        }

        public BigInteger getQ() {
            return q;
        }

        public BigInteger getG() {
            return g;
        }

        public BigInteger getPrivateKey() {
            return privateKey;
        }

        public BigInteger getPublicKey() {
            return publicKey;
        }

        public BigInteger getSigS() {
            return sigS;
        }

        public BigInteger getSigE() {
            return sigE;
        }

        public boolean hasKeys() {
            return privateKey != null;
        }

        public boolean hasSignature() {
            return sigS != null && sigE != null;
        }
    }

    // =========================================================
    // 2. GIAO DIỆN – Swing GUI
    // =========================================================
    static class SchnorrGUI {

        // --- Bảng màu ---
        private static final Color BG_DARK = new Color(13, 17, 30);
        private static final Color BG_PANEL = new Color(22, 28, 48);
        private static final Color BG_FIELD = new Color(30, 38, 62);
        private static final Color ACCENT = new Color(82, 130, 255);
        private static final Color ACCENT_HOV = new Color(110, 155, 255);
        private static final Color SUCCESS = new Color(52, 211, 153);
        private static final Color DANGER = new Color(251, 91, 91);
        private static final Color FG_MAIN = new Color(220, 228, 255);
        private static final Color FG_MUTED = new Color(120, 135, 170);
        private static final Color FG_LABEL = new Color(165, 180, 220);
        private static final Color BORDER_COL = new Color(45, 58, 90);

        // --- Font ---
        private static final Font FONT_MONO = new Font("JetBrains Mono", Font.PLAIN, 11);
        private static final Font FONT_MONO2 = new Font("Consolas", Font.PLAIN, 11);
        private static final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 13);
        private static final Font FONT_HEAD = new Font("Segoe UI Semibold", Font.BOLD, 13);
        private static final Font FONT_TITLE = new Font("Segoe UI Light", Font.PLAIN, 20);
        private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 13);

        // --- Widgets ---
        private JFrame frame;
        private JTextField msgField;
        private JTextArea paramsArea, privKeyArea, pubKeyArea,
                sigSArea, sigEArea;
        private JLabel statusLabel;
        private JButton btnGenKey, btnSign, btnVerify;
        private JProgressBar progressBar;

        // --- Engine ---
        private final SchnorrEngine engine = new SchnorrEngine();

        // ---------------------------------------------------------
        // Khởi tạo & hiển thị cửa sổ
        // ---------------------------------------------------------
        public void show() {
            frame = new JFrame("Chữ Ký Số Schnorr – Mô Phỏng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(820, 740));
            frame.setSize(920, 820);
            frame.getContentPane().setBackground(BG_DARK);
            frame.setLayout(new BorderLayout(0, 0));

            frame.add(buildHeader(), BorderLayout.NORTH);
            frame.add(buildCenter(), BorderLayout.CENTER);
            frame.add(buildFooter(), BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        // --- Header ---
        private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(BG_PANEL);
            p.setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
                    new EmptyBorder(18, 24, 18, 24)));

            JLabel title = new JLabel("⬡  Schnorr Digital Signature");
            title.setFont(FONT_TITLE);
            title.setForeground(FG_MAIN);

            JLabel sub = new JLabel("SHA-256 · BigInteger · Java Swing");
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            sub.setForeground(FG_MUTED);

            JPanel left = new JPanel(new GridLayout(2, 1, 0, 3));
            left.setOpaque(false);
            left.add(title);
            left.add(sub);
            p.add(left, BorderLayout.WEST);
            return p;
        }

        // --- Nội dung chính ---
        private JScrollPane buildCenter() {
            JPanel main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBackground(BG_DARK);
            main.setBorder(new EmptyBorder(20, 22, 20, 22));

            main.add(buildMessageSection());
            main.add(gap(14));
            main.add(buildButtonBar());
            main.add(gap(14));
            main.add(buildParamsSection());
            main.add(gap(14));
            main.add(buildKeysSection());
            main.add(gap(14));
            main.add(buildSignatureSection());

            JScrollPane scroll = new JScrollPane(main);
            scroll.setBorder(null);
            scroll.getViewport().setBackground(BG_DARK);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            return scroll;
        }

        // --- Nhập thông điệp ---
        private JPanel buildMessageSection() {
            JPanel card = card("📄  Thông Điệp (Message)");
            msgField = styledField("Nhập thông điệp cần ký...");
            msgField.setFont(FONT_UI.deriveFont(14f));
            msgField.setPreferredSize(new Dimension(0, 40));
            card.add(msgField, BorderLayout.CENTER);
            return card;
        }

        // --- Thanh nút bấm ---
        private JPanel buildButtonBar() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            p.setOpaque(false);

            btnGenKey = accentButton("🔑  Generate Keys", ACCENT);
            btnSign = accentButton("✍  Sign Message", new Color(100, 60, 200));
            btnVerify = accentButton("✔  Verify Signature", new Color(30, 150, 110));

            btnSign.setEnabled(false);
            btnVerify.setEnabled(false);

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(false);
            progressBar.setVisible(false);
            progressBar.setPreferredSize(new Dimension(120, 22));
            progressBar.setBackground(BG_FIELD);
            progressBar.setForeground(ACCENT);
            progressBar.setBorderPainted(false);

            p.add(btnGenKey);
            p.add(btnSign);
            p.add(btnVerify);
            p.add(progressBar);

            // ---- Sự kiện ----
            btnGenKey.addActionListener(e -> doGenerateKeys());
            btnSign.addActionListener(e -> doSign());
            btnVerify.addActionListener(e -> doVerify());

            return p;
        }

        // --- Tham số hệ thống (p, q, g) ---
        private JPanel buildParamsSection() {
            JPanel card = card("⚙  Tham Số Hệ Thống  (p, q, g)");
            paramsArea = monoArea(5);
            paramsArea.setToolTipText("p: số nguyên tố lớn | q: bậc nhóm con | g: phần tử sinh");
            card.add(new JScrollPane(paramsArea), BorderLayout.CENTER);
            return card;
        }

        // --- Hiển thị khóa ---
        private JPanel buildKeysSection() {
            JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
            row.setOpaque(false);

            JPanel privCard = card("🔒  Khóa Bí Mật  (Private Key  x)");
            privKeyArea = monoArea(4);
            privCard.add(new JScrollPane(privKeyArea), BorderLayout.CENTER);

            JPanel pubCard = card("🔓  Khóa Công Khai  (Public Key  y = gˣ mod p)");
            pubKeyArea = monoArea(4);
            pubCard.add(new JScrollPane(pubKeyArea), BorderLayout.CENTER);

            row.add(privCard);
            row.add(pubCard);
            return row;
        }

        // --- Hiển thị chữ ký ---
        private JPanel buildSignatureSection() {
            JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
            row.setOpaque(false);

            JPanel sCard = card("🖊  Chữ Ký – Thành Phần  s  =  (k − x·e) mod q");
            sigSArea = monoArea(4);
            sCard.add(new JScrollPane(sigSArea), BorderLayout.CENTER);

            JPanel eCard = card("🖊  Chữ Ký – Thành Phần  e  =  H(M ‖ r) mod q");
            sigEArea = monoArea(4);
            eCard.add(new JScrollPane(sigEArea), BorderLayout.CENTER);

            row.add(sCard);
            row.add(eCard);
            return row;
        }

        // --- Footer (trạng thái) ---
        private JPanel buildFooter() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(BG_PANEL);
            p.setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL),
                    new EmptyBorder(10, 24, 10, 24)));

            statusLabel = new JLabel("  Sẵn sàng. Nhấn 'Generate Keys' để bắt đầu.");
            statusLabel.setFont(FONT_UI.deriveFont(Font.BOLD));
            statusLabel.setForeground(FG_MUTED);
            p.add(statusLabel, BorderLayout.CENTER);

            JLabel copy = new JLabel("Schnorr Signature  ©  2025");
            copy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            copy.setForeground(FG_MUTED);
            p.add(copy, BorderLayout.EAST);
            return p;
        }

        // =========================================================
        // 3. XỬ LÝ SỰ KIỆN – Gọi engine trên worker thread
        // =========================================================

        // ---- Generate Keys ----
        private void doGenerateKeys() {
            setStatus("⏳ Đang phát sinh khóa (1024-bit)...", FG_MUTED);
            setLoading(true);
            new Thread(() -> {
                try {
                    engine.generateKeys();
                    SwingUtilities.invokeLater(() -> {
                        displayKeys();
                        setStatus("✅  Phát sinh khóa thành công!", SUCCESS);
                        btnSign.setEnabled(true);
                        btnVerify.setEnabled(false);
                        clearSignature();
                        setLoading(false);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        setStatus("❌  Lỗi: " + ex.getMessage(), DANGER);
                        setLoading(false);
                    });
                }
            }).start();
        }

        // ---- Sign Message ----
        private void doSign() {
            String msg = msgField.getText().trim();
            if (msg.isEmpty()) {
                setStatus("⚠  Vui lòng nhập thông điệp trước khi ký!", new Color(255, 200, 50));
                return;
            }
            setStatus("⏳ Đang tạo chữ ký...", FG_MUTED);
            setLoading(true);
            new Thread(() -> {
                try {
                    engine.sign(msg);
                    SwingUtilities.invokeLater(() -> {
                        displaySignature();
                        setStatus("✅  Tạo chữ ký thành công! Chữ ký = (s, e)", SUCCESS);
                        btnVerify.setEnabled(true);
                        setLoading(false);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        setStatus("❌  Lỗi: " + ex.getMessage(), DANGER);
                        setLoading(false);
                    });
                }
            }).start();
        }

        // ---- Verify Signature ----
        private void doVerify() {
            String msg = msgField.getText().trim();
            if (msg.isEmpty()) {
                setStatus("⚠  Thông điệp không được để trống!", new Color(255, 200, 50));
                return;
            }
            setStatus("⏳ Đang kiểm tra chữ ký...", FG_MUTED);
            setLoading(true);
            new Thread(() -> {
                try {
                    boolean valid = engine.verify(msg);
                    SwingUtilities.invokeLater(() -> {
                        if (valid) {
                            setStatus("✅  Chữ ký HỢP LỆ  —  ev == e  ✓  Xác thực thành công!", SUCCESS);
                        } else {
                            setStatus("❌  Chữ ký KHÔNG HỢP LỆ  —  ev ≠ e  ✗  Dữ liệu bị thay đổi hoặc chữ ký sai!",
                                    DANGER);
                        }
                        setLoading(false);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        setStatus("❌  Lỗi: " + ex.getMessage(), DANGER);
                        setLoading(false);
                    });
                }
            }).start();
        }

        // =========================================================
        // 4. CẬP NHẬT HIỂN THỊ
        // =========================================================

        private void displayKeys() {
            // Hiển thị tham số hệ thống
            paramsArea.setText(
                    "p (1024-bit) =\n" + hexWrap(engine.getP()) +
                            "\n\nq (160-bit) =\n" + hexWrap(engine.getQ()) +
                            "\n\ng (generator) =\n" + hexWrap(engine.getG()));
            privKeyArea.setText("x =\n" + hexWrap(engine.getPrivateKey()));
            pubKeyArea.setText("y = g^x mod p =\n" + hexWrap(engine.getPublicKey()));
        }

        private void displaySignature() {
            sigSArea.setText("s =\n" + hexWrap(engine.getSigS()));
            sigEArea.setText("e =\n" + hexWrap(engine.getSigE()));
        }

        private void clearSignature() {
            sigSArea.setText("");
            sigEArea.setText("");
        }

        // =========================================================
        // 5. TIỆN ÍCH GIAO DIỆN
        // =========================================================

        /** Gói BigInteger thành chuỗi hex 64 ký tự mỗi dòng */
        private String hexWrap(BigInteger n) {
            String hex = n.toString(16).toUpperCase();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hex.length(); i += 64) {
                sb.append(hex, i, Math.min(i + 64, hex.length())).append('\n');
            }
            return sb.toString().trim();
        }

        private void setStatus(String text, Color color) {
            statusLabel.setText("  " + text);
            statusLabel.setForeground(color);
        }

        private void setLoading(boolean on) {
            progressBar.setVisible(on);
            progressBar.setIndeterminate(on);
            btnGenKey.setEnabled(!on);
            if (!on) {
                btnSign.setEnabled(engine.hasKeys());
                btnVerify.setEnabled(engine.hasSignature());
            } else {
                btnSign.setEnabled(false);
                btnVerify.setEnabled(false);
            }
        }

        // --- Factory: card container ---
        private JPanel card(String title) {
            JPanel p = new JPanel(new BorderLayout(0, 8));
            p.setBackground(BG_PANEL);
            p.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COL, 1, true),
                    new EmptyBorder(12, 14, 14, 14)));

            JLabel lbl = new JLabel(title);
            lbl.setFont(FONT_HEAD);
            lbl.setForeground(FG_LABEL);
            lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
            p.add(lbl, BorderLayout.NORTH);
            return p;
        }

        // --- Factory: mono text area ---
        private JTextArea monoArea(int rows) {
            JTextArea ta = new JTextArea(rows, 0);
            ta.setEditable(false);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(false);
            ta.setBackground(BG_FIELD);
            ta.setForeground(new Color(130, 220, 160));
            ta.setCaretColor(FG_MAIN);
            ta.setFont(fontMono());
            ta.setBorder(new EmptyBorder(6, 8, 6, 8));
            return ta;
        }

        // --- Factory: styled input field ---
        private JTextField styledField(String placeholder) {
            JTextField tf = new JTextField() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (getText().isEmpty()) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(FG_MUTED);
                        g2.setFont(getFont().deriveFont(Font.ITALIC));
                        g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                    }
                }
            };
            tf.setBackground(BG_FIELD);
            tf.setForeground(FG_MAIN);
            tf.setCaretColor(ACCENT);
            tf.setFont(FONT_UI);
            tf.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COL, 1, true),
                    new EmptyBorder(4, 10, 4, 10)));
            return tf;
        }

        // --- Factory: accent button với hover effect ---
        private JButton accentButton(String text, Color base) {
            JButton btn = new JButton(text) {
                private boolean hovered = false;
                {
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) {
                            hovered = true;
                            repaint();
                        }

                        public void mouseExited(MouseEvent e) {
                            hovered = false;
                            repaint();
                        }
                    });
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = isEnabled()
                            ? (hovered ? base.brighter() : base)
                            : new Color(60, 65, 90);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(isEnabled() ? Color.WHITE : FG_MUTED);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                    int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(getText(), tx, ty);
                    g2.dispose();
                }

                @Override
                protected void paintBorder(Graphics g) {
                }

                @Override
                public boolean isOpaque() {
                    return false;
                }
            };
            btn.setFont(FONT_BTN);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(180, 36));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }

        /** Trả về font monospace tốt nhất có trên máy */
        private Font fontMono() {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            java.util.Set<String> fonts = new java.util.HashSet<>(
                    java.util.Arrays.asList(ge.getAvailableFontFamilyNames()));
            for (String f : new String[] { "JetBrains Mono", "Fira Code", "Cascadia Code", "Consolas", "Courier New" })
                if (fonts.contains(f))
                    return new Font(f, Font.PLAIN, 11);
            return new Font(Font.MONOSPACED, Font.PLAIN, 11);
        }

        private Component gap(int h) {
            return Box.createRigidArea(new Dimension(0, h));
        }
    }

    // =========================================================
    // ENTRY POINT
    // =========================================================
    public static void main(String[] args) {
        // Dùng FlatLaf nếu có, fallback về System L&F
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Bật font anti-alias toàn cục
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> new SchnorrGUI().show());
    }
}