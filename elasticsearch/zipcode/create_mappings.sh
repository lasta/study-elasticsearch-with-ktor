#!/usr/bin/env bash
set -uo pipefail

readonly MAPPING_YAML='mappings.yaml'
readonly MAPPING_JSON='mappings.json'
readonly ES_HOST='http://localhost:9200'
readonly INDEX_NAME='zipcode'

function create_mappings_json_file() {
  rm "${MAPPING_JSON}"
  yq . "${MAPPING_YAML}" > "${MAPPING_JSON}"
}

function delete_mappings() {
  curl -X DELETE --location "${ES_HOST}/${INDEX_NAME}"
}

function create_mappings_into_elasticsearch() {
  curl -X PUT --location "${ES_HOST}/${INDEX_NAME}" \
    -H "Content-Type: application/json" \
    --data-binary "@${MAPPING_JSON}"
}

function main() {
  create_mappings_json_file || return $?
  delete_mappings || return $?
  create_mappings_into_elasticsearch || return $?
}

main
