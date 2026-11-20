package view;

import dao.LogDAO;
import dao.MakananDAO;
import dao.MakananRepository;
import dao.PenggunaDAO;
import model.Makanan;
import model.Pengguna;
import util.KalkulatorHarrisBennedict;
import util.KalkulatorMifflin;
import util.PenghitungKalori;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final Pengguna pengguna;

    private final MakananRepository makananRepo = new MakananDAO();
    private final LogDAO logDao = new LogDAO();
    private final PenggunaDAO penggunaDao = new PenggunaDAO();

    private final JComboBox<Makanan> cbMakanan = new JComboBox<>();
    private final JComboBox<String> cbWaktu = new JComboBox<>(
            new String[]{"Sarapan", "Makan Siang", "Makan Malam", "Camilan"});
    private final JSpinner spPorsi = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 20.0, 0.5));

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Waktu Makan", "Makanan", "Porsi", "Kalori (kkal)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabel = new JTable(tableModel);
    private final List<Integer> idDetail = new ArrayList<>();

    private final JLabel lblRingkasan = new JLabel(" ");
    private final JProgressBar bar = new JProgressBar(0, 100);

    public MainFrame(Pengguna pengguna) {
        super("Food Intake Management - " + pengguna.getSapa());
        this.pengguna = pengguna;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 480);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(8, 8));

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        atas.add(new JLabel("Waktu:"));
        atas.add(cbWaktu);
        atas.add(new JLabel("Makanan:"));
        atas.add(cbMakanan);
        atas.add(new JLabel("Porsi:"));
        atas.add(spPorsi);
        JButton btnTambah = new JButton("Catat");
        btnTambah.addActionListener(e -> catat());
        atas.add(btnTambah);
        JButton btnMakananBaru = new JButton("Tambah Makanan Baru");
        btnMakananBaru.addActionListener(e -> tambahMakananBaru());
        atas.add(btnMakananBaru);
        add(atas, BorderLayout.NORTH);

        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new BorderLayout(8, 8));
        bawah.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bawah.add(lblRingkasan, BorderLayout.NORTH);
        bar.setStringPainted(true);
        bawah.add(bar, BorderLayout.CENTER);
        JButton btnTarget = new JButton("Hitung Target Kalori");
        btnTarget.addActionListener(e -> hitungTarget());
        JButton btnRiwayat = new JButton("Riwayat");
        btnRiwayat.addActionListener(e -> new RiwayatFrame(pengguna).setVisible(true));
        JButton btnHapus = new JButton("Hapus Terpilih");
        btnHapus.addActionListener(e -> hapusTerpilih());
        JButton btnHapusSemua = new JButton("Hapus Semua Hari Ini");
        btnHapusSemua.addActionListener(e -> hapusSemuaHariIni());
        JPanel kanan = new JPanel(new GridLayout(0, 2, 4, 4));
        kanan.add(btnTarget);
        kanan.add(btnRiwayat);
        kanan.add(btnHapus);
        kanan.add(btnHapusSemua);
        bawah.add(kanan, BorderLayout.EAST);
        add(bawah, BorderLayout.SOUTH);

        muatKatalog();
        refresh();
    }

    private void muatKatalog() {
        try {
            cbMakanan.removeAllItems();
            for (Makanan m : makananRepo.findAll(pengguna.getId())) {
                cbMakanan.addItem(m);
            }
        } catch (SQLException ex) {
            tampilError("Gagal memuat katalog makanan", ex);
        }
    }

    private void catat() {
        Makanan dipilih = (Makanan) cbMakanan.getSelectedItem();
        if (dipilih == null) {
            return;
        }
        double porsi = ((Number) spPorsi.getValue()).doubleValue();
        try {
            logDao.catat(pengguna.getId(), (String) cbWaktu.getSelectedItem(),
                    dipilih.getId(), porsi);
            refresh();
        } catch (SQLException ex) {
            tampilError("Gagal mencatat makanan", ex);
        }
    }

    private void tambahMakananBaru() {
        JTextField tfNama = new JTextField();
        JTextField tfPorsi = new JTextField("100 g");
        JTextField tfKalori = new JTextField();
        JTextField tfProtein = new JTextField("0");
        JTextField tfKarbo = new JTextField("0");
        JTextField tfLemak = new JTextField("0");

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nama makanan"));
        form.add(tfNama);
        form.add(new JLabel("Porsi (contoh: 100 g)"));
        form.add(tfPorsi);
        form.add(new JLabel("Kalori (kkal)"));
        form.add(tfKalori);
        form.add(new JLabel("Protein (g)"));
        form.add(tfProtein);
        form.add(new JLabel("Karbohidrat (g)"));
        form.add(tfKarbo);
        form.add(new JLabel("Lemak (g)"));
        form.add(tfLemak);

        int pilihan = JOptionPane.showConfirmDialog(this, form, "Tambah Makanan Baru",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (pilihan != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String nama = tfNama.getText().trim();
            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama makanan tidak boleh kosong.",
                        "Input tidak valid", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Makanan baru = pengguna.buatMakanan(nama, tfPorsi.getText().trim(),
                    Double.parseDouble(tfKalori.getText().trim()),
                    Double.parseDouble(tfProtein.getText().trim()),
                    Double.parseDouble(tfKarbo.getText().trim()),
                    Double.parseDouble(tfLemak.getText().trim()));

            makananRepo.insert(baru);
            muatKatalog();
            JOptionPane.showMessageDialog(this, "Tersimpan di: " + baru.getSumber());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kalori, protein, karbohidrat, dan lemak harus angka.",
                    "Input tidak valid", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            tampilError("Gagal menyimpan makanan", ex);
        }
    }

    private void refresh() {
        try {
            tableModel.setRowCount(0);
            idDetail.clear();
            for (Object[] baris : logDao.logHariIniDenganId(pengguna.getId())) {
                idDetail.add((Integer) baris[0]);
                tableModel.addRow(new Object[]{baris[1], baris[2], baris[3], baris[4]});
            }

            double total = logDao.totalKaloriHariIni(pengguna.getId());
            int target = penggunaDao.targetKalori(pengguna.getId());
            long sisa = Math.round(target - total);

            lblRingkasan.setText(String.format(
                    "Hari ini: %.0f kkal dari target %d kkal (%s %d kkal)",
                    total, target, sisa >= 0 ? "sisa" : "lebih", Math.abs(sisa)));

            int persen = target > 0 ? (int) Math.min(100, Math.round(total * 100 / target)) : 0;
            bar.setValue(persen);
            bar.setString(persen + "%");
        } catch (SQLException ex) {
            tampilError("Gagal memuat data hari ini", ex);
        }
    }

    private void hitungTarget() {
        JTextField tfUsia = new JTextField("20");
        JTextField tfBerat = new JTextField("60");
        JTextField tfTinggi = new JTextField("165");
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"L", "P"});
        JComboBox<String> cbAktivitas = new JComboBox<>(new String[]{
            "Jarang olahraga (1.2)", "Olahraga ringan (1.375)",
            "Olahraga sedang (1.55)", "Sangat aktif (1.725)"});
        double[] faktor = {1.2, 1.375, 1.55, 1.725};

        JComboBox<PenghitungKalori> cbRumus = new JComboBox<>(new PenghitungKalori[]{
            new KalkulatorMifflin(), new KalkulatorHarrisBennedict()});

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Rumus"));
        form.add(cbRumus);
        form.add(new JLabel("Usia (tahun)"));
        form.add(tfUsia);
        form.add(new JLabel("Berat (kg)"));
        form.add(tfBerat);
        form.add(new JLabel("Tinggi (cm)"));
        form.add(tfTinggi);
        form.add(new JLabel("Gender (L/P)"));
        form.add(cbGender);
        form.add(new JLabel("Aktivitas"));
        form.add(cbAktivitas);

        int pilihan = JOptionPane.showConfirmDialog(this, form, "Hitung Target Kalori",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (pilihan != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int usia = Integer.parseInt(tfUsia.getText().trim());
            double berat = Double.parseDouble(tfBerat.getText().trim());
            double tinggi = Double.parseDouble(tfTinggi.getText().trim());
            char gender = ((String) cbGender.getSelectedItem()).charAt(0);

            PenghitungKalori rumus = (PenghitungKalori) cbRumus.getSelectedItem();
            double tdee = rumus.hitungTdee(gender, berat, tinggi, usia,
                    faktor[cbAktivitas.getSelectedIndex()]);

            penggunaDao.simpanTarget(pengguna.getId(), (int) Math.round(tdee));
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Usia, berat, dan tinggi harus berupa angka.",
                    "Input tidak valid", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            tampilError("Gagal menyimpan target kalori", ex);
        }
    }

    private void hapusTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu baris yang ingin dihapus di tabel.",
                    "Belum ada pilihan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nama = (String) tableModel.getValueAt(baris, 1);
        int ya = JOptionPane.showConfirmDialog(this,
                "Hapus \"" + nama + "\" dari catatan hari ini?",
                "Konfirmasi hapus", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ya != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            logDao.hapusDetail(idDetail.get(baris), pengguna.getId());
            refresh();
        } catch (SQLException ex) {
            tampilError("Gagal menghapus catatan", ex);
        }
    }

    private void hapusSemuaHariIni() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada catatan hari ini.");
            return;
        }
        int ya = JOptionPane.showConfirmDialog(this,
                "Hapus SEMUA catatan makanan hari ini? Tindakan ini tidak bisa dibatalkan.",
                "Konfirmasi hapus semua", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ya != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            logDao.hapusSemuaHariIni(pengguna.getId());
            refresh();
        } catch (SQLException ex) {
            tampilError("Gagal menghapus catatan", ex);
        }
    }

    private void tampilError(String pesan, Exception ex) {
        JOptionPane.showMessageDialog(this, pesan + ":\n" + ex.getMessage(),
                "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}