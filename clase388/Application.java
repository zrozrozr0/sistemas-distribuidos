import java.util.Arrays;
import java.util.List;

class Demo implements java.io.Serializable {
    public int a;
    public String b;

    public Demo(int a, String b) {
        this.a = a;
        this.b = b;
    }
}


public class Application {
    private static final String WORKER_ADDRESS_1 = "http://localhost:8080/task";

    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();
        /*
        String task1 = "1757600,IPN";
        String task2 = "1757600,SOL";
        String task3 = "1757600,IBM";
        String task4 = "1757600,TIO";
        */
        Demo object = new Demo(2022, "Prueba serializacion y deserializacion");
        
        System.out.println("Se envian los siguientes datos");
        System.out.println(object.a);
        System.out.println(object.b);
        List<String> results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1),
                Arrays.asList(object));

        for (String result : results) {
            System.out.println(result);
        }
    }
}
