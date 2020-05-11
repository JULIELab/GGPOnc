package de.julielab.annotation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//import de.julielab.jsyncc.readbooks.TextDocument;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "doc")
public class TextAnnotation {
	public String id = "";

	@XmlElement(name = "sent", type = StandOffSentence.class)
	public List<StandOffSentence> sentencesAnnotation = new ArrayList<StandOffSentence>();

	@XmlElement(name = "tok", type = StandOffToken.class)
	public List<StandOffToken> tokenAnnotation = new ArrayList<StandOffToken>();

	@XmlElement(name = "pos", type = StandOffPos.class)
	public List<StandOffPos> posAnnotation = new ArrayList<StandOffPos>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<StandOffSentence> getSentencesAnnotation() {
		return sentencesAnnotation;
	}

	public List<StandOffToken> getTokenAnnotation() {
		return tokenAnnotation;
	}

	public void setTokenAnnotation(ArrayList<StandOffToken> tokenAnnotation) {
		this.tokenAnnotation = tokenAnnotation;
	}

	public void setSentencesAnnotation(ArrayList<StandOffSentence> sentencesAnnotation) {
		this.sentencesAnnotation = sentencesAnnotation;
	}

	public List<StandOffPos> getPosAnnotation() {
		return posAnnotation;
	}

	public void setPosAnnotation(ArrayList<StandOffPos> posAnnotation) {
		this.posAnnotation = posAnnotation;
	}
}
