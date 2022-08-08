package gitlet;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Charlie Zhou
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        try {
            if (args.length == 0) {
                throw Utils.error("Please enter a command.");
            }

            String command = args[0];
            String[] operands = Arrays.copyOfRange(args, 1, args.length);
            Locale.setDefault(Locale.ENGLISH);
            process(command, operands);
        } catch (GitletException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Process commands.
     *
     * @param command the command.
     * @param operands the operands.
     */
    private static void process(String command, String[] operands) {
        Gitlet git = new Gitlet(Paths.get(".").toAbsolutePath().normalize());
        if ("init".equals(command)) {
            init(git, operands);
        } else if ("add".equals(command)) {
            add(git, operands);
        } else if ("commit".equals(command)) {
            commit(git, operands);
        } else if ("rm".equals(command)) {
            rm(git, operands);
        } else if ("log".equals(command)) {
            log(git, operands);
        } else if ("global-log".equals(command)) {
            globalLog(git, operands);
        } else if ("find".equals(command)) {
            find(git, operands);
        } else if ("status".equals(command)) {
            status(git, operands);
        } else if ("checkout".equals(command)) {
            checkout(git, operands);
        } else {
            handleRest(command, operands, git);
        }
    }

    /**
     * Since a method can have maximun 60 lines long, split it up.
     * @param command command.
     * @param operands operands.
     * @param git git.
     */
    private static void handleRest(String command,
                                   String[] operands, Gitlet git) {
        if ("branch".equals(command)) {
            branch(git, operands);
        } else if ("rm-branch".equals(command)) {
            rmBranch(git, operands);
        } else if ("reset".equals(command)) {
            reset(git, operands);
        } else if ("merge".equals(command)) {
            merge(git, operands);
        } else if ("add-remote".equals(command)) {
            addRemote(git, operands);
        } else if ("rm-remote".equals(command)) {
            rmRemote(git, operands);
        } else if ("push".equals(command)) {
            push(git, operands);
        } else if ("fetch".equals(command)) {
            fetch(git, operands);
        } else {
            throw Utils.error("No command with that name exists.");
        }
    }


    /**
     * fetch.
     * @param gitlet gitlet.
     * @param operands opreands.
     */
    private static void fetch(Gitlet gitlet, String[] operands) {
        if (operands.length != 2) {
            throw Utils.error("Incorrect operands.");
        }

        gitlet.fetch(operands[0]);
    }

    /**
     * push.
     * @param gitlet gitlet.
     * @param operands operands.
     */
    private static void push(Gitlet gitlet, String[] operands) {
        if (operands.length != 2) {
            throw Utils.error("Incorrect operands.");
        }

        gitlet.push(operands[0]);
    }

    /**
     * rm-remote.
     * @param gitlet gitlet.
     * @param operands operands.
     */
    private static void rmRemote(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }

        gitlet.deleteRemote(operands[0]);
    }

    /**
     * add-remote.
     * @param gitlet gitlet.
     * @param operands operands.
     */
    private static void addRemote(Gitlet gitlet, String[] operands) {
        if (operands.length != 2) {
            throw Utils.error("Incorrect operands.");
        }

        gitlet.addRemote(operands[0], operands[1]);
    }

    /**
     * merge.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void merge(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }

        boolean conflict = gitlet.merge(operands[0]);
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * reset.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void reset(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }

        gitlet.reset(operands[0]);
    }

    /**
     * rm-branch.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void rmBranch(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }

        gitlet.deleteBranch(operands[0]);
    }

    /**
     * branch.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void branch(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }

        gitlet.newBranch(operands[0]);
    }

    /**
     * status.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void status(Gitlet gitlet, String[] operands) {
        if (operands.length != 0) {
            throw Utils.error("Incorrect operands.");
        }

        List<Branch> branches = gitlet.branches();
        branches.sort(Comparator.comparing(Branch::name));
        Branch active = gitlet.activeBranch();
        Set<Path> stagedFiles = gitlet.stagedFiles();
        List<String> stagedFileNames = stagedFiles.stream()
                .map(path -> path.getFileName().toString())
                .sorted().collect(Collectors.toList());
        Set<Path> deletedFiles = gitlet.deletedFiles();
        List<String> deletedFileNames = deletedFiles.stream()
                .map(path -> path.getFileName().toString())
                .sorted().collect(Collectors.toList());

        System.out.println("=== Branches ===");
        for (Branch branch : branches) {
            if (branch == active) {
                System.out.print("*");
            }
            System.out.println(branch.name());
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String fileName : stagedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName : deletedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        Map<Path, FileState> fileStates = gitlet.getFileStates();
        for (Map.Entry<Path, FileState> entry : fileStates.entrySet()) {
            if (entry.getValue() == FileState.MODIFIED) {
                System.out.print(entry.getKey().getFileName().toString());
                System.out.println(" (modified)");
            } else if (entry.getValue() == FileState.DELETED) {
                System.out.print(entry.getKey().getFileName().toString());
                System.out.println(" (deleted)");
            }
        }

        System.out.println();
        System.out.println("=== Untracked Files ===");
        List<Path> untracked = gitlet.untrackedFiles();
        for (Path file : untracked) {
            System.out.println(file.getFileName().toString());
        }
        System.out.println();
    }

    /**
     * Find.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void find(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }
        String message = operands[0];
        List<Commit> commits = gitlet.find(message);
        if (commits == null || commits.isEmpty()) {
            System.err.println("Found no commit with that message.");
        } else {
            for (Commit commit : commits) {
                System.out.println(commit.hash());
            }
        }
    }

    /**
     * Checkout.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void checkout(Gitlet gitlet, String[] operands) {
        if (operands.length == 0) {
            throw Utils.error("Incorrect operands.");
        } else if (operands.length == 1) {
            gitlet.checkout(operands[0]);
        } else if (operands.length == 2) {
            if (!operands[0].equals("--")) {
                throw Utils.error("Incorrect operands.");
            }
            gitlet.restore(Paths.get(operands[1]));
        } else if (operands.length == 3) {
            if (!operands[1].equals("--")) {
                throw Utils.error("Incorrect operands.");
            }
            gitlet.checkout(operands[0], Paths.get(operands[2]));
        } else {
            throw Utils.error("Incorrect operands.");
        }
    }

    /**
     * Global log.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void globalLog(Gitlet gitlet, String[] operands) {
        if (operands.length != 0) {
            throw Utils.error("Incorrect operands.");
        }
        Collection<Commit> logs = gitlet.globalLog();
        String message = logs.stream()
                .map(Commit::print)
                .collect(Collectors.joining(String.format("%n")));
        System.out.println(message);
    }

    /**
     * Log.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void log(Gitlet gitlet, String[] operands) {
        if (operands.length != 0) {
            throw Utils.error("Incorrect operands.");
        }
        List<Commit> logs = gitlet.log();
        String message = logs.stream()
                .map(Commit::print)
                .collect(Collectors.joining(String.format("%n")));
        System.out.println(message);
    }

    /**
     * Rm.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void rm(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }
        gitlet.rm(Paths.get(operands[0]));
    }

    /**
     * Commit.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void commit(Gitlet gitlet, String[] operands) {
        if (operands.length != 1) {
            throw Utils.error("Incorrect operands.");
        }
        gitlet.commit(operands[0]);
    }

    /**
     * Add.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void add(Gitlet gitlet, String[] operands) {
        if (operands.length == 0) {
            throw Utils.error("Incorrect operands.");
        }
        gitlet.add(
                Arrays.stream(operands)
                        .map(op -> Paths.get(op))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Init.
     * @param gitlet the gitlet.
     * @param operands the operands.
     */
    private static void init(Gitlet gitlet, String[] operands) {
        if (operands.length > 0) {
            throw Utils.error("Incorrect operands.");
        }
        gitlet.init();
    }


}
