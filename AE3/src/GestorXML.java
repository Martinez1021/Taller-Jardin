import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GestorXML {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // ============ CRUD ALUMNOS ============

    public List<Alumno> leerAlumnos(String rutaArchivo) throws Exception {
        List<Alumno> alumnos = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return alumnos;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(archivo);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("alumno");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Alumno alumno = new Alumno();
                alumno.setId(element.getAttribute("id"));
                alumno.setNombre(getElementValue(element, "nombre"));
                alumno.setApellidos(getElementValue(element, "apellidos"));
                alumno.setCurso(getElementValue(element, "curso"));
                alumnos.add(alumno);
            }
        }
        return alumnos;
    }

    public void crearAlumno(String rutaArchivo, Alumno alumno) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc;

        File archivo = new File(rutaArchivo);
        if (archivo.exists()) {
            doc = builder.parse(archivo);
        } else {
            doc = builder.newDocument();
            Element rootElement = doc.createElement("alumnos");
            doc.appendChild(rootElement);
        }

        Element root = doc.getDocumentElement();
        Element alumnoElement = doc.createElement("alumno");
        alumnoElement.setAttribute("id", alumno.getId());

        alumnoElement.appendChild(crearElemento(doc, "nombre", alumno.getNombre()));
        alumnoElement.appendChild(crearElemento(doc, "apellidos", alumno.getApellidos()));
        alumnoElement.appendChild(crearElemento(doc, "curso", alumno.getCurso()));

        root.appendChild(alumnoElement);
        guardarDocumento(doc, rutaArchivo);
    }

    public void actualizarAlumno(String rutaArchivo, Alumno alumno) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(rutaArchivo));

        NodeList nodeList = doc.getElementsByTagName("alumno");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equals(alumno.getId())) {
                actualizarElemento(element, "nombre", alumno.getNombre());
                actualizarElemento(element, "apellidos", alumno.getApellidos());
                actualizarElemento(element, "curso", alumno.getCurso());
                break;
            }
        }

        guardarDocumento(doc, rutaArchivo);
    }

    public void eliminarAlumno(String rutaArchivo, String id) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(rutaArchivo));

        NodeList nodeList = doc.getElementsByTagName("alumno");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equals(id)) {
                element.getParentNode().removeChild(element);
                break;
            }
        }

        guardarDocumento(doc, rutaArchivo);
    }

    public Alumno buscarAlumnoPorId(String rutaArchivo, String id) throws Exception {
        List<Alumno> alumnos = leerAlumnos(rutaArchivo);
        for (Alumno alumno : alumnos) {
            if (alumno.getId().equals(id)) {
                return alumno;
            }
        }
        return null;
    }

    // ============ CRUD ASIGNATURAS ============

    public List<Asignatura> leerAsignaturas(String rutaArchivo) throws Exception {
        List<Asignatura> asignaturas = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return asignaturas;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(archivo);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("asignatura");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Asignatura asignatura = new Asignatura();
                asignatura.setId(element.getAttribute("id"));
                asignatura.setNombre(getElementValue(element, "nombre"));
                asignatura.setCurso(getElementValue(element, "curso"));
                asignatura.setCreditos(Integer.parseInt(getElementValue(element, "creditos")));
                asignaturas.add(asignatura);
            }
        }
        return asignaturas;
    }

    public void crearAsignatura(String rutaArchivo, Asignatura asignatura) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc;

        File archivo = new File(rutaArchivo);
        if (archivo.exists()) {
            doc = builder.parse(archivo);
        } else {
            doc = builder.newDocument();
            Element rootElement = doc.createElement("asignaturas");
            doc.appendChild(rootElement);
        }

        Element root = doc.getDocumentElement();
        Element asignaturaElement = doc.createElement("asignatura");
        asignaturaElement.setAttribute("id", asignatura.getId());

        asignaturaElement.appendChild(crearElemento(doc, "nombre", asignatura.getNombre()));
        asignaturaElement.appendChild(crearElemento(doc, "curso", asignatura.getCurso()));
        asignaturaElement.appendChild(crearElemento(doc, "creditos",
                String.valueOf(asignatura.getCreditos())));

        root.appendChild(asignaturaElement);
        guardarDocumento(doc, rutaArchivo);
    }

    public void actualizarAsignatura(String rutaArchivo, Asignatura asignatura) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(rutaArchivo));

        NodeList nodeList = doc.getElementsByTagName("asignatura");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equals(asignatura.getId())) {
                actualizarElemento(element, "nombre", asignatura.getNombre());
                actualizarElemento(element, "curso", asignatura.getCurso());
                actualizarElemento(element, "creditos", String.valueOf(asignatura.getCreditos()));
                break;
            }
        }

        guardarDocumento(doc, rutaArchivo);
    }

    public void eliminarAsignatura(String rutaArchivo, String id) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(rutaArchivo));

        NodeList nodeList = doc.getElementsByTagName("asignatura");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equals(id)) {
                element.getParentNode().removeChild(element);
                break;
            }
        }

        guardarDocumento(doc, rutaArchivo);
    }

    public Asignatura buscarAsignaturaPorId(String rutaArchivo, String id) throws Exception {
        List<Asignatura> asignaturas = leerAsignaturas(rutaArchivo);
        for (Asignatura asignatura : asignaturas) {
            if (asignatura.getId().equals(id)) {
                return asignatura;
            }
        }
        return null;
    }

    // ============ CRUD AUSENCIAS ============

    public List<Ausencia> leerAusencias(String rutaArchivo) throws Exception {
        List<Ausencia> ausencias = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return ausencias;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(archivo);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("ausencia");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Ausencia ausencia = new Ausencia();
                ausencia.setId(element.getAttribute("id"));
                ausencia.setAlumnoId(getElementValue(element, "alumnoId"));
                ausencia.setAsignaturaId(getElementValue(element, "asignaturaId"));
                ausencia.setFecha(LocalDateTime.parse(
                        getElementValue(element, "fecha"), formatter));
                ausencia.setTipo(getElementValue(element, "tipo"));
                ausencias.add(ausencia);
            }
        }
        return ausencias;
    }

    public void crearAusencia(String rutaArchivo, Ausencia ausencia) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc;

        File archivo = new File(rutaArchivo);
        if (archivo.exists()) {
            doc = builder.parse(archivo);
        } else {
            doc = builder.newDocument();
            Element rootElement = doc.createElement("ausencias");
            doc.appendChild(rootElement);
        }

        Element root = doc.getDocumentElement();
        Element ausenciaElement = doc.createElement("ausencia");
        ausenciaElement.setAttribute("id", ausencia.getId());

        ausenciaElement.appendChild(crearElemento(doc, "alumnoId", ausencia.getAlumnoId()));
        ausenciaElement.appendChild(crearElemento(doc, "asignaturaId", ausencia.getAsignaturaId()));
        ausenciaElement.appendChild(crearElemento(doc, "fecha",
                ausencia.getFecha().format(formatter)));
        ausenciaElement.appendChild(crearElemento(doc, "tipo", ausencia.getTipo()));

        root.appendChild(ausenciaElement);
        guardarDocumento(doc, rutaArchivo);
    }

    public void actualizarAusencia(String rutaArchivo, Ausencia ausencia) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(rutaArchivo));

        NodeList nodeList = doc.getElementsByTagName("ausencia");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equals(ausencia.getId())) {
                actualizarElemento(element, "alumnoId", ausencia.getAlumnoId());
                actualizarElemento(element, "asignaturaId", ausencia.getAsignaturaId());
                actualizarElemento(element, "fecha", ausencia.getFecha().format(formatter));
                actualizarElemento(element, "tipo", ausencia.getTipo());
                break;
            }
        }

        guardarDocumento(doc, rutaArchivo);
    }

    public void eliminarAusencia(String rutaArchivo, String id) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(rutaArchivo));

        NodeList nodeList = doc.getElementsByTagName("ausencia");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equals(id)) {
                element.getParentNode().removeChild(element);
                break;
            }
        }

        guardarDocumento(doc, rutaArchivo);
    }

    public List<Ausencia> buscarAusenciasPorAlumno(String rutaArchivo, String alumnoId) throws Exception {
        List<Ausencia> ausencias = leerAusencias(rutaArchivo);
        List<Ausencia> resultado = new ArrayList<>();
        for (Ausencia ausencia : ausencias) {
            if (ausencia.getAlumnoId().equals(alumnoId)) {
                resultado.add(ausencia);
            }
        }
        return resultado;
    }

    // ============ MÉTODOS AUXILIARES ============

    private String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    private Element crearElemento(Document doc, String nombre, String valor) {
        Element elemento = doc.createElement(nombre);
        elemento.appendChild(doc.createTextNode(valor));
        return elemento;
    }

    private void actualizarElemento(Element parent, String tagName, String nuevoValor) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            nodeList.item(0).setTextContent(nuevoValor);
        }
    }

    private void guardarDocumento(Document doc, String rutaArchivo) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(rutaArchivo));
        transformer.transform(source, result);
    }
}
