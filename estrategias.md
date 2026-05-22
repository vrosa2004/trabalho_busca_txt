# Estrategias de Busca

## Busca sem Paralelismo

**Arquivo:** `busca sem paralelismo/src/Main.java`

### Estrategia

1. Percorre o diretorio recursivamente com `Files.walk`, filtrando apenas arquivos `.txt`.
2. Para cada arquivo, abre um `BufferedReader` e le o conteudo linha por linha.
3. Em cada linha, aplica `String.contains()` para verificar se o termo buscado esta presente.
4. Ao encontrar, imprime o caminho do arquivo e o numero da linha no console.
5. Ao fim, imprime o tempo total de execucao em nanosegundos.

### Execucao

- Unica thread. Os arquivos sao processados um apos o outro, na ordem em que o `Files.walk` os retorna.

---

## Busca com Paralelismo

**Arquivos:** `busca com paralelismo/src/main/java/org/example/`

### Estrategia

O algoritmo de busca em cada arquivo e identico ao sequencial: `BufferedReader` linha por linha + `String.indexOf()`. A diferenca esta em como os arquivos sao distribuidos entre as threads.

1. Percorre o diretorio recursivamente com `Files.walk`, montando a lista de arquivos `.txt`.
2. Cria N threads, onde N e o numero de nucleos logicos da CPU (`Runtime.getRuntime().availableProcessors()`).
3. Cada thread compartilha um `AtomicInteger` como cursor. A thread incrementa o cursor atomicamente e processa o arquivo no indice obtido, repetindo ate que todos os arquivos sejam consumidos. Esse modelo e chamado de **work stealing**: threads mais rapidas nao ficam ociosas, elas pegam o proximo arquivo disponivel.
4. Cada thread escreve seus resultados em sua propria lista privada, eliminando a necessidade de sincronizacao durante a busca.
5. Ao fim, as listas sao concatenadas e exibidas na interface grafica, junto com o tempo sequencial, o tempo paralelo e o **SpeedUp** calculado.

### Execucao

- N threads concorrentes (uma por nucleo de CPU).
- Interface grafica (Swing) com tabela de resultados mostrando arquivo, linha, coluna, offset e trecho da ocorrencia.

---

## Diferencas

| | Sem Paralelismo | Com Paralelismo |
|---|---|---|
| Threads | 1 | N (nucleos da CPU) |
| Algoritmo de busca | `String.contains()` | `String.indexOf()` (equivalente) |
| Leitura dos arquivos | `BufferedReader` linha a linha | `BufferedReader` linha a linha |
| Distribuicao de trabalho | Loop sequencial | `AtomicInteger` compartilhado (work stealing) |
| Saida | Console (arquivo + linha) | Interface grafica (arquivo, linha, coluna, offset, trecho) |
| Medicao de tempo | Tempo total em nanosegundos | Tempo sequencial, paralelo e SpeedUp lado a lado |
