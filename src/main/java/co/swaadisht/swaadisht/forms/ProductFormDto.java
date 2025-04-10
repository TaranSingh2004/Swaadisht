package co.swaadisht.swaadisht.forms;

import co.swaadisht.swaadisht.entities.CustomizationIngredient;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductFormDto {

    private Integer id;
    private String name;
    private String description;
    private boolean status;
    private int stock;
    private int price;
    private int discount;
    private int discountPrice;
    private int categoryId;
    private MultipartFile imageFile;
    private List<Integer> selectedIngredientIds = new ArrayList<>();
    private boolean customizable;
}
