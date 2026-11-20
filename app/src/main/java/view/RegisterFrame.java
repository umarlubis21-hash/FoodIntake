package view;

import dao.AuthDAO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterFrame extends JFrame {

    private static final int PASSWORD_MINIMAL = 6;
    private static final Pattern POLA_EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final AuthDAO authDao = new AuthDAO();
    private final LoginFrame loginFrame;

    private final JTextField tfNama = new JTextField(20);
    private final JTextField tfEmail = new JTextField(20);
    private final JPasswordField pfPassword = new JPasswordField(20);
    private final JPasswordField pfKonfirmasi = new JPasswordField(20);

    private boolean berhasil = false;

    public RegisterFrame(LoginFrame loginFrame) {
        super("Food Intake Management - Daftar");
        this.loginFrame = loginFrame;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (!berhasil) {
                    RegisterFrame.this.loginFrame.tampilkanLagi(null);
                }
            }
        });

        setLayout(new BorderLayout(8, 8));

        JLabel judul = new JLabel("Buat Akun Baru", SwingConstants.CENTER);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 18f));
        judul.setBorder(BorderFactory.createEmptyBorder(14, 8, 0, 8));
        add(judul, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(BorderFactory.createEmptyBorder(12, 20, 8, 20));
        form.add(new JLabel("Nama lengkap"));
        form.add(tfNama);
        form.add(new JLabel("Email"));
        form.add(tfEmail);
        form.add(new JLabel("Password (min. " + PASSWORD_MINIMAL + " karakter)"));
        form.add(pfPassword);
        form.add(new JLabel("Ulangi password"));
        form.add(pfKonfirmasi);
        add(form, BorderLayout.CENTER);

        JButton btnDaftar = new JButton("Daftar");
        btnDaftar.addActionListener(e -> daftar());
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dispose());

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        tombol.add(btnDaftar);
        tombol.add(btnBatal);
        add(tombol, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnDaftar);
        pack();
        setLocationRelativeTo(null);
    }

    private void daftar() {
        String nama = tfNama.getText().trim();
        String email = tfEmail.getText().trim().toLowerCase();
        String password = new String(pfPassword.getPassword());
        String konfirmasi = new String(pfKonfirmasi.getPassword());

        String pesanSalah = validasi(nama, email, password, konfirmasi);
        if (pesanSalah != null) {
            JOptionPane.showMessageDialog(this, pesanSalah,
                    "Input tidak valid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!authDao.daftar(nama, email, password)) {
                JOptionPane.showMessageDialog(this, "Email ini sudah terdaftar. Gunakan email lain atau masuk.",
                        "Email sudah dipakai", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Registrasi berhasil. Silakan masuk.");
            berhasil = true;
            dispose();
            loginFrame.tampilkanLagi(email);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan akun:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validasi(String nama, String email, String password, String konfirmasi) {
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || konfirmasi.isEmpty()) {
            return "Semua kolom harus diisi.";
        }
        if (!POLA_EMAIL.matcher(email).matches()) {
            return "Format email tidak valid. Contoh: nama@mail.com";
        }
        if (password.length() < PASSWORD_MINIMAL) {
            return "Password minimal " + PASSWORD_MINIMAL + " karakter.";
        }
        if (!password.equals(konfirmasi)) {
            return "Konfirmasi password tidak sama.";
        }
        return null;
    }
}