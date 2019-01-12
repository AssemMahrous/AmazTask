package task.com.amaz.amaztask.util;

public class MessageHelper {

    public static String getSentiment(String message) {
        return message.substring(message.lastIndexOf(": ") + 2);
    }

    public static String getMessage(String message) {
        return message.substring(message.indexOf("message: ") + 9
                , message.lastIndexOf(","));
    }

    public static String getCity(String message) {
        if (message.contains(",")) {
            if (message.contains(" in"))
                return message.substring(message.lastIndexOf("in ") + 3, message.indexOf(","));
            else if (message.contains("the "))
                return message.substring(message.lastIndexOf("the ") + 4, message.indexOf(","));
            else return message.substring(message.lastIndexOf(", ") + 2, message.length());
        } else {
            if (message.contains(" in"))
                return message.substring(message.lastIndexOf("in ") + 3, message.length());
            else if (message.contains(" is"))
                return message.substring(0, message.indexOf(" "));
            else
                return message.substring(message.lastIndexOf(" "), message.length());
        }
    }
}
