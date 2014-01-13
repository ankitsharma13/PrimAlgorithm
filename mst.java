import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;


public class mst 
{
	public static void main(String[] arguments) throws FileNotFoundException
	{
		long start,stop,time; //CLOCK TIMER VARIABLES

		//USER INPUT MODE 

		//SIMPLE SCHEME MODE STARTS
		if(arguments[0].equals("-s"))
		{
			System.out.print("SIMPLE SCHEME USER INPUT");
			Graph simpleGraph = new Graph(arguments[1]);
			start=System.currentTimeMillis();
			simpleGraph.simpleSchemeMST();
			stop=System.currentTimeMillis();
			time=stop-start;
			System.out.println("User Input, Simple Scheme time : "+time+" ms");
		}
		//SIMPLE SCHEME MODE ENDS

		//FIBONACCI MODE STARTS
		else if(arguments[0].equals("-f"))
		{
			System.out.print("USER INPUT FIBONACCI SCHEME STARTS");
			Graph fibonaccigraph = new Graph(arguments[1]);
			start=System.currentTimeMillis();
			fibonaccigraph.fibonacciSchemeMST();
			stop=System.currentTimeMillis();
			time=stop-start;
			System.out.println("User Input, Fibonacci Scheme time:	"+time+" ms");
		}
		//FIBONACCI MODE ENDS

		//USER INPUT MODE ENDS


		//RANDOM MODE STARTS
		else if(arguments[0].equals("-r"))
		{
			System.out.println("RANDOM MODE");
			System.out.println("The No. of vertices: "+arguments[1]+"	Density: "+arguments[2]);
			int noOfVertices=Integer.parseInt(arguments[1]);
			int density=Integer.parseInt(arguments[2]);

			//GRAPH GENERATION
			Graph randomGraph=new Graph(noOfVertices,density);

			//SIMPLE SCHEME
			start=System.currentTimeMillis();
			randomGraph.simpleSchemeMST();
			stop=System.currentTimeMillis();
			time=stop-start;
			System.out.println("Random Mode, Simple Scheme time :"+time+" ms");

			//FIBONACCI SCHEME
			start=System.currentTimeMillis();
			randomGraph.fibonacciSchemeMST();
			stop=System.currentTimeMillis();
			time=stop-start;
			System.out.println("Random Mode, Fibonacci Scheme time :"+time+" ms");
		}
		//RANDOM MODE ENDS

		//WRONG INPUT MODE ENTERED
		else
		{
			System.out.println("Please give proper input mode from below");
			System.out.println("Random Mode (-r)");
			System.out.println("Simple Scheme (-s)");
			System.out.println("Fibonacci Scheme (-f)");
		}
	}
}

//GRAPH GENERATION STARTS
class Graph
{
	int loop; // LOOP COUNTER VARIABLE

	//GRAPH VARIABLES
	int presentNode;
	final int numberOfVertices;
	PriorityQueue priorityQueue;
	int minimum_wt;
	Fibonacci fibonacci;
	int c; 
	int numberOfEdges;
	Vertex[] adjacencyList;

	//GRAPH GENERATION FROM USER INPUT FILE
	Graph(String file) throws FileNotFoundException
	{
		Scanner scan = new Scanner(new File(file));
		numberOfVertices=scan.nextInt();
		numberOfEdges=scan.nextInt();
		adjacencyList=new Vertex[numberOfVertices];
		priorityQueue=new PriorityQueue(numberOfEdges);
		fibonacci=new Fibonacci(this);

		//INITIALIZING ADJACENCY LIST
		loop=0;
		while(loop<adjacencyList.length)
		{
			adjacencyList[loop]=new Vertex(loop,null,false);
			loop++;
		}

		while(scan.hasNext())
		{
			int vertex_1=scan.nextInt();
			int vertex_2=scan.nextInt();
			int weight=scan.nextInt();
			adjacencyList[vertex_1].adjacentNode=new AdjacentNode(vertex_2,weight,adjacencyList[vertex_1].adjacentNode);
			adjacencyList[vertex_2].adjacentNode=new AdjacentNode(vertex_1,weight,adjacencyList[vertex_2].adjacentNode);
		}
		scan.close();
	}
	//ENDS

