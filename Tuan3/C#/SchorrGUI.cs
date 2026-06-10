using System;
Local comment
Comment on line R1


using System.Drawing;
using System.IO;
using System.Numerics;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows.Forms;

namespace Schnorr
{
    public class SchorrForm : Form
    {
        private readonly SchorrSignatureAlgorithm algorithm = new SchorrSignatureAlgorithm();
        private SchorrKeyPair currentKeyPair;
        private SchorrSignature currentSignature;

        private readonly Font TITLE_FONT = new Font("Serif", 22, FontStyle.Bold);
        private readonly Font SUBTITLE_FONT = new Font("Serif", 13, FontStyle.Italic);
        private readonly Font LABEL_FONT = new Font("Segoe UI", 9, FontStyle.Bold);
        private readonly Font BODY_FONT = new Font("Segoe UI", 10, FontStyle.Regular);
        private readonly Font CODE_FONT = new Font("Consolas", 9, FontStyle.Regular);
        private readonly Font TAB_FONT = new Font("Segoe UI", 10, FontStyle.Bold);
        private readonly Font BTN_FONT = new Font("Segoe UI", 9, FontStyle.Bold);
        private readonly Font STATUS_FONT = new Font("Segoe UI", 9, FontStyle.Italic);

        private Color APP_BG, PANEL_BG, CARD_BG, CARD_BORDER, TEXT, MUTED;
        private Color HEADER_BG1, HEADER_BG2, TITLE_CLR, SUBTITLE_CLR, SCROLLBAR_CLR;
        private Color GOLD, GOLD_LIGHT, TEAL, TEAL_DARK, DANGER, DANGER_DARK, NEUTRAL, NEUTRAL_DARK;
        private bool darkMode;

        private GradientPanel headerPanel;
        private Label headerTitleLabel, headerSubtitleLabel;
        private Label keyStatusLabel, signStatusLabel, verifyStatusLabel, verificationResultLabel;
        private ToggleSwitch toggleDark;
        private StyledTabControl tabs;

        private TextBox pDisplay, qDisplay, gDisplay;
        private TextBox privateKeyDisplay, publicKeyDisplay, keySummaryDisplay;
        private TextBox messageInput, hashDisplay, signatureDisplay;
        private TextBox verifyMessageInput, verifySignatureInput, verifyPublicKeyDisplay;
        private TextBox instructionArea;

        public SchorrForm()
        {
            Text = "Phần Mềm Chữ Ký Điện Tử Schnorr";
            Size = new Size(1080, 780);
            MinimumSize = new Size(960, 680);
            StartPosition = FormStartPosition.CenterScreen;
            SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer, true);

            SetPalette(false);
            InitializeComponents();
        }

        private void SetPalette(bool dark)
        {
            GOLD = Color.FromArgb(212, 175, 55);
            GOLD_LIGHT = Color.FromArgb(240, 210, 90);
            TEAL = Color.FromArgb(32, 201, 151);
            TEAL_DARK = Color.FromArgb(22, 160, 115);
            DANGER = Color.FromArgb(220, 75, 75);
            DANGER_DARK = Color.FromArgb(175, 45, 45);
            NEUTRAL = Color.FromArgb(100, 116, 139);
            NEUTRAL_DARK = Color.FromArgb(71, 85, 105);

            if (dark)
            {
                APP_BG = Color.FromArgb(15, 17, 23);
                PANEL_BG = Color.FromArgb(22, 26, 34);
                CARD_BG = Color.FromArgb(30, 35, 45);
                CARD_BORDER = Color.FromArgb(55, 62, 78);
                TEXT = Color.FromArgb(210, 218, 230);
                MUTED = Color.FromArgb(110, 125, 142);
                HEADER_BG1 = Color.FromArgb(20, 24, 32);
                HEADER_BG2 = Color.FromArgb(30, 22, 14);
                TITLE_CLR = GOLD_LIGHT;
                SUBTITLE_CLR = Color.FromArgb(130, 145, 162);
                SCROLLBAR_CLR = Color.FromArgb(70, 80, 95);
            }
            else
            {
                APP_BG = Color.FromArgb(232, 236, 242);
                PANEL_BG = Color.FromArgb(248, 249, 252);
                CARD_BG = Color.White;
                CARD_BORDER = Color.FromArgb(208, 215, 224);
                TEXT = Color.FromArgb(28, 34, 46);
                MUTED = Color.FromArgb(105, 118, 135);
                HEADER_BG1 = Color.White;
                HEADER_BG2 = Color.FromArgb(255, 252, 240);
                TITLE_CLR = Color.FromArgb(30, 30, 30);
                SUBTITLE_CLR = Color.FromArgb(110, 120, 132);
                SCROLLBAR_CLR = Color.FromArgb(190, 198, 210);
            }
        }

