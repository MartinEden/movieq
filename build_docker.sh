#!/usr/bin/env bash
if [ $# -lt 1 ]; then
    echo Usage: 
    echo "    build_docker.sh VERSION"
    exit 1
fi

set -ex
docker build . -t martinseden/movieq:$1
docker push martinseden/movieq:$1
