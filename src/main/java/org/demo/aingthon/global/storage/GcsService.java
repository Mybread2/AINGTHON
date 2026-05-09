package org.demo.aingthon.global.storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.demo.aingthon.global.exception.BusinessException;
import org.demo.aingthon.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GcsService {

    private final Storage storage;
    private final String bucketName;

    public GcsService(
            @Value("${gcp.storage.bucket}") String bucketName,
            @Value("${gcp.credentials.path}") String credentialsPath) {
        this.bucketName = bucketName;
        try {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(
                    new FileInputStream(credentialsPath));
            this.storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException("GCS 인증 파일 로드 실패: " + credentialsPath, e);
        }
    }

    public String upload(MultipartFile file, String folder) {
        try {
            String objectName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            storage.create(blobInfo, file.getBytes());
            return objectName;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public String getSignedUrl(String objectName) {
        if (objectName == null) return null;
        URL url = storage.signUrl(
                BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build(),
                7, TimeUnit.DAYS,
                Storage.SignUrlOption.withV4Signature()
        );
        return url.toString();
    }

    public void delete(String objectName) {
        if (objectName != null) {
            storage.delete(BlobId.of(bucketName, objectName));
        }
    }
}
