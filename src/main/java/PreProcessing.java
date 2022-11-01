import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.util.Scanner;


public class PreProcessing {
    public void split(){
        //may need to be modified to point to the webpage address
        File file = new File("cran.all.1400");

        try {
            Directory directory = null;
            Scanner scanner = new Scanner(file);
            String line =scanner.nextLine();
            // Create index on ./index
            directory = FSDirectory.open(new File("index/").toPath());
            // divide the cran.all.1400 into 1400 files
            for(int i=1;i<=1400;i++){
                // create file and write text into the file
                String s = "splitdoc/"+i + ".txt";
                File fout = new File(s);
                FileOutputStream fos = new FileOutputStream(fout);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(line);
                bw.newLine();
                line = scanner.nextLine();
                // use ".I" to select the content part
                while(line.contains(".I")==false){
                    bw.write(line);
                    bw.newLine();
                    if(scanner.hasNextLine())
                        line = scanner.nextLine();
                    else
                        break;
                }
                bw.close();
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // main function to preProcess file
    public static void main(String[] args){
        try {
            System.out.println("begin preprocessing cran.all.1400 file");
            PreProcessing preProcessing = new PreProcessing();
            preProcessing.split();
            File sizeOut = new File("lengthOfDocs.txt");
            FileOutputStream outStrm = new FileOutputStream(sizeOut);
            BufferedWriter buffWri = new BufferedWriter(new OutputStreamWriter(outStrm));
            for(int i=1; i<=1400; i++){
                String infile = "splitdoc/" + i +".txt";
                String outfile = "stemmeddoc/" +i + ".txt";
                File file = new File(infile);
                Scanner scanner = new Scanner(file);
                File fout = new File(outfile);
                FileOutputStream fos = new FileOutputStream(fout);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                String line = scanner.nextLine();
                while (line.contains(".W")==false) {
                    line = scanner.nextLine();
                }
                String text = "";
                while(scanner.hasNextLine()){
                    line = scanner.nextLine();
                    line = line.replaceAll("[^a-zA-Z0-9+]" ," ").toLowerCase();
                    // replace stop-word
                    line = line.replaceAll("\\b(s|a|about|above|after|again|against|all|am|an|and|any|are|aren't|as|at|be|because|been|before|being|below|between|both|but|by|can't|cannot|could|couldn't|did|didn't|do|does|doesn't|doing|don't|down|during|each|few|for|from|further|had|hadn't|has|hasn't|have|haven't|having|he|he'd|he'll|he's|her|here|here's|hers|herself|him|himself|his|how|how's|i|i'd|i'll|i'm|i've|if|in|into|is|isn't|it|it's|its|itself|let's|me|more|most|mustn't|my|myself|no|nor|not|of|off|on|once|only|or|other|ought|our|ours|ourselves|out|over|own|same|shan't|she|she'd|she'll|she's|should|shouldn't|so|some|such|than|that|that's|the|their|theirs|them|themselves|then|there|there's|these|they|they'd|they'll|they're|they've|this|those|through|to|too|under|until|up|very|was|wasn't|we|we'd|we'll|we're|we've|were|weren't|what|what's|when|when's|where|where's|which|while|who|who's|whom|why|why's|with|won't|would|wouldn't|you|you'd|you'll|you're|you've|your|yours|yourself|yourselves)\\b\\s?","");
                    text +=line + " ";
                }

                text = text.trim().replaceAll(" +", " ");
                bw.write(text);
                bw.newLine();
                buffWri.write("Length of Document "+i+" is "+text.split(" ").length);
                buffWri.newLine();
                scanner.close();
                bw.close();
            }
            buffWri.close();
            System.out.println("finish preprocessing cran.all.1400 into 1400 files");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}