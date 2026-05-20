import java.io.IOException;
import java.nio.file.*;

public class Main {

  public static void main(String[] args) {
    String diretorio = "../arquivos_txt";
    String nomeBuscado = "Brendan Mckenzie";

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
    long tempo = fim - inicio;

    System.out.println("Tempo: " + tempo + " ns");
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