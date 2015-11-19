package com.stefansavev.fuzzysearch.demo;

import java.io.BufferedReader;
import java.io.File;
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
            List<FuzzySearchResult> results = index.search(10, item.getVector());

        }
        reader.close();
    }

    static void runQueriesFromIndex(String indexFile) throws IOException {
        FuzzySearchIndex index = FuzzySearchIndex.open(indexFile);

        Iterator<FuzzySearchItem> itemsIterator = index.getItems();
        double agree = 0.0;
        double disagree = 0.0;
        long start = System.currentTimeMillis();
        int i = 0;
        while (itemsIterator.hasNext()) {
            FuzzySearchItem item = itemsIterator.next();
            List<FuzzySearchResult> results = index.search(10, item.getVector());
            if (results.get(0).getLabel() != item.getLabel()){
                throw new IllegalStateException("The top result should be the query itself");
            }
            if (item.getLabel() == results.get(1).getLabel()){
                agree ++;
            }
            else{
                disagree ++;
            }
            if ((++i) % 5000 == 0){
                System.out.println("Processed " + i + " queries");
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
        String inputTextFile = "testdata/mnist/preprocessed-train.csv";
        String queriesFile = inputTextFile; //same as training in this example
        String indexFile = "testoutput/mnist";

        //create directories if they don't exist
        (new File(indexFile)).mkdirs();

        buildIndex(inputTextFile, indexFile);
        //runQueriesFromFile(queriesFile, indexFile);
        runQueriesFromIndex(indexFile);
    }
}

/*
DataFrameView(42000, 100)
Using SVD on the full data
Nov 19, 2015 4:26:49 PM com.github.fommil.netlib.BLAS <clinit>
WARNING: Failed to load implementation from: com.github.fommil.netlib.NativeSystemBLAS
Nov 19, 2015 4:26:50 PM com.github.fommil.jni.JniLoader liberalLoad
INFO: successfully loaded D:\Users\stefan\AppData\Local\Temp\jniloader6582973504082836255netlib-native_ref-win-x86_64.dll
Nov 19, 2015 4:26:51 PM com.github.fommil.netlib.LAPACK <clinit>
WARNING: Failed to load implementation from: com.github.fommil.netlib.NativeSystemLAPACK
Nov 19, 2015 4:26:51 PM com.github.fommil.jni.JniLoader load
INFO: already loaded netlib-native_ref-win-x86_64.dll
Started transformation with SVD
Processed 0 rows
Processed 5000 rows
Processed 10000 rows
Processed 15000 rows
Processed 20000 rows
Processed 25000 rows
Processed 30000 rows
Processed 35000 rows
Processed 40000 rows
Finished transformation with SVD
#storage of tree: 0.010564804077148438 MB
Time for 'Build tree 0' 291 ms.
#storage of tree: 0.010519027709960938 MB
Time for 'Build tree 1' 114 ms.
#storage of tree: 0.010385513305664062 MB
Time for 'Build tree 2' 93 ms.
#storage of tree: 0.010196685791015625 MB
Time for 'Build tree 3' 84 ms.
#storage of tree: 0.010316848754882812 MB
Time for 'Build tree 4' 72 ms.
#storage of tree: 0.010370254516601562 MB
Time for 'Build tree 5' 65 ms.
#storage of tree: 0.010564804077148438 MB
Time for 'Build tree 6' 65 ms.
#storage of tree: 0.010625839233398438 MB
Time for 'Build tree 7' 65 ms.
#storage of tree: 0.010572433471679688 MB
Time for 'Build tree 8' 68 ms.
#storage of tree: 0.009939193725585938 MB
Time for 'Build tree 9' 59 ms.
Time for 'Create trees' 5.347 secs.
Processed 5000 queries
Processed 10000 queries
Processed 15000 queries
Processed 20000 queries
Processed 25000 queries
Processed 30000 queries
Processed 35000 queries
Processed 40000 queries
Total search time in secs.: 19.482
Num queries: 42000
Accuracy estimate: 97.51904761904763
Time per query in ms.: 0.46385714285714286
Queries per sec.: 2155.725504285788
 */