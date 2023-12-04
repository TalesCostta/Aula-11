import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

class NoAVL {
  int valor;
  int altura;
  NoAVL esquerda, direita;

  public NoAVL(int valor) {
    this.valor = valor;
    this.altura = 1;
  }
}

public class ArvoreAVL {
  private NoAVL raiz;

  private int altura(NoAVL no) {
    if (no == null) {
      return 0;
    }
    return no.altura;
  }

  private int fatorBalanceamento(NoAVL no) {
    if (no == null) {
      return 0;
    }
    return altura(no.esquerda) - altura(no.direita);
  }

  private void atualizaAltura(NoAVL no) {
    if (no != null) {
      no.altura = 1 + Math.max(altura(no.esquerda), altura(no.direita));
    }
  }

  private NoAVL rotacaoDireita(NoAVL y) {
    NoAVL x = y.esquerda;
    NoAVL T2 = x.direita;

    x.direita = y;
    y.esquerda = T2;

    atualizaAltura(y);
    atualizaAltura(x);

    return x;
  }

  private NoAVL rotacaoEsquerda(NoAVL x) {
    NoAVL y = x.direita;
    NoAVL T2 = y.esquerda;

    y.esquerda = x;
    x.direita = T2;

    atualizaAltura(x);
    atualizaAltura(y);

    return y;
  }

  private NoAVL inserir(NoAVL no, int valor) {
    if (no == null) {
      return new NoAVL(valor);
    }

    if (valor < no.valor) {
      no.esquerda = inserir(no.esquerda, valor);
    } else if (valor > no.valor) {
      no.direita = inserir(no.direita, valor);
    } else {
      return no;
    }

    atualizaAltura(no);

    int balanceamento = fatorBalanceamento(no);

    if (balanceamento > 1 && valor < no.esquerda.valor) {
      return rotacaoDireita(no);
    }

    if (balanceamento < -1 && valor > no.direita.valor) {
      return rotacaoEsquerda(no);
    }

    if (balanceamento > 1 && valor > no.esquerda.valor) {
      no.esquerda = rotacaoEsquerda(no.esquerda);
      return rotacaoDireita(no);
    }

    if (balanceamento < -1 && valor < no.direita.valor) {
      no.direita = rotacaoDireita(no.direita);
      return rotacaoEsquerda(no);
    }

    return no;
  }

  private NoAVL remover(NoAVL no, int valor) {
    if (no == null) {
      return no;
    }

    if (valor < no.valor) {
      no.esquerda = remover(no.esquerda, valor);
    } else if (valor > no.valor) {
      no.direita = remover(no.direita, valor);
    } else {
      if ((no.esquerda == null) || (no.direita == null)) {
        NoAVL temp = null;
        if (temp == no.esquerda) {
          temp = no.direita;
        } else {
          temp = no.esquerda;
        }

        if (temp == null) {
          temp = no;
          no = null;
        } else {
          no = temp;
        }
      } else {
        NoAVL temp = menorValorNo(no.direita);
        no.valor = temp.valor;
        no.direita = remover(no.direita, temp.valor);
      }
    }
    if (no == null) {
      return no;
    }

    atualizaAltura(no);

    int balanceamento = fatorBalanceamento(no);

    if (balanceamento > 1 && fatorBalanceamento(no.esquerda) >= 0) {
      return rotacaoDireita(no);
    }

    if (balanceamento < -1 && fatorBalanceamento(no.direita) <= 0) {
      return rotacaoEsquerda(no);
    }

    if (balanceamento > 1 && fatorBalanceamento(no.esquerda) < 0) {
      no.esquerda = rotacaoEsquerda(no.esquerda);
      return rotacaoDireita(no);
    }

    if (balanceamento < -1 && fatorBalanceamento(no.direita) > 0) {
      no.direita = rotacaoDireita(no.direita);
      return rotacaoEsquerda(no);
    }

    return no;
  }

  private NoAVL menorValorNo(NoAVL no) {
    NoAVL atual = no;
    while (atual.esquerda != null) {
      atual = atual.esquerda;
    }
    return atual;
  }

  private void contarOcorrencias(NoAVL no, int valor, int[] contador) {
    if (no != null) {
      if (no.valor == valor) {
        contador[0]++;
      }
      contarOcorrencias(no.esquerda, valor, contador);
      contarOcorrencias(no.direita, valor, contador);
    }
  }

  public void realizarOperacoes(int numero) {
    if (numero % 3 == 0) {
      System.out.println("Inserindo " + numero + " na árvore AVL.");
      raiz = inserir(raiz, numero);
    } else if (numero % 5 == 0) {
      System.out.println("Removendo " + numero + " da árvore AVL.");
      raiz = remover(raiz, numero);
    } else {
      int[] contador = { 0 };
      contarOcorrencias(raiz, numero, contador);
      System.out.println("O número " + numero + " aparece na árvore AVL " + contador[0] + " vezes.");
    }
  }

  public void inserirValoresDoArquivo(String caminhoDoArquivo) {
    try {
      BufferedReader leitor = new BufferedReader(new FileReader(new File(caminhoDoArquivo)));
      String linha = leitor.readLine();
      String[] valores = linha.replaceAll("\\[|\\]|\\s", "").split(",");
      for (String valor : valores) {
        int num = Integer.parseInt(valor);
        raiz = inserir(raiz, num);
      }
      leitor.close();
      System.out.println("Árvore AVL em ordem:");
      imprimirEmOrdem(raiz);
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }
  }

  public void inserirValoresAleatorios(int quantidade) {
    Random random = new Random();
    for (int i = 0; i < quantidade; i++) {
      int numero = random.nextInt(19999) - 9999; 
      raiz = inserir(raiz, numero);
    }
  }

  public void imprimirEmOrdem() {
    imprimirEmOrdem(raiz);
  }

  private void imprimirEmOrdem(NoAVL no) {
    if (no != null) {
      imprimirEmOrdem(no.esquerda);
      System.out.print(no.valor + " ");
      imprimirEmOrdem(no.direita);
    }
  }

  public static void main(String[] args) {
    ArvoreAVL arvore = new ArvoreAVL();
    long inicio = System.currentTimeMillis();

    arvore.inserirValoresDoArquivo("./dados100_mil.txt");
    arvore.inserirValoresAleatorios(50000);

    System.out.println("\nÁrvore AVL após inserções aleatórias:");
    arvore.imprimirEmOrdem();

    arvore.realizarOperacoes(15); 
    arvore.realizarOperacoes(9);
    arvore.realizarOperacoes(25);

    System.out.println("\nÁrvore AVL após operações:");
    arvore.imprimirEmOrdem();

    long fim = System.currentTimeMillis();
    long tempoDeExecucao = fim - inicio;
    System.out.println("Tempo de execução: " + tempoDeExecucao + " ms");
  }
}
