package meca.atlantique.hurco;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MTConnectApi {

    private static StringBuilder getXML(String ip, short port) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(String.format("http://%s:%s/current", ip, port));
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Lire la réponse
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private static String parseExprXml(String ip, short port, XPathExpression expr) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            
            StringBuilder xmlBuilder = getXML(ip, port);
            ByteArrayInputStream input = new ByteArrayInputStream(xmlBuilder.toString().getBytes("UTF-8"));
            Document xmldoc = docBuilder.parse(input);

            Node node = (Node) expr.evaluate(xmldoc, XPathConstants.NODE);

            return node.getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPrgStatus(String ip, short port) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile("//*[local-name()='ProgramStatus']");
            
            return parseExprXml(ip, port, expr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPrgName(String ip, short port) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile("//*[local-name()='Program']");
            
            return parseExprXml(ip, port, expr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
    