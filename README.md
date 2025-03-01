```sh
docker run --name postgres-container \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=templates \
  -p 5432:5432 \
  -d postgres:15-alpine
````

```sh
docker images
````

```sh
docker rmi afbf3abf6aeb
````

```sh
docker rm -f postgres-container
````