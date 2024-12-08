import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectJ extends JFrame {
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton signInButton;
    private JButton signUpButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private String username = "You"; // Default username before sign-in
    private Timer refreshTimer;

    private DatabaseManager databaseManager;

    public ConnectJ() {
        // Initialize DatabaseManager (assumes a simple placeholder for this)
        databaseManager = new DatabaseManager();

        // Show the main screen initially
        showMainScreen();
    }

    private void clearContent() {
        getContentPane().removeAll(); // Remove all components from the container
        revalidate(); // Revalidate the layout
        repaint(); // Repaint the frame
    }

    private void showMainScreen() {
        clearContent(); // Clear the previous content

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        JLabel welcomeLabel = new JLabel("Welcome to ConnectJ");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomeLabel.setForeground(new Color(40, 40, 40));

        signInButton = createModernButton("Sign In");
        signInButton.addActionListener(e -> showSignInScreen());

        signUpButton = createModernButton("Sign Up");
        signUpButton.addActionListener(e -> showSignUpScreen());

        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(signInButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(signUpButton);

        add(mainPanel); // Add the main panel to the frame

        setTitle("ConnectJ - Main");
        pack(); // Automatically size the window based on content
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255)); // Bootstrap blue
        button.setFocusPainted(false);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 105, 217)); // Darker blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255)); // Original blue
            }
        });

        return button;
    }


    private JTextField createModernTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(new Color(245, 245, 245)); // Same light gray background
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), // Light border
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding inside the text field
        ));
        textField.setPreferredSize(new Dimension(300, 40)); // Same size as the password field
        textField.setCaretColor(new Color(0, 123, 255)); // Blue caret
        return textField;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(245, 245, 245)); // Same light gray background
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180))); // Light border
        passwordField.setPreferredSize(new Dimension(300, 40)); // Same size as the text field
        return passwordField;
    }

    private void showSignInScreen() {
        clearContent(); // Clear the previous content

        JPanel signInPanel = new JPanel();
        signInPanel.setLayout(new BoxLayout(signInPanel, BoxLayout.Y_AXIS));
        signInPanel.setBackground(new Color(245, 245, 245));
        signInPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = createModernTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = createModernPasswordField(); // Use the custom method for password field

        signInButton = createModernButton("Sign In");
        signInButton.addActionListener(e -> signIn());

        signInPanel.add(usernameLabel);
        signInPanel.add(usernameField);
        signInPanel.add(passwordLabel);
        signInPanel.add(passwordField);
        signInPanel.add(Box.createVerticalStrut(20));
        signInPanel.add(signInButton);

        add(signInPanel); // Add the sign-in panel to the frame

        setTitle("Sign In");
        pack(); // Automatically size the window based on content
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void showSignUpScreen() {
        clearContent(); // Clear the previous content

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

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = createModernPasswordField(); // Use the custom method for password field

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField = createModernPasswordField(); // Use the custom method for password field

        signUpButton = createModernButton("Sign Up");
        signUpButton.addActionListener(e -> signUp());

        signUpPanel.add(firstNameLabel);
        signUpPanel.add(firstNameField);
        signUpPanel.add(lastNameLabel);
        signUpPanel.add(lastNameField);
        signUpPanel.add(usernameLabel);
        signUpPanel.add(usernameField);
        signUpPanel.add(passwordLabel);
        signUpPanel.add(passwordField);
        signUpPanel.add(confirmPasswordLabel);
        signUpPanel.add(confirmPasswordField);
        signUpPanel.add(Box.createVerticalStrut(20));
        signUpPanel.add(signUpButton);

        add(signUpPanel); // Add the sign-up panel to the frame

        setTitle("Sign Up");
        pack(); // Automatically size the window based on content
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void signIn() {
        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());

        if (databaseManager.verifyPassword(enteredUsername, enteredPassword)) {
            username = enteredUsername;
            JOptionPane.showMessageDialog(this, "Welcome, " + username + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showMainAppWindow(); // Proceed to the main application window
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void signUp() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!enteredPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (databaseManager.isUsernameTaken(enteredUsername)) {
            JOptionPane.showMessageDialog(this, "Username is already taken!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            boolean success = databaseManager.addUser(enteredUsername, enteredPassword, firstName, lastName);
            if (success) {
                JOptionPane.showMessageDialog(this, "Sign-up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                signIn();
            } else {
                JOptionPane.showMessageDialog(this, "Error during sign-up!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showMainAppWindow() {
        clearContent(); // Clear the login/signup screen

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

        sendButton = createModernButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessageToServer();
            }
        });

        chatPanel.add(chatLabel);
        chatPanel.add(scrollPane);
        chatPanel.add(inputField);
        chatPanel.add(sendButton);

        add(chatPanel); // Add the chat panel to the frame

        setTitle("ConnectJ - Chat");
        pack(); // Automatically size the window based on content
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void sendMessageToServer() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            // Send message via WebSocket
            WebSocketClient client = new WebSocketClient();
            client.sendMessage(message);

            // Display message in the message area (local echo)
            messageArea.append(username + ": " + message + "\n");
            inputField.setText(""); // Clear the input field after sending
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            return; // Avoid sending empty messages
        }

        boolean messageSaved = databaseManager.saveMessage(username, message);
        if (messageSaved) {
            messageArea.append(username + ": " + message + "\n");
            inputField.setText(""); // Clear the input after sending
        }
    }

    private void startMessageRefresh() {
        refreshTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshMessages();
            }
        });
        refreshTimer.start();
    }

    private String lastMessageTimestamp = ""; // Keep track of last fetched timestamp

    private void refreshMessages() {
        // Fetch only messages after the last timestamp
        String query = "SELECT username, message FROM messages WHERE timestamp > ? ORDER BY timestamp DESC LIMIT 10";

        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(query)) {
            stmt.setString(1, lastMessageTimestamp); // Use last fetched timestamp as filter
            ResultSet rs = stmt.executeQuery();

            StringBuilder newMessages = new StringBuilder();
            while (rs.next()) {
                String username = rs.getString("username");
                String message = rs.getString("message");
                String timestamp = rs.getString("timestamp");

                // Append new messages
                newMessages.append(username).append(": ").append(message).append("\n");
                lastMessageTimestamp = timestamp; // Update the last fetched timestamp
            }

            if (newMessages.length() > 0) {
                messageArea.append(newMessages.toString());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConnectJ());
    }
}