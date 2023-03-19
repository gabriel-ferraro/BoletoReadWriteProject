@echo off
@REM Inicia container
docker-compose up -d

@REM Executa query de adição no DB
docker exec -i postgresDBWriter psql -U admin postgresDBWriter < ../databaseFiles/createClientesTable.sql

@REM Executa a aplicação java para carregamento dos dados XLSM no DB usando o gradlew run
cd ../XLSMtoPostgresDB
./gradlew run
