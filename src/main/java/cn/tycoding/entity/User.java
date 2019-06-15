package cn.tycoding.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tycoding
 * @date 2019-06-13
 */
@Data
public class User implements Serializable {

    private Long id;

    private String name;

    private String avatar;

    public void setName(String name) {
        this.name = name.trim();
    }
}