        private void ApplyTheme(bool dark)
        {
            var saved = new
            {
                selectedTab = tabs?.SelectedIndex ?? 0,
                p = pDisplay?.Text,
                q = qDisplay?.Text,
                g = gDisplay?.Text,
                privateKey = privateKeyDisplay?.Text,
                publicKey = publicKeyDisplay?.Text,
                keySummary = keySummaryDisplay?.Text,
                message = messageInput?.Text,
                hash = hashDisplay?.Text,
                signature = signatureDisplay?.Text,
                verifyMessage = verifyMessageInput?.Text,
                verifySignature = verifySignatureInput?.Text,
                verifyPublicKey = verifyPublicKeyDisplay?.Text,
                instruction = instructionArea?.Text,
                keyStatus = keyStatusLabel?.Text,
                signStatus = signStatusLabel?.Text,
                verifyStatus = verifyStatusLabel?.Text,
                resultText = verificationResultLabel?.Text,
                resultBack = verificationResultLabel?.BackColor,
                resultFore = verificationResultLabel?.ForeColor
            };

            darkMode = dark;
            SetPalette(darkMode);
            Controls.Clear();
            InitializeComponents();

            if (pDisplay != null && saved.p != null) pDisplay.Text = saved.p;
            if (qDisplay != null && saved.q != null) qDisplay.Text = saved.q;
            if (gDisplay != null && saved.g != null) gDisplay.Text = saved.g;
            if (privateKeyDisplay != null && saved.privateKey != null) privateKeyDisplay.Text = saved.privateKey;
            if (publicKeyDisplay != null && saved.publicKey != null) publicKeyDisplay.Text = saved.publicKey;
            if (keySummaryDisplay != null && saved.keySummary != null) keySummaryDisplay.Text = saved.keySummary;
            if (messageInput != null && saved.message != null) messageInput.Text = saved.message;
            if (hashDisplay != null && saved.hash != null) hashDisplay.Text = saved.hash;
            if (signatureDisplay != null && saved.signature != null) signatureDisplay.Text = saved.signature;
            if (verifyMessageInput != null && saved.verifyMessage != null) verifyMessageInput.Text = saved.verifyMessage;
            if (verifySignatureInput != null && saved.verifySignature != null) verifySignatureInput.Text = saved.verifySignature;
            if (verifyPublicKeyDisplay != null && saved.verifyPublicKey != null) verifyPublicKeyDisplay.Text = saved.verifyPublicKey;
            if (instructionArea != null && saved.instruction != null) instructionArea.Text = saved.instruction;
            if (keyStatusLabel != null && saved.keyStatus != null) RestoreStatus(keyStatusLabel, saved.keyStatus);
            if (signStatusLabel != null && saved.signStatus != null) RestoreStatus(signStatusLabel, saved.signStatus);
            if (verifyStatusLabel != null && saved.verifyStatus != null) RestoreStatus(verifyStatusLabel, saved.verifyStatus);

            if (verificationResultLabel != null && saved.resultText != null)
            {
                verificationResultLabel.Text = saved.resultText;
                if (saved.resultText == "CHƯA XÁC MINH")
                {
                    verificationResultLabel.BackColor = CARD_BG;
                    verificationResultLabel.ForeColor = MUTED;
                }
                else
                {
                    verificationResultLabel.BackColor = saved.resultBack ?? CARD_BG;
                    verificationResultLabel.ForeColor = saved.resultFore ?? MUTED;
                }
            }

            if (tabs != null)
                tabs.SelectedIndex = Math.Min(Math.Max(0, saved.selectedTab), tabs.TabCount - 1);
        }

        private void RestoreStatus(Label label, string text)
        {
            label.Text = text;
            if (text.StartsWith("✓"))
                label.ForeColor = TEAL;
            else if (text.StartsWith("✗"))
                label.ForeColor = DANGER;
            else
                label.ForeColor = MUTED;
        }

        private void InitializeComponents()
        {
            BackColor = APP_BG;
            Font = BODY_FONT;

            var root = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = APP_BG,
                ColumnCount = 1,
                RowCount = 2,
                Padding = new Padding(18, 14, 18, 18)
            };
            root.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            root.RowStyles.Add(new RowStyle(SizeType.Absolute, 108));
            root.RowStyles.Add(new RowStyle(SizeType.Percent, 100));

            headerPanel = BuildHeaderPanel();
            headerPanel.Margin = new Padding(0, 0, 0, 14);

            tabs = new StyledTabControl
            {
                Dock = DockStyle.Fill,
                Font = TAB_FONT,
                ItemSize = new Size(168, 34),
                Margin = Padding.Empty,
                SizeMode = TabSizeMode.Fixed
            };
            tabs.SetTheme(APP_BG, PANEL_BG, CARD_BG, CARD_BORDER, TEXT, MUTED, GOLD);
            tabs.TabPages.Add(CreateKeyTab());
            tabs.TabPages.Add(CreateSignTab());
            tabs.TabPages.Add(CreateVerifyTab());
            tabs.TabPages.Add(CreateHelpTab());

