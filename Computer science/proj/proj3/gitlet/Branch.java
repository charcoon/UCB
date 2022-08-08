package gitlet;

import java.io.Serializable;

/**
 * Represents a branch.
 * @author Charlie Zhou
 */
public class Branch implements Serializable {
    private static final long serialVersionUID = 6842327900457338779L;

    /**
     * The head.
     */
    private Commit head;
    /**
     * The branch name.
     */
    private String name;

    /**
     * Creates a branch.
     * @param head0 the head.
     * @param name0 the name.
     */
    public Branch(Commit head0, String name0) {
        this.head = head0;
        this.name = name0;
    }

    /**
     * @return the head.
     */
    public Commit head() {
        return head;
    }

    /**
     * @return the name.
     */
    public String name() {
        return name;
    }

    /**
     * @param commit the new commit.
     */
    public void setHead(Commit commit) {
        this.head = commit;
    }
}
