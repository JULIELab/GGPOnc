package de.julielab.annotation;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import de.julielab.jcore.types.Annotation;
import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;

public class GetSentencesTokensFraMed {
	private static int index = 1;

	private static String sentences = "";
	private static String tokens = "";

	private static AnalysisEngine sentenceAE;
	private static AnalysisEngine tokenAE;
	private static AnalysisEngine posAE;
	
	private static JCas jCas;

	static {
		try {
			sentenceAE = AnalysisEngineFactory.createEngine("de.julielab.jcore.ae.jsbd.desc.jcore-jsbd-ae-medical-german");
			tokenAE = AnalysisEngineFactory.createEngine("de.julielab.jcore.ae.jtbd.desc.jcore-jtbd-ae-medical-german");
			posAE = AnalysisEngineFactory.createEngine("de.julielab.jcore.ae.jpos.desc.jcore-jpos-ae-medical-german");
			jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types");
		} catch (IOException | UIMAException e) {
			throw new RuntimeException(e);
		}
	}

	public static TextAnnotation runPipeline(String inputText, String longId) throws IOException, UIMAException {
		jCas.reset();
		jCas.setDocumentText(inputText);

		sentenceAE.process(jCas);
		tokenAE.process(jCas);
		posAE.process(jCas);

		sentences = "";
		tokens = "";

		sentences = getTokSent(jCas, Sentence.type);
		tokens = getTokSent(jCas, Token.type);

		ArrayList<StandOffSentence> annoSentences = getSentAnno(jCas, Sentence.type);
		ArrayList<StandOffToken> annoTokens = getTokAnno(jCas, Token.type);
		ArrayList<StandOffPos> annoPos = getPOSAnno(jCas, POSTag.type);

		TextAnnotation tAnno = new TextAnnotation();

		tAnno.sentencesAnnotation = annoSentences;
		tAnno.tokenAnnotation = annoTokens;
		tAnno.posAnnotation = annoPos;
		tAnno.id = Integer.toString(index);
		index++;

		return tAnno;
	}

	public static ArrayList<StandOffPos> getPOSAnno(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		ArrayList<StandOffPos> posAnno = new ArrayList<StandOffPos>();

		while (elements.hasNext()) {
			POSTag p = (POSTag) elements.next();

			StandOffPos pAnno = new StandOffPos();

			pAnno.start = Integer.toString(p.getBegin());
			pAnno.end = Integer.toString(p.getEnd());
			pAnno.posTag = p.getValue();
			posAnno.add(pAnno);
		}
		return posAnno;
	}

	public static String getTokSent(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		String e = "";
		while (elements.hasNext()) {
			Annotation s = (Annotation) elements.next();
			e = e + "\n" + s.getCoveredText();
		}
		e = e.replaceFirst("\n", "");
		return e;
	}

	public static ArrayList<StandOffToken> getTokAnno(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		ArrayList<StandOffToken> tokAnno = new ArrayList<StandOffToken>();

		while (elements.hasNext()) {
			Annotation s = (Annotation) elements.next();
			StandOffToken tAnno = new StandOffToken();

			tAnno.start = Integer.toString(s.getBegin());
			tAnno.end = Integer.toString(s.getEnd());
			tAnno.tokenString = s.getCoveredText();
			tokAnno.add(tAnno);
		}
		return tokAnno;
	}

	public static ArrayList<StandOffSentence> getSentAnno(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		ArrayList<StandOffSentence> sentAnno = new ArrayList<StandOffSentence>();

		while (elements.hasNext()) {
			Annotation s = (Annotation) elements.next();
			StandOffSentence sAnno = new StandOffSentence();

			sAnno.start = Integer.toString(s.getBegin());
			sAnno.end = Integer.toString(s.getEnd());
			sentAnno.add(sAnno);
		}
		return sentAnno;
	}

	public static String getSentences(){
		return sentences;
	}
	public static void setSentences(String sentences){
		GetSentencesTokensFraMed.sentences = sentences;
	}

	public static String getTokens(){
		return tokens;
	}
	public static void setTokens(String tokens){
		GetSentencesTokensFraMed.tokens = tokens;
	}
}
