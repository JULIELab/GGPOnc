AMIA2020
========

# Creation text data

## Conversion of corpus XML file to plain text and preprocessing

* Copy `cpg-corpus-cms.xml` into src/main/resources
* Usage: Start CPGXMLReader.java and look into the directory /output (`mvn exec:java -Dexec.mainClass="de.hpi.guidelines.reader.CPGXMLReader"` from the command line).

## Get German PubMed Abstracts from Case Reports

 * install the [Entres API from NCBI](https://www.ncbi.nlm.nih.gov/books/NBK179288/)
 * run the following command:
 * `esearch -db pubmed -query "Case Reports[Publication Type] AND GER[LA]" | efetch -format xml > allGermanPubMedCaseAbstracts.xml`
 * import the file name of the extracted XML file in `allGermanPubMedCaseAbstracts.java` (in package de.julielab.handelpubmedfiles)

# Creation Dictionaries

## Dictionary Format

* We use following format in our dictionaries:
   * one line per entry
   * separated by tabulators

## UMLS

* Register at NIV and download the UMLS from https://www.nlm.nih.gov/research/umls/index.html
* You need files from the UMLS. After registration at [UTS](https:/uts.nlm.nih.gov), you can download the UMLS files from the [U.S. National Library of Medicine (NIH)](https://www.nlm.nih.gov/research/umls/). Unpack the ZIP file of a UMLS version (e.g. `umls-2019AB-full.zip`).
* Unpack the files `MRCONSO.RRF.aa` and `MRCONSO.RRF.ab` (files of Concept Names and Sources), concatenate these both to one file and unpack `MRSTY.RFF` (Semantic Types). Use these files for the input of JuFiT. 
* More information on the UMLS can be found in the [UMLS® Reference Manual](https://www.ncbi.nlm.nih.gov/books/NBK9676/).

## JuFiT: filtered dictionaries from UMLS

* Download JuFit from https://github.com/JULIELab/jufit and create the jar file by Maven
* run `java -jar JenaUmlsFilter-1.1-jar-with-dependencies.jar MRCONSO.RRF MRSTY.RRF GER --grounded > UMLS_dict.txt`
* run the script `request-jufit.sh` for dictionaries of the different semantic groups
* run the script `createDics.py` to create on large dictionary (before run: adapt paths)

## JCoRe Pipeline
* unpack the `*.zip` files in `jcore-pipelines`, there are 2 pipelines: _dectectUMLSentries_ and _detectStopwords_
* put the UMLS dictionary file into `jcore-pipelines/detectUMLSentries/resources`
* put your analysis text data into data/files (subdirectories are not read, be carefuly with `*.tar` files)
* adapt filename of the dictionary and the stopword dictionary in the following files:
   * `desc/GazetteerAnnotator` Template Descriptor with Configurable External `Resource.xml`
   * `descAll/GazetteerAnnotator` Template Descriptor with Configurable External `Resource.xml`
* open a terminal and root into one of the pipeline directories
* start the pipeline with `java -jar ../jcore-pipeline-runner-base-0.4.1-SNAPSHOT-cli-assembly.jar run.xml`
* and have a look into 
   * `offsets.tsv`
   * `data/outData/output-xmi`
