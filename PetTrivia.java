import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple Java Swing application to view pet facts with an image placeholder
 * and a description.
 * This version features a modern, dark-mode aesthetic with a gradient
 * background and a "card" layout.
 */
public class PetTrivia extends JFrame {

    // --- Modern Color Palette ---
    private static final Color COLOR_BACKGROUND = new Color(0xFF6B6B); // Happy coral/pink
    private static final Color COLOR_BACKGROUND_MID = new Color(0x4ECDC4); // Turquoise
    private static final Color COLOR_BACKGROUND_END = new Color(0x45B7D1); // Sky blue
    private static final Color COLOR_FOREGROUND = new Color(0xFFFFFF); // Pure white for contrast
    private static final Color COLOR_PANEL = new Color(255, 255, 255, 230);      // Semi-transparent white
    private static final Color COLOR_IMAGE_BG = new Color(0xF8F9FA);    // Light gray
    private static final Color COLOR_ACCENT = new Color(0xFF6B35);      // Orange
    private static final Color COLOR_ACCENT_HOVER = new Color(0xE85A2B); // Darker orange

    // --- Core App State ---
    private List<Fact> facts;
    private int currentFactIndex;
    private Timer autoAdvanceTimer; // Add timer for automatic advancement

    // --- UI Components ---
    private JLabel titleLabel;
    private JLabel imagePlaceholderLabel;
    private JLabel descriptionLabel;
    private JButton nextButton; // Will be our RoundedButton
    private RoundedPanel contentCard; // Add this field

    /**
     * Inner class to hold the data for a single fact.
     */
    private static class Fact {
        String description;
        String imagePath; // MODIFIED: This now holds the path to the image file

        public Fact(String description, String imagePath) {
            this.description = description;
            this.imagePath = imagePath;
        }
    }

