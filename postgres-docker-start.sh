docker run -d \
  --name postgres_tayyib \
  -e POSTGRES_DB=tayyib \
  -e POSTGRES_USER=tayyib \
  -e POSTGRES_PASSWORD=tayyib_local \
  -e PGDATA=/var/lib/postgresql/data/tayyib \
  -v pg-data-tayyib:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres:16.3
