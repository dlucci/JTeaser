package com.java.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.gravity.goose.Article;
import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;

/**
 * Created by derlucci on 7/11/15.
 *
 * This program was written because I was having problems getting PyTeaser (https://github.com/xiaoxu193/PyTeaser/) to
 * run in java so I ported the code.  I did not come up with any of this logic.  Full credit goes to github.com/xiaoxu194
 * for that
 */
public class PyTeaser {

    // keywords
    String[] stopWords = {"-", " ", ",", ".", "a", "e", "i", "o", "u", "t", "about", "above",
            "above", "across", "after", "afterwards", "again", "against", "all",
            "almost", "alone", "along", "already", "also", "although", "always",
            "am", "among", "amongst", "amoungst", "amount", "an", "and",
            "another", "any", "anyhow", "anyone", "anything", "anyway",
            "anywhere", "are", "around", "as", "at", "back", "be", "became",
            "because", "become", "becomes", "becoming", "been", "before",
            "beforehand", "behind", "being", "below", "beside", "besides",
            "between", "beyond", "both", "bottom", "but", "by", "call", "can",
            "cannot", "can't", "co", "con", "could", "couldn't", "de",
            "describe", "detail", "did", "do", "done", "down", "due", "during",
            "each", "eg", "eight", "either", "eleven", "else", "elsewhere",
            "empty", "enough", "etc", "even", "ever", "every", "everyone",
            "everything", "everywhere", "except", "few", "fifteen", "fifty",
            "fill", "find", "fire", "first", "five", "for", "former",
            "formerly", "forty", "found", "four", "from", "front", "full",
            "further", "get", "give", "go", "got", "had", "has", "hasnt",
            "have", "he", "hence", "her", "here", "hereafter", "hereby",
            "herein", "hereupon", "hers", "herself", "him", "himself", "his",
            "how", "however", "hundred", "i", "ie", "if", "in", "inc", "indeed",
            "into", "is", "it", "its", "it's", "itself", "just", "keep", "last",
            "latter", "latterly", "least", "less", "like", "ltd", "made", "make",
            "many", "may", "me", "meanwhile", "might", "mill", "mine", "more",
            "moreover", "most", "mostly", "move", "much", "must", "my", "myself",
            "name", "namely", "neither", "never", "nevertheless", "new", "next",
            "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing",
            "now", "nowhere", "of", "off", "often", "on", "once", "one", "only",
            "onto", "or", "other", "others", "otherwise", "our", "ours",
            "ourselves", "out", "over", "own", "part", "people", "per",
            "perhaps", "please", "put", "rather", "re", "said", "same", "see",
            "seem", "seemed", "seeming", "seems", "several", "she", "should",
            "show", "side", "since", "sincere", "six", "sixty", "so", "some",
            "somehow", "someone", "something", "sometime", "sometimes",
            "somewhere", "still", "such", "take", "ten", "than", "that", "the",
            "their", "them", "themselves", "then", "thence", "there",
            "thereafter", "thereby", "therefore", "therein", "thereupon",
            "these", "they", "thickv", "thin", "third", "this", "those",
            "though", "three", "through", "throughout", "thru", "thus", "to",
            "together", "too", "top", "toward", "towards", "twelve", "twenty",
            "two", "un", "under", "until", "up", "upon", "us", "use", "very",
            "via", "want", "was", "we", "well", "were", "what", "whatever",
            "when", "whence", "whenever", "where", "whereafter", "whereas",
            "whereby", "wherein", "whereupon", "wherever", "whether", "which",
            "while", "whither", "who", "whoever", "whole", "whom", "whose",
            "why", "will", "with", "within", "without", "would", "yet", "you",
            "your", "yours", "yourself", "yourselves", "the", "reuters", "news",
            "monday", "tuesday", "wednesday", "thursday", "friday", "saturday",
            "sunday", "mon", "tue", "wed", "thu", "fri", "sat", "sun",
            "rappler", "rapplercom", "inquirer", "yahoo", "home", "sports",
            "1", "10", "2012", "sa", "says", "tweet", "pm", "home", "homepage",
            "sports", "section", "newsinfo", "stories", "story", "photo",
            "2013", "na", "ng", "ang", "year", "years", "percent", "ko", "ako",
            "yung", "yun", "2", "3", "4", "5", "6", "7", "8", "9", "0", "time",
            "january", "february", "march", "april", "may", "june", "july",
            "august", "september", "october", "november", "december",
            "government", "police" };

