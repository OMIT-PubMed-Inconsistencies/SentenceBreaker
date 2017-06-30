import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;

public class SentenceBreaker {

    ArrayList<String> pubMedIds=new ArrayList<String>();

    public static void main(String[] args) {
        SentenceBreaker sb=new SentenceBreaker();
    }

    public SentenceBreaker() {
        readList();
        for(int i=0;i<pubMedIds.size();i++) {
            String pubMedid=pubMedIds.get(i);
            System.out.println((i+1)+" out of "+pubMedIds.size()+ " is "+  pubMedid);
            String lines=getSentences(pubMedid);
            writeToFile(pubMedid,lines);
        }
    }


    private void writeToFile(String pubMedid,String content){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("../output/03_SentenceBreaker/"+pubMedid+".txt", "UTF-8");
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private String getSentences(String fileName){


        try {
            File fXmlFile = new File("../output/02_Stanford/"+fileName+".txt.out");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

           // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList sListR = doc.getElementsByTagName("sentences");
            NodeList sList = sListR.item(0).getChildNodes();

            StringBuilder sb=new StringBuilder();

            for (int i = 0; i <sList.getLength(); i++) {
                Node n=sList.item(i);
                if(n.getNodeName().equalsIgnoreCase("sentence")){
                    NodeList nList=n.getChildNodes();
                    Node tokenNode=nList.item(1);
                    //Now we have the sentence with id i

                    //Each token is a word
                    NodeList tList =tokenNode.getChildNodes();



                    for (int j = 1; j <tList.getLength() ; j=j+2) {
                        Node t=tList.item(j); //This is the token
                        NodeList detailsList=t.getChildNodes();
                        String word=detailsList.item(1).getTextContent(); //This is the word
                        if(j==1){
                            sb.append(word); //First word of the sentence
                        }
                        else{
                            sb.append(" ");
                            sb.append(word);
                        }
                    }
                    sb.append("\n");

                }
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading "+fileName);
        }


        return "";


    }

    private void readList(){
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("../pubmed-list.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line=null;
            while((line = bufferedReader.readLine()) != null) {
                //  System.out.println(line);
                pubMedIds.add(line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
