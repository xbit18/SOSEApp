package com.example.soseapp;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReadXmlDomParser {

    public static ArrayList<MatchWithWeather> parseMatchWithWeather(Document doc) {

        System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
        System.out.println("------");

        // get <staff>
        NodeList matchesList = doc.getElementsByTagName("MatchWithWeather");
        ArrayList<MatchWithWeather> matches = new ArrayList<>();
        //System.out.println(matchesList.item(0).getChildNodes().item(0).getTextContent());
        for (int temp = 0; temp < matchesList.getLength(); temp++) {

            Node node = matchesList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                String coordinates = element.getChildNodes().item(0).getTextContent();
                //System.out.println("Coordinates: " + coordinates);
                Element matchNode = (Element) element.getChildNodes().item(1);
                int localTeamID = Integer.parseInt(matchNode.getChildNodes().item(1).getChildNodes().item(0).getTextContent());
                String localTeamName = matchNode.getChildNodes().item(1).getChildNodes().item(1).getTextContent();
                int localTeamScore = Integer.parseInt(matchNode.getChildNodes().item(2).getChildNodes().item(0).getTextContent());
                int visitorTeamID = Integer.parseInt(matchNode.getChildNodes().item(3).getChildNodes().item(0).getTextContent());
                String visitorTeamName = matchNode.getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                int visitorTeamScore = Integer.parseInt(matchNode.getChildNodes().item(4).getChildNodes().item(0).getTextContent());
                Element weatherNode = (Element) element.getChildNodes().item(2);
                String city = weatherNode.getChildNodes().item(0).getTextContent();
                float temperature = Float.parseFloat(weatherNode.getChildNodes().item(1).getTextContent());
                String weather = weatherNode.getChildNodes().item(2).getTextContent();
                System.out.println(weather);
                Team localTeam = new Team(localTeamID, localTeamName);
                Team visitorsTeam = new Team(visitorTeamID, visitorTeamName);
                MatchWithWeather match = new MatchWithWeather(localTeam,visitorsTeam,localTeamScore,visitorTeamScore,coordinates,city,temperature,weather);
                matches.add(match);
            }
        }
        return matches;

    }
    public static ArrayList<MatchWithBet> parseMatchWithBet(Document doc) {

        System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
        System.out.println("------");

        // get <staff>
        NodeList matchesList = doc.getElementsByTagName("MatchWithBet");
        ArrayList<MatchWithBet> matches = new ArrayList<>();
        //System.out.println(matchesList.item(0).getChildNodes().item(0).getTextContent());
        for (int temp = 0; temp < matchesList.getLength(); temp++) {

            Node node = matchesList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;
                //System.out.println("Coordinates: " + coordinates);
                Element betNode = (Element) element.getChildNodes().item(0);
                double localTeamQuote = Double.parseDouble(betNode.getChildNodes().item(0).getTextContent());
                double visitorTeamQuote = Double.parseDouble(betNode.getChildNodes().item(2).getTextContent());
                double tieQuote = Double.parseDouble(betNode.getChildNodes().item(1).getTextContent());
                System.out.println(tieQuote);
                Element matchNode = (Element) element.getChildNodes().item(1);
                String coordinates = matchNode.getChildNodes().item(0).getTextContent();
                int localTeamID = Integer.parseInt(matchNode.getChildNodes().item(1).getChildNodes().item(0).getTextContent());
                String localTeamName = matchNode.getChildNodes().item(1).getChildNodes().item(1).getTextContent();
                int localTeamScore = Integer.parseInt(matchNode.getChildNodes().item(2).getChildNodes().item(0).getTextContent());
                int visitorTeamID = Integer.parseInt(matchNode.getChildNodes().item(3).getChildNodes().item(0).getTextContent());
                String visitorTeamName = matchNode.getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                int visitorTeamScore = Integer.parseInt(matchNode.getChildNodes().item(4).getChildNodes().item(0).getTextContent());
                Team localTeam = new Team(localTeamID, localTeamName);
                Team visitorsTeam = new Team(visitorTeamID, visitorTeamName);
                MatchWithBet match = new MatchWithBet(localTeam,visitorsTeam,localTeamScore,visitorTeamScore,localTeamQuote, visitorTeamQuote, tieQuote, coordinates);
                matches.add(match);
            }
        }
        return matches;

    }

}

