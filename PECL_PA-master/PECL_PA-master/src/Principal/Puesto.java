package Principal;

import javax.swing.JTextField;

/**
 * Esta clase crea un puesto el que entrarán tanto sanitarios y auxiliares como
 * pacientes.
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class Puesto {

    private JTextField jtfPuesto;//JTextField que mostrará por pantalla las personas que ocupan el puesto
    private boolean disponible, disponiblePaciente;//booleanos que indican si el puesto está disponible para sanitarios y pacientes, correspondientemente
    private boolean limpiando = false;//Booleano que indica si el puesto está siendo limpiado o no
    private Sanitario sanitario;//Sanitario asociado a los puestos de vacunación para poder indicar cuando se limpian dichos puestos

    /**
     * Constructor para la clase Puesto
     *
     * @param jtfPuesto El parámetro jtfPuesto define el JTextField asociado al
     * puesto
     * @param disponible El parámetro disponible define del booleano que indica
     * si un Sanitario puede accerder al puesto
     * @param disponiblePaciente El parámetro disponiblePaciente define el
     * booleano que indica si un paciente puede acceder al puesto
     */
    public Puesto(JTextField jtfPuesto, boolean disponible, boolean disponiblePaciente) {
        this.jtfPuesto = jtfPuesto;
        this.disponible = disponible;
        this.disponiblePaciente = disponiblePaciente;
    }

    public JTextField getJtfPuesto() {
        return jtfPuesto;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public boolean isDisponiblePaciente() {
        return disponiblePaciente;
    }

    public void setDisponiblePaciente(boolean disponiblePaciente) {
        this.disponiblePaciente = disponiblePaciente;
    }

    public void setJtfPuesto(JTextField jtfPuesto) {
        this.jtfPuesto = jtfPuesto;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isLimpiando() {
        return limpiando;
    }

    public void setLimpiando(boolean limpiando) {
        this.limpiando = limpiando;
    }

    public Sanitario getSanitario() {
        return sanitario;
    }

    public void setSanitario(Sanitario sanitario) {
        this.sanitario = sanitario;
    }

}
