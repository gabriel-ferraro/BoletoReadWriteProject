@echo off
@REM Inicia container
docker-compose up -d

@REM Executa query de adição no DB
docker exec -i postgresDB psql -U postgres postgres < ../databaseFiles/createClientesTable.sql

@REM Executa a aplicação java para carregamento dos dados XLSM no DB usando o gradlew
cd ../XLSMtoPostgresDB
./gradlew run
