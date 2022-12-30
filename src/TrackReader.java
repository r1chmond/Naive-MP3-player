import java.io.File;

public class TrackReader {
/**
 * Create the track reader, ready to read tracks from the music library folder.
 */
    public TrackReader()
    {
        // Nothing to do here.
    }

    /**
     * Try to decode details of the artist and the title
     * from the file name.
     * It is assumed that the details are in the form:
     *     artist-title.mp3
     * @param file The track file.
     * @return A Track containing the details.
     */
    public Track decodeDetails(File file)
    {
        // The information needed.
        String artist = "unknown";
        String title = "unknown";
        String filename = file.getPath();

        // Look for artist and title in the name of the file.
        String details = file.getName();
        String[] parts = details.split("-");

        if(parts.length == 2) {
            artist = parts[0];
            String titlePart = parts[1];
            // Remove a file-type suffix.
            parts = titlePart.split("\\.");
            if(parts.length >= 1) {
                title = parts[0];
            }
            else {
                title = titlePart;
            }
        }
        return new Track(artist, title, filename);
    }
}

