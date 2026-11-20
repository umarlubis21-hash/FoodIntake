package view;

import dao.LogDAO;
import dao.PenggunaDAO;
import model.Pengguna;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class RiwayatFrame extends JFrame {

    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Pengguna pengguna;
    private final LogDAO logDao = new LogDAO();
    private final PenggunaDAO penggunaDao = new PenggunaDAO();

    private final JSpinner spTanggal =
            new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Waktu Makan", "Makanan", "Porsi", "Kalori (kkal)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JLabel lblRingkasan = new JLabel(" ");
    private final JLabel lblPerWaktu = new JLabel(" ");

    public RiwayatFrame(Pengguna pengguna) {
        super("Riwayat Konsumsi - " + pengguna.getNama());
        this.pengguna = pengguna;

        // Menutup jendela riwayat tidak menutup seluruh aplikasi
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 460);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        spTanggal.setEditor(new JSpinner.DateEditor(spTanggal, "dd/MM/yyyy"));
        spTanggal.addChangeListener(e -> muat());

        JButton btnSebelum = new JButton("< Sebelumnya");
        btnSebelum.addActionListener(e -> geser(-1));
        JButton btnHariIni = new JButton("Hari Ini");
        btnHariIni.addActionListener(e -> setTanggal(LocalDate.now()));
        JButton btnBerikut = new JButton("Berikutnya >");
        btnBerikut.addActionListener(e -> geser(1));

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        atas.add(new JLabel("Tanggal:"));
        atas.add(spTanggal);
        atas.add(btnSebelum);
        atas.add(btnHariIni);
        atas.add(btnBerikut);
        add(atas, BorderLayout.NORTH);

        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new GridLayout(0, 1, 4, 4));
        bawah.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        bawah.add(lblRingkasan);
        bawah.add(lblPerWaktu);
        add(bawah, BorderLayout.SOUTH);

        muat();
    }

    private LocalDate tanggalTerpilih() {
        Date d = (Date) spTanggal.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void setTanggal(LocalDate tanggal) {
        spTanggal.setValue(Date.from(tanggal.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void geser(int hari) {
        setTanggal(tanggalTerpilih().plusDays(hari));
    }

    private void muat() {
        LocalDate tanggal = tanggalTerpilih();
        try {
            tableModel.setRowCount(0);
            Map<String, Long> perWaktu = new LinkedHashMap<>();

            for (Object[] baris : logDao.logPadaTanggal(pengguna.getId(), tanggal)) {
                tableModel.addRow(baris);
                perWaktu.merge((String) baris[0], (Long) baris[3], Long::sum);
            }

            if (tableModel.getRowCount() == 0) {
                lblRingkasan.setText("Tidak ada catatan pada " + tanggal.format(FORMAT_TANGGAL) + ".");
                lblPerWaktu.setText(" ");
                return;
            }

            double total = logDao.totalKaloriPadaTanggal(pengguna.getId(), tanggal);
            int target = penggunaDao.targetKalori(pengguna.getId());
            long sisa = Math.round(target - total);

            lblRingkasan.setText(String.format("%s: %.0f kkal dari target %d kkal (%s %d kkal)",
                    tanggal.format(FORMAT_TANGGAL), total, target,
                    sisa >= 0 ? "sisa" : "lebih", Math.abs(sisa)));

            StringBuilder sb = new StringBuilder("Per waktu makan: ");
            boolean pertama = true;
            for (Map.Entry<String, Long> e : perWaktu.entrySet()) {
                if (!pertama) {
                    sb.append("  |  ");
                }
                sb.append(e.getKey()).append(" ").append(e.getValue()).append(" kkal");
                pertama = false;
            }
            lblPerWaktu.setText(sb.toString());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat riwayat:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }
}