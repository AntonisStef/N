# Gaiadr3 Bcg
#
#
## this is an intermediary development step where we use a python wrapper
## the next steps are : 
###  * to use a bash script to directly call the python wrapper by giving arguments
###  * to rewrite the main java class as a python class 
#
to get the project, use the git repository : 
https://gitlab.oca.eu/ordenovic/gaiadr3_bcg

you need to install the python package py4j, by using 
pip install py4j

then, you can run the test : 
python3 run.py

inside the python script you have 2 parts:
the first one creates the gate between python and java; it first run the java code by emulating the java command as a thread:
java -classpath  ***.jar bolometriccorrectionsreader.BolometricCorrectionsReader

then the gate is created and the java class is linked (here : app)

The second part shows how to call the method : 
you can call it by  : app.getBc(double teff,double logg, double metal , double alpha , double offset)


It returns the value of the bolometric correction


