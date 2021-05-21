package fr.esgi.dispatcher.code.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileService {
    public boolean createFile(String code, String name) {
        try {
            var fos = new FileOutputStream(name, true);
            byte[] b = code.getBytes();
            fos.write(b);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFile(String extension){
        if()
    }
}
