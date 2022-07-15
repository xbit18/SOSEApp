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

    /**
     * Parses XML response and return array list of MatchWithWeather objects
     */
    public static ArrayList<MatchWithWeather> parseMatchWithWeather(Document doc) {
        /** Get node list of MatchWithWeather **/
        NodeList matchesList = doc.getElementsByTagName("MatchWithWeather");
        ArrayList<MatchWithWeather> matches = new ArrayList<>();
        for (int temp = 0; temp < matchesList.getLength(); temp++) {

            Node node = matchesList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                /** Get coordinates **/
                String coordinates = element.getChildNodes().item(0).getTextContent();

                /** Get match details **/
                Element matchNode = (Element) element.getChildNodes().item(1);
                    int localTeamID = Integer.parseInt(matchNode.getChildNodes().item(1).getChildNodes().item(0).getTextContent());
                    String localTeamName = matchNode.getChildNodes().item(1).getChildNodes().item(1).getTextContent();
                    int localTeamScore = Integer.parseInt(matchNode.getChildNodes().item(2).getChildNodes().item(0).getTextContent());
                    int visitorTeamID = Integer.parseInt(matchNode.getChildNodes().item(3).getChildNodes().item(0).getTextContent());
                    String visitorTeamName = matchNode.getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                    int visitorTeamScore = Integer.parseInt(matchNode.getChildNodes().item(4).getChildNodes().item(0).getTextContent());
                /** Get weather details **/
                Element weatherNode = (Element) element.getChildNodes().item(2);
                    String city = weatherNode.getChildNodes().item(1).getTextContent();
                    float temperature = Float.parseFloat(weatherNode.getChildNodes().item(2).getTextContent());
                    String weather = weatherNode.getChildNodes().item(0).getTextContent();

                /** Create match object and add to array **/
                Team localTeam = new Team(localTeamID, localTeamName);
                Team visitorsTeam = new Team(visitorTeamID, visitorTeamName);
                MatchWithWeather match = new MatchWithWeather(localTeam,visitorsTeam,localTeamScore,visitorTeamScore,coordinates,city,temperature,weather);
                matches.add(match);
            }
        }
        return matches;

    }

    /**
     * Parses XML response and return array list of MatchWithBet objects
     */
    public static ArrayList<MatchWithBet> parseMatchWithBet(Document doc) {
        /** Get node list of MatchWithBet **/
        NodeList matchesList = doc.getElementsByTagName("MatchWithBet");
        ArrayList<MatchWithBet> matches = new ArrayList<>();
        for (int temp = 0; temp < matchesList.getLength(); temp++) {

            Node node = matchesList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;
                /** Get bet details **/
                Element betNode = (Element) element.getChildNodes().item(0);
                    double localTeamQuote = Double.parseDouble(betNode.getChildNodes().item(0).getTextContent());
                    double visitorTeamQuote = Double.parseDouble(betNode.getChildNodes().item(2).getTextContent());
                    double tieQuote = Double.parseDouble(betNode.getChildNodes().item(1).getTextContent());

                /** Get match details **/
                Element matchNode = (Element) element.getChildNodes().item(1);
                    String coordinates = matchNode.getChildNodes().item(0).getTextContent();
                    int localTeamID = Integer.parseInt(matchNode.getChildNodes().item(1).getChildNodes().item(0).getTextContent());
                    String localTeamName = matchNode.getChildNodes().item(1).getChildNodes().item(1).getTextContent();
                    int localTeamScore = Integer.parseInt(matchNode.getChildNodes().item(2).getChildNodes().item(0).getTextContent());
                    int visitorTeamID = Integer.parseInt(matchNode.getChildNodes().item(3).getChildNodes().item(0).getTextContent());
                    String visitorTeamName = matchNode.getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                    int visitorTeamScore = Integer.parseInt(matchNode.getChildNodes().item(4).getChildNodes().item(0).getTextContent());

                /** Create match object and add to array **/
                Team localTeam = new Team(localTeamID, localTeamName);
                Team visitorsTeam = new Team(visitorTeamID, visitorTeamName);
                MatchWithBet match = new MatchWithBet(localTeam,visitorsTeam,localTeamScore,visitorTeamScore,localTeamQuote, visitorTeamQuote, tieQuote, coordinates);
                matches.add(match);
            }
        }
        return matches;

    }

    /**
     * Parses XML response and return array list of CompleteMatch objects
     */
    public static ArrayList<CompleteMatch> parseCompleteMatch(Document doc) {
        /** Get node list of CompleteMatch **/
        NodeList matchesList = doc.getElementsByTagName("CompleteMatch");
        ArrayList<CompleteMatch> matches = new ArrayList<>();
        for (int temp = 0; temp < matchesList.getLength(); temp++) {

            Node node = matchesList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;
                /** Get bet details **/
                Element betNode = (Element) element.getChildNodes().item(0);
                    Double localTeamQuote = Double.parseDouble(betNode.getChildNodes().item(0).getTextContent());
                    Double tieQuote = Double.parseDouble(betNode.getChildNodes().item(1).getTextContent());
                    Double visitorTeamQuote = Double.parseDouble(betNode.getChildNodes().item(2).getTextContent());

                /** Get match details **/
                Element matchNode = (Element) element.getChildNodes().item(1);
                    String coordinates = matchNode.getChildNodes().item(0).getTextContent();

                    int localScore = Integer.parseInt(matchNode.getChildNodes().item(1).getTextContent());
                    int localTeamID = Integer.parseInt(matchNode.getChildNodes().item(2).getChildNodes().item(0).getTextContent());
                    String localTeamName = matchNode.getChildNodes().item(2).getChildNodes().item(1).getTextContent();

                    int visitorScore = Integer.parseInt(matchNode.getChildNodes().item(3).getTextContent());
                    int visitorTeamID = Integer.parseInt(matchNode.getChildNodes().item(4).getChildNodes().item(0).getTextContent());
                    String visitorTeamName = matchNode.getChildNodes().item(4).getChildNodes().item(1).getTextContent();

                /** Get weather details **/
                Element weatherNode = (Element) element.getChildNodes().item(2);
                    String weather = weatherNode.getChildNodes().item(0).getTextContent();
                    String city = weatherNode.getChildNodes().item(1).getTextContent();
                    Double temperature = Double.parseDouble(weatherNode.getChildNodes().item(2).getTextContent());

                /** Create match object and add to array **/
                Team localTeam = new Team(localTeamID, localTeamName);
                Team visitorsTeam = new Team(visitorTeamID, visitorTeamName);
                CompleteMatch match = new CompleteMatch(localTeam,visitorsTeam,localScore,visitorScore,localTeamQuote,visitorTeamQuote,tieQuote,coordinates,city,temperature,weather);
                matches.add(match);
            }
        }
        return matches;

    }

    /**
     * Parses XML response and returns Weather object
     */
    public static Weather parseWeather(Document doc) {
        /** Get weather node **/
        Node weatherNode = (doc.getElementsByTagName("Weather")).item(0);

        Weather weather = null;
        if (weatherNode.getNodeType() == Node.ELEMENT_NODE){
            Element element = (Element) weatherNode;
            if(weatherNode.getChildNodes().getLength()!=0){
                /** Get weather details **/
                String condition = element.getChildNodes().item(0).getTextContent();
                String city = element.getChildNodes().item(1).getTextContent();
                Double temperature = Double.parseDouble(element.getChildNodes().item(2).getTextContent());

                /** Create weather object with given values **/
                weather = new Weather(condition,temperature,city);
            }
        }
        return weather;
    }
}

