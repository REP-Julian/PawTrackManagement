import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

public class AboutUs {

    // --- DESIGN THEME: MIDNIGHT ELEGANCE ---
    
    // Deep, rich dark gradient for background
    private static final Color GRADIENT_START = new Color(20, 30, 48);  // Deep Navy
    private static final Color GRADIENT_END = new Color(36, 59, 85);    // Slate Blue

    // Dark card with high contrast text
    private static final Color CARD_COLOR = new Color(30, 35, 45);      // Dark Charcoal
    private static final Color TITLE_COLOR = new Color(255, 255, 255);  // Pure White
    private static final Color TEXT_COLOR = new Color(200, 210, 220);   // Soft Light Grey
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);   // Amber/Gold for contrast
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            }
            new AboutUs().createAndShowGUI();
        });
    }

    // New: static factory to create the About Us panel for embedding in Dashboard
    public static JPanel createAboutUsPanel() {
        // Background Gradient Panel (same look as createAndShowGUI)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, w, h, GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Card panel
        RoundedPanel cardPanel = new RoundedPanel(30, CARD_COLOR);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(50, 60, 50, 60));

        JLabel titleLabel = new JLabel("About Us");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accentLine = new JPanel();
        accentLine.setBackground(ACCENT_COLOR);
        accentLine.setMaximumSize(new Dimension(100, 5));
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel contentSplitPanel = new JPanel();
        contentSplitPanel.setLayout(new BoxLayout(contentSplitPanel, BoxLayout.X_AXIS));
        contentSplitPanel.setOpaque(false);
        contentSplitPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setFocusable(false);
        textPane.setOpaque(false);
        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        String content = "Imagine a world where finding your new best friend is as joyful as the moment " +
                "you bring them home. Our comprehensive platform revolutionizes the pet " +
                "ownership journey, starting with a streamlined approach to pet adopting. We " +
                "are dedicated to helping users adopt more conveniently, stripping away the " +
                "paperwork and stress to focus on connection. But our care doesn't stop at the " +
                "doorstep. We support the entire lifecycle of your companion with specialized " +
                "tools for owners looking to responsibly breed their pet, alongside an " +
                "integrated booking system that lets you schedule a vet appointment in just a " +
                "few clicks. It's not just an app; it's a lifetime partner for your pet's health and " +
                "happiness. Our system focus on helping pets also make the system user-friendly.";

        // HTML styling optimized for Dark Mode
        String htmlBody = String.format(
            "<html><body style='font-family: \"Segoe UI\", Helvetica, sans-serif; font-size: 18px; color: %s; text-align: justify; line-height: 1.8; font-weight: 400;'>%s</body></html>", 
            toHexString(TEXT_COLOR), content
        );

        textPane.setText(htmlBody);
        // Ensure text pane doesn't shrink to zero
        textPane.setMinimumSize(new Dimension(300, 200));

        // 2. IMAGE CONTAINER
        // Removed image container as per update

        // Add components to split panel
        contentSplitPanel.add(textPane);
        // Image container removed — keep flexible spacing so text breathes on wide layouts
        contentSplitPanel.add(Box.createHorizontalGlue());


        // --- ASSEMBLY ---
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(accentLine);
        cardPanel.add(Box.createVerticalStrut(35));
        cardPanel.add(contentSplitPanel); // Add the split panel instead of just textPane
        
        // Push content up slightly so footer sits at bottom properly
        cardPanel.add(Box.createVerticalGlue());

        // --- FOOTER SECTION ---
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        // Increased gap to 40 to account for wider text elements
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 10)); 
        
        // Add Social Media Links with Text
        footerPanel.add(createSocialLink("facebook", "Julian S. Agustino ", "https://www.facebook.com/Julian.Agustino.9206"));
        footerPanel.add(createSocialLink("instagram", "Julian Agustino ", "https://www.instagram.com/masterzhiju/"));
        footerPanel.add(createSocialLink("linkedin", "Julian S Agustino ", "https://www.linkedin.com/in/julian-agustino-a87846366/"));

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("About Us - Midnight Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height - 40);
        frame.setLocationRelativeTo(null);

        // reuse the static factory to avoid duplicating construction
        JPanel mainPanel = createAboutUsPanel();

        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    // make helper static so static factory can call it
    private static String toHexString(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	// make createSocialLink static for use from the static factory
	private static JPanel createSocialLink(String iconText, String handle, String url) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);

		SocialIcon icon = new SocialIcon(iconText, url);

		JLabel handleLabel = new JLabel(handle);
		handleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		handleLabel.setForeground(new Color(220, 220, 220)); // Light grey text

		panel.add(icon);
		panel.add(Box.createHorizontalStrut(12)); // Gap between icon and text
		panel.add(handleLabel);

		return panel;
	}

    // --- CUSTOM COMPONENT: ROUNDED PANEL ---
    static class RoundedPanel extends JPanel {
        private int cornerRadius = 15;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
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
        }
    }

    // --- CUSTOM COMPONENT: SOCIAL ICON ---
    static class SocialIcon extends JLabel {
        private boolean isHovered = false;
        private final String iconType; // "facebook" | "instagram" | "linkedin"
 
        public SocialIcon(String iconType, String url) {
            super("", SwingConstants.CENTER);
            this.iconType = iconType == null ? "" : iconType.toLowerCase();
            setFont(new Font("Segoe UI", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(45, 45)); // Slightly smaller to balance with text
            setMaximumSize(new Dimension(45, 45));
            setMinimumSize(new Dimension(45, 45));
            
            // Add hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    repaint();
                }
 
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    repaint();
                }
 
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (url == null || url.isEmpty()) return;
                    try {
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                        }
                    } catch (IOException | URISyntaxException ex) {
                        // Fail quietly; developer may log if desired

                    }
                }
            });
         }
 
         @Override
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

          // choose brand/background color
            Color bg;
            bg = switch (iconType) {
                case "facebook" -> new Color(59, 89, 152);
                case "instagram" -> new Color(225, 48, 108);
                case "linkedin" -> new Color(10, 102, 194);
                default -> new Color(255,255,255,30);
            }; // Facebook blue
            // base magenta-ish, we'll mix later
            // LinkedIn blue
            // lighten on hover a bit
            if (isHovered) bg = bg.brighter();

            // draw circular background
            if (!"instagram".equals(iconType)) {
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // draw simple letter marks for facebook/linkedin
                g2.setColor(Color.WHITE);
                Font iconFont = getFont().deriveFont(Font.BOLD, 18f);
                g2.setFont(iconFont);
                FontMetrics fm = g2.getFontMetrics();
                String mark = " ";
                if ("facebook".equals(iconType)) mark = "f";
                else if ("linkedin".equals(iconType)) mark = "in";
                int strW = fm.stringWidth(mark);
                int strH = fm.getAscent();
                g2.drawString(mark, (getWidth() - strW)/2, (getHeight() + strH)/2 - 2);
                super.paintComponent(g);
                return;
            }

            // Instagram: gradient circular background + white camera glyph
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 140, 0), getWidth(), getHeight(), new Color(193, 53, 132));
            g2.setPaint(gp);
            g2.fillOval(0, 0, getWidth(), getHeight());

            int w = getWidth(), h = getHeight();
            int pad = Math.max(8, Math.min(w, h) / 8);
            int size = Math.min(w, h) - pad * 2;
            int x = (w - size) / 2;
            int y = (h - size) / 2;

            // draw white rounded-square camera outline
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(Math.max(2f, size / 18f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.WHITE);
            int arc = Math.max(6, size / 6);
            g2.drawRoundRect(x, y, size, size, arc, arc);

            // draw lens (filled white circle)
            int cx = x + size/2;
            int cy = y + size/2;
            int lensR = Math.max(6, size / 6);
            g2.fillOval(cx - lensR/2, cy - lensR/2, lensR, lensR);

            // draw small flash square at top-right inside the rounded square
            int flash = Math.max(5, size / 8);
            int fx = x + size - flash - Math.max(4, size/20);
            int fy = y + Math.max(6, size/12);
            g2.fillRoundRect(fx, fy, flash, flash, flash/3, flash/3);

            g2.setStroke(oldStroke);
            super.paintComponent(g);
         }
     }
 }