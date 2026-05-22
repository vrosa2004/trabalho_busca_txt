package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Manager {
    private static final int THREAD_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors());

    public static TextFile[] getTextFiles(File pathFile) {
        if (pathFile == null || !pathFile.isDirectory()) {
            throw new IllegalArgumentException("Caminho invalido: deve ser uma pasta.");
        }

        List<File> found = new ArrayList<>();
        try {
            Files.walk(pathFile.toPath())
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".txt"))
                    .forEach(p -> found.add(p.toFile()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Erro ao listar arquivos: " + e.getMessage());
        }

        int fileCount = found.size();
        if (fileCount == 0) return new TextFile[0];

        TextFile[] result = new TextFile[fileCount];
        ExecutorService pool = Executors.newFixedThreadPool(Math.min(THREAD_COUNT, fileCount));
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < fileCount; i++) {
            final int idx = i;
            final File f = found.get(i);
            futures.add(pool.submit(() -> {
                try {
                    List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
                    result[idx] = new TextFile(f.getName(), lines.toArray(new String[0]));
                } catch (IOException e) {
                    result[idx] = null;
                }
            }));
        }

        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result;
    }

    public static Match[] findAll(TextFile[] files, String needle) {
        if (files == null || needle == null || needle.isEmpty() || files.length == 0) {
            return new Match[0];
        }

        record LineRef(TextFile file, int lineNum, String content) {}

        List<LineRef> allLines = new ArrayList<>();
        for (TextFile file : files) {
            if (file == null) continue;
            String[] lines = file.getLines();
            for (int i = 0; i < lines.length; i++) {
                allLines.add(new LineRef(file, i + 1, lines[i]));
            }
        }

        int total = allLines.size();
        if (total == 0) return new Match[0];

        int chunkSize = (total + THREAD_COUNT - 1) / THREAD_COUNT;
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<Match>>> futures = new ArrayList<>();

        for (int t = 0; t < THREAD_COUNT; t++) {
            final int from = t * chunkSize;
            final int to   = Math.min(from + chunkSize, total);
            if (from >= total) break;

            futures.add(pool.submit(() -> {
                List<Match> local = new ArrayList<>();
                for (int i = from; i < to; i++) {
                    LineRef ref = allLines.get(i);
                    int col = ref.content().indexOf(needle);
                    while (col >= 0) {
                        local.add(new Match(ref.file(), ref.lineNum(), col + 1));
                        col = ref.content().indexOf(needle, col + needle.length());
                    }
                }
                return local;
            }));
        }

        pool.shutdown();

        List<Match> all = new ArrayList<>();
        try {
            for (Future<List<Match>> f : futures) {
                all.addAll(f.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        return all.toArray(new Match[0]);
    }
}
