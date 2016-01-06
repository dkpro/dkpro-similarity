/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package dkpro.similarity.algorithms.wikipedia.measures;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import dkpro.similarity.algorithms.wikipedia.measures.WikiLinkComparator;

@Ignore("This is a performance test - excluding from normal junit runs")
public class WikiLinkPerformanceTest {

    private static final int CYCLES = 5;

    @Test
    @Ignore
    public void test()
        throws Exception
    {
        DatabaseConfiguration db = new DatabaseConfiguration();
        db.setDatabase("wikiapi_de");
        db.setHost("bender.ukp.informatik.tu-darmstadt.de");
        db.setUser("student");
        db.setPassword("student");
        db.setLanguage(Language.german);
        Wikipedia wiki = new Wikipedia(db);

        WikiLinkComparator wlmc = new WikiLinkComparator(wiki);

        // do not use cache, as this interferes with correct testing
        // correct behaviour of caching should be tested in another method
        wlmc.setUseCache(false);

        String[] gerTokens1 = testDocumentShortGer1.split(" ");
        System.out.println(gerTokens1.length);


        WikiLinkPerformanceTest.fullyConnectedTest(wlmc, gerTokens1, CYCLES);

    }

    private static void fullyConnectedTest(WikiLinkComparator comparator, String[] tokens, int cycles)
        throws Exception
    {
        double cycleSum = 0.0;
        for (int c=0; c<cycles; c++) {
            System.out.print(".");
            long start = System.currentTimeMillis();
            for (int i=0; i<tokens.length; i++) {
                for (int j=i; j<tokens.length; j++) {
                    comparator.getSimilarity(tokens[i], tokens[j]);
                }
            }
            cycleSum += System.currentTimeMillis() - start;
        }
        System.out.println();
        double cycleAvg = cycleSum / cycles;
        System.out.println("WLM fully connected: " + cycleAvg + "ms");
        System.out.println();
    }

    private final static String testDocumentGer1 = "Die Elektrizitätskonzerne haben bis April den Strompreis für einen Durchschnittshaushalt im Vergleich zum vorigen Jahr um 7 Prozent erhöht. Im April 2009 zahlte eine dreiköpfige Familie demnach im Schnitt 67,70 Euro oder 4,55 Euro mehr als 2008. Das geht aus bisher unveröffentlichten Unterlagen des Bundesverbandes der Energie- und Wasserwirtschaft (BDEW) hervor, die der Nachrichtenagentur AP vorliegen." +
    "Die allgemeine Teuerungsrate lag in dem Zeitraum nur bei 0,5 Prozent, die Preise für Öl und Gas gingen sogar zurück. Der Strompreis ist damit 2009 auf den höchsten Stand seit der Freigabe Ende der 90er Jahre gestiegen: Im Jahr 2000 zahlte eine dreiköpfige Familie mit einem Jahresverbrauch von 3.500 Kilowattstunden noch 40,66 Euro pro Monat. Das bedeutet einen Preisanstieg bis heute um mehr als 55 Prozent." +
    "Erst am Montag hatte der BDEW auf der Hannovermesse mitgeteilt, dass Industrieunternehmen derzeit bei Neuverträgen rund 26 Prozent weniger für Strom zahlten als noch im Oktober 2008. Verbandschefin Hildegard Müller hatte den Widerspruch zum Preisanstieg bei Privatkunden damit erklärt, dass die Versorger an der Leipziger Strombörse Strom für Haushalte zum Großteil ein bis zwei Jahre im Voraus kauften. Sie hätten sich eingedeckt, als dort die Preise noch hoch gewesen seien." +
    "Müller sagte auch, langfristig würden in Privathaushalten die Preise fallen, wenn die Großhandelspreise an der Börse niedrig blieben. Die fallenden Preise für Industriekunden würden nur bei Neuabschlüssen von Lieferverträgen gelten, sagte sie. Im Jahr 2009 ist der Stromverbrauch bis Ende März schon um vier Prozent zurückgegangen." +
    "Das Bundeskartellamt geht seit vergangener Woche dem Verdacht nach, dass die großen Unternehmen durch absichtliche Verknappung der Strommengen die Preise an der Leipziger Strombörse EEX und im Großhandel zulasten der Verbraucher künstlich verteuert haben könnten. Die 60 größten Unternehmen der Branche müssen der Behörde bis zum 6. Mai Auskunft über Kosten der Stromproduktion, die Einsatzplanung der Kraftwerke und ihr Angebotsverhalten auf den Großhandelsmärkten in den Jahren 2007 und 2008 geben. Hintergrund: Rund 80 Prozent der deutschen Stromproduktion liegt in der Hand von nur vier Konzernen, nämlich RWE, E.ON, Vattenfall und EnBW." +
    "Verbraucherschützer sehen darin einen der Hauptgründe für die hohen Strompreise in Deutschland. Der Wechsel zu einem anderen Stromlieferanten kann für einen Durchschnittshaushalt eine Ersparnis von 300 Euro pro Jahr bringen. Die Bundesnetzagentur fordert die Kunden auf, stärker von dieser Möglichkeit Gebrauch zu machen. Vor allem bei Preiserhöhungen sollten die Kunden alternative Angebote prüfen. Bei Haushaltskunden haben 2007 nur 4,23 Prozent den Stromanbieter gewechselt." +
    "Der BDEW erklärt den massiven Anstieg der Strompreise über die Jahre mit hohen staatlichen Abgaben auf Energie: Der Betrag für Steuern und Abgaben wuchs seit 1998 pro Musterfamilie von gut 12 Euro auf 26 Euro. Damit gingen 40 Prozent der Stromrechnung einer Familie an den Staat, hieß es.";

