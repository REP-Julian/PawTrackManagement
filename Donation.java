import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class Donation extends JFrame {

    // --- CONFIGURATION ---
    // 1. Main Header Image
    private static final String DEFAULT_IMAGE_PATH = "image/DONATION.1.png"; 
    
    // 2. QR Code Images (Put your file paths here)
    private static final String GCASH_QR_PATH = "image/GCASH.png";      
    private static final String MAYA_QR_PATH = "image/MAYA.png";       

    // --- THEME COLORS ---
    // NEW GRADIENT: "Neon Twilight" (Deep Violet to Hot Pink)
    private static final Color GRADIENT_START = new Color(45, 10, 65);   // Deep Violet
    private static final Color GRADIENT_END = new Color(200, 40, 110);   // Vibrant Pink/Magenta

    private static final Color ACCENT_PINK = new Color(255, 180, 210);   // Pastel Pink for highlights
    private static final Color TEXT_PRIMARY = new Color(255, 245, 250);  // White with slight pink tint
    private static final Color TEXT_SECONDARY = new Color(230, 200, 220); // Soft pink-grey

    // UI Components
    private JPanel mainContentPanel;
    private JLabel imageDisplayLabel;

    // --- SLIDESHOW CONFIG ---
    // Add as many image paths as you want; will cycle every 5 seconds.
    private String[] slideshowImagePaths = new String[] {
        DEFAULT_IMAGE_PATH,
        "image/DONATION.2.png",
        "image/DONATION.3.png",
        "image/DONATION.4.png"
    };
    private int currentImageIndex = 0;
    private javax.swing.Timer slideshowTimer;
    
    public Donation() {
        // 1. Main Frame Setup (keep frame config but reuse builder)
        setTitle("Sanctuary | Wildlife Rescue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Build the donation panel and set as content for standalone window usage
        JPanel panel = createDonationPanel();
        setContentPane(panel);

        // Load Default Image
        loadDefaultImage();

        // Start slideshow for standalone use
        startSlideshow();
    }

    // New public method: returns a fully-built Donation panel suitable for embedding
    public JPanel createDonationPanel() {
        BackgroundPanel bgPanel = new BackgroundPanel();
        bgPanel.setLayout(new BorderLayout());

        // 3. Main Scroll Layout (create/assign mainContentPanel so helpers work)
        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(50, 0, 50, 0));

        // 4. Add Sections
        addSection(createHeaderSection());
        addSection(createImageSection());
        addSection(createDescriptionSection());
        addSection(createDonationSection());

        // 5. Custom ScrollPane (Invisible)
        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        bgPanel.add(scrollPane, BorderLayout.CENTER);

        // Ensure the image label gets populated if a path is configured
        loadDefaultImage();

        // Start slideshow when panel is created (safe-guard to not start multiple timers)
        startSlideshow();

        return bgPanel;
    }

    // Helper to center sections
    private void addSection(JComponent section) {
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContentPanel.add(section);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 40))); // More spacing
    }

    // --- SECTION 1: HEADER ---
    private JPanel createHeaderSection() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        
        JLabel title = new JLabel("WILDLIFE SANCTUARY");
        title.setFont(new Font("Serif", Font.BOLD, 42)); 
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel(" — Preserving Nature, One Life at a Time — ");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(ACCENT_PINK); 
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        header.add(subtitle);
        return header;
    }

    // --- SECTION 2: IMAGE DISPLAY ---
    private JPanel createImageSection() {
        ModernPanel container = new ModernPanel(900, 550);
        container.setLayout(new BorderLayout());
        
        imageDisplayLabel = new JLabel("No Image Source Configured", SwingConstants.CENTER);
        imageDisplayLabel.setForeground(new Color(255, 255, 255, 100));
        imageDisplayLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        container.add(imageDisplayLabel, BorderLayout.CENTER);
        return container;
    }

    // --- SECTION 3: DESCRIPTION ---
    private JPanel createDescriptionSection() {
        ModernPanel container = new ModernPanel(900, 350);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel title = new JLabel("Our Mission");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JTextArea descText = new JTextArea();
        descText.setText("We are dedicated to the rescue and rehabilitation of wild animals. " +
                "Our commitment to saving lives is being tested as our shelter reaches capacity, and we need your immediate help to ensure no animal is turned away. Every day, we witness the transformation of scared, injured strays into loving companions, but this work is impossible without the financial backing to cover veterinary bills, food, and shelter maintenance. We are asking you to open your heart and contribute knowing that your donation acts as a direct lifeline for an animal who has nowhere else to turn, giving them the second chance they so desperately deserve.");
        descText.setWrapStyleWord(true);
        descText.setLineWrap(true);
        descText.setEditable(false);
        descText.setFocusable(false);
        descText.setOpaque(false); 
        descText.setFont(new Font("SansSerif", Font.PLAIN,20)); 
        descText.setForeground(TEXT_SECONDARY);

        container.add(title, BorderLayout.NORTH);
        container.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.CENTER);
        container.add(descText, BorderLayout.CENTER);

        return container;
    }

    // --- SECTION 4: DONATION ---
    private JPanel createDonationSection() {
        ModernPanel container = new ModernPanel(900, 400);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(40, 0, 40, 0));

        JLabel title = new JLabel("Support The Cause");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subTitle = new JLabel("Scan to Donate");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subTitle.setForeground(ACCENT_PINK);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // QR Row
        JPanel qrRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 30));
        qrRow.setOpaque(false);

        qrRow.add(createQRCard("GCash", GCASH_QR_PATH));
        qrRow.add(createQRCard("Maya", MAYA_QR_PATH));

        container.add(title);
        container.add(subTitle);
        container.add(qrRow);

        return container;
    }

    private JPanel createQRCard(String appName, String path) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);

        // App Name
        JLabel name = new JLabel(appName);
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setForeground(TEXT_PRIMARY);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        // QR Box Frame
        JPanel qrFrame = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 20)); // Very subtle white bg
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(255, 255, 255, 50)); // Border
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        qrFrame.setOpaque(false);
        qrFrame.setPreferredSize(new Dimension(180, 180));
        qrFrame.setMaximumSize(new Dimension(180, 180));
        
        JLabel qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrFrame.add(qrLabel, BorderLayout.CENTER);

        // Load Logic
        boolean loaded = false;
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(f.getPath());
                Image img = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                qrLabel.setIcon(new ImageIcon(img));
                loaded = true;
            }
        }
        
        if (!loaded) {
            qrLabel.setText("<html><center>No QR<br>Path Found</center></html>");
            qrLabel.setForeground(new Color(255, 255, 255, 100));
        }

        card.add(qrFrame);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(name);
        
        return card;
    }

    // --- LOGIC: IMAGE LOADING ---
    private void loadDefaultImage() {
        if (DEFAULT_IMAGE_PATH != null && !DEFAULT_IMAGE_PATH.isEmpty()) {
            File imgFile = new File(DEFAULT_IMAGE_PATH);
            if (imgFile.exists()) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        ImageIcon icon = new ImageIcon(imgFile.getPath());
                        Image img = icon.getImage();
                        // Scale to fit card roughly
                        Image scaled = img.getScaledInstance(900, 550, Image.SCALE_SMOOTH);
                        if (imageDisplayLabel != null) {
                          imageDisplayLabel.setText("");
                          imageDisplayLabel.setIcon(new ImageIcon(scaled));
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                });
            }
        }
    }

    // --- SLIDESHOW: autoplay every 5 seconds ---
    private void startSlideshow() {
        if (slideshowTimer != null && slideshowTimer.isRunning()) return; // already running

        // Immediately show current index image
        showImageAtIndex(currentImageIndex);

        slideshowTimer = new javax.swing.Timer(5000, e -> {
            currentImageIndex = (currentImageIndex + 1) % slideshowImagePaths.length;
            showImageAtIndex(currentImageIndex);
        });
        slideshowTimer.setInitialDelay(5000);
        slideshowTimer.start();
    }

    private void stopSlideshow() {
        if (slideshowTimer != null) {
            slideshowTimer.stop();
            slideshowTimer = null;
        }
    }

    private void showImageAtIndex(int index) {
        if (slideshowImagePaths == null || slideshowImagePaths.length == 0) return;
        String path = slideshowImagePaths[index];
        if (path == null || path.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                if (imageDisplayLabel != null) {
                    imageDisplayLabel.setIcon(null);
                    imageDisplayLabel.setText("No Image Source Configured");
                }
            });
            return;
        }
        File imgFile = new File(path);
        if (imgFile.exists()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    ImageIcon icon = new ImageIcon(imgFile.getPath());
                    Image img = icon.getImage();
                    Image scaled = img.getScaledInstance(900, 550, Image.SCALE_SMOOTH);
                    if (imageDisplayLabel != null) {
                        imageDisplayLabel.setText("");
                        imageDisplayLabel.setIcon(new ImageIcon(scaled));
                    }
                } catch (Exception ignored) { }
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                if (imageDisplayLabel != null) {
                    imageDisplayLabel.setIcon(null);
                    imageDisplayLabel.setText("<html><center>No Image<br>Path Found</center></html>");
                    imageDisplayLabel.setForeground(new Color(255, 255, 255, 100));
                }
            });
        }
    }

    // --- CUSTOM UI CLASS: BACKGROUND PANEL (GRADIENT) ---
    class BackgroundPanel extends JPanel {
        public BackgroundPanel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            
            // Diagonal Gradient
            GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, w, h, GRADIENT_END);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

    // --- CUSTOM UI CLASS: MODERN "GLASS" PANEL ---
    class ModernPanel extends JPanel {
        private int width, height;

        public ModernPanel(int w, int h) {
            this.width = w;
            this.height = h;
            setOpaque(false);
            setMaximumSize(new Dimension(w, h));
            setPreferredSize(new Dimension(w, h));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Drop Shadow
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 40, 40);

            // 2. Background (Translucent Black/Glass)
            g2.setColor(new Color(0, 0, 0, 80)); // Slightly transparent
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

            // 3. Subtle Border/Glow (Pinkish Tint)
            g2.setColor(new Color(255, 192, 203, 40));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 40, 40);
            
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Donation app = new Donation();
            app.setVisible(true);
        });
    }
}