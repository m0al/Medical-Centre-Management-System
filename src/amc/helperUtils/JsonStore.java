package amc.helperUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Simple JSON helper. It reads and writes List<T> to a file.
public final class JsonStore {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private JsonStore(){}

    // Ensure the file and its folder exist. Create an empty JSON array if missing.
    private static void ensureFile(String filePath) {
        try {
            File file = new File(filePath);
            File parentFolder = file.getParentFile();
            if (parentFolder != null && !parentFolder.exists()) { parentFolder.mkdirs(); }
            if (!file.exists()) {
                Writer writer = new FileWriter(file);
                writer.write("[]");
                writer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Ensure the file and its folder exist. Create an empty JSON object if missing.
    private static void ensureObjectFile(String filePath) {
        try {
            File file = new File(filePath);
            File parentFolder = file.getParentFile();
            if (parentFolder != null && !parentFolder.exists()) { parentFolder.mkdirs(); }
            if (!file.exists()) {
                Writer writer = new FileWriter(file);
                writer.write("{}"); // Write an empty JSON object
                writer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Read a JSON array into a list. Return an empty list on error.
    public static <T> List<T> readList(String filePath, Type listType) {
        ensureFile(filePath);
        Reader reader = null;
        try {
            reader = new FileReader(filePath);
            List<T> data = gson.fromJson(reader, listType);
            if (data == null) return new ArrayList<T>();
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<T>();
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignore) {}
        }
    }

    // Write the whole list back to the file. Overwrite existing content.
    public static <T> void writeList(String filePath, List<T> data, Type listType) {
        ensureFile(filePath);
        Writer writer = null;
        try {
            writer = new FileWriter(filePath, false);
            gson.toJson(data, listType, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try { if (writer != null) writer.close(); } catch (Exception ignore) {}
        }
    }

    // Read a JSON object into a generic type. Return a new instance on error or if file is empty.
    public static <T> T readObject(String filePath, Class<T> classOfT) {
        ensureObjectFile(filePath);
        Reader reader = null;
        try {
            reader = new FileReader(filePath);
            T data = gson.fromJson(reader, classOfT);
            if (data == null) {
                try {
                    return classOfT.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    // Fallback for types without a default constructor, or rethrow
                    return null;
                }
            }
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                return classOfT.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignore) {}
        }
    }

    // Write a generic object to a JSON file. Overwrite existing content.
    public static <T> void writeObject(String filePath, T object) {
        ensureObjectFile(filePath);
        Writer writer = null;
        try {
            writer = new FileWriter(filePath, false);
            gson.toJson(object, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try { if (writer != null) writer.close(); } catch (Exception ignore) {}
        }
    }
}