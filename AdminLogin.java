import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*; // FIXED: Imports Rectangle2D, RoundRectangle2D, etc.

public class AdminLogin extends JFrame implements ActionListener {

    // UI Components
    private JPanel loginCard;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel userLabel;
    private JLabel passLabel;
    private ModernTextField userTextField;
    private ModernPasswordField passwordField;
    private ModernButton loginButton;
    private ModernButton resetButton;
    private JCheckBox showPassword;

    // Colors
    private Color primaryColor = new Color(41, 128, 185);   // Professional Blue
    private Color accentColor = new Color(46, 204, 113);    // Emerald Green
    private Color errorColor = new Color(231, 76, 60);      // Alizarin Red
    private Color textColor = new Color(236, 240, 241);     // Off White

    public AdminLogin() {
        // 1. Frame Setup
        setTitle("Admin System - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // 2. Custom Background
        BackgroundPanel bgPanel = new BackgroundPanel("background.jpg");
        bgPanel.setLayout(new GridBagLayout());
        setContentPane(bgPanel);

        // 3. Glass Login Card
        loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glass Background (Dark gradient feel)
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 40, 200), 
                                                     0, getHeight(), new Color(44, 62, 80, 220));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Subtle White Border
                g2.setStroke(new BasicStroke(1));
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 40, 40);
                g2.dispose();
            }
        };
        
        loginCard.setLayout(null);
        loginCard.setPreferredSize(new Dimension(420, 480)); // Adjusted height
        loginCard.setOpaque(false);

        // 4. Title Section
        titleLabel = new JLabel("ADMIN PORTAL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 30, 420, 40);
        loginCard.add(titleLabel);

        subtitleLabel = new JLabel("Please sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200)); 
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(0, 70, 420, 20);
        loginCard.add(subtitleLabel);

        // 5. Username Input
        userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(textColor);
        userLabel.setBounds(60, 110, 100, 30);
        loginCard.add(userLabel);

        userTextField = new ModernTextField("Enter username...");
        userTextField.setBounds(60, 140, 300, 40);
        loginCard.add(userTextField);

        // 6. Password Input
        passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(textColor);
        passLabel.setBounds(60, 190, 100, 30);
        loginCard.add(passLabel);

        passwordField = new ModernPasswordField("Enter password...");
        passwordField.setBounds(60, 220, 300, 40);
        loginCard.add(passwordField);

        // 7. Show Password
        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(60, 270, 150, 30);
        showPassword.setOpaque(false);
        showPassword.setForeground(Color.WHITE);
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassword.setFocusPainted(false);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        loginCard.add(showPassword);

        // 8. Action Buttons
        loginButton = new ModernButton("LOGIN", accentColor);
        loginButton.setBounds(60, 330, 145, 45);
        loginButton.addActionListener(this);
        loginCard.add(loginButton);

        resetButton = new ModernButton("RESET", errorColor);
        resetButton.setBounds(215, 330, 145, 45);
        resetButton.addActionListener(this);
        loginCard.add(resetButton);

        add(loginCard);
        setVisible(true);
        
        // Focus on background first so placeholder text is visible
        SwingUtilities.invokeLater(() -> loginCard.requestFocusInWindow());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String userText = userTextField.getText();
            String pwdText = new String(passwordField.getPassword());

            if (userText.equalsIgnoreCase("admin") && pwdText.equals("12345")) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome Admin.");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == resetButton) {
            userTextField.setText("");
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new AdminLogin());
    }

    // ==============================================
    // CUSTOM UI CLASSES
    // ==============================================

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(String fileName) {
            ImageIcon icon = new ImageIcon(fileName);
            if (icon.getIconWidth() > 0) backgroundImage = icon.getImage();
            setBackground(new Color(44, 62, 80)); 
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    class ModernButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.hoverColor = color.brighter();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? hoverColor : baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(getText(), g2);
            int x = (getWidth() - (int) r.getWidth()) / 2;
            int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
            
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    class ModernTextField extends JTextField {
        private final int ARC_WIDTH = 30;
        private final int ARC_HEIGHT = 30;
        private String placeholder;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 20, 10, 20));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(Color.DARK_GRAY);
            setCaretColor(Color.DARK_GRAY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_WIDTH, ARC_HEIGHT);

            if (isFocusOwner()) {
                g2.setColor(new Color(41, 128, 185));
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(new Color(200, 200, 200));
            }
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, ARC_WIDTH, ARC_HEIGHT);
            
            // Draw Placeholder
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(Color.GRAY);
                g2.drawString(placeholder, getInsets().left, g2.getFontMetrics().getAscent() + getInsets().top - 4);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class ModernPasswordField extends JPasswordField {
        private final int ARC_WIDTH = 30;
        private final int ARC_HEIGHT = 30;
        private String placeholder;

        public ModernPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 20, 10, 20));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(Color.DARK_GRAY);
            setCaretColor(Color.DARK_GRAY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_WIDTH, ARC_HEIGHT);

            if (isFocusOwner()) {
                g2.setColor(new Color(41, 128, 185));
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(new Color(200, 200, 200));
            }
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, ARC_WIDTH, ARC_HEIGHT);
            
            // Draw Placeholder (only if password is empty)
            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setColor(Color.GRAY);
                g2.drawString(placeholder, getInsets().left, g2.getFontMetrics().getAscent() + getInsets().top - 4);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}