import java.io.*;

import org.apache.lucene.analysis.classic.ClassicAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class CreateIndex {
    private static final String PATH_OF_FILE = "stemmeddoc/";   // directory of the index file 1
    private static final String PATH_OF_INDEX = "index/"; // directory of the index file 2
    private void createIndex() {
        Directory directory = null;
        // Write index through IndexWriter
        IndexWriter writer = null;
        // Use a doc to save the index table
        Document doc = null;
        try {
            // Create index on ./index
            directory = FSDirectory.open(new File(PATH_OF_INDEX).toPath());
            // Use IndexWriterConfig() to bulid IndexWriter and use StandardAnalyzer() to Participle
            // The version of LUCENE IS 9.0.0
            writer = new IndexWriter(directory, new IndexWriterConfig(new SimpleAnalyzer()));
            System.out.println("begin creating index");
            for (File file : new File(PATH_OF_FILE).listFiles()) {
                doc = new Document();
                // Create field of the index (for the field "content")
                FieldType fieldType = new FieldType();
                fieldType.setStored(false);
                fieldType.setTokenized(true);
                fieldType.setStoreTermVectors(true);
                // set IndexOptions
                fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // 请尝试不同的选项的效果
                doc.add(new Field("content", getContentFromFile(file),fieldType));
                // Create field of the index (for the field "fileName" and "filePath")
                FieldType fieldType2 = new FieldType();
                fieldType2.setStored(true);
                fieldType2.setTokenized(false);
                // set IndexOptions
                fieldType2.setIndexOptions(IndexOptions.NONE); // 请尝试不同的选项的效果
                doc.add(new Field("fileName", file.getName(),fieldType2));
                doc.add(new Field("filePath", file.getAbsolutePath(),fieldType2));
                writer.addDocument(doc);
            }
            System.out.println("finish creating index");
        }
        catch (Exception e) {
            System.out.println("An exception was encountered during the creation of the index, the stack trace is as follows");
            e.printStackTrace();
        }
        finally {
            if (null != writer) {
                try {
                    // close IndexWriter after finishing using it
                    writer.close();
                }
                catch (IOException ce) {
                    System.out.println("An exception was encountered when closing IndexWriter, the stack trace is as follows");
                    ce.printStackTrace();
                }
            }
        }
    }

    // process cranfield document and get context of each document
    private String getContentFromFile(File myFile) {
        StringBuffer sb = new StringBuffer();
        if (!myFile.exists()) {
            return "";
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(myFile));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
        }
        catch (IOException e) {
            e.getStackTrace();
        }
        return sb.toString();
    }


// Run main function to create index
    public static void main(String[] args) {
        try {
            CreateIndex createIndex = new CreateIndex();
            createIndex.createIndex();
        }
       finally {

        }
    }

}
