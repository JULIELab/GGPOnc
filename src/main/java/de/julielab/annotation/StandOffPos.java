package de.julielab.annotation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "pos")
public class StandOffPos {
	public String start = "";
	public String end = "";
	public String posTag = "";

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getPosTag() {
		return posTag;
	}

	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}
}
