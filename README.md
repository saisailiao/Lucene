Steps:
1. run:
sh CreateSystem.sh

1)Create the whole lucene system
2)use the Free Searching System:

*******Free Searching System***************
**************************************

please choose the way to use the Lucene:
 1. enter query number
 2. enter query text
 3. exit
 
 you can choose 1 or 2 to use the search engine to find the result of certain query. 
 The "query number" means the .I xxx in the cran.qry file. xxx is the "query number".
 The "query text" should not involve \n in each line, the input query text should be in one line or the system may breakdown.

5. use commonline

../trec_eval-9.0.7\ 2/trec_eval -m all_trec qrel.test answer.test

to get the result of trec_val. If you want to use this common line, you should cd to the directory /LuceneSearch.
