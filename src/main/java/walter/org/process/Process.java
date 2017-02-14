
package walter.org.process;


import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;


/**
 *
 * @author swalter
 */
public class Process {
    public static void main(String[] args) throws IOException{
        
        String path = args[1];
        
        Map<String,Integer> anchor_pair_counter = new HashMap<>();
        
        //String path = "/home/swalter/development/wikipedia/extracted_english";
        System.out.println("Intitialization done");

        
        try(Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        List<List<String>> results = preprocessing(filePath);
                        results.stream().forEach((r) -> {
                            update_and_clean_Anchors(r.get(2), anchor_pair_counter);
                        });
                        
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } 
     	 
        exportAnchorPairCounter(anchor_pair_counter);
        }

    private static void exportAnchorPairCounter(Map<String, Integer> anchor_pair_counter) throws IOException {
        List<String> lines = new ArrayList<>();
        anchor_pair_counter.keySet().stream().forEach((key) -> {
            lines.add(key.replace("##","\t")+"\t"+anchor_pair_counter.get(key));
        });
        Files.write(Paths.get("anchor_pair_counter.tsv"), lines);
    }

    private static List<List<String>> preprocessing(Path filePath) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        String input = new String(Files.readAllBytes(filePath));
        Document doc = Jsoup.parse(input);
        Elements body2 = doc.getElementsByTag("doc");
        List <List<String>> results = new ArrayList<>();
        for(Element x : body2){
            try{
                String id = x.attr("id");
                String url = x.attr("url");
                String title = x.attr("title");
                String text = x.text();
                try{
			text = text.replaceFirst(title, "");
                } catch (Exception e){
		}
		text = text.trim();
                List <String> result = new ArrayList<>();
                result.add(id);
                result.add(title);
                result.add(text);
                results.add(result);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            
        }
        
        return results;
    }

    private static void update_and_clean_Anchors(String text, Map<String, Integer> anchor_pair_counter) {
        String patternString1 = "(\\[\\[.[^\\]]*\\]\\])";

        Pattern pattern = Pattern.compile(patternString1);
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            String anchor_tmp = matcher.group(1);
            if(!anchor_tmp.contains("Category:") && !anchor_tmp.contains("File:")){
                String anchor = "";
                String verbalization = "";
                String anchor_tmp_original = anchor_tmp;
                anchor_tmp = anchor_tmp.replace("[[","").replace("]]","");
                if(anchor_tmp.contains("|")){
                    String[] tmp = anchor_tmp.split("|");
                    anchor = tmp[0];
                    verbalization = tmp[1];
                }
                else{
                    anchor = anchor_tmp;
                    verbalization = anchor_tmp;
                }
                String anchor_verbalization = anchor+"##"+verbalization;
                if(anchor_pair_counter.containsKey(anchor_verbalization)){
                    anchor_pair_counter.put(anchor_verbalization, anchor_pair_counter.get(anchor_verbalization) +1);
                }
                else{
                    anchor_pair_counter.put(anchor_verbalization,1);
                }
                
            }
        }
    }


        


        
        
}