    /**
     * Constructor: Sets up the main window and initializes the app.
     */
    public PetTrivia() {
        // --- Set up the main window (JFrame) ---
        setTitle("Pet Fact Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the window

        // --- Use a Gradient Background Panel ---
        setContentPane(new GradientPanel());
        setLayout(new BorderLayout(10, 10));

        // --- Initialize UI Components ---

        // 1. Title Label (Top)
        titleLabel = new JLabel("Pet Facts", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36)); // Even bigger
        titleLabel.setForeground(COLOR_FOREGROUND);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10)); // More top padding
        add(titleLabel, BorderLayout.NORTH);

        // 2. Main Content Card (Center)
        // This panel will hold the image and description
        contentCard = new RoundedPanel(30); // Remove RoundedPanel declaration, just assign
        contentCard.setBackground(COLOR_PANEL);
        contentCard.setLayout(new BorderLayout(10, 10));
        // Add padding *inside* the card
        contentCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentCard.setMaximumSize(new Dimension(800, 600)); // Max card width
        contentCard.setPreferredSize(new Dimension(800, 600)); // Preferred card size


        // 2a. Image Placeholder (Inside Card - Center)
        // This JLabel will now hold our scaled image
        imagePlaceholderLabel = new JLabel("Loading image...", SwingConstants.CENTER);
        imagePlaceholderLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
        imagePlaceholderLabel.setOpaque(true);
        imagePlaceholderLabel.setBackground(COLOR_IMAGE_BG); // Darker placeholder BG
        imagePlaceholderLabel.setForeground(new Color(0x333333)); // Dark gray for visibility
        Border lineBorder = BorderFactory.createLineBorder(COLOR_PANEL.brighter(), 1);
        imagePlaceholderLabel.setBorder(lineBorder);
        contentCard.add(imagePlaceholderLabel, BorderLayout.CENTER);


        // 2b. Description Label (Inside Card - Bottom)
        descriptionLabel = new JLabel("Loading fact...", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24)); // Bigger font
        descriptionLabel.setForeground(new Color(0x333333)); // Dark gray for visibility
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        contentCard.add(descriptionLabel, BorderLayout.SOUTH);

        // --- Panel to center the card ---
        // This panel uses GridBagLayout to force the card to the center
        // of the screen, and it is transparent.
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Transparent
        centerPanel.add(contentCard);
        add(centerPanel, BorderLayout.CENTER);


        // 3. Bottom Panel (Button)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false); // Make panel transparent
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20)); // More bottom padding

        // Next Button (Using our new RoundedButton)
        nextButton = new RoundedButton("Next Fact");
        bottomPanel.add(nextButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Add Action Listener to the Button ---
        nextButton.addActionListener(new NextButtonListener());

        // --- Initialize Timer for Auto-Advance ---
        autoAdvanceTimer = new Timer(5000, new AutoAdvanceListener()); // 5 seconds

        // --- Initialize and Start the App ---
        initializeFacts();
        startApp();
    }

    /**
     * Populates the list of 10 trivia facts.
     *
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * IMPORTANT: Create a folder named "images" where your compiled .class
     * file is. Place your 10 images in that folder and make sure their
     * names match the paths below (e.g., "dog_nose.png", "cat_candy.png", etc.).
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    private void initializeFacts() {
        facts = new ArrayList<>();
        facts.add(new Fact(
            "A dog's sense of smell is 10,000 to 100,000 times more powerful than a human's.",
            "SYSTEM/image/dognose.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "Cats do not have taste receptors for sweet flavors.",
            "SYSTEM/image/catcandy.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "A goldfish's memory span is at least three months, not just a few seconds.",
            "SYSTEM/image/goldfish.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "The name 'hamster' comes from the German word 'hamstern,' which means 'to hoard'.",
            "SYSTEM/image/hamster.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "Parrots (like African Greys) can mimic household sounds, like microwaves and phones.",
            "SYSTEM/image/parrot.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "The Basenji dog breed is famous for not barking. Instead, it makes a yodel-like sound.",
            "SYSTEM/image/dog.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "A cat's nose print is unique, much like a human's fingerprint.",
            "SYSTEM/image/catnose.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "A rabbit's vision is nearly 360 degrees, allowing them to see predators from almost any angle.",
            "SYSTEM/image/rabbit.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "Guinea pigs are not from Guinea; they are native to the Andes mountains in South America.",
            "SYSTEM/image/guinea pig.jpg" // UPDATED
        ));
        facts.add(new Fact(
            "A cat's purr vibrates at a frequency that can promote bone density and healing.",
            "SYSTEM/image/cat-purring.jpeg" // UPDATED
        ));
    }

    /**
     * Resets variables and loads the first fact.
     */
    private void startApp() {
        currentFactIndex = 0;
        Collections.shuffle(facts); // Randomize the fact order
        loadFact(currentFactIndex);
        
        // Start the auto-advance timer
        if (autoAdvanceTimer != null) {
            autoAdvanceTimer.start();
        }
    }

    /**
     * Loads the fact, description, and image placeholder for the given index.
     */
    private void loadFact(int index) {
        // Add safety checks
        if (facts == null || facts.isEmpty() || index < 0 || index >= facts.size()) {
            descriptionLabel.setText("No facts available.");
            imagePlaceholderLabel.setIcon(null);
            imagePlaceholderLabel.setText("No image available");
            return;
        }
        
        Fact f = facts.get(index);
        if (f == null) {
            descriptionLabel.setText("Fact not available.");
            imagePlaceholderLabel.setIcon(null);
            imagePlaceholderLabel.setText("No image available");
            return;
        }

        // Use HTML for automatic word wrapping and set the text color
        descriptionLabel.setText("<html><body style='width: 600px; text-align: center; color: #333333;'>"
            + f.description + "</body></html>");

        // --- THIS IS WHERE THE IMAGE IS LOADED ---
        // Use robust loader that tries classpath and several file locations
        ImageIcon imageIcon = loadImageIcon(f.imagePath);

        // 2. Check if the image was found
        if (imageIcon == null || imageIcon.getIconWidth() == -1) {
            // Error: Image not found
            imagePlaceholderLabel.setIcon(null); // Remove old icon
            imagePlaceholderLabel.setText("Image not found: " + f.imagePath);
        } else {
            // Success: Scale the image and set it
            // We scale it to fit inside the label's parent card (minus padding)
            // Add null check for contentCard
            int labelWidth = (contentCard != null) ? contentCard.getPreferredSize().width - 40 : 760; // Default fallback
            int labelHeight = (contentCard != null) ? (int)(contentCard.getPreferredSize().height * 0.7) : 420; // Default fallback

            Icon scaledIcon = scaleImageIcon(imageIcon, labelWidth, labelHeight);
            imagePlaceholderLabel.setText(null); // Remove text
            imagePlaceholderLabel.setIcon(scaledIcon); // Set the new scaled image
        }
        
        // Update button text
        if (currentFactIndex == facts.size() - 1) {
            nextButton.setText("Restart");
        } else {
            nextButton.setText("Next Fact");
        }
    }

    /**
     * NEW HELPER METHOD
     * Scales an ImageIcon to fit within a target width and height,
     * maintaining its original aspect ratio.
     *
     * @param icon The source ImageIcon.
     * @param targetWidth The maximum width of the new icon.
     * @param targetHeight The maximum height of the new icon.
     * @return A new, scaled Icon.
     */
    private Icon scaleImageIcon(ImageIcon icon, int targetWidth, int targetHeight) {
        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();

        // If the image is already smaller than the target, no need to scale up
        if (originalWidth <= targetWidth && originalHeight <= targetHeight) {
            return icon;
        }

        // Calculate the new dimensions while maintaining aspect ratio
        double widthRatio = (double) targetWidth / (double) originalWidth;
        double heightRatio = (double) targetHeight / (double) originalHeight;
        double ratio = Math.min(widthRatio, heightRatio); // Use the smaller ratio to fit inside

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        // Get the Image object from the ImageIcon
        Image image = icon.getImage();

        // Create a new scaled Image
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        // Return the scaled image as a new ImageIcon
        return new ImageIcon(scaledImage);
    }


    /**
     * Main action listener for the "Next" / "Restart" button.
     */
    private class NextButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (facts == null || facts.isEmpty()) {
                return; // Do nothing if no facts are available
            }
            
            // Restart the timer when user manually clicks
            if (autoAdvanceTimer != null) {
                autoAdvanceTimer.restart();
            }
            
            advanceToNextFact();
        }
    }

    /**
     * Auto-advance listener for the timer.
     */
    private class AutoAdvanceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (facts == null || facts.isEmpty()) {
                return; // Do nothing if no facts are available
            }
            
            advanceToNextFact();
        }
    }

    /**
     * Helper method to advance to the next fact.
     */
    private void advanceToNextFact() {
        currentFactIndex++;
        if (currentFactIndex >= facts.size()) {
            // If we've shown all facts, restart
            startApp();
        } else {
            // Otherwise, load the next fact
            loadFact(currentFactIndex);
        }
    }

    /**
     * NEW METHOD: Creates a JPanel version of PetTrivia for embedding in Dashboard
     * instead of opening as a separate window.
     */
    public static JPanel createTriviaPanel() {
        // Create a wrapper panel with gradient background
        JPanel wrapperPanel = new GradientPanel();
        wrapperPanel.setLayout(new BorderLayout(10, 10));

        // Create an instance of PetTrivia to access its methods and components
        PetTrivia triviaInstance = new PetTrivia();
        
        // Extract the content from the PetTrivia frame
        Container contentPane = triviaInstance.getContentPane();
        
        // Create new components similar to the original but for panel use
        JLabel titleLabel = new JLabel("Pet Facts", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        titleLabel.setForeground(COLOR_FOREGROUND);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));
        wrapperPanel.add(titleLabel, BorderLayout.NORTH);

        // Create the main content card
        RoundedPanel contentCard = new RoundedPanel(30);
        contentCard.setBackground(COLOR_PANEL);
        contentCard.setLayout(new BorderLayout(10, 10));
        contentCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentCard.setMaximumSize(new Dimension(800, 600));
        contentCard.setPreferredSize(new Dimension(800, 600));

        // Image placeholder
        JLabel imagePlaceholderLabel = new JLabel("Loading image...", SwingConstants.CENTER);
        imagePlaceholderLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
        imagePlaceholderLabel.setOpaque(true);
        imagePlaceholderLabel.setBackground(COLOR_IMAGE_BG);
        imagePlaceholderLabel.setForeground(new Color(0x333333));
        Border lineBorder = BorderFactory.createLineBorder(COLOR_PANEL.brighter(), 1);
        imagePlaceholderLabel.setBorder(lineBorder);
        contentCard.add(imagePlaceholderLabel, BorderLayout.CENTER);

        // Description label
        JLabel descriptionLabel = new JLabel("Loading fact...", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        descriptionLabel.setForeground(new Color(0x333333));
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        contentCard.add(descriptionLabel, BorderLayout.SOUTH);

        // Center panel for the card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(contentCard);
        wrapperPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));

        RoundedButton nextButton = new RoundedButton("Next Fact");
        bottomPanel.add(nextButton);
        wrapperPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Initialize trivia data and functionality
        List<Fact> facts = new ArrayList<>();
        initializePanelFacts(facts);
        
        final int[] currentFactIndex = {0};
        Timer autoAdvanceTimer = new Timer(5000, null);
        
        // Set up the auto-advance and manual advance functionality
        ActionListener advanceAction = e -> {
            currentFactIndex[0]++;
            if (currentFactIndex[0] >= facts.size()) {
                currentFactIndex[0] = 0;
                Collections.shuffle(facts);
            }
            loadPanelFact(facts.get(currentFactIndex[0]), imagePlaceholderLabel, descriptionLabel, contentCard, nextButton, currentFactIndex[0], facts.size());
            if (autoAdvanceTimer.isRunning()) {
                autoAdvanceTimer.restart();
            }
        };
        
        autoAdvanceTimer.addActionListener(advanceAction);
        nextButton.addActionListener(advanceAction);
        
        // Load first fact
        Collections.shuffle(facts);
        loadPanelFact(facts.get(0), imagePlaceholderLabel, descriptionLabel, contentCard, nextButton, 0, facts.size());
        autoAdvanceTimer.start();

        return wrapperPanel;
    }

    private static void initializePanelFacts(List<Fact> facts) {
        facts.add(new Fact(
            "A dog's sense of smell is 10,000 to 100,000 times more powerful than a human's.",
            "SYSTEM/image/dognose.jpg"
        ));
        facts.add(new Fact(
            "Cats do not have taste receptors for sweet flavors.",
            "SYSTEM/image/catcandy.jpg"
        ));
        facts.add(new Fact(
            "A goldfish's memory span is at least three months, not just a few seconds.",
            "SYSTEM/image/goldfish.jpg"
        ));
        facts.add(new Fact(
            "The name 'hamster' comes from the German word 'hamstern,' which means 'to hoard'.",
            "SYSTEM/image/hamster.jpg"
        ));
        facts.add(new Fact(
            "Parrots (like African Greys) can mimic household sounds, like microwaves and phones.",
            "SYSTEM/image/parrot.jpg"
        ));
        facts.add(new Fact(
            "The Basenji dog breed is famous for not barking. Instead, it makes a yodel-like sound.",
            "SYSTEM/image/dog.jpg"
        ));
        facts.add(new Fact(
            "A cat's nose print is unique, much like a human's fingerprint.",
            "SYSTEM/image/catnose.jpg"
        ));
        facts.add(new Fact(
            "A rabbit's vision is nearly 360 degrees, allowing them to see predators from almost any angle.",
            "SYSTEM/image/rabbit.jpg"
        ));
        facts.add(new Fact(
            "Guinea pigs are not from Guinea; they are native to the Andes mountains in South America.",
            "SYSTEM/image/guinea pig.jpg"
        ));
        facts.add(new Fact(
            "A cat's purr vibrates at a frequency that can promote bone density and healing.",
            "SYSTEM/image/cat-purring.jpeg"
        ));
    }

    private static void loadPanelFact(Fact fact, JLabel imagePlaceholderLabel, JLabel descriptionLabel, 
                                    RoundedPanel contentCard, RoundedButton nextButton, 
                                    int currentIndex, int totalFacts) {
        if (fact == null) {
            descriptionLabel.setText("Fact not available.");
            imagePlaceholderLabel.setIcon(null);
            imagePlaceholderLabel.setText("No image available");
            return;
        }

        // Set description with HTML for word wrapping
        descriptionLabel.setText("<html><body style='width: 600px; text-align: center; color: #333333;'>"
            + fact.description + "</body></html>");

        // Load and scale image (robust loader)
        ImageIcon imageIcon = loadImageIconStatic(fact.imagePath);

        if (imageIcon == null || imageIcon.getIconWidth() == -1) {
            imagePlaceholderLabel.setIcon(null);
            imagePlaceholderLabel.setText("Image not found: " + fact.imagePath);
        } else {
            int labelWidth = (contentCard != null) ? contentCard.getPreferredSize().width - 40 : 760;
            int labelHeight = (contentCard != null) ? (int)(contentCard.getPreferredSize().height * 0.7) : 420;
            Icon scaledIcon = scaleImageIconStatic(imageIcon, labelWidth, labelHeight);
            imagePlaceholderLabel.setText(null);
            imagePlaceholderLabel.setIcon(scaledIcon);
        }
        
        // Update button text
        if (currentIndex == totalFacts - 1) {
            nextButton.setText("Restart");
        } else {
            nextButton.setText("Next Fact");
        }
    }

    private static Icon scaleImageIconStatic(ImageIcon icon, int targetWidth, int targetHeight) {
        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();

        if (originalWidth <= targetWidth && originalHeight <= targetHeight) {
            return icon;
        }

        double widthRatio = (double) targetWidth / (double) originalWidth;
        double heightRatio = (double) targetHeight / (double) originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

    /**
     * Attempts to load an ImageIcon using several fallbacks:
     * 1) Classpath resource (e.g. resources bundled in JAR under /image/...)
     * 2) Direct file path as given
     * 3) Relative to user.dir
     * 4) Try with a leading "image/" prefix
     */
    private ImageIcon loadImageIcon(String path) {
        if (path == null) return null;

        // 1) Try loading as classpath resource
        try {
            String resourcePath = path.startsWith("/") ? path.substring(1) : path;
            java.net.URL url = getClass().getClassLoader().getResource(resourcePath);
            if (url != null) {
                return new ImageIcon(url);
            }
        } catch (Exception ignored) {}

        // 2) Try direct file path
        try {
            java.io.File f = new java.io.File(path);
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath());
            }
        } catch (Exception ignored) {}

        // 3) Try relative to working directory
        try {
            java.io.File wd = new java.io.File(System.getProperty("user.dir"));
            java.io.File f = new java.io.File(wd, path);
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath());
            }
        } catch (Exception ignored) {}

        // 4) Try prefixing with "image/" (common project layout)
        try {
            java.io.File f = new java.io.File("image" + java.io.File.separator + new java.io.File(path).getName());
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath());
            }
        } catch (Exception ignored) {}

        // Not found
        return null;
    }

    // Static variant used by static helper methods
    private static ImageIcon loadImageIconStatic(String path) {
        if (path == null) return null;

        // 1) Try classloader resource
        try {
            String resourcePath = path.startsWith("/") ? path.substring(1) : path;
            java.net.URL url = PetTrivia.class.getClassLoader().getResource(resourcePath);
            if (url != null) return new ImageIcon(url);
        } catch (Exception ignored) {}

        // 2) Direct file
        try {
            java.io.File f = new java.io.File(path);
            if (f.exists()) return new ImageIcon(f.getAbsolutePath());
        } catch (Exception ignored) {}

        // 3) Relative to user.dir
        try {
            java.io.File wd = new java.io.File(System.getProperty("user.dir"));
            java.io.File f = new java.io.File(wd, path);
            if (f.exists()) return new ImageIcon(f.getAbsolutePath());
        } catch (Exception ignored) {}

        // 4) Try image/ prefix
        try {
            java.io.File f = new java.io.File("image" + java.io.File.separator + new java.io.File(path).getName());
            if (f.exists()) return new ImageIcon(f.getAbsolutePath());
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * A custom JPanel with a gradient background.
     */
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Create a multi-stop gradient for a happy, vibrant effect
            float[] fractions = {0.0f, 0.5f, 1.0f};
            Color[] colors = {COLOR_BACKGROUND, COLOR_BACKGROUND_MID, COLOR_BACKGROUND_END};
            
            // Diagonal gradient from top-left to bottom-right for more dynamic look
            LinearGradientPaint gradient = new LinearGradientPaint(
                0, 0, w, h, fractions, colors
            );
            
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, w, h);
        }
    }

    /**
     * A custom JPanel with rounded corners.
     */
    private static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false); // We will paint our own background
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint the rounded rectangle background
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            
            g2.dispose();
        }
    }


    /**
     * A custom JButton with rounded corners and hover effects.
     */
    private static class RoundedButton extends JButton {
        
        public RoundedButton(String text) {
            super(text);
            
            // Set basic style
            setBackground(COLOR_ACCENT);
            setForeground(Color.WHITE);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            
            // Add padding *inside* the button
            setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
            
            // Remove all default painting
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);

            // Add hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(COLOR_ACCENT_HOVER);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(COLOR_ACCENT);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint the rounded rectangle background
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground()); // Use the color set by MouseListener
            } else {
                g2.setColor(getBackground());
            }
            
            // 30px for arc width and height
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
            
            g2.dispose();
            
            // Let the button paint its text (label) on top of our background
            super.paintComponent(g); 
        }
    }

    /**
     * Main method to run the application.
     * Ensures the GUI is created on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        // Swing best practice: run UI code on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PetTrivia().setVisible(true);
            }
        });
    }
}