**My Assumptions:**
1. Looking at the data, I can see that only every other line starts with name - the rest starts with a space - I'm going to skip the lines that starts with space since none has names in it. 
2. I assume the name lines has a dash(-) after the first name which I use to break the tokens.
3. Looking at the data and the instructions, none of the names have middle names.
4. The document requires the top ten outputs to be "sorted in descending order" and I assume that the descending order of the count(how often they appeared in the data).
5. I did write some test for small sample sizes but far from complete test cases and edge cases, and I did not try to clean up the code/modularize it. But I put some notes for the future to do so.

**Code explanation:**
1. I wrote LineProcessor interface with an implementation CommaAndDashLineProcessor based on My Assumptions point 2 above. If in the future we read other types of data with different format, then we can have other implementations.
    This class will process names by tokenizing and reject the ones that do not pass the criteria mentioned in the instruction file(coding-test.txt).

2. There are two implementations:
    A. For NamesServiceSerial, I stream the data serially and so it's the slowest.
    B. For NamesServiceMultiThreading, I implemented a simple two  threads concurrent program where the data is processed parallelly by two threads, and the result is then combined similar to concepts of map-reduce. It cuts the execution time by 70 or more percent when the data is big. 

3. All these files are just a simple implementation, and they could be reorganized, and get cleaned up and so on.

4. Both the programs return a NameStat which has all data points asked for plus the execution duration is also calculated. I convert that to json just for readibility while testing. 

**Testing:**
1. Testing through NamesServiceSerialTest and NamesServiceMultiThreading has been done against the file data-test-data.txt file which has 100K records.
2. The NamesServiceSerialTest has a few more tests with smaller sample size.
3. To run the programs, right click on the test files in your IDE, and run the test, or from command line in the root directory of the project, run this command:
    mvn clean test
4. Please look at the 100K_NameServiceMultiThreadingOutput.json and 100K_NameServiceSerialOutput.json for sample output.
5. I did not include the files data-huge-shard-one.txt, data-huge-shard-two.txt and data-huge.txt inside this  

**Extra Testing:**
1. If you want to test against a larger data set, NamesServiceSerialTest has a test method processNamesWithHugeSampleData() that
    can you can use to test. To do so, please set the absolute path to the large file(data-huge.txt). 
   In my case, I have data-huge.txt which has 17,503,200 records, and is located /Users/wahid/coding/data-huge.txt.

2. If you want to test against a larger data set, NamesServiceMultiThreading has a test method processNamesWithHugeSampleData() that
   can you can use to test. To do so, please set the absolute path to the two large files you have.
   In my case, I have "/Users/wahid/coding/data-huge-shard-one.txt" and "/Users/wahid/coding/data-huge-shard-two.txt" which has 17,503,200 records combined.
3. As you can see in the chart below, when the data size grows, big data concepts and parallel processing is much better.

**Performance of NamesServiceMultiThreading vs NamesServiceSerialTest**
                                    NamesServiceSerialTest          NamesServiceMultiThreading
Data size:                              100K                            100K
Execution time in millisecond:          318                             385   
Performance gain %:                     ~20%                            No gain

Data size:                              17.5 Million                    17.5 Million
Execution time in millisecond:          10782                           6078   
Performance gain %:                     No gain                         77% faster
