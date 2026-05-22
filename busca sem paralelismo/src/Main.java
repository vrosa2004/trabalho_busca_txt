import java.io.IOException;
import java.nio.file.*;
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
    String nomeBuscado = scanner.nextLine().trim();

    long inicio = System.nanoTime();
    try {
      Files.walk(Paths.get(diretorio))
              .filter(Files::isRegularFile)
              .filter(p -> p.toString().endsWith(".txt"))
              .forEach(p -> buscarNoArquivo(p, nomeBuscado));
    } catch (IOException e) {
      e.printStackTrace();
    }
    long fim = System.nanoTime();

    System.out.println("Tempo: " + (fim - inicio) / 1_000_000 + " ms");
  }

  public static void buscarNoArquivo(Path path, String nome) {
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(path.toFile()))) {
      String linha;
      int numeroLinha = 1;
      while ((linha = br.readLine()) != null) {
        if (linha.contains(nome)) {
          System.out.println("Arquivo: " + path + " | Linha: " + numeroLinha);
        }
        numeroLinha++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
