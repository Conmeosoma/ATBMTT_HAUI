import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Giao diện GUI nâng cao cho thuật toán Schnorr - Security-Tech Refined
 * Aesthetic.
 */
public class SchorrGUI extends JFrame {

    // ─── Core references ────────────────────────────────────────────────────────
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JTabbedPane tabbedPane;
    private ToggleSwitch themeToggle;

    private JLabel headerTitleLabel;
    private JLabel headerSubtitleLabel;
    private JLabel keyStatusLabel;
    private JLabel signStatusLabel;
    private JLabel verifyStatusLabel;
    private JLabel verificationResultLabel;

    private JTextArea pDisplay, qDisplay, gDisplay;
    private JTextArea privateKeyDisplay, publicKeyDisplay, keySummaryDisplay;
    private JTextArea messageInput, hashDisplay, signatureDisplay;
    private JTextArea verifyMessageInput, verifySignatureInput, verifyPublicKeyDisplay;
    private JTextArea instructionArea;

    private JButton generateKeysButton, randomKeysButton, saveKeyButton;
    private JButton signMessageButton;
    private JButton loadMessageButton, saveMessageButton, saveSignatureButton;
    private JButton verifyButton;
    private JButton loadVerifyMessageButton, loadVerifySignatureButton, loadPublicKeyButton;

    private SchorrSignatureAlgorithm algorithm;
    private SchorrKeyPair currentKeyPair;
    private SchorrSignature currentSignature;
    private boolean darkMode;

