package com.dolphin.example.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 */
@Data
public class User implements Serializable {

    /**
     * 用户名
     */
    private String username;

}
