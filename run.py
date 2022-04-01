#!/usr/bin/python3
from py4j.java_gateway import JavaGateway
import subprocess
import time
#commentbyorlagh

localDir=''
#Exécuter en spécifiant le chemin de classe
args=(["java","-classpath",localDir+"lib/py4j-0.10.9.5.jar:"+localDir+"dist/BolometricCorrectionsReader.jar",'bolometriccorrectionsreader.BolometricCorrectionsReader'])
p=subprocess.Popen(args)
#Empêcher le traitement de s'arrêter avant de démarrer le serveur
time.sleep(0.5) 
gateway = JavaGateway(start_callback_server=True)
app = gateway.entry_point
#
#------------------------------
#
teff=4500.0
logg=2.2
metal=0.23
alpha=0.0
offset=0.0


print(app.getBc(teff, logg, metal, alpha, offset))
#Tuez le processus
gateway.shutdown()
