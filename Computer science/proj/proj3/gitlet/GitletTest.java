package gitlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class GitletTest {
    private static Path testHome;

    @Before
    public void setUp() throws Exception {
        try {
            testHome = Files.createTempDirectory("githome");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void init() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Assert.assertTrue(
                Files.exists(testHome.resolve(".gitlet")));
        Assert.assertTrue(
                Files.exists(testHome.resolve(".gitlet").resolve("branches")));
        Assert.assertTrue(
                Files.exists(testHome.resolve(".gitlet").resolve("commits")));

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertTrue(newGitlet.initialized());
    }

    @Test
    public void add() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Path testFile = testHome.resolve("test_file");
        IO.writeString(testFile, "Hello World");
        gitlet.add(List.of(Paths.get("test_file")));

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertTrue(newGitlet.trackedFiles().size() > 0);
    }

    @Test
    public void commit() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Path testFile = testHome.resolve("test_file");
        IO.writeString(testFile, "Hello World");
        gitlet.add(List.of(Paths.get("test_file")));

        gitlet.commit("message");

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertEquals(1, newGitlet.trackedFiles().size());
    }

    @Test
    public void rm() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Path testFile = testHome.resolve("test_file");
        IO.writeString(testFile, "Hello World");
        gitlet.add(List.of(Paths.get("test_file")));

        gitlet.commit("message");
        Assert.assertEquals(0, gitlet.deletedFiles().size());
        gitlet.rm(testFile);

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertEquals(1, newGitlet.deletedFiles().size());
    }

    @Test
    public void log() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Path testFile = testHome.resolve("test_file");
        IO.writeString(testFile, "Hello World");
        gitlet.add(List.of(Paths.get("test_file")));

        gitlet.commit("message");

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertEquals(2, newGitlet.log().size());
    }

    @Test
    public void globalLog() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Path testFile = testHome.resolve("test_file");
        IO.writeString(testFile, "Hello World");
        gitlet.add(List.of(Paths.get("test_file")));

        gitlet.commit("message");

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertEquals(2, newGitlet.globalLog().size());
    }

    @Test
    public void untrackedFile() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Path testFile = testHome.resolve("test_file");
        IO.writeString(testFile, "Hello World");

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertEquals(1, newGitlet.untrackedFiles().size());
    }

    @Test
    public void addedFile() throws IOException {
        Gitlet gitlet = new Gitlet(testHome);
        gitlet.init();

        Path testFile = testHome.resolve("test_file");
        IO.writeString(testFile, "Hello World");
        gitlet.add(Collections.singletonList(testFile));

        Gitlet newGitlet = new Gitlet(testHome);
        Assert.assertEquals(0, newGitlet.untrackedFiles().size());
        Assert.assertEquals(1, newGitlet.trackedFiles().size());
    }
}
