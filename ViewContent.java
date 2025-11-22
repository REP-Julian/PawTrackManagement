import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.RoundRectangle2D;


public class ViewContent extends JFrame {

    private JFrame parentFrame;
    private String petName;

    public ViewContent() {
        this(null, "Buddy");
    }
    
    
    public ViewContent(JFrame parent, String petName) {
        this.parentFrame = parent;
        this.petName = petName != null ? petName : "Buddy";
        
        
        setTitle(this.petName + " - Dog Adoption Profile");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setResizable(false);
        setSize(760, 560);
        setLocationRelativeTo(parent); 
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                    parentFrame.toFront();
                }
            }
        });
        
        getContentPane().setBackground(new Color(243, 244, 246));
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));


        JPanel mainCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 20, 20);
                g2d.setColor(new Color(0, 0, 0, 4));
                g2d.fillRoundRect(1, 2, getWidth() - 2, getHeight() - 2, 20, 20);
                
                g2d.dispose();
            }
        };
        mainCard.setLayout(new BorderLayout(10, 28));
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(new EmptyBorder(40, 40, 40, 40));
        mainCard.setPreferredSize(new Dimension(680, 480));
        
        JPanel topSection = new JPanel(new BorderLayout(40, 0));
        topSection.setOpaque(false);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        detailsPanel.add(createDetailLabel("Name:", this.petName));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(createDetailLabel("Gender:", "Male"));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(createDetailLabel("Age:", "2 years"));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(createDetailLabel("Breed:", "Golden Retriever"));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(createDetailLabel("Color:", "Golden"));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(createDetailLabel("Health:", "Vaccinated & Neutered"));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(createDetailLabel("Personality:", "Friendly, Playful"));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(createDetailLabel("Contact Number:", "123-456-7890"));

        JLabel imageLabel = createPetImageLabel();
        
        topSection.add(detailsPanel, BorderLayout.CENTER);
        topSection.add(imageLabel, BorderLayout.EAST);
        
        JPanel bottomSection = new JPanel(new BorderLayout(0, 16));
        bottomSection.setOpaque(false);
        
        JLabel reasonTitle = new JLabel("Reason for Adoption:");
        reasonTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        reasonTitle.setForeground(new Color(17, 24, 39));

        JTextArea reasonText = new JTextArea(
            "Buddy is a wonderful and energetic dog looking for a forever home. " +
            "His previous owner had to move to a location where pets were not allowed. " +
            "He loves to play fetch and enjoys long walks in the park. He is great " +
            "with kids and other dogs, making him a perfect family companion."
        );
        reasonText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reasonText.setForeground(new Color(75, 85, 99));
        reasonText.setWrapStyleWord(true);
        reasonText.setLineWrap(true);
        reasonText.setEditable(false);
        reasonText.setOpaque(false); 
        reasonText.setHighlighter(null);

        bottomSection.add(reasonTitle, BorderLayout.NORTH);
        bottomSection.add(reasonText, BorderLayout.CENTER);

        mainCard.add(topSection, BorderLayout.NORTH);
        mainCard.add(createStyledSeparator(), BorderLayout.CENTER);
        mainCard.add(bottomSection, BorderLayout.SOUTH);
        
        add(mainCard);
    }

    private JLabel createDetailLabel(String key, String value) {
        String labelText = "<html><body style='font-family: Segoe UI; font-size: 13px; line-height: 1.6;'>" +
                        "<b style='color: rgb(17,24,39);'>" + key + "</b>" +
                        "<span style='color: rgb(75,85,99); margin-left: 8px;'> " + value + "</span>" +
                        "</body></html>";
        return new JLabel(labelText);
    }

    private JLabel createPetImageLabel() {
        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Draw image with rounded corners if available
                if (getIcon() != null) {
                    g2d.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                    super.paintComponent(g);
                }
                
                g2d.dispose();
                
                // Draw text if no icon
                if (getIcon() == null) {
                    super.paintComponent(g);
                }
            }
        };
        
        Dimension imageSize = new Dimension(180, 180);
        imageLabel.setPreferredSize(imageSize);
        imageLabel.setMaximumSize(imageSize);
        imageLabel.setMinimumSize(imageSize);
        imageLabel.setBorder(BorderFactory.createEmptyBorder());
        
        ImageIcon petImage = loadPetImage(this.petName);
        if (petImage != null) {
            imageLabel.setIcon(petImage);
        } else {
            imageLabel.setBackground(new Color(243, 244, 246));
            imageLabel.setOpaque(false);
            imageLabel.setText("No Image");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            imageLabel.setForeground(new Color(156, 163, 175));
        }
        
        return imageLabel;
    }
    
    private JPanel createStyledSeparator() {
        JPanel separatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(229, 231, 235));
                g2d.fillRoundRect(0, getHeight() / 2 - 1, getWidth(), 1, 1, 1);
                g2d.dispose();
            }
        };
        separatorPanel.setOpaque(false);
        separatorPanel.setPreferredSize(new Dimension(0, 1));
        return separatorPanel;
    }
    
    private ImageIcon loadPetImage(String petName) {
        try {
            String[] extensions = {".jpg", ".jpeg", ".png", ".gif"};
            String[] directories = {
                "images/pets/",
                "src/images/pets/",
                "resources/images/pets/",
                ""
            };
            
            for (String dir : directories) {
                for (String ext : extensions) {
                    String imagePath = dir + petName.toLowerCase().replaceAll("\\s+", "_") + ext;
                    File imageFile = new File(imagePath);
                    
                    if (imageFile.exists()) {
                        BufferedImage originalImage = ImageIO.read(imageFile);
                        Image scaledImage = originalImage.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading image for " + petName + ": " + e.getMessage());
        }
        
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewContent frame = new ViewContent();
            frame.setVisible(true);
        });
    }
}

