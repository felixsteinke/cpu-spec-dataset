# CPU Specification Dataset

Dataset for CPU specifications from Intel, AMD and Ampere, extended by CPU benchmark and CPU-World data.

## Table of Content

* [Raw Datasets](dataset)
* [Dataset API](dataset-api/src/main/java/cpu/spec/dataset/api)
* [AMD Web Scraper](amd-web-scraper/src/main/java/cpu/spec/scraper)
* [Ampere Web Scraper](ampere-web-scraper/src/main/java/cpu/spec/scraper)
* [Intel Web Scraper](intel-web-scraper/src/main/java/cpu/spec/scraper)
* [Benchmark Web Scraper](benchmark-web-scraper/src/main/java/cpu/spec/scraper)
* [Cpuworld Web Scraper](cpuworld-web-scraper/src/main/java/cpu/spec/scraper)

## Installation

Requires __JDK 25__ & __Maven 3.6.3+__ (tested with OpenJDK 25 & Maven 3.6.3).

__Windows__:

```shell
xcopy .\dataset\ .\dataset-api\src\main\resources\dataset /s /e /h /y 
mvn clean install -DskipTests
```

__Linux__:

```shell
mkdir -p ./dataset-api/src/main/resources/dataset/ && cp ./dataset/* ./dataset-api/src/main/resources/dataset/
mvn clean install -DskipTests
```

## Dataset API Usage

```shell
cd dataset-api
mvn spring-boot:run
```

Endpoint is available at: `GET http://localhost:8080/api/cpu-dataset`

<details>
  <summary>Configurations</summary>

[dataset-api/application.properties](dataset-api/src/main/resources)

| Property                                     | Example Value                         | Description                        |
|----------------------------------------------|---------------------------------------|------------------------------------|
| `spring.datasource.url`                      | `jdbc:mysql://localhost:3306/cpu_db`  | Full url to a MySQL database       |
| `spring.datasource.username`                 | `root`                                | Username and usually root          |
| `spring.datasource.password`                 | `password`                            | Custom password                    |
| `spring.jpa.hibernate.ddl-auto`              | `update` or `validate`                | Table schema update mode           |
| `spring.jpa.defer-datasource-initialization` | `false` or `true`                     | `true` skips the dataset import    |

</details>

## Container Image

```shell
docker run --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=cpu_db -p 3306:3306 -d mysql:8
```

