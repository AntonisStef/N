/*------------------------------------------------------------------------------
 *
 *                        Laboratoire Lagrange
 *                    Observatoire de la Cote d'Azur
 *
 *                        (c) 2015 OCA
 *
 *------------------------------------------------------------------------------
 *
 * CU8 FLAME
 * Copyright (C) 2006-2011 Gaia Data Processing and Analysis Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package bolometriccorrectionsreader.struct;


import bolometriccorrectionsreader.exception.OutOfBolometryGridException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 *
 * @author ordenovic
 * @version $Id
 */
public class BolometryTable {
    private final List<Bolometry> bolometry;
    private double[] maxValues;
    private double [] minValues;
    private LinkedHashMap<Integer, double[]> parameters;
    private double[] delta;
    
    /**
     * sort the list of bolometric table.
     * @param bolometry the list of bolometry objects
     */
    public BolometryTable(List<Bolometry> bolometry) {
        Collections.sort(bolometry);
        this.bolometry = bolometry;
        init();
    }

    /**
     * 
     * @return the list of bolometry objects
     */
    public List<Bolometry> getBolometry() {
        return bolometry;
    }
    
    
    
    
    /**
     * work on a sorted list.
     * @param g the bolometry object
     * @return its index on the list
     */
    public int where(Bolometry g) {    
        int bas = 0;
        int haut = bolometry.size()-1;
        int result = -1;
        int milieu;

        do {
            milieu = (bas + haut) / 2;
            final Bolometry elem = bolometry.get(milieu);

            if (elem.equals(g)) {
                result = milieu;
            } else if (elem.compareTo(g) == -1) {
                bas = milieu + 1;
            } else {
                haut = milieu -1;
            }
        } while ( !g.equals(bolometry.get(milieu)) && (bas <= haut));
        return result;
    }
    
    /**
     *
     * @param type {@inheritDoc }
     * @param g {@inheritDoc }
     * @return {@inheritDoc }
     */ 
    public int getNextNode(int type, Bolometry g) {
        return search(type, g, 1);
    }
    
     /**
     *
     * @param type {@inheritDoc }
     * @param g {@inheritDoc }
     * @return {@inheritDoc }
     */ 
    public int getPreviousNode(int type, Bolometry g) {
        return search(type, g, -1);
    }
    
    /**
     * 
     * @param g a set of values teff, logg, feh, alpha
     * @return the bolometric correction associated with the nearest set of parameters 
     */
    public double nearestBolometryCorrection(Bolometry g) {
        final int pos = nearestIndex(g);
        return bolometry.get(pos).getBc();
    }
    
