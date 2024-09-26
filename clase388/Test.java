import java.io.*;

class Test {
    public static void main(String[] args) {
        Demo object = new Demo(2022, "Prueba serializacion y deserializacion");
        Demo objeto = null;
        byte[] serializado = SerializationUtils.serialize(object);
        System.out.println(serializado);
        objeto = (Demo) SerializationUtils.deserialize(serializado);
        System.out.println("a = " + objeto.a);
        System.out.println("b = " + objeto.b);
    }
}