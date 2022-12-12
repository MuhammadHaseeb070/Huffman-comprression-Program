import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Tree implements Serializable {

  Node root;
  Map<Character, String> huffCodes = new HashMap<>();
  char array1[];
  int array2[];
  String text;

  Tree(char arr[], int arr1[], String text) {
    this.array1 = arr;
    this.text = text;
    this.array2 = arr1;
  }

  static class Node implements Serializable {

    Node right;
    Node left;
    char data;
    int freq;
    byte edge;

    Node(int freq) {
      this.freq = freq;
      right = null;
      left = null;
    }

    Node(char data, int freq) {
      this.data = data;
      this.freq = freq;
      right = null;
      left = null;
    }
  }

  void addNode(int a, char b) {
    if (a < root.freq) {
      Node n1 = new Node(root.freq + a);
      Node n2 = new Node(b, a);
      n1.left = n2;
      n1.right = root;
      root = n1;
    } else {
      Node n1 = new Node(root.freq + a);
      Node n2 = new Node(b, a);
      n1.right = n2;
      n1.left = root;
      root = n1;
    }
  }

  void addNode(int a, char b, int c, char d) {
    root = new Node(a + c);
    Node n1 = new Node(b, a);
    Node n2 = new Node(d, c);
    root.left = n1;
    root.right = n2;
  }

  void huffCodegenerator(Node root, String code) {
    if ((root.left == null) && (root.right == null)) {
      huffCodes.put(root.data, code);
      return;
    }
    huffCodegenerator(root.left, code + "0");
    huffCodegenerator(root.right, code + "1");
  }

  String getEncodedtext() {
    String s = "";

    for (int i = 0; i < text.length(); i++) {
      s = s + huffCodes.get(text.charAt(i));
    }
    return s;
  }

  String Decoder(String code) {
    Node cur = root;
    String text = "";
    for (int i = 0; i < code.length(); i++) {
      if ((cur.left == null) && (cur.right == null)) {
        text = text + cur.data;
        cur = root;
      }
      if (code.charAt(i) == '0') {
        cur = cur.left;
      } else {
        cur = cur.right;
      }
    }
    if ((cur.left == null) && (cur.right == null)) {
      text = text + cur.data;
      cur = root;
    }

    return text;
  }

  void Display(Node root) {
    if (root == null) {
      return;
    }
    Display(root.left);
    if (root.data != ' ') {
      System.out.print(root.data + "  ");
    } else {
      System.out.print("z  ");
    }

    Display(root.right);
  }

  void createTree(int array2[], char array1[]) {
    addNode(array2[0], array1[0], array2[1], array1[1]);
    for (int i = 2; i < array1.length; i++) {
      addNode(array2[i], array1[i]);
    }
  }
}

class Program {

  public static void main(String[] args)
    throws IOException, ClassNotFoundException {
    File myFile = new File(
      "D:/Semester3/Data Structure/Final Project/TextFile.txt"
    );
    String text = "";
    Scanner reader1 = new Scanner(myFile);

    while (reader1.hasNextLine()) {
      text = text + reader1.nextLine();
    }
    reader1.close();

    int counter = 1;
    ArrayList<Character> charlist = new ArrayList<Character>();
    ArrayList<Integer> freqlist = new ArrayList<Integer>();

    for (int i = 0; i < text.length(); i++) {
      char a = text.charAt(i);

      if (charlist.contains(a)) {} else {
        charlist.add(a);
        for (int j = i + 1; j < text.length(); j++) {
          if (a == text.charAt(j)) {
            counter++;
          }
        }
        freqlist.add(counter);
        counter = 1;
      }
    }

    char array1[] = new char[charlist.size()];
    int array2[] = new int[freqlist.size()];

    for (int i = 0; i < charlist.size(); i++) {
      array1[i] = charlist.get(i);
      array2[i] = freqlist.get(i);
    }

    //for sorting
    char a = ' ';
    for (int i = 0; i < array2.length; i++) {
      int temp = array2[i];
      for (int j = i + 1; j < array2.length; j++) {
        if (array2[j] < array2[i]) {
          temp = array2[i];
          array2[i] = array2[j];
          array2[j] = temp;

          a = array1[i];
          array1[i] = array1[j];
          array1[j] = a;
        }
      }
    }

    Tree t1 = new Tree(array1, array2, text);
    t1.createTree(array2, array1);
    //  t1.Display(t1.root);
    t1.huffCodegenerator(t1.root, "");
    String encoded = t1.getEncodedtext();

    BitSet bitset = new BitSet();

    for (int i = 0; i < encoded.length(); i++) {
      if (encoded.charAt(i) == '1') {
        bitset.set(i);
      }
    }

    // System.out.println(encoded);

    FileOutputStream writer = new FileOutputStream(
      "D:/Semester3/Data Structure/Final Project/encodedfile.txt"
    );
    ObjectOutputStream ou = new ObjectOutputStream(writer);

    ou.writeObject(t1);
    ou.writeObject(bitset);
    ou.close();
    FileInputStream reader = new FileInputStream(
      "D:/Semester3/Data Structure/Final Project/encodedfile.txt"
    );
    ObjectInputStream iu = new ObjectInputStream(reader);
    Tree t2 = (Tree) iu.readObject();
    BitSet bitset2 = (BitSet) iu.readObject();
    iu.close();
    //  System.out.println("After reading");

    encoded = "";
    for (int i = 0; i < bitset2.length(); i++) {
      if (bitset2.get(i) == false) {
        encoded = encoded + "0";
      } else {
        encoded = encoded + "1";
      }
    }
 
    String plaintext = t2.Decoder(encoded);
    System.out.println("After decoding");
    System.out.println(plaintext);
  }
}
