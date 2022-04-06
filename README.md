# Gaiadr3 Bcg
#
#
#
## To get the project, use the git repository : 
* git clone https://gitlab.oca.eu/ordenovic/gaiadr3_bcg
#
## you will find :
* the python class : bcg.py
* the unit test class : test_bcg.py
* a demo python file : demo.py
#
#
## The directory data contains the main bc table used for the DR3 : 
* bc_dr3_feh_all.txt

## You can run the unit test file by the command:
* python3 -m unittest test_bcg.py

## The demo can be run by:
* python3 demo.py

## For using it in your code, you just have to:
* import the python file bc.py by the command : import bcg
* create the object by calling the constructor: bcg.BolometryTable('data/bc_dr3_feh_all.txt')
* call the method computeBc(point <,offset>)
* point is a list of 4 elements : [teff, logg, metallicity, alpha/Fe]
* offset is an optional floating value (0 by default)

## other way running by script
* e.g : python3 bcg.py 2555 5 -0.5 0.2

