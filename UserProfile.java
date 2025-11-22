import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File; // Added for file chooser
import javax.swing.filechooser.FileNameExtensionFilter; // Added for image file filter
import java.awt.geom.RoundRectangle2D; // Added for rounded corners


public class UserProfile extends JFrame implements ActionListener {

    // --- Profile Data ---
    private String userName = "Julian Agustino";
    private String userEmail = "Julian123@gmail.com";
    private String userContact = "09876543211";
    private String userAddress = "178 marikina binabaha na port 123 quezon city";
    
    // Add shared image storage
    private static Image sharedProfileImage = null;

    // --- UI Components to be updated ---
    private JLabel nameLabel;
    private JLabel infoLabel;
    private ImagePlaceholder largeImage;
    private JPanel bottomContentPanel;
    private CardLayout bottomCardLayout;
    private JButton regPetButton; // Add reference to buttons
    private JButton aboutButton;
    
    // Store reference to embedded image placeholder for updates
    private static ImagePlaceholder embeddedImagePlaceholder = null;

    /**
     * Custom component to draw a rounded placeholder or a rounded user-selected image.
     */
    class ImagePlaceholder extends JComponent {
        private Color sky = new Color(206, 230, 245);
        private Color sun = new Color(250, 215, 90);
        private Color mountain = new Color(145, 210, 144);
        private Image displayImage = null;
        private int arc = 25; // Arc width and height for rounded corners

        public ImagePlaceholder(int width, int height) {
            setPreferredSize(new Dimension(width, height));
        }

        public void setImage(Image img) {
            this.displayImage = img;
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create(); // Create a copy to not affect other components
            
            // Enable antialiasing for smooth corners and text
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();

            // Create a rounded rectangle shape to clip the drawing
            RoundRectangle2D.Float roundedRect = new RoundRectangle2D.Float(0, 0, width, height, arc, arc);
            g2.setClip(roundedRect);

            if (displayImage != null) {
                // If an image is set, draw it, scaled to fit the component
                g2.drawImage(displayImage, 0, 0, width, height, this);
            } else {
                // Otherwise, draw the default placeholder
                g2.setColor(sky);
                g2.fillRect(0, 0, width, height);

                g2.setColor(sun);
                int sunDiameter = width / 4;
                g2.fillOval(width / 8, height / 8, sunDiameter, sunDiameter);

                g2.setColor(mountain);
                Polygon p = new Polygon();
                p.addPoint(0, (int)(height * 0.75));
                p.addPoint((int)(width * 0.5), (int)(height * 0.33));
                p.addPoint((int)(width * 0.75), (int)(height * 0.5));
                p.addPoint(width, (int)(height * 0.75));
                p.addPoint(width, height);
                p.addPoint(0, height);
                g2.fillPolygon(p);
            }
            
            // Dispose of the graphics copy
            g2.dispose();
        }
    }

    /**
     * Custom JButton with rounded corners and hover/press effects.
     */
    class RoundedButton extends JButton {
        private Color bgColor;
        private Color fgColor;

        public RoundedButton(String text, Color background, Color foreground) {
            super(text);
            this.bgColor = background;
            this.fgColor = foreground;

            setForeground(fgColor);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16)); // Increased from 12
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Make the button transparent so we can draw our own shape
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set color based on button state
            if (getModel().isPressed()) {
                g2.setColor(bgColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(bgColor.brighter());
            } else {
                g2.setColor(bgColor);
            }

            // Draw the rounded rectangle
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            // Let the parent class paint the text
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /**
     * A custom JPanel with a subtle gradient background.
     */
    class GradientPanel extends JPanel {
        public GradientPanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            // A subtle light grey to white gradient
            Color color1 = new Color(238, 238, 238);
            Color color2 = new Color(252, 252, 252);
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }


    public UserProfile() {
        setTitle("User Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Changed from DO_NOTHING_ON_CLOSE
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Use the new GradientPanel for the main background
        JPanel mainPanel = new GradientPanel(new GridBagLayout());
        
        // Use a softer, off-white for the content panel
        Color contentBgColor = new Color(248, 249, 250);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(contentBgColor);
        contentPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(contentPanel, gbc); // Add content panel directly

        // --- Top Buttons (Using new RoundedButton) ---
        JButton editProfileButton = new RoundedButton("EDIT PROFILE", new Color(34, 177, 76), Color.WHITE);
        editProfileButton.addActionListener(this); 

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        contentPanel.add(editProfileButton, gbc);

        // --- Left Panel (Large Image) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(contentBgColor);

        largeImage = new ImagePlaceholder(350, 350); // Increased from 250x250
        // Set shared image if it exists when creating main profile
        if (sharedProfileImage != null) {
            largeImage.setImage(sharedProfileImage);
        }
        largeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(largeImage);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(20, 10, 10, 20);
        contentPanel.add(leftPanel, gbc);

        // --- Right Panel (Info, Buttons, Small Images) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(contentBgColor); // Changed from Color.WHITE

        // Name
        nameLabel = new JLabel(userName);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36)); // Increased from 28
        nameLabel.setForeground(Color.BLACK);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 10);
        rightPanel.add(nameLabel, gbc);
        
