/*
 *  MIT License
 *
 *  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.mycompany.app;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.fasterxml.jackson.databind.DeserializationFeature;   
import com.fasterxml.jackson.databind.ObjectMapper;             

public class WebServer {
    private String ADDRESS_1 = "http://localhost:8081/factorial";
    private String ADDRESS_2 = "http://localhost:8082/factorial";
    private String ADDRESS_3 = "http://localhost:8083/factorial";
   
    private static final String STATUS_ENDPOINT = "/status";
    private static final String TASK_ENDPOINT = "/task";
    private static final String HOME_PAGE_ENDPOINT = "/";
    private static final String HOME_PAGE_UI_ASSETS_BASE_DIR = "/ui_assets/";
    private static final String ENDPOINT_PROCESS = "/procesar_datos";

    private final int port; 
    private HttpServer server; 
    private final ObjectMapper objectMapper;

    public WebServer(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT); 
        HttpContext taskContext = server.createContext(ENDPOINT_PROCESS);
        HttpContext homePageContext = server.createContext(HOME_PAGE_ENDPOINT);
        HttpContext factorialContext = server.createContext(TASK_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        homePageContext.setHandler(this::handleRequestForAsset);
        factorialContext.setHandler(this::handleFactorialRequest);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    // Entramos al handleRequestForAsset ya que es el primero en ser enviado por parte del navegador
    private void handleRequestForAsset(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        // Creamos un arreglo para almacenar los bytes de respuesta
        byte[] response;

        String asset = exchange.getRequestURI().getPath(); 
        // Si lo que se solicita es la raiz, se envian los datos que se encuentran en los assets
        if (asset.equals(HOME_PAGE_ENDPOINT)) { 
            response = readUiAsset(HOME_PAGE_UI_ASSETS_BASE_DIR + "index.html");
        } else {
            response = readUiAsset(asset); 
        }
        // Agregamos el tipo de contenido con el que vamos a responder
        addContentType(asset, exchange);
        // Enviamos la respuesta
        sendResponse(response, exchange);
    }

    private byte[] readUiAsset(String asset) throws IOException {
        InputStream assetStream = getClass().getResourceAsStream(asset);

        if (assetStream == null) {
            return new byte[]{};
        }
        return assetStream.readAllBytes(); 
    }

    private static void addContentType(String asset, HttpExchange exchange) {

        String contentType = "text/html";  
        if (asset.endsWith("js")) {
            contentType = "text/javascript";
        } else if (asset.endsWith("css")) {
            contentType = "text/css";
        }
        exchange.getResponseHeaders().add("Content-Type", contentType);
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) { 
            exchange.close();
            return;
        }

        try {
            FrontendSearchRequest frontendSearchRequest = objectMapper.readValue(exchange.getRequestBody().readAllBytes(), FrontendSearchRequest.class); 
            String frase = frontendSearchRequest.getSearchQuery();
            String response1 = "", response2 = "", response3 = "";
            int number = Integer.parseInt(frase);
            int rango1, rango2, rango3;
            rango1 = number / 3;
            rango2 = rango1 * 2;
            rango3 = number;
            int aux = 0;
            System.out.println("Primer rango: 0 - " + rango1);
            Aggregator aggregator = new Aggregator();
            List<String> results = aggregator.sendTasksToWorkers(Arrays.asList(ADDRESS_1),
                    Arrays.asList("0,"+rango1));
            for (String result : results) {
                response1 = result;
            }
            aux = rango1 + 1;
            System.out.println("Segundo rango: " + aux + " - " + rango2);
            results = aggregator.sendTasksToWorkers(Arrays.asList(ADDRESS_2),
                    Arrays.asList(aux + "," + rango2));
            for (String result : results) {
                response2 = result;
            }
            aux = rango2 + 1;
            System.out.println("Tercer rango: " + aux + " - " + rango3);
            results = aggregator.sendTasksToWorkers(Arrays.asList(ADDRESS_3),
                    Arrays.asList(aux + "," + rango3));
            for (String result : results) {
                response3 = result;
            }
            
            System.out.println("Resultado 1: " + response1);
            System.out.println("Resultado 2: " + response2);
            System.out.println("Resultado 3: " + response3);
            

            BigInteger result = BigInteger.ONE;
            
            BigInteger bigInteger1 = new BigInteger(response1);
            BigInteger bigInteger2 = new BigInteger(response2);
            BigInteger bigInteger3 = new BigInteger(response3);

            result = bigInteger1.multiply(bigInteger2.multiply(bigInteger3));

            System.out.println("Total: " + result);
            StringTokenizer st = new StringTokenizer(frase);
            FrontendSearchResponse frontendSearchResponse = new FrontendSearchResponse(String.valueOf(result), st.countTokens());
            byte[] responseBytes = objectMapper.writeValueAsBytes(frontendSearchResponse);

            sendResponse(responseBytes, exchange);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void handleFactorialRequest (HttpExchange exchange) throws IOException {
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = calculateResponse(requestBytes);


        sendResponse(responseBytes, exchange);
    }

    private byte[] calculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        
        
        int result = 300;
        return String.format("El resultado de la multiplicación es %s\n", result).getBytes();
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        System.out.println("Bytes enviados: " + responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }
}


