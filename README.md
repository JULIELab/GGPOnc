GGPONC - A Corpus of German Medical Text with Rich Metadata Based on Clinical Practice Guidelines

=================================================================================================

This repository contains the code to reproduce the results in:
[https://arxiv.org/abs/2007.06400](https://arxiv.org/abs/2007.06400)

# Prerequisites

## Requesting text data

* GGPONC source files:
    * Follow the instructions of the [GGPONC website (Access & Download)](https://www.leitlinienprogramm-onkologie.de/projekte/ggponc-english/)
    * Copy `cpg-corpus-cms.xml` into `src/main/resources`
* PubMed Abstracts from German Case Reports and Case Descriptions
    * Install [Entres API from NCBI](https://www.ncbi.nlm.nih.gov/books/NBK179288/) or [EDirect](https://dataguide.nlm.nih.gov/edirect/install.html), the commandline tools requesting the PubMed infrastructure
    * Open a terminal and type `esearch -db pubmed -query "Case Reports[Publication Type] AND GER[LA]" | efetch -format xml > allGermanPubMedCaseAbstracts.xml` (This step could take an hour.)
    * export the extracted file `allGermanPubMedCaseAbstracts.xml` into `src/main/resources`
* JSYNCC v1.1: follow the instructions of [https://github.com/JULIELab/jsyncc](https://github.com/JULIELab/jsyncc) or contact [Christina Lohr](https://github.com/chlor)
* [3000PA](http://ebooks.iospress.nl/volumearticle/48747): no public access
* [KRAUTS Corpus](https://sites.google.com/site/ittimeml/documents) [(Strötgen et al)](https://www.aclweb.org/anthology/L18-1085/): 
* [WikiWarsDe Corpus](https://heidata.uni-heidelberg.de/dataset.xhtml?persistentId=doi:10.11588/data/10026) [(Strötgen et al)](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.384.6136&rep=rep1&type=pdf#page=135)

## UMLS Terminology data

* You need files from the [UMLS](https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html).
* You need a registration at [UTS](https:/uts.nlm.nih.gov), you can download the UMLS files from the [U.S. National Library of Medicine (NIH)](https://www.nlm.nih.gov/research/umls/). 
* For our current work, we used the [UMLS release 2019AB](https://www.nlm.nih.gov/research/umls/licensedcontent/umlsarchives04.html#2019AB) and you need the following files:
    * [2019AB MRCONSO.RRF](https://download.nlm.nih.gov/umls/kss/2019AB/umls-2019AB-mrconso.zip)
    * [2019AB MRSTY.RRF](https://download.nlm.nih.gov/umls/kss/2019AB/umls-2019AB-full.zip) (only accessible from the full release zip file.)
    * unzip the files.

* [More information of UMLS releases](https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html)
* More information on the UMLS can be found in the [UMLS® Reference Manual](https://www.ncbi.nlm.nih.gov/books/NBK9676/).
	
## Software requirements
* [Java 11](https://openjdk.java.net/projects/jdk/11/) - We prefer [Open JDK](https://openjdk.java.net/)
* [Apache Maven (mvn)](https://maven.apache.org/)
* [Python 3](https://www.python.org/)
=> We prefer to use [Eclipse IDE](https://www.eclipse.org/downloads/) or [IntelliJ IDEA](https://www.jetbrains.com/de-de/idea/)

## Configuration after downloading of this repository
* Configure the project as a Maven project
    * In Eclipse: right click on project => Configure => Convert to Maven Project
    * Command line: `mvn compile`

# Processing the data

## Conversion of GGPOnc corpus XML file to plain text and preprocessing
* Run `mvn compile` before executing `mvn exec:java -Dexec.mainClass="de.hpi.guidelines.reader.CPGXMLReader"` or run `CPGXMLReader.java` (in package `de.hpi.guidelines.reader`) in Eclipse (Run As => Java Application)
* Wait a minute
* Look into the directory `/output`

## Create PubMed abstract text files

* We download PubMed data at February 21 2020, if you download PubMed data by esearch commands, you will receive a larger text corpus than our export. The file `src/main/resources/usedPubMedIds_20200221.txt` contains a list with the used PubMed identifiers from February 21 2020.
* If you want to create the described data set from PubMed, import your extracted XML file and run the `src/main/extractPubMedCaseAbstracts.java`. This code is able to filter our used PubMed text data from your new created download.   

# Processing dictionaries

## Filtered dictionaries from UMLS by JuFiT

* We worked with JuFit v1.1 - you can find the right jar file in this repository.
* If you want to work with the real JuFit, follow the steps below:
    * Download JuFit from [https://github.com/JULIELab/jufit](https://github.com/JULIELab/jufit)
    * create the jar file by Apache Maven and run `mvn clean package`
    * run `java -jar JuFiT.jar MRCONSO.RRF MRSTY.RRF GER --grounded > UMLS_dict.txt`
* Run the Java Code `RequestJuFiT.java` (package `de.julielab.dictionaryhandling`) or the Python script `extended_script_dictionaries/request-jufit.sh`

## Gene Dictionary
* We used a list of gene names compiled from *Entrez Gene* and *UniProt* with the approach originating from [Wermter et al.](https://pubmed.ncbi.nlm.nih.gov/19188193/)
* Code of [JULIELab/gene-name-mapping](https://zenodo.org/record/3874895#.XxG0Zh0aRhE)
* The integration of this code in the GGPOnc Repository is coming soon.

## Connect Dictionaries
* For the usage of JCoRe Pipelines you will need one large file `global_dictionary.txt` 
* Run the script `extended_script_dictionaries/createDics.py` to create on large dictionary (before run: adapt path names in the script file)
* Or run the Java Code `CreateLargeDictionary.java` (package `de.julielab.dictionaryhandling`) (before run: adapt path names in the script file)

## JCoRe Pipeline
* Unpack the `*.zip` files in `jcore-pipelines`, there are 2 pipelines:
    * _dectectUMLSentries_
    * _detectStopwords_
* Create the folder `data/files` in the pipeline directories and put the data to be analyzed in the directory `data/files` (subdirectories are not read, be carefully with `*.tar` files)
* Put the global dictionary file into `jcore-pipelines/detectUMLSentries/resources`
* Adapt filename of the dictionary and the stopword dictionary in the following files:
   * `desc/GazetteerAnnotator` Template Descriptor with Configurable External `Resource.xml`
   * `descAll/GazetteerAnnotator` Template Descriptor with Configurable External `Resource.xml`
* Open a terminal and root into one of the pipeline directories
* Start the pipeline with `java -jar ../jcore-pipeline-runner-base-0.4.1-SNAPSHOT-cli-assembly.jar run.xml`
* Results 
   * `offsets.tsv`
   * `data/outData/output-xmi`

# Evaluation

To calculate precision and recall between automatically created annotations and the human annotated data run:

* `pip install bratutils`
* `python src/main/python/umls_evaluation.py <path to gold annotations> <path to automatic annotations>`
