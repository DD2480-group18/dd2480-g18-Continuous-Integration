import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Build {
    private int buildID;
    private String commitHash;
    private String buildDate;
    private String branch;
    private installResult installResult;
    private buildResult buildResult;
    private testResult testResult;

    public Build(int buildID, String commitHash, String buildDate, String branch, installResult installResult, buildResult buildResult, testResult testResult) {
        this.buildID = buildID;
        this.commitHash = commitHash;
        this.buildDate = buildDate;
        this.branch = branch;
        this.installResult = installResult;
        this.buildResult = buildResult;
        this.testResult = testResult;
    }

    /**
     * Special constructor that creates a build object but initializes install/build/test logs by reading from
     * the generated files from the bash script.
     * Requeries the existance of .mvn_install.log, .mvn_compile.log mvn_test.log
     * @param buildID
     * @param commitHash
     * @param buildDate
     * @param branch
     * @param installStatus
     * @param buildStatus
     * @param testStatus
     * @param savedLocally
     */
    public Build(int buildID, String commitHash, String buildDate, String branch, String installStatus, String buildStatus, String testStatus, boolean savedLocally){
        String buildDir = (savedLocally) ? "localbuilds/" : "cloudbuilds/";
        String installLog ="", buildLog="", testLog="";
        try {
            installLog = Files.readString(Path.of(buildDir + commitHash + "/.mvn_install.log"));
            buildLog = Files.readString(Path.of(buildDir + commitHash + "/.mvn_compile.log"));
            testLog = Files.readString(Path.of(buildDir + commitHash + "/.mvn_test.log"));
        }catch(Exception e){
            e.printStackTrace();
        }
        installResult installRes = new installResult(installStatus.equals(Integrator.STATUS_SUCCESS), installLog);
        buildResult buildRes = new buildResult(buildStatus.equals(Integrator.STATUS_SUCCESS), buildLog);
        testResult testRes = new testResult(testStatus.equals(Integrator.STATUS_SUCCESS), testLog);
        this.buildID = buildID;
        this.commitHash = commitHash;
        this.buildDate = buildDate;
        this.branch = branch;
        this.installResult = installRes;
        this.buildResult = buildRes;
        this.testResult = testRes;
    }


    //--------------Getters and Setters ------------------
    public buildResult getBuildResult() {
        return buildResult;
    }

    public void setBuildResult(buildResult buildResult) {
        this.buildResult = buildResult;
    }

    public testResult getTestResult() {
        return testResult;
    }

    public void setTestResult(testResult testResult) {
        this.testResult = testResult;
    }

    public int getBuildID() {
        return buildID;
    }

    public void setBuildID(int buildID) {
        this.buildID = buildID;
    }

    public installResult getInstallResult() {
        return installResult;
    }

    public void setInstallResult(installResult installResult) {
        this.installResult = installResult;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Build build = (Build) o;
        return buildID == build.buildID && commitHash.equals(build.commitHash) && buildDate.equals(build.buildDate) && branch.equals(build.branch) && installResult.equals(build.installResult) && buildResult.equals(build.buildResult) && testResult.equals(build.testResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildID, commitHash, buildDate, branch, installResult, buildResult, testResult);
    }
}

class buildResult{
    private boolean buildSuccessfull;
    private String buildLogs;

    public buildResult(boolean buildSuccessfull, String buildLogs) {
        this.buildSuccessfull = buildSuccessfull;
        this.buildLogs = buildLogs;
    }


    //--------------Getters and Setters ------------------

    public boolean isBuildSuccessfull() {
        return buildSuccessfull;
    }

    public void setBuildSuccessfull(boolean buildSuccessfull) {
        this.buildSuccessfull = buildSuccessfull;
    }

    public String getBuildLogs() {
        return buildLogs;
    }

    public void setBuildLogs(String buildLogs) {
        this.buildLogs = buildLogs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        buildResult that = (buildResult) o;
        return buildSuccessfull == that.buildSuccessfull && buildLogs.equals(that.buildLogs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildSuccessfull, buildLogs);
    }
}

class testResult{
    private boolean testSuccessfull;
    private String testLogs;


    public testResult(boolean testSuccessfull, String testLogs) {
        this.testSuccessfull = testSuccessfull;
        this.testLogs = testLogs;
    }

    //--------------Getters and Setters ------------------

    public boolean isTestSuccessfull() {
        return testSuccessfull;
    }

    public void setTestSuccessfull(boolean testSuccessfull) {
        this.testSuccessfull = testSuccessfull;
    }

    public String getTestLogs() {
        return testLogs;
    }

    public void setTestLogs(String testLogs) {
        this.testLogs = testLogs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        testResult that = (testResult) o;
        return testSuccessfull == that.testSuccessfull && testLogs.equals(that.testLogs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testSuccessfull, testLogs);
    }
}



class installResult{
    private boolean installSuccessfull;
    private String installLogs;

    public installResult(boolean installSuccessfull, String installLogs) {
        this.installSuccessfull = installSuccessfull;
        this.installLogs = installLogs;
    }

    //--------------Getters and Setters ------------------

    public boolean isInstallSuccessfull() {
        return installSuccessfull;
    }

    public void setInstallSuccessfull(boolean installSuccessfull) {
        this.installSuccessfull = installSuccessfull;
    }

    public String getInstallLogs() {
        return installLogs;
    }

    public void setInstallLogs(String installLogs) {
        this.installLogs = installLogs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        installResult that = (installResult) o;
        return installSuccessfull == that.installSuccessfull && installLogs.equals(that.installLogs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(installSuccessfull, installLogs);
    }
}

