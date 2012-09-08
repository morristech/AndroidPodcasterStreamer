package com.example.podcast.streamer;

public class PodcastEpisode {
	protected String strShowName = "";
	protected String strEpisodeTitle = "";
	protected String strEpisodeDescription = "";
	protected String strUrl = "";
	
	public void setShowName(String strName) {
		try {
			this.strShowName = strName;
		} catch (Exception ex) {
			// what to do with this boner?
		}
	}
	
	public void setEpisodeTitle(String strTitle){
		try {
			this.strEpisodeTitle = strTitle;
		} catch (Exception ex) {
			// what to do with this boner?
		}
	}
	
	public void setEpisodeDescription(String strDescription) {
		String strAlteredDescription = "";
		
		try {
			strAlteredDescription = strDescription.replace("&#8217;", "'");
			strAlteredDescription = strAlteredDescription.replace("&#8230;", "");
			strAlteredDescription = strAlteredDescription.replace("&#8211;", "");
			
			this.strEpisodeDescription = strAlteredDescription;
		} catch (Exception ex) {
			// what to do with this boner?
		} finally {
			strAlteredDescription = null;
		}
	}
	
	public void setEpisodeURL(String URLEpisode) {
		try {
			this.strUrl = URLEpisode;
		} catch (Exception ex) {
			// what to do with this boner?
		}
	}
	
	public String getShowName() {
		try {
			return this.strShowName;
		} catch (Exception ex) {
			// what to do with this boner?
			return "";
		}
	}
	
	public String getEpisodeTitle() {
		try {
			return this.strEpisodeTitle;
		} catch (Exception ex) {
			// what to do with this boner?
			return "";
		}
	}
	
	public String getEpisodeDescription() {
		try {
			return this.strEpisodeDescription;
		} catch (Exception ex) {
			// what to do with this boner?
			return "";
		}
	}
	
	public String getEpisodeUrl() {
		try {
			return this.strUrl;
		} catch (Exception ex) {
			// what to do with this boner?
			return null;
		}
		
	}
}
