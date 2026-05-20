import java.io.*;
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
              .forEach(p -> buscarNoArquivoKMP(p, nomeBuscado));

    } catch (IOException e) {
      e.printStackTrace();
    }

    long fim = System.nanoTime();

    long tempo = fim - inicio;

    System.out.println("Tempo sequencial (KMP): " + tempo + " ns");
  }

  // Busca no arquivo usando KMP
  public static void buscarNoArquivoKMP(Path path, String padrao) {
    try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {

      String linha;
      int numeroLinha = 1;

      while ((linha = br.readLine()) != null) {
        if (kmpSearch(linha, padrao)) {
          System.out.println("Arquivo: " + path + " | Linha: " + numeroLinha);
        }
        numeroLinha++;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // KMP SEARCH
  public static boolean kmpSearch(String texto, String padrao) {
    int[] lps = computeLPSArray(padrao);

    int i = 0; // texto
    int j = 0; // padrão

    while (i < texto.length()) {
      if (padrao.charAt(j) == texto.charAt(i)) {
        i++;
        j++;
      }

      if (j == padrao.length()) {
        return true;
      } else if (i < texto.length() && padrao.charAt(j) != texto.charAt(i)) {
        if (j != 0) {
          j = lps[j - 1];
        } else {
          i++;
        }
      }
    }

    return false;
  }

  // LPS ARRAY
  public static int[] computeLPSArray(String padrao) {
    int[] lps = new int[padrao.length()];

    int len = 0;
    int i = 1;

    while (i < padrao.length()) {
      if (padrao.charAt(i) == padrao.charAt(len)) {
        len++;
        lps[i] = len;
        i++;
      } else {
        if (len != 0) {
          len = lps[len - 1];
        } else {
          lps[i] = 0;
          i++;
        }
      }
    }

    return lps;
  }
}