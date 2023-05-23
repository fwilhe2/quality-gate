package com.github.fwilhe2;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import edu.hm.hafner.analysis.FileReaderFactory;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import edu.hm.hafner.analysis.parser.checkstyle.CheckStyleParser;
import edu.hm.hafner.analysis.parser.findbugs.FindBugsParser;
import edu.hm.hafner.analysis.parser.pmd.PmdParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    @Parameter(names = {"--directory", "-d"})
    String directory;

    @Parameter(names = {"--pmd-max-errors"}, validateWith = PositiveInteger.class)
    Integer pmdMaximumAllowedErrors = 0;

    @Parameter(names = {"--pmd-max-warnings-high"}, validateWith = PositiveInteger.class)
    Integer pmdMaximumAllowedWarningsHigh = 3;

    @Parameter(names = {"--spotbugs-max-errors"}, validateWith = PositiveInteger.class)
    Integer spotbugsMaximumAllowedErrors = 0;

    @Parameter(names = {"--spotbugs-max-warnings-high"}, validateWith = PositiveInteger.class)
    Integer spotbugsMaximumAllowedWarningsHigh = 3;

    @Parameter(names = {"--checkstyle-max-errors"}, validateWith = PositiveInteger.class)
    Integer checkstyleMaximumAllowedErrors = 0;

    @Parameter(names = {"--checkstyle-max-warnings-high"}, validateWith = PositiveInteger.class)
    Integer checkstyleMaximumAllowedWarningsHigh = 3;

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    private void run() throws IOException {
        var pmdReports = Files
                .walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().contains("pmd.xml"))
                .map(path -> new PmdParser().parse(new FileReaderFactory(path))).toList();
        pmdReports.forEach(issues -> issues.forEach(System.out::println));

        var spotbugsReports = Files
                .walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().contains("spotbugsXml.xml"))
                .map(path -> new FindBugsParser(FindBugsParser.PriorityProperty.RANK).parse(new FileReaderFactory(path))).toList();
        spotbugsReports.forEach(issues -> issues.forEach(System.out::println));

        var checkstyleReports = Files
                .walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().contains("checkstyle-result.xml"))
                .map(path -> new CheckStyleParser().parse(new FileReaderFactory(path))).toList();
        checkstyleReports.forEach(issues -> issues.forEach(System.out::println));

        var pmdErrors = countOccurrences(pmdReports, Severity.ERROR);
        var pmdHigh = countOccurrences(pmdReports, Severity.WARNING_HIGH);

        var spotbugsErrors = countOccurrences(spotbugsReports, Severity.ERROR);
        var spotbugsHigh = countOccurrences(spotbugsReports, Severity.WARNING_HIGH);

        var checkstyleErrors = countOccurrences(checkstyleReports, Severity.ERROR);
        var checkstyleHigh = countOccurrences(checkstyleReports, Severity.WARNING_HIGH);

        var failureReasons = new ArrayList<String>();

        failureReasons.add(check("pmd", "error", pmdErrors, pmdMaximumAllowedErrors));
        failureReasons.add(check("pmd", "high", pmdHigh, pmdMaximumAllowedWarningsHigh));
        failureReasons.add(check("spotbugs", "error", spotbugsErrors, spotbugsMaximumAllowedErrors));
        failureReasons.add(check("spotbugs", "high", spotbugsHigh, spotbugsMaximumAllowedWarningsHigh));
        failureReasons.add(check("checkstyle", "error", checkstyleErrors, checkstyleMaximumAllowedErrors));
        failureReasons.add(check("checkstyle", "high", checkstyleHigh, checkstyleMaximumAllowedWarningsHigh));

        var failureReasonsNonNull = failureReasons.stream().filter(Objects::nonNull).toList();
        if (!failureReasonsNonNull.isEmpty()) {
            throw new RuntimeException(String.join("\n", failureReasonsNonNull));
        }
    }

    Long countOccurrences(List<Report> reports, Severity severity) {
        return reports.stream().map(report -> report.get().stream().filter(issue -> issue.getSeverity().equals(severity)).count()).reduce(0L, Long::sum);
    }

    String check(String tool, String type, long actual, long max) {
        if (actual > max) {
            return "Tool " + tool + " has " + actual + " findings of type " + type + ", but allowed are only " + max + ".";
        }
        return null;
    }
}