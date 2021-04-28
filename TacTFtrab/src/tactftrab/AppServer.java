/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

/**
 *
 * @author joma
 */
public class AppServer extends DefaultSingleRecoverable {

    public AppServer(int id) {
        
        new ServiceReplica(id, this, this);
    }

    @Override
    public byte[] appExecuteOrdered(byte[] bytes, MessageContext mc) {
        return null;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] bytes, MessageContext mc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getSnapshot() {
        return null;
    }

    @Override
    public void installSnapshot(byte[] bytes) {

    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
}
