package com.bank.atm.client.function;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ClientHttp {
    public String sendHttp(String message, String url){
        String result="";
        String urls = "http://localhost:8080/atm/"+url;
        try{
            CloseableHttpClient client = HttpClients.createSystem();
            HttpPost httpPost = new HttpPost(urls);

            StringEntity entity = new StringEntity(message);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString (response.getEntity ());
            client.close();
        }catch (Exception e){
            System.out.println ("Error: "+e.getMessage ());
        }
        return result;
    }

    public String sendIsoToSwitching(String message, String url){
        String result="";
        String urls = "http://localhost:8081/"+url;
        try{
            CloseableHttpClient client = HttpClients.createSystem();
            HttpPost httpPost = new HttpPost(urls);

            StringEntity entity = new StringEntity(message);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString (response.getEntity ());
            client.close();
        }catch (Exception e){
            System.out.println ("Error: "+e.getMessage ());
        }
        return result;
    }
}
