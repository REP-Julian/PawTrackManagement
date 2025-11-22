import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Pet Adoption Form with contemporary design
 */
public class PetAdoptionForm extends JFrame {

    // --- UI Components ---
    private JButton backButton;
    private JLabel titleLabel;
    private JTextField lastNameField, firstNameField, middleNameField, dobField, ageField;
    private JRadioButton maleRadio, femaleRadio, nonBinaryRadio, preferNotToSayRadio;
    private ButtonGroup genderGroup;
    private JTextField occupationField, emailField, contactField;
    private JTextField provinceField, cityField, barangayField, addressField;
    private JRadioButton resSingleRadio, resDuplexRadio, resCondoRadio, resApartmentRadio, resTrailerRadio;
    private ButtonGroup residencyGroup;
    private JRadioButton petsYesRadio, petsNoRadio;
    private ButtonGroup otherPetsGroup;
    private JCheckBox termsCheckbox;
    private JTextArea termsTextArea;
    private JToggleButton gcashButton, mayaButton;
    private ButtonGroup paymentGroup;
    private JButton submitButton;
    private JFrame parentDashboard;
    private final List<Component> errorFields = new ArrayList<>();

    // Modern Color Palette
    private final Color primaryColor = new Color(99, 102, 241);      // Indigo
    private final Color primaryDark = new Color(79, 70, 229);
    private final Color accentColor = new Color(236, 72, 153);       // Pink
    private final Color successColor = new Color(16, 185, 129);      // Emerald
    private final Color dangerColor = new Color(239, 68, 68);        // Red
    private final Color warningColor = new Color(245, 158, 11);      // Amber
    private final Color backgroundColor = new Color(249, 250, 251);  // Ultra light gray
    private final Color surfaceColor = Color.WHITE;
    private final Color textPrimary = new Color(17, 24, 39);         // Almost black
    private final Color textSecondary = new Color(107, 114, 128);    // Medium gray
    private final Color borderColor = new Color(229, 231, 235);      // Light border
    private final Color errorColor = new Color(254, 226, 226);       // Light red bg

