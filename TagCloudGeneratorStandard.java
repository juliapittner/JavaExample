import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Creates a tag cloud for a given input file at a given output location. The
 * tag cloud is in all lowercase and is in alphabetical order.
 *
 * @author Julia Pittner
 *
 */
public final class TagCloudGeneratorStandard {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGeneratorStandard() {
    }

    /**
     * Compare values of Map.Pair<String, Integer>s.
     */
    private static class IntegerValue
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }

    /**
     * Reads a file into a map of form word -> word count.
     *
     * @param fileReader
     *            the input file
     * @param wordAndCount
     *            holds the words and word counts from the input file
     * @throws IOException
     * @replaces wordAndCount
     * @requires <pre> fileName is a valid file location file located at
     * fileName is a text file </pre>
     * @ensures <pre> wordAndCount contains word -> frequency of word in file
     * </pre>
     */
    private static void readInputFile(BufferedReader fileReader,
            Map<String, Integer> wordAndCount) throws IOException {

        wordAndCount.clear();
        Set<Character> separators = new HashSet<Character>();
        generateElements(" \t\n\r,-.!?[]';:/()", separators);
        String line = fileReader.readLine();
        while (line != null) {
            int index = 0;
            while (index < line.length()) {
                String word = nextWordOrSeparator(line, index, separators);
                index += word.length();
                //skip separators
                if (!separators.contains(word.charAt(0))) {
                    word = word.toLowerCase();
                    addOrIncrement(word, wordAndCount);
                }
            }
            line = fileReader.readLine();
        }

        fileReader.close();
    }

    /**
     * adds word to wordAndCount or increments the value associated with word if
     * already in wordAndCount.
     *
     * @param word
     *            the key to add or increment
     * @param wordAndCount
     *            the map to add or increment word in
     * @ensures <pre> if wordAndCount does not contain the key word, word will
     *          be added with value 1.
     *          if wordAndCount does contain the key word, the value associated
     *          with word will be incremented.
     * </pre>
     */
    private static void addOrIncrement(String word,
            Map<String, Integer> wordAndCount) {

        if (!wordAndCount.containsKey(word)) {
            wordAndCount.put(word, 1);
        } else {
            incrementWordCount(word, wordAndCount);
        }

    }

    /**
     * increments the value(word count) associated with the given key(word).
     *
     * @param key
     *            the word with which to increase the count
     * @param wordAndCount
     *            holds the words and word counts from the input file
     * @requires <pre>
     * wordAndCount has key
     * </pre>
     *
     */
    private static void incrementWordCount(String key,
            Map<String, Integer> wordAndCount) {
        int oldCount = wordAndCount.remove(key);
        int newCount = oldCount + 1;
        wordAndCount.put(key, newCount);
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
     * text[position, position + |nextWordOrSeparator|) and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     * entries(nextWordOrSeparator) intersection separators = {} and
     * (position + |nextWordOrSeparator| = |text| or
     * entries(text[position, position + |nextWordOrSeparator| + 1))
     * intersection separators /= {})
     * else
     * entries(nextWordOrSeparator) is subset of separators and
     * (position + |nextWordOrSeparator| = |text| or
     * entries(text[position, position + |nextWordOrSeparator| + 1))
     * is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {

        boolean done = false;
        String word = "";
        int i = position;
        char c = text.charAt(i);
        while (i < text.length() && !done) {
            c = text.charAt(i);
            if (separators.contains(c)) {
                done = true;
            }
            i++;
        }
        //increments i if the substring will be only a separator
        //increments i if the last character of the substring is not a separator
        if (i == position + 1) {
            i++;
        } else if (i == text.length() && !separators.contains(c)) {
            i++;
        }
        word = text.substring(position, i - 1);

        return word;
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given{@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     *
     *
     */
    private static void generateElements(String str, Set<Character> charSet) {
        Set<Character> list = new HashSet<Character>();
        int i = 0;
        while (i < str.length()) {
            char c = str.charAt(i);
            if (!list.contains(c)) {
                list.add(c);
            }
            i++;
        }
        charSet.clear();
        charSet.addAll(list);
    }

    /**
     * Moves the last n elements from sorter1 into sorter2.
     *
     * @param n
     *            the number of elements to move
     * @param sorter1
     *            the Set with elements to transfer
     * @param sorter2
     *            the Map to receive elements
     * @clears sorter1
     * @replaces sorter2
     * @ensures sorter2 has n elements and its entries are a subset of the
     *          entries in sorter1
     */
    private static void transferElements(int n,
            TreeSet<Map.Entry<String, Integer>> sorter1,
            TreeMap<String, Integer> sorter2) {
        sorter2.clear();
        int i = 0;
        while (i < n) {
            Map.Entry<String, Integer> entry = sorter1.last();
            String key = entry.getKey();
            int value = entry.getValue();
            sorter1.remove(entry);
            sorter2.put(key, value);
            i++;
        }
        sorter1.clear();

    }

    /**
     * Prints the generated tag cloud to outFileName in HTML.
     *
     * @param inFileName
     *            the name of the file the word were read from
     * @param htmlFile
     *            the file to write to
     * @param words
     *            a sortingMachine of words
     * @param numWords
     *            the number of words in the cloud
     * @clears words
     * @requires <pre>
     * outFileName must be a valid location for an HTML file.
     * </pre>
     * @ensures <pre>
     * An HTML file named outFileName is created.
     * Any elements in words are printed in the order in words.
     * </pre>
     */
    private static void printToHTML(String inFileName, PrintWriter htmlFile,
            TreeMap<String, Integer> words, int numWords) {
        htmlFile.println("<html>");
        htmlFile.println("<head>");
        htmlFile.println("<title>Top " + numWords + " words in " + inFileName
                + "</title>");
        htmlFile.println("<link href=\"http://web.cse.ohio-state.edu/software"
                + "/2231/web-sw2/assignments/projects/tag-cloud-generator/data"
                + "/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">;");

        htmlFile.println(
                "<link href=\"doc/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        htmlFile.println("</head>");
        htmlFile.println("<body>");
        htmlFile.println(
                "<h2>Top " + numWords + " words in " + inFileName + "</h2>");
        htmlFile.println("<hr>");
        htmlFile.println("<div class = \"cdiv\">");
        htmlFile.println("<p class = \"cbox\">");

        htmlFile.println("<div class=\"cdiv\">");

        htmlFile.println("<p class =" + '"' + "cbox" + '"' + ">");

        TreeMap<String, Integer> temp = new TreeMap<String, Integer>(
                words.comparator());
        int average = 0;
        while (words.size() > 0) {
            String key = words.firstKey();
            int value = words.remove(key);
            average += value;
            temp.put(key, value);
        }
        if (numWords > 0) {
            average /= numWords;
        } else {
            average = 0;
        }
        words.putAll(temp);

        while (words.size() > 0) {
            String key = words.firstKey();
            int value = words.remove(key);
            String fontSize = getFontSize(average, value);
            String tag = "<span style=\"cursor:default\" class=\"" + fontSize
                    + "\" title=\"count: " + value + "\">" + key + "</span>";
            htmlFile.println(tag);
        }

        htmlFile.println("</p>");
        htmlFile.println("</div>");
        htmlFile.println("</body>");
        htmlFile.println("</html>");
    }

    /**
     * Asks the user for the number of tags to make. Prints error messages if an
     * invalid input is provided.
     *
     * @param input
     *            the input stream
     * @param maxInt
     *            the maximum number of tags allowed
     * @return the user inputed integer
     */
    private static int getNumberOfTags(BufferedReader input, int maxInt) {

        int num = -1;
        while (num < 0) {
            System.out.print("Number of words to include: ");
            try {
                num = Integer.parseInt(input.readLine());
                if (num > maxInt) {
                    num = -1;
                    System.out.println(
                            "Please enter an integer <= the number of words"
                                    + " in the input file.");
                }
            } catch (NumberFormatException e) {
                num = -1;
                System.out.println("Please enter a positive integer <= 2^31");
            } catch (IOException e) {
                System.err.println("Error - " + e);
            }
        }
        return num;
    }

    /**
     * Determines whether the word frequency {@code freq} is small, medium, or
     * large and returns the font tag associated with its size.
     *
     * @param average
     *            the average frequency for this tag cloud
     * @param freq
     *            the frequency of the word to check
     * @return the small, medium, or large font tag
     */
    private static String getFontSize(int average, int freq) {
        String fontSize = "f14";
        final int thirds = 3;
        if (freq > (average * thirds) / 2) {
            fontSize = "f48";
        } else if (freq > average / 2) {
            fontSize = "f26";
        }

        return fontSize;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in));
        BufferedReader inputFile = null;
        PrintWriter outputFile = null;

        System.out.print("Enter the name of the input file: ");
        String inFileName = "";
        try {
            inFileName = input.readLine();
            inputFile = new BufferedReader(new FileReader(inFileName));
            System.out.print("Enter the name of the output file: ");
            String outFileName = "";
            outFileName = input.readLine();
            outputFile = new PrintWriter(
                    new BufferedWriter(new FileWriter(outFileName)));

            HashMap<String, Integer> wordAndCount = new HashMap<String, Integer>();
            Comparator<Map.Entry<String, Integer>> freq = new IntegerValue();
            TreeSet<Map.Entry<String, Integer>> sorter1 = new TreeSet<>(freq);
            TreeMap<String, Integer> sorter2 = new TreeMap<String, Integer>();

            readInputFile(inputFile, wordAndCount);

            sorter1.addAll(wordAndCount.entrySet());
            int n = getNumberOfTags(input, sorter1.size());
            transferElements(n, sorter1, sorter2);

            printToHTML(inFileName, outputFile, sorter2, n);

            try {
                input.close();
                outputFile.close();
                inputFile.close();
            } catch (IOException e) {
                System.err.println("Error closing streams");
            }
        } catch (IOException e) {
            System.err.println("Error. File invalid because of " + e);
        }
    }

}
