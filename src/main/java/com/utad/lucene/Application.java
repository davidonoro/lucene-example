package com.utad.lucene;



import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {
    private static RAMDirectory directory =new RAMDirectory();
    private static Analyzer analyzer =new SimpleAnalyzer(Version.LUCENE_47);
    private static IndexWriterConfig config =new IndexWriterConfig(Version.LUCENE_47,analyzer);

    public static void main(String [] args) throws IOException {
        readCommand();
    }

    private static void readCommand() throws IOException {
        String command="";
        while (!command.equals("e")){
            System.out.println("(A)dd information - (S)earch information - (E)xit");
            command=  readLine();
            if(command.toLowerCase().equals("a")){
                addDocument();
            }else if(command.toLowerCase().equals("s")){
                searchDocument();
            }else if(command.toLowerCase().equals("e")){
                endApplication();
            }
        }
    }

    private static void endApplication() {
        System.out.println("Goodbye!!");
    }


    /**
     * search document
     * @throws IOException
     */
    private static void searchDocument() throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher= new IndexSearcher(reader);
        System.out.println("Type of query: (E)xact - (S)tarts with - (W)ildcard");
        String typeOfQuery=readLine().toLowerCase();

        Query query;
        switch (typeOfQuery){
            case "w":
                query = getWildcarQuery();
                break;
            case "e":
                query = getExactQuery();
                break;
            case "s":
                query = getStartWithQuery();
                break;
            default:
                query = getWildcarQuery();
                break;
        }

        TopDocs search = searcher.search(query, 10);
        Document document;
        for(ScoreDoc scoreDoc: search.scoreDocs){
            document=searcher.doc(scoreDoc.doc);
            System.out.println("Name ==>" + document.get("name"));
            System.out.println("Data ==>" + document.get("data"));
        }
        reader.close();

    }

    /**
     * wildcard query
     * @return
     * @throws IOException
     */
    private static Query getWildcarQuery() throws IOException {
        System.out.println("Query:");
        String queryStr=readLine();
        return new WildcardQuery(new Term("data",queryStr));
    }

    /**
     * exact query
     * @return
     * @throws IOException
     */
    private static Query getExactQuery() throws IOException {
        System.out.println("Query:");
        String queryStr=readLine();
        PhraseQuery query = new PhraseQuery();
        String[] terms = queryStr.split(" ");
        for(int i = 0;i<terms.length;i++){
            query.add(new Term("data",terms[i]),i);
        }
        return query;
    }

    /**
     * prefix query
     * @return
     * @throws IOException
     */
    private static Query getStartWithQuery() throws IOException {
        System.out.println("Query:");
        String queryStr=readLine();
        return new PrefixQuery(new Term("data",queryStr));
    }

    /**
     * add doc
     * @throws IOException
     */
    private static void addDocument() throws IOException {
        IndexWriter writer=new IndexWriter(directory, config.clone());
        System.out.println("Name:");
        String name=readLine();
        System.out.println("Data:");
        String data=readLine();
        Document document =new Document();
        document.add(new StringField("name",name, Field.Store.YES));
        document.add(new TextField("data",data, Field.Store.YES));
        writer.addDocument(document);
        writer.commit();
        writer.close();
    }

    /**
     * read line
     * @return
     * @throws IOException
     */
    private static String readLine() throws IOException {
        if (System.console() != null) {
            return System.console().readLine();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        return reader.readLine();
    }

}
