import javax.swing.*;
import java.awt.event.*;
import java.rmi.Naming;

public class RegistrationForm extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private RegistrationInterface regService;

    public RegistrationForm(String serverIp) {
        setTitle("User Registration (Client)");
        setSize(420, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(40, 30, 80, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 30, 220, 25);
        add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(40, 70, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(140, 70, 220, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(40, 110, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 110, 220, 25);
        add(passwordField);

        registerButton = new JButton("Register");
        registerButton.setBounds(160, 160, 100, 30);
        add(registerButton);

        try {
            String rmiUrl = "rmi://" + serverIp + "/RegistrationService";
            regService = (RegistrationInterface) Naming.lookup(rmiUrl);
            System.out.println("Connected to " + rmiUrl);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server: " + e.getMessage());
            e.printStackTrace();
        }

        registerButton.addActionListener(e -> registerUser());
    }

    private void registerUser() {
        if (regService == null) {
            JOptionPane.showMessageDialog(this, "Not connected to server.");
            return;
        }
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
                return;
            }
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an email.");
                return;
            }

            String response = regService.registerUser(name, email, password);

            if ("Email already registered.".equalsIgnoreCase(response.trim())) {
                JOptionPane.showMessageDialog(this, response);
                return;
            }

            JOptionPane.showMessageDialog(this, response);

            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");

            try {
                String allUsers = regService.getAllUsers();
                System.out.println("=== All Registered Users from Server ===");
                System.out.println(allUsers);
                System.out.println("========================================");
            } catch (Exception ex) {
                System.err.println("Failed to fetch all users: " + ex.getMessage());
                ex.printStackTrace();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calling server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java RegistrationForm <server-ip>");
            System.exit(1);
        }
        String serverIp = args[0];
        SwingUtilities.invokeLater(() -> {
            new RegistrationForm(serverIp).setVisible(true);
        });
    }
}
