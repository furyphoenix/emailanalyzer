package neos.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class TextFile extends ArrayList<String> {

    // Normally read by lines:
    public TextFile(String fileName) {
        this(fileName, "\n");
    }

    // Read a file, split by any regular expression:
    public TextFile(String fileName, String splitter) {
        super(Arrays.asList(read(fileName).split(splitter)));

        // Regular expression split() often leaves an empty
        // String at the first position:
        if (get(0).equals("")) {
            remove(0);
        }
    }

    public TextFile(String fileName, String splitter, String charset) {
        super(Arrays.asList(readWithCharset(fileName, charset).split(splitter)));

        // Regular expression split() often leaves an empty
        // String at the first position:
        if (get(0).equals("")) {
            remove(0);
        }
    }

    public static String read(String fileName) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()));

            try {
                String s;

                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    public static String readWithCharset(String fileName, String charset) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader in =
                new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName).getAbsoluteFile()),
                    charset));

            try {
                String s;

                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    // Write a single file in one method call:
    public static void write(String fileName, String text) {
        try {
            PrintWriter out = new PrintWriter(new File(fileName).getAbsoluteFile());

            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeWithCharset(String fileName, String text, String charset) {
        try {
            BufferedWriter bw =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName).getAbsoluteFile()),
                    charset));

            try {
                bw.write(text);
            } finally {
                bw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String fileName) {
        try {
            PrintWriter out = new PrintWriter(new File(fileName).getAbsoluteFile());

            try {
                for (String item : this) {
                    out.println(item);
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeWithCharset(String fileName, String charset) {
        try {
            BufferedWriter bw =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName).getAbsoluteFile()),
                    charset));

            try {
                for (String item : this) {
                    bw.write(item + "\n");
                }
            } finally {
                bw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean add(String item) {
        if (!this.contains(item)) {
            return super.add(item);
        } else {
            return false;
        }
    }
}
