import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class Log {
    public Logger logger;
    FileHandler handler;
    public Log(String fileName) throws SecurityException, IOException
    {
        File file = new File(fileName); //создание файла
        handler = new FileHandler(fileName, true); //работа с файлом
        logger = Logger.getLogger("logger"); //логгер
        logger.addHandler(handler); //привязка файла к логгеру
        SimpleFormatter formatter = new SimpleFormatter(); //форматирование файла
        handler.setFormatter(formatter);
    }
}
