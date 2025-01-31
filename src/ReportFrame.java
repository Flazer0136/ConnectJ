import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.*;
import javax.swing.*;

public class ReportFrame extends JFrame {
    private JTextArea reportArea;
    private DatabaseManager databaseManager;

    public ReportFrame(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        setTitle("Report");
        setSize(600, 400);
        setLayout(new BorderLayout());

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        loadReportData();

        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton gridButton = createModernButton("Interactive Grid");
        gridButton.addActionListener(e -> showInteractiveGrid("users"));

        JButton closeButton = createModernButton("Close");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(gridButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadReportData() {
        String query = "SELECT * FROM messages";

        String query2 = "select count(message) from messages where sender = 7 and recipient = 5;";
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query2)) {

            while (rs.next()) {
                reportArea.append("Message ID: " + rs.getInt("message_id") + "\n");
                reportArea.append("Sender: " + rs.getInt("sender") + "\n");
                reportArea.append("Recipient: " + rs.getInt("recipient") + "\n");
                reportArea.append("Message: " + rs.getString("message") + "\n");
                reportArea.append("Sent At: " + rs.getTimestamp("sent_at") + "\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void showInteractiveReport(String tableName) {
        JDialog dialog = new JDialog(this, tableName + " Report", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);

        String query = "SELECT * FROM " + tableName;
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    reportArea.append(metaData.getColumnName(i) + ": " + rs.getString(i) + "\n");
                }
                reportArea.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dialog.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showInteractiveGrid(String tableName) {
        // Implementation for showing the interactive grid
        JDialog dialog = new JDialog(this, tableName + " Table", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel();

        // Add columns based on the table name
        if (tableName.equals("users")) {
            model.addColumn("User ID");
            model.addColumn("Username");
            model.addColumn("First Name");
            model.addColumn("Last Name");
        } else if (tableName.equals("messages")) {
            model.addColumn("Message ID");
            model.addColumn("Sender");
            model.addColumn("Recipient");
            model.addColumn("Message");
            model.addColumn("Sent At");
        } else if (tableName.equals("staff")) {
            model.addColumn("Staff ID");
            model.addColumn("Username");
            model.addColumn("Role");
        }

        String query = "SELECT * FROM " + tableName;
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                if (tableName.equals("users")) {
                    model.addRow(new Object[]{
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    });
                } else if (tableName.equals("messages")) {
                    model.addRow(new Object[]{
                            rs.getInt("message_id"),
                            rs.getInt("sender"),
                            rs.getInt("recipient"),
                            rs.getString("message"),
                            rs.getTimestamp("sent_at")
                    });
                } else if (tableName.equals("staff")) {
                    model.addRow(new Object[]{
                            rs.getInt("staff_id"),
                            rs.getString("username"),
                            rs.getString("role")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setModel(model);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    private void createNavigationMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Navigation");
        JMenuItem usersGridItem = new JMenuItem("Users Grid");
        usersGridItem.addActionListener(e -> showInteractiveGrid("users"));
        menu.add(usersGridItem);

        JMenuItem messagesGridItem = new JMenuItem("Messages Grid");
        messagesGridItem.addActionListener(e -> showInteractiveGrid("messages"));
        menu.add(messagesGridItem);

        JMenuItem staffGridItem = new JMenuItem("Staff Grid");
        staffGridItem.addActionListener(e -> showInteractiveGrid("staff"));
        menu.add(staffGridItem);

        JMenuItem usersReportItem = new JMenuItem("Users Report");
        usersReportItem.addActionListener(e -> showInteractiveReport("users"));
        menu.add(usersReportItem);

        JMenuItem messagesReportItem = new JMenuItem("Messages Report");
        messagesReportItem.addActionListener(e -> showInteractiveReport("messages"));
        menu.add(messagesReportItem);

        JMenuItem staffReportItem = new JMenuItem("Staff Report");
        staffReportItem.addActionListener(e -> showInteractiveReport("staff"));
        menu.add(staffReportItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private boolean isAuthorized(String username, String action) {
        String query = "SELECT role FROM staff WHERE username = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                // Implement your authorization logic based on role and action
                return role.equals("admin") || (role.equals("user") && action.equals("view"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setFocusPainted(false);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 105, 217));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager databaseManager = new DatabaseManager(); // Initialize your DatabaseManager
            ReportFrame reportFrame = new ReportFrame(databaseManager);
            reportFrame.createNavigationMenu();
        });
    }
}