const yaml = require('yaml');
const fs = require('fs');
const handlebars = require('handlebars');
const prettier = require('prettier');

const main = async () => {
    const kameletDirPath = 'src/main/resources/kamelets';
    const kamelets = fs.readdirSync(kameletDirPath).map(
        fileName => {
            return yaml.parse(fs.readFileSync(kameletDirPath + '/' + fileName, 'utf8'));
        }
    );
    const catalogTemplate = handlebars.compile(fs.readFileSync('kamelet-catalog.md.handlebars', 'utf8'));
    const catalog = catalogTemplate(kamelets);
    fs.writeFile("kamelet-catalog.md", await prettier.format(catalog, {
        parser: "markdown"
    }), function(err) {
        if (err) {
            return console.log(err);
        }
    });
}

main();