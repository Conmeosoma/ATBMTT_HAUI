using System;
using System.Drawing;
using System.Numerics;
using System.Linq;
using System.Windows.Forms;

namespace Schnorr
{
    public class SchorrForm : Form
    {
        private readonly SchorrSignatureAlgorithm algorithm = new SchorrSignatureAlgorithm();
        private SchorrKeyPair currentKeyPair;
        private SchorrSignature currentSignature;

        // Theme & colours (match Java)
        private Color APP_BG, PANEL_BG, CARD_BG, CARD_BORDER, TEXT, MUTED, HEADER_BG1, HEADER_BG2, TITLE_CLR, SUBTITLE_CLR, GOLD, GOLD_LIGHT, TEAL, TEAL_DARK, DANGER;

        // Controls
        private Panel headerPanel;
        private Label headerTitleLabel, headerSubtitleLabel, badgeLabel;
        private ToggleSwitch toggleDark;
        private TabControl tabs;

        // Key tab controls
        private TextBox pDisplay, qDisplay, gDisplay;
        private TextBox privateKeyDisplay, publicKeyDisplay, keySummaryDisplay;

        // Sign tab controls
        private TextBox messageInput, hashDisplay, signatureDisplay;

        // Verify tab controls
        private TextBox verifyMessageInput, verifySignatureInput, verifyPublicKeyDisplay;
        private Label verificationResultLabel;

        public SchorrForm()
        {
            Text = "Chữ Ký Điện Tử Schnorr";
            Size = new Size(1080, 780);
            StartPosition = FormStartPosition.CenterScreen;
            DoubleBuffered = false; // disable Framework double buffering to avoid GDI+ GetHdc exception
            InitializeTheme();
            InitializeComponents();
        }

        private void InitializeTheme()
        {
            GOLD = Color.FromArgb(212, 175, 55);
            GOLD_LIGHT = Color.FromArgb(240, 210, 90);
            TEAL = Color.FromArgb(32, 201, 151);
            TEAL_DARK = Color.FromArgb(22, 160, 115);
            DANGER = Color.FromArgb(220, 75, 75);

            APP_BG = Color.FromArgb(232, 236, 242);
            PANEL_BG = Color.FromArgb(248, 249, 252);
            CARD_BG = Color.White;
            CARD_BORDER = Color.FromArgb(208, 215, 224);
            TEXT = Color.FromArgb(28, 34, 46);
            MUTED = Color.FromArgb(105, 118, 135);
            HEADER_BG1 = Color.FromArgb(255, 255, 255);
            HEADER_BG2 = Color.FromArgb(255, 252, 240);
            TITLE_CLR = Color.FromArgb(30, 30, 30);
            SUBTITLE_CLR = Color.FromArgb(110, 120, 132);
        }

