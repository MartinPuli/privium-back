package Marketplace.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @Column(length = 20)           // varchar(20) en la tabla
    private String id;

    @Column(nullable = false, length = 100)
    private String name;
}
