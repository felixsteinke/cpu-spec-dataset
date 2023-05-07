package cpu.spec.dataset.api.database;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.file.ResourceReader;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

@Component
public class CsvToDbImporter {
    private final Logger LOGGER = Logger.getLogger(CsvToDbImporter.class.getName());
    private final String tableName = CpuSpecification.class.getAnnotation(Table.class).name();
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final boolean datasetDbImport;
    private final String datasetDirPath;

    @Autowired
    public CsvToDbImporter(@Value("${spring.datasource.url}") String dbUrl,
                           @Value("${spring.datasource.username}") String dbUser,
                           @Value("${spring.datasource.password}") String dbPassword,
                           @Value("${dataset.db.import}") String datasetDbImport,
                           @Value("${dataset.directory.path}") String datasetDirPath) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.datasetDbImport = Boolean.parseBoolean(datasetDbImport);
        this.datasetDirPath = datasetDirPath;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        if (datasetDbImport) {
            LOGGER.info("Start importing CSV Datasets");
            importCsvDatasets();
            LOGGER.info("Finished importing CSV Datasets");
        }
    }

    public void importCsvDatasets() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(buildQueryInstruction("intel-cpus.csv", ResourceReader.getIntelCsvMapping()));
            statement.executeUpdate(buildQueryInstruction("amd-cpus.csv", ResourceReader.getAmdCsvMapping()));

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private String buildQueryInstruction(String dataFile, List<CsvColumnMapping> mappings) {
        return "LOAD DATA INFILE '" + datasetDirPath + dataFile + "' INTO TABLE " + tableName
                + " FIELDS TERMINATED BY ','"
                // + " ENCLOSED BY '\"'"
                + " LINES TERMINATED BY '\n'"
                + " IGNORE 1 ROWS "
                + buildMappingInstruction(mappings);

    }

    /**
     * @param mappings as configuration (all csv columns need to be in the list & unmapped columns need to be 'null')
     * @return query instructions how to map the csv values like '(@includedColumn, excludedColumn) SET column = @includedColumn;'
     */
    private String buildMappingInstruction(List<CsvColumnMapping> mappings) {
        StringBuilder columnString = new StringBuilder("(");
        StringBuilder setString = new StringBuilder("SET ");

        for (CsvColumnMapping column : mappings) {
            String csvColumnId = column.getCsv().replaceAll(" ", "");
            if (column.getDb() == null) {
                columnString.append(csvColumnId).append(", ");
                continue;
            }
            columnString.append("@").append(csvColumnId).append(", ");
            setString.append(column.getDb()).append(" = @").append(csvColumnId).append(", ");
        }
        return columnString.substring(0, columnString.length() - 2) + ") " +
                setString.substring(0, setString.length() - 2) + ";";
    }
}
