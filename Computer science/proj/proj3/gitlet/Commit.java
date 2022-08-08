package gitlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents a commit.
 *
 * @author Charlie Zhou
 */
public class Commit {
    /**
     * The default date formatter.
     */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss uuuu ZZ");
    /**
     * The default time zone.
     */
    private static final ZoneId DEFAULT_ZONE =
            ZoneId.systemDefault();

    /**
     * The commit hash.
     */
    private String hash;
    /**
     * The commit date.
     */
    private Instant date;
    /**
     * The parent commits.
     */
    private List<Commit> parents;
    /**
     * The commit message.
     */
    private String message;
    /**
     * The staged files.
     */
    private StagedFiles stagedFiles;

    /**
     * Create a commit w/o parents.
     *
     * @param date0 The commit date.
     * @param message0 The commit message.
     * @param stagedFiles0 the staged files.
     */
    public Commit(Instant date0, String message0, StagedFiles stagedFiles0) {
        this(date0, new LinkedList<>(), message0, stagedFiles0);
    }


    /**
     * Create a commit w/ parents.
     * @param date0 the date.
     * @param parents0 the parents.
     * @param message0 the message.
     * @param stagedFiles0 the staged files.
     */
    public Commit(Instant date0, List<Commit> parents0,
                  String message0, StagedFiles stagedFiles0) {
        this(Utils.sha1(stagedFiles0.hash(), Utils.sha1(parents0)), date0,
                parents0, message0, stagedFiles0);
    }

    /**
     * A detailed commit.
     * @param hash0 the hash.
     * @param date0 the date.
     * @param parents0 the parents.
     * @param message0 the message.
     * @param stagedFiles0 the staged files.
     */
    private Commit(String hash0, Instant date0, List<Commit> parents0,
                   String message0, StagedFiles stagedFiles0) {
        this.hash = hash0;
        this.date = date0;
        this.parents = parents0;
        this.message = message0;
        this.stagedFiles = stagedFiles0;
    }

    /**
     * @return The hash.
     */
    public String hash() {
        return hash;
    }

    /**
     * Whether the file is tracked by this commit.
     * @param path the file.
     * @return tracked.
     */
    public boolean isTracking(Path path) {
        return this.stagedFiles.isStaged(path);
    }

    /**
     * Write a commit to an object stream.
     * @param commit the commit.
     * @param s the steam.
     * @throws IOException the IOException.
     */
    public static void writeCommit(Commit commit,
                                   ObjectOutputStream s)
            throws IOException {
        s.writeObject(commit.date);
        s.writeUTF(commit.message);
        s.writeInt(commit.parents.size());

        for (Commit parent : commit.parents) {
            s.writeUTF(parent.hash());
        }

        StagedFiles.writeStaging(commit.stagedFiles, s);
    }

    /**
     * Read commit from an object stream.
     * @param s the stream.
     * @param workingDir the working directory.
     * @param commitResolver the commit resolver.
     * @param hash the hash of commit.
     * @return the Commit
     * @throws IOException the IOException.
     * @throws ClassNotFoundException the ClassNotFoundException.
     */
    public static Commit readCommit(ObjectInputStream s,
                                    Path workingDir,
                                    Function<String, Commit> commitResolver,
                                    String hash)
            throws IOException, ClassNotFoundException {
        Instant date = (Instant) s.readObject();
        String message = s.readUTF();

        int size = s.readInt();

        List<Commit> parents = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            String parentHash = s.readUTF();
            parents.add(commitResolver.apply(parentHash));
        }
        StagedFiles stagedFiles = StagedFiles.readStaging(s, workingDir);

        return new Commit(hash, date, parents, message, stagedFiles);
    }

    /**
     * @return log format of commit.
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("===%n"));
        sb.append(String.format("commit %s%n", hash));
        if (parents.size() > 1) {
            sb.append(String.format("Merge: %s %s%n",
                    parents.get(0).shortHash(),
                    parents.get(1).shortHash()));
        }
        sb.append(String.format("Date: %s%n",
                date.atZone(DEFAULT_ZONE).format(FORMATTER)));
        sb.append(String.format("%s%n", message));
        return sb.toString();
    }

    /**
     * 7 chars of hash.
     *
     * @return short hash.
     */
    public String shortHash() {
        return hash.substring(0, 7);
    }

    /**
     * @return parents.
     */
    public List<Commit> parents() {
        return this.parents;
    }

    /**
     * @return staged files.
     */
    public StagedFiles stagedFiles() {
        return stagedFiles;
    }

    /**
     * @return commit message.
     */
    public String message() {
        return message;
    }
}
