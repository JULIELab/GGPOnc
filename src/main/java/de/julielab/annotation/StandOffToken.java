package de.julielab.annotation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tok")
public class StandOffToken {
	public String start = "";
	public String end = "";
	@XmlTransient
	public String tokenString = null;
	
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
	
	public void setTokenString(String token) {
		this.tokenString = token;
	}
		
	public String getTokenString() {
		return tokenString;
	}
}
