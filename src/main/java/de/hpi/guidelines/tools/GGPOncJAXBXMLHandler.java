package de.hpi.guidelines.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.hpi.guidelines.reader.GGPOncCollecedCorpus;
import de.hpi.guidelines.reader.GGPOncDocument;
import de.julielab.annotation.AnnotatedCorpus;
import de.julielab.annotation.TextAnnotation;

public class GGPOncJAXBXMLHandler {
	private static Logger logger = Logger.getAnonymousLogger();

	// export XML - corpus
	public static void marshalCorpus(List<GGPOncDocument> listOfDocuments, File outputFile)
			throws IOException, JAXBException {
		JAXBContext context;
		context = JAXBContext.newInstance(GGPOncCollecedCorpus.class);

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputFile));

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		GGPOncCollecedCorpus corpus = new GGPOncCollecedCorpus(listOfDocuments);
		m.marshal(corpus, writer);
		logger.info("Written corpus with " + corpus.getNumberDocuments() + " documents and "
				+ corpus.getNumberRecommendations() + " elements");
		writer.close();
	}

	// import XML - corpus
	public static List<GGPOncDocument> unmarshalCorpus(File importFile) throws JAXBException {
		GGPOncCollecedCorpus col = new GGPOncCollecedCorpus();

		JAXBContext context = JAXBContext.newInstance(GGPOncCollecedCorpus.class);
		Unmarshaller um = context.createUnmarshaller();
		col = (GGPOncCollecedCorpus) um.unmarshal(importFile);

		return col.getListDocuments();
	}

	// export XML - annotation
	// export XML - corpus
	public static void marshalAnnotation(List<TextAnnotation> listOfAnnotations, File outputFile)
			throws IOException, JAXBException {
		JAXBContext context;
		context = JAXBContext.newInstance(AnnotatedCorpus.class);

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputFile));

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(new AnnotatedCorpus(listOfAnnotations), writer);
		writer.close();
	}
}