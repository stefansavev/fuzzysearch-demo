package com.stefansavev.fuzzysearch.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.stefansavev.fuzzysearch.*;

public class MnistExample {

    static FuzzySearchItem parseItem(int lineNumber, String line, int numDimensions){
        String[] tokens = line.split(",");
        int label = Integer.parseInt(tokens[0]);
        double[] values = new double[numDimensions];
        for(int i = 0; i < numDimensions; i ++){
            values[i] = Double.parseDouble(tokens[i + 1]);
        }
        return new FuzzySearchItem(Integer.toString(lineNumber), label, values);
    }

    static void buildIndex(String inputFile, String outputIndexFile) throws IOException {
        int dataDimension = 100;
        int numTrees = 10;
        //create an indexer
        FuzzySearchIndexBuilder indexBuilder = new FuzzySearchIndexBuilder(dataDimension, FuzzySearchEngines.fastTrees(numTrees));

        //read the data points from a file and add them to the indexer one by one
        //each point has a name(string), label(int), and a vector
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        reader.readLine(); //skip header, in this case the format is label,f1,f2,...,f100
        int lineNumber = 1;
        String line = null;
        while ((line = reader.readLine()) != null) {
            FuzzySearchItem item = parseItem(lineNumber, line, dataDimension);
            indexBuilder.addItem(item);
            lineNumber ++;
        }
        reader.close();

        //build the index
        FuzzySearchIndex index = indexBuilder.build();

        //save the index to file
        index.save(outputIndexFile);
    }

    static void runQueriesFromFile(String queriesFile, String indexFile) throws IOException {
        FuzzySearchIndex index = FuzzySearchIndex.open(indexFile);
        int dataDimension = index.getDimension();

        String line = null;
        BufferedReader reader = new BufferedReader(new FileReader(queriesFile));
        reader.readLine(); //skip header, in this case the format is label,f1,f2,...,f100
        int lineNumber = 1;

        while ((line = reader.readLine()) != null) {
            FuzzySearchItem item = parseItem(lineNumber, line, dataDimension);
            List<FuzzySearchResult> results = index.getNearestNeighborsByQuery(10, item.getVector());

        }
        reader.close();
    }

    static void runQueriesFromIndex(String indexFile) throws IOException {
        FuzzySearchIndex index = FuzzySearchIndex.open(indexFile);

        Iterator<FuzzySearchItem> itemsIterator = index.getItems();
        double agree = 0.0;
        double disagree = 0.0;
        long start = System.currentTimeMillis();

        while (itemsIterator.hasNext()) {
            FuzzySearchItem item = itemsIterator.next();
            List<FuzzySearchResult> results = index.getNearestNeighborsByQuery(10, item.getVector());
            if (results.get(0).getLabel() != item.getLabel()){
                throw new IllegalStateException("The top result should be the query itself");
            }
            //System.out.println(results.get(1).getCosineSimilarity());
            if (item.getLabel() == results.get(1).getLabel()){
                agree ++;
            }
            else{
                disagree ++;
            }
        }
        long end = System.currentTimeMillis();
        double accuracy = 100.0*agree/(agree + disagree);
        int numQueries = (int)(agree + disagree);
        double timePerQuery = ((double)(end - start))/numQueries;
        double queriesPerSec = 1000.0*numQueries/((double)(end - start + 1L));

        System.out.println("Total search time in secs.: " + (((double)(end - start))/1000.0));
        System.out.println("Num queries: " + numQueries);
        System.out.println("Accuracy estimate: " + accuracy);
        System.out.println("Time per query in ms.: " + timePerQuery);
        System.out.println("Queries per sec.: " + queriesPerSec);
    }

    public static void main(String[] args) throws Exception {
        String inputTextFile = "D:/RandomTreesData-144818512896186816/input/" + "mnist/svdpreprocessed/train.csv";
        String queriesFile = inputTextFile; //same as training in this example
        String indexFile = "C:/tmp/output-index/";

        buildIndex(inputTextFile, indexFile);
        //runQueriesFromFile(queriesFile, indexFile);
        runQueriesFromIndex(indexFile);
    }
}
