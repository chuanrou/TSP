/*
問題定義：
graph G = (V, E) that has a nonnegative integer cost c(u, v) 
associated with each edge (u, v) ∈ E, the
traveling-salesman problem is to ﬁnd 
a hamiltonian cycle (a tour) of G with minimum cost.

方法說明如下：
TSP with the triangle inequality
Triangle inequality:
對所有的u, v, w ∈ V , c(u, w) ≤ c(u, v) + c(v, w) 
c是 cost function，並且滿足上面之三角不等式.

用一個2-approximation algorithm可以來解這個問題
• 輸入：G是complete undirected graph, c是cost function.
• MST-PRIM: 找minimum spanning tree的演算法 .
• 若c符合三角不等式, 以下演算法可保證找到一個hamiltonian cycleH , 
   其cost總合不超過optimal hamiltonian cycle的2倍.
   
   演算法如下：
   APPROX-TSP-TOUR(G,c)
	1. select a vertex r，r 是屬於V[G} to be a “root” vertex
	2. compute a minimum spanning tree T for G from root r using MST-PRIM(G, c, r)演算法
	3. let L be a list of vertices, ordered according to when they are ﬁrst visited
	   in a preorder tree walk of T.
	4. return the hamiltonian cycle H that visits the vertices in the order L.
	
	其中，Preorder sequence 為作MST的點追蹤順序(preorder traversal)，並且在做
	在做preorder traversal時, 把過程中第一次和回程時經過的每一點都記錄下來當成L 
	(稱之為full walk).
	最後，將full walk中前面已出現過的點刪除, 再連回起點, 就是我們要的
	H(Hamiltonian cycle)。由 於 滿 足 triangle inequality， 拿 掉 重 複 點 後 的
	cost 不 會變 大 。.

結論：
	此一方法 是 polynomial time 演 算 法 ， 因為 只 需 要 做 minimum-spanning tree，
 	再 加 上 preorder traversal 即 可 ，這 個 演 算 法 可 以 得 到 不 錯 的 approximation ratio。	
 */
