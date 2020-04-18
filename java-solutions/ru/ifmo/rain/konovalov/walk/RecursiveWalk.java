package ru.ifmo.rain.konovalov.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveWalk extends SimpleFileVisitor<Path> {
    private final BufferedWriter output;

    RecursiveWalk(BufferedWriter output) {
        this.output = output;
    }

    private void FNVCall(String file) throws IOException {
        output.write(String.format("%1$08x " + file, FNV.evaluate(file)));
        output.newLine();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        FNVCall(file.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        FNVCall(file.toString());
        return FileVisitResult.CONTINUE;
    }

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
                    RecursiveWalk recursiveWalk = new RecursiveWalk(os);
                    for (String str; (str = is.readLine()) != null; )
                        try {
                            Files.walkFileTree(Paths.get(str), recursiveWalk);
                        } catch (InvalidPathException exception) {
                            recursiveWalk.FNVCall(str);
                        }
                } catch (IOException e) {
                    System.out.println("An output error occurred:" + e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("An input error occurred:" + e.getMessage());
            } catch (InvalidPathException e) {
                System.out.println("Invalid input path:" + e.getMessage());
            }
        } catch (InvalidPathException e) {
            System.out.println("Invalid output path:" + e.getMessage());
        }
    }
}
