#!/usr/bin/env bash
# Recreates the datasets: builds the project, runs the web scrapers and validates the datasets for the Dataset API.
#
# Usage:   ./scripts/scrape-all.sh [scraper ...]
#          scrapers: amd ampere benchmark intel cpuworld (default: all, fast ones first)
# Options: JAVA_OPTS="..." ./scripts/scrape-all.sh
#          -Dscraper.limit=5     smoke test: only 5 pages per level, output to dataset/*.limited.csv
#          -Dscraper.delay=500   wait 500 ms between detail page requests (intel)
#          -Dscraper.force=true  replace a dataset even if the new run has much fewer records
# Exit:    0 all scrapers OK and datasets valid
#          1 unknown scraper, build or dataset validation failed (datasets must not be used)
#          2 at least one scraper failed, was incomplete or empty (the other datasets are valid)
#
# Logs are written to logs/<scraper>-web-scraper.log and logs/dataset-api-validation.log.
set -u
cd "$(dirname "$0")/.."

SCRAPERS=("$@")
if [ ${#SCRAPERS[@]} -eq 0 ]; then
  SCRAPERS=(amd ampere benchmark intel cpuworld)
fi
for scraper in "${SCRAPERS[@]}"; do
  if [ ! -f "$scraper-web-scraper/pom.xml" ]; then
    echo "Unknown scraper: $scraper (available: amd ampere benchmark intel cpuworld)"
    exit 1
  fi
done
JAVA_OPTS="${JAVA_OPTS:-}"
mkdir -p logs

# data rows of a csv file (non-empty lines without the header)
rows() {
  local lines
  lines=$(grep -c . "$1" 2>/dev/null) || lines=0
  echo $(( lines > 0 ? lines - 1 : 0 ))
}

echo "== Build (mvn clean install -DskipTests)"
if ! mvn -B -ntp -q clean install -DskipTests > logs/build.log 2>&1; then
  echo "Build failed, see logs/build.log"
  exit 1
fi

RESULTS=()  # "scraper|result" (no associative arrays: macOS ships bash 3.2)
for scraper in "${SCRAPERS[@]}"; do
  module="$scraper-web-scraper"
  log="logs/$module.log"
  dataset="dataset/$scraper-cpus"  # each scraper writes dataset/<scraper>-cpus[.limited|.incomplete].csv
  echo "== $module (log: $log)"
  rm -f "$dataset.limited.csv" "$dataset.incomplete.csv"  # outputs of previous runs
  start=$(date +%s)
  # shellcheck disable=SC2086
  (cd "$module" && java $JAVA_OPTS -jar target/executable.jar) > "$log" 2>&1
  exit_code=$?
  elapsed="$(( $(date +%s) - start ))s"
  if [ $exit_code -ne 0 ]; then
    RESULTS+=("$scraper|FAILED ($elapsed)")
  elif [ -e "$dataset.incomplete.csv" ]; then
    RESULTS+=("$scraper|INCOMPLETE ($(rows "$dataset.incomplete.csv") rows, $elapsed)")
  else
    output="$dataset.csv"
    [ -e "$dataset.limited.csv" ] && output="$dataset.limited.csv"
    count=$(rows "$output")
    if [ "$count" -eq 0 ]; then
      RESULTS+=("$scraper|EMPTY (0 rows, $elapsed)")
    else
      RESULTS+=("$scraper|OK ($count rows, $elapsed)")
    fi
  fi
  grep -h -E "Extracted [0-9]+|Output at|SEVERE|incomplete|Smoke test|Exception in thread" "$log" \
    | sed 's/\x1b\[[0-9;]*m//g' | cut -c1-220 | tail -n 4
done

echo "== Validate datasets for the Dataset API"
mkdir -p dataset-api/src/main/resources/dataset
cp dataset/*-cpus.csv dataset-api/src/main/resources/dataset/
if mvn -B -ntp test -pl dataset-api -Dtest='CsvMapperTest,*DatasetTest' -Dsurefire.failIfNoSpecifiedTests=false \
    > logs/dataset-api-validation.log 2>&1; then
  VALIDATION="OK"
else
  VALIDATION="FAILED (see logs/dataset-api-validation.log)"
fi

echo
echo "== Summary"
STATUS=0
for result in "${RESULTS[@]}"; do
  printf "  %-10s %s\n" "${result%%|*}" "${result#*|}"
  case "${result#*|}" in OK*) ;; *) STATUS=2 ;; esac
done
printf "  %-10s %s\n" "validation" "$VALIDATION"
[ "$VALIDATION" = "OK" ] || STATUS=1
for scraper in "${SCRAPERS[@]}"; do
  for file in "dataset/$scraper-cpus.incomplete.csv" "dataset/$scraper-cpus.limited.csv"; do
    [ -e "$file" ] && echo "  note: $file was written instead of the dataset (see the scraper log)"
  done
done
echo "  Review the changes with: git diff --stat dataset/"
exit $STATUS
