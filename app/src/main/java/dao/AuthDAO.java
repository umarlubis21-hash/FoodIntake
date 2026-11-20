package dao;

import koneksi.Koneksi;
import model.Admin;
import model.Member;
import model.Pengguna;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthDAO {

    public boolean emailTerdaftar(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean daftar(String nama, String email, String password) throws SQLException {
        String sqlUser = "INSERT INTO users (nama, email, password, peran) VALUES (?, ?, ?, 'MEMBER')";
        String sqlProfil = "INSERT INTO profil (user_id, target_kalori) VALUES (?, 0)";

        try (Connection c = Koneksi.getConnection()) {
            c.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement ps = c.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nama);
                    ps.setString(2, email);
                    ps.setString(3, PasswordUtil.hash(password));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        userId = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(sqlProfil)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                if ("23000".equals(e.getSQLState())) {
                    return false;
                }
                throw e;
            }
        }
    }


    public Pengguna login(String email, String password) throws SQLException {
        String sql = "SELECT id, nama, password, peran FROM users WHERE email = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                if (!PasswordUtil.verifikasi(password, rs.getString("password"))) {
                    return null;
                }
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                return "ADMIN".equalsIgnoreCase(rs.getString("peran"))
                        ? new Admin(id, nama)
                        : new Member(id, nama);
            }
        }
    }
}