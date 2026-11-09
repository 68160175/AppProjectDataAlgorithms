package Project;

import java.io.*;

public class DataStore {
    private static final String FILE_NAME = "src/Project/duolearn-data.ser";

    public static AppData load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new AppData();

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            Object object = input.readObject();
            if (object instanceof AppData) return (AppData) object;
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("ไม่สามารถอ่านข้อมูลเดิมได้: " + ex.getMessage());
        }
        return new AppData();
    }

    public static void save(AppData data) {
        try (ObjectOutputStream output =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            output.writeObject(data);
        } catch (IOException ex) {
            System.err.println("ไม่สามารถบันทึกข้อมูลได้: " + ex.getMessage());
        }
    }
}
