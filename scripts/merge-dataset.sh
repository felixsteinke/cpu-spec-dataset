#!/usr/bin/env bash
# Merges the records of a scraper run into its dataset, the same way the scrapers do by default: records of CPUs in the
# dataset replace their row, new CPUs are appended, CPUs without a record keep their row (see DatasetMerge.java).
# E.g. for an incomplete run of an older version, which wrote dataset/*.incomplete.csv instead of merging it.
#
# Usage:   ./scripts/merge-dataset.sh <records.csv> [dataset.csv]
#          dataset: default is the records file without .incomplete or .limited,
#          e.g. dataset/intel-cpus.incomplete.csv -> dataset/intel-cpus.csv
# Exit:    0 merged, 1 invalid arguments, build or merge failed (the dataset is unchanged)
set -eu
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

if [ $# -lt 1 ] || [ $# -gt 2 ]; then
  echo "Usage: $0 <records.csv> [dataset.csv]"
  exit 1
fi
records="$1"
dataset="${2:-$(echo "$records" | sed -E 's/\.(incomplete|limited)\.csv$/.csv/')}"
if [ "$records" = "$dataset" ]; then
  echo "The records file is the dataset, pass the dataset explicitly: $0 <records.csv> <dataset.csv>"
  exit 1
fi
for file in "$records" "$dataset"; do
  if [ ! -f "$file" ]; then
    echo "File not found: $file"
    exit 1
  fi
done

echo "== Build web-scraper-library"
mvn -B -ntp -q -f "$ROOT/pom.xml" -pl web-scraper-library -am compile

echo "== Merge $records into $dataset"
java -cp "$ROOT/web-scraper-library/target/classes" cpu.spec.scraper.file.DatasetMerge "$records" "$dataset"
echo "Review the changes with: git diff --stat $dataset (and validate them with the Dataset API tests, see README)"