    private final static String testDocumentShortGer1 = "Die Elektrizitätskonzerne haben bis April den Strompreis für einen Durchschnittshaushalt im Vergleich zum vorigen Jahr um 7 Prozent erhöht. Im April 2009 zahlte eine dreiköpfige Familie demnach im Schnitt 67,70 Euro oder 4,55 Euro mehr als 2008. Das geht aus bisher unveröffentlichten Unterlagen des Bundesverbandes der Energie- und Wasserwirtschaft (BDEW) hervor, die der Nachrichtenagentur AP vorliegen." +
    "Die allgemeine Teuerungsrate lag in dem Zeitraum nur bei 0,5 Prozent, die Preise für Öl und Gas gingen sogar zurück. Der Strompreis ist damit 2009 auf den höchsten Stand seit der Freigabe Ende der 90er Jahre gestiegen: Im Jahr 2000 zahlte eine dreiköpfige Familie mit einem Jahresverbrauch von 3.500 Kilowattstunden noch 40,66 Euro pro Monat. Das bedeutet einen Preisanstieg bis heute um mehr als 55 Prozent.";

    private final static String testDocumentShortEng1 = "The Venice giardini is an area of parkland in the historic city of Venice which hosts the Venice Art Festival a major part of the city's cultural biennale The gardens were created by Napoleon Bonaparte who drained an area of marshland in order to create a public garden on the banks of the Bacino di San Marco which is a narrow stretch of water dividing the gardens from St Mark's Square and the Doge's Palace"
        + "The gardens contain thirty permanent pavilions Each pavilion is allocated to a particular nation and displays works of art by its nationals Some of the pavilions have fallen into a state of decay and there is some criticism of the allocation of pavilions to certain nations who do not appear to have the funds required for structural maintenance and repair";

    private final static String testDocumentShortEng2 = "The gardens are also sometimes criticised for having become a stifling and cramped architectural museum since the pavilions are constructed in distinct but extremely disparate architectural styles and their collection in a relatively confined space can have a jarring even irritating effect leading many to query whether the area could be better used as a more open and relaxed recreational space"
        + "The gardens are also famous for the many cats which run wild in the vicinity and for some of the sculptures such as the statue of Garibaldi situated at the entrance";


}
