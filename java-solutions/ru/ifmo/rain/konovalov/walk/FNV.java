package ru.ifmo.rain.konovalov.walk;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FNV {
    private static final int FNV_INIT = 0x811c9dc5;
    private static final int FNV_EKE = 0x01000193;

    public static int evaluate(String FileName) {
        int xkey = FNV_INIT;
        try  (InputStream is = new FileInputStream(FileName)) {
            byte[] b = new byte[1024];
            for (int c; (c = is.read(b)) >= 0;) {
                for(int i = 0; i < c; i++) {
                    //xkey = (xkey * 0x01000193) ^ (b[i] & 0xff);
                    xkey *= FNV_EKE;
                    xkey ^= (int) b[i] & 0xff;
                }
            }
        } catch (IOException ignored) {
            return 0;
        }
        return xkey;
    }
}
