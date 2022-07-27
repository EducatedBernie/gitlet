package gitlet;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Bernie Miao
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains.
     */
    public static void main(String... args) {
        argsCheckZeroInput(args);
        Repository repo = new Repository();
        argsCheckInit(args, repo);
        switch (args[0]) {
        case "add":
            validateNumArgs("add", args, 2);
            repo.add(args[1]);
            break;
        case "init":
            mainInit(args, repo);
            break;
        case "commit":
            validateNumArgs("commit", args, 2);
            repo.commit(args[1]);
            break;
        case "checkout":
            checkoutCaseDetermine(args, repo);
            break;
        case "rm":
            repo.rm(args[1]);
            break;
        case "log":
            repo.log();
            break;
        case "global-log":
            Repository.global();
            break;
        case "find":
            System.out.println(repo.find(args[1])); break;
        case "status":
            repo.status(); break;
        case "branch":
            repo.branch(args[1]); break;
        case "rm-branch":
            repo.rmBranch(args[1]); break;
        case "merge":
            repo.merge(args[1]);
            break;
        case "reset":
            repo.reset(args[1]);
            break;
        case "add-remote":
            repo.addRemote(args[1], args[2]);
            break;
        case "fetch":
            repo.fetch(args[1], args[2]);
            break;
        case "push":
            repo.push(args[1], args[2]);
            break;
        case "rm-remote":
            repo.rmRemote(args[1]);
            break;
        default:
            exitWithError("No command with that name exists.");
        }
        repo.saveRepo();
    }

    /**
     * fuck your stylecheck.
     * @param args d
     * @param repo d
     */
    public static void mainInit(String[] args, Repository repo) {
        validateNumArgs("init", args, 1);
        if (repo.initCheck()) {
            exitWithError("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }
        repo.init();
    }

    /**
     * asd.
     * @param args asd.
     * @param theRepo asd
     */
    public static void argsCheckInit(String[] args, Repository theRepo) {
        if (!args[0].equals("init")) {
            if (!theRepo.initCheck()) {
                exitWithError("Not in an initialized Gitlet directory.");
            }
            theRepo.updateRepo();
        }
    }

    /**
     * asd.
     * @param args asd
     */
    public static void argsCheckZeroInput(String[] args) {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
    }

    /**
     * asdoij.
     * @param args s
     * @param tRepo s
     */
    public static void checkoutCaseDetermine(String [] args, Repository tRepo) {
        if (args.length == 2) {
            tRepo.checkoutBranch(args[1]);
            return;
        } else if (args.length == 3 && args[1].equals("--")) {
            tRepo.checkoutRecent(args[2]);
            return;
        } else if (args.length == 4 && args[2].equals("--")) {
            tRepo.checkoutCommit(args[1], args[3]);
            return;
        }

        exitWithError("Incorrect operands.");
    }

    /**
     * s.
     * @param cmd s
     * @param args s
     * @param n s
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

    /**
     * asd.
     * @param message s
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }


}
