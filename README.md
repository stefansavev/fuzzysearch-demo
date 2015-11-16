# fuzzysearch-demo
Demo of fuzzy (nearest neighbor) search with dense data.

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