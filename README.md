# fuZzYsearch

Demo of fuzzy (nearest neighbor) search with dense data.

This tool can be used to:

* search words represented as word vectors
* search documents using a dense representation from Latent Dirichlet Allocation (LDA) and SVD
* build an image search engine
* build a music search engine
* build a recommendation system
* search representations of deep neural networks


The demo uses the following library:

https://github.com/stefansavev/random-projections-at-berlinbuzzwords

This library implements a fast and scalable fuzzy search of dense vectors (as opposed to exact search of sparse vectors) using the cosine similarity
 between the vectors. The library improves the speed of search up to 10 times compared to the
obvious brute force approach. With this library it is practical to compute all nearest neighbors
for all data points in datasets of sizes up to half a million data points.

| Dataset name           | Number of data points |  Number of dimensions | Number of Trees (Memory) Used | Queries per sec. | Queries per sec. brute force | Recall@10|
| ---------------------- | --------------------: | ---------------------:| -----------------------------:| ----------------:|-----------------------------:|---------:|
| MNIST                  | 42 000                 |  100                 | 10                            | 1104             | 164                          | 91.5%    |
| Google Word Vectors    | 70 000                 |  200                 | 50                            | 528              | 49                           | 91.0%    |
| Glove Word Vectors     | 400 000                |  100                 | 150                           | 165              | 18                           | 90.9%    |

90% Recall@10 means that in top 10 results returned by the library we could not find (100 - 90)% = 10%. This is common for search using
dense vectors. The remaining 10% can be found by increasing the number of trees, essentially giving more computational time and memory to the library.

##API

###Indexing (in batch mode)

```java
int dataDimension = 100;
int numTrees = 10;

//create an indexer
FuzzySearchIndexBuilder indexBuilder =
    new FuzzySearchIndexBuilder(dataDimension, FuzzySearchEngines.fastTrees(numTrees));

//add sample data
String key = "key";
int label = 8; //class/label in machine learning
double[] vector = ...; //a double array of dimension specified above
FuzzySearchItem item = new FuzzySearchItem(key, values);
indexBuilder.addItem(item);

//build the index
FuzzySearchIndex index = indexBuilder.build();

//save the index to file
index.save(outputIndexFile);
```

###Queries (Search)

```java
//load the index
FuzzySearchIndex index = FuzzySearchIndex.open(indexFile);

//specify a query
double[] query = ...; //the query is a vector of dimension specified during indexing
//retrieve the results (data point names and similarity scores)
List<FuzzySearchResult> results = index.search(10, query); //return top 10 results
FuzzySearchResult topResult = results.get(0);
String topResultName = topResult.getName();
double topResultSimilarity = topResult.getCosineSimilarity();
```

## Setup

Execute the following commands:

```
mkdir some_dir
cd some_dir
git clone https://github.com/stefansavev/fuzzysearch-demo.git
```

The repository contains one large file of size 70MB and
cloning can take a few minutes

```
cd fuzzysearch-demo
```

###Setup IntelliJ IDEA Project

```
gradle idea
```

In IntelliJ IDEA:
```
File/Open...
Select the folder fuzzysearch-demo.
Right click on MnistExample, click Run
```

###Build Project

```
gradle clean build
```

###Run Project

```
gradle run
```

###Expected output

You should see

```
DataFrameView(42000, 100)
Using SVD on the full data
Nov 15, 2015 10:58:44 PM com.github.fommil.netlib.BLAS <clinit>
WARNING: Failed to load implementation from: com.github.fommil.netlib.NativeSystemBLAS
Nov 15, 2015 10:58:45 PM com.github.fommil.jni.JniLoader liberalLoad
INFO: successfully loaded D:\Users\stefan\AppData\Local\Temp\jniloader9091513825361286694netlib-native_ref-win-x86_64.dll
Nov 15, 2015 10:58:46 PM com.github.fommil.netlib.LAPACK <clinit>
WARNING: Failed to load implementation from: com.github.fommil.netlib.NativeSystemLAPACK
Nov 15, 2015 10:58:46 PM com.github.fommil.jni.JniLoader load
INFO: already loaded netlib-native_ref-win-x86_64.dll
Started transformation with SVD
Processed 0 rows
Processed 5000 rows
...
Processed 40000 rows
Finished transformation with SVD
Time for 'Build tree 0' 318 ms.
Time for 'Build tree 1' 145 ms.
Time for 'Build tree 2' 104 ms.
Time for 'Build tree 3' 83 ms.
Time for 'Build tree 4' 60 ms.
Time for 'Build tree 5' 64 ms.
Time for 'Build tree 6' 59 ms.
Time for 'Build tree 7' 49 ms.
Time for 'Build tree 8' 54 ms.
Time for 'Build tree 9' 52 ms.
Time for 'Create trees' 4.205 secs.
Total search time in secs.: 7.36
Num queries: 42000
Accuracy estimate: 97.46666666666667
Time per query in ms.: 0.17523809523809525
Queries per sec.: 5705.74650183399
```