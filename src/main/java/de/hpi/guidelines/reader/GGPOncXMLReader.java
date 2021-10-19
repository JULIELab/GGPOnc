package de.hpi.guidelines.reader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hpsf.Array;
import org.apache.uima.UIMAException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.guidelines.tools.GGPOncJAXBXMLHandler;
import de.julielab.annotation.GetSentencesTokensFraMed;
import de.julielab.annotation.StandOffToken;
import de.julielab.annotation.TextAnnotation;
import de.julielab.tools.CleanText;

/**
 * Reads XML version of the cancer society guidelines and applies Java analysis pipelines
 * 
 * @author Florian.Borchert
 */
public class GGPOncXMLReader {

	public static final boolean RECOMMENDATIONS_ONLY = false;

	private static NodeList cpgDocs;

	private static ArrayList<TextAnnotation> rec_annotatedCorpus = new ArrayList<TextAnnotation>();
	private static ArrayList<TextAnnotation> annotatedCorpus = new ArrayList<TextAnnotation>();

	public static final Logger LOGGER = Logger.getLogger(GGPOncXMLReader.class.getName());

	private static final Path output = Paths.get("output-GGPOnc-data");
	private static final Path outDirXML = Paths.get(output + File.separator + "xml");
	private static final Path outDirTXT = Paths.get(output + File.separator + "txt");
	private static final Path outDirTXTfiles = Paths.get(output + File.separator + "txt" + File.separator + "files");
	private static final Path outDirTXTfilesAll = Paths.get(output + File.separator + "txt" + File.separator + "all_files");
	private static final Path outDirTXTfilesRec = Paths.get(output + File.separator + "txt" + File.separator + "files-rec");
	private static final Path outDirTXTfilesRecAll = Paths.get(output + File.separator + "txt" + File.separator + "all_rec_files");

	private static final Path outDirSENT = Paths.get(output + File.separator + "sentences");
	private static final Path outDirSENTfiles = Paths.get(output + File.separator + "sentences" + File.separator + "all_files_sentences");
	private static final Path outDirSENTfilesRec = Paths.get(output + File.separator + "sentences" + File.separator + "all_files_rec_sentences");

	private static final Path outDirTOK = Paths.get(output + File.separator + "tokens");
	private static final Path outDirTOKfiles = Paths.get(output + File.separator + "tokens" + File.separator + "all_files_tokens");
	private static final Path outDirTOKfilesRec = Paths.get(output + File.separator + "tokens" + File.separator + "all_files_rec_tokens");

	static class StdoutConsoleHandler extends ConsoleHandler {
		protected void setOutputStream(OutputStream out) throws SecurityException {
			super.setOutputStream(System.out);
		}
	}

	public static void main(String[] args) throws Exception {
		String importFile = args.length > 0 ? args[0] : Paths.get("src", "main", "resources", "cpg-corpus-cms.xml").toString();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File(importFile));

		doc.getDocumentElement().normalize();

		cpgDocs = doc.getElementsByTagName("document");

		writeTxtFilesAndAnnotations();

