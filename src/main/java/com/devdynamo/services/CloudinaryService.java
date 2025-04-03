package com.devdynamo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinaryConfig;

    public List<String> uploadFiles(List<MultipartFile> files) {
        return files.parallelStream()
                .map(this::uploadSingleFile)
                .collect(Collectors.toList());
    }

    public String uploadSingleFile(MultipartFile gif) {
        try {
            File uploadedFile = convertMultiPartToFile(gif);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            boolean isDeleted = uploadedFile.delete();

            if (isDeleted){
                System.out.println("File successfully deleted");
            }else
                System.out.println("File doesn't exist");
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
