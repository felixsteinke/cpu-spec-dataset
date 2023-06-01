package cpu.spec.scraper.file;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class ResourceFileReader {
    /**
     * @param resourcePath path within the resource directory
     * @return file at resource path
     * @throws IOException if resource does not exist
     */
    public static File getFile(String resourcePath) throws IOException {
        URL resourceUrl = ResourceFileReader.class.getClassLoader().getResource(resourcePath);
        if (resourceUrl == null)
            throw new IOException("No content at " + resourcePath);
        return new File(resourceUrl.getFile());
    }
}
