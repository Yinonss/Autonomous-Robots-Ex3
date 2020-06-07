
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.JFrame;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.spanning.*;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;

public class Graph implements org.jgrapht.Graph<String, DefaultEdge> {

	DefaultDirectedGraph<String, DefaultEdge> g;
    private DefaultWeightedEdge e1;
    String source = "";
    boolean first = true;
    
    public Graph() {
    	g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
    }
    public boolean addVertex(String name) {
        String last_vertex = "";
        Set<String> all = g.vertexSet();
        
        if(first) {
        	source = "" + name;
        	first = false;
        }
        
        if(all.size() > 0) {
        	last_vertex = getLastElement(all);
        }
        g.addVertex(name);
        if(!last_vertex.equals("")) 
        	g.addEdge(last_vertex, name);
        //graph.addVertex(name);
		return true;
    }
    
    public String getLastElement(Set<String> c) {
    	String last = "";
    	if(c.size() > 0) {
    		for(String x : c) {
    			last = ""+x;
    		}
    	}
    	System.out.println("last : "+ last);
        return last.toString();
    }
    /*public void addEdge(String v1,String v2) {
        g.addEdge(v1, v2);
    }*/


    public DefaultDirectedGraph<String, DefaultEdge> getGraph() {
        return g;
    }
    
    public String getOutput() {
    	return g.toString();
    }
    
    public void drawGraph() {
    	JFrame new_window = new JFrame();
    	new_window.setSize(500,500);
    	new_window.setTitle("Graph Viewer");
    	JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<String, DefaultEdge>(g);

        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        new_window.add(new mxGraphComponent(graphAdapter));

        new_window.pack();
        new_window.setLocationByPlatform(true);
        new_window.setVisible(true);
    }

    /*public SimpleWeightedGraph<String,DefaultWeightedEdge> getGraph() {
        return graph;
    }*/

    public void getSpanningTree() {
        KruskalMinimumSpanningTree k=new KruskalMinimumSpanningTree(g);
        System.out.println(k.getSpanningTree().toString());
        //KruskalMinimumSpanningTree k1=new KruskalMinimumSpanningTree(graph);
        //System.out.println(k1.getEdgeSet().toString());   
    }
	@Override
	public DefaultEdge addEdge(String arg0, String arg1) {
		// TODO Auto-generated method stub
		g.addEdge(arg0, arg1);
		return null;
	}
	@Override
	public boolean addEdge(String arg0, String arg1, DefaultEdge arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String addVertex() {
		// TODO Auto-generated method stub
		return null;
	}
	//@Override
	/*public boolean addVertex(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}*/
	@Override
	public boolean containsEdge(DefaultEdge arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsEdge(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsVertex(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int degreeOf(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Set<DefaultEdge> edgeSet() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<DefaultEdge> edgesOf(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<DefaultEdge> getAllEdges(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public DefaultEdge getEdge(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getEdgeSource(DefaultEdge arg0) {
		// TODO Auto-generated method stub
		return source;
	}
	@Override
	public Supplier<DefaultEdge> getEdgeSupplier() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getEdgeTarget(DefaultEdge arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double getEdgeWeight(DefaultEdge arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public GraphType getType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Supplier<String> getVertexSupplier() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int inDegreeOf(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Set<DefaultEdge> incomingEdgesOf(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int outDegreeOf(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Set<DefaultEdge> outgoingEdgesOf(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean removeAllEdges(Collection<? extends DefaultEdge> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Set<DefaultEdge> removeAllEdges(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean removeAllVertices(Collection<? extends String> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean removeEdge(DefaultEdge arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public DefaultEdge removeEdge(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean removeVertex(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setEdgeWeight(DefaultEdge arg0, double arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Set<String> vertexSet() {
		// TODO Auto-generated method stub
		return null;
	}

    /*public void getSpanningTreeCost() {
        KruskalMinimumSpanningTree k=new KruskalMinimumSpanningTree(graph);
        System.out.println(k.getSpanningTreeCost());
    }*/ 
}