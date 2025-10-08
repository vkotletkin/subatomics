package ru.kotletkin.subatomics.deployments.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Builder
@Table(name = "deployments_planes")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeploymentPlane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    String name;

    @NotBlank
    @Column(name = "namespace", nullable = false)
    String namespace;

    @NotBlank
    @Column(name = "requester_name", nullable = false)
    String requesterName;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "deployment_manifests",
            joinColumns = @JoinColumn(name = "deployment_plane_id")
    )
    @MapKeyColumn(name = "module_name")
    @Column(name = "module_manifest", columnDefinition = "TEXT")
    @NotNull
    Map<String, String> modulesManifestMap = new HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;
}
