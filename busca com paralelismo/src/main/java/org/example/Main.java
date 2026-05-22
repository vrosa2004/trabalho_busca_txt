package org.example;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Selecione o dataset:");
        System.out.println("  1 - Todos");
        System.out.println("  2 - dataset_p");
        System.out.println("  3 - dataset_g");
        System.out.print("Opcao: ");
        String opcao = scanner.nextLine().trim();

        String diretorio = switch (opcao) {
            case "1" -> "datasets";
            case "2" -> "datasets/dataset_p";
            case "3" -> "datasets/dataset_g";
            default -> null;
        };

        if (diretorio == null) {
            System.out.println("Opcao invalida.");
            return;
        }

        System.out.print("Termo de busca: ");
        String termoBuscado = scanner.nextLine().trim();

        TextFile[] files = Manager.getTextFiles(new File(diretorio));

        long inicio = System.nanoTime();
        Match[] matches = Manager.findAll(files, termoBuscado);
        long tempo = System.nanoTime() - inicio;

        for (Match m : matches) {
            System.out.println("Arquivo: " + m.file().getFilename() + " | Linha: " + m.line());
        }

        System.out.println("Tempo: " + tempo / 1_000_000 + " ms");
    }
}
