package edu.wisc.ece.pockethow;

/*
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Cameron on 10/18/2017.
 */
/*
public class LuceneSearch {
    public void runLucene(){
        try {
            Analyzer analyzer = new StandardAnalyzer();

            //Store index in Memory
            Directory directory = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter iwriter = new IndexWriter(directory, config);
            Document doc = new Document();
            String text = "This is the text to be indexed.";
            doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
            iwriter.addDocument(doc);
            iwriter.close();

            //Now search
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            // Parse a simple query that searcehs for "text"
            QueryParser parser = new QueryParser("fieldname", analyzer);
            Query query = parser.parse("text");
            ScoreDoc[] hits = isearcher.search(query,1000,null).scoreDocs;
            assertEquals(1, hits.length);
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
            }
            ireader.close();
            directory.close();
        }
        catch (IOException e){

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
*/