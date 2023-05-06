# CPU Specification Dataset

Dataset for CPU specifications from Intel and AMD.

## Table of Content

* [Raw Dataset](dataset)
* [Dataset API Code](dataset-api/src/main/java/cpu/spec/dataset/api)
* [Dataset API Configs](dataset-api/src/main/resources)
* [Intel Web Scraper Code](intel-web-scraper/src/main/java/cpu/spec/scraper)

## Installation

Tested on __OpenJDK 17.0.2__ & __Maven 3.6.3__:

```shell
mvn clean install
```

## Dataset API Usage

```shell
cd dataset-api
mvn spring-boot:run
```

## Container Image

```shell
docker build -t cpu-spec-dataset-api .
docker run -p 8080:80 --name dataset-api cpu-spec-dataset-api
```

```shell
docker push ghcr.io/felixsteinke/cpu-power-calculator:latest
```

## Dataset Update

### Intel Dataset

```shell
cd intel-web-scraper
mvn exec:java
```

### [AMD Dataset](dataset/amd-cpus.csv)

__Export Data__ as __CSV/Excel__
from [https://www.amd.com/en/products/specifications/processors](https://www.amd.com/en/products/specifications/processors)

<details>
  <summary>Website Screenshot</summary>

![amd-csv-export](.docs/amd-csv-export.png)

</details>

