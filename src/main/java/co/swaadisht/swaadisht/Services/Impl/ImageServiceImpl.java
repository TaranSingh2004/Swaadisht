package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Services.ImageService;
import co.swaadisht.swaadisht.helpers.AppContants;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;

    @Value("${app.default.product-image}")
    private String defaultProductImageId;  // Configure in application.properties

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile file, String filename) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        byte[] data = file.getBytes();
        cloudinary.uploader().upload(data, ObjectUtils.asMap(
                "public_id", filename,
                "folder", "product_images"  // Add folder organization
        ));

        return this.getUrlFromPublicId(filename);
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary.url()
                .transformation(new Transformation<>()
                        .width(800)  // Adjust as needed
                        .height(800)
                        .crop("fill"))
                .generate(publicId);
    }

    @Override
    public String getDefaultProductImageUrl() {
        return this.getUrlFromPublicId(defaultProductImageId);
    }
}