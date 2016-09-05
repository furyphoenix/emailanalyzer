package neos.algorithm.edmonds;

public class EdmondsBlossomAlgorithm {
	private final boolean[][] g;
	private final int size;
	private int[] match;
	private boolean[] inque;
	private boolean finish;
	private int[] que;
	private int head;
	private int tail;
	private int[] father;
	private int n;
	private int[] base;
	private int[] inblossom;
	private int ans;
	
	public EdmondsBlossomAlgorithm(boolean[][] matrix){
		this.g=matrix;
		this.size=matrix.length;
		
		this.match=new int[size];
		this.inque=new boolean[size];
		this.finish=false;
		this.que=new int[size];
		this.head=-1;
		this.tail=-1;
		this.father=new int[size];
		this.n=size;
		this.base=new int[size];
		this.inblossom=new int[size];
		this.ans=-1;
	}
	
	private int pop(){
		return que[head++];
	}
	
	private int push(int i){
		que[++tail]=i;
		inque[i]=true;
		return 0;
	}
	
	private int findAncestor(int u, int v){
		boolean[] inpath=new boolean[size];
		for(int i=1; i<=n; i++){
			inpath[i]=false;
		}
		while(u!=0){
			u=base[u];
			inpath[u]=true;
			u=father[match[v]];
		}
		
		while(v!=0){
			v=base[v];
			if(inpath[v]){
				return v;
			}
			v=father[match[v]];
		}
		
		return v;
	}
	
	private void reset(int u, int anc){
		while(u!=anc){
			int v=match[u];
			inblossom[base[v]]=1;
			inblossom[base[u]]=1;
			v=father[v];
			if(base[v]!=anc){
				father[v]=match[u];
			}
			u=v;
		}
	}
	
	private void contract(int u, int v){
		int anc=findAncestor(u, v);
		for(int i=1; i<=n; i++){
			inblossom[i]=1;
		}
		reset(u, anc);
		reset(v, anc);
		if(base[u]!=anc){
			father[u]=v;
		}
		if(base[v]!=anc){
			father[v]=u;
		}
		for(int i=1; i<=n; i++){
			if(inblossom[base[i]]!=0){
				base[i]=anc;
				if(!inque[i]){
					push(i);
				}
			}
		}
	}
	
	private void findAugment(int start){
		for(int i=1; i<=n; i++){
			father[i]=0;
			inque[i]=false;
			base[i]=i;
		}
		
		head=1;
		tail=1;
		que[1]=start;
		inque[start]=true;
		
		while(head<=tail){
			int u=pop();
			for(int v=1; v<=n; v++){
				if(g[u][v]&&(base[v]!=base[u])&&(match[v]!=u)){
					if((v==start)||((match[v]!=0)&&(father[match[v]]!=0))){
						contract(u,v);
					}
				}else{
					if(father[v]==0){
						if(match[v]!=0){
							push(match[v]);
							father[v]=u;
						}else{
							father[v]=u;
							//finish=v;
							return;
						}
					}
				}
			}
		}
	}
	
}
