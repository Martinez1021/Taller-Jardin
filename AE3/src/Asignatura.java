public class Asignatura {
    private String id;
    private String nombre;
    private String curso;
    private int creditos;

    public Asignatura() {}

    public Asignatura(String id, String nombre, String curso, int creditos) {
        this.id = id;
        this.nombre = nombre;
        this.curso = curso;
        this.creditos = creditos;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    @Override
    public String toString() {
        return "Asignatura{id='" + id + "', nombre='" + nombre +
                "', creditos=" + creditos + "}";
    }
}
