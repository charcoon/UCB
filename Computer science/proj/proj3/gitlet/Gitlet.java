package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a Gitlet instance.
 *
 * @author Charlie Zhou
 */
public class Gitlet {
    /**
     * The gitlet dir.
     */
    public static final String GITLET_DIR = ".gitlet";
    /**
     * The commit dir.
     */
    private static final String COMMIT_DIR = "commits";
    /**
     * The branch dir.
     */
    private static final String BRANCH_DIR = "branches";

    /**
     * The remote dir.
     */
    private static final String REMOTE_DIR = "remotes";
    /**
     * The head.
     */
    private static final String HEAD_FILE = "head";
    /**
     * The active branch.
     */
    private static final String BRANCH_FILE = "branch";
    /**
     * The index.
     */
    private static final String STAGING_FILE = "index";

    /**
     * The working directory.
     */
    private Path workingDir;
    /**
     * The active branch.
     */
    private Branch active;
    /**
     * All branches.
     */
    private List<Branch> branches = new LinkedList<>();
    /**
     * The commit map.
     */
    private Map<String, Commit> commitMap = new HashMap<>();

    /**
     * All remotes.
     */
    private Map<String, Path> remotes = new HashMap<>();

    /**
     * The commit head refers.
     */
    private Commit head;
    /**
     * Current staged files.
     */
    private StagedFiles stagedFiles;
    /**
     * If the gitlet initialized.
     */
    private boolean initialized;
    /**
     * The commit resolver.
     */
    private Function<String, Commit> commitResolver = new Function<>() {
        /**
         * Gets the commit by hash.
         * @param hash the hash.
         * @return the commit.
         */
        @Override
        public Commit apply(String hash) {
            Commit commit = commitMap.get(hash);
            if (commit != null) {
                return commit;
            }

            Path commitDir = workingDir
                    .resolve(GITLET_DIR).resolve(COMMIT_DIR);

            Path commitPath = commitDir.resolve(hash);
            if (Files.notExists(commitPath)) {
                throw Utils.error("No commit with that id exists.");
            }

            commit = IO.readCommit(commitPath,
                    workingDir, commitResolver, hash);
            commitMap.put(hash, commit);
            return commit;
        }
    };

    /**
     * Create a gitlet.
     * @param workingDir0 the working directory.
     */
    public Gitlet(Path workingDir0) {
        this.workingDir = workingDir0;

        Path gitletDir = workingDir0.resolve(GITLET_DIR);
        initialized = Files.exists(gitletDir);

        if (initialized) {
            loadFromFileSystem();
        }
    }

    /**
     * The init.
     */
    public void init() {
        if (initialized) {
            throw Utils.error(
                    "A Gitlet version-control system "
                            + "already exists in the current directory.");
        }
        Path gitletDir = workingDir.resolve(GITLET_DIR);

        try {
            Files.createDirectory(gitletDir);
            Files.createFile(gitletDir.resolve(HEAD_FILE));
            Files.createDirectory(gitletDir.resolve(COMMIT_DIR));
            Files.createDirectory(gitletDir.resolve(BRANCH_DIR));
            Files.createDirectory(gitletDir.resolve(REMOTE_DIR));
        } catch (IOException e) {
            throw new GitletException(e);
        }

        Commit init = new Commit(Instant.EPOCH,
                "initial commit", new StagedFiles(workingDir));
        Branch master = new Branch(init, "master");

        this.initialized = true;
        this.active = master;
        this.head = init;
        this.branches.add(master);
        this.commitMap.put(init.hash(), init);
        this.stagedFiles = new StagedFiles(this.workingDir);

        saveToFileSystem();
    }

