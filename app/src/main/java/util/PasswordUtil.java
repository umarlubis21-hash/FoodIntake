package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERASI = 120_000;
    private static final int PANJANG_SALT = 16;     // byte
    private static final int PANJANG_HASH = 32;     // byte

    public static String hash(String password) {
        byte[] salt = new byte[PANJANG_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERASI);
        return ITERASI + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifikasi(String password, String tersimpan) {
        if (tersimpan == null) {
            return false;
        }
        String[] bagian = tersimpan.split(":");
        if (bagian.length != 3) {
            return false;
        }
        try {
            int iterasi = Integer.parseInt(bagian[0]);
            byte[] salt = Base64.getDecoder().decode(bagian[1]);
            byte[] hashAsli = Base64.getDecoder().decode(bagian[2]);
            byte[] hashUji = pbkdf2(password.toCharArray(), salt, iterasi);
            return MessageDigest.isEqual(hashAsli, hashUji);   // perbandingan waktu-konstan
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterasi) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterasi, PANJANG_HASH * 8);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 tidak tersedia di Java ini", e);
        }
    }

    public static void main(String[] args) {
        for (String p : args.length > 0 ? args : new String[]{"demo123", "admin123"}) {
            System.out.println(p + " -> " + hash(p));
        }
    }
}