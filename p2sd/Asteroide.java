//Proyecto 2 -- Rodriguez Olmos Noé -- 7CM2

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Asteroide {
    private List<Coordenada> vertices;
    private Random rand;
    private int windowWidth,windowHeight;

    public Asteroide(int numVertices, int windowWidth, int windowHeight) {
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        vertices = new ArrayList<Coordenada>();
        rand = new Random();

        for (int i = 0; i < numVertices; i++) {
            double angulo = Math.toRadians(i * (360.0 / numVertices));
            double radio = rand.nextDouble() * 80 + 60;

            double x = radio * Math.cos(angulo) + windowWidth / 2;
            double y = radio * Math.sin(angulo) + windowHeight / 2;
            Coordenada coordenada = new Coordenada(x, y);
            vertices.add(coordenada);
        }

        ajustarCentro();
    }

    public Polygon getPolygon() {
         Polygon p = new Polygon();
        for (Coordenada vertice : vertices) {
            p.addPoint((int) vertice.abcisa(), (int) vertice.ordenada());
            
        }
        return p;
    }

    public double obtienePerimetro() {
        Polygon p = getPolygonRandom();
        double perimetro = 0.0;
        int numVertices = p.npoints;

        for (int i = 0; i < numVertices; i++) {
            int x1 = p.xpoints[i];
            int y1 = p.ypoints[i];
            int x2 = p.xpoints[(i + 1) % numVertices];
            int y2 = p.ypoints[(i + 1) % numVertices];
            double distancia = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            perimetro += distancia;
        }

        return perimetro;
    }

    private void ajustarCentro() {
        if (vertices.isEmpty()) {
            return;
        }

        double sumX = 0.0;
        double sumY = 0.0;

        for (Coordenada punto : vertices) {
            sumX += punto.abcisa();
            sumY += punto.ordenada();
        }

        double x = sumX / vertices.size();
        double y = sumY / vertices.size();

        for (Coordenada vertice : vertices) {
            vertice = new Coordenada(vertice.abcisa() - (x - windowWidth / 2), vertice.ordenada() - (y - windowHeight / 2));
        }
    }

    public Polygon getPolygonRandom() {
        Polygon p = new Polygon();
        int x,y;
        x = rand.nextInt(100) + rand.nextInt(150);
        y = rand.nextInt(100) + rand.nextInt(150);
        for (Coordenada vertice : vertices) {
            p.addPoint((int) vertice.abcisa()+x, (int) vertice.ordenada()+y);
            
        }
        return p;
    }
}