    // ─── Typography (logical fonts → full Unicode / tiếng Việt) ─────────────────
    private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 22);
    private final Font SUBTITLE_FONT = new Font("Serif", Font.ITALIC, 13);
    private final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 12);
    private final Font BODY_FONT = new Font("Dialog", Font.PLAIN, 13);
    private final Font CODE_FONT = new Font("Monospaced", Font.PLAIN, 12);
    private final Font TAB_FONT = new Font("Dialog", Font.BOLD, 13);
    private final Font BTN_FONT = new Font("Dialog", Font.BOLD, 13);
    private final Font STATUS_FONT = new Font("Dialog", Font.ITALIC, 12);

    // ─── Fixed accent colours ────────────────────────────────────────────────────
    private final Color GOLD = new Color(212, 175, 55);
    private final Color GOLD_LIGHT = new Color(240, 210, 90);
    private final Color TEAL = new Color(32, 201, 151);
    private final Color TEAL_DARK = new Color(22, 160, 115);
    private final Color DANGER = new Color(220, 75, 75);
    private final Color DANGER_DARK = new Color(175, 45, 45);
    private final Color NEUTRAL = new Color(100, 116, 139);
    private final Color NEUTRAL_DARK = new Color(71, 85, 105);

    // ─── Theme-dependent colours ─────────────────────────────────────────────────
    private Color APP_BG, PANEL_BG, CARD_BG, CARD_BORDER;
    private Color TEXT, MUTED, HEADER_BG1, HEADER_BG2;
    private Color TITLE_CLR, SUBTITLE_CLR, SCROLLBAR_CLR;

    // ════════════════════════════════════════════════════════════════════════════
    public SchorrGUI() {
        algorithm = new SchorrSignatureAlgorithm();
        applyTheme(false);
        setTitle("Phần Mềm Chữ Ký Điện Tử Schnorr");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 780);
        setLocationRelativeTo(null);
        setContentPane(buildMainPanel());
        setVisible(true);
    }

    // ─── Root panel ──────────────────────────────────────────────────────────────
    private JPanel buildMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(APP_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 18, 18));

        headerPanel = buildHeaderPanel();
        tabbedPane = buildTabbedPane();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        return mainPanel;
    }

    // ─── Header ──────────────────────────────────────────────────────────────────
    private JPanel buildHeaderPanel() {
        JPanel panel = new GradientPanel(HEADER_BG1, HEADER_BG2);
        panel.setLayout(new BorderLayout(12, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(CARD_BORDER, 14, 1),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)));

        // Left: title block with accent bar
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JPanel accentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        accentRow.setOpaque(false);

        JLabel bar = new JLabel("▐");
        bar.setFont(new Font("Dialog", Font.BOLD, 26));
        bar.setForeground(GOLD);

        headerTitleLabel = new JLabel("Chữ Ký Điện Tử Schnorr");
        headerTitleLabel.setFont(TITLE_FONT);
        headerTitleLabel.setForeground(TITLE_CLR);

        accentRow.add(bar);
        accentRow.add(headerTitleLabel);

        headerSubtitleLabel = new JLabel("  Hệ thống mô phỏng sinh khóa · ký số · xác minh thông điệp");
        headerSubtitleLabel.setFont(SUBTITLE_FONT);
        headerSubtitleLabel.setForeground(SUBTITLE_CLR);

        titleBlock.add(accentRow);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(headerSubtitleLabel);

        // Right: badge + toggle
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        rightBox.setOpaque(false);

        JLabel badge = new JLabel("  🔐  SCHNORR v1.0  ");
        badge.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
        badge.setForeground(GOLD);
        badge.setOpaque(true);
        badge.setBackground(darkMode ? new Color(60, 50, 10) : new Color(255, 248, 220));
        badge.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(GOLD, 10, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JLabel themeLabel = new JLabel("Dark ");
        themeLabel.setFont(BODY_FONT);
        themeLabel.setForeground(SUBTITLE_CLR);

        themeToggle = new ToggleSwitch(darkMode);
        themeToggle.addActionListener(e -> {
            applyTheme(themeToggle.isSelected());
            refreshTheme();
        });

        rightBox.add(badge);
        rightBox.add(themeLabel);
        rightBox.add(themeToggle);

        panel.add(titleBlock, BorderLayout.WEST);
        panel.add(rightBox, BorderLayout.EAST);
        return panel;
    }

    // ─── Tabbed pane ─────────────────────────────────────────────────────────────
    private JTabbedPane buildTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        tabs.setFocusable(false);
        tabs.setOpaque(false);
        tabs.addTab("🔑  Tạo Khóa", buildKeyTab());
        tabs.addTab("✍  Ký Văn Bản", buildSignTab());
        tabs.addTab("✅  Xác Minh", buildVerifyTab());
        tabs.addTab("📘  Hướng Dẫn", buildHelpTab());
        return tabs;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // TAB 1 – KEY GENERATION
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel buildKeyTab() {
        JPanel tab = makeTabPanel();

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel heading = sectionHeading("Thiết lập Tham số Schnorr & Cặp Khóa Bảo Mật");
        keyStatusLabel = makeStatusLabel("Trạng thái: Chưa tạo khóa");
        topRow.add(heading, BorderLayout.WEST);
        topRow.add(keyStatusLabel, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 2, 18, 0));
        cards.setOpaque(false);
        cards.add(buildParamCard());
        cards.add(buildKeyOutputCard());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        btnRow.setOpaque(false);

        generateKeysButton = makeBtn("⚙  Tạo khóa tự động", GOLD, GOLD_LIGHT);
        randomKeysButton = makeBtn("🎲  Khóa ngẫu nhiên", NEUTRAL, NEUTRAL_DARK);
        saveKeyButton = makeBtn("💾  Lưu khóa ra tệp", TEAL, TEAL_DARK);

        generateKeysButton.addActionListener(e -> handleGenerateKeys());
        randomKeysButton.addActionListener(e -> handleGenerateKeys());
        saveKeyButton.addActionListener(e -> saveKeyPairToFile());

        btnRow.add(generateKeysButton);
        btnRow.add(randomKeysButton);
        btnRow.add(saveKeyButton);

        tab.add(topRow, BorderLayout.NORTH);
        tab.add(cards, BorderLayout.CENTER);
        tab.add(btnRow, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel buildParamCard() {
        pDisplay = makeCodeArea(2);
        qDisplay = makeCodeArea(2);
        gDisplay = makeCodeArea(2);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 12));
        form.setOpaque(false);
        form.add(makeFieldLabel("Số nguyên tố P"));
        form.add(wrapScroll(pDisplay));
        form.add(makeFieldLabel("Số nguyên tố Q"));
        form.add(wrapScroll(qDisplay));
        form.add(makeFieldLabel("Phần tử sinh G"));
        form.add(wrapScroll(gDisplay));

        return makeCard("Tham số P · Q · G", form);
    }

    private JPanel buildKeyOutputCard() {
        privateKeyDisplay = makeCodeArea(3);
        publicKeyDisplay = makeCodeArea(3);
        keySummaryDisplay = makeCodeArea(5);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 12));
        form.setOpaque(false);
        form.add(makeFieldLabel("Khóa bí mật (x)"));
        form.add(wrapScroll(privateKeyDisplay));
        form.add(makeFieldLabel("Khóa công khai (y)"));
        form.add(wrapScroll(publicKeyDisplay));
        form.add(makeFieldLabel("Tóm tắt độ dài bit"));
        form.add(wrapScroll(keySummaryDisplay));

        return makeCard("Cặp Khóa Schnorr", form);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // TAB 2 – SIGN
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel buildSignTab() {
        JPanel tab = makeTabPanel();

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        toolBar.setOpaque(false);
        loadMessageButton = makeBtn("📂  Tải văn bản", NEUTRAL, NEUTRAL_DARK);
        saveMessageButton = makeBtn("💾  Lưu văn bản", NEUTRAL, NEUTRAL_DARK);
        saveSignatureButton = makeBtn("📤  Xuất chữ ký", TEAL, TEAL_DARK);

        loadMessageButton.addActionListener(e -> loadTextInto(messageInput));
        saveMessageButton.addActionListener(e -> saveTextFrom(messageInput, "vanban.txt"));
        saveSignatureButton.addActionListener(e -> saveTextFrom(signatureDisplay, "chuky.txt"));

        toolBar.add(loadMessageButton);
        toolBar.add(saveMessageButton);
        toolBar.add(saveSignatureButton);

        messageInput = makeEditableArea(7);
        hashDisplay = makeCodeArea(5);
        signatureDisplay = makeCodeArea(5);

        JPanel center = new JPanel(new GridLayout(3, 1, 0, 14));
        center.setOpaque(false);
        center.add(makeCard("Văn bản cần ký", wrapScroll(messageInput)));
        center.add(makeCard("Kết quả băm SHA-256", wrapScroll(hashDisplay)));
        center.add(makeCard("Chữ ký số tạo ra", wrapScroll(signatureDisplay)));

        signMessageButton = makeBtn("✍  Thực Hiện Ký", GOLD, GOLD_LIGHT);
        signMessageButton.setPreferredSize(new Dimension(200, 44));
        signMessageButton.setFont(new Font("Dialog", Font.BOLD, 15));
        signMessageButton.addActionListener(e -> handleSignMessage());

        signStatusLabel = makeStatusLabel("Trạng thái: Chưa ký văn bản");

        JPanel bottom = new JPanel(new BorderLayout(14, 0));
        bottom.setOpaque(false);
        bottom.add(signStatusLabel, BorderLayout.WEST);
        JPanel signBtnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        signBtnWrap.setOpaque(false);
        signBtnWrap.add(signMessageButton);
        bottom.add(signBtnWrap, BorderLayout.EAST);

        tab.add(toolBar, BorderLayout.NORTH);
        tab.add(center, BorderLayout.CENTER);
        tab.add(bottom, BorderLayout.SOUTH);
        return tab;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // TAB 3 – VERIFY
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel buildVerifyTab() {
        JPanel tab = makeTabPanel();

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        toolBar.setOpaque(false);
        loadVerifyMessageButton = makeBtn("📂  Tải văn bản", NEUTRAL, NEUTRAL_DARK);
        loadVerifySignatureButton = makeBtn("📂  Tải chữ ký", NEUTRAL, NEUTRAL_DARK);
        loadPublicKeyButton = makeBtn("🔑  Tải khóa công khai", NEUTRAL, NEUTRAL_DARK);

        loadVerifyMessageButton.addActionListener(e -> loadTextInto(verifyMessageInput));
        loadVerifySignatureButton.addActionListener(e -> loadTextInto(verifySignatureInput));
        loadPublicKeyButton.addActionListener(e -> {
            if (currentKeyPair != null)
                verifyPublicKeyDisplay.setText(formatPublicKeyForDisplay(currentKeyPair.getPublicKey()));
        });

        toolBar.add(loadVerifyMessageButton);
        toolBar.add(loadVerifySignatureButton);
        toolBar.add(loadPublicKeyButton);

        verifyMessageInput = makeEditableArea(6);
        verifySignatureInput = makeEditableArea(5);
        verifyPublicKeyDisplay = makeCodeArea(5);

        JPanel center = new JPanel(new GridLayout(3, 1, 0, 14));
        center.setOpaque(false);
        center.add(makeCard("Nội dung văn bản", wrapScroll(verifyMessageInput)));
        center.add(makeCard("Dữ liệu chữ ký", wrapScroll(verifySignatureInput)));
        center.add(makeCard("Khóa công khai người gửi", wrapScroll(verifyPublicKeyDisplay)));

        verifyButton = makeBtn("✅  Xác Minh Chữ Ký", GOLD, GOLD_LIGHT);
        verifyButton.setPreferredSize(new Dimension(220, 44));
        verifyButton.setFont(new Font("Dialog", Font.BOLD, 15));
        verifyButton.addActionListener(e -> handleVerifySignature());

        verifyStatusLabel = makeStatusLabel("Tiến trình: Đang chờ xác minh");

        // ── Result banner ──
        verificationResultLabel = new JLabel("CHƯA XÁC MINH", SwingConstants.CENTER);
        verificationResultLabel.setFont(new Font("Serif", Font.BOLD, 17));
        verificationResultLabel.setOpaque(true);
        verificationResultLabel.setPreferredSize(new Dimension(0, 46));
        verificationResultLabel.setBackground(CARD_BG);
        verificationResultLabel.setForeground(MUTED);
        verificationResultLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(CARD_BORDER, 12, 1),
                BorderFactory.createEmptyBorder(0, 14, 0, 14)));

        JPanel resultArea = new JPanel(new BorderLayout(6, 6));
        resultArea.setOpaque(false);
        resultArea.add(verifyStatusLabel, BorderLayout.NORTH);
        resultArea.add(verificationResultLabel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(14, 0));
        bottom.setOpaque(false);
        bottom.add(resultArea, BorderLayout.CENTER);
        JPanel vBtnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        vBtnWrap.setOpaque(false);
        vBtnWrap.add(verifyButton);
        bottom.add(vBtnWrap, BorderLayout.EAST);

        tab.add(toolBar, BorderLayout.NORTH);
        tab.add(center, BorderLayout.CENTER);
        tab.add(bottom, BorderLayout.SOUTH);
        return tab;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // TAB 4 – HELP
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel buildHelpTab() {
        JPanel tab = makeTabPanel();
        instructionArea = makeCodeArea(20);
        instructionArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        instructionArea.setText(
                "HƯỚNG DẪN SỬ DỤNG PHẦN MỀM CHỮ KÝ SỐ SCHNORR\n" +
                        "══════════════════════════════════════════════════════════\n\n" +
                        "① Tab Tạo Khóa (Key Generation)\n" +
                        "   • Nhấn 'Tạo khóa tự động' để sinh tham số p, q, g và cặp khóa bí mật/công khai.\n" +
                        "   • Nhấn 'Lưu khóa ra tệp' để xuất file .txt dùng cho các phiên sau.\n\n" +
                        "② Tab Ký Văn Bản (Signing)\n" +
                        "   • Nhập hoặc tải thông điệp vào ô 'Văn bản cần ký'.\n" +
                        "   • Nhấn 'Thực Hiện Ký' — hệ thống dùng khóa bí mật (x) để sinh bộ chữ ký (s, e).\n" +
                        "   • Xuất chữ ký thành .txt để gửi cho bên nhận.\n\n" +
                        "③ Tab Xác Minh (Verification)\n" +
                        "   • Nhập văn bản gốc và chữ ký nhận được vào ô tương ứng.\n" +
                        "   • Đảm bảo khóa công khai (y) của người gửi đã được tải.\n" +
                        "   • Nhấn 'Xác Minh Chữ Ký': banner xanh = hợp lệ, banner đỏ = lỗi/giả mạo.\n\n" +
                        "④ Giao Diện Tối (Dark Mode)\n" +
                        "   • Bật công tắc 'Dark' ở góc trên phải để chuyển sang giao diện tối bảo vệ mắt.\n\n" +
                        "──────────────────────────────────────────────────────────\n" +
                        "Lưu ý bảo mật: Không chia sẻ khóa bí mật (x) cho bất kỳ ai.\n" +
                        "Chỉ chia sẻ khóa công khai (y) và bộ tham số (p, q, g).");
        tab.add(makeCard("Tài liệu Hướng Dẫn", wrapScroll(instructionArea)), BorderLayout.CENTER);
        return tab;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // LOGIC (unchanged from original)
    // ════════════════════════════════════════════════════════════════════════════
    private void handleGenerateKeys() {
        try {
            currentKeyPair = algorithm.generateKeys();
            SchorrSignatureParams params = currentKeyPair.getParams();

            pDisplay.setText(formatBigInteger(params.getP()));
            qDisplay.setText(formatBigInteger(params.getQ()));
            gDisplay.setText(formatBigInteger(params.getG()));
            privateKeyDisplay.setText(formatBigInteger(currentKeyPair.getPrivateKey()));
            publicKeyDisplay.setText(formatBigInteger(currentKeyPair.getPublicKey()));
            keySummaryDisplay.setText(buildKeySummary(currentKeyPair));

            keyStatusLabel.setText("✓ Đã tạo khóa thành công");
            keyStatusLabel.setForeground(TEAL);
            signStatusLabel.setText("Trạng thái: Sẵn sàng ký văn bản");
            verifyStatusLabel.setText("Trạng thái: Sẵn sàng xác minh");

            verifyPublicKeyDisplay.setText(formatPublicKeyForDisplay(currentKeyPair.getPublicKey()));
            if (verifyMessageInput.getText().isEmpty())
                verifyMessageInput.setText(messageInput.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo khóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSignMessage() {
        try {
            String message = messageInput.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập văn bản cần ký.", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (currentKeyPair == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng tạo khóa trước khi ký.", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            currentSignature = algorithm.sign(message);
            hashDisplay.setText(formatHashDisplay(currentSignature, message));
            signatureDisplay.setText(formatSignatureForDisplay(currentSignature));
            signStatusLabel.setText("✓ Đã ký văn bản thành công");
            signStatusLabel.setForeground(TEAL);

            verifyMessageInput.setText(message);
            verifySignatureInput.setText(formatSignatureForDisplay(currentSignature));
            verifyPublicKeyDisplay.setText(formatPublicKeyForDisplay(currentKeyPair.getPublicKey()));
            verifyStatusLabel.setText("Trạng thái: Sẵn sàng kiểm tra chữ ký");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi ký: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleVerifySignature() {
        try {
            String message = verifyMessageInput.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập văn bản để xác minh.", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (currentKeyPair == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng tạo khóa hoặc tải khóa công khai.", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            SchorrSignature signature = currentSignature;
            if (signature == null)
                signature = parseSignature(verifySignatureInput.getText());
            if (signature == null) {
                JOptionPane.showMessageDialog(this, "Không có chữ ký hợp lệ để xác minh.", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean valid = algorithm.verify(message, signature);
            if (valid) {
                verificationResultLabel.setText("  ✓  VĂN BẢN VÀ CHỮ KÝ HỢP LỆ  ");
                verificationResultLabel.setBackground(TEAL);
                verificationResultLabel.setForeground(Color.WHITE);
                verifyStatusLabel.setText("✓ Đã xác minh xong — Hợp lệ");
                verifyStatusLabel.setForeground(TEAL);
            } else {
                verificationResultLabel.setText("  ✗  VĂN BẢN ĐÃ BỊ SỬA ĐỔI HOẶC CHỮ KÝ SAI  ");
                verificationResultLabel.setBackground(DANGER);
                verificationResultLabel.setForeground(Color.WHITE);
                verifyStatusLabel.setText("✗ Phát hiện lỗi bảo mật");
                verifyStatusLabel.setForeground(DANGER);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xác minh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTextInto(JTextArea target) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt", "md", "log"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                target.setText(Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Không thể tải tệp: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTextFrom(JTextArea source, String defaultFileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultFileName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(chooser.getSelectedFile().toPath(), source.getText(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Không thể lưu tệp: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveKeyPairToFile() {
        if (currentKeyPair == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo khóa trước.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextArea temp = new JTextArea();
        temp.setText("P = " + currentKeyPair.getParams().getP() + "\nQ = " + currentKeyPair.getParams().getQ() +
                "\nG = " + currentKeyPair.getParams().getG() + "\n\nx = " + currentKeyPair.getPrivateKey() +
                "\ny = " + currentKeyPair.getPublicKey());
        saveTextFrom(temp, "schorr_keypair.txt");
    }

    private String buildKeySummary(SchorrKeyPair kp) {
        return "THÔNG SỐ BẢO MẬT (Schnorr):\n" +
                "──────────────────────────\n" +
                "• P bits : " + kp.getParams().getP().bitLength() + "\n" +
                "• Q bits : " + kp.getParams().getQ().bitLength() + "\n" +
                "• G bits : " + kp.getParams().getG().bitLength() + "\n" +
                "• X bits : " + kp.getPrivateKey().bitLength() + "  (Khóa bí mật)\n" +
                "• Y bits : " + kp.getPublicKey().bitLength() + "  (Khóa công khai)";
    }

    private String formatBigInteger(BigInteger v) {
        return "HEX:\n" + v.toString(16).toUpperCase() + "\n\nDECIMAL:\n" + v;
    }

    private String formatPublicKeyForDisplay(BigInteger pk) {
        return "y = " + pk;
    }

    private String formatSignatureForDisplay(SchorrSignature sig) {
        return "s = " + sig.getS() + "\ne = " + sig.getE();
    }

    private String formatHashDisplay(SchorrSignature sig, String msg) {
        return "SHA-256 của thông điệp đã ký:\n[" + msg + "]\n\nMã băm e = " + sig.getE();
    }

    private SchorrSignature parseSignature(String text) {
        Matcher sm = Pattern.compile("s\\s*=\\s*([0-9]+)", Pattern.CASE_INSENSITIVE).matcher(text);
        Matcher em = Pattern.compile("e\\s*=\\s*([0-9]+)", Pattern.CASE_INSENSITIVE).matcher(text);
        if (sm.find() && em.find())
            return new SchorrSignature(new BigInteger(sm.group(1)), new BigInteger(em.group(1)));
        return null;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // THEME
    // ════════════════════════════════════════════════════════════════════════════
    private void applyTheme(boolean dark) {
        darkMode = dark;
        if (dark) {
            APP_BG = new Color(15, 17, 23);
            PANEL_BG = new Color(22, 26, 34);
            CARD_BG = new Color(30, 35, 45);
            CARD_BORDER = new Color(55, 62, 78);
            TEXT = new Color(210, 218, 230);
            MUTED = new Color(110, 125, 142);
            HEADER_BG1 = new Color(20, 24, 32);
            HEADER_BG2 = new Color(30, 22, 14);
            TITLE_CLR = new Color(240, 210, 90);
            SUBTITLE_CLR = new Color(130, 145, 162);
            SCROLLBAR_CLR = new Color(70, 80, 95);
        } else {
            APP_BG = new Color(232, 236, 242);
            PANEL_BG = new Color(248, 249, 252);
            CARD_BG = Color.WHITE;
            CARD_BORDER = new Color(208, 215, 224);
            TEXT = new Color(28, 34, 46);
            MUTED = new Color(105, 118, 135);
            HEADER_BG1 = new Color(255, 255, 255);
            HEADER_BG2 = new Color(255, 252, 240);
            TITLE_CLR = new Color(30, 30, 30);
            SUBTITLE_CLR = new Color(110, 120, 132);
            SCROLLBAR_CLR = new Color(190, 198, 210);
        }
    }

    private void refreshTheme() {
        if (mainPanel != null)
            mainPanel.repaint();
        if (headerPanel instanceof GradientPanel gp) {
            gp.setColors(HEADER_BG1, HEADER_BG2);
        }
        if (headerTitleLabel != null)
            headerTitleLabel.setForeground(TITLE_CLR);
        if (headerSubtitleLabel != null)
            headerSubtitleLabel.setForeground(SUBTITLE_CLR);
        if (themeToggle != null) {
            themeToggle.setSelected(darkMode);
            themeToggle.repaint();
        }
        if (verificationResultLabel != null && verificationResultLabel.getText().equals("CHƯA XÁC MINH")) {
            verificationResultLabel.setBackground(CARD_BG);
            verificationResultLabel.setForeground(MUTED);
        }
        refreshTree(mainPanel);
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    private void refreshTree(Component c) {
        if (c == null)
            return;
        if (c instanceof JTextArea a) {
            a.setBackground(CARD_BG);
            a.setForeground(TEXT);
            a.setCaretColor(TEXT);
        } else if (c instanceof JScrollPane sp) {
            sp.getViewport().setBackground(CARD_BG);
            sp.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
            styleScrollBar(sp);
        } else if (c instanceof JPanel p) {
            if (p != headerPanel)
                p.setBackground(PANEL_BG);
        } else if (c instanceof JLabel l && l != headerTitleLabel && l != headerSubtitleLabel
                && l != verificationResultLabel) {
            if (l == keyStatusLabel || l == signStatusLabel || l == verifyStatusLabel) {
                if (l.getForeground().equals(TEAL) || l.getForeground().equals(TEAL_DARK))
                    return;
                l.setForeground(MUTED);
            } else {
                l.setForeground(TEXT);
            }
        }
        if (c instanceof Container ct)
            for (Component ch : ct.getComponents())
                refreshTree(ch);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // FACTORY HELPERS
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel makeTabPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(PANEL_BG);
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return p;
    }

    private JPanel makeCard(String title, Component content) {
        ShadowCard card = new ShadowCard(CARD_BG, CARD_BORDER);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(CARD_BORDER, 12, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(GOLD);

        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        sep.setBackground(CARD_BORDER);

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(lbl, BorderLayout.NORTH);
        top.add(sep, BorderLayout.SOUTH);

        card.add(top, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeCard(String title, JPanel content) {
        ShadowCard card = new ShadowCard(CARD_BG, CARD_BORDER);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(CARD_BORDER, 12, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(GOLD);
        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(lbl, BorderLayout.NORTH);
        top.add(sep, BorderLayout.SOUTH);
        card.add(top, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JTextArea makeCodeArea(int rows) {
        JTextArea a = new JTextArea(rows, 20);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(CODE_FONT);
        a.setBackground(CARD_BG);
        a.setForeground(TEXT);
        a.setCaretColor(TEXT);
        a.setMargin(new Insets(6, 8, 6, 8));
        a.setSelectionColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 90));
        return a;
    }

    private JTextArea makeEditableArea(int rows) {
        JTextArea a = makeCodeArea(rows);
        a.setEditable(true);
        return a;
    }

    private JScrollPane wrapScroll(JTextArea a) {
        JScrollPane sp = new JScrollPane(a);
        sp.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        sp.getViewport().setBackground(CARD_BG);
        styleScrollBar(sp);
        return sp;
    }

    private void styleScrollBar(JScrollPane sp) {
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getVerticalScrollBar().setBackground(CARD_BG);
    }

    private JLabel makeFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT);
        return l;
    }

    private JLabel makeStatusLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(STATUS_FONT);
        l.setForeground(MUTED);
        return l;
    }

    private JLabel sectionHeading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 14));
        l.setForeground(GOLD);
        return l;
    }

    /** Flat gradient button with hover effect */
    private JButton makeBtn(String text, Color base, Color hover) {
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
                Color c1 = hovered ? hover : base;
                Color c2 = hovered ? base : base.darker();
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // subtle top highlight
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10);
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(base.darker());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        btn.setFont(BTN_FONT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(168, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ════════════════════════════════════════════════════════════════════════════

    /** Gradient panel helper */
    static class GradientPanel extends JPanel {
        private Color c1, c2;

        GradientPanel(Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;
            setOpaque(false);
        }

        void setColors(Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
        }
    }

    /** Rounded border */
    static class RoundBorder implements javax.swing.border.Border {
        private final Color color;
        private final int arc, thick;

        RoundBorder(Color color, int arc, int thick) {
            this.color = color;
            this.arc = arc;
            this.thick = thick;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thick));
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thick, thick, thick, thick);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /** Card panel with subtle drop shadow */
    class ShadowCard extends JPanel {
        ShadowCard(Color bg, Color border) {
            setBackground(bg);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // shadow
            for (int i = 4; i > 0; i--) {
                g2.setColor(new Color(0, 0, 0, darkMode ? 40 - i * 6 : 12 - i * 2));
                g2.fillRoundRect(i, i + 1, getWidth() - i, getHeight() - i, 12, 12);
            }
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Slim custom scrollbar */
    static class ThinScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(150, 160, 175);
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int o) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int o) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 6, 6);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        }
    }

    /** Modern toggle switch */
    class ToggleSwitch extends JComponent {
        private boolean selected;
        private final List<ActionListener> listeners = new ArrayList<>();

        ToggleSwitch(boolean init) {
            selected = init;
            setPreferredSize(new Dimension(50, 26));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    setSelected(!selected);
                    fireAction();
                }
            });
        }

        void addActionListener(ActionListener l) {
            listeners.add(l);
        }

        private void fireAction() {
            ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, selected ? "ON" : "OFF");
            listeners.forEach(l -> l.actionPerformed(ev));
        }

        void setSelected(boolean s) {
            selected = s;
            repaint();
        }

        boolean isSelected() {
            return selected;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            // track
            Color track = selected ? GOLD : (darkMode ? new Color(70, 78, 92) : new Color(190, 198, 210));
            g2.setColor(track);
            g2.fillRoundRect(0, 0, w, h, h, h);
            // knob shadow
            int ks = h - 6, kx = selected ? w - ks - 3 : 3, ky = 3;
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fillOval(kx + 1, ky + 2, ks, ks);
            // knob
            g2.setColor(Color.WHITE);
            g2.fillOval(kx, ky, ks, ks);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(SchorrGUI::new);
    }
}