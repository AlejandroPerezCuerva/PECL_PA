
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    private JTextField salidaTextField;
    private BlockingQueue capacidadObservacion;
    private BlockingQueue pacientesObservacion;

    public SalaObservacion(int aforoObservacion, ArrayList<JTextField> puestosObservacion, JTextField salidaTextField, BlockingQueue pacientesObservacion) {
        this.aforoObservacion = aforoObservacion;
        this.puestosObservacion = puestosObservacion;
        //Añadimos los puestos de observación a un array de la clase puestos para tener un mayor control sobre ellos
        for (int i = 0; i < aforoObservacion; i++) {
            Puesto nuevoPuesto = new Puesto(puestosObservacion.get(i), true, true);
            puestos.add(nuevoPuesto);
        }
        this.capacidadObservacion = new LinkedBlockingQueue(aforoObservacion);
        this.salidaTextField = salidaTextField;
        this.pacientesObservacion = pacientesObservacion;
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
            paciente.getSalaVacunacion().getPuestos().get(paciente.getPuesto()).setDisponiblePaciente(true); //Hasta que el paciente no entra en la sala de observación no deja libre su puesto en la sala de vacunación

            capacidadObservacion.put(paciente);//Entra el paciente y el semaforo hace aquire
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
            sleep(10000); //El paciente está 10 segundos es la observación 
            if (reaccion) {
                System.out.println("***El paciente " + paciente.toString() + " se encuentra mal***");
                pacientesObservacion.put(paciente);
                paciente.getReaccionVacuna().set(true);//El paciente tiene una reaccion a la vacuna                
            } else {
                paciente.getSemObservar().release();
                puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Ponemos que está libre el puesto del paciente porque ya ha terminado
                capacidadObservacion.take();
                puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");
                paciente.getRecepcion().getSemSalasOcupadas().release(); //Se avisa de que hay hueco en la sala de observación
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaObservacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void atenderPaciente(Sanitario sanitario, Paciente paciente) throws InterruptedException {
        /*
        mirar si es mejor que se haga el take directamente aquí en el metodo
        y no en el run del sanitario
         */

        //Cogemos lo que hay en el JTextField y le añadimos el sanitario para que le cure
        String puestoCurar = "";
        puestoCurar = puestos.get(paciente.getPuesto()).getJtfPuesto().getText() + "," + sanitario.toString();
        puestos.get(paciente.getPuesto()).getJtfPuesto().setText(puestoCurar);

        //El sanitario y el pacienten hacen el sleep para ver la reacción del paciente a la vacuna
        sleep((int) (3000 * Math.random() + 2000));//Tarda entre 2 y 5 segundos
        paciente.getReaccionVacuna().set(false);
        paciente.getSemObservar().release();
        System.out.println("***El paciente " + paciente.toString() + " tiene el visto bueno***");
        puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Ponemos que está libre el puesto del paciente porque ya ha terminado
        capacidadObservacion.take();
        puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");
        paciente.getRecepcion().getSemSalasOcupadas().release(); //Se avisa de que hay hueco en la sala de observación

    }

    public void setPacientesObservacion(BlockingQueue pacientesObservacion) {
        this.pacientesObservacion = pacientesObservacion;
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

    public int getAforoObservacion() {
        return aforoObservacion;
    }

    public void setAforoObservacion(int aforoObservacion) {
        this.aforoObservacion = aforoObservacion;
    }

    public JTextField getSalidaTextField() {
        return salidaTextField;
    }

    public void setSalidaTextField(JTextField salidaTextField) {
        this.salidaTextField = salidaTextField;
    }

    public BlockingQueue getCapacidadObservacion() {
        return capacidadObservacion;
    }

    public void setCapacidadObservacion(BlockingQueue capacidadObservacion) {
        this.capacidadObservacion = capacidadObservacion;
    }

    public BlockingQueue getPacientesObservacion() {
        return pacientesObservacion;
    }

}
