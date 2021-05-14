package Principal;

import javax.swing.JTextField;

public class Puesto {

    private JTextField jtfPuesto;
    private boolean disponible, disponiblePaciente;
    private boolean limpiando=false;
    private Sanitario sanitario;//Sanitario asociado a los puestos de vacunación para poder indicar cuando se limpian dichos puestos

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
