/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package weather.serverside.utilities;

import javax.swing.JLabel;

/**
 *
 * @author Zach Rothweiler
 * @version Spring 2012
 */
public class ServiceCheckThread extends Thread {

    private long frequency;
    private WeatherServiceNames service;
    private boolean running;
    private String serviceStatus;
    private JLabel labelToUpdate;

    /**
     * Creates a runnable object to check the status of a service at
     * a set interval.  If it is passed a label, the label will be updated
     * with the new status.
     * @param frequency The interval on which the status should be checked.
     * @param service The service to check.
     * @param labelToUpdate The label to update with the status.
     */
    public ServiceCheckThread(long frequency,WeatherServiceNames service,
            JLabel labelToUpdate){
        this.frequency=frequency;
        this.service=service;
        this.serviceStatus=ServiceControl.UNKOWN;
        this.labelToUpdate=labelToUpdate;
        
        this.labelToUpdate.setText(serviceStatus);
    }

    /**
     * Checks the current status of the service.  If the label is not null,
     * updates the label with the retrieved status.  Sleeps for a specified
     * amount of time to check at the frequency interval.
     */
    @Override
    public void run() {
        running=true;
        while(running){
            try{
                serviceStatus=ServiceControl.checkService(service);
                if(labelToUpdate!=null){
                    labelToUpdate.setText(serviceStatus);
                }
                Thread.sleep(frequency);
            }
            catch(InterruptedException ex){
                running=false;
            }
        }
    }

    /**
     * Tells the thread to stop running.
     */
    public void stopRunning(){
        running=false;
    }

}
