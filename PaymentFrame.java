import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import javax.imageio.ImageIO;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * A Java Swing application that replicates the payment method screen from the image.
 * It features a gradient background, custom-styled components, a QR code modal,
 * and functional buttons.
 */
public class PaymentFrame extends JFrame {

    // --- Custom Fonts ---
    // We can't easily bundle custom fonts like "Luckiest Guy",
    // so we'll create a bold, large sans-serif font to approximate the style.
    private static final Font FONT_DISPLAY = new Font(Font.SANS_SERIF, Font.BOLD, 40);
    private static final Font FONT_DISPLAY_STEP = new Font(Font.SANS_SERIF, Font.BOLD, 30);
    private static final Font FONT_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    private static final Font FONT_BUTTON = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    private static final Font FONT_REFERENCE = new Font(Font.MONOSPACED, Font.BOLD, 16);

    // --- Custom Colors ---
    private static final Color COLOR_GRADIENT_START = new Color(0, 0, 139); // Dark Blue
    private static final Color COLOR_GRADIENT_END = new Color(75, 0, 130); // Indigo/Purple
    private static final Color COLOR_TEXT_LIGHT = Color.WHITE;
    private static final Color COLOR_GCASH = new Color(255, 223, 0); // #FFDF00
    private static final Color COLOR_MAYA = new Color(0, 175, 255); // #00AFFF
    private static final Color COLOR_BUTTON_BACK = new Color(220, 53, 69); // Red
    private static final Color COLOR_BUTTON_PAY = new Color(40, 167, 69); // Green
    private static final Color COLOR_INPUT_BG = new Color(108, 117, 125); // Gray