    /**
     * 
     * @param g the parameters
     * @return the nearest grid  node index
     */
    private int nearestIndex(Bolometry g) {   
        final double roundingVal=1e12;
        final double[] nearest = new double[4];
        // parameters stored as : node -> values
        //double[][] parameters = this.getParametersAsArray();
        final double[] gParams = g.getParamsAsArray();
        final double[] paramsLower = new double[gParams.length];
        final double[] paramsUpper = new double[gParams.length];
        
        for (int paramOrdinal = 0 ; paramOrdinal < 4 ; paramOrdinal++) {              
            // valeur en T de l'input
            final double valG = Math.round(gParams[paramOrdinal] * roundingVal)/roundingVal;
            
            if (valG >= this.maxValues[paramOrdinal] ) {
                nearest[paramOrdinal] = this.maxValues[paramOrdinal];           
            } else if (valG <= this.minValues[paramOrdinal]) {
                nearest[paramOrdinal] = this.minValues[paramOrdinal];
            } else {
           
                // loop on nodes
                for (int j=0 ; j < parameters.get(paramOrdinal).length ; j++) {
              
                    if ( Math.abs(parameters.get(paramOrdinal)[j] - gParams[paramOrdinal] ) < 1/roundingVal) {
                        nearest[paramOrdinal] = parameters.get(paramOrdinal)[j];                      
                        paramsLower[paramOrdinal] = nearest[paramOrdinal];
                        paramsUpper[paramOrdinal] = nearest[paramOrdinal];
                        break;
                    } else if (parameters.get(paramOrdinal)[j] > gParams[paramOrdinal] ) {
                        paramsLower[paramOrdinal] = parameters.get(paramOrdinal)[j-1];
                        paramsUpper[paramOrdinal] = parameters.get(paramOrdinal)[j];
                        
                        if (Math.abs(parameters.get(paramOrdinal)[j-1] - gParams[paramOrdinal]) > 
                                Math.abs(parameters.get(paramOrdinal)[j] - gParams[paramOrdinal])) {
                            nearest[paramOrdinal] = parameters.get(paramOrdinal)[j];
                        } else {
                             nearest[paramOrdinal] = parameters.get(paramOrdinal)[j-1];
                        }
                        
                        break;
                    }
                }
            }
        }
        
        int index = this.where(new Bolometry(nearest[0],nearest[1],nearest[2],nearest[3],0));
        
        if (index != -1) {
            return index;
        } else {
            
        // copy of upper et lowwer
        int idMin = this.where(new Bolometry(paramsLower[0],paramsLower[1],paramsLower[2],paramsLower[3],0));
       
        if (idMin == -1) {     
            
            for (int j=0 ; j < 4 ; j++) {
                
                  if ( (idMin = search(j,
                          new Bolometry(paramsLower[0],paramsLower[1],paramsLower[2],paramsLower[3],0),-1)) != -1) {
                      break;
                  }
             }
        }
        
        if (idMin == -1) {
            idMin = 0;
        }
        
         // copy of upper et lowwer
        int idMax = this.where(new Bolometry(paramsUpper[0],paramsUpper[1],paramsUpper[2],paramsUpper[3],0));
    
        if (idMax == -1) {
                        
            for (int j=0 ; j < 4 ; j++) {
                
                if ( (idMax = search(j,
                        new Bolometry(paramsUpper[0],paramsUpper[1],paramsUpper[2],paramsUpper[3],0),1)) != -1) {
                    break;
                }
            }
        }
        
        if (idMax == -1) {
            idMax = this.bolometry.size()-1;
        }
   
        double dMax = Double.POSITIVE_INFINITY;
        
        for (int j=idMin ; j <=idMax ; j++ ) {
            double distance=0;
                  
            for (int k=0 ; k < 4 ; k++) {
                    final double x = this.bolometry.get(j).getParamsAsArray()[k];
                    final double y = gParams[k];                   
                    distance += Math.pow( (x-y) / delta[k],2);              
            }         
                     
            if (distance < dMax) {
                dMax = distance;
                index = j;
            }
        }
        return index;
        }
    }
    
