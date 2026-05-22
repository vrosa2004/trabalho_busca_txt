import java.io.*;
import java.util.Scanner;

public class Benchmark {
    private static final int ITERACOES = 100;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Selecione o dataset:");
        System.out.println("  1 - Todos");
        System.out.println("  2 - dataset_p");
        System.out.println("  3 - dataset_g");
        System.out.print("Opcao: ");
        String opcao = scanner.nextLine().trim();

        if (!opcao.equals("1") && !opcao.equals("2") && !opcao.equals("3")) {
            System.out.println("Opcao invalida.");
            return;
        }

        System.out.print("Termo de busca: ");
        String termo = scanner.nextLine().trim();

        String stdin = opcao + "\n" + termo + "\n";
        File root = new File(System.getProperty("user.dir"));

        System.out.println();

        long seqTotal = executarN("busca sem paralelismo/out", "Main", stdin, root, "Sem paralelismo");
        long parTotal = executarN("busca com paralelismo/out", "org.example.Main", stdin, root, "Com paralelismo");

        double seqMedia = (double) seqTotal / ITERACOES;
        double parMedia = (double) parTotal / ITERACOES;
        double speedup  = parMedia > 0 ? seqMedia / parMedia : 0;

        System.out.println();
        System.out.println("=== Resultados (media de " + ITERACOES + " execucoes) ===");
        System.out.println();
        System.out.printf("Sem paralelismo : %.2f ms%n", seqMedia);
        System.out.printf("Com paralelismo : %.2f ms%n", parMedia);
        System.out.printf("SpeedUp         : %.2fx%n", speedup);
    }

    private static long executarN(String cp, String mainClass, String stdin, File dir, String label) throws Exception {
        long total = 0;
        for (int i = 1; i <= ITERACOES; i++) {
            System.out.printf("\r%s: %d/%d  ", label, i, ITERACOES);
            total += executar(cp, mainClass, stdin, dir);
        }
        System.out.println();
        return total;
    }

    private static long executar(String cp, String mainClass, String stdin, File dir) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", cp, mainClass);
        pb.directory(dir);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (OutputStream os = p.getOutputStream()) {
            os.write(stdin.getBytes());
        }

        long tempo = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Tempo:")) {
                    tempo = Long.parseLong(line.split(" ")[1]);
                }
            }
        }

        p.waitFor();
        return tempo;
    }
}
