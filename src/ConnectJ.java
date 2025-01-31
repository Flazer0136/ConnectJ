import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class ConnectJ extends JFrame {
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton signInButton;
    private JButton signUpButton;
    private JButton backButton;
    private JComboBox<String> userComboBox;
    private JComboBox<String> groupComboBox;
    private JComboBox<String> userGroupComboBox; // Declare userGroupComboBox here
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private String username = "You"; // Default username before sign-in
    private Timer refreshTimer;
    private Stack<JPanel> screenStack = new Stack<>();

    private DatabaseManager databaseManager;

    public ConnectJ() {
        databaseManager = new DatabaseManager();
        showMainScreen();
    }

    private void clearContent() {
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    private void showMainScreen() {
        clearContent();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome to ConnectJ");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomeLabel.setForeground(new Color(40, 40, 40));

        signInButton = createModernButton("Sign In");
        signInButton.addActionListener(e -> {
            screenStack.push(mainPanel);
            showSignInScreen();
        });

        signUpButton = createModernButton("Sign Up");
        signUpButton.addActionListener(e -> {
            screenStack.push(mainPanel);
            showSignUpScreen();
        });

        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(signInButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(signUpButton);

        add(mainPanel);

        setTitle("ConnectJ - Main");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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

    private JTextField createModernTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(new Color(245, 245, 245));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setCaretColor(new Color(0, 123, 255));
        return textField;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(245, 245, 245));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        passwordField.setPreferredSize(new Dimension(300, 40));
        return passwordField;
    }

    private void showSignInScreen() {
        clearContent();

        JPanel signInPanel = new JPanel();
        signInPanel.setLayout(new BoxLayout(signInPanel, BoxLayout.Y_AXIS));
        signInPanel.setBackground(new Color(245, 245, 245));
        signInPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = createModernTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = createModernPasswordField();

        signInButton = createModernButton("Sign In");
        signInButton.addActionListener(e -> signIn());

        backButton = createModernButton("Back");
        backButton.addActionListener(e -> showPreviousScreen());

        signInPanel.add(usernameLabel);
        signInPanel.add(usernameField);
        signInPanel.add(passwordLabel);
        signInPanel.add(passwordField);
        signInPanel.add(Box.createVerticalStrut(20));
        signInPanel.add(signInButton);
        signInPanel.add(backButton);

        add(signInPanel);

        setTitle("Sign In");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void showSignUpScreen() {
        clearContent();

        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.Y_AXIS));
        signUpPanel.setBackground(new Color(245, 245, 245));
        signUpPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        firstNameField = createModernTextField();

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        lastNameField = createModernTextField();

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = createModernTextField();

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField = createModernTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = createModernPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField = createModernPasswordField();

        signUpButton = createModernButton("Sign Up");
        signUpButton.addActionListener(e -> signUp());

        backButton = createModernButton("Back");
        backButton.addActionListener(e -> showPreviousScreen());

        signUpPanel.add(firstNameLabel);
        signUpPanel.add(firstNameField);
        signUpPanel.add(lastNameLabel);
        signUpPanel.add(lastNameField);
        signUpPanel.add(usernameLabel);
        signUpPanel.add(usernameField);
        signUpPanel.add(emailLabel);
        signUpPanel.add(emailField);
        signUpPanel.add(passwordLabel);
        signUpPanel.add(passwordField);
        signUpPanel.add(confirmPasswordLabel);
        signUpPanel.add(confirmPasswordField);
        signUpPanel.add(Box.createVerticalStrut(20));
        signUpPanel.add(signUpButton);
        signUpPanel.add(backButton);

        add(signUpPanel);

        setTitle("Sign Up");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void showPreviousScreen() {
        if (!screenStack.isEmpty()) {
            JPanel previousScreen = screenStack.pop();
            clearContent();
            add(previousScreen);
            revalidate();
            repaint();
        }
    }

    private void signIn() {
        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());

        if (databaseManager.verifyPassword(enteredUsername, enteredPassword)) {
            username = enteredUsername;
            JOptionPane.showMessageDialog(this, "Welcome, " + username + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showMainAppWindow();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void signUp() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String enteredUsername = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!enteredPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!databaseManager.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (databaseManager.isUsernameTaken(enteredUsername)) {
            JOptionPane.showMessageDialog(this, "Username is already taken!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                boolean success = databaseManager.addUser(enteredUsername, email, enteredPassword, firstName, lastName);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Sign-up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    signIn();
                } else {
                    JOptionPane.showMessageDialog(this, "Error during sign-up!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showMainAppWindow() {
        clearContent();

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 245, 245));
        chatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel chatLabel = new JLabel("Chat - " + username);
        chatLabel.setFont(new Font("Arial", Font.BOLD, 18));
        chatLabel.setAlignmentX(CENTER_ALIGNMENT);
        chatLabel.setForeground(new Color(40, 40, 40));

        messageArea = new JTextArea(20, 40);
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(240, 240, 240));
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(messageArea);

        inputField = new JTextField(40);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setPreferredSize(new Dimension(300, 40));
        inputField.addActionListener(e -> sendMessageToServer());

        sendButton = createModernButton("Send");
        sendButton.addActionListener(e -> sendMessageToServer());

        backButton = createModernButton("Back");
        backButton.addActionListener(e -> showPreviousScreen());

        userGroupComboBox = new JComboBox<>();
        loadUsersAndGroups();

        groupComboBox = new JComboBox<>(); // Initialize groupComboBox here
        loadGroups();

        JButton createGroupButton = createModernButton("Create Group");
        createGroupButton.addActionListener(e -> showCreateGroupDialog());

        JButton addUserToGroupButton = createModernButton("Add User to Group");
        addUserToGroupButton.addActionListener(e -> showAddUserToGroupDialog());

        chatPanel.add(chatLabel);
        chatPanel.add(userGroupComboBox);
        chatPanel.add(scrollPane);
        chatPanel.add(inputField);
        chatPanel.add(sendButton);
        chatPanel.add(createGroupButton);
        chatPanel.add(addUserToGroupButton);
        chatPanel.add(backButton);

        add(chatPanel);

        setTitle("ConnectJ - Chat");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        startMessageRefresh();
        refreshMessages(); // Call refreshMessages to load messages when the chat window is shown
    }

    private void loadUsersAndGroups() {
        try {
            userGroupComboBox.removeAllItems();
            ResultSet rs = databaseManager.getConnection().createStatement().executeQuery("SELECT username FROM users");
            while (rs.next()) {
                userGroupComboBox.addItem("User: " + rs.getString("username"));
            }
            rs = databaseManager.getConnection().createStatement().executeQuery("SELECT group_name FROM groups");
            while (rs.next()) {
                userGroupComboBox.addItem("Group: " + rs.getString("group_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToServer() {
        String message = inputField.getText().trim();
        String selectedItem = (String) userGroupComboBox.getSelectedItem();
        if (!message.isEmpty() && selectedItem != null) {
            if (selectedItem.startsWith("User: ")) {
                String recipientUsername = selectedItem.substring(6);
                int senderId = getUserIdByUsername(username);
                int recipientId = getUserIdByUsername(recipientUsername);
                if (senderId != -1 && recipientId != -1) {
                    boolean messageSaved = databaseManager.saveMessage(senderId, recipientId, message, senderId);
                    if (messageSaved) {
                        messageArea.append(username + " to " + recipientUsername + ": " + message + "\n");
                        inputField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error saving message. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (selectedItem.startsWith("Group: ")) {
                String groupName = selectedItem.substring(7);
                int groupId = databaseManager.getGroupIdByName(groupName);
                int userId = getUserIdByUsername(username);
                if (groupId != -1 && userId != -1 && databaseManager.isUserInGroup(groupId, userId)) {
                    boolean messageSaved = databaseManager.saveGroupMessage(groupId, userId, message);
                    if (messageSaved) {
                        messageArea.append(username + " to " + groupName + ": " + message + "\n");
                        inputField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error saving message. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "You are not a member of this group.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private int getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user not found or an error occurs
    }

    private String getUsernameById(int userId) {
        String query = "SELECT username FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found or an error occurs
    }

    private void startMessageRefresh() {
        refreshTimer = new Timer(1000, e -> refreshMessages());
        refreshTimer.start();
    }

    private void refreshMessages() {
        String selectedItem = (String) userGroupComboBox.getSelectedItem();
        if (selectedItem == null) return;

        if (selectedItem.startsWith("User: ")) {
            String recipientUsername = selectedItem.substring(6);
            int senderId = getUserIdByUsername(username);
            int recipientId = getUserIdByUsername(recipientUsername);
            if (senderId == -1 || recipientId == -1) return;

            String query = "SELECT sender, message FROM messages WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?) ORDER BY sent_at";
            StringBuilder newMessages = new StringBuilder();

            try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(query)) {
                stmt.setInt(1, senderId);
                stmt.setInt(2, recipientId);
                stmt.setInt(3, recipientId);
                stmt.setInt(4, senderId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String sender = getUsernameById(rs.getInt("sender"));
                    String message = rs.getString("message");
                    newMessages.append(sender).append(": ").append(message).append("\n");
                }

                messageArea.setText(newMessages.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (selectedItem.startsWith("Group: ")) {
            String groupName = selectedItem.substring(7);
            int groupId = databaseManager.getGroupIdByName(groupName);
            if (groupId == -1) return;

            List<String> messages = databaseManager.getGroupMessages(groupId);
            messageArea.setText(String.join("\n", messages));
        }
    }

    private int getSelectedGroupId() {
        // Implement logic to get the selected group ID from your UI
        // For example, if you have a JComboBox for groups:
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        if (selectedGroup == null) return -1;
        return databaseManager.getGroupIdByName(selectedGroup); // Implement getGroupIdByName in DatabaseManager
    }

    private void sendGroupMessageToServer() {
        String message = inputField.getText().trim();
        int groupId = getSelectedGroupId(); // Implement this method to get the selected group ID
        if (!message.isEmpty() && groupId != -1) {
            int userId = getUserIdByUsername(username);
            if (userId != -1) {
                boolean messageSaved = databaseManager.saveGroupMessage(groupId, userId, message);
                if (messageSaved) {
                    messageArea.append(username + ": " + message + "\n");
                    inputField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error saving message. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showCreateGroupDialog() {
        String groupName = JOptionPane.showInputDialog(this, "Enter group name:", "Create Group", JOptionPane.PLAIN_MESSAGE);
        if (groupName != null && !groupName.trim().isEmpty()) {
            int userId = getUserIdByUsername(username);
            boolean success = databaseManager.createGroup(groupName.trim(), userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Group created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadGroups(); // Refresh the group list
            } else {
                JOptionPane.showMessageDialog(this, "Error creating group. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadGroups() {
        try {
            groupComboBox.removeAllItems();
            String query = "SELECT g.group_name FROM groups g JOIN group_members gm ON g.group_id = gm.group_id WHERE gm.user_id = ?";
            try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(query)) {
                stmt.setInt(1, getUserIdByUsername(username));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    groupComboBox.addItem(rs.getString("group_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddUserToGroupDialog() {
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        if (selectedGroup == null) {
            JOptionPane.showMessageDialog(this, "Please select a group first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = JOptionPane.showInputDialog(this, "Enter username to add to the group:", "Add User to Group", JOptionPane.PLAIN_MESSAGE);
        if (username != null && !username.trim().isEmpty()) {
            int groupId = databaseManager.getGroupIdByName(selectedGroup);
            int userId = getUserIdByUsername(username.trim());
            if (groupId != -1 && userId != -1) {
                boolean success = databaseManager.addUserToGroup(groupId, userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User added to the group successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding user to the group. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid group or user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshGroupMessages() {
        int groupId = getSelectedGroupId(); // Implement this method to get the selected group ID
        if (groupId == -1) return;

        List<String> messages = databaseManager.getGroupMessages(groupId);
        messageArea.setText(String.join("\n", messages));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConnectJ());
    }
}