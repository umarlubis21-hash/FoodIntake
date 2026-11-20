package view;

import dao.AuthDAO;
import model.Pengguna;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final AuthDAO authDao = new AuthDAO();

    private final JTextField tfEmail = new JTextField(20);
    private final JPasswordField pfPassword = new JPasswordField(20);

    public LoginFrame() {
        super("Food Intake Management - Masuk");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel judul = new JLabel("Masuk ke Akun Anda", SwingConstants.CENTER);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.add(new JLabel("Email"));
        form.add(tfEmail);
        form.add(new JLabel("Password"));
        form.add(pfPassword);

        JButton btnMasuk = new JButton("Masuk");
        btnMasuk.addActionListener(e -> masuk());
        JButton btnDaftar = new JButton("Daftar Akun Baru");
        btnDaftar.addActionListener(e -> bukaRegistrasi());

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        tombol.add(btnMasuk);
        tombol.add(btnDaftar);

        JPanel kartu = new JPanel(new BorderLayout(8, 12));
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        kartu.add(judul, BorderLayout.NORTH);
        kartu.add(form, BorderLayout.CENTER);
        kartu.add(tombol, BorderLayout.SOUTH);

        setLayout(new GridBagLayout());
        add(kartu);

       
        pfPassword.addActionListener(e -> masuk());
        getRootPane().setDefaultButton(btnMasuk);

        setSize(640, 420);                          
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void masuk() {
        String email = tfEmail.getText().trim().toLowerCase();
        String password = new String(pfPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email dan password harus diisi.",
                    "Input tidak lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Pengguna pengguna = authDao.login(email, password);
            if (pengguna == null) {
                JOptionPane.showMessageDialog(this, "Email atau password salah.",
                        "Gagal masuk", JOptionPane.ERROR_MESSAGE);
                pfPassword.setText("");
                return;
            }
            dispose();
            new MainFrame(pengguna).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal terhubung ke database:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bukaRegistrasi() {
        RegisterFrame reg = new RegisterFrame(this);
        reg.setVisible(true);
        setVisible(false);
    }

    public void tampilkanLagi(String emailBaru) {
        if (emailBaru != null) {
            tfEmail.setText(emailBaru);
            pfPassword.setText("");
        }
        setVisible(true);
        pfPassword.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}