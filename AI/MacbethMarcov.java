import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class MacbethMarcov {
  

  // Hashmap
  public static Hashtable<String, Vector<String>> markovChain = new Hashtable<String, Vector<String>>();
  static Random rnd = new Random();
  static Scanner scanner= null;
  
  /*
   * Main constructor
   */
  public static void main(String[] args) throws IOException {
    
    // Create the first two entries (k:_start, k:_end)
    markovChain.put("_start", new Vector<String>());
    markovChain.put("_end", new Vector<String>());
    
    try {
      scanner = new Scanner(new BufferedReader(new FileReader("macbethLines.txt")));

      while (scanner.hasNextLine()) {
          addWords(scanner.nextLine());
      }
    } 
    finally {
      if (scanner != null) {
          scanner.close();
      }
    }
    
    while(true) {
    // Get some words 
    System.out.print("Enter your phrase > ");
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String sInput = in.readLine() + ".";
    
    generateSentence();
    }
    
  }
  
  /*
   * Add words
   */
  public static void addWords(String phrase) {
    // put each word into an array
    String[] words = phrase.split(" ");
    System.out.println(phrase);
        
    // Loop through each word, check if it's already added
    // if its added, then get the suffix vector and add the word
    // if it hasn't been added then add the word to the list
    // if its the first or last word then select the _start / _end key
    
    for (int i=0; i<words.length; i++) {
            
      // Add the start and end words to their own
      if (i == 0) {
        Vector<String> startWords = markovChain.get("_start");
        startWords.add(words[i]);
        
        if (words.length > 1) {
          Vector<String> suffix = markovChain.get(words[i]);
          if (suffix == null) {
            suffix = new Vector<String>();
          }
          suffix.add(words[i+1]);
          markovChain.put(words[i], suffix);
        }
        
        
      } else if (i == words.length-1) {
        Vector<String> endWords = markovChain.get("_end");
        endWords.add(words[i]);
        
      } else {  
        Vector<String> suffix = markovChain.get(words[i]);
        if (suffix == null) {
          suffix = new Vector<String>();
        }
        suffix.add(words[i+1]);
        markovChain.put(words[i], suffix);
      }
    }   
  }
  
  
  /*
   * Generate a markov phrase
   */
  public static void generateSentence() {
    
    // Vector to hold the phrase
    Vector<String> newPhrase = new Vector<String>();
    
    // String for the next word
    String nextWord = "";
        
    // Select the first word
    Vector<String> startWords = markovChain.get("_start");
    int startWordsLen = startWords.size();
    nextWord = startWords.get(rnd.nextInt(startWordsLen));
    newPhrase.add(nextWord);
    
    // Keep looping through the words until we've reached the end
    while (nextWord.charAt(nextWord.length()-1) != '.') {
      Vector<String> wordSelection = markovChain.get(nextWord);
      int wordSelectionLen = wordSelection.size();
      nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));
      newPhrase.add(nextWord);
    }
    
    System.out.println("New phrase: " + newPhrase.toString());  
  }

}