            root.Controls.Add(headerPanel, 0, 0);
            root.Controls.Add(tabs, 0, 1);
            Controls.Add(root);
        }

        private GradientPanel BuildHeaderPanel()
        {
            var panel = new GradientPanel(HEADER_BG1, HEADER_BG2, CARD_BORDER, 14)
            {
                Dock = DockStyle.Fill,
                Padding = new Padding(22, 16, 22, 16)
            };

            var layout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 2,
                RowCount = 1
            };
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));

            var titleBlock = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                RowCount = 2,
                ColumnCount = 1
            };
            titleBlock.RowStyles.Add(new RowStyle(SizeType.Absolute, 34));
            titleBlock.RowStyles.Add(new RowStyle(SizeType.Percent, 100));

            var accentRow = new FlowLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                FlowDirection = FlowDirection.LeftToRight,
                WrapContents = false,
                Margin = Padding.Empty,
                Padding = Padding.Empty
            };

            var bar = new Label
            {
                Text = "▐",
                Font = new Font("Segoe UI", 20, FontStyle.Bold),
                ForeColor = GOLD,
                AutoSize = true,
                Margin = new Padding(0, 0, 10, 0)
            };

            headerTitleLabel = new Label
            {
                Text = "Chữ Ký Điện Tử Schnorr",
                Font = TITLE_FONT,
                ForeColor = TITLE_CLR,
                AutoSize = true,
                Margin = Padding.Empty
            };

            headerSubtitleLabel = new Label
            {
                Text = "  Hệ thống mô phỏng sinh khóa · ký số · xác minh thông điệp",
                Font = SUBTITLE_FONT,
                ForeColor = SUBTITLE_CLR,
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleLeft
            };

            accentRow.Controls.Add(bar);
            accentRow.Controls.Add(headerTitleLabel);
            titleBlock.Controls.Add(accentRow, 0, 0);
            titleBlock.Controls.Add(headerSubtitleLabel, 0, 1);

            var rightBox = new FlowLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                FlowDirection = FlowDirection.LeftToRight,
                WrapContents = false,
                AutoSize = true,
                Padding = new Padding(0, 8, 0, 0)
            };

            var badge = new RoundedBadgeLabel
            {
                Text = "  🔐  SCHNORR v1.0  ",
                Font = new Font("Trebuchet MS", 8, FontStyle.Bold),
                ForeColor = GOLD,
                FillColor = darkMode ? Color.FromArgb(60, 50, 10) : Color.FromArgb(255, 248, 220),
                BorderColor = GOLD,
                Radius = 10,
                Size = new Size(154, 28),
                Margin = new Padding(0, 0, 12, 0),
                TextAlign = ContentAlignment.MiddleCenter
            };

            var themeLabel = new Label
            {
                Text = "Dark",
                Font = BODY_FONT,
                ForeColor = SUBTITLE_CLR,
                AutoSize = true,
                Margin = new Padding(0, 5, 8, 0)
            };

            toggleDark = new ToggleSwitch(darkMode, GOLD, darkMode ? Color.FromArgb(70, 78, 92) : SCROLLBAR_CLR)
            {
                Margin = new Padding(0, 1, 0, 0)
            };
            toggleDark.Toggled += (s, e) => ApplyTheme(toggleDark.IsOn);

            rightBox.Controls.Add(badge);
            rightBox.Controls.Add(themeLabel);
            rightBox.Controls.Add(toggleDark);

            layout.Controls.Add(titleBlock, 0, 0);
            layout.Controls.Add(rightBox, 1, 0);
            panel.Controls.Add(layout);
            return panel;
        }

        private TabPage CreateKeyTab()
        {
            var page = MakeTabPage("🔑  Tạo Khóa");
            var tab = MakeTabLayout();
            tab.RowStyles.Add(new RowStyle(SizeType.Absolute, 30));
            tab.RowStyles.Add(new RowStyle(SizeType.Percent, 100));
            tab.RowStyles.Add(new RowStyle(SizeType.Absolute, 58));

            var topRow = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 2,
                RowCount = 1,
                Margin = Padding.Empty
            };
            topRow.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 65));
            topRow.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 35));
            topRow.Controls.Add(SectionHeading("Thiết lập Tham số Schnorr & Cặp Khóa Bảo Mật"), 0, 0);
            keyStatusLabel = MakeStatusLabel("Trạng thái: Chưa tạo khóa");
            keyStatusLabel.TextAlign = ContentAlignment.MiddleRight;
            topRow.Controls.Add(keyStatusLabel, 1, 0);

            var cards = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 2,
                RowCount = 1,
                Margin = Padding.Empty
            };
            cards.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));
            cards.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));
            var paramCard = BuildParamCard();
            var keyCard = BuildKeyOutputCard();
            paramCard.Margin = new Padding(0, 0, 9, 0);
            keyCard.Margin = new Padding(9, 0, 0, 0);
            cards.Controls.Add(paramCard, 0, 0);
            cards.Controls.Add(keyCard, 1, 0);

            var buttonRow = new FlowLayoutPanel
            {
                AutoSize = true,
                Anchor = AnchorStyles.None,
                BackColor = Color.Transparent,
                FlowDirection = FlowDirection.LeftToRight,
                WrapContents = false,
                Margin = Padding.Empty,
                Padding = new Padding(0, 8, 0, 0)
            };

            var generateKeysButton = MakeButton("⚙  Tạo khóa tự động", GOLD, GOLD_LIGHT);
            var randomKeysButton = MakeButton("🎲  Khóa ngẫu nhiên", NEUTRAL, NEUTRAL_DARK);
            var saveKeyButton = MakeButton("💾  Lưu khóa ra tệp", TEAL, TEAL_DARK);
            generateKeysButton.Click += (s, e) => HandleGenerateKeys();
            randomKeysButton.Click += (s, e) => HandleGenerateKeys();
            saveKeyButton.Click += (s, e) => SaveKeyPairToFile();
            buttonRow.Controls.Add(generateKeysButton);
            buttonRow.Controls.Add(randomKeysButton);
            buttonRow.Controls.Add(saveKeyButton);

            tab.Controls.Add(topRow, 0, 0);
            tab.Controls.Add(cards, 0, 1);
            tab.Controls.Add(buttonRow, 0, 2);
            page.Controls.Add(tab);
            return page;
        }

        private ShadowCard BuildParamCard()
        {
            pDisplay = MakeCodeBox();
            qDisplay = MakeCodeBox();
            gDisplay = MakeCodeBox();

            var form = MakeTwoColumnForm();
            AddFormRow(form, 0, "Số nguyên tố P", WrapTextBox(pDisplay));
            AddFormRow(form, 1, "Số nguyên tố Q", WrapTextBox(qDisplay));
            AddFormRow(form, 2, "Phần tử sinh G", WrapTextBox(gDisplay));
            return MakeCard("Tham số P · Q · G", form);
        }

        private ShadowCard BuildKeyOutputCard()
        {
            privateKeyDisplay = MakeCodeBox();
            publicKeyDisplay = MakeCodeBox();
            keySummaryDisplay = MakeCodeBox();

            var form = MakeTwoColumnForm();
            AddFormRow(form, 0, "Khóa bí mật (x)", WrapTextBox(privateKeyDisplay));
            AddFormRow(form, 1, "Khóa công khai (y)", WrapTextBox(publicKeyDisplay));
            AddFormRow(form, 2, "Tóm tắt độ dài bit", WrapTextBox(keySummaryDisplay));
            return MakeCard("Cặp Khóa Schnorr", form);
        }

        private TabPage CreateSignTab()
        {
            var page = MakeTabPage("✍  Ký Văn Bản");
            var tab = MakeTabLayout();
            tab.RowStyles.Add(new RowStyle(SizeType.Absolute, 42));
            tab.RowStyles.Add(new RowStyle(SizeType.Percent, 100));
            tab.RowStyles.Add(new RowStyle(SizeType.Absolute, 58));

            messageInput = MakeEditableBox();
            hashDisplay = MakeCodeBox();
            signatureDisplay = MakeCodeBox();

            var toolBar = new FlowLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                FlowDirection = FlowDirection.LeftToRight,
                WrapContents = false,
                Margin = Padding.Empty
            };
            var loadMessageButton = MakeButton("📂  Tải văn bản", NEUTRAL, NEUTRAL_DARK);
            var saveMessageButton = MakeButton("💾  Lưu văn bản", NEUTRAL, NEUTRAL_DARK);
            var saveSignatureButton = MakeButton("📤  Xuất chữ ký", TEAL, TEAL_DARK);
            loadMessageButton.Click += (s, e) => LoadTextInto(messageInput);
            saveMessageButton.Click += (s, e) => SaveTextFrom(messageInput, "vanban.txt");
            saveSignatureButton.Click += (s, e) => SaveTextFrom(signatureDisplay, "chuky.txt");
            toolBar.Controls.Add(loadMessageButton);
            toolBar.Controls.Add(saveMessageButton);
            toolBar.Controls.Add(saveSignatureButton);

            var center = MakeThreeRowContent();
            center.Controls.Add(MakeCard("Văn bản cần ký", WrapTextBox(messageInput)), 0, 0);
            center.Controls.Add(MakeCard("Kết quả băm SHA-256", WrapTextBox(hashDisplay)), 0, 1);
            center.Controls.Add(MakeCard("Chữ ký số tạo ra", WrapTextBox(signatureDisplay)), 0, 2);

            var bottom = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 2,
                RowCount = 1,
                Margin = Padding.Empty
            };
            bottom.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            bottom.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 220));
            signStatusLabel = MakeStatusLabel("Trạng thái: Chưa ký văn bản");
            signStatusLabel.TextAlign = ContentAlignment.MiddleLeft;

            var signButton = MakeButton("✍  Thực Hiện Ký", GOLD, GOLD_LIGHT, new Size(200, 44), new Font("Segoe UI", 11, FontStyle.Bold));
            signButton.Anchor = AnchorStyles.Right;
            signButton.Click += (s, e) => HandleSignMessage();
            bottom.Controls.Add(signStatusLabel, 0, 0);
            bottom.Controls.Add(signButton, 1, 0);

            tab.Controls.Add(toolBar, 0, 0);
            tab.Controls.Add(center, 0, 1);
            tab.Controls.Add(bottom, 0, 2);
            page.Controls.Add(tab);
            return page;
        }

        private TabPage CreateVerifyTab()
        {
            var page = MakeTabPage("✅  Xác Minh");
            var tab = MakeTabLayout();
            tab.RowStyles.Add(new RowStyle(SizeType.Absolute, 42));
            tab.RowStyles.Add(new RowStyle(SizeType.Percent, 100));
            tab.RowStyles.Add(new RowStyle(SizeType.Absolute, 72));

            verifyMessageInput = MakeEditableBox();
            verifySignatureInput = MakeEditableBox();
            verifyPublicKeyDisplay = MakeCodeBox();

            var toolBar = new FlowLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                FlowDirection = FlowDirection.LeftToRight,
                WrapContents = false,
                Margin = Padding.Empty
            };
            var loadVerifyMessageButton = MakeButton("📂  Tải văn bản", NEUTRAL, NEUTRAL_DARK);
            var loadVerifySignatureButton = MakeButton("📂  Tải chữ ký", NEUTRAL, NEUTRAL_DARK);
            var loadPublicKeyButton = MakeButton("🔑  Tải khóa công khai", NEUTRAL, NEUTRAL_DARK);
            loadVerifyMessageButton.Click += (s, e) => LoadTextInto(verifyMessageInput);
            loadVerifySignatureButton.Click += (s, e) => LoadTextInto(verifySignatureInput);
            loadPublicKeyButton.Click += (s, e) =>
            {
                if (currentKeyPair != null)
                    verifyPublicKeyDisplay.Text = FormatPublicKeyForDisplay(currentKeyPair.PublicKey);
                else
                    MessageBox.Show(this, "Chưa có khóa công khai trong phiên hiện tại.", "Cảnh báo", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            };
            toolBar.Controls.Add(loadVerifyMessageButton);
            toolBar.Controls.Add(loadVerifySignatureButton);
            toolBar.Controls.Add(loadPublicKeyButton);

            var center = MakeThreeRowContent();
            center.Controls.Add(MakeCard("Nội dung văn bản", WrapTextBox(verifyMessageInput)), 0, 0);
            center.Controls.Add(MakeCard("Dữ liệu chữ ký", WrapTextBox(verifySignatureInput)), 0, 1);
            center.Controls.Add(MakeCard("Khóa công khai người gửi", WrapTextBox(verifyPublicKeyDisplay)), 0, 2);

            var bottom = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 2,
                RowCount = 1,
                Margin = Padding.Empty
            };
            bottom.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            bottom.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 240));

            var resultArea = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                RowCount = 2,
                ColumnCount = 1,
                Margin = Padding.Empty
            };
            resultArea.RowStyles.Add(new RowStyle(SizeType.Absolute, 22));
            resultArea.RowStyles.Add(new RowStyle(SizeType.Percent, 100));

            verifyStatusLabel = MakeStatusLabel("Tiến trình: Đang chờ xác minh");
            verificationResultLabel = new RoundedBannerLabel
            {
                Text = "CHƯA XÁC MINH",
                Font = new Font("Serif", 17, FontStyle.Bold),
                ForeColor = MUTED,
                FillColor = CARD_BG,
                BorderColor = CARD_BORDER,
                Radius = 12,
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleCenter
            };
            resultArea.Controls.Add(verifyStatusLabel, 0, 0);
            resultArea.Controls.Add(verificationResultLabel, 0, 1);

            var verifyButton = MakeButton("✅  Xác Minh Chữ Ký", GOLD, GOLD_LIGHT, new Size(220, 44), new Font("Segoe UI", 11, FontStyle.Bold));
            verifyButton.Anchor = AnchorStyles.Right;
            verifyButton.Click += (s, e) => HandleVerifySignature();
            bottom.Controls.Add(resultArea, 0, 0);
            bottom.Controls.Add(verifyButton, 1, 0);

            tab.Controls.Add(toolBar, 0, 0);
            tab.Controls.Add(center, 0, 1);
            tab.Controls.Add(bottom, 0, 2);
            page.Controls.Add(tab);
            return page;
        }

        private TabPage CreateHelpTab()
        {
            var page = MakeTabPage("📘  Hướng Dẫn");
            var tab = MakeTabLayout();
            tab.RowStyles.Add(new RowStyle(SizeType.Percent, 100));

            instructionArea = MakeCodeBox();
            instructionArea.Font = new Font("Segoe UI", 10, FontStyle.Regular);
            instructionArea.Text =
                "HƯỚNG DẪN SỬ DỤNG PHẦN MỀM CHỮ KÝ SỐ SCHNORR\r\n" +
                "══════════════════════════════════════════════════════════\r\n\r\n" +
                "① Tab Tạo Khóa (Key Generation)\r\n" +
                "   • Nhấn 'Tạo khóa tự động' để sinh tham số p, q, g và cặp khóa bí mật/công khai.\r\n" +
                "   • Nhấn 'Lưu khóa ra tệp' để xuất file .txt dùng cho các phiên sau.\r\n\r\n" +
                "② Tab Ký Văn Bản (Signing)\r\n" +
                "   • Nhập hoặc tải thông điệp vào ô 'Văn bản cần ký'.\r\n" +
                "   • Nhấn 'Thực Hiện Ký' - hệ thống dùng khóa bí mật (x) để sinh bộ chữ ký (s, e).\r\n" +
                "   • Xuất chữ ký thành .txt để gửi cho bên nhận.\r\n\r\n" +
                "③ Tab Xác Minh (Verification)\r\n" +
                "   • Nhập văn bản gốc và chữ ký nhận được vào ô tương ứng.\r\n" +
                "   • Đảm bảo khóa công khai (y) của người gửi đã được tải.\r\n" +
                "   • Nhấn 'Xác Minh Chữ Ký': banner xanh = hợp lệ, banner đỏ = lỗi/giả mạo.\r\n\r\n" +
                "④ Giao Diện Tối (Dark Mode)\r\n" +
                "   • Bật công tắc 'Dark' ở góc trên phải để chuyển sang giao diện tối bảo vệ mắt.\r\n\r\n" +
                "──────────────────────────────────────────────────────────\r\n" +
                "Lưu ý bảo mật: Không chia sẻ khóa bí mật (x) cho bất kỳ ai.\r\n" +
                "Chỉ chia sẻ khóa công khai (y) và bộ tham số (p, q, g).";

            tab.Controls.Add(MakeCard("Tài liệu Hướng Dẫn", WrapTextBox(instructionArea)), 0, 0);
            page.Controls.Add(tab);
            return page;
        }

        private TabPage MakeTabPage(string title)
        {
            return new TabPage(title)
            {
                BackColor = PANEL_BG,
                Padding = Padding.Empty,
                Margin = Padding.Empty
            };
        }

        private TableLayoutPanel MakeTabLayout()
        {
            var panel = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = PANEL_BG,
                ColumnCount = 1,
                RowCount = 3,
                Padding = new Padding(18),
                Margin = Padding.Empty
            };
            panel.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            return panel;
        }

        private TableLayoutPanel MakeTwoColumnForm()
        {
            var form = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 2,
                RowCount = 3,
                Margin = Padding.Empty
            };
            form.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 32));
            form.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 68));
            form.RowStyles.Add(new RowStyle(SizeType.Percent, 33.33f));
            form.RowStyles.Add(new RowStyle(SizeType.Percent, 33.33f));
            form.RowStyles.Add(new RowStyle(SizeType.Percent, 33.34f));
            return form;
        }

        private void AddFormRow(TableLayoutPanel form, int row, string label, Control field)
        {
            var lbl = MakeFieldLabel(label);
            lbl.Margin = new Padding(0, 0, 10, row == 2 ? 0 : 12);
            field.Margin = new Padding(0, 0, 0, row == 2 ? 0 : 12);
            form.Controls.Add(lbl, 0, row);
            form.Controls.Add(field, 1, row);
        }

        private TableLayoutPanel MakeThreeRowContent()
        {
            var center = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 1,
                RowCount = 3,
                Margin = Padding.Empty
            };
            center.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            center.RowStyles.Add(new RowStyle(SizeType.Percent, 33.33f));
            center.RowStyles.Add(new RowStyle(SizeType.Percent, 33.33f));
            center.RowStyles.Add(new RowStyle(SizeType.Percent, 33.34f));
            return center;
        }

        private ShadowCard MakeCard(string title, Control content)
        {
            var card = new ShadowCard(CARD_BG, CARD_BORDER, darkMode)
            {
                Dock = DockStyle.Fill,
                Padding = new Padding(14, 12, 14, 12),
                Margin = new Padding(0, 0, 0, 14)
            };

            var layout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.Transparent,
                ColumnCount = 1,
                RowCount = 3,
                Margin = Padding.Empty
            };
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            layout.RowStyles.Add(new RowStyle(SizeType.Absolute, 20));
            layout.RowStyles.Add(new RowStyle(SizeType.Absolute, 10));
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 100));

            var label = new Label
            {
                Text = title,
                Font = LABEL_FONT,
                ForeColor = GOLD,
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleLeft,
                Margin = Padding.Empty
            };
            var separator = new Panel
            {
                Dock = DockStyle.Top,
                Height = 1,
                BackColor = CARD_BORDER,
                Margin = new Padding(0, 5, 0, 4)
            };

            content.Dock = DockStyle.Fill;
            content.Margin = Padding.Empty;
            layout.Controls.Add(label, 0, 0);
            layout.Controls.Add(separator, 0, 1);
            layout.Controls.Add(content, 0, 2);
            card.Controls.Add(layout);
            return card;
        }

        private TextBox MakeCodeBox()
        {
            return new TextBox
            {
                Multiline = true,
                ReadOnly = true,
                WordWrap = true,
                ScrollBars = ScrollBars.Vertical,
                BorderStyle = BorderStyle.None,
                Font = CODE_FONT,
                BackColor = CARD_BG,
                ForeColor = TEXT,
                Margin = Padding.Empty
            };
        }

        private TextBox MakeEditableBox()
        {
            var tb = MakeCodeBox();
            tb.ReadOnly = false;
            return tb;
        }

        private Panel WrapTextBox(TextBox textBox)
        {
            textBox.Dock = DockStyle.Fill;
            textBox.BackColor = CARD_BG;
            textBox.ForeColor = TEXT;

            var frame = new Panel
            {
                Dock = DockStyle.Fill,
                BackColor = CARD_BORDER,
                Padding = new Padding(1),
                Margin = Padding.Empty
            };
            frame.Controls.Add(textBox);
            return frame;
        }

        private Label MakeFieldLabel(string text)
        {
            return new Label
            {
                Text = text,
                Font = LABEL_FONT,
                ForeColor = TEXT,
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleLeft
            };
        }

        private Label MakeStatusLabel(string text)
        {
            return new Label
            {
                Text = text,
                Font = STATUS_FONT,
                ForeColor = MUTED,
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleLeft
            };
        }

        private Label SectionHeading(string text)
        {
            return new Label
            {
                Text = text,
                Font = new Font("Segoe UI", 10, FontStyle.Bold),
                ForeColor = GOLD,
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleLeft
            };
        }

        private GradientButton MakeButton(string text, Color baseColor, Color hoverColor)
        {
            return MakeButton(text, baseColor, hoverColor, new Size(168, 36), BTN_FONT);
        }

        private GradientButton MakeButton(string text, Color baseColor, Color hoverColor, Size size, Font font)
        {
            return new GradientButton(text, baseColor, hoverColor)
            {
                Size = size,
                MinimumSize = size,
                MaximumSize = size,
                Font = font,
                Margin = new Padding(0, 0, 14, 0)
            };
        }

        private void HandleGenerateKeys()
        {
            try
            {
                currentKeyPair = algorithm.GenerateKeys();

                pDisplay.Text = FormatBigInteger(currentKeyPair.Params.P);
                qDisplay.Text = FormatBigInteger(currentKeyPair.Params.Q);
                gDisplay.Text = FormatBigInteger(currentKeyPair.Params.G);
                privateKeyDisplay.Text = FormatBigInteger(currentKeyPair.PrivateKey);
                publicKeyDisplay.Text = FormatBigInteger(currentKeyPair.PublicKey);
                keySummaryDisplay.Text = BuildKeySummary(currentKeyPair);

                keyStatusLabel.Text = "✓ Đã tạo khóa thành công";
                keyStatusLabel.ForeColor = TEAL;
                signStatusLabel.Text = "Trạng thái: Sẵn sàng ký văn bản";
                signStatusLabel.ForeColor = MUTED;
                verifyStatusLabel.Text = "Trạng thái: Sẵn sàng xác minh";
                verifyStatusLabel.ForeColor = MUTED;

                verifyPublicKeyDisplay.Text = FormatPublicKeyForDisplay(currentKeyPair.PublicKey);
                if (string.IsNullOrWhiteSpace(verifyMessageInput.Text))
                    verifyMessageInput.Text = messageInput.Text;
            }
            catch (Exception ex)
            {
                MessageBox.Show(this, "Lỗi tạo khóa: " + ex.Message, "Lỗi", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void HandleSignMessage()
        {
            try
            {
                var message = (messageInput.Text ?? string.Empty).Trim();
                if (string.IsNullOrWhiteSpace(message))
                {
                    MessageBox.Show(this, "Vui lòng nhập văn bản cần ký.", "Cảnh báo", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }

                if (currentKeyPair == null)
                {
                    MessageBox.Show(this, "Vui lòng tạo khóa trước khi ký.", "Cảnh báo", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }

                currentSignature = algorithm.Sign(message);
                hashDisplay.Text = FormatHashDisplay(currentSignature, message);
                signatureDisplay.Text = FormatSignatureForDisplay(currentSignature);
                signStatusLabel.Text = "✓ Đã ký văn bản thành công";
                signStatusLabel.ForeColor = TEAL;

                verifyMessageInput.Text = message;
                verifySignatureInput.Text = FormatSignatureForDisplay(currentSignature);
                verifyPublicKeyDisplay.Text = FormatPublicKeyForDisplay(currentKeyPair.PublicKey);
                verifyStatusLabel.Text = "Trạng thái: Sẵn sàng kiểm tra chữ ký";
                verifyStatusLabel.ForeColor = MUTED;

                verificationResultLabel.Text = "CHƯA XÁC MINH";
                SetBannerColors(CARD_BG, MUTED, CARD_BORDER);
                tabs.SelectedIndex = 2;
            }
            catch (Exception ex)
            {
                MessageBox.Show(this, "Lỗi ký: " + ex.Message, "Lỗi", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void HandleVerifySignature()
        {
            try
            {
                var message = (verifyMessageInput.Text ?? string.Empty).Trim();
                if (string.IsNullOrWhiteSpace(message))
                {
                    MessageBox.Show(this, "Vui lòng nhập văn bản để xác minh.", "Cảnh báo", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }

                if (currentKeyPair == null)
                {
                    MessageBox.Show(this, "Vui lòng tạo khóa hoặc tải khóa công khai.", "Cảnh báo", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }

                var signature = ParseSignature(verifySignatureInput.Text) ?? currentSignature;
                if (signature == null)
                {
                    MessageBox.Show(this, "Không có chữ ký hợp lệ để xác minh.", "Cảnh báo", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }

                bool valid = algorithm.Verify(message, signature);
                if (valid)
                {
                    verificationResultLabel.Text = "  ✓  VĂN BẢN VÀ CHỮ KÝ HỢP LỆ  ";
                    SetBannerColors(TEAL, Color.White, TEAL);
                    verifyStatusLabel.Text = "✓ Đã xác minh xong - Hợp lệ";
                    verifyStatusLabel.ForeColor = TEAL;
                }
                else
                {
                    verificationResultLabel.Text = "  ✗  VĂN BẢN ĐÃ BỊ SỬA ĐỔI HOẶC CHỮ KÝ SAI  ";
                    SetBannerColors(DANGER, Color.White, DANGER);
                    verifyStatusLabel.Text = "✗ Phát hiện lỗi bảo mật";
                    verifyStatusLabel.ForeColor = DANGER;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(this, "Lỗi xác minh: " + ex.Message, "Lỗi", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void SetBannerColors(Color background, Color foreground, Color border)
        {
            verificationResultLabel.BackColor = background;
            verificationResultLabel.ForeColor = foreground;
            if (verificationResultLabel is RoundedBannerLabel banner)
            {
                banner.FillColor = background;
                banner.BorderColor = border;
                banner.Invalidate();
            }
        }

        private void LoadTextInto(TextBox target)
        {
            using var dialog = new OpenFileDialog
            {
                Filter = "Text files|*.txt;*.md;*.log|All files|*.*"
            };

            if (dialog.ShowDialog(this) == DialogResult.OK)
            {
                try
                {
                    target.Text = File.ReadAllText(dialog.FileName, Encoding.UTF8);
                }
                catch (Exception ex)
                {
                    MessageBox.Show(this, "Không thể tải tệp: " + ex.Message, "Lỗi", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
        }

        private void SaveTextFrom(TextBox source, string defaultFileName)
        {
            using var dialog = new SaveFileDialog
            {
                Filter = "Text files|*.txt|All files|*.*",
                FileName = defaultFileName
            };

            if (dialog.ShowDialog(this) == DialogResult.OK)
            {
                try
                {
                    File.WriteAllText(dialog.FileName, source.Text ?? string.Empty, Encoding.UTF8);
                }
                catch (Exception ex)
                {
                    MessageBox.Show(this, "Không thể lưu tệp: " + ex.Message, "Lỗi", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
        }

        private void SaveKeyPairToFile()
        {
            if (currentKeyPair == null)
            {
                MessageBox.Show(this, "Vui lòng tạo khóa trước.", "Cảnh báo", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            using var dialog = new SaveFileDialog
            {
                Filter = "Text files|*.txt|All files|*.*",
                FileName = "schorr_keypair.txt"
            };

            if (dialog.ShowDialog(this) == DialogResult.OK)
            {
                var content =
                    $"P = {currentKeyPair.Params.P}\r\n" +
                    $"Q = {currentKeyPair.Params.Q}\r\n" +
                    $"G = {currentKeyPair.Params.G}\r\n\r\n" +
                    $"x = {currentKeyPair.PrivateKey}\r\n" +
                    $"y = {currentKeyPair.PublicKey}";
                File.WriteAllText(dialog.FileName, content, Encoding.UTF8);
            }
        }

        private string BuildKeySummary(SchorrKeyPair keyPair)
        {
            return
                "THÔNG SỐ BẢO MẬT (Schnorr):\r\n" +
                "──────────────────────────\r\n" +
                $"• P bits : {BigIntegerExtensions.GetBitLengthExt(keyPair.Params.P)}\r\n" +
                $"• Q bits : {BigIntegerExtensions.GetBitLengthExt(keyPair.Params.Q)}\r\n" +
                $"• G bits : {BigIntegerExtensions.GetBitLengthExt(keyPair.Params.G)}\r\n" +
                $"• X bits : {BigIntegerExtensions.GetBitLengthExt(keyPair.PrivateKey)}  (Khóa bí mật)\r\n" +
                $"• Y bits : {BigIntegerExtensions.GetBitLengthExt(keyPair.PublicKey)}  (Khóa công khai)";
        }

        private static string FormatBigInteger(BigInteger value)
        {
            return "HEX:\r\n" + value.ToString("X") + "\r\n\r\nDECIMAL:\r\n" + value;
        }

        private static string FormatPublicKeyForDisplay(BigInteger publicKey)
        {
            return "y = " + publicKey;
        }

        private static string FormatSignatureForDisplay(SchorrSignature signature)
        {
            return "s = " + signature.S + "\r\n" + "e = " + signature.E;
        }

        private static string FormatHashDisplay(SchorrSignature signature, string message)
        {
            return "SHA-256 của thông điệp đã ký:\r\n[" + message + "]\r\n\r\nMã băm e = " + signature.E;
        }

        private static SchorrSignature ParseSignature(string text)
        {
            var source = text ?? string.Empty;
            var sMatch = Regex.Match(source, @"s\s*=\s*([0-9]+)", RegexOptions.IgnoreCase);
            var eMatch = Regex.Match(source, @"e\s*=\s*([0-9]+)", RegexOptions.IgnoreCase);
            if (!sMatch.Success || !eMatch.Success)
                return null;

            return new SchorrSignature(BigInteger.Parse(sMatch.Groups[1].Value), BigInteger.Parse(eMatch.Groups[1].Value));
        }

        private class StyledTabControl : TabControl
        {
            private Color appBg, panelBg, cardBg, border, text, muted, accent;

            public StyledTabControl()
            {
                DrawMode = TabDrawMode.OwnerDrawFixed;
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer, true);
            }

            public void SetTheme(Color appBg, Color panelBg, Color cardBg, Color border, Color text, Color muted, Color accent)
            {
                this.appBg = appBg;
                this.panelBg = panelBg;
                this.cardBg = cardBg;
                this.border = border;
                this.text = text;
                this.muted = muted;
                this.accent = accent;
            }

            protected override void OnDrawItem(DrawItemEventArgs e)
            {
                var selected = e.Index == SelectedIndex;
                var bounds = GetTabRect(e.Index);
                bounds.Inflate(-2, -2);

                using var fill = new SolidBrush(selected ? cardBg : appBg);
                using var outline = new Pen(selected ? accent : border);
                e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                e.Graphics.FillRoundedRectangle(fill, bounds, 10);
                e.Graphics.DrawRoundedRectangle(outline, bounds, 10);

                var color = selected ? text : muted;
                TextRenderer.DrawText(
                    e.Graphics,
                    TabPages[e.Index].Text,
                    Font,
                    bounds,
                    color,
                    TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter | TextFormatFlags.EndEllipsis
                );
            }

            protected override void OnPaintBackground(PaintEventArgs pevent)
            {
                pevent.Graphics.Clear(panelBg);
            }
        }

        private class GradientPanel : Panel
        {
            private readonly Color c1;
            private readonly Color c2;
            private readonly Color border;
            private readonly int radius;

            public GradientPanel(Color c1, Color c2, Color border, int radius)
            {
                this.c1 = c1;
                this.c2 = c2;
                this.border = border;
                this.radius = radius;
                BackColor = Color.Transparent;
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw | ControlStyles.UserPaint, true);
            }

            protected override void OnPaint(PaintEventArgs e)
            {
                e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                var rect = new Rectangle(0, 0, Math.Max(1, Width - 1), Math.Max(1, Height - 1));
                using var brush = new System.Drawing.Drawing2D.LinearGradientBrush(rect, c1, c2, 45f);
                using var pen = new Pen(border, 1);
                e.Graphics.FillRoundedRectangle(brush, rect, radius);
                e.Graphics.DrawRoundedRectangle(pen, rect, radius);
            }
        }

        private class ShadowCard : Panel
        {
            private readonly Color cardColor;
            private readonly Color borderColor;
            private readonly bool dark;

            public ShadowCard(Color cardColor, Color borderColor, bool dark)
            {
                this.cardColor = cardColor;
                this.borderColor = borderColor;
                this.dark = dark;
                BackColor = Color.Transparent;
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw | ControlStyles.UserPaint, true);
            }

            protected override void OnPaint(PaintEventArgs e)
            {
                e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                for (int i = 4; i > 0; i--)
                {
                    var alpha = dark ? 40 - i * 6 : 12 - i * 2;
                    using var shadow = new SolidBrush(Color.FromArgb(Math.Max(0, alpha), 0, 0, 0));
                    var shadowRect = new Rectangle(i, i + 1, Math.Max(1, Width - i - 1), Math.Max(1, Height - i - 1));
                    e.Graphics.FillRoundedRectangle(shadow, shadowRect, 12);
                }

                var rect = new Rectangle(0, 0, Math.Max(1, Width - 5), Math.Max(1, Height - 5));
                using var bg = new SolidBrush(cardColor);
                using var pen = new Pen(borderColor, 1);
                e.Graphics.FillRoundedRectangle(bg, rect, 12);
                e.Graphics.DrawRoundedRectangle(pen, rect, 12);
            }
        }

        private class ToggleSwitch : Control
        {
            private readonly Color onColor;
            private readonly Color offColor;
            private bool selected;
            private float animPos;
            private readonly Timer animTimer;

            public bool IsOn => selected;
            public event EventHandler Toggled;

            public ToggleSwitch(bool init, Color onColor, Color offColor)
            {
                selected = init;
                animPos = selected ? 1f : 0f;
                this.onColor = onColor;
                this.offColor = offColor;
                Cursor = Cursors.Hand;
                Size = new Size(50, 26);
                MinimumSize = Size;
                MaximumSize = Size;
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer | ControlStyles.UserPaint, true);

                animTimer = new Timer { Interval = 15 };
                animTimer.Tick += (s, e) => AnimateStep();
            }

            protected override void OnClick(EventArgs e)
            {
                selected = !selected;
                Toggled?.Invoke(this, EventArgs.Empty);
                animTimer.Start();
                base.OnClick(e);
            }

            private void AnimateStep()
            {
                var target = selected ? 1f : 0f;
                animPos += (target - animPos) * 0.25f;
                if (Math.Abs(animPos - target) < 0.01f)
                {
                    animPos = target;
                    animTimer.Stop();
                }
                Invalidate();
            }

            protected override void OnPaint(PaintEventArgs e)
            {
                e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                using var track = new SolidBrush(selected ? onColor : offColor);
                e.Graphics.FillRoundedRectangle(track, new Rectangle(0, 0, Width - 1, Height - 1), Height);

                var knobSize = Height - 6;
                var minX = 3;
                var maxX = Width - knobSize - 3;
                var knobX = (int)(minX + (maxX - minX) * animPos);
                var knobY = 3;

                using var shadow = new SolidBrush(Color.FromArgb(35, 0, 0, 0));
                e.Graphics.FillEllipse(shadow, knobX + 1, knobY + 2, knobSize, knobSize);
                e.Graphics.FillEllipse(Brushes.White, knobX, knobY, knobSize, knobSize);
            }
        }

        private class GradientButton : Button
        {
            private readonly Color baseColor;
            private readonly Color hoverColor;
            private bool hovered;

            public GradientButton(string text, Color baseColor, Color hoverColor)
            {
                Text = text;
                this.baseColor = baseColor;
                this.hoverColor = hoverColor;
                FlatStyle = FlatStyle.Flat;
                FlatAppearance.BorderSize = 0;
                ForeColor = Color.White;
                Cursor = Cursors.Hand;
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer | ControlStyles.UserPaint, true);
            }

            protected override void OnMouseEnter(EventArgs e)
            {
                hovered = true;
                Invalidate();
                base.OnMouseEnter(e);
            }

            protected override void OnMouseLeave(EventArgs e)
            {
                hovered = false;
                Invalidate();
                base.OnMouseLeave(e);
            }

            protected override void OnPaint(PaintEventArgs e)
            {
                e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                var c1 = hovered ? hoverColor : baseColor;
                var c2 = hovered ? baseColor : ControlPaint.Dark(baseColor);
                using var brush = new System.Drawing.Drawing2D.LinearGradientBrush(ClientRectangle, c1, c2, 90f);
                using var border = new Pen(ControlPaint.Dark(baseColor), 1);

                var rect = new Rectangle(0, 0, Math.Max(1, Width - 1), Math.Max(1, Height - 1));
                e.Graphics.FillRoundedRectangle(brush, rect, 10);
                using (var highlight = new SolidBrush(Color.FromArgb(40, 255, 255, 255)))
                {
                    e.Graphics.FillRoundedRectangle(highlight, new Rectangle(0, 0, Width, Height / 2), 10);
                }
                e.Graphics.DrawRoundedRectangle(border, rect, 10);

                TextRenderer.DrawText(
                    e.Graphics,
                    Text,
                    Font,
                    ClientRectangle,
                    ForeColor,
                    TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter | TextFormatFlags.EndEllipsis
                );
            }
        }

        private class RoundedBadgeLabel : Label
        {
            public Color FillColor { get; set; }
            public Color BorderColor { get; set; }
            public int Radius { get; set; } = 10;

            public RoundedBadgeLabel()
            {
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer | ControlStyles.UserPaint, true);
            }

            protected override void OnPaint(PaintEventArgs e)
            {
                e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                var rect = new Rectangle(0, 0, Width - 1, Height - 1);
                using var bg = new SolidBrush(FillColor);
                using var pen = new Pen(BorderColor, 1);
                e.Graphics.FillRoundedRectangle(bg, rect, Radius);
                e.Graphics.DrawRoundedRectangle(pen, rect, Radius);
                TextRenderer.DrawText(e.Graphics, Text, Font, rect, ForeColor, TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter);
            }
        }

        private class RoundedBannerLabel : Label
        {
            public Color FillColor { get; set; }
            public Color BorderColor { get; set; }
            public int Radius { get; set; } = 12;

            public RoundedBannerLabel()
            {
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer | ControlStyles.UserPaint, true);
            }

            protected override void OnPaint(PaintEventArgs e)
            {
                e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                var rect = new Rectangle(0, 0, Width - 1, Height - 1);
                using var bg = new SolidBrush(FillColor);
                using var pen = new Pen(BorderColor, 1);
                e.Graphics.FillRoundedRectangle(bg, rect, Radius);
                e.Graphics.DrawRoundedRectangle(pen, rect, Radius);
                TextRenderer.DrawText(e.Graphics, Text, Font, rect, ForeColor, TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter | TextFormatFlags.EndEllipsis);
            }
        }
    }

    static class GraphicsExtensions
    {
        public static void FillRoundedRectangle(this Graphics graphics, Brush brush, Rectangle rectangle, int radius)
        {
            if (graphics == null || brush == null || rectangle.Width <= 0 || rectangle.Height <= 0)
                return;

            using var path = CreateRoundedRectanglePath(rectangle, radius);
            graphics.FillPath(brush, path);
        }

        public static void DrawRoundedRectangle(this Graphics graphics, Pen pen, Rectangle rectangle, int radius)
        {
            if (graphics == null || pen == null || rectangle.Width <= 0 || rectangle.Height <= 0)
                return;

            using var path = CreateRoundedRectanglePath(rectangle, radius);
            graphics.DrawPath(pen, path);
        }

        private static System.Drawing.Drawing2D.GraphicsPath CreateRoundedRectanglePath(Rectangle rectangle, int radius)
        {
            var path = new System.Drawing.Drawing2D.GraphicsPath();
            var diameter = Math.Min(Math.Max(1, radius), Math.Min(rectangle.Width, rectangle.Height));

            if (diameter <= 1)
            {
                path.AddRectangle(rectangle);
                return path;
            }

            path.AddArc(rectangle.X, rectangle.Y, diameter, diameter, 180, 90);
            path.AddArc(rectangle.Right - diameter, rectangle.Y, diameter, diameter, 270, 90);
            path.AddArc(rectangle.Right - diameter, rectangle.Bottom - diameter, diameter, diameter, 0, 90);
            path.AddArc(rectangle.X, rectangle.Bottom - diameter, diameter, diameter, 90, 90);
            path.CloseFigure();
            return path;
        }
    }
}