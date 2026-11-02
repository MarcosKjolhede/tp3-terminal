# Justificación de la selección de colecciones (TP3 - Punto B.1)

Servicios, buscados por su código( Map<String, Servicio>) ( LinkedHashMap) La operación más frecuente es buscar un servicio por su código. Un mapa lo resuelve directo, sin recorrer una lista. Las claves no se repiten, así que el mapa garantiza que no haya dos servicios con el mismo código. (LinkedHashMap) además conserva el orden de carga al listarlos. 

*Pasajes de un servicio, por número de asiento (Map<Integer, Pasaje>) (TreeMap) Se consulta por número de asiento y las claves únicas garantizan que un asiento se venda una sola vez. Elegimos (TreeMap) porque el informe B.6 exige listar los pasajeros ordenados por asiento: así el recorrido ya sale en orden, sin ordenar a mano.
 
*Lista de espera de un servicio (ListaDeEspera<Pasajero>) (ArrayDeque) dentro de (ListaDeEspera). Se necesita orden de llegada: el primero que entra es el primero que sale (FIFO), que es lo que modela una cola. La clase genérica agrega el límite de 5 y avisa cuando está llena. 

*Lo recaudado por cada empresa (Map<String, Double>) (TreeMap) La clave es el nombre de la empresa y el valor el total acumulado, que se va sumando al recorrer los servicios. (TreeMap) hace que el informe salga ordenado alfabéticamente.

