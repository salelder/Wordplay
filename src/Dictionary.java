import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

class Node {
	public Node parent;
	public char ch;
	public int depth; // root is at depth -1. So the depth is the character index in word
	public boolean isWord;
	public Hashtable<Character,Node> edges;
	
	public Node(Node par, char c, int d, boolean isw) {
		parent = par;
		ch = Character.toLowerCase(c);
		depth = d;
		isWord = isw;
		edges = new Hashtable<Character,Node>();
	}
	
	String getWord() {
		StringBuilder sb = new StringBuilder();
		
		Node i = this;
		while (i.parent != null) {
			sb.insert(0, i.ch);
			i = i.parent;
		}
		
		return sb.toString();
	}
}

public class Dictionary {
	Node root;
	String description;
	
	public Dictionary(String des) {
		root = new Node(null, '\0', -1, false);
	}
	
	public void addWordList(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String nextword = br.readLine();
			while (nextword != null) {
				addWord(nextword);
				nextword = br.readLine();
			}
			br.close();
		}
		catch (FileNotFoundException e) { System.out.println("No such file."); }
		catch (IOException e) { System.out.println("File error."); }
		
	}
	
	// May be called multiple times on the same word, with no adverse effects.
	public void addWord(String word) {
		word = word.toLowerCase();
		Node curr = root;
		Node next;
		char ch; // next character
		for (int i = 0; i < word.length(); ++i) {
			ch = word.charAt(i);
			next = curr.edges.get(ch);
			if (next == null) { // no edge for that character
				// make one
				next = new Node(curr, ch, i, false);
				curr.edges.put(ch, next);
			}
			curr = next;
		}
		curr.isWord = true; // mark it as a word
	}
	
	/* want is a combination of any-caps characters and wildcards (spaces). */
	public ArrayList<String> lookup(String want) {
		ArrayList<String> matches = new ArrayList<String>();
		if (want == "") return matches;
		lookup(want.toLowerCase(), root, matches);
		return matches;
	}
	
	/* want is a combination of lowercase characters and wildcards (spaces).
	 * Start at node n and put all matching strings into matches.
	 * Precondition: the n.depth character of want matches n.ch. */
	public void lookup(String want, Node n, ArrayList<String> matches) {
		// do a DFS search
		if (n.depth == want.length()-1) {
			// We've come to the end of our search query.
			if (n.isWord) {
				matches.add(0, n.getWord());
			}
			return; // return either way
		};
		
		char nextChar = want.charAt(n.depth+1);
		if (nextChar == ' ') { // Wildcard. Search all child nodes
			for (Node k : n.edges.values()) {
				lookup(want, k, matches);
			}
		}
		else {
			Node k = n.edges.get(nextChar);
			if (k != null) {
				lookup(want, k, matches);
			}
		}
	}
}
