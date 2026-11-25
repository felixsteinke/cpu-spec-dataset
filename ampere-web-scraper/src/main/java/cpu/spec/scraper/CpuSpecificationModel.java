package cpu.spec.scraper;

/**
 * @param cpuName    product name as listed in the brief (e.g. "M128-30" for Ampere Altra, "AmpereOne® A192-32X")
 * @param family     product family of the brief (e.g. "Ampere Altra", "AmpereOne M")
 * @param cores      core count
 * @param clockSpeed (sustained) frequency in GHz
 * @param tdp        usage power in W
 * @param sourceUrl  product brief
 */
public record CpuSpecificationModel(String cpuName, String family, String cores, String clockSpeed, String tdp,
                                    String sourceUrl) {
}
