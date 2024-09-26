import networking.WebClient;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Aggregator {
    // Solo tiene un objeto
    private WebClient webClient;
    // Constructor instancia un objeto webclient
    public Aggregator() {
        this.webClient = new WebClient();
    }
    int serv;
    StringBuilder sb = new StringBuilder();
    // Solo tiene el metodo, recibe la lista de las direcciones de los trabajadores
    // y tambien de las tareas
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<Demo> tasks) {
        // Permite ejecutar la ejecucion de codigo bloqueante
        CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];
        
        // Se iteran los elementos de la lista junto con las tareas
        for (int i = 0; i < workersAddresses.size(); i++) {
            String workerAddress = workersAddresses.get(i);
            byte[] task = SerializationUtils.serialize(tasks.get(i));
            // Se envia por medio de bytes, las tareas asyncronas con el metodo send task
            byte[] requestPayload = task;
            futures[i] = webClient.sendTask(workerAddress, requestPayload);
            
        }
        System.out.println(sb.toString());   
        

        // Lista de resultados que se almacenan conforme vayan llegando
        List<String> results = new ArrayList();
        for (int i = 0; i < tasks.size(); i++) {
            results.add(tasks.get(i) + futures[i].join());
        }

        return results;
    }

    
}
