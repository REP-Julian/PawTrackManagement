import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;

/**
 * UserProfile.java
 * * A complete Java Swing application displaying a User Profile including:
 * - Modern Aesthetic UI with Gradients and Rounded Corners
 * - Custom drawn Profile Picture
 * - Personal Information (Name, Age, Address, Contact)
 * - Editable Profile Information via Dialog
 * - Card-style layouts for About and Tables
 * - Status Color Logic
 * * To run: Compile `javac UserProfile.java` and run `java UserProfile`
 */
public class UserProfile extends JFrame {

    // Modern Color Palette
    private static final Color ACCENT_COLOR = new Color(52, 152, 219); // Bright Blue
    private static final Color ACCENT_DARK = new Color(41, 128, 185);
    private static final Color SIDEBAR_BG = Color.WHITE;
    private static final Color MAIN_BG = new Color(244, 247, 246); // Very light gray/blue
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80); // Dark Slate
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141); // Gray
    private static final Color CARD_BG = Color.WHITE;

    // Editable Labels & Fields
    private JLabel nameLabel;
    private JLabel ageValLabel;
    private JLabel addressValLabel;
    private JLabel emailValLabel;
    private JLabel contactValLabel;
    private JTextArea aboutText; 

    // Expose the built UI so dashboard can embed it
    private JPanel mainContentPanel;

    public UserProfile() {
        setTitle("User Profile - Pet Management System");
        setSize(1000, 750);
        // Use DISPOSE_ON_CLOSE so embedding doesn't terminate the whole app when closed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());

        // --- Main Content Panel (promoted from local variable to field) ---
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(MAIN_BG);

        // --- Left Sidebar (Profile Image & Contact) ---
        JPanel sidebarPanel = createSidebar();
        mainContentPanel.add(sidebarPanel, BorderLayout.WEST);

        // --- Center Panel (Tabs/Lists) ---
        JTabbedPane contentTabs = createContentTabs();
        
        // Add padding around the tabbed pane to let the background show
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(MAIN_BG);
        contentWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentWrapper.add(contentTabs, BorderLayout.CENTER);
        
        mainContentPanel.add(contentWrapper, BorderLayout.CENTER);

        add(mainContentPanel);
    }

    // Public getter so Dashboard can embed the profile UI
    public JPanel getMainContentPanel() {
        return mainContentPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBackground(SIDEBAR_BG);
        // Subtle shadow border on the right
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // 1. Gradient Profile Header
        GradientPanel profileHeader = new GradientPanel(ACCENT_COLOR, new Color(142, 68, 173)); // Blue to Purple gradient
        profileHeader.setLayout(new BoxLayout(profileHeader, BoxLayout.Y_AXIS));
        profileHeader.setBorder(new EmptyBorder(40, 20, 40, 20));
        profileHeader.setMaximumSize(new Dimension(320, 280));

        // Custom Component for Circular Image
        ProfileImagePanel imgPanel = new ProfileImagePanel();
        imgPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameLabel = new JLabel("Alex Johnson");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

        profileHeader.add(imgPanel);
        profileHeader.add(nameLabel);

        // 2. Personal Details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(SIDEBAR_BG);
        detailsPanel.setBorder(new EmptyBorder(30, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Initialize value labels
        ageValLabel = new JLabel("28");
        addressValLabel = new JLabel("123 Maple Street, Springfield");
        emailValLabel = new JLabel("alex.j@example.com");
        contactValLabel = new JLabel("+1 (555) 019-2834");

        // Add rows
        addDetailRow(detailsPanel, gbc, "AGE", ageValLabel);
        addDetailRow(detailsPanel, gbc, "ADDRESS", addressValLabel);
        addDetailRow(detailsPanel, gbc, "EMAIL", emailValLabel);
        addDetailRow(detailsPanel, gbc, "CONTACT", contactValLabel);

        // 3. Edit Button Area
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(SIDEBAR_BG);
        buttonPanel.setBorder(new EmptyBorder(10, 25, 30, 25));
        
        ModernButton editButton = new ModernButton("Edit Profile");
        editButton.addActionListener(e -> openEditProfileDialog());
        editButton.setPreferredSize(new Dimension(270, 45));
        
        buttonPanel.add(editButton);

        // Add sections to sidebar
        sidebar.add(profileHeader);
        sidebar.add(detailsPanel);
        sidebar.add(Box.createVerticalGlue()); 
        sidebar.add(buttonPanel);

        return sidebar;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, JLabel valueLabel) {
        gbc.gridy++;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(149, 165, 166)); // Muted Label Color
        panel.add(lbl, gbc);

        gbc.gridy++;
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        valueLabel.setForeground(TEXT_PRIMARY);
        panel.add(valueLabel, gbc);
        
        // Add a separator line
        gbc.gridy++;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        panel.add(sep, gbc);
    }

    private void openEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(480, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(MAIN_BG);

        // Container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        contentPanel.setBackground(MAIN_BG);

        // Form Grid - reduced rows since Role removed
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 20));
        formPanel.setBackground(MAIN_BG);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = createStyledTextField(nameLabel.getText());
        JTextField ageField = createStyledTextField(ageValLabel.getText());
        JTextField addressField = createStyledTextField(addressValLabel.getText());
        JTextField emailField = createStyledTextField(emailValLabel.getText());
        JTextField contactField = createStyledTextField(contactValLabel.getText());

        addFormRow(formPanel, "Name", nameField);
        addFormRow(formPanel, "Age", ageField);
        addFormRow(formPanel, "Address", addressField);
        addFormRow(formPanel, "Email", emailField);
        addFormRow(formPanel, "Contact", contactField);

        // Bio Field
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioPanel.setBackground(MAIN_BG);
        bioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bioPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel bioLabel = new JLabel("Bio");
        bioLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bioLabel.setForeground(TEXT_SECONDARY);
        bioLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        JTextArea bioField = new JTextArea(aboutText.getText());
        bioField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bioField.setRows(6);
        bioField.setLineWrap(true);
        bioField.setWrapStyleWord(true);
        bioField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane bioScroll = new JScrollPane(bioField);
        bioScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        bioPanel.add(bioLabel, BorderLayout.NORTH);
        bioPanel.add(bioScroll, BorderLayout.CENTER);

        contentPanel.add(formPanel);
        contentPanel.add(bioPanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        ModernButton saveButton = new ModernButton("Save Changes");
        saveButton.setPreferredSize(new Dimension(140, 40));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        saveButton.addActionListener(e -> {
            nameLabel.setText(nameField.getText());
            ageValLabel.setText(ageField.getText());
            addressValLabel.setText(addressField.getText());
            emailValLabel.setText(emailField.getText());
            contactValLabel.setText(contactField.getText());
            aboutText.setText(bioField.getText());
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void addFormRow(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_SECONDARY);
        panel.add(label);
        panel.add(field);
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JTabbedPane createContentTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(MAIN_BG);
        tabs.setFocusable(false);

        // Tab 1: About Me
        tabs.addTab("About Me", createAboutPanel());

        // Tab 2: Registered Pets History
        tabs.addTab("Registered Pets", createHistoryPanel(getRegisteredPetsData(), new String[]{"Pet Name", "Type", "Breed", "Health Status", "Status"}));

        // Tab 3: Adopted Pets History
        tabs.addTab("Adoption History", createHistoryPanel(getAdoptedPetsData(), new String[]{"Pet Name", "Type", "Breed", "Health Status", "Status"}));

        return tabs;
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Use a Card-like panel for the content
        JPanel card = new RoundedPanel(15, CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        aboutText = new JTextArea();
        aboutText.setText("Hi, I'm Alex! \n\n" +
                "I have been a passionate animal lover since I was a child. " +
                "I actively participate in local shelter programs and have dedicated my free time to fostering kittens and walking dogs.\n\n" +
                "My goal is to create a safe environment for all pets and ensure every stray finds a loving home. " +
                "Currently, I own two dogs and a parrot, and I'm looking to adopt a cat soon.");
        aboutText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        aboutText.setForeground(TEXT_PRIMARY);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setEditable(false);
        aboutText.setOpaque(false); // Make transparent to show card bg
        aboutText.setBorder(null);

        JLabel bioHeader = new JLabel("Biography");
        bioHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bioHeader.setForeground(ACCENT_COLOR);
        bioHeader.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        card.add(bioHeader, BorderLayout.NORTH);
        card.add(aboutText, BorderLayout.CENTER);
        
        panel.add(card, BorderLayout.CENTER); // Align top
        return panel;
    }

    private JPanel createHistoryPanel(Object[][] data, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Card wrapper
        JPanel card = new RoundedPanel(15, CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40); // Taller rows
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 246, 254));
        table.setSelectionForeground(Color.BLACK);
        
        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(new Color(100, 100, 100));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240, 240, 240)));
        header.setReorderingAllowed(false);

        // Apply custom renderer for "Status" column to color text
        for (int i = 0; i < table.getColumnCount(); i++) {
            if ("Status".equalsIgnoreCase(table.getColumnName(i))) {
                table.getColumnModel().getColumn(i).setCellRenderer(new StatusCellRenderer());
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(new PaddingCellRenderer());
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // --- Mock Data Generators ---

    private Object[][] getRegisteredPetsData() {
        return new Object[][]{
            {"Bella", "Dog", "Labrador", "Vaccinated", "Available"},
            {"Charlie", "Cat", "Siamese", "Healthy", "Available"},
            {"Max", "Dog", "Beagle", "Injured", "Not Available"},
            {"Luna", "Rabbit", "Netherland Dwarf", "Checkup Needed", "Available"},
            {"Daisy", "Hamster", "Syrian", "Healthy", "Available"}
        };
    }

    private Object[][] getAdoptedPetsData() {
        return new Object[][]{
            {"Rocky", "Dog", "German Shepherd", "Healthy", "Adopted"},
            {"Coco", "Bird", "Macaw", "Vaccinated", "Adopted"},
            {"Misty", "Cat", "Persian", "Under Treatment", "Available"}
        };
    }

    // --- Custom Components ---

    /**
     * Custom Cell Renderer for standard columns to add padding.
     */
    static class PaddingCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(noFocusBorder); // Remove focus dotted line
            if (c instanceof JLabel) {
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10)); // Add left/right padding
            }
            if (!isSelected) {
                 // Alternating row colors
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
            }
            return c;
        }
    }

    /**
     * Custom Cell Renderer to color text based on status availability.
     */
    static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setBorder(noFocusBorder);
            if (c instanceof JLabel) ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));

            // Background logic for alternating rows
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
            }
            
            if (value instanceof String) {
                String status = (String) value;
                Font f = c.getFont();
                c.setFont(f.deriveFont(Font.BOLD)); // Make status bold

                if (status.equalsIgnoreCase("Available")) {
                    c.setForeground(new Color(46, 204, 113)); // Emerald Green
                } else if (status.equalsIgnoreCase("Not Available") || status.equalsIgnoreCase("Adopted")) {
                    c.setForeground(new Color(231, 76, 60)); // Alizarin Red
                } else {
                    c.setForeground(TEXT_PRIMARY);
                }
            }
            return c;
        }
    }

    /**
     * A panel that draws a rounded rectangle background.
     */
    static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Subtle border
            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }

    /**
     * A panel with a vertical gradient background.
     */
    static class GradientPanel extends JPanel {
        private Color color1;
        private Color color2;

        public GradientPanel(Color c1, Color c2) {
            this.color1 = c1;
            this.color2 = c2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }
    }

    /**
     * Modern Rounded Button.
     */
    static class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(ACCENT_DARK);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(74, 163, 223)); // Lighter Blue
            } else {
                g2.setColor(ACCENT_COLOR);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g);
        }
    }

    /**
     * Circular Profile Image.
     */
    static class ProfileImagePanel extends JPanel {
        private final int size = 120;

        public ProfileImagePanel() {
            setPreferredSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Draw White Circle Background
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(0, 0, size, size));

            // 2. Draw Placeholder "Person" Icon
            g2.setColor(new Color(220, 220, 220));
            // Head
            int headSize = 45;
            g2.fill(new Ellipse2D.Double((size - headSize) / 2.0, 20, headSize, headSize));
            // Body (Semi-circle)
            int bodyWidth = 76;
            int bodyHeight = 55;
            g2.fillArc((size - bodyWidth) / 2, 68, bodyWidth, bodyHeight, 0, 180);

            // 3. Draw Border Ring
            g2.setColor(new Color(255, 255, 255, 100)); // Semi-transparent white ring
            g2.setStroke(new BasicStroke(4));
            g2.draw(new Ellipse2D.Double(2, 2, size - 4, size - 4));
        }
    }

    // --- Main Entry Point ---
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Tweak tabbed pane defaults for better look
                UIManager.put("TabbedPane.selected", Color.WHITE);
                UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            } catch (Exception e) {
                e.printStackTrace();
            }
            new UserProfile().setVisible(true);
        });
    }
}