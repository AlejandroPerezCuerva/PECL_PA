package Principal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.accessibility.AccessibleContext;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class Main extends javax.swing.JFrame {

    private Recepcion recepcion; //Inicializamos la recepcion y le pasamos los JTextFields correspondientes
    private CrearPacientes crearPacientes; //Es necesario crear los pacientes en una clase a parte por culpa del sleep
    private SalaDescanso salaDescanso; //Inicializamos la sala de descanso y le pasamos los JTextFields correspondientes
    private SalaVacunacion salaVacunacion; //Inicializamos la sala de vacunacion y le pasamos los JTextFields correspondientes
    private SalaObservacion salaObservacion; //Inicializamos la sala de observacion y le pasamos los JTextFields correspondientes
    private AtomicInteger contadorAux1 = new AtomicInteger(0); //Se utiliza un atomic porque es mas seguro que un int normal, lo utilizamos de contador
    private AtomicInteger contadorAux2 = new AtomicInteger(0); //Hacen falta dos contadores porque sino se hace referencia al mismo
    private ArrayList<AtomicInteger> contadoresSanitarios = new ArrayList<>(); //Cada sanitario necesita un contador para llevar la cuenta de los pacientes vacunados
    private ArrayList<JTextField> puestosVacunacion = new ArrayList<JTextField>();
    private ArrayList<JTextField> puestosObservacion = new ArrayList<JTextField>();
    private Semaphore semRegistrar = new Semaphore(0); //Es un semaforo que será objeto compartido entre los pacientes y el auxiliar 1 para que el auxiliar tenga
    //el privilegio de detener al paciente hasta que termine el registro de la vacuna. Tiene que ser un objeto compartido para que el paciente pueda esperar
    private BlockingQueue pacientesObservacion = new LinkedBlockingQueue();//Objeto para coordinar la observacion de los pacientes

    private File archivo; //De esta manera se guarda el fichero en la propia carpeta del proyecto y no hace falta poner la ruta
    private BufferedWriter fichero;

    /**
     * Creates new form Main
     */
    public Main() throws IOException {
        initComponents();

        archivo = new File("evolucionHospital.txt");
        fichero = new BufferedWriter(new FileWriter(archivo));
        //Se añaden los puestos a un array para que luego sea mas cómodo utilizarlos
        puestosVacunacion.add(puesto1);
        puestosVacunacion.add(puesto2);
        puestosVacunacion.add(puesto3);
        puestosVacunacion.add(puesto4);
        puestosVacunacion.add(puesto5);
        puestosVacunacion.add(puesto6);
        puestosVacunacion.add(puesto7);
        puestosVacunacion.add(puesto8);
        puestosVacunacion.add(puesto9);
        puestosVacunacion.add(puesto10);

        //Es mas fácil tener todos los JTextFiel en un array que pasarlos y utilizarlos uno por uno
        puestosObservacion.add(puestoO1);
        puestosObservacion.add(puestoO2);
        puestosObservacion.add(puestoO3);
        puestosObservacion.add(puestoO4);
        puestosObservacion.add(puestoO5);
        puestosObservacion.add(puestoO6);
        puestosObservacion.add(puestoO7);
        puestosObservacion.add(puestoO8);
        puestosObservacion.add(puestoO9);
        puestosObservacion.add(puestoO10);
        puestosObservacion.add(puestoO11);
        puestosObservacion.add(puestoO12);
        puestosObservacion.add(puestoO13);
        puestosObservacion.add(puestoO14);
        puestosObservacion.add(puestoO15);
        puestosObservacion.add(puestoO16);
        puestosObservacion.add(puestoO17);
        puestosObservacion.add(puestoO18);
        puestosObservacion.add(puestoO19);
        puestosObservacion.add(puestoO20);

        //Insertamos todos los JTextField necesarios de SalaVacunacion
        salaVacunacion = new SalaVacunacion(10, auxiliarVacunacion, numeroVacunas, puestosVacunacion);

        //Insertamos todos los JTextField necesarios de SalaVacunacion
        salaObservacion = new SalaObservacion(20, puestosObservacion, salidaTextField, pacientesObservacion, fichero);

        //Insertamos todos los JTextField necesarios de Recepion
        recepcion = new Recepcion(colaRecepcion, pacienteRecepcion, auxiliarRecepcion, salaVacunacion, salaObservacion, fichero);

        //Insertamos todos los JTextField necesarios de SalaDescanso
        salaDescanso = new SalaDescanso(colaSalaDescanso, pacientesObservacion, fichero);

        //Inicializamos crearPacientes y le pasamos los parámetros necesarios que necesitan los pacientes
        crearPacientes = new CrearPacientes(recepcion, salaVacunacion, salaObservacion, semRegistrar);
        crearPacientes.start();

        for (int i = 0; i < 10; i++) {
            AtomicInteger nuevoContador = new AtomicInteger(0);
            contadoresSanitarios.add(nuevoContador); //Inicializamos todos los contadores a 0
            Sanitario sanitarioNuevo = new Sanitario(i + 1, salaDescanso, salaVacunacion, salaObservacion, contadoresSanitarios); //Todos los parámetros necesarios para los sanitarios
            sanitarioNuevo.start();
        }

        Auxiliar a1 = new Auxiliar(1, contadorAux1, recepcion, salaDescanso, semRegistrar);
        Auxiliar a2 = new Auxiliar(2, contadorAux2, salaVacunacion, salaDescanso);
        a1.start();
        a2.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pacienteRecepcion = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        auxiliarRecepcion = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        colaRecepcion = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        colaSalaDescanso = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        puesto1 = new javax.swing.JTextField();
        puesto2 = new javax.swing.JTextField();
        puesto3 = new javax.swing.JTextField();
        puesto4 = new javax.swing.JTextField();
        puesto5 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        auxiliarVacunacion = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        numeroVacunas = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        puesto6 = new javax.swing.JTextField();
        puesto7 = new javax.swing.JTextField();
        puesto8 = new javax.swing.JTextField();
        puesto9 = new javax.swing.JTextField();
        puesto10 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        puestoO1 = new javax.swing.JTextField();
        puestoO2 = new javax.swing.JTextField();
        puestoO3 = new javax.swing.JTextField();
        puestoO4 = new javax.swing.JTextField();
        puestoO5 = new javax.swing.JTextField();
        puestoO6 = new javax.swing.JTextField();
        puestoO7 = new javax.swing.JTextField();
        puestoO8 = new javax.swing.JTextField();
        puestoO9 = new javax.swing.JTextField();
        puestoO10 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        puestoO11 = new javax.swing.JTextField();
        puestoO12 = new javax.swing.JTextField();
        puestoO13 = new javax.swing.JTextField();
        puestoO14 = new javax.swing.JTextField();
        puestoO15 = new javax.swing.JTextField();
        puestoO16 = new javax.swing.JTextField();
        puestoO17 = new javax.swing.JTextField();
        puestoO18 = new javax.swing.JTextField();
        puestoO19 = new javax.swing.JTextField();
        puestoO20 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        salidaTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hospital de Vacunación");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Recepción");

        jLabel39.setText("Cola de espera");

        jLabel5.setText("Paciente");

        pacienteRecepcion.setEditable(false);

        jLabel6.setText("Auxiliar");

        auxiliarRecepcion.setEditable(false);
        auxiliarRecepcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                auxiliarRecepcionActionPerformed(evt);
            }
        });

        colaRecepcion.setEditable(false);
        colaRecepcion.setColumns(20);
        colaRecepcion.setRows(5);
        jScrollPane2.setViewportView(colaRecepcion);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pacienteRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(120, 120, 120)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(auxiliarRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(220, 220, 220)
                        .addComponent(jLabel39))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pacienteRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(auxiliarRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setText("Sala de descanso");

        colaSalaDescanso.setEditable(false);
        colaSalaDescanso.setColumns(20);
        colaSalaDescanso.setRows(5);
        jScrollPane1.setViewportView(colaSalaDescanso);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(jLabel2)))
                .addContainerGap(180, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setText("Puesto 1");

        puesto1.setEditable(false);

        puesto2.setEditable(false);
        puesto2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puesto2ActionPerformed(evt);
            }
        });

        puesto3.setEditable(false);
        puesto3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puesto3ActionPerformed(evt);
            }
        });

        puesto4.setEditable(false);
        puesto4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puesto4ActionPerformed(evt);
            }
        });

        puesto5.setEditable(false);

        jLabel8.setText("Puesto 2");

        jLabel9.setText("Puesto 3");

        jLabel10.setText("Puesto 4");

        jLabel11.setText("Puesto 5");

        jLabel17.setText("Auxiliar ");

        auxiliarVacunacion.setEditable(false);
        auxiliarVacunacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                auxiliarVacunacionActionPerformed(evt);
            }
        });

        jLabel18.setText("Vacunas disponibles");

        numeroVacunas.setEditable(false);

        jLabel12.setText("Puesto 6");

        jLabel13.setText("Puesto 7");

        jLabel14.setText("Puesto 8");

        jLabel15.setText("Puesto 9");

        jLabel16.setText("Puesto 10");

        puesto6.setEditable(false);
        puesto6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puesto6ActionPerformed(evt);
            }
        });

        puesto7.setEditable(false);
        puesto7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puesto7ActionPerformed(evt);
            }
        });

        puesto8.setEditable(false);

        puesto9.setEditable(false);

        puesto10.setEditable(false);
        puesto10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puesto10ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel3.setText("Sala de vacunación");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(puesto6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(puesto7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(puesto8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(puesto9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(puesto10, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(149, 149, 149)
                                .addComponent(numeroVacunas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(32, 32, 32))
                                            .addComponent(puesto1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(256, 256, 256)
                                                .addComponent(jLabel15))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(puesto2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(10, 10, 10)
                                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(puesto3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel9))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(puesto4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel10)))
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(jLabel13)
                                                        .addGap(72, 72, 72)
                                                        .addComponent(jLabel14)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel11)
                                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(puesto5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                    .addComponent(jLabel12))
                                .addGap(151, 151, 151)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(auxiliarVacunacion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel17)))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(487, 487, 487)
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(auxiliarVacunacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(puesto2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puesto1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puesto3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puesto4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puesto5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(puesto6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(puesto8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puesto7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puesto9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puesto10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(numeroVacunas, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel4.setText("Sala de observación");

        jLabel27.setText("Puesto 9");

        jLabel28.setText("Puesto 10");

        puestoO1.setEditable(false);

        puestoO2.setEditable(false);

        puestoO3.setEditable(false);

        puestoO4.setEditable(false);

        puestoO5.setEditable(false);

        puestoO6.setEditable(false);

        puestoO7.setEditable(false);

        puestoO8.setEditable(false);

        puestoO9.setEditable(false);

        puestoO10.setEditable(false);

        jLabel19.setText("Puesto 1");

        jLabel20.setText("Puesto 2");

        jLabel21.setText("Puesto 3");

        jLabel22.setText("Puesto 4");

        jLabel23.setText("Puesto 5");

        jLabel24.setText("Puesto 6");

        jLabel25.setText("Puesto 7");

        jLabel26.setText("Puesto 8");

        jLabel29.setText("Puesto 11");

        jLabel30.setText("Puesto 12");

        jLabel31.setText("Puesto 13");

        jLabel32.setText("Puesto 14");

        jLabel33.setText("Puesto 15");

        jLabel34.setText("Puesto 16");

        jLabel35.setText("Puesto 17");

        jLabel36.setText("Puesto 18");

        jLabel37.setText("Puesto19");

        jLabel38.setText("Puesto 20");

        puestoO11.setEditable(false);

        puestoO12.setEditable(false);

        puestoO13.setEditable(false);
        puestoO13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puestoO13ActionPerformed(evt);
            }
        });

        puestoO14.setEditable(false);

        puestoO15.setEditable(false);

        puestoO16.setEditable(false);

        puestoO17.setEditable(false);

        puestoO18.setEditable(false);

        puestoO19.setEditable(false);

        puestoO20.setEditable(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(puestoO1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(puestoO2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(puestoO3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(puestoO4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel22)))
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(jLabel31)
                                                .addGap(65, 65, 65)
                                                .addComponent(jLabel32)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(puestoO5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel23)
                                            .addComponent(jLabel33))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(puestoO6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel24)
                                            .addComponent(jLabel34))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(puestoO7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel25)
                                            .addComponent(jLabel35))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(puestoO8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel26)
                                            .addComponent(jLabel36))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(puestoO9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel27)
                                            .addComponent(jLabel37))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel38)
                                            .addComponent(jLabel28)
                                            .addComponent(puestoO10, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addGap(72, 72, 72)
                                        .addComponent(jLabel21))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(puestoO11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel30)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(puestoO12, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO13, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO14, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO15, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO16, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO17, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO18, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO19, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(puestoO20, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(454, 454, 454)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(puestoO1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(puestoO8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel33)
                    .addComponent(jLabel32)
                    .addComponent(jLabel36)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(puestoO12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(puestoO11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(puestoO14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(puestoO15, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(puestoO13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(puestoO16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puestoO17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puestoO18, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puestoO19, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(puestoO20, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLabel40.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel40.setText("Salida del hospital");

        salidaTextField.setEditable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(463, 463, 463)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(salidaTextField)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel40)
                .addGap(12, 12, 12)
                .addComponent(salidaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void auxiliarVacunacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_auxiliarVacunacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_auxiliarVacunacionActionPerformed

    private void puestoO13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puestoO13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puestoO13ActionPerformed

    private void auxiliarRecepcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_auxiliarRecepcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_auxiliarRecepcionActionPerformed

    private void puesto3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puesto3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puesto3ActionPerformed

    private void puesto4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puesto4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puesto4ActionPerformed

    private void puesto2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puesto2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puesto2ActionPerformed

    private void puesto6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puesto6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puesto6ActionPerformed

    private void puesto7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puesto7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puesto7ActionPerformed

    private void puesto10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puesto10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puesto10ActionPerformed

    public Recepcion getRecepcion() {
        return recepcion;
    }

    public void setRecepcion(Recepcion recepcion) {
        this.recepcion = recepcion;
    }

    public CrearPacientes getCrearPacientes() {
        return crearPacientes;
    }

    public void setCrearPacientes(CrearPacientes crearPacientes) {
        this.crearPacientes = crearPacientes;
    }

    public SalaDescanso getSalaDescanso() {
        return salaDescanso;
    }

    public void setSalaDescanso(SalaDescanso salaDescanso) {
        this.salaDescanso = salaDescanso;
    }

    public SalaVacunacion getSalaVacunacion() {
        return salaVacunacion;
    }

    public void setSalaVacunacion(SalaVacunacion salaVacunacion) {
        this.salaVacunacion = salaVacunacion;
    }

    public SalaObservacion getSalaObservacion() {
        return salaObservacion;
    }

    public void setSalaObservacion(SalaObservacion salaObservacion) {
        this.salaObservacion = salaObservacion;
    }

    public AtomicInteger getContadorAux1() {
        return contadorAux1;
    }

    public void setContadorAux1(AtomicInteger contadorAux1) {
        this.contadorAux1 = contadorAux1;
    }

    public AtomicInteger getContadorAux2() {
        return contadorAux2;
    }

    public void setContadorAux2(AtomicInteger contadorAux2) {
        this.contadorAux2 = contadorAux2;
    }

    public ArrayList<AtomicInteger> getContadoresSanitarios() {
        return contadoresSanitarios;
    }

    public void setContadoresSanitarios(ArrayList<AtomicInteger> contadoresSanitarios) {
        this.contadoresSanitarios = contadoresSanitarios;
    }

    public ArrayList<JTextField> getPuestosVacunacion() {
        return puestosVacunacion;
    }

    public void setPuestosVacunacion(ArrayList<JTextField> puestosVacunacion) {
        this.puestosVacunacion = puestosVacunacion;
    }

    public ArrayList<JTextField> getPuestosObservacion() {
        return puestosObservacion;
    }

    public void setPuestosObservacion(ArrayList<JTextField> puestosObservacion) {
        this.puestosObservacion = puestosObservacion;
    }

    public Semaphore getSemRegistrar() {
        return semRegistrar;
    }

    public void setSemRegistrar(Semaphore semRegistrar) {
        this.semRegistrar = semRegistrar;
    }

    public BlockingQueue getPacientesObservacion() {
        return pacientesObservacion;
    }

    public void setPacientesObservacion(BlockingQueue pacientesObservacion) {
        this.pacientesObservacion = pacientesObservacion;
    }

    public File getArchivo() {
        return archivo;
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    public BufferedWriter getFichero() {
        return fichero;
    }

    public void setFichero(BufferedWriter fichero) {
        this.fichero = fichero;
    }

    public JTextField getAuxiliarRecepcion() {
        return auxiliarRecepcion;
    }

    public void setAuxiliarRecepcion(JTextField auxiliarRecepcion) {
        this.auxiliarRecepcion = auxiliarRecepcion;
    }

    public JTextField getAuxiliarVacunacion() {
        return auxiliarVacunacion;
    }

    public void setAuxiliarVacunacion(JTextField auxiliarVacunacion) {
        this.auxiliarVacunacion = auxiliarVacunacion;
    }

    public JTextArea getColaRecepcion() {
        return colaRecepcion;
    }

    public void setColaRecepcion(JTextArea colaRecepcion) {
        this.colaRecepcion = colaRecepcion;
    }

    public JTextArea getColaSalaDescanso() {
        return colaSalaDescanso;
    }

    public void setColaSalaDescanso(JTextArea colaSalaDescanso) {
        this.colaSalaDescanso = colaSalaDescanso;
    }

    public JLabel getjLabel1() {
        return jLabel1;
    }

    public void setjLabel1(JLabel jLabel1) {
        this.jLabel1 = jLabel1;
    }

    public JLabel getjLabel10() {
        return jLabel10;
    }

    public void setjLabel10(JLabel jLabel10) {
        this.jLabel10 = jLabel10;
    }

    public JLabel getjLabel11() {
        return jLabel11;
    }

    public void setjLabel11(JLabel jLabel11) {
        this.jLabel11 = jLabel11;
    }

    public JLabel getjLabel12() {
        return jLabel12;
    }

    public void setjLabel12(JLabel jLabel12) {
        this.jLabel12 = jLabel12;
    }

    public JLabel getjLabel13() {
        return jLabel13;
    }

    public void setjLabel13(JLabel jLabel13) {
        this.jLabel13 = jLabel13;
    }

    public JLabel getjLabel14() {
        return jLabel14;
    }

    public void setjLabel14(JLabel jLabel14) {
        this.jLabel14 = jLabel14;
    }

    public JLabel getjLabel15() {
        return jLabel15;
    }

    public void setjLabel15(JLabel jLabel15) {
        this.jLabel15 = jLabel15;
    }

    public JLabel getjLabel16() {
        return jLabel16;
    }

    public void setjLabel16(JLabel jLabel16) {
        this.jLabel16 = jLabel16;
    }

    public JLabel getjLabel17() {
        return jLabel17;
    }

    public void setjLabel17(JLabel jLabel17) {
        this.jLabel17 = jLabel17;
    }

    public JLabel getjLabel18() {
        return jLabel18;
    }

    public void setjLabel18(JLabel jLabel18) {
        this.jLabel18 = jLabel18;
    }

    public JLabel getjLabel19() {
        return jLabel19;
    }

    public void setjLabel19(JLabel jLabel19) {
        this.jLabel19 = jLabel19;
    }

    public JLabel getjLabel2() {
        return jLabel2;
    }

    public void setjLabel2(JLabel jLabel2) {
        this.jLabel2 = jLabel2;
    }

    public JLabel getjLabel20() {
        return jLabel20;
    }

    public void setjLabel20(JLabel jLabel20) {
        this.jLabel20 = jLabel20;
    }

    public JLabel getjLabel21() {
        return jLabel21;
    }

    public void setjLabel21(JLabel jLabel21) {
        this.jLabel21 = jLabel21;
    }

    public JLabel getjLabel22() {
        return jLabel22;
    }

    public void setjLabel22(JLabel jLabel22) {
        this.jLabel22 = jLabel22;
    }

    public JLabel getjLabel23() {
        return jLabel23;
    }

    public void setjLabel23(JLabel jLabel23) {
        this.jLabel23 = jLabel23;
    }

    public JLabel getjLabel24() {
        return jLabel24;
    }

    public void setjLabel24(JLabel jLabel24) {
        this.jLabel24 = jLabel24;
    }

    public JLabel getjLabel25() {
        return jLabel25;
    }

    public void setjLabel25(JLabel jLabel25) {
        this.jLabel25 = jLabel25;
    }

    public JLabel getjLabel26() {
        return jLabel26;
    }

    public void setjLabel26(JLabel jLabel26) {
        this.jLabel26 = jLabel26;
    }

    public JLabel getjLabel27() {
        return jLabel27;
    }

    public void setjLabel27(JLabel jLabel27) {
        this.jLabel27 = jLabel27;
    }

    public JLabel getjLabel28() {
        return jLabel28;
    }

    public void setjLabel28(JLabel jLabel28) {
        this.jLabel28 = jLabel28;
    }

    public JLabel getjLabel29() {
        return jLabel29;
    }

    public void setjLabel29(JLabel jLabel29) {
        this.jLabel29 = jLabel29;
    }

    public JLabel getjLabel3() {
        return jLabel3;
    }

    public void setjLabel3(JLabel jLabel3) {
        this.jLabel3 = jLabel3;
    }

    public JLabel getjLabel30() {
        return jLabel30;
    }

    public void setjLabel30(JLabel jLabel30) {
        this.jLabel30 = jLabel30;
    }

    public JLabel getjLabel31() {
        return jLabel31;
    }

    public void setjLabel31(JLabel jLabel31) {
        this.jLabel31 = jLabel31;
    }

    public JLabel getjLabel32() {
        return jLabel32;
    }

    public void setjLabel32(JLabel jLabel32) {
        this.jLabel32 = jLabel32;
    }

    public JLabel getjLabel33() {
        return jLabel33;
    }

    public void setjLabel33(JLabel jLabel33) {
        this.jLabel33 = jLabel33;
    }

    public JLabel getjLabel34() {
        return jLabel34;
    }

    public void setjLabel34(JLabel jLabel34) {
        this.jLabel34 = jLabel34;
    }

    public JLabel getjLabel35() {
        return jLabel35;
    }

    public void setjLabel35(JLabel jLabel35) {
        this.jLabel35 = jLabel35;
    }

    public JLabel getjLabel36() {
        return jLabel36;
    }

    public void setjLabel36(JLabel jLabel36) {
        this.jLabel36 = jLabel36;
    }

    public JLabel getjLabel37() {
        return jLabel37;
    }

    public void setjLabel37(JLabel jLabel37) {
        this.jLabel37 = jLabel37;
    }

    public JLabel getjLabel38() {
        return jLabel38;
    }

    public void setjLabel38(JLabel jLabel38) {
        this.jLabel38 = jLabel38;
    }

    public JLabel getjLabel39() {
        return jLabel39;
    }

    public void setjLabel39(JLabel jLabel39) {
        this.jLabel39 = jLabel39;
    }

    public JLabel getjLabel4() {
        return jLabel4;
    }

    public void setjLabel4(JLabel jLabel4) {
        this.jLabel4 = jLabel4;
    }

    public JLabel getjLabel40() {
        return jLabel40;
    }

    public void setjLabel40(JLabel jLabel40) {
        this.jLabel40 = jLabel40;
    }

    public JLabel getjLabel5() {
        return jLabel5;
    }

    public void setjLabel5(JLabel jLabel5) {
        this.jLabel5 = jLabel5;
    }

    public JLabel getjLabel6() {
        return jLabel6;
    }

    public void setjLabel6(JLabel jLabel6) {
        this.jLabel6 = jLabel6;
    }

    public JLabel getjLabel7() {
        return jLabel7;
    }

    public void setjLabel7(JLabel jLabel7) {
        this.jLabel7 = jLabel7;
    }

    public JLabel getjLabel8() {
        return jLabel8;
    }

    public void setjLabel8(JLabel jLabel8) {
        this.jLabel8 = jLabel8;
    }

    public JLabel getjLabel9() {
        return jLabel9;
    }

    public void setjLabel9(JLabel jLabel9) {
        this.jLabel9 = jLabel9;
    }

    public JPanel getjPanel1() {
        return jPanel1;
    }

    public void setjPanel1(JPanel jPanel1) {
        this.jPanel1 = jPanel1;
    }

    public JPanel getjPanel2() {
        return jPanel2;
    }

    public void setjPanel2(JPanel jPanel2) {
        this.jPanel2 = jPanel2;
    }

    public JPanel getjPanel3() {
        return jPanel3;
    }

    public void setjPanel3(JPanel jPanel3) {
        this.jPanel3 = jPanel3;
    }

    public JPanel getjPanel4() {
        return jPanel4;
    }

    public void setjPanel4(JPanel jPanel4) {
        this.jPanel4 = jPanel4;
    }

    public JPanel getjPanel5() {
        return jPanel5;
    }

    public void setjPanel5(JPanel jPanel5) {
        this.jPanel5 = jPanel5;
    }

    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }

    public void setjScrollPane1(JScrollPane jScrollPane1) {
        this.jScrollPane1 = jScrollPane1;
    }

    public JScrollPane getjScrollPane2() {
        return jScrollPane2;
    }

    public void setjScrollPane2(JScrollPane jScrollPane2) {
        this.jScrollPane2 = jScrollPane2;
    }

    public JTextField getNumeroVacunas() {
        return numeroVacunas;
    }

    public void setNumeroVacunas(JTextField numeroVacunas) {
        this.numeroVacunas = numeroVacunas;
    }

    public JTextField getPacienteRecepcion() {
        return pacienteRecepcion;
    }

    public void setPacienteRecepcion(JTextField pacienteRecepcion) {
        this.pacienteRecepcion = pacienteRecepcion;
    }

    public JTextField getPuesto1() {
        return puesto1;
    }

    public void setPuesto1(JTextField puesto1) {
        this.puesto1 = puesto1;
    }

    public JTextField getPuesto10() {
        return puesto10;
    }

    public void setPuesto10(JTextField puesto10) {
        this.puesto10 = puesto10;
    }

    public JTextField getPuesto2() {
        return puesto2;
    }

    public void setPuesto2(JTextField puesto2) {
        this.puesto2 = puesto2;
    }

    public JTextField getPuesto3() {
        return puesto3;
    }

    public void setPuesto3(JTextField puesto3) {
        this.puesto3 = puesto3;
    }

    public JTextField getPuesto4() {
        return puesto4;
    }

    public void setPuesto4(JTextField puesto4) {
        this.puesto4 = puesto4;
    }

    public JTextField getPuesto5() {
        return puesto5;
    }

    public void setPuesto5(JTextField puesto5) {
        this.puesto5 = puesto5;
    }

    public JTextField getPuesto6() {
        return puesto6;
    }

    public void setPuesto6(JTextField puesto6) {
        this.puesto6 = puesto6;
    }

    public JTextField getPuesto7() {
        return puesto7;
    }

    public void setPuesto7(JTextField puesto7) {
        this.puesto7 = puesto7;
    }

    public JTextField getPuesto8() {
        return puesto8;
    }

    public void setPuesto8(JTextField puesto8) {
        this.puesto8 = puesto8;
    }

    public JTextField getPuesto9() {
        return puesto9;
    }

    public void setPuesto9(JTextField puesto9) {
        this.puesto9 = puesto9;
    }

    public JTextField getPuestoO1() {
        return puestoO1;
    }

    public void setPuestoO1(JTextField puestoO1) {
        this.puestoO1 = puestoO1;
    }

    public JTextField getPuestoO10() {
        return puestoO10;
    }

    public void setPuestoO10(JTextField puestoO10) {
        this.puestoO10 = puestoO10;
    }

    public JTextField getPuestoO11() {
        return puestoO11;
    }

    public void setPuestoO11(JTextField puestoO11) {
        this.puestoO11 = puestoO11;
    }

    public JTextField getPuestoO12() {
        return puestoO12;
    }

    public void setPuestoO12(JTextField puestoO12) {
        this.puestoO12 = puestoO12;
    }

    public JTextField getPuestoO13() {
        return puestoO13;
    }

    public void setPuestoO13(JTextField puestoO13) {
        this.puestoO13 = puestoO13;
    }

    public JTextField getPuestoO14() {
        return puestoO14;
    }

    public void setPuestoO14(JTextField puestoO14) {
        this.puestoO14 = puestoO14;
    }

    public JTextField getPuestoO15() {
        return puestoO15;
    }

    public void setPuestoO15(JTextField puestoO15) {
        this.puestoO15 = puestoO15;
    }

    public JTextField getPuestoO16() {
        return puestoO16;
    }

    public void setPuestoO16(JTextField puestoO16) {
        this.puestoO16 = puestoO16;
    }

    public JTextField getPuestoO17() {
        return puestoO17;
    }

    public void setPuestoO17(JTextField puestoO17) {
        this.puestoO17 = puestoO17;
    }

    public JTextField getPuestoO18() {
        return puestoO18;
    }

    public void setPuestoO18(JTextField puestoO18) {
        this.puestoO18 = puestoO18;
    }

    public JTextField getPuestoO19() {
        return puestoO19;
    }

    public void setPuestoO19(JTextField puestoO19) {
        this.puestoO19 = puestoO19;
    }

    public JTextField getPuestoO2() {
        return puestoO2;
    }

    public void setPuestoO2(JTextField puestoO2) {
        this.puestoO2 = puestoO2;
    }

    public JTextField getPuestoO20() {
        return puestoO20;
    }

    public void setPuestoO20(JTextField puestoO20) {
        this.puestoO20 = puestoO20;
    }

    public JTextField getPuestoO3() {
        return puestoO3;
    }

    public void setPuestoO3(JTextField puestoO3) {
        this.puestoO3 = puestoO3;
    }

    public JTextField getPuestoO4() {
        return puestoO4;
    }

    public void setPuestoO4(JTextField puestoO4) {
        this.puestoO4 = puestoO4;
    }

    public JTextField getPuestoO5() {
        return puestoO5;
    }

    public void setPuestoO5(JTextField puestoO5) {
        this.puestoO5 = puestoO5;
    }

    public JTextField getPuestoO6() {
        return puestoO6;
    }

    public void setPuestoO6(JTextField puestoO6) {
        this.puestoO6 = puestoO6;
    }

    public JTextField getPuestoO7() {
        return puestoO7;
    }

    public void setPuestoO7(JTextField puestoO7) {
        this.puestoO7 = puestoO7;
    }

    public JTextField getPuestoO8() {
        return puestoO8;
    }

    public void setPuestoO8(JTextField puestoO8) {
        this.puestoO8 = puestoO8;
    }

    public JTextField getPuestoO9() {
        return puestoO9;
    }

    public void setPuestoO9(JTextField puestoO9) {
        this.puestoO9 = puestoO9;
    }

    public JTextField getSalidaTextField() {
        return salidaTextField;
    }

    public void setSalidaTextField(JTextField salidaTextField) {
        this.salidaTextField = salidaTextField;
    }

    public JRootPane getRootPane() {
        return rootPane;
    }

    public void setRootPane(JRootPane rootPane) {
        this.rootPane = rootPane;
    }

    public boolean isRootPaneCheckingEnabled() {
        return rootPaneCheckingEnabled;
    }

    public void setRootPaneCheckingEnabled(boolean rootPaneCheckingEnabled) {
        this.rootPaneCheckingEnabled = rootPaneCheckingEnabled;
    }

    public AccessibleContext getAccessibleContext() {
        return accessibleContext;
    }

    public void setAccessibleContext(AccessibleContext accessibleContext) {
        this.accessibleContext = accessibleContext;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Main().setVisible(true);
                } catch (IOException ex) {
                    System.out.println("Se ha producido un error con el txt");
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField auxiliarRecepcion;
    private javax.swing.JTextField auxiliarVacunacion;
    private javax.swing.JTextArea colaRecepcion;
    private javax.swing.JTextArea colaSalaDescanso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField numeroVacunas;
    private javax.swing.JTextField pacienteRecepcion;
    private javax.swing.JTextField puesto1;
    private javax.swing.JTextField puesto10;
    private javax.swing.JTextField puesto2;
    private javax.swing.JTextField puesto3;
    private javax.swing.JTextField puesto4;
    private javax.swing.JTextField puesto5;
    private javax.swing.JTextField puesto6;
    private javax.swing.JTextField puesto7;
    private javax.swing.JTextField puesto8;
    private javax.swing.JTextField puesto9;
    private javax.swing.JTextField puestoO1;
    private javax.swing.JTextField puestoO10;
    private javax.swing.JTextField puestoO11;
    private javax.swing.JTextField puestoO12;
    private javax.swing.JTextField puestoO13;
    private javax.swing.JTextField puestoO14;
    private javax.swing.JTextField puestoO15;
    private javax.swing.JTextField puestoO16;
    private javax.swing.JTextField puestoO17;
    private javax.swing.JTextField puestoO18;
    private javax.swing.JTextField puestoO19;
    private javax.swing.JTextField puestoO2;
    private javax.swing.JTextField puestoO20;
    private javax.swing.JTextField puestoO3;
    private javax.swing.JTextField puestoO4;
    private javax.swing.JTextField puestoO5;
    private javax.swing.JTextField puestoO6;
    private javax.swing.JTextField puestoO7;
    private javax.swing.JTextField puestoO8;
    private javax.swing.JTextField puestoO9;
    private javax.swing.JTextField salidaTextField;
    // End of variables declaration//GEN-END:variables
}
