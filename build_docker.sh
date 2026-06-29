#!/usr/bin/env bash
if [ $# -lt 1 ]; then
    echo Usage: 
    echo "    build_docker.sh VERSION"
    exit 1
fi

name=martinseden/movieq
full_name=$name:$1

set -ex
docker build . -t $full_name
docker tag $full_name $name:latest
docker push $full_name
docker push $name:latest
