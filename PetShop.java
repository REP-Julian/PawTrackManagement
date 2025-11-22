import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class PetShop extends JFrame {
    // ...existing fields...
    private JPanel mainPanelForEmbedding;
    private final boolean embedded;
    
    // Constructors: ensure final fields are initialized
    public PetShop() {
        this(false);
    }

    public PetShop(boolean embedded) {
        this.embedded = embedded;
        this.theme = new UITheme();
        this.inventoryManager = new InventoryManager();
        this.cartManager = new CartManager();
        initializeApplication();
    }
    
    // Core Data Models
    private final InventoryManager inventoryManager;
    private final CartManager cartManager;
    private final UITheme theme;
    
    // UI Components
    private NavigationBar navigationBar;
    private SidebarPanel sidebarPanel;
    private ProductGridPanel productGridPanel;
    private FilterManager filterManager;
    
    // Design System
    public static class UITheme {
        // Elegant Color Palette
        public static final Color PRIMARY = new Color(16, 24, 40);
        public static final Color PRIMARY_LIGHT = new Color(30, 41, 59);
        public static final Color ACCENT = new Color(79, 70, 229);
        public static final Color ACCENT_LIGHT = new Color(99, 102, 241);
        public static final Color SUCCESS = new Color(16, 185, 129);
        public static final Color SUCCESS_LIGHT = new Color(52, 211, 153);
        public static final Color WARNING = new Color(245, 158, 11);
        public static final Color WARNING_LIGHT = new Color(251, 191, 36);
        public static final Color DANGER = new Color(239, 68, 68);
        public static final Color DANGER_LIGHT = new Color(248, 113, 113);
        
        // Neutral Colors
        public static final Color GRAY_50 = new Color(249, 250, 251);
        public static final Color GRAY_100 = new Color(243, 244, 246);
        public static final Color GRAY_200 = new Color(229, 231, 235);
        public static final Color GRAY_300 = new Color(209, 213, 219);
        public static final Color GRAY_400 = new Color(156, 163, 175);
        public static final Color GRAY_500 = new Color(107, 114, 128);
        public static final Color GRAY_600 = new Color(75, 85, 99);
        public static final Color GRAY_700 = new Color(55, 65, 81);
        public static final Color GRAY_800 = new Color(31, 41, 55);
        public static final Color GRAY_900 = new Color(17, 24, 39);
        
        // Typography
        public static Font getFont(FontWeight weight, int size) {
            String fontName = "Segoe UI";
            int style = weight == FontWeight.BOLD ? Font.BOLD : 
                       weight == FontWeight.MEDIUM ? Font.BOLD : Font.PLAIN;
            return new Font(fontName, style, size);
        }
        
        public enum FontWeight { LIGHT, REGULAR, MEDIUM, BOLD }
        
        // Shadows
        public static void drawShadow(Graphics2D g2d, int x, int y, int width, int height, 
                                    int radius, int shadowSize, float opacity) {
            for (int i = 0; i < shadowSize; i++) {
                g2d.setColor(new Color(0, 0, 0, (int)(opacity * (shadowSize - i) / shadowSize * 255)));
                g2d.fillRoundRect(x + i, y + i, width - 2*i, height - 2*i, radius, radius);
            }
        }
    }
    
    // Modern Button Component
    public static class ElegantButton extends JButton {
        private final Color baseColor;
        private final Color hoverColor;
        private final int borderRadius;
        private boolean isHovered = false;
        private boolean isPrimary;
        
        public ElegantButton(String text, Color baseColor, Color hoverColor, boolean isPrimary) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.borderRadius = isPrimary ? 12 : 8;
            this.isPrimary = isPrimary;
            
            setupButton();
        }
        
        private void setupButton() {
            setBackground(baseColor);
            setForeground(Color.WHITE);
            setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, isPrimary ? 14 : 12));
            setBorder(BorderFactory.createEmptyBorder(isPrimary ? 18 : 14, 
                                                    isPrimary ? 28 : 20,
                                                    isPrimary ? 18 : 14,
                                                    isPrimary ? 28 : 20));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
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
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Shadow
            if (isPrimary) {
                UITheme.drawShadow(g2d, 2, 4, getWidth() - 4, getHeight() - 8, borderRadius, 8, 0.15f);
            }
            
            // Button background
            g2d.setColor(isHovered ? hoverColor : baseColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);
            
            g2d.dispose();
            super.paintComponent(g);
        }
    }
    
    // Data Models
    public static class Item {
        private final String id;
        private final String name;
        private final String category;
        private final double price;
        private final String description;
        private int stock;
        private final double rating;
        private final int reviews;
        private final String brand;
        private final boolean freeShipping;
        private final String imagePath;
        
        public Item(String name, String category, double price, String description, int stock) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.category = category;
            this.price = price;
            this.description = description;
            this.stock = stock;
            this.rating = 3.5 + (Math.random() * 1.5);
            this.reviews = (int)(Math.random() * 500) + 10;
            this.brand = generateRandomBrand(category);
            this.freeShipping = Math.random() > 0.3;
            this.imagePath = getImagePathForItem(name, category);
        }
        
        private String sanitizeFileName(String s) {
            return s.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
        }
        
        private String getImagePathForItem(String name, String category) {
            // Expected directory layout (adjust to your project):
            // assets/images/food/, assets/images/utilities/, assets/images/accessories/, assets/images/healthcare/
            String cat = category.toLowerCase();
            String folder;
            switch (cat) {
                case "food": folder = "food"; break;
                case "utilities": folder = "utilities"; break;
                case "accessories": folder = "accessories"; break;
                case "healthcare": folder = "healthcare"; break;
                default: folder = "default"; break;
            }
            String fileName = sanitizeFileName(name) + ".jpg";
            return "assets/images/" + folder + "/" + fileName; // relative path; adjust as needed
        }
         
        private String generateRandomBrand(String category) {
            // Simplified brand selection: use a small generic pool (removed large category map)
            String[] genericBrands = new String[]{"Generic", "PetCo", "PawSource", "PetBasics", "NaturePet"};
            return genericBrands[(int)(Math.random() * genericBrands.length)];
         }
         
         // Getters
         public String getId() { return id; }
         public String getName() { return name; }
         public String getCategory() { return category; }
         public double getPrice() { return price; }
         public String getDescription() { return description; }
         public int getStock() { return stock; }
         public double getRating() { return rating; }
         public int getReviews() { return reviews; }
         public String getBrand() { return brand; }
         public boolean hasFreeShipping() { return freeShipping; }
         public String getImagePath() { return imagePath; }
         
         public void decreaseStock(int amount) { 
             this.stock = Math.max(0, this.stock - amount); 
         }
     }
     
    public static class CartItem {
        private final Item item;
        private int quantity;
        
        public CartItem(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
        
        public Item getItem() { return item; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }
        public double getTotalPrice() { return item.getPrice() * quantity; }
    }
    
    // Business Logic Managers
    public static class InventoryManager {
        private final List<Item> items;
        private final List<Consumer<List<Item>>> listeners;
        
        public InventoryManager() {
            this.items = new ArrayList<>();
            this.listeners = new ArrayList<>();
            generateSampleInventory();
        }
        
        public void addListener(Consumer<List<Item>> listener) {
            listeners.add(listener);
        }
        
        public List<Item> getAllItems() {
            return new ArrayList<>(items);
        }
        
        public List<Item> filterItems(String searchText, String category, double maxPrice, boolean freeShippingOnly) {
            return items.stream()
                .filter(item -> searchText.isEmpty() || 
                    item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    item.getBrand().toLowerCase().contains(searchText.toLowerCase()))
                .filter(item -> category.equals("All Categories") || item.getCategory().equals(category))
                .filter(item -> item.getPrice() <= maxPrice)
                .filter(item -> !freeShippingOnly || item.hasFreeShipping())
                .collect(Collectors.toList());
        }
        
        public Optional<Item> findItemById(String id) {
            return items.stream().filter(item -> item.getId().equals(id)).findFirst();
        }
        
        private void generateSampleInventory() {
            String[] foodItems = {"Cattery best formula Cat&dog kitten snacks pure chicken treats freeze dried meat 500g", "Luca 1KG Dog and Cat Food Puppy/Kitten and Adult Nutrition Food All Breeds with Natural Ingredients", "50PCS Dog Treats Sausage for Training & Snacks", 
                                "Cat Dry Food - Veterinarian Approved, All Life Stages 1kg", "Nutritious Petsup Freeze-Dried Treats for Dogs & Cats - 30g, Made with Real Meat, High Protein & Grain-Free", "Royal Canin RECOVERY DIET in Can for Dog & Cat WET inCAN 195g VET Range"};
            String[] utilities = {"Dog Food Scoop Dog Cat Food Water Bowl Sealing Clip Silicone Collapsible Pet Bowl Dog Food Spoon", "Pet toy cat toy tumbler food leakage ball cat stick infrared laser cat toy", "Ins Pet Dog Plush Portable Phone Big Brother Toy Sounding bb Barking Paper Tibetan Food Toy Cat Dog Chewing", 
                                "Dog Toys Tumbler Leakage Ball Pet Cat Food Dispenser Ball Adjustable Food Hole Roly IQ Treat", "Pet Outdoor Water Cup Portable Travel Cup Food Feeding Cup Dog Water Dispenser Cat Travel Dual-use Drinking Cup", "Pet Dog Toy for Small Medium Large Dogs Interactive Food Dispenser IQ Training Treat Toys Cat Slow Feeder"};
            String[] accessories = {"Dog Bowl Dog Food Bowl Dog Drinking Water Bowl Non-slip Stainless Steel Durable Bowl Cat Food Bowl", "Paw. Automatic Pet Dog Cat Food Water Bowls Dispenser Gravity Pet Self Feeder Food Bowls Set 2in1", "Elizabeth Cone E-Collar Pet Cat Dog Cone Adjustable Safety Collar Circle Pet Head Cover Bite Anti", 
                                  "Bamboo Pet Bowl Detachable Cat Dog Food Water Feeder Double Bowl Wooden 2in1 Bowl Spine Protection", "Pet Bowl Cat Bowl Dog Bowl 2 in 1 Food Bowl Drinking Bottle Set Puppy Kitty Food Bowls Water Bowl", "Sniffing Pads Consume Pets Energy Dog Educational Toys Dog Food Toys Cats Relieve Boredom Animals Slow Food Sniff"};
            String[] healthcare = {"PUAINTA Multi Vitamin b for Dogs and Cats Hair Loss Oral Care Vitamin b1 b2 b6 Supplement 30ml", "Omogs Probiotics for Dogs and Cats Pet's Probiotic Food Supplements Digestion&Immune 250 Tablets", "Petsmed Immuno Care Syrup for Dog and Cat 100ML (1 Bottle)", 
                                 "Happy Tummy Cats ​​Treats Rich in Multivitamins & Probiotics Immunity Boost Cat Food For Overall Cat", "VCO organic extra virgin coconut oil for Pets Salmon oil (rich in Omega-3,EPA and DHA) for Dog&Cat", "Bark n’ bite Virgin Coconut Oil for dogs & cats 250 ml (Organic Cold Pressed VCO)"};
            
            addItemsToInventory(foodItems, "Food", 15);
            addItemsToInventory(utilities, "Utilities", 12);
            addItemsToInventory(accessories, "Accessories", 10);
            addItemsToInventory(healthcare, "Healthcare", 8);
            
            notifyListeners();
        }
        
        private void addItemsToInventory(String[] itemNames, String category, int count) {
            Random random = new Random();
            for (int i = 0; i < count; i++) {
                String name = itemNames[random.nextInt(itemNames.length)];
                
                // Generate random prices with 2-3 digits (limited to 1000)
                double price;
                int priceType = random.nextInt(3); // 0, 1, or 2
                
                switch (priceType) {
                    case 0: // 2-digit prices (10-99)
                        price = 10 + random.nextDouble() * 90; // 10.00 to 99.99
                        break;
                    case 1: // 3-digit prices (100-999)
                        price = 100 + random.nextDouble() * 500; // 100.00 to 999.99
                        break;
                    case 2: // Exactly 1000 or close to it (950-1000)
                        price = 550 + random.nextDouble() * 50; // 950.00 to 1000.00
                        break;
                    default:
                        price = 100; // fallback
                }
                
                // Round to 2 decimal places and ensure max 1000
                price = Math.min(1000.0, Math.round(price * 100.0) / 100.0);
                
                int stock = random.nextInt(25) + 5;
                String description = "Premium quality " + category.toLowerCase() + " for your beloved pet";
                
                items.add(new Item(name, category, price, description, stock));
            }
        }
        
        private void notifyListeners() {
            listeners.forEach(listener -> listener.accept(getAllItems()));
        }
    }
    
    public static class CartManager {
        private final List<CartItem> cartItems;
        private final List<Runnable> updateListeners;
        
        public CartManager() {
            this.cartItems = new ArrayList<>();
            this.updateListeners = new ArrayList<>();
        }
        
        public void addUpdateListener(Runnable listener) {
            updateListeners.add(listener);
        }
        
        public void addItem(Item item, int quantity) {
            Optional<CartItem> existing = findCartItem(item.getId());
            if (existing.isPresent()) {
                CartItem cartItem = existing.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            } else {
                cartItems.add(new CartItem(item, quantity));
            }
            notifyListeners();
        }
        
        public void removeItem(String itemId) {
            cartItems.removeIf(cartItem -> cartItem.getItem().getId().equals(itemId));
            notifyListeners();
        }
        
        public void updateQuantity(String itemId, int newQuantity) {
            findCartItem(itemId).ifPresent(cartItem -> {
                if (newQuantity <= 0) {
                    removeItem(itemId);
                } else {
                    cartItem.setQuantity(newQuantity);
                    notifyListeners();
                }
            });
        }
        
        public void clear() {
            cartItems.clear();
            notifyListeners();
        }
        
        public List<CartItem> getItems() {
            return new ArrayList<>(cartItems);
        }
        
        public int getTotalItems() {
            return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        }
        
        public double getTotalPrice() {
            return cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        }
        
        public boolean isEmpty() {
            return cartItems.isEmpty();
        }
        
        private Optional<CartItem> findCartItem(String itemId) {
            return cartItems.stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findFirst();
        }
        
        private void notifyListeners() {
            updateListeners.forEach(Runnable::run);
        }
    }
    
    // UI Components
    public class NavigationBar extends JPanel {
        private final JLabel cartBadge;
        protected final JTextField searchField; // Made protected for keyboard shortcut access
        
        public NavigationBar() {
            this.cartBadge = createCartBadge();
            this.searchField = createSearchField();
            setupLayout();
            buildNavigationBar();
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            // Responsive height based on screen size with better text accommodation
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int navHeight = Math.max(100, (int)(screenSize.height * 0.1));
            setPreferredSize(new Dimension(0, navHeight));
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Elegant gradient background
            GradientPaint gradient = new GradientPaint(0, 0, UITheme.PRIMARY, 0, getHeight(), UITheme.PRIMARY_LIGHT);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Bottom shadow
            UITheme.drawShadow(g2d, 0, getHeight() - 4, getWidth(), 4, 0, 4, 0.1f);
            
            g2d.dispose();
        }
        
        private void buildNavigationBar() {
            // Responsive padding based on screen size with better spacing
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int horizontalPadding = Math.max(40, (int)(screenSize.width * 0.025));
            int verticalPadding = Math.max(20, (int)(screenSize.height * 0.02));
            
            setBorder(BorderFactory.createEmptyBorder(verticalPadding, horizontalPadding, 
                                                 verticalPadding, horizontalPadding));
            
            // Logo section
            JPanel logoPanel = createResponsiveLogoPanel();
            add(logoPanel, BorderLayout.WEST);
            
            // Search section
            JPanel searchPanel = createResponsiveSearchPanel();
            add(searchPanel, BorderLayout.CENTER);
            
            // Cart section
            JPanel cartPanel = createResponsiveCartPanel();
            add(cartPanel, BorderLayout.EAST);
        }
        
        private JPanel createResponsiveLogoPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panel.setOpaque(false);
            
            // Responsive font sizes
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int logoFontSize = Math.max(24, (int)(screenSize.width * 0.018));
            int taglineFontSize = Math.max(12, (int)(screenSize.width * 0.008));
            
            JLabel logo = new JLabel("PawShop");
            logo.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, logoFontSize));
            logo.setForeground(Color.WHITE);
            
            JLabel tagline = new JLabel("Premium Pet Marketplace");
            tagline.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, taglineFontSize));
            tagline.setForeground(UITheme.GRAY_300);
            
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.add(logo);
            textPanel.add(tagline);
            
            panel.add(textPanel);
            return panel;
        }
        
        private JPanel createResponsiveSearchPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            panel.setOpaque(false);
            
            // Responsive search field width with better text accommodation
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int searchWidth = Math.max(350, (int)(screenSize.width * 0.3));
            int searchHeight = Math.max(55, (int)(screenSize.height * 0.06));
            
            searchField.setPreferredSize(new Dimension(searchWidth, searchHeight));
            
            ElegantButton searchBtn = new ElegantButton("Search", UITheme.ACCENT, UITheme.ACCENT_LIGHT, false);
            searchBtn.setPreferredSize(new Dimension(140, searchHeight));
            searchBtn.addActionListener(e -> performSearch());
            
            panel.add(searchField);
            panel.add(Box.createHorizontalStrut(15));
            panel.add(searchBtn);
            
            return panel;
        }
        
        private JPanel createResponsiveCartPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            panel.setOpaque(false);
            
            // Responsive cart badge and button sizing with better text space
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int buttonHeight = Math.max(55, (int)(screenSize.height * 0.06));
            
            panel.add(cartBadge);
            
            return panel;
        }
        
        private JLabel createCartBadge() {
            JLabel badge = new JLabel("Cart (0)") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(UITheme.WARNING);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                    
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            
            badge.setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, 14));
            badge.setForeground(Color.WHITE);
            badge.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            badge.setCursor(new Cursor(Cursor.HAND_CURSOR));
            badge.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showCartDialog();
                }
            });
            
            return badge;
        }
        
        public void updateCartBadge(int itemCount) {
            cartBadge.setText("Cart (" + itemCount + ")");
        }
        
        public String getSearchText() {
            String text = searchField.getText();
            return text.equals("Search products...") ? "" : text;
        }
        
        private void performSearch() {
            if (filterManager != null) {
                filterManager.applyFilters();
            }
        }

        private JTextField createSearchField() {
            JTextField field = new JTextField() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Modern search field background
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    
                    // Border
                    g2d.setColor(UITheme.GRAY_300);
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            
            field.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 16));
            field.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            field.setOpaque(false);
            field.setForeground(UITheme.PRIMARY);
            field.setText("Search products...");
            field.setForeground(UITheme.GRAY_500);
            
            // Placeholder behavior
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals("Search products...")) {
                        field.setText("");
                        field.setForeground(UITheme.PRIMARY);
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText("Search products...");
                        field.setForeground(UITheme.GRAY_500);
                    }
                }
            });
            
            // Enter key search
            field.addActionListener(e -> performSearch());
            
            return field;
        }
    }
    
    public class SidebarPanel extends JPanel {
        private JComboBox<String> categoryFilter;
        private JComboBox<String> sortFilter;
        private JCheckBox freeShippingCheck;
        
        public SidebarPanel() {
            setupSidebar();
        }
        
        private void setupSidebar() {
            // Responsive sidebar width based on screen size with better text space
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int sidebarWidth = Math.max(320, Math.min(400, (int)(screenSize.width * 0.22)));
            int sidebarPadding = Math.max(30, (int)(screenSize.width * 0.02));
            
            setPreferredSize(new Dimension(sidebarWidth, 0));
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(sidebarPadding, sidebarPadding, 
                                                 sidebarPadding, sidebarPadding));
            
            // Responsive title font with better sizing
            int titleFontSize = Math.max(22, (int)(screenSize.width * 0.016));
            
            JLabel titleLabel = new JLabel("Filters");
            titleLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, titleFontSize));
            titleLabel.setForeground(UITheme.PRIMARY);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(sidebarPadding));
            
            // Category Filter Section
            add(createFilterSection("Category", createCategoryFilter()));
            add(Box.createVerticalStrut(20));
            
            // Sort Filter Section
            add(createFilterSection("Sort by", createSortFilter()));
            add(Box.createVerticalStrut(20));
            
            // Special Offers Section
            add(createSpecialOffersSection());
            
            add(Box.createVerticalGlue());
        }
        
        private JPanel createFilterSection(String title, JComponent component) {
            JPanel section = new JPanel();
            section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
            section.setOpaque(false);
            section.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, 16));
            titleLabel.setForeground(UITheme.PRIMARY);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            section.add(titleLabel);
            section.add(Box.createVerticalStrut(8));
            section.add(component);
            
            return section;
        }
        
        private JComboBox<String> createCategoryFilter() {
            categoryFilter = new JComboBox<>(new String[]{"All Categories", "Food", "Utilities", "Accessories", "Healthcare"}) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    
                    g2d.setColor(UITheme.GRAY_200);
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            
            categoryFilter.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 16));
            categoryFilter.setBackground(Color.WHITE);
            categoryFilter.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            categoryFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            categoryFilter.setOpaque(false);
            categoryFilter.addActionListener(e -> {
                if (filterManager != null) {
                    filterManager.applyFilters();
                }
            });
            
            return categoryFilter;
        }
        
        private JComboBox<String> createSortFilter() {
            sortFilter = new JComboBox<>(new String[]{"Relevance", "Price: Low to High", "Price: High to Low", "Customer Reviews", "Newest"}) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    
                    g2d.setColor(UITheme.GRAY_200);
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            
            sortFilter.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 16));
            sortFilter.setBackground(Color.WHITE);
            sortFilter.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            sortFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            sortFilter.setOpaque(false);
            sortFilter.addActionListener(e -> {
                if (filterManager != null) {
                    filterManager.applyFilters();
                }
            });
            
            return sortFilter;
        }
        
        private JPanel createSpecialOffersSection() {
            JPanel section = new JPanel();
            section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
            section.setOpaque(false);
            section.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel titleLabel = new JLabel("Special Offers");
            titleLabel.setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, 16));
            titleLabel.setForeground(UITheme.PRIMARY);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            freeShippingCheck = new JCheckBox("Free Shipping Available");
            freeShippingCheck.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 14));
            freeShippingCheck.setOpaque(false);
            freeShippingCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
            freeShippingCheck.addActionListener(e -> {
                if (filterManager != null) {
                    filterManager.applyFilters();
                }
            });
            
            section.add(titleLabel);
            section.add(Box.createVerticalStrut(8));
            section.add(freeShippingCheck);
            
            return section;
        }
        
        public String getSelectedCategory() {
            return (String) categoryFilter.getSelectedItem();
        }
        
        public double getMaxPrice() {
            return 1000.0; // Return maximum price since slider is removed
        }
        
        public boolean isFreeShippingSelected() {
            return freeShippingCheck.isSelected();
        }
        
        public String getSelectedSort() {
            return (String) sortFilter.getSelectedItem();
        }
    }
    
    public class ProductGridPanel extends JPanel {
        private JScrollPane scrollPane;
        private JPanel gridContainer;
        
        public ProductGridPanel() {
            setupProductGrid();
        }
        
        private void setupProductGrid() {
            setLayout(new BorderLayout());
            setOpaque(false);
            
            // Create header
            JPanel headerPanel = createHeaderPanel();
            add(headerPanel, BorderLayout.NORTH);
            
            // Create grid container
            gridContainer = new JPanel();
            updateGridLayout();
            gridContainer.setOpaque(false);
            gridContainer.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            
            scrollPane = new JScrollPane(gridContainer);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            
            add(scrollPane, BorderLayout.CENTER);
        }
        
        private JPanel createHeaderPanel() {
            JPanel headerPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    
                    // Shadow
                    UITheme.drawShadow(g2d, 2, 2, getWidth() - 4, getHeight() - 4, 16, 4, 0.1f);
                }
            };
            
            // Responsive header sizing with better text accommodation
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int headerPadding = Math.max(30, (int)(screenSize.width * 0.025));
            int headerHeight = Math.max(120, (int)(screenSize.height * 0.1));
            
            headerPanel.setBorder(BorderFactory.createEmptyBorder(headerPadding, headerPadding * 2, 
                                                              headerPadding, headerPadding * 2));
            headerPanel.setOpaque(false);
            headerPanel.setPreferredSize(new Dimension(0, headerHeight));
            
            // Responsive fonts with better readability
            int titleFontSize = Math.max(28, (int)(screenSize.width * 0.018));
            int subtitleFontSize = Math.max(18, (int)(screenSize.width * 0.012));
            
            JLabel titleLabel = new JLabel("Pet Supplies - Premium Products");
            titleLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, titleFontSize));
            titleLabel.setForeground(UITheme.PRIMARY);
            
            JLabel subtitleLabel = new JLabel("Discover quality products for your beloved pets");
            subtitleLabel.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, subtitleFontSize));
            subtitleLabel.setForeground(UITheme.GRAY_600);
            
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.add(titleLabel);
            textPanel.add(subtitleLabel);
            
            headerPanel.add(textPanel, BorderLayout.WEST);
            
            return headerPanel;
        }
        
        private void updateGridLayout() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int availableWidth = screenSize.width - 400; // Subtract sidebar and padding
            int columns = calculateOptimalColumns(availableWidth);
            int gap = Math.max(35,(int)(screenSize.width * 0.015));
            
            gridContainer.setLayout(new GridLayout(0, columns, gap, gap));
        }
        
        private int calculateOptimalColumns(int availableWidth) {
            // Calculate based on available width minus sidebar with better spacing
            int cardMinWidth = 420; // Minimum card width for better text display
            int columns = Math.max(2,availableWidth / (cardMinWidth + 35)); // 25 for gap
            
            // Cap maximum columns for readability
            return Math.min(columns, 5);
        }
        
        public void updateProducts(List<Item> items) {
            gridContainer.removeAll();
            updateGridLayout();
            
            for (Item item : items) {
                JPanel productCard = createProductCard(item);
                gridContainer.add(productCard);
            }
            
            gridContainer.revalidate();
            gridContainer.repaint();
        }
        
        private JPanel createProductCard(Item item) {
            JPanel card = new JPanel(new BorderLayout()) {
                private boolean isHovered = false;
                
                {
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
                
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Enhanced shadows with responsive sizing
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int shadowLayers = isHovered ? 8 : 4;
                    int shadowIntensity = isHovered ? 20 : 12;
                    
                    for (int i = 0; i < shadowLayers; i++) {
                        g2d.setColor(new Color(0, 0, 0, shadowIntensity - i * 2));
                        g2d.fillRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 20, 20);
                    }
                    
                    // Main card background
                    g2d.setColor(UITheme.GRAY_50);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    
                    g2d.dispose();
                }
            };
            
            card.setOpaque(false);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Responsive card sizing with better text space
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int cardPadding = Math.max(20, (int)(screenSize.width * 0.012));
            int cardHeight = Math.max(480, (int)(screenSize.height * 0.5));
            
            card.setBorder(BorderFactory.createEmptyBorder(cardPadding, cardPadding, cardPadding, cardPadding));
            card.setPreferredSize(new Dimension(0, cardHeight)); // Width will be managed by GridLayout
        
            // Enhanced product image with responsive sizing and click functionality
            int imageHeight = Math.max(140, (int)(cardHeight * 0.32));
            
            // Image label will show the image loaded from the item's imagePath
            JLabel imageLabel = new JLabel() {
                 @Override
                 protected void paintComponent(Graphics g) {
                     Graphics2D g2d = (Graphics2D) g.create();
                     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                     
                     GradientPaint gradient = new GradientPaint(0, 0, new Color(248, 250, 252), 
                         0, getHeight(), new Color(241, 245, 249));
                     g2d.setPaint(gradient);
                     g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                     
                     g2d.setColor(UITheme.GRAY_200);
                     g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                     
                     g2d.dispose();
                    super.paintComponent(g);
                 }
             };
             imageLabel.setPreferredSize(new Dimension(0, imageHeight));
             // Try to load image from path and scale to fit the imageHeight
             ImageIcon icon = loadImageIcon(item.getImagePath(), imageHeight * 2, imageHeight); // width heuristic
             if (icon != null) imageLabel.setIcon(icon);
             imageLabel.setOpaque(false);
             imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
             
             // Add click listener to image for full-screen view
             imageLabel.addMouseListener(new MouseAdapter() {
                 @Override
                 public void mouseClicked(MouseEvent e) {
                     showProductDetailModal(item);
                 }
             });
             
             // Enhanced product info panel with responsive fonts and better spacing
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(cardPadding, 0, 0, 0));
            
            // Responsive font sizes with better readability
            int brandFontSize = Math.max(13, (int)(screenSize.width * 0.009));
            int nameFontSize = Math.max(16, (int)(screenSize.width * 0.011));
            int priceFontSize = Math.max(20, (int)(screenSize.width * 0.016));
            int detailFontSize = Math.max(12, (int)(screenSize.width * 0.008));
            
            // Brand label
            JLabel brandLabel = new JLabel(item.getBrand());
            brandLabel.setFont(new Font("Segoe UI", Font.BOLD, brandFontSize));
            brandLabel.setForeground(UITheme.ACCENT);
            brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Product name with better text wrapping — use JTextArea for reliable wrapping and tooltip
            JTextArea nameArea = new JTextArea(item.getName());
            nameArea.setFont(new Font("Segoe UI", Font.PLAIN, nameFontSize));
            nameArea.setForeground(UITheme.PRIMARY);
            nameArea.setOpaque(false);
            nameArea.setEditable(false);
            nameArea.setLineWrap(true);
            nameArea.setWrapStyleWord(true);
            nameArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameArea.setBorder(null);
            nameArea.setToolTipText(item.getName());
            // Limit visual height to ~3 lines so cards stay consistent
            nameArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameFontSize * 4 + 8));
            
            // Rating panel
            JPanel ratingPanel = createResponsiveRatingPanel(item, detailFontSize);
            
            // Price
            JLabel priceLabel = new JLabel(String.format("₱%,.2f", item.getPrice()));
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, priceFontSize));
            priceLabel.setForeground(UITheme.DANGER);
            priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Shipping and stock info
            JPanel shippingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            shippingPanel.setOpaque(false);
            shippingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            if (item.hasFreeShipping()) {
                JLabel shippingLabel = new JLabel("Free Shipping");
                shippingLabel.setFont(new Font("Segoe UI", Font.BOLD, detailFontSize));
                shippingLabel.setForeground(UITheme.SUCCESS);
                shippingPanel.add(shippingLabel);
            }
            
            JLabel stockLabel = new JLabel(item.getStock() > 0 ? "In Stock (" + item.getStock() + ")" : "Out of Stock");
            stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, detailFontSize));
            stockLabel.setForeground(item.getStock() > 0 ? UITheme.SUCCESS : UITheme.DANGER);
            stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Add to cart button with responsive sizing and improved spacing
            int buttonHeight = Math.max(45, (int)(cardHeight * 0.09));
            
            ElegantButton addButton = new ElegantButton("Add to Cart", UITheme.ACCENT,
                UITheme.ACCENT_LIGHT, true);
            addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            // cap width so button doesn't overlap other UI; allow horizontal grow but with sensible cap
            addButton.setMaximumSize(new Dimension(320, buttonHeight));
            addButton.setPreferredSize(new Dimension(220, buttonHeight));
            addButton.setMargin(new Insets(8, 18, 8, 18));
            addButton.setFont(new Font("Segoe UI", Font.BOLD, Math.max(14, brandFontSize + 1)));
            addButton.setEnabled(item.getStock() > 0);
            addButton.addActionListener(e -> addToCartFromCard(item));
            
            // Buy Now button placed beside Add to Cart (adds item then proceeds to checkout)
            ElegantButton buyNowCardBtn = new ElegantButton("Buy Now", UITheme.SUCCESS, UITheme.SUCCESS_LIGHT, true);
            buyNowCardBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            buyNowCardBtn.setPreferredSize(new Dimension(140, buttonHeight));
            buyNowCardBtn.setMaximumSize(new Dimension(160, buttonHeight));
            buyNowCardBtn.setMargin(new Insets(8, 14, 8, 14));
            buyNowCardBtn.setFont(new Font("Segoe UI", Font.BOLD, Math.max(13, brandFontSize)));
            buyNowCardBtn.setEnabled(item.getStock() > 0);
            buyNowCardBtn.addActionListener(e -> {
                // add one item then immediately perform checkout
                cartManager.addItem(item, 1);
                performCheckout();
            });

            // Action panel to hold both buttons side-by-side and avoid overlap
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            actionPanel.setOpaque(false);
            actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            actionPanel.add(addButton);
            actionPanel.add(buyNowCardBtn);
            
            // Assemble info panel with slightly larger spacing so elements breathe
            int spacing = Math.max(10, cardPadding / 2);
            
            infoPanel.add(brandLabel);
            infoPanel.add(Box.createVerticalStrut(spacing));
            infoPanel.add(nameArea);
            infoPanel.add(Box.createVerticalStrut(spacing));
            infoPanel.add(ratingPanel);
            infoPanel.add(Box.createVerticalStrut(spacing * 2));
            infoPanel.add(priceLabel);
            infoPanel.add(Box.createVerticalStrut(spacing));
            infoPanel.add(shippingPanel);
            infoPanel.add(Box.createVerticalStrut(spacing));
            infoPanel.add(stockLabel);
            infoPanel.add(Box.createVerticalStrut(spacing * 2));
            infoPanel.add(actionPanel);
            
            card.add(imageLabel, BorderLayout.NORTH);
            card.add(infoPanel, BorderLayout.CENTER);
            
            return card;
        }
        
        private JPanel createResponsiveRatingPanel(Item item, int fontSize) {
            JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            ratingPanel.setOpaque(false);
            ratingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Create custom circle rating display
            JPanel circlePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int circleSize = fontSize + 2;
                    int spacing = 3;
                    int totalWidth = 5 * circleSize + 4 * spacing;
                    
                    for (int i = 0; i < 5; i++) {
                        int x = i * (circleSize + spacing);
                        int y = 2;
                        
                        // Determine if this circle should be filled based on rating
                        if (i < (int) item.getRating()) {
                            // Filled yellow circle
                            g2d.setColor(new Color(255, 193, 7)); // Yellow color
                            g2d.fillOval(x, y, circleSize, circleSize);
                        } else {
                            // Empty circle (gray border)
                            g2d.setColor(new Color(209, 213, 219)); // Light gray
                            g2d.setStroke(new BasicStroke(1.5f));
                            g2d.drawOval(x, y, circleSize, circleSize);
                        }
                    }
                    
                    g2d.dispose();
                }
                
                @Override
                public Dimension getPreferredSize() {
                    int circleSize = fontSize + 2;
                    int spacing = 3;
                    int totalWidth = 5 * circleSize + 4 * spacing;
                    int totalHeight = circleSize + 4;
                    return new Dimension(totalWidth, totalHeight);
                }
            };
            
            circlePanel.setOpaque(false);
            ratingPanel.add(circlePanel);
            
            JLabel ratingText = new JLabel(String.format(" %.1f", item.getRating()));
            ratingText.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            ratingText.setForeground(UITheme.PRIMARY);
            
            JLabel reviewsLabel = new JLabel(" (" + item.getReviews() + ")");
            reviewsLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize - 1));
            reviewsLabel.setForeground(UITheme.GRAY_500);
            
            ratingPanel.add(ratingText);
            ratingPanel.add(reviewsLabel);
            
            return ratingPanel;
        }
        
        private void addToCartFromCard(Item item) {
            cartManager.addItem(item, 1);
            showAddedToCartNotification(item);
        }
        
        private void showAddedToCartNotification(Item item) {
            JOptionPane.showMessageDialog(this, 
                "Added " + item.getName() + " to cart!", 
                "Added to Cart", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Placeholder classes for remaining components
    private class FilterManager {
        public void applyFilters() {
            String searchText = navigationBar != null ? navigationBar.getSearchText() : "";
            String category = sidebarPanel != null ? sidebarPanel.getSelectedCategory() : "All Categories";
            double maxPrice = sidebarPanel != null ? sidebarPanel.getMaxPrice() : 100;
            boolean freeShippingOnly = sidebarPanel != null ? sidebarPanel.isFreeShippingSelected() : false;
            
            List<Item> filteredItems = inventoryManager.filterItems(searchText, category, maxPrice, freeShippingOnly);
            
            // Apply sorting if needed
            String sortOption = sidebarPanel != null ? sidebarPanel.getSelectedSort() : "Relevance";
            filteredItems = applySorting(filteredItems, sortOption);
            
            if (productGridPanel != null) {
                productGridPanel.updateProducts(filteredItems);
            }
        }
        
        private List<Item> applySorting(List<Item> items, String sortOption) {
            return switch (sortOption) {
                case "Price: Low to High" -> items.stream()
                    .sorted(Comparator.comparing(Item::getPrice))
                    .collect(Collectors.toList());
                case "Price: High to Low" -> items.stream()
                    .sorted(Comparator.comparing(Item::getPrice).reversed())
                    .collect(Collectors.toList());
                case "Customer Reviews" -> items.stream()
                    .sorted(Comparator.comparing(Item::getRating).reversed())
                    .collect(Collectors.toList());
                default -> items; // Relevance or Newest
            };
        }
    }
    
    private void showCartDialog() {
        JDialog cartDialog = new JDialog(this, "Shopping Cart", true);
        
        // Responsive dialog sizing with better text accommodation
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogWidth = Math.max(900, (int)(screenSize.width * 0.65));
        int dialogHeight = Math.max(700, (int)(screenSize.height * 0.75));
        
        cartDialog.setSize(dialogWidth, dialogHeight);
        cartDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, UITheme.GRAY_50, 0, getHeight(), UITheme.GRAY_100);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        int padding = Math.max(30, (int)(screenSize.width * 0.02));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        // Responsive header font with better sizing
        int headerFontSize = Math.max(28, (int)(screenSize.width * 0.018));
        
        JLabel headerLabel = new JLabel("Your Shopping Cart");
        headerLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, headerFontSize));
        headerLabel.setForeground(UITheme.PRIMARY);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Cart items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        
        if (cartManager.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            emptyLabel.setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, 18));
            emptyLabel.setForeground(UITheme.GRAY_500);
            itemsPanel.add(emptyLabel);
        } else {
            for (CartItem cartItem : cartManager.getItems()) {
                itemsPanel.add(createCartItemPanel(cartItem, cartDialog));
                itemsPanel.add(Box.createVerticalStrut(12));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer with total and buttons
        JPanel footerPanel = createCartFooter(cartDialog);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        cartDialog.add(mainPanel);
        cartDialog.setVisible(true);
    }
    
    private JPanel createCartItemPanel(CartItem cartItem, JDialog parentDialog) {
        Item item = cartItem.getItem();
        
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                UITheme.drawShadow(g2d, 1, 1, getWidth() - 2, getHeight() - 2, 12, 3, 0.1f);
            }
        };
        
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 100));
        
        // Item info with better text sizing
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 18));
        nameLabel.setForeground(UITheme.PRIMARY);
        
        JLabel priceLabel = new JLabel(String.format("₱%,.2f each", item.getPrice()));
        priceLabel.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 16));
        priceLabel.setForeground(UITheme.GRAY_600);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Quantity controls
        JPanel controlsPanel = new JPanel(new FlowLayout());
        controlsPanel.setOpaque(false);
        
        ElegantButton decreaseBtn = new ElegantButton("−", UITheme.GRAY_500, UITheme.GRAY_600, false);
        decreaseBtn.addActionListener(e -> {
            cartManager.updateQuantity(item.getId(), cartItem.getQuantity() - 1);
            parentDialog.dispose();
            showCartDialog();
        });
        
        JLabel quantityLabel = new JLabel(String.valueOf(cartItem.getQuantity()));
        quantityLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 18));
        quantityLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        ElegantButton increaseBtn = new ElegantButton("+", UITheme.SUCCESS, UITheme.SUCCESS_LIGHT, false);
        increaseBtn.addActionListener(e -> {
            cartManager.updateQuantity(item.getId(), cartItem.getQuantity() + 1);
            parentDialog.dispose();
            showCartDialog();
        });
        
        ElegantButton removeBtn = new ElegantButton("Remove", UITheme.DANGER, UITheme.DANGER_LIGHT, false);
        removeBtn.addActionListener(e -> {
            cartManager.removeItem(item.getId());
            parentDialog.dispose();
            showCartDialog();
        });
        
        controlsPanel.add(decreaseBtn);
        controlsPanel.add(quantityLabel);
        controlsPanel.add(increaseBtn);
        controlsPanel.add(removeBtn);
        
        panel.add(controlsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCartFooter(JDialog cartDialog) {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        
        // Total
        JLabel totalLabel = new JLabel(String.format("Total: ₱%,.2f", cartManager.getTotalPrice()));
        totalLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 20));
        totalLabel.setForeground(UITheme.PRIMARY);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        ElegantButton continueBtn = new ElegantButton("Continue Shopping", UITheme.GRAY_500, UITheme.GRAY_600, false);
        continueBtn.addActionListener(e -> cartDialog.dispose());
        
        ElegantButton checkoutBtn = new ElegantButton("Checkout", UITheme.SUCCESS, UITheme.SUCCESS_LIGHT, true);
        checkoutBtn.setEnabled(!cartManager.isEmpty());
        checkoutBtn.addActionListener(e -> {
            performCheckout();
            cartDialog.dispose();
        });
        
        buttonPanel.add(continueBtn);
        buttonPanel.add(checkoutBtn);
        
        footerPanel.add(totalLabel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private void performCheckout() {
        if (cartManager.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double total = cartManager.getTotalPrice();
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Checkout total: ₱%,.2f\nProceed with purchase?", total),
            "Confirm Purchase", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Process the checkout
            for (CartItem cartItem : cartManager.getItems()) {
                cartItem.getItem().decreaseStock(cartItem.getQuantity());
            }
            
            cartManager.clear();
            
            // Refresh the product display
            if (filterManager != null) {
                filterManager.applyFilters();
            }
            
            JOptionPane.showMessageDialog(this, 
                "Purchase successful! Thank you for shopping with us!", 
                "Purchase Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void initializeApplication() {
        // Only create a visible JFrame when not embedded
        if (!embedded) {
            setupWindow();
        }

        createComponents();
        this.filterManager = new FilterManager(); // Initialize after components
        setupEventListeners();

        // layoutComponents now returns the main panel so we can embed it when needed
        JPanel builtMain = layoutComponents();
        if (embedded) {
            // store panel for embedding into another container
            this.mainPanelForEmbedding = builtMain;
        } else {
            // add to this JFrame for standalone mode
            add(builtMain);
            // Initial load and then show
            filterManager.applyFilters();
            setVisible(true);
        }
        // if embedded, apply filters now so products populate
        if (embedded) {
            filterManager.applyFilters();
        }
    }
    
    private void setupWindow() {
        setTitle("PawShop - Pet Marketplace");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Changed from EXIT_ON_CLOSE
        
        // Get screen dimensions and set to full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
    }
    
    private void createComponents() {
        navigationBar = new NavigationBar();
        sidebarPanel = new SidebarPanel();
        productGridPanel = new ProductGridPanel();
    }
    
    private void setupEventListeners() {
        // Cart update listener
        cartManager.addUpdateListener(() -> 
        navigationBar.updateCartBadge(cartManager.getTotalItems()));
        
        // Inventory update listener
        inventoryManager.addListener(items -> 
        productGridPanel.updateProducts(items));
        
        // Window resize listener for dynamic layout adjustments
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update grid layout based on new window size
                if (productGridPanel != null && filterManager != null) {
                    SwingUtilities.invokeLater(() -> {
                        filterManager.applyFilters();
                    });
                }
            }
        });
        
        // Keyboard shortcuts
        KeyStroke searchShortcut = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(searchShortcut, "focusSearch");
        getRootPane().getActionMap().put("focusSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (navigationBar != null && navigationBar.searchField != null) {
                    navigationBar.searchField.requestFocus();
                }
            }
        });
        
        KeyStroke cartShortcut = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cartShortcut, "openCart");
        getRootPane().getActionMap().put("openCart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCartDialog();
            }
        });
    }
    
    private JPanel layoutComponents() {
        // Build and return the mainPanel instead of always adding to the JFrame.
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Responsive gradient background
                GradientPaint gradient = new GradientPaint(0, 0, UITheme.GRAY_50, 0, getHeight(), UITheme.GRAY_100);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        mainPanel.add(navigationBar, BorderLayout.NORTH);
        
        // Content area with responsive layout
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        contentArea.add(sidebarPanel, BorderLayout.WEST);
        contentArea.add(productGridPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentArea, BorderLayout.CENTER);
        return mainPanel;
    }
    
    // Expose built panel so Dashboard can embed PetShop UI
    public JPanel getMainPanel() {
        return mainPanelForEmbedding;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new PetShop();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start PawTrack: " + e.getMessage(), 
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void returnToDashboard() {
        this.dispose();
    }
    
    private void showProductDetailModal(Item item) {
        JDialog modal = new JDialog(this, "Product Details", true);
        
        // Full screen modal
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        modal.setSize(screenSize);
        modal.setLocationRelativeTo(this);
        
        // Main container with gradient background
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dark overlay with gradient
                GradientPaint overlay = new GradientPaint(0, 0, new Color(0, 0, 0, 180), 
                0, getHeight(), new Color(0, 0, 0, 120));
                g2d.setPaint(overlay);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        mainContainer.setOpaque(false);
        
        // Close button
        JButton closeBtn = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setFont(new Font("Arial", Font.BOLD, 24));
        closeBtn.setForeground(UITheme.GRAY_700);
        closeBtn.setPreferredSize(new Dimension(50, 50));
        closeBtn.setBorder(null);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> modal.dispose());
        
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setOpaque(false);
        closePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        closePanel.add(closeBtn);
        
        // Content panel
        JPanel contentPanel = createProductDetailContent(item, modal);
        
        mainContainer.add(closePanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        modal.add(mainContainer);
        modal.setVisible(true);
    }
    
    private JPanel createProductDetailContent(Item item, JDialog modal) {
        JPanel contentPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Main content background with shadow
                int width = getWidth() - 100;
                int height = getHeight() - 100;
                int x = 50;
                int y = 50;
                
                // Shadow
                UITheme.drawShadow(g2d, x + 5, y + 5, width - 10, height - 10, 30, 20, 0.3f);
                
                // Background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x, y, width, height, 30, 30);
                
                g2d.dispose();
            }
        };
        contentPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 40, 40, 40);
        
        // Image section (left side)
        JPanel imageSection = createImageSection(item);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(imageSection, gbc);
        
        // Details section (right side)
        JPanel detailsSection = createDetailsSection(item, modal);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(detailsSection, gbc);
        
        return contentPanel;
    }
    
    private JPanel createImageSection(Item item) {
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Image background with gradient
                GradientPaint gradient = new GradientPaint(0, 0, UITheme.GRAY_50, 
                                                          0, getHeight(), UITheme.GRAY_100);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2d.setColor(UITheme.GRAY_200);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25);
                
                g2d.dispose();
            }
        };
        imagePanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
        
        // Create image display with category image and styling
        JPanel imageContainer = new JPanel(new GridBagLayout());
        imageContainer.setOpaque(false);
        
        // Category image (large)
        JLabel iconLabel = new JLabel();
        ImageIcon bigIcon = loadImageIcon(item.getImagePath(), 480, 480);
        if (bigIcon != null) iconLabel.setIcon(bigIcon);
         
         // Product category label
         JLabel categoryLabel = new JLabel(item.getCategory(), SwingConstants.CENTER);
         categoryLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 24));
         categoryLabel.setForeground(UITheme.PRIMARY);
         
         // Brand label
         JLabel brandLabel = new JLabel(item.getBrand(), SwingConstants.CENTER);
         brandLabel.setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, 18));
         brandLabel.setForeground(UITheme.ACCENT);
         
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.insets = new Insets(10, 0, 10, 0);
         
         gbc.gridy = 0;
         imageContainer.add(iconLabel, gbc);
         
         gbc.gridy = 1;
         imageContainer.add(categoryLabel, gbc);
         
         gbc.gridy = 2;
         imageContainer.add(brandLabel, gbc);
         
         imagePanel.add(imageContainer, BorderLayout.CENTER);
         return imagePanel;
     }
    
    private JPanel createDetailsSection(Item item, JDialog modal) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 20));
        
        // Brand
        JLabel brandLabel = new JLabel(item.getBrand());
        brandLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 18));
        brandLabel.setForeground(UITheme.ACCENT);
        brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Product title
        JLabel titleLabel = new JLabel(item.getName());
        titleLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 36));
        titleLabel.setForeground(UITheme.PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Rating panel
        JPanel ratingPanel = createModalRatingPanel(item);
        
        // Price
        JLabel priceLabel = new JLabel(String.format("₱%,.2f", item.getPrice()));
        priceLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 42));
        priceLabel.setForeground(UITheme.DANGER);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 20));
        descLabel.setForeground(UITheme.PRIMARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea descArea = new JTextArea(item.getDescription());
        descArea.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 16));
        descArea.setForeground(UITheme.GRAY_700);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Stock and shipping info
        JPanel infoPanel = createModalInfoPanel(item);
        
        // Quantity selector
        JPanel quantityPanel = createQuantitySelector();
        
        // Action buttons
        JPanel buttonPanel = createModalActionButtons(item, modal);
        
        // Assembly with spacing
        detailsPanel.add(brandLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(15));
        detailsPanel.add(ratingPanel);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(25));
        detailsPanel.add(descLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(descArea);
        detailsPanel.add(Box.createVerticalStrut(25));
        detailsPanel.add(infoPanel);
        detailsPanel.add(Box.createVerticalStrut(30));
        detailsPanel.add(quantityPanel);
        detailsPanel.add(Box.createVerticalStrut(30));
        detailsPanel.add(buttonPanel);
        
        return detailsPanel;
    }
    
    private JPanel createModalRatingPanel(Item item) {
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ratingPanel.setOpaque(false);
        ratingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Circle rating display
        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                               super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int circleSize = 25;
                int spacing = 5;
                
                for (int i = 0; i < 5; i++) {
                    int x = i * (circleSize + spacing);
                    int y = 2;
                    
                    if (i < (int) item.getRating()) {
                        g2d.setColor(new Color(255, 193, 7));
                        g2d.fillOval(x, y, circleSize, circleSize);
                    } else {
                        g2d.setColor(UITheme.GRAY_300);
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.drawOval(x, y, circleSize, circleSize);
                    }
                }
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(5 * 25 + 4 * 5, 29);
            }
        };
        
        JLabel ratingText = new JLabel(String.format(" %.1f", item.getRating()));
        ratingText.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 18));
        ratingText.setForeground(UITheme.PRIMARY);
        
        JLabel reviewsLabel = new JLabel(" (" + item.getReviews() + " reviews)");
        reviewsLabel.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 16));
        reviewsLabel.setForeground(UITheme.GRAY_500);
        
        ratingPanel.add(circlePanel);
        ratingPanel.add(ratingText);
        ratingPanel.add(reviewsLabel);
        
        return ratingPanel;
    }
    
    private JPanel createModalInfoPanel(Item item) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Stock status
        JLabel stockLabel = new JLabel(item.getStock() > 0 ? 
            "In Stock (" + item.getStock() + " available)" : 
            "Out of Stock");
        stockLabel.setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, 16));
        stockLabel.setForeground(item.getStock() > 0 ? UITheme.SUCCESS : UITheme.DANGER);
        
        // Shipping info
        JLabel shippingLabel = new JLabel(item.hasFreeShipping() ? 
            "Free Shipping Available" : 
            "Standard Shipping");
        shippingLabel.setFont(UITheme.getFont(UITheme.FontWeight.MEDIUM, 16));
        shippingLabel.setForeground(item.hasFreeShipping() ? UITheme.SUCCESS : UITheme.GRAY_600);
        
        infoPanel.add(stockLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(shippingLabel);
        
        return infoPanel;
    }
    
    private JPanel createQuantitySelector() {
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        quantityPanel.setOpaque(false);
        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 18));
        quantityLabel.setForeground(UITheme.PRIMARY);
        
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        quantitySpinner.setFont(UITheme.getFont(UITheme.FontWeight.REGULAR, 16));
        quantitySpinner.setPreferredSize(new Dimension(80, 40));
        
        quantityPanel.add(quantityLabel);
        quantityPanel.add(Box.createHorizontalStrut(15));
        quantityPanel.add(quantitySpinner);
        
        return quantityPanel;
    }
    
    private JPanel createModalActionButtons(Item item, JDialog modal) {
        // add horizontal gap so buttons don't sit on top of each other
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add to Cart button
        ElegantButton addToCartBtn = new ElegantButton("Add to Cart", UITheme.ACCENT, UITheme.ACCENT_LIGHT, true);
        addToCartBtn.setPreferredSize(new Dimension(180, 50));
        addToCartBtn.setMaximumSize(new Dimension(220, 60));
        addToCartBtn.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 16));
        addToCartBtn.setEnabled(item.getStock() > 0);
        addToCartBtn.addActionListener(e -> {
            cartManager.addItem(item, 1);
            showAddedToCartNotification(item);
        });
        
        // Buy Now button
        ElegantButton buyNowBtn = new ElegantButton("Buy Now", UITheme.SUCCESS, UITheme.SUCCESS_LIGHT, true);
        buyNowBtn.setPreferredSize(new Dimension(180, 50));
        buyNowBtn.setMaximumSize(new Dimension(220, 60));
        buyNowBtn.setFont(UITheme.getFont(UITheme.FontWeight.BOLD, 16));
        buyNowBtn.setEnabled(item.getStock() > 0);
        buyNowBtn.addActionListener(e -> {
            cartManager.addItem(item, 1);
            modal.dispose();
            performCheckout();
        });
        
        buttonPanel.add(addToCartBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(buyNowBtn);
        
        return buttonPanel;
    }

    private void showAddedToCartNotification(PetShop.Item item) {
        JOptionPane.showMessageDialog(this, 
            "Added " + item.getName() + " to cart!", 
            "Added to Cart", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Helper to load and scale image icons safely. Returns null if image not found.
    private ImageIcon loadImageIcon(String path, int width, int height) {
        try {
            if (path == null || path.isEmpty()) return null;
            ImageIcon raw = new ImageIcon(path);
            Image img = raw.getImage();
            if (img == null) return null;
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            // If loading fails return null so UI can handle missing images gracefully
            return null;
        }
    }
}