		LOGGER.info("# documents of CPG corpus: " + cpgDocs.getLength());
		LOGGER.info("Size of Annotated Corpus " + annotatedCorpus.size());
	}

	public static void writeTxtFilesAndAnnotations() throws IOException, UIMAException {
		checkOrCreateOutputDirs();

		StringBuilder removedHeadings = new StringBuilder();

		StringBuilder rec_fullText = new StringBuilder();
		StringBuilder rec_fullText_temp = new StringBuilder();
		StringBuilder rec_fullSent = new StringBuilder();
		StringBuilder rec_fullTokens = new StringBuilder();

		StringBuilder fullText = new StringBuilder();
		StringBuilder fullText_temp = new StringBuilder();
		StringBuilder fullSent = new StringBuilder();
		StringBuilder fullTokens = new StringBuilder();

		StringBuilder stats = new StringBuilder();
		
		StringBuilder metadata = new StringBuilder();
		
		metadata.append("file").append("\t").
				 append("guideline_id").append("\t").
				 append("name").append("\t").
				 append("sections").append("\t").
				 append(GGPOncMetaData.NUMBER).append("\t").
				 append(GGPOncMetaData.REC).append("\t").
				 append(GGPOncMetaData.RECOMMENDATION_CREATION_DATE).append("\t").
				 append(GGPOncMetaData.EDIT_STATE).append("\t").
				 append(GGPOncMetaData.RECOMMENDATION_GRADE).append("\t").
				 append(GGPOncMetaData.TYPE_OF_RECOMMENDATION).append("\t").
				 append(GGPOncMetaData.STRENGTH_OF_CONSENSUS).append("\t").
				 append(GGPOncMetaData.LEVEL_OF_EVIDENCES).append("\t").
				 append(GGPOncMetaData.EXPERT_OPINION).append("\t").
				 append(GGPOncMetaData.VOTE).append("\n");

		stats.append("name").append(";").
			append("num_docs").append(";").
			append("num_recommendations").append(";").
			append("num_rec_sentences").append(";").
			append("num_rec_tokens").append(";").
			append("num_rec_types").append(";").
			append("num_sentences").append(";").
			append("num_tokens").append(";").
			append("num_types").append(";").
			append("num_litref").append("\n");

		int n = cpgDocs.getLength();

		int[] num_recommendations = new int[n];
		int[] num_rec_sentences = new int[n];
		int[] num_rec_tokens = new int[n];
		int[] num_sentences = new int[n];
		int[] num_tokens = new int[n];
		int[] num_docs = new int[n];
		int[] num_litrefs = new int[n];
		SortedSet<String> all_types = new TreeSet<>();
		SortedSet<String> all_rec_types = new TreeSet<>();

		for (int i = 0; i < n; i++) {
			Element cpgDoc = (Element)cpgDocs.item(i);
			String name = cpgDoc.getElementsByTagName("name").item(0).getTextContent().trim();
			String id = name.replace(" ", "-").toLowerCase().replace("ä", "ae").
					replace("ö", "oe").
					replace("ü", "ue").
					replace("ß", "ss").
					replace("(", "").
					replace(")", "");
			LOGGER.info((i + 1) + File.separator + n + " - converting and annotating: " + name);

			NodeList recommendations = cpgDoc.getElementsByTagName("recommendation");

			num_litrefs[i] = cpgDoc.getElementsByTagName("litref").getLength();

			Pattern p = Pattern.compile("(###.*|.*###)"); // Removing headings

			SortedSet<String> rec_types = new TreeSet<>();
			SortedSet<String> types = new TreeSet<>();

			String rec_dirTXT = outDirTXTfilesRec + File.separator + String.format("%02d", i) + "_" + id + File.separator;
			String dirTXT = outDirTXTfiles + File.separator + String.format("%02d", i) + "_" + id + File.separator;

			checkOrCreateDir(Paths.get(rec_dirTXT));
			checkOrCreateDir(Paths.get(dirTXT));

			for (int j = 0; j < recommendations.getLength(); j++) {
				Element elem = (Element) recommendations.item(j);
				Node textNode = elem.getElementsByTagName("text").item(0);
				String text = textNode.getTextContent().trim();

				Matcher m = p.matcher(text);

				while (m.find())
					removedHeadings.append(text.substring(m.start(), m.end())).append("\n");

				text = m.replaceAll("");

				text = CleanText.reviseText(text);

				if (text.trim().isEmpty()) {
					continue;
				}

				Files.write(Paths.get(rec_dirTXT + getFileName(i, j, id)), text.getBytes("UTF-8"));
				Files.write(Paths.get(outDirTXTfilesRecAll.toString(), getFileName(i, j, id)), text.getBytes("UTF-8"));

				TextAnnotation textAnnotation = GetSentencesTokensFraMed.runPipeline(text, id + "_" + j);
				rec_annotatedCorpus.add(textAnnotation);
				String sent = GetSentencesTokensFraMed.getSentences();
				Files.write(Paths.get(outDirSENTfilesRec.toString(), getFileName(i, j, id)), sent.getBytes("UTF-8"));
				String token = GetSentencesTokensFraMed.getTokens();
				Files.write(Paths.get(outDirTOKfilesRec.toString(), getFileName(i, j, id)), token.getBytes("UTF-8"));

				num_rec_sentences[i] += textAnnotation.getSentencesAnnotation().size();
				num_rec_tokens[i] += textAnnotation.getTokenAnnotation().size();
				num_recommendations[i]++;

				for (StandOffToken soToken : textAnnotation.getTokenAnnotation())
					rec_types.addAll(Arrays.asList(soToken.getTokenString().replaceAll("[^äöüßÄÖÜ\\w\\d]", "")));

				rec_fullText.append(text).append("\n");
				rec_fullText_temp.append(text).append("\n");
				rec_fullSent.append(sent).append("\n");
				rec_fullTokens.append(token).append("\n");
			}

			NodeList all_text = cpgDoc.getElementsByTagName("text");

			for (int j = 0; j < all_text.getLength(); j++) {
				Element elem = (Element) all_text.item(j);
				String text = elem.getTextContent().trim();
				
				Matcher m = p.matcher(text);

				while (m.find())
					removedHeadings.append(text.substring(m.start(), m.end())).append("\n");

				text = m.replaceAll("");

				text = CleanText.reviseText(text);

				if (text.trim().isEmpty()) {
					continue;
				}

				String fileName = getFileName(i, j, id);
				
				GGPOncMetaData mdExtractor = new GGPOncMetaData();
				
				Map<String, String> metadataElements = mdExtractor.getMetaData(elem);
				List<String> structure = mdExtractor.findStructure(elem);
				String gl_id = structure.remove(0);
				
				metadata.append(fileName.replace(".txt", "")).append("\t").
					 append(gl_id).append("\t").
					 append(name).append("\t").
					 append(String.join(";", structure)).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.NUMBER, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.REC, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.RECOMMENDATION_CREATION_DATE, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.EDIT_STATE, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.RECOMMENDATION_GRADE, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.TYPE_OF_RECOMMENDATION, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.STRENGTH_OF_CONSENSUS, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.LEVEL_OF_EVIDENCES, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.EXPERT_OPINION, "")).append("\t").
					 append(metadataElements.getOrDefault(GGPOncMetaData.VOTE, "")).append("\n");
				
				Files.write(Paths.get(dirTXT + fileName), text.getBytes("UTF-8"));
				Files.write(Paths.get(outDirTXTfilesAll.toString(), fileName), text.getBytes("UTF-8"));

				TextAnnotation textAnnotation = GetSentencesTokensFraMed.runPipeline(text, id + "_" + j);
				annotatedCorpus.add(textAnnotation);
				String sent = GetSentencesTokensFraMed.getSentences();
				Files.write(Paths.get(outDirSENTfiles.toString(), fileName), sent.getBytes("UTF-8"));
				String token = GetSentencesTokensFraMed.getTokens();
				Files.write(Paths.get(outDirTOKfiles.toString(), fileName), token.getBytes("UTF-8"));

				num_sentences[i] += textAnnotation.getSentencesAnnotation().size();
				num_tokens[i] += textAnnotation.getTokenAnnotation().size();
				num_docs[i]++;

				for (StandOffToken soToken : textAnnotation.getTokenAnnotation())
					types.addAll(Arrays.asList(soToken.getTokenString().replaceAll("[^äöüßÄÖÜ\\w\\d]", "")));

				fullText.append(text).append("\n");
				fullText_temp.append(text).append("\n");
				fullSent.append(sent).append("\n");
				fullTokens.append(token).append("\n");
			}

			all_types.addAll(types);
			all_rec_types.addAll(rec_types);

			stats.append(name).append(";").
				append(num_docs[i]).append(";").
				append(num_recommendations[i]).append(";").
				append(num_rec_sentences[i]).append(";").
				append(num_rec_tokens[i]).append(";").
				append(rec_types.size()).append(";").
				append(num_sentences[i]).append(";").
				append(num_tokens[i]).append(";").
				append(types.size()).append(";").
				append(num_litrefs[i]).append("\n");

			writeTextToFile(new StringBuilder(String.join("\n", rec_fullText_temp)), String.format("%02d", i) + "_" + id + "_rec.txt");
			writeTextToFile(new StringBuilder(String.join("\n", fullText_temp)), String.format("%02d", i) + "_" + id + ".txt");

			rec_fullText_temp.delete(0, rec_fullText_temp.length());
			fullText_temp.delete(0, fullText_temp.length());
		}

		stats.append("Sum").append(";").
			append(annotatedCorpus.size()).append(";").
			append(sum(num_recommendations)).append(";").
			append(sum(num_rec_sentences)).append(";").
			append(sum(num_rec_tokens)).append(";").
			append(all_rec_types.size()).append(";").
			append(sum(num_sentences)).append(";").
			append(sum(num_tokens)).append(";").
			append(all_types.size()).append(";").
			append(sum(num_litrefs)).append("\n");

		writeTextToFile(new StringBuilder(String.join("\n", all_rec_types)), "cpg-rec-types.txt");
		writeTextToFile(new StringBuilder(String.join("\n", all_types)), "cpg-types.txt");

		writeTextToFile(removedHeadings, "removed-headings.txt");
		
		Files.write(output.resolve("metadata_index.tsv"), metadata.toString().getBytes("UTF-8"));		

		writeTextToFile(stats, "cpg-stats.csv");
		writeTextToFile(fullText, "cpg-text.txt");
		writeTextToFile(fullSent, "cpg-sentences.txt");
		writeTextToFile(fullTokens, "cpg-tokens.txt");

		writeTextToFile(rec_fullText, "cpg-rec-text.txt");
		writeTextToFile(rec_fullSent, "cpg-rec-sentences.txt");
		writeTextToFile(rec_fullTokens, "cpg-rec-tokens.txt");

		writeAnno("cpg-rec-annotations.xml", rec_annotatedCorpus);
		writeAnno("cpg-annotations.xml", annotatedCorpus);
	}
	
	private static int sum(int[] values) {
		int sum = 0;
		for (int i = 0; i < values.length; i++)
			sum += values[i];
		return sum;
	}

	private static String getFileName(int i, int j, String id) {
		String fileName = String.format( "%02d_%s_%04d.txt", i, id, j).toLowerCase();
		return fileName;
	}

	private static void writeTextToFile(StringBuilder sb, String fileName) throws IOException {
		Files.write(Paths.get(outDirTXT + File.separator + fileName), sb.toString().getBytes("UTF-8"));
		LOGGER.info(outDirTXT + File.separator + fileName + " created successfully.");
	}

	private static void writeAnno(String annoFile, List<TextAnnotation> annotatedCorpus) throws IOException {
		try {
			GGPOncJAXBXMLHandler.marshalAnnotation(annotatedCorpus, new File(outDirXML + File.separator + annoFile));
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

		LOGGER.info(outDirXML + File.separator + annoFile + " created successfully.");

		ProcessBuilder pb = new ProcessBuilder(
				"tar", "czvf",
				outDirXML + File.separator + annoFile + ".tar.gz",
				outDirXML + File.separator + annoFile);

		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void checkOrCreateOutputDirs() throws IOException{
		checkOrCreateDir(output);
		checkOrCreateDir(outDirXML);
		checkOrCreateDir(outDirTXT);
		checkOrCreateDir(outDirTXTfiles);
		checkOrCreateDir(outDirTXTfilesRec);
		checkOrCreateDir(outDirTXTfilesAll);
		checkOrCreateDir(outDirTXTfilesRecAll);
		checkOrCreateDir(outDirSENT);
		checkOrCreateDir(outDirSENTfiles);
		checkOrCreateDir(outDirSENTfilesRec);
		checkOrCreateDir(outDirTOK);
		checkOrCreateDir(outDirTOKfiles);
		checkOrCreateDir(outDirTOKfilesRec);
	}

	private static void checkOrCreateDir(Path dir) throws IOException{
		if (Files.notExists(dir)) {
			Files.createDirectories(dir);
		}
	}
}
