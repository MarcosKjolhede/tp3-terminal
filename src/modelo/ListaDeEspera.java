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
        if(capacidadMaxima <=0 ){
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor que 0.");
        }
        this.capacidadMaxima = capacidadMaxima;
    }

    public boolean agregar(T elemento) {
        if (elementos.size() >= capacidadMaxima) {
            return false;
        }
        return elementos.offer(elemento);
    }

    public T siguiente() {
        return elementos.remove(); 
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