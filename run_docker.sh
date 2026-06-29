#!/usr/bin/env bash
if [ $# -lt 1 ]; then
    echo Usage: 
    echo "    run_docker.sh VERSION"
    echo "    e.g. run_docker.sh latest"
    exit 1
fi

container_name=movieq

set -e
echo "DO NOT RUN AS ROOT"

data_path=${HOME}/.movieq
mkdir -p "$data_path"

db_path=$data_path/db
echo "- Create blank database at $db_path"
touch "$db_path"

thumbnail_path=$data_path/thumbnails/
echo "- Create thumbnail store at $thumbnail_path"
mkdir -p "$thumbnail_path"

timestamp=$(date +"%Y-%m-%dT%H%M%S")
backup_path="$data_path/$timestamp.db"
echo "- Backup database to $backup_path"
cp $db_path $backup_path

image="martinseden/movieq:$1"
echo "- Get docker image '$image' from repository"
docker pull "$image"

echo "- Stop and remove existing container (if any)" 
docker stop $container_name && docker rm $container_name

echo "- Apply any necessary database migrations"
docker run --rm -it \
    --mount type=bind,source="$db_path",target=/code/movieq.db \
    "$image" migrate

echo "- Start new container"
docker run \
    -p 127.0.0.1:8080:8080 \
    -d \
    --restart unless-stopped \
    --name $container_name \
    --mount type=bind,source="$db_path",target=/code/movieq.db \
    --mount type=bind,source="$thumbnail_path",target=/code/static/thumbnails \
    "$image"

echo "MovieQ is now running at port 8080"

