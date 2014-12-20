package edu.sjsu.cmpe.cache.client;

import java.util.Collection;
import java.util.ArrayList;


public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
               
        Collection<String> nodes= new ArrayList<String>();
    	nodes.add("http://localhost:3000");
    	nodes.add("http://localhost:3001");
    	nodes.add("http://localhost:3002");
    	String value=null;
    	
    	CRDTClient crdt= new CRDTClient(nodes);
    	
    	System.out.println("@@@@@ Put Vale a @@@@@ Sep 1");
    	crdt.put(1, "a");
    	
    	    	
    	System.out.println("@@@@ Put Vale b @@@@ Sep 2");
    	try {
		    Thread.sleep(30000);                
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
    	CRDTClient crdt1= new CRDTClient(nodes);
    	crdt1.put(1, "b");
    	
    	System.out.println("@@@@ Get Vale 1 @@@@ Sep 3");
    	try {
		    Thread.sleep(30000);                
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
    	
    	CRDTClient crdt2= new CRDTClient(nodes);
    	value=crdt2.get(1);
    	System.out.println("-------------value "+value);
    	
    	System.out.println("Existing Cache Client...");
    }

}
