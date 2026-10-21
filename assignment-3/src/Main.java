import java.util.*;

public class Main {
    static Graph randomGraph(int n,int e,boolean directed,boolean dag,long seed) {
        Graph graph=new Graph(n,directed);
        Random random=new Random(seed);
        Set<Long> seen=new HashSet<>();
        if(!dag) {
            for(int i=1;i<n;i++) addUnique(graph,i-1,i,random.nextInt(20)+1,seen);
            if(directed&&n>2) addUnique(graph,2,0,random.nextInt(20)+1,seen);
        }
        while(graph.edges.size()<e) {
            int from=random.nextInt(n);
            int to=random.nextInt(n);
            if(from==to) continue;
            if(dag&&from>to) {
                int temp=from;
                from=to;
                to=temp;
            }
            addUnique(graph,from,to,random.nextInt(20)+1,seen);
        }
        return graph;
    }

    static void addUnique(Graph graph,int from,int to,int weight,Set<Long> seen) {
        if(!graph.directed&&from>to) {
            int temp=from;
            from=to;
            to=temp;
        }
        long key=((long)from<<32)|to;
        if(seen.add(key)) graph.add(from,to,weight);
    }

    interface Task {
        Graph.Result run();
    }

    static void benchmark(String name,int n,int e,Task task) {
        task.run();
        long time=0;
        long ops=0,checks=0,comparisons=0,dsuOps=0;
        for(int i=0;i<5;i++) {
            long start=System.nanoTime();
            Graph.Result result=task.run();
            time+=System.nanoTime()-start;
            ops+=result.ops;
            checks+=result.checks;
            comparisons+=result.comparisons;
            dsuOps+=result.dsuOps;
        }
        System.out.printf(Locale.US,"%s,%d,%d,%.2f,%d,%d,%d,%d%n",name,n,e,time/5000.0,ops/5,checks/5,comparisons/5,dsuOps/5);
    }

    static void check(boolean ok) {
        if(!ok) throw new IllegalStateException("Test failed");
    }

    static void validOrder(Graph graph,int[] order) {
        check(order!=null&&order.length==graph.n);
        int[] position=new int[graph.n];
        boolean[] seen=new boolean[graph.n];
        for(int i=0;i<order.length;i++) {
            check(!seen[order[i]]);
            seen[order[i]]=true;
            position[order[i]]=i;
        }
        for(Graph.Edge edge:graph.edges) check(position[edge.from]<position[edge.to]);
    }

    static void validParts(int n,List<List<Integer>> parts) {
        boolean[] seen=new boolean[n];
        int count=0;
        for(List<Integer> part:parts) {
            check(!part.isEmpty());
            for(int node:part) {
                check(!seen[node]);
                seen[node]=true;
                count++;
            }
        }
        check(count==n);
    }

