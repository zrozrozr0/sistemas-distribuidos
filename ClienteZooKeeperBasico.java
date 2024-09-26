import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

public class ClienteZooKeeperBasico {
    private static final int TIEMPO_ESPERA = 5000; // Intervalo de tiempo en milisegundos (aquí cada 5 segundos)

    public static void main(String[] args) throws Exception {
        String hostPort = "10.0.2.15:2181"; // Cambia esto por tu dirección de ZooKeeper

        ZooKeeper zooKeeper = new ZooKeeper(hostPort, 3000, null);

        while (true) {
            try {
                // Verifica si el nodo "/mi_nodo" existe
                Stat stat = zooKeeper.exists("/mi_nodo", false);

                // Imprime un mensaje según el estado del nodo
                if (stat == null) {
                    System.out.println("El servidor no está corriendo...");
                } else {
                    System.out.println("El servidor está corriendo...");
                }

                Thread.sleep(TIEMPO_ESPERA); // Espera antes de realizar la siguiente verificación
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

