import java.util.*;

public class ShortestPath {
public static Graph.Result dijkstra(Graph graph,int start) {
        for(Graph.Edge edge:graph.edges) if(edge.weight<0) throw new IllegalArgumentException("Negative weight");
        Graph.Result result=new Graph.Result();
        result.dist=new int[graph.n];
        result.prev=new int[graph.n];
        Arrays.fill(result.dist,Integer.MAX_VALUE);
        Arrays.fill(result.prev,-1);
        result.dist[start]=0;
        PriorityQueue<int[]> queue=new PriorityQueue<>((a,b)-> {
            result.ops++;
            result.comparisons++;
            return Integer.compare(a[1],b[1]);
        });
        queue.add(new int[]{start,0});
        while(!queue.isEmpty()) {
            int[] current=queue.poll();
            result.ops++;
            int node=current[0];
            if(current[1]!=result.dist[node]) continue;
            for(Graph.Edge edge:graph.adj.get(node)) {
                result.ops++;
                result.checks++;
                int distance=result.dist[node]+edge.weight;
                if(distance<result.dist[edge.to]) {
                    result.dist[edge.to]=distance;
                    result.prev[edge.to]=node;
                    queue.add(new int[]{edge.to,distance});
                }
            }
        }
        return result;
    }

public static String path(Graph.Result result,int target) {
        if(result.dist[target]==Integer.MAX_VALUE) return "unreachable";
        LinkedList<Integer> route=new LinkedList<>();
        for(int node=target;node!=-1;node=result.prev[node]) route.addFirst(node);
        return route.toString();
    }
}
