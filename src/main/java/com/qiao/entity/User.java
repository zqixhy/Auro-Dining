package com.qiao.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * Mobile User Entity.
 * Login and verification code are by email (AWS SES). Phone is optional for contact.
 */
@Data
@Entity
@Table(name = "\"user\"")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** Optional; used for contact. Login identity is email. */
    private String phone;

    /** Login identity; verification code sent via AWS SES. */
    private String email;

    // Map the database field 'id_number' to the Java field
    @Column(name = "id_number")
    private String idNumber;

    private String avatar;

    // Status: 0 Disabled, 1 Enabled
    private Integer status;

    private String password;
}