    public PaymentFrame() {
        setTitle("Payment Method");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize window to full screen
        setLocationRelativeTo(null); // Center the window

        // --- Main Panel with Gradient Background ---
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Title ---
        ShadowLabel titleLabel = new ShadowLabel("PAYMENT METHOD:", FONT_DISPLAY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Span all 3 columns
        gbc.insets = new Insets(0, 0, 60, 0); // Bottom margin
        gbc.anchor = GridBagConstraints.NORTH; // Anchor to the top
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill the horizontal space
        mainPanel.add(titleLabel, gbc);

        // --- Reset GridWidth & Insets ---
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 20, 0, 20); // Padding between columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH; // Align components to the top
        gbc.weightx = 1.0; // Allow columns to have equal width
        gbc.weighty = 1.0; // Allow rows to push content up

        // --- Step 1: QR Code ---
        gbc.gridx = 0;
        gbc.gridy = 1; // Explicitly set to row 1
        mainPanel.add(createStep1Panel(), gbc);

        // --- Step 2: Reference Number ---
        gbc.gridx = 1;
        gbc.gridy = 1; // Explicitly set to row 1
        mainPanel.add(createStep2Panel(), gbc);

        // --- Step 3: Enter Reference ---
        gbc.gridx = 2;
        gbc.gridy = 1; // Explicitly set to row 1
        mainPanel.add(createStep3Panel(), gbc);

        // --- Bottom Buttons ---
        // Back Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0; // Don't let buttons stretch
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(40, 20, 0, 0); // Top margin
        JButton backButton = createStyledButton("BACK", COLOR_BUTTON_BACK);
        backButton.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                PetAdoptionForm adoptionForm = new PetAdoptionForm();
                adoptionForm.setVisible(true);
            });
        });
        mainPanel.add(backButton, gbc);

        // Pay Now Button
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(40, 0, 0, 20);
        JButton payNowButton = createStyledButton("PAY NOW", COLOR_BUTTON_PAY);
        mainPanel.add(payNowButton, gbc);

        // Add main panel to frame
        setContentPane(mainPanel);
        // Don't call pack() when using full screen

        // --- Pay Now Button Logic ---
        // We get the reference to the text field from the Step 3 panel
        JPanel step3Panel = (JPanel) mainPanel.getComponent(3); // 3 is the index of the 3rd panel
        PlaceholderTextField refInputField = (PlaceholderTextField) step3Panel.getComponent(2); // 2 is index of text field
        
        payNowButton.addActionListener(e -> {
            String refText = refInputField.getText().trim();
            if (refText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a reference number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please wait a moment for re-verify (2-5 mins).",
                        "Payment Verification",
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Close current PaymentFrame and open Dashboard
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    Dashboard dashboard = new Dashboard();
                    dashboard.setVisible(true);
                });
            }
        });
    }

    /**
     * Creates the panel for Step 1.
     */
    private JPanel createStep1Panel() {
        JPanel panel = createStepPanel();
        
        ShadowLabel step1Label = new ShadowLabel("STEP 1", FONT_DISPLAY_STEP);
        panel.add(step1Label, BorderLayout.NORTH);

        // Using JTextPane for rich text (colors)
        JTextPane description = createDescriptionPane();
        description.setText("Open your Gcash App (Gcash) or Maya App (Maya) and scan the QR Code provided for the payment:");
        // This is complex. A simple JTextArea is easier.
        JTextArea descArea = createDescriptionArea(
            "Open your Gcash App / Maya App and scan the QR Code provided for the payment:"
        );
        panel.add(descArea, BorderLayout.CENTER);

        JButton showQrButton = createStyledButton("SHOW QR CODE", Color.WHITE, Color.BLACK);
        showQrButton.addActionListener(e -> showQrModal());
        panel.add(showQrButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the panel for Step 2.
     */
    private JPanel createStep2Panel() {
        JPanel panel = createStepPanel();
        
        ShadowLabel step2Label = new ShadowLabel("STEP 2", FONT_DISPLAY_STEP);
        panel.add(step2Label, BorderLayout.NORTH);

        JTextArea descArea = createDescriptionArea(
            "Save the reference number after your payment:"
        );
        panel.add(descArea, BorderLayout.CENTER);

        // --- Reference Number Box ---
        JPanel refPanel = new JPanel(new BorderLayout(10, 0));
        refPanel.setBackground(Color.WHITE);
        refPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel refTitleLabel = new JLabel("Reference Number");
        refTitleLabel.setFont(FONT_BODY.deriveFont(12f));
        refTitleLabel.setForeground(Color.BLUE.darker());
        refPanel.add(refTitleLabel, BorderLayout.NORTH);

        JTextField refNumberField = new JTextField("0034621105277");
        refNumberField.setFont(FONT_REFERENCE);
        refNumberField.setForeground(Color.BLACK);
        refNumberField.setEditable(false);
        refNumberField.setBorder(null);
        refNumberField.setBackground(Color.WHITE);
        refPanel.add(refNumberField, BorderLayout.CENTER);

        // Copy Button
        JButton copyButton = new JButton("Copy"); // Using text as icons are hard in Swing
        copyButton.setFont(FONT_BODY.deriveFont(Font.BOLD, 12f));
        copyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(refNumberField.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(panel, "Copied to clipboard!");
        });
        refPanel.add(copyButton, BorderLayout.EAST);

        panel.add(refPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates the panel for Step 3.
     */
    private JPanel createStep3Panel() {
        JPanel panel = createStepPanel();

        ShadowLabel step3Label = new ShadowLabel("STEP 3", FONT_DISPLAY_STEP);
        panel.add(step3Label, BorderLayout.NORTH);

        JTextArea descArea = createDescriptionArea(
            "Enter the Reference Number here for proof of payments."
        );
        panel.add(descArea, BorderLayout.CENTER);

        PlaceholderTextField refInput = new PlaceholderTextField("Enter your Reference Number");
        refInput.setBackground(COLOR_INPUT_BG);
        refInput.setForeground(Color.WHITE);
        refInput.setCaretColor(Color.WHITE);
        refInput.setFont(FONT_BODY.deriveFont(16f));
        refInput.setBorder(new EmptyBorder(12, 12, 12, 12));
        refInput.setColumns(20); // Give it a preferred size
        
        // Limit input to 13 digits only
        ((AbstractDocument) refInput.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= 13 && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                int currentLength = fb.getDocument().getLength();
                int newLength = currentLength - length + text.length();
                if (newLength <= 13 && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        panel.add(refInput, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Helper to create a base panel for a Step.
     */
    private JPanel createStepPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20)); // Vertical gap
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Helper to create a non-editable, word-wrapping, transparent text area.
     */
    private JTextArea createDescriptionArea(String text) {
        JTextArea descArea = new JTextArea(text);
        descArea.setFont(FONT_BODY);
        descArea.setForeground(COLOR_TEXT_LIGHT);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        return descArea;
    }

    /**
     * Helper to create a styled JTextPane (for rich text).
     * Note: This is more complex and not fully implemented for brevity.
     */
    private JTextPane createDescriptionPane() {
        JTextPane textPane = new JTextPane();
        textPane.setOpaque(false);
        textPane.setEditable(false);
        textPane.setFont(FONT_BODY);
        textPane.setForeground(COLOR_TEXT_LIGHT);
        // Logic to style "Gcash" and "Maya" would go here
        return textPane;
    }

    /**
     * Helper to create a styled button.
     */
    private JButton createStyledButton(String text, Color background) {
        return createStyledButton(text, background, Color.WHITE);
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true); // Required for background to show on Mac
        button.setBorderPainted(false); // No ugly default border
        button.setFocusPainted(false); // No focus ring
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 25, 12, 25)); // Padding
        return button;
    }

    /**
     * Shows the QR Code modal dialog.
     */
    private void showQrModal() {
        JDialog qrDialog = new JDialog(this, "Scan to Pay", true); // Modal
        qrDialog.setSize(800, 600);
        qrDialog.setLocationRelativeTo(this);
        qrDialog.setLayout(new BorderLayout(0, 20));
        qrDialog.getContentPane().setBackground(Color.WHITE);
        qrDialog.getRootPane().setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Scan to Pay", SwingConstants.CENTER);
        titleLabel.setFont(FONT_BODY.deriveFont(Font.BOLD, 24f));
        qrDialog.add(titleLabel, BorderLayout.NORTH);

        // --- Main panel for both QR codes ---
        JPanel qrPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        qrPanel.setBackground(Color.WHITE);

        // --- GCash QR Code Panel ---
        JPanel gcashPanel = new JPanel(new BorderLayout(0, 15));
        gcashPanel.setBackground(Color.WHITE);
        gcashPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_GCASH, 3),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel gcashLogo = new JLabel("GCash", SwingConstants.CENTER);
        gcashLogo.setFont(FONT_BODY.deriveFont(Font.BOLD, 28f));
        gcashLogo.setForeground(new Color(0, 114, 188)); // GCash blue
        gcashPanel.add(gcashLogo, BorderLayout.NORTH);

        // Load GCash QR from the image you provided
        JLabel gcashQR = new JLabel();
        gcashQR.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            // Try multiple path strategies
            ImageIcon gcashIcon = null;
            
            // Strategy 1: Try as resource (if images are in src folder)
            URL gcashUrl = getClass().getResource("/image/GCASH.png");
            if (gcashUrl == null) {
                // Strategy 2: Try without leading slash
                gcashUrl = getClass().getResource("image/GCASH.png");
            }
            
            if (gcashUrl != null) {
                gcashIcon = new ImageIcon(gcashUrl);
            } else {
                // Strategy 3: Try as file path (if images are outside src)
                String currentDir = System.getProperty("user.dir");
                String imagePath = currentDir + "\\src\\image\\GCASH.png";
                java.io.File imageFile = new java.io.File(imagePath);
                if (imageFile.exists()) {
                    gcashIcon = new ImageIcon(imagePath);
                } else {
                    // Try alternative path
                    imagePath = currentDir + "\\image\\GCASH.png";
                    imageFile = new java.io.File(imagePath);
                    if (imageFile.exists()) {
                        gcashIcon = new ImageIcon(imagePath);
                    }
                }
            }
            
            if (gcashIcon != null && gcashIcon.getIconWidth() > 0) {
                // Use SCALE_REPLICATE for crisp QR codes (preserves pixel boundaries)
                Image img = gcashIcon.getImage().getScaledInstance(250, 250, Image.SCALE_REPLICATE);
                gcashQR.setIcon(new ImageIcon(img));
            } else {
                throw new Exception("Image not found in any location");
            }
        } catch (Exception e) {
            gcashQR.setText("<html><center>GCash QR Code<br/>Image not found<br/><small>(" + e.getMessage() + ")</small></center></html>");
            gcashQR.setFont(FONT_BODY);
            e.printStackTrace(); // Print stack trace to console for debugging
        }
        gcashPanel.add(gcashQR, BorderLayout.CENTER);

        JPanel gcashInfoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        gcashInfoPanel.setBackground(Color.WHITE);
        JLabel gcashName = new JLabel("Name: Pay2Go.pet / Ju***N A.", SwingConstants.CENTER);
        gcashName.setFont(FONT_BODY.deriveFont(Font.BOLD, 14f));
        JLabel gcashNumber = new JLabel("Number: 09940986034", SwingConstants.CENTER);
        gcashNumber.setFont(FONT_BODY.deriveFont(14f));
        gcashInfoPanel.add(gcashName);
        gcashInfoPanel.add(gcashNumber);
        gcashPanel.add(gcashInfoPanel, BorderLayout.SOUTH);

        // --- Maya QR Code Panel ---
        JPanel mayaPanel = new JPanel(new BorderLayout(0, 15));
        mayaPanel.setBackground(Color.WHITE);
        mayaPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 200, 117), 3), // Maya green
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel mayaLogo = new JLabel("maya", SwingConstants.CENTER);
        mayaLogo.setFont(FONT_BODY.deriveFont(Font.BOLD, 28f));
        mayaLogo.setForeground(new Color(0, 200, 117)); // Maya green
        mayaPanel.add(mayaLogo, BorderLayout.NORTH);

        // Load Maya QR from the image you provided
        JLabel mayaQR = new JLabel();
        mayaQR.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            // Try multiple path strategies
            ImageIcon mayaIcon = null;
            
            // Strategy 1: Try as resource (if images are in src folder)
            URL mayaUrl = getClass().getResource("/image/MAYA.png");
            if (mayaUrl == null) {
                // Strategy 2: Try without leading slash
                mayaUrl = getClass().getResource("image/MAYA.png");
            }
            
            if (mayaUrl != null) {
                mayaIcon = new ImageIcon(mayaUrl);
            } else {
                // Strategy 3: Try as file path (if images are outside src)
                String currentDir = System.getProperty("user.dir");
                String imagePath = currentDir + "\\src\\image\\MAYA.png";
                java.io.File imageFile = new java.io.File(imagePath);
                if (imageFile.exists()) {
                    mayaIcon = new ImageIcon(imagePath);
                } else {
                    // Try alternative path
                    imagePath = currentDir + "\\image\\MAYA.png";
                    imageFile = new java.io.File(imagePath);
                    if (imageFile.exists()) {
                        mayaIcon = new ImageIcon(imagePath);
                    }
                }
            }
            
            if (mayaIcon != null && mayaIcon.getIconWidth() > 0) {
                // Use SCALE_REPLICATE for crisp QR codes (preserves pixel boundaries)
                Image img = mayaIcon.getImage().getScaledInstance(250, 250, Image.SCALE_REPLICATE);
                mayaQR.setIcon(new ImageIcon(img));
            } else {
                throw new Exception("Image not found in any location");
            }
        } catch (Exception e) {
            mayaQR.setText("<html><center>Maya QR Code<br/>Image not found<br/><small>(" + e.getMessage() + ")</small></center></html>");
            mayaQR.setFont(FONT_BODY);
            e.printStackTrace(); // Print stack trace to console for debugging
        }
        mayaPanel.add(mayaQR, BorderLayout.CENTER);

        JPanel mayaInfoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        mayaInfoPanel.setBackground(Color.WHITE);
        JLabel mayaName = new JLabel("Name: JULIAN AGUSTINO", SwingConstants.CENTER);
        mayaName.setFont(FONT_BODY.deriveFont(Font.BOLD, 14f));
        JLabel mayaNumber = new JLabel("Number: 09940986034", SwingConstants.CENTER);
        mayaNumber.setFont(FONT_BODY.deriveFont(14f));
        mayaInfoPanel.add(mayaName);
        mayaInfoPanel.add(mayaNumber);
        mayaPanel.add(mayaInfoPanel, BorderLayout.SOUTH);

        // Add both panels
        qrPanel.add(gcashPanel);
        qrPanel.add(mayaPanel);
        qrDialog.add(qrPanel, BorderLayout.CENTER);

        // --- OK Button ---
        JButton okButton = createStyledButton("OKAY", new Color(40, 167, 69));
        okButton.addActionListener(e -> qrDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        qrDialog.add(buttonPanel, BorderLayout.SOUTH);

        qrDialog.setVisible(true); // Show the dialog
    }


    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        // Set Look and Feel for better cross-platform consistency
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new PaymentFrame().setVisible(true);
        });
    }
}

