import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.imageio.ImageIO;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Modern, aesthetically enhanced Java Swing application with gradient backgrounds,
 * rounded corners, hover effects, and sophisticated dark theme design.
 */
public class CreateAccount extends JFrame {

    // --- Enhanced Color Palette ---
    private static final Color COLOR_PANEL_BG_START = new Color(10, 25, 60);      // Navy blue
    private static final Color COLOR_PANEL_BG_END = new Color(20, 40, 80);        // Darker navy blue
    private static final Color COLOR_APP_BG = new Color(5, 15, 40);               // Very dark navy blue background
    private static final Color COLOR_INFO_BOX_START = new Color(15, 30, 65);      // Medium navy blue
    private static final Color COLOR_INFO_BOX_END = new Color(25, 45, 85);        // Lighter navy blue
    private static final Color COLOR_INFO_TITLE_BLUE = new Color(255, 255, 255);  // White
    private static final Color COLOR_ACCENT_BLUE = new Color(240, 240, 240);      // Light gray/white
    private static final Color COLOR_BUTTON_RED = new Color(255, 75, 75);
    private static final Color COLOR_BUTTON_RED_HOVER = new Color(255, 95, 95);
    private static final Color COLOR_BUTTON_GREEN = new Color(75, 200, 130);
    private static final Color COLOR_BUTTON_GREEN_HOVER = new Color(95, 220, 150);
    private static final Color COLOR_TEXT_LIGHT = new Color(255, 255, 255);       // White for text
    private static final Color COLOR_TEXT_FIELD_BG = new Color(20, 35, 70);       // Navy-tinted field background
    private static final Color COLOR_TEXT_FIELD_BORDER = new Color(40, 60, 100);  // Navy-tinted border
    private static final Color COLOR_TEXT_FIELD_FOCUS = new Color(100, 150, 255); // Blue focus
    private static final Color COLOR_SHADOW = new Color(0, 0, 0, 40);             // Slightly darker shadow

    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    // --- Custom Components ---
    
