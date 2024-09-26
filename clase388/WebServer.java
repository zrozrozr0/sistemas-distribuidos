
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String SEARCH_ENDPOINT = "/searchtoken";

    private final int port;
    private HttpServer server;

    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        WebServer webServer = new WebServer(serverPort);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    public WebServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        HttpContext searchContext = server.createContext(SEARCH_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        searchContext.setHandler(this::handleSearchRequest);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
            String dummyResponse = "123\n";
            sendResponse(dummyResponse.getBytes(), exchange);
            return;
        }

        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }

        long startTime = System.nanoTime();

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = calculateResponse(requestBytes);

        long finishTime = System.nanoTime();
        long secondsTime = (finishTime - startTime) / (long)Math.pow(10, 9);
        long milisTime = ((finishTime - startTime) % (long)Math.pow(10, 9));
        milisTime = milisTime / (long)Math.pow(10, 6);
        
        if (isDebugMode) {
            String debugMessage = String.format("La operación tomó %d nanosegundos = %d segundos con %d milisegundos", (finishTime - startTime) ,secondsTime, milisTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
        }
        sendResponse(responseBytes, exchange);
    }

    private byte[] calculateResponse(byte[] requestBytes) {
        Demo dm = null;
        dm = (Demo) SerializationUtils.deserialize(requestBytes);
        //Demo dm = new Demo(port, bodyString);
        System.out.println("Se han recibido los siguientes datos: ");
        System.out.println(dm.a);
        System.out.println(dm.b);
        
        String ok = "OK";
        return ok.toString().getBytes();
    }

    private byte[] getOcurrences (byte[] requestBytes) {
        String bodyString = new String(requestBytes, StandardCharsets.UTF_8);
        String[] stringNumbers = bodyString.split(",");
        return findOcurrences(Integer.parseInt(stringNumbers[0]), stringNumbers[1]);

    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void handleSearchRequest (HttpExchange exchange) throws IOException {
        long startTime = System.nanoTime();

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = getOcurrences(requestBytes);

        Headers headers = exchange.getRequestHeaders();

        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }

        long finishTime = System.nanoTime();
        long secondsTime = (finishTime - startTime) / (long)Math.pow(10, 9);
        long milisTime = ((finishTime - startTime) % (long)Math.pow(10, 9));
        milisTime = milisTime / (long)Math.pow(10, 6);
        if (isDebugMode) {
            String debugMessage = String.format("La operación tomó %d nanosegundos = %d segundos con %d milisegundos", (finishTime - startTime) ,secondsTime, milisTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
        }
        
        sendResponse(responseBytes,exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    public byte[] findOcurrences (Integer n, String token) {
        int occurrences = 0;

        StringBuilder cadenota = new StringBuilder();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 3; j++) {
                char randomChar = (char) ('A' + Math.random() * 26);
                cadenota.append(randomChar);
            }
            if (i < n - 1) {
                cadenota.append(' '); 
            }
        }

        String bigString = cadenota.toString();
        System.out.println("");
        String searchStr = token;
        int index = bigString.indexOf(searchStr);

        while (index != -1) {
            occurrences++;
            index = bigString.indexOf(searchStr, index + 1);
        }

        String res = "Número de ocurrencias de '" + token + "': " + occurrences;
        return res.getBytes();
    }
}


