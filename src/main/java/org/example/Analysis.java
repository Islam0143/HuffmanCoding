package org.example;

import java.io.IOException;

public class Analysis {

    public static void main(String[] args) throws IOException {
        for(int i = 1; i < 6; i++) {
            double[] compressionResults1 = new HuffmanCoding().compress("C:\\Users\\Islam\\Desktop\\pdf\\Algorithms - Lectures 7 and 8 (Greedy algorithms).pdf", i);
            System.out.print((long)compressionResults1[0] + "\t" + compressionResults1[1] + "\t");
            System.out.print(new HuffmanCoding().decompress("C:\\Users\\Islam\\Desktop\\pdf\\20010312."+i+".Algorithms - Lectures 7 and 8 (Greedy algorithms).pdf.hc") + "\t");

            double[] compressionResults2 = new HuffmanCoding().compress("C:\\Users\\Islam\\Desktop\\seq\\gbbct10.seq", i);
            System.out.print((long)compressionResults2[0] + "\t" + compressionResults2[1] + "\t");
            System.out.println(new HuffmanCoding().decompress("C:\\Users\\Islam\\Desktop\\seq\\20010312."+i+".gbbct10.seq.hc"));
        }
    }
}