    /**
     * Document filter that converts all input to uppercase
     */
    private static class UppercaseDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) 
                throws BadLocationException {
            if (text != null) {
                super.insertString(fb, offset, text.toUpperCase(), attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            if (text != null) {
                super.replace(fb, offset, length, text.toUpperCase(), attrs);
            }
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color startColor;
        private final Color endColor;
        private boolean isHighlighted = false;
        private static final Color HIGHLIGHT_COLOR = new Color(64, 224, 208, 80); // Turquoise/blue-green

        public RoundedPanel(int radius, Color startColor, Color endColor) {
            this.radius = radius;
            this.startColor = startColor;
            this.endColor = endColor;
            setOpaque(false);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isHighlighted = true;
                    repaint();
                    
                    // Remove highlight after 200ms
                    Timer timer = new Timer(200, evt -> {
                        isHighlighted = false;
                        repaint();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            // Highlight overlay when clicked
            if (isHighlighted) {
                g2.setColor(HIGHLIGHT_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            }
            
            g2.dispose();
        }
    }

    private static class RoundedTextField extends JTextField {
        private final int radius = 8; // Smaller radius for compact design
        private boolean focused = false;

        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Further reduced padding
            setBackground(COLOR_TEXT_FIELD_BG);
            setForeground(COLOR_TEXT_LIGHT);
            setCaretColor(COLOR_TEXT_LIGHT);
            setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Smaller font
            
            // Apply uppercase filter
            ((AbstractDocument) getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
            
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    focused = true;
                    repaint();
                }
                @SuppressWarnings("override")
                public void focusLost(java.awt.event.FocusEvent evt) {
                    focused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            if (focused) {
                g2.setColor(COLOR_TEXT_FIELD_FOCUS);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(COLOR_TEXT_FIELD_BORDER);
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedPasswordField extends JPasswordField {
        private final int radius = 8;
        private boolean focused = false;
        private JButton eyeButton;
        private boolean passwordVisible = false;

        public RoundedPasswordField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 42));
            setBackground(COLOR_TEXT_FIELD_BG);
            setForeground(COLOR_TEXT_LIGHT);
            setCaretColor(COLOR_TEXT_LIGHT);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setLayout(null);
            
            ((AbstractDocument) getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
            
            // Create eye button with custom icon
            eyeButton = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    
                    // Draw button background
                    if (getModel().isPressed()) {
                        g2d.setColor(COLOR_TEXT_FIELD_BORDER);
                    } else if (getModel().isRollover()) {
                        g2d.setColor(COLOR_TEXT_FIELD_FOCUS);
                    } else {
                        g2d.setColor(new Color(40, 60, 100, 150));
                    }
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    
                    // Set icon color
                    Color iconColor = getModel().isRollover() ? COLOR_TEXT_FIELD_FOCUS : COLOR_TEXT_LIGHT;
                    g2d.setColor(iconColor);
                    g2d.setStroke(new BasicStroke(2f));
                    
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    
                    if (passwordVisible) {
                        // Draw eye with slash (hidden state)
                        drawEyeIcon(g2d, centerX, centerY, iconColor);
                        g2d.drawLine(centerX - 8, centerY - 8, centerX + 8, centerY + 8);
                    } else {
                        // Draw open eye
                        drawEyeIcon(g2d, centerX, centerY, iconColor);
                    }
                    
                    g2d.dispose();
                }
                
                private void drawEyeIcon(Graphics2D g2d, int centerX, int centerY, Color color) {
                    // Draw eye outline (almond shape)
                    java.awt.geom.Path2D.Float eyeShape = new java.awt.geom.Path2D.Float();
                    eyeShape.moveTo(centerX - 10, centerY);
                    eyeShape.curveTo(centerX - 10, centerY - 6, centerX - 6, centerY - 8, centerX, centerY - 8);
                    eyeShape.curveTo(centerX + 6, centerY - 8, centerX + 10, centerY - 6, centerX + 10, centerY);
                    eyeShape.curveTo(centerX + 10, centerY + 6, centerX + 6, centerY + 8, centerX, centerY + 8);
                    eyeShape.curveTo(centerX - 6, centerY + 8, centerX - 10, centerY + 6, centerX - 10, centerY);
                    eyeShape.closePath();
                    
                    g2d.setColor(color);
                    g2d.draw(eyeShape);
                    
                    // Draw pupil
                    g2d.fillOval(centerX - 3, centerY - 3, 6, 6);
                }
            };
            
            eyeButton.setFocusPainted(false);
            eyeButton.setBorderPainted(false);
            eyeButton.setContentAreaFilled(false);
            eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            eyeButton.setToolTipText("Show/Hide Password");
            
            eyeButton.addActionListener(e -> togglePasswordVisibility());
            
            add(eyeButton);
            
            addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    focused = true;
                    repaint();
                }
                @SuppressWarnings("override")
                public void focusLost(java.awt.event.FocusEvent evt) {
                    focused = false;
                    repaint();
                }
            });
            
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    positionEyeButton();
                }
            });
        }
        
        private void positionEyeButton() {
            int buttonSize = getHeight() - 8;
            eyeButton.setBounds(getWidth() - buttonSize - 8, 4, buttonSize, buttonSize);
        }
        
        private void togglePasswordVisibility() {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                setEchoChar((char) 0);
            } else {
                setEchoChar('●');
            }
            eyeButton.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            if (focused) {
                g2.setColor(COLOR_TEXT_FIELD_FOCUS);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(COLOR_TEXT_FIELD_BORDER);
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
        
        @Override
        public void doLayout() {
            super.doLayout();
            positionEyeButton();
        }
    }

    private static class ModernButton extends JButton {
        private final Color baseColor;
        private final Color hoverColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color baseColor, Color hoverColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            
            setOpaque(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setFont(FONT_BUTTON);
            setForeground(COLOR_TEXT_LIGHT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color currentColor = isHovered ? hoverColor : baseColor;
            
            // Draw shadow first
            g2.setColor(COLOR_SHADOW);
            g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 15, 15);
            
            // Draw main button
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 15, 15);
            
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(size.width + 40, size.height + 20);
        }
    }

    // --- Form Components ---
    private JTextField lastNameField;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JTextField contactField;

    // --- Buttons ---
    private JButton backButton;
    private JButton submitButton;

    public CreateAccount() {
        // --- Enhanced Frame Setup for Proper Maximized Display ---
        setTitle("Create Account - Pet Adoption Service");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set initial size before maximizing
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        
        // Create main content panel with gradient background
        JPanel mainPanel = new RoundedPanel(0, COLOR_APP_BG, COLOR_APP_BG);
        mainPanel.setLayout(new GridLayout(1, 2, (int)(screenWidth * 0.015), 0)); // Reduced gap
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            (int)(screenHeight * 0.015), // Reduced margins
            (int)(screenWidth * 0.015),   
            (int)(screenHeight * 0.015),  
            (int)(screenWidth * 0.015)    
        ));
        
        setContentPane(mainPanel);

        // --- Enhanced Left Panel (Form) ---
        RoundedPanel leftPanel = new RoundedPanel(20, COLOR_PANEL_BG_START, COLOR_PANEL_BG_END);
        leftPanel.setLayout(new BorderLayout(8, (int)(screenHeight * 0.015)));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(
            (int)(screenHeight * 0.02), 
            (int)(screenWidth * 0.02), 
            (int)(screenHeight * 0.02), 
            (int)(screenWidth * 0.02)
        ));
        createFormContent(leftPanel);
        mainPanel.add(leftPanel);

        // --- Enhanced Right Panel (Info) ---
        RoundedPanel rightPanel = new RoundedPanel(20, COLOR_PANEL_BG_START, COLOR_PANEL_BG_END);
        rightPanel.setLayout(new BorderLayout(0, (int)(screenHeight * 0.015)));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(
            (int)(screenHeight * 0.02), 
            (int)(screenWidth * 0.02), 
            (int)(screenHeight * 0.02), 
            (int)(screenWidth * 0.02)
        ));
        createInfoContent(rightPanel);
        mainPanel.add(rightPanel);

        // Optional: Add ESC key listener to close application
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });

        // Set to maximized state after all components are added
        SwingUtilities.invokeLater(() -> {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
        
        setVisible(true);
    }

    private void createFormContent(JPanel formPanel) {
        // Get screen dimensions for responsive font sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int baseFontSize = Math.max(20, screenSize.width / 120); // Further reduced base font size
        
        // Enhanced title with gradient effect and responsive sizing
        JLabel titleLabel = new JLabel("Create Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, COLOR_INFO_TITLE_BLUE, 
                getWidth(), 0, COLOR_ACCENT_BLUE);
                g2.setPaint(gradient);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, Math.max(20, (int)(baseFontSize * 1.8)))); // Responsive title size
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, (int)(screenSize.height * 0.01), 0)); // Reduced margin
        formPanel.add(titleLabel, BorderLayout.NORTH);

        // Enhanced fields panel with optimized spacing
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        int verticalSpacing = Math.max(3, screenSize.height / 200); // Further reduced vertical spacing
        gbc.insets = new Insets(verticalSpacing, 6, verticalSpacing/2, 6); // Reduced insets
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Create responsive text fields with optimized size
        int fieldColumns = Math.max(15, screenSize.width / 100); // Further reduced field size
        
        lastNameField = new RoundedTextField(fieldColumns);
        addEnhancedField(fieldsPanel, "LAST NAME", lastNameField, 0);

        firstNameField = new RoundedTextField(fieldColumns);
        addEnhancedField(fieldsPanel, "FIRST NAME", firstNameField, 1);

        middleNameField = new RoundedTextField(fieldColumns);
        addEnhancedField(fieldsPanel, "MIDDLE NAME", middleNameField, 2);

        emailField = new RoundedTextField(fieldColumns);
        addEnhancedField(fieldsPanel, "EMAIL ADDRESS", emailField, 3);

        usernameField = new RoundedTextField(fieldColumns);
        addEnhancedField(fieldsPanel, "USERNAME", usernameField, 4);

        passwordField = new RoundedPasswordField(fieldColumns);
        addEnhancedField(fieldsPanel, "PASSWORD", passwordField, 5);

        repeatPasswordField = new RoundedPasswordField(fieldColumns);
        addEnhancedField(fieldsPanel, "REPEAT PASSWORD", repeatPasswordField, 6);

        contactField = new RoundedTextField(fieldColumns);
        addEnhancedField(fieldsPanel, "CONTACT NUMBER", contactField, 7);
        
        // Minimal filler space
        gbc.gridy = 16;
        gbc.weighty = 0.05; // Very minimal filler space
        fieldsPanel.add(new JPanel(), gbc);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
    }

    private void addEnhancedField(JPanel panel, String labelText, Component component, int yPos) {
        // Get screen dimensions for optimized spacing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int verticalSpacing = Math.max(12, screenSize.height / 250); // Further reduced spacing
        int horizontalSpacing = Math.max(12, screenSize.width / 300); // Further reduced spacing
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Enhanced label styling with optimized font
        gbc.gridx = 0;
        gbc.gridy = yPos * 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(verticalSpacing, horizontalSpacing, 1, horizontalSpacing); // Minimal label spacing
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel label = new JLabel(labelText);
        int labelFontSize = Math.max(20, screenSize.width / 160); // Smaller label font
        label.setFont(new Font("Segoe UI", Font.PLAIN, labelFontSize));
        label.setForeground(COLOR_ACCENT_BLUE);
        panel.add(label, gbc);
        
        // Component with optimized spacing
        gbc.gridy = yPos * 2 + 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, horizontalSpacing, verticalSpacing, horizontalSpacing); // Minimal bottom margin
        panel.add(component, gbc);
    }

    private void createInfoContent(JPanel rightPanel) {
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int baseFontSize = Math.max(14, screenSize.width / 100);
        
        // Enhanced image placeholder with responsive size
        RoundedPanel imagePlaceholder = new RoundedPanel(15, COLOR_INFO_BOX_START, COLOR_INFO_BOX_END);
        imagePlaceholder.setLayout(new BorderLayout());
        imagePlaceholder.setPreferredSize(new Dimension(
            (int)(screenSize.width * 0.3), 
            (int)(screenSize.height * 0.2)
        ));
        
        // Load and display image
        JLabel imageLabel = createImageLabel("image/background.png", 
            (int)(screenSize.width * 0.3), (int)(screenSize.height * 0.2));
        imagePlaceholder.add(imageLabel, BorderLayout.CENTER);

        // Enhanced info section with responsive sizing
        RoundedPanel infoSection = new RoundedPanel(15, COLOR_INFO_BOX_START, COLOR_INFO_BOX_END);
        infoSection.setLayout(new BorderLayout(15, 15));
        int infoPadding = Math.max(20, screenSize.width / 60);
        infoSection.setBorder(BorderFactory.createEmptyBorder(infoPadding, infoPadding, infoPadding, infoPadding));
        
        JLabel infoTitle = new JLabel("How the System Works:");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, (int)(baseFontSize * 1.3)));
        infoTitle.setForeground(COLOR_INFO_TITLE_BLUE);
        infoTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, infoPadding/2, 0));
        infoSection.add(infoTitle, BorderLayout.NORTH);

        @SuppressWarnings("unused")
        String infoText = 
            """
        Pet Registration - Shelters and rescuers register pets with detailed profiles including age, breed, health status, and personality traits  
       
        Pet Listings - Browse beautiful galleries of available pets with comprehensive information and high-quality photos
        
        Adoption Application - Submit detailed applications through our streamlined digital process with instant confirmation
        
        Screening Process - Our advanced matching system ensures perfect compatibility between pets and potential families
        
        Final Adoption - Complete the adoption process and welcome your new family member into their forever home!
       
        Secure Platform - All data is encrypted and protected with industry-standard security measures
            """;
        
        JTextArea infoTextArea = new JTextArea(infoText);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new Font("Segoe UI", Font.PLAIN, baseFontSize));
        infoTextArea.setOpaque(false);
        infoTextArea.setForeground(COLOR_TEXT_LIGHT);
        
        JScrollPane infoScrollPane = new JScrollPane(infoTextArea);
        infoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        infoScrollPane.setOpaque(false);
        infoScrollPane.getViewport().setOpaque(false);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        infoSection.add(infoScrollPane, BorderLayout.CENTER);

        // Enhanced button panel with responsive sizing
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints btnGbc = new GridBagConstraints();
        int buttonSpacing = Math.max(15, screenSize.width / 100);
        btnGbc.insets = new Insets(buttonSpacing, buttonSpacing, buttonSpacing, buttonSpacing);
        
        backButton = new ModernButton("← BACK", COLOR_BUTTON_RED, COLOR_BUTTON_RED_HOVER);
        submitButton = new ModernButton("SUBMIT →", COLOR_BUTTON_GREEN, COLOR_BUTTON_GREEN_HOVER);
        
        // Scale button fonts
        Font buttonFont = new Font("Segoe UI", Font.BOLD, Math.max(12, baseFontSize));
        backButton.setFont(buttonFont);
        submitButton.setFont(buttonFont);
        
        backButton.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new PawTrackLogin().setVisible(true));
        });
        submitButton.addActionListener(e -> handleSubmit());
        
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonPanel.add(backButton, btnGbc);
        
        btnGbc.gridx = 1;
        buttonPanel.add(submitButton, btnGbc);

        rightPanel.add(imagePlaceholder, BorderLayout.NORTH);
        rightPanel.add(infoSection, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

  /**
     * Action handler for the SUBMIT button.
     * Gathers all form data and prints it to the console.
     */
    private void handleSubmit() {
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String repeatPassword = new String(repeatPasswordField.getPassword());
        String contact = contactField.getText().trim();

        // Basic validation
        if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(repeatPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to your database
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/pawpatrol_db", "root", "");

            // Insert into users table
            String sql = "INSERT INTO users (full_name, username, password, contact_number, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName + " " + middleName + " " + lastName);
            stmt.setString(2, username);
            stmt.setString(3, password); // ⚠️ plain text for now
            stmt.setString(4, contact);
            stmt.setString(5, email);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Account created successfully! You can now log in.");
                this.dispose();
                SwingUtilities.invokeLater(() -> new PawTrackLogin().setVisible(true));
            }

            stmt.close();
            conn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving account: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void styleOptionPane() {
        UIManager.put("Panel.background", COLOR_PANEL_BG_START);
        UIManager.put("OptionPane.background", COLOR_PANEL_BG_START);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT_LIGHT);
        UIManager.put("Label.foreground", COLOR_TEXT_LIGHT);
        UIManager.put("Button.background", new Color(255, 255, 255));  // White button
        UIManager.put("Button.foreground", new Color(0, 0, 0));        // Black text on button
        UIManager.put("Button.select", new Color(240, 240, 240));      // Light gray on select
        UIManager.put("Button.focus", new Color(200, 200, 200));       // Gray focus
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
    }

    /**
     * Creates a JLabel with an image scaled to fit the specified dimensions.
     * Falls back to text if image cannot be loaded.
     * 
     * @param imagePath Path to the image file (relative or absolute)
     * @param width Target width for the image
     * @param height Target height for the image
     * @return JLabel containing the scaled image or fallback text
     */
    private JLabel createImageLabel(String imagePath, int width, int height) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            BufferedImage originalImage = null;
            
            // Try loading from resources folder first
            try {
                var resource = getClass().getClassLoader().getResourceAsStream(imagePath);
                if (resource != null) {
                    originalImage = ImageIO.read(resource);
                }
            } catch (Exception e) {
                // Resource not found, try file system
            }
            
            // If not found in resources, try as file path
            if (originalImage == null) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    originalImage = ImageIO.read(imageFile);
                }
            }
            
            if (originalImage != null) {
                // Scale image to fit while maintaining aspect ratio
                Image scaledImage = originalImage.getScaledInstance(
                    width - 40, height - 40, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } else {
                // Fallback to text if image not found
                label.setText("🐾 Pet Adoption Service");
                label.setFont(new Font("Segoe UI", Font.BOLD, Math.max(18, width / 25)));
                label.setForeground(COLOR_INFO_TITLE_BLUE);
            }
        } catch (IOException e) {
            // Fallback to text on error
            label.setText("🐾 Pet Adoption Service");
            label.setFont(new Font("Segoe UI", Font.BOLD, Math.max(18, width / 25)));
            label.setForeground(COLOR_INFO_TITLE_BLUE);
            System.err.println("Could not load image: " + imagePath);
        }
        
        return label;
    }

    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set system look and feel.");
        }
        
        // Enhanced UI Manager settings - removed all white colors
        UIManager.put("TextField.background", COLOR_TEXT_FIELD_BG);
        UIManager.put("TextField.foreground", COLOR_TEXT_LIGHT);
        UIManager.put("TextField.caretForeground", COLOR_TEXT_LIGHT);
        UIManager.put("TextField.selectionBackground", COLOR_ACCENT_BLUE);
        UIManager.put("TextField.selectionForeground", COLOR_TEXT_LIGHT);
        UIManager.put("PasswordField.background", COLOR_TEXT_FIELD_BG);
        UIManager.put("PasswordField.foreground", COLOR_TEXT_LIGHT);
        UIManager.put("PasswordField.caretForeground", COLOR_TEXT_LIGHT);
        UIManager.put("PasswordField.selectionBackground", COLOR_ACCENT_BLUE);
        UIManager.put("PasswordField.selectionForeground", COLOR_TEXT_LIGHT);
        UIManager.put("TextArea.background", COLOR_TEXT_FIELD_BG);
        UIManager.put("TextArea.foreground", COLOR_TEXT_LIGHT);
        UIManager.put("TextArea.selectionBackground", COLOR_ACCENT_BLUE);
        UIManager.put("TextArea.selectionForeground", COLOR_TEXT_LIGHT);
        UIManager.put("ScrollPane.background", COLOR_INFO_BOX_START);
        UIManager.put("Viewport.background", COLOR_INFO_BOX_START);
        UIManager.put("ScrollBar.background", COLOR_INFO_BOX_START);
        UIManager.put("ScrollBar.thumb", COLOR_ACCENT_BLUE);
        UIManager.put("ScrollBar.track", COLOR_TEXT_FIELD_BG);

        SwingUtilities.invokeLater(() -> {
            System.out.println("Starting Pet Adoption Service - Create Account (Maximized Window Mode)");
            System.out.println("Press ESC to close the application or use the window controls");
            new CreateAccount();
        });
    }
}


