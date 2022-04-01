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

/**
 *
 * @author ordenovic
 * @version $Id
 */
public class Bolometry implements Comparable {
    private final double teff;
    private final double logg;
    private final double feh;
    private final double alphaFe;
    private final double bc;
  
    /**
     * 
     * @param teff the effective temp (K)
     * @param logg the log10 of gravity (dex)
     * @param feh the metallicity (dex)
     * @param alphaFe the abundances (dex)
     * @param bc the bolometric correction (mag)
     */
    public Bolometry(double teff, double logg, double feh, double alphaFe, double bc) {
        this.teff = teff;
        this.logg = logg;
        this.feh = feh;
        this.alphaFe = alphaFe;
        this.bc = bc;
    }
   
    
    /**
     * 
     * @return the temperature 
     */
    public double getTeff() {
        return teff;
    }

    /**
     * 
     * @return the log of gravity
     */
    public double getLogg() {
        return logg;
    }

    /**
     * 
     * @return the metallicity
     */
    public double getFeh() {
        return feh;
    }

    /**
     * 
     * @return the abundance
     */
    public double getAlphaFe() {
        return alphaFe;
    }

    /**
     * 
     * @return the bolometric correction
     */
    public double getBc() {
        return bc;
    }

    @Override
    public int hashCode() {
        final int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bolometry other = (Bolometry) obj;
        
        if (Math.abs(this.teff - other.getTeff()) > 1e-15) {
            return false;
        }
        if (Math.abs(this.logg - other.getLogg()) > 1e-15) {
            return false;
        }
        if (Math.abs(this.feh - other.getFeh()) > 1e-15) {
            return false;
        }
        if (Math.abs(this.alphaFe - other.getAlphaFe()) > 1e-15)  {
            return false;
        }
        return true;
    }
    
   
    /**
     * @param o
     * @return 1 if o lower than this, -1 if upper
     */
    @Override
    public int compareTo(Object o) {
       final double[] p2 = ((Bolometry)o).getParamsAsArray();
       final double[] p1 = this.getParamsAsArray();

       // T1 > T2
        if (p1[0] > p2[0]) {
            return 1;
        }

        if (p1[0] < p2[0]) {
            return -1;
        }

        //T1 == T2
        if ( Math.abs(p1[0] -p2[0]) < 1e-15) {
            
            // Log1 > Log2
            if (p1[1] > p2[1]) {
                return 1;
            }

            if (p1[1] < p2[1]) {
                return -1;
            }

            // Log1 = Log2
            if ( Math.abs(p1[1] -p2[1]) < 1e-15 ) {
                
                // Fe1 > Fe2
                if (p1[2] > p2[2]) {
                    return 1;
                }
                if (p1[2] < p2[2]) {
                    return -1;
                }

                //Fe1 = Fe2
                if ( Math.abs(p1[2] -p2[2]) < 1e-15) {

                    // Al1 > Al2
                    if (p1[3] > p2[3]) {
                        return 1;
                    }
                    if (p1[3] < p2[3]) {
                        return -1;
                    }
                }
            }
        }
        return 0;
    }
    
    /**
     * 
     * @return parameters as an array
     */
    public double[] getParamsAsArray() {       
        return new double[]{teff, logg, feh, alphaFe};
    }
    
    /**
     * 
     * @return a string view
     */
    @Override
    public String toString() {
        final String msg = Double.toString(teff) +" " +Double.toString(logg) + " "+ 
                Double.toString(feh) + " " +Double.toString(alphaFe) + " "+ Double.toString(bc);
        return msg;
    } 
}