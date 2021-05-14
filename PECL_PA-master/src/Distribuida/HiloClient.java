/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Distribuida;

/**
 *
 * @author Alvaro Gonzalez Garcia
 */
public class HiloClient extends Thread{
    
    
    private MainClient mainClient;

    public HiloClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }
    
    
    public void run(){

    }
}
