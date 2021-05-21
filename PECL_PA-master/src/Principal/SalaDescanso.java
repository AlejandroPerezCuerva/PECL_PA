package Principal;

import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class SalaDescanso {

    private JTextArea colaSalaDescanso; //JTextArea donde salen todos los trabajadores del hospital cuando descansan
    private BlockingQueue colaSala; //Cola donde se meten los trabajadores para luego poder sacarla por la interfaz
    private BlockingQueue pacientesObservacion; //Cola de los pacientes que están en observación
    private BufferedWriter fichero; //Fichero donde se guarda toda la información
    private Date objDate; //Fecha de los movimientos de la sala de descanso
    private DateFormat diaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //Formato de la fecha

    /**
     * Constructor de la sala de descanso
     *
     * @param colaSalaDescanso JTextArea donde se guarda la información de todos
     * los trabajadores que descansan
     * @param pacientesObservacion Cola de pacientes de observación
     * @param fichero Fichero donde se guarda la información
     */
    public SalaDescanso(JTextArea colaSalaDescanso, BlockingQueue pacientesObservacion, BufferedWriter fichero) {
        this.colaSalaDescanso = colaSalaDescanso;
        this.pacientesObservacion = pacientesObservacion;
        this.colaSala = new LinkedBlockingDeque();
        this.fichero = fichero;
    }

    /**
     * Método en el cuál llegan los sanitarios y descansan el tiempo indicado,
     * este método solo se utiliza al principio del programa porque es cuando
     * los sanitarios se cambian para ir a sus puestos
     *
     * @param sanitario Sanitario que se cambia cuando llega al hospital
     * @throws InterruptedException Excepción de interrupción
     * @throws IOException Excepción de entrada salida
     */
    public void cambiarseSanitario(Sanitario sanitario) throws InterruptedException, IOException {
        objDate = new Date();
        String mensaje = diaHora.format(objDate) + "\t\tEl sanitario " + sanitario + " empieza a cambiarse\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();

        //Primero se meten, hacen el sleep y luego salen a sus puestos
        colaSala.put(sanitario);
        colaSalaDescanso.setText(colaSala.toString());
        sleep((int) (2000 * Math.random() + 1000)); //Sleep de 1 a 3 segundos que es lo que tardan en cambiarse
        colaSala.take();
        colaSalaDescanso.setText(colaSala.toString()); //Se actualiza el JTextFiel con los Sanitarios que han ido saliendo

        objDate = new Date();
        mensaje = diaHora.format(objDate.getTime()) + "\t\tEl sanitario " + sanitario + " termina de cambiarse\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();
    }

    /**
     * Método que utilizan los auxiliares para cuando les toca descanso, lo
     * único que hacen es salir en la interfaz y descansar el tiempo que pasan
     * por parámetro
     *
     * @param auxiliar Auxiliar que descansa
     * @param maximo Tiempo máximo que puede estar descansando
     * @param minimo Tiempo mínimo que puede estar descansando
     * @throws IOException Excepción de entrada salida
     */
    public void descansoAuxiliares(Auxiliar auxiliar, int maximo, int minimo) throws IOException {
        objDate = new Date();
        String mensaje = diaHora.format(objDate) + "\t\tEl auxiliar " + auxiliar + " comienza su descanso\n";
        System.out.print(mensaje);

        fichero.write(mensaje);
        fichero.flush();

        try {
            colaSala.put(auxiliar); //Metemos el auxiliar en la cola
            colaSalaDescanso.setText(colaSala.toString());
            sleep((int) ((maximo - minimo) * Math.random() + minimo)); //Dormimos al auxiliar el tiempo que nos indican
            colaSala.take();
            colaSalaDescanso.setText(colaSala.toString()); //Actualizamos la cola 
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaDescanso.class.getName()).log(Level.SEVERE, null, ex);
        }
        objDate = new Date();
        mensaje = diaHora.format(objDate) + "\t\tEl auxiliar " + auxiliar + " termina su descanso\n";
        System.out.print(mensaje);

        fichero.write(mensaje);
        fichero.flush();
    }

    /**
     * Método en el cuál descansan los sanitarios, pasan por parámetro el tiempo
     * que pueden estar descansando
     *
     * @param sanitario Sanitario que descansa después de vacunar o cuando el
     * cliente en distribuida se lo indica
     * @param maximo Tiempo máximo que puede estar descansando
     * @param minimo Tiempo mínimo que puede estar descansando
     * @throws IOException Excepción de entrada salida
     * @throws InterruptedException Excepción de interrupción
     */
    public void descansoSanitarios(Sanitario sanitario, int maximo, int minimo) throws IOException, InterruptedException {
        objDate = new Date();
        String mensaje = diaHora.format(objDate) + "\t\tEl sanitario " + sanitario + " empieza su descanso\n";
        System.out.print(mensaje);

        fichero.write(mensaje);
        fichero.flush();

        //Primero se meten, hacen el sleep y luego salen a sus puestos
        colaSala.put(sanitario);
        colaSalaDescanso.setText(colaSala.toString());
        sleep((int) ((maximo - minimo) * Math.random() + minimo)); //Sleep de 5 a 8 segundos que es lo que tardan en descansar
        colaSala.take();
        colaSalaDescanso.setText(colaSala.toString()); //Se actualiza el JTextFiel con los Sanitarios que han ido saliendo

        objDate = new Date();
        mensaje = diaHora.format(objDate) + "\t\tEl sanitario " + sanitario + " termina su descanso\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();
    }

    /**
     * Método que se utiliza para la segunda parte de la práctica, se recoge
     * toda la información de la sala de descanso (lo que hay en el JTextArea)
     *
     * @return String con toda la información de la sala de descanso
     */
    public String crearMensajeDescanso() {//Se pasa el contenido de la sala de descanso a String para enviarla al cliente
        String textoColaDescanso;
        textoColaDescanso = colaSalaDescanso.getText();
        return textoColaDescanso;
    }

    public JTextArea getColaSalaDescanso() {
        return colaSalaDescanso;
    }

    public void setColaSalaDescanso(JTextArea colaSalaDescanso) {
        this.colaSalaDescanso = colaSalaDescanso;
    }

    public BlockingQueue getColaSala() {
        return colaSala;
    }

    public void setColaSala(BlockingQueue colaSala) {
        this.colaSala = colaSala;
    }

    public BlockingQueue getPacientesObservacion() {
        return pacientesObservacion;
    }

    public void setPacientesObservacion(BlockingQueue pacientesObservacion) {
        this.pacientesObservacion = pacientesObservacion;
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

}
