@echo off
@REM Inicia container
docker-compose up --build

@REM Executa a aplicação java para carregamento dos dados XLSM no DB usando o gradlew
cd XLSMtoPostgresDB
./gradlew run

@REM Executa a aplicação java para carregamento dos dados das relações nos arquivos de remessa cnab240
cd ../boletoWriterJava
./gradlew run

pause