    /**
     * Add files to gitlet.
     * @param files the files.
     */
    public void add(List<Path> files) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }

        for (Path file : files) {
            Path path = workingDir.resolve(file);
            if (Files.notExists(path)) {
                throw Utils.error("File does not exist.");
            }

            stagedFiles.addFile(path);
        }
        saveToFileSystem();
    }

    /**
     * @return if the gitlet initialized.
     */
    public boolean initialized() {
        return initialized;
    }

    /**
     * Commit current staging area.
     * @param message the commit message.
     */
    public void commit(String message) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        if (message == null || message.isEmpty()) {
            throw Utils.error("Please enter a commit message.");
        }
        if (stagedFiles.same(head.stagedFiles())) {
            throw Utils.error("No changes added to the commit.");
        }

        Commit commit = new Commit(Instant.now(),
                List.of(head), message, stagedFiles);

        commitMap.put(commit.hash(), commit);
        head = commit;
        stagedFiles = new StagedFiles(this.workingDir, this.stagedFiles);
        active.setHead(commit);

        saveToFileSystem();
    }

    /**
     * Remove a file.
     * @param file the file.
     */
    public void rm(Path file) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        Path path = workingDir.resolve(file);
        boolean tracked = head.isTracking(path);
        boolean staged = stagedFiles.isStaged(path);
        if (!tracked && !staged) {
            throw Utils.error("No reason to remove the file.");
        }
        stagedFiles.remove(path);

        if (tracked) {
            IO.deleteIfExists(path);
        }
        saveToFileSystem();
    }

    /**
     * Return the logs.
     * @return the log.
     */
    public List<Commit> log() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        List<Commit> commits = new ArrayList<>();
        Commit current = head;
        while (current != null) {
            commits.add(current);
            List<Commit> parents = current.parents();
            if (parents == null || parents.isEmpty()) {
                break;
            }
            current = parents.get(0);
        }
        return commits;
    }

    /**
     * Save the gitlet to file system.
     */
    public void saveToFileSystem() {
        Path gitletDir = workingDir.resolve(GITLET_DIR);

        IO.writeString(gitletDir.resolve(HEAD_FILE), head.hash());
        IO.writeString(gitletDir.resolve(BRANCH_FILE), active.name());

        Path branchDir = gitletDir.resolve(BRANCH_DIR);
        IO.walk(branchDir)
                .filter(child -> !Files.isDirectory(child))
                .forEach(IO::deleteIfExists);
        for (Branch branch : branches) {
            IO.writeBranch(branchDir.resolve(branch.name()), branch);
        }

        Path commitDir = gitletDir.resolve(COMMIT_DIR);
        for (Commit commit : commitMap.values()) {
            IO.writeCommit(commitDir.resolve(commit.hash()), commit);
        }

        Path remoteDir = gitletDir.resolve(REMOTE_DIR);
        IO.walk(remoteDir)
                .filter(child -> !Files.isDirectory(child))
                .forEach(IO::deleteIfExists);
        for (Map.Entry<String, Path> entry : remotes.entrySet()) {
            String str = entry.getValue().normalize().toString();
            IO.writeString(remoteDir.resolve(entry.getKey()), str);
        }

        IO.writeStaging(gitletDir.resolve(STAGING_FILE), stagedFiles);
    }

    /**
     * Loads the gitlet from file system.
     */
    public void loadFromFileSystem() {
        Path gitletDir = workingDir.resolve(GITLET_DIR);

        head = commitResolver.apply(
                IO.readString(gitletDir.resolve(HEAD_FILE)));
        String activeBranch = IO.readString(gitletDir.resolve(BRANCH_FILE));

        try {
            Path branchDir = gitletDir.resolve(BRANCH_DIR);
            branches = Files.list(branchDir)
                    .map(branchFile ->
                            IO.readBranch(branchFile, commitResolver))
                    .collect(Collectors.toCollection(LinkedList::new));

            for (Branch branch : branches) {
                if (Objects.equals(branch.name(), activeBranch)) {
                    active = branch;
                    break;
                }
            }

            Path commitDir = gitletDir.resolve(COMMIT_DIR);
            commitMap = Files.list(commitDir)
                    .map(commitFile -> IO.readCommit(
                            commitFile, this.workingDir, commitResolver,
                            commitFile.getFileName().toString()))
                    .collect(Collectors.toMap(Commit::hash,
                        c -> c, (c, c2) -> c, HashMap::new));

            Path remoteDir = gitletDir.resolve(REMOTE_DIR);
            remotes = Files.list(remoteDir)
                    .collect(Collectors.toMap(
                        file -> file.getFileName().toString(),
                        file -> Paths.get(IO.readString(file))));

            stagedFiles = IO.readStaging(
                    gitletDir.resolve(STAGING_FILE), this.workingDir);
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * @return The tracked files.
     */
    public Set<Path> trackedFiles() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        return this.stagedFiles.files();
    }

    /**
     * @return The staged files.
     */
    public Set<Path> stagedFiles() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        return this.stagedFiles.difference(head.stagedFiles(), true);
    }

    /**
     * @return The deleted files.
     */
    public Set<Path> deletedFiles() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        return head.stagedFiles().difference(this.stagedFiles, false);
    }

    /**
     * @return The globalized logs.
     */
    public Collection<Commit> globalLog() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        return commitMap.values();
    }

    /**
     * Check out to a branch.
     * @param branchName the branch name.
     */
    public void checkout(String branchName) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        Branch target = null;
        for (Branch branch : branches) {
            if (branch.name().equals(branchName)) {
                target = branch;
                break;
            }
        }
        if (target == null) {
            throw Utils.error("No such branch exists.");
        }
        if (target == active) {
            throw Utils.error("No need to checkout the current branch.");
        }

        checkout(target.head());
        active = target;
        saveToFileSystem();
    }

    /**
     * Checkout to a specific commit.
     * @param target the commit.
     */
    private void checkout(Commit target) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        checkNoUntrackedFiles();

        head = target;
        StagedFiles newStaging = head.stagedFiles();
        this.stagedFiles = new StagedFiles(workingDir, newStaging);

        try {
            Files.walk(workingDir, 1)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path1 -> {
                        try {
                            Files.delete(path1);
                        } catch (IOException e) {
                            throw new GitletException(e);
                        }
                    });
        } catch (IOException e) {
            throw new GitletException(e);
        }

        Set<Path> files = newStaging.files();
        for (Path file : files) {
            Path originFile = newStaging.getFile(file);
            IO.copyFile(originFile, file);
        }
    }

    /**
     * Checks the directory contains no untracked files.
     */
    private void checkNoUntrackedFiles() {
        if (!untrackedFiles().isEmpty()) {
            throw Utils.error("There is an untracked file in the way; "
                    + "delete it or add it first.");
        }
    }

    /**
     * Finds the directory's untracked files.
     * @return untracked files.
     */
    public List<Path> untrackedFiles() {
        try {
            Set<Path> trackedFiles = trackedFiles();
            return Files
                    .walk(workingDir, 1)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> !trackedFiles.contains(path))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new GitletException(e);
        }
    }

    /**
     * @return all tracking files' state.
     */
    public Map<Path, FileState> getFileStates() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        Map<Path, FileState> fileStates = new TreeMap<>();
        for (Path file : this.stagedFiles.files()) {
            Path origin = this.stagedFiles.getFile(file);
            if (!Files.exists(file)) {
                fileStates.put(file, FileState.DELETED);
            } else if (IO.different(file, origin)) {
                fileStates.put(file, FileState.MODIFIED);
            } else {
                fileStates.put(file, FileState.UNMODIFIED);
            }
        }
        return fileStates;
    }

    /**
     * Check out a file to a commit.
     *
     * @param commitHash the commit hash.
     * @param file the file.
     */
    public void checkout(String commitHash, Path file) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        file = this.workingDir.resolve(file);
        Commit commit = findCommit(commitHash);
        if (!commit.isTracking(file)) {
            throw Utils.error("File does not exist in that commit.");
        }
        Path trackedFile = commit.stagedFiles().getFile(file);
        IO.copyFile(trackedFile, file);
    }

    /**
     * Restore a file to head.
     * @param file the file.
     */
    public void restore(Path file) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        checkout(head.hash(), file);
    }

    /**
     * Find a set of commits.
     * @param message the commit message.
     * @return the commits found.
     */
    public List<Commit> find(String message) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        List<Commit> result = new ArrayList<>();
        for (Commit commit : commitMap.values()) {
            if (commit.message().equals(message)) {
                result.add(commit);
            }
        }
        return result;
    }

    /**
     * @return all branches.
     */
    public List<Branch> branches() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        return branches;
    }

    /**
     * @return the current active branch.
     */
    public Branch activeBranch() {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        return active;
    }

    /**
     * Create a new branch.
     * @param name the branch name.
     */
    public void newBranch(String name) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        if (name == null || name.isEmpty()) {
            throw Utils.error("Please enter a branch name.");
        }

        Branch existed = findBranch(name);
        if (existed != null) {
            throw Utils.error("A branch with that name already exists.");
        }

        Branch newBranch = new Branch(head, name);
        branches.add(newBranch);
        saveToFileSystem();
    }

    /**
     * Delete a new branch.
     * @param name the branch name.
     */
    public void deleteBranch(String name) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        if (name == null || name.isEmpty()) {
            throw Utils.error("Please enter a branch name.");
        }

        Branch branch = findBranch(name);
        if (branch == null) {
            throw Utils.error("A branch with that name does not exist.");
        }
        if (branch == active) {
            throw Utils.error("Cannot remove the current branch.");
        }
        branches.remove(branch);
        saveToFileSystem();
    }

    /**
     * Find a branch by its name.
     * @param name the branch name.
     * @return the branch.
     */
    private Branch findBranch(String name) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        for (Branch branch : branches) {
            if (branch.name().equals(name)) {
                return branch;
            }
        }
        return null;
    }

    /**
     * Find a commit by its hash.
     * @param hash the hash (can be a abbr).
     * @return the commit.
     */
    private Commit findCommit(String hash) {
        if (hash.length() == Utils.UID_LENGTH) {
            Commit commit = commitMap.get(hash);
            if (commit != null) {
                return commit;
            } else {
                throw Utils.error("No commit with that id exists.");
            }
        }

        for (Map.Entry<String, Commit> entry : commitMap.entrySet()) {
            if (entry.getKey().startsWith(hash)) {
                return entry.getValue();
            }
        }
        throw Utils.error("No commit with that id exists.");
    }

    /**
     * reset.
     * @param hash the commit hash.
     */
    public void reset(String hash) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        Commit commit = findCommit(hash);
        checkout(commit);
        active.setHead(commit);
        saveToFileSystem();
    }

    /**
     * merge.
     * @param branchName the target branch.
     * @return whether conflicts.
     */
    public boolean merge(String branchName) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        Branch target = findBranch(branchName);
        if (target == null) {
            throw Utils.error("A branch with that name does not exist.");
        }
        if (!stagedFiles().isEmpty() || !deletedFiles().isEmpty()) {
            throw Utils.error("You have uncommitted changes.");
        }
        if (target == active) {
            throw Utils.error("Cannot merge a branch with itself.");
        }
        checkNoUntrackedFiles();

        Commit splitPoint = latestCommonAncestor(active, target);
        if (splitPoint == target.head()) {
            throw Utils.error("Given branch is an "
                + "ancestor of the current branch.");
        }

        if (splitPoint == active.head()) {
            active.setHead(target.head());
            head = active.head();
            checkout(target.head());
            saveToFileSystem();
            throw Utils.error("Current branch fast-forwarded.");
        }

        boolean conflict = doMerge(splitPoint, active, target);
        String message = String.format("Merged %s into %s.",
                target.name(), active.name());
        Commit commit = new Commit(Instant.now(),
                List.of(head, target.head()), message, stagedFiles);

        commitMap.put(commit.hash(), commit);
        head = commit;
        stagedFiles = new StagedFiles(this.workingDir, this.stagedFiles);
        active.setHead(commit);

        saveToFileSystem();
        return conflict;
    }

    /**
     * Do merge of two branches.
     * @param splitPoint the split point.
     * @param current the current branch.
     * @param target the target branch.
     * @return whether conflicts.
     */
    private boolean doMerge(Commit splitPoint,
                            Branch current, Branch target) {
        Set<Path> allFiles = new HashSet<>();
        StagedFiles originFiles = splitPoint.stagedFiles();
        StagedFiles currentFiles = current.head().stagedFiles();
        StagedFiles targetFiles = target.head().stagedFiles();
        allFiles.addAll(originFiles.files());
        allFiles.addAll(currentFiles.files());
        allFiles.addAll(targetFiles.files());
        boolean conflict = false;

        for (Path file : allFiles) {
            String originHash = originFiles.getHash(file);
            String targetHash = targetFiles.getHash(file);
            String currentHash = currentFiles.getHash(file);

            if (!Objects.equals(targetHash, originHash)
                || !Objects.equals(currentHash, originHash)) {
                if (!Objects.equals(targetHash, originHash)) {
                    if (Objects.equals(currentHash, originHash)) {
                        if (targetHash == null) {
                            IO.deleteIfExists(file);
                            stagedFiles.remove(file);
                        } else {
                            Path targetFile = targetFiles.getFile(file);
                            IO.copyFile(targetFile, file);
                            stagedFiles.addFile(file);
                        }
                    } else if (!Objects.equals(currentHash, targetHash)) {
                        String currentContent = IO.readString(file);
                        String targetContent =
                                IO.readString(targetFiles.getFile(file));
                        IO.writeString(file, String.format(""
                                + "<<<<<<< HEAD%n"
                                + currentContent
                                + "=======%n"
                                + targetContent
                                + ">>>>>>>%n"));
                        stagedFiles.addFile(file);
                        conflict = true;
                    }
                }
            }
        }
        return conflict;
    }

    /**
     * Find ancestors for a commit.
     * @param commit the commit.
     * @param processor the processor.
     * @param <T> result type.
     * @return the result.
     */
    private <T> T findAncestors(Commit commit,
                                Function<Commit, T> processor) {
        Queue<Commit> commitQueue = new LinkedList<>();
        commitQueue.offer(commit);
        while (!commitQueue.isEmpty()) {
            Commit pointer = commitQueue.poll();
            T found = processor.apply(pointer);
            if (found != null) {
                return found;
            }
            if (pointer.parents() != null && !pointer.parents().isEmpty()) {
                commitQueue.addAll(pointer.parents());
            }
        }
        return null;
    }

    /**
     * Finds the latest common ancestor of two branches.
     * @param current the current branch.
     * @param target the target branch.
     * @return the ancestor commit.
     */
    private Commit latestCommonAncestor(Branch current, Branch target) {
        Set<String> ancestors = new HashSet<>();
        findAncestors(target.head(),
            (commit) -> {
                ancestors.add(commit.hash());
                return null;
            });

        Commit result = findAncestors(current.head(),
            (commit) -> ancestors.contains(commit.hash()) ? commit : null);
        if (result == null) {
            throw new IllegalStateException("Branches "
                    + "contain no common ancestor.");
        }
        return result;
    }

    /**
     * Adds a remote.
     * @param name remote's name.
     * @param path remote's path.
     */
    public void addRemote(String name, String path) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        if (remotes.containsKey(name)) {
            throw Utils.error("A remote with that name already exists.");
        }

        String normalized = path.replace('/', File.separatorChar);
        Path remotePath = Paths.get(normalized);
        remotes.put(name, remotePath);
        saveToFileSystem();
    }

    /**
     * @param operand remote's name.
     */
    public void deleteRemote(String operand) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }

        if (remotes.containsKey(operand)) {
            remotes.remove(operand);
        } else {
            throw Utils.error("A remote with that name does not exist.");
        }
        saveToFileSystem();
    }

    /**
     * @param operand remote's name.
     */
    public void fetch(String operand) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        Path remote = remotes.get(operand);
        if (remote == null) {
            throw Utils.error("A remote with that name does not exist.");
        }
        if (!Files.exists(remote.resolve(GITLET_DIR))) {
            throw Utils.error("Remote directory not found.");
        } else {
            throw Utils.error("That remote does not have that branch.");
        }
    }

    /**
     * @param name remote's name.
     */
    public void push(String name) {
        if (!initialized) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        Path remote = remotes.get(name);
        if (remote == null) {
            throw Utils.error("A remote with that name does not exist.");
        }
        if (!Files.exists(remote.resolve(GITLET_DIR))) {
            throw Utils.error("Remote directory not found.");
        } else {
            throw Utils.error("Please pull down "
                    + "remote changes before pushing.");
        }
    }
}
