package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * No unused imports here.
 *
 * @author bernie
 */
public class Repository implements Serializable {
    /**
     * asd.
     */
    private StagingArea stage = new StagingArea();
    /**
     * asd.
     */
    private String headCommitID = "";
    /**
     * asd.
     */
    private String activeBranch = "";
    /**
     * asd.
     */
    private TreeMap<String, String> branches = new TreeMap<>();

    /**
     * asd.
     */
    private static File cwd = new File(".");
    /**
     * asd.
     */
    static final File GITLET_FOLDER = Utils.join(cwd, ".gitlet");
    /**
     * asd.f.
     */
    static final File BRANCHES_FILE
            = Utils.join(GITLET_FOLDER, "branches");
    /**
     * asd.
     */
    static final File ACTIVE_BRANCH_FILE
            = Utils.join(GITLET_FOLDER, "active.txt");
    /**
     * asd.
     */
    static final File HEAD_COMMIT_FILE
            = Utils.join(GITLET_FOLDER, "headcommit");

    /**
     * asd.
     */
    static final File REMOTE_FOLDER
            = Utils.join(GITLET_FOLDER, "remote");
    /**
     * asd.
     */
    static final File COMMITS_FOLDER = Utils.join(GITLET_FOLDER, "commits");
    /**
     * asd.
     */
    static final File STAGING_AREA_FILE
            = Utils.join(GITLET_FOLDER, "stage");
    /**
     * asd.
     */
    static final File REPO_FILE = Utils.join(GITLET_FOLDER, "repo");
    /**
     * asd.
     */
    static final File BLOBS_FOLDER = Utils.join(GITLET_FOLDER, "blobs");
    /**
     * asd.
     */
    static final File GLOBAL_LOG_FILE
            = Utils.join(GITLET_FOLDER, "log.txt");

    /**
     * Hashtable for keeping track of remotes.
     */

    static final File REMOTE_REPO_FILE
            = Utils.join(GITLET_FOLDER, "remote.txt");

    /**
     * asd.
     */
    public static void setupPersistence() {
        try {
            GITLET_FOLDER.mkdir();
            COMMITS_FOLDER.mkdir();
            BLOBS_FOLDER.mkdir();
            BRANCHES_FILE.createNewFile();
            ACTIVE_BRANCH_FILE.createNewFile();
            HEAD_COMMIT_FILE.createNewFile();
            STAGING_AREA_FILE.createNewFile();
            REPO_FILE.createNewFile();
            GLOBAL_LOG_FILE.createNewFile();
            REMOTE_FOLDER.mkdir();
        } catch (IOException exception) {
            Main.exitWithError("Handling exception");
        }
    }

    /**
     * Check for the existence of head commit file,
     * staging area, and branches file.
     *
     * @return Whether or not makedir has been run
     */
    public boolean initCheck() {
        if (!HEAD_COMMIT_FILE.exists() || !STAGING_AREA_FILE.exists()
                || !BRANCHES_FILE.exists() || !GITLET_FOLDER.exists()) {

            return false;
        }
        if (Utils.readContentsAsString(HEAD_COMMIT_FILE).equals("")) {

            return false;
        }
        return true;
    }

    /**
     * Serializes only the related information.
     */
    public void saveRepo() {
        if (!initCheck()) {
            throw new GitletException("Cannot update an uninitialized repo");
        }
        stage.saveStage();
        Utils.writeContents(HEAD_COMMIT_FILE, headCommitID);
        Utils.writeObject(BRANCHES_FILE, branches);
        Utils.writeContents(ACTIVE_BRANCH_FILE, activeBranch);
    }

    /**
     * s.
     */
    public void updateRepo() {
        if (!initCheck()) {
            throw new GitletException("Cannot update an uninitialized repo");
        }
        stage.updateStage();
        this.headCommitID = Utils.readContentsAsString(HEAD_COMMIT_FILE);
        this.branches = Utils.readObject(BRANCHES_FILE, TreeMap.class);
        this.activeBranch = Utils.readContentsAsString(ACTIVE_BRANCH_FILE);
    }

