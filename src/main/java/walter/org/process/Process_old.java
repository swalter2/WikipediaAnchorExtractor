
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
import java.util.stream.Collectors;
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
public class Process_old {
    public static void main(String[] args) throws IOException, ParserConfigurationException, UnsupportedEncodingException, SAXException{
        
        String path = args[0];
        
        Map<String,Integer> anchor_pair_counter = new HashMap<>();
        int number_article = 0;
        System.out.println("Intitialization done");
        for(Path x:  Files.list(Paths.get(path)).collect(Collectors.toList())){
            for(Path file_path : Files.list(Paths.get(x.toString())).filter(Files::isRegularFile).collect(Collectors.toList())){
                List<String> results = preprocessing(file_path);
                for(String r: results){
                     update_and_clean_Anchors(r, anchor_pair_counter);
                }
            }
        }
        System.out.println("Processed "+number_article+" articles");
//     	System.out.println(anchor_pair_counter.size());
        exportAnchorPairCounter(anchor_pair_counter);
        }

    private static void exportAnchorPairCounter(Map<String, Integer> anchor_pair_counter) throws IOException {
        List<String> lines = new ArrayList<>();
        anchor_pair_counter.keySet().stream().forEach((key) -> {
            lines.add(key.replace("##","\t")+"\t"+anchor_pair_counter.get(key));
        });
        Files.write(Paths.get("anchor_pair_counter.tsv"), lines);
    }

    private static List<String> preprocessing(Path filePath) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        String input = new String(Files.readAllBytes(filePath));
        Document doc = Jsoup.parse(input);
        Elements body2 = doc.getElementsByTag("doc");
        List <String> results = new ArrayList<>();
        for(Element x : body2){
            try{
//                String id = x.attr("id");
//                String url = x.attr("url");
//                String title = x.attr("title");
                String text = x.text();
		text = text.trim();
//                List <String> result = new ArrayList<>();
//                result.add(id);
//                result.add(title);
//                result.add(text);
                results.add(text);
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
        boolean found = false;

        while(matcher.find()) {
            String anchor_tmp = matcher.group(1);
            found = true;
            if(!anchor_tmp.contains("Category:") && !anchor_tmp.contains("File:")){
                String anchor = "";
                String verbalization = "";
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
        if(text.contains("Michelle Obama")){
            System.out.println(text);
        }
    }


        


        
        
}
