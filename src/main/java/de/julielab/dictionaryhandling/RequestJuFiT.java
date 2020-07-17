package de.julielab.dictionaryhandling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestJuFiT {
	
	public static final String UMLS_VERSION = "UMLS-2019AB";
	
	public static void main(String[] args) {
		Path umlsDir = Paths.get("src", "main", "resources", "UMLS-Dictionaries");

		if (!(umlsDir.toFile().exists())) {
			try {
				Files.createDirectory(umlsDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Runtime.getRuntime().exec(getJuFiTCommand(umlsDir, "ANAT", "GER"));
			Runtime.getRuntime().exec(getJuFiTCommand(umlsDir, "CHEM", "GER"));
			Runtime.getRuntime().exec(getJuFiTCommand(umlsDir, "DEVI", "GER"));
			Runtime.getRuntime().exec(getJuFiTCommand(umlsDir, "DISO", "GER"));
			Runtime.getRuntime().exec(getJuFiTCommand(umlsDir, "LIVB", "GER"));
			Runtime.getRuntime().exec(getJuFiTCommand(umlsDir, "PHEN", "GER"));
			Runtime.getRuntime().exec(getJuFiTCommand(umlsDir, "PHYS", "GER"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getJuFiTCommand(Path umlsDir, String semGroup, String langauge) {
		String firstPart = "java -jar JenaUmlsFilter-1.1-jar-with-dependencies.jar MRCONSO.RRF MRSTY.RRF GER --grounded --outFile=";

		return firstPart + umlsDir + File.separator + UMLS_VERSION + "-" + semGroup + "-" + langauge + ".txt"
				+ " --semanticGroup=" + semGroup;
	}
}
