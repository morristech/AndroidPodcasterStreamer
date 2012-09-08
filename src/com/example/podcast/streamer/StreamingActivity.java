package com.example.podcast.streamer;

import java.net.URLDecoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class StreamingActivity extends Activity implements View.OnClickListener, OnBufferingUpdateListener, OnCompletionListener {

	// volume controller
			private AudioManager audio;
			
			// Handler for the media player
			private final Handler mpHandler = new Handler();
			
			// podcast URL and feed parser
			private String strPodcastXmlUrl = "http://www.hngshow.com/feed/podcast";
			private PodcastParser feedParser = null;
			
			// the image buttons
			private ImageButton btnFastForward = null;
			private ImageButton btnNext = null;
			private ImageButton btnPlay = null;
			private ImageButton btnPrevious = null;
			private ImageButton btnRewind = null;
			
			// integers
			private int intCurrentEpisodeSelected = 0;
			
			// media player
			private MediaPlayer mp = null;
			
			// runnable object
			private final Runnable r = new Runnable() {
				public void run() {
					updateSeekProgress();
				}
			};
			
			// the seek bar needs to be visible to the whole class
			private SeekBar sb = null; 
			
		    @Override
		    public void onCreate(Bundle savedInstanceState)
		    {
		    	// the form elements that we will be changing
		    	TextView lblTemp = null;
		    	
		    	// This is the SAX Parser
		    	SAXParserFactory spf = null;
		    	SAXParser sp = null;
		    	
		    	try
		    	{
		    		super.onCreate(savedInstanceState);
		            setContentView(R.layout.activity_streaming);
		            
		            mp = new MediaPlayer();
		            
		            // create the audio controller
		            audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		            
		            // create the new parser
		            spf = SAXParserFactory.newInstance();
		            sp = spf.newSAXParser();
		            feedParser = new PodcastParser();
		            
		            // set the seeker bar
		            this.sb = (SeekBar)findViewById(R.id.seekBar1);
		            this.sb.setMax(100);
		            
		            // configure the fast forward button
		            this.btnFastForward = (ImageButton) findViewById(R.id.imgBtnFastForward);
		            this.btnFastForward.setOnClickListener(this);
		            
		            // configure the next button
		            this.btnNext = (ImageButton) findViewById(R.id.imgBtnNext);
		            this.btnNext.setOnClickListener(this);
		            this.btnNext.setEnabled(false);
		            
		            // configure the play button
		            this.btnPlay = (ImageButton) findViewById(R.id.imgBtnPlay);
		            this.btnPlay.setOnClickListener(this);
		            
		            // configure the previous button
		            this.btnPrevious = (ImageButton) findViewById(R.id.imgBtnPrevious);
		            this.btnPrevious.setOnClickListener(this);
		            
		            // configure the rewind button
		            this.btnRewind = (ImageButton) findViewById(R.id.imgBtnRewind);
		            this.btnRewind.setOnClickListener(this);
		            
		            // parse the XML feed
		            sp.parse(strPodcastXmlUrl, feedParser);
		            
		            // set as main song
		            initAudioFile();
		        }
		    	catch(Exception e)
		    	{
		    		// we're screwed if we hit this
		    		showToastMessage(e.toString());
		    	}
		    	finally
		    	{	
		    		if (lblTemp != null) {
		    			lblTemp = null;
		    		}
		    		
		    		if (spf != null) {
		    			spf = null;
		    		}
		    		
		    		if (sp != null) {
		    			sp = null;
		    		}
		    	}
		    }
		    
		    private void showToastMessage(String msg) {
		    	Toast toast = null;
		    	
		    	try
		    	{
		    		toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		            toast.show();
		    	}
		    	catch (Exception e)
		    	{
		    		showToastMessage(e.toString());
		    	} 
		    	finally
		    	{
		    		toast = null;
		    	} 	
			}

			private void updateSeekProgress() {
				float fltSeekBarPosition = 0;
				
		    	try {
		    		if (mp.isPlaying()) {
		    			fltSeekBarPosition = ((float)mp.getCurrentPosition() / (float)mp.getDuration()) * 100;
		    			
		    			sb.setProgress((int)fltSeekBarPosition);
		    			mpHandler.postDelayed(r, 1000);
		    		}
		    	} catch (Exception ex) {
		    		showToastMessage(ex.toString());
		    	} 
		    }
		    
		    private void stopAudio() {
		    	try {
		    		if (mp.isPlaying()) {
		    			mp.stop();
		    		}
		    		
	    			// change the image to a play button
					this.btnPlay.setImageResource(R.drawable.play_green);
		    		
		    	} catch (Exception ex) {
		    		showToastMessage(ex.toString());
		    	}
		    }
		    
		    private void playAudio() {
				try {
					if (mp.isPlaying()) {
						// pause the audio
						mp.pause();
						
						// change the image to a play button
						this.btnPlay.setImageResource(R.drawable.play_green);
					} else {
						// play the audio
						mp.start();			
					}
		    	} catch (Exception ex) {
		    		showToastMessage(ex.toString());
		    	}
		    }

			public void onClick(View v) {
				try {
					switch (v.getId()) {
						case R.id.imgBtnPlay:
							playAudio();
							break;
						case R.id.imgBtnPrevious:
							// increment the current song by one
							incrementSongIndex();
							
							// load the information
							initAudioFile();
							break;
						case R.id.imgBtnNext:
							// decrement the current song by one
							decrementSongIndex();
							
							initAudioFile();
							break;
						case R.id.imgBtnFastForward:
							// go forward 10 seconds
							mp.seekTo(mp.getCurrentPosition() + 10000);
							break;
						case R.id.imgBtnRewind:
							// go back 10 seconds
							mp.seekTo(mp.getCurrentPosition() - 10000);
							break;
					}
					
					updateSeekProgress();
				} catch(Exception ex) {
					showToastMessage(ex.toString());
				}
			}
			
			private void decrementSongIndex() {
				try {
					if (this.intCurrentEpisodeSelected != 0) {
						stopAudio();
						
						// the first song is index 0 (newest first)
						this.intCurrentEpisodeSelected--;
						
						if (!this.btnPrevious.isEnabled()) {
							this.btnPrevious.setEnabled(true);
						}
						
						if (this.intCurrentEpisodeSelected == 0) {
							// disable the previous button
							this.btnNext.setEnabled(false);
						}
					}
				} catch (Exception ex) {
					showToastMessage(ex.toString());
				}
			}

			public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
				try {
					sb.setSecondaryProgress(arg1);	
				} catch (Exception ex) {
					showToastMessage(ex.toString());
				}
			}

			public void onCompletion(MediaPlayer arg0) {
				try {
					
				} catch (Exception ex) {
					showToastMessage(ex.toString());
				}	
			}
			
			public void initAudioFile() {
				String strDecodedDescription = "";
				TextView lblTemp = null;
				
				try {
					// reset the media player
					mp.reset();
					
					// reset the seek bar
					sb.setProgress(0);
					
					// set all the text elements on the form to reflect the most recent show
		            lblTemp = (TextView) findViewById(R.id.txtEpisodeName);
		            lblTemp.setText(feedParser.Episodes.get(this.intCurrentEpisodeSelected).strEpisodeTitle);
		            
		            lblTemp = (TextView) findViewById(R.id.txtEpisodeDescription);
		            strDecodedDescription = URLDecoder.decode(feedParser.Episodes.get(this.intCurrentEpisodeSelected).strEpisodeDescription, "ISO-8859-1");
		            lblTemp.setText(strDecodedDescription);
					
		            // set the title text
		            lblTemp = (TextView) findViewById(R.id.txtShowName);
		            lblTemp.setText(feedParser.Episodes.get(this.intCurrentEpisodeSelected).strShowName);
		            
					// set as main song
		            mp.setOnBufferingUpdateListener(this);
		            mp.setOnCompletionListener(this);
		            mp.setDataSource(feedParser.Episodes.get(this.intCurrentEpisodeSelected).getEpisodeUrl());
		            
		            // set the stream type to music
		            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		            
		            // We're going to call prepareAsync to load part of the file at a time
		            mp.prepareAsync();
				} catch (Exception ex) {
					showToastMessage(ex.toString());
				}
				finally {
					if (lblTemp != null) {
						lblTemp = null;
					}
					
					strDecodedDescription = null;
				}	
			}
			
			public void incrementSongIndex() {
				try {
					if (this.intCurrentEpisodeSelected <= (feedParser.Episodes.size() - 1)) {
						stopAudio();
						
						// the first song is index 0 (newest first)
						this.intCurrentEpisodeSelected++;
						
						if (!this.btnNext.isEnabled()) {
							this.btnNext.setEnabled(true);
						}
						
						if (this.intCurrentEpisodeSelected == (feedParser.Episodes.size() - 1)) {
							// disable the previous button
							this.btnPrevious.setEnabled(false);
						}
					}
				} catch (Exception ex) {
					showToastMessage(ex.toString());
				}	
			}
			
			// This function handles an event from a hardware key
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				try {
				    switch (keyCode) {
				    case KeyEvent.KEYCODE_VOLUME_UP:
				        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				        return true;
				    case KeyEvent.KEYCODE_VOLUME_DOWN:
				        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
				        return true;
				    case KeyEvent.KEYCODE_HOME:
				    	stopAudio();
				    	this.finish();
				        return true;
				    case KeyEvent.KEYCODE_BACK:
				    	stopAudio();
				    	this.finish();
				    	return true;
				    default:
				        return false;
				    }
				} catch (Exception ex) {
					showToastMessage(ex.toString());
					return false;
				}
			}
			
}
