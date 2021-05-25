package de.hpi.guidelines.reader;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "corpus")
public class GGPOncCollectedCorpus
{
	@XmlElement(name = "document", type = GGPOncDocument.class)
	private static List<GGPOncDocument> listDocuments = new ArrayList<GGPOncDocument>();

	public GGPOncCollectedCorpus(){}

	public GGPOncCollectedCorpus(List<GGPOncDocument> listDocuments) {
		GGPOncCollectedCorpus.listDocuments = listDocuments;
	}

	public List<GGPOncDocument> getListDocuments() {
		return listDocuments;
	}

	public static void setListDocuments(List<GGPOncDocument> listDocuments) {
		GGPOncCollectedCorpus.listDocuments = listDocuments;
	}

	public int getNumberDocuments() {
		return listDocuments.size();
	}

	public int getNumberRecommendations() {
		int n = 0;
		for (GGPOncDocument doc : listDocuments) {
			n += doc.getRecommendations().size();
		}
		return n;
	}
}
