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

import de.hpi.guidelines.reader.CPGCollecedCorpus;
import de.hpi.guidelines.reader.CPGDocument;
import de.julielab.annotation.AnnotatedCorpus;
import de.julielab.annotation.TextAnnotation;

public class CPGJAXBXMLHandler {
	private static Logger logger = Logger.getAnonymousLogger();

	// export XML - corpus
	public static void marshalCorpus(List<CPGDocument> listOfDocuments, File outputFile)
			throws IOException, JAXBException {
		JAXBContext context;
		context = JAXBContext.newInstance(CPGCollecedCorpus.class);

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputFile));

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		CPGCollecedCorpus corpus = new CPGCollecedCorpus(listOfDocuments);
		m.marshal(corpus, writer);
		logger.info("Written corpus with " + corpus.getNumberDocuments() + " documents and "
				+ corpus.getNumberRecommendations() + " elements");
		writer.close();
	}

	// import XML - corpus
	public static List<CPGDocument> unmarshalCorpus(File importFile) throws JAXBException {
		CPGCollecedCorpus col = new CPGCollecedCorpus();

		JAXBContext context = JAXBContext.newInstance(CPGCollecedCorpus.class);
		Unmarshaller um = context.createUnmarshaller();
		col = (CPGCollecedCorpus) um.unmarshal(importFile);

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