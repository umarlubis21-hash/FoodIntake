package dao;

import koneksi.Koneksi;
import model.Makanan;
import model.MakananBawaan;
import model.MakananCustom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MakananDAO implements MakananRepository {

    @Override
    public List<Makanan> findAll(int userId) throws SQLException {
        String sql = "SELECT id, user_id, nama, porsi, kalori, protein, karbo, lemak "
                   + "FROM makanan WHERE user_id IS NULL OR user_id = ? ORDER BY nama";
        List<Makanan> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int pemilik = rs.getInt("user_id");
                    boolean bawaan = rs.wasNull();
                    Makanan m;
                    if (bawaan) {
                        m = new MakananBawaan(rs.getInt("id"), rs.getString("nama"),
                                rs.getString("porsi"), rs.getDouble("kalori"),
                                rs.getDouble("protein"), rs.getDouble("karbo"),
                                rs.getDouble("lemak"));
                    } else {
                        m = new MakananCustom(rs.getInt("id"), rs.getString("nama"),
                                rs.getString("porsi"), rs.getDouble("kalori"),
                                rs.getDouble("protein"), rs.getDouble("karbo"),
                                rs.getDouble("lemak"), pemilik);
                    }
                    hasil.add(m);
                }
            }
        }
        return hasil;
    }

    @Override
    public void insert(Makanan makanan) throws SQLException {
        String sql = "INSERT INTO makanan (user_id, nama, porsi, kalori, protein, karbo, lemak) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            Integer pemilik = makanan.getPemilikId();
            if (pemilik == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, pemilik);
            }
            ps.setString(2, makanan.getNama());
            ps.setString(3, makanan.getPorsi());
            ps.setDouble(4, makanan.getKalori());
            ps.setDouble(5, makanan.getProtein());
            ps.setDouble(6, makanan.getKarbo());
            ps.setDouble(7, makanan.getLemak());
            ps.executeUpdate();
        }
    }
}