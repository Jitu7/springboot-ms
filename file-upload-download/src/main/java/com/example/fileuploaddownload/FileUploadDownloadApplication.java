package com.example.fileuploaddownload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FileUploadDownloadApplication {

  public static void main(String[] args) {
    SpringApplication.run(FileUploadDownloadApplication.class, args);
  }

}

@Slf4j
@RestController
@RequiredArgsConstructor
class UploadDownloadWithFileSystemController {

  private final FileStorageService fileStorageService;

  @PostMapping("single/upload")
  FileUploadResponse singleUpload(@RequestParam("file") MultipartFile file) {

    final String contentType = file.getContentType();

    log.info("{} uploading file original name", file.getOriginalFilename());
    log.info("{} uploading file content type", contentType);

    String fileName = fileStorageService.storeFile(file);

    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
     .path("/download/")
     .path(fileName)
     .toUriString();

    return new FileUploadResponse(fileName, contentType, url);
  }

  @GetMapping("/download/{fileName}")
  ResponseEntity<Resource> downloadSingleFile(@PathVariable String fileName, ServletRequest request) {
    Resource resource = fileStorageService.downloadFile(fileName);

//    final MediaType contentType = MediaType.APPLICATION_PDF;

    String contentType;

    try {
      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    } catch (IOException e) {
      contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }


    return ResponseEntity.ok()
     .contentType(MediaType.parseMediaType(contentType))
//     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + resource.getFilename())
     .header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFilename())
     .body(resource);
  }
}

class UploadDownloadWithDatabaseController {

}

@Service
class FileStorageService {

  private final Path fileStoragePath;

  private final String fileStorageLocation;

  public FileStorageService(@Value("${file.storage.location:temp}") String fileStorageLocation) {

    this.fileStorageLocation = fileStorageLocation;
    this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

    try {
      Files.createDirectories(fileStoragePath);
    } catch (IOException e) {
      ReflectionUtils.rethrowRuntimeException(new RuntimeException("Issuing Creating File Directory"));
    }

  }

  public String storeFile(MultipartFile file) {

    final var fileName = StringUtils.cleanPath(file.getOriginalFilename());
    final var filePath = Paths.get(fileStoragePath + "/" + fileName);

    try {
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      ReflectionUtils.rethrowRuntimeException(new RuntimeException("Issuing Creating File Directory"));
    }

    return fileName;
  }

  public Resource downloadFile(String fileName) {

    final Path path = Paths.get(fileStorageLocation).resolve(fileName);

    Resource resource = new FileSystemResource(path);

    if (resource.exists() && resource.isReadable()) {
      return resource;
    } else {
      throw new RuntimeException("the file doesn't exit or not readable");
    }

  }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class FileUploadResponse {
  private String fileName;
  private String ContentType;
  private String url;
}