        private void InitializeComponents()
        {
            BackColor = APP_BG;
            Font = new Font("Segoe UI", 9);

            var root = new TableLayoutPanel { Dock = DockStyle.Fill, RowCount = 2 };
            root.RowStyles.Add(new RowStyle(SizeType.Absolute, 120));
            root.RowStyles.Add(new RowStyle(SizeType.Percent, 100));

            headerPanel = new GradientPanel(HEADER_BG1, HEADER_BG2) { Dock = DockStyle.Fill };
            headerPanel.Padding = new Padding(16, 16, 16, 16);
            BuildHeader(headerPanel);

            tabs = new TabControl { Dock = DockStyle.Fill, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
            tabs.TabPages.Add(CreateKeyTab());
            tabs.TabPages.Add(CreateSignTab());
            tabs.TabPages.Add(CreateVerifyTab());
            tabs.TabPages.Add(CreateHelpTab());

            root.Controls.Add(headerPanel, 0, 0);
            root.Controls.Add(tabs, 0, 1);
            Controls.Add(root);
        }

        private void BuildHeader(Panel panel)
        {
            panel.Controls.Clear();
            var left = new Panel { Dock = DockStyle.Left, Width = 700, BackColor = Color.Transparent };
            var right = new FlowLayoutPanel { Dock = DockStyle.Right, FlowDirection = FlowDirection.RightToLeft, BackColor = Color.Transparent, AutoSize = true };

            headerTitleLabel = new Label { Text = "Chữ Ký Điện Tử Schnorr", Font = new Font("Serif", 22, FontStyle.Bold), ForeColor = TITLE_CLR, AutoSize = true };
            headerSubtitleLabel = new Label { Text = "Hệ thống mô phỏng sinh khóa · ký số · xác minh thông điệp", Font = new Font("Serif", 10, FontStyle.Italic), ForeColor = SUBTITLE_CLR, AutoSize = true };

            left.Controls.Add(headerTitleLabel);
            left.Controls.Add(headerSubtitleLabel);
            headerTitleLabel.Location = new Point(8, 8);
            headerSubtitleLabel.Location = new Point(8, 46);

            badgeLabel = new Label { Text = "  🔐  SCHNORR v1.0  ", Font = new Font("Trebuchet MS", 9, FontStyle.Bold), ForeColor = GOLD, AutoSize = true, BackColor = Color.FromArgb(255, 248, 220) };
            badgeLabel.Padding = new Padding(8, 4, 8, 4);
            badgeLabel.BorderStyle = BorderStyle.None;

            toggleDark = new ToggleSwitch(false) { Margin = new Padding(8) };
            toggleDark.Toggled += (s, e) => { /* toggle theme not fully implemented */ };

            right.Controls.Add(toggleDark);
            right.Controls.Add(new Label { Text = "Dark", ForeColor = SUBTITLE_CLR, AutoSize = true, TextAlign = ContentAlignment.MiddleCenter, Padding = new Padding(8) });
            right.Controls.Add(badgeLabel);

            panel.Controls.Add(left);
            panel.Controls.Add(right);
        }

        private TabPage CreateKeyTab()
        {
            var tab = new TabPage("🔑  Tạo Khóa");
            tab.BackColor = PANEL_BG;
            var main = new TableLayoutPanel { Dock = DockStyle.Fill, ColumnCount = 2 };
            main.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));
            main.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));

            // Left: params
            var paramCard = new ShadowCard { Dock = DockStyle.Fill, Padding = new Padding(12) };
            var paramLayout = new TableLayoutPanel { Dock = DockStyle.Fill, RowCount = 3 };
            paramLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 33));
            paramLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 33));
            paramLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 34));

            pDisplay = MakeCodeBox(3);
            qDisplay = MakeCodeBox(3);
            gDisplay = MakeCodeBox(3);
            paramLayout.Controls.Add(WrapWithLabel("Số nguyên tố P", pDisplay), 0, 0);
            paramLayout.Controls.Add(WrapWithLabel("Số nguyên tố Q", qDisplay), 0, 1);
            paramLayout.Controls.Add(WrapWithLabel("Phần tử sinh G", gDisplay), 0, 2);
            paramCard.Controls.Add(paramLayout);

            var keyCard = new ShadowCard { Dock = DockStyle.Fill, Padding = new Padding(12) };
            var keyLayout = new TableLayoutPanel { Dock = DockStyle.Fill, RowCount = 3 };
            keyLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 35));
            keyLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 35));
            keyLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 30));
            privateKeyDisplay = MakeCodeBox(3);
            publicKeyDisplay = MakeCodeBox(3);
            keySummaryDisplay = MakeCodeBox(4);
            keyLayout.Controls.Add(WrapWithLabel("Khóa bí mật (x)", privateKeyDisplay), 0, 0);
            keyLayout.Controls.Add(WrapWithLabel("Khóa công khai (y)", publicKeyDisplay), 0, 1);
            keyLayout.Controls.Add(WrapWithLabel("Tóm tắt độ dài bit", keySummaryDisplay), 0, 2);
            keyCard.Controls.Add(keyLayout);

            var leftPanel = new TableLayoutPanel { Dock = DockStyle.Fill, RowCount = 3 };
            leftPanel.RowStyles.Add(new RowStyle(SizeType.Percent, 60));
            leftPanel.RowStyles.Add(new RowStyle(SizeType.Percent, 30));
            leftPanel.RowStyles.Add(new RowStyle(SizeType.Percent, 10));
            leftPanel.Controls.Add(paramCard, 0, 0);
            leftPanel.Controls.Add(keyCard, 0, 1);

            var btnRow = new FlowLayoutPanel { Dock = DockStyle.Fill, FlowDirection = FlowDirection.LeftToRight };
            var genBtn = new GradientButton("⚙  Tạo khóa tự động", GOLD, GOLD_LIGHT);
            genBtn.Click += (s, e) => { HandleGenerateKeys(); };
            var randBtn = new GradientButton("🎲  Khóa ngẫu nhiên", MUTED, CARD_BORDER);
            randBtn.Click += (s, e) => { HandleGenerateKeys(); };
            var saveBtn = new GradientButton("💾  Lưu khóa ra tệp", TEAL, TEAL_DARK);
            saveBtn.Click += (s, e) => { SaveKeyPairToFile(); };
            btnRow.Controls.Add(genBtn);
            btnRow.Controls.Add(randBtn);
            btnRow.Controls.Add(saveBtn);
            leftPanel.Controls.Add(btnRow, 0, 2);

            main.Controls.Add(leftPanel, 0, 0);

            // Right: placeholder for sign/verify summary
            var rightPanel = new Panel { Dock = DockStyle.Fill, BackColor = Color.Transparent };
            main.Controls.Add(rightPanel, 1, 0);

            tab.Controls.Add(main);
            return tab;
        }

        private TabPage CreateSignTab()
        {
            var tab = new TabPage("✍  Ký Văn Bản") { BackColor = PANEL_BG };
            var layout = new TableLayoutPanel { Dock = DockStyle.Fill, RowCount = 3 };
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 50));
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 25));
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 25));

            messageInput = MakeEditableBox(8);
            hashDisplay = MakeCodeBox(4);
            signatureDisplay = MakeCodeBox(4);

            layout.Controls.Add(MakeCard("Văn bản cần ký", messageInput), 0, 0);
            layout.Controls.Add(MakeCard("Kết quả băm SHA-256", hashDisplay), 0, 1);
            layout.Controls.Add(MakeCard("Chữ ký số tạo ra", signatureDisplay), 0, 2);

            var signBtn = new GradientButton("✍  Thực Hiện Ký", GOLD, GOLD_LIGHT) { Width = 220, Height = 40 };
            signBtn.Click += (s, e) => HandleSignMessage();

            var actionPanel = new FlowLayoutPanel { Dock = DockStyle.Bottom, FlowDirection = FlowDirection.RightToLeft, Padding = new Padding(8), Height = 56 };
            actionPanel.Controls.Add(signBtn);

            tab.Controls.Add(layout);
            tab.Controls.Add(actionPanel);
            return tab;
        }

        private TabPage CreateVerifyTab()
        {
            var tab = new TabPage("✅  Xác Minh") { BackColor = PANEL_BG };
            var layout = new TableLayoutPanel { Dock = DockStyle.Fill, RowCount = 3, ColumnCount = 2 };
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 70));
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 30));
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 40));
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 40));
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 20));

            verifyMessageInput = MakeEditableBox(6);
            verifySignatureInput = MakeEditableBox(4);
            verifyPublicKeyDisplay = MakeCodeBox(4);

            layout.Controls.Add(MakeCard("Nội dung văn bản", verifyMessageInput), 0, 0);
            layout.Controls.Add(MakeCard("Dữ liệu chữ ký", verifySignatureInput), 0, 1);
            layout.Controls.Add(MakeCard("Khóa công khai người gửi", verifyPublicKeyDisplay), 0, 2);

            var actionPanel = new Panel { Dock = DockStyle.Fill, Padding = new Padding(8) };
            var verifyBtn = new GradientButton("✅  Xác Minh Chữ Ký", GOLD, GOLD_LIGHT) { Width = 240, Height = 40, Anchor = AnchorStyles.Top | AnchorStyles.Right };
            verifyBtn.Click += (s, e) => HandleVerifySignature();
            actionPanel.Controls.Add(verifyBtn);

            verificationResultLabel = new Label { Text = "CHƯA XÁC MINH", Height = 48, TextAlign = ContentAlignment.MiddleCenter, BackColor = CARD_BG, ForeColor = MUTED, Dock = DockStyle.Bottom };
            actionPanel.Controls.Add(verificationResultLabel);

            layout.Controls.Add(actionPanel, 1, 2);

            tab.Controls.Add(layout);
            return tab;
        }

        private TabPage CreateHelpTab()
        {
            var tab = new TabPage("📘  Hướng Dẫn") { BackColor = PANEL_BG };
            var instr = MakeCodeBox(20);
            instr.Font = new Font("Segoe UI", 10);
            instr.Text = "HƯỚNG DẪN SỬ DỤNG...";
            tab.Controls.Add(MakeCard("Tài liệu Hướng Dẫn", instr));
            return tab;
        }

        // Helpers to create styled controls
        private TextBox MakeCodeBox(int rows)
        {
            var tb = new TextBox { Multiline = true, ReadOnly = true, Font = new Font("Consolas", 10), ScrollBars = ScrollBars.Vertical, BackColor = CARD_BG, ForeColor = TEXT }; 
            tb.Height = rows * 20 + 20;
            return tb;
        }

        private TextBox MakeEditableBox(int rows)
        {
            var tb = new TextBox { Multiline = true, Font = new Font("Segoe UI", 10), ScrollBars = ScrollBars.Vertical }; 
            tb.Height = rows * 20 + 20;
            return tb;
        }

        private Panel MakeCard(string title, Control content)
        {
            var card = new ShadowCard { Padding = new Padding(12), Dock = DockStyle.Fill };
            var lbl = new Label { Text = title, Font = new Font("Segoe UI", 10, FontStyle.Bold), ForeColor = GOLD, AutoSize = true };
            lbl.Location = new Point(8, 8);
            card.Controls.Add(lbl);
            content.Location = new Point(8, 36);
            content.Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
            card.Controls.Add(content);
            return card;
        }

        private Control WrapWithLabel(string label, Control ctl)
        {
            var p = new Panel { Dock = DockStyle.Fill };
            var l = new Label { Text = label, Font = new Font("Segoe UI", 9, FontStyle.Bold), ForeColor = TEXT, AutoSize = true };
            l.Location = new Point(6, 6);
            ctl.Location = new Point(6, 28);
            p.Controls.Add(l);
            p.Controls.Add(ctl);
            return p;
        }

        // --- Logic hooks (reuse existing algorithm calls)
        private void HandleGenerateKeys()
        {
            try
            {
                currentKeyPair = algorithm.GenerateKeys();
                pDisplay.Text = currentKeyPair.Params.P.ToString();
                qDisplay.Text = currentKeyPair.Params.Q.ToString();
                gDisplay.Text = currentKeyPair.Params.G.ToString();
                privateKeyDisplay.Text = currentKeyPair.PrivateKey.ToString();
                publicKeyDisplay.Text = currentKeyPair.PublicKey.ToString();
                // also populate the verify tab public key display so user can verify without copying manually
                if (verifyPublicKeyDisplay != null)
                    verifyPublicKeyDisplay.Text = currentKeyPair.PublicKey.ToString();
                keySummaryDisplay.Text = $"P bits: {BigIntegerExtensions.GetBitLengthExt(currentKeyPair.Params.P)}\r\nQ bits: {BigIntegerExtensions.GetBitLengthExt(currentKeyPair.Params.Q)}\r\nX bits: {BigIntegerExtensions.GetBitLengthExt(currentKeyPair.PrivateKey)}";
            }
            catch (Exception ex)
            {
                MessageBox.Show("Lỗi tạo khóa: " + ex.Message);
            }
        }

        private void HandleSignMessage()
        {
            if (currentKeyPair == null) { MessageBox.Show("Vui lòng tạo khóa trước."); return; }
            var msg = messageInput.Text ?? string.Empty;
            if (string.IsNullOrWhiteSpace(msg)) { MessageBox.Show("Vui lòng nhập văn bản cần ký."); return; }
            currentSignature = algorithm.Sign(msg);
            hashDisplay.Text = currentSignature.E.ToString();
            signatureDisplay.Text = "s = " + currentSignature.S + "\r\n e = " + currentSignature.E;
            // auto-fill verify tab with the signed data and public key
            if (verifyMessageInput != null) verifyMessageInput.Text = msg;
            if (verifySignatureInput != null) verifySignatureInput.Text = signatureDisplay.Text;
            if (verifyPublicKeyDisplay != null) verifyPublicKeyDisplay.Text = currentKeyPair.PublicKey.ToString();
            if (verificationResultLabel != null)
            {
                verificationResultLabel.Text = "CHƯA XÁC MINH";
                verificationResultLabel.BackColor = CARD_BG;
                verificationResultLabel.ForeColor = MUTED;
            }
            // switch to verify tab for convenience
            try { tabs.SelectedIndex = 2; } catch { }
        }

        private void HandleVerifySignature()
        {
            if (currentKeyPair == null) { MessageBox.Show("Vui lòng tạo hoặc tải khóa công khai."); return; }
            SchorrSignature sig = null;
            // if user provided a signature text, prefer parsing it
            if (!string.IsNullOrWhiteSpace(verifySignatureInput.Text))
            {
                try
                {
                    var lines = verifySignatureInput.Text.Split(new[] { '\r', '\n' }, StringSplitOptions.RemoveEmptyEntries);
                    BigInteger s = BigInteger.Zero, e = BigInteger.Zero;
                    foreach (var ln in lines)
                    {
                        var t = ln.Split('='); if (t.Length == 2)
                        {
                            var key = t[0].Trim().ToLower(); var val = t[1].Trim();
                            if (key.StartsWith("s")) s = BigInteger.Parse(val);
                            if (key.StartsWith("e")) e = BigInteger.Parse(val);
                        }
                    }
                    sig = new SchorrSignature(s, e);
                }
                catch { MessageBox.Show("Không có chữ ký hợp lệ."); return; }
            }
            // fallback to the last signature produced in-session
            if (sig == null) sig = currentSignature;
            if (sig == null) { MessageBox.Show("Không có chữ ký để xác minh."); return; }
            bool valid = algorithm.Verify(verifyMessageInput.Text ?? string.Empty, sig);
            if (valid)
            {
                verificationResultLabel.Text = "  ✓  VĂN BẢN VÀ CHỮ KÝ HỢP LỆ  ";
                verificationResultLabel.BackColor = TEAL;
                verificationResultLabel.ForeColor = Color.White;
            }
            else
            {
                verificationResultLabel.Text = "  ✗  VĂN BẢN ĐÃ BỊ SỬA ĐỔI HOẶC CHỮ KÝ SAI  ";
                verificationResultLabel.BackColor = DANGER;
                verificationResultLabel.ForeColor = Color.White;
            }
        }

        private void SaveKeyPairToFile()
        {
            if (currentKeyPair == null) { MessageBox.Show("Vui lòng tạo khóa trước."); return; }
            using var sfd = new SaveFileDialog { Filter = "Text files|*.txt", FileName = "schorr_keypair.txt" };
            if (sfd.ShowDialog() == DialogResult.OK)
            {
                System.IO.File.WriteAllText(sfd.FileName, $"P = {currentKeyPair.Params.P}\nQ = {currentKeyPair.Params.Q}\nG = {currentKeyPair.Params.G}\n\nx = {currentKeyPair.PrivateKey}\ny = {currentKeyPair.PublicKey}");
            }
        }

        // --- Custom controls ---
        private class GradientPanel : Panel
        {
            private Color c1, c2;
            public GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; }
            protected override void OnPaint(PaintEventArgs e)
            {
                try
                {
                    base.OnPaint(e);
                    using var g = e.Graphics;
                    using var br = new System.Drawing.Drawing2D.LinearGradientBrush(ClientRectangle, c1, c2, 45f);
                    if (ClientRectangle.Width > 0 && ClientRectangle.Height > 0)
                        g.FillRectangle(br, ClientRectangle);
                }
                catch (Exception)
                {
                    // swallow painting exceptions to avoid crashing the app
                }
            }
        }

        private class ShadowCard : Panel
        {
            public ShadowCard() { BackColor = Color.White; }
            protected override void OnPaint(PaintEventArgs e)
            {
                try
                {
                    using var g = e.Graphics; g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                    // subtle multi-layer shadow like Java version
                    for (int i = 6; i >= 1; i--)
                    {
                        int alpha = 10 + (6 - i) * 8;
                        using var sh = new SolidBrush(Color.FromArgb(Math.Max(0, Math.Min(255, alpha)), 0, 0, 0));
                        var r = new Rectangle(i, i + 1, Math.Max(0, Width - i * 2 - 1), Math.Max(0, Height - i * 2 - 1));
                        if (r.Width > 0 && r.Height > 0)
                            g.FillRoundedRectangle(sh, r, 12);
                    }
                    // card background
                    var rect = new Rectangle(6, 6, Math.Max(0, Width - 12), Math.Max(0, Height - 12));
                    if (rect.Width > 0 && rect.Height > 0)
                    {
                        using var brush = new SolidBrush(BackColor);
                        g.FillRoundedRectangle(brush, rect, 12);
                    }
                    base.OnPaint(e);
                }
                catch (Exception)
                {
                    // swallow painting exceptions
                }
            }
        }

        private class ToggleSwitch : Control
        {
            private bool selected;
            private float animPos; // 0..1
            private System.Windows.Forms.Timer animTimer;
            public event EventHandler Toggled;
            public ToggleSwitch(bool init)
            {
                selected = init;
                animPos = selected ? 1f : 0f;
                SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.UserPaint, true);
                Width = 52; Height = 28;
                animTimer = new System.Windows.Forms.Timer { Interval = 15 };
                animTimer.Tick += (s, e) => { AnimateStep(); };
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
                float target = selected ? 1f : 0f;
                animPos += (target - animPos) * 0.25f;
                if (Math.Abs(animPos - target) < 0.01f) { animPos = target; animTimer.Stop(); }
                Invalidate();
            }
            protected override void OnPaint(PaintEventArgs e)
            {
                try
                {
                    base.OnPaint(e);
                    var g = e.Graphics; g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                    var trackColor = selected ? Color.FromArgb(212, 175, 55) : Color.FromArgb(200, 200, 200);
                    using var trackBrush = new SolidBrush(trackColor);
                    if (Width > 0 && Height > 0)
                        g.FillRoundedRectangle(trackBrush, new Rectangle(0, 0, Width, Height), Math.Max(1, Height));
                    int ks = Math.Max(0, Height - 6); int minX = 3; int maxX = Math.Max(minX, Width - ks - 3);
                    int kx = (int)(minX + (maxX - minX) * animPos);
                    int ky = 3;
                    // knob shadow
                    using var shadow = new SolidBrush(Color.FromArgb(40, 0, 0, 0));
                    if (ks > 0)
                        g.FillEllipse(shadow, kx + 1, ky + 2, ks, ks);
                    g.FillEllipse(Brushes.White, kx, ky, ks, ks);
                }
                catch (Exception)
                {
                    // swallow painting exceptions
                }
            }
        }

        private class GradientButton : Button
        {
            private readonly Color baseCol, hoverCol; private bool hovered;
            public GradientButton(string text, Color baseCol, Color hoverCol) { Text = text; this.baseCol = baseCol; this.hoverCol = hoverCol; FlatStyle = FlatStyle.Flat; ForeColor = Color.White; Height = 40; Padding = new Padding(8); Font = new Font("Segoe UI", 9, FontStyle.Bold); }
            protected override void OnMouseEnter(EventArgs e) { hovered = true; Invalidate(); base.OnMouseEnter(e); }
            protected override void OnMouseLeave(EventArgs e) { hovered = false; Invalidate(); base.OnMouseLeave(e); }
            protected override void OnPaint(PaintEventArgs pe)
            {
                try
                {
                    var g = pe.Graphics; g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                    var c1 = hovered ? hoverCol : baseCol; var c2 = hovered ? baseCol : ControlPaint.Dark(baseCol);
                    using var brush = new System.Drawing.Drawing2D.LinearGradientBrush(ClientRectangle, c1, c2, 90f);
                    if (Width > 0 && Height > 0)
                        g.FillRoundedRectangle(brush, new Rectangle(0, 0, Width, Height), 8);
                    // subtle top highlight
                    using var highlight = new SolidBrush(Color.FromArgb(40, 255, 255, 255));
                    if (Width > 4 && Height > 2)
                        g.FillRoundedRectangle(highlight, new Rectangle(2, 2, Math.Max(0, Width - 4), Height / 2), 8);
                    TextRenderer.DrawText(g, Text, Font, ClientRectangle, ForeColor, TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter);
                }
                catch (Exception)
                {
                    // swallow painting exceptions
                }
            }
        }
    }
    static class GraphicsExtensions
    {
        public static void FillRoundedRectangle(this Graphics g, Brush b, Rectangle r, int radius)
        {
            // Validate rectangle and radius to avoid GDI+ "Parameter is not valid" exceptions
            if (g == null || b == null) return;
            if (r.Width <= 0 || r.Height <= 0)
            {
                // Nothing to draw
                return;
            }
            // clamp radius to half of the smallest dimension
            int maxRadius = Math.Min(r.Width, r.Height) / 2;
            if (radius <= 0 || maxRadius <= 0)
            {
                g.FillRectangle(b, r);
                return;
            }
            int rr = Math.Min(radius, maxRadius);
            using var path = new System.Drawing.Drawing2D.GraphicsPath();
            try
            {
                path.AddArc(r.X, r.Y, rr, rr, 180, 90);
                path.AddArc(r.Right - rr, r.Y, rr, rr, 270, 90);
                path.AddArc(r.Right - rr, r.Bottom - rr, rr, rr, 0, 90);
                path.AddArc(r.X, r.Bottom - rr, rr, rr, 90, 90);
                path.CloseFigure();
                g.FillPath(b, path);
            }
            catch
            {
                // fallback to simple fill to avoid crashing
                g.FillRectangle(b, r);
            }
        }
    }
}
