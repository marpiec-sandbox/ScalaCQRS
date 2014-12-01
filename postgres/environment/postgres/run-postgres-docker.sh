#!/bin/bash
docker rm event-store-postgres
docker run --name event-store-postgres -e POSTGRES_PASSWORD=eventstore -e POSTGRES_USER=eventstore -p 5432:5432 -v /tmp/event-store-postgres-data:/var/lib/postgresql/data -d postgres:9.3.5