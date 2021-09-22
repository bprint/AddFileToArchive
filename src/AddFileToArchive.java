import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class AddFileToArchive {

/*
Добавление файла в архив
В метод main приходит список аргументов. Первый аргумент - полный путь к файлу fileName.
Второй аргумент - путь к zip-архиву. Добавить файл (fileName) внутрь архива в директорию 'new'.
Если в архиве есть файл с таким именем, то заменить его.

Пример входных данных:
C:/result.mp3
C:/pathToTest/test.zip

Файлы внутри test.zip:
a.txt
b.txt

После запуска Solution.main архив test.zip должен иметь такое содержимое:
new/result.mp3
a.txt
b.txt

Подсказка: нужно сначала куда-то сохранить содержимое всех энтри, а потом записать в архив все энтри вместе с добавленным файлом.
Пользоваться файловой системой нельзя.
*/


    public static void main(String[] args) throws IOException {
        Path file = Paths.get(args[0]);
        Map<Path, byte[]> map = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(args[1]))) {
            ZipEntry currentEntry;
            byte[] buffer = new byte[1024];
            while ((currentEntry = zis.getNextEntry()) != null) {
                if (!currentEntry.toString().equals("new/" + file.getFileName())) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, length);
                    }
                    map.put(Paths.get(currentEntry.getName()), baos.toByteArray());
                }
            }
        }
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(args[1]))) {
            zos.putNextEntry(new ZipEntry("new/" + file.getFileName()));
            Files.copy(file, zos);

            for (Map.Entry<Path, byte[]> entry : map.entrySet()) {
                if (!entry.getKey().getFileName().toString().equals("new/" + file.getFileName())) {
                    ZipEntry zipEntry = new ZipEntry(entry.getKey().toString());
                    zos.putNextEntry(zipEntry);
                    zos.write(entry.getValue());
                }
            }
        }
    }
}
