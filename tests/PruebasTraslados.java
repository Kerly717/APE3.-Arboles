import app.DatosPrueba;
import enums.TipoRecorrido;
import enums.TipoUnidad;
import modelo.Estudiante;
import modelo.UnidadAcademica;
import negocio.ArbolEstudiantes;
import negocio.ArbolOrganizacional;

public class PruebasTraslados {
    private static int comprobaciones;

    private static void verificar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static ArbolOrganizacional inicial() {
        ArbolOrganizacional arbol = new ArbolOrganizacional(DatosPrueba.universidad());
        DatosPrueba.cargar(arbol);
        return arbol;
    }

    private static String matriculas(ArbolEstudiantes abb) {
        StringBuilder sb = new StringBuilder();
        for (Estudiante e : abb.recorrer(TipoRecorrido.INORDEN)) {
            if (sb.length() > 0) sb.append(',');
            sb.append(e.getMatricula());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ArbolOrganizacional arbol = inicial();
        ArbolEstudiantes a = arbol.buscar("EB-N1-A").getEstudiantes();
        ArbolEstudiantes b = arbol.buscar("EB-N1-B").getEstudiantes();

        // Traslado válido: la misma instancia pasa de A a B.
        Estudiante e10 = a.buscar(10);
        verificar(arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 10), "Traslado válido de una hoja");
        verificar(a.buscar(10) == null && b.buscar(10) == e10, "Se conserva la misma instancia");
        verificar(a.contar() == 8 && b.contar() == 1, "Conteos tras el primer traslado");

        // Traslados de nodos con uno y dos hijos y de la raíz del ABB.
        verificar(arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 40), "Traslado de nodo con un hijo");
        verificar(arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 45), "Traslado de nodo con dos hijos");
        verificar(arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 30), "Traslado de la raíz");
        verificar(matriculas(a).equals("15,20,35,50,55"), "Origen queda ordenado");
        verificar(matriculas(b).equals("10,30,40,45"), "Destino queda ordenado");

        // Casos rechazados: el estado no debe cambiar.
        verificar(!arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 99), "Matrícula inexistente");
        verificar(!arbol.trasladarEstudiante("EB-N1-A", "NO-EXISTE", 15), "Destino inexistente");
        verificar(!arbol.trasladarEstudiante("NO-EXISTE", "EB-N1-B", 15), "Origen inexistente");
        verificar(!arbol.trasladarEstudiante("EB-N1-A", "EB-N1-A", 15), "Origen igual a destino");
        verificar(!arbol.trasladarEstudiante("EB-N1-A", "EB-N1", 15), "Destino que no es paralelo");

        // Duplicado: la matrícula ya existe en el destino.
        a.insertar(new Estudiante(10, "0000000099", "Duplicado", 7.5));
        verificar(!arbol.trasladarEstudiante("EB-N1-A", "EB-N1-B", 10), "Duplicado en destino");
        verificar(a.buscar(10) != null && b.buscar(10) == e10, "Duplicado no altera ningún paralelo");
        a.eliminar(10);

        verificar(a.contar() == 5 && b.contar() == 4, "Conteos sin cambios tras rechazos");

        // Traslado hacia un paralelo agregado después de la carga inicial.
        arbol.agregar("EB-N1", new UnidadAcademica("EB-N1-C", "Paralelo C", TipoUnidad.PARALELO));
        ArbolEstudiantes c = arbol.buscar("EB-N1-C").getEstudiantes();
        verificar(arbol.trasladarEstudiante("EB-N1-B", "EB-N1-C", 30), "Traslado a paralelo nuevo");
        verificar(c.contar() == 1 && b.buscar(30) == null, "Paralelo nuevo recibe al estudiante");

        System.out.println("APROBADAS: " + comprobaciones + " comprobaciones de traslados");
    }
}
