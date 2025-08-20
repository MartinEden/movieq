#!/usr/bin/env bash
if [ $# -lt 1 ]; then
    echo Usage: 
    echo "    run_docker.sh VERSION"
    echo "    e.g. run_docker.sh latest"
    exit 1
fi

set -ex

data_path=${HOME}/.movieq
mkdir -p "$data_path"

db_path=$data_path/db
touch "$db_path"

thumbnail_path=$data_path/thumbnails/
mkdir -p "$thumbnail_path"

touch "$db_path"

docker pull "martinseden/movieq:$1"
docker run \
    -p 127.0.0.1:8080:8080 \
    --rm \
    --name movieq \
    --mount type=bind,source="$db_path",target=/code/.movieq.db \
    --mount type=bind,source="$thumbnail_path",target=/code/build/resources/main/static/thumbnails \
    "martinseden/movieq:$1"


