import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {

	/*
	 * Klasa do przechowywania informacji o wezle w grafie na kracie liczb calkowitych
	 */
	public static class Node {
		//zmienne do przechowywania sasiadow danego wezla (gora dol lewo prawo)
		Node north;
		Node south;
		Node west;
		Node east;
		//zmienne przechowujace wspolrzedne wezla na kracie liczb calkowitych (od 0 do rozmiarLabiryntu-1)
		int i;
		int j;
		//zmienna do zaznaczenia, ze wezel zostal juz przeszukany w ramach operacji dfs
		boolean visited = false;
		//zmienna do zaznaczenia, ze wezel zostal juz dodany do kolejki w ramach operacji bfs
		boolean queued = false;
		//to samo co north south east west, przechowuje tylko 4 elementy w tablicy - poczawszy od north, east, south, west
		Node neighbours[];
		//zmienna tablicowa rozmiaru 4 przechowujaca korytarze z danego wezla, jesli element tablicy jest rowny null, to nie ma korytarza do tego sasiada
		//kolejnosc jest taka sama jak w przypadku neighbours = north east south west
		Node connections[];
		/*
		 * Tu jest mnostwo automatycznie wyegenerowanych getterow i setterow (w Eclipse jest taka opcja)
		 */
		public boolean isQueued() {
			return queued;
		}
		public void setQueued(boolean queued) {
			this.queued = queued;
		}
		public int getI() {
			return i;
		}
		public void setI(int i) {
			this.i = i;
		}
		public int getJ() {
			return j;
		}
		public void setJ(int j) {
			this.j = j;
		}
		public boolean isVisited() {
			return visited;
		}
		public void setVisited(boolean visited) {
			this.visited = visited;
		}
		public Node getNorth() {
			return north;
		}
		public void setNorth(Node north) {
			this.north = north;
		}
		public Node getSouth() {
			return south;
		}
		public void setSouth(Node south) {
			this.south = south;
		}
		public Node getWest() {
			return west;
		}
		public void setWest(Node west) {
			this.west = west;
		}
		public Node getEast() {
			return east;
		}
		public void setEast(Node east) {
			this.east = east;
		}
		public Node[] getConnections() {
			return connections;
		}
		public void setConnections(Node[] connections) {
			this.connections = connections;
		}
		public Node(Node north, Node south, Node west, Node east) {
			super();
			this.north = north;
			this.south = south;
			this.west = west;
			this.east = east;
			this.neighbours = new Node[4];
			this.neighbours[0] = this.north;
			this.neighbours[1] = this.west;
			this.neighbours[2] = this.south;
			this.neighbours[3] = this.east;
			this.connections = new Node[4];
			this.connections[0] = null;
			this.connections[1] = null;
			this.connections[2] = null;
			this.connections[3] = null;
		}
		public Node() {
			this.north = this.south = this.west = this.east = null;
			this.neighbours = new Node[4];
			this.neighbours[0] = null;
			this.neighbours[1] = null;
			this.neighbours[2] = null;
			this.neighbours[3] = null;
			this.connections = new Node[4];
			this.connections[0] = null;
			this.connections[1] = null;
			this.connections[2] = null;
			this.connections[3] = null;
		}
		/*
		 * Ta metoda jest uzywana w trakcie konstruowania grafu opartego na kracie licz calkowitych 0 .. size-1 x 0 .. size -1
		 * 
		 */
		public void setNeighbours(Node[][] maze, int size, int i, int j) {
			this.i = i;
			this.j = j;
			//przetwarzany wezel kraty (this) ma ustawianych sasiadow north south east west o ile nie jest na granicy kraty
			this.north = (i>0) ? (maze[i-1][j]) : (null);
			this.south = (i<size-1) ? (maze[i+1][j]) : (null);
			this.east = (j>0) ? (maze[i][j-1]) : (null);
			this.west = (j<size-1) ? (maze[i][j+1]) : (null);		
			//ta sama informacja jest przechowywana w ramach tablicy neighbours
			this.neighbours = new Node[4];
			this.neighbours[0] = this.north;
			this.neighbours[1] = this.west;
			this.neighbours[2] = this.south;
			this.neighbours[3] = this.east;
		}
		/*
		 * Funkcja sprawdza czy wezel ma jeszcze nieodwiedzonych sasiadow w ramach procedury dfs
		 */
		public boolean hasNonvisitedNeighbours() {
			for(int i = 0; i < 4; ++i) {
				if(this.neighbours[i] != null && this.neighbours[i].isVisited() == false) {
					return true;
				}
			}
			return false;
		}
		/*
		 * Funkcja sprawdza czy wezel ma jeszce nie zakolejkowanych sasiadow w ramach procedury bfs
		 */
		public boolean hasNonqueuedNeighbours() {
			for(int i = 0; i < 4; ++i) {
				if(this.neighbours[i] != null && this.neighbours[i].isQueued() == false) {
					return true;
				}
			}
			return false;
		}
		/*
		 * Funkcja pobiera losowego jeszcze nie odwiedzonego sasiada wezla w ramach dfs
		 */
		public Node getRandomNonvisitedNeighbour(Random r) throws Exception {
			if(this.hasNonvisitedNeighbours() == false) {
				throw new Exception("No nonvisited neighbours.");
			}
			while(true) {
				int n = r.nextInt(4);
				if(this.neighbours[n] != null && this.neighbours[n].isVisited() == false) {
					return this.neighbours[n];
				}
			}
		}
		/*
		 * Funkcja pobiera losowego jeszcze nie zakolejkowanego sasiada w ramach bfs
		 */
		public Node getRandomNonqueuedNeighbour(Random r) throws Exception {
			if(this.hasNonqueuedNeighbours() == false) {
				throw new Exception("No nonvisited neighbours.");
			}
			while(true) {
				int n = r.nextInt(4);
				if(this.neighbours[n] != null && this.neighbours[n].isQueued() == false) {
					return this.neighbours[n];
				}
			}
		}
		/*
		 * To jest wazna funkcja ktora tworzy korytarz od wezla do wezla przekazanego jako parametr.
		 * Informacja jest umieszczana w zmiennej connections (jej wartosc jest zmieniana z null na wezel nb)
		 */
		public void removeWall(Node nb) {
			if(nb == this.north) {
				this.connections[0] = nb;
			}
			if(nb == this.west) {
				this.connections[1] = nb;
			}
			if(nb == this.south) {
				this.connections[2] = nb;
			}
			if(nb == this.east) {
				this.connections[3] = nb;
			}
		}
	}
	
	//generator liczb losowych
	public static Random r = new Random();
	
	/*
	 * Ta procedura generuje labirynt z uzyciem rekurencji (sama siebie wywoluje) z uzyciem algorytmu dfs
	 * Procedura zaczyna generowanie labiryntu od wezla o numerach i x j na kracie liczb calkowitych - w main jest to 0x0 (lewy gorny rog labiryntu)
	 */
	public static void generateMaze(Node maze[][], int size, int i, int j) throws Exception {
		Node nd = maze[i][j];
		//zgodnie z dfs zaznaczamy, ze aktualny wezel jest odwiedzony
		nd.setVisited(true);
		//i dopoki wezel ma jeszcze nieodwiedzonych sasiadow (to beda north south east west)
		while(nd.hasNonvisitedNeighbours()) {
			//losujemy takiego sasiada
			Node nb = nd.getRandomNonvisitedNeighbour(r);
			//zaznaczamy, ze jest miedzy nimi korytarz
			nd.removeWall(nb);
			if(nb != null) nb.removeWall(nd);
			//i wywouljemy funkcje rekurencyjnie dla tego sasiada - to jest wlasnie wywolanie wglab dfs
			generateMaze(maze, size, nb.getI(), nb.getJ());
		}
	}
	
	/*
	 * Ta funkcja tez dziala w oparciu o dfs, ale uzywa wlasnego stosu, a nie stosu wywolan funkcji jak poprzednia
	 * dzieki temu jest dostepne wiecej pamieci i mozna przy pomocy tej funkcji generowac labirytny o wiekszym rozmiarze
	 */
	public static void generateMazeListDFS(Node maze[][], int size, int i, int j) throws Exception {
		//lista list sluzy jako stos dla procedury dfs
		List<Node> list = new ArrayList<Node>();
		//dodajemy wezel Node, od ktorego zaczynamy generowanie labirytnu
		list.add(maze[i][j]);
		
		Label:
		//i dopoki cos jest na stosie, powtarzamy:
		while(!list.isEmpty()) {
			//bierzemy wierzcholek ze szczytu stosu
			Node node = list.get(0);
			//zaznaczamy go jako odwiedzony
			node.setVisited(true);
			//i jesli ma nieodwiedzonych sasiadow
			if(node.hasNonvisitedNeighbours()) {
				//to losujemy takiego sasiada
				Node nb = node.getRandomNonvisitedNeighbour(r);
				//tworzymy do tego sasiada korytarz w labiryncie
				node.removeWall(nb);
				if(nb != null) nb.removeWall(node);
				//i dodajemy tego sasiada na poczatek listy - szczyt stosu
				list.add(0, nb);
				continue Label;
			} else {
				//jesli wezel nie ma juz nieodwiedzonych sasiadow, to go usuwamy ze stosu
				list.remove(0);
			}
		}
	}
	
	/*
	 * Ta funkcja generuje labirytn z uzyciem bfs
	 */
	public static void generateMazeListBFS(Node maze[][], int size, int i, int j) throws Exception {
		//lista list sluzy jako lista wezlow juz dolaczonych do labiryntu
		List<Node> list = new ArrayList<Node>();
		//na poczatku dodajemy do listy wezel startowy ixj
		list.add(maze[i][j]);
		//zaznaczamy ze jest juz zakolejkowany
		maze[i][j].setQueued(true);
		
		//i dopoki cos jest na liscie
		Label:
		while(!list.isEmpty()) {
			//bierzemy pierwszy element listy i 
			Node node = list.get(0);
			//i dopiki ma niezakolejkowanych jeszcze sasiadow
			while(node.hasNonqueuedNeighbours()) {
				//bierzemy losowego z nich
				Node nb = node.getRandomNonqueuedNeighbour(r);
				//zaznaczamy ze jest miedzy nimi korytarz
				node.removeWall(nb);
				if(nb != null) nb.removeWall(node);
				//tutaj dodajemy tego sasiada na losowe miejsce do przetwarzania na liscie, bo inaczej jak dodajemy na koniec listy to wygenerowany labirytm jest za prosty
				list.add(r.nextInt((list.size()-1)+1), nb);
				//zaznaczamy ze ten sasiad jest zakolejkowany
				nb.setQueued(true);
			} 
			//po dodaniu wszystkich sasiadow wezla usuwamy go z poczatku kolejki list
			list.remove(0);
		}
	}
	
	//pomocnicza funkcja zeby za duzo razy nie pisac System.out.println
	public static void p(String s, PrintWriter writer) {
		System.out.print(s);
		writer.print(s);
	}
	
	/*
	 * Ta funkcja sluzy do wypisania labiryntu na kosnole i do pliku maze.txt
	 * do tej funkcji doszedlem metoda prob i bledow, ciezko mi ja opisac
	 * + oznacza naroznik | - oznaczaja brak przejscia - scianek
	 * # oznacza komnate/korytarz
	 */
	public static void printMaze(Node maze[][], int size) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("maze.txt");
		p("+", writer);
		p("#", writer);
		for(int j = 1; j < size; ++j) {
			if(maze[0][j].getConnections()[0] == null) {
				p("-", writer);
			} else {
				p("#", writer);
			}
			p("+", writer);
		}
		p("\r\n", writer);
		for(int i = 0; i < size; ++i) {
			p("|", writer);
			for(int j = 0; j < size; ++j) { 
				p("#", writer);
				if(maze[i][j].getConnections()[1] == null) {
					p("|", writer);
				} else {
					p("#", writer);
				}
			}
			p("\r\n", writer);
			p("+", writer);
			for(int j = 0; j < size-1; ++j) {
				if(maze[i][j].getConnections()[2] == null) {
					p("-", writer);
				} else {
					p("#", writer);
				}
				p("+", writer);
			}
			p("#", writer);
			p("+", writer);
			p("\r\n", writer);
		}
		writer.close();
	}
	
	public static void main(String[] args) throws Exception {
		
		//Pobranie rozmiaru labiryntu
		System.out.println("Podaj rozmiar labirytnu: ");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String s = bufferRead.readLine();
		int size = Integer.parseInt(s);
		System.out.println("Podaj metode (1-DFS 2-BfS):");
		s = bufferRead.readLine();
		int metoda = Integer.parseInt(s);
		
		//tutaj tworzymy krate liczb calkowitych 0..size-1 x 0..size-1 i w kazdym miejscu kraty wstawiamy obiekt Node
		Node[][] maze = new Node[size][size];
		for(int i = 0; i < size; ++i) {
			for(int j = 0; j < size; ++j) {
				maze[i][j] = new Node();
			}
		}		
		//jak juz mamy powstawiane obiekty Node, to budujemy strukture grafu - kazdy wezel ma ustawiane wewnetrzne zmienne
		//neighbours, north, south, west, east na odpiwednich swoich sasiadow w kracie
		for(int i = 0; i < size; ++i) {
			for(int j = 0; j < size; ++j) {
				maze[i][j].setNeighbours(maze, size, i, j);
			}
		}
		
		long start = System.currentTimeMillis();
		
		//Do wyboru funkcja generujaca labirynt
		switch(metoda) {
			case 1: {
				generateMazeListDFS(maze, size, 0, 0);	//dla duzych, ale dla powyzej 500 dziala dlugo, podstawowa funkcja do generowania w tym programie	
				long end = System.currentTimeMillis();
				printMaze(maze, size);
				System.out.println("Generowanie labiryntu DFS zajelo " +  (end - start) + " milisekdun.");	
			} break;
			case 2: {
				generateMazeListBFS(maze, size, 0, 0);	//dorzucony bfs
				long end = System.currentTimeMillis();
				printMaze(maze, size);
				System.out.println("Generowanie labiryntu BFS zajelo " +  (end - start) + " milisekdun.");	
			} break;
			default: {
				System.out.println("Zly numer metody");
			}
		}	

	}

}
