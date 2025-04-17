package co.swaadisht.swaadisht.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSizeDTO {
    private Integer productId;
    private List<String> sizes;
    private boolean hasSizes;
}
