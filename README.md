# AMIA2020

# Creation Dictionary

## UMLS

* Register at NIV and download the UMLS from https://www.nlm.nih.gov/research/umls/index.html
* You need files from the UMLS. After registration at [UTS](https:/uts.nlm.nih.gov), you can download the UMLS files from the [U.S. National Library of Medicine (NIH)](https://www.nlm.nih.gov/research/umls/). Unpack the ZIP file of a UMLS version (e.g. `umls-2017AA-full.zip`).
* Unpack the files `MRCONSO.RRF.aa` and `MRCONSO.RRF.ab` (files of Concept Names and Sources), concatenate these both to one file and unpack `MRSTY.RFF` (Semantic Types). Use these files for the input of JuFiT. 
* More information on the UMLS can be found in the [UMLS® Reference Manual](https://www.ncbi.nlm.nih.gov/books/NBK9676/).

# JuFit

* Download JuFit from https://github.com/JULIELab/jufit and create the jar file by maven

