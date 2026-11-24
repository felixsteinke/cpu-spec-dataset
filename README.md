# CPU Specification Dataset

Dataset for CPU specifications from Intel and AMD.

## Table of Content

* [Raw Datasets](dataset)
* [Dataset API](dataset-api/src/main/java/cpu/spec/dataset/api)
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
| `spring.jpa.defer-datasource-initialization` | `false` or `true`                     | Defer database update with dataset |

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

| Environment Key              | Example Value                                   | Description                  |
|------------------------------|-------------------------------------------------|------------------------------|
| `SPRING_DATASOURCE_URL`      | `jdbc:mysql://host.docker.internal:3306/cpu_db` | Full url to a MySQL database |
| `SPRING_DATASOURCE_USERNAME` | `root`                                          | Username and usually root    |
| `SPRING_DATASOURCE_PASSWORD` | `password`                                      | Custom password              |

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

## Dataset Update

All updates to the dataset need to carefully reviewed. The columns are mapped by their __header name__ in the
corresponding [Dataset Class](dataset-api/src/main/java/cpu/spec/dataset/api/dataset) for the __Dataset API__.
If a regenerated dataset renames or drops a mapped column, the dataset tests fail:

```shell
cd dataset-api
mvn test -Dtest="CsvMapperTest,*DatasetTest"
```

### [AMD Dataset](dataset/amd-cpus.csv)

Manually __Export Data__ as __CSV/Excel__
from [https://www.amd.com/en/products/specifications/processors.html](https://www.amd.com/en/products/specifications/processors.html)

<details>
  <summary>Website Screenshot</summary>

![amd-csv-export](.docs/amd-csv-export.png)

</details>

### [Ampere Dataset](dataset/ampere-cpus.csv)

Extracted from
[https://amperecomputing.com/briefs/ampere-altra-family-product-brief](https://amperecomputing.com/briefs/ampere-altra-family-product-brief)
with the responsible __Web Scraper__.

```shell
cd ampere-web-scraper
java -jar ./target/executable.jar
```

### [Intel Dataset](dataset/intel-cpus.csv)

Extracted from
[https://www.intel.com/content/www/us/en/ark.html](https://www.intel.com/content/www/us/en/ark.html)
with the responsible __Web Scraper__.

```shell
cd intel-web-scraper
java -jar ./target/executable.jar
```

### [CPU Benchmark Dataset](dataset/benchmark-cpus.csv)

Extracted from [https://www.cpubenchmark.net/cpu_list.php](https://www.cpubenchmark.net/cpu_list.php)
with the responsible __Web Scraper__.

```shell
cd benchmark-web-scraper
java -jar ./target/executable.jar
```

### [CPU World Dataset](dataset/cpuworld-cpus.csv)

Extracted from [https://www.cpu-world.com/CPUs/CPU.html](https://www.cpu-world.com/CPUs/CPU.html)
with the responsible __Web Scraper__.

```shell
cd cpuworld-web-scraper
java -jar ./target/executable.jar
```