    final double ideal = 20.0;
    // ideal number of words in sentence = 20. Use this to calculate score later

    final List<String> stopList = Arrays.asList(stopWords);
    // asList returns a fixed sized list, backed as a single array

    public static String[] keywords;

    public List<String> SummarizeUrl(String url){

        Article article = grabLink(url);
        // Gets the name and text of article

        if(article == null || article.cleanedArticleText() == null || article.title() == null)
            return null;

        List<String> summaries = summarize(article.title(), article.cleanedArticleText());
        // summarize method parses title and text into strings contained in a tuple list

        return summaries;
    }

    private List<String> summarize(String title, String article) {
    // Splits title, get keywords, and sentences
    // if 5 < sentences, return as is (summary = 5 lines)
    // otherwise, assigns value to score based on length, location, and # keywords
        List<String> summaries = new ArrayList<>();

        String[] sentences = split_sentences(article);

        TreeMap<String, Double> keys = keywords(article);
        // consists of keys (string values) and values (double values)

        keywords = keys.keySet().toArray(new String[keys.keySet().size()]);

        String[] titleWords = split_words(title);

        if(sentences.length <= 5) {
            List<String> retVal = Arrays.asList(sentences);
            return retVal;
        }

        Map<String, Double> ranks = score(sentences, titleWords, keys);
    // score is calculated via sentences titleWords and keys within each sentence
    // To be evaluated later

        ValueComparator vc = new ValueComparator(ranks);
        TreeMap<String, Double> sorted = new TreeMap<>(vc);

        sorted.putAll(ranks);
    //  Puts ranked objects into treeMap, then sorts them, generally based on score

        int x = 0;
        for(Map.Entry<String, Double> entry : sorted.entrySet()){
            if(x < 5){
                summaries.add(entry.getKey());
                x++;
            } else
                break;;
        }

        return summaries;
    }

    private Map<String, Double> score(String[] sentences, String[] titleWords, TreeMap<String, Double> keys){
    // calculates the score for each sentence and returns it as a Map

        int senSize = sentences.length;

        Map<String, Double> ranks = new HashMap<>();

        List<String> keywords = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for(Map.Entry<String, Double> entry : keys.entrySet()){
            keywords.add(entry.getKey());
            values.add(entry.getValue());
        }

        List<String> goodTitle = getGoodTitle(titleWords);
        // obtain title of the article (modified)

        for(int x = 0; x < senSize; x++){

            String[] splitty = split_words(sentences[x]);

            double title_feature = title_score(goodTitle, splitty);

            double sentenceLength = length_score(splitty);
            double sentencePosition = sentence_position(x+1, senSize);
            double sbsFeature = sbs(splitty, keywords, values);
            double dbsFeature = dbs(splitty, keywords, values);
            double frequency = (sbsFeature + dbsFeature) / 2.0 * 10.0;
            double totalScore = (title_feature * 1.5 + frequency*2.0 + sentenceLength*1.0 + sentencePosition*1.0) / 4.0;

            ranks.put(sentences[x], totalScore);
        }

        return ranks;
    }

