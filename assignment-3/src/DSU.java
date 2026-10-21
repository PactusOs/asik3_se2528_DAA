public class DSU {
        int[] parent,size;
        public long ops;
        public DSU(int n) {
            parent=new int[n];
            size=new int[n];
            for(int i=0;i<n;i++) {
                parent[i]=i;
                size[i]=1;
            }
        }
        public int find(int x) {
            ops++;
            if(parent[x]!=x) parent[x]=find(parent[x]);
            return parent[x];
        }
        public boolean union(int a,int b) {
            ops++;
            a=find(a);
            b=find(b);
            if(a==b) return false;
            if(size[a]<size[b]) {
                int temp=a;
                a=b;
                b=temp;
            }
            parent[b]=a;
            size[a]+=size[b];
            return true;
        }
    }
