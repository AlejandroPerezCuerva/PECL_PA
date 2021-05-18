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
 * @author aleja
 */
public class SalaDescanso {

    private JTextArea colaSalaDescanso;
    private BlockingQueue colaSala = new LinkedBlockingDeque();
    private BlockingQueue pacientesObservacion;
    private BufferedWriter fichero;
    private Date objDate;
    private DateFormat diaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public SalaDescanso(JTextArea colaSalaDescanso, BlockingQueue pacientesObservacion, BufferedWriter fichero) {
        this.colaSalaDescanso = colaSalaDescanso;
        this.pacientesObservacion = pacientesObservacion;
        this.fichero = fichero;
    }

    //Añadimos los sanitarios a la sala de Descanso
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
