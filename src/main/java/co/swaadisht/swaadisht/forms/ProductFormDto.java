package co.swaadisht.swaadisht.forms;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


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
    private List<Integer> selectedToppingIds = new ArrayList<>();
    private boolean customizable;
    private List<Integer> selectedSizeIds;
}
