public class Alumno {
    private String id;
    private String nombre;
    private String apellidos;
    private String curso;

    public Alumno() {}

    public Alumno(String id, String nombre, String apellidos, String curso) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.curso = curso;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    @Override
    public String toString() {
        return "Alumno{id='" + id + "', nombre='" + nombre + " " + apellidos +
                "', curso='" + curso + "'}";
    }
}