     /**
     *
     *
     * @param g {@inheritDoc }
     * @return {@inheritDoc }
     * @throws bolometriccorrectionsreader.exception.OutOfBolometryGridException
     */ 
    public double interpolate(Bolometry g) throws OutOfBolometryGridException {
        final int nParams = 4;
        
        for (int type=0 ; type < nParams ; type++) {
            final double val = g.getParamsAsArray()[type];

            if (val > maxValues[type]+1e-12 || val < minValues[type]-1e-12) {
                throw new OutOfBolometryGridException(g.toString() + "is outside grid\n"+
                        "range is : [ " + minValues[type]+ " " + maxValues[type]+"] for value : " + val);
            }
        }

        if (this.where(g) != -1) {
            final int pos = this.where(g);
            return bolometry.get(pos).getBc();
        } else {
           
            // recherche du point de grille le plus proche
            final int index  = nearestIndex(g);
            
            final Bolometry nearGridNode = bolometry.get(index);
            final double[] gridValue = new double[nParams];
            final double[] nearGridValue = new double[nParams];
            final double[] boloParamValue = new double[nParams];          

            for (int type=0 ; type < nParams ; type++ ) {               
                boloParamValue[type] = g.getParamsAsArray()[type];
                nearGridValue[type] = nearGridNode.getParamsAsArray()[type];
                
                if (boloParamValue[type] < nearGridValue[type]) {
                    final int prevNode = this.getPreviousNode(type, nearGridNode);

                    if (prevNode == -1) {                    
                        throw new OutOfBolometryGridException("starting point  is outside grid for parameter " +
                                type + " : "+ boloParamValue[type]);                       
                    }
                    final double val = bolometry.get(prevNode).getParamsAsArray()[type];
                    nearGridValue[type] = val;
                    gridValue[type] = val;
                    
                } else {
                    final double val = nearGridNode.getParamsAsArray()[type];                   
                    gridValue[type] = val;
                }
            }
            
            // copy of nearGridNode
            final Bolometry nearGridNodeCopy = 
                    new Bolometry(gridValue[0], gridValue[1], gridValue[2], gridValue[3],0);

            // building of nearest nodes 
            // ie  4 parameters :
            //    {0,0,0,0},{0,0,0,1},{0,0,1,0},{0,0,1,1},
            //    {0,1,0,0},{0,1,0,1},{0,1,1,0},{0,1,1,1},
            //    {1,0,0,0},{1,0,0,1},{1,0,1,0},{1,0,1,1},
            //    {1,1,0,0},{1,1,0,1},{1,1,1,0},{1,1,1,1}};*/
            final int nElems = (int)Math.pow(2,nParams);
            final Bolometry[] hyperCubeNodes = new Bolometry[nElems];
            final double[] hyperCubeBC = new double[nElems];
            final int [][] coeff = new int[nElems][nParams];
            final LinkedHashMap<Integer, Integer> v = new LinkedHashMap<>();

            // hyperCubeparameters contient les coordonnees des sommets de l'hypercube.
            for (int i=0 ; i < nElems; i++) {
              
                try {               
                    int val = i;
                    double[] par = new double[nParams];
                    
                    for (int j=0 ; j < nParams ; j++) {
                        double currentParamNodeCube = gridValue[j];
                
                        if ( val %2  == 1) {
                            coeff[i][j] = 1;
                            final int type = j;

                            if (!v.containsKey(type)) {
                                int nextNode = this.getNextNode(type, nearGridNodeCopy);
                       
                                if (nextNode == -1) {
                                    nextNode = this.getPreviousNode(type, nearGridNodeCopy);

                                    if (nextNode == -1) {
                                        throw new OutOfBolometryGridException("can not find previous point : " +
                                                type + " for "  + nearGridNodeCopy);
                                    }
                                }
                                v.put(type,nextNode);
                            }
                            currentParamNodeCube = bolometry.get(v.get(type)).getParamsAsArray()[type];
                        }
                        val /= 2 ;
                        par[j]= currentParamNodeCube;
                    }
                                        
                    final Bolometry hyperCubeNode = new Bolometry(par[0], par[1], par[2], par[3],0);
                    hyperCubeNodes[i] = hyperCubeNode;
                    final int indexHyperCubeNode = this.where(hyperCubeNode);

                    if (indexHyperCubeNode == -1) {
                        throw new OutOfBolometryGridException("can not build hypercube : point " +
                                hyperCubeNode + "does not exists");
                    }
                    hyperCubeBC[i] = bolometry.get(indexHyperCubeNode).getBc();    
                } catch(OutOfBolometryGridException e) {                   
                    hyperCubeNodes[i] = new Bolometry(gridValue[0], gridValue[1], gridValue[2], gridValue[3],0);
                    final int indexHyperCubeNode = this.where(hyperCubeNodes[i]);

                    try {
                        hyperCubeBC[i] = bolometry.get(indexHyperCubeNode).getBc();   
                    } catch(ArrayIndexOutOfBoundsException ex) {
                        throw new OutOfBolometryGridException("point outside grid" );
                    }
                }
            }
            double interpolated=0;
            final double[]  x = new double[nParams];

            for (int i=0 ; i < nParams ; i++) {

                if (hyperCubeNodes[ (int)Math.pow(2,i)].getParamsAsArray()[i] == gridValue[i]) {
                    x[i] = 0;
                } else {
                    x[i] = (boloParamValue[i] - gridValue[i]) /
                    (hyperCubeNodes[ (int)Math.pow(2,i)].getParamsAsArray()[i] - gridValue[i]);
                }
            }

            for (int i=0 ; i < coeff.length ; i++) {
                double c = 1;

                for (int j=0 ; j < coeff[i].length ; j++) {
                    c *= ((1-x[j]) + coeff[i][j] * (2*x[j] -1));
                }               
                interpolated += hyperCubeBC[i] * c;              
            }
            return interpolated;
        }    
    }
    
  
     /**
     *
     */
    private void init() {
        final int nParams =4;
        maxValues = new double[nParams];
        minValues =  new double[nParams];
        final int[] step = new int[nParams];
        final boolean[] flag = new boolean[nParams];

        Arrays.fill(step, 1);
        Arrays.fill(flag, true);
        
        for (int i=0 ; i < nParams ; i++) {
            minValues[i] = Double.MAX_VALUE;
        }
      
        final Bolometry start = bolometry.get(0);

        for(int i=1 ; i < bolometry.size(); i++) {
           final Bolometry map = bolometry.get(i);

            for (int type =0 ; type < nParams; type++ ) {
                final double value = map.getParamsAsArray()[type];               
                final double v = start.getParamsAsArray()[type];

                if ( value == v && flag[type]) {
                    step[type]++;
                } else {
                    flag[type] = false;
                }

                if (value > maxValues[type]) {
                    maxValues[type] = value;
                }

                if (value < minValues[type]) {
                    minValues[type] = value;
                }
            }
        }
        initParameters();
        delta = new double[step.length];

        int i=0;

        for (int type=0 ; type < nParams; type++) {

            if (parameters.get(type).length > 1) {
                delta[i++] = parameters.get(type)[1] - parameters.get(type)[0];
            }
        }  
    }

