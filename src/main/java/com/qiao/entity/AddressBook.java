package com.qiao.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Address Book Entity
 */
@Data
@Entity
@Table(name = "address_book")
public class AddressBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User ID
    private Long userId;

    // Consignee Name (Receiver)
    private String consignee;

    // Phone Number
    private String phone;

    // Area Codes
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;

    // Detailed Address
    private String detail;

    // Label (e.g., Home, Company)
    private String label;

    // Is Default Address: 0 No, 1 Yes
    private Integer isDefault = 0;

    // Creation Time
    private LocalDateTime createTime;

    // Update Time
    private LocalDateTime updateTime;

    // Creator User ID
    private Long createUser;

    // Updater User ID
    private Long updateUser;

    // Logic Delete Status (0: not deleted, 1: deleted)
    // Note: JPA uses physical deletion by default, this is kept for compatibility
    private Integer isDeleted = 0;
}