/**
 * A custom JPanel with a gradient background.
 */
class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        // A gradient from top-left (blue) to bottom-right (purple)
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(0, 0, 139), 
                w, h, new Color(75, 0, 130));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}

/**
 * A custom JLabel that draws text with a shadow effect.
 */
class ShadowLabel extends JLabel {
    private final Color shadowColor = new Color(0, 0, 0, 100); // Semi-transparent black
    private final int shadowOffsetX = 3;
    private final int shadowOffsetY = 3;

    public ShadowLabel(String text, Font font) {
        super(text);
        setFont(font);
        setForeground(Color.WHITE);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // --- Enable Anti-aliasing for smooth text ---
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
        FontMetrics fm = g2.getFontMetrics();
        
        // --- Center the text horizontally ---
        int stringWidth = fm.stringWidth(getText());
        int x = (getWidth() - stringWidth) / 2;
        int y = fm.getAscent(); // Align text to top

        // 1. Draw Shadow
        g2.setColor(shadowColor);
        g2.drawString(getText(), x + shadowOffsetX, y + shadowOffsetY);

        // 2. Draw Main Text
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}

/**
 * A custom JTextField that shows placeholder text when empty.
 */
class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            
            // --- Enable Anti-aliasing ---
            g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // --- Set placeholder color and font ---
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            
            // --- Draw the placeholder text ---
            // Get insets to draw text in the correct position
            Insets insets = getInsets();
            int x = insets.left;
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(placeholder, x, y);
            
            g2.dispose();
        }
    }
}