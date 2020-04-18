package ru.ifmo.rain.konovalov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;


public class Walk {
    public static void main(String[] args) {
        if (args == null || args.length < 2 || args[0] == null || args[1] == null) {
            System.out.println("Error: null argument");
            return;
        }

        try {
            Path out = Paths.get(args[1]);
            if (!Files.exists(out)) {
                Path parent = out.getParent();
                try {
                    if (parent != null)
                        Files.createDirectories(parent);
                    Files.createFile(out);
                } catch (IOException e) {
                    System.out.println("Output file creation error:" + e.getMessage());
                    return;
                }
            }

            try (BufferedReader is = Files.newBufferedReader(Paths.get(args[0]), StandardCharsets.UTF_8)) {
                try (BufferedWriter os = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
                    for (String str; (str = is.readLine()) != null; ) {
                        os.write(String.format("%1$08x " + str, FNV.evaluate(str)));
                        os.newLine();
                    }
                }
                catch (IOException e) {
                    System.out.println("An output error occurred:" + e.getMessage());
                }
            }
            catch (IOException e) {
                System.out.println("An input error occurred:" + e.getMessage());
            } catch (InvalidPathException e) {
                System.out.println("Invalid input path:" + e.getMessage());
            }
            System.out.println("OK");
        }
        catch (InvalidPathException e) {
            System.out.println("Invalid output path:" + e.getMessage());
        }
    }
}
