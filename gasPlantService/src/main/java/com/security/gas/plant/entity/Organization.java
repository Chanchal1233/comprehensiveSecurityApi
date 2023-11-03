package com.security.gas.plant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "organization_table")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long id;

    @Column(name = "organization_name")
    private String name;

    @Column(name = "organization_registration_number")
    private Integer reg;

    @Column(name = "organization_industry")
    private String industry;

    @Column(name = "organization_location")
    private String location;

    @Column(name = "organization_contact")
    private Integer contact;

    @OneToMany(targetEntity = Company.class, cascade = CascadeType.ALL)
    @JoinColumn(name ="organization_id", referencedColumnName = "organization_id")
    private List<Company> companies;
}