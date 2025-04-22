package ServiDiesel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WriteException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class InventarioApp extends JFrame {
    private JTextField idField, nombreField, cantidadField, precioField, searchField;
    private JButton agregarButton, modificarButton, eliminarButton, vendidoButton, exportarButton, modificarUsuarioButton;
    private JTable productosTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private Connection connection;
    private JRadioButton quitarFondoButton;
    private Font font;
    private JTextField newUsernameField, newPasswordField;
JTable table = new JTable(tableModel);

    public InventarioApp() {
        // Configuración de la ventana principal
        setTitle("Sistema de Inventario - ServiDiesel");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana

        // Crear el panel de fondo con la imagen
        BackgroundPanel mainPanel = new BackgroundPanel("img/");
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear el font
        font = new Font("Arial", Font.PLAIN, 16);

        // Logo y título
        JLabel logoLabel = new JLabel(new ImageIcon("img/logo_1.jpg"));
        JLabel titleLabel = new JLabel("ServiDiesel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Crear el panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Buscar:"));
        searchField = new JTextField(20);
        searchField.setFont(font);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        searchPanel.add(searchField);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Crear el panel de formulario
        JPanel formularioPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formularioPanel.setOpaque(false);
        formularioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Datos del Producto", 0, 0, null, Color.WHITE));

        formularioPanel.add(createLabel("ID del Producto:"));
        idField = new JTextField();
        idField.setFont(font);
        idField.setOpaque(false);
        idField.setForeground(Color.BLACK);
        formularioPanel.add(idField);

        formularioPanel.add(createLabel("Nombre del Producto:"));
        nombreField = new JTextField();
        nombreField.setFont(font);
        nombreField.setOpaque(false);
        nombreField.setForeground(Color.BLACK);
        formularioPanel.add(nombreField);

        formularioPanel.add(createLabel("Cantidad:"));
        cantidadField = new JTextField();
        cantidadField.setFont(font);
        cantidadField.setOpaque(false);
        cantidadField.setForeground(Color.BLACK);
        formularioPanel.add(cantidadField);

        formularioPanel.add(createLabel("Precio:"));
        precioField = new JTextField();
        precioField.setFont(font);
        precioField.setOpaque(false);
        precioField.setForeground(Color.BLACK);
        formularioPanel.add(precioField);

        mainPanel.add(formularioPanel, BorderLayout.WEST);

        // Crear el panel de botones en la parte inferior
        JPanel botonesPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        botonesPanel.setOpaque(false);
        botonesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Acciones", 0, 0, null, Color.WHITE));

        agregarButton = new JButton("Agregar");
        agregarButton.setFont(font);
        agregarButton.addActionListener(e -> agregarProducto());
        botonesPanel.add(agregarButton);

        modificarButton = new JButton("Modificar");
        modificarButton.setFont(font);
        modificarButton.addActionListener(e -> modificarProducto());
        botonesPanel.add(modificarButton);

        eliminarButton = new JButton("Eliminar");
        eliminarButton.setFont(font);
        eliminarButton.addActionListener(e -> eliminarProducto());
        botonesPanel.add(eliminarButton);

        vendidoButton = new JButton("Vendido");
        vendidoButton.setFont(font);
        vendidoButton.addActionListener(e -> venderProducto());
        botonesPanel.add(vendidoButton);

        exportarButton = new JButton("Exportar");
        exportarButton.setFont(font);
        exportarButton.addActionListener(e -> exportarTablaAExcel());
        botonesPanel.add(exportarButton);

        modificarUsuarioButton = new JButton("Modificar Usuario");
        modificarUsuarioButton.setFont(font);
        modificarUsuarioButton.addActionListener(e -> cambiarUsuario());
        botonesPanel.add(modificarUsuarioButton);

        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        // Crear el radio button para quitar el fondo
        quitarFondoButton = new JRadioButton("Quitar Fondo");
        quitarFondoButton.setFont(font);
        quitarFondoButton.setOpaque(false);
        quitarFondoButton.setForeground(Color.BLACK);
        quitarFondoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (quitarFondoButton.isSelected()) {
                    mainPanel.setBackground(null);
                } else {
                    mainPanel.repaint();
                }
            }
        });

       

        // Crear la tabla para mostrar los productos
        tableModel = new DefaultTableModel(new String[]{"ID", "Producto", "Cantidad", "Precio"}, 0);
        productosTable = new JTable(tableModel);
        productosTable.setFont(font);
        productosTable.setRowHeight(30); // Aumentar la altura de las filas
        rowSorter = new TableRowSorter<>(tableModel);
        productosTable.setRowSorter(rowSorter);
        productosTable.setFillsViewportHeight(true);
        productosTable.setForeground(Color.BLACK); // Letra negra
        productosTable.setOpaque(false);
        ((DefaultTableCellRenderer) productosTable.getDefaultRenderer(Object.class)).setOpaque(false);

        // Aplicar el renderer personalizado para la columna de precios
        productosTable.getColumnModel().getColumn(3).setCellRenderer(new PriceCellRenderer());

        JScrollPane scrollPane = new JScrollPane(productosTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Agregar el panel principal a la ventana
        add(mainPanel);

        // Conexión a la base de datos
        conectarBaseDatos();

        // Cargar productos en la tabla
        cargarProductos();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.BLACK);
        return label;
    }

    private void conectarBaseDatos() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventariodb", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos");
        }
    }

    private void cargarProductos() {
        try {
            String query = "SELECT * FROM productos";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            tableModel.setRowCount(0);  // Limpiar tabla
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre_producto");
                int cantidad = resultSet.getInt("cantidad");
                double precio = resultSet.getDouble("precio");
                // Mostrar "No hay en stock" si la cantidad es 0
                String cantidadMostrar = (cantidad > 0) ? String.valueOf(cantidad) : "No hay en stock";
                tableModel.addRow(new Object[]{id, nombre, cantidadMostrar, precio});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarProducto() {
        String idTexto = idField.getText();
        String nombre = nombreField.getText();
        int cantidad = Integer.parseInt(cantidadField.getText());
        double precio = Double.parseDouble(precioField.getText());

        try {
            String query = "INSERT INTO productos (id, nombre_producto, cantidad, precio) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(idTexto));
            preparedStatement.setString(2, nombre);
            preparedStatement.setInt(3, cantidad);
            preparedStatement.setDouble(4, precio);

            preparedStatement.executeUpdate();
            cargarProductos();
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al agregar el producto");
        }
    }

public void modificarProducto() {
    // Verifica si se ha seleccionado una fila
    int selectedRow = productosTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para modificar.");
        return;
    }

    // Obtener los valores de la fila seleccionada
    String id = productosTable.getValueAt(selectedRow, 0).toString();
    String nombre = productosTable.getValueAt(selectedRow, 1).toString();
    String cantidad = productosTable.getValueAt(selectedRow, 2).toString();
    String precio = productosTable.getValueAt(selectedRow, 3).toString();

    // Validar los datos antes de la actualización
    if (nombre.isEmpty() || cantidad.isEmpty() || precio.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Todos los campos deben estar llenos.");
        return;
    }

    try {
        // Conectar a la base de datos
        String url = "jdbc:mysql://localhost:3306/inventariodb";
        String user = "root";
        String password = "";
        Connection connection = DriverManager.getConnection(url, user, password);

        // Convertir los valores de cantidad y precio a sus tipos correspondientes
        int cantidadInt = Integer.parseInt(cantidad);
        double precioDouble = Double.parseDouble(precio);

        // Actualizar el producto en la base de datos
        String sql = "UPDATE productos SET nombre_producto = ?, cantidad = ?, precio = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, nombre);
        statement.setInt(2, cantidadInt);
        statement.setDouble(3, precioDouble);
        statement.setInt(4, Integer.parseInt(id));

        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Producto modificado correctamente.");

            // Actualizar los valores en la tabla después de la modificación
            productosTable.setValueAt(nombre, selectedRow, 1);
            productosTable.setValueAt(cantidad, selectedRow, 2);
            productosTable.setValueAt(precio, selectedRow, 3);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar el producto.");
        }

        // Cerrar la conexión
        statement.close();
        connection.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al modificar el producto: " + e.getMessage());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error en el formato de los números: " + e.getMessage());
    }
}



