
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author aleja
 */
public class Sanitario extends Thread {

    private String id;
    private SalaDescanso salaDescanso; //Se le pasa como parámetro porque necesita acceder a ella
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;
    private ArrayList<AtomicInteger> contadoresSanitarios;

    public Sanitario(int num, SalaDescanso salaDescanso, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, ArrayList<AtomicInteger> contadoresSanitarios) {
        id = "S" + String.format("%02d", num);
        this.salaDescanso = salaDescanso;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.contadoresSanitarios = contadoresSanitarios;
        for (int i = 0; i < 10; i++) {
            contadoresSanitarios.add(new AtomicInteger(0));
        } 
    }

    public void run() {
        //Lo primero que hacen es ir a la sala de descanso donde tardan en cambiarse entre 1 y 3 segundos
        salaDescanso.cambiarseSanitario(this);
        //Ahora los sanitarios se tienen que ir a la sala de vacunación para meterse cada uno en su puesto
        while (true) {

        }
    }

    public ArrayList<AtomicInteger> getContadoresSanitarios() {
        return contadoresSanitarios;
    }

    public void setContadoresSanitarios(ArrayList<AtomicInteger> contadoresSanitarios) {
        this.contadoresSanitarios = contadoresSanitarios;
    }

    @Override
    public String toString() {
        return id;
    }

}
