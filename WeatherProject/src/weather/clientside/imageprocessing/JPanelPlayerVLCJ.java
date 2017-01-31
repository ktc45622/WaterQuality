
package weather.clientside.imageprocessing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.JPanel;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import weather.common.data.resource.AVIInstance;
import weather.common.utilities.*;

/**
 * VLC Implementation for AVI playback.
 *
 * Uses VLCJ 2.0, requires VLC 2.0.0+
 *
 * The media player CANNOT even attempt to play if the panel/anything containing
 * the panel is not visible.
 *
 * This implementation uses a play list and reads movie files from the local 
 * hard drive. 
 * @author Justin Gamble
 */
public class JPanelPlayerVLCJ extends JPanel {

    /**
     * listPlayer handles the loading of new elements of the playlist. Only ever
     * call play on listPlayer, not media player.
     */
    //private MediaListPlayer listPlayer;
    /**
     * mediaPlayer handles the actual playback of the videos loaded by the
     * listPlayer.
     */
    private EmbeddedMediaPlayer mediaPlayer;
    /**
     * Actual video surface.
     */
    private Canvas canvas;
    /**
     * Used for creation of listPlayer and mediaPlayer and joining them.
     */
    private MediaPlayerFactory mpf;
    /**
     * Playlist of videos.
     */
    //private MediaList ml;
    /**
     * Used for serialization.
     */
    private final static long serialVersionUID = 1L;
    /**
     * Holds the directory listings for the video files.
     */
    private final LinkedList<String> movies;
    /**
     * Playback rate of the movies.
     */
    private float playRate;
    /**
     * Dimensions of the video player.
     */
    private int width;
    private int height;
    /** 
     * How long an hour of video is in milliseconds of video time.
     */
    private final int MILLISECONDS_OF_VIDEO_TIME_PER_HOUR_SEGMENT =
            Integer.parseInt(PropertyManager
                    .getGeneralProperty("MOVIE_LENGTH")) * 1000;

    /**
     * The length of a hour in seconds of real time.
     */
    private final int SECONDS_IN_ONE_HOUR = 3600;
    /**
     * Index of the currently playing video in the playlist.
     */
    private int movieIndex;
    /**
     * Whether the media should be paused or not.
     */
    private boolean paused;
    /**
     * Thread to monitor the video to make sure it is paused when it should be.
     * VLC has no way of reporting when it finished loading or seeking, as such
     * the pause calls can come before the video would be able to handle them.
     */
    private final Thread watcher;
    private boolean terminateWatcher;
    private final Object shouldBePaused;
    private final Object shouldBePlaying;
    /**
     * VLC playback commands.
     */
    private final String[] mediaOptions = {"--no-video-title-show", 
        "--stop-time=" + (MILLISECONDS_OF_VIDEO_TIME_PER_HOUR_SEGMENT),
        "--no-playlist-autostart", "-q"};

