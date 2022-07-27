package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;


/**
 * IDK.
 * @author bernie
 */
public class Commit implements Serializable {

    /**
     * IDK.
     */
    private String message;
    /**
     * IDK.
     */
    private Date timestamp;
    /**
     * IDK.
     */
    private String parent;
    /**
     * IDK.
     */
    private String secondParent;
    /**
     * IDK.
     */
    private TreeMap<String, String> blobs;

    /**
     * yes.
     *
     * @param msg e
     * @param parento  e
     */
    public Commit(String msg, String parento) {
        this.message = msg;
        this.parent = parento;
        this.secondParent = null;
        this.blobs = new TreeMap<>();
        if (this.parent == null) {
            timestamp = new Date(0);
        } else {
            timestamp = new Date();

        }
    }

    /**
     * Accessor for parent.
     *
     * @return e
     */
    public String getMessage() {
        return message;
    }

    /**
     * Acesssor for parent.
     *
     * @return e
     */
    public String getParent() {
        return parent;
    }

    /**
     * @param filename Want to see if commit contains this.
     * @return truthValue
     */
    public Boolean tracks(String filename) {
        return blobs.containsKey(filename);
    }

    /**
     * @param hash Want to see if commit is tracking this hash.
     * @return truth value
     */
    public Boolean containsHash(String hash) {
        return blobs.containsValue(hash);
    }

    /**
     * yes.
     *
     * @param hash asdf
     * @return
     */
    public static Commit findCommit(String hash) {
        File commitPath = Utils.join(Repository.COMMITS_FOLDER, hash);
        if (commitPath.exists()) {
            return Utils.readObject(commitPath, Commit.class);
        }
        List<String> allCommits
                = Utils.plainFilenamesIn(Repository.COMMITS_FOLDER);
        for (String filenames : allCommits) {
            commitPath = Utils.join(Repository.COMMITS_FOLDER, filenames);
            if (filenames.contains(hash)) {
                return Utils.readObject(commitPath, Commit.class);
            }
        }
        Main.exitWithError("No commit with that id exists.");
        return null;
    }

    /**
     * yes.
     * @param secondParento asdf
     */
    public void setSecondParent(String secondParento) {
        this.secondParent = secondParento;
    }


    /**
     * yes.
     *
     * @return asdf
     */
    public String getSecondParent() {
        return secondParent;
    }

    /**
     * yes.
     *
     * @param str asdf
     */
    public void setParent(String str) {
        this.parent = str;
    }

    /**
     * yes.
     */
    public void saveCommit() {
        File storagePath = Utils.join(Repository.COMMITS_FOLDER,
                Commit.computeHash(this));
        Utils.writeObject(storagePath, this);
    }

    /**
     * yes.
     *
     * @return sdf
     */
    public String getTimestamp() {
        SimpleDateFormat l = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return l.format(timestamp);
    }

    /**
     * yes.
     *
     * @param str s
     */
    public void setMessage(String str) {
        this.message = str;
    }

    /**
     * yes.
     *
     * @return s
     */
    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    /**
     * yes.
     *
     * @param str s
     */
    public void setBlobs(TreeMap<String, String> str) {
        this.blobs = str;
    }

    /**
     * yes.
     *
     * @param filename e
     * @return s
     */
    public String getBlobHash(String filename) {
        return blobs.get(filename);
    }

    @Override
    public String toString() {
        String str = "";
        str += "===";
        str += "\ncommit " + Commit.computeHash(this);
        if (secondParent != null) {
            str += "\nMerge: " + getParent().substring(0, 7)
                    + " " + getSecondParent().substring(0, 7);
        }
        str += "\nDate: " + getTimestamp();
        str += "\n" + getMessage() + "\n";
        return str;
    }

    /**
     * yes.
     *
     * @return
     */
    public Date getDate() {
        return timestamp;
    }

    /**
     * COMPUTES A HASH BASE.
     *
     * @param commit yes
     * @return
     */
    public static String computeHash(Commit commit) {
        return Utils.sha1(Utils.serialize(commit));
    }

}
