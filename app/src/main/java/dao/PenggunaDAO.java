package dao;

import koneksi.Koneksi;
import model.Admin;
import model.Member;
import model.Pengguna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PenggunaDAO {

    public Pengguna cariById(int id) throws SQLException {
        String sql = "SELECT nama, peran FROM users WHERE id = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String nama = rs.getString("nama");
                return "ADMIN".equalsIgnoreCase(rs.getString("peran"))
                        ? new Admin(id, nama)
                        : new Member(id, nama);
            }
        }
    }
    
    public int targetKalori(int userId) throws SQLException {
        String sql = "SELECT target_kalori FROM profil WHERE user_id = ?";
        try (Connection c = Koneksi.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void simpanTarget(int userId, int target) throws SQLException{
        String sql = "UPDATE profil SET target_kalori = ? WHERE user_id = ?";
        try (Connection c = Koneksi.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
                ps.setInt(1, target);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
        }
}