import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class DashboardAdmin extends JFrame {

    // Color Palette
    private final Color SIDEBAR_BG_TOP = new Color(33, 42, 60);       // Dark Blue/Grey
    private final Color SIDEBAR_BG_BOTTOM = new Color(25, 32, 45);    // Darker gradient end
    private final Color ACTIVE_BTN_BG = new Color(255, 255, 255, 20); // Transparent white for active
    private final Color HOVER_BTN_BG = new Color(255, 255, 255, 10);  // Fainter transparent for hover
    private final Color ACCENT_COLOR = new Color(74, 144, 226);       // Bright Blue
    private final Color MAIN_BG = new Color(245, 247, 250);           // Light Grey
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color TEXT_MUTED = new Color(160, 170, 185);
    
    // Mode Colors
    private final Color VIEW_MODE_COLOR = new Color(231, 76, 60);     // Red
    private final Color EDIT_MODE_COLOR = new Color(46, 204, 113);    // Green

    // Components
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private List<SidebarButton> sidebarButtons = new ArrayList<>();
    
    // State
    private boolean isEditMode = false; // Default is View Mode (Read-only)
    
    // Font settings
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public DashboardAdmin() {
        setTitle("Pet Adoption Admin System");
        setSize(1200, 750); // Slightly larger
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Create the Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Create the Main Content Area
        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);
        mainContentPanel.setBackground(MAIN_BG);

        // Add Screens (Cards)
        mainContentPanel.add(createDashboardPanel(), "Dashboard");
        mainContentPanel.add(createRegisteredAccountsPanel(), "Registered Accounts");
        mainContentPanel.add(createAdoptingPetPanel(), "Adopting Pet");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the sidebar with Admin Profile and Navigation.
     */
    private JPanel createSidebar() {
        // Custom Panel for Gradient Background
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_BG_TOP, 0, getHeight(), SIDEBAR_BG_BOTTOM);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setLayout(new BorderLayout());

        // --- Top: Admin Profile Section ---
        JPanel profilePanel = new JPanel();
        profilePanel.setOpaque(false); // Transparent to show gradient
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(new EmptyBorder(40, 20, 40, 20));

        // Custom Circle Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Avatar Circle
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Double(0, 0, 80, 80));
                
                // Initials
                g2.setColor(SIDEBAR_BG_TOP);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                String initials = "AD";
                g2.drawString(initials, (80 - fm.stringWidth(initials)) / 2, (80 + fm.getAscent()) / 2 - 8);

                // Online Status Indicator
                g2.setColor(new Color(46, 204, 113)); // Green
                g2.fill(new Ellipse2D.Double(60, 60, 18, 18));
                g2.setColor(SIDEBAR_BG_TOP); // Border for status
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Ellipse2D.Double(60, 60, 18, 18));
            }
        };
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setMaximumSize(new Dimension(80, 80));
        avatar.setOpaque(false);

        JLabel nameLabel = new JLabel("Admin User");
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("System Administrator");
        roleLabel.setForeground(TEXT_MUTED);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(avatar);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        profilePanel.add(nameLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        profilePanel.add(roleLabel);

        // --- Middle: Navigation Buttons ---
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Create Sidebar Buttons
        SidebarButton btnHome = new SidebarButton("Dashboard", "⊞", e -> switchCard("Dashboard"));
        SidebarButton btnAccounts = new SidebarButton("Registered Accounts", "👥", e -> switchCard("Registered Accounts"));
        SidebarButton btnAdopt = new SidebarButton("Adopting Pet", "🐾", e -> switchCard("Adopting Pet"));
        
        // Set first button as active initially
        btnHome.setActive(true);

        // Add to list for management
        sidebarButtons.add(btnHome);
        sidebarButtons.add(btnAccounts);
        sidebarButtons.add(btnAdopt);

        menuPanel.add(btnHome);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnAccounts);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnAdopt);

        // Wrapper for top components
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(profilePanel, BorderLayout.NORTH);
        topContainer.add(menuPanel, BorderLayout.CENTER);

        sidebar.add(topContainer, BorderLayout.CENTER);

        // --- Bottom: Logout Button ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 20, 30, 20));

        JButton btnLogout = new JButton("LOGOUT");
        styleLogoutButton(btnLogout);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        bottomPanel.add(btnLogout, BorderLayout.CENTER);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void switchCard(String cardName) {
        cardLayout.show(mainContentPanel, cardName);
        // Update active state of buttons
        for (SidebarButton btn : sidebarButtons) {
            btn.setActive(btn.targetCard.equals(cardName));
        }
    }

    // --- Custom Sidebar Button Class ---
    private class SidebarButton extends JButton {
        private boolean isActive = false;
        private boolean isHovered = false;
        private String targetCard;
        private String iconSymbol;

        public SidebarButton(String text, String iconSymbol, ActionListener action) {
            super(text);
            this.targetCard = text; // assuming text matches card name for simplicity
            this.iconSymbol = iconSymbol;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(TEXT_COLOR);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addActionListener(action);
            
            // Padding
            setBorder(new EmptyBorder(12, 50, 12, 20));

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

        public void setActive(boolean active) {
            this.isActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(ACTIVE_BTN_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Accent Bar
                g2.setColor(ACCENT_COLOR);
                g2.fillRect(0, 0, 5, getHeight());
                setForeground(Color.WHITE);
            } else if (isHovered) {
                g2.setColor(HOVER_BTN_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Hover bar (thinner)
                g2.setColor(new Color(74, 144, 226, 150));
                g2.fillRect(0, 0, 3, getHeight());
                setForeground(Color.WHITE);
            } else {
                setForeground(new Color(200, 210, 220));
            }

            // Draw Icon
            Font originalFont = getFont();
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18)); // Ensure font supports symbols
            g2.drawString(iconSymbol, 20, getHeight() / 2 + 6);
            
            g2.setFont(originalFont);
            
            super.paintComponent(g);
        }
    }

    private void styleLogoutButton(JButton btn) {
        btn.setBackground(new Color(231, 76, 60, 20)); // Transparent Red
        btn.setForeground(new Color(255, 100, 100));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(231, 76, 60), 1));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 45));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(231, 76, 60));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(231, 76, 60, 20));
                btn.setForeground(new Color(255, 100, 100));
            }
        });
    }

    // --- Content Panels ---

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(HEADER_FONT);
        title.setForeground(SIDEBAR_BG_TOP);

        // Simple stats dashboard cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(createStatCard("Total Pets", "142", new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Pending", "8", new Color(241, 196, 15)));
        statsPanel.add(createStatCard("New Users", "24", new Color(52, 152, 219)));

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(title, BorderLayout.NORTH);
        container.add(Box.createVerticalStrut(30), BorderLayout.CENTER);
        container.add(statsPanel, BorderLayout.SOUTH);

        // Center visual
        JLabel instruction = new JLabel("<html><div style='text-align: center; color: #7f8c8d;'>Select an option from the sidebar to manage the system.</div></html>");
        instruction.setHorizontalAlignment(SwingConstants.CENTER);
        instruction.setFont(new Font("Segoe UI", Font.ITALIC, 16));

        panel.add(container, BorderLayout.NORTH);
        panel.add(instruction, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createMatteBorder(0, 0, 4, 0, color)
        ));
        
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valLabel.setForeground(SIDEBAR_BG_TOP);
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel txtLabel = new JLabel(label);
        txtLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtLabel.setForeground(Color.GRAY);
        txtLabel.setHorizontalAlignment(SwingConstants.CENTER);
        txtLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        card.add(valLabel, BorderLayout.CENTER);
        card.add(txtLabel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(0, 130));
        return card;
    }

    private JPanel createRegisteredAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- Header Section with Title and Mode Button ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(MAIN_BG);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Registered User Accounts");
        title.setFont(HEADER_FONT);
        title.setForeground(SIDEBAR_BG_TOP);

        // Mode Button Panel
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        modePanel.setOpaque(false);

        // Create the Toggle Button
        JButton btnToggleMode = new JButton();
        
        // Initial Style update
        updateToggleButton(btnToggleMode);

        // Action Listener to Toggle Mode
        btnToggleMode.addActionListener(e -> {
            isEditMode = !isEditMode; // Toggle state
            updateToggleButton(btnToggleMode); // Update visual
            
            // Force table to repaint to reflect new editable state
            // Finding the ScrollPane's viewport view (the table) and repainting it is handled dynamically
            // by the TableModel's isCellEditable, but we should probably trigger a repaint or focus change
            // to visualy refresh properly if a cell was currently selected.
            panel.repaint(); 
        });

        modePanel.add(btnToggleMode);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(modePanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // --- Columns ---
        String[] columns = {
            "Last Name", 
            "First Name", 
            "Middle Name", 
            "Email Address", 
            "Username", 
            "Password", 
            "Contact #"
        };

        // --- Data ---
        Object[][] data = {
            {"Doe", "John", "A.", "john@example.com", "johndoe", "pass123", "0917-123-4567"},
            {"Smith", "Jane", "B.", "jane@test.com", "janesmith", "secure456", "0918-987-6543"},
            {"Wilson", "Bob", "C.", "bobw@mail.com", "bwilson", "qwerty789", "0919-555-1212"},
            {"Cooper", "Alice", "D.", "alice@music.com", "alcooper", "rocknroll", "0920-333-4444"},
            {"Brown", "Charlie", "E.", "charlie@peanuts.com", "cbrown", "snoopy1", "0921-777-8888"}
        };

        // Override DefaultTableModel to control editability based on state
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditMode;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);
        
        // Adjust column width for Email since it's usually long
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdoptingPetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Adoption Applications");
        title.setFont(HEADER_FONT);
        title.setForeground(SIDEBAR_BG_TOP);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {
            "Pet Name", "Gender", "Age", "Breed", "Health", "Contact", "Traits", "Reason"
        };

        Object[][] data = {
            {"Buddy", "Male", "2 yrs", "Golden Retriever", "Vaccinated", "0917-123-4567", "Friendly", "Companion"},
            {"Luna", "Female", "1 yr", "Siamese Cat", "Spayed", "0998-765-4321", "Calm", "Loves cats"},
            {"Max", "Male", "4 mos", "Beagle", "Healthy", "0912-345-6789", "Playful", "Family pet"},
            {"Bella", "Female", "3 yrs", "Poodle", "Sick", "0922-111-2222", "Smart", "Gift"},
            {"Rocky", "Male", "5 yrs", "Bulldog", "Healthy", "0915-888-9999", "Lazy", "Guard dog"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        styleTable(table);
        
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setFont(MAIN_FONT);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(Color.WHITE);
                lbl.setForeground(new Color(120, 120, 120));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(new MatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
    }

    // Helper method to update the single button based on current state
    private void updateToggleButton(JButton btn) {
        if (isEditMode) {
            btn.setText("Edit Mode");
            btn.setBackground(EDIT_MODE_COLOR);
            btn.setToolTipText("Click to switch to View Only mode");
        } else {
            btn.setText("View Mode");
            btn.setBackground(VIEW_MODE_COLOR);
            btn.setToolTipText("Click to enable editing");
        }
        
        // Common Styling
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DashboardAdmin().setVisible(true);
        });
    }
    
    static class MatteBorder extends javax.swing.border.MatteBorder {
        public MatteBorder(int top, int left, int bottom, int right, Color matteColor) {
            super(top, left, bottom, right, matteColor);
        }
    }
}