    // TODO Rename or add documentation on what this method does
    private double dbs(String[] sentences, List<String> keywords , List<Double> values) {
    // remember: array = data structure, consists of sequential memory used for storing objects
        // list = interface, can utilize multiple implementations, not restricted to just an array
            // requires iterators to traverse

        // Takes in sentences and keywords and gives a score for each keyword based on their occurences
        // within the sentences
        // basically does the same thing as dbs? haha

        if(sentences.length == 0)
            return 0;

        double sum = 0;

        double[] first = new double[2];
        double[] second = new double[2];
        // second keeps track of the last values of first

        first[0] = -1;
        first[1] = -1;

        for(int x = 0; x < sentences.length; x++){

            String s = sentences[x];

            if(keywords.contains(s)){
                double score = values.get(keywords.indexOf(s));
                // pulls out the value found for that keyword

                if(first[0] == -1 && first[1] == -1){
                    first[0] = x;
                    first[1] = score;
                    // initial input
                } else {
                    second[0] = first[0];
                    second[1] = first[1];
                    first[0] = x;
                    first[1] = score;

                    double diff =  first[0] - second[0];
                    sum += (first[1] + second[1]) / (Math.pow(diff, 2));
                    // overall value sum is then calculated based on number of occurences
                    // that this word appears
                }
            }
        }

        List<String> keys = new ArrayList<>(keywords);
        Set<String > words = new HashSet<>(Arrays.asList(sentences));
        keys.retainAll(words);

       double k = keys.size() + 1;
       return (1/(k*(k+1.0))*sum);
        // final score is calculated as such
    }

    private double sbs(String[] sentences, List<String> keywords, List<Double> values) {
    // compares words in sentence to keywords.
        // if word appears, added to score. SBS = score by sentence
        double score = 0.0;

        if(sentences.length == 0)
            return 0.0;


        for(int x = 0 ; x<sentences.length ; x++){
            sentences[x] = sentences[x].trim();
            // not too sure as to what trim does...

            if(keywords.contains(sentences[x]))
                score+=values.get(keywords.indexOf(sentences[x]));
            // scoring is calculated based on location of the keyword(???)
        }

        return (1.0 / Math.abs(sentences.length) * score)/10.0;
    }

    private double sentence_position(int i, int senSize) {
        // score calculated based on location in sentence(?)
        // 'i' is the next word's index(?) (pass in x+1 when called, x = the next word in a sentence)

        double normalized = i*1.0/senSize;
        if(normalized > 0 && normalized<= 0.1)
            return 0.17;
        else if(normalized > 0.1 && normalized <= 0.2)
            return 0.23;
        else if(normalized > 0.2 && normalized <= 0.3)
            return 0.14;
        else if(normalized > 0.3 && normalized <= 0.4)
            return 0.08;
        else if(normalized > 0.4 && normalized <= 0.5)
            return 0.05;
        else if(normalized > 0.5 && normalized <= 0.6)
            return 0.04;
        else if(normalized > 0.6 && normalized <= 0.7)
            return 0.06;
        else if(normalized > 0.7 && normalized <= 0.8)
            return 0.04;
        else if(normalized > 0.8 && normalized <= 0.9)
            return 0.04;
        else if(normalized > 0.9 && normalized <= 1.0)
            return 0.15;

        return 0;
    }

    private double length_score(String[] sentences) {
    // score based on number of sentences found
        return 1-Math.abs(ideal - sentences.length)/ideal;
    }

    private List<String> getGoodTitle(String[] titleWords){
    // score based on unique words in the title
        List<String> goodTitle = new ArrayList<>();
        for(int x = 0; x < titleWords.length; x++){
            if(!stopList.contains(titleWords[x])){
                goodTitle.add(titleWords[x]);
                // eliminates repeats
            }
        }

        return goodTitle;
    }

    private double title_score(List<String> goodTitle, String[] sentences){

        double count = 0.0;

        for(int x = 0; x < sentences.length; x++){
            if(!stopList.contains(sentences[x]) && goodTitle.contains(sentences[x])){
                count += 1.0;
            }
        }

        if(goodTitle.size() == 0)
            return 0.0;

        return count/goodTitle.size();
    }

