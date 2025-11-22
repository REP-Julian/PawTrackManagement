import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.ParseException;
import java.util.Date;

public class VetAppointmentSystem extends JFrame {

    // --- THEME CONSTANTS ---
    private static final Color COL_PRIMARY = new Color(79, 70, 229);    // Indigo
    private static final Color COL_PRIMARY_HOVER = new Color(67, 56, 202);
    private static final Color COL_BACKGROUND = new Color(243, 244, 246); // Light Gray
    private static final Color COL_SURFACE = Color.WHITE;
    private static final Color COL_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COL_TEXT_SEC = new Color(107, 114, 128);
    private static final Color COL_ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color COL_BORDER = new Color(229, 231, 235);
    
    private static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    // --- DATA STRUCTURES ---
    
    // Redesigned Data Class
    record Doctor(
        String name, 
        String specialty, 
        String phone, 
        String email,
        String schedule, 
        String clinic,
        String rating,
        int yearsExp
    ) {}

    private int currentVetIndex = 0;
    
    // Enhanced Data Set
    private final Doctor[] doctors = {
        new Doctor("Dr. Olivia Sterling", "Chief of Surgery", "0917-555-0101", "olivia.s@paws.ph", "Mon-Fri: 8am-4pm", "Sterling Surgical Center", "5.0", 15),
        new Doctor("Dr. Marcus Chen", "Exotic Animal Specialist", "0917-555-0102", "marcus.c@paws.ph", "Tue-Sat: 10am-6pm", "Wild & Free Clinic", "4.9", 12),
        new Doctor("Dr. Sarah Jenkins", "Internal Medicine", "0917-555-0103", "sarah.j@paws.ph", "Mon-Thu: 9am-5pm", "City Vet Hospital", "4.8", 8),
        new Doctor("Dr. James Wilson", "Orthopedic Specialist", "0917-555-0104", "james.w@paws.ph", "Wed-Sun: 11am-8pm", "Bone & Joint Center", "4.9", 20),
        new Doctor("Dr. Emily Rose", "Dermatology & Allergy", "0917-555-0105", "emily.r@paws.ph", "Fri-Mon: 8am-6pm", "Skin Care Clinic", "4.7", 6),
        new Doctor("Dr. David Park", "Emergency & Critical Care", "0917-555-0106", "david.p@paws.ph", "Daily: 6pm-2am", "24/7 Pet Emergency", "5.0", 10)
    };

    // --- COMPONENTS ---
    private JPanel vetCardPanel;
    private JComboBox<String> vetSelector;
    // Form Fields
    private JTextField txtOwner, txtPetName, txtPetType, txtBreed, txtWeight, txtEmergency;
    private JComboBox<String> cmbGender;
    private JFormattedTextField txtDate, txtPhone, txtVaccine;
    private JTextArea txtHistory, txtAllergies;
    private JSpinner timeSpinner;
    private JPanel builtMainPanel; // root panel for embedded usage

    // Replace the old single constructor with two forms:
    // - no-arg constructor keeps existing JFrame behavior (standalone)
    // - boolean embedded constructor builds UI but does not treat this as a visible frame
    public VetAppointmentSystem() {
        // Standalone behavior: set up frame and add built panel
        setTitle("Paws & Claws | Modern Appointment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 850);
        setLocationRelativeTo(null);
        setBackground(COL_BACKGROUND);

        // Build UI and add to frame
        builtMainPanel = buildMainPanel();
        add(builtMainPanel);
    }

    // New constructor to create an embeddable instance (no frame config)
    public VetAppointmentSystem(boolean embedded) {
        // If embedded == true, only construct the UI and keep it available via getMainPanel()
        // If embedded == false, behave like no-arg constructor (but we delegate to no-arg for simplicity)
        if (!embedded) {
            // delegate to default behavior
            // Note: calling this() here would require it to be first statement; so mirror minimal settings
            setTitle("Paws & Claws | Modern Appointment System");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1200, 850);
            setLocationRelativeTo(null);
            setBackground(COL_BACKGROUND);
            builtMainPanel = buildMainPanel();
            add(builtMainPanel);
        } else {
            // embedded: just prepare the panel
            builtMainPanel = buildMainPanel();
        }
    }

    // Public getter for embedding
    public JPanel getMainPanel() {
        return builtMainPanel;
    }

