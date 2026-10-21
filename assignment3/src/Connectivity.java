import java.util.*;

public class Connectivity {
public static Graph.Result topological(Graph graph) {
        Graph.Result result=new Graph.Result();
        int[] degree=new int[graph.n];
        for(Graph.Edge edge:graph.edges) {
            degree[edge.to]++;
            result.ops++;
            result.checks++;
        }
        Queue<Integer> queue=new ArrayDeque<>();
        for(int i=0;i<graph.n;i++) if(degree[i]==0) queue.add(i);
        int[] order=new int[graph.n];
        int count=0;
        while(!queue.isEmpty()) {
            int node=queue.poll();
            result.ops++;
            order[count++]=node;
            for(Graph.Edge edge:graph.adj.get(node)) {
                result.ops++;
                result.checks++;
                if(--degree[edge.to]==0) queue.add(edge.to);
            }
        }
        if(count==graph.n) result.order=order;
        return result;
    }

static void dfs1(Graph graph,int node,boolean[] used,List<Integer> order,Graph.Result result) {
        used[node]=true;
        for(Graph.Edge edge:graph.adj.get(node)) {
            result.ops++;
            result.checks++;
            if(!used[edge.to]) dfs1(graph,edge.to,used,order,result);
        }
        order.add(node);
    }

static void dfs2(Graph graph,int node,boolean[] used,List<Integer> part,Graph.Result result) {
        used[node]=true;
        part.add(node);
        for(Graph.Edge edge:graph.adj.get(node)) {
            result.ops++;
            result.checks++;
            if(!used[edge.to]) dfs2(graph,edge.to,used,part,result);
        }
    }

public static Graph.Result scc(Graph graph) {
        Graph.Result result=new Graph.Result();
        boolean[] used=new boolean[graph.n];
        List<Integer> order=new ArrayList<>();
        for(int i=0;i<graph.n;i++) if(!used[i]) dfs1(graph,i,used,order,result);
        Graph reversed=new Graph(graph.n,true);
        for(Graph.Edge edge:graph.edges) {
            reversed.add(edge.to,edge.from,edge.weight);
            result.ops++;
            result.checks++;
        }
        Arrays.fill(used,false);
        result.parts=new ArrayList<>();
        for(int i=order.size()-1;i>=0;i--) {
            int node=order.get(i);
            if(!used[node]) {
                List<Integer> part=new ArrayList<>();
                dfs2(reversed,node,used,part,result);
                result.parts.add(part);
            }
        }
        return result;
    }
}
