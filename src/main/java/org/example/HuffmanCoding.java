package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Author: [Islam Yasser]
 * Academic Integrity Acknowledgment:
 * I acknowledge that I am aware of the academic integrity guidelines of this course,
 * and that I worked on this assignment independently without any unauthorized help.
 */

public class HuffmanCoding {
    private int bytesSize;
    private long codedStrLength;
    private int originalStrLength;
    private String originalStr;
    public HuffmanCoding() {
        this.bytesSize = 0;
        this.codedStrLength = 0;
        this.originalStr = null;
    }
    public Map<String , Integer> getWordsFrequency(String filePath, int n) throws IOException {
        byte[] originalFile = Files.readAllBytes(Path.of(filePath));

        originalStr = new String(originalFile, StandardCharsets.ISO_8859_1);
        originalStrLength = originalStr.length();
        originalFile = null;

        Map<String, Integer> frequencies = new HashMap<>();
        int i;
        for(i = 0; i < originalStr.length() - n; i += n) {
            String word = originalStr.substring(i, i+n);
            frequencies.merge(word, 1, Integer::sum);
        }
        String word = originalStr.substring(i);
        frequencies.merge(word, 1, Integer::sum);
        return frequencies;
    }

    public PriorityQueue<Node> toPriorityQueue(Map<String, Integer> frequencies) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>((a, b) -> Integer.compare(a.frequency, b.frequency));
        frequencies.forEach((word, frequency) -> priorityQueue.offer(new Node(frequency, word)));
        return priorityQueue;
    }

    public Node HuffmanTreeConstruction(PriorityQueue<Node> priorityQueue) {
        while(priorityQueue.size() >= 2) {
            Node node1 = priorityQueue.poll();
            Node node2 = priorityQueue.poll();
            priorityQueue.offer(new Node(node1.frequency + node2.frequency, node1, node2));
        }
        return priorityQueue.peek();
    }

    public Map<String, String> assignCodes(Node root, Map<String, Integer> frequencies) {
        Map<String, String> codeMap = new HashMap<>();
        assignCodes(root, "", codeMap, frequencies);
        return codeMap;
    }
    public void assignCodes(Node node, String code, Map<String, String> codeMap, Map<String, Integer> frequencies) {
        if(node.word != null) {
            codeMap.put(node.word, code);
            codedStrLength += (long) frequencies.get(node.word) * code.length();
            return;
        }
        assignCodes(node.left, code + "0", codeMap, frequencies);
        assignCodes(node.right, code + "1", codeMap, frequencies);
    }
    private void writeHuffmanTree(Node node, ObjectOutputStream oos) throws IOException {
        oos.writeObject(node.word);
        if(node.left != null) {
            writeHuffmanTree(node.left, oos);
            writeHuffmanTree(node.right, oos);
        }
    }
    public long SaveCompressedFile(Map<String, String> codeMap, int n, String filePath, Node root) throws IOException {
        int lastSeparatorIndex = filePath.lastIndexOf(File.separator);
        String compressedFilePath = "";
        if(lastSeparatorIndex == -1) {
            compressedFilePath = 20010312 + "." + n + "." + filePath + ".hc";
        }
        else {
            String directoryPath = filePath.substring(0, lastSeparatorIndex + 1);
            compressedFilePath = directoryPath + 20010312 + "." + n + "." + filePath.substring(lastSeparatorIndex + 1) + ".hc";
        }
        FileOutputStream fos = new FileOutputStream(compressedFilePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeInt((int) codedStrLength % 8 == 0 ? 8 : (int) codedStrLength % 8);
        writeHuffmanTree(root, oos);
        root = null;
        bytesSize = (int) ((codedStrLength + 7) / 8);
        byte[] bytes = new byte[bytesSize];
        int k = 0;
        StringBuilder codedStr = new StringBuilder();
        int i;
        for (i = 0; i < originalStr.length() - n; i += n) {
            String word = originalStr.substring(i, i+n);
            codedStr.append(codeMap.get(word));
            while (codedStr.length() >= 8) {
                bytes[k++] = (byte) Integer.parseInt(codedStr.substring(0, 8), 2);
                codedStr.delete(0, 8);
            }
        }
        String word = originalStr.substring(i);
        originalStr = null;
        codedStr.append(codeMap.get(word));
        while (codedStr.length() >= 8) {
            bytes[k++] = (byte) Integer.parseInt(codedStr.substring(0, 8), 2);
            codedStr.delete(0, 8);
        }
        if(codedStrLength % 8 != 0) {
            int shift = (int) (8 - codedStrLength % 8);
            bytes[k] = (byte) Integer.parseInt(String.valueOf(codedStr), 2);
            bytes[k] = (byte) (bytes[k] << shift);
        }
        fos.write(bytes);
        fos.flush();
        bytes = null;
        oos.close();
        fos.close();
        return new File(compressedFilePath).length();
    }

    private Node readHuffmanTree(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return readNode(ois);
    }
    private Node readNode(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        String word = (String) ois.readObject();
        if(word != null) return new Node(word, null, null);
        Node left = readNode(ois);
        Node right = readNode(ois);
        return new Node(word, left, right);
    }
    public void decompressFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);
            int lastByte = ois.readInt();
            Node root = readHuffmanTree(ois);

            int remainingBytes = bis.available();
            byte[] bytes = new byte[remainingBytes];
            bis.read(bytes);

            int lastSeparatorIndex = filePath.lastIndexOf(File.separator);
            String directoryPath = lastSeparatorIndex != -1 ? filePath.substring(0, lastSeparatorIndex + 1) : "";
            String fileName = filePath.substring(lastSeparatorIndex + 1);
            fileName = fileName.substring(0, fileName.length() - 3);
            FileOutputStream fos = new FileOutputStream(directoryPath + "extracted." + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            Node currNode = root;
            for (int j = 0; j < bytes.length - 1; j++) {
                for (int i = 7; i >= 0; i--) {
                    if(((bytes[j] >> i) & 1) != 0) currNode = currNode.right;
                    else currNode = currNode.left;
                    if(currNode.word != null) {
                        bos.write(currNode.word.getBytes(StandardCharsets.ISO_8859_1));
                        currNode = root;
                    }
                }
            }
            for(int i = 7; i >= 8 - lastByte; i--) {
                if(((bytes[remainingBytes-1] >> i) & 1) != 0) currNode = currNode.right;
                else currNode = currNode.left;
                if(currNode.word != null) {
                    bos.write(currNode.word.getBytes(StandardCharsets.ISO_8859_1));
                    currNode = root;
                }
            }
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public double[] compress(String filePath, int n) throws IOException {
        long t1;
        long start = t1 = System.currentTimeMillis();
        Map<String, Integer> frequencies = getWordsFrequency(filePath, n);
        long t2 = System.currentTimeMillis();
        System.out.println("frequency time: " + (t2-t1));

        PriorityQueue<Node> priorityQueue = toPriorityQueue(frequencies);
        t1 = System.currentTimeMillis();
        System.out.println("to priority queue time: " + (t1-t2));

        Node root = HuffmanTreeConstruction(priorityQueue);
        t2 = System.currentTimeMillis();
        System.out.println("tree construction time: " + (t2-t1));

        Map<String, String> codeMap = assignCodes(root, frequencies);
        t1 = System.currentTimeMillis();
        System.out.println("assign codes time: " + (t1-t2));

        long fileSize = SaveCompressedFile(codeMap, n, filePath, root);
        t2 = System.currentTimeMillis();
        System.out.println("compressing time: " + (t2-t1));
        System.out.println("whole time: " + (t2-start));
        float compressionRatio = (float) fileSize / originalStrLength;
        System.out.println("compression ratio: " + compressionRatio);
        return new double[]{(double) t2-start, compressionRatio};
    }

    public long decompress(String filePath) {
        long t1 = System.currentTimeMillis();
        decompressFile(filePath);
        long t2 = System.currentTimeMillis();
        System.out.println("decompressing time: " + (t2-t1));
        return t2-t1;
    }

    public static void main(String[] args) throws IOException {
        if(args.length == 3 && args[0].equals("c"))
            new HuffmanCoding().compress(args[1], Integer.parseInt(args[2]));

        else if(args.length == 2 && args[0].equals("d"))
            new HuffmanCoding().decompress(args[1]);
    }

}