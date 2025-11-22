import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * A complete, runnable Java Swing application to display a
 * transaction history UI with an enhanced aesthetic design.
 *
 * The main class is named 'TransactionHistory' as requested.
 */
public class TransactionHistory {

    // New: expose the transaction UI as a reusable panel for embedding in Dashboard
    public static JPanel createTransactionPanel() {
        // --- NEW Color Palette (Design 3 - Mint/Teal) ---
        Color primaryColor = new Color(0, 150, 136); // Modern Teal
        Color pendingColor = new Color(230, 145, 0);  // Orange-Yellow for pending
        Color backgroundColor = new Color(248, 249, 250); // Very light gray background
        Color tableHeaderBackground = new Color(238, 238, 238); // Light gray
        Color tableEvenRow = Color.WHITE;
        Color tableOddRow = new Color(248, 248, 248); // Very light gray
        Color debitRed = new Color(200, 0, 0); // Darker red
        Color creditGreen = new Color(0, 150, 0); // Darker green
        Color borderColor = new Color(220, 220, 220); // Border color

        // --- 1. Main Content Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20)); // Increased gaps
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // More padding

        // --- 2. Title Label ---
        JLabel titleLabel = new JLabel("Your Recent Transactions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Modern font, larger
        titleLabel.setForeground(primaryColor); // Use new primary color
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- 3. Table Data ---
        // Column names updated as requested
        String[] columnNames = {"DATE", "DESCRIPTION", "AMOUNT", "PAYMENT", "STATUS", "REFERENCE NUMBER"};
        
        // Data updated to match new columns
        Object[][] data = {
                {"2025-11-15", "Grocery Store - Whole Foods", -120.50, "Maya", "Completed", "1123456789012"},
                {"2025-11-15", "Coffee Shop - Starbucks", -5.75, "GCash", "Pending", "1123456789013"},
                {"2025-11-14", "Electric Bill Payment", -65.20, "GCash", "Completed", "1123456789014"},
                {"2025-11-12", "Restaurant - Dinner with Friends", -45.00, "Maya", "Completed", "1123456789015"}, 
                {"2025-11-11", "Gas Station - Shell", -55.10, "GCash", "Completed", "1123456789016"},
                {"2025-11-10", "Gym Membership Renewal", -30.00, "Maya", "Completed", "1123456789017"},
                {"2025-10-30", "Movie Tickets", -28.00, "GCash", "Completed", "1123456789018"},
                {"2025-10-29", "Internet Bill - ISP Co.", -75.00, "Maya", "Completed", "1123456789019"}
        };

        // Use DefaultTableModel to make cells non-editable and define column types
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) { // Amount column
                    return Double.class;
                }
                return String.class;
            }
        };

        JTable table = new JTable(tableModel);

        // --- Table Styling ---
        table.setFillsViewportHeight(true);
        // Use PLAIN font for data readability, BOLD header provides contrast
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        table.setRowHeight(35); // Taller rows for better spacing

        // Remove grid lines for a cleaner look
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0)); // No space between cells

        // --- Custom Cell Renderer ---
        // This handles striped rows, amount formatting, and cell padding
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private DecimalFormat formatter = new DecimalFormat("¤ #,##0.00"); // Currency format

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Reset foreground for all cells first
                c.setForeground(table.getForeground());

                // Striped rows
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? tableEvenRow : tableOddRow);
                } else {
                    c.setBackground(new Color(200, 225, 220)); // Selection color matches theme
                }

                // Amount column styling
                if (column == 2 && value instanceof Double) {
                    double amount = (Double) value;
                    setText(formatter.format(amount)); // Format as currency
                    if (amount < 0) {
                        c.setForeground(debitRed);
                    } else {
                        c.setForeground(creditGreen);
                    }
                    setHorizontalAlignment(JLabel.RIGHT); // Right-align
                } 
                // --- NEW: Status Column Styling ---
                else if (column == 4) { 
                    String status = (String) value;
                    if ("Pending".equalsIgnoreCase(status)) {
                        c.setForeground(pendingColor);
                    } else if ("Completed".equalsIgnoreCase(status)) {
                        c.setForeground(creditGreen); // Use green for completed
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                }
                // Other columns
                else {
                    setHorizontalAlignment(JLabel.LEFT); // Default for other columns
                    // Specific alignment for center columns
                    // "PAYMENT" (3), "REFERENCE NUMBER" (5)
                    if (column == 3 || column == 5) {
                        setHorizontalAlignment(JLabel.CENTER);
                    }
                }

                // Add padding to cell content
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                return c;
            }
        });

        // --- Table Header Styling ---
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableHeader.setBackground(tableHeaderBackground); // Use new header background
        tableHeader.setForeground(primaryColor); // Use new primary color
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 40)); // Taller header
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor)); // Bottom border

        // --- Disable column reordering (moving) ---
        tableHeader.setReorderingAllowed(false);

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // DATE
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // DESCRIPTION
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // AMOUNT
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // PAYMENT
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // STATUS
        table.getColumnModel().getColumn(5).setPreferredWidth(130); // REFERENCE NUMBER

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        // --- NEW: Add a subtle border to frame the table ---
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
        scrollPane.getViewport().setBackground(backgroundColor); // Match background

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- 4. Control Panel (Buttons) ---
        // All buttons and the control panel have been removed as requested.

        // --- Add main panel to frame and display ---
        return mainPanel;
    }

    public static void main(String[] args) {
        // Run the UI creation on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Transaction History");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1000, 600); // Window size
            frame.setLocationRelativeTo(null); // Center the window
            frame.setMinimumSize(new Dimension(800, 500)); // Minimum size
            frame.add(createTransactionPanel());
            frame.setVisible(true);
        });
    }
}