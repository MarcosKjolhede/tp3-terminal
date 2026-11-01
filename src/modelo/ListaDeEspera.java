package modelo;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class ListaDeEspera<T> {
    private final Queue<T> elementos = new ArrayDeque<>();
    private final int capacidadMaxima;

    public ListaDeEspera(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor que 0.");
        }
        this.capacidadMaxima = capacidadMaxima;
    }

    //Agrega al final. Devuelve false si no pudo porque está llena
    public boolean agregar(T elemento) {
        if (estaLlena()) {
            return false;
        }
        elementos.add(elemento);
        return true;
    }

    //Entrega y saca el primero. Si está vacía devuelve Optional.empty(), nunca null
    public Optional<T> siguiente() {
        return Optional.ofNullable(elementos.poll());
    }

    public boolean estaVacia() {
        return elementos.isEmpty();
    }

    public boolean estaLlena() {
        return elementos.size() >= capacidadMaxima;
    }

    public int cantidad() {
        return elementos.size();
    }

    // Copia en orden de llegada, para informes (no expone la cola interna)
    public List<T> comoLista() {
        return new ArrayList<>(elementos);
    }
}