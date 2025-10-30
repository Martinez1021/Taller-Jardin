import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ausencia {
    private String id;
    private String alumnoId;
    private String asignaturaId;
    private LocalDateTime fecha;
    private String tipo; // "falta", "retraso", "justificada"

    public Ausencia() {}

    public Ausencia(String id, String alumnoId, String asignaturaId,
                    LocalDateTime fecha, String tipo) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.asignaturaId = asignaturaId;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAlumnoId() { return alumnoId; }
    public void setAlumnoId(String alumnoId) { this.alumnoId = alumnoId; }
    public String getAsignaturaId() { return asignaturaId; }
    public void setAsignaturaId(String asignaturaId) { this.asignaturaId = asignaturaId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Ausencia{id='" + id + "', alumnoId='" + alumnoId +
                "', asignaturaId='" + asignaturaId + "', fecha=" +
                fecha.format(formatter) + ", tipo='" + tipo + "'}";
    }
}
