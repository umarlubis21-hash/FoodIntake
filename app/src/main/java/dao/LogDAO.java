package dao;

import koneksi.Koneksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {
        public void catat(int userId, String waktuMakan, int makananId, double jumlahPorsi)
            throws SQLException {
        try (Connection c = Koneksi.getConnection()) {
            int logId = cariAtauBuatLog(c, userId, waktuMakan);
            System.out.println("DEBUG logId = " + logId);   // sementara, hapus setelah beres
            String sql = "INSERT INTO detail_log (log_id, makanan_id, jumlah_porsi) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, logId);
                ps.setInt(2, makananId);
                ps.setDouble(3, jumlahPorsi);
                ps.executeUpdate();
            }
        }
    }

    private int cariAtauBuatLog(Connection c, int userId, String waktuMakan) throws SQLException {
        String cari = "SELECT id FROM log_konsumsi "
                    + "WHERE user_id = ? AND tanggal = CURDATE() AND waktu_makan = ?";
        try (PreparedStatement ps = c.prepareStatement(cari)) {
            ps.setInt(1, userId);
            ps.setString(2, waktuMakan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        String buat = "INSERT INTO log_konsumsi (user_id, tanggal, waktu_makan) "
                    + "VALUES (?, CURDATE(), ?)";
        try (PreparedStatement ps = c.prepareStatement(buat, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, waktuMakan);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
    
    public List<Object[]> logHariIni (int userId) throws SQLException {
        String sql = "SELECT l.waktu_makan, m.nama, d.jumlah_porsi, m.kalori * d.jumlah_porsi AS kal "
                   + "FROM log_konsumsi l "
                   + "JOIN detail_log d ON d.log_id = l.id "
                   + "JOIN makanan m ON m.id = d.makanan_id "
                   + "WHERE l.user_id = ? AND l.tanggal = CURDATE() "
                   + "ORDER BY l.id, d.id";
        List<Object[]> baris = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    baris.add(new Object []{
                        rs.getString("waktu_makan"),
                        rs.getString("nama"),
                        rs.getDouble("jumlah_porsi"),
                        Math.round(rs.getDouble("kal"))
                    });
                }
            }
        }
        return baris;
    }
    
    public List<Object[]> logPadaTanggal(int userId, LocalDate tanggal) throws SQLException {
        String sql = "SELECT l.waktu_makan, m.nama, d.jumlah_porsi, m.kalori * d.jumlah_porsi AS kal "
                   + "FROM log_konsumsi l "
                   + "JOIN detail_log d ON d.log_id = l.id "
                   + "JOIN makanan m ON m.id = d.makanan_id "
                   + "WHERE l.user_id = ? AND l.tanggal = ? "
                   + "ORDER BY l.id, d.id";
        List<Object[]> baris = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    baris.add(new Object[]{
                            rs.getString("waktu_makan"),
                            rs.getString("nama"),
                            rs.getDouble("jumlah_porsi"),
                            Math.round(rs.getDouble("kal"))});
                }
            }
        }
        return baris;
    }

    public double totalKaloriPadaTanggal(int userId, LocalDate tanggal) throws SQLException {
        String sql = "SELECT COALESCE(SUM(m.kalori * d.jumlah_porsi), 0) "
                   + "FROM log_konsumsi l "
                   + "JOIN detail_log d ON d.log_id = l.id "
                   + "JOIN makanan m ON m.id = d.makanan_id "
                   + "WHERE l.user_id = ? AND l.tanggal = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }
    
    public double totalKaloriHariIni(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(m.kalori * d.jumlah_porsi), 0) "
                   + "FROM log_konsumsi l "
                   + "JOIN detail_log d ON d.log_id = l.id "
                   + "JOIN makanan m ON m.id = d.makanan_id "
                   + "WHERE l.user_id = ? AND l.tanggal = CURDATE()";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }
}
