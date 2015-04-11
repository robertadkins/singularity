import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class MarcovCaller {
  
  public static Hashtable<String, Vector<String>> markovChain = new Hashtable<String, Vector<String>>();
  static Random rnd = new Random();
  static Scanner scanner= null;
  
  public static void main(String[] args) throws IOException {
    
    // Create the first two entries (k:_start, k:_end)
    markovChain.put("_start", new Vector<String>());
    markovChain.put("_end", new Vector<String>());
    
    try {
      scanner = new Scanner(new BufferedReader(new FileReader("macbethLines.txt")));

      while (scanner.hasNextLine()) {
          MacbethMarcov.addWords(scanner.nextLine());
      }
    } 
    finally {
      if (scanner != null) {
          scanner.close();
      }
    }
    
  }
}
