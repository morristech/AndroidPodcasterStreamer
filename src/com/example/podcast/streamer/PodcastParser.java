package com.example.podcast.streamer;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PodcastParser extends DefaultHandler {
	private PodcastEpisode Episode = null;
	public List<PodcastEpisode> Episodes = null;
	private String tempVal = "";
	
	PodcastParser() {
		try {
			Episodes = new ArrayList<PodcastEpisode>();
		} catch(Exception ex) {
			
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			tempVal = "";
			if (localName.equalsIgnoreCase("ITEM")) {
				Episode = new PodcastEpisode();
			} else if (localName.equalsIgnoreCase("ENCLOSURE")) {
				Episode.setEpisodeURL(attributes.getValue("url"));
			} else {
				
			}
		} catch(Exception ex) {
			
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		try {
			tempVal = new String(ch,start,length);
		} catch (Exception ex) {
			
		}
		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (localName.equalsIgnoreCase("ITEM")) {
				// add the show to the list
				Episodes.add(Episode);
			} else if (localName.equalsIgnoreCase("LINK")) {
				// set the episode URL
				// Episode.setEpisodeURL(tempVal);
			} else if (localName.equalsIgnoreCase("TITLE")) {
				// set the episode title
				Episode.setEpisodeTitle(tempVal);
			} else if (localName.equalsIgnoreCase("DESCRIPTION")) {
				// set the show description
				Episode.setEpisodeDescription(tempVal);			
			}  else if (localName.contains("author")) {
				// set the show name
				Episode.setShowName(tempVal);
			}
			else {
				
			}
		} catch (Exception ex) {
			
		}
	}
}
