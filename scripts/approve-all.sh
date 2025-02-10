#!/usr/bin/env bash

pushd ../libraries/renderer/src/test/resources/io/pdf4k/approval
for file in *.actual.pdf; do
  mv -- "$file" "${file%.actual.pdf}.approved.pdf"
done
popd