    // Extracted UI build into a single method used by both constructors
    private JPanel buildMainPanel() {
        // The original constructor body is moved here. Use instance fields as before.
        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(COL_BACKGROUND);

        // 1. Header
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // 2. Content (Split Pane or GridBag)
        JPanel contentGrid = new JPanel(new GridBagLayout());
        contentGrid.setBackground(COL_BACKGROUND);
        contentGrid.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        // Left Panel (Form) - Takes 70% width
        gbc.gridx = 0;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 0, 20);
        contentGrid.add(createFormSection(), gbc);

        // Right Panel (Vet Info & Map) - Takes 30% width
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentGrid.add(createSideBar(), gbc);

        mainPanel.add(contentGrid, BorderLayout.CENTER);

        // return the assembled panel
        return mainPanel;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_SURFACE);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COL_BORDER));

        JPanel internal = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        internal.setOpaque(false);

        // Logo / Icon representation
        JLabel logo = new JLabel("✚") { // Simple text icon, could be image
            @Override
            public void setForeground(Color fg) { super.setForeground(COL_PRIMARY); }
        };
        logo.setFont(new Font("Segoe UI Symbol", Font.BOLD, 32));
        
        JPanel textContainer = new JPanel(new GridLayout(2, 1));
        textContainer.setOpaque(false);
        
        JLabel title = new JLabel("Veterinary Appointment System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COL_TEXT_MAIN);
        
        JLabel subtitle = new JLabel("New Appointment Booking");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(COL_TEXT_SEC);
        
        textContainer.add(title);
        textContainer.add(subtitle);

        internal.add(logo);
        internal.add(textContainer);
        header.add(internal, BorderLayout.WEST);

        return header;
    }

    private JPanel createFormSection() {
        RoundedPanel panel = new RoundedPanel(20, COL_SURFACE);
        panel.setLayout(new BorderLayout());
        
        // Form Header
        JLabel lblTitle = new JLabel("Patient Information");
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(COL_TEXT_MAIN);
        lblTitle.setBorder(new EmptyBorder(20, 25, 10, 25));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Scrollable Form Content
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(COL_SURFACE);
        formContent.setBorder(new EmptyBorder(10, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // --- ROW 1: Owner & Pet Name ---
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.5;
        addInput(formContent, gbc, "Owner Name", txtOwner = createTextField());
        gbc.gridx = 1;
        addInput(formContent, gbc, "Pet Name", txtPetName = createTextField());

        // --- ROW 2: Type & Breed ---
        gbc.gridy++; gbc.gridx = 0;
        addInput(formContent, gbc, "Pet Type (e.g. Dog)", txtPetType = createTextField());
        gbc.gridx = 1;
        addInput(formContent, gbc, "Breed/Color", txtBreed = createTextField());

        // --- ROW 3: Gender & Weight ---
        gbc.gridy++; gbc.gridx = 0;
        cmbGender = new JComboBox<>(new String[]{"Male", "Female"});
        styleComboBox(cmbGender);
        addInput(formContent, gbc, "Gender", cmbGender);
        gbc.gridx = 1;
        addInput(formContent, gbc, "Weight (kg)", txtWeight = createTextField());

        // --- ROW 4: Phone & Emergency ---
        gbc.gridy++; gbc.gridx = 0;
        txtPhone = createFormattedField("+63 ###-###-####");
        addInput(formContent, gbc, "Contact Number", txtPhone);
        gbc.gridx = 1;
        addInput(formContent, gbc, "Emergency Contact", txtEmergency = createTextField());

        // --- DIVIDER ---
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(COL_BORDER);
        gbc.insets = new Insets(20, 10, 20, 10);
        formContent.add(sep, gbc);
        gbc.insets = new Insets(8, 10, 8, 10); // Reset insets
        gbc.gridwidth = 1; // Reset width

        // --- ROW 5: Vet & Date ---
        gbc.gridy++; gbc.gridx = 0;
        vetSelector = new JComboBox<>();
        for(Doctor d : doctors) vetSelector.addItem(d.name);
        styleComboBox(vetSelector);
        vetSelector.addActionListener(e -> {
            currentVetIndex = vetSelector.getSelectedIndex();
            updateVetCard();
        });
        addInput(formContent, gbc, "Select Veterinarian", vetSelector);
        
        gbc.gridx = 1;
        txtDate = createFormattedField("##/##/####");
        addInput(formContent, gbc, "Date (MM/DD/YYYY)", txtDate);

        // --- ROW 6: Time & Last Vaccine ---
        gbc.gridy++; gbc.gridx = 0;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
        timeSpinner.setEditor(de);
        styleSpinner(timeSpinner);
        addInput(formContent, gbc, "Preferred Time", timeSpinner);

        gbc.gridx = 1;
        txtVaccine = createFormattedField("##/##/####");
        addInput(formContent, gbc, "Last Vaccination", txtVaccine);

        // --- ROW 7: History (Full Width) ---
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        txtHistory = createTextArea(3);
        addInput(formContent, gbc, "Medical History", new JScrollPane(txtHistory));

        // --- ROW 8: Allergies (Full Width) ---
        gbc.gridy++;
        txtAllergies = createTextArea(2);
        addInput(formContent, gbc, "Known Allergies", new JScrollPane(txtAllergies));

        // --- BUTTONS ---
        gbc.gridy++;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(COL_SURFACE);
        
        ModernButton btnClear = new ModernButton("Clear Form", COL_BACKGROUND, COL_TEXT_MAIN);
        btnClear.addActionListener(e -> clearForm());
        
        ModernButton btnBook = new ModernButton("Confirm Booking", COL_PRIMARY, Color.WHITE);
        btnBook.addActionListener(e -> showSuccessDialog());

        btnPanel.add(btnClear);
        btnPanel.add(btnBook);
        
        gbc.insets = new Insets(20, 10, 0, 10);
        formContent.add(btnPanel, gbc);

        // Wrap in custom scroll pane
        JScrollPane scroll = new JScrollPane(formContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSideBar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);

        // 1. Vet Card
        vetCardPanel = new JPanel(new BorderLayout());
        vetCardPanel.setOpaque(false);
        updateVetCard(); // Initial draw
        sidebar.add(vetCardPanel);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Navigation
        JPanel navPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        navPanel.setOpaque(false);
        navPanel.setMaximumSize(new Dimension(300, 40));
        
        ModernButton btnPrev = new ModernButton("← Prev", COL_SURFACE, COL_TEXT_MAIN);
        btnPrev.setBorderColor(COL_BORDER);
        btnPrev.addActionListener(e -> {
            currentVetIndex = (currentVetIndex - 1 + doctors.length) % doctors.length;
            vetSelector.setSelectedIndex(currentVetIndex);
        });

        ModernButton btnNext = new ModernButton("Next →", COL_SURFACE, COL_TEXT_MAIN);
        btnNext.setBorderColor(COL_BORDER);
        btnNext.addActionListener(e -> {
            currentVetIndex = (currentVetIndex + 1) % doctors.length;
            vetSelector.setSelectedIndex(currentVetIndex);
        });

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        sidebar.add(navPanel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Map Placeholder
        RoundedPanel mapPanel = new RoundedPanel(20, new Color(219, 234, 254)); // Light Blue
        mapPanel.setLayout(new GridBagLayout());
        mapPanel.setPreferredSize(new Dimension(0, 200));
        mapPanel.setMaximumSize(new Dimension(3000, 250));
        
        JLabel mapIcon = new JLabel("📍");
        mapIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        JLabel mapText = new JLabel("Clinic Location Map");
        mapText.setFont(FONT_LABEL);
        mapText.setForeground(COL_PRIMARY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        mapPanel.add(mapIcon, gbc);
        gbc.gridy++;
        mapPanel.add(mapText, gbc);

        sidebar.add(mapPanel);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void updateVetCard() {
        vetCardPanel.removeAll();
        
        Doctor doc = doctors[currentVetIndex];
        
        RoundedPanel card = new RoundedPanel(20, COL_SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Avatar Circle
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COL_PRIMARY);
                g2.fillOval(getWidth()/2 - 35, 0, 70, 70);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                // Initials logic: "Dr. Olivia Sterling" -> "OS"
                String[] parts = doc.name.split(" ");
                String initials = (parts.length >= 3) ? parts[1].substring(0,1) + parts[2].substring(0,1) : "DR";
                
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = (70 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, x, y);
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(300, 80));
        avatarPanel.setMaximumSize(new Dimension(300, 80));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(doc.name);
        lblName.setFont(FONT_H2);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel(doc.specialty);
        lblTitle.setFont(FONT_BODY);
        lblTitle.setForeground(COL_ACCENT_GREEN);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Star Rating
        JLabel lblRating = new JLabel("★ " + doc.rating + " (" + doc.yearsExp + " yrs exp)");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRating.setForeground(new Color(245, 158, 11)); // Amber
        lblRating.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(200, 10));
        sep.setForeground(COL_BORDER);
        
        JPanel infoBox = new JPanel(new GridLayout(4, 1, 0, 8));
        infoBox.setOpaque(false);
        infoBox.add(createIconLabel("📞  " + doc.phone));
        infoBox.add(createIconLabel("✉  " + doc.email));
        infoBox.add(createIconLabel("🕒  " + doc.schedule));
        infoBox.add(createIconLabel("🏥  " + doc.clinic));
        infoBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(avatarPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblName);
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblRating);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(sep);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(infoBox);

        vetCardPanel.add(card);
        vetCardPanel.revalidate();
        vetCardPanel.repaint();
    }

    // --- HELPERS ---

    private void addInput(JPanel p, GridBagConstraints gbc, String label, Component c) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setBackground(COL_SURFACE);
        
        JLabel l = new JLabel(label);
        l.setFont(FONT_LABEL);
        l.setForeground(COL_TEXT_SEC);
        
        wrapper.add(l, BorderLayout.NORTH);
        wrapper.add(c, BorderLayout.CENTER);
        
        p.add(wrapper, gbc);
    }

    private JLabel createIconLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(COL_TEXT_SEC);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JTextField createTextField() {
        ModernTextField tf = new ModernTextField();
        return tf;
    }

    private JFormattedTextField createFormattedField(String format) {
        try {
            MaskFormatter mf = new MaskFormatter(format);
            mf.setPlaceholderCharacter('_');
            ModernFormattedTextField tf = new ModernFormattedTextField(mf);
            return tf;
        } catch (ParseException e) { return new ModernFormattedTextField(); }
    }

    private JTextArea createTextArea(int rows) {
        JTextArea ta = new JTextArea(rows, 0);
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 8, 8, 8));
        ta.setBackground(new Color(249, 250, 251)); // Very light gray inside
        return ta;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(FONT_BODY);
        box.setBackground(Color.WHITE);
        box.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton b = super.createArrowButton();
                b.setBackground(Color.WHITE);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });
        ((JComponent) box.getRenderer()).setBorder(new EmptyBorder(5, 5, 5, 5));
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(FONT_BODY);
        spinner.setBorder(BorderFactory.createLineBorder(COL_BORDER));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor)editor).getTextField().setBorder(new EmptyBorder(8,8,8,8));
        }
    }

    private void showSuccessDialog() {
        JOptionPane.showMessageDialog(this, 
            "Appointment for " + txtPetName.getText() + " confirmed with " + vetSelector.getSelectedItem() + ".",
            "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        txtOwner.setText(""); txtPetName.setText(""); txtBreed.setText(""); txtPetType.setText("");
        txtWeight.setText(""); txtEmergency.setText(""); txtDate.setValue(null); txtPhone.setValue(null);
        txtHistory.setText(""); txtAllergies.setText("");
    }

    // --- CUSTOM UI CLASSES ---

    // 1. Rounded Panel with Shadow
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Shadow
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, radius, radius);

            // Main bg
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, radius, radius);
            
            // Border
            g2.setColor(COL_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-4, getHeight()-4, radius, radius);
            
            super.paintComponent(g);
        }
    }

    // 2. Modern Button
    class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private Color textColor;
        private Color borderColor = null;

        public ModernButton(String text, Color bg, Color fg) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = bg.equals(COL_SURFACE) ? new Color(243, 244, 246) : bg.darker();
            this.textColor = fg;
            
            setFont(FONT_LABEL);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(140, 40));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(normalColor); repaint(); }
            });
        }
        
        public void setBorderColor(Color c) { this.borderColor = c; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getModel().isRollover() ? hoverColor : normalColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }

            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
        }
    }

    // 3. Modern Text Field (Focus Glow)
    class ModernTextField extends JTextField {
        public ModernTextField() {
            setFont(FONT_BODY);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COL_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            setBackground(new Color(255, 255, 255));
            
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COL_PRIMARY, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }
                public void focusLost(FocusEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COL_BORDER, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }
            });
        }
    }
    
    class ModernFormattedTextField extends JFormattedTextField {
        public ModernFormattedTextField() { super(); init(); }
        public ModernFormattedTextField(MaskFormatter mf) { super(mf); init(); }
        
        private void init() {
            setFont(FONT_BODY);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COL_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            setBackground(Color.WHITE);
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COL_PRIMARY, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }
                public void focusLost(FocusEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COL_BORDER, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }
            });
        }
    }

    // 4. Minimal Scrollbar
    class ModernScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            this.thumbColor = new Color(209, 213, 219);
            this.trackColor = COL_SURFACE;
        }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        
        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            return jbutton;
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
        }
    }

    public static void main(String[] args) {
        // Try to use the system look and feel for window borders, but use our custom components inside
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new VetAppointmentSystem().setVisible(true));
    }
}