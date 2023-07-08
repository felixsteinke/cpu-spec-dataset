package cpu.spec.scraper.utils;

import cpu.spec.scraper.exception.DirectoryNotFoundException;

import java.io.File;

public abstract class FileUtils {
    /**
     * @param directoryName without slashes (e.g. dataset)
     * @return path with './' or '../' as prefix and '/' as postfix (e.g. ./dataset/)
     * @throws DirectoryNotFoundException if target directory was not in the execution directory
     */
    public static String getOutputDirectoryPath(String directoryName) throws DirectoryNotFoundException {
        String outputDir = "./" + directoryName + "/";
        if (outputDirectoryExists(outputDir)) {
            return outputDir;
        }
        outputDir = "../" + directoryName + "/";
        if (outputDirectoryExists(outputDir)) {
            return outputDir;
        }
        throw new DirectoryNotFoundException(directoryName);
    }

    /**
     * @return current execution directory on the system
     */
    public static String getExecutionDirectory() {
        return System.getProperty("user.dir");
    }

    private static boolean outputDirectoryExists(String path) {
        return new File(path).exists();
    }
}