    /**
     * Creates the player for avi playback. Loads the movies to be played in
     * order using a playlist.
     *
     * @param moviePaths Set of movies to play.
     */
    public JPanelPlayerVLCJ(Vector<AVIInstance> moviePaths) {
        shouldBePaused = new Object();
        shouldBePlaying = new Object();
        movieIndex = -1;//first movie has not played yet
        paused = true;
        terminateWatcher = false;

        //Create the Playback surface
        mpf = new MediaPlayerFactory(mediaOptions);

        canvas = new Canvas();
        canvas.setBackground(Color.black);
        CanvasVideoSurface videoSurface = mpf.newVideoSurface(canvas);

        mediaPlayer = mpf.newEmbeddedMediaPlayer();
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

            @Override
            public void finished(MediaPlayer thisMediaPlayer) {               
                movieIndex++;
                if (movieIndex == movies.size()) {
                    reset();
                    return;
                }

                thisMediaPlayer.prepareMedia(movies.get(movieIndex), mediaOptions);
                
                playMovie();
            }
       });

        mediaPlayer.setVideoSurface(videoSurface);

        movies = new LinkedList<>();
        for (AVIInstance avii : moviePaths) {
            movies.add( avii.getMovie());
        }

        mediaPlayer.setStandardMediaOptions(mediaOptions);

        //Add Components
        this.add(canvas);

        //misc
        playRate = 1.0f;

        //watcher
        watcher = new Thread("VLC watcher thread." + (new Date().toString())) {

            @Override
            public void run() {
                //Debug.println("VLC Watcher thread is running");
                while (!terminateWatcher) {
                    //Debug.println("Paused is : " + paused);
                    while (paused) {
                        //Debug.println("VLCWatcher thread is pausing.");
                        if (mediaPlayer.canPause() && mediaPlayer.isPlaying()) {
                            mediaPlayer.setPause(true);
                            synchronized (shouldBePaused) {
                                try {
                                    shouldBePaused.wait();
                                } catch (InterruptedException e) {
                                    Debug.println("VLC watcher Interrupted");
                                }
                            }
                        }
                    }

                    //Debug.println("AVI - Outside of loop in run");
                    synchronized (shouldBePlaying) {
                        try {
                            shouldBePlaying.wait();
                        } catch (InterruptedException e) {
                            Debug.println("VLC watcher interrupted");
                        }
                    }
                }
                //Debug.println("VLCWatcher thread terminating");
            }
        };
    }
    
    /**
     * Creates the player for avi playback.  Loads a single movie given is absolute
     * file path.
     *
     * @param filePath The path to the movie.
     */
    public JPanelPlayerVLCJ(String filePath) {
        shouldBePaused = new Object();
        shouldBePlaying = new Object();
        movieIndex = -1;//first movie has not played yet
        paused = true;
        terminateWatcher = false;
        
        //Create the Playback surface
        mpf = new MediaPlayerFactory(mediaOptions);

        canvas = new Canvas();
        canvas.setBackground(Color.black);
        CanvasVideoSurface videoSurface = mpf.newVideoSurface(canvas);

        mediaPlayer = mpf.newEmbeddedMediaPlayer();
        // mediaPlayer.setAspectRatio("16:9"); this line forces widescreen
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                Debug.println("finished");
                movieIndex++;
                if (movieIndex == movies.size()) {
                    reset();
                    return;
                }

                mediaPlayer.prepareMedia(movies.get(movieIndex), mediaOptions);

                playMovie();
            }
        });

        mediaPlayer.setVideoSurface(videoSurface);


        movies = new LinkedList<String>();
        movies.add(filePath);


        mediaPlayer.setStandardMediaOptions(mediaOptions);

        //Add Components
        this.add(canvas);

        //misc
        playRate = 1.0f;

        //watcher
        watcher = new Thread("VLC watcher thread." + (new Date().toString())) {
            @Override
            public void run() {
                //Debug.println("VLC Watcher thread is running");
                while (!terminateWatcher) {
                    //Debug.println("Paused is : " + paused);
                    while (paused) {
                        //Debug.println("VLCWatcher thread is pausing.");
                        if (mediaPlayer.canPause() && mediaPlayer.isPlaying()) {
                            mediaPlayer.setPause(true);
                            synchronized (shouldBePaused) {
                                try {
                                    shouldBePaused.wait();
                                } catch (InterruptedException e) {
                                    Debug.println("VLC watcher Interrupted");
                                }
                            }
                        }
                    }

                    //Debug.println("AVI - Outside of loop in run");
                    synchronized (shouldBePlaying) {
                        try {
                            shouldBePlaying.wait();
                        } catch (InterruptedException e) {
                            Debug.println("VLC watcher interrupted");
                        }
                    }
                }
                //Debug.println("VLCWatcher thread terminating");
            }
        };
    }
    
    /**
     * Gets the image showing on the player.
     * 
     * @return The image showing on the player.
     */
    public BufferedImage getBufferedImage() {
        return mediaPlayer.getSnapshot();
    }

    /**
     * Plays the video from the current time.
     */
    private void playMovie() {
        paused = false;
        synchronized (shouldBePaused) {
            shouldBePaused.notify();
        }
        //Debug.println("Playing");
        if (movieIndex == -1) {
            movieIndex++;
            mediaPlayer.prepareMedia(movies.get(movieIndex), mediaOptions);

        }
        if (canvas.isShowing()) {

            mediaPlayer.play();
        }
        else{
            //Debug.println("Video surface was not showing.");
        }



    }

    /**
     * Stops playing the current video.
     */
    public void stopMovie() {
        paused = true;
        synchronized (shouldBePlaying) {
            shouldBePlaying.notify();
        }
    }

    /**
     * Whether the video is currently playing or not.
     *
     * @return True if the player is currently playing, false if stopped or
     * paused.
     */
    public boolean isPlaying() {
        return !paused;
    }

    /**
     * Resets the play list to play from the first item again. Also loads the
     * video for a brief moment to get a preview image on the window.
     *
     * DO NOT call this without the panel visible.
     */
    public void reset() {
        if (!watcher.isAlive()) {
            watcher.start();
        }

        //Debug.println("time: " + mediaPlayer.getTime());
        playMovie();
        mediaPlayer.prepareMedia(movies.get(0), mediaOptions);

        mediaPlayer.setPosition(0);
        playMovie();

        stopMovie();

        movieIndex = 0;
    }

    /**
     * Changes the window size for the player.
     *
     * Aspect ratio will be maintained, black borders will be shown when
     * dimensions do not match native video aspect ratio.
     *
     * @param width Width of the new window.
     * @param height Height of the new window.
     * @throws WeatherException
     */
    public void setMovieSize(int width, int height) throws WeatherException {
        this.width = width;
        this.height = height;
        Dimension dim = new Dimension(width, height);

        //Set JPanel Size
        super.setSize(dim);
        super.setPreferredSize(dim);
        super.setMaximumSize(dim);
        super.setMinimumSize(dim);
        //Set Component Size
        canvas.setSize(dim);
        canvas.setPreferredSize(dim);
        canvas.setMaximumSize(dim);
        canvas.setMinimumSize(dim);


        super.validate();
    }

    /**
     * Gets the current movie dimensions.
     *
     * @return Dimension of the current movie.
     * @throws WeatherException
     */
    public Dimension getMovieSize() throws WeatherException {
        return new Dimension(this.width, this.height);
    }

    /**
     * Sets the rate of the currently playing movie(s).
     *
     * TODO : Find a way to frameskip, currently have to limit speed to 2x.
     *
     * @param rate The rate at which to play the movie. (0.0 - 2.0)
     * @throws WeatherException
     */
    public void setMovieRate(float rate) throws WeatherException {
        //Debug.println("setMovieRate called.");
        if (rate > 2.0) {
            rate = 2.0f;
        }
        playRate = rate;
        mediaPlayer.setRate(rate);
        this.playMovie();
    }

    /**
     * Returns the rate of the currently playing movie(s).
     *
     * @return The play rate of the current movie.
     * @throws WeatherException
     */
    public float getMovieRate() throws WeatherException {
        return playRate;
    }
    
    /**
     * Preform cleanup considerations after playback.
     */
    public void cleanup() {
        //Debug.println("VLC cleanup called");
        terminateWatcher = true;
        paused = false;
        while (watcher.isAlive()) {
            //Debug.println("watcher is still alive");
            try {
                synchronized (shouldBePlaying) {
                    shouldBePlaying.notify();
                }
                synchronized (shouldBePaused) {
                    shouldBePaused.notify();
                }
            } catch (Exception ex) {
            }
        }

        mediaPlayer.release();
        mpf.release();
    }

    /**
     * Returns the current time in seconds of the playing movie as converted 
     * into seconds of real time being filmed.
     *
     * @return The current time in seconds of the playing movie as converted 
     * into seconds of real time being filmed (-1 if the player is not 
     * initialized).
     * 
     * TODO: Fix bugs and make work for day-long videos.
     */
    public int getCurrentTimeOfMovies() {
         Debug.println("getCurrentTimeOfMovies(): mills in segment: " + mediaPlayer.getTime());
        
         if (mediaPlayer.getTime() == -1) {
             return -1;
         }
         
         //Get part of current hour.
         double partOfCurrentHour = ((double) mediaPlayer.getTime())
                 / MILLISECONDS_OF_VIDEO_TIME_PER_HOUR_SEGMENT;
         
         //Get tatal hours played as faction.
         double totalHoursPlayed = partOfCurrentHour + movieIndex;
         
         //Return result as seconds.
         return (int) (totalHoursPlayed * SECONDS_IN_ONE_HOUR);
    }

    /**
     * Sets the playlist to the playback time passed. This function uses
     * the amount of real time being filmed.
     *
     * @param secs Playlist time to set the playlist to in seconds using the
     * amount of real time being filmed.
     * 
     * TODO: Fix bugs and make work for day-long videos
     */
    public synchronized void setTimeInSeconds(int secs) {
        Debug.println("setTimeInSeconds to " + secs);
        stopMovie();

        int selectedIndex = secs / SECONDS_IN_ONE_HOUR;     
        if (selectedIndex >= movies.size()) {
            return;
        }
        
        //Change video segments if necessary.
        if (movieIndex != -1 && selectedIndex != movieIndex) {
            movieIndex = selectedIndex;
            if (!watcher.isAlive()) {
                watcher.start();
            }
            playMovie();
            mediaPlayer.prepareMedia(movies.get(movieIndex), mediaOptions);
            playMovie();
            stopMovie();
        }
        
        Debug.println("setTimeInSeconds( " + secs + " ): movieIndex: " + movieIndex);

        /**
         * This section finds the requested point in the video segment.
         */
        
        //Compute the number of seconds into the current hour that the input
        //represents.
        int elapsedSecondsInPartialHour = secs % SECONDS_IN_ONE_HOUR;

        //Now, express these seconds as a fracton of an hour.
        double hourFraction = ((double) elapsedSecondsInPartialHour)
                / SECONDS_IN_ONE_HOUR;

        //Find the milliseconds of video time to have elapsed.
        int selectedMovieTime = ((int) (hourFraction
                * MILLISECONDS_OF_VIDEO_TIME_PER_HOUR_SEGMENT));

        Debug.println("setTimeInSeconds( " + secs + " ): Movie Time: " + selectedMovieTime);

        mediaPlayer.setTime(selectedMovieTime);
    }

    /**
     * Gets the total duration of the playlist in milliseconds.
     *
     * @return Total playlist length in milliseconds.  (-1 if the player is not
     * initialized).
     * 
     * TODO: Test this method.
     */
    public long getTotalTimeInMillis() {
        /**
         * Here it is assumed that, if there are multiple movies in the 
         * playlist, all movies are the same length in video time.
         */
        if (mediaPlayer.getLength() == -1) {
            mediaPlayer.prepareMedia(movies.get(0), mediaOptions);
        }
        return mediaPlayer.getLength() * movies.size();
    }

    /**
     * Returns the paths to the currently playing movie.
     *
     * @return The paths to the currently playing movie.
     */
    public LinkedList<String> getCurrentMoviePaths() {
        return movies;
    }
}
