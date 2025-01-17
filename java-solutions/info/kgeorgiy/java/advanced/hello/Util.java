//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package info.kgeorgiy.java.advanced.hello;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.Assert;

public class Util {
    public static final Charset CHARSET;
    private static final List<String> ANSWER;
    private static Util.Mode mode;
    private static final List<Function<String, String>> CORRUPTIONS;
    private static final List<BiFunction<String, Random, String>> EVIL_MODIFICATIONS;

    private Util() {
    }

    public static String getString(DatagramPacket var0) {
        return getString(var0.getData(), var0.getOffset(), var0.getLength());
    }

    public static String getString(byte[] var0, int var1, int var2) {
        return new String(var0, var1, var2, CHARSET);
    }

    public static void setString(DatagramPacket var0, String var1) {
        byte[] var2 = getBytes(var1);
        var0.setData(var2);
        var0.setLength(var0.getData().length);
    }

    public static byte[] getBytes(String var0) {
        return var0.getBytes(CHARSET);
    }

    public static DatagramPacket createPacket(DatagramSocket var0) throws SocketException {
        return new DatagramPacket(new byte[var0.getReceiveBufferSize()], var0.getReceiveBufferSize());
    }

    public static String request(String var0, DatagramSocket var1, SocketAddress var2) throws IOException {
        send(var1, var0, var2);
        return receive(var1);
    }

    public static String receive(DatagramSocket var0) throws IOException {
        DatagramPacket var1 = createPacket(var0);
        var0.receive(var1);
        return getString(var1);
    }

    public static void send(DatagramSocket var0, String var1, SocketAddress var2) throws IOException {
        DatagramPacket var3 = new DatagramPacket(new byte[0], 0);
        setString(var3, var1);
        var3.setSocketAddress(var2);
        var0.send(var3);
    }

    public static String response(String var0) {
        return String.format("Hello, %s", var0);
    }

    public static AtomicInteger[] server(String var0, int var1, double var2, DatagramSocket var4) {
        AtomicInteger[] var5 = (AtomicInteger[])Stream.generate(AtomicInteger::new).limit((long)var1).toArray((var0x) -> {
            return new AtomicInteger[var0x];
        });
        (new Thread(() -> {
            Random var6 = new Random(4357204587045842850L + (long)Objects.hash(new Object[]{var0, var1, var2}));

            try {
                while(true) {
                    DatagramPacket var7 = createPacket(var4);
                    var4.receive(var7);
                    String var8 = getString(var7);
                    Boolean flag = var8.startsWith(var0);
                    String var9 = "Invalid or unexpected request |" + var8 + "| expected: |" + var0 + "|" + flag;
                    Assert.assertTrue(var9, flag);
                    var9 += "1_";
                    String[] var10 = var8.substring(var0.length()).split("_");
                    var9 += "2_";
                    Assert.assertEquals(var9, 2L, (long)var10.length);
                    var9 += "3_";
                    try {
                        int var11 = Integer.parseInt(var10[0]);
                        int var12 = Integer.parseInt(var10[1]);
                        var9 += "|>" + var10[0] + "<|";
                        var9 += "|>" + var10[1] + "<|";
                        var9 += "|>" + var11 + "<|";
                        var9 += "|>" + var12 + "<|";
                        var9 += "max : |>" + var5.length + "<|";
                        var9 += " " + Arrays.toString(var5);


                        Assert.assertTrue(var9, 0 <= var11 && var11 < var5.length);
                        var9 += "6_";
                        Assert.assertEquals(var9, (long)var5[var11].get(), (long)var12);
                        var9 += "7_";
                        String var13 = mode.response(var8, var6);
                        var9 += "8_";
                        if (var2 >= var6.nextDouble()) {
                            var9 += "9_";
                            var5[var11].incrementAndGet();
                            var9 += "10_";
                            setString(var7, var13);
                            var9 += "11_";
                            System.out.println("=========Server send: " + var13);
                            var4.send(var7);
                            var9 += "12_";
                        } else if (var6.nextBoolean()) {
                            var9 += "13_";
                            String ans = (String)((Function)select(CORRUPTIONS, var6)).apply(var13);
                            setString(var7, ans);
                            var9 += "14_";
                            System.out.println("________Server send: " + ans);
                            var4.send(var7);
                        }
                    } catch (NumberFormatException var14) {
                        var9 += "15_";
                        throw new AssertionError(var9);
                    }
                }
            } catch (IOException var15) {
                System.err.println(var15.getMessage());
            }
        })).start();
        return var5;
    }

    private static <T> T select(List<T> var0, Random var1) {
        return var0.get(var1.nextInt(var0.size()));
    }

    static void setMode(String var0) {
        mode = var0.matches(".*-i18n") ? Util.Mode.I18N : (var0.matches(".*-evil") ? Util.Mode.EVIL : Util.Mode.NORMAL);
    }

    static {
        CHARSET = StandardCharsets.UTF_8;
        ANSWER = List.of("Hello, %s", "%s ආයුබෝවන්", "Բարեւ, %s", "مرحبا %s", "Салом %s", "Здраво %s", "Здравейте %s", "Прывітанне %s", "Привіт %s", "Привет, %s", "Поздрав %s", "سلام به %s", "שלום %s", "Γεια σας %s", "העלא %s", "ہیل%s٪ ے", "Bonjou %s", "Bonjour %s", "Bună ziua %s", "Ciao %s", "Dia duit %s", "Dobrý deň %s", "Dobrý den, %s", "Habari %s", "Halló %s", "Hallo %s", "Halo %s", "Hei %s", "Hej %s", "Hello  %s", "Hello %s", "Hello %s", "Helo %s", "Hola %s", "Kaixo %s", "Kamusta %s", "Merhaba %s", "Olá %s", "Ola %s", "Përshëndetje %s", "Pozdrav %s", "Pozdravljeni %s", "Salom %s", "Sawubona %s", "Sveiki %s", "Tere %s", "Witaj %s", "Xin chào %s", "ສະບາຍດີ %s", "สวัสดี %s", "ഹലോ %s", "ಹಲೋ %s", "హలో %s", "हॅलो %s", "नमस्कार%sको", "হ্যালো %s", "ਹੈਲੋ %s", "હેલો %s", "வணக்கம் %s", "ကို %s မင်္ဂလာပါ", "გამარჯობა %s", "ជំរាបសួរ %s បាន", "こんにちは%s", "你好%s", "안녕하세요  %s");
        CORRUPTIONS = List.of((var0) -> {
            return var0.replaceAll("[_\\-]", "0");
        }, (var0) -> {
            return var0.replaceAll("([0-9])", "$1$1");
        }, (var0) -> {
            return var0.replaceFirst("[0-9]", "-");
        }, (var0) -> {
            return "";
        }, (var0) -> {
            return "~";
        });
        EVIL_MODIFICATIONS = List.of((var0, var1) -> {
            return var0;
        }, (var0, var1) -> {
            return var0;
        }, (var0, var1) -> {
            return var0;
        }, (var0, var1) -> {
            return var0;
        }, (var0, var1) -> {
            return var0;
        }, (var0, var1) -> {
            return var0;
        }, (var0, var1) -> {
            return var0.replaceAll("[^0-9]", "_");
        }, (var0, var1) -> {
            return var0.replaceAll("[^0-9]", "-");
        }, (var0, var1) -> {
            return Pattern.compile("([^0-9]+)").matcher(var0).replaceAll((var1x) -> {
                return (String)select(ANSWER, var1);
            });
        }, (var0, var1) -> {
            return var0.replaceAll("([^0-9])", "$1$1");
        });
    }

    static enum Mode {
        NORMAL((var0, var1) -> {
            return Util.response(var0);
        }),
        I18N((var0, var1) -> {
            return String.format((String)Util.select(Util.ANSWER, var1), var0);
        }),
        EVIL((var0, var1) -> {
            return I18N.response((String)((BiFunction)Util.select(Util.EVIL_MODIFICATIONS, var1)).apply(var0, var1), var1);
        });

        private final BiFunction<String, Random, String> f;

        private Mode(BiFunction<String, Random, String> var3) {
            this.f = var3;
        }

        public String response(String var1, Random var2) {
            return (String)this.f.apply(var1, var2);
        }
    }
}
