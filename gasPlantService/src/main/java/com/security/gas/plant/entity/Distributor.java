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
@Table(name = "distributors_table")
public class Distributor{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "distributor_id")
    private Long id;

    @Column(name = "distributor_name")
    private String name;

    @Column(name = "distributor_reg")
    private Integer reg;

    @Column(name = "distributor_contact")
    private Long contact;

    @Column(name = "distributor_address")
    private String address;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name ="distributor_id", referencedColumnName = "distributor_id")
    private List<User> users;

    @OneToMany(targetEntity = Employee.class, cascade = CascadeType.ALL)
    @JoinColumn(name ="distributor_id", referencedColumnName = "distributor_id")
    private List<Employee> employees;
}