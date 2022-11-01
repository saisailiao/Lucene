import java.io.*;

import org.apache.lucene.analysis.classic.ClassicAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class NewSearchFile {
    // directory of the index file 1
    private static final String PATH_OF_INDEX = "index/";
    private void newSearchFile() {
        IndexSearcher searcher = null;
        IndexReader reader = null;
        Directory directory = null;
        try {
            System.out.println("Create searcher....");
            // answer.test will put the search answer
            Writer writer = new FileWriter(new File("./answer.test"));
            directory = FSDirectory.open(new File(PATH_OF_INDEX).toPath());
            reader = DirectoryReader.open(directory);
            searcher = new IndexSearcher(reader);

            // subclass of TFIDFSimilarity(based on vector space model)
//            searcher.setSimilarity(new ClassicSimilarity());
            // bm25 similarity
//            searcher.setSimilarity(new BM25Similarity());
            // boolean similarity
            searcher.setSimilarity(new BooleanSimilarity());
            // create search Query based on Paraser
            System.out.println("Create QueryParser....");
            QueryParser parser = new QueryParser("content", new SimpleAnalyzer());
            // support * and ? in qurey
            parser.setAllowLeadingWildcard(true);

            // create arraies that contain 300 items (the total queries are 225 docid)
            String[] docIdArr = new String[225];
            String[] qryArr = new String[225];
            docIdArr = createDocArray();
            qryArr = createQryArray();

            int i = 0;
            int num1 = 0;
            System.out.println("begin creating answer1.test");
            while (i < docIdArr.length) {
                int rank = 1;
                // Create query paraser
                Query query = parser.parse(qryArr[i]);
                // search the top 10000 result in order to create the answer.test
                // since it only need certain document ranking scroe not the top results
                TopDocs tds = searcher.search(query, 100);
                // TopDocs is not storing our document, it is storing the ScoreDoc object for our document
                ScoreDoc[] sds = tds.scoreDocs;
                for (ScoreDoc sd : sds) {
                    Document doc = searcher.doc(sd.doc);
                    writer.write(docIdArr[i] + "\s" + "Q0" + "\s");
                    writer.write(removeSuffix(doc.get("fileName"),".txt") + "\s" + rank++ + "\s" + sd.score + "\s" + "STANDARD" + "\n");
                }
                i++;
            }
            System.out.println("finish creating answer.test");
            writer.close();
            // in order to compare the different scroing approaches
            // use the first query in cran.qry as an emample to show the result
            // I return the top five documents
            searcher.setSimilarity(new ClassicSimilarity());
            System.out.println("*****ClassicSimilarity*****");
            for (i = 0; i < 1; i++) {
                int rank = 1;
                Query query = parser.parse(qryArr[i]);
                System.out.println("QUERY:" + qryArr[i]);
                TopDocs tds = searcher.search(query, 5);
                ScoreDoc[] sds = tds.scoreDocs;
                for (ScoreDoc sd : sds) {
                    Document doc = searcher.doc(sd.doc);
                    System.out.println("rank:" + rank++ + " docId:" + doc.get("fileName") + " score:" + sd.score);
                }
            }
            searcher.setSimilarity(new BM25Similarity());
            System.out.println("*****BM25Similarity*****");
            for (i = 0; i < 1; i++) {
                int rank = 1;
                Query query = parser.parse(qryArr[i]);
                System.out.println("QUERY:" + qryArr[i]);
                TopDocs tds = searcher.search(query, 5);
                ScoreDoc[] sds = tds.scoreDocs;
                for (ScoreDoc sd : sds) {
                    Document doc = searcher.doc(sd.doc);
                    System.out.println("rank:" + rank++ + " docId:" + doc.get("fileName") + " score:" + sd.score);
                }
            }
            // Free Searching System
            // user can search the result of certain question base on this system
            System.out.println("**************************************");
            System.out.println("*******Free Searching System***************");
            System.out.println("**************************************\n\n");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("please choose the way to use the Lucene:\n 1. enter query number\n 2. enter query text\n 3. exit\n Your choice is:");
                String choiceNum = br.readLine();
                int choice = Integer.valueOf(choiceNum);
                while (choice == 1 || choice == 2) {
                    if (choice == 1) {
                        int rank = 1;
                        System.out.println("please enter the query number:\n");
                        String number = br.readLine();
                        String res = findQueryText(Integer.valueOf(number), qryArr);
                        System.out.println("Your query is:\n" + res);
                        Query query = parser.parse(res);
                        TopDocs tds = searcher.search(query, 5);
                        ScoreDoc[] sds = tds.scoreDocs;
                        for (ScoreDoc sd : sds) {
                            Document doc = searcher.doc(sd.doc);
                            System.out.println("\n rank:" + rank++ + " docId:" + doc.get("fileName") + " score:" + sd.score);
                        }

                    } else if (choice == 2) {
                        int rank = 1;
                        System.out.println("please enter the query text:");
                        String queryText;
                        queryText = br.readLine();
                        Query query = parser.parse(queryText);
                        TopDocs tds = searcher.search(query, 5);
                        ScoreDoc[] sds = tds.scoreDocs;
                        for (ScoreDoc sd : sds) {
                            Document doc = searcher.doc(sd.doc);
                            System.out.println("\n rank:" + rank++ + " docId:" + doc.get("fileName") + " score:" + sd.score);
                        }
                    }
                    System.out.println("\nIf you want to continue, please enter choice:\n 1. enter query number\n 2. enter query text\n 3. exit\n Your choice is:");
                    choiceNum = br.readLine();
                    choice = Integer.valueOf(choiceNum);
                }
                System.out.println("\n ByeBye");
            } catch (IOException e) {
                System.out.println("An exception was encountered while reading the input information");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("An exception was encountered while searching for files. The stack trace is as follows");
            e.printStackTrace();
        }
        finally {
            if (null != reader) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    System.out.println("An exception was encountered while closing IndexReader with the following stack trace");
                    e.printStackTrace();
                }
            }
        }
    }

    // scan the cran.qry and get the qry id of the qurey
    public static String[] createDocArray () {
        String[] res = new String[225];
        int num = 0;
        try {
            FileInputStream inputStream = new FileInputStream("./cran.qry");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while((str = bufferedReader.readLine()) != null) {
                if (str.startsWith(".I")) {
                    res[num] = Integer.toString(num + 1);;
                    num++;
                }
            }
        }  catch (Exception e) {
            System.out.println("Can not read the cran.qry file");
            e.printStackTrace();
        }
        return res;
    }

    public static String findQueryText (int number, String[] qryArr) {
        String res = "";
        int num = 0;
        String[] arr = new String[2];
        try {
            FileInputStream inputStream = new FileInputStream("./cran.qry");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while((str = bufferedReader.readLine()) != null) {
                if (str.startsWith(".I")) {
                    arr = str.split(" +");
                    if (Integer.valueOf(arr[1])  == number ) {
                        return qryArr[num];
                    }
                    num++;
                }
            }
        }  catch (Exception e) {
            System.out.println("Can not read the cran.qry file");
            e.printStackTrace();
        }
        return res;
    }

    // scan the cran.qry and get the qry content of the qurey
    public static String[] createQryArray () {
        String[] res = new String[225];
        int num = 0;
        try {
            FileInputStream inputStream = new FileInputStream("./cran.qry");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            String qry = "";
            String[] arr = new String[2];
            while((str = bufferedReader.readLine()) != null) {
                if (!str.startsWith(".I") && !str.startsWith(".W")) {
                    qry = qry + "\s" + str;
                } else if (str.startsWith(".I")) {
                    if (qry.length() > 0) {
                        res[num++] = qry;
                        qry = "";
                    }
                }
            }
            res[num++] = qry;
        }  catch (Exception e) {
            System.out.println("Can not read the cran.qry file");
            e.printStackTrace();
        }
        return res;
    }

    // remove suffix of the string
    public static String removeSuffix(final String s, final String suffix)
    {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }


    public static void main(String[] args) {
        try {
            NewSearchFile searchFile = new NewSearchFile();
            searchFile.newSearchFile();
        }
        finally {

        }
    }
}
