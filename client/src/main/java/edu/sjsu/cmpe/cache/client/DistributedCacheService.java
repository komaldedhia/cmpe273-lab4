package edu.sjsu.cmpe.cache.client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
    private final String cacheServerUrl;
    private int responseCode=0;
    private Future<HttpResponse<JsonNode>> future=null;
    private Future<HttpResponse<JsonNode>> futureGet=null;

    public DistributedCacheService(String serverUrl) {
        this.cacheServerUrl = serverUrl;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
     */
    @Override
    public String get(long key) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        String value = response.getBody().getObject().getString("value");

        return value;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
     *      java.lang.String)
     */
    @Override
    public void put(long key, String value) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .put(this.cacheServerUrl + "/cache/{key}/{value}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key))
                    .routeParam("value", value).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getCode() != 200) {
            System.out.println("Failed to add to the cache.");
        }
    }
    
    @Override
    public void asynchroPut(long key, String value) {
    	
    	 future = Unirest.put(this.cacheServerUrl + "/cache/{key}/{value}")
	    			.header("accept", "application/json")
	                .routeParam("key", Long.toString(key))
	                .routeParam("value", value)
    			    .asJsonAsync(new Callback<JsonNode>() {

    			    public void failed(UnirestException e) {
    			    	System.out.println("The request has failed");
    			    }

    			    public void completed(HttpResponse<JsonNode> response) {
    			         responseCode = response.getCode() ;
    			        // System.out.println("Put Completed "+responseCode);
    			         Map<String, List<String>> headers = response.getHeaders();
    			         JsonNode body = response.getBody();
    			         InputStream rawBody = response.getRawBody();
    			    }

    			    public void cancelled() {
    			        System.out.println("The request has been cancelled");
    			    }

    			});   
    }
    
    @Override
    public void delete(long key) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .delete(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getCode() != 200) {
            System.out.println("Failed to delete from the cache.");
        }
    }
    
    @Override
    public int getResponseCode()
    {
    	int responseCode=0;
    	try
    	{
    		 
    	 HttpResponse<JsonNode> response=future.get(200,TimeUnit.MILLISECONDS);
    	 responseCode=response.getCode();
    	// System.out.println("Get Responce Code "+response.getCode());
    	
    	}
     catch (Exception  e) 
    	{ System.err.println("From getResponseCode "+e);
    	 System.out.println("Cancelling the future task.");
    	 future.cancel(true); }
    	return responseCode;
    }
    
    @Override
    public void asynchroGet(long key,final ConcurrentHashMap<String,String> valueMap,final CountDownLatch cntLatch) {
    	
    	future = Unirest.get(this.cacheServerUrl + "/cache/{key}")
	    			.header("accept", "application/json")
	                .routeParam("key", Long.toString(key))
	                .asJsonAsync(new Callback<JsonNode>() {

    			    public void failed(UnirestException e) {
    			    	
    			        System.out.println("The request has failed");
    			        
    			    }

    			    public void completed(HttpResponse<JsonNode> response) {
    			    	 String value="0";
    			         responseCode = response.getCode() ;
    			        // System.out.println("Get Completed "+responseCode);
    			         if (responseCode==200){
    			         value = response.getBody().getObject().getString("value");
    			         }
    			         valueMap.put(cacheServerUrl, value);
    			         cntLatch.countDown();
    			         //System.out.println("In Complete Get Response Value "+value);
    			        
    			    }

    			    public void cancelled() {
    			        System.out.println("The request has been cancelled");
    			    }

    			});   
    }
    
      
}
