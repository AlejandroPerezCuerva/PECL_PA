
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class SalaObservacion {
    
    private int aforoObservacion, puestoLibre;
    private ArrayList<Puesto> puestos = new ArrayList<Puesto>();
    private ArrayList<JTextField> puestosObservacion = new ArrayList<JTextField>();
    private Semaphore capacidadObservacion; //Semaforo con la capacidad máxima de la sala de observacion
    private JTextField salidaTextField;
    
    public SalaObservacion(int aforoObservacion, ArrayList<JTextField> puestosObservacion, JTextField salidaTextField) {
        this.aforoObservacion = aforoObservacion;
        this.puestosObservacion = puestosObservacion;
        //Añadimos los puestos de observación a un array de la clase puestos para tener un mayor control sobre ellos
        for (int i = 0; i < aforoObservacion; i++) {
            Puesto nuevoPuesto = new Puesto(puestosObservacion.get(i), true, true);
            puestos.add(nuevoPuesto);
        }
        this.capacidadObservacion = new Semaphore(aforoObservacion); //Se inicializa el semaforo con la capacidad máxima de aforo
        this.salidaTextField = salidaTextField;
    }
    
    public boolean puestoLibre() {
        boolean resultado = false;
        for (int i = 0; i < puestos.size(); i++) {
            resultado = resultado | puestos.get(i).isDisponible();//falso o dispoible ->true, falso o no disponible ->false
            if (resultado) {
                puestoLibre = i;
            }
        }
        return resultado;
    }
    
    public void entraPaciente(Paciente paciente) {
        try {
            capacidadObservacion.acquire(); //Entra el paciente y el semaforo hace aquire
            int i = 0;
            boolean puestoObtenido = false;
            while (!puestoObtenido && i < puestos.size()) {
                if (puestos.get(i).isDisponiblePaciente()) {
                    puestos.get(i).setDisponiblePaciente(false);
                    paciente.setPuesto(i);
                    puestoObtenido = true;
                    puestos.get(i).getJtfPuesto().setText(paciente.toString());
                }
                i++;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaObservacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //El paciente está en observación y un 5% se ponen malos y los sanitarios le cuidan
    public void pacienteEnObservacion(Paciente paciente) {
        boolean reaccion = (int) (100 * Math.random()) <= 5; //En el 5% de los casos el paciente sufre efectos adversos
        try {
            paciente.currentThread().sleep(10000); //El paciente está 10 segundos es la observación 
           /* if (reaccion) {
                puestos.get(paciente.getPuesto()).getAtendido().set(false);//El puesto necesita ser atendido
                paciente.getReaccionVacuna().set(true);//El paciente tiene una reaccion a la vacuna
                while (paciente.getReaccionVacuna().equals(true)) {//Mientras el sanitario no determine que no tiene reaccion espera
                    try {
                        wait();
                    } catch (Exception e) {
                    }
                }
            }*/
            puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Ponemos que está libre el puesto del paciente porque ya ha terminado
            capacidadObservacion.release();
            puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");
            synchronized (capacidadObservacion) {
                capacidadObservacion.notifyAll(); //Cuando se hace release se avisa al auxiliar 1 que hay hueco en la sala de observación
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaObservacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void salirHospital(Paciente paciente) {
        salidaTextField.setText(paciente.toString());
    }
    
    public int getPuestoLibre() {
        return puestoLibre;
    }
    
    public void setPuestoLibre(int puestoLibre) {
        this.puestoLibre = puestoLibre;
    }
    
    public ArrayList<Puesto> getPuestos() {
        return puestos;
    }
    
    public void setPuestos(ArrayList<Puesto> puestos) {
        this.puestos = puestos;
    }
    
    public ArrayList<JTextField> getPuestosObservacion() {
        return puestosObservacion;
    }
    
    public void setPuestosObservacion(ArrayList<JTextField> puestosObservacion) {
        this.puestosObservacion = puestosObservacion;
    }
    
    public Semaphore getCapacidadObservacion() {
        return capacidadObservacion;
    }
    
    public void setCapacidadObservacion(Semaphore capacidadObservacion) {
        this.capacidadObservacion = capacidadObservacion;
    }
    
}
