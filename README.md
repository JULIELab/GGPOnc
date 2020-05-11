# AMIA2020

# Creation Dictionary

## UMLS

* Register at NIV and download the UMLS from https://www.nlm.nih.gov/research/umls/index.html
* You need files from the UMLS. After registration at [UTS](https:/uts.nlm.nih.gov), you can download the UMLS files from the [U.S. National Library of Medicine (NIH)](https://www.nlm.nih.gov/research/umls/). Unpack the ZIP file of a UMLS version (e.g. `umls-2019AB-full.zip`).
* Unpack the files `MRCONSO.RRF.aa` and `MRCONSO.RRF.ab` (files of Concept Names and Sources), concatenate these both to one file and unpack `MRSTY.RFF` (Semantic Types). Use these files for the input of JuFiT. 
* More information on the UMLS can be found in the [UMLS® Reference Manual](https://www.ncbi.nlm.nih.gov/books/NBK9676/).

# JuFiT: filtered dictionaries from UMLS

* Download JuFit from https://github.com/JULIELab/jufit and create the jar file by Maven
* run `java -jar JenaUmlsFilter-1.1-jar-with-dependencies.jar MRCONSO.RRF MRSTY.RRF GER --grounded > UMLS_dict.txt`
* run the script request-jufit.sh for dictionaries of the different semantic groups
* run the script createDics.py to create on large dictionary (before run: adapt paths)


# Dictionary Format

* We use following format in our dictionaries:
    * one line per entry
    * seperated by tabulators

# JCoRe Pipeline
* unpack the *.zip files in jcore-pipelines, there are 2 pipelines: dectectUMLSentries and detectStopwords
* put the UMLS dictionary file into jcore-pipelines/detectUMLSentries/resources
* put your analysis text data into data/files (subdirectories are not read, be carefuly with *.tar files)
* adapt filename of the dictionary and the stopword dictionary in the following files:
Einstellung des zu filternden Wörterbuches und des Stopwörterbuches in folgenden Dateien anpassen:
   * desc/GazetteerAnnotator Template Descriptor with Configurable External Resource.xml
   * descAll/GazetteerAnnotator, Template Descriptor with Configurable External Resource.xml
* open a terminal and root into one of the pipeline directories
* start the pipeline with 'java -jar ../jcore-pipeline-runner-base-0.4.1-SNAPSHOT-cli-assembly.jar run.xml '
* and have a look into 
   * offsets.tsv
   * data/outData/output-xmi
   
# Conversion of corpus XML file to plain text and preprocessing

* Copy `cpg-corpus-cms.xml` into src/main/resources
* Usage: Start CPGXMLReader.java and look into the directory /output (`mvn exec:java -Dexec.mainClass="de.hpi.guidelines.reader.CPGXMLReader"` from the command line).
