import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class WordMorphs {
	// When you look up a word, you get references to all the words it is
	// a one-step morph of. As long as we're not making copies of strings
	// between key and values, there should only be one string in memory per word.
	HashMap<String, HashSet<String>> graph;
	Dictionary dict;	
	
	public WordMorphs(String seed, Dictionary dict) {
		this.dict = dict;
		dict.addWord(seed); // just in case!
		graph = new HashMap<String, HashSet<String>>();
		insert(seed);
	}
	
	/* Recursively add word and all its descendants.
	 * Go until the entire equivalence class has been filled.
	 * Make sure every word is connected directly to each
	 * one-step neighbor in the graph. */
	public void insert(String word) {
		if (!graph.containsKey(word)) graph.put(word, new HashSet<String>());
		for (int k = 0; k < word.length(); ++k) { // loop over letter positions
			String query = word.substring(0, k) + ' ' + word.substring(k+1);
			ArrayList<String> neighbors = dict.lookup(query);
			for (String n : neighbors) {
				if (!graph.containsKey(n)) {
					graph.put(n, new HashSet<String>());
					insert(n);
				}
				
				// ensure connections even if we've already seen this word
				graph.get(n).add(word);
				graph.get(word).add(n);
			}
		}
	}
	
	public boolean contains(String word) {
		return graph.containsKey(word);
	}
	
	public void printAll() {
		for (String key : graph.keySet()) {
			System.out.println(key);
		}
	}
	
	public int getSize() {
		return graph.keySet().size();
	}
	
	/* Assumes strings are already in the dists hashmap!!
	 * Don't put a string into the pqueue until AFTER
	 * setting the distance. */
	private class StrCmp implements Comparator<String>{
		HashMap<String,Integer> dists;
		public StrCmp(HashMap<String,Integer> d) {
			dists = d;
		}
		public int compare(String s1, String s2) {
			return dists.get(s1) - dists.get(s2);
		}
	}
	
	public ArrayList<String> shortestPath(String source, String dest) {
		ArrayList<String> res = new ArrayList<String>();
		if (!graph.containsKey(source) || !graph.containsKey(dest)) return res;
		// at this point, assume both are present.
		HashMap<String, Integer> dists = new HashMap<String, Integer>();
		HashMap<String, String> prev = new HashMap<String, String>();
		HashSet<String> visited = new HashSet<String>();
		Comparator<String> cmp = new StrCmp(dists);
		
		dists.put(source, 0);
		PriorityQueue<String> frontier = new PriorityQueue<String>(cmp);
		
		String curr = source;
		while (curr != null && curr != dest) {
			visited.add(curr); // the newest visited node!
			int d = dists.get(curr);
			// process all its neighbors
			for (String n : graph.get(curr)) {
				if (!dists.containsKey(n)) {
					// new node, not yet explored!
					dists.put(n, d + 1);
					prev.put(n, curr);
					frontier.add(n);
				}
				else {
					// this node already has a distance
					if (d + 1 < dists.get(n)) {
						// need to update its distance with a shorter way
						prev.put(n, curr);
						// update the pqueue as appropriate
						frontier.remove(n);
						dists.put(n, d+1);
						frontier.add(n);
					}
				}
			}
			curr = frontier.poll();
		}
		// we have processed the shortest path!
		String p = dest;
		while (p != null) {
			res.add(0, p);
			p = prev.get(p);
		}
		return res;
	}
	
	public String shortestPathString(String source, String dest) {
		ArrayList<String> path = shortestPath(source, dest);
		if (path.size() == 0) return "No path found.";
		StringBuilder sb = new StringBuilder();
		for (String s : path) {
			sb.append(s); sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}
}
