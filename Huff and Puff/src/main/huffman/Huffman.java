package main.huffman;

import test.huffman.HuffmanTests;

import java.util.*;
import java.io.ByteArrayOutputStream; // Optional

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {

    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    // TreeMap chosen here just to make debugging easier
    private TreeMap<Character, String> encodingMap;
    // Character that represents the end of a compressed transmission
    private static final char ETB_CHAR = 23;

    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    public Huffman (String corpus) {

        // Creates a list containing one of each unique character in the given corpus:
        ArrayList<Character> charList = new ArrayList<>();
        for (int i = 0; i < corpus.length(); i++) {
            char temp = corpus.charAt(i);
            if ( !(charList.contains(temp)) ) {
                charList.add(temp);
            }
        }

        // Generates a Map with each unique character in the corpus paired with its frequency in said corpus:
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (int i = 0; i < charList.size(); i++) {
            int charCount = 0;
            for (int j = 0; j < corpus.length(); j++) {
                if (charList.get(i) == corpus.charAt(j)) {
                    charCount++;
                }
            }
            frequencyMap.put(charList.get(i), charCount);
        }

        // Creates leaf nodes with each node holding a unique character & its frequency, the ETB_CHAR is also added with a frequency of 1:
        PriorityQueue<HuffNode> nodePriorityQueue = new PriorityQueue<>();
        HuffNode currNode;
        for (int i = -1; i < frequencyMap.size(); i++) { // "i" initialized to -1 to account for the first iteration of the loop appending the ETB_CHAR node first
            if (nodePriorityQueue.size() == 0) {
                currNode = new HuffNode( ETB_CHAR, 1 , ""); // Appends Node to symbolize end of compressed transmission
            } else {
                currNode = new HuffNode( charList.get(i), frequencyMap.get(charList.get(i)), "" ); // Appends a regular node containing a character
            }
            currNode.left = null;
            currNode.right = null;
            nodePriorityQueue.add(currNode);
        }

        // Creates the Huffman Trie:
        while( !(nodePriorityQueue.size() == 0) ) {
            if (nodePriorityQueue.size() > 1) {
                HuffNode currLeft;
                HuffNode currRight;

                currLeft = nodePriorityQueue.poll();
                currRight = nodePriorityQueue.poll();

                currLeft.bitCode = "0";
                currRight.bitCode = "1";

                HuffNode newParent = new HuffNode(' ', (currLeft.count + currRight.count), "" );
                newParent.left = currLeft;
                newParent.right = currRight;

                nodePriorityQueue.add(newParent);
            } else {
                trieRoot = nodePriorityQueue.poll();
            }
        }
        // Initializes the Encoding Map:
        encodingMap = new TreeMap<>();

        // Establishes the encodingMap using a DFS search on the Huffman Trie:
        Trie_Traversal(trieRoot, trieRoot.bitCode, charList);
    }

    /**
     * This method traverses the Huffman Trie using a Depth-First Search traversal.
     * With each traversal, the method checks if the current node's character is one
     * of the characters in the corpus; if so, it adds that character plus the characters
     * bitCode to the encoding map.
     * @param inputNode current node being analyzed by the traversal... used when traversing
     *                  to the left and right sub-nodes if they exist, or returns if the inputNode
     *                  is a leaf node.
     * @param currBitCode integer used to store the bitcode as the Trie is traversed... is mapped to
     *                    a character in the encoding map as that character's bit code.
     * @param charList A list of all the unique characters in the corpus. Passed as a parameter
     *                 so each traversal can check if the current node contains a character in
     *                 the corpus.
     */
    public void Trie_Traversal(HuffNode inputNode, String currBitCode, ArrayList<Character> charList) {

        String currCode = currBitCode;
        if (inputNode == null) { // charList.contains(inputNode.character)
            return;
        }

        currCode += inputNode.bitCode;
        if (charList.contains(inputNode.character) || inputNode.character == ETB_CHAR ) {
            encodingMap.put(inputNode.character, currCode);
        }

        Trie_Traversal(inputNode.right, currCode, charList);
        Trie_Traversal(inputNode.left, currCode, charList);
    }


    // -----------------------------------------------
    // Compression
    // -----------------------------------------------

    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as:
     *         (1) the bitstring containing the message itself, (2) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {

        String outputByteCode = "";
        for (int i = 0; i < message.length(); i++) {
            Character currChar = message.charAt(i);
            outputByteCode += encodingMap.get(currChar);
        }

        outputByteCode += encodingMap.get(ETB_CHAR);
        while ( !(outputByteCode.length() % 8 == 0) ) {
            outputByteCode += "0"; // Adds padding
        }

        int numOfBytes = outputByteCode.length()/8;
        String[] ByteArray = new String[numOfBytes];
        for (int i = 0; i < numOfBytes; i++) {
            ByteArray[i] = "";
        }

        int index = 0;
        for (int i = 0; i < numOfBytes; i++) {
            for (int j = 8; j > 0; j--) {
                ByteArray[i] += outputByteCode.charAt(index);
                index += 1;
            }
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (int i = 0; i < numOfBytes; i++) {
            output.write(Integer.parseInt(ByteArray[i], 2));
        }

        return output.toByteArray();
    }


    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------

    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as:
     *        (1) the bitstring containing the message itself, (2) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {

        String byteString = "";
        int numOfBytes = compressedMsg.length;
        for (int i = 0; i < numOfBytes; i++) {
            byteString += getBinary(compressedMsg[i]);
        }

        String Decompressed = "";
        String currBitCode = "";
        for (int i = 0; i < byteString.length(); i++) {
            currBitCode += byteString.charAt(i);
            for ( Map.Entry<Character, String> entry: encodingMap.entrySet() ) {
                if (entry.getValue().equals(currBitCode)) {
                    if (entry.getValue().equals(encodingMap.get(ETB_CHAR))) {
                        break;
                    } else {
                        Decompressed += entry.getKey();
                        currBitCode = "";
                    }
                }
            }
        }

        return Decompressed;
    }

    /**
     * This function is used to convert the integer equivelent of a byte[] into its
     * binary equivalent.
     * @param inputInt The entered integer that will be converted to its
     *                 binary equivalent.
     * @return returns the binary equivalent of the entered integer as a string.
     */
    public String getBinary (int inputInt) {

        String output = "";
        String binaryNum = Integer.toBinaryString(inputInt);
        if (inputInt < 0) {
            for (int i = 24; i < 32; i++ ) {
                output += binaryNum.charAt(i);
            }
        } else {
            for (int i = 0; i < (8 - binaryNum.length()); i++) {
                output += "0";
            }
            output += binaryNum;
        }

        return output;
    }


    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------

    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents, and a count field that holds the
     * number of times the node's character (or those in its subtrees) appear
     * in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {

        HuffNode left, right;
        char character;
        int count;
        String bitCode;

        HuffNode (char character, int count, String bitCode) {
            this.count = count;
            this.character = character;
            this.bitCode = bitCode;
        }

        public int compareTo (HuffNode other) {
            if (this.character == ETB_CHAR) {
                return -1;
            } else if (this.character == '0') {
                return -1;
            } else {
                if (this.count == other.count) {
                    if ( Character.toLowerCase(this.character) == Character.toLowerCase(other.character) ) {
                        return 0;
                    } else if (Character.toLowerCase(this.character) > Character.toLowerCase(other.character)) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    if (this.count > other.count) {
                        return 1;
                    } else if (this.count < other.count){
                        return -1;
                    }
                }
            }
            return this.character;
        }

    }

}
