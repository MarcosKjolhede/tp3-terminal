package modelo;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class ListaDeEspera<T> {
    private Queue<T> elementos;
    private int capacidadMaxima;

    public ListaDeEspera(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.elementos = new LinkedList<>();
    }

    public boolean agregar(T elemento) {
        if (elementos.size() >= capacidadMaxima) {
            return false;
        }
        return elementos.offer(elemento);
    }

    // Entrega el siguiente sin devolver null (manda excepción si está vacía)
    public T siguiente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista de espera está vacía.");
        }
        return elementos.poll();
    }

    public boolean estaVacia() {
        return elementos.isEmpty();
    }

    public int cantidad() {
        return elementos.size();
    }

    public void imprimirLista() {
        if (estaVacia()) {
            System.out.println("  (Vacía)");
            return;
        }
        int pos = 1;
        for (T elem : elementos) {
            System.out.println("  " + pos + ". " + elem);
            pos++;
        }
    }
}

