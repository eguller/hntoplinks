# /bin/sh


basedir=$(dirname "$0")
docker-compose --project-name hntoplinks-db -f ${basedir}/infra/local/docker-compose-db.yml up -d

echo "Local environment is up and running at http://localhost:8067"
