package de.hpi.guidelines.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class GGPOncMetaData {
	
	static final String LITERATURE_REFERENCES = "literature_references";
	static final String RECOMMENDATION_GRADE = "recommendation_grade";
	static final String TYPE_OF_RECOMMENDATION = "type_of_recommendation";
	static final String STRENGTH_OF_CONSENSUS = "strength_of_consensus";
	static final String LEVEL_OF_EVIDENCES = "level_of_evidences";
	static final String EXPERT_OPINION = "expert_opinion";
	static final String VOTE = "vote";
	static final String RECOMMENDATION_CREATION_DATE = "recommendation_creation_date";
	static final String EDIT_STATE = "edit_state";
	static final String NUMBER = "number";
	static final String TEXT = "text";
	static final String REC = "recommendation";

	/**
	 * extracts all relevant meta data elements around a piece of text
	 * 
	 * @param elem
	 * @return meta data dictionary
	 */
	Map<String, String> getMetaData(Element elem) {
		Map<String, String> metadata = new HashMap<>();
		List<String> litrefs = new ArrayList<String>();
		
		NodeList text_litrefs = elem.getElementsByTagName("litref");
		for (int i = 0; i < text_litrefs.getLength(); i++) {
			litrefs.add(((Element) text_litrefs.item(i)).getAttribute("id"));
		}
		
		Element parentNode = (Element) elem.getParentNode();
		
		if (parentNode.getTagName().equals("section")) {
			metadata.put(REC, "False");
			return metadata; // top level background text
		}
		if (parentNode.getTagName().equals("recommendation"))
			metadata.put(REC, "True");
		else
			throw new RuntimeException(parentNode.getTagName());

		
		NodeList siblings = parentNode.getChildNodes();
		
		for (int i = 0; i < siblings.getLength(); i++) {
			Node sib = (Node) siblings.item(i);
			if (sib.getNodeType() == Node.TEXT_NODE)
				continue;
			if (sib.isSameNode(elem)) {
				continue; // skipping current text node
			}
			else {
				Element sibElem = (Element) sib;
				final String tagName = sibElem.getTagName().toString();
				switch (tagName) {
					case TEXT:
						if (parentNode.getTagName().equals("recommendation")) {
							throw new RuntimeException(sibElem.toString());
						}
					case NUMBER:
					case RECOMMENDATION_CREATION_DATE:
					case VOTE:
					case EXPERT_OPINION:
					case EDIT_STATE:
						metadata.put(tagName, sibElem.getAttribute("value"));
						break;
					case LEVEL_OF_EVIDENCES:
						metadata.put("loe", ((Element)sibElem.getElementsByTagName("loe").item(0)).getAttribute("id"));
						break;
					case STRENGTH_OF_CONSENSUS:
					case TYPE_OF_RECOMMENDATION:
					case RECOMMENDATION_GRADE:
						metadata.put(tagName, sibElem.getAttribute("id"));
						break;
					case LITERATURE_REFERENCES:
						NodeList refs = sibElem.getElementsByTagName("litref");
						for (int j = 0; j < refs.getLength(); j++) {
							litrefs.add(((Element)refs.item(j)).getAttribute("id"));
						}
						break;
					default:
						System.out.println(parentNode);
						throw new RuntimeException("Unknown metadata element " + tagName);
				}
			}
		}
		metadata.put("litref", String.join(";", litrefs));

		return metadata;
	}
	
	List<String> findStructure(Element elem) {
		List<String> structure = new ArrayList<>();
		Element current = elem;
		while (current.getParentNode() != null && current.getParentNode().getNodeType() != Node.DOCUMENT_NODE) {
			current = (Element) current.getParentNode();
			if (current.getTagName().equals("section") || current.getTagName().equals("document")) {
				structure.add(0, current.getElementsByTagName("name").item(0).getTextContent().trim());
				if (current.getTagName().equals("document")) {
					structure.add(0, current.getAttribute("id").trim());
				}
			}
		}
		return structure;
	}
	
	
}
