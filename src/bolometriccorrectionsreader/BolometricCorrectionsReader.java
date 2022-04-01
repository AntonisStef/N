/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bolometriccorrectionsreader;

import bolometriccorrectionsreader.exception.OutOfBolometryGridException;
import bolometriccorrectionsreader.io.DataReader;
import bolometriccorrectionsreader.struct.Bolometry;
import bolometriccorrectionsreader.struct.BolometryTable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import py4j.GatewayServer;

/**
 *
 * @author ordenovic
 */
public class BolometricCorrectionsReader {
    private BolometryTable table;
    private static final Logger LOG=Logger.getLogger("BolometricCorrectionsReader");
    
    /**
     * 
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        final BolometricCorrectionsReader app = new BolometricCorrectionsReader();
        final GatewayServer server = new GatewayServer(app);
        server.start();
        LOG.fine("Gateway Server Started");
    }
    
    /**
     * 
     * @throws IOException 
     */
    public BolometricCorrectionsReader() throws IOException {     
        Properties properties = new Properties();
        properties.load(new FileInputStream("conf/props.properties"));
        String fileName = properties.getProperty("bc");
        loadTable(fileName);
    }
    
    /**
     * 
     * @throws IOException 
     */
    private void loadTable(String fileName) throws IOException {
        table = new BolometryTable(DataReader.loadBolometricCorrectionsFile(fileName));
    }
    
    /**
     * 
     * @param teff
     * @param logg
     * @param metalH
     * @param alphaFe
     * @param offset
     * @return 
     */
    public double getBc(double teff, double logg, double metalH, double alphaFe, double offset) {
        final Bolometry b = new Bolometry(teff, logg, metalH, alphaFe, 0);
        
        if (table != null) {
            double bc;
            
            try {            
                bc = table.interpolate(b);
            } catch(OutOfBolometryGridException e) {
               // logger.warn(e.getMessage());
                bc = table.nearestBolometryCorrection(b);          
            }     
            bc = bc+ offset;//;
            return bc;
        } else {
            return Double.NaN;
        }
    }  
}