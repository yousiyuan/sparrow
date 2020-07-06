package com.sparrow.backend.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "t_customer")
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_title")
    private String contactTitle;

    @Basic
    private String address;

    @Basic
    private String city;

    @Basic
    private String region;

    @Column(name = "postal_code")
    private String postalCode;

    @Basic
    private String country;

    @Basic
    private String phone;

    @Basic
    private String fax;

}