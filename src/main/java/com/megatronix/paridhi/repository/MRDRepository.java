package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.model.MRD;
import com.megatronix.paridhi.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MRDRepository extends JpaRepository< MRD, Long > {
	List< MRD > findAllByUser ( User user );

	Optional< MRD > findByGid ( String gid );

	@EntityGraph ( attributePaths = {"user"} )
	Optional< MRD > findWithUserByGid ( String gid );

	boolean existsByGid ( String gid );

	List< MRD > findByHasPaid ( boolean hasPaid );

	boolean existsByGidAndUserEmail ( String gid, String userEmail );

	boolean existsByGidAndHasPaidTrue ( String gid );

	@Query ( "SELECT DISTINCT m.user.email FROM MRD m WHERE m.gid IN :gidList" )
	List< String > findUserEmailListByGidList ( @Param ( "gidList" ) List< String > gidList );

	@Query ( "SELECT m.gid FROM MRD m WHERE m.user.email = :email" )
	List< String > findGidListByUserEmail ( @Param ( "email" ) String email );
}
