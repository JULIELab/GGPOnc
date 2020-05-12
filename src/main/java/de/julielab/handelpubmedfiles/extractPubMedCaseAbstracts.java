package de.julielab.handelpubmedfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

/**
 * We installed Entrez Direct from and run at Feb 21 2020:
 * esearch -db pubmed-query "Case Reports[Publication Type] AND GER[LA]" | efetch -format xml > allGermanPubMedCaseAbstracts.xml
 * This file is the inport for the following code.
 */

public class extractPubMedCaseAbstracts {
	public static void main(String[] args) throws IOException// , UIMAException
	{
		String source = "allGermanPubMedCaseAbstracts.xml"; // 300 MB
		String out = "outPubMedTxt";

		List<TextDocument> listDocuments = new ArrayList<>();
		listDocuments = extractContent(source);

		writeTxtFiles(Paths.get(out), listDocuments);
	}

	public static List<TextDocument> extractContent(String source) throws IOException {
		List<TextDocument> listDocuments = new ArrayList<>();
		String language = "ger";
		List<String> lines = Files.readAllLines(Paths.get(source));

		boolean readAbstract = false;
		String pmid = "";
		String text = "";
		HashSet<String> pmids = new HashSet<String>();

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).startsWith("<PMID Version=\"1\">")) {
				pmid = lines.get(i).replaceAll("<PMID Version=\"1\">", "");
				pmid = pmid.replaceAll("</PMID>", "");
			}

			if (readAbstract) {
				text = text + "\n" + lines.get(i);
			}

			if (lines.get(i).startsWith("<OtherAbstract Type=\"Publisher\" Language=\"" + language + "\">")) {
				readAbstract = true;
			}

			if ((lines.get(i).startsWith("</OtherAbstract>")) && (!(text.isEmpty()))) {
				readAbstract = false;

				text = text.replaceAll("<AbstractText>", "");
				text = text.replaceAll("</AbstractText>", "");
				text = text.replaceAll("<AbstractText/>", "");
				text = text.replaceAll("</OtherAbstract>", "");

				text = text.replaceAll("<AbstractText Label=\"", "");
				text = text.replaceAll("\" NlmCategory=\"UNASSIGNED\">", "\n");
				text = text.replaceAll("\" NlmCategory=\"BACKGROUND\">", "\n");

				text = text.replaceAll("\">", "\n");
				text = text.replaceAll("\u00AD", ""); // soft hyphen

				if (text.startsWith("\n")) {
					text = text.replaceFirst("\n", "");
				}

				TextDocument textDocument = new TextDocument();
				textDocument.setText(text);
				textDocument.setId(pmid);

				LanguageDetector detector = new OptimaizeLangDetector().loadModels();
				LanguageResult result = detector.detect(text);
				String lang = result.getLanguage();

				if ( (!(pmids.contains(pmid))) && (lang.equals("de")) )
				{
					listDocuments.add(textDocument);
					pmids.add(pmid);
				}
				else
				{
					System.out.println("WARNING: " + pmid + " has more than 1 Abstract or is not German.");
				}

				text = "";
			}
		}
		return listDocuments;
	}

	public static void writeTxtFiles(Path outDirTxt, List<TextDocument> listDocuments) throws IOException {
		String fullText = "";

		if (Files.notExists(outDirTxt)) {
			Files.createDirectory(outDirTxt);
		}

		HashSet<String> pmids = new HashSet<String>();

		for (int i = 0; i < listDocuments.size(); i++) {
			String text = listDocuments.get(i).getText();
			fullText = fullText + text + "\n";

			String item = listDocuments.get(i).getId();

			String fileName = outDirTxt + File.separator + item + ".txt";
			Files.write(Paths.get(fileName), listDocuments.get(i).getText().getBytes());
			pmids.add(listDocuments.get(i).getId());
		}

		String filePMids = "Used Abstracts from following German PubMed Identifiers\n" + pmids.toString();

		Files.write(Paths.get("usedPubMedIds.txt"), filePMids.getBytes());
	}
}