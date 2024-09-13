package service;

import domain.entity.Board;
import domain.entity.UploadImage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import repository.BoardRepository;
import repository.UploadImageRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadImageService {
    private final UploadImageRepository uploadImageRepository;
    private final BoardRepository boardRepository;
    private final String rootPath = System.getProperty("user.dir");
    private final String fileDir = rootPath + "/src/main/resources/static/upload-images/";

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public UploadImage saveImage(MultipartFile multipartFile, Board board) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        // 元のファイル名 -> サーバーに保存されたファイル名（重複なし）
        // ファイル名が重複しないように、UUIDを使用して設定し、拡張子は維持
        String savedFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

        // ファイルを保存
        try {
            Path path = Paths.get(fileDir, savedFilename);
            Files.createFile(path);
            multipartFile.transferTo(path);
        } catch (Exception e) {
            System.out.println("create file failed");
        }

        return uploadImageRepository.save(UploadImage.builder()
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                //.board(board)
                .build());
    }

    @Transactional
    public void deleteImage(UploadImage uploadImage) throws IOException {
        uploadImageRepository.delete(uploadImage);
        Files.deleteIfExists(Paths.get(getFullPath(uploadImage.getSavedFilename())));
    }

    // 拡張子を抽出
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public ResponseEntity<UrlResource> downloadImage(Long boardId) throws MalformedURLException {
        //boardIdに該当する投稿がない場合は null return
        Board board = boardRepository.findById(boardId).get();
        if (board == null || board.getUploadImage() == null) {
            return null;
        }

        UrlResource urlResource = new UrlResource("file:" + getFullPath(board.getUploadImage().getSavedFilename()));

        // アップロードされたファイル名が韓国語の場合、以下の作業を行わないと文字化けする可能性があります
        String encodedUploadFileName = UriUtils.encode(board.getUploadImage().getOriginalFilename(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        // HEADERに CONTENT_DISPOSITION を設定し、クリック時にファイルのダウンロードが進行す
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);

    }
}