    static void tests() {
        Graph one=new Graph(1,false);
        check(MST.kruskal(one).connected&&MST.prim(one).connected);
        check(MST.kruskal(one).weight==0&&MST.prim(one).weight==0);
        Graph single=new Graph(1,true);
        check(Connectivity.topological(single).order.length==1);
        check(Connectivity.scc(single).parts.size()==1&&ShortestPath.dijkstra(single,0).dist[0]==0);

        Graph separate=new Graph(4,false);
        separate.add(0,1,2);
        separate.add(2,3,3);
        check(!MST.kruskal(separate).connected&&!MST.prim(separate).connected);
        check(MST.kruskal(separate).weight==5&&MST.prim(separate).weight==5);

        Graph equal=new Graph(3,false);
        equal.add(0,1,1);
        equal.add(1,2,1);
        equal.add(0,2,1);
        check(MST.kruskal(equal).weight==2&&MST.prim(equal).weight==2);

        Graph weighted=new Graph(4,false);
        weighted.add(0,1,1);
        weighted.add(1,2,2);
        weighted.add(2,3,3);
        weighted.add(0,3,10);
        weighted.add(0,2,7);
        check(MST.kruskal(weighted).weight==6&&MST.prim(weighted).weight==6);

        Graph dag=new Graph(5,true);
        dag.add(0,1,1);
        dag.add(0,2,1);
        dag.add(1,3,1);
        dag.add(2,3,1);
        dag.add(3,4,1);
        validOrder(dag,Connectivity.topological(dag).order);

        Graph cycle=new Graph(3,true);
        cycle.add(0,1,1);
        cycle.add(1,2,1);
        cycle.add(2,0,1);
        check(Connectivity.topological(cycle).order==null);
        check(Connectivity.scc(cycle).parts.size()==1);

        Graph parts=new Graph(5,true);
        parts.add(0,1,1);
        parts.add(1,0,1);
        parts.add(1,2,1);
        parts.add(2,3,1);
        parts.add(3,2,1);
        Graph.Result components=Connectivity.scc(parts);
        check(components.parts.size()==3);
        validParts(5,components.parts);

        Graph routes=new Graph(4,true);
        routes.add(0,1,2);
        routes.add(1,2,3);
        routes.add(0,2,10);
        Graph.Result shortest=ShortestPath.dijkstra(routes,0);
        check(Arrays.equals(shortest.dist,new int[]{0,2,5,Integer.MAX_VALUE}));
        check(ShortestPath.path(shortest,2).equals("[0, 1, 2]"));
        check(ShortestPath.path(shortest,3).equals("unreachable"));

        Graph negative=new Graph(2,true);
        negative.add(0,1,-1);
        boolean rejected=false;
        try {
            ShortestPath.dijkstra(negative,0);
        } catch(IllegalArgumentException error) {
            rejected=true;
        }
        check(rejected);

        for(int n:new int[]{10,100,1000}) {
            int e=n==10?30:n==100?500:5000;
            Graph undirected=randomGraph(n,e,false,false,42+n);
            check(MST.kruskal(undirected).connected&&MST.prim(undirected).connected);
            check(MST.kruskal(undirected).weight==MST.prim(undirected).weight);
            Graph directed=randomGraph(n,e,true,false,142+n);
            check(Connectivity.topological(directed).order==null);
            validParts(n,Connectivity.scc(directed).parts);
            Graph acyclic=randomGraph(n,e,true,true,242+n);
            validOrder(acyclic,Connectivity.topological(acyclic).order);
        }
    }

    public static void main(String[] args) {
        tests();
        System.out.println("Tests: PASS");
        Graph city=randomGraph(10,30,false,false,52);
        System.out.println("Kruskal MST: "+MST.kruskal(city).weight);
        System.out.println("Prim MST: "+MST.prim(city).weight);
        Graph roads=randomGraph(10,30,true,false,152);
        System.out.println("Cycle detected: "+(Connectivity.topological(roads).order==null));
        System.out.println("SCC: "+Connectivity.scc(roads).parts);
        Graph dag=randomGraph(10,30,true,true,252);
        System.out.println("Topological order: "+Arrays.toString(Connectivity.topological(dag).order));
        Graph.Result shortest=ShortestPath.dijkstra(roads,0);
        for(int i=0;i<roads.n;i++) System.out.println("Route 0 to "+i+": "+ShortestPath.path(shortest,i)+", distance: "+(shortest.dist[i]==Integer.MAX_VALUE?"unreachable":shortest.dist[i]));
        System.out.println("Algorithm,V,E,AverageTimeUs,Operations,EdgeChecks,Comparisons,DSUOps");
        int[][] sizes={{10,30},{100,500},{1000,5000}};
        for(int[] size:sizes) {
            int n=size[0],e=size[1];
            Graph undirected=randomGraph(n,e,false,false,42+n);
            Graph directed=randomGraph(n,e,true,false,142+n);
            Graph acyclic=randomGraph(n,e,true,true,242+n);
            benchmark("Kruskal",n,e,()->MST.kruskal(undirected));
            benchmark("Prim",n,e,()->MST.prim(undirected));
            benchmark("SCC",n,e,()->Connectivity.scc(directed));
            benchmark("Topological_DAG",n,e,()->Connectivity.topological(acyclic));
            benchmark("Topological_Cycle",n,e,()->Connectivity.topological(directed));
            benchmark("Dijkstra",n,e,()->ShortestPath.dijkstra(directed,0));
        }
    }
}
