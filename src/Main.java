import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.StandardCopyOption.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {
    public static class SAXLocalNameCount extends DefaultHandler {

        private Hashtable tags;

    }


    public static void main(String[] args) throws IOException {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new SAXLocalNameCount());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();

            byte[] buffer = new byte[1024];
          //  StringBuffer buffer = new StringBuffer("");

            String currentDir = System.getProperty("user.dir");
            //System.out.println(currentDir);

            String FilePath = currentDir + "\\" + "files\\Myfile2.zip";
            String outputFolder = currentDir + "\\" + "files\\";
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(FilePath));
            System.out.println(outputFolder);
            ZipEntry zi = zipInputStream.getNextEntry();
            String fileName = zi.getName();
            System.out.println(fileName);
            while (zipInputStream.getNextEntry()!= null) {
                zi = zipInputStream.getNextEntry();
                fileName = zi.getName();
                if (fileName.contains("document.xml")) {
                    File newFile = new File(outputFolder + File.separator + fileName);

                    System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                    new File(newFile.getParent()).mkdirs();

                    FileOutputStream fos = new FileOutputStream(newFile);

                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                    StringBuffer sbuffer = new StringBuffer("");
                    FileInputStream fin = new FileInputStream(newFile);
                    int len;
                    int ch;
                    while ((ch = fin.read()) != -1) {
                        sbuffer.append((char) ch);
                    }
                    Document doc = builder.parse(new InputSource(new StringReader(sbuffer.toString())));
                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                    if (doc.hasChildNodes()) {

                        printNote(doc.getChildNodes());

                    }
                }
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void printNote(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempNode = nodeList.item(count);

            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value =" + tempNode.getTextContent());

                if (tempNode.hasAttributes()) {

                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();

                    for (int i = 0; i < nodeMap.getLength(); i++) {

                        Node node = nodeMap.item(i);
                       System.out.println("attr name : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());

                    }

                }

                if (tempNode.hasChildNodes()) {

                    // loop again if has child nodes
                    printNote(tempNode.getChildNodes());

                }

                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

            }

        }
    }
}