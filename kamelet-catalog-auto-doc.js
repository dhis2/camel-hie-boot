const yaml = require("yaml");
const fs = require("fs");
const handlebars = require("handlebars");
const prettier = require("prettier");
const path = require("path");

function inlineRequired(items, required) {
    Object.entries(items).forEach(function([itemName, itemDefinition]) {
        itemDefinition.required = required.includes(itemName);
    });
}

const main = async function() {
    const kameletDirPath = "src/main/resources/kamelets";
    const kamelets = fs.readdirSync(kameletDirPath).map(function(fileName) {
        const kamelet = yaml.parse(fs.readFileSync(path.join(kameletDirPath, fileName), "utf8"));

        const {
            properties = {},
            required: requiredProperties = []
        } = kamelet.spec.definition;
        inlineRequired(properties, requiredProperties);

        if (kamelet.spec?.dataTypes?.in) {
            const {
                headers = {},
                required: requiredHeaders = []
            } = kamelet.spec.dataTypes.in;
            inlineRequired(headers, requiredHeaders);
        }

        return kamelet;
    });

    handlebars.registerHelper("noSpaces", function(input) {
        return input.toLowerCase().replaceAll(" ", "-");
    });
    const catalogTemplate = handlebars.compile(fs.readFileSync("kamelet-catalog.md.handlebars", "utf8"));
    const catalog = catalogTemplate(kamelets);
    fs.writeFile("kamelet-catalog.md", await prettier.format(catalog, {
        parser: "markdown"
    }), function(err) {
        if (err) {
            return console.log(err);
        }
    });
};

main();