        // Contact Info
        infoLabel = new JLabel();
        updateInfoLabel();
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18)); // Increased from 14
        infoLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        rightPanel.add(infoLabel, gbc);

        // Separator Line
        JSeparator separator = new JSeparator();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        rightPanel.add(separator, gbc);

        // "REGISTERED PET" and "ABOUT" Buttons
        JPanel greyButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        greyButtonsPanel.setBackground(contentBgColor);
        
        regPetButton = new JButton("REGISTERED PET");
        styleTransparentButton(regPetButton, Color.BLACK); 
        regPetButton.addActionListener(this); 
        
        aboutButton = new JButton("ABOUT");
        styleTransparentButton(aboutButton, Color.BLACK); 
        aboutButton.addActionListener(this); 

        // Set initial highlight state (REGISTERED PET is default)
        highlightButton(regPetButton, true);
        highlightButton(aboutButton, false);

        greyButtonsPanel.add(regPetButton);
        greyButtonsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        greyButtonsPanel.add(aboutButton);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 20, 10);
        rightPanel.add(greyButtonsPanel, gbc);

        // --- "Registered Pet" Panel Card ---
        JPanel regPetPanel = createImageGalleryPanel();
        regPetPanel.setBackground(contentBgColor);

        // --- "About" Panel Card ---
        JPanel aboutPanel = createImageGalleryPanel();
        aboutPanel.setBackground(contentBgColor);

        // --- Create the CardLayout Panel ---
        bottomCardLayout = new CardLayout();
        bottomContentPanel = new JPanel(bottomCardLayout);
        bottomContentPanel.setBackground(contentBgColor); // Changed from Color.WHITE
        
        bottomContentPanel.add(regPetPanel, "PETS");
        bottomContentPanel.add(aboutPanel, "ABOUT");
        
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 10);
        rightPanel.add(bottomContentPanel, gbc); 

        // Add Right Panel to Content Panel
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2; 
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(rightPanel, gbc);

        // Bottom-filler
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        contentPanel.add(new JLabel(), gbc);

        add(mainPanel);
    }

    // Create a panel that matches the image layout with 3 images in a purple bordered container with scroll
    private JPanel createImageGalleryPanel() {
        JPanel galleryPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw purple border similar to the image
                g2d.setColor(new Color(138, 43, 226)); // Purple color
                g2d.setStroke(new BasicStroke(3.0f)); // Thick border
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                
                g2d.dispose();
            }
        };
        
        // Create inner content panel that will hold the images
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        contentPanel.setOpaque(false);
        
        // Add 3 unique image placeholders
        for (int i = 0; i < 3; i++) {
            ImagePlaceholder imagePlaceholder = createUniqueImagePlaceholder(160, 140);
            contentPanel.add(imagePlaceholder);
        }
        
        // Create scroll pane for the content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null); // Remove border for cleaner look
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Customize scroll bar appearance
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        // Set up the main gallery panel layout
        galleryPanel.setLayout(new BorderLayout());
        galleryPanel.setOpaque(false);
        galleryPanel.setPreferredSize(new Dimension(600, 220)); // Slightly taller for scroll bars
        galleryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Inner padding
        
        // Add scroll pane to the gallery panel
        galleryPanel.add(scrollPane, BorderLayout.CENTER);
        
        return galleryPanel;
    }

    // Create unique ImagePlaceholder instances to avoid duplication
    private ImagePlaceholder createUniqueImagePlaceholder(int width, int height) {
        return new ImagePlaceholder(width, height);
    }

    private void updateInfoLabel() {
        String infoText = "<html><ul style='margin-left: 18px; padding: 0;'>"
                + "<li style='margin-bottom: 5px;'>" + userEmail + "</li>"
                + "<li style='margin-bottom: 5px;'>" + userContact + "</li>"
                + "<li>" + userAddress + "</li>"
                + "</ul></html>";
        infoLabel.setText(infoText);
    }

    public void updateProfile(String newName, String newEmail, String newContact, String newAddress) {
        this.userName = newName;
        this.userEmail = newEmail;
        this.userContact = newContact;
        this.userAddress = newAddress;
        nameLabel.setText(newName);
        updateInfoLabel();
    }

    public void updateProfileImage(Image newImage) {
        // Update shared image
        sharedProfileImage = newImage;
        
        // Update main profile image
        if (largeImage != null) {
            largeImage.setImage(newImage);
        }
        
        // Update embedded profile image
        if (embeddedImagePlaceholder != null) {
            embeddedImagePlaceholder.setImage(newImage);
        }
    }
    
    // Get shared profile image
    public static Image getSharedProfileImage() {
        return sharedProfileImage;
    }

    // Add method to highlight/unhighlight buttons
    private void highlightButton(JButton button, boolean highlight) {
        if (highlight) {
            button.setForeground(new Color(34, 177, 76)); // Green color for active
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18)); // Slightly larger and bold
            button.setOpaque(true);
            button.setBackground(new Color(240, 255, 240)); // Light green background
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 177, 76), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else {
            button.setForeground(Color.BLACK); // Default color
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16)); // Normal size
            button.setOpaque(false);
            button.setBackground(null);
            button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12)); // Maintain spacing
        }
        button.repaint();
    }

    // This method is now only for the text-like buttons
    private void styleTransparentButton(JButton button, Color foreground) {
        button.setForeground(foreground);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12)); // Add padding
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("EDIT PROFILE".equals(command)) {
            EditProfileDialog dialog = new EditProfileDialog(this, userName, userEmail, userContact, userAddress);
            dialog.setVisible(true);
        } else if ("REGISTERED PET".equals(command)) {
            // Update button highlights
            highlightButton(regPetButton, true);
            highlightButton(aboutButton, false);
            bottomCardLayout.show(bottomContentPanel, "PETS");
        } else if ("ABOUT".equals(command)) {
            // Update button highlights
            highlightButton(regPetButton, false);
            highlightButton(aboutButton, true);
            bottomCardLayout.show(bottomContentPanel, "ABOUT");
        } else {
            JOptionPane.showMessageDialog(this,
                "'" + command + "' button clicked!",
                "Button Clicked",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Add method to get the main content panel for embedding
    public JPanel getMainContentPanel() {
        // Create a completely new panel structure for dashboard embedding
        Color contentBgColor = new Color(248, 249, 250);
        
        JPanel embeddedPanel = new GradientPanel(new GridBagLayout());
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(contentBgColor);
        contentPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        embeddedPanel.add(contentPanel, gbc);

        // Create new components for embedded version to avoid duplication
        createEmbeddedComponents(contentPanel, contentBgColor);
        
        return embeddedPanel;
    }

    // Create components specifically for the embedded version
    private void createEmbeddedComponents(JPanel contentPanel, Color contentBgColor) {
        GridBagConstraints gbc;

        // --- Top Buttons ---
        JButton editProfileButton = new RoundedButton("EDIT PROFILE", new Color(34, 177, 76), Color.WHITE);
        editProfileButton.addActionListener(this); 

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        contentPanel.add(editProfileButton, gbc);

        // --- Left Panel (Large Image) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(contentBgColor);

        embeddedImagePlaceholder = createUniqueImagePlaceholder(350, 350);
        // Set shared image if it exists
        if (sharedProfileImage != null) {
            embeddedImagePlaceholder.setImage(sharedProfileImage);
        }
        embeddedImagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(embeddedImagePlaceholder);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(20, 10, 10, 20);
        contentPanel.add(leftPanel, gbc);

        // --- Right Panel ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(contentBgColor);

        // Name
        JLabel embeddedNameLabel = new JLabel(userName);
        embeddedNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        embeddedNameLabel.setForeground(Color.BLACK);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 10);
        rightPanel.add(embeddedNameLabel, gbc);
        
        // Contact Info
        JLabel embeddedInfoLabel = new JLabel();
        String infoText = "<html><ul style='margin-left: 18px; padding: 0;'>"
                + "<li style='margin-bottom: 5px;'>" + userEmail + "</li>"
                + "<li style='margin-bottom: 5px;'>" + userContact + "</li>"
                + "<li>" + userAddress + "</li>"
                + "</ul></html>";
        embeddedInfoLabel.setText(infoText);
        embeddedInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        embeddedInfoLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        rightPanel.add(embeddedInfoLabel, gbc);

        // Separator Line
        JSeparator separator = new JSeparator();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        rightPanel.add(separator, gbc);

        // Section Buttons
        JPanel greyButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        greyButtonsPanel.setBackground(contentBgColor);
        
        JButton embeddedRegPetButton = new JButton("REGISTERED PET");
        styleTransparentButton(embeddedRegPetButton, Color.BLACK); 
        
        JButton embeddedAboutButton = new JButton("ABOUT");
        styleTransparentButton(embeddedAboutButton, Color.BLACK); 

        // Set initial highlight state
        highlightButton(embeddedRegPetButton, true);
        highlightButton(embeddedAboutButton, false);

        greyButtonsPanel.add(embeddedRegPetButton);
        greyButtonsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        greyButtonsPanel.add(embeddedAboutButton);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 20, 10);
        rightPanel.add(greyButtonsPanel, gbc);

        // Gallery panels
        JPanel embeddedRegPetPanel = createImageGalleryPanel();
        embeddedRegPetPanel.setBackground(contentBgColor);

        JPanel embeddedAboutPanel = createImageGalleryPanel();
        embeddedAboutPanel.setBackground(contentBgColor);

        CardLayout embeddedBottomCardLayout = new CardLayout();
        JPanel embeddedBottomContentPanel = new JPanel(embeddedBottomCardLayout);
        embeddedBottomContentPanel.setBackground(contentBgColor);
        
        embeddedBottomContentPanel.add(embeddedRegPetPanel, "PETS");
        embeddedBottomContentPanel.add(embeddedAboutPanel, "ABOUT");
        
        // Add action listeners for embedded buttons
        embeddedRegPetButton.addActionListener(e -> {
            highlightButton(embeddedRegPetButton, true);
            highlightButton(embeddedAboutButton, false);
            embeddedBottomCardLayout.show(embeddedBottomContentPanel, "PETS");
        });
        
        embeddedAboutButton.addActionListener(e -> {
            highlightButton(embeddedRegPetButton, false);
            highlightButton(embeddedAboutButton, true);
            embeddedBottomCardLayout.show(embeddedBottomContentPanel, "ABOUT");
        });
        
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 10);
        rightPanel.add(embeddedBottomContentPanel, gbc); 

        // Add Right Panel to Content Panel
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2; 
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(rightPanel, gbc);

        // Bottom-filler
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        contentPanel.add(new JLabel(), gbc);
    }

    public static void main(String[] args) {
        try {
            // Set Nimbus Look and Feel for a modern aesthetic
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Failed to set Nimbus Look and Feel. Using default.");
            // If Nimbus isn't available, it will fall back to the default
        }

        SwingUtilities.invokeLater(() -> {
            UserProfile frame = new UserProfile();
            frame.setVisible(true);
        });
    }

    // =================================================================
    // INNER CLASS FOR THE EDIT PROFILE DIALOG
    // =================================================================
    class EditProfileDialog extends JDialog implements ActionListener {

        private JTextField nameField;
        private JTextField emailField;
        private JTextField contactField;
        private JTextField addressField;
        private JLabel imagePreviewLabel;
        private ImageIcon selectedProfileImage = null;
        private UserProfile owner;

        public EditProfileDialog(UserProfile owner, String name, String email, String contact, String address) {
            super(owner, "Edit Profile", true);
            this.owner = owner;
            
            // Create gradient background panel instead of using default content pane
            JPanel gradientBackgroundPanel = new GradientPanel(new GridBagLayout());
            setContentPane(gradientBackgroundPanel);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 15, 15, 15);
            gbc.anchor = GridBagConstraints.WEST;

            // --- Form Fields ---
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gradientBackgroundPanel.add(nameLabel, gbc);
            
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            nameField = new JTextField(name, 30);
            nameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            gradientBackgroundPanel.add(nameField, gbc);

            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            gradientBackgroundPanel.add(emailLabel, gbc);
            
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            emailField = new JTextField(email, 30);
            emailField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            gradientBackgroundPanel.add(emailField, gbc);

            JLabel contactLabel = new JLabel("Contact:");
            contactLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            gbc.gridx = 0;
            gbc.gridy = 2;
            gradientBackgroundPanel.add(contactLabel, gbc);
            
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            contactField = new JTextField(contact, 30);
            contactField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            gradientBackgroundPanel.add(contactField, gbc);

            JLabel addressLabel = new JLabel("Address:");
            addressLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            gbc.gridx = 0;
            gbc.gridy = 3;
            gradientBackgroundPanel.add(addressLabel, gbc);
            
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            addressField = new JTextField(address, 30);
            addressField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            gradientBackgroundPanel.add(addressField, gbc);

            // --- Image Preview Label ---
            JLabel previewLabel = new JLabel("Image Preview:");
            previewLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gradientBackgroundPanel.add(previewLabel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            imagePreviewLabel = new JLabel("No image selected.");
            imagePreviewLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            imagePreviewLabel.setPreferredSize(new Dimension(150, 150));
            imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePreviewLabel.setOpaque(true);
            imagePreviewLabel.setBackground(Color.WHITE); // Keep preview label background white for better contrast
            gradientBackgroundPanel.add(imagePreviewLabel, gbc);

            // --- Upload Image Button (in dialog) ---
            JButton uploadButton = new JButton("UPLOAD IMAGE");
            uploadButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            styleTransparentButton(uploadButton, Color.GRAY);
            gbc.gridx = 1;
            gbc.gridy = 5; 
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            gradientBackgroundPanel.add(uploadButton, gbc);
            uploadButton.addActionListener(this); 

            // --- Save Changes Button (Using new RoundedButton) ---
            JButton saveButton = new RoundedButton("SAVE CHANGES", new Color(34, 177, 76), Color.WHITE);
            gbc.gridx = 0; 
            gbc.gridwidth = 2; 
            gbc.gridy = 6; 
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE; 
            gradientBackgroundPanel.add(saveButton, gbc);
            saveButton.addActionListener(this); 

            pack(); 
            setLocationRelativeTo(owner); 
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            
            if ("SAVE CHANGES".equals(command)) {
                String newName = nameField.getText();
                String newEmail = emailField.getText();
                String newContact = contactField.getText();
                String newAddress = addressField.getText();
                owner.updateProfile(newName, newEmail, newContact, newAddress);

                if (selectedProfileImage != null) {
                    owner.updateProfileImage(selectedProfileImage.getImage());
                }
                dispose();
                
            } else if ("UPLOAD IMAGE".equals(command)) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files (jpg, png, gif)", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);
                
                int returnValue = fileChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedProfileImage = new ImageIcon(selectedFile.getAbsolutePath());
                    
                    Image scaledImage = selectedProfileImage.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                    imagePreviewLabel.setText(null); 
                    
                    // Immediately update the profile image for preview
                    owner.updateProfileImage(selectedProfileImage.getImage());
                }
            }
        }
    }
}