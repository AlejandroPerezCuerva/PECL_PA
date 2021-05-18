package Principal;

import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    private BufferedWriter fichero;
    private Date objDate;
    private DateFormat diaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public SalaObservacion(int aforoObservacion, ArrayList<JTextField> puestosObservacion, JTextField salidaTextField, BlockingQueue pacientesObservacion, BufferedWriter fichero) {
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
        this.fichero = fichero;
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

    public void entraPaciente(Paciente paciente) throws InterruptedException {
        paciente.getSalaVacunacion().getPuestos().get(paciente.getPuesto()).setDisponiblePaciente(true); //Hasta que el paciente no entra en la sala de observación no deja libre su puesto en la sala de vacunación
        capacidadObservacion.put(paciente);//Entra el paciente a la cola que es bloqueante segun el aforo que haya
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
    }

    //El paciente está en observación y un 5% se ponen malos y los sanitarios le cuidan
    public void pacienteEnObservacion(Paciente paciente) throws InterruptedException, IOException {
        boolean reaccion = (int) (100 * Math.random()) <= 5; //En el 5% de los casos el paciente sufre efectos adversos

        sleep(10000); //El paciente está 10 segundos es la observación 
        if (reaccion) {
            capacidadObservacion.take(); //También hay que sacar al paciente de esta lista
            objDate = new Date();
            String mensaje = diaHora.format(objDate) + "\t\tEl paciente " + paciente.toString() + " muestra sintomas\n";
            System.out.print(mensaje);
            fichero.write(mensaje);
            fichero.flush();
            pacientesObservacion.put(paciente);
            paciente.getReaccionVacuna().set(true);//El paciente tiene una reaccion a la vacuna                
        } else {
            paciente.getSemObservar().release();
            puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Ponemos que está libre el puesto del paciente porque ya ha terminado
            capacidadObservacion.take();
            puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");

            //IGUAL QUE EN VACUNACIÓN, HAY QUE BUSCAR UN METODO MEJOR PARA AVISAR AL AUXILIAR 1
            paciente.getRecepcion().getSemSalasOcupadas().release(); //Se avisa de que hay hueco en la sala de observación
        }

    }

    public void atenderPaciente(Sanitario sanitario) throws InterruptedException, IOException {
        //Cuando llega el sanitario, saca al paciente que va a atender de la cola        
        Paciente paciente = (Paciente) pacientesObservacion.take();
        objDate = new Date();
        String mensaje = diaHora.format(objDate) + "\t\tPaciente " + paciente.toString() + " sufre una reacción y es atendido por " + sanitario.toString() + "\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();

        //Cogemos lo que hay en el JTextField y le añadimos el sanitario para que le cure
        String puestoCurar = "";
        puestoCurar = puestos.get(paciente.getPuesto()).getJtfPuesto().getText() + "," + sanitario.toString();
        puestos.get(paciente.getPuesto()).getJtfPuesto().setText(puestoCurar);

        //El sanitario y el pacienten hacen el sleep para ver la reacción del paciente a la vacuna
        sleep((int) (3000 * Math.random() + 2000));//Tarda entre 2 y 5 segundos
        paciente.getReaccionVacuna().set(false);
        paciente.getSemObservar().release();
        objDate = new Date();
        mensaje = diaHora.format(objDate) + "\t\tAl paciente " + paciente.toString() + " se le da el alta\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();

        puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Ponemos que está libre el puesto del paciente porque ya ha terminado
        puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");

        //Posible para quitar
        paciente.getRecepcion().getSemSalasOcupadas().release(); //Se avisa de que hay hueco en la sala de observación
    }

    //El paciente sale del hospital
    public void salirHospital(Paciente paciente) throws IOException {
        objDate = new Date();
        String mensaje = diaHora.format(objDate) + "\t\tEl paciente " + paciente.toString() + " se va del Hospital\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();
        salidaTextField.setText(paciente.toString());
    }

    public ArrayList<String> crearMensajeObservacion() {//Se crea un array con el contenido de los 20 puestos para enviarlo al cliente
        ArrayList<String> mensaje = new ArrayList<String>();
        for (int i = 0; i < puestos.size(); i++) {
            mensaje.add(puestos.get(i).getJtfPuesto().getText());
        }
        return mensaje;
    }

    public BufferedWriter getFichero() {
        return fichero;
    }

    public void setFichero(BufferedWriter fichero) {
        this.fichero = fichero;
    }

    public Date getObjDate() {
        return objDate;
    }

    public void setObjDate(Date objDate) {
        this.objDate = objDate;
    }

    public DateFormat getDiaHora() {
        return diaHora;
    }

    public void setDiaHora(DateFormat diaHora) {
        this.diaHora = diaHora;
    }

    public void setPacientesObservacion(BlockingQueue pacientesObservacion) {
        this.pacientesObservacion = pacientesObservacion;
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
