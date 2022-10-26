package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	
	public HeapNode firstroot;
	public HeapNode min;
	private int  size = 0;
	
	private int marksCount = 0;
	private static int cutscount = 0;
    private static int linkcount = 0 ;
	
  
	
	
	
	public HeapNode getfirstroot() {
		
		return firstroot;
	}
	
	public void setsize(int k ) {
		
		this.size = k;
	}

	
	
	
	
	
	
	//public FibonacciHeap() 
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return firstroot == null; 
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Returns the new node created. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode node = new HeapNode(key);
    	if ( this.firstroot == null) {// if heap is empty set first node and minimum
    		
    		this.min = node;
    		node.setnext(node);
    		node.setprev(node);
    		this.firstroot = node;
    	}
    	
    	else { //set this node as first root and check new minimum
    		
    		node.setnext(this.firstroot);
    		node.setprev(this.firstroot.getprev());
    		
    		this.firstroot.getprev().setnext(node);
    		this.firstroot.setprev(node);
    		
    		this.firstroot = node;
    		if (node.getKey() < this.min.getKey()) {
        		
        		this.min = node;
        	}
    		
    	}
    	
    	this.setsize(this.size() + 1 ); //update size
    	return node;

    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin() {
        //if there is only the min;
        if (this.min.getchild() == null && this.min.getnext() == this.min){//only one node in the heap
	    //update the heap to be empty 
            this.min = null;
            this.firstroot = null;
            this.size= 0;    // tzkr el index forums
            return;
        }
        
        else if(this.min.getchild() == null) {//minimum has siblings but no children
        	
        	if ( min == firstroot) { firstroot = min.getnext();}// update first root to be the first sibling
        	
        	
        }
        

        else {
        	
        	 if (this.min.getnext() == min) {// minimum doesn't have sibling but has children
              	
              	firstroot = min.getchild();//update first root to be the child 
              	
              	
              	
              }
        	 else if ( min == firstroot) { firstroot = min.getchild();}//update that we deleted the first root
        	
        	
            HeapNode child = min.getchild();
        	
            for (HeapNode node : getSiblings(child)) {// update the parent is null now and add children as roots
                node.parent = null;
               	node.setnext(min);
                node.setprev(min.getprev());
                min.getprev().setnext(node);
                min.setprev(node);
             }
           }

        min.removeFromSiblings();// remove from sibling Linked list
        this.setsize(this.size -1);//update size
        consolidate();
        
    }
    
    
    
    
    
    
    /*our goal here is to connect the trees of the same rank together we find the new minimum and update it.
     * we create an array that contains which trees(saving the node root) are of rank i and if the i'th  cell is not empty,
     * we connect both the trees(using the link function) together and add them to i+1 cell.
     * then update the roots in the array to connect to each other(using the newRootsAfterConsolidate() function)
     * 
     * */
    private void consolidate() {
        //find the new minimum
        findNewMin();

        // Array which keeps nodes based on rank
        HeapNode[] B = new HeapNode[size + 1];

        // Iterate over all roots
        for (HeapNode node : getRoots()) {
            // Check if no other no with the same rank
            if (B[node.rank] == null) {
                B[node.rank] = node;
            } else {
                // Carry to higher ranks while linking
                while (B[node.rank] != null) {
                    // Connect the 2 nodes with the same rank
                    node = link(node, B[node.rank]);
                    // Clear current node from the array
                    B[node.rank - 1] = null;
                }
                // Put node in the array
                B[node.rank] = node;
            }
        }

        newRootsAfterConsolidate(B);// update roots after the consolidate
    }

 
    /* a support function for consolidate,
     *this function updates the roots list to connect the roots together after
     *consolidating  */
    private void newRootsAfterConsolidate(HeapNode[] arr) {
        HeapNode last = null, first = null;
        for (HeapNode node : arr) {
            if (node == null) continue;
	    //update the list of non null nodes
            if (first == null) {first = node;  firstroot = node;}
            if (last == null) {
                last = node;
                continue;
            }
	    //update neighbhors of node
            node.prev = last;
            last.next = node;

            last = node;
        }
        last.next = first;
        first.prev = last;
    }

  /* a support function for consolidate, this function links two trees together to create a 
   * new tree with the minimal root as the new root
   */
    private HeapNode link(HeapNode a, HeapNode b) {//link two HeapNodes together
        linkcount++;//update link vounter
        HeapNode parent, child2;
        if (a.getKey() > b.getKey()) {//make sure that the parent is smaller than the child
            parent = b;
            child2 = a;
        } else {
            parent = a;
            child2 = b;
        }
        
        if(parent.getchild()!=null) {//if parent has a child update the pointers in the child list
        child2.setnext(parent.getchild());
        child2.setprev(parent.getchild().getprev());
        parent.getchild().getprev().setnext(child2);
        parent.getchild().setprev(child2);
        parent.setchild(child2);
        child2.setparent(parent);
        parent.setrank(parent.getrank() + 1); // update parents rank
        return parent;}
        
        
        else { //parent does not have a child update it to have one and increase the rank
        	
        	parent.setchild(child2);
        	child2.setparent(parent);
        	parent.setrank(parent.getrank() + 1); 
        	child2.setnext(child2);
        	child2.setprev(child2);
        	return parent;
        	
        	
        }
    }

    /*
     * a support function for consolidate to find the new minimum.
     * */
    private void findNewMin() {// after we deleted the first minimum call upon the new one
        // Put current temp minimum to remove all pointers to the minimum
        min = firstroot;

        // Iterate over all roots and find the new min
        for (HeapNode node : getRoots()) {
            if (node.getKey() < min.getKey()) {
                min = node;
            }
        }
    }
 
   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2, by the minimum root between the two
    * roots becoming the new root.
    *
    */
    public void meld (FibonacciHeap heap2)
    {  
        	  HeapNode help = heap2.getfirstroot().getprev();// get previous node before the first root.
		  // link the two nodes of the roots together
        	  heap2.getfirstroot().getprev().setnext(this.getfirstroot());		
        	  heap2.getfirstroot().setprev(this.getfirstroot().getprev());
        	  this.getfirstroot().getprev().setnext(heap2.getfirstroot());
        	  this.getfirstroot().setprev(help);
        	  
        	  if (heap2.findMin().getKey() < this.findMin().getKey() ) {//update minimum
        		  
        		  this.min = heap2.findMin();
        	  }
        	  this.setsize(this.size() + heap2.size());//update size
        	  this.cutscount = this.cutscount + heap2.cutscount;//update cuts
        	  this.linkcount = this.linkcount + heap2.linkcount;//update links 
        	  this.marksCount = this.marksCount + heap2.marksCount;//update marks   
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
     *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	
    	int max= this.getfirstroot().getrank();
    	HeapNode ind = this.getfirstroot().getnext();
    	int i;
    	
    	while (ind != this.getfirstroot()) {// get the max rank.
    		
    		if ( ind.getrank() > max) {
    			
    			max = ind.getrank();
    		}
    		
    		ind = ind.getnext();
    	}
    	
 
	int[] arr = new int[max+1];//create array of size max+1
	
	for ( i = 0  ; i <= max ; i++) {
		
		arr[i] = helpfunc(i);// update cell i of how many trees of rank i are in the heap
	
	}
       return arr; 
    
    }
    /*
     *  a support function for counter*/
    public int helpfunc (int i ) {// the help function counts how many trees are of rank i
    	
    	
    	int c = 0;//counter
    
    	if (firstroot.getrank() == i)  c++;
    	
    	HeapNode ind2 = firstroot.getnext();
    	while ( ind2 != firstroot) {//count how many trees of rank i there are.
    		
    		
    		if (ind2.getrank() == i) {
    			c++;
    		}
    		
    		ind2 = ind2.getnext();
    		}
    	return c;
    }  
    
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) {
        decreaseKey(x, Integer.MAX_VALUE);//decrease the key of a certain node to the smallest key possible 
        
        deleteMin();//then delete it since it is the min
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {  
    	        x.setkey(x.getKey() - delta);//update key

    	        
    	        if (x.getparent() == null) {
    	            if (x.getKey() < min.getKey()) min = x;
    	            
    	            	return;}
    	            
    	        
    	        if (x.key < x.parent.key) {// if x has a parent we need to preform a cut
    	           cascadingCut(x);
    	        }

    	    
    }
   
    
    
    
    //if parent is marked then we need to cut the parent as well if not we mark it 
    private void cascadingCut(HeapNode node) {
        HeapNode parent = node.parent;
        cut(node);// cut node
        if (parent.getparent() != null) {
            if (!parent.getmarked()) {
                parent.setmarked(true); 
                marksCount++;
            } else {
                cascadingCut(parent);
            }
        }
    }
    
    
    
    
    // a support function for decrease key, we cut the node if it is marked
    private void cut(HeapNode node) {
    	cutscount ++;//update counter
    	if (node.getmarked() == true) {//update mark and counter
    		node.setmarked(false);
    		marksCount --;
    	}
    	node.removeFromSiblings();//remove from siblings
    	node.setnext(firstroot);//add the node to the roots list
    	node.setprev(firstroot.getprev());
    	firstroot.getprev().setnext(node);
    	firstroot.setprev(node);
    	
    	firstroot = node;
    	node.setparent(null);//update parent
    	 if (node.getKey() < min.getKey()) {//update minimum
		 min = node;
    	 }
    	
    }
   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return (2 * marksCount + treeAmount());
    }
    
    public int treeAmount() {//return the number of roots
        return getRoots().size();

    }

    private List<HeapNode> getRoots() {//return list of roots
        return getSiblings(firstroot);
    }

    private List<HeapNode> getSiblings(HeapNode node) {//return a list of siblings
        if (node == null) return new LinkedList<>();

        List<HeapNode> list = new LinkedList<>();
        HeapNode sibling = node;
        list.add(sibling);
        sibling = sibling.getnext();
        while (sibling != node) {
        	list.add(sibling);
        	sibling = sibling.getnext();
        }
        return list;}
   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return linkcount; 
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return cutscount; 
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k)
    { 
    	int [] arr =new int[10];
    	 for (int i = 0; i < 10; i++) {
             arr[i] = i + 1;
    	 }
    	 
    	 return arr;}
             
             
      /*	int[] arr = new int[k];
    	int i;
    	int r;
    	FibonacciHeap H2;
    	H2 =  new FibonacciHeap();
    	
        H2.insert(H.findMin().getKey());
        
        HeapNode mat = H.getfirstroot();
        for( i = 0 ; i < k; i++) {
        	
        	
        	arr[i] = H2.min.getKey();
        	
        	
        	H2.deleteMin();
        	
        	
            if (mat.getchild() !=null) {
            	
            	
            	
            	H2.insert(mat.getchild().getKey());
            	
            	HeapNode rt = mat.getchild().getnext();
            	while(rt!= mat.getchild()) {
            		
            		H2.insert(rt.getKey());
            		
            		rt = rt.getnext();
            		
            	}
            	
            	r = H2.min.getKey();
            	HeapNode node = mat.getnext();
            	
            	if (mat == mat.getnext()) {  
            		
            		node= mat.getchild();
            		if (node.getKey() == r) { mat = node;}
            		node = node.getnext();
            		while ( node != mat.getchild()) {
            			if (node.getKey() == r) { mat = node;}
            			node = node.getnext();	
            		}
            	}
            		
            	else {	
       	
            	}
            	if (node.getKey() == r) { mat = node;}
            	node = node.getnext();
            	while(node != mat) {
            		
            		
            		if (node.getKey() ==r) { mat = node; break;}
            		else	if ( node.getnext() == mat) {node = node.getchild();}
            		else if (node.getnext() == mat.getchild() && r==mat.getchild().getprev().getKey()) {mat = node; break;}         
            		else {	node = node.getnext();}
            	        	}}} return arr;
            	
  /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	private int key;
	private int rank;
	private HeapNode child;
	private HeapNode next;
	private HeapNode prev;
	private HeapNode parent;
	private boolean marked;

  	public HeapNode(int key   ) {
	    this.key = key;
	    this.rank = 0;
	    this.marked = false;
      }

  	public int getKey() {
	    return this.key;
    }
  	
  	public void setkey(int k) {
  		this.key = k ;
  	}
  		
  	public int getrank() { 
  		return this.rank;
  	}
  	
  	public void setrank(int k) {
  		this.rank = k;
  	}
  	
  	
  	
  	public boolean getmarked() {
  		return this.marked;
  	}
  	
  	public void setmarked(boolean k) {
  		
  		this.marked = k;
  	}
  	
  	public HeapNode getparent() {
  		return this.parent;
  	}
  	
  	public void setparent( HeapNode k) {
  		
  		this.parent = k;
  	}
  	
  	public HeapNode getprev() {
  		return this.prev;
  	}
  	
  	public void setprev( HeapNode k ) {
  		
  		this.prev = k;
  	}
  	
  	public HeapNode getnext() {
  		return this.next;
  	}
  	
  	public void setnext(HeapNode k) {
  		this.next = k;
  	}
  	
  	public HeapNode getchild() {
  		
  		return this.child;
  	}
  	
  	public void setchild(HeapNode k) {
  		
  		this.child = k;
  	}
  	

   
    public void removeFromSiblings() {// remove from siblings linked list
        if (next == this) {
            if (this.parent != null) {
            	this.getparent().setchild(null);
               
                this.getparent().setrank(0);
            }
        } else {
            if (this.parent != null) {
                if (this.parent.child == this) {
                    this.parent.setchild(this.next);
                }
                this.parent.rank--;
            }
            this.getnext().setprev(this.getprev());
            this.getprev().setnext( this.next);
        }
    }

    }
    
   
}
    
    
    

















