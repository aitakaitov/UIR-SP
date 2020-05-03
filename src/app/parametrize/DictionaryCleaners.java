package app.parametrize;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Offers static functions, that clean dictionaries with the aim
 * to reduce overall noise
 * Intended to be used in sequence (e.g. clean dictionary from numbers, then remove low occurrences)
 */
public class DictionaryCleaners
{
    private static ArrayList<String> stopWords = new ArrayList<>(List.of(
           "ačkoli", "ahoj", "ale", "anebo", "ano", "asi", "aspoň", "během", "bez", "beze", "blízko", "bohužel", "brzo", "bude", "budeme", "budeš", "budete",
            "budou", "budu", "byl", "byla", "byli", "bylo", "byly", "bys", "čau", "chce", "chceme", "chceš", "chcete", "chci", "chtějí", "chtít", "chuť", "chuti",
            "co", "čtrnáct", "čtyři", "dál", "dále", "daleko", "děkovat", "děkujeme", "děkuji", "den", "deset", "devatenáct", "devět", "dík", "díky",
            "dle", "do", "dobrý", "docela", "dva", "dvacet", "dvanáct", "dvě", "hodně", "já", "jak", "jde", "je", "jeden", "jedenáct", "jedna", "jedno", "jednou", "jedou",
            "jeho", "její", "jejich", "jemu", "jen", "jenom", "ještě", "jestli", "jestliže", "jí", "jich", "jím", "jimi", "jinak", "jsem", "jsi", "jsme", "jsou", "jste", "kam",
            "kde", "kdo", "kdy", "když", "k", "ke", "kol", "kolem", "kolik", "kromě", "která", "které", "kteří", "který", "kvůli", "má", "mají", "málo", "mám", "máme", "máš", "máte", "mé", "mě", "mezi", "mí", "mít", "mně",
            "mnou", "moc", "mohl", "mohou", "moje", "moji", "možná", "můj", "musí", "může", "my", "na", "nad", "nade", "nám", "námi", "naproti", "nás", "náš", "naše", "naši", "ne", "ně", "nebo", "nebyl", "nebyla", "nebyli",
            "nebyly", "něco", "nedělá", "nedělají", "nedělám", "neděláme", "neděláš", "neděláte", "nějak", "nejsi", "někde", "někdo", "nemají", "nemáme", "nemáte", "neměl", "němu",
            "není", "nestačí", "nevadí", "než", "nic", "nich", "ním", "nimi", "nula", "o", "od", "ode", "okolo", "on", "ona", "oni", "ono", "ony", "oproti", "osm", "osmnáct", "pak", "patnáct", "pět", "po", "pořád",
            "potom", "pozdě", "před", "přes", "přese", "pro", "proč", "prosím", "prostě", "proti", "protože", "rovně", "se", "sedm", "sedmnáct", "šest", "šestnáct", "skoro", "smějí", "smí", "snad",
            "spolu", "sta", "sté", "sto", "ta", "tady", "tak", "takhle", "taky", "tam", "tamhle", "tamhleto", "tamto", "tě", "tebe", "tebou", "ted", "tedy", "ten", "ti", "tisíc", "tisíce", "to", "tobě",
            "tohle", "toto", "třeba", "tři", "třináct", "trošku", "tvá", "tvé", "tvoje", "tvůj", "ty", "určitě", "už", "vám", "vámi", "vás", "váš", "vaše", "vaši", "včetně", "ve", "večer",
            "vedle", "vlastně", "všechno", "všichni", "vůbec", "vy", "vždy", "za", "zač", "zatímco", "ze", "že"
    ));

    /**
     * Cleans dictionary
     * Removes stop words
     * @param map dictionary to clean
     * @return cleaned dictionary
     */
    public static TreeMap<String, Integer> removeStopWords(TreeMap<String, Integer> map)
    {
        TreeMap<String, Integer> newMap = new TreeMap<>();

        for (Map.Entry<String, Integer> e : map.entrySet())
        {
            if (stopWords.contains(e.getKey()))
            {
                continue;
            }

            newMap.put(e.getKey(), e.getValue());
        }

        return newMap;
    }

    /**
     * Cleans dictionary
     * Eliminates all Strings containing numbers
     * @param map dictionary to clean
     * @return cleaned dictionary
     */
    public static TreeMap<String, Integer> cleanDictNumbersInStrings(TreeMap<String, Integer> map)
    {
        TreeMap<String, Integer> newMap = new TreeMap<>();

        for (Map.Entry<String, Integer> e : map.entrySet())
        {
            if (e.getKey().matches(".*\\d+.*"))
            {
                continue;
            }

            newMap.put(e.getKey(), e.getValue());
        }

        return newMap;
    }

    /**
     * Cleans dictionary
     * Eliminates all Strings with less than N occurences
     * @param map dictionary to clean
     * @return cleaned dictionary
     */
    public static TreeMap<String, Integer> cleanDictStringOccurrences(TreeMap<String, Integer> map, int minOccurences)
    {
        TreeMap<String, Integer> newMap = new TreeMap<>();

        for (Map.Entry<String, Integer> e : map.entrySet())
        {
            if (e.getValue() < minOccurences)
            {
                continue;
            }

            newMap.put(e.getKey(), e.getValue());
        }

        return newMap;
    }

     /**
     * Cleans dictionary
     * Eliminates all Strings with less than N occurences
     * @param map dictionary to clean
     * @return cleaned dictionary
     */
    public static TreeMap<String, Integer> removeWhitespaces(TreeMap<String, Integer> map)
    {
        TreeMap<String, Integer> newMap = new TreeMap<>();

        for (Map.Entry<String, Integer> e : map.entrySet())
        {
            if (e.getKey().matches("\\s+"))
            {
                continue;
            }

            newMap.put(e.getKey(), e.getValue());
        }

        return newMap;
    }
}
