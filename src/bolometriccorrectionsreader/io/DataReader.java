/*------------------------------------------------------------------------------
 *
 *                        Laboratoire Cassiopee
 *                    Observatoire de la Cote d'Azur
 *
 *                        (c) 2009 OCA
 *
 *------------------------------------------------------------------------------
 *
 * CU8 OCA
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
 * 
 * Copyright (c) 2009 Observatoire de la Cote d'Azur
 *
 * 
 */
package bolometriccorrectionsreader.io;

import bolometriccorrectionsreader.struct.Bolometry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Ordenovic
 * @version Id$
 *
 */
public class DataReader {
    
    /**
     * 
     * @param fileName the bolometric correction ascii file
     * @return the gaia dm object
     * @throws IOException if it not exists
     */
    public static List<Bolometry> loadBolometricCorrectionsFile(String fileName) throws IOException {
        final FileReader reader = new FileReader(fileName);
        final BufferedReader buffer=new BufferedReader(reader);
        String line="";
        buffer.readLine();
        final ArrayList<Bolometry> listBc = new ArrayList<>();
                
        while ( (line=buffer.readLine()) != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(line);            
            final double teff = Double.parseDouble(stringTokenizer.nextToken());
            final double logg = Double.parseDouble(stringTokenizer.nextToken());
            final double feh = Double.parseDouble(stringTokenizer.nextToken());
            final double alphaFe = Double.parseDouble(stringTokenizer.nextToken());
            final double bc = Double.parseDouble(stringTokenizer.nextToken());
            final Bolometry b = new Bolometry(teff, logg, feh, alphaFe, bc);
            listBc.add(b);
        }     
        return listBc;
    }
}