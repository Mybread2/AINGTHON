package org.demo.aingthon.global.storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.demo.aingthon.global.exception.BusinessException;
import org.demo.aingthon.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GcsService {

    private Storage storage;
    private final String bucketName;
    private final String credentialsPath;

    public GcsService(
            @Value("${gcp.storage.bucket}") String bucketName,
            @Value("${gcp.credentials.path:}") String credentialsPath) {
        this.bucketName = bucketName;
        this.credentialsPath = credentialsPath;
    }

    private synchronized Storage storage() {
        if (storage == null) {
            try {
                StorageOptions.Builder builder = StorageOptions.newBuilder();
                if (credentialsPath != null && !credentialsPath.isBlank()) {
                    File credFile = new File(credentialsPath);
                    if (credFile.exists()) {
                        builder.setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(credFile)));
                    }
                }
                storage = builder.build().getService();
            } catch (IOException e) {
                throw new RuntimeException("GCS 초기화 실패: " + credentialsPath, e);
            }
        }
        return storage;
    }

    public String upload(MultipartFile file, String folder) {
        try {
            String objectName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            storage().create(blobInfo, file.getBytes());
            return objectName;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public String getSignedUrl(String objectName) {
        if (objectName == null) return null;
        URL url = storage().signUrl(
                BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build(),
                7, TimeUnit.DAYS,
                Storage.SignUrlOption.withV4Signature()
        );
        return url.toString();
    }

    public void delete(String objectName) {
        if (objectName != null) {
            storage().delete(BlobId.of(bucketName, objectName));
        }
    }
}