	//GRAPH GENERATION FOR RANDOM MODE
	Graph(int V,int D)
	{
		int edges=0;
		numberOfVertices=V;

		//CALCULATE MAXIMUM POSSIBLE EDGES
		int maxEdges= calculateMaximumEdges(numberOfVertices);

		//CALCULATE MINIMUM NUMBER OF EDGES REQUIRED TO CONNECT THE GRAPH
		int densityVal=D;
		numberOfEdges=calculateRequiredEdges(maxEdges,densityVal);

		//EDGES NOT SUFFICIENT TO CONNECT THE GRAPH
		if(numberOfEdges<numberOfVertices-1)
			throw new IllegalArgumentException("Graph Cannot be created. Given Another input");
		//ENOUGH EDGES TO CONNECT THE GRAPH
		else
			System.out.println("Number of Edges are : "+numberOfEdges);


		//INITIALIZING ADJACENCY LIST
		adjacencyList=new Vertex[numberOfVertices];
		loop=0;
		while(loop<adjacencyList.length)
		{
			adjacencyList[loop]=new Vertex(loop,null,false);
			loop++;
		}

		priorityQueue=new PriorityQueue(numberOfEdges);
		fibonacci=new Fibonacci(this);
		Random randonNumber=new Random();

		while(!depthFirstSearch(this))
		{
			int weight=randonNumber.nextInt(100000)+1;;
			int vertex_1=randonNumber.nextInt(numberOfVertices);
			int vertex_2=randonNumber.nextInt(numberOfVertices);
			while(vertex_1==vertex_2)
			{
				vertex_1=randonNumber.nextInt(numberOfVertices);
			}

			adjacencyList[vertex_1].adjacentNode=new AdjacentNode(vertex_2,weight,adjacencyList[vertex_1].adjacentNode);
			adjacencyList[vertex_2].adjacentNode=new AdjacentNode(vertex_1,weight,adjacencyList[vertex_2].adjacentNode);
			edges=edges+1;

			while( edges!=numberOfEdges)
			{
				vertex_1=randonNumber.nextInt(numberOfVertices);
				vertex_2= randonNumber.nextInt(numberOfVertices);
				while(vertex_1==vertex_2 && existing(this,vertex_1,vertex_2))
				{
					vertex_2= randonNumber.nextInt(numberOfVertices);
				}
				weight=randonNumber.nextInt(1000)+1;
				adjacencyList[vertex_1].adjacentNode=new AdjacentNode(vertex_2,weight,adjacencyList[vertex_1].adjacentNode);
				adjacencyList[vertex_2].adjacentNode=new AdjacentNode(vertex_1,weight,adjacencyList[vertex_2].adjacentNode);
				edges++;
			}
		}
	}

	//GRAPH GENERATION FOR RANDOM MODE ENDS

	//AUXILARY FUNCTION

	//CALCULATE MAX EDGES
	int calculateMaximumEdges(int V)
	{
		return (V*(V-1))/2;
	}

	//CALCULATE REQUIRED EDGES
	int calculateRequiredEdges(int e, int d)
	{
		return ((e*d)/100);
	}

	boolean existing(Graph graph,int vertex_1,int vertex_2)
	{
		AdjacentNode present;
		present=graph.adjacencyList[vertex_1].adjacentNode;

		while (adjacencyList[vertex_1].adjacentNode!=null && present!=null)
		{	
			if(present.vertexId==vertex_2)
				return true;
			present=present.nextNode;
		}

		present=graph.adjacencyList[vertex_2].adjacentNode;

		while (adjacencyList[vertex_2].adjacentNode!=null && present!=null)
		{	
			if(present.vertexId==vertex_1)
				return true;

			present=present.nextNode;
		}
		return false;
	}

	//CALCULATE ARRAY SIZE
	/*public int calculateArraySize(int n,double d)
	{
		return (((int)Math.floor(Math.log(n)*d))+1);
	}*/
	//AUXILIARY FUNCTION ENDS

