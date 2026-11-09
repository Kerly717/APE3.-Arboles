import enums.TipoUnidad;
import modelo.Estudiante;
import modelo.UnidadAcademica;
import negocio.ArbolEstudiantes;

public class PruebasValidaciones {
    public static void main(String[] args) {
        UnidadAcademica unidad = new UnidadAcademica(" eb ", " Educación Básica ", TipoUnidad.CARRERA);
        comprobar(unidad.getCodigo().equals("EB") && unidad.getNombre().equals("Educación Básica"),
                "Normalización de unidad");
        rechaza(() -> new UnidadAcademica(" ", "Carrera", TipoUnidad.CARRERA));
        rechaza(() -> unidad.setNombre(" "));
        Estudiante estudiante = new Estudiante(1, "0000000001", " Ana ", 8.0);
        comprobar(estudiante.getNombres().equals("Ana"), "Normalización de nombre");
        rechaza(() -> estudiante.setCedula("123"));
        rechaza(() -> estudiante.setPromedio(Double.NaN));
        rechaza(() -> estudiante.setPromedio(10.01));
        ArbolEstudiantes arbol = new ArbolEstudiantes();
        comprobar(arbol.insertar(estudiante), "Inserción inicial");
        rechaza(() -> arbol.modificar(1, "incorrecta", "Nuevo nombre", 9.0));
        comprobar(estudiante.getCedula().equals("0000000001")
                && estudiante.getNombres().equals("Ana") && estudiante.getPromedio() == 8.0,
                "Una modificación inválida conserva todos los datos");
        System.out.println("PruebasValidaciones: aprobadas");
    }

    private static void rechaza(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException esperado) {
            return;
        }
        throw new AssertionError("Se esperaba IllegalArgumentException");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
