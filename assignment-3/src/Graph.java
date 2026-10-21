import java.util.*;

public class Graph {
    public int n;
    public boolean directed;
    public List<Edge> edges=new ArrayList<>();
    public List<List<Edge>> adj=new ArrayList<>();

    public Graph(int n,boolean directed) {
        this.n=n;
        this.directed=directed;
        for(int i=0;i<n;i++) adj.add(new ArrayList<>());
    }

    public void add(int from,int to,int weight) {
        Edge edge=new Edge(from,to,weight);
        edges.add(edge);
        adj.get(from).add(edge);
        if(!directed) adj.get(to).add(new Edge(to,from,weight));
    }

    public static class Edge {
        public int from,to,weight;

        public Edge(int from,int to,int weight) {
            this.from=from;
            this.to=to;
            this.weight=weight;
        }
    }

    public static class Result {
        public long weight,ops,checks,comparisons,dsuOps;
        public boolean connected;
        public int[] order,dist,prev;
        public List<List<Integer>> parts;
    }
}
