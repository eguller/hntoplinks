# /bin/sh


basedir=$(dirname "$0")
gradle build
cd app
docker build -t hntoplinks:latest .
cd $basedir
docker-compose --project-name hntoplinks-app -f ${basedir}/infra/local/docker-compose-db.yml up -d

echo "Local environment is up and running at http://localhost:8066"
