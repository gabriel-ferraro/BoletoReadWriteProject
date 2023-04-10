@REM Inicia container
@REM docker-compose up --build

@REM Executa a aplicação java para carregamento dos dados XLSM no DB usando o gradlew
cd XLSMtoPostgresDB
call gradlew run

@REM Executa a aplicação java para carregamento dos dados das relações nos arquivos de remessa cnab240
cd ../boletoWriterJAVA
call gradlew run

@echo Dados do XLSM carregados no BD postgres e registros armazenados como remessa cnab240

pause
