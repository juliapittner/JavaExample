import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * A simple program that counts the number of times a word occurs in a given
 * input file. It then displays each word and how many times it occurs.
 *
 * @author Julia Pittner
 */
public final class WordCounter {
    /**
     *
     * Alphabetizes a Queue.
     *
     */

    private static class Alphabetize implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }

    /**
     * Default constructor--private to prevent instantiation.
     */
    private WordCounter() {

    }

    /**
     * Prints the opening tags in the HTML file.
     *
     * @param out
     *            file to be printed to
     * @param file
     *            input file
     */
    private static void printHeader(SimpleWriter out, String file) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + file + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Words Counted in " + file + "</h2>");
        out.println("<hr/>");
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<th>Words</th>");
        out.println("<th>Counts</th>");
        out.println("</tr>");

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int length = text.length();
        String wordOrSeparator = "";

        char character = text.charAt(position);
        boolean isSubset = separators.contains(character);

        if (isSubset) {
            while (isSubset && position < length) {
                character = text.charAt(position);
                wordOrSeparator += character;
                int len = position + 1;
                if (len < length) {
                    char test = text.charAt(position + 1);
                    isSubset = separators.contains(test);
                }
                position++;
            }
        } else {
            while (!isSubset && position < length) {
                character = text.charAt(position);
                wordOrSeparator += character;
                int len = position + 1;
                if (len < length) {
                    char test = text.charAt(position + 1);
                    isSubset = separators.contains(test);
                }

                position++;

            }
        }
        return wordOrSeparator;
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param stringSet
     *            the {@code Set} to be replaced
     * @replaces stringSet
     * @ensures stringSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> stringSet) {
        assert str != null : "Violation of: str is not null";
        assert stringSet != null : "Violation of: stringSet is not null";
        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);
            boolean isSubset = stringSet.contains(character);
            if (!isSubset) {
                stringSet.add(character);
            }

        }

    }

    /**
     * Counts the number of times each word occurs in the given input file and
     * puts the results in a HTML table in alphabetical order.
     *
     * @param words
     *            queue with words
     * @param wordMap
     *            map with the words and counts
     * @param outputName
     *            file to be printed to
     * @param a
     *            sorts the words from the queue in alphabetical order
     */

    public static void countWord(Queue<String> words,
            Map<String, Integer> wordMap, SimpleWriter outputName,
            Comparator<String> a) {

        while (words.length() > 0) {
            String word = words.dequeue();
            if (wordMap.hasKey(word)) {
                int value = wordMap.value(word);
                int newValue = value + 1;
                wordMap.replaceValue(word, newValue);

            } else {
                wordMap.add(word, 1);
            }
        }
        Map<String, Integer> newMap = wordMap.newInstance();
        Queue<String> newWords = words.newInstance();

        while (wordMap.size() != 0) {
            Map.Pair<String, Integer> tempPair = wordMap.removeAny();
            newMap.add(tempPair.key(), tempPair.value());
            newWords.enqueue(tempPair.key());
        }
        wordMap.clear();
        newWords.sort(a);

        while (newWords.iterator().hasNext()) {
            String word = newWords.dequeue();
            Map.Pair<String, Integer> alpha = newMap.remove(word);
            outputName.println("<tr>");
            outputName.println("<td>" + alpha.key() + "</td>");
            outputName.println("<td>" + alpha.value() + "</td>");
            outputName.println("</tr>");
        }
    }

    /**
     * Prints the closing tags of the HTML file.
     *
     * @param out
     *            file to be printed to
     */

    private static void outputFooter(SimpleWriter out) {
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Separates the words from the characters in the given input file.
     *
     * @param words
     *            queue with words
     * @param input
     *            file to be read
     * @param separatorsSet
     *            set containing separators
     * @return words
     */

    public static Queue<String> separateWords(SimpleReader input,
            Set<Character> separatorsSet, Queue<String> words) {

        while (!input.atEOS()) {
            String line = input.nextLine();
            int position = 0;
            while (position < line.length()) {
                String word = nextWordOrSeparator(line, position,
                        separatorsSet);
                if (!separatorsSet.contains(word.charAt(0))) {
                    words.enqueue(word);
                }
                position += word.length();
            }
        }
        return words;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        final String separatorString = "," + " " + ":" + "." + ";" + "-" + "?"
                + "!";
        Set<Character> separatorsSet = new Set1L<>();
        generateElements(separatorString, separatorsSet);

        SimpleWriter out = new SimpleWriter1L();
        SimpleReader in = new SimpleReader1L();

        out.println("Enter name of the input file: ");
        String inputFile = in.nextLine();
        out.println("Enter output folder: ");
        String output = in.nextLine();
        SimpleReader input = new SimpleReader1L(inputFile);

        Map<String, Integer> wordMap = new Map1L<>();
        Queue<String> words = new Queue1L<>();

        words = separateWords(input, separatorsSet, words);

        Comparator<String> a = new Alphabetize();
        words.sort(a);

        SimpleWriter outputName = new SimpleWriter1L(output + ".html");
        printHeader(outputName, inputFile);
        countWord(words, wordMap, outputName, a);
        outputFooter(outputName);

        in.close();
        out.close();
        outputName.close();
        input.close();
    }

}
