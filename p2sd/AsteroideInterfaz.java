//Proyecto 2 -- Rodriguez Olmos Noé -- 7CM2

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class AsteroideInterfaz extends JFrame {
    private List<Asteroide> asteroides = new ArrayList<>();
    private JPanel panel;
    private int asteroideActual = 0;
    private boolean mostrarAleatorios = true;
    private Timer timer;

    public AsteroideInterfaz() {
        setTitle("RODRIGUEZ_N PRACTICA 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                planoXY(g);

                if (mostrarAleatorios) {
                    imprimirAsteroideAleatorio(g);
                } else {
                    dibujarAsteroidesOrdenados(g);
                }
            }
        };

        add(panel);

        int sides = obtenerNumeroAsteroides();
        crearAsteroides(sides);
        iniciarAnimacion();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AsteroideInterfaz app = new AsteroideInterfaz();
            app.setVisible(true);
        });
    }

    private static int obtenerNumeroAsteroides() {
        Scanner scanner = new Scanner(System.in);
        int numAsteroides = 0;

        boolean entradaValida = false;
        while (!entradaValida) {
            System.out.print("Teclea el No. de asteroides --->");
            if (scanner.hasNextInt()) {
                numAsteroides = scanner.nextInt();
                entradaValida = true;
            } else {
                System.out.println("Prueba con un numero");
                scanner.nextLine();
            }
        }
        scanner.close();
        return numAsteroides;
    }

    private void crearAsteroides(int n) {
        asteroides.clear();

        for (int i = 0; i < n; i++) {
            Asteroide asteroide = new Asteroide((int) (Math.random() * 10) + 5, getWidth(), getHeight());
            asteroides.add(asteroide);
            System.out.println("add asteroid" + i);
        }
        asteroideActual = 0;
        panel.repaint();
    }

    private void dibujarAsteroidesOrdenados(Graphics g) {
        if (!asteroides.isEmpty()) {
            g.setColor(Color.blue);
            for (int i = 0; i <= asteroideActual; i++) {
                Asteroide asteroide = asteroides.get(i % asteroides.size());
                Polygon polygon = asteroide.getPolygon();
                g.drawPolygon(polygon);
            }
            
            if (asteroideActual >= asteroides.size()) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
            }
        }
    }

    private void iniciarAnimacion() {
        timer = new Timer(500, e -> {
            asteroideActual++;
            if (mostrarAleatorios) {
                if (asteroideActual >= asteroides.size()) {
                    mostrarAleatorios = false;
                    asteroideActual = 0;
                }
            } else {
                if (asteroideActual >= asteroides.size()) {
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    limpiarPantalla();
                    Collections.sort(asteroides, Comparator.comparingDouble(Asteroide::obtienePerimetro));
                    asteroideActual = 0;
                    mostrarAleatorios = true;
                    iniciarAnimacion();
                }
            }
            panel.repaint();
        });
        timer.start();
    }

    private void limpiarPantalla() {
        panel.repaint();
    }

    private void planoXY(Graphics g) {
        g.setColor(Color.black);
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
    }

    private void imprimirAsteroideAleatorio(Graphics g) {
        if (!asteroides.isEmpty() && asteroideActual < asteroides.size()) {
            Asteroide ast = asteroides.get(asteroideActual % asteroides.size());
            g.setColor(Color.BLACK);
            Polygon polygon = ast.getPolygonRandom();
            g.drawPolygon(polygon);
        }
    }
}
