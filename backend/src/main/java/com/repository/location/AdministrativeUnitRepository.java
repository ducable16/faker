package com.repository.location;

import com.entity.AdministrativeRegion;
import com.entity.AdministrativeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrativeUnitRepository extends JpaRepository<AdministrativeUnit, Integer> {
}
