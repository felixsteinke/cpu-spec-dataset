package cpu.spec.dataset.api.database;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CsvColumnMapping {
    private String csv;
    private String db;
}
