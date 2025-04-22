package ServiDiesel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginApp extends JFrame {
    private JComboBox<String> usuarioComboBox;
    private JPasswordField passwordField;

    public LoginApp() {
        // Configuración de la ventana de inicio de sesión
        setTitle("Inicio de Sesión");
        setSize(400, 250); // Aumentado para incluir el comboBox
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana
        setResizable(false); // Evitar redimensionamiento

        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Logo en la esquina superior derecha
        JLabel logoLabel = new JLabel(new ImageIcon("src/img/logo_1.png"));
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoPanel.add(logoLabel);
        mainPanel.add(logoPanel, BorderLayout.NORTH);

        // Panel de campos de usuario y contraseña
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ingrese sus datos"));

        formPanel.add(new JLabel("Usuario:"));
        usuarioComboBox = new JComboBox<>(obtenerUsuarios());
        formPanel.add(usuarioComboBox);

        formPanel.add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Botón de inicio de sesión
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(e -> {
            String usuario = (String) usuarioComboBox.getSelectedItem();
            String password = new String(passwordField.getPassword());

            if (autenticarUsuario(usuario, password)) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    InventarioApp app = new InventarioApp();
                    app.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
            }
        });

        // Botón de recuperación de contraseña
        JButton recoverButton = new JButton("Recuperar Contraseña");
        recoverButton.addActionListener(e -> mostrarDialogoRecuperacion());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(recoverButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private String[] obtenerUsuarios() {
        List<String> usuarios = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventariodb", "root", "")) {
            String query = "SELECT username FROM usuarios";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                usuarios.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios.toArray(new String[0]);
    }

    private boolean autenticarUsuario(String usuario, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventariodb", "root", "")) {
            String query = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, usuario);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   private void mostrarDialogoRecuperacion() {
    // Crear un diálogo para recuperar la contraseña
    JDialog dialog = new JDialog(this, "Recuperar Contraseña", true);
    dialog.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;

    // Título
    JLabel titleLabel = new JLabel("Recuperación de Contraseña");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    dialog.add(titleLabel, gbc);

    // Clave especial
    gbc.gridy++;
    gbc.gridwidth = 1;
    JLabel keyLabel = new JLabel("Ingrese la clave especial:");
    dialog.add(keyLabel, gbc);

    JTextField keyField = new JTextField(20);
    gbc.gridx = 1;
    dialog.add(keyField, gbc);

    // Usuario
    gbc.gridy++;
    gbc.gridx = 0;
    JLabel userLabel = new JLabel("Usuario:");
    dialog.add(userLabel, gbc);

    JComboBox<String> userComboBox = new JComboBox<>(obtenerUsuarios());
    gbc.gridx = 1;
    dialog.add(userComboBox, gbc);

    // Resultado
    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    JLabel resultLabel = new JLabel("");
    resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
    dialog.add(resultLabel, gbc);

    // Botón de enviar
    gbc.gridy++;
    gbc.gridwidth = 1;
    JButton submitButton = new JButton("Enviar");
    submitButton.addActionListener(e -> {
        String key = keyField.getText();
        if ("0000".equals(key)) {
            String usuario = (String) userComboBox.getSelectedItem();
            String password = obtenerContraseña(usuario);
            resultLabel.setText("Contraseña: " + password);
        } else {
            resultLabel.setText("Clave incorrecta.");
        }
    });
    dialog.add(submitButton, gbc);

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}

    private String obtenerContraseña(String usuario) {
        // Lógica para obtener la contraseña de la base de datos
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventariodb", "root", "")) {
            String query = "SELECT password FROM usuarios WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, usuario);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("password");
            } else {
                return "No se encontró usuario.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al recuperar contraseña.";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginApp login = new LoginApp();
            login.setVisible(true);
        });
    }
}
