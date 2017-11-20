package dictionaryserver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class Dictionary {

    // key value pairs of dictionary words
    public SortedMap<String, List<String>> map;

    private String filePath;

    public Dictionary(String fileName) {
        this.filePath = fileName;
        this.map = (SortedMap<String, List<String>>) Collections.synchronizedSortedMap(
                new TreeMap<String, List<String>>());

        this.readJSONFile(fileName);
    }

    public synchronized void readJSONFile(String fileName) {
        JSONParser parser = new JSONParser();

        try {
            Object object = parser.parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject) object;

            // load json into treemap
            for (Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
                String word = (String) iterator.next();

                JSONArray definitions = (JSONArray) jsonObject.get(word);
                List<String> defsList = new ArrayList<>();
                for (Iterator iter = definitions.iterator(); iter.hasNext();) {
                    defsList.add((String) iter.next());
                }

                map.put(word, defsList);
            }

        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateJSONFile(String json) {
        try {
            RandomAccessFile file = new RandomAccessFile(this.filePath, "rw");

            // search for "}" in the file
            long pos = file.length();
            while (pos > 0) {
                pos--;
                file.seek(pos);
                if (file.readByte() == '}') {
                    file.seek(pos);
                    break;
                }
            }

            if (pos <= 0) {
                throw new Exception("JSON file cannot be parsed");
            }

            file.writeBytes("," + json + "}");
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // convenient method to write JSON file from JSONObject
    public void writeJSONFile(JSONObject jsonObject) {
        try {
            FileWriter file = new FileWriter(this.filePath);
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addWord(String word, List<String> defs) {
        if (this.hasWord(word)) {
            System.out.println("Word " + word + " already exists. Cannot add");
            return;
        }

        this.map.put(word, defs);

        // create json object and get its json string equivalent
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(word, defs);
        String json = jsonObject.toJSONString();
        json = json.substring(1, json.length() - 1);    // trim curly brace of json object

        this.updateJSONFile(json);
    }

    public synchronized void removeWord(String word) {
        List<String> values = this.map.remove(word);
        if (values == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        for (Iterator iterator = this.map.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            List<String> defs = this.map.get(key);
            jsonObject.put(key, defs);
        }

        this.writeJSONFile(jsonObject);
    }

    public List<String> getDefinitions(String word) {
        return this.map.get(word);
    }

    public boolean hasWord(String word) {
        return this.map.get(word) != null;
    }

    public Set<String> keySet() {
        System.out.println(this.map.keySet());
        return this.map.keySet();
    }

}
