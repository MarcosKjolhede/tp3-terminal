# Trabajo Práctico 3 — Colecciones, excepciones y genericidad
UTN. Tecnicatura universitaria en programación. Programación II. Trabajo práctico número 3

## Integrantes
* Marcos Kjolhede
* Ezequiel Cepeda
* Bautista Arevalo

## Compilacion y Ejecucion

### 1. Compilacion
Desde la carpeta raiz del repositorio, ejecutar: 
````
javac -d bin src/modelo/*.java src/app/*.java
````
### 2. Ejecucion
````
java -cp bin app.Main
````
## Preguntas

### 1. Elijan una excepcion propia y expliquen por que la hicieron checked o unchecked.
Elegimos 'ServicioInexistenteException' como **checked**(extends Exception) porque el error surge al leer un codigo raro en el archivo 'ventas.txt'. Al ser checked, java nos obliga a ponerle un 'try-catch' dentro del bucle de lectura, de esa forma capturamos la falla, mostramos que linea fallo y el programa sigue procesando el resto del archivo sin colgarse.

### 2. La lista de espera se uso con dos tipos. ¿Que habria que haber escrito sin genericidad?
Sin genericidad nos habria tocado duplicar el codigo creando dos clases separadas ('ListaEsperaPasajero' y 'ListaEsperaReclamo'), o bien guardar todo como 'Object'. Si usabamos 'Object', tendriamos que hacer casteo manual a cada rato y nos arriesgabamos a que salte un 'ClassCastException' en ejecucion. Con '<T>' reutilizamos la lista y el compilador nos asegura el tipo.

### 3.¿Por que pasajero necesita hashCode, si ningun informe lo llama?
Aunque nosotros no invoquemos 'hashCode()' a mano, las colecciones como 'HashSet' o 'HashMap' lo usan internamente para calcular en que casillero guardar o buscar cada elemento antes de comparar con 'equals()'. Si no lo sobreescribimos, dos objetos 'Pasajero' con el mismo DNI generan numeros de hash distintos y la coleccion no puede detectar duplicados ni encontrarlos.