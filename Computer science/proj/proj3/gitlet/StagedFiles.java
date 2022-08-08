package gitlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the staging area of the repository.
 *
 * @author Charlie Zhou
 */
public class StagedFiles {
    /**
     * The working directory.
     */
    private Path workingDir;
    /**
     * The files and corresponding hashes.
     */
    private Map<Path, String> fileAndHash;
    /**
     * The file store.
     */
    private FileStore fileStore;

    /**
     * Create an empty file stage.
     * @param workingDir0 the working directory.
     */
    public StagedFiles(Path workingDir0) {
        this(workingDir0, new HashMap<>());
    }

    /**
     * Create an inherent file stage.
     * @param workingDir0 the working directory.
     * @param parent0 the parent stage.
     */
    public StagedFiles(Path workingDir0, StagedFiles parent0) {
        this(workingDir0, parent0.fileAndHash);
    }

    /**
     * The constructor to create a StagedFiles.
     * @param workingDir0 the working directory.
     * @param fileAndHash0 the files and hashes.
     */
    private StagedFiles(Path workingDir0, Map<Path, String> fileAndHash0) {
        this.workingDir = workingDir0;
        this.fileAndHash = new HashMap<>(fileAndHash0);
        this.fileStore = new FileStore(this.workingDir);
    }

    /**
     * Adds a file to the staging area and assuming it's existed.
     * @param file the file to be added.
     */
    public void addFile(Path file) {
        file = file.toAbsolutePath().normalize();
        String hash = fileStore.putFile(file);
        fileAndHash.put(file, hash);
    }

    /**
     * @return the staged files.
     */
    public Set<Path> files() {
        return Collections.unmodifiableSet(this.fileAndHash.keySet());
    }

    /**
     * Writes the staging to a stream.
     * @param stagedFiles the staged file.
     * @param s the output stream.
     * @throws IOException the IOException.
     */
    public static void writeStaging(StagedFiles stagedFiles,
                                    ObjectOutputStream s) throws IOException {
        s.writeInt(stagedFiles.fileAndHash.size());

        for (Map.Entry<Path, String> entry
                : stagedFiles.fileAndHash.entrySet()) {
            String path = stagedFiles.workingDir
                    .relativize(entry.getKey()).toString();
            String hash = entry.getValue();
            s.writeUTF(path);
            s.writeUTF(hash);
        }
    }

    /**
     * Reads a staging from a stream.
     * @param s the stream.
     * @param workingDir the working directory.
     * @return the staging.
     * @throws IOException the IOException.
     */
    public static StagedFiles readStaging(ObjectInputStream s,
                                          Path workingDir)
            throws IOException {
        int size = s.readInt();

        Map<Path, String> pathAndFile = new HashMap<>(size);

        for (int i = 0; i < size; i++) {
            String path = s.readUTF();
            String hash = s.readUTF();

            pathAndFile.put(workingDir.resolve(path).normalize(), hash);
        }

        return new StagedFiles(workingDir, pathAndFile);
    }

    /**
     * @return the hash of files.
     */
    public String hash() {
        return Utils.sha1(fileAndHash.keySet(), fileAndHash.values());
    }

    /**
     * @param path the file.
     * @return if the file is staged.
     */
    public boolean isStaged(Path path) {
        path = path.toAbsolutePath().normalize();
        return files().contains(path);
    }

    /**
     * Removes a file.
     * @param path the file.
     */
    public void remove(Path path) {
        fileAndHash.remove(path);
    }

    /**
     * Checks if the other staging same as this.
     * @param other the other staging.
     * @return result.
     */
    public boolean same(StagedFiles other) {
        return this.hash().equals(other.hash());
    }

    /**
     * Gets a file's path of truth.
     * @param file the file.
     * @return the truth.
     */
    public Path getFile(Path file) {
        file = file.toAbsolutePath().normalize();
        String hash = fileAndHash.get(file);
        if (hash == null) {
            return null;
        }
        return fileStore.getFile(hash);
    }

    /**
     * Calculates the difference between two staging areas.
     * @param anotherStage another staging.
     * @param identity whether to care the file content.
     * @return the difference of files.
     */
    public Set<Path> difference(StagedFiles anotherStage, boolean identity) {
        Set<Path> result = new HashSet<>();

        for (Map.Entry<Path, String> entry : fileAndHash.entrySet()) {
            String anotherHash = anotherStage.getHash(entry.getKey());
            if (identity) {
                if (anotherHash == null
                    || !anotherHash.equals(entry.getValue())) {
                    result.add(entry.getKey());
                }
            } else if (anotherHash == null) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Gets the hash of a file.
     * @param file the file.
     * @return the hash.
     */
    public String getHash(Path file) {
        return fileAndHash.get(file);
    }

    /**
     * Checks if contains the file.
     * @param file the file.
     * @return the result.
     */
    public boolean contains(Path file) {
        return fileAndHash.containsKey(file);
    }
}