private void eliminarProducto() {
    int selectedRow = productosTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para eliminar.");
        return;
    }

    String idTexto = productosTable.getValueAt(selectedRow, 0).toString();

    try {
        String query = "DELETE FROM productos WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, Integer.parseInt(idTexto));

        int result = preparedStatement.executeUpdate();
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
            cargarProductos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al eliminar el producto.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al eliminar el producto.");
    }
}

  private void venderProducto() {
    int selectedRow = productosTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un producto para vender");
        return;
    }

    String idTexto = productosTable.getValueAt(selectedRow, 0).toString();

    try {
        // Consultar la cantidad del producto antes de la venta
        String selectQuery = "SELECT cantidad FROM productos WHERE id = ?";
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        selectStatement.setInt(1, Integer.parseInt(idTexto));
        ResultSet resultSet = selectStatement.executeQuery();
        
        if (resultSet.next()) {
            int cantidadActual = resultSet.getInt("cantidad");
            if (cantidadActual <= 0) {
                JOptionPane.showMessageDialog(this, "No hay suficiente stock para vender el producto");
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Producto no encontrado");
            return;
        }

        // Actualizar la cantidad del producto
        String updateQuery = "UPDATE productos SET cantidad = cantidad - 1 WHERE id = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setInt(1, Integer.parseInt(idTexto));
        
        int filasActualizadas = updateStatement.executeUpdate();
        if (filasActualizadas > 0) {
            JOptionPane.showMessageDialog(this, "Producto vendido con éxito");
            cargarProductos();
            productosTable.setRowSelectionInterval(selectedRow, selectedRow); // Mantener la fila seleccionada
        } else {
            JOptionPane.showMessageDialog(this, "Error al vender el producto");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al vender el producto");
    }
}


    private void limpiarCampos() {
        idField.setText("");
        nombreField.setText("");
        cantidadField.setText("");
        precioField.setText("");
    }

private void exportarTablaAExcel() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Guardar como");
    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        
        // Asegúrate de que el archivo tenga la extensión .xls
        if (!fileToSave.getName().endsWith(".xls")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xls");
        }

        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(fileToSave);
            WritableSheet sheet = workbook.createSheet("Inventario", 0);

            // Escribir los encabezados
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                sheet.addCell(new Label(i, 0, tableModel.getColumnName(i)));
            }

            // Escribir los datos de la tabla
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object value = tableModel.getValueAt(i, j);
                    if (value instanceof Number) {
                        sheet.addCell(new Number(j, i + 1, ((Number) value).getValue()));
                    } else {
                        sheet.addCell(new Label(j, i + 1, value.toString()));
                    }
                }
            }

            workbook.write();
            JOptionPane.showMessageDialog(this, "Exportación a Excel completada");
        } catch (IOException | WriteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al exportar a Excel");
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


    private void cambiarUsuario() {
        // Crear un nuevo JFrame para ingresar los datos del nuevo usuario
        JFrame cambiarUsuarioFrame = new JFrame("Cambiar Usuario");
        cambiarUsuarioFrame.setSize(300, 200);
        cambiarUsuarioFrame.setLayout(new GridLayout(3, 2, 10, 10));

        cambiarUsuarioFrame.add(createLabel("Nuevo Usuario:"));
        newUsernameField = new JTextField();
        cambiarUsuarioFrame.add(newUsernameField);

        cambiarUsuarioFrame.add(createLabel("Nueva Contraseña:"));
        newPasswordField = new JTextField();
        cambiarUsuarioFrame.add(newPasswordField);

        JButton guardarButton = new JButton("Guardar");
        guardarButton.addActionListener(e -> guardarNuevoUsuario());
        cambiarUsuarioFrame.add(guardarButton);

        cambiarUsuarioFrame.setLocationRelativeTo(this);
        cambiarUsuarioFrame.setVisible(true);
    }

    private void guardarNuevoUsuario() {
        String nuevoUsuario = newUsernameField.getText();
        String nuevaContraseña = newPasswordField.getText();

        try {
            String query = "INSERT INTO usuarios (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nuevoUsuario);
            preparedStatement.setString(2, nuevaContraseña);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario cambiado exitosamente");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cambiar el usuario");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InventarioApp app = new InventarioApp();
            app.setVisible(true);
        });
    }
}

// Clase BackgroundPanel para manejar la imagen de fondo
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// Renderer personalizado para la columna de precios
class PriceCellRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value instanceof Double) {
            setText(String.format("$%.2f", (Double) value));
        } else {
            super.setValue(value);
        }
    }
}