    public PetAdoptionForm() {
        setTitle("Pet Adoption Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);
        
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(backgroundColor);
        mainContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // --- Modern Header with Gradient ---
        JPanel headerPanel = createGradientHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // --- Form Container ---
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(backgroundColor);
        formContainer.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Section 1: Personal Information
        formContainer.add(createSectionCard("👤 Personal Information", createPersonalInfoPanel()));
        formContainer.add(Box.createVerticalStrut(15));

        // Section 2: Contact & Location
        formContainer.add(createSectionCard("📍 Contact & Location", createContactPanel()));
        formContainer.add(Box.createVerticalStrut(15));

        // Section 3: Living Situation
        formContainer.add(createSectionCard("🏠 Living Situation", createLivingPanel()));
        formContainer.add(Box.createVerticalStrut(15));

        // Section 4: Terms & Payment
        JPanel termsPaymentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        termsPaymentPanel.setBackground(backgroundColor);
        termsPaymentPanel.add(createSectionCard("📋 Terms & Conditions", createTermsPanel()));
        termsPaymentPanel.add(createSectionCard("💳 Payment Method", createPaymentPanel()));
        formContainer.add(termsPaymentPanel);
        formContainer.add(Box.createVerticalStrut(20));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBackground(backgroundColor);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // --- Modern Footer ---
        mainContainer.add(createModernFooter(), BorderLayout.SOUTH);

        add(mainContainer);

        // Action Listeners
        submitButton.addActionListener(e -> handleSubmit());
        backButton.addActionListener(e -> handleBack());

        // Frame settings - Full Screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
    }

    /**
     * Creates a gradient header panel
     */
    private JPanel createGradientHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Multi-color gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, primaryColor,
                    getWidth(), 0, new Color(139, 92, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-30, -30, 120, 120);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(getWidth() - 100, -40, 140, 140);
            }
        };
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        backButton = createModernIconButton("← BACK", dangerColor);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        titleLabel = new JLabel("🐾 Pet Adoption Application");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Find your perfect companion 💕");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(subtitle);

        header.add(backButton, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        
        return header;
    }

    /**
     * Creates a modern section card with title
     */
    private JPanel createSectionCard(String title, JPanel content) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(surfaceColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textPrimary);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }

    /**
     * Creates personal information panel
     */
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(surfaceColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 1: Name fields
        gbc.gridy = 0;
        panel.add(createFloatingLabelField("Last Name", lastNameField = createStyledTextField()), setGbc(gbc, 0, 1));
        panel.add(createFloatingLabelField("First Name", firstNameField = createStyledTextField()), setGbc(gbc, 1, 1));
        panel.add(createFloatingLabelField("Middle Name", middleNameField = createStyledTextField()), setGbc(gbc, 2, 1));

        // Row 2: DOB, Age, Gender
        gbc.gridy = 1;
        panel.add(createFloatingLabelField("Date of Birth", dobField = createStyledTextField()), setGbc(gbc, 0, 1));
        panel.add(createFloatingLabelField("Age", ageField = createStyledTextField()), setGbc(gbc, 1, 1));
        
        genderGroup = new ButtonGroup();
        maleRadio = createChipRadio("Male");
        femaleRadio = createChipRadio("Female");
        nonBinaryRadio = createChipRadio("Non-Binary");
        preferNotToSayRadio = createChipRadio("Prefer Not to Say");
        panel.add(createChipRadioPanel("Gender", genderGroup, maleRadio, femaleRadio, nonBinaryRadio, preferNotToSayRadio), setGbc(gbc, 2, 1));

        return panel;
    }

    /**
     * Creates contact information panel
     */
    private JPanel createContactPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(surfaceColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        panel.add(createFloatingLabelField("Occupation", occupationField = createStyledTextField()), setGbc(gbc, 0, 1));
        panel.add(createFloatingLabelField("Email Address", emailField = createStyledTextField()), setGbc(gbc, 1, 1));
        panel.add(createFloatingLabelField("Contact Number", contactField = createStyledTextField()), setGbc(gbc, 2, 1));

        gbc.gridy = 1;
        panel.add(createFloatingLabelField("Province", provinceField = createStyledTextField()), setGbc(gbc, 0, 1));
        panel.add(createFloatingLabelField("City", cityField = createStyledTextField()), setGbc(gbc, 1, 1));
        panel.add(createFloatingLabelField("Barangay", barangayField = createStyledTextField()), setGbc(gbc, 2, 1));

        gbc.gridy = 2;
        panel.add(createFloatingLabelField("Street Address", addressField = createStyledTextField()), setGbc(gbc, 0, 3));

        return panel;
    }

    /**
     * Creates living situation panel
     */
    private JPanel createLivingPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(surfaceColor);

        residencyGroup = new ButtonGroup();
        resSingleRadio = createChipRadio("Single Family");
        resDuplexRadio = createChipRadio("Duplex");
        resCondoRadio = createChipRadio("Condo");
        resApartmentRadio = createChipRadio("Apartment");
        resTrailerRadio = createChipRadio("Trailer");
        panel.add(createChipRadioPanel("Residency Type", residencyGroup, resSingleRadio, resDuplexRadio, resCondoRadio, resApartmentRadio, resTrailerRadio));

        otherPetsGroup = new ButtonGroup();
        petsYesRadio = createChipRadio("Yes");
        petsNoRadio = createChipRadio("No");
        panel.add(createChipRadioPanel("Do you have other pets?", otherPetsGroup, petsYesRadio, petsNoRadio));

        return panel;
    }

    /**
     * Creates terms panel
     */
    private JPanel createTermsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(surfaceColor);

        termsTextArea = new JTextArea(
            "I hereby agree to the terms and regulations regarding pet adoption. I commit to providing a safe and loving home for the adopted pet and understand the responsibilities involved in pet ownership. I acknowledge that the welfare of the pet is a priority and agree to adhere to all guidelines set forth by the adoption agency."
        );
        termsTextArea.setWrapStyleWord(true);
        termsTextArea.setLineWrap(true);
        termsTextArea.setEditable(false);
        termsTextArea.setBackground(new Color(249, 250, 251));
        termsTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        termsTextArea.setForeground(textSecondary);
        termsTextArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(termsTextArea);
        scroll.setPreferredSize(new Dimension(0, 100));
        scroll.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));

        termsCheckbox = new JCheckBox("I agree to the terms and conditions");
        termsCheckbox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        termsCheckbox.setForeground(textPrimary);
        termsCheckbox.setBackground(surfaceColor);
        termsCheckbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        termsCheckbox.setFocusPainted(false);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(termsCheckbox, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates payment panel
     */
    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 12));
        panel.setBackground(surfaceColor);

        paymentGroup = new ButtonGroup();
        gcashButton = createPaymentCard("GCash", "💙 Digital Wallet", new Color(0, 112, 255));
        mayaButton = createPaymentCard("Maya", "💚 Digital Wallet", new Color(16, 185, 129));

        paymentGroup.add(gcashButton);
        paymentGroup.add(mayaButton);

        panel.add(gcashButton);
        panel.add(mayaButton);

        return panel;
    }

    /**
     * Creates modern footer
     */
    private JPanel createModernFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(surfaceColor);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

        submitButton = createGradientButton("SUBMIT APPLICATION");
        submitButton.setPreferredSize(new Dimension(220, 45));

        footer.add(submitButton);
        return footer;
    }

    /**
     * Creates a styled text field with modern design
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(textPrimary);
        field.setBackground(surfaceColor);
        field.setCaretColor(primaryColor);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!errorFields.contains(field)) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryColor, 2, true),
                        new EmptyBorder(9, 11, 9, 11)
                    ));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!errorFields.contains(field)) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1, true),
                        new EmptyBorder(10, 12, 10, 12)
                    ));
                }
            }
        });

        return field;
    }

    /**
     * Creates a floating label field
     */
    private JPanel createFloatingLabelField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(surfaceColor);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(textSecondary);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates payment card toggle button
     */
    private JToggleButton createPaymentCard(String title, String subtitle, Color accentColor) {
        JToggleButton button = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (isSelected()) {
                    g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                } else {
                    g2d.setColor(new Color(249, 250, 251));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Border
                if (isSelected()) {
                    g2d.setColor(accentColor);
                    g2d.setStroke(new BasicStroke(2));
                } else {
                    g2d.setColor(borderColor);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                
                super.paintComponent(g);
            }
        };
        
        button.setLayout(new BorderLayout(10, 5));
        button.setBorder(new EmptyBorder(15, 20, 15, 20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(textPrimary);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(textSecondary);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subtitleLabel);
        
        button.add(textPanel, BorderLayout.CENTER);
        
        button.addItemListener(e -> button.repaint());
        
        return button;
    }

    /**
     * Creates chip-style radio button
     */
    private JRadioButton createChipRadio(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radio.setForeground(textPrimary);
        radio.setBackground(new Color(243, 244, 246));
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setFocusPainted(false);
        radio.setBorder(new EmptyBorder(6, 12, 6, 12));
        radio.setOpaque(true);

        radio.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                radio.setBackground(primaryColor);
                radio.setForeground(Color.WHITE);
            } else {
                radio.setBackground(new Color(243, 244, 246));
                radio.setForeground(textPrimary);
            }
        });

        return radio;
    }

    /**
     * Creates chip radio panel
     */
    private JPanel createChipRadioPanel(String labelText, ButtonGroup group, JRadioButton... buttons) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(surfaceColor);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(textSecondary);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));

        panel.add(label);

        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        chipsPanel.setBackground(surfaceColor);
        chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (JRadioButton button : buttons) {
            group.add(button);
            chipsPanel.add(button);
        }

        panel.add(chipsPanel);
        return panel;
    }

    /**
     * Creates icon button
     */
    private JButton createModernIconButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(new EmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Creates gradient button
     */
    private JButton createGradientButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, primaryColor,
                    getWidth(), 0, primaryDark
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Custom rounded border
     */
    class RoundedBorder extends LineBorder {
        private int radius;

        RoundedBorder(Color color, int radius) {
            super(color, 1, true);
            this.radius = radius;
        }
    }

    /**
     * Sets GridBagConstraints for component placement
     */
    private GridBagConstraints setGbc(GridBagConstraints gbc, int gridx, int gridwidth) {
        gbc.gridx = gridx;
        gbc.gridwidth = gridwidth;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    /**
     * Handles form submission
     */
    private void handleSubmit() {
        // Validate and collect form data
        // Submit logic here
        JOptionPane.showMessageDialog(this, "Redirecting to payment. Please wait...","Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Close current form and open PaymentFrame
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            PaymentFrame paymentFrame = new PaymentFrame();
            paymentFrame.setVisible(true);
        });
    }

    /**
     * Handles back navigation
     */
    private void handleBack() {
        this.dispose();
        
        SwingUtilities.invokeLater(() -> {
            if (parentDashboard != null) {
                parentDashboard.setVisible(true);
                parentDashboard.toFront();
                parentDashboard.requestFocus();
            } else {
                try {
                    new Dashboard().setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error opening Dashboard: " + e.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PetAdoptionForm().setVisible(true));
    }

    public void setParentDashboard(Dashboard dashboard) {
        this.parentDashboard = dashboard;
    }
}