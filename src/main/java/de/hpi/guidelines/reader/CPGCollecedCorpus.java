package de.hpi.guidelines.reader;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "corpus")
public class CPGCollecedCorpus
{
	@XmlElement(name = "document", type = CPGDocument.class)
	private static List<CPGDocument> listDocuments = new ArrayList<CPGDocument>();

	public CPGCollecedCorpus(){}

	public CPGCollecedCorpus(List<CPGDocument> listDocuments) {
		CPGCollecedCorpus.listDocuments = listDocuments;
	}

	public List<CPGDocument> getListDocuments() {
		return listDocuments;
	}

	public static void setListDocuments(List<CPGDocument> listDocuments) {
		CPGCollecedCorpus.listDocuments = listDocuments;
	}

	public int getNumberDocuments() {
		return listDocuments.size();
	}

	public int getNumberRecommendations() {
		int n = 0;
		for (CPGDocument doc : listDocuments) {
			n += doc.getRecommendations().size();
		}
		return n;
	}
}
