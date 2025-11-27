import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.swing.Timer;

public class Dashboard extends JFrame {

    private JPanel sidebar;
    private JPanel mainContent;
    private JPanel headerPanel;
    private CardLayout cardLayout;
    private JTextField currentSearchField;

    private final Color SIDEBAR_START = new Color(25, 42, 86);
    private final Color SIDEBAR_END = new Color(13, 27, 62);
    private final Color BUTTON_HOVER = new Color(45, 73, 145);
    private final Color BUTTON_ACTIVE = new Color(93, 135, 255);
    private final Color TEXT_COLOR = new Color(205, 214, 244);
    private final Color ACCENT_PRIMARY = new Color(137, 180, 250);
    private final Color ACCENT_SECONDARY = new Color(245, 194, 231);
    private final Color HEADER_BACKGROUND = new Color(255, 255, 255);
    private JButton activeButton = null;

    public Dashboard() {
        setTitle("Paw Track Management Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        createModernSidebar();
        createModernHeaderPanel();
        createMainContentPanel();

        add(sidebar, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private void createModernSidebar() {
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_START, 0, getHeight(), SIDEBAR_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        String logoPath = "image/Logo.png";
        JLabel logoLabel = createCircularLogo(logoPath, 120);
        
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        sidebar.add(logoLabel);

        JLabel brandLabel = new JLabel("PawTrack");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        brandLabel.setForeground(TEXT_COLOR);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(brandLabel);

        JLabel taglineLabel = new JLabel("Management System");
        taglineLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        taglineLabel.setForeground(new Color(180, 190, 210));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(taglineLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(88, 91, 112));
        separator.setMaximumSize(new Dimension(200, 1));
        sidebar.add(separator);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        sidebar.add(createModernSidebarButton("🐾", "Paw Management", "PAW_PANEL", true));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createModernSidebarButton("📜", "Vet Appointments", "VET_PANEL", false));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createModernSidebarButton("💝", "Donations", "DONATION_PANEL", false));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createModernSidebarButton("🎯", "Trivia", "TRIVIA_PANEL", false));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createModernSidebarButton("💰", "Shop", "SHOP_PANEL", false));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createModernSidebarButton("ℹ️", "About Us", "ABOUT_PANEL", false));
        
        sidebar.add(Box.createVerticalGlue());
        
        // Add separator before profile section
        JSeparator bottomSeparator = new JSeparator();
        bottomSeparator.setForeground(new Color(88, 91, 112));
        bottomSeparator.setMaximumSize(new Dimension(200, 1));
        sidebar.add(bottomSeparator);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Add profile button at the bottom
        sidebar.add(createModernSidebarButton("👤", "Profile", "PROFILE_PANEL", false));
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add watermark at the very bottom
        JLabel watermarkLabel = new JLabel("© 2025 PawTrack", SwingConstants.CENTER);
        watermarkLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        watermarkLabel.setForeground(new Color(120, 130, 150, 180)); // Semi-transparent
        watermarkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        watermarkLabel.setMaximumSize(new Dimension(200, 20));
        sidebar.add(watermarkLabel);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private JLabel createCircularLogo(String imagePath, int diameter) {
        JLabel logoLabel = new JLabel() {
            private Image image;

            {
                File logoFile = new File(imagePath);
                if (logoFile.exists()) {
                    try {
                        ImageIcon icon = new ImageIcon(imagePath);
                        image = icon.getImage();
                    } catch (Exception e) {
                        System.err.println("Error loading logo: " + e.getMessage());
                    }
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw circular shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillOval(3, 3, diameter, diameter);

                // Draw white circular border
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, diameter, diameter);

                // Clip to circle for image
                g2d.setClip(new Ellipse2D.Float(5, 5, diameter - 10, diameter - 10));

                if (image != null) {
                    g2d.drawImage(image, 5, 5, diameter - 10, diameter - 10, this);
                } else {
                    // Fallback gradient background
                    GradientPaint gp = new GradientPaint(0, 0, ACCENT_PRIMARY, diameter, diameter, ACCENT_SECONDARY);
                    g2d.setPaint(gp);
                    g2d.fillOval(5, 5, diameter - 10, diameter - 10);
                    
                    // Draw paw emoji
                    g2d.setClip(null);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 50));
                    g2d.setColor(Color.WHITE);
                    String emoji = "🐾";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (diameter - fm.stringWidth(emoji)) / 2;
                    int y = ((diameter - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(emoji, x, y);
                }

                g2d.dispose();
            }
        };
        
        logoLabel.setPreferredSize(new Dimension(diameter, diameter));
        logoLabel.setMinimumSize(new Dimension(diameter, diameter));
        logoLabel.setMaximumSize(new Dimension(diameter, diameter));
        
        return logoLabel;
    }

    private void createModernHeaderPanel() {
        headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(HEADER_BACKGROUND);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle bottom shadow
                GradientPaint shadow = new GradientPaint(0, getHeight() - 3, new Color(0, 0, 0, 15), 
                                                        0, getHeight(), new Color(0, 0, 0, 0));
                g2d.setPaint(shadow);
                g2d.fillRect(0, getHeight() - 3, getWidth(), 3);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchPanel.setOpaque(false);
        
        JPanel searchFieldPanel = createModernSearchField();
        searchPanel.add(searchFieldPanel);

        JButton searchButton = createModernActionButton("🔍 Search", new Color(99, 102, 241), 100);
        searchButton.addActionListener((ActionEvent e) -> {
            JTextField searchField = findSearchField(searchFieldPanel);
            if (searchField != null) {
                String searchText = searchField.getText();
                if (!searchText.trim().isEmpty() && !searchText.equals("Search paws, licenses, or contacts...")) {
                    performSearch(searchText);
                } else {
                    JOptionPane.showMessageDialog(Dashboard.this, 
                        "Please enter something to search for.", "Search", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        searchPanel.add(searchButton);

        JButton adoptionButton = createModernActionButton("🏠 Adoption", new Color(236, 72, 153), 110);
        adoptionButton.addActionListener((ActionEvent e) -> handleAddNew());
        searchPanel.add(adoptionButton);

        JButton logoutButton = createModernActionButton("🚪 Logout", new Color(239, 68, 68), 90);
        logoutButton.addActionListener((ActionEvent e) -> handleLogout());

        headerPanel.add(searchPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
    }

    private JPanel createModernSearchField() {
        JPanel searchFieldPanel = new JPanel(new BorderLayout()) {
            private boolean hover = false;
            private float hoverProgress = 0f;
            private Timer hoverTimer;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Animated background color
                int bgR = (int)(248 + (240 - 248) * hoverProgress);
                int bgG = (int)(250 + (245 - 250) * hoverProgress);
                int bgB = (int)(252 + (255 - 252) * hoverProgress);
                g2d.setColor(new Color(bgR, bgG, bgB));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                
                // Animated border with glow effect
                if (hover && hoverProgress > 0) {
                    // Outer glow
                    int glowAlpha = (int)(20 * hoverProgress);
                    for (int i = 0; i < 3; i++) {
                        int alpha = glowAlpha - (i * 7);
                        if (alpha > 0) {
                            g2d.setColor(new Color(99, 102, 241, alpha));
                            g2d.setStroke(new BasicStroke(2.5f + i));
                            g2d.draw(new RoundRectangle2D.Float(-i, -i, 
                                getWidth() + i * 2, getHeight() + i * 2, 25 + i, 25 + i));
                        }
                    }
                }
                
                // Main border
                int borderR = (int)(226 + (99 - 226) * hoverProgress * 0.5f);
                int borderG = (int)(232 + (102 - 232) * hoverProgress * 0.5f);
                int borderB = (int)(240 + (241 - 240) * hoverProgress * 0.5f);
                g2d.setColor(new Color(borderR, borderG, borderB));
                g2d.setStroke(new BasicStroke(1.5f + hoverProgress * 0.5f));
                g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 25, 25));
                
                g2d.dispose();
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        startHoverAnimation(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        startHoverAnimation(false);
                    }
                });
            }

            private void startHoverAnimation(boolean forward) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(15, null);
                hoverTimer.addActionListener(e -> {
                    if (forward) {
                        hoverProgress += 0.1f;
                        if (hoverProgress >= 1f) {
                            hoverProgress = 1f;
                            hoverTimer.stop();
                        }
                    } else {
                        hoverProgress -= 0.1f;
                        if (hoverProgress <= 0f) {
                            hoverProgress = 0f;
                            hoverTimer.stop();
                        }
                    }
                    repaint();
                });
                hoverTimer.start();
            }
        };
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.setPreferredSize(new Dimension(320, 40));
        
        JLabel searchIcon = new JLabel("🔍") {
            private float bounceOffset = 0f;
            private Timer bounceTimer;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(0, (int)bounceOffset);
                super.paintComponent(g2d);
                g2d.dispose();
            }

            {
                searchFieldPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        startBounce();
                    }
                });
            }

            private void startBounce() {
                if (bounceTimer != null && bounceTimer.isRunning()) {
                    return;
                }

                bounceTimer = new Timer(20, null);
                final int[] step = {0};
                bounceTimer.addActionListener(e -> {
                    step[0]++;
                    bounceOffset = (float)(Math.sin(step[0] * 0.4) * 3 * Math.exp(-step[0] * 0.08));
                    
                    if (step[0] > 25 || Math.abs(bounceOffset) < 0.1f) {
                        bounceOffset = 0f;
                        bounceTimer.stop();
                    }
                    repaint();
                });
                bounceTimer.start();
            }
        };
        searchIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 5));
        
        final JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));
        searchField.setOpaque(false);
        
        final String placeholder = "Search paws, licenses, or contacts...";
        searchField.setText(placeholder);
        searchField.setForeground(new Color(148, 163, 184));
        
        currentSearchField = searchField;
        
        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(new Color(30, 41, 59));
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(new Color(148, 163, 184));
                }
            }
        });

        // Simplified key listener - only handle Enter key for search
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = searchField.getText();
                    if (!text.trim().isEmpty() && !text.equals(placeholder)) {
                        performSearch(text);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        
        return searchFieldPanel;
    }

    private JTextField findSearchField(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField) {
                return (JTextField) comp;
            } else if (comp instanceof Container) {
                JTextField found = findSearchField((Container) comp);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void performSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter something to search for.", 
                "Search", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message = "Searching for: " + searchText + "\n\nThis is where you would implement the actual search functionality.";
        JOptionPane.showMessageDialog(this, message, "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createModernActionButton(String text, Color baseColor, int width) {
        JButton button = new JButton(text) {
            private boolean hover = false;
            private boolean pressed = false;
            private float hoverProgress = 0f;
            private Timer hoverTimer;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int width = getWidth();
                int height = getHeight();

                // Draw glowing shadow on hover
                if (hover && hoverProgress > 0) {
                    int shadowAlpha = (int)(30 * hoverProgress);
                    for (int i = 0; i < 4; i++) {
                        int alpha = shadowAlpha - (i * 7);
                        if (alpha > 0) {
                            Color shadowColor = new Color(baseColor.getRed(), baseColor.getGreen(), 
                            baseColor.getBlue(), alpha);
                            g2d.setColor(shadowColor);
                            g2d.fill(new RoundRectangle2D.Float(-i, -i, 
                                width + i * 2, height + i * 2, 20 + i, 20 + i));
                        }
                    }
                }

                // Animated background color
                Color bgColor;
                if (pressed) {
                    bgColor = baseColor.darker().darker();
                } else if (hover) {
                    int r = (int)(baseColor.getRed() + (baseColor.darker().getRed() - baseColor.getRed()) * hoverProgress * 0.5f);
                    int gr = (int)(baseColor.getGreen() + (baseColor.darker().getGreen() - baseColor.getGreen()) * hoverProgress * 0.5f);
                    int b = (int)(baseColor.getBlue() + (baseColor.darker().getBlue() - baseColor.getBlue()) * hoverProgress * 0.5f);
                    bgColor = new Color(r, gr, b);
                } else {
                    bgColor = baseColor;
                }
                
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 20, 20));

                // Animated highlight
                if (hover && hoverProgress > 0) {
                    int highlightAlpha = (int)(40 * hoverProgress);
                    g2d.setColor(new Color(255, 255, 255, highlightAlpha));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, width, height / 2.5f, 20, 20));
                }

                // Shimmer effect
                if (hover && hoverProgress > 0.7f) {
                    int shimmerAlpha = (int)((hoverProgress - 0.7f) * 3.3f * 50);
                    GradientPaint shimmer = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 0),
                        width, height / 2, new Color(255, 255, 255, shimmerAlpha),
                        false
                    );
                    g2d.setPaint(shimmer);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 20, 20));
                }

                g2d.dispose();
                super.paintComponent(g);
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        startHoverAnimation(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        pressed = false;
                        startHoverAnimation(false);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }
                });
            }

            private void startHoverAnimation(boolean forward) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(15, null);
                hoverTimer.addActionListener(e -> {
                    if (forward) {
                        hoverProgress += 0.12f;
                        if (hoverProgress >= 1f) {
                            hoverProgress = 1f;
                            hoverTimer.stop();
                        }
                    } else {
                        hoverProgress -= 0.12f;
                        if (hoverProgress <= 0f) {
                            hoverProgress = 0f;
                            hoverTimer.stop();
                        }
                    }
                    repaint();
                });
                hoverTimer.start();
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createModernSidebarButton(String icon, String text, final String panelName, boolean isFirst) {
        JButton button = new JButton() {
            private boolean hover = false;
            private boolean active = isFirst;
            private boolean pressed = false;
            private float hoverProgress = 0f;
            private Timer hoverTimer;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int width = getWidth();
                int height = getHeight();

                // Draw glowing shadow on hover
                if ((hover || active) && hoverProgress > 0) {
                    int shadowAlpha = (int)(25 * hoverProgress);
                    for (int i = 0; i < 6; i++) {
                        int alpha = shadowAlpha - (i * 4);
                        if (alpha > 0) {
                            g2d.setColor(new Color(137, 180, 250, alpha));
                            g2d.fill(new RoundRectangle2D.Float(-i, -i, 
                                width + i * 2, height + i * 2, 12 + i, 12 + i));
                        }
                    }
                }

                // Main button background
                if (active) {
                    g2d.setColor(BUTTON_ACTIVE);
                } else if (hover) {
                    int r = (int)(BUTTON_HOVER.getRed() + (BUTTON_ACTIVE.getRed() - BUTTON_HOVER.getRed()) * hoverProgress * 0.3f);
                    int gr = (int)(BUTTON_HOVER.getGreen() + (BUTTON_ACTIVE.getGreen() - BUTTON_HOVER.getGreen()) * hoverProgress * 0.3f);
                    int b = (int)(BUTTON_HOVER.getBlue() + (BUTTON_ACTIVE.getBlue() - BUTTON_HOVER.getBlue()) * hoverProgress * 0.3f);
                    g2d.setColor(new Color(r, gr, b));
                } else {
                    g2d.setColor(new Color(0, 0, 0, 0));
                }
                
                g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 12, 12));

                // Sliding left border indicator
                if (active) {
                    g2d.setColor(ACCENT_PRIMARY);
                    int barHeight = (int)(height * 0.6f);
                    int barY = (height - barHeight) / 2;
                    g2d.fill(new RoundRectangle2D.Float(0, barY, 4, barHeight, 4, 4));
                } else if (hover && hoverProgress > 0) {
                    g2d.setColor(new Color(ACCENT_PRIMARY.getRed(), ACCENT_PRIMARY.getGreen(), 
                                           ACCENT_PRIMARY.getBlue(), (int)(255 * hoverProgress)));
                    int barHeight = (int)(height * 0.6f * hoverProgress);
                    int barY = (height - barHeight) / 2;
                    g2d.fill(new RoundRectangle2D.Float(0, barY, 4, barHeight, 4, 4));
                }

                // Shimmer effect on hover
                if (hover && hoverProgress > 0.5f) {
                    int shimmerAlpha = (int)((hoverProgress - 0.5f) * 2 * 35);
                    GradientPaint shimmer = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 0),
                        width, 0, new Color(255, 255, 255, shimmerAlpha)
                    );
                    g2d.setPaint(shimmer);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 12, 12));
                }

                g2d.dispose();
                super.paintComponent(g);
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        startHoverAnimation(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        pressed = false;
                        startHoverAnimation(false);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }
                });
            }

            private void startHoverAnimation(boolean forward) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(15, null);
                hoverTimer.addActionListener(e -> {
                    if (forward) {
                        hoverProgress += 0.1f;
                        if (hoverProgress >= 1f) {
                            hoverProgress = 1f;
                            hoverTimer.stop();
                        }
                    } else {
                        hoverProgress -= 0.1f;
                        if (hoverProgress <= 0f) {
                            hoverProgress = 0f;
                            hoverTimer.stop();
                        }
                    }
                    repaint();
                });
                hoverTimer.start();
            }

            public void setActive(boolean active) {
                this.active = active;
                if (active) {
                    hoverProgress = 1f;
                } else {
                    hoverProgress = 0f;
                }
                repaint();
            }
        };

        // Use BorderLayout and allow multi-line wrapped label so long texts don't get truncated.
        button.setLayout(new BorderLayout(10, 0));
        button.setOpaque(false);

        JLabel iconLabel = new JLabel(icon) {
            private float bounceOffset = 0f;
            private Timer bounceTimer;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(0, (int)bounceOffset);
                super.paintComponent(g2d);
                g2d.dispose();
            }

            {
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        startBounce();
                    }
                });
            }

            private void startBounce() {
                if (bounceTimer != null && bounceTimer.isRunning()) {
                    return;
                }

                bounceTimer = new Timer(20, null);
                final int[] step = {0};
                bounceTimer.addActionListener(e -> {
                    step[0]++;
                    bounceOffset = (float)(Math.sin(step[0] * 0.4) * 4 * Math.exp(-step[0] * 0.08));
                    
                    if (step[0] > 25 || Math.abs(bounceOffset) < 0.1f) {
                        bounceOffset = 0f;
                        bounceTimer.stop();
                    }
                    repaint();
                });
                bounceTimer.start();
            }
        };
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        iconLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Use HTML to allow wrapping. Keep styling minimal so look stays consistent.
        JLabel textLabel = new JLabel("<html><div style='white-space:normal;'>" + text + "</div></html>") {
            private Timer colorTimer;
            private float colorProgress = 0f;

            @Override
            protected void paintComponent(Graphics g) {
                // Smooth color transition on hover
                if (button.getModel().isRollover() && !isButtonActive()) {
                    if (colorProgress < 1f) {
                        startColorTransition(true);
                    }
                    int r = (int)(TEXT_COLOR.getRed() + (ACCENT_PRIMARY.getRed() - TEXT_COLOR.getRed()) * colorProgress);
                    int gr = (int)(TEXT_COLOR.getGreen() + (ACCENT_PRIMARY.getGreen() - TEXT_COLOR.getGreen()) * colorProgress);
                    int b = (int)(TEXT_COLOR.getBlue() + (ACCENT_PRIMARY.getBlue() - TEXT_COLOR.getBlue()) * colorProgress);
                    setForeground(new Color(r, gr, b));
                } else {
                    if (colorProgress > 0f && !isButtonActive()) {
                        startColorTransition(false);
                    }
                    if (!isButtonActive()) {
                        int r = (int)(TEXT_COLOR.getRed() + (ACCENT_PRIMARY.getRed() - TEXT_COLOR.getRed()) * colorProgress);
                        int gr = (int)(TEXT_COLOR.getGreen() + (ACCENT_PRIMARY.getGreen() - TEXT_COLOR.getGreen()) * colorProgress);
                        int b = (int)(TEXT_COLOR.getBlue() + (ACCENT_PRIMARY.getBlue() - TEXT_COLOR.getBlue()) * colorProgress);
                        setForeground(new Color(r, gr, b));
                    } else {
                        setForeground(Color.WHITE);
                    }
                }
                super.paintComponent(g);
            }

            private boolean isButtonActive() {
                try {
                    java.lang.reflect.Field activeField = button.getClass().getDeclaredField("active");
                    activeField.setAccessible(true);
                    return (boolean) activeField.get(button);
                } catch (Exception ex) {
                    return false;
                }
            }

            private void startColorTransition(boolean forward) {
                if (colorTimer != null && colorTimer.isRunning()) {
                    return;
                }

                colorTimer = new Timer(15, null);
                colorTimer.addActionListener(e -> {
                    if (forward) {
                        colorProgress += 0.15f;
                        if (colorProgress >= 1f) {
                            colorProgress = 1f;
                            colorTimer.stop();
                        }
                    } else {
                        colorProgress -= 0.15f;
                        if (colorProgress <= 0f) {
                            colorProgress = 0f;
                            colorTimer.stop();
                        }
                    }
                    repaint();
                });
                colorTimer.start();
            }
        };
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        textLabel.setForeground(isFirst ? Color.WHITE : TEXT_COLOR);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);

        button.add(iconLabel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        // Increase preferred height so wrapped text won't be cut; allow width to expand.
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(230, 60));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isFirst) {
            activeButton = button;
        }

        button.addActionListener((ActionEvent e) -> {
            // Use unified helper to switch panels so Profile and other buttons behave the same.
            showPanel(button, panelName);
        });

        return button;
    }

    // Add a small helper to centralize panel switching and active-button visuals
    private void showPanel(JButton button, String panelName) {
        // Deactivate previous active button visually
        if (activeButton != null && activeButton instanceof JButton) {
            try {
                activeButton.getClass().getMethod("setActive", boolean.class).invoke(activeButton, false);
            } catch (Exception ex) {
                // ignore visual failure
            }
        }

        // Activate this button visually
        if (button != null) {
            try {
                button.getClass().getMethod("setActive", boolean.class).invoke(button, true);
            } catch (Exception ex) {
                // ignore visual failure
            }
        }

        // Remember active button and show the requested card
        activeButton = button;
        if (cardLayout != null && mainContent != null && panelName != null) {
            cardLayout.show(mainContent, panelName);
        }
    }

    private void createMainContentPanel() {
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        // Paw Management Panel
        try {
            mainContent.add(new PawManagement(), "PAW_PANEL");
        } catch (Exception e) {
            System.err.println("PawManagement class not found, using placeholder");
            mainContent.add(createContentPanel("Paw Management"), "PAW_PANEL");
        }
        
        // Vet Appointment Panel (embedded instead of adding a JFrame)
        try {
            // Embed VetAppointmentSystem's main panel into the dashboard
            VetAppointmentSystem embeddedVet = new VetAppointmentSystem(true);
            JPanel vetPanel = embeddedVet.getMainPanel();
            mainContent.add(vetPanel, "VET_PANEL");
        } catch (Exception e) {
            System.err.println("VetAppointment class not found or failed to embed, using placeholder: " + e.getMessage());
            mainContent.add(createContentPanel("Vet Appointments"), "VET_PANEL");
        }

        // Donation panel integration with better error handling
        try {
            System.out.println("Creating Donation panel...");
            Donation donation = new Donation();
            JPanel donationPanel = donation.createDonationPanel();
            mainContent.add(donationPanel, "DONATION_PANEL");
            System.out.println("Donation panel successfully added to main content");
        } catch (Exception e) {
            System.err.println("Error creating Donation panel: " + e.getMessage());
            mainContent.add(createDonationFallbackPanel(), "DONATION_PANEL");
        }
        
        // PetTrivia panel integration with error handling
        try {
            JPanel triviaPanel = PetTrivia.createTriviaPanel();
            mainContent.add(triviaPanel, "TRIVIA_PANEL");
            System.out.println("Pet Trivia panel successfully integrated");
        } catch (Exception e) {
            System.err.println("Error creating PetTrivia panel: " + e.getMessage());
            mainContent.add(createContentPanel("Trivia"), "TRIVIA_PANEL");
        }
        
        // Shop panel
        try {
            // Create PetShop in embedded mode (no separate window) and get its main panel
            PetShop embeddedShop = new PetShop(true);
            JPanel shopPanel = embeddedShop.getMainPanel();
            if (shopPanel != null) {
                mainContent.add(shopPanel, "SHOP_PANEL");
            } else {
                // Fallback to placeholder if embedding failed
                mainContent.add(createContentPanel("Shop"), "SHOP_PANEL");
            }
        } catch (Exception e) {
            System.err.println("Error embedding PetShop into dashboard: " + e.getMessage());
            mainContent.add(createContentPanel("Shop"), "SHOP_PANEL");
        }
        
        // Transactions panel
        // mainContent.add(createTransactionsPanel(), "TRANSACTIONS_PANEL");
        
        // About Us panel with error handling
        try {
            mainContent.add(AboutUs.createAboutUsPanel(), "ABOUT_PANEL");
        } catch (Exception e) {
            System.err.println("AboutUs class not found, using placeholder");
            mainContent.add(createContentPanel("About Us"), "ABOUT_PANEL");
        }
        
        // Profile panel with improved error handling
        try {
            UserProfile userProfile = new UserProfile();
            JPanel userProfileContent = userProfile.getMainContentPanel();
            if (userProfileContent != null) {
                JPanel profileWrapper = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        GradientPaint gp = new GradientPaint(0, 0, new Color(243, 244, 246), 0, getHeight(), new Color(229, 231, 235));
                        g2d.setPaint(gp);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
                profileWrapper.add(userProfileContent, BorderLayout.CENTER);
                mainContent.add(profileWrapper, "PROFILE_PANEL");
            } else {
                mainContent.add(createProfilePanel(), "PROFILE_PANEL");
            }
        } catch (Exception e) {
            System.err.println("Error creating user profile panel: " + e.getMessage());
            mainContent.add(createProfilePanel(), "PROFILE_PANEL");
        }

        // Ensure the Profile view is shown and the sidebar reflects it
        // (Do this after all panels have been added)
        try {
            cardLayout.show(mainContent, "PROFILE_PANEL");

            // Find the Profile button in the sidebar and update active visuals
            JButton profileBtn = findButtonInSidebar(sidebar, "Profile");
            if (profileBtn != null) {
                if (activeButton != null) {
                    try {
                        activeButton.getClass().getMethod("setActive", boolean.class).invoke(activeButton, false);
                    } catch (Exception ex) { /* ignore */ }
                }
                try {
                    profileBtn.getClass().getMethod("setActive", boolean.class).invoke(profileBtn, true);
                } catch (Exception ex) { /* ignore */ }
                activeButton = profileBtn;
            }
        } catch (Exception e) {
            System.err.println("Failed to default-show PROFILE_PANEL: " + e.getMessage());
        }
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(243, 244, 246), 0, getHeight(), new Color(229, 231, 235));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Create profile content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Profile header
        JLabel titleLabel = new JLabel("User Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(71, 85, 105));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Profile picture placeholder
        JLabel profilePic = createCircularLogo("", 120);
        profilePic.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(profilePic);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // User info
        JLabel nameLabel = new JLabel("User Name", SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(new Color(51, 65, 85));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(nameLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel emailLabel = new JLabel("user@pawtrack.com", SwingConstants.CENTER);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailLabel.setForeground(new Color(100, 116, 139));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(emailLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Profile actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton editButton = createModernActionButton("✏️ Edit Profile", new Color(99, 102, 241), 150);
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit Profile functionality would be implemented here.", "Profile", JOptionPane.INFORMATION_MESSAGE));
        buttonPanel.add(editButton);
        
        JButton settingsButton = createModernActionButton("⚙️ Settings", new Color(107, 114, 128), 120);
        settingsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Settings functionality would be implemented here.", "Settings", JOptionPane.INFORMATION_MESSAGE));
        buttonPanel.add(settingsButton);
        
        contentPanel.add(buttonPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDonationFallbackPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(243, 244, 246), 0, getHeight(), new Color(229, 231, 235));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        try {
            // Try to load and display the donation image
            ImageIcon donationIcon = new ImageIcon("image/DONATION.png");
            if (donationIcon.getIconWidth() > 0) {
                JLabel imageLabel = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (donationIcon.getIconWidth() > 0) {
                            Graphics2D g2d = (Graphics2D) g.create();
                            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            
                            int panelWidth = getWidth();
                            int panelHeight = getHeight();
                            int imageWidth = donationIcon.getIconWidth();
                            int imageHeight = donationIcon.getIconHeight();
                            
                            double scaleX = (double) panelWidth / imageWidth;
                            double scaleY = (double) panelHeight / imageHeight;
                            double scale = Math.min(scaleX, scaleY) * 0.8; // Scale down to 80% for padding
                            
                            int scaledWidth = (int) (imageWidth * scale);
                            int scaledHeight = (int) (imageHeight * scale);
                            
                            int x = (panelWidth - scaledWidth) / 2;
                            int y = (panelHeight - scaledHeight) / 2;
                            
                            g2d.drawImage(donationIcon.getImage(), x, y, scaledWidth, scaledHeight, this);
                            g2d.dispose();
                        }
                    }
                };
                panel.add(imageLabel, BorderLayout.CENTER);
            } else {
                throw new Exception("Could not load donation image");
            }
        } catch (Exception e) {
            // If image loading fails, show text
            JLabel label = new JLabel("Donation Panel", SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 32));
            label.setForeground(new Color(71, 85, 105));
            panel.add(label, BorderLayout.CENTER);
        }
        
        return panel;
    }

    private JPanel createContentPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(243, 244, 246), 0, getHeight(), new Color(229, 231, 235));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 32));
        label.setForeground(new Color(71, 85, 105));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void handleAddNew() {
        JButton adoptionButton = findAdoptionButton();
        if (adoptionButton != null) {
            adoptionButton.setEnabled(false);
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                AdoptionForm adoptionForm = new AdoptionForm();
                
                adoptionForm.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        returnToDashboard(adoptionButton);
                    }
                    
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        returnToDashboard(adoptionButton);
                    }
                });
                
                this.setVisible(false);
                adoptionForm.setVisible(true);
                
            } catch (Exception e) {
                System.err.println("Error opening adoption form: " + e.getMessage());
                
                JOptionPane.showMessageDialog(this, 
                    "Adoption form is not available at the moment.",
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                if (adoptionButton != null) {
                    adoptionButton.setEnabled(true);
                }
            }
        });
    }
    
    private JButton findAdoptionButton() {
        return findButtonInPanel(headerPanel, "Adoption");
    }
    
    private JButton findButtonInPanel(Container container, String buttonText) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().contains(buttonText)) {
                    return button;
                }
            } else if (comp instanceof Container) {
                Container container1 = (Container) comp;
                JButton found = findButtonInPanel(container1, buttonText);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    private void returnToDashboard(JButton adoptionButton) {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
            if (adoptionButton != null) {
                adoptionButton.setEnabled(true);
            }
            this.toFront();
            this.requestFocus();
        });
    }

    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            this.dispose();
            
            SwingUtilities.invokeLater(() -> {
                try {
                    new PawTrackLogin().setVisible(true);
                } catch (Exception e) {
                    System.err.println("PawTrackLogin class not found: " + e.getMessage());
                    // Create a simple message instead of exiting
                    JOptionPane.showMessageDialog(null, 
                        "Logged out successfully.", 
                        "Logout", 
                        JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            });
        }
    }
    
    private void openPetShop() {
        SwingUtilities.invokeLater(() -> {
            try {
                JButton shopButton = findShopButton();
                if (shopButton != null) {
                    shopButton.setEnabled(false);
                }
                
                PetShop petShop = new PetShop();
                
                petShop.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        returnFromPetShop(shopButton);
                    }
                    
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        returnFromPetShop(shopButton);
                    }
                });
                
                this.setVisible(false);
                petShop.setVisible(true);
                
            } catch (Exception e) {
                System.err.println("Error opening PetShop: " + e.getMessage());
                
                JOptionPane.showMessageDialog(this, 
                    "Pet Shop is not available at the moment.",
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private JButton findShopButton() {
        return findButtonInSidebar(sidebar, "Shop");
    }
    
    private JButton findButtonInSidebar(Container container, String buttonText) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                // Check inner components (labels) for text containing the buttonText (handles HTML-wrapped labels)
                Container buttonContainer = (Container) button;
                for (Component innerComp : buttonContainer.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel) innerComp;
                        String lblText = label.getText();
                        if (lblText != null && lblText.contains(buttonText)) {
                            return button;
                        }
                    }
                }
            } else if (comp instanceof Container) {
                JButton found = findButtonInSidebar((Container) comp, buttonText);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    private void returnFromPetShop(JButton shopButton) {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
            if (shopButton != null) {
                shopButton.setEnabled(true);
            }
            this.toFront();
            this.requestFocus();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}