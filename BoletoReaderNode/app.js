const fs = require('fs');
const path = require('path');

const directoryPath = path.join(__dirname, '../hotFolder');

fs.readdir(directoryPath, (err, files) => {
    if (err) {
        return console.log(`Erro ao ler diretório: ${err}`);
    }

    const filteredFiles = files.filter((file) =>
        fs.statSync(path.join(directoryPath, file)).isFile()
    );

    filteredFiles.forEach((file) => {
        // Adquirindo arquivo e linhas das remessas
        const filePath = path.join(directoryPath, file);
        const arquivo = fs.readFileSync(filePath, 'utf-8');
        const linhas = arquivo.split('\n');
        // "Extraindo" valores das remessas
        const dia = linhas[0].slice(143, 145);
        const mes = linhas[0].slice(145, 147);
        const ano = linhas[0].slice(147, 151);
        const preco = linhas[2].slice(86, 98);
        const centavos = linhas[2].slice(98, 100);
        const diaVencimento = linhas[2].slice(77, 79);
        const mesVencimento = linhas[2].slice(79, 81);
        const anoVencimento = linhas[2].slice(81, 85);
        // const diaVencimento = linhas[1].slice(191, 193);
        // const mesVencimento = linhas[1].slice(193, 195);
        // const anoVencimento = linhas[1].slice(195, 199);
        const nomePagador = linhas[3].slice(33, 73);
        const nomeBeneficiario = linhas[0].slice(72, 102);
        const valorTitulo = Number(`${preco}.${centavos}`);

        const dadosRemessa = {
            remessa: file,
            dataBoleto: `${dia}/${mes}/${ano}`,
            nomeBeneficiario: nomeBeneficiario.trimEnd(),
            nomePagador: nomePagador.trimEnd(),
            dataGeracao: `${dia}/${mes}/${ano}`.trimEnd(),
            dataVencimento: `${diaVencimento}/${mesVencimento}/${anoVencimento}`.trimEnd(),
            valor: `R$ ${valorTitulo}`
        };

        console.log(dadosRemessa);
    });
});
