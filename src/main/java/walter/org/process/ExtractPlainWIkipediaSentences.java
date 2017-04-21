package walter.org.process;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.io.*;
import java.net.URLDecoder;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ExtractPlainWIkipediaSentences {

    public static void main(String[] args) throws Exception {

	List<String> testfiles = new ArrayList<>();
	try (Stream<Path> paths = Files.walk(Paths.get("/Users/swalter/Documents/extracted"))) {
	    paths.forEach(filePath -> {
		if (filePath.toString().contains("wiki")) {
		    testfiles.add(filePath.toString());
		}
	    });
	}

	for (String s : testfiles) {
	    String content = new String(Files.readAllBytes(Paths.get(s)));
	    List<String> lines = extract_sentences(clean_text(content));
	    Files.write(Paths.get("plain_wikipedia_sentences.txt"), lines, UTF_8, APPEND, CREATE);

	}

    }

    private static String clean_text(String text_input) {
	String patternString1 = "(\\<a href.[^\\<]*\\<\\/a\\>)";

	Pattern pattern = Pattern.compile(patternString1);
	Matcher matcher = pattern.matcher(text_input);

	while (matcher.find()) {
	    String anchor_tmp = matcher.group(1);
	    // <a href="Ferdinand%20de%20Saussure">Ferdinand de Saussure</a>
	    anchor_tmp = anchor_tmp.replace("<a href=\"", "");
	    anchor_tmp = anchor_tmp.replace("</a>", "");
	    String anchor = "";
	    String verbalization = "";
	    try {
		String[] tmp = anchor_tmp.split("\">");
		if (tmp.length == 1) {
		    anchor_tmp = anchor_tmp.replace("\">", "");
		    verbalization = URLDecoder.decode(anchor, "UTF-8");
		    anchor = verbalization.replace(" ", "_");
		} else {
		    anchor = tmp[0];
		    verbalization = tmp[1];
		}

		String replacementstring = "<a href=\"" + anchor + "\">" + verbalization + "</a>";
		text_input = text_input.replace(replacementstring, verbalization);

	    } catch (Exception e) {
		System.out.println("Error in:" + anchor_tmp);
	    }

	}
	return text_input;
    }

    private static List<String> extract_sentences(String clean_text) {
	Reader reader = new StringReader(clean_text);
	DocumentPreprocessor dp = new DocumentPreprocessor(reader);
	List<String> sentenceList = new ArrayList<String>();

	for (List<HasWord> sentence : dp) {
	    // SentenceUtils not Sentence
	    if (sentence.size() > 8 && sentence.size() < 30) {
		String sentenceString = SentenceUtils.listToString(sentence);
		if (!sentenceString.contains("-LRB-") && !sentenceString.contains("-RRB-") && !sentenceString.contains("<doc")
			&& !sentenceString.contains("</doc>"))
		    sentenceList.add(sentenceString);
	    }
	}

	// for (String sentence : sentenceList) {
	// System.out.println(sentence);
	// }
	return sentenceList;
    }

}
