
public class Track {
    private String artist;
    private String title;
    private String filename;

    /**
     * Constructor for the Track class
     * @param artist The artist name
     * @param title The track's title
     * @param filename The file name
     */
    public Track(String artist, String title, String filename)
    {
        setDetails(artist, title, filename);
    }

    /**
     * Return details of the track: artist, title and file name.
     * @return The track's details.
     */
    public String getDetails()
    {
        return artist + ": " + title + "  (file: " + filename + ")";
    }

    /**
     *
     */
    public void setDetails(String artist, String title, String filename)
    {
        this.artist = artist;
        this.title = title;
        this.filename = filename;
    }
}
