package gitlet;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The file store.
 *
 * @author Charlie Zhou
 */
public class FileStore {
    /**
     * The blobs folder name.
     */
    private static final String BLOBS = "blobs";
    /**
     * the blob dir.
     */
    private final Path blobDir;

    /**
     * Create a file store.
     * @param workingDir the working directory.
     */
    public FileStore(Path workingDir) {
        this.blobDir = workingDir.resolve(Gitlet.GITLET_DIR).resolve(BLOBS);
    }

    /**
     * Puts a file into the store.
     * @param file the file.
     * @return the hash.
     */
    public String putFile(Path file) {
        String hash = Utils.fileHash(file);
        Path hashFile = blobDir.resolve(hash);
        if (Files.notExists(hashFile)) {
            IO.copyFile(file, hashFile);
        }

        return hash;
    }

    /**
     * Gets a file from the store.
     * @param hash the hash.
     * @return the file's path.
     */
    public Path getFile(String hash) {
        Path hashFile = blobDir.resolve(hash);
        if (Files.notExists(hashFile)) {
            throw Utils.error("File does not exist.");
        }

        return hashFile;
    }
}
