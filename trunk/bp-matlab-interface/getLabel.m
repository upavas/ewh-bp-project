function xlatedString = getLabel(lang, label)

xlatedString = '???';
switch lang
    case 'en'
        xlatedString = EnglishStrings.(label);
    case 'pt'
        xlatedString = GermanStrings.(label);
    case 'fr'
        xlatedString = FrenchStrings.(label);
end
