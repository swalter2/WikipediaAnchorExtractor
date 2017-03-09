
package walter.org.process;


import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.apache.commons.lang.WordUtils;
import org.xml.sax.SAXException;


/**
 *
 * @author swalter
 */
public class Process_new {
    public static void main(String[] args) throws IOException, ParserConfigurationException, UnsupportedEncodingException, SAXException{
        
        String path = args[0];
        
        Map<String,Integer> anchor_pair_counter = new HashMap<>();
        for(Path x:  Files.list(Paths.get(path)).collect(Collectors.toList())){
            for(Path file_path : Files.list(Paths.get(x.toString())).filter(Files::isRegularFile).collect(Collectors.toList())){
               preprocessing(file_path,anchor_pair_counter);
            }
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

    private static void preprocessing(Path filePath,Map<String, Integer> anchor_pair_counter) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        String input = new String(Files.readAllBytes(filePath));
        update_and_clean_Anchors(input,anchor_pair_counter);
    }

    private static void update_and_clean_Anchors(String text, Map<String, Integer> anchor_pair_counter) {
        String patternString1 = "(\\<a href.[^\\<]*\\<\\/a\\>)";

        Pattern pattern = Pattern.compile(patternString1);
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            String anchor_tmp = matcher.group(1);
            //<a href="Ferdinand%20de%20Saussure">Ferdinand de Saussure</a>
            anchor_tmp = anchor_tmp.replace("<a href=\"","");
            anchor_tmp = anchor_tmp.replace("</a>","");
            if(!anchor_tmp.contains("Category:") && !anchor_tmp.contains("File:") 
                    && !anchor_tmp.contains("http%3A//")
                    && !anchor_tmp.contains("https%3A//")){
                String anchor = "";
                String verbalization = "";
                try{
                    String[] tmp = anchor_tmp.split("\">");
                    if(tmp.length==1){
                        anchor_tmp = anchor_tmp.replace("\">", "");
                        verbalization =URLDecoder.decode(anchor, "UTF-8");
                        anchor = verbalization.replace(" ", "_");
                    }
                    else{
                        anchor = tmp[0];
                        anchor =URLDecoder.decode(anchor, "UTF-8");
                        anchor = anchor.replace(" ", "_");
                        verbalization = tmp[1];
                    }
                    
                    anchor = "http://dbpedia.org/resource/"+WordUtils.capitalize(anchor); 
                    String anchor_verbalization = anchor+"##"+verbalization;
                    if(anchor_pair_counter.containsKey(anchor_verbalization)){
                        anchor_pair_counter.put(anchor_verbalization, anchor_pair_counter.get(anchor_verbalization) +1);
                    }
                    else{
                        anchor_pair_counter.put(anchor_verbalization,1);
                    }
                }
                catch(Exception e){
                    System.out.println("Error in:"+anchor_tmp);
                }
            }
        }
    }      
}
