
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class SalaVacunacion {

    private int max; //Número máximo de pacientes en la sala de vacunación
    private ArrayList<Puesto> puestos = new ArrayList<Puesto>();
    private ArrayList<JTextField> puestosVacunacion;
    private JTextField auxiliarVacunacion, numeroVacunas;
    private AtomicInteger contadorVacunas = new AtomicInteger(0);
    private SalaObservacion salaObservacion;    //Sala necesaria para que los pacientes pasen de vacunar a observar
    private BlockingQueue colaVacunar = new LinkedBlockingDeque(); //Cola de espera hasta que el auxiliar indique a que puesto ir

    private Semaphore capacidadVacunacion; //Semaforo con la capacidad máxima de la sala de vacunación

    public SalaVacunacion(int max, JTextField auxiliarVacunacion, JTextField numeroVacunas, ArrayList<JTextField> puestosVacunacion) {
        this.max = max;
        this.puestosVacunacion = puestosVacunacion;
        for (int i = 0; i < puestosVacunacion.size(); i++) {
            Puesto nuevoPuesto = new Puesto(puestosVacunacion.get(i), true, true); //NO SE PUEDEN CREAR LOS JTEXTFIELD SE TIENEN QUE PASAR DEL MAIN
            puestos.add(nuevoPuesto);
        }
        this.auxiliarVacunacion = auxiliarVacunacion;
        this.numeroVacunas = numeroVacunas;
        this.capacidadVacunacion = new Semaphore(max);
    }

    public ArrayList<Puesto> getPuestos() {
        return puestos;
    }

    public boolean puestoLibre() {
        boolean resultado = false;
        for (int i = 0; i < puestos.size(); i++) {
            resultado = resultado || puestos.get(i).isDisponible();//falso o dispoible ->true, falso o no disponible ->false
        }
        return resultado;
    }

    //Método para el auxiliar 2, genera 20 dosis con el periodo indicado. Tiene un contador que llega hasta 20
    /**
     * *
     * En introducirDosis comprobar simultaneamente que haya hueco en Vacunacion
     * y Observacion, cuando haya en ambos toma un permiso de ambos semáforos y
     * ocupa primero el puesto de vacunación y cuando acabe el de observación
     *
     **
     */
    public void introducirDosis(Auxiliar auxiliar2) {
        //Primero ponemos el contador del auxiliar 2 a 0
        auxiliar2.getContadorAux2().set(0);
        while (auxiliar2.getContadorAux2().incrementAndGet() <= 20) { //Comprobamos el contador y lo incrementamos
            try {
                auxiliarVacunacion.setText(auxiliar2.toString());
                numeroVacunas.setText(contadorVacunas.incrementAndGet() + ""); //Incrementamos en 1 el número de vacunas y lo sacamos en el JTextField correspondiente
                auxiliar2.currentThread().sleep((int) (500 * Math.random() + 500)); //Tiempo que tarda en preparar una dosis
            } catch (Exception e) {
            }
        }
        auxiliarVacunacion.setText(""); //Actualizamos el JTextField cuando el auxiliar se va a descansar
    }

    //Los sanitarios se colocan en el puesto que tengan libre
    public void colocarSanitarios(Sanitario sanitario) {
        int i = 0;
        // Hacemos un bucle para que el sanitario elija puesto, una vez elegido el puesto se queda interrumpido para que salga del bucle while y solo ocupe un puesto
        while (!sanitario.isInterrupted() && i < puestos.size()) {
            if (puestos.get(i).isDisponible()) {
                puestos.get(i).entraSanitario();
                puestos.get(i).getJtfPuesto().setText(sanitario.toString());
                sanitario.setPuesto(i);
                sanitario.interrupt(); //Controlamos el comportamiento del sanitario
            }
            i++;
        }
    }

    public void entraPaciente(Paciente paciente) {
        //procedimiento para meter al paciente en la salaVacunacion
        //Aquí hay que meter al paciente en un puesto
        try {
            //Semaforo porque solo puede haber 10 como mucho en la cola porque se estarán vacunando
            capacidadVacunacion.acquire(); //Igual no hace falta ni la cola porque con el semaforo es suficiente

            colaVacunar.put(paciente); //Se mete al paciente en la cola
            synchronized (colaVacunar) {
                colaVacunar.notifyAll();//Cuando se hace release se avisa al auxiliar 1 que hay hueco en la sala de vacunacion

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void vacunarPaciente(Sanitario sanitario) {
        sanitario.interrupted(); // le quitamos el interrup al saniratio que se le ha puesto en colocar
        //Se resetea el contador cada vez que un sanitario va a empezar a vacunar
        sanitario.getContadoresSanitarios().set(0);
        //Mientras haya vacunas y el contador de los sanitarios no llegue a 
        while (contadorVacunas.get() > 1 && sanitario.getContadoresSanitarios().getAndIncrement() < 15) {
            while (colaVacunar.isEmpty()) {
                synchronized (colaVacunar) {
                    try {
                        colaVacunar.wait(); //Cuando se hace release se avisa al auxiliar 1 que hay hueco en la sala de vacunacion
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            //De esta manera los sanitarios esperan hasta que hay un paciente en su puesto 

            try {
                contadorVacunas.decrementAndGet(); //El sanitario utiliza una vacuna
                //Igual hacer un wait si no hay vacunas disponibles en vez de dejarlo en el while
                sleep((int) (2000 * Math.random() + 3000)); //El sanitario tarda entre 3 y 5 segundos en vacunar
                capacidadVacunacion.release();
                synchronized (capacidadVacunacion) {
                    capacidadVacunacion.notifyAll(); //Cuando se hace release se avisa al auxiliar 1 que hay hueco en la sala de vacunacion
                }
                puestos.get(sanitario.getPuesto()).setDisponiblePaciente(true);
                puestos.get(sanitario.getPuesto()).getJtfPuesto().setText(sanitario.toString()); // se actualiza el JTextField para el siguiente paciente
                try {
                    Paciente paciente = (Paciente) colaVacunar.take();  //Cuando lo sacamos de la cola le avisamos para que se vaya al puesto de observacion
                    synchronized (paciente.getRegistrado()) {
                        paciente.getRegistrado().notify();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public ArrayList<JTextField> getPuestosVacunacion() {
        return puestosVacunacion;
    }

    public void setPuestosVacunacion(ArrayList<JTextField> puestosVacunacion) {
        this.puestosVacunacion = puestosVacunacion;
    }

    public JTextField getAuxiliarVacunacion() {
        return auxiliarVacunacion;
    }

    public void setAuxiliarVacunacion(JTextField auxiliarVacunacion) {
        this.auxiliarVacunacion = auxiliarVacunacion;
    }

    public JTextField getNumeroVacunas() {
        return numeroVacunas;
    }

    public void setNumeroVacunas(JTextField numeroVacunas) {
        this.numeroVacunas = numeroVacunas;
    }

    public AtomicInteger getContadorVacunas() {
        return contadorVacunas;
    }

    public void setContadorVacunas(AtomicInteger contadorVacunas) {
        this.contadorVacunas = contadorVacunas;
    }

    public SalaObservacion getSalaObservacion() {
        return salaObservacion;
    }

    public void setSalaObservacion(SalaObservacion salaObservacion) {
        this.salaObservacion = salaObservacion;
    }

    public BlockingQueue getColaVacunar() {
        return colaVacunar;
    }

    public void setColaVacunar(BlockingQueue colaVacunar) {
        this.colaVacunar = colaVacunar;
    }

    public Semaphore getCapacidadVacunacion() {
        return capacidadVacunacion;
    }

    public void setCapacidadVacunacion(Semaphore capacidadVacunacion) {
        this.capacidadVacunacion = capacidadVacunacion;
    }

}
