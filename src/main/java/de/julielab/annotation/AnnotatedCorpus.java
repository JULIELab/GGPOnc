package de.julielab.annotation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "corpus")
public class AnnotatedCorpus {
	@XmlElement(name = "doc", type = TextAnnotation.class)
	private static List<TextAnnotation> listDocuments = new ArrayList<TextAnnotation>();

	public AnnotatedCorpus() {
	}

	public AnnotatedCorpus(List<TextAnnotation> listDocuments) {
		AnnotatedCorpus.listDocuments = listDocuments;
	}

	public List<TextAnnotation> getListDocuments() {
		return listDocuments;
	}

	public static void setListDocuments(List<TextAnnotation> listDocuments) {
		AnnotatedCorpus.listDocuments = listDocuments;
	}
}
