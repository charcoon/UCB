package gitlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * IOs.
 *
 * @author Charlie Zhou
 */
public class IO {
    /**
     * Write a string to the path.
     * @param path the path.
     * @param seq the string.
     */
    public static void writeString(Path path, String seq) {
        try {
            byte[] bytes = seq.getBytes(StandardCharsets.UTF_8);
            Files.write(path, bytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Read a string from path.
     * @param path the path.
     * @return the string.
     */
    public static String readString(Path path) {
        if (path == null || !Files.exists(path)) {
            return "";
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Reads a branch from file.
     * @param path the path.
     * @param commitResolver the resolver.
     * @return the branch.
     */
    public static Branch readBranch(Path path,
                                    Function<String, Commit> commitResolver) {
        String headHash = IO.readString(path);
        String name = path.getFileName().toString();
        return new Branch(commitResolver.apply(headHash), name);
    }

    /**
     * Writes a branch to file.
     * @param path the file.
     * @param branch the branch.
     */
    public static void writeBranch(Path path, Branch branch) {
        IO.writeString(path, branch.head().hash());
    }

    /**
     * Writes a commit to file.
     * @param path the file.
     * @param commit the commit.
     */
    public static void writeCommit(Path path, Commit commit) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(path))) {
            Commit.writeCommit(commit, out);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Read a commit from file.
     * @param path the file.
     * @param workingDir the working directory.
     * @param commitResolver the resolver.
     * @param hash the hash.
     * @return the commit.
     */
    public static Commit readCommit(Path path, Path workingDir,
                                    Function<String, Commit> commitResolver,
                                    String hash) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(path))) {
            return Commit.readCommit(in, workingDir, commitResolver, hash);
        } catch (IOException | ClassNotFoundException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Writes a staging to file.
     * @param path the file.
     * @param stagedFiles the staged files.
     */
    public static void writeStaging(Path path, StagedFiles stagedFiles) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(path))) {
            StagedFiles.writeStaging(stagedFiles, out);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Reads a staging from file.
     * @param path the file.
     * @param workingDir the working directory.
     * @return the staging.
     */
    public static StagedFiles readStaging(Path path, Path workingDir) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(path))) {
            return StagedFiles.readStaging(in, workingDir);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Copy files from from to to.
     * @param from the form.
     * @param to the to.
     */
    public static void copyFile(Path from, Path to) {
        try {
            Files.createDirectories(to.getParent());
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Deletes a file if it's existed.
     * @param file the file.
     */
    public static void deleteIfExists(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * Walks.
     * @param from from path.
     * @return Stream of paths.
     */
    public static Stream<Path> walk(Path from) {
        try {
            return Files.walk(from, 1);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * @param firstFile the first file.
     * @param secondFile the second file.
     * @return whether are they different.
     */
    public static boolean different(Path firstFile, Path secondFile) {
        try {
            byte[] first = Files.readAllBytes(firstFile);
            byte[] second = Files.readAllBytes(secondFile);

            return !Arrays.equals(first, second);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }
}