	//DEPTH FIRST SEARCH
	boolean depthFirstSearch(Graph graph)
	{
		Stack<Integer> stack= new Stack<Integer>();
		int [] checked= new int [graph.numberOfVertices];
		int loop =0; //Loop Variable

		while(loop<checked.length)
		{
			checked[loop]=-1;
			loop++;
		}
		stack.push(0);
		while(!stack.isEmpty())
		{
			int pop=stack.pop();
			if(checked[pop]==-1)
			{
				checked[pop]=1;
			}

			AdjacentNode adjacentNode= graph.adjacencyList[pop].adjacentNode;
			while(adjacentNode!=null)
			{
				if(checked[adjacentNode.vertexId]==-1)
				{
					stack.push(adjacentNode.vertexId);
				}
				adjacentNode=adjacentNode.nextNode;
			}
		}

		int count=0;
		loop=0;
		while(loop<checked.length)
		{
			if(checked[loop]==1)
			{
				count++;
			}
			loop++;
		}

		if(count==graph.numberOfVertices)
		{
			System.out.println("It is a Connected Graph.");
			return true;
		}
		else
		{
			return false;
		}
	}
	//DEPTH FIRST SEARCH ENDS

	//SIMPLE SCHEME FOR MST STARTS
	void simpleSchemeMST()
	{
		int loop=0;
		presentNode=0;
		while(c<adjacencyList.length-1) 
		{
			adjacencyList[presentNode].in=true;
			c++;

			for (AdjacentNode adjacentNode=adjacencyList[presentNode].adjacentNode;adjacentNode!=null;adjacentNode=adjacentNode.nextNode)
			{
				if(adjacencyList[adjacentNode.vertexId].in)
					continue;

				if(adjacencyList[adjacentNode.vertexId]==adjacencyList[presentNode])
					continue;

				int weight=adjacentNode.weight;
				insertPQ(adjacentNode.vertexId, weight);
			}

			if(priorityQueue.size==0)
			{
				System.out.println("GRAPH NOT CONNECTED");
				return;
			}

			edges theEdge = priorityQueue.removeMinimum();
			presentNode = theEdge.dVertex;
			minimum_wt+=theEdge.weight;
		}
		System.out.println("Simple Schemese Cost of MST : "+minimum_wt);

		//RESETTING BACK
		loop=0;
		while(loop<adjacencyList.length)
		{
			adjacencyList[loop].in = false;
			loop++;
		}
	}	
	//	SIMPLE SCHEME MST ENDS

	//INSERT A VERTEX IN PRIORITY QUEUE
	void insertPQ(int V,int W)
	{
		int qindex=priorityQueue.find(V);
		if(qindex!=-1)
		{
			edges tempedge=priorityQueue.peekN(qindex);
			int old_wt=tempedge.weight;
			if(old_wt>W)
			{
				priorityQueue.removeN(qindex);
				edges theEdge=new edges(presentNode,V,W);
				priorityQueue.insert(theEdge);
			}
		}
		else
		{
			edges theEdge=new edges(presentNode,V,W);
			priorityQueue.insert(theEdge);
		}
	}

	//FIBONACCI SCHEME FOR MST STARTS
	void fibonacciSchemeMST()
	{
		int [] vertexArr= new int[numberOfVertices];	//	ARRAY OF VERTEX
		int [] cost= new int[numberOfVertices];	//	 COST
		int[] final_cost= new int[numberOfVertices];	//	FINAL COST
		FibonacciHeapNode [] secondary= new FibonacciHeapNode[this.numberOfVertices];	//	SECONDARY ARRAY FOR REFERENCE

		ArrayList<Integer> travel= new ArrayList<>();
		vertexArr[0]=0;
		FibonacciHeapNode node= new FibonacciHeapNode(0, 0);
		secondary[0]= this.fibonacci.insert(node, 0);

		int loop=0;
		while(loop<numberOfVertices)
		{
			node=new FibonacciHeapNode(loop+1, 99999);
			secondary[loop]= fibonacci.insert(node, 99999);
			loop++;
		} 

		loop=0;
		while(loop<numberOfVertices)
		{
			FibonacciHeapNode a= fibonacci.removeMin();
			if(a.data==0)
				travel.add(a.data);
			else
				travel.add(a.data-1);
			final_cost[loop]=(int)a.key;

			AdjacentNode j=this.adjacencyList[loop].adjacentNode;
			while(j !=null)
			{
				if(!travel.contains(j.vertexId))
				{
					cost[j.vertexId]=j.weight;
					vertexArr[j.vertexId]=a.data;
					fibonacci.decreaseKey(secondary[j.vertexId], j.weight);
				}
				else if (j.vertexId==0)
				{
					if(secondary[j.vertexId].key==99999.0)
						secondary[j.vertexId].key=j.weight;
				}

				j=j.nextNode;
			}
			loop++;
		}

		int sumOfCost=0;
		loop=0;
		while(loop<final_cost.length)
		{
			sumOfCost+=final_cost[loop];
			loop++;
		}
		System.out.println("Fibonacci cost of MST : "+sumOfCost);
	}
}


