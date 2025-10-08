package ru.kotletkin.subatomics.deployments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kotletkin.subatomics.deployments.model.DeploymentPlane;

import java.util.List;

@Repository
public interface DeploymentPlaneRepository extends JpaRepository<DeploymentPlane, Long> {
    List<DeploymentPlane> findByNamespace(String namespace);
}
