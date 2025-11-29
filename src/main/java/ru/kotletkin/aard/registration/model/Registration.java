package ru.kotletkin.aard.registration.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Builder
@Table(name = "registration_services")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    String name;

    @NotBlank
    @Column(name = "version", nullable = false)
    String version;

    @NotBlank
    @Column(name = "image", nullable = false)
    String image;

    @URL
    @NotBlank
    @Column(name = "gitlab_link", nullable = false)
    String gitlabLink;

    @NotBlank
    @Column(name = "author", nullable = false)
    String author;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "registration_service_environments",
            joinColumns = @JoinColumn(name = "registration_id")
    )
    @MapKeyColumn(name = "variable_key")
    @Column(name = "variable_value")
    @NotNull
    Map<String, String> environmentVariables = new HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;
}