class Vertex
{
	int vertexName;
	AdjacentNode adjacentNode;
	boolean in=false;
	boolean visited=false;

	Vertex(int name,AdjacentNode adjNode, boolean a)
	{
		vertexName=name;
		adjacentNode=adjNode;
		in=false;
		this.visited=a;
	}

	void displayVertex()
	{
		System.out.print(this.vertexName);
	}

	void displayAdjacentNode(int name)
	{
		AdjacentNode present;
		present=adjacentNode;
		int count=0;
		while(present!=null)
		{
			System.out.print(present.vertexId+",");
			present=present.nextNode;
			count+=1;
		}
	}
}

class AdjacentNode
{
	int vertexId;
	int weight;
	AdjacentNode nextNode;

	AdjacentNode(int id,int wt,AdjacentNode n)
	{
		vertexId=id;
		weight=wt;
		nextNode=n;
	}	
}

class edges
{
	int sVertex;
	int dVertex;
	int weight;
	edges(int s,int d,int wt)
	{
		sVertex=s;
		dVertex=d;
		weight=wt;
	}
}

class PriorityQueue
{
	edges[] pqArray;
	int size;
	PriorityQueue(int size)
	{
		pqArray=new edges[size];
		this.size=0;
	}
	void insert(edges item)
	{
		int j;

		for (j=0;j<size;j++)
			if(item.weight>=pqArray[j].weight)
				break;

		for (int k=size-1;k>=j;k--)
			pqArray[k+1]=pqArray[k];

		pqArray[j]=item;
		size++;
	}
	edges removeMinimum()
	{
		return pqArray[--size];
	}
	void removeN(int n)
	{
		for(int j=n;j<size-1;j++)
			pqArray[j]=pqArray[j+1];
		size--;
	}
	edges peekMin() 
	{ 
		return pqArray[size-1]; 
	}
	int size() 
	{ 
		return size; 
	}
	boolean isEmpty()
	{ 
		return (size==0); 
	}
	edges peekN(int n)
	{
		return pqArray[n]; 
	}
	int find(int findDex) 
	{
		int loop=0;
		while(loop<size)
		{
			if(pqArray[loop].dVertex == findDex)
				return loop;
			loop++;
		}
		return -1;
	}
}

class Fibonacci 
{
	public Fibonacci(Graph g)
	{
	}
	private FibonacciHeapNode minNode;
	private int nNodes;
	public boolean isEmpty()
	{
		return minNode==null;
	}
	public void clear()
	{
		minNode=null;
		nNodes=0;
	}
	double fibonacciVariableArray=1.0/Math.log((1.0+Math.sqrt(5.0))/2.0);
	public FibonacciHeapNode insert(FibonacciHeapNode node,double key)
	{
		node.key=key;
		if(minNode!=null)
		{
			node.left = minNode;
			node.right = minNode.right;
			minNode.right = node;
			node.right.left = node;
			if (key < minNode.key)
			{
				minNode = node;
			}
		}
		else
		{
			minNode = node;
		}
		nNodes++;
		return node;
	}
	public FibonacciHeapNode min()
	{
		return minNode;
	}
	public int size()
	{
		return nNodes;
	}
	void link(FibonacciHeapNode y, FibonacciHeapNode x)
	{
		y.left.right = y.right;
		y.right.left = y.left;
		y.parent = x;
		if (x.child == null)
		{
			x.child = y;
			y.right = y;
			y.left = y;
		} 
		else
		{
			y.left = x.child;
			y.right = x.child.right;
			x.child.right = y;
			y.right.left = y;
		}
		x.degree++;
		y.mark = false;
	}
	public FibonacciHeapNode removeMin()
	{
		FibonacciHeapNode z = minNode;
		if (z != null)
		{
			int numKids = z.degree;
			FibonacciHeapNode x = z.child;
			FibonacciHeapNode tempRight;
			while (numKids > 0) 
			{
				tempRight = x.right;
				x.left.right = x.right;
				x.right.left = x.left;
				x.left = minNode;
				x.right = minNode.right;
				minNode.right = x;
				x.right.left = x;
				x.parent = null;
				x = tempRight;
				numKids--;
			}
			z.left.right = z.right;
			z.right.left = z.left;
			if (z == z.right)
			{
				minNode = null;
			} 
			else 
			{
				minNode = z.right;
				consolidate();
			}
			nNodes--;
		}
		return z;
	}

