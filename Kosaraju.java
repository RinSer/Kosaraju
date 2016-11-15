import java.io.*;
import java.util.*;
import java.nio.*;

/**
 * My implementation of Kosaraju algorithm 
 * to find the strongly connected components 
 * in a given graph.
 * 
 * Usage: java [-Xss1000m] Kosaraju [graph_size graph_file]
 *
 * Outputs all the SCCs in a graph with size < 13 nodes or 
 * the five biggest SCCs in a larger one.
 *
 * created by RinSer
 */
public class Kosaraju
{
	public static void main(String[] args)
	{
		int gsize;
		String fpath;
        gsize = 875714;
        fpath = "SCC.txt";
        if (args.length > 1) {
            try {
                gsize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
            fpath = args[1];
        }
        Graph assignment = new Graph(gsize);
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(fpath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			System.out.println("The graph file is being processed");
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the consolepool
				//System.out.println (strLine);
				String[] nodes = strLine.split(" ");
				int tail = Integer.parseInt(nodes[0]);
				int head = Integer.parseInt(nodes[1]);
				assignment.vertices.get(tail-1).addTail(head);
				assignment.vertices.get(head-1).addHead(tail);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		System.out.println("Assignment graph has been created");
        // Run the algorithm
		assignment.Kosaraju();
        // Show the output
		assignment.printSCC();
	}
}

/**
 * Data structure to store the graph data 
 * and run the algorithm.
 */
class Graph {
	
	static int vertices_number;
	static ArrayList<Vertex> vertices;
	static Deque<Vertex> magic_order;
	
	Graph(int n) {
		this.vertices_number = n;
		this.vertices = new ArrayList<Vertex>();
		for (int i = 0; i < n; i++) {
			this.vertices.add(new Vertex(i+1));
		}
		this.magic_order = new ArrayDeque<Vertex>();
	}
	
	Graph(int n, ArrayList<Integer[]> edges) {
		this.vertices_number = n;
		this.vertices = new ArrayList<Vertex>();
		for (int i = 0; i < n; i++) {
			ArrayList<Integer> heads = new ArrayList<Integer>();
			ArrayList<Integer> tails = new ArrayList<Integer>();
			for (int j = 0; j < edges.size(); j++) {
				Integer[] edge = edges.get(j);
				if (edge[0] == i+1) {
					heads.add(edge[1]);
				}
				if (edge[1] == i+1) {
					tails.add(edge[0]);
				}
			}
			Vertex current = new Vertex(i+1, heads, tails);
			this.vertices.add(i, current);
		}
	}
	
	public void addVertex(Vertex v) {
		this.vertices.add(v);
	}
	
	public ArrayList<Vertex> getVertices() {
		return this.vertices;
	}
	
	public int size() {
		return this.vertices_number;
	}
	
    /**
     * Implementation of Kosaraju algorithm with 
     * recursive visit subroutine to find the correct 
     * order and stack-based root assignment part.
     */
	public void Kosaraju() {
		System.out.println("Kosaraju has started");
		System.out.println("Visit subroutine");
		for (int i = this.vertices_number-1; i > -1; i--) {
			Vertex current = this.vertices.get(i);
			if (!current.explored) {
				Visit(current);
			}
		}
		System.out.println("Assign subroutine");
		while (this.magic_order.size() > 0) {
			Vertex current = this.magic_order.pollFirst();
			int root = current.getNumber();
			if (current.root < 0) {
				Deque<Vertex> assign = new ArrayDeque<Vertex>();
				assign.addLast(current);
				while (assign.size() > 0) {
					Vertex node = assign.removeLast();
					if (node.root == -1) {
						node.setRoot(root);
                        ArrayList<Integer> heads = node.Heads();
			            for (int i = 0; i < heads.size(); i++) {
                            if (vertices.get(heads.get(i)-1).root == -1)
				                assign.addLast(getVertex(heads.get(i)));
			            }
					}
				}
			}
		}
	}

    static void Visit(Vertex node) {
		if (!node.explored) {
			node.setExplored();
			ArrayList<Integer> tails = node.Tails();
			for (int i = 0; i < tails.size(); i++) {
				Visit(vertices.get(tails.get(i)-1));
			}
			magic_order.addFirst(node);
		}
	}
	
	/**
     * Implementation of Kosaraju algorithm 
     * for Android AIDE environment.
     * Wargning: buggy!
     */
	public void Kosaraju(String f) {
		System.out.println("Kosaraju has started");
		System.out.println("Visit subroutine");
		for (int i = this.vertices_number-1; i > -1; i--) {
			Vertex current = this.vertices.get(i);
			//System.out.println("Visiting vertice "+current.number);
			if (!current.explored) {
				Deque<Vertex> visit = new ArrayDeque<Vertex>();
				visit.addLast(current);
				while (visit.size() > 0) {
					Vertex node = visit.removeLast();
					if (!node.explored) {
						node.setExplored();
						ArrayList<Integer> tails = node.Tails();
						//for (int j = tails.size()-1; j > -1; j--) {
                        for (int j = 0; j < tails.size(); j++) {
							//System.out.println(tails.get(i));
							visit.addLast(vertices.get(tails.get(j)-1));
						}
						this.magic_order.addLast(node);
					}
				}
			}
		}
		Transpose(f);
		System.out.println("Assign subroutine");
		//for (int i = order.size()-1; i > -1; i--) {
		while (this.magic_order.size() > 0) {
			//System.out.println(this.order);
			//Vertex current = this.vertices.get(this.order.get(i)-1);
			Vertex current = this.magic_order.removeLast();
			//System.out.println(current.number);
			//System.out.println("Assigning vertex "+current.number);
			int root = current.getNumber();
			if (current.root < 0) {
				Deque<Vertex> assign = new ArrayDeque<Vertex>();
				assign.addLast(current);
				while (assign.size() > 0) {
					Vertex node = assign.removeLast();
					if (node.root == -1) {
						node.setRoot(root);
						ArrayList<Integer> tails = node.Tails();
						//for (int j = tails.size()-1; j > -1;  j--) {
						for (int j = 0; j < tails.size(); j++) {
							if (vertices.get(tails.get(j)-1).root == -1)
							    assign.addLast(vertices.get(tails.get(j)-1));
						}
					}
				}
			}
		}
	}
	
	public static void Transpose(String file_path) {
		System.out.println("Transposing the graph");
		// Destroy all arcs
		for (int i = 0; i < vertices_number; i++) {
			vertices.get(i).out = new ArrayList<Integer>();
		}
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(file_path);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			System.out.println("The graph file is being processed");
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				//System.out.println (strLine);
				String[] nodes = strLine.split(" ");
				int tail = Integer.parseInt(nodes[0]);
				int head = Integer.parseInt(nodes[1]);
				vertices.get(head-1).addTail(tail);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	static Vertex getVertex(int n) {
		return vertices.get(n-1);
	}
	
	public void printRoots() {
		for (int i = 0; i < this.vertices.size(); i++) {
			int n = this.vertices.get(i).getNumber();
			int r = this.vertices.get(i).root;
			System.out.println(r+" "+n);
		}
	}
	
	public void printSCC() {
		System.out.println("Computing SCC sizes");
		Map<Integer, Integer> distribution = new HashMap<Integer, Integer>();
		for (int i = 0; i < this.vertices.size(); i++) {
			Vertex node = this.vertices.get(i);
			int root = node.root;
			if (distribution.containsKey(root)) {
				int d = distribution.get(root);
				distribution.put(root, d+1);
			}
			else {
				distribution.put(root, 1);
			}
		}
        if (this.vertices_number < 13) {
		    System.out.println(distribution);
        }
        else {
		    System.out.println("Finding the max 5");
		    int mroot = 0;
	        int msize = 0;
		     Map<Integer, Integer> five = new HashMap<Integer, Integer>();
		     while (five.size() < 5) {
			     for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
				     int v = entry.getValue();
				     if (v > msize) {
					     mroot = entry.getKey();
					     msize = v;
				     }
			     }
			     five.put(mroot, msize);
			     distribution.remove(mroot);
			     msize = 0;
		     }
             System.out.println("5 SCCs with maximum size:");
		     System.out.println(five);
        }
	}
}

/**
 * Data structure to store the data for each node 
 * in the graph.
 */
class Vertex {
	int number;
	ArrayList<Integer> out;
	ArrayList<Integer> in;
	boolean explored;
	int magic;
	int root;
	
	Vertex(int n) {
		this.number = n;
		this.out = new ArrayList<Integer>();
		this.in = new ArrayList<Integer>();
		this.explored = false;
		this.root = -1;
	}
	
	Vertex(int n, ArrayList<Integer> out, ArrayList<Integer> in) {
		this.number = n;
		this.out = out;
		this.in = in;
		this.explored = false;
		this.root = -1;
	}
	
	public void addTail(int n) {
		this.out.add(n);
	}
	
	public void addHead(int n) {
		this.in.add(n);
	}

	public void setExplored() {
		this.explored = true;
	}
	
	public void setRoot(int new_root) {
		this.root = new_root;
	}
	
	public ArrayList<Integer> Tails() {
		return this.out;
	}
	
	public ArrayList<Integer> Heads() {
		return this.in;
	}
	
	public int getNumber() {
		return this.number;
	}
}
