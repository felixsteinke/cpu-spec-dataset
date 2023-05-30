package cpu.spec.scraper;


import java.util.HashMap;
import java.util.Map;

public class CpuSpecificationModel {
    public String id;
    public String cpuName;
    public String sourceUrl;

    public Map<String, String> dataValues = new HashMap<>();
}
