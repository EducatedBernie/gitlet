package gitlet;

import java.io.File;
import java.util.HashSet;

/**
 * @author Bernie
 */
public class History {

    /**
     * Do I need to explain. No.
     */
    private HashSet<String> history;

    /**
     * no.
     *
     * @param commit yup
     * @return
     */
    public HashSet<String> findHistory(Commit commit) {
        history = new HashSet<>();
        traverse(Commit.computeHash(commit));
        return history;
    }

    /**
     * no.
     *
     * @param commit yup
     * @param commitFolder s
     * @return
     */
    public HashSet<String> findHistoryRm(Commit commit, File commitFolder) {
        history = new HashSet<>();
        traverseRm(Commit.computeHash(commit), commitFolder);
        return history;
    }

    /**
     * fuckno.
     * @return asd
     */
    public HashSet<String> getHistory() {
        return history;
    }

    /**
     * The helper.
     * @param commitHash ss
     */
    public void traverse(String commitHash) {
        Commit curr;
        Commit firstParent;
        Commit secondParent;
        if (commitHash != null) {
            curr = Commit.findCommit(commitHash);
            if (curr.getParent() != null) {
                firstParent = Commit.findCommit(curr.getParent());
                traverse(curr.getParent());
            }
            history.add(commitHash);
            if (curr.getSecondParent() != null) {
                secondParent = Commit.findCommit(curr.getSecondParent());
                traverse(curr.getSecondParent());
            }
        }
    }

    /**
     * The helper.
     * @param commitHash s
     * @param commitFolder s
     */
    public void traverseRm(String commitHash, File commitFolder) {
        Commit curr;
        if (commitHash != null) {
            curr = Repository.rmFindCommit(commitHash, commitFolder);
            if (curr.getParent() != null) {
                traverseRm(curr.getParent(), commitFolder);
            }
            history.add(commitHash);
            if (curr.getSecondParent() != null) {
                traverseRm(curr.getSecondParent(), commitFolder);
            }
        }
    }

}
