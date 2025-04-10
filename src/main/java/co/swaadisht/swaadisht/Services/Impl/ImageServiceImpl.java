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
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;

    @Value("${app.default.product-image}")
    private String defaultProductImageId;  // Configure in application.properties

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(String file, String publicId) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "product_images",
                "overwrite", true,
                "resource_type", "image"
        );

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("secure_url");
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

    @Override
    public String getDefaultImageUrl() {
        return cloudinary.url().generate("default_product_image");
    }
}