    /**
     * Description: Creates a new Gitlet version-control system in th
     * e current directory.
     * This system will automatically start with one commit: a commit
     * that contains no
     * files and has the commit message initial commit (just like that
     * with no punctuation).
     * It will have a single branch: master, which initially points
     * to this initial commit,
     * and master will be the current branch. The timestamp for this initial
     * commit will be 00:00:00 UTC, Thursday, 1 January 1970 in whatever format
     * you choose for dates (this is called "The (Unix) Epoch", represented
     * system already exists in the current directory.
     */
    public void init() {
        setupPersistence();
        stage = new StagingArea();
        stage.saveStage();

        Commit initialCommit = new Commit("initial commit", null);
        byte[] serializedInitialCommit = Utils.serialize(initialCommit);

        headCommitID = Utils.sha1(serializedInitialCommit);
        Utils.writeContents(HEAD_COMMIT_FILE, headCommitID);

        setActiveBranch("master");

        setActiveBranchHead(initialCommit);
        Utils.writeObject(BRANCHES_FILE, branches);

        File initialCommitPath = Utils.join(COMMITS_FOLDER, headCommitID);
        Utils.writeObject(initialCommitPath, initialCommit);
        updateGlobalLog(initialCommit);



    }

    /**
     * asd.
     * @return
     */
    public TreeMap<String, String> getBranches() {
        return branches;
    }

    /**
     * asd.
     * @param directory s
     */
    public void init(String directory) {
        setupPersistence();
        stage = new StagingArea();
        stage.saveStage();

        Commit initialCommit = new Commit("initial commit", null);
        byte[] serializedInitialCommit = Utils.serialize(initialCommit);

        headCommitID = Utils.sha1(serializedInitialCommit);
        Utils.writeContents(HEAD_COMMIT_FILE, headCommitID);

        setActiveBranch("master");

        setActiveBranchHead(initialCommit);
        Utils.writeObject(BRANCHES_FILE, branches);

        File initialCommitPath = Utils.join(COMMITS_FOLDER, headCommitID);
        Utils.writeObject(initialCommitPath, initialCommit);
        updateGlobalLog(initialCommit);

    }


    /**
     * s.
     *
     * @param theActiveBranch s
     */
    public void setActiveBranch(String theActiveBranch) {
        this.activeBranch = theActiveBranch;
    }

    /**
     * Description: Adds a copy of the file as it currently
     * exists to the staging area FILENAME.
     */
    public void add(String filename) {
        updateRepo();
        File toBeAdded = Utils.join(cwd, filename);
        if (!toBeAdded.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String fileContents = Utils.readContentsAsString(toBeAdded);
        String sha1 = Utils.sha1(filename + fileContents);

        stage.flagForAddition(filename, sha1);
        if (Commit.findCommit(headCommitID).containsHash(sha1)) {

            stage.getToAdd().remove(filename);
        }

        File hashedBlob = Utils.join(BLOBS_FOLDER, sha1);
        Utils.writeContents(hashedBlob, fileContents);
        stage.unstageForRemoval(filename);
        saveRepo();
    }

    /**
     * asd.
     *
     * @param message je
     */
    public void commit(String message) {
        updateRepo();
        if (stage.getToAdd().isEmpty() && stage.getToRemove().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        Commit currCommit = Commit.findCommit(headCommitID);
        TreeMap<String, String> clonedBlobs
                = new TreeMap<>(currCommit.getBlobs());

        ArrayList<String> addThis = new ArrayList(stage.getToAdd().keySet());
        ArrayList<String> removeThat = new ArrayList<>(stage.getToRemove());
        for (String name : addThis) {
            clonedBlobs.put(name, stage.getToAdd().get(name));
        }
        for (String name : removeThat) {
            clonedBlobs.remove(name);
        }
        Commit newCommit = new Commit(message, Commit.computeHash(currCommit));
        newCommit.setBlobs(clonedBlobs);

        setActiveBranchHead(newCommit);
        newCommit.saveCommit();

        updateGlobalLog(newCommit);
        stage.clear();
        saveRepo();
    }

    /**
     * asd.
     *
     * @param branchname s
     */
    public void mergeCommit(String branchname) {
        if (stage.getToAdd().isEmpty() && stage.getToRemove().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit currCommit = Commit.findCommit(headCommitID);
        TreeMap<String, String> clonedBlobs
                = new TreeMap<>(currCommit.getBlobs());

        ArrayList<String> addThis = new ArrayList(stage.getToAdd().keySet());
        ArrayList<String> removeThat = new ArrayList<>(stage.getToRemove());
        for (String name : addThis) {
            clonedBlobs.put(name, stage.getToAdd().get(name));
        }
        for (String name : removeThat) {
            clonedBlobs.remove(name);
        }
        Commit newCommit = new Commit("Merged " + branchname
                + " into " + getActiveBranch()
                + ".", Commit.computeHash(currCommit));

        newCommit.setSecondParent(branches.get(branchname));
        newCommit.setBlobs(clonedBlobs);

        setActiveBranchHead(newCommit);
        newCommit.saveCommit();

        updateGlobalLog(newCommit);
        stage.clear();
        saveRepo();
    }

    /**
     * asd.
     *
     * @param filename s
     * @return
     */
    public String retrieveCWDBlob(String filename) {
        File path = Utils.join(Repository.cwd, filename);
        if (!path.exists()) {
            return null;
        }
        return Utils.readContentsAsString(path);
    }

    /**
     * asd.
     *
     * @param sha1 s
     * @return
     */
    public String retrieveBlobContents(String sha1) {
        File path = Utils.join(BLOBS_FOLDER, sha1);
        if (!path.exists()) {
            throw new GitletException("Blob doesn't exist!");
        }
        return Utils.readContentsAsString(path);
    }

    /**
     * sd.
     *
     * @param filename s
     */
    public void checkoutRecent(String filename) {
        checkoutCommit(headCommitID, filename);
    }

    /**
     * s.
     *
     * @param commitID s
     * @param filename s
     */
    public void checkoutCommit(String commitID, String filename) {
        Commit specifiedCommit = Commit.findCommit(commitID);
        if (!specifiedCommit.tracks(filename)) {
            Main.exitWithError("File does not exist in that commit.");
        }
        TreeMap<String, String> commitedBlobs = specifiedCommit.getBlobs();

        File thisFile = Utils.join(cwd, filename);
        String sha1 = commitedBlobs.get(filename);
        String overWrite = retrieveBlobContents(sha1);

        if (thisFile.exists()) {
            Utils.writeContents(thisFile, overWrite);
        } else {
            try {
                thisFile.createNewFile();
                Utils.writeContents(thisFile, overWrite);
            } catch (IOException e) {
                Main.exitWithError("Handling exception");
            }
        }
    }


    /**
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions of
     * the files that are already there if they exist. Also, at the end of
     * this command, the given branch will now be considered the current
     * branch (HEAD). Any files that are tracked in the current branch but
     * are not present in the checked-out branch are deleted. The staging area
     * is cleared, unless the checked-out branch is the current branch
     * (see Failure cases below). BRANCHNAME
     */
    public void checkoutBranch(String branchname) {
        if (!branches.containsKey(branchname)) {
            Main.exitWithError("No such branch exists.");
        }
        if (branchname.equals(getActiveBranch())) {
            Main.exitWithError("No need to checkout the current branch.");
        }
        setActiveBranch(branchname);
        reset(branches.get(branchname));
    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch's head to that commit node.
     * See the intro for an example of what happens to the head pointer
     * after using reset. The [commit id] may be abbreviated as for checkout.
     * The staging area is cleared. The command is essentially checkout of
     * an arbitrary commit that also changes the current branch head.
     * <p>
     * If no commit with the given id exists, print No commit with that
     * id exists. If a working file is untracked in the current branch
     * and would be overwritten by the reset, print There is an untracked
     * file in the way; delete it, or add and commit it first. and exit;
     * perform this check before doing anything else.
     *
     * @param commitID f
     */
    public void reset(String commitID) {
        untrackedFileError(commitID);
        Commit incomingCommit = Commit.findCommit(commitID);
        Commit currCommit = Commit.findCommit(headCommitID);
        ArrayList<String> trackedByIncoming
                = new ArrayList(incomingCommit.getBlobs().keySet());
        ArrayList<String> trackedByCurr
                = new ArrayList(currCommit.getBlobs().keySet());


        for (String filename : trackedByIncoming) {
            checkoutCommit(commitID, filename);
        }

        for (String filename : trackedByCurr) {
            if (!incomingCommit.tracks(filename)) {
                if (filename.contains(".txt")) {
                    Utils.restrictedDelete(filename);
                }
            }
        }
        stage.clear();
        setActiveBranchHead(incomingCommit);
    }

    /**
     * asd.
     *
     * @param commitID asd
     */
    public void untrackedFileError(String commitID) {
        List<String> cwdfiles = Utils.plainFilenamesIn(cwd);
        Commit incomingCommit = Commit.findCommit(commitID);
        Commit currCommit = Commit.findCommit(headCommitID);
        assert cwdfiles != null;
        for (String filename : cwdfiles) {
            if (!currCommit.tracks(filename)
                    && incomingCommit.tracks(filename)) {
                System.out.println("There is an untracked "
                        + "file in the way; delete it,"
                        + " or add and commit it first.");
                System.exit(0);
            }
        }
    }

    /**
     * In the active branch, put the current active branch paired with.
     *
     * @param commit s
     */
    public void setActiveBranchHead(Commit commit) {
        branches.put(getActiveBranch(), Commit.computeHash(commit));
        headCommitID = Commit.computeHash(commit);
    }

    /**
     * asd.
     *
     * @return
     */
    public String getActiveBranch() {
        String str = activeBranch;
        if (str.equals("")) {
            Main.exitWithError("Error: Active branch isn't initialized.");
        }
        return activeBranch;
    }

    /**
     * asds.
     *
     * @param filename s
     */
    public void rm(String filename) {
        stage.updateStage();
        File toBeRemoved = Utils.join(cwd, filename);
        if (!stage.getToAdd().containsKey(filename)
                && !Commit.findCommit(headCommitID).tracks(filename)) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (stage.getToAdd().containsKey(filename)) {
            stage.unstageForAddition(filename);
        }

        if (Commit.findCommit(headCommitID).tracks(filename)) {
            stage.flagForRemoval(filename);
            if (filename.contains(".txt")) {
                Utils.restrictedDelete(filename);
            }
        }
        stage.saveStage();
    }


    /**
     * Method doesn't work for two parents.
     */
    public void log() {
        Commit curr = Commit.findCommit(headCommitID);
        while (curr != null) {
            System.out.println(curr.toString());
            if (curr.getMessage().equals("initial commit")) {
                break;
            }
            curr = Commit.findCommit(curr.getParent());
        }
    }

    /**
     * asd.
     */
    public static void global() {
        System.out.println(Utils.readContentsAsString(GLOBAL_LOG_FILE));
    }

    /**
     * asd.
     *
     * @param commit s
     */
    public void updateGlobalLog(Commit commit) {
        String temp = Utils.readContentsAsString(GLOBAL_LOG_FILE);
        temp += commit.toString() + "\n";
        Utils.writeContents(GLOBAL_LOG_FILE, temp);
    }

    /**
     * asdasd.
     *
     * @param msg s
     * @return
     */
    public static String find(String msg) {
        StringBuilder str = new StringBuilder();
        List<String> allCommits = Utils.plainFilenamesIn(COMMITS_FOLDER);
        assert allCommits != null;
        for (String commitHash : allCommits) {
            if (Commit.findCommit(commitHash).getMessage().equals(msg)) {
                str.append(commitHash).append("\n");
            }
        }
        if (str.toString().isBlank()) {
            Main.exitWithError("Found no commit with that message.");
        }
        return str.toString();
    }

    /**
     * asd.
     *
     * @param filename s
     * @return
     */
    public Enum isModifiedButUnstaged(String filename) {

        File path = Utils.join(cwd, filename);
        Commit curr = Commit.findCommit(headCommitID);
        String cwdBlob = retrieveCWDBlob(filename);
        String stagedBlobContents;
        String commitedBlobContents = "";

        if (cwdBlob == null) {
            if (!stage.getToRemove().contains(filename)
                    && curr.tracks(filename)) {
                return BlobStates.NOTPRESENT;
            }
            if (stage.getToAdd().containsKey(filename)) {
                return BlobStates.NOTPRESENT;
            }
        }

        if (curr.tracks(filename)) {
            commitedBlobContents
                    = retrieveBlobContents(curr.getBlobs().get(filename));
            if (cwdBlob != null
                    && !cwdBlob.equals(commitedBlobContents)
                    && !stage.getToAdd().containsKey(filename)) {
                return BlobStates.MODIFIED;
            }
        }

        if (stage.getToAdd().containsKey(filename)) {
            stagedBlobContents
                    = retrieveBlobContents(stage.getToAdd().get(filename));

            if (curr.tracks(filename)
                    && !stagedBlobContents.equals(commitedBlobContents)) {
                return BlobStates.MODIFIED;
            }
        }
        return null;
    }

    /**
     * Displays what branches currently exist, and marks the current
     * branch with a *. Also displays what files have been staged for
     * addition or removal. An example of the exact format it
     * should follow is as follows.
     * A file in the working directory is "modified but not staged" if it is
     * <p>
     * Tracked in the current commit, changed in the working directory,
     * but not staged; or
     * Staged for addition, but with different contents than in the
     * working directory; or
     * Staged for addition, but deleted in the working directory; or
     * Not staged for removal, but tracked in the current commit and
     * deleted from the working directory.
     * <p>
     * The final category ("Untracked Files") is for files present
     * in the working directory but neither staged for addition nor
     * tracked. This includes files that have been staged for removal,
     * but then re-created without Gitlet's knowledge.
     * Ignore any subdirectories that may have been introduced,
     * since Gitlet does not deal with them.
     * <p>
     * The last two sections (modifications not staged and untracked files)
     * are extra credit, worth 1 point.
     */
    public void status() {
        System.out.println("=== Branches ===");
        for (Map.Entry<String, String> entry : branches.entrySet()) {
            if (entry.getKey().equals(getActiveBranch())) {
                System.out.print("*");
            }
            System.out.println(entry.getKey());
        }

        System.out.println("\n=== Staged Files ===");
        for (Map.Entry<String, String> entry : stage.getToAdd().entrySet()) {
            System.out.println(entry.getKey());
        }

        System.out.println("\n=== Removed Files ===");
        TreeSet<String> removeFiles = stage.getToRemove();
        for (String file : removeFiles) {
            System.out.println(file);
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");
        TreeSet<String> allFiles = new TreeSet<>();
        Commit curr = Commit.findCommit(headCommitID);
        ArrayList<String> filesTrackedByCurr
                = new ArrayList<>(curr.getBlobs().keySet());
        List<String> localFiles = Utils.plainFilenamesIn(cwd);
        allFiles.addAll(localFiles);
        allFiles.addAll(filesTrackedByCurr);
        Enum fileState;
        for (String file : allFiles) {
            if (file.contains(".txt")) {
                fileState = isModifiedButUnstaged(file);
                if (fileState != null) {
                    if (fileState.equals(BlobStates.MODIFIED)) {
                        System.out.println(file + " (modified)");
                    } else {
                        System.out.println(file + " (deleted)");
                    }
                }
            }
        }

        System.out.println("\n=== Untracked Files ===");
        ArrayList<String> untracked = getUntrackedFiles();
        for (String filename : untracked) {
            if (filename.contains(".txt")) {
                System.out.println(filename);
            }
        }
    }

    /**
     * asd.
     *
     * @param branchName s
     */
    public void branch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branches.put(branchName, headCommitID);
    }

    /**
     * asd.
     *
     * @param branchName s
     */
    public void rmBranch(String branchName) {
        if (getActiveBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        if (!branches.containsKey(branchName)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        branches.remove(branchName);
    }

    /**
     * A file is untracked if it’s in the CWD but isn’t staged to be added
     * and it’s not accounted for in the current commit.
     *
     * @param filename d
     * @return
     */
    public boolean isUntracked(String filename) {
        File path = Utils.join(cwd, filename);
        Commit head = Commit.findCommit(headCommitID);
        if (path.exists()) {
            if (!head.tracks(filename)
                    && !stage.getToAdd().containsKey(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterates through files present in the CWD,
     * returns a list of untracked files.
     *
     * @return
     */
    public ArrayList<String> getUntrackedFiles() {
        List<String> localFiles = Utils.plainFilenamesIn(cwd);
        ArrayList<String> output = new ArrayList<>();
        for (String file : localFiles) {
            if (isUntracked(file)) {
                output.add(file);
            }
        }
        return output;
    }

    /**
     * BRANCHNAME Leaglity checks.
     *
     * @param branchName g
     */
    public void mergeLegal(String branchName) {
        if (!branches.containsKey(branchName)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (!stage.getToAdd().isEmpty() || !stage.getToRemove().isEmpty()) {
            Main.exitWithError("You have uncommitted changes.");
        }
        if (branchName.equals(getActiveBranch())) {
            Main.exitWithError("Cannot merge a branch with itself.");
        }
        if (Commit.computeHash
                (getSplit(branchName)).equals(branches.get(branchName))) {
            Main.exitWithError("Given branch is an ancestor"
                    + " of the current branch.");
        }
        if (Commit.computeHash(getSplit(branchName)).equals(headCommitID)) {
            checkoutBranch(branchName);
            Main.exitWithError("Current branch fast-forwarded.");
        }
        untrackedFileError(branches.get(branchName));
    }

    /**
     * Proud of my babies.
     */
    enum BlobStates {
        MODIFIED, UNMODIFIED, NOTPRESENT, PRESENT;
    }

    /**
     * Maps from Filename.
     *
     * @param split    s
     * @param allFiles s
     * @param compare  i
     * @return
     */
    public TreeMap<String, Enum> computeBlobStates(Commit split, Commit compare,
                                                   HashSet<String> allFiles) {
        TreeMap<String, Enum> blobStates = new TreeMap<>();
        for (String file : allFiles) {
            if (!compare.tracks(file)) {
                blobStates.put(file, BlobStates.NOTPRESENT);
            } else if (split.tracks(file)) {
                if ((compare.getBlobHash(file)
                        .equals(split.getBlobHash(file)))) {
                    blobStates.put(file, BlobStates.UNMODIFIED);
                } else if ((!compare.getBlobHash(file)
                        .equals(split.getBlobHash(file)))) {
                    blobStates.put(file, BlobStates.MODIFIED);
                }
            } else if (!split.tracks(file) && compare.tracks(file)) {
                blobStates.put(file, BlobStates.MODIFIED);
            }
        }
        return blobStates;
    }

    /**
     * Didn.
     *
     * @param split    s
     * @param allFiles s
     * @return
     */
    public TreeMap<String, Enum> computeBlobStates(Commit split,
                                                   HashSet<String> allFiles) {
        TreeMap<String, Enum> blobStates = new TreeMap<>();
        for (String file : allFiles) {
            if (split.tracks(file)) {
                blobStates.put(file, BlobStates.PRESENT);
            } else {
                blobStates.put(file, BlobStates.NOTPRESENT);
            }
        }
        return blobStates;
    }

    /**
     * asdasd.
     *
     * @param head     d
     * @param other    d
     * @param split    d
     * @param filename d
     * @return
     */
    public int mergeDetermineCase(Enum head, Enum other,
                                  Enum split, String filename) {
        if (other == BlobStates.MODIFIED && head == BlobStates.UNMODIFIED) {
            return 1;
        }
        if (head == BlobStates.MODIFIED && other == BlobStates.UNMODIFIED) {
            return 2;
        }

        if (other == BlobStates.MODIFIED && head == BlobStates.MODIFIED) {
            return 3;
        }
        if (split == BlobStates.PRESENT && other == BlobStates.NOTPRESENT
                && head == BlobStates.NOTPRESENT) {
            return 8;
        }
        if (split == BlobStates.PRESENT && other == BlobStates.MODIFIED
                && head == BlobStates.NOTPRESENT) {
            return 3;
        }

        if (split == BlobStates.PRESENT && other == BlobStates.NOTPRESENT
                && head == BlobStates.MODIFIED) {
            return 3;
        }

        if (split == BlobStates.NOTPRESENT && other == BlobStates.NOTPRESENT
                && head == BlobStates.MODIFIED) {
            return 4;
        }
        if (split == BlobStates.NOTPRESENT && other == BlobStates.MODIFIED
                && head == BlobStates.NOTPRESENT) {
            return 5;
        }
        if (head == BlobStates.UNMODIFIED && other == BlobStates.NOTPRESENT) {
            return 6;
        }
        if (other == BlobStates.UNMODIFIED && head == BlobStates.NOTPRESENT) {
            return 7;
        }
        return 0;
    }

    /**
     * asd.
     *
     * @param branchName s
     */
    public void merge(String branchName) {
        mergeLegal(branchName);
        Commit head = Commit.findCommit(headCommitID);
        Commit other = Commit.findCommit(branches.get(branchName));
        Commit split = getSplit(branchName);
        HashSet<String> allFiles = new HashSet<>();
        allFiles.addAll(new ArrayList<>(split.getBlobs().keySet()));
        allFiles.addAll(new ArrayList<>(head.getBlobs().keySet()));
        allFiles.addAll(new ArrayList<>(other.getBlobs().keySet()));
        TreeMap<String, Enum> headBlobStates
                = computeBlobStates(split, head, allFiles);
        TreeMap<String, Enum> otherBlobStates
                = computeBlobStates(split, other, allFiles);
        TreeMap<String, Enum> sBs = computeBlobStates(split, allFiles);
        for (String filename : allFiles) {
            Enum headBlobState = headBlobStates.get(filename);
            Enum otherBlobState = otherBlobStates.get(filename);
            Enum splitBlobState = sBs.get(filename);
            String headBlobHash = head.getBlobHash(filename);
            String otherBlobHash = other.getBlobHash(filename);
            File path = Utils.join(cwd, filename);
            switch (mergeDetermineCase(headBlobState, otherBlobState,
                    splitBlobState, filename)) {
            case 1:
                add(filename);
                Utils.writeContents(path, retrieveBlobContents(otherBlobHash));
                break;
            case 3:
                if (headBlobState != BlobStates.NOTPRESENT && otherBlobState
                        != BlobStates.NOTPRESENT) {
                    if (other.getBlobHash(filename).equals(headBlobHash)) {
                        break;
                    }
                }
                System.out.println("Encountered a merge conflict.");
                String mergedContent = "<<<<<<< HEAD\n";
                if (headBlobState != BlobStates.NOTPRESENT) {
                    mergedContent += retrieveBlobContents(headBlobHash);
                }
                mergedContent += "=======\n";
                if (otherBlobState != BlobStates.NOTPRESENT) {
                    mergedContent += retrieveBlobContents(otherBlobHash);
                }
                mergedContent += ">>>>>>>\n";
                Utils.writeContents(path, mergedContent);
                add(filename);
                break;
            case 5:
                Utils.writeContents(path, retrieveBlobContents(otherBlobHash));
                add(filename);
                break;
            case 6:
                rm(filename);
                break;
            default:
                break;
            }
        }
        mergeCommit(branchName);
    }

    /**
     * s.
     *
     * @param branchname s
     * @return
     */
    public Commit getSplit(String branchname) {
        Commit head = Commit.findCommit(headCommitID);
        Commit otherHead = Commit.findCommit(branches.get(branchname));
        History headTraversal = new History();
        History otherTraversal = new History();
        HashSet<String> headHistory = headTraversal.findHistory(head);
        HashSet<String> otherHistory = otherTraversal.findHistory(otherHead);
        ArrayList<String> commonAncestors = new ArrayList<>();
        for (String commitHash : headHistory) {
            if (otherHistory.contains(commitHash)
                    && !commonAncestors.contains(commitHash)) {
                commonAncestors.add(commitHash);
            }
        }
        assert (!commonAncestors.isEmpty());
        if (commonAncestors.size() == 1) {
            return Commit.findCommit(commonAncestors.get(0));
        }

        return Commit.findCommit(breadthFirstSearch(headCommitID,
                commonAncestors));
    }

    /**
     * sd.
     * @return
     */
    public String getHeadCommitID() {
        return headCommitID;
    }

    /**
     * asdasd.
     *
     * @param source          s
     * @param commonAncestors s
     * @return
     */
    public String breadthFirstSearch(String source, ArrayList commonAncestors) {
        ArrayList<String> marked = new ArrayList<>();
        LinkedList<String> fringe = new LinkedList<>();
        fringe.add(source);
        marked.add(source);
        while (!fringe.isEmpty()) {
            String v = fringe.remove();
            if (commonAncestors.contains(v)) {
                return v;
            }
            Commit vCommit = Commit.findCommit(v);
            String parent1 = vCommit.getParent();
            String parent2 = vCommit.getSecondParent();
            if (!marked.contains(parent1)) {
                fringe.add(parent1);
                marked.add(parent1);
            }
            if (parent2 != null && !marked.contains(parent2)) {
                fringe.add(parent2);
                marked.add(parent2);
            }
        }
        return Repository.find("initial commit");
    }

    /**
     * Stops whens the traversal reaches stopping point.
     *
     * @param head s
     * @param stop s
     * @return
     */
    public ArrayList fetchLocalNewCommits(Commit head, Commit stop) {
        String stopHash = Commit.computeHash(stop);
        ArrayList<String> newCommits = new ArrayList<>();
        while (Commit.computeHash(head) != stopHash
                && head.getParent() != null) {
            newCommits.add(Commit.computeHash(head));
            head = Commit.findCommit(head.getParent());
        }
        return newCommits;
    }


    /**
     * asd.
     *
     * @return asd
     */
    public StagingArea getStage() {
        return stage;
    }


    /**
     * asas.
     * @param remoteName d
     * @return
     */
    public static File getRemotePath(String remoteName) {
        String remotePathString
                = Utils.readContentsAsString
                (Utils.join(REMOTE_FOLDER, remoteName));
        return Utils.join(remotePathString);
    }

    /**
     * asdasd.
     * @param repoName h
     * @param dir h
     */
    public void addRemote(String repoName, String dir) {
        File path = Utils.join(REMOTE_FOLDER, repoName);
        if (path.exists()) {
            Main.exitWithError("A remote with that name already exists.");
        }
        Utils.writeContents(Utils.join(REMOTE_FOLDER, repoName), dir);
    }

    /**
     * asfasdf.
     * @param remoteName s.
     */
    public void rmRemote(String remoteName) {
        if (Utils.join(REMOTE_FOLDER, remoteName).exists()) {
            Utils.join(REMOTE_FOLDER, remoteName).delete();
        } else {
            Main.exitWithError("A remote with that name does not exist.");
        }

    }

    /**
     * asd.
     * @param remoteName s
     * @param remoteBranchName s
     */

    public void fetch(String remoteName, String remoteBranchName) {


        File remotePath = getRemotePath(remoteName);

        if (!remotePath.exists()) {
            Main.exitWithError("Remote directory not found.");
        }
        File rmBranchesPath = Utils.join(remotePath, "branches");
        File rmCommitsFolder = Utils.join(remotePath, "commits");
        File rmBlobsFolder = Utils.join(remotePath, "blobs");

        TreeMap<String, String> rmBranches
                = Utils.readObject(rmBranchesPath, TreeMap.class);
        if (!rmBranches.containsKey(remoteBranchName)) {
            Main.exitWithError("The remote does not have that branch.");
        }

        String otherBranchHeadHash = rmBranches.get(remoteBranchName);
        Commit otherBranchHead
                = Utils.readObject
                (Utils.join
                        (rmCommitsFolder, otherBranchHeadHash), Commit.class);
        History otherHeadTraversal = new History();
        HashSet<String> rmBranchHistory
                = otherHeadTraversal.findHistoryRm
                (otherBranchHead, rmCommitsFolder);
        List<String> localFileCommits
                = Utils.plainFilenamesIn(Repository.COMMITS_FOLDER);

        for (String rmCommitNames : rmBranchHistory) {
            if (!localFileCommits.contains(rmCommitNames)) {
                Commit fetchCommitObject
                        = rmFindCommit(rmCommitNames, rmCommitsFolder);
                File fetchedCommitPath
                        = Utils.join(Repository.COMMITS_FOLDER, rmCommitNames);
                Utils.writeObject(fetchedCommitPath, fetchCommitObject);
            }
        }

        List<String> localBlobs
                = Utils.plainFilenamesIn(Repository.BLOBS_FOLDER);
        List<String> rmBlobs = Utils.plainFilenamesIn(rmBlobsFolder);
        for (String rmBlob : rmBlobs) {
            if (!localBlobs.contains(rmBlob)) {
                String blobContents
                        = rmRetrieveBlobContents(rmBlob, rmBlobsFolder);
                File fetchedBlobPath
                        = Utils.join(Repository.BLOBS_FOLDER, rmBlob);
                Utils.writeContents(fetchedBlobPath, blobContents);
            }
        }
        this.branches.put(remoteName + "/" + remoteBranchName,
                Commit.computeHash(otherBranchHead));
    }

    /**
     * find.
     * @param hash s
     * @param commitFolder s
     * @return
     */
    public static Commit rmFindCommit(String hash, File commitFolder) {
        File commitPath = Utils.join(commitFolder, hash);
        if (commitPath.exists()) {
            return Utils.readObject(commitPath, Commit.class);
        }
        List<String> allCommits = Utils.plainFilenamesIn(commitFolder);
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
     * Attempts to append the current branch's commits
     * to the end of the given branch at the given remote.
     *
     * @param remoteName s
     * @param remoteBranchName s
     */
    public void push(String remoteName, String remoteBranchName) {
        File rmPath = getRemotePath(remoteName);
        File rmBranchesFolder = Utils.join(rmPath, "branches");
        File rmCommitsFolder = Utils.join(rmPath, "commits");
        File rmBlobsFolder = Utils.join(rmPath, "blobs");

        if (!rmPath.exists()) {
            Main.exitWithError("Remote directory not found.");
        }

        String rmHeadCommitID
                = Utils.readContentsAsString
                (Utils.join(rmPath, "headcommit"));

        Commit otherHead = rmFindCommit(rmHeadCommitID, rmCommitsFolder);
        Commit head = Commit.findCommit(headCommitID);
        History headTraversal = new History();

        HashSet<String> headHistory = new HashSet<>();
        if (!headHistory.contains(Commit.computeHash(otherHead))) {
            Main.exitWithError(" Please pull down "
                    + "remote changes before pushing.");
        }

        ArrayList<String> newLocalCommits
                = fetchLocalNewCommits(head, otherHead);

        for (String newCommitHash : newLocalCommits) {
            Commit newCommit = Commit.findCommit(newCommitHash);
            Utils.writeObject(rmCommitsFolder, newCommit);
        }

    }

    /**
     * asdasd.
     * @param sha1 s
     * @param blobsFolder s
     * @return
     */
    public static String rmRetrieveBlobContents(String sha1, File blobsFolder) {
        File path = Utils.join(blobsFolder, sha1);
        if (!path.exists()) {
            throw new GitletException("Blob doesn't exist!");
        }
        return Utils.readContentsAsString(path);
    }


}

