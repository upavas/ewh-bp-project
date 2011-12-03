function xlatedString = getLabel(lang, label)

xlatedString = '???';
switch lang
    case 'en'
        xlatedString = EnglishStrings.(label);
    case 'pt'
        xlatedString = PortugueseStrings.(label);
    case 'fr'
        xlatedString = FrenchStrings.(label);
    case 'hi'
        xlatedString = HindiStrings.(label);
    case 'ne'
        xlatedString = NepaliStrings.(label);
%     case 'es'
%         xlatedString = SpanishStrings.(label);
%     case 'zh'
%         xlatedString = ChineseStrings.(label);
%     case 'am_ET'
%         xlatedString = AmharicStrings.(label);
%     case 'ar'
%         xlatedString = ArabicStrings.(label);
end
