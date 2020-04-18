package ru.ifmo.rain.konovalov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;


public class Walk {
    public static void main(String[] args) {
        if (args == null || args.length < 2 || args[0] == null || args[1] == null) {
            System.out.println("An I/O error occurred: null argument");
            return;
        }

        try {
            Path out = Paths.get(args[1]);
            if (!Files.exists(out)) {
                Path parent = out.getParent();
                if (parent != null)
                    Files.createDirectories(parent);
                Files.createFile(out);
            }

            try (BufferedReader is = Files.newBufferedReader(Paths.get(args[0]), StandardCharsets.UTF_8)) {
                try (BufferedWriter os = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
                    for (String str; (str = is.readLine()) != null; ) {
                        os.write(String.format("%1$08x " + str, FNV.evaluate(str)));
                        os.newLine();
                    }
                }
            }
            System.out.println("OK");
        } catch (IOException | InvalidPathException e) {
            System.err.println("An I/O error occurred:" + e.getMessage());
        }
    }
}
