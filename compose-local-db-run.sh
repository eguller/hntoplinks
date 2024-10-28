# /bin/sh


basedir=$(dirname "$0")
docker-compose --project-name hntoplinks-db -f ${basedir}/infra/local/docker-compose-db.yml up -d

echo "Local Postgres database running at localhost:15432"
