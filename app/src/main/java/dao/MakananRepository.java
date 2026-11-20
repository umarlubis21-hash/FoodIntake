package dao;

import model.Makanan;

import java.sql.SQLException;
import java.util.List;

public interface MakananRepository {

    List<Makanan> findAll(int penggunaId) throws SQLException;

    void insert(Makanan makanan) throws SQLException;
}