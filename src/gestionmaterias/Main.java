package gestionmaterias;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main extends JFrame {

    // Componentes de la interfaz
    private JTextField nombreField;
    private JComboBox<Integer> nivelComboBox;
    private JButton agregarButton;

    // Datos de conexión
    private final String url = "jdbc:postgresql://localhost:5432/gestion_materias";
    private final String user = "tu_usuario"; // Reemplaza con tu usuario de PostgreSQL
    private final String password = "tu_contraseña"; // Reemplaza con tu contraseña de PostgreSQL

    public Main() {
        super("Gestión de Materias");

        // Configurar la interfaz
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Etiqueta y campo para el nombre
        JLabel nombreLabel = new JLabel("Nombre de la Materia:");
        nombreField = new JTextField(20);

        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(nombreLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(nombreField, gbc);

        // Etiqueta y combo para el nivel de complejidad
        JLabel nivelLabel = new JLabel("Nivel de Complejidad:");
        Integer[] niveles = {1, 2, 3, 4, 5};
        nivelComboBox = new JComboBox<>(niveles);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(nivelLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(nivelComboBox, gbc);

        // Botón para agregar
        agregarButton = new JButton("Agregar Materia");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 10, 10, 10);
        add(agregarButton, gbc);

        // Acción del botón
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarMateria();
            }
        });

        // Configurar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Centrar la ventana
        setVisible(true);
    }

    private void agregarMateria() {
        String nombre = nombreField.getText().trim();
        int nivel = (Integer) nivelComboBox.getSelectedItem();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la materia no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insertar en la base de datos
        String insertSQL = "INSERT INTO materias (nombre, nivel_complejidad) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, nivel);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Materia agregada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                nombreField.setText("");
                nivelComboBox.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar la materia.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Cargar el controlador JDBC
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Controlador JDBC no encontrado. Asegúrate de que el archivo JAR esté en el classpath.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Iniciar la aplicación
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}
