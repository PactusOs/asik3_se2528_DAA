import java.util.*;

public class MST {
public static Graph.Result kruskal(Graph graph) {
        Graph.Result result=new Graph.Result();
        DSU dsu=new DSU(graph.n);
        List<Graph.Edge> edges=new ArrayList<>(graph.edges);
        edges.sort((a,b)-> {
            result.ops++;
            result.comparisons++;
            return Integer.compare(a.weight,b.weight);
        });
        int chosen=0;
        for(Graph.Edge edge:edges) {
            result.ops++;
            result.checks++;
            if(dsu.union(edge.from,edge.to)) {
                result.weight+=edge.weight;
                chosen++;
                if(chosen==graph.n-1) break;
            }
        }
        result.dsuOps=dsu.ops;
        result.ops+=dsu.ops;
        result.connected=chosen==graph.n-1;
        return result;
    }

public static Graph.Result prim(Graph graph) {
        Graph.Result result=new Graph.Result();
        boolean[] used=new boolean[graph.n];
        PriorityQueue<Graph.Edge> queue=new PriorityQueue<>((a,b)-> {
            result.ops++;
            result.comparisons++;
            return Integer.compare(a.weight,b.weight);
        });
        int groups=0;
        for(int start=0;start<graph.n;start++) {
            if(used[start]) continue;
            groups++;
            queue.add(new Graph.Edge(start,start,0));
            while(!queue.isEmpty()) {
                Graph.Edge edge=queue.poll();
                result.ops++;
                if(used[edge.to]) continue;
                used[edge.to]=true;
                result.weight+=edge.weight;
                for(Graph.Edge next:graph.adj.get(edge.to)) {
                    result.ops++;
                    result.checks++;
                    if(!used[next.to]) queue.add(next);
                }
            }
        }
        result.connected=groups==1;
        return result;
    }
}