    /**
     * 
     * @return the max values of grid parameters
     */
    public double[] getMaxValues() {
        return maxValues;
    }

    /**
     * 
     * @return the min values of grid parameters
     */
    public double[] getMinValues() {
        return minValues;
    }

    /**
     * 
     * @return the grid step
     */
    public double[] getDelta() {
        return delta;
    }
    
    
    
    
    /**
     *
     * @param val the value
     * @param type the parameter id (0,1,2,3)
     * @param k (incrasing 1 or decreasing -1)
     * @return the next or previoous node value
     */
    private double findValue(double val, int type, int k) {
        double result = Double.NaN;
        final double[] values = parameters.get(type);

        for (int i=0 ; i < values.length ; i++) {

            if (val == values[i]) {

                try {
                    result = values[i+k];
                } catch(ArrayIndexOutOfBoundsException e) {
                    break;
                }
                break;
            }
        }
        return result;
    }
    
    /**
     *
     * @param type  the parameter
     * @param value the searched value
     * @param direction (incerasing 1 or decreasing -1)
     * @return true if the value exists in the grid
     */
    private boolean check(int type, double value,int direction) {
        boolean check = true;
     
        if (direction < 0) {

            if (value < minValues[type] ) {
                check = false;
            }
        } else if (direction > 0) {

            if (value > maxValues[type] ) {
                check = false;
            }
        }
        return check;
    }
    
    /**
     *
     * @param type the parameter
     * @param g the parameter
     * @param direction (incrasing 1 or decreasing -1)
     * @return the corresponding node index
     */
    private int search(int type, Bolometry g, int direction) {
        int result = -1;
        int k = 0;
        double paramValueSearched;

        do {
            if (direction < 0 ) {
                k--;
            } else {
                k++;
            }
            paramValueSearched = findValue(g.getParamsAsArray()[type], type, k);

            if (!Double.isNaN(paramValueSearched)) {
                final double[] p = new double[4];

                for (int t=0 ; t < 4 ; t++ ) {

                    if (t == type) {
                        p[t] =paramValueSearched;
                    } else {
                        p[t]= g.getParamsAsArray()[t];
                    }
                }
                result = this.where(new Bolometry(p[0], p[1], p[2], p[3],0));
            } else {
                return -1;
            }
        } while (result == -1 && check(type, paramValueSearched, direction));
        return result;
    }
    
    /**
     *
     */
    private void initParameters() {
         final double[][] allElems = getParametersAsArray();
         parameters = new LinkedHashMap<>();

         for (int type=0 ; type < 4 ; type++) {
            final Vector<Double> vecValues = new Vector<>();
            final Vector<Double> reference = new Vector<>();

            for (double[] allElem : allElems) {
                final double current = allElem[type];
                 
                if (!reference.contains(current)) {
                    vecValues.addElement(current);
                    reference.addElement(current);
                }
            }

            final double[] values = new double[vecValues.size()];

            for (int i=0 ; i < values.length ; i++) {
                values[i] = vecValues.get(i);
            }
            Arrays.sort(values);
            parameters.put(type, values);
        }
    }
    
    /**
     *
     * @return the parameters as an array
     */
    public double[][] getParametersAsArray() {
        final int nParams = 4;
        final double[][] p = new double[bolometry.size()][nParams];

        for (int i=0 ; i < bolometry.size() ; i++) {

            for (int type=0 ; type < nParams; type++ ) {
                p[i][type] = bolometry.get(i).getParamsAsArray()[type];
            }
        }
        return p;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String msg="";
        
        for (int i=0 ; i < bolometry.size() ; i++) {
            msg += String.format("%4d",i);
            
            for (int type=0 ; type < 4; type++ ) {
                msg += "  " + String.format(Locale.US, "%7.2f", bolometry.get(i).getParamsAsArray()[type]);
            }
            msg += " " +bolometry.get(i).getBc() + "\n";
        }
        return msg;
    }
}
