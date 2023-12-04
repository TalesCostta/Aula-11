import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class RubroNegra {

  private static class RedBlackTree<Key extends Comparable<Key>, Value> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private long startTime;

    private Node root;

    private class Node {
      Key key;
      Value value;
      Node left, right;
      boolean color;
      int size;

      public Node(Key key, Value value, boolean color, int size) {
        this.key = key;
        this.value = value;
        this.color = color;
        this.size = size;
      }
    }

    private boolean isRed(Node x) {
      if (x == null)
        return false;
      return x.color == RED;
    }

    private int size(Node x) {
      if (x == null)
        return 0;
      return x.size;
    }

    public Value get(Key key) {
      return get(root, key);
    }

    private Value get(Node x, Key key) {
      while (x != null) {
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
          x = x.left;
        else if (cmp > 0)
          x = x.right;
        else
          return x.value;
      }
      return null;
    }

    public void put(Key key, Value value) {
      root = put(root, key, value);
      root.color = BLACK;
    }

    private Node put(Node h, Key key, Value value) {
      if (h == null)
        return new Node(key, value, RED, 1);

      int cmp = key.compareTo(h.key);
      if (cmp < 0)
        h.left = put(h.left, key, value);
      else if (cmp > 0)
        h.right = put(h.right, key, value);
      else
        h.value = value;

      if (isRed(h.right) && !isRed(h.left))
        h = rotateLeft(h);
      if (isRed(h.left) && isRed(h.left.left))
        h = rotateRight(h);
      if (isRed(h.left) && isRed(h.right))
        flipColors(h);

      h.size = size(h.left) + size(h.right) + 1;
      return h;
    }

    private Node rotateLeft(Node h) {
      Node x = h.right;
      h.right = x.left;
      x.left = h;
      x.color = h.color;
      h.color = RED;
      x.size = h.size;
      h.size = 1 + size(h.left) + size(h.right);
      return x;
    }

    private Node rotateRight(Node h) {
      Node x = h.left;
      h.left = x.right;
      x.right = h;
      x.color = h.color;
      h.color = RED;
      x.size = h.size;
      h.size = 1 + size(h.left) + size(h.right);
      return x;
    }

    private void flipColors(Node h) {
      if (h == null || h.left == null || h.right == null) {
        return;
      }

      h.color = !h.color;
      h.left.color = !h.left.color;
      h.right.color = !h.right.color;
    }

    public void delete(Key key) {
      if (key == null)
        throw new IllegalArgumentException("Key cannot be null");
      if (!contains(key))
        return;

      if (!isRed(root.left) && !isRed(root.right))
        root.color = RED;

      root = delete(root, key);
      if (root != null)
        root.color = BLACK;
    }

    private Node delete(Node h, Key key) {
      if (key.compareTo(h.key) < 0) {
        if (!isRed(h.left) && !isRed(h.left.left))
          h = moveRedLeft(h);
        h.left = delete(h.left, key);
      } else {
        if (isRed(h.left))
          h = rotateRight(h);
        if (key.compareTo(h.key) == 0 && (h.right == null))
          return null;
        if (!isRed(h.right) && !isRed(h.right.left))
          h = moveRedRight(h);
        if (key.compareTo(h.key) == 0) {
          Node x = min(h.right);
          h.key = x.key;
          h.value = x.value;
          h.right = deleteMin(h.right);
        } else {
          h.right = delete(h.right, key);
        }
      }
      return balance(h);
    }

    private Node deleteMin(Node h) {
      if (h.left == null)
        return null;
      if (!isRed(h.left) && !isRed(h.left.left))
        h = moveRedLeft(h);
      h.left = deleteMin(h.left);
      return balance(h);
    }

    private Node moveRedLeft(Node h) {
      flipColors(h);
      if (isRed(h.right.left)) {
        h.right = rotateRight(h.right);
        h = rotateLeft(h);
        flipColors(h);
      }
      return h;
    }

    private Node moveRedRight(Node h) {
      if (h == null || h.left == null || h.right == null) {
        return h;
      }

      flipColors(h);

      if (h.left.left != null && isRed(h.left.left)) {
        h = rotateRight(h);
        flipColors(h);
      }

      return h;
    }

    private Node balance(Node h) {
      if (isRed(h.right))
        h = rotateLeft(h);
      if (isRed(h.left) && isRed(h.left.left))
        h = rotateRight(h);
      if (isRed(h.left) && isRed(h.right))
        flipColors(h);

      h.size = size(h.left) + size(h.right) + 1;
      return h;
    }

    public boolean contains(Key key) {
      return get(key) != null;
    }

    public Iterable<Key> inOrder() {
      List<Key> keys = new ArrayList<>();
      inOrder(root, keys);
      return keys;
    }

    private void inOrder(Node x, List<Key> keys) {
      if (x == null)
        return;
      inOrder(x.left, keys);
      keys.add(x.key);
      inOrder(x.right, keys);
    }

    public Node min(Node x) {
      if (x.left == null)
        return x;
      else
        return min(x.left);
    }

    public void startTimer() {
      startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      System.out.println("Total execution time: " + totalTime + " milliseconds");
    }

  }

  public static void main(String[] args) {
    RedBlackTree<Integer, String> redBlackTree = new RedBlackTree<>();

    List<Integer> numbersFromFile = readNumbersFromFile("./dados100_mil.txt");

    for (Integer number : numbersFromFile) {
      redBlackTree.put(number, "Value-" + number);
    }

    redBlackTree.startTimer();

    for (int i = 0; i < 50000; i++) {
      int randomNumber = (int) (Math.random() * 19999 - 9999); 

      if (randomNumber % 3 == 0) {
        redBlackTree.put(randomNumber, "Inserted-" + i);
      } else if (randomNumber % 5 == 0) {
        redBlackTree.delete(randomNumber);
      } else {
        int occurrences = countOccurrences(redBlackTree.root, randomNumber);
        System.out.println("Number " + randomNumber + " appears " + occurrences + " times in the tree.");
      }
    }

    System.out.println("In-Order Traversal:");
    for (Integer key : redBlackTree.inOrder()) {
      System.out.println(key + ": " + redBlackTree.get(key));
    }
    redBlackTree.stopTimer();
  }

  private static List<Integer> readNumbersFromFile(String filePath) {
    List<Integer> numbers = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line = br.readLine();
      if (line != null) {
        String[] numberStrings = line.replaceAll("[\\[\\]]", "").split(",");
        for (String numStr : numberStrings) {
          numbers.add(Integer.parseInt(numStr.trim()));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return numbers;
  }

  private static <Key extends Comparable<Key>> int countOccurrences(RedBlackTree.Node x, Key key) {
    if (x == null)
      return 0;

    int cmp = key.compareTo((Key) x.key);
    if (cmp < 0)
      return countOccurrences(x.left, key);
    else if (cmp > 0)
      return countOccurrences(x.right, key);
    else
      return 1 + countOccurrences(x.left, key) + countOccurrences(x.right, key);
  }

}
