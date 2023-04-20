import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Scanner;
import java.lang.System;
import java.lang.String;

public class FileManipulator{
    public static void checkFile(String file) throws NotValidAutoSarFileException{
        String[] fileStrings = file.split("\\.");   
        if(fileStrings[1].equals("arxml")){
            System.out.println("File read successfully.");
            return;
        }else{
            throw new NotValidAutoSarFileException("Please Enter a valid arxml file.");
        }    
    }
    public static void checkFileLength(File file) throws EmptyAutoSarFileException{
        if(file.length() > 2){

        }else{
            throw new EmptyAutoSarFileException("File is empty.");
        }
    }
    public static void main(String[] args){
        //Declaration of some vriables.
        String[] uuid;
        String[] shortName;
        String[] longName;

        try{
            checkFile(args[0]);
        }catch(NotValidAutoSarFileException e){
            System.out.println(e);
            System.exit(0);
        }
        try{
            File file = new File(args[0]);
            try{
                checkFileLength(file);
            }catch(EmptyAutoSarFileException e){
                System.out.println(e);
                System.exit(0);
            }
            // Building doc
            DocumentBuilderFactory docbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docbfac.newDocumentBuilder();
            Document document = docBuilder.parse(file);
            document.getDocumentElement().normalize();

            System.out.println("Root Element:" + document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getElementsByTagName("CONTAINER");
            int n = nodeList.getLength();
            for(int i=0;i<n;i++){       
                Node node = nodeList.item(i);
                System.out.println("node:" + node.getNodeName());
                Element e = (Element)node;
                System.out.println("nodeShortName:" + e.getElementsByTagName("SHORT-NAME").item(0).getTextContent());
                System.out.println("nodeLongName:" + e.getElementsByTagName("LONG-NAME").item(0).getTextContent());
                System.out.println("ID:" + e.getAttribute("UUID"));
            }

            Node[] nodeArr = new Node[n];

            Element[] el = new Element[n];

            for(int i=0;i<n;i++){
                nodeArr[i] = nodeList.item(i);
                //Node node = nodeList.item(i);
                el[i] = (Element)nodeArr[i];
            }

            for(int i=0;i<n;i++){
                Node minNode = nodeArr[i];
                for(int j=i+1;j<n;j++){
                    int r = (el[j].getElementsByTagName("SHORT-NAME").item(0).getTextContent()).compareTo(el[i].getElementsByTagName("SHORT-NAME").item(0).getTextContent());
                    if(r < 0){
                        nodeArr[i] = nodeList.item(j);
                        nodeArr[j] = minNode;
                    }
                }
            }

            System.out.println();

            // Arranging the containers.
            uuid = new String[n];
            shortName = new String[n];
            longName = new String[n];
            for(int i=0;i<nodeList.getLength();i++){
                Element newEl = (Element)nodeArr[i];
                uuid[i] = newEl.getAttribute("UUID");
                shortName[i] = newEl.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                longName[i] = newEl.getElementsByTagName("LONG-NAME").item(0).getTextContent();
                System.out.println(newEl.getElementsByTagName("SHORT-NAME").item(0).getTextContent());
            }
            //Containers are Arranged!
            
            // Creating the new document.
            try{
                Document newDoc = docBuilder.newDocument();

                Element root = newDoc.createElement("AUTOSAR");
                newDoc.appendChild(root);

                for(int i=0;i<n;i++){
                    Element container = newDoc.createElement("CONTAINER");
                    root.appendChild(container);

                    Attr id = newDoc.createAttribute("UUID");
                    id.setValue(uuid[i]);
                    container.setAttributeNode(id);

                    Element newShortName = newDoc.createElement("SHORT-NAME");
                    container.appendChild(newShortName);
                    newShortName.appendChild(newDoc.createTextNode(shortName[i]));

                    Element newLongName = newDoc.createElement("LONG-NAME");
                    container.appendChild(newLongName);
                    newLongName.appendChild(newDoc.createTextNode(longName[i]));
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(newDoc);
                try{
                    StreamResult streamResult = new StreamResult(new File("C:\\Education\\CSE-Spring 23\\Advanced Programming\\Assignments\\ExceptionHandling\\Exception-Handling-\\" + (args[0].split("\\."))[0] + "_mod.arxml"));
                    transformer.transform(domSource,streamResult);
                }catch(Exception err){
                    System.out.println(err + "DOM error");
                }
            }catch(Exception e){
                System.out.println(e + "dom err");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
}