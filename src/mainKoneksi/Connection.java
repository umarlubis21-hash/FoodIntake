package mainKoneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection{
    private static final String URL = "jdbc:mariadb://localhost:3306/food_intake";
    private static final String USER = "Sols2";
    private static final String PASS = "M201ns";
    
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

    public static void main(String[] args) {
        try (mainKoneksi.Connection c = getConnection()) {
            System.out.println("Koneksi berhasil ke database: " + c.getCatalog());
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
    }
}
