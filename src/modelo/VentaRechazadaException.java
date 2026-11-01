package modelo;
/*
 Cubre las reglas(asiento invAlido,
 fuera de rango, ocupado, espera llena). La mete en el modelo en cualquier
 punto de venta, y se atrapa en un unico lugar (el procesamiento del archivo).
 */
public class VentaRechazadaException extends RuntimeException{
    public VentaRechazadaException(String mensaje){
        super(mensaje);
    }
    public VentaRechazadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