Published images: [ghcr.io/felixsteinke/cpu-spec-dataset-api](https://github.com/users/felixsteinke/packages/container/package/cpu-spec-dataset-api)
with one tag per release (e.g. `v2.1`) and `latest` for the newest release.

```shell
docker run -p 8080:80 --name dataset-api ghcr.io/felixsteinke/cpu-spec-dataset-api:latest
```

Local build:

```shell
docker build -t cpu-spec-dataset-api:local .
docker run -p 8080:80 --name dataset-api cpu-spec-dataset-api:local
```

### Container Configuration

| Environment Key                              | Example Value                                   | Description                         |
|----------------------------------------------|-------------------------------------------------|-------------------------------------|
| `SPRING_DATASOURCE_URL`                      | `jdbc:mysql://host.docker.internal:3306/cpu_db` | Full url to a MySQL database        |
| `SPRING_DATASOURCE_USERNAME`                 | `root`                                          | Username and usually root           |
| `SPRING_DATASOURCE_PASSWORD`                 | `password`                                      | Custom password                     |
| `SPRING_JPA_HIBERNATE_DDL_AUTO`              | `validate` (default) or `update`                | Table schema update mode            |
| `SPRING_JPA_DEFER_DATASOURCE_INITIALIZATION` | `false` (default) or `true`                     | `true` skips the dataset import     |

> __Dataset import:__ by default every start imports all datasets of the image into the database (existing rows are
> updated, new ones added, none deleted), which takes a few seconds before the data is complete. With
> `SPRING_JPA_DEFER_DATASOURCE_INITIALIZATION=true` the import is skipped and the API serves the database as it is,
> e.g. for faster restarts of an already filled database. A new image version only provides its datasets through the
> import, so do not skip it on the first start of a new version.

> __Upgrading an existing database:__ the `prod` profile only validates the schema (`ddl-auto=validate`) and fails to
> start with `missing column [...]` if the table was created by an older version (e.g. `v1.0` lacks the `*_index`
> columns). Start the new version once with `SPRING_JPA_HIBERNATE_DDL_AUTO=update` to add the missing columns, then
> use the default configuration again. Existing rows are kept and updated by the dataset import.

## Release

Versions follow `vMAJOR.MINOR` (e.g. `v2.1`): __MAJOR__ for recreated datasets or breaking API changes, __MINOR__ for
updates and fixes. The Maven project version (all `pom.xml` files) matches the release version.

1. Update the version in all `pom.xml` files (e.g. `2.1`) and merge to `main`.
2. Publish a [GitHub Release](https://github.com/felixsteinke/cpu-spec-dataset/releases/new) with the tag `v2.1`.
3. The [Docker-Image workflow](.github/workflows/docker-image.yml) builds the image and pushes the tags `v2.1`
   and `latest` (pre-releases only get their version tag). The workflow fails before building if the release tag
   does not match `v` + the version of all `pom.xml` files, or if the image `v2.1` already exists (published versions
   are never overwritten).

## Workflows

| Workflow                                                       | Trigger                  | Description                                                                                                                     |
|----------------------------------------------------------------|--------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| [CI](.github/workflows/ci.yml)                                 | push to `main`, manual   | Builds all modules and runs the tests (with MySQL), except the tests loading live web pages                                     |
| [Scraper Smoke Test](.github/workflows/scraper-smoke-test.yml) | manual                   | Runs the scrapers with `-Dscraper.limit=1`, each has to return at least one CPU (datasets unchanged, logs as artifact)          |
| [Dataset Update](.github/workflows/dataset-update.yml)         | manual                   | Runs the scrapers and opens a pull request from the branch `dataset-update`, stops early if the branch already exists           |
| [Docker-Image](.github/workflows/docker-image.yml)             | release, manual          | Builds and publishes the image, see [Release](#release)                                                                         |

* Tests loading live web pages are tagged with `@Tag("live")`, run the others with `mvn verify -DexcludedGroups=live`.
* The __Dataset Update__ workflow requires __Settings > Actions > General > Allow GitHub Actions to create and approve
  pull requests__. After merging, the branch is deleted (repository setting), so the next update can run.
* Websites may block requests from GitHub's runners. Then run [the script](#recreate-all-datasets) on another machine.

## Dataset Update

All updates to the dataset need to carefully reviewed. The columns are mapped by their __header name__ in the
corresponding [Dataset Class](dataset-api/src/main/java/cpu/spec/dataset/api/dataset) for the __Dataset API__.
If a regenerated dataset renames or drops a mapped column, the dataset tests fail:

```shell
cd dataset-api
mvn test -Dtest="CsvMapperTest,*DatasetTest"
```

### Recreate all Datasets

Requires __JDK 25__, __Maven__ and __Google Chrome__ (benchmark & cpu-world scraper, the driver is resolved
automatically by Selenium) and runs on Linux, macOS and Windows (Git Bash):

```shell
./scripts/scrape-all.sh                 # all scrapers: amd ampere benchmark intel cpuworld
./scripts/scrape-all.sh amd benchmark   # selected scrapers
```

The script builds the project, runs the scrapers (logs in `logs/`), validates the datasets with the Dataset API
tests and prints a summary. It exits with `1` if the build or the validation failed and with `2` if a scraper failed
(the other datasets are valid). Review the result with `git diff --stat dataset/` before committing it, or let the
[Dataset Update](#workflows) workflow open a pull request.

| JVM Option (`JAVA_OPTS="..."`)                | Description                                                                     |
|-----------------------------------------------|---------------------------------------------------------------------------------|
| `-Dscraper.limit=5`                           | Smoke test with the first 5 pages per level, written to `dataset/*.limited.csv` |
| `-Dscraper.delay=500`                         | Wait 500 ms between detail page requests (Intel)                                |
| `-Dscraper.force=true`                        | Replace a dataset even if the new run has much fewer records                    |
| `-Djavax.net.ssl.trustStoreType=Windows-ROOT` | Use the Windows certificate store (TLS-intercepting proxy, `PKIX` error)        |

Safeguards:

* __Incomplete runs never replace a dataset:__ if a scraper extracts less than 80 % of the existing records
  (e.g. blocked requests), it writes `dataset/*.incomplete.csv` instead (ignored by git).
* __Stable API:__ the CPU name is the id of the Dataset API. The scrapers keep the names, columns and value formats of
  the previous dataset version, new columns are appended (benchmark: CPUs keep their previous name and values the
  source does not provide anymore).
* The Intel scraper stops after 25 failed specification pages in a row (requests blocked).

### [AMD Dataset](dataset/amd-cpus.csv)

Extracted from the specification tables (client and server processors) of
[https://www.amd.com/en/products/specifications/processors.html](https://www.amd.com/en/products/specifications/processors.html)
with the responsible __Web Scraper__. The pages embed the same data the __Export__ button of the table uses (1 request
per page, a few seconds).

```shell
cd amd-web-scraper
java -jar ./target/executable.jar
```

<details>
  <summary>Manual Export (fallback)</summary>

![amd-csv-export](.docs/amd-csv-export.png)

</details>

### [Ampere Dataset](dataset/ampere-cpus.csv)

Extracted from the SKU tables of the product briefs
([Ampere Altra](https://amperecomputing.com/briefs/ampere-altra-family-product-brief),
[AmpereOne](https://amperecomputing.com/briefs/ampereone-family-product-brief),
[AmpereOne M](https://amperecomputing.com/briefs/ampereone-m-product-brief))
with the responsible __Web Scraper__.

```shell
cd ampere-web-scraper
java -jar ./target/executable.jar
```

### [Intel Dataset](dataset/intel-cpus.csv)

Extracted from
[https://www.intel.com/content/www/us/en/ark.html](https://www.intel.com/content/www/us/en/ark.html)
with the responsible __Web Scraper__ (about 3,000 specification pages, run a smoke test with `-Dscraper.limit=5` first).

```shell
cd intel-web-scraper
java -jar ./target/executable.jar
```

### [CPU Benchmark Dataset](dataset/benchmark-cpus.csv)

Extracted from the data table of [https://www.cpubenchmark.net/CPU_mega_page.html](https://www.cpubenchmark.net/CPU_mega_page.html)
(all CPUs with specifications and benchmark scores, 1 page load) with the responsible __Web Scraper__ (Chrome).

```shell
cd benchmark-web-scraper
java -jar ./target/executable.jar
```

### [CPU World Dataset](dataset/cpuworld-cpus.csv)

Extracted from [https://www.cpu-world.com/CPUs/CPU.html](https://www.cpu-world.com/CPUs/CPU.html)
(Intel Xeon Platinum & Gold, waits 3 - 10 s between pages) with the responsible __Web Scraper__ (Chrome).

```shell
cd cpuworld-web-scraper
java -jar ./target/executable.jar
```
