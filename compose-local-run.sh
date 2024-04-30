# /bin/sh

basedir="$(dirname -- "$(realpath -- "${BASH_SOURCE[0]}")")"
echo $basedir
gradle build
cd app
docker build -t hntoplinks:latest .
cd $basedir
docker-compose --project-name hntoplinks-app -f ${basedir}/infra/local/docker-compose.yml up -d

echo "Local environment is up and running at http://localhost:8066"