	void consolidate()
	{
		int arraySize=((int)Math.floor(Math.log(nNodes)*fibonacciVariableArray))+1;
		ArrayList<FibonacciHeapNode> array =new ArrayList<FibonacciHeapNode>(arraySize);
		int loop=0;
		while(loop<arraySize)
		{
			array.add(null);
			loop++;
		}

		int numRoots = 0;
		FibonacciHeapNode x = minNode;

		if (x != null)
		{
			numRoots++;
			x = x.right;
			while (x != minNode) 
			{
				numRoots++;
				x = x.right;
			}
		}
		while (numRoots > 0)
		{
			int degree = x.degree;
			FibonacciHeapNode nextNode = x.right;
			for (;;)
			{
				FibonacciHeapNode y = array.get(degree);
				if (y == null)
				{
					break;
				}
				if (x.key > y.key)
				{
					FibonacciHeapNode temp = y;
					y = x;
					x = temp;
				}
				link(y, x);
				array.set(degree, null);
				degree++;
			}
			array.set(degree, x);
			x = nextNode;
			numRoots--;
		}
		minNode = null;
		for (int i=0;i<arraySize;i++)
		{
			FibonacciHeapNode y = array.get(i);
			if (y == null)
			{
				continue;
			}
			if (minNode != null) 
			{
				y.left.right = y.right;
				y.right.left = y.left;
				y.left = minNode;
				y.right = minNode.right;
				minNode.right = y;
				y.right.left = y;
				if (y.key < minNode.key) 
				{
					minNode = y;
				}
			} 
			else 
			{
				minNode = y;
			}
		}
	}

	public String toString()
	{
		if (minNode==null)
		{
			return "FibonacciHeap=[]";
		}
		Stack<FibonacciHeapNode> stack= new Stack<FibonacciHeapNode>();
		stack.push(minNode);
		StringBuffer buffer=new StringBuffer(1024);
		buffer.append("FibonacciHeap=[");
		while (!stack.empty())
		{
			FibonacciHeapNode current=stack.pop();
			buffer.append(current);
			buffer.append(", ");

			if (current.child != null)
			{
				stack.push(current.child);
			}
			FibonacciHeapNode start=current;
			current=current.right;
			while (current != start)
			{
				buffer.append(current);
				buffer.append(", ");

				if (current.child != null)
				{
					stack.push(current.child);
				}
				current=current.right;
			}
		}
		buffer.append(']');
		return buffer.toString();
	}
	protected void cascadingCut(FibonacciHeapNode nodeY)
	{
		FibonacciHeapNode nodeZ=nodeY.parent;
		if (nodeZ != null)
		{
			if (!nodeY.mark)
			{
				nodeY.mark=true;
			}
			else 
			{
				cut(nodeY,nodeZ);
				cascadingCut(nodeZ);
			}
		}
	}
	public void decreaseKey(FibonacciHeapNode node,double d)
	{
		if(d>node.key)
		{
			return;
		}
		node.key=d;
		FibonacciHeapNode node2=node.parent;
		if((node2!=null) && (node.key<node2.key)) 
		{
			cut(node,node2);
			cascadingCut(node2);
		}
		if (node.key<minNode.key) {
			minNode=node;
		}
	}


	void cut(FibonacciHeapNode node1, FibonacciHeapNode node2)
	{
		node1.left.right = node1.right;
		node1.right.left = node1.left;
		node2.degree--;
		if (node2.child == node1)
		{
			node2.child = node1.right;
		}
		if (node2.degree == 0) {
			node2.child = null;
		}
		node1.left = minNode;
		node1.right = minNode.right;
		minNode.right = node1;
		node1.right.left = node1;
		node1.parent = null;
		node1.mark = false;
	}
}

class FibonacciHeapNode
{
	int data;
	FibonacciHeapNode child;
	FibonacciHeapNode left;
	FibonacciHeapNode right;
	FibonacciHeapNode parent;
	boolean mark;
	double key;
	int degree;
	public  FibonacciHeapNode(int d,double k)
	{
		right=this;
		left=this;
		data= d;
		key= k;
	}
	public final double getKey()
	{
		return key;
	}
	public final int getData()
	{
		return data;
	}
}