    public TreeMap<String, Double> keywords(String article) {
    // takes in an article and scores the individual words

        String[] words = split_words(article);

        int numWords = words.length;

        Map<String, Double> wordFreq = new HashMap<>();
        // creates a Hashmap based on words and their frequencies

        ValueComparator vc = new ValueComparator(wordFreq);
        TreeMap<String, Double> sortedHist = new TreeMap<>(vc);
        // treemap based on how word frequency

        for(int x = 0; x < words.length; x++){
            if(!stopList.contains(words[x])){
                if(wordFreq.containsKey(words[x])){
                    wordFreq.put(words[x], wordFreq.get(words[x]) + 1);
                    // updates words encountered in stopList
                } else {
                    wordFreq.put(words[x], 1.0);
                    // adds new word to the list, apperance = 1
                }
            }

        }

        sortedHist.putAll(wordFreq);

        int minSize = Math.min(10, sortedHist.size());
        int x = 0;

        Map<String, Double> keywords = new HashMap<>();
        for(Map.Entry<String, Double> item : sortedHist.entrySet()){
            if(x < minSize) {
                keywords.put(item.getKey(), item.getValue());
                // put method replaces item.getKey's previous value with item.getValue
                x++;
            }else
                break;
        }


        TreeMap<String, Double> submap = new TreeMap<>();

       for(Map.Entry<String, Double> entry : keywords.entrySet()){
            double numerator = entry.getValue();
            double articleScore = (numerator*1.0)/numWords;

            submap.put( entry.getKey(), (articleScore * 1.5) + 1);
        }

        return submap;
    }

    private String[] split_words(String article) {

        /**
         * Really don't like this.  Once this is stable,
         * TODO create a regex to encapsulate this
         */

        // don't consider punctuation
        article = article.replace('?', '\0');
        article = article.replace('.', '\0');
        article = article.replace('!', '\0');
        article = article.replace('’', '\0');
        article = article.replace('"', '\0');
        article = article.replace('(', '\0');
        article = article.replace(')', '\0');
        article = article.replace(',', '\0');
        article = article.replace('—', '\0');
        article = article.replace('-', '\0');
        article = article.replace('\n', '\0');
        article = article.replace('“', '\0');
        article = article.replace('”', '\0');
        article = article.replace('\r', '\0');

        String[] words = article.toLowerCase().split(" ");
        // eliminate spaces

        List<String> retWords = new ArrayList<>();

        for(int x = 0; x < words.length ; x++) {
            words[x] = words[x].trim();
            // still no idea what trim does...

            if(!words[x].isEmpty()) {
                retWords.add(words[x]);
            }
        }

        String[] arrWords = (String[])retWords.toArray(new String[retWords.size()]);

        return arrWords;
        // returns a list of words after they have been modified to reduce non-alphanumerics
    }

    private String[] split_sentences(String text){

        String regex = "(?![A-Z])((?<=[?.!])|(?=[?.!])\"?)(?=\\s+\"?[A-Z])";

        String[] split_text = text.split(regex.toString());
        for(int x = 0; x < split_text.length; x++){
            split_text[x] = StringUtils.stripStart(split_text[x], " ");
        }

        return split_text;
    }

    public Article grabLink(String url){
        // takes in a url and returns an article object, consisting of the article's title and main text
        // does this using methods from a Goose database

        Goose goose = new Goose(new Configuration());
        Article article = goose.extractContent(url);
        return article;
    }

    private class ValueComparator implements Comparator<String>{
        // class that overrides typical compare method
        // allows string comparison based on scores

        private Map<String, Double> values;
        public ValueComparator(Map<String, Double> values){
            this.values = values;
        }

        @Override
        public int compare(String o1, String o2) {
            if(values.get(o1) >= values.get(o2)){
                return -1;
            } else
                return 1;
        }
    }
}
