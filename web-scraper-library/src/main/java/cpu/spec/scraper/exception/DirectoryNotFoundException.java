package cpu.spec.scraper.exception;

import cpu.spec.scraper.utils.FileUtils;

public class DirectoryNotFoundException extends Exception {
    public DirectoryNotFoundException(String directoryName) {
        super("Directory ()" + directoryName + ") not found in execution directory: " + FileUtils.getExecutionDirectory());
    }
}
