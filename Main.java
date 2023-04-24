import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static final String OUTPUT_TEMPLATE = "Студент %s получил %s по предмету %s.\n";
    static final String OUT_FILE_PATH = "text.txt";

    public static void main(String[] args) {

        String json = "[{\"фамилия\":\"Иванов\",\"оценка\":\"5\",\"предмет\":\"математика\"}," +
                "{\"фамилия\":\"Петров\",\"оценка\":\"3\",\"предмет\":\"физика\"}," +
                "{\"фамилия\":\"Краснов\",\"оценка\":\"5\",\"предмет\":\"Физика\"}]";
        try (FileWriter fileWriter = new FileWriter(OUT_FILE_PATH, false)){
            StringBuilder result = new StringBuilder();
            List<Mark> marks = parseJson(json);
            for (Mark mark : marks) {
                result.append(String.format(OUTPUT_TEMPLATE,mark.secondName,mark.mark,mark.subject));
            }
            fileWriter.write(result.toString());
        } catch (IllegalArgumentException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static List<Mark> parseJson(String json) {
        if (!json.startsWith("[") || !json.endsWith("]")) {
            throw new IllegalArgumentException("Входящий json должен содержать массив");
        }

        json = json.substring(1, json.length() - 1);
        if (json.isBlank()) {
            return Collections.emptyList();
        }

        List<Mark> marks = new LinkedList<>();

        do {
            json = json.trim();
            if (!json.startsWith("{")) {
                throw new IllegalArgumentException("Входящий json некорректный, ожидался символ '{'");
            }
            int objectEndPos = json.indexOf('}');
            if (objectEndPos == -1) {
                throw new IllegalArgumentException("Входящий json некорректный, ожидался символ '}'");
            }

            marks.add(parseObject(json.substring(1, objectEndPos)));

            json = json.substring(objectEndPos + 1).trim().replaceFirst(",", "");
        } while (!json.isBlank());
        return marks;
    }

    static Mark parseObject(String json) {
        Mark mark = new Mark();
        for (String propertyJson : json.split(",")) {
            propertyJson = propertyJson.trim();
            String[] pair = propertyJson.split(":");
            if (pair.length != 2) {
                throw new IllegalArgumentException("Входящий json некорректный");
            }
            String key = parseKeyOrValue(pair[0]);
            switch (key.toLowerCase()) {
                case "фамилия" -> mark.secondName = parseKeyOrValue(pair[1]);
                case "оценка" -> mark.mark = Integer.parseInt(parseKeyOrValue(pair[1]));
                case "предмет" -> mark.subject = parseKeyOrValue(pair[1]);
            }
        }
        return mark;
    }

    static String parseKeyOrValue(String json) {
        String result = json.trim();
        if (result.charAt(0) != '"' || result.charAt(result.length() - 1) != '"') {
            throw new IllegalArgumentException("Входящий json некорректный, ожидался символ '\"'");
        }
        return result.substring(1, result.length() - 1);
    }


    static class Mark {
        private String secondName;
        private String subject;
        private Integer mark;
    }
}


