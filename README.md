# Gaiadr3 Bcg

## Description
This function takes as input the effective temperature Teff, surface gravity log g, iron abundance [Fe/H], and alpha-enhancement [alpha/Fe] values of a star, and returns the model bolometric correction in G-band (BC_G), using the Gaia EDR3 passbands, for that combination of parameters. The value of BC_G is based on the assumption of that the absolute magnitude of the Sun in this G-band, MG_Sun, is 4.66 mag, i.e. BC_G,Sun = +0.08 mag, see Creevey et al. (2022) for details. A user can specify an offset (in magnitudes) to this reference zeropoint directly in the function.

## To get the project, use the git repository : 
* git clone https://gitlab.oca.eu/ordenovic/gaiadr3_bcg

## you will find :
* the directory (package) gdr3bgc with the python class : bcg.py
* the test directory with the unit test class : test_bcg.py
* a demo python file : demo.py
* a setup file for package installation

## installation
* use python 3.6(+) and setuptools library
* run python3 setup.py install for package installation

## The directory data contains the main bc table used for the DR3 : 
* bc_dr3_feh_all.dat

## You can run the unit test file by the command:
* python3 setup.py test

## The demo can be run by:
* python3 demo.py

## To use it directly in your python code:
* import the python file bcg.py by the command : import gdr3bcg as bcg
* create the object by calling the constructor: bcg.BolometryTable()
* call the method computeBc(point <,offset>)
    * point is a list of 4 elements : [teff, logg, metallicity, alpha/Fe]
    * offset is an optional floating value (0 by default), see below for details.

## To run the script directly from the command line
* python3 bcg.py teff logg [Fe/H] [alpha/Fe]  e.g. python3 bcg.py 2555 5 -0.5 0.2  (answer -2.000)
* or to adjust the bolometric correction zeropoint: python3 bcg.py 2555 5 -0.5 0.2 0.02 (answer -1.980)

## Acknowledgements
When using this function we ask you to cite Creevey, O. L., Sordo, R., Pailler F., et al. 2022, A&A, "Gaia Data Release 3: Astrophysical parameters inference system (Apsis) I - overview"

This product makes use of public auxiliary models provided by ESA/Gaia/DPAC/CU8 and prepared by Christophe Ordenovic, Orlagh Creevey, Andreas Korn, Bengt Edvardsson, Oleg Kochukhov, and Frédéric Thévenin.

## Details of the models
The file bc_dr3_feh_all.dat is a compilation of two sets of tables: 
* the first is based on the MARCS models (Gustaffson et al. 2008) for the 2500 - 8000 K Teff range and was provided by Bengt Edvardsson for Gaia/DPAC/CU8/FLAME (Creevey et al. 2022). 
* The second is based on A star models (Shulyak et al. 2004) for the 6000 - 20000 K Teff range and was provided by Oleg Kochukhov for Gaia/DPAC/CU8/FLAME. 
For production of data in Gaia Data Release 3 the two tables are matched at 8,000 K.  Additionally, the tables were provided with an arbitrary offset such that the bolometric correction of the Sun in G band is -0.12 mag.  Therefore, in the function, a constant value of 0.20 mag is added to all values for the production of luminosities in Gaia Data Release 3, i.e. BC_G,Sun = +0.08 mag and M_G,Sun = 4.66 mag.


Note: for a given Teff, logg, [Fe/H] (or [M/H] with [alpha/Fe] = 0) in Gaia DR3, the published bolometric correction may be slightly different to the one given here, because it is based on the median value of Monte Carlo samples. 



