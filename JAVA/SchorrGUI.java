import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện GUI kiểu tab cho thuật toán Schnorr - Đã được làm đẹp theo phong
 * cách hiện đại.
 */
public class SchorrGUI extends JFrame {
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

    private JTextArea pDisplay;
    private JTextArea qDisplay;
    private JTextArea gDisplay;
    private JTextArea privateKeyDisplay;
    private JTextArea publicKeyDisplay;
    private JTextArea keySummaryDisplay;

    private JTextArea messageInput;
    private JTextArea hashDisplay;
    private JTextArea signatureDisplay;

    private JTextArea verifyMessageInput;
    private JTextArea verifySignatureInput;
    private JTextArea verifyPublicKeyDisplay;

    private JTextArea instructionArea;

    private JButton generateKeysButton;
    private JButton randomKeysButton;
    private JButton saveKeyButton;
    private JButton signMessageButton;
    private JButton loadMessageButton;
    private JButton saveMessageButton;
    private JButton saveSignatureButton;
    private JButton verifyButton;
    private JButton loadVerifyMessageButton;
    private JButton loadVerifySignatureButton;
    private JButton loadPublicKeyButton;

    private SchorrSignatureAlgorithm algorithm;
    private SchorrKeyPair currentKeyPair;
    private SchorrSignature currentSignature;

    private boolean darkMode;

    // Sử dụng Font hiện đại hơn
    private final String FONT_FAMILY = "Segoe UI";
    private final Font titleFont = new Font(FONT_FAMILY, Font.BOLD, 22);
    private final Font labelFont = new Font(FONT_FAMILY, Font.BOLD, 13);
    private final Font bodyFont = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private final Font textFont = new Font("Consolas", Font.PLAIN, 12); // Font code cho key/hash

    // Bảng màu hiện đại
    private final Color accentBlue = new Color(52, 152, 219);
    private final Color accentBlueHover = new Color(41, 128, 185);
    private final Color successGreen = new Color(46, 204, 113);
    private final Color successGreenHover = new Color(39, 174, 96);
    private final Color dangerRed = new Color(231, 76, 60);
    private final Color dangerRedHover = new Color(192, 57, 43);
    private final Color neutralButton = new Color(149, 165, 166);
    private final Color neutralButtonHover = new Color(127, 140, 141);

    private Color appBackground;
    private Color panelBackground;
    private Color cardBackground;
    private Color textColor;
    private Color mutedTextColor;
    private Color titleBackgroundColor;
    private Color titleTextColor;
    private Color subtitleTextColor;
    private Color borderColor;

    public SchorrGUI() {
        algorithm = new SchorrSignatureAlgorithm();
        applyTheme(false);

        setTitle("Phần Mềm Chữ Ký Điện Tử Schnorr");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 750);
        setLocationRelativeTo(null);
        setContentPane(createMainPanel());
        setVisible(true);
    }

    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        mainPanel.setBackground(appBackground);

        headerPanel = createHeaderPanel();
        tabbedPane = createTabbedPane();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(titleBackgroundColor);
        // Bo góc nhẹ cho header (mô phỏng bằng border)
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        headerTitleLabel = new JLabel("Chữ Ký Điện Tử Schnorr");
        headerTitleLabel.setFont(titleFont);
        headerTitleLabel.setForeground(titleTextColor);

        headerSubtitleLabel = new JLabel("Hệ thống mô phỏng sinh khóa, ký và xác minh thông điệp");
        headerSubtitleLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        headerSubtitleLabel.setForeground(subtitleTextColor);

        titleBox.add(headerTitleLabel);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(headerSubtitleLabel);

        themeToggle = new ToggleSwitch(darkMode);
        themeToggle.addActionListener(e -> {
            applyTheme(themeToggle.isSelected());
            refreshTheme();
        });

        JPanel themeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        themeBox.setOpaque(false);
        JLabel themeLabel = new JLabel("Giao diện tối: ");
        themeLabel.setFont(bodyFont);
        themeLabel.setForeground(subtitleTextColor);
        themeBox.add(themeLabel);
        themeBox.add(themeToggle);

        panel.add(titleBox, BorderLayout.WEST);
        panel.add(themeBox, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tạo khóa", createKeyTab());
        tabs.addTab("Ký văn bản", createSignTab());
        tabs.addTab("Xác minh", createVerifyTab());
        tabs.addTab("Hướng dẫn", createHelpTab());
        tabs.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        tabs.setFocusable(false);
        return tabs;
    }

    private JPanel createKeyTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(panelBackground);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel note = new JLabel("Thiết lập tham số Schnorr và Cặp khóa bảo mật");
        note.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
        note.setForeground(accentBlue);

        JPanel center = new JPanel(new GridLayout(1, 2, 20, 20));
        center.setOpaque(false);
        center.add(createParameterCard());
        center.add(createKeyOutputCard());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.setOpaque(false);
        generateKeysButton = createActionButton("Tạo khóa tự động", accentBlue, accentBlueHover);
        randomKeysButton = createActionButton("Tạo khóa ngẫu nhiên", neutralButton, neutralButtonHover);
        saveKeyButton = createActionButton("Lưu khóa ra tệp", successGreen, successGreenHover);

        generateKeysButton.addActionListener(e -> handleGenerateKeys());
        randomKeysButton.addActionListener(e -> handleGenerateKeys());
        saveKeyButton.addActionListener(e -> saveKeyPairToFile());

        buttons.add(generateKeysButton);
        buttons.add(randomKeysButton);
        buttons.add(saveKeyButton);

        keyStatusLabel = createStatusLabel("Trạng thái: Chưa tạo khóa");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(note, BorderLayout.WEST);
        topPanel.add(keyStatusLabel, BorderLayout.EAST);

        tab.add(topPanel, BorderLayout.NORTH);
        tab.add(center, BorderLayout.CENTER);
        tab.add(buttons, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel createParameterCard() {
        JPanel card = createCardPanel("Tham số P, Q, G");
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setOpaque(false);

        pDisplay = createReadOnlyArea(2);
        qDisplay = createReadOnlyArea(2);
        gDisplay = createReadOnlyArea(2);

        form.add(createFieldLabel("Số nguyên tố P:"));
        form.add(wrapScrollPane(pDisplay));
        form.add(createFieldLabel("Số nguyên tố Q:"));
        form.add(wrapScrollPane(qDisplay));
        form.add(createFieldLabel("Phần tử sinh G:"));
        form.add(wrapScrollPane(gDisplay));

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel createKeyOutputCard() {
        JPanel card = createCardPanel("Khóa điện tử Schnorr");
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setOpaque(false);

        privateKeyDisplay = createReadOnlyArea(3);
        publicKeyDisplay = createReadOnlyArea(3);
        keySummaryDisplay = createReadOnlyArea(5);

        form.add(createFieldLabel("Khóa bí mật (x):"));
        form.add(wrapScrollPane(privateKeyDisplay));
        form.add(createFieldLabel("Khóa công khai (y):"));
        form.add(wrapScrollPane(publicKeyDisplay));
        form.add(createFieldLabel("Tóm tắt độ dài bit:"));
        form.add(wrapScrollPane(keySummaryDisplay));

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSignTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(panelBackground);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topButtons.setOpaque(false);
        loadMessageButton = createActionButton("Tải văn bản", neutralButton, neutralButtonHover);
        saveMessageButton = createActionButton("Lưu văn bản", neutralButton, neutralButtonHover);
        saveSignatureButton = createActionButton("Xuất chữ ký", successGreen, successGreenHover);

        loadMessageButton.addActionListener(e -> loadTextInto(messageInput));
        saveMessageButton.addActionListener(e -> saveTextFrom(messageInput, "vanban.txt"));
        saveSignatureButton.addActionListener(e -> saveTextFrom(signatureDisplay, "chuky.txt"));

        topButtons.add(loadMessageButton);
        topButtons.add(saveMessageButton);
        topButtons.add(saveSignatureButton);

        JPanel center = new JPanel(new GridLayout(3, 1, 15, 15));
        center.setOpaque(false);

        messageInput = createEditableArea(7);
        hashDisplay = createReadOnlyArea(5);
        signatureDisplay = createReadOnlyArea(5);

        center.add(createAreaCard("Văn bản cần ký", messageInput));
        center.add(createAreaCard("Kết quả băm SHA-256", hashDisplay));
        center.add(createAreaCard("Chữ ký số tạo ra", signatureDisplay));

        signMessageButton = createActionButton("Thực Hiện Ký", accentBlue, accentBlueHover);
        signMessageButton.setPreferredSize(new Dimension(200, 45));
        signMessageButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
        signMessageButton.addActionListener(e -> handleSignMessage());

        signStatusLabel = createStatusLabel("Trạng thái: Chưa ký văn bản");

        JPanel bottom = new JPanel(new BorderLayout(15, 15));
        bottom.setOpaque(false);
        bottom.add(signStatusLabel, BorderLayout.WEST);

        JPanel signBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        signBtnPanel.setOpaque(false);
        signBtnPanel.add(signMessageButton);
        bottom.add(signBtnPanel, BorderLayout.EAST);

        tab.add(topButtons, BorderLayout.NORTH);
        tab.add(center, BorderLayout.CENTER);
        tab.add(bottom, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel createVerifyTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(panelBackground);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topButtons.setOpaque(false);
        loadVerifyMessageButton = createActionButton("Tải văn bản", neutralButton, neutralButtonHover);
        loadVerifySignatureButton = createActionButton("Tải chữ ký", neutralButton, neutralButtonHover);
        loadPublicKeyButton = createActionButton("Tải khóa công khai", neutralButton, neutralButtonHover);

        loadVerifyMessageButton.addActionListener(e -> loadTextInto(verifyMessageInput));
        loadVerifySignatureButton.addActionListener(e -> loadTextInto(verifySignatureInput));
        loadPublicKeyButton.addActionListener(e -> {
            if (currentKeyPair != null) {
                verifyPublicKeyDisplay.setText(formatPublicKeyForDisplay(currentKeyPair.getPublicKey()));
            }
        });
        topButtons.add(loadVerifyMessageButton);
        topButtons.add(loadVerifySignatureButton);
        topButtons.add(loadPublicKeyButton);

        JPanel center = new JPanel(new GridLayout(3, 1, 15, 15));
        center.setOpaque(false);

        verifyMessageInput = createEditableArea(6);
        verifySignatureInput = createEditableArea(5);
        verifyPublicKeyDisplay = createReadOnlyArea(5);

        center.add(createAreaCard("Nội dung văn bản", verifyMessageInput));
        center.add(createAreaCard("Dữ liệu chữ ký", verifySignatureInput));
        center.add(createAreaCard("Khóa công khai người gửi", verifyPublicKeyDisplay));

        verifyButton = createActionButton("Xác Minh Chữ Ký", accentBlue, accentBlueHover);
        verifyButton.setPreferredSize(new Dimension(200, 45));
        verifyButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
        verifyButton.addActionListener(e -> handleVerifySignature());

        verifyStatusLabel = createStatusLabel("Tiến trình: Đang chờ xác minh");
        verificationResultLabel = new JLabel("CHƯA XÁC MINH", SwingConstants.CENTER);
        verificationResultLabel.setOpaque(true);
        verificationResultLabel.setPreferredSize(new Dimension(0, 50));
        verificationResultLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        verificationResultLabel.setBackground(cardBackground);
        verificationResultLabel.setForeground(mutedTextColor);
        // Bo góc nhẹ cho kết quả
        verificationResultLabel.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));

        JPanel bottomCenter = new JPanel(new BorderLayout(10, 10));
        bottomCenter.setOpaque(false);
        bottomCenter.add(verifyStatusLabel, BorderLayout.NORTH);
        bottomCenter.add(verificationResultLabel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(15, 15));
        bottom.setOpaque(false);
        bottom.add(bottomCenter, BorderLayout.CENTER);

        JPanel verifyBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 15));
        verifyBtnPanel.setOpaque(false);
        verifyBtnPanel.add(verifyButton);
        bottom.add(verifyBtnPanel, BorderLayout.EAST);

        tab.add(topButtons, BorderLayout.NORTH);
        tab.add(center, BorderLayout.CENTER);
        tab.add(bottom, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel createHelpTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(panelBackground);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        instructionArea = createReadOnlyArea(18);
        instructionArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, 15));
        instructionArea.setText(
                "HƯỚNG DẪN SỬ DỤNG PHẦN MỀM CHỮ KÝ SỐ SCHNORR:\n" +
                        "──────────────────────────────────────────────────────────\n\n" +
                        "1. Tab Tạo khóa (Key Generation)\n" +
                        "   • Nhấn 'Tạo khóa tự động' để hệ thống sinh các tham số p, q, g và cặp khóa bí mật/công khai.\n"
                        +
                        "   • Nhấn 'Lưu khóa ra tệp' để xuất thông tin khóa phòng trường hợp cần dùng lại.\n\n" +
                        "2. Tab Ký văn bản (Signing)\n" +
                        "   • Nhập hoặc tải lên thông điệp bạn muốn ký vào ô 'Văn bản cần ký'.\n" +
                        "   • Nhấn 'Thực Hiện Ký'. Hệ thống sẽ dùng khóa bí mật (x) để tạo ra bộ chữ ký (s, e).\n" +
                        "   • Bạn có thể lưu chữ ký này thành file txt để gửi cho người nhận.\n\n" +
                        "3. Tab Xác minh (Verification)\n" +
                        "   • Nhập văn bản gốc và chữ ký nhận được vào các ô tương ứng.\n" +
                        "   • Đảm bảo khóa công khai của người gửi (y) đã được tải.\n" +
                        "   • Nhấn 'Xác Minh Chữ Ký'. Nếu hợp lệ, hệ thống sẽ báo màu xanh, ngược lại là màu đỏ.\n\n" +
                        "4. Tuỳ chỉnh Giao diện\n" +
                        "   • Sử dụng công tắc 'Giao diện tối' ở góc phải phía trên để đổi nền sang Dark Mode bảo vệ mắt.\n");

        tab.add(createAreaCard("Tài liệu hướng dẫn", instructionArea), BorderLayout.CENTER);
        return tab;
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                " " + title + " ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                labelFont,
                accentBlue);
        card.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        card.setBackground(panelBackground);
        return card;
    }

    private JPanel createAreaCard(String title, JTextArea area) {
        JPanel card = createCardPanel(title);
        card.add(wrapScrollPane(area), BorderLayout.CENTER);
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(textColor);
        return label;
    }

    private JTextArea createReadOnlyArea(int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(textFont);
        area.setBackground(cardBackground);
        area.setForeground(textColor);
        area.setCaretColor(textColor);
        area.setMargin(new Insets(8, 8, 8, 8)); // Thêm padding cho text
        return area;
    }

    private JTextArea createEditableArea(int rows) {
        JTextArea area = createReadOnlyArea(rows);
        area.setEditable(true);
        return area;
    }

    private JScrollPane wrapScrollPane(JTextArea area) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        scrollPane.getViewport().setBackground(cardBackground);
        // Tùy chỉnh thanh cuộn thanh thoát hơn
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        return scrollPane;
    }

    // TẠO NÚT BẤM STYLE FLAT DESIGN
    private JButton createActionButton(String text, Color defaultColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        button.setBackground(defaultColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false); // Bỏ viền Swing cũ
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(160, 36));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hiệu ứng Hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
            }
        });

        return button;
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, Font.ITALIC, 13));
        label.setForeground(mutedTextColor);
        return label;
    }

    private void applyTheme(boolean useDarkMode) {
        darkMode = useDarkMode;
        if (darkMode) {
            appBackground = new Color(24, 26, 31);
            panelBackground = new Color(34, 39, 46);
            cardBackground = new Color(45, 51, 59);
            textColor = new Color(205, 217, 229);
            mutedTextColor = new Color(118, 131, 144);
            titleBackgroundColor = new Color(34, 39, 46);
            titleTextColor = new Color(88, 166, 255);
            subtitleTextColor = new Color(118, 131, 144);
            borderColor = new Color(68, 76, 86);
        } else {
            appBackground = new Color(240, 242, 245);
            panelBackground = Color.WHITE;
            cardBackground = new Color(248, 249, 250);
            textColor = new Color(33, 37, 41);
            mutedTextColor = new Color(108, 117, 125);
            titleBackgroundColor = Color.WHITE;
            titleTextColor = accentBlue;
            subtitleTextColor = new Color(108, 117, 125);
            borderColor = new Color(222, 226, 230);
        }
    }

    private void refreshTheme() {
        if (mainPanel != null)
            mainPanel.setBackground(appBackground);
        if (headerPanel != null) {
            headerPanel.setBackground(titleBackgroundColor);
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1, true),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        }
        if (tabbedPane != null) {
            tabbedPane.setBackground(panelBackground);
            tabbedPane.setForeground(textColor);
        }
        if (headerTitleLabel != null)
            headerTitleLabel.setForeground(titleTextColor);
        if (headerSubtitleLabel != null)
            headerSubtitleLabel.setForeground(subtitleTextColor);
        if (themeToggle != null) {
            themeToggle.setSelected(darkMode);
            themeToggle.repaint();
        }

        refreshComponentTree(mainPanel);

        if (verificationResultLabel != null) {
            if (verificationResultLabel.getText().equals("CHƯA XÁC MINH")) {
                verificationResultLabel.setBackground(cardBackground);
                verificationResultLabel.setForeground(mutedTextColor);
            }
            verificationResultLabel.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        }
    }

    private void refreshComponentTree(Component component) {
        if (component == null)
            return;

        if (component instanceof JTextArea area) {
            area.setBackground(cardBackground);
            area.setForeground(textColor);
            area.setCaretColor(textColor);
        } else if (component instanceof JLabel label) {
            if (label == headerTitleLabel) {
                label.setForeground(titleTextColor);
            } else if (label == headerSubtitleLabel || label == keyStatusLabel || label == signStatusLabel
                    || label == verifyStatusLabel) {
                label.setForeground(mutedTextColor);
            } else if (label != verificationResultLabel) {
                label.setForeground(textColor);
            }
        } else if (component instanceof JPanel panel) {
            if (panel != headerPanel && panel != mainPanel) {
                panel.setBackground(panelBackground);
                // Cập nhật lại màu viền cho TitledBorder
                if (panel.getBorder() instanceof javax.swing.border.CompoundBorder) {
                    TitledBorder tb = BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(borderColor, 1, true),
                            ((TitledBorder) ((javax.swing.border.CompoundBorder) panel.getBorder()).getOutsideBorder())
                                    .getTitle(),
                            TitledBorder.LEFT, TitledBorder.TOP, labelFont, accentBlue);
                    panel.setBorder(
                            BorderFactory.createCompoundBorder(tb, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                }
            }
        } else if (component instanceof JScrollPane scrollPane) {
            scrollPane.setBackground(panelBackground);
            scrollPane.getViewport().setBackground(cardBackground);
            scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                refreshComponentTree(child);
            }
        }
    }

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
            signStatusLabel.setText("Trạng thái: Sẵn sàng ký văn bản");
            verifyStatusLabel.setText("Trạng thái: Sẵn sàng xác minh");

            verifyPublicKeyDisplay.setText(formatPublicKeyForDisplay(currentKeyPair.getPublicKey()));
            if (verifyMessageInput.getText().isEmpty()) {
                verifyMessageInput.setText(messageInput.getText());
            }
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
            signStatusLabel.setForeground(successGreen);

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
            if (signature == null) {
                signature = parseSignature(verifySignatureInput.getText());
            }
            if (signature == null) {
                JOptionPane.showMessageDialog(this, "Không có chữ ký hợp lệ để xác minh.", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean valid = algorithm.verify(message, signature);
            if (valid) {
                verificationResultLabel.setText("✓ VĂN BẢN VÀ CHỮ KÝ HỢP LỆ");
                verificationResultLabel.setBackground(successGreen);
                verificationResultLabel.setForeground(Color.WHITE);
                verifyStatusLabel.setText("Đã xác minh xong");
            } else {
                verificationResultLabel.setText("✗ VĂN BẢN ĐÃ BỊ SỬA ĐỔI HOẶC CHỮ KÝ SAI");
                verificationResultLabel.setBackground(dangerRed);
                verificationResultLabel.setForeground(Color.WHITE);
                verifyStatusLabel.setText("Phát hiện lỗi bảo mật");
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
                Path path = chooser.getSelectedFile().toPath();
                target.setText(Files.readString(path, StandardCharsets.UTF_8));
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
                Path path = chooser.getSelectedFile().toPath();
                Files.writeString(path, source.getText(), StandardCharsets.UTF_8);
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
        temp.setText("P = " + currentKeyPair.getParams().getP() + "\n" +
                "Q = " + currentKeyPair.getParams().getQ() + "\n" +
                "G = " + currentKeyPair.getParams().getG() + "\n\n" +
                "x = " + currentKeyPair.getPrivateKey() + "\n" +
                "y = " + currentKeyPair.getPublicKey());
        saveTextFrom(temp, "schorr_keypair.txt");
    }

    private String buildKeySummary(SchorrKeyPair keyPair) {
        return "THÔNG SỐ BẢO MẬT (Schnorr):\n" +
                "---------------------------\n" +
                "• P bits: " + keyPair.getParams().getP().bitLength() + "\n" +
                "• Q bits: " + keyPair.getParams().getQ().bitLength() + "\n" +
                "• G bits: " + keyPair.getParams().getG().bitLength() + "\n" +
                "• X bits: " + keyPair.getPrivateKey().bitLength() + " (Khóa bí mật)\n" +
                "• Y bits: " + keyPair.getPublicKey().bitLength() + " (Khóa công khai)";
    }

    private String formatBigInteger(BigInteger value) {
        return "HEX:\n" + value.toString(16).toUpperCase() + "\n\nDECIMAL:\n" + value.toString();
    }

    private String formatPublicKeyForDisplay(BigInteger publicKey) {
        return "y = " + publicKey.toString();
    }

    private String formatSignatureForDisplay(SchorrSignature signature) {
        return "s = " + signature.getS().toString() + "\n" +
                "e = " + signature.getE().toString();
    }

    private String formatHashDisplay(SchorrSignature signature, String message) {
        return "SHA-256 của thông điệp đã ký:\n" +
                "[" + message + "]\n\n" +
                "Mã băm e = " + signature.getE().toString();
    }

    private SchorrSignature parseSignature(String text) {
        Matcher sMatcher = Pattern.compile("s\\s*=\\s*([0-9]+)", Pattern.CASE_INSENSITIVE).matcher(text);
        Matcher eMatcher = Pattern.compile("e\\s*=\\s*([0-9]+)", Pattern.CASE_INSENSITIVE).matcher(text);
        if (sMatcher.find() && eMatcher.find()) {
            return new SchorrSignature(new BigInteger(sMatcher.group(1)), new BigInteger(eMatcher.group(1)));
        }
        return null;
    }

    /**
     * ToggleSwitch: Công tắc UI được làm mượt mà hơn.
     */
    private class ToggleSwitch extends JComponent {
        private boolean selected = false;
        private final List<ActionListener> listeners = new ArrayList<>();

        public ToggleSwitch(boolean initial) {
            selected = initial;
            setPreferredSize(new Dimension(50, 26));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelected(!selected);
                    fireActionEvent();
                }
            });
        }

        public void addActionListener(ActionListener l) {
            listeners.add(l);
        }

        private void fireActionEvent() {
            ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, selected ? "ON" : "OFF");
            for (ActionListener l : listeners)
                l.actionPerformed(ev);
        }

        public void setSelected(boolean s) {
            selected = s;
            repaint();
        }

        public boolean isSelected() {
            return selected;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int arc = h;

            Color trackOn = accentBlue;
            Color trackOff = new Color(200, 200, 200);
            if (darkMode)
                trackOff = new Color(90, 93, 101);

            g2.setColor(selected ? trackOn : trackOff);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            int knobSize = h - 6;
            int knobY = 3;
            int knobX = selected ? w - knobSize - 3 : 3;

            g2.setColor(Color.WHITE);
            g2.fillOval(knobX, knobY, knobSize, knobSize);

            // Bóng nhẹ cho nút tròn
            g2.setColor(new Color(0, 0, 0, 30));
            g2.drawOval(knobX, knobY, knobSize, knobSize);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            // Loại bỏ Look And Feel mặc định của hệ thống để đồng bộ toàn bộ Flat UI thủ
            // công
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(SchorrGUI::new);
    }
}