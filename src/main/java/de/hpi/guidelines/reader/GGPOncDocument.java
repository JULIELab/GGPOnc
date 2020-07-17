package de.hpi.guidelines.reader;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "document")
public class GGPOncDocument {

	private String name;
	
	private Integer year;
	
	private List<CPGRecommendation> recommendations;
	
	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (CPGRecommendation r : recommendations) {
			sb.append(r.toString());
		}
		return sb.toString();
	}
	
	public void setRecommendations(List<CPGRecommendation> recommendations) {
		this.recommendations = recommendations;
	}
	
	@XmlElement(name = "recommendation", type = CPGRecommendation.class)
	public List<CPGRecommendation> getRecommendations() {
		return recommendations;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = Integer.valueOf(year);
	}

	@XmlAccessorType(XmlAccessType.PROPERTY)
	@XmlRootElement(name = "recommendation")
	public static class CPGRecommendation {
		
		public String number;
		
		public String text = "";
		
		public String type;
		
		public String recommendationLevel;
	
		public List<String> literature = new ArrayList<>();
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getRecommendationLevel() {
			return recommendationLevel;
		}
		
		public void setRecommendationLevel(String recommendationLevel) {
			this.recommendationLevel = recommendationLevel;
		}
		
		public String getNumber() {
			return number;
		}
		
		public void setNumber(String number) {
			this.number = number;
		}
		
//		public List<String> getLiterature() {
//			return literature;
//		}
//		
//		public void setLiterature(List<String> literature) {
//			this.literature = literature;
//		}
		
		@Override
		public String toString() {
			return  number + ": " + type + (recommendationLevel != null ? " (" + recommendationLevel + ") " : "") + text; 
		}
		
	}

	public String getIdLong() {
		// TODO Auto-generated method stub
		return null;
	}

}
