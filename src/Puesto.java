
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JTextField;

public class Puesto {

    private JTextField jtfPuesto;
    private boolean disponible, disponiblePaciente;
    private Paciente paciente;

    public Puesto(JTextField jtfPuesto, boolean disponible, boolean disponiblePaciente) {
        this.jtfPuesto = jtfPuesto;
        this.disponible = disponible;
        this.disponiblePaciente = disponiblePaciente;
    }

    public JTextField getJtfPuesto() {
        return jtfPuesto;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    
    public boolean isDisponible() {
        return disponible;
    }

    public void entraSanitario() {
        disponible = false;
    }

    public void saleSanitario() {
        disponible = true;
    }

    public void entraPaciente(Paciente paciente) {
        this.paciente = paciente;
        disponible = false;
    }

    public void salePaciente() {
        paciente = null;
        disponible = true;
    }

    public boolean isDisponiblePaciente() {
        return disponiblePaciente;
    }

    public void setDisponiblePaciente(boolean disponiblePaciente) {
        this.disponiblePaciente = disponiblePaciente;
    }

}
