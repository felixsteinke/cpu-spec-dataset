#!/usr/bin/env bash
# Recreates the datasets: builds the project, runs the web scrapers and validates the datasets for the Dataset API.
#
# Usage:   ./scripts/scrape-all.sh [scraper ...]
#          scrapers: amd ampere benchmark intel cpuworld (default: all, fast ones first)
# Options: JAVA_OPTS="..." ./scripts/scrape-all.sh
#          -Dscraper.limit=5     smoke test: only 5 detail pages, output to dataset/*.limited.csv
#          -Dscraper.delay=500   wait 500 ms between detail page requests (intel)
#          -Dscraper.force=true  replace a dataset even if the new run has much fewer records
#          -Djavax.net.ssl.trustStoreType=Windows-ROOT   behind a TLS-intercepting proxy on Windows
#
# Logs are written to logs/<scraper>-web-scraper.log and logs/dataset-api-validation.log.
set -u
cd "$(dirname "$0")/.."

SCRAPERS=("$@")
if [ ${#SCRAPERS[@]} -eq 0 ]; then
  SCRAPERS=(amd ampere benchmark intel cpuworld)
fi
JAVA_OPTS="${JAVA_OPTS:-}"
mkdir -p logs

echo "== Build (mvn clean install -DskipTests)"
if ! mvn -B -ntp -q clean install -DskipTests > logs/build.log 2>&1; then
  echo "Build failed, see logs/build.log"
  exit 1
fi

RESULTS=()  # "scraper|result" (no associative arrays: macOS ships bash 3.2)
for scraper in "${SCRAPERS[@]}"; do
  module="$scraper-web-scraper"
  log="logs/$module.log"
  if [ ! -f "$module/target/executable.jar" ]; then
    echo "== $module: unknown scraper"
    RESULTS+=("$scraper|UNKNOWN")
    continue
  fi
  echo "== $module (log: $log)"
  start=$(date +%s)
  # shellcheck disable=SC2086
  if (cd "$module" && java $JAVA_OPTS -jar target/executable.jar) > "$log" 2>&1; then
    RESULTS+=("$scraper|OK ($(( $(date +%s) - start ))s)")
  else
    RESULTS+=("$scraper|FAILED ($(( $(date +%s) - start ))s)")
  fi
  grep -h -E "Extracted [0-9]+|Output at|SEVERE|incomplete|Smoke test|Exception in thread" "$log" \
    | sed 's/\x1b\[[0-9;]*m//g' | cut -c1-220 | tail -n 4
done

echo "== Validate datasets for the Dataset API"
mkdir -p dataset-api/src/main/resources/dataset
cp dataset/*-cpus.csv dataset-api/src/main/resources/dataset/
if mvn -B -ntp -o test -pl dataset-api -Dtest='CsvMapperTest,*DatasetTest' -Dsurefire.failIfNoSpecifiedTests=false \
    > logs/dataset-api-validation.log 2>&1; then
  VALIDATION="OK"
else
  VALIDATION="FAILED (see logs/dataset-api-validation.log)"
fi

echo
echo "== Summary"
for result in "${RESULTS[@]}"; do
  printf "  %-10s %s\n" "${result%%|*}" "${result#*|}"
done
printf "  %-10s %s\n" "validation" "$VALIDATION"
for file in dataset/*.incomplete.csv dataset/*.limited.csv; do
  [ -e "$file" ] && echo "  note: $file was written instead of the dataset (see the scraper log)"
done
echo "  Review the changes with: git diff --stat dataset/"
