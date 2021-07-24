package fr.esgi.dispatcher.code.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Service
public class FileService {
    public boolean createFile(String code, String name, String folderName) {
        try {
            deleteDirectoryStream(Paths.get(folderName));
            File userFile = createUserCompilationFolder(folderName);
            var fos = new FileOutputStream(userFile + File.separator+ name, true);
            byte[] b = code.getBytes();
            fos.write(b);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private File createUserCompilationFolder(String folderName) throws IOException {
        File dir = new File(folderName);
        if (dir.exists()) {
            return dir;
        }
        if (dir.mkdirs()) {
            return dir;
        }
        throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
    }

    public boolean deleteFile(String name, String folderName){
        try{
            deleteDirectoryStream(Paths.get(folderName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        var mainFile = new File(name);
        if(mainFile.exists()){
            return mainFile.delete();
        }
        return false;
    }

    void deleteDirectoryStream(Path path) throws IOException {
        File dir = path.toFile();
        if (dir.exists()) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
