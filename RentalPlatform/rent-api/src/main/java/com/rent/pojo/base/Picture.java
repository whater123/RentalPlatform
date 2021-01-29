package com.rent.pojo.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Picture {
    String pictureId;
    String pictureName;

    public Picture(String pictureId) {
        this.pictureId = pictureId;
    }
}
