public class Profesor implements Comparable<Profesor> {
    private final String nombre;
    private int numSustituciones;

    public Profesor(String nombre, int numSustituciones) {
        this.nombre = nombre;
        this.numSustituciones = numSustituciones;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getNumSustituciones() {
        return this.numSustituciones;
    }

    @Override
    public int compareTo(Profesor otro) {
        return Integer.compare(this.numSustituciones, otro.numSustituciones);
    }

    @Override
    public String toString() {
        return nombre + " (Sustituciones: " + numSustituciones + ")